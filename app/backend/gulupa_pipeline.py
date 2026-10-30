# -*- coding: utf-8 -*-
"""
gulupa_pipeline.py — Pipeline para el modelo PROPIO de gulupa (YOLO11-seg).

Cómo se activa: basta con que exista el archivo de pesos
    models/gulupa_seg.pt          (o la ruta en la variable de entorno GULUPA_SEG_MODEL)
Si no existe, api.py sigue usando el pipeline de PowderyVision7.

Qué debe saber detectar el modelo (clases del dataset, nombres flexibles):
    fruto / gulupa          -> contorno del fruto completo (OBLIGATORIA)
    rona / roña / scab      -> roña (Cladosporium spp.)
    dano_mecanico / mecanico / mechanical
    trips / thrips
Otras clases se reportan con su nombre (la app las muestra como "Otro daño").

Salida: % de cada daño = área del daño dentro del fruto / área del fruto * 100.
"""

import os
import time
import unicodedata

import cv2
import numpy as np

MODEL_PATH = os.environ.get("GULUPA_SEG_MODEL", "models/gulupa_seg.pt")
CONF_MIN = float(os.environ.get("GULUPA_CONF", "0.25"))
# Opcional: diámetro promedio del fruto en cm para estimar áreas en cm² (ej. 5.5).
FRUIT_DIAMETER_CM = float(os.environ.get("GULUPA_DIAMETRO_CM", "0") or 0)

FRUIT = "fruit"
HEALTHY = "healthy"

# Nombre de clase del dataset (normalizado: minúsculas, sin tildes, "_") -> clave de la app.
CLASS_TO_KEY = {
    "fruto": FRUIT, "gulupa": FRUIT, "fruit": FRUIT, "fruta": FRUIT,
    "rona": "scab", "scab": "scab", "cladosporium": "scab",
    "dano_mecanico": "mechanical", "mecanico": "mechanical", "mechanical": "mechanical",
    "golpe": "mechanical", "mechanical_damage": "mechanical",
    "trips": "thrips", "thrips": "thrips",
}

NAMES_ES = {"scab": "roña", "mechanical": "daño mecánico", "thrips": "trips"}
NAMES_EN = {"scab": "scab", "mechanical": "mechanical damage", "thrips": "thrips"}

# Colores RGB iguales a los de la app.
COLORS_RGB = {
    "scab": (229, 57, 53),
    "mechanical": (47, 128, 237),
    "thrips": (242, 153, 74),
}
OTHER_RGB = (142, 68, 173)

_model = None


def available():
    return os.path.isfile(MODEL_PATH)


def _get_model():
    global _model
    if _model is None:
        from ultralytics import YOLO  # import diferido: solo si hay modelo de gulupa
        _model = YOLO(MODEL_PATH)
    return _model


def _norm(name):
    s = unicodedata.normalize("NFKD", str(name)).encode("ascii", "ignore").decode("ascii")
    return s.strip().lower().replace(" ", "_").replace("-", "_")


def class_key(name):
    n = _norm(name)
    return CLASS_TO_KEY.get(n, n)


def _bi(en, es):
    return f"{en} / {es}"


def analyze(bgr):
    """
    Devuelve un dict con la misma forma que usa api.py:
      ok, msg, cls_name, cls_conf, cls_time_ms, seg_time_ms, crop_name_es, species,
      disease, disease_es, is_healthy, leaf_conf, severidad_pct,
      damages=[{key, pct, area_cm2}], layers={original, crop, leaf, mask, overlay,
      overlays{key}, result, hue} (arreglos numpy BGR / gris / BGRA).
    """
    H, W = bgr.shape[:2]
    t0 = time.time()
    res = _get_model()(bgr, conf=CONF_MIN, retina_masks=True, verbose=False)[0]
    seg_ms = (time.time() - t0) * 1000

    base = {"ok": False, "cls_time_ms": 0.0, "seg_time_ms": seg_ms, "species": "gulupa",
            "crop_name_es": "Gulupa", "layers": {"original": bgr}}

    if res.masks is None or res.boxes is None or len(res.boxes) == 0:
        base["msg"] = _bi("No gulupa was detected. Take a closer, centered photo.",
                          "No se detectó una gulupa. Toma la foto más cerca y centrada.")
        return base

    masks = res.masks.data.cpu().numpy()
    classes = res.boxes.cls.cpu().numpy().astype(int)
    confs = res.boxes.conf.cpu().numpy()
    names = res.names

    def full_mask(i):
        m = (masks[i] > 0.5).astype(np.uint8)
        if m.shape != (H, W):
            m = cv2.resize(m, (W, H), interpolation=cv2.INTER_NEAREST)
        return m

    keys = [class_key(names[int(c)]) for c in classes]
    fruit_idx = [i for i, k in enumerate(keys) if k == FRUIT]
    if not fruit_idx:
        base["msg"] = _bi("The fruit outline was not detected (class 'fruto').",
                          "No se detectó el contorno del fruto (clase 'fruto').")
        return base

    # Un solo fruto: el de mayor área.
    best = max(fruit_idx, key=lambda i: int(full_mask(i).sum()))
    fruit = full_mask(best)
    fruit_area = int(fruit.sum())
    if fruit_area < 500:
        base["msg"] = _bi("The fruit is too small in the photo.", "El fruto se ve muy pequeño en la foto.")
        return base

    # Unión de máscaras por tipo de daño, recortadas al fruto.
    per_key = {}
    for i, k in enumerate(keys):
        if k == FRUIT:
            continue
        m = full_mask(i) & fruit
        per_key[k] = (per_key.get(k, np.zeros_like(fruit)) | m)

    any_damage = np.zeros_like(fruit)
    for m in per_key.values():
        any_damage |= m

    cm2_per_fruit = (np.pi * (FRUIT_DIAMETER_CM / 2.0) ** 2) if FRUIT_DIAMETER_CM > 0 else None

    def entry(key, area_px):
        pct = round(100.0 * area_px / fruit_area, 2)
        cm2 = round(cm2_per_fruit * pct / 100.0, 2) if cm2_per_fruit else None
        return {"key": key, "pct": pct, "area_cm2": cm2}

    damages = sorted((entry(k, int(m.sum())) for k, m in per_key.items() if m.sum() > 0),
                     key=lambda d: -d["pct"])
    damages.append(entry(HEALTHY, fruit_area - int(any_damage.sum())))

    # ---- Capas para la app (recortadas al fruto) ----
    ys, xs = np.where(fruit > 0)
    pad = 10
    y1, y2 = max(0, ys.min() - pad), min(H, ys.max() + pad + 1)
    x1, x2 = max(0, xs.min() - pad), min(W, xs.max() + pad + 1)
    crop = bgr[y1:y2, x1:x2].copy()
    fruit_c = fruit[y1:y2, x1:x2]
    leaf = cv2.bitwise_and(crop, crop, mask=fruit_c)          # foto sin fondo
    mask_bin = (any_damage[y1:y2, x1:x2] * 255).astype(np.uint8)

    overlay = np.zeros((crop.shape[0], crop.shape[1], 4), np.uint8)
    overlays = {}
    result = leaf.copy()
    for k, m in per_key.items():
        mc = m[y1:y2, x1:x2]
        if mc.sum() == 0:
            continue
        r, g, b = COLORS_RGB.get(k, OTHER_RGB)
        single = np.zeros_like(overlay)
        single[mc > 0] = (b, g, r, 255)
        overlays[k] = single
        overlay[mc > 0] = (b, g, r, 255)
        cnts, _ = cv2.findContours((mc * 255).astype(np.uint8), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
        cv2.drawContours(result, cnts, -1, (b, g, r), 3)
    fc, _ = cv2.findContours((fruit_c * 255).astype(np.uint8), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cv2.drawContours(result, fc, -1, (68, 158, 46), 2)  # contorno del fruto en verde

    hsv = cv2.cvtColor(leaf, cv2.COLOR_BGR2HSV)
    hue = cv2.applyColorMap(cv2.normalize(hsv[:, :, 0], None, 0, 255, cv2.NORM_MINMAX).astype(np.uint8),
                            cv2.COLORMAP_HSV)
    hue[fruit_c == 0] = 0

    harmful = [d for d in damages if d["key"] != HEALTHY]
    top = harmful[0]["key"] if harmful else None
    total = round(sum(d["pct"] for d in harmful), 2)

    return {
        **base,
        "ok": True,
        "msg": None,
        "cls_name": f"gulupa_{top}" if top else "gulupa_sana",
        "cls_conf": float(confs[best]),
        "leaf_conf": float(confs[best]),
        "is_healthy": not harmful,
        "disease": NAMES_EN.get(top, top) if top else None,
        "disease_es": NAMES_ES.get(top, top) if top else None,
        "status": "diseased" if harmful else "healthy",
        "status_es": "con daño" if harmful else "sana",
        "severidad_pct": total,
        "damages": damages,
        "layers": {
            "original": bgr, "crop": result, "leaf": leaf, "mask": mask_bin,
            "overlay": overlay, "overlays": overlays, "result": result, "hue": hue,
        },
    }

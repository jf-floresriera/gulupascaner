# Guía: entrenar y conectar el modelo de gulupa

El backend (`app/backend/api.py`) ya está preparado: **en cuanto exista el archivo
`models/gulupa_seg.pt` en el servidor, la app empieza a usar el modelo de gulupa**.
Si el archivo no está, sigue funcionando con PowderyVision7 (mildiu en arveja/tomate).

```
Entrenar YOLO11-seg ──▶ best.pt ──▶ renombrar a gulupa_seg.pt ──▶ subir al Space (nube)
                                                               └─▶ respaldo en GitHub (LFS)
```

## 1. Qué debe aprender el modelo

Un solo modelo de **segmentación de instancias YOLO11-seg** con estas clases
(los nombres pueden ir con o sin tilde, en español o inglés):

| Clase en el dataset | Qué se anota | Clave en la app |
|---------------------|--------------|-----------------|
| `fruto` (o `gulupa`) | **Contorno completo del fruto. Obligatoria en todas las imágenes.** | — (base del %) |
| `rona` (o `roña`, `scab`) | Polígono de cada lesión de roña | `scab` |
| `dano_mecanico` (o `mecanico`) | Golpes, rozaduras, cortes | `mechanical` |
| `trips` (o `thrips`) | Raspaduras por trips | `thrips` |

Así se calcula: **% del daño = área del daño dentro del fruto ÷ área del fruto × 100**,
y el resto es "Tejido sano". Por eso es indispensable anotar el fruto completo.

Recomendaciones para el dataset:
- Fotos con celular, como las tomará el usuario: un fruto, centrado, fondo claro, buena luz.
- Incluir frutos **sanos** (solo con la clase `fruto`), para que el modelo no invente daños.
- Mínimo orientativo: 150–300 imágenes por tipo de daño; separar 15–20 % para validación.
- Herramientas de anotación con polígonos y exportación YOLO: Roboflow, CVAT o Label Studio.

## 2. Entrenar (Google Colab o un PC con GPU)

`gulupa.yaml`:

```yaml
path: /content/dataset_gulupa
train: images/train
val: images/val
names:
  0: fruto
  1: rona
  2: dano_mecanico
  3: trips
```

```bash
pip install ultralytics
yolo segment train model=yolo11n-seg.pt data=gulupa.yaml imgsz=640 epochs=150 patience=30
# Para más precisión (más lento en el servidor): model=yolo11s-seg.pt
```

El modelo queda en `runs/segment/train/weights/best.pt`. Revisa en `runs/segment/train/`
las métricas `mAP50-95(M)` (máscaras) y la matriz de confusión antes de publicarlo.

Prueba rápida antes de subirlo:

```bash
yolo segment predict model=runs/segment/train/weights/best.pt source=foto_prueba.jpg
```

## 3. Dónde guardar los pesos

### a) En la nube (obligatorio: allí corre el backend)

En el Space de Hugging Face `jf-floresriera/powderyvision7` (*Files › Add file › Upload files*):

| Archivo | Ubicación en el Space |
|---------|----------------------|
| `best.pt` renombrado a **`gulupa_seg.pt`** | `models/gulupa_seg.pt` |
| `app/backend/api.py` de este repo | raíz del Space (junto a `app.py`) |
| `app/backend/gulupa_pipeline.py` de este repo | raíz del Space |

Y en el `Dockerfile` del Space, la última línea debe ser:

```dockerfile
CMD gunicorn --bind 0.0.0.0:$PORT --workers 1 --timeout 120 --keep-alive 5 api:app
```

`requirements.txt` del Space ya incluye `ultralytics`, no hay que agregar nada.
Hugging Face guarda los `.pt` grandes automáticamente con LFS/Xet.

### b) En GitHub (respaldo y control de versiones)

```bash
git lfs install                       # una sola vez por computador
cp runs/segment/train/weights/best.pt models/gulupa_seg.pt
git add models/gulupa_seg.pt models/README.md
git commit -m "feat(models): modelo de gulupa v1"
git push
```

`.gitattributes` ya envía los `*.pt` a Git LFS. Anota la versión y las métricas en `models/README.md`.

## 4. Verificar que quedó funcionando

1. Abre `https://jf-floresriera-powderyvision7.hf.space/api/health`. Debe decir
   `"engine": "gulupa"`. Si dice `"powderyvision7"`, el archivo no está en `models/gulupa_seg.pt`.
2. En la app: desactiva *Modo demostración* y toma una foto. En *Ver detalles técnicos* el motor
   debe decir **YOLO11-seg (gulupa)**.

## 5. Ajustes opcionales (Space › Settings › Variables)

| Variable | Para qué | Ejemplo |
|----------|----------|---------|
| `GULUPA_SEG_MODEL` | Otra ruta/nombre de pesos | `models/gulupa_seg_v2.pt` |
| `GULUPA_CONF` | Confianza mínima de detección | `0.30` |
| `GULUPA_DIAMETRO_CM` | Diámetro promedio del fruto, para mostrar áreas en cm² (estimadas) | `5.5` |

- Si usan **otros nombres de clase**, agréguenlos en `CLASS_TO_KEY` de `gulupa_pipeline.py`.
- Si aparece un **daño nuevo** (p. ej. antracnosis), agréguenlo también en la app:
  `domain/DamageCatalog.kt` + textos en `res/values/strings.xml`.
- Los **umbrales de severidad** (escala 1–10 y clasificación general) están en
  `app/android/.../domain/Severity.kt`; calíbrenlos con el asesor agrónomo.

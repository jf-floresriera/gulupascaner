# app — GulupaScan (Android · Kotlin)

Aplicación móvil nativa de GulupaScan. Es la adaptación a **Kotlin + Jetpack Compose** de la
app web PowderyVision7 (`proyecto-base/powderyvision7`), con el diseño del prototipo del
**Reto 2** (`docs/retos/reto-02-canvas-prototipo`).

```
app/
├── android/      ← Proyecto de Android Studio (Kotlin, Compose, Material 3)
└── backend/      ← api.py: API JSON que reutiliza el pipeline de PowderyVision7
```

## Arquitectura

```
 Celular (Kotlin)                                Servidor (Hugging Face / Cloud Run)
┌────────────────────────────┐   POST /api/analyze  ┌──────────────────────────────────┐
│ Cámara (CameraX) / Galería │ ───── imagen JPEG ──▶│ api.py                           │
│ Procesando…                │                      │  └─ pipeline.py (YOLO11 cls+seg) │
│ Tipos de daño / Detalle    │ ◀── JSON + imágenes ─│  └─ segment_mildew.py (Chan-Vese)│
│ Resultado / Detalles       │      (base64)        └──────────────────────────────────┘
│ Historial local (offline)  │
└────────────────────────────┘
```

- El **pipeline de visión no cambia**: `api.py` importa `pipeline.py` y `segment_mildew.py`
  tal cual y solo agrega la ruta `/api/analyze`. La web original (`/`) sigue funcionando.
- La app guarda el historial **en el teléfono** (funciona sin internet para consultar reportes).
- **Modo demostración**: reproduce los valores del mockup (roña 12.5 %, daño mecánico 5.5 %,
  trips 2.1 %) sin servidor. Útil para exponer en clase.

## Pantallas (según el mockup del Reto 2)

| Mockup | Pantalla en la app | Archivo |
|--------|--------------------|---------|
| 1 · Abrir app | Splash verde con logo | `ui/screens/AuthScreens.kt` |
| Iniciar sesión / Regístrate | Login local | `ui/screens/AuthScreens.kt` |
| 1 · Tomar o seleccionar foto | Cámara FOTO/GALERÍA, flash, cambiar cámara, consejos | `ui/screens/CameraScreen.kt` |
| 2 · Detección y segmentación | Procesando imagen + progreso + cancelar | `ui/screens/ScanFlowScreens.kt` |
| 3 · Tipos de daño identificados | Lista por tipo de daño con % de área | `ui/screens/ScanFlowScreens.kt` |
| 4 · Severidad por daño | Máscara con opacidad + escala 1-10 | `ui/screens/ScanFlowScreens.kt` |
| 5 · Resultado y recomendaciones | Clasificación general, resumen, guardar, compartir | `ui/screens/ScanFlowScreens.kt` |
| 5 · Detalles | Original, sin fondo, máscara, resultado, canal H, panel | `ui/screens/ScanFlowScreens.kt` |
| Perfil de usuario | Perfil + menú | `ui/screens/ManagementScreens.kt` |
| Mis reportes / Alertas | Historial (exporta CSV) y alertas | `ui/screens/ManagementScreens.kt` |
| Acerca de nosotros | Misión, equipo, cómo funciona, alcance | `ui/screens/ManagementScreens.kt` |

Lo que se conserva de la versión web: flujo cargar → analizar → resultados → detalles técnicos,
mensajes de error del pipeline (EN/ES según el idioma del teléfono), consejos para la foto
(antes el tutorial), alcance del modelo y exportación a CSV.

## Cómo ejecutar la app

1. Instala **Android Studio** (Koala 2024.1 o más reciente).
2. *File › Open…* y elige la carpeta `app/android`.
3. Espera a que termine *Gradle Sync* (descarga las dependencias la primera vez).
4. Conecta un celular con *Depuración USB* (o crea un emulador) y pulsa **Run ▶**.
5. Crea tu cuenta (se guarda solo en el teléfono).
6. Si aún no desplegaste la API: *Perfil › Configuración de cuenta › Modo demostración*.

Versiones: Kotlin 2.0.20 · AGP 8.5.2 · Gradle 8.9 · compileSdk 34 · minSdk 26 (Android 8.0).
Si Android Studio propone actualizar AGP/Gradle, puedes aceptarlo.

## Cómo desplegar la API (`backend/api.py`)

En el Space de Hugging Face `jf-floresriera/powderyvision7` (o en Cloud Run):

1. Copia `app/backend/api.py` junto a `app.py`.
2. En el `Dockerfile`, cambia la última línea a:
   ```dockerfile
   CMD gunicorn --bind 0.0.0.0:$PORT --workers 1 --timeout 120 --keep-alive 5 api:app
   ```
3. Haz commit en el Space. Prueba: `https://jf-floresriera-powderyvision7.hf.space/api/health`
   debe responder `{"ok": true, "api_version": 1}`.

La URL del servidor viene configurada en `app/android/app/build.gradle.kts`
(`DEFAULT_SERVER_URL`) y se puede cambiar dentro de la app.

### Respuesta de `/api/analyze`

```json
{
  "ok": true,
  "cls_name": "pea_powderymildew", "cls_conf": 0.93,
  "is_healthy": false, "severidad_pct": 12.4,
  "damages": [ {"key": "powdery_mildew", "pct": 12.4}, {"key": "healthy", "pct": 87.6} ],
  "images": { "original": "<b64>", "crop": "...", "leaf": "...", "mask": "...",
              "overlay": "...", "result": "...", "hue": "...", "panel": "..." }
}
```

## Cuando exista el modelo de gulupa

La app ya muestra cualquier lista de daños. Solo hay que:

1. En `api.py › _damages()` devolver las claves `scab` (roña), `mechanical` (daño mecánico),
   `thrips` (trips) con su porcentaje (y opcionalmente `area_cm2`).
2. Si aparece un daño nuevo, agregarlo en `domain/DamageCatalog.kt` y sus textos en
   `res/values/strings.xml`.
3. Ajustar los umbrales de severidad en `domain/Severity.kt` con el asesor agrónomo.

> Para análisis **offline** en el teléfono, el siguiente paso sería exportar los modelos YOLO11 a
> TensorFlow Lite (`yolo export format=tflite`) e implementar otra clase `Analyzer` en
> `data/Analyzers.kt`; el resto de la app no cambia.

## Limitaciones conocidas

- El inicio de sesión es local (prototipo); para producción usar un backend (p. ej. Firebase Auth).
- El modelo actual reconoce **mildiu polvoso en hojas de arveja y tomate**, no daños de gulupa.
- El área en cm² solo aparece en modo demostración (el pipeline no conoce la escala de la foto).

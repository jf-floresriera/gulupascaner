# Changelog

## [tema-morado-modelo-gulupa] - 2026-10-30
- App: tema **Morado gulupa** (colores y formas de la presentación) seleccionable en Configuración.
- Backend: `gulupa_pipeline.py` (YOLO11-seg: fruto, roña, daño mecánico, trips) que se activa al
  existir `models/gulupa_seg.pt`; `/api/health` informa el motor activo.
- App: máscara propia por tipo de daño en el detalle.
- Guía `docs/guia-modelos.md` para entrenar y publicar el modelo.

## [app-kotlin] - 2026-10-30
- Se agrega `app/android/`: app nativa en Kotlin + Jetpack Compose con el diseño del prototipo del Reto 2
  (splash, login, cámara, procesando, tipos de daño, severidad, resultado, detalles técnicos,
  historial con CSV, alertas, perfil, acerca de nosotros). Español e inglés. Modo demostración.
- Se agrega `app/backend/api.py`: ruta `/api/analyze` que devuelve JSON reutilizando sin cambios
  `pipeline.py` y `segment_mildew.py` de PowderyVision7.

## [proyecto-base] - 2026-10-13
- Se agrega `proyecto-base/powderyvision7/` (copia del Space de Hugging Face PowderyVision7) como referencia para GulupaScaner.

## [reto-02] - 2026-09-17
- Canvas y prototipo.

## [reto-01] - 2026-09-08
- Propuesta de proyecto.

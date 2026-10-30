# models

Modelos usados por la app (versionados con **Git LFS**). Guía completa para entrenar y
publicar el modelo de gulupa: [`docs/guia-modelos.md`](../docs/guia-modelos.md).

| Archivo | Versión | Origen / datos de entrenamiento | Licencia | Precisión |
|---------|---------|---------------------------------|----------|-----------|
| `gulupa_seg.pt` | _pendiente_ | YOLO11-seg · clases: fruto, rona, dano_mecanico, trips | | mAP50-95(M): _—_ |

Modelos actuales del backend (PowderyVision7, mildiu en arveja y tomate), ya respaldados en
`proyecto-base/powderyvision7/models/`:

| Archivo | Tamaño | Función |
|---------|--------|---------|
| `yolo11m-cls_best.pt` | 21 MB | Clasificación (arveja/tomate · sana/mildiu/fondo) |
| `yolo11n_leaf_seg_win.pt` | 6 MB | Segmentación de la hoja |

# 🍈 GulupaScaner

> Proyecto de la asignatura **Desarrollo de Aplicaciones para Dispositivos Móviles**.
> En este repositorio se versiona el desarrollo de la app y los retos semanales asignados en clase.

![Estado](https://img.shields.io/badge/estado-en%20desarrollo-yellow)
![Reto actual](https://img.shields.io/badge/reto%20actual-2-blue)

---

## 📑 Tabla de contenido

1. [Descripción](#-descripción)
2. [Entregables del proyecto](#-entregables-del-proyecto)
3. [Estructura del repositorio](#-estructura-del-repositorio)
4. [Guía paso a paso desde la terminal](#-guía-paso-a-paso-desde-la-terminal)
5. [Flujo de trabajo semanal](#-flujo-de-trabajo-semanal)
6. [Convenciones y buenas prácticas](#-convenciones-y-buenas-prácticas)
7. [Modelos y archivos pesados](#-modelos-y-archivos-pesados)
8. [Ética y seguridad](#-ética-y-seguridad)
9. [Autor](#-autor)

---

## 📌 Descripción

_Escribe aquí en 2–3 líneas qué hace la app, para quién es y qué problema resuelve
(puedes tomarlo del documento del Reto 1)._

---

## 📦 Entregables del proyecto

| Reto | Tema | Fecha | Documento | Carpeta |
|------|------|-------|-----------|---------|
| Reto 1 | Idea de app / Propuesta | 08/SEP/2026 | [Drive](https://drive.google.com/file/d/1yPa9i7oMgsIIfYg961yNXq2lm0O0DNuv/view) | [`docs/retos/reto-01-propuesta`](docs/retos/reto-01-propuesta) |
| Reto 2 | Canvas y Prototipo | 17/SEP/2026 | [Drive](https://drive.google.com/file/d/1vH9kCUcDCh1jfP8vfhxUf-OU4_n3O2Dp/view) | [`docs/retos/reto-02-canvas-prototipo`](docs/retos/reto-02-canvas-prototipo) |
| Reto 3 | Primer avance del proyecto | Por definir | — | [`docs/retos/reto-03-primer-avance`](docs/retos/reto-03-primer-avance) |
| Final | Entrega final | Por definir | — | [`docs/retos/entrega-final`](docs/retos/entrega-final) |

---

## 🗂 Estructura del repositorio

```
gulupascaner/
├── README.md                 ← Este archivo (portada del proyecto)
├── CHANGELOG.md              ← Qué cambió en cada reto
├── LICENSE                   ← Licencia del código
├── .gitignore                ← Archivos que Git NO debe subir
├── .gitattributes            ← Reglas de Git LFS y fin de línea
│
├── app/                      ← Código fuente de la aplicación móvil
│   ├── README.md             ← Cómo ejecutar la app y desplegar la API
│   ├── android/              ← App nativa Kotlin + Jetpack Compose (Android Studio)
│   └── backend/api.py        ← API JSON sobre el pipeline de PowderyVision7
│
├── models/                   ← Modelos (ML) usados por la app — con Git LFS
│   └── README.md
│
├── proyecto-base/            ← Proyectos de referencia (inspiración y protocolo)
│   ├── README.md
│   └── powderyvision7/       ← Copia del Space de Hugging Face PowderyVision7
│
├── docs/                     ← Documentación del proyecto
│   ├── retos/
│   │   ├── reto-01-propuesta/
│   │   │   ├── README.md
│   │   │   └── reto-01-propuesta-proyecto.pdf
│   │   ├── reto-02-canvas-prototipo/
│   │   │   ├── README.md
│   │   │   └── reto-02-canvas-prototipo.pdf
│   │   ├── reto-03-primer-avance/
│   │   └── entrega-final/
│   ├── presentaciones/       ← Diapositivas de cada exposición
│   ├── diagramas/            ← Arquitectura, casos de uso, flujos
│   └── assets/               ← Imágenes y capturas usadas en la documentación
│
└── scripts/
    └── crear-estructura.sh   ← Crea toda esta estructura automáticamente
```

**¿Por qué así?** El código (`app/`), los modelos (`models/`) y la documentación (`docs/`)
quedan separados; cada reto tiene su propia carpeta con un `README.md` que explica lo entregado,
y el profesor puede navegar todo desde la tabla de entregables.

---

## 💻 Guía paso a paso desde la terminal

> Todos los comandos se ejecutan en **Git Bash** (se instala con [Git for Windows](https://git-scm.com/download/win)).
> Para abrirlo: clic derecho en una carpeta → *Open Git Bash here*.

### Paso 0 · Configurar tu identidad (solo la primera vez en el equipo)

```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu-correo@ejemplo.com"
git config --global init.defaultBranch main
```

| Comando | Qué hace |
|---------|----------|
| `git config --global user.name` | Nombre que aparecerá como autor de tus commits. |
| `git config --global user.email` | Correo asociado a tus commits (usa el mismo de GitHub). |
| `git config --global init.defaultBranch main` | Hace que los repositorios nuevos empiecen en la rama `main`. |

> ⚠️ Si estás en un computador compartido del laboratorio, usa `git config` **sin** `--global`
> dentro de la carpeta del proyecto, para no dejar tu identidad configurada para otros.

### Paso 1 · Crear la carpeta del proyecto y entrar en ella

```bash
cd ~/Documents
mkdir gulupascaner
cd gulupascaner
```

| Comando | Qué hace |
|---------|----------|
| `cd ~/Documents` | Se mueve a tu carpeta Documentos (`~` = tu carpeta de usuario). |
| `mkdir gulupascaner` | Crea la carpeta del proyecto. |
| `cd gulupascaner` | Entra en la carpeta recién creada. |

### Paso 2 · Crear la estructura de carpetas

Copia la carpeta `scripts/` y los archivos `README.md`, `.gitignore` y `.gitattributes` dentro de `gulupascaner`, y ejecuta:

```bash
bash scripts/crear-estructura.sh
```

El script crea todas las carpetas, los `README.md` de cada reto y el `CHANGELOG.md`.
Si prefieres hacerlo a mano, el equivalente es:

```bash
mkdir -p app models docs/retos/{reto-01-propuesta,reto-02-canvas-prototipo,reto-03-primer-avance,entrega-final} docs/{presentaciones,diagramas,assets}
```

| Parte | Qué hace |
|-------|----------|
| `mkdir -p` | Crea carpetas, incluidas las intermedias, sin error si ya existen. |
| `{a,b,c}` | Expansión de llaves: crea varias carpetas en un solo comando. |

### Paso 3 · Copiar los PDF de los retos con nombres limpios

```bash
cp "/c/Users/lab22/Downloads/Reto 1_Propuesta de Proyecto Desarrollo de Aplicaciones para Dispositivos Móviles.pdf" \
   docs/retos/reto-01-propuesta/reto-01-propuesta-proyecto.pdf

cp "/c/Users/lab22/Downloads/Reto2-Canvas y Prototipado.pdf" \
   docs/retos/reto-02-canvas-prototipo/reto-02-canvas-prototipo.pdf
```

| Parte | Qué hace |
|-------|----------|
| `cp origen destino` | Copia un archivo. |
| `"/c/Users/..."` | En Git Bash, `C:\` se escribe `/c/` y las barras van hacia `/`. Las comillas son obligatorias porque el nombre tiene espacios. |
| `\` al final de línea | Permite partir un comando largo en varias líneas. |
| Nombre nuevo | Sin espacios, tildes ni mayúsculas: evita errores en terminal y en URLs. |

Verifica que quedaron bien:

```bash
ls -R docs/retos
```

`ls -R` lista el contenido de las carpetas de forma recursiva.

### Paso 4 · Iniciar Git y conectar con GitHub

```bash
git init
git branch -M main
git remote add origin https://github.com/jf-floresriera/gulupascaner.git
git remote -v
```

| Comando | Qué hace |
|---------|----------|
| `git init` | Convierte la carpeta en un repositorio Git (crea la carpeta oculta `.git`). |
| `git branch -M main` | Renombra la rama actual a `main`. |
| `git remote add origin URL` | Registra tu repositorio de GitHub con el alias `origin`. |
| `git remote -v` | Muestra los remotos configurados para comprobar que la URL es correcta. |

### Paso 5 · (Opcional, recomendado) Activar Git LFS para archivos pesados

```bash
git lfs install
```

Instala Git LFS en tu repositorio. Las reglas ya están en `.gitattributes` (ver [Modelos y archivos pesados](#-modelos-y-archivos-pesados)).

### Paso 6 · Primer commit y subida

```bash
git status
git add .
git commit -m "chore: estructura inicial del repositorio y retos 1 y 2"
git push -u origin main
```

| Comando | Qué hace |
|---------|----------|
| `git status` | Muestra qué archivos son nuevos o cambiaron. Revísalo **siempre** antes de `add`. |
| `git add .` | Prepara (staging) todos los cambios de la carpeta actual. |
| `git commit -m "..."` | Guarda una "foto" de los cambios con un mensaje descriptivo. |
| `git push -u origin main` | Sube la rama `main` a GitHub; `-u` la deja vinculada para que luego baste con `git push`. |

> 🔐 GitHub ya no acepta la contraseña de la cuenta en la terminal. Cuando te la pida,
> inicia sesión con la ventana del navegador que abre Git for Windows, o usa un
> [Personal Access Token](https://github.com/settings/tokens).

### Paso 7 · Marcar la entrega con una etiqueta (tag)

```bash
git tag -a reto-02 -m "Entrega Reto 2: Canvas y Prototipo"
git push origin reto-02
```

| Comando | Qué hace |
|---------|----------|
| `git tag -a nombre -m "..."` | Crea una etiqueta anotada que marca el estado exacto del proyecto en esa entrega. |
| `git push origin nombre` | Sube la etiqueta a GitHub (los tags no se suben con `git push` normal). |

Así el profesor puede ver exactamente qué entregaste en cada reto desde la pestaña **Releases/Tags** de GitHub.

---

## 🔁 Flujo de trabajo semanal

Cada vez que el profesor asigne un reto nuevo:

```bash
# 1. Traer los últimos cambios
git switch main
git pull

# 2. Crear una rama para el reto
git switch -c reto-03-primer-avance

# 3. Trabajar... y guardar avances con frecuencia
git add docs/retos/reto-03-primer-avance app
git commit -m "feat(reto-03): pantalla de escaneo inicial"
git push -u origin reto-03-primer-avance

# 4. En GitHub: abrir un Pull Request de la rama hacia main y fusionarlo

# 5. Volver a main, actualizar y etiquetar la entrega
git switch main
git pull
git tag -a reto-03 -m "Entrega Reto 3: Primer avance"
git push origin reto-03

# 6. (Opcional) Borrar la rama ya fusionada
git branch -d reto-03-primer-avance
```

| Comando | Qué hace |
|---------|----------|
| `git switch main` | Cambia a la rama `main`. |
| `git pull` | Descarga y aplica los cambios que haya en GitHub. |
| `git switch -c nombre` | Crea una rama nueva y se cambia a ella. |
| `git branch -d nombre` | Borra una rama local que ya fue fusionada. |
| `git log --oneline --graph` | Muestra el historial resumido (útil para revisar). |

Después de cada reto: actualiza la **tabla de entregables** de este README y agrega una entrada en `CHANGELOG.md`.

---

## ✅ Convenciones y buenas prácticas

**Nombres de archivos y carpetas:** minúsculas, sin tildes ni espacios, separados por guiones
(`reto-03-primer-avance.pdf`, no `Reto 3 Primer Avance.pdf`).

**Mensajes de commit** ([Conventional Commits](https://www.conventionalcommits.org/es/)):

| Prefijo | Uso | Ejemplo |
|---------|-----|---------|
| `feat:` | Nueva funcionalidad | `feat: agregar cámara para escanear` |
| `fix:` | Corrección de error | `fix: corregir cierre al girar pantalla` |
| `docs:` | Documentación | `docs: subir presentación reto 2` |
| `style:` | Formato, sin cambiar lógica | `style: ordenar imports` |
| `refactor:` | Reorganizar código | `refactor: separar capa de datos` |
| `chore:` | Configuración y mantenimiento | `chore: actualizar .gitignore` |

**Ramas:** `main` siempre estable y entregable; una rama por reto o funcionalidad.

**Commits pequeños y frecuentes:** es mejor 5 commits claros que 1 gigante al final de la semana.

---

## 🧠 Modelos y archivos pesados

GitHub rechaza archivos de más de **100 MB** y recomienda no pasar de 50 MB.
Los modelos (`.tflite`, `.onnx`, `.pt`, `.h5`, etc.), videos y diseños pesados se versionan con **Git LFS**,
ya configurado en `.gitattributes`.

```bash
git lfs install                 # una vez por equipo
git lfs track "*.tflite"        # agregar un tipo nuevo si hace falta
git add .gitattributes models/
git commit -m "feat: agregar modelo de clasificación v1"
git lfs ls-files                # ver qué archivos gestiona LFS
```

Buenas prácticas para modelos:

- Nombrarlos con versión: `models/gulupa-clasificador-v1.tflite`.
- Documentar en `models/README.md` de dónde sale cada modelo, con qué datos se entrenó, su licencia y su precisión.
- Si un modelo supera la cuota gratuita de LFS (1 GB), súbelo a Drive o a *Releases* de GitHub y deja el enlace en `models/README.md`.

---

## 🛡 Ética y seguridad

- **Nunca subas secretos:** contraseñas, claves de API, `google-services.json` con credenciales reales o archivos `.env`. Ya están bloqueados en `.gitignore`; si alguno se sube por error, cambia la clave de inmediato (borrarlo del repo no basta, queda en el historial).
- **Datos personales:** no subas fotos o datos de personas sin su consentimiento; anonimiza los conjuntos de datos.
- **Da crédito:** cita librerías, modelos preentrenados, datasets, íconos e imágenes de terceros, respetando sus licencias.
- **Uso de IA:** si usas herramientas de IA para código o documentos, decláralo según las reglas de tu curso.
- **Trabajo propio:** el historial de commits muestra tu proceso; súbelo de forma honesta y progresiva.

---

## 👤 Autor

| Nombre | Usuario GitHub | Rol |
|--------|----------------|-----|
| _Tu nombre_ | [@jf-floresriera](https://github.com/jf-floresriera) | Desarrollo |

**Curso:** Desarrollo de Aplicaciones para Dispositivos Móviles · **Docente:** _nombre_ · **Periodo:** 2026-2

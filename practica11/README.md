# Práctica 11 – Manejo de archivos y SDCard

Desarrollo Móvil Nativo · Android Studio + Kotlin + Jetpack Compose

Aplicación Android nativa para guardar y leer archivos de texto en la memoria
interna y en la SDCard, además de un pequeño reproductor de audio que utiliza el
Storage Access Framework (SAF). El proyecto cubre la consigna de la actividad y
el reto de la guía: agregar un botón **Detener** que pausa la pista y la regresa
al segundo cero.

## Objetivo

Comprender el almacenamiento interno y externo de Android, el uso de `File`,
`FileInputStream` y `FileOutputStream`, la solicitud de permisos en tiempo de
ejecución y el acceso seguro a archivos con SAF.

## Tecnología utilizada

| Concepto            | Detalle                                             |
|---------------------|-----------------------------------------------------|
| Lenguaje            | Kotlin 2.2.10                                       |
| SDK de Android      | compileSdk / targetSdk 37, minSdk 28                |
| Interfaz            | Jetpack Compose + Material 3                        |
| Almacenamiento      | `File`, `FileOutputStream`, `FileInputStream`       |
| Reproductor         | `MediaPlayer` + `GetContent()` (SAF)                |
| Iconos              | Material Icons Extended                             |
| Versionado          | Git                                                 |

## Estructura del proyecto

```
app/src/main/java/com/example/practica11/
├── MainActivity.kt                    # solo llama a Practica11App()
├── data/
│   └── FileStorageManager.kt          # rutas, E/S de archivos y permisos
├── presentation/
│   ├── Practica11App.kt               # Scaffold + navegación inferior
│   ├── ArchivosScreen.kt              # formulario de archivos de texto
│   └── ReproductorScreen.kt           # reproductor con SAF y botón Detener
└── ui/theme/
    ├── Color.kt
    └── Theme.kt

docs/capturas/                         # evidencia de ejecución en el emulador
```

## Permisos y almacenamiento

En `AndroidManifest.xml` se declaran los permisos clásicos con límite de versión:

```xml
<uses-permission
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission
    android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
```

La app comprueba la versión de Android antes de pedir permisos:

- **Android 9 o inferior:** solicita `READ_EXTERNAL_STORAGE` y
  `WRITE_EXTERNAL_STORAGE` con `RequestMultiplePermissions`. Si el usuario
  concede el permiso, el guardado pendiente se ejecuta automáticamente. En esta
  versión se escribe en `Documentos/practica11`.
- **Android 10 o superior:** el sistema aplica Scoped Storage. La app escribe en
  su carpeta privada dentro del volumen externo, obtenida con
  `getExternalFilesDirs(null)`; si hay una SDCard montada se elige el volumen
  removible con `Environment.isExternalStorageRemovable`. No se necesitan
  permisos globales.

En el emulador de pruebas (Android 17, API 37) la app muestra
«No se requieren permisos en Android 17» y usa la SDCard virtual de 512 MB.

Rutas usadas:

```
Interna: /data/user/0/com.example.practica11/files/nota.txt
SDCard:  /storage/0000-0000/Android/data/com.example.practica11/files/documentos/nota.txt
```

## Lectura y escritura de archivos

`FileStorageManager` centraliza las operaciones y las ejecuta fuera del hilo
principal con `Dispatchers.IO`:

```kotlin
FileOutputStream(archivo).use { flujo ->
    OutputStreamWriter(flujo, Charsets.UTF_8).use { escritor ->
        escritor.write(contenido)
    }
}
```

```kotlin
val texto = FileInputStream(archivo).use { flujo ->
    BufferedReader(InputStreamReader(flujo, Charsets.UTF_8)).use { lector ->
        lector.readText()
    }
}
```

El nombre del archivo se limpia (se eliminan caracteres no válidos) y se le
agrega `.txt` cuando no tiene extensión. Los errores de permisos o de E/S se
devuelven como un resultado con mensaje y se muestran en la interfaz.

## Interfaz

La pantalla **Archivos** permite:

- escribir el nombre y el contenido del archivo;
- elegir entre memoria interna y SDCard;
- ver la ruta exacta antes de guardar;
- guardar, leer y eliminar con confirmación;
- consultar el estado de los permisos.

Después de cada operación, la pantalla se desplaza automáticamente hasta una
tarjeta de resultado (verde para éxito, roja para error). El texto leído se
muestra en una tarjeta aparte con tipografía monoespaciada.

La pantalla **Reproductor** permite elegir un audio con el explorador del
sistema, muestra nombre, peso y duración, y ofrece los controles de reproducir,
pausar y detener.

## Reproductor con SAF y reto Detener

`GetContent()` abre el explorador nativo con el filtro `audio/*`; la app recibe
solo el URI del archivo elegido, sin pedir permiso global de audio:

```kotlin
val selectorAudio = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri -> uri?.let { cargarPista(it) } }
```

El reproductor se libera cuando la pantalla sale de composición:

```kotlin
DisposableEffect(Unit) {
    onDispose {
        reproductor?.release()
        reproductor = null
    }
}
```

El botón **Detener** no es lo mismo que pausar. Además de pausar, regresa la
pista al inicio y actualiza el estado:

```kotlin
fun detenerReproduccion() {
    reproductor?.pause()
    reproductor?.seekTo(0)
    isPlaying = false
    posicionMs = 0L
    estado = EstadoReproductor.DETENIDO
}
```

## Pruebas realizadas

Emulador `Medium_Phone` con Android 17 (API 37) y SDCard virtual de 512 MB.

| Prueba                                   | Resultado |
|------------------------------------------|-----------|
| Guardar texto en memoria interna          | Archivo de 45 bytes creado en `files/nota.txt` |
| Leer texto de memoria interna             | Contenido recuperado correctamente |
| Guardar texto en SDCard                   | Archivo creado en `/storage/0000-0000/Android/data/...` |
| Leer texto de SDCard                      | Mismo contenido recuperado |
| Estado de permisos en Android 17          | «No se requieren permisos» |
| Seleccionar audio con SAF                 | `pista_practica11.wav`, 340 KB, 0:03 |
| Reproducir y pausar                       | La posición avanza y se detiene correctamente |
| Detener                                   | Estado «Detenido» y posición `0:00` |
| Compilar con `assembleDebug`              | BUILD SUCCESSFUL |

La existencia y el contenido de los archivos también se verificaron desde la
terminal con `run-as` (memoria interna) y con `adb shell cat` (SDCard).

## Capturas

En [`docs/capturas/`](docs/capturas/):

| Archivo                                  | Contenido |
|------------------------------------------|-----------|
| `01-pantalla-inicial.png`                | Pestaña Archivos y estado de permisos |
| `02-guardado-memoria-interna.png`        | Confirmación y ruta del archivo interno |
| `03-lectura-memoria-interna.png`         | Texto recuperado desde memoria interna |
| `04-guardado-sdcard.png`                 | Confirmación y ruta en la SDCard |
| `05-lectura-sdcard.png`                  | Texto recuperado desde la SDCard |
| `06-permisos-almacenamiento.png`         | Diálogo con los permisos declarados |
| `07-reproductor-pista-seleccionada.png`  | Pista elegida con SAF |
| `08-reproduciendo.png`                   | Reproducción en curso |
| `09-detener-segundo-cero.png`            | Pista detenida en `0:00` |

## Cómo ejecutar

1. Abrir la carpeta `practica11` en Android Studio.
2. Sincronizar Gradle.
3. Seleccionar un emulador con SDCard o un dispositivo físico y pulsar **Run**.

Desde la terminal:

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.practica11/.MainActivity
```

En un dispositivo Android 9 o inferior, al guardar en la SDCard aparecerá el
diálogo del sistema para conceder los permisos de almacenamiento. En Android 10
o superior no se muestran porque la app usa su carpeta privada y SAF.

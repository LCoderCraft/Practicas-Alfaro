# Práctica 05 – SharedPreferences

Desarrollo Móvil Nativo · Android Studio + Kotlin + Jetpack Compose

Práctica guiada para implementar **persistencia de datos local** en Android con
`SharedPreferences`, dentro de una arquitectura con **Jetpack Compose** y **sin layouts XML**.

---

## Objetivo

Comprender el ciclo de lectura y escritura de pares **clave-valor (key-value)** para guardar
preferencias del usuario, de modo que los datos permanezcan intactos incluso después de
cerrar la aplicación por completo.

## Tecnología utilizada

| Concepto                    | Detalle                                        |
|-----------------------------|------------------------------------------------|
| Lenguaje                    | Kotlin 2.2                                     |
| SDK de Android              | compileSdk / targetSdk 37, minSdk 28           |
| Interfaz                    | Jetpack Compose + Material 3 (sin XML layouts) |
| Persistencia                | `SharedPreferences` (archivo XML interno)      |
| Versionado                  | Git                                            |

## Estructura del proyecto

```
app/src/main/java/com/example/practica05/
├── MainActivity.kt                 # Activity mínima: solo llama a FormScreen
├── FormScreen.kt                   # UI: OutlinedTextField, Switches y botones
├── data/
│   └── PreferencesManager.kt       # Lee / escribe / borra en SharedPreferences
└── ui/theme/                       # Colores, tipografía y tema (soporta modo oscuro)
```

## Cómo funciona `SharedPreferences`

`SharedPreferences` guarda pares **clave → valor** en un archivo XML dentro del almacenamiento
privado de la app. En este proyecto el archivo es:

```
/data/data/com.example.practica05/shared_prefs/practica05_prefs.xml
```

Contenido generado al guardar:

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <boolean name="notificaciones_activas" value="true" />
    <boolean name="modo_oscuro" value="true" />
    <string name="nombre_usuario">Alfar</string>
</map>
```

| Llave                     | Tipo      | Valor por defecto |
|---------------------------|-----------|-------------------|
| `nombre_usuario`          | `String`  | `""`              |
| `notificaciones_activas`  | `Boolean` | `false`           |
| `modo_oscuro`             | `Boolean` | `false`           |

### API utilizada en `PreferencesManager.kt`

```kotlin
// Escritura asíncrona: no bloquea la UI
prefs.edit().putString(KEY_NOMBRE, nombre).apply()

// Escritura síncrona: devuelve el resultado de la operación
prefs.edit().clear().commit()

// Lectura con valor por defecto
prefs.getString(KEY_NOMBRE, "") ?: ""
```

> `apply()` escribe en segundo plano (recomendado para la UI).
> `commit()` es síncrono y devuelve `Boolean`, por eso se usa en el borrado.

## Pantalla de la aplicación

La pantalla de configuración contiene:

- `OutlinedTextField` → **Nombre del usuario**
- `Switch` → **Notificaciones activas**
- `Switch` → **Modo oscuro** (el tema oscuro se aplica realmente)
- Botón **Guardar** → escribe los tres valores
- Botón **Cargar** → vuelve a leer los valores del archivo
- Botón **Borrar** → limpia todas las llaves
- Botón **Ver archivo de preferencias** → indica la ruta del XML interno

## Cómo ejecutar

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Seleccionar un emulador o dispositivo y pulsar **Run**.

Desde la terminal:

```bash
./gradlew assembleDebug                    # compilar
./gradlew installDebug                     # instalar
./gradlew testDebugUnitTest                # pruebas unitarias
./gradlew connectedDebugAndroidTest        # pruebas instrumentadas (requiere dispositivo)
```

## Pruebas realizadas

### Pruebas instrumentadas (`connectedDebugAndroidTest`)

| Prueba                                        | Resultado |
|-----------------------------------------------|-----------|
| `useAppContext`                                | PASSED    |
| `guardarYRecuperarPreferencias`               | PASSED    |
| `losDatosPersistenEnUnaNuevaInstanciaDelManager` | PASSED |
| `sinDatosGuardadosDevuelveValoresPorDefecto`  | PASSED    |
| `borrarEliminaTodasLasLlaves`                 | PASSED    |

### Pruebas manuales sobre el emulador

| Escenario                                        | Resultado esperado                                  |
|--------------------------------------------------|-----------------------------------------------------|
| Guardar con nombre + ambos switches activos       | `Guardado: Alfar \| notif=true \| oscuro=true`      |
| `am force-stop` (cierre total) y reabrir          | Los datos aparecen restaurados automáticamente       |
| `Cargar` sin datos guardados                      | `No hay datos guardados todavia`                     |
| `Guardar` con el campo de nombre vacío            | `Error: el nombre no puede estar vacio`             |
| `Borrar`                                          | `Preferencias borradas` y el XML queda `<map />`    |

## Capturas de evidencia

En [`docs/capturas/`](docs/capturas/):

| Archivo                            | Contenido                                        |
|------------------------------------|--------------------------------------------------|
| `01-pantalla-inicial.png`         | Pantalla al abrir la app por primera vez         |
| `02-nombre-ingresado.png`         | Nombre escrito en el `OutlinedTextField`         |
| `03-switches-activados.png`       | Ambos `Switch` activados                         |
| `04-datos-guardados.png`          | Mensaje de confirmación tras pulsar **Guardar**  |
| `05-persistencia-tras-reabrir.png`| Datos persistentes tras cerrar y reabrir la app  |
| `06-shared-prefs-xml.txt`         | Contenido del archivo XML interno                |
| `07-boton-cargar.png`             | Resultado del botón **Cargar**                   |
| `08-error-campo-vacio.png`        | Caso borde: guardar con el campo vacío           |
| `09-datos-borrados.png`           | Resultado del botón **Borrar**                   |

## Commits

El historial usa mensajes descriptivos y Convention Commits:

- `chore: inicializar proyecto practica05 con Empty Activity`
- `feat(data): agregar PreferencesManager para leer, guardar y borrar en SharedPreferences`
- `feat(ui): construir FormScreen con OutlinedTextField, Switches y botones de accion`
- `feat: aplicar el modo oscuro persistido en el tema de la app`
- `fix(ui): restaurar los datos guardados al reabrir la aplicacion`
- `test: agregar pruebas instrumentadas del ciclo guardar, leer y borrar`
- `docs: anadir README y capturas de pantalla de la evidencia`

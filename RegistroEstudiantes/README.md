# Sistema de Registro de Estudiantes — Miniproyecto Integrador

Desarrollo Móvil Nativo · Android Studio + Kotlin + Jetpack Compose

Miniproyecto que integra los conceptos de las Prácticas 1 a 5 en una sola app:
interfaz con controles interactivos, navegación entre pantallas pasando parámetros
y persistencia local con `SharedPreferences`.

---

## Funcionalidades

| Requisito | Implementación |
|---|---|
| Matrícula y Nombre completo | `OutlinedTextField` |
| Carrera | `ExposedDropdownMenuBox` (menú desplegable) |
| Turno: Matutino / Vespertino | `RadioButton` |
| Estatus: Activo / Inactivo | `Switch` |
| Persistencia local | `SharedPreferences` (clave-valor + lista JSON) |
| Navegación con parámetros | `NavHostController` + `navArgument` |
| Video / GIF de evidencia | Grabado sobre el emulador |

## Estructura del proyecto

El código está organizado **por pantallas**, tal como pide la consigna:

```
app/src/main/java/com/example/registroestudiantes/
├── MainActivity.kt                      # Activity mínima: solo monta el NavHost
├── data/
│   ├── Estudiante.kt                    # Modelo de datos (@Immutable)
│   ├── PreferencesManager.kt            # Config clave-valor (último registro)
│   └── EstudianteRepository.kt          # Lista de estudiantes serializada a JSON
├── navigation/
│   └── AppNavHost.kt                    # Grafo de navegación y rutas
└── ui/
    ├── registro/RegistroScreen.kt       # Pantalla 1: formulario
    ├── detalle/DetalleScreen.kt         # Pantalla 2: confirmación con argumentos
    ├── lista/ListaRegistrosScreen.kt    # Pantalla 3: registros leídos de prefs
    └── theme/                           # Colores, tipografía y tema
```

## Pantalla 1 — Formulario de registro

Cuatro controles de captura, cada uno con el componente de Compose indicado:

```kotlin
// Matrícula y Nombre completo
OutlinedTextField(value = matricula, onValueChange = { matricula = it }, label = { Text("Matricula") })

// Carrera: menú desplegable
ExposedDropdownMenuBox(expanded = menuExpanded, onExpandedChange = { menuExpanded = it }) {
    OutlinedTextField(value = valor, onValueChange = {}, readOnly = true, ...)
    ExposedDropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
        opciones.forEach { opcion -> DropdownMenuItem(text = { Text(opcion) }, onClick = { onSeleccion(opcion) }) }
    }
}

// Turno: RadioButton
RadioButton(selected = turno == opcion, onClick = { turno = opcion })

// Estatus: Switch
Switch(checked = activo, onCheckedChange = { activo = it })
```

### Validación de campos

| Campo vacío / inválido | Mensaje mostrado |
|---|---|
| Sin matrícula | `La matricula es obligatoria` |
| Matrícula con menos de 4 caracteres | `La matricula debe tener al menos 4 caracteres` |
| Sin nombre | `El nombre completo es obligatorio` |
| Sin carrera | `Selecciona una carrera` |
| Sin turno | `Selecciona un turno` |

## Persistencia con SharedPreferences

Se usan **dos archivos** con propósitos distintos:

### 1. `registro_prefs.xml` — configuración clave-valor

Permite que el formulario **recupere el último registro** al reabrir la app.

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="ultima_matricula">20234567</string>
    <string name="ultimo_nombre">Juan Perez Lopez</string>
    <boolean name="ultimo_activo" value="false" />
    <string name="ultima_carrera">Ingeniería en Sistemas</string>
    <string name="ultimo_turno">Vespertino</string>
</map>
```

```kotlin
// Escritura asíncrona: no bloquea la UI
prefs.edit()
    .putString(KEY_ULTIMA_MATRICULA, estudiante.matricula)
    .apply()

// Lectura con valor por defecto
prefs.getString(KEY_ULTIMA_MATRICULA, "") ?: ""

// Borrado síncrono: commit() devuelve el resultado
prefs.edit().clear().commit()
```

Al abrir la app, `RegistroScreen` inicializa sus estados con estos valores:

```kotlin
val ultimo = remember(context) { preferencesManager.getUltimoEstudiante() }
var matricula by rememberSaveable { mutableStateOf(ultimo?.matricula ?: "") }
var turno     by rememberSaveable { mutableStateOf(ultimo?.turno ?: Estudiante.TURNO_MATUTINO) }
var activo    by rememberSaveable { mutableStateOf(ultimo?.activo ?: true) }
```

### 2. `registro_datos.xml` — lista completa en JSON

`SharedPreferences` solo maneja clave → valor simple, así que la colección se
serializa con `org.json` (incluido en Android, sin dependencias extra):

```xml
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="estudiantes_json">[{&quot;matricula&quot;:&quot;20234567&quot;,&quot;nombre&quot;:&quot;Juan Perez Lopez&quot;,&quot;carrera&quot;:&quot;Ingeniería en Sistemas&quot;,&quot;turno&quot;:&quot;Vespertino&quot;,&quot;estatus&quot;:&quot;Inactivo&quot;}]</string>
</map>
```

Ruta interna de ambos archivos:

```
/data/data/com.example.registroestudiantes/shared_prefs/
├── registro_prefs.xml
└── registro_datos.xml
```

## Navegación y paso de parámetros

`navigation/AppNavHost.kt` declara el grafo con `NavHost`, `NavHostController` y `navArgument`.

```kotlin
object Rutas {
    const val REGISTRO = "registro"
    const val LISTA    = "lista"
    const val DETALLE  = "detalle/{matricula}"      // ruta con argumento
    fun detalle(matricula: String) = "detalle/$matricula"
}
```

```kotlin
composable(
    route = Rutas.DETALLE,
    arguments = listOf(navArgument("matricula") { type = NavType.StringType })
) { backStackEntry ->
    val matricula = backStackEntry.arguments?.getString("matricula").orEmpty()
    val estudiante = EstudianteRepository(context).buscarPorMatricula(matricula)
    DetalleScreen(estudiante = estudiante, ...)
}
```

Y desde el formulario se envía el dato:

```kotlin
composable(Rutas.REGISTRO) {
    RegistroScreen(
        onRegistrar = { estudiante ->
            navController.navigate(Rutas.detalle(estudiante.matricula))
        },
        onVerLista = { navController.navigate(Rutas.LISTA) }
    )
}
```

> **Por qué solo se pasa la matrícula y no el objeto completo:** las rutas de
> navegación deben ser cortas y legibles. Se envía la clave y en la pantalla de
> detalle se reconstruye el `Estudiante` leyéndolo de `SharedPreferences`. Es el
> patrón recomendado y evita duplicar o exponer datos dentro de la ruta.

## Cómo ejecutar

```bash
./gradlew assembleDebug             # compilar
./gradlew installDebug              # instalar en el emulador
./gradlew connectedDebugAndroidTest # pruebas instrumentadas
```

## Casos de prueba verificados

| # | Escenario | Resultado |
|---|---|---|
| 1 | Registrar con todos los campos vacíos | `La matricula es obligatoria` |
| 2 | Matrícula `12` (muy corta) | `La matricula debe tener al menos 4 caracteres` |
| 3 | Matrícula válida, nombre vacío | `El nombre completo es obligatorio` |
| 4 | Matrícula y nombre válidos, sin carrera | `Selecciona una carrera` |
| 5 | Registro completo (Vespertino / Inactivo) | Navega a Detalle con los 5 datos |
| 6 | `am force-stop` y reapertura de la app | Formulario recuperado: 20234567, Vespertino, Inactivo |
| 7 | Pantalla de lista | Muestra el estudiante leído de `SharedPreferences` |
| 8 | Verificación del XML interno | Ambas llaves presentes con los valores esperados |

## Capturas de evidencia

En [`docs/capturas/`](docs/capturas/):

| Archivo                              | Contenido                                                |
|--------------------------------------|----------------------------------------------------------|
| `01-formulario-vacio.png`            | Pantalla 1 al abrir la app por primera vez              |
| `02-datos-personales.png`            | Matrícula y nombre capturados                            |
| `03-dropdown-carrera-abierto.png`    | `ExposedDropdownMenu` con las 5 carreras                 |
| `04-carrera-seleccionada.png`        | Carrera elegida en el desplegable                        |
| `05-turno-y-estatus.png`             | `RadioButton` Vespertino y `Switch` en Inactivo         |
| `06-pantalla-detalle.png`            | Pantalla 2 con los datos recibidos por navegación        |
| `07-registro-prefs.xml`              | Archivo XML de configuración clave-valor                 |
| `08-registro-datos.xml`              | Archivo XML con la lista de estudiantes en JSON          |
| `09-persistencia-tras-reabrir.png`   | Datos recuperados tras cerrar y reabrir la app           |
| `10-lista-registros.png`             | Pantalla 3 con el registro leído de SharedPreferences    |
| `11-validacion-campos-vacios.png`    | Mensaje de validación de campos obligatorios             |

## Commits

Mensajes descriptivos con Convention Commits:

- `chore: inicializar proyecto RegistroEstudiantes con Empty Activity`
- `chore(deps): agregar navigation-compose para el grafo de navegacion`
- `feat(data): crear el modelo Estudiante con carrera, turno y estatus`
- `feat(data): agregar PreferencesManager para el ultimo registro clave-valor`
- `feat(data): agregar EstudianteRepository con la lista serializada en JSON`
- `feat(registro): construir la pantalla 1 con TextField, DropdownMenu, RadioButton y Switch`
- `feat(registro): validar los campos obligatorios del formulario`
- `feat(detalle): crear la pantalla 2 que recibe los datos por navegacion`
- `feat(lista): crear la pantalla 3 que lee los registros de SharedPreferences`
- `feat(navigation): configurar NavHost y el paso del argumento matricula`
- `docs: anadir README y capturas de pantalla de la evidencia`

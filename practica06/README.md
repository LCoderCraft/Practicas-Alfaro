# Práctica 06 – Listas dinámicas (LazyColumn y RecyclerView)

Desarrollo Móvil Nativo · Android Studio + Kotlin + Jetpack Compose

Práctica guiada para implementar **listas dinámicas** en Android. El mismo
listado de contactos se construye dos veces: con `LazyColumn` de Compose y con
`RecyclerView` + `Adapter` + `ViewHolder`, para comparar ambos enfoques.

---

## Objetivo

Comprender cómo se renderiza una colección de datos dinámica en pantalla,
que solo dibuja los elementos visibles y **recicla** las vistas que salen de la
pantalla para no agotar la memoria.

## Tecnología utilizada

| Concepto            | Detalle                                             |
|---------------------|-----------------------------------------------------|
| Lenguaje            | Kotlin 2.2                                          |
| SDK de Android      | compileSdk / targetSdk 37, minSdk 28                |
| Interfaz moderna    | Jetpack Compose + Material 3 (`LazyColumn`)         |
| Interfaz clásica    | `RecyclerView` + `Adapter`/`ViewHolder` + layout XML |
| Librerías XML       | Material Components 1.12, ConstraintLayout 2.2.1    |
| Versionado          | Git                                                 |

## Estructura del proyecto

```
app/src/main/java/com/example/practica06/
├── MainActivity.kt                 # Activity mínima: solo llama a App()
├── Contacto.kt                     # Modelo de datos + generador de 12 contactos
├── ListaScreen.kt                  # UI con LazyColumn + Card (versión Compose)
├── RecyclerScreen.kt               # UI con RecyclerView (Linear/GridLayoutManager)
└── ui/
    ├── ContactoAdapter.kt          # Adapter + ViewHolder (patrón RecyclerView)
    └── theme/                      # Colores, tipografía y tema

app/src/main/res/layout/
└── item_contacto.xml               # Diseño del ítem con MaterialCardView
```

## Las dos implementaciones

### 1. `LazyColumn` (Compose) — `ListaScreen.kt`

```kotlin
LazyColumn {
    items(items = contactos, key = { it.id }) { contacto ->
        Card(modifier = Modifier.clickable { onClick() }) { /* contenido */ }
    }
}
```

| Punto clave                | Por qué                                                              |
|----------------------------|----------------------------------------------------------------------|
| `items(contacts)`          | Solo compone las filas visibles en pantalla                          |
| `key = { it.id }`          | Identificador estable: Compose reutiliza bien el item al reordenar   |
| `Modifier.clickable`       | Convierte la `Card` completa en una zona pulsable                    |
| `elevation` en la `Card`   | Sombreado que da profundidad y jerarquía visual al ítem              |

### 2. `RecyclerView` (vistas XML) — `RecyclerScreen.kt` + `ui/ContactoAdapter.kt`

El patrón **Adapter** es el puente entre los datos y la vista:

| Componente              | Responsabilidad                                                     |
|-------------------------|--------------------------------------------------------------------|
| `ContactoViewHolder`    | Guarda las referencias a las vistas de **un** ítem                 |
| `onCreateViewHolder`    | Infla el XML **solo** cuando hace falta un ViewHolder nuevo        |
| `onBindViewHolder`      | Inyecta los datos de la posición actual en el ítem                 |
| `getItemCount()`        | Cantidad total de elementos de la colección                        |
| `getItemId()`           | ID estable, permite que el RecyclerView distinga y recicle bien    |
| `LayoutManager`         | Define la disposición: `LinearLayoutManager` (1 columna) o `GridLayoutManager` (2) |

El RecyclerView se aloja dentro de Compose con `AndroidView`, de modo que ambas
pantallas coexisten en la misma Activity.

> Nota: en `RecyclerView` 1.4.0 el método `hasStableIds()` es `final`. Los IDs
> estables se activan automáticamente porque `getItemId()` devuelve un valor
> distinto de `NO_ID`.

## Reto: mensaje emergente al tocar un contacto

Cada tarjeta captura el toque y muestra un `Toast` con el nombre del contacto:

```kotlin
val context = LocalContext.current

Card(
    modifier = Modifier.clickable {
        Toast.makeText(
            context,
            "Seleccionaste a ${contacto.nombre}",
            Toast.LENGTH_SHORT
        ).show()
    }
)
```

`LocalContext.current` es el puente de Compose hacia la API de Android: sin él
no hay forma de invocar `Toast.makeText`.

## Evidencias

| Captura                                  | Contenido                                  |
|------------------------------------------|--------------------------------------------|
| `01-lazycolumn.png`                      | Lista con `LazyColumn`                     |
| `02-lazycolumn-scroll.png`               | Scroll de la lista Compose                 |
| `03-recyclerview-linearlayoutmanager.png`| Lista con `RecyclerView`                   |
| `04-recyclerview-scroll.png`             | Scroll con reciclado de ViewHolders        |

APK de depuración en `apk/practica06-debug.apk`.

## Cómo ejecutarlo

```bash
gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.example.practica06/.MainActivity
```

El botón superior alterna entre las dos versiones del listado.
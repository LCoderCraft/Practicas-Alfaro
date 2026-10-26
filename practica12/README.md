# Práctica 12 – Consumo de API REST

Aplicación Android desarrollada con Kotlin, Jetpack Compose y Material 3. Consume la API pública PokeAPI para construir una Pokédex de tres pantallas: Tipos → Lista de Pokémon → Detalle.

## Características

- Consumo de API REST con Retrofit y Gson.
- Mapeo de JSON anidado a Data Classes.
- Peticiones asíncronas con Kotlin Coroutines y `Dispatchers.IO`.
- Arquitectura MVVM con `StateFlow`.
- Navegación con Navigation Compose.
- Carga de imágenes con Coil (`AsyncImage`).
- Interfaz inspirada en cartas TCG, con colores por tipo y Pokébola animada.

## API utilizada

Base URL: `https://pokeapi.co/api/v2/`

Endpoints:

- `GET type`
- `GET type/{name}`
- `GET pokemon/{name}`

## Estructura

```text
practica12/
├── app/src/main/java/com/example/practica12/
│   ├── data/
│   │   ├── PokeApi.kt
│   │   ├── PokeModels.kt
│   │   └── PokeRepository.kt
│   ├── presentation/
│   │   ├── PokeViewModel.kt
│   │   ├── ListScreen.kt
│   │   └── PokemonDetailScreen.kt
│   ├── ui/theme/
│   └── MainActivity.kt
├── docs/capturas/
└── docs/codigo/
```

## Ejecución

1. Abrir el proyecto en Android Studio.
2. Sincronizar Gradle.
3. Ejecutar en un emulador o dispositivo con Internet.
4. La app carga los tipos, permite seleccionar uno y muestra el detalle del Pokémon elegido.

## Capturas

Las capturas de ejecución se encuentran en `docs/capturas/` y las capturas del código fuente en `docs/codigo/`.

## Autor

Alfaro Lopez Luis Humberto – Desarrollo Móvil Nativo

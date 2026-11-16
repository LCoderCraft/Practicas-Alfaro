# Practica14 - GPS y Mapas (OpenStreetMap)

App nativa Android en Kotlin + Jetpack Compose que solicita permisos de ubicacion en tiempo de ejecucion, obtiene la posicion actual (latitud/longitud) mediante `FusedLocationProviderClient` y despliega un mapa interactivo de OpenStreetMap (osmdroid) con un marcador en la ubicacion obtenida.

## Que hace
- Solicita `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION` en tiempo de ejecucion.
- Obtiene la ubicacion actual con `FusedLocationProviderClient`.
- Renderiza el mapa con osmdroid (`MapView`) dentro de Compose via `AndroidView`.
- Muestra la ubicacion en tiempo real con un icono rojo personalizado (`MyLocationNewOverlay`).
- Persiste marcadores en Room: toque prolongado sobre el mapa guarda un marcador que se renderiza automaticamente (Flow reactivo).

## Ejecutar
1. Abrir la carpeta en Android Studio.
2. Sync Gradle, conectar dispositivo o emulador con servicios de Google Play.
3. Run `app` o `./gradlew assembleDebug`.

## Evidencia
- `docs/capturas/` - capturas de permiso, mapa y marcador.
- `docs/Practica14_Gps_Mapas.pdf` - documento de la practica.
- `docs/capturas/demo14.mp4` - video demostrativo.
- PDF: `Actividad_12.1_Practica14_GPS_y_Mapas.pdf` (escritorio).

## Reto
- Icono de ubicacion personalizado en rojo (Vector Asset -> Bitmap).
- Persistencia de marcadores con Room (entidad, DAO, Singleton, long press).

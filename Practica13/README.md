# Practica13 - Sensores (Eje X, Y, Z) y Deteccion de Sacudidas

App nativa Android en Kotlin + Jetpack Compose que lee el acelerometro en tiempo real y detecta sacudidas (shake).

## Que hace
- Obtiene `SensorManager` y registra un `SensorEventListener` con `Sensor.TYPE_ACCELEROMETER`.
- Muestra en vivo los valores de Eje X, Eje Y y Eje Z sin bloquear el hilo UI.
- Calcula la magnitud vectorial `sqrt(X^2 + Y^2 + Z^2)` y la compara contra umbral `30 m/s²`.
- Al superar el umbral ejecuta respuesta visual: fondo rosado, tarjeta roja `¡SACUDIDA DETECTADA!` y contador de sacudidas.
- Gestiona ciclo de vida: registra en `ON_RESUME`, desregistra en `ON_PAUSE`.
- Incluye boton `Simular sacudida (emulador)` para video/GIF sin movimiento fisico.

## Ejecutar
1. Abrir la carpeta en Android Studio.
2. Sync Gradle, conectar dispositivo o emulador con sensores.
3. Run `app` o `./gradlew assembleDebug` desde PowerShell.

## Evidencia
- `docs/capturas/` - capturas de ejecucion.
- `docs/codigo/` - capturas del codigo.
- PDF: `Actividad_11.2_Practica13_Sensores.pdf` en el escritorio.
- GIF: `Practica13_Sensores_demo.gif` en el escritorio.

# Practica 15 - Notificaciones Locales y Push (FCM)

App nativa Android en Kotlin + Jetpack Compose que configura y dispara notificaciones locales (NotificationChannel, NotificationManager, PendingIntent) e integra Firebase Cloud Messaging (FCM) para recepcion de notificaciones push, gestion de token, procesamiento de Data Payload y navegacion profunda mediante Deep Links.

## Que hace
- Crea y registra un canal de notificaciones (NotificationChannel) con importancia alta.
- Dispara notificaciones locales con PendingIntent que abre la app en una pantalla especifica.
- Solicita POST_NOTIFICATIONS en Android 13+ y obtiene el FCM Token.
- Muestra el token en la interfaz para pruebas desde Firebase Console o Postman.
- Recibe mensajes en primer/segundo plano y procesa Data Payload.
- Implementa Deep Link que navega automaticamente a una pantalla de detalle.

## Ejecutar
1. Abrir la carpeta en Android Studio.
2. Reemplazar app/google-services.json por el archivo real de tu proyecto Firebase.
3. Sync Gradle, conectar dispositivo o emulador.
4. Run `app` o `./gradlew assembleDebug`.

## Evidencia
- `docs/capturas/` - capturas de permiso, token, notificacion local, bandeja y deep link.
- `docs/capturas/demo15.gif` - video demostrativo de notificacion local y deep link.
- `docs/Actividad_13.2_Practica15_Notificaciones_Locales_y_Push.pdf` - documento de la practica.

## Configuracion Firebase
1. Crear proyecto en Firebase Console.
2. Registrar app con package `com.example.practica15`.
3. Descargar `google-services.json` y colocarlo en `app/`.
4. En Firebase Console > Messaging, enviar mensaje de prueba usando el token mostrado en la app.




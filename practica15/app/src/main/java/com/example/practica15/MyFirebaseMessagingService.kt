package com.example.practica15

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val titulo = message.notification?.title ?: message.data["titulo"] ?: "Notificacion Push"
        val cuerpo = message.notification?.body ?: message.data["mensaje"] ?: ""
        val destino = message.data["destino"] ?: "detalle"
        NotificacionesUtils.mostrarNotificacionLocal(this, titulo, cuerpo, destino)
    }
}

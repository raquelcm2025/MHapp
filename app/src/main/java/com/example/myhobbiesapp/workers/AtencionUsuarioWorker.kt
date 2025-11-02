package com.example.myhobbiesapp.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build // <-- ¡IMPORTACIÓN NECESARIA!
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myhobbiesapp.R
import com.example.myhobbiesapp.ui.activity.SplashActivity

class AtencionUsuarioWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        try {
            crearCanalDeNotificacion()

            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            )

            //  Construir la notificación
            val builder = NotificationCompat.Builder(context, "CANAL_REGRESO")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("¡MyHobbiesApp te extraña!")
                .setContentText("Vuelve a MyHobbiesApp, ¡conecta más!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            // Mostrar la notificación
            with(NotificationManagerCompat.from(context)) {
                notify(123, builder.build())
            }

            return Result.success()

        } catch (_: Exception) {
            return Result.failure()
        }
    }

    private fun crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Recordatorios"
            val descripcion = "Recordatorios para volver a la app"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel("CANAL_REGRESO", nombre, importancia).apply {
                description = descripcion
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }

    }
}
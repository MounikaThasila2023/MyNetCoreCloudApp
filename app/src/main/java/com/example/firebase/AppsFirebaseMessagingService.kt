package com.example.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.mynetcorecloudapp.MainActivity
import com.example.mynetcorecloudapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.netcore.android.smartechpush.SmartPush
import org.json.JSONObject
import java.lang.ref.WeakReference

class AppsFirebaseMessagingService : FirebaseMessagingService() {

    val channelId = "smtDefault"
    val channelName = "Smartech Default"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        SmartPush.getInstance(WeakReference(this)).setDevicePushToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val pushFromSmartech:Boolean = SmartPush.getInstance(WeakReference(applicationContext)).isNotificationFromSmartech(
            JSONObject(remoteMessage.data.toString())
        )
        if(pushFromSmartech){
            SmartPush.getInstance(WeakReference(applicationContext)).handlePushNotification(remoteMessage.data.toString())
        } else {
            if (remoteMessage.notification != null) {
                generateNotification(
                    remoteMessage.notification!!.title!!,
                    remoteMessage.notification!!.body!!
                )
            }
        }
    }

    private fun generateNotification(title: String, message: String) {

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setSmallIcon(R.drawable.logo_dev)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
            notificationManager.createNotificationChannel(notificationChannel)

        }
        notificationManager.notify(0, builder.build())
    }

    @SuppressLint("RemoteViewLayout")
    private fun getRemoteView(title: String, message: String): RemoteViews {

        val remoteView =
            RemoteViews("com.example.fcmpushnotification", R.layout.custom_notification).apply {
                setTextViewText(R.id.notify_title, title)
                setTextViewText(R.id.notify_des, message)
                setImageViewResource(R.id.notify_logo, R.drawable.logo_dev)
            }
        return remoteView
    }
}

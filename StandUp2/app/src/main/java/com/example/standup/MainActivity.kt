package com.example.standup

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val notifyIntent = Intent(this, AlarmReceiver::class.java)

        val alarmUp = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            notifyIntent,
            PendingIntent.FLAG_NO_CREATE
        ) != null

        switchButton.isChecked = alarmUp

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            NOTIFICATION_ID,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP
                val repeatIntent = AlarmManager.INTERVAL_FIFTEEN_MINUTES
                val triggerTime = SystemClock.elapsedRealtime() + repeatIntent
                alarmManager.setInexactRepeating(
                    alarmType,
                    repeatIntent,
                    triggerTime,
                    pendingIntent
                )
            } else {
                notificationManager.cancelAll()
                alarmManager.cancel(pendingIntent)
            }
        }

        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            Companion.PRIMARY_CHANNEL_ID,
            "Stand up notification",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = "Notifies every 15 minutes to stand up and walk"
        }
        notificationManager.createNotificationChannel(notificationChannel)
    }

    companion object {
        private const val NOTIFICATION_ID = 10
        private const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

}

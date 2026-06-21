package com.manoj.financetracker.presentation.payment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.manoj.financetracker.R

object NotificationHelper {

    private const val CHANNEL_ID =
        "pending_payment_channel"

    private const val NOTIFICATION_ID = 1001

    fun showPendingPaymentNotification(
        context: Context,
        amount: String,
        category: String
    ) {

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "Pending Payments",
                NotificationManager.IMPORTANCE_HIGH
            )

        manager.createNotificationChannel(
            channel
        )

        val yesIntent =
            Intent(
                context,
                PaymentNotificationReceiver::class.java
            ).apply {

                action = "PAYMENT_YES"
            }

        val noIntent =
            Intent(
                context,
                PaymentNotificationReceiver::class.java
            ).apply {

                action = "PAYMENT_NO"
            }

        val yesPendingIntent =
            PendingIntent.getBroadcast(
                context,
                1,
                yesIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val noPendingIntent =
            PendingIntent.getBroadcast(
                context,
                2,
                noIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )
        val openAppIntent =
            Intent(
                context,
                com.manoj.financetracker.MainActivity::class.java
            )

        val openAppPendingIntent =
            PendingIntent.getActivity(
                context,
                100,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.mipmap.ic_launcher
                )
                .setContentTitle(
                    "💳 Pending Payment"
                )
                .setContentText(
                    "₹$amount for $category is pending approval"
                )
                .setContentIntent(
                    openAppPendingIntent
                )
                .addAction(
                    0,
                    "YES",
                    yesPendingIntent
                )
                .addAction(
                    0,
                    "NO",
                    noPendingIntent
                )
                .setAutoCancel(true)
                .build()

        manager.notify(
            NOTIFICATION_ID,
            notification
        )
    }
}
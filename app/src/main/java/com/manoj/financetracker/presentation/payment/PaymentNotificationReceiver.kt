package com.manoj.financetracker.presentation.payment

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.manoj.financetracker.data.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class PaymentNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {

        val prefs =
            UserPreferences(context)

        when (intent.action) {

            "PAYMENT_YES" -> {

                CoroutineScope(
                    Dispatchers.IO
                ).launch {

                    val amount =
                        prefs.pendingAmountFlow.first()

                    val category =
                        prefs.pendingCategoryFlow.first()

                    if (
                        amount.isNotEmpty()
                    ) {

                        prefs.saveExpenseFromNotification(
                            amount =
                                amount.toIntOrNull() ?: 0,
                            category = category
                        )

                        prefs.clearPendingTransaction()
                    }
                }

                val manager =
                    context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                    ) as NotificationManager

                manager.cancel(1001)
            }

            "PAYMENT_NO" -> {

                CoroutineScope(
                    Dispatchers.IO
                ).launch {

                    prefs.clearPendingTransaction()
                }

                val manager =
                    context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                    ) as NotificationManager

                manager.cancel(1001)
            }
        }
    }
}
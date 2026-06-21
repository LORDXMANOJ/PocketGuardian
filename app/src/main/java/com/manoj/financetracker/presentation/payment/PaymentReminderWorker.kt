package com.manoj.financetracker.presentation.payment

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.manoj.financetracker.data.UserPreferences
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class PaymentReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {

        android.util.Log.d(
            "PocketGuardian",
            "WORKER STARTED"
        )
        val prefs =
            UserPreferences(applicationContext)

        val pendingAmount =
            runBlocking {
                prefs.pendingAmountFlow.first()
            }

        if (pendingAmount.isEmpty()) {

            android.util.Log.d(
                "PocketGuardian",
                "NO PENDING TRANSACTION"
            )

            return Result.success()
        }

        val amount =
            inputData.getString("amount")
                ?: return Result.success()

        val category =
            inputData.getString("category")
                ?: return Result.success()

        android.util.Log.d(
            "PocketGuardian",
            "SHOWING NOTIFICATION"
        )

        NotificationHelper.showPendingPaymentNotification(
            context = applicationContext,
            amount = amount,
            category = category
        )

        return Result.success()
    }
}
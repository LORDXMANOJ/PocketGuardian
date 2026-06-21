package com.manoj.financetracker.presentation

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Data
import java.util.concurrent.TimeUnit
import com.manoj.financetracker.presentation.payment.PaymentReminderWorker
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manoj.financetracker.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Expense(
    val amount: Int,
    val category: String,
    val date: String
)

data class PendingTransaction(
    val amount: String,
    val category: String,
    val merchantName: String = "",
    val upiId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

class PocketGuardianViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application)

    var totalAllowance = mutableStateOf(0)
    var daysLeft = mutableStateOf(1)
    var originalDays = mutableStateOf(1)
    var todayBudget = mutableStateOf(0)

    var isLoading = mutableStateOf(true)
    var isSetupComplete = mutableStateOf(false)
    val recentExpenses = mutableStateListOf<Expense>()
    var pendingTransaction =
        mutableStateOf<PendingTransaction?>(null)

    init {
        viewModelScope.launch {
            isSetupComplete.value = prefs.isSetupFlow.first()
            if (isSetupComplete.value) {
                totalAllowance.value = prefs.allowanceFlow.first()
                val savedDays = prefs.daysFlow.first()

                daysLeft.value = savedDays
                originalDays.value = savedDays
                todayBudget.value = prefs.budgetFlow.first()

                val pendingAmount =
                    prefs.pendingAmountFlow.first()

                if (pendingAmount.isNotEmpty()) {

                    pendingTransaction.value =
                        PendingTransaction(
                            amount = pendingAmount,
                            category =
                                prefs.pendingCategoryFlow.first(),
                            merchantName =
                                prefs.pendingMerchantFlow.first(),
                            upiId =
                                prefs.pendingUpiFlow.first()
                        )

                    println(
                        "PENDING PAYMENT RESTORED = ${pendingTransaction.value}"
                    )
                }

                // Load the receipts right away
                val savedExpenses = prefs.expensesFlow.first()

                println("DEBUG SAVED EXPENSES = $savedExpenses")

                if (savedExpenses.isNotEmpty()) {
                    val parsedList = savedExpenses.split(";").mapNotNull { item ->
                        val parts = item.split(",")

                        if (parts.size == 3)
                            Expense(
                                amount = parts[1].toIntOrNull() ?: 0,
                                category = parts[0],
                                date = parts[2]
                            )
                        else
                            null
                    }
                    recentExpenses.addAll(parsedList)

                    println("DEBUG PARSED SIZE = ${parsedList.size}")

                    parsedList.forEach {
                        println("DEBUG EXPENSE = ${it.category} ${it.amount}")
                    }
                }

                // Run the time check on boot
                checkMidnightRollover()
            }
            isLoading.value = false
        }
    }

    // NEW: We separated the time check so the UI can trigger it anytime!
    fun checkMidnightRollover() {
        println("DEBUG CHECK ROLLOVER CALLED")
        viewModelScope.launch {
            if (!isSetupComplete.value) return@launch // Don't check if we haven't set up yet

            val savedDateStr = prefs.lastDateFlow.first()
            val todayStr = LocalDate.now().toString()

            if (savedDateStr.isNotEmpty() && savedDateStr != todayStr) {
                val savedDate = LocalDate.parse(savedDateStr)
                val daysPassed = ChronoUnit.DAYS.between(savedDate, LocalDate.now()).toInt()
                println("DEBUG SAVED DATE = $savedDate")
                println("DEBUG TODAY DATE = ${LocalDate.now()}")
                println("DEBUG DAYS PASSED = $daysPassed")
                println("DEBUG DAYS LEFT BEFORE = ${daysLeft.value}")

                if (daysPassed > 0) {
                    println("ORIGINAL DAYS = ${originalDays.value}")
                    println("DAYS PASSED = $daysPassed")
                    val newDaysLeft = maxOf(1, originalDays.value - daysPassed)

                    daysLeft.value = newDaysLeft
                    println("DEBUG DAYS LEFT AFTER = ${daysLeft.value}")
                    todayBudget.value = totalAllowance.value / daysLeft.value
                    prefs.performMidnightRollover(
                        newDaysLeft,
                        todayBudget.value,
                        todayStr
                    )
                }
            }
        }
    }

    private fun getExpensesString(): String {
        return recentExpenses.joinToString(";") {
            "${it.category},${it.amount},${it.date}"
        }
    }

    fun setupAllowance(allowance: Int, days: Int) {
        val safeDays = if (days > 0) days else 1
        val budget = allowance / safeDays

        totalAllowance.value = allowance
        daysLeft.value = safeDays
        originalDays.value = safeDays
        todayBudget.value = budget
        isSetupComplete.value = true

        val todayStr = LocalDate.now().toString()

        viewModelScope.launch {
            prefs.saveSetup(allowance, safeDays, budget, todayStr)
        }
    }

    fun createPendingTransaction(
        amount: String,
        category: String
    ) {

        pendingTransaction.value =
            PendingTransaction(
                amount = amount,
                category = category
            )

        viewModelScope.launch {

            prefs.savePendingTransaction(
                amount = amount,
                category = category,
                merchant = "",
                upiId = ""
            )
        }
        val workData =
            Data.Builder()
                .putString("amount", amount)
                .putString("category", category)
                .build()

        val reminderWork =
            OneTimeWorkRequestBuilder<PaymentReminderWorker>()
                .setInitialDelay(
                    1,
                    TimeUnit.MINUTES
                )
                .setInputData(workData)
                .build()

        WorkManager
            .getInstance(getApplication())
            .enqueue(reminderWork)
    }

    fun updateMerchantInfo(
        merchantName: String,
        upiId: String
    ) {

        val current =
            pendingTransaction.value ?: return

        pendingTransaction.value =
            current.copy(
                merchantName = merchantName,
                upiId = upiId
            )

        viewModelScope.launch {

            prefs.savePendingTransaction(
                amount = current.amount,
                category = current.category,
                merchant = merchantName,
                upiId = upiId
            )
        }
    }

    fun confirmPendingTransaction() {

        val transaction =
            pendingTransaction.value ?: return

        saveExpense(
            transaction.amount,
            transaction.category
        )

        pendingTransaction.value = null
        val manager =
            getApplication<Application>()
                .getSystemService(
                    android.content.Context.NOTIFICATION_SERVICE
                ) as android.app.NotificationManager

        manager.cancel(1001)

        viewModelScope.launch {
            prefs.clearPendingTransaction()
        }
    }

    fun cancelPendingTransaction() {

        pendingTransaction.value = null

        val manager =
            getApplication<Application>()
                .getSystemService(
                    android.content.Context.NOTIFICATION_SERVICE
                ) as android.app.NotificationManager

        manager.cancel(1001)

        viewModelScope.launch {
            prefs.clearPendingTransaction()
        }
    }

    fun saveExpense(amountStr: String, category: String) {
        val amount = amountStr.toIntOrNull() ?: 0
        if (amount > 0) {
            recentExpenses.add(
                Expense(
                    amount = amount,
                    category = category,
                    date = LocalDate.now().toString()
                )
            )
            todayBudget.value -= amount
            totalAllowance.value -= amount

            viewModelScope.launch {
                prefs.updateBudgetAndExpenses(
                    totalAllowance.value,
                    todayBudget.value,
                    getExpensesString()
                )
            }
        }
    }
}
package com.manoj.financetracker.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "pocket_guardian_storage")

class UserPreferences(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val ALLOWANCE = intPreferencesKey("allowance")
        val DAYS = intPreferencesKey("days")
        val BUDGET = intPreferencesKey("budget")
        val IS_SETUP = booleanPreferencesKey("is_setup")
        val EXPENSES_LIST = stringPreferencesKey("expenses_list")
        // NEW: A folder to remember the exact day we last used the app
        val LAST_DATE = stringPreferencesKey("last_date")
        val PENDING_AMOUNT =
            stringPreferencesKey("pending_amount")

        val PENDING_CATEGORY =
            stringPreferencesKey("pending_category")

        val PENDING_MERCHANT =
            stringPreferencesKey("pending_merchant")

        val PENDING_UPI =
            stringPreferencesKey("pending_upi")

        val REMINDER_COUNT =
            intPreferencesKey("reminder_count")

    }

    val isSetupFlow: Flow<Boolean> = dataStore.data.map { it[IS_SETUP] ?: false }
    val allowanceFlow: Flow<Int> = dataStore.data.map { it[ALLOWANCE] ?: 0 }
    val daysFlow: Flow<Int> = dataStore.data.map { it[DAYS] ?: 1 }
    val budgetFlow: Flow<Int> = dataStore.data.map { it[BUDGET] ?: 0 }
    val expensesFlow: Flow<String> = dataStore.data.map { it[EXPENSES_LIST] ?: "" }
    // NEW: Reading the date
    val lastDateFlow: Flow<String> = dataStore.data.map { it[LAST_DATE] ?: "" }

    val pendingAmountFlow: Flow<String> =
        dataStore.data.map {
            it[PENDING_AMOUNT] ?: ""
        }

    val pendingCategoryFlow: Flow<String> =
        dataStore.data.map {
            it[PENDING_CATEGORY] ?: ""
        }

    val pendingMerchantFlow: Flow<String> =
        dataStore.data.map {
            it[PENDING_MERCHANT] ?: ""
        }

    val pendingUpiFlow: Flow<String> =
        dataStore.data.map {
            it[PENDING_UPI] ?: ""
        }

    val reminderCountFlow: Flow<Int> =
        dataStore.data.map {
            it[REMINDER_COUNT] ?: 0
        }

    // Updated to save the date when we first set up
    suspend fun saveSetup(allowance: Int, days: Int, budget: Int, date: String) {
        dataStore.edit { prefs ->
            prefs[ALLOWANCE] = allowance
            prefs[DAYS] = days
            prefs[BUDGET] = budget
            prefs[IS_SETUP] = true
            prefs[EXPENSES_LIST] = ""
            prefs[LAST_DATE] = date
        }
    }

    suspend fun updateBudgetAndExpenses(allowance: Int, budget: Int, expensesString: String) {
        dataStore.edit { prefs ->
            prefs[ALLOWANCE] = allowance
            prefs[BUDGET] = budget
            prefs[EXPENSES_LIST] = expensesString
        }
    }

    // NEW: A special function that wipes the slate clean for a new day
    suspend fun performMidnightRollover(newDays: Int, newBudget: Int, newDate: String) {
        dataStore.edit { prefs ->
            prefs[DAYS] = newDays
            prefs[BUDGET] = newBudget
            prefs[LAST_DATE] = newDate
        }
    }

    suspend fun savePendingTransaction(
        amount: String,
        category: String,
        merchant: String,
        upiId: String
    ) {

        dataStore.edit { prefs ->

            prefs[PENDING_AMOUNT] = amount
            prefs[PENDING_CATEGORY] = category
            prefs[PENDING_MERCHANT] = merchant
            prefs[PENDING_UPI] = upiId
        }
    }

    suspend fun clearPendingTransaction() {

        dataStore.edit { prefs ->

            prefs.remove(PENDING_AMOUNT)
            prefs.remove(PENDING_CATEGORY)
            prefs.remove(PENDING_MERCHANT)
            prefs.remove(PENDING_UPI)
            prefs.remove(REMINDER_COUNT)
        }
    }
    suspend fun saveExpenseFromNotification(
        amount: Int,
        category: String
    ) {

        dataStore.edit { prefs ->

            val currentAllowance =
                prefs[ALLOWANCE] ?: 0

            val currentBudget =
                prefs[BUDGET] ?: 0

            val currentExpenses =
                prefs[EXPENSES_LIST] ?: ""

            val today =
                java.time.LocalDate.now().toString()

            val newExpense =
                "$category,$amount,$today"

            val updatedExpenses =
                if (currentExpenses.isEmpty())
                    newExpense
                else
                    "$currentExpenses;$newExpense"

            prefs[ALLOWANCE] =
                currentAllowance - amount

            prefs[BUDGET] =
                currentBudget - amount

            prefs[EXPENSES_LIST] =
                updatedExpenses
        }
    }
    suspend fun incrementReminderCount() {

        dataStore.edit { prefs ->

            val current =
                prefs[REMINDER_COUNT] ?: 0

            prefs[REMINDER_COUNT] =
                current + 1
        }
    }
    suspend fun resetReminderCount() {

        dataStore.edit { prefs ->

            prefs[REMINDER_COUNT] = 0
        }
    }

}
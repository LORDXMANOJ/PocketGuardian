package com.manoj.financetracker.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.manoj.financetracker.presentation.dashboard.DashboardScreen
import com.manoj.financetracker.presentation.expense.AddExpenseScreen
import com.manoj.financetracker.presentation.payment.PaymentConfirmationScreen
import com.manoj.financetracker.presentation.payment.PaymentReviewScreen
import com.manoj.financetracker.presentation.payment.PaymentScreen
import com.manoj.financetracker.presentation.payment.QRScannerScreen
import com.manoj.financetracker.presentation.payment.UpiPaymentHelper
import com.manoj.financetracker.presentation.setup.SetupScreen

object Screen {
    const val Setup = "setup"
    const val Dashboard = "dashboard"
    const val AddExpense = "add_expense"
    const val Payment = "payment"
    const val QRScanner = "qr_scanner"
    const val PaymentReview = "payment_review"
    const val PaymentConfirmation = "payment_confirmation"
}

@Composable
fun PocketGuardianNav() {

    val navController = rememberNavController()
    val sharedViewModel: PocketGuardianViewModel = viewModel()
    val context = LocalContext.current

    if (sharedViewModel.isLoading.value) {
        return
    }

    NavHost(
        navController = navController,
        startDestination =
            if (sharedViewModel.isSetupComplete.value)
                Screen.Dashboard
            else
                Screen.Setup
    ) {

        composable(Screen.Setup) {

            SetupScreen(
                viewModel = sharedViewModel,
                onSetupComplete = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.Setup) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Dashboard) {

            DashboardScreen(
                viewModel = sharedViewModel,

                onAddExpenseClick = {
                    navController.navigate(Screen.AddExpense)
                },

                onPayAndTrackClick = {
                    navController.navigate(Screen.Payment)
                }
            )
        }

        composable(Screen.AddExpense) {

            AddExpenseScreen(
                viewModel = sharedViewModel,

                onBackClick = {
                    navController.popBackStack()
                },

                onSaveComplete = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Payment) {

            PaymentScreen(

                viewModel = sharedViewModel,

                onBackClick = {
                    navController.popBackStack()
                },

                onContinueClick = {

                    navController.navigate(
                        Screen.QRScanner
                    )
                }
            )
        }

        composable(Screen.QRScanner) {

            QRScannerScreen(

                onQrScanned = { qrData ->

                    println("QR RESULT = $qrData")

                    if (
                        !qrData.startsWith("upi://pay") ||
                        !qrData.contains("pa=")
                    ) {

                        println("INVALID QR")

                        navController.popBackStack()

                        return@QRScannerScreen
                    }

                    val upiId =
                        Regex("pa=([^&]+)")
                            .find(qrData)
                            ?.groupValues
                            ?.get(1)
                            ?: ""

                    val merchantName =
                        Regex("pn=([^&]+)")
                            .find(qrData)
                            ?.groupValues
                            ?.get(1)
                            ?: "Unknown Merchant"

                    sharedViewModel.updateMerchantInfo(
                        merchantName = merchantName.replace("%20", " "),
                        upiId = upiId
                    )

                    println("MERCHANT = $merchantName")
                    println("UPI ID = $upiId")

                    navController.navigate(
                        Screen.PaymentReview
                    )
                },

                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PaymentReview) {

            PaymentReviewScreen(

                viewModel = sharedViewModel,

                onPayNow = {

                    val pending =
                        sharedViewModel.pendingTransaction.value

                    if (pending != null) {

                        UpiPaymentHelper.launchUpiPayment(
                            context = context,
                            upiId = pending.upiId,
                            merchantName = pending.merchantName,
                            amount = pending.amount
                        )
                    }
                },

                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.PaymentConfirmation) {

            PaymentConfirmationScreen(

                viewModel = sharedViewModel,

                onBackToDashboard = {

                    navController.navigate(
                        Screen.Dashboard
                    ) {

                        popUpTo(Screen.Dashboard) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}
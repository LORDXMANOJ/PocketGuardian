package com.manoj.financetracker.presentation.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manoj.financetracker.presentation.PocketGuardianViewModel

@Composable
fun PaymentConfirmationScreen(
    viewModel: PocketGuardianViewModel,
    onBackToDashboard: () -> Unit
) {

    val pending = viewModel.pendingTransaction.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Confirm Payment",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Text(
            text = "Amount: ₹${pending?.amount ?: "0"}",
            fontSize = 20.sp
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Category: ${pending?.category ?: ""}",
            fontSize = 20.sp
        )

        Spacer(
            modifier = Modifier.height(40.dp)
        )

        Button(
            onClick = {

                viewModel.confirmPendingTransaction()

                onBackToDashboard()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("✅ Payment Successful")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = {

                viewModel.cancelPendingTransaction()

                onBackToDashboard()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("❌ Payment Failed")
        }
    }
}
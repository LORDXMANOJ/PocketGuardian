package com.manoj.financetracker.presentation.payment

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manoj.financetracker.presentation.PocketGuardianViewModel

@Composable
fun PaymentReviewScreen(
    viewModel: PocketGuardianViewModel,
    onPayNow: () -> Unit,
    onBack: () -> Unit
) {

    val pending = viewModel.pendingTransaction.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Review Payment",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Merchant",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = pending?.merchantName ?: "Unknown Merchant"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "UPI ID",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = pending?.upiId ?: "Unknown"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Amount",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "₹${pending?.amount ?: "0"}"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = "Category",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = pending?.category ?: ""
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        Button(
            onClick = onPayNow,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("💳 Pay With UPI")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
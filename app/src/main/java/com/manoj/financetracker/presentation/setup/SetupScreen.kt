package com.manoj.financetracker.presentation.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manoj.financetracker.presentation.PocketGuardianViewModel

@Composable
fun SetupScreen(
    viewModel: PocketGuardianViewModel,
    onSetupComplete: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Pocket Guardian",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "New Reload Received!",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Total Allowance (₹)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next // Shows a "Next" arrow to jump to the next box
                ),
                singleLine = true, // Prevents the enter key from making a new line!
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = days,
                onValueChange = { days = it },
                label = { Text("How many days must it last?") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done // Shows the "Tick" checkmark to close keyboard
                ),
                singleLine = true, // Prevents the enter key from making a new line!
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
            )

            Button(
                onClick = {
                    val parsedAmount = amount.toIntOrNull() ?: 0
                    val parsedDays = days.toIntOrNull() ?: 1

                    if (parsedAmount > 0) {
                        viewModel.setupAllowance(parsedAmount, parsedDays)
                        onSetupComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Start Surviving", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
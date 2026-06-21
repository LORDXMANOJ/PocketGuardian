package com.manoj.financetracker.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import java.time.LocalDate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.manoj.financetracker.presentation.PocketGuardianViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: PocketGuardianViewModel,
    onAddExpenseClick: () -> Unit,
    onPayAndTrackClick: () -> Unit
) {
    // NEW: The Wake-Up Alarm!
    // This tells the Brain to check the date EVERY TIME you switch back to the app.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkMidnightRollover()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val pending = viewModel.pendingTransaction.value

    if (pending != null) {

        AlertDialog(
            onDismissRequest = {},

            title = {
                Text("⚠ Pending Payment")
            },

            text = {

                Text(
                    """
Merchant: ${pending.merchantName}

Amount: ₹${pending.amount}

Category: ${pending.category}

Did this payment succeed?
                """.trimIndent()
                )
            },

            confirmButton = {

                Button(
                    onClick = {
                        viewModel.confirmPendingTransaction()
                    }
                ) {
                    Text("YES")
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {
                        viewModel.cancelPendingTransaction()
                    }
                ) {
                    Text("NO")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Pocket Guardian", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddExpenseClick,
                icon = { Icon(Icons.Filled.Add, contentDescription = "Add Expense") },
                text = { Text("Add Expense") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Today's Budget", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Text(
                text = "₹${viewModel.todayBudget.value}",
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
            ) {
                Text(
                    text = if (viewModel.todayBudget.value >= 0) "🟢 Safe to spend" else "🔴 Danger Zone",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Total Left", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "₹${viewModel.totalAllowance.value}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Days Left", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${viewModel.daysLeft.value}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onPayAndTrackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("💳 Pay & Track")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { /* WhatsApp linked later */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Generate Parent Report")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Today's Expenses", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            if (viewModel.recentExpenses.isEmpty()) {
                Text(
                    text = "No expenses logged today. Keep saving!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 32.dp)
                )
            } else {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    viewModel.recentExpenses
                        .filter {
                            it.date == LocalDate.now().toString()
                        }
                        .reversed()
                        .forEach { expense ->
                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = expense.category, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text(text = "-₹${expense.amount}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
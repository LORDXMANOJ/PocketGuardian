package com.manoj.financetracker.presentation.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manoj.financetracker.presentation.PocketGuardianViewModel // Importing the Brain!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: PocketGuardianViewModel, // DOOR OPEN: Accepting the Brain
    onBackClick: () -> Unit,
    onSaveComplete: () -> Unit // Tells the bridge we are done saving
) {
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Breakfast") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Expense", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer, titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
            Text(text = "How much did you spend?", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = LocalTextStyle.current.copy(fontSize = 32.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp),
                singleLine = true
            )

            Text(text = "Select Category", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryButton("Breakfast", selectedCategory, Modifier.weight(1f)) { selectedCategory = it }
                    CategoryButton("Lunch", selectedCategory, Modifier.weight(1f)) { selectedCategory = it }
                    CategoryButton("Dinner", selectedCategory, Modifier.weight(1f)) { selectedCategory = it }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryButton("Petrol", selectedCategory, Modifier.weight(1f)) { selectedCategory = it }
                    CategoryButton("Snacks/Tea", selectedCategory, Modifier.weight(1f)) { selectedCategory = it }
                    CategoryButton("Friends", selectedCategory, Modifier.weight(1f)) { selectedCategory = it }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryButton("College", selectedCategory, Modifier.weight(1f)) { selectedCategory = it }
                    CategoryButton("Xerox", selectedCategory, Modifier.weight(1f)) { selectedCategory = it }
                    CategoryButton("Others", selectedCategory, Modifier.weight(1f)) { selectedCategory = it }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    // SEND TO BRAIN: Deduct math and save to list
                    viewModel.saveExpense(amount, selectedCategory)
                    // TELL BRIDGE TO GO HOME
                    onSaveComplete()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Save Expense", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CategoryButton(text: String, selectedCategory: String, modifier: Modifier = Modifier, onClick: (String) -> Unit) {
    if (text == selectedCategory) {
        Button(onClick = { onClick(text) }, modifier = modifier) { Text(text) }
    } else {
        OutlinedButton(onClick = { onClick(text) }, modifier = modifier) { Text(text) }
    }
}
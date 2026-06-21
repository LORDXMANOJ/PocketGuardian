package com.manoj.financetracker.presentation.payment

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    viewModel: com.manoj.financetracker.presentation.PocketGuardianViewModel,
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {

    var amount by remember {
        mutableStateOf("")
    }

    var selectedCategory by remember {
        mutableStateOf("Lunch")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pay & Track",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Enter Amount",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->

                    amount = newValue.filter {
                        it.isDigit()
                    }
                },

                singleLine = true,

                label = {
                    Text("Amount (₹)")
                },

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Select Category",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    CategoryButton(
                        "Breakfast",
                        selectedCategory
                    ) {
                        selectedCategory = it
                    }

                    CategoryButton(
                        "Lunch",
                        selectedCategory
                    ) {
                        selectedCategory = it
                    }

                    CategoryButton(
                        "Dinner",
                        selectedCategory
                    ) {
                        selectedCategory = it
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    CategoryButton(
                        "Petrol",
                        selectedCategory
                    ) {
                        selectedCategory = it
                    }

                    CategoryButton(
                        "Friends",
                        selectedCategory
                    ) {
                        selectedCategory = it
                    }

                    CategoryButton(
                        "College",
                        selectedCategory
                    ) {
                        selectedCategory = it
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    CategoryButton(
                        "Snacks",
                        selectedCategory
                    ) {
                        selectedCategory = it
                    }

                    CategoryButton(
                        "Xerox",
                        selectedCategory
                    ) {
                        selectedCategory = it
                    }

                    CategoryButton(
                        "Others",
                        selectedCategory
                    ) {
                        selectedCategory = it
                    }
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {

                    if (amount.isNotEmpty()) {

                        viewModel.createPendingTransaction(
                            amount = amount,
                            category = selectedCategory
                        )

                        onContinueClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "Continue to QR Scan"
                )
            }
        }
    }
}

@Composable
private fun CategoryButton(
    text: String,
    selectedCategory: String,
    onClick: (String) -> Unit
) {

    if (text == selectedCategory) {

        Button(
            onClick = {
                onClick(text)
            }
        ) {
            Text(text)
        }

    } else {

        OutlinedButton(
            onClick = {
                onClick(text)
            }
        ) {
            Text(text)
        }
    }
}
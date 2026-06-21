package com.manoj.financetracker.presentation.payment

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun QRScannerScreen(
    onQrScanned: (String) -> Unit,
    onCancel: () -> Unit
) {

    val launcher =
        rememberLauncherForActivityResult(
            contract = ScanContractCustom()
        ) { result ->

            if (result.contents != null) {

                println(
                    "QR RESULT = ${result.contents}"
                )

                onQrScanned(
                    result.contents
                )

            } else {

                onCancel()
            }
        }

    LaunchedEffect(Unit) {

        launcher.launch(
            createScanOptions()
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        CircularProgressIndicator()
    }
}
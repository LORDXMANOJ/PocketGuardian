package com.manoj.financetracker.presentation.payment

import android.content.Context
import android.content.Intent
import android.net.Uri

object UpiPaymentHelper {

    fun launchUpiPayment(
        context: Context,
        upiId: String,
        merchantName: String,
        amount: String
    ) {

        val uri = Uri.parse(
            "upi://pay" +
                    "?pa=$upiId" +
                    "&pn=$merchantName" +
                    "&am=$amount" +
                    "&cu=INR"
        )

        val intent = Intent(Intent.ACTION_VIEW, uri)

        val chooser = Intent.createChooser(
            intent,
            "Choose UPI App"
        )

        context.startActivity(chooser)
    }
}
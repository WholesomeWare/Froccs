package com.csakitheone.froccs.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.android.billingclient.api.*

val products = listOf(
    QueryProductDetailsParams.Product.newBuilder()
        .setProductId("support_one_drink")
        .setProductType(BillingClient.ProductType.INAPP)
        .build()
)

@Preview
@Composable
fun SupportDeveloperLayout(activity: Activity? = null) {
    val context = LocalContext.current

    val billingClient by remember { mutableStateOf(
        BillingClient.newBuilder(context)
            .setListener { billingResult, purchases ->
                val message = if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    "✅"
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    "❌"
                } else {
                    billingResult.responseCode.toString()
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
            .enablePendingPurchases()
            .build()
    ) }
    var isClientReady by remember { mutableStateOf(false) }
    var productDetails by remember { mutableStateOf(listOf<ProductDetails>()) }

    fun connectBillingClient(onSuccess: () -> Unit) {
        billingClient.startConnection(object: BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                connectBillingClient(onSuccess)
            }
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    isClientReady = true
                }
            }
        })
    }

    fun buy(productDetails: ProductDetails) {
        val offerToken = productDetails.subscriptionOfferDetails?.first()?.offerToken

        if (offerToken.isNullOrEmpty() || activity == null) {
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                // retrieve a value for "productDetails" by calling queryProductDetailsAsync()
                .setProductDetails(productDetails)
                // to get an offer token, call ProductDetails.subscriptionOfferDetails()
                // for a list of offers that are available to the user
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .setIsOfferPersonalized(false)
            .build()

        // Launch the billing flow
        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    LaunchedEffect(Unit) {
        connectBillingClient {
            val queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                    .setProductList(products)
                    .build()

            billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                    billingResult,
                    productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetails = productDetailsList
                }
            }
        }
    }

    Column {
        for (productDetail in productDetails) {
            Button(
                enabled = isClientReady,
                onClick = {
                    buy(productDetail)
                }
            ) {
                Text(text = productDetail.name)
            }
        }
    }
}
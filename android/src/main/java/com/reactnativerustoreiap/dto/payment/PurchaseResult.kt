package com.reactnativerustoreiap.dto.payment

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.reactnativerustoreiap.interfaces.JsOutput
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult.PurchaseResult

internal data class PurchaseResult(val purchaseResult: PurchaseResult): JsOutput {
  override fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putString("purchaseId", purchaseResult.purchaseId)
      putString("productId", purchaseResult.productId)
      putString("orderId", purchaseResult.orderId)
      putString("subscriptionToken", purchaseResult.subscriptionToken)
      putString("finishCode", purchaseResult.finishCode.toString())
      putString("invoiceId", purchaseResult.invoiceId)
    }
  }
}

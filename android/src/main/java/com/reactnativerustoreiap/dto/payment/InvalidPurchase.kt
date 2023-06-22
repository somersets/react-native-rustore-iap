package com.reactnativerustoreiap.dto.payment

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.reactnativerustoreiap.interfaces.JsOutput
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult.InvalidPurchase

internal data class InvalidPurchase(val invalidPurchase: InvalidPurchase): JsOutput {
  override fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putString("purchaseId", invalidPurchase.purchaseId)
      invalidPurchase.errorCode?.let { putInt("errorCode", it) }
      putString("productId", invalidPurchase.productId)
      putString("orderId", invalidPurchase.orderId)
      putString("invoiceId", invalidPurchase.invoiceId)
      invalidPurchase.quantity?.let { putInt("quantity", it) }
    }
  }
}

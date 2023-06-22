package com.reactnativerustoreiap.dto.purchase

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.reactnativerustoreiap.interfaces.JsOutput
import ru.rustore.sdk.billingclient.model.purchase.Purchase

internal data class RuStorePurchaseInfo(
  val purchase: Purchase?,
): JsOutput {
  override fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putString("amount", purchase?.amount?.toString())
      putString("amountLabel", purchase?.amountLabel)
      putString("currency", purchase?.currency)
      putString("description", purchase?.description)
      putString("invoiceId", purchase?.invoiceId)
      putString("language", purchase?.language)
      putString("orderId", purchase?.orderId)
      putString("productId", purchase?.productId)
      putString("productType", purchase?.productType?.toString())
      putString("purchaseId", purchase?.purchaseId)
      putString("purchaseTime", purchase?.purchaseTime?.toString())
      putString("quantity", purchase?.quantity?.toString())
      putString("subscriptionToken", purchase?.subscriptionToken)
      putString("purchaseState", purchase?.purchaseState?.toString())
    }
  }

}

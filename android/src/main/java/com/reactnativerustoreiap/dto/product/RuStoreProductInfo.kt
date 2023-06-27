package com.reactnativerustoreiap.dto.product

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.reactnativerustoreiap.interfaces.JsOutput
import ru.rustore.sdk.billingclient.model.product.Product

internal data class RuStoreProductInfo(val product: Product): JsOutput {
  override fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      putString("productId", product.productId)
      putString("productType", product.productType.toString())
      putString("productStatus", product.productStatus.toString())
      putString("priceLabel", product.priceLabel)
      product.price?.let { putInt("price", it) }
      putString("currency", product.currency)
      putString("language", product.language)
      putString("title", product.title)
      putString("description", product.description)
      putString("imageUrl", product.imageUrl.toString())
      putString("promoImageUrl", product.promoImageUrl.toString())
    }
  }

}

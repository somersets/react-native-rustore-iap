package com.reactnativerustoreiap.dto.product

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.reactnativerustoreiap.interfaces.JsOutput
import ru.rustore.sdk.billingclient.model.product.ProductSubscription

internal data class RuStoreProductSubscriptionInfo(val productSubscription: ProductSubscription?): JsOutput {
  override fun toMap(): WritableMap {
    val freeTrialPeriod = Arguments.createMap()
    productSubscription?.freeTrialPeriod.let {
      freeTrialPeriod.putInt(
        "days",
        it?.days ?: 0
      )
    }
    productSubscription?.freeTrialPeriod.let {
      freeTrialPeriod.putInt(
        "months",
        it?.months ?: 0
      )
    }
    productSubscription?.freeTrialPeriod.let {
      freeTrialPeriod.putInt(
        "years",
        it?.years ?: 0
      )
    }

    val gracePeriod = Arguments.createMap()
    productSubscription?.gracePeriod.let { gracePeriod.putInt("days", it?.days ?: 0) }
    productSubscription?.gracePeriod.let {
      gracePeriod.putInt(
        "months",
        it?.months ?: 0
      )
    }
    productSubscription?.gracePeriod.let {
      gracePeriod.putInt(
        "years",
        it?.years ?: 0
      )
    }

    val introductoryPricePeriod = Arguments.createMap()
    productSubscription?.introductoryPricePeriod.let {
      introductoryPricePeriod.putInt(
        "days",
        it?.days ?: 0
      )
    }
    productSubscription?.introductoryPricePeriod.let {
      introductoryPricePeriod.putInt(
        "months",
        it?.months ?: 0
      )
    }
    productSubscription?.introductoryPricePeriod.let {
      introductoryPricePeriod.putInt(
        "years",
        it?.years ?: 0
      )
    }

    val subscriptionPeriod = Arguments.createMap()
    productSubscription?.subscriptionPeriod.let {
      subscriptionPeriod.putInt(
        "days",
        it?.days ?: 0
      )
    }
    productSubscription?.subscriptionPeriod.let {
      subscriptionPeriod.putInt(
        "months",
        it?.months ?: 0
      )
    }
    productSubscription?.subscriptionPeriod.let {
      subscriptionPeriod.putInt(
        "years",
        it?.years ?: 0
      )
    }

    return Arguments.createMap().apply {
      putString("introductoryPrice", productSubscription?.introductoryPrice)
      putString(
        "introductoryPriceAmount",
        productSubscription?.introductoryPriceAmount
      )
      putMap("freeTrialPeriod", freeTrialPeriod)
      putMap("gracePeriod", gracePeriod)
      putMap("introductoryPricePeriod", introductoryPricePeriod)
      putMap("subscriptionPeriod", subscriptionPeriod)
    }
  }
}

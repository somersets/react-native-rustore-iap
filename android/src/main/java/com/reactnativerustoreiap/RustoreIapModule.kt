package com.reactnativerustoreiap

import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.product.ProductsResponse
import ru.rustore.sdk.billingclient.model.purchase.Purchase
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult
import ru.rustore.sdk.core.tasks.OnCompleteListener

class RustoreIapModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "RustoreIap"
  }

  @ReactMethod
  fun checkRuStorePurchasesAvailability(cb: Callback) {
    RuStoreBillingClient.purchases.checkPurchasesAvailability()
      .addOnCompleteListener(object : OnCompleteListener<FeatureAvailabilityResult> {
        override fun onFailure(throwable: Throwable) {
          // Process error
          cb.invoke(throwable, null);
        }
        override fun onSuccess(result: FeatureAvailabilityResult) {
          when (result) {
            is FeatureAvailabilityResult.Available -> {
              cb.invoke(null, true);
            }
            is FeatureAvailabilityResult.Unavailable -> {
              cb.invoke(null, false);
            }
          }
        }
      })
  }

  @ReactMethod
  fun getRuStoreProducts(productIds: ReadableArray, promise: Promise) {
    try {
      val ids = productIds.toArrayList().toList() as List<String>;
      val productsResponse = RuStoreBillingClient.products.getProducts(ids).await();
      promise.resolve(productsResponse.products?.toTypedArray());
    } catch (e: Throwable) {
      promise.reject("Getting products error!", e);
    }
  }

  @ReactMethod
  fun getRuStorePurchases(promise: Promise) {
    try {
      val purchaseResponse = RuStoreBillingClient.purchases.getPurchases().await();
      promise.resolve(purchaseResponse.purchases?.toTypedArray());
    } catch (e: Throwable) {
      promise.reject("Getting products error!", e);
    }
  }

}

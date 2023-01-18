package com.reactnativerustoreiap

import com.facebook.react.bridge.*
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.product.Product
import ru.rustore.sdk.billingclient.model.product.ProductType
import ru.rustore.sdk.billingclient.model.purchase.PaymentFinishCode
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.response.ConfirmPurchaseResponse
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult
import ru.rustore.sdk.core.tasks.OnCompleteListener


class RustoreIapModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "RustoreIap"
  }

  @ReactMethod
  fun checkRuStorePurchasesAvailability(promise: Promise) {
    RuStoreBillingClient.purchases.checkPurchasesAvailability()
      .addOnCompleteListener(object : OnCompleteListener<FeatureAvailabilityResult> {
        override fun onFailure(throwable: Throwable) {
          // Process error
          promise.reject(throwable, null);
        }

        override fun onSuccess(result: FeatureAvailabilityResult) {
          when (result) {
            is FeatureAvailabilityResult.Available -> {
              promise.resolve(true);
            }
            is FeatureAvailabilityResult.Unavailable -> {
              promise.resolve(false);
            }
          }
        }
      })
  }

  @ReactMethod
  fun purchaseProduct(product: Product, promise: Promise) {
    RuStoreBillingClient.purchases.purchaseProduct(product.productId)
      .addOnSuccessListener { paymentResult ->
        handlePaymentResult(paymentResult, product) { it: Throwable?, response: ConfirmPurchaseResponse? ->
          if (it != null) {
            promise.reject(it)
          } else {
            promise.resolve(response)
          }
        }
      }
      .addOnFailureListener {
        promise.reject(it)
      }
  }

  private fun handlePaymentResult(paymentResult: PaymentResult, product: Product, callback: (Throwable?, ConfirmPurchaseResponse?) -> Unit) {
    when (paymentResult) {
      is PaymentResult.InvalidPurchase -> {
        paymentResult.purchaseId?.let { deletePurchase(it) }
      }
      is PaymentResult.PurchaseResult -> {
        when (paymentResult.finishCode) {
          PaymentFinishCode.SUCCESSFUL_PAYMENT -> {
            if (product.productType == ProductType.CONSUMABLE) {
              confirmPurchase(paymentResult.purchaseId) { it: Throwable?, response: ConfirmPurchaseResponse? ->
                if (it != null) {
                  callback.invoke(it, null)
                  throw it;
                } else {
                  callback.invoke(null, response)
                }
              }

            }
          }
          PaymentFinishCode.CLOSED_BY_USER,
          PaymentFinishCode.UNHANDLED_FORM_ERROR,
          PaymentFinishCode.PAYMENT_TIMEOUT,
          PaymentFinishCode.DECLINED_BY_SERVER,
          PaymentFinishCode.RESULT_UNKNOWN,
          -> {
            deletePurchase(paymentResult.purchaseId)
          }
        }
      }
      else -> Unit
    }
  }

  @ReactMethod
  fun getRuStoreProducts(productIds: ReadableArray, promise: Promise) {
    try {
      val nativeArrayListIds: ArrayList<String> = ArrayList()

      for (i in 0 until productIds.size()) {
        nativeArrayListIds.add(productIds.getString(i))
      }
      val productsResponse = RuStoreBillingClient.products.getProducts(nativeArrayListIds.toList()).await();
      promise.resolve(productsResponse.products);
    } catch (e: Throwable) {
      promise.reject("Getting products error!", e);
    }
  }

  @ReactMethod
  fun getRuStorePurchases(promise: Promise) {
    try {
      val purchaseResponse = RuStoreBillingClient.purchases.getPurchases().await();
      promise.resolve(purchaseResponse.purchases);
    } catch (e: Throwable) {
      promise.reject("Getting products error!", e);
    }
  }

  private fun deletePurchase(purchaseId: String){
    RuStoreBillingClient.purchases.deletePurchase(purchaseId)
      .addOnSuccessListener { response ->
      }
      .addOnFailureListener {
      }
  }

  private fun confirmPurchase(purchaseId: String, callback: (Throwable?, ConfirmPurchaseResponse?) -> Unit) {
    RuStoreBillingClient.purchases.confirmPurchase(purchaseId)
      .addOnSuccessListener { response ->
        callback.invoke(null, response);
      }
      .addOnFailureListener {
        callback.invoke(it, null);
      }
  }

}

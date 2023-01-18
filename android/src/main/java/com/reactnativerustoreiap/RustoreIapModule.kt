package com.reactnativerustoreiap

import android.net.Uri
import com.facebook.react.bridge.*
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.model.product.*
import ru.rustore.sdk.billingclient.model.purchase.PaymentFinishCode
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.billingclient.model.purchase.response.ConfirmPurchaseResponse
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult
import ru.rustore.sdk.core.tasks.OnCompleteListener
import java.util.concurrent.Flow.Subscription


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
  fun purchaseRuStoreProduct(product: ReadableMap, promise: Promise) {
    val subscription = product.getMap("subscription")
    val freeTrialPeriod = subscription?.getMap("freeTrialPeriod")
    val gracePeriod = subscription?.getMap("gracePeriod")
    val introductoryPricePeriod = subscription?.getMap("introductoryPricePeriod");
    val subscriptionPeriod = subscription?.getMap("subscriptionPeriod");

    val nativeFreeTrialPeriod: SubscriptionPeriod? = freeTrialPeriod?.let {
      SubscriptionPeriod(
        days = it.getInt("days"),
        months = it.getInt("months"),
        years = it.getInt("years"),
      )
    }

    val nativeGracePeriod: SubscriptionPeriod? = gracePeriod?.let {
      SubscriptionPeriod(
        days = it.getInt("days"),
        months = it.getInt("months"),
        years = it.getInt("years"),
    )
    }
    val nativeIntroductoryPricePeriod: SubscriptionPeriod? = introductoryPricePeriod?.let {
      SubscriptionPeriod(
        days = it.getInt("days"),
        months = it.getInt("months"),
        years = it.getInt("years"),
      )
    }

    val nativeSubscriptionPeriod: SubscriptionPeriod? = subscriptionPeriod?.let {
      SubscriptionPeriod(
        days = it.getInt("days"),
        months = it.getInt("months"),
        years = it.getInt("years"),
      )
    }


    val nativeProductSubscription: ProductSubscription = ProductSubscription(
      freeTrialPeriod = nativeFreeTrialPeriod,
      gracePeriod = nativeGracePeriod,
      introductoryPrice = subscription?.getString("introductoryPrice"),
      introductoryPriceAmount = subscription?.getString("introductoryPriceAmount"),
      introductoryPricePeriod = nativeIntroductoryPricePeriod,
      subscriptionPeriod = nativeSubscriptionPeriod,
    )

    val nativeProduct: Product = Product(
      currency = product.getString("currency"),
      description = product.getString("description"),
      imageUrl = Uri.parse(product.getString("imageUrl")),
      language = product.getString("language"),
      price = product.getInt("price"),
      priceLabel = product.getString("priceLabel"),
      productId = product.getString("productId").toString(),
      productStatus = enumValueOf<ProductStatus>(product.getString("productStatus")!!),
      productType = enumValueOf<ProductType>(product.getString("productType")!!),
      promoImageUrl = Uri.parse(product.getString("promoImageUrl")),
      subscription = nativeProductSubscription,
      title = product.getString("title")
    )



    RuStoreBillingClient.purchases.purchaseProduct(nativeProduct.productId)
      .addOnSuccessListener { paymentResult ->
        handlePaymentResult(
          paymentResult,
          nativeProduct
        ) { it: Throwable?, response: ConfirmPurchaseResponse? ->
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

  private fun handlePaymentResult(
    paymentResult: PaymentResult,
    product: Product,
    callback: (Throwable?, ConfirmPurchaseResponse?) -> Unit
  ) {
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
      val ids = productIds.toArrayList().toList() as List<String>
      val productsResponse = RuStoreBillingClient.products.getProducts(ids).await();

      val products = Arguments.createArray()

      productsResponse.products?.let { it ->
        for (nativeProduct in it) {
          val product = Arguments.createMap()
          val subscription = Arguments.createMap()
          val freeTrialPeriod = Arguments.createMap()
          val gracePeriod = Arguments.createMap()
          val introductoryPricePeriod = Arguments.createMap()
          val subscriptionPeriod = Arguments.createMap()

          product.putString("productId", nativeProduct.productId)
          product.putString("productType", nativeProduct.productType.toString())
          product.putString("productStatus", nativeProduct.productStatus.toString())
          product.putString("priceLabel", nativeProduct.priceLabel)
          nativeProduct.price?.let { product.putInt("price", it) }
          product.putString("currency", nativeProduct.currency)
          product.putString("language", nativeProduct.language)
          product.putString("title", nativeProduct.title)
          product.putString("description", nativeProduct.description)
          product.putString("imageUrl", nativeProduct.imageUrl.toString())
          product.putString("promoImageUrl", nativeProduct.promoImageUrl.toString())
          subscription.putString("introductoryPrice", nativeProduct.subscription?.introductoryPrice)
          subscription.putString(
            "introductoryPriceAmount",
            nativeProduct.subscription?.introductoryPriceAmount
          )

          nativeProduct.subscription?.freeTrialPeriod?.let {
            freeTrialPeriod.putInt(
              "day",
              it.days
            )
          }
          nativeProduct.subscription?.freeTrialPeriod?.let {
            freeTrialPeriod.putInt(
              "months",
              it.months
            )
          }
          nativeProduct.subscription?.freeTrialPeriod?.let {
            freeTrialPeriod.putInt(
              "years",
              it.years
            )
          }

          nativeProduct.subscription?.gracePeriod?.let { gracePeriod.putInt("day", it.days) }
          nativeProduct.subscription?.gracePeriod?.let { gracePeriod.putInt("months", it.months) }
          nativeProduct.subscription?.gracePeriod?.let { gracePeriod.putInt("years", it.years) }

          nativeProduct.subscription?.introductoryPricePeriod?.let {
            introductoryPricePeriod.putInt(
              "day",
              it.days
            )
          }
          nativeProduct.subscription?.introductoryPricePeriod?.let {
            introductoryPricePeriod.putInt(
              "months",
              it.months
            )
          }
          nativeProduct.subscription?.introductoryPricePeriod?.let {
            introductoryPricePeriod.putInt(
              "years",
              it.years
            )
          }

          nativeProduct.subscription?.subscriptionPeriod?.let {
            subscriptionPeriod.putInt(
              "day",
              it.days
            )
          }
          nativeProduct.subscription?.subscriptionPeriod?.let {
            subscriptionPeriod.putInt(
              "months",
              it.months
            )
          }
          nativeProduct.subscription?.subscriptionPeriod?.let {
            subscriptionPeriod.putInt(
              "years",
              it.years
            )
          }

          subscription.putMap("freeTrialPeriod", freeTrialPeriod)
          subscription.putMap("gracePeriod", gracePeriod)
          subscription.putMap("introductoryPricePeriod", introductoryPricePeriod)
          subscription.putMap("subscriptionPeriod", subscriptionPeriod)
          product.putMap("subscription", subscription)
          products.pushMap(product)
        }

      }

      promise.resolve(products);
    } catch (e: Throwable) {
      promise.reject("Getting products error!", e);
    }
  }

  @ReactMethod
  fun getRuStorePurchases(promise: Promise) {
    try {
      val purchaseResponse = RuStoreBillingClient.purchases.getPurchases().await()
      val purchases = Arguments.createArray()

      purchaseResponse.purchases?.let { it ->
        for (nativeProduct in it) {
          val purchase = Arguments.createMap()

          purchase.putString("purchaseId", nativeProduct.purchaseId)
          purchase.putString("productId", nativeProduct.productId)
          purchase.putString("description", nativeProduct.description)
          purchase.putString("language", nativeProduct.language)
          purchase.putString("purchaseTime", nativeProduct.purchaseTime.toString())
          purchase.putString("orderId", nativeProduct.orderId)
          purchase.putString("amountLabel", nativeProduct.amountLabel)
          nativeProduct.amount?.let { purchase.putInt("amount", it) }
          purchase.putString("currency", nativeProduct.currency)
          nativeProduct.quantity?.let { purchase.putInt("quantity", it) }
          purchase.putString("purchaseState", nativeProduct.purchaseState.toString())
          purchase.putString(
            "developerPayload",
            nativeProduct.developerPayload
          )

          purchases.pushMap(purchase)
        }
      }

      promise.resolve(purchases);
    } catch (e: Throwable) {
      promise.reject("Getting products error!", e);
    }
  }

  private fun deletePurchase(purchaseId: String) {
    RuStoreBillingClient.purchases.deletePurchase(purchaseId)
      .addOnSuccessListener { response ->
      }
      .addOnFailureListener {
      }
  }

  private fun confirmPurchase(
    purchaseId: String,
    callback: (Throwable?, ConfirmPurchaseResponse?) -> Unit
  ) {
    RuStoreBillingClient.purchases.confirmPurchase(purchaseId)
      .addOnSuccessListener { response ->
        callback.invoke(null, response);
      }
      .addOnFailureListener {
        callback.invoke(it, null);
      }
  }

}

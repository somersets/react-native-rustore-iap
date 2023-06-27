package com.reactnativerustoreiap

import android.net.Uri
import com.facebook.react.bridge.*
import com.reactnativerustoreiap.dto.OnSuccessInfo
import com.reactnativerustoreiap.dto.payment.InvalidPurchase
import com.reactnativerustoreiap.dto.payment.PurchaseResult
import com.reactnativerustoreiap.dto.product.RuStoreProductInfo
import com.reactnativerustoreiap.dto.purchase.RuStorePurchaseInfo
import com.reactnativerustoreiap.dto.product.RuStoreProductSubscriptionInfo
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import ru.rustore.sdk.billingclient.RuStoreBillingClientFactory
import ru.rustore.sdk.billingclient.model.product.*
import ru.rustore.sdk.billingclient.model.purchase.PaymentFinishCode
import ru.rustore.sdk.billingclient.model.purchase.PaymentResult
import ru.rustore.sdk.core.feature.model.FeatureAvailabilityResult
import ru.rustore.sdk.core.tasks.OnCompleteListener


class RustoreIapModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {
  companion object {
    lateinit var ruStoreBillingClient: RuStoreBillingClient
  }

  override fun getName(): String {
    return "RustoreIap"
  }

  @ReactMethod
  fun initializeRuStore(
    consoleAppId: String,
    deeplinkScheme: String = "",
    externalPaymentLoggerFactory: Boolean?,
  ) {
    if (RuStoreBillingClientFactory.isSingletonInitialized) return;
    if (externalPaymentLoggerFactory != null && externalPaymentLoggerFactory) {
      currentActivity?.let {
        ruStoreBillingClient = RuStoreBillingClientFactory.create(
          it.application,
          consoleAppId,
          deeplinkScheme,
          externalPaymentLoggerFactory = { tag -> PaymentLogger(tag) },
          true,
        )
      }
    } else {
      currentActivity?.let {
        ruStoreBillingClient = RuStoreBillingClientFactory.create(
          it.application,
          consoleAppId,
          deeplinkScheme,
          null,
          false,
        )
      }
    }
  }

  @ReactMethod
  fun checkRuStorePurchasesAvailability(promise: Promise) {
    if (!RuStoreBillingClientFactory.isSingletonInitialized) {
      promise.resolve(false)
      return;
    }

    ruStoreBillingClient.purchases.checkPurchasesAvailability()
      .addOnCompleteListener(object : OnCompleteListener<FeatureAvailabilityResult> {
        override fun onFailure(throwable: Throwable) {
          // Process error
          promise.reject(throwable, null)
        }

        override fun onSuccess(result: FeatureAvailabilityResult) {
          when (result) {
            is FeatureAvailabilityResult.Available -> {
              promise.resolve(true)
            }
            is FeatureAvailabilityResult.Unavailable -> {
              promise.resolve(result.cause.message)
            }
          }
        }
      })
  }

  @ReactMethod
  fun purchaseRuStoreProduct(product: ReadableMap, developerPayload: String?, promise: Promise) {
    if (!RuStoreBillingClientFactory.isSingletonInitialized) return;

    val subscription = product.getMap("subscription")
    val freeTrialPeriod = subscription?.getMap("freeTrialPeriod")
    val gracePeriod = subscription?.getMap("gracePeriod")
    val introductoryPricePeriod = subscription?.getMap("introductoryPricePeriod")
    val subscriptionPeriod = subscription?.getMap("subscriptionPeriod")


    val nativeFreeTrialPeriod: SubscriptionPeriod? = freeTrialPeriod?.let {
      SubscriptionPeriod(
        days = freeTrialPeriod.getInt("days"),
        months = freeTrialPeriod.getInt("months"),
        years = freeTrialPeriod.getInt("years"),
      )
    }


    val nativeGracePeriod: SubscriptionPeriod? = gracePeriod?.let {
      SubscriptionPeriod(
        days = gracePeriod.getInt("days"),
        months = gracePeriod.getInt("months"),
        years = gracePeriod.getInt("years"),
      )
    }

    val nativeIntroductoryPricePeriod: SubscriptionPeriod? = introductoryPricePeriod?.let {
      SubscriptionPeriod(
        days = introductoryPricePeriod.getInt("days"),
        months = introductoryPricePeriod.getInt("months"),
        years = introductoryPricePeriod.getInt("years"),
      )
    }


    val nativeSubscriptionPeriod: SubscriptionPeriod? = subscriptionPeriod?.let {
      SubscriptionPeriod(
        days = subscriptionPeriod.getInt("days"),
        months = subscriptionPeriod.getInt("months"),
        years = subscriptionPeriod.getInt("years"),
      )
    }


    val nativeProductSubscription = ProductSubscription(
      freeTrialPeriod = nativeFreeTrialPeriod,
      gracePeriod = nativeGracePeriod,
      introductoryPrice = subscription?.getString("introductoryPrice"),
      introductoryPriceAmount = subscription?.getString("introductoryPriceAmount"),
      introductoryPricePeriod = nativeIntroductoryPricePeriod,
      subscriptionPeriod = nativeSubscriptionPeriod,
    )

    val nativeProduct = Product(
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



    ruStoreBillingClient.purchases.purchaseProduct(nativeProduct.productId)
      .addOnSuccessListener { paymentResult ->
        handlePaymentResult(
          paymentResult,
          nativeProduct,
          developerPayload,
          promise,
        )
      }
      .addOnFailureListener {
        promise.reject(it)
      }
  }

  private fun handlePaymentResult(
    paymentResult: PaymentResult,
    product: Product,
    developerPayload: String?,
    promise: Promise
  ) {
    if (!RuStoreBillingClientFactory.isSingletonInitialized) return;

    when (paymentResult) {
      is PaymentResult.InvalidPurchase -> {
        paymentResult.purchaseId?.let { deleteRuStorePurchase(it, null) }
        val payRes = InvalidPurchase(paymentResult).toMap()
        promise.resolve(payRes)
      }
      is PaymentResult.PurchaseResult -> {
        when (paymentResult.finishCode) {
          PaymentFinishCode.SUCCESSFUL_PAYMENT -> {
            if (product.productType == ProductType.CONSUMABLE ||
              product.productType == ProductType.SUBSCRIPTION
            ) {
              confirmPurchase(paymentResult.purchaseId, developerPayload, promise)
              val payRes = PurchaseResult(paymentResult).toMap()
              promise.resolve(payRes)
            }
          }
          PaymentFinishCode.CLOSED_BY_USER,
          PaymentFinishCode.UNHANDLED_FORM_ERROR,
          PaymentFinishCode.PAYMENT_TIMEOUT,
          PaymentFinishCode.DECLINED_BY_SERVER,
          PaymentFinishCode.RESULT_UNKNOWN,
          -> {
            deleteRuStorePurchase(paymentResult.purchaseId, null)
            val payRes = PurchaseResult(paymentResult).toMap()
            promise.resolve(payRes)
          }
        }
      }
      else -> Unit
    }
  }

  @ReactMethod
  fun getRuStorePurchaseInfo(purchaseId: String, promise: Promise) {
    if (!RuStoreBillingClientFactory.isSingletonInitialized) return;

    try {
      val purchaseInfo = ruStoreBillingClient.purchases.getPurchaseInfo(purchaseId).await()
      val purchaseResponseMap = RuStorePurchaseInfo(purchaseInfo.purchase).toMap()
      promise.resolve(purchaseResponseMap)
    } catch (e: Throwable) {
      promise.reject("Getting purchase info error!", e)
    }

  }

  @ReactMethod
  fun getRuStoreProducts(productIds: ReadableArray, promise: Promise) {
    if (!RuStoreBillingClientFactory.isSingletonInitialized) return;

    try {
      val ids = productIds.toArrayList().toList() as List<String>
      val productsResponse = ruStoreBillingClient.products.getProducts(ids).await()

      val products = Arguments.createArray()

      productsResponse.products?.let { it ->
        for (nativeProduct in it) {
          val product = RuStoreProductInfo(nativeProduct).toMap()
          val subscription = RuStoreProductSubscriptionInfo(nativeProduct.subscription).toMap()

          product.putMap("subscription", subscription)
          products.pushMap(product)
        }

      }

      promise.resolve(products)
    } catch (e: Throwable) {
      promise.reject("Getting products error!", e)
    }
  }

  @ReactMethod
  fun getRuStorePurchases(promise: Promise) {
    if (!RuStoreBillingClientFactory.isSingletonInitialized) return;

    try {
      val purchaseResponse = ruStoreBillingClient.purchases.getPurchases().await()
      val purchases = Arguments.createArray()

      purchaseResponse.purchases?.let { it ->
        for (nativeProduct in it) {
          val purchase = RuStorePurchaseInfo(nativeProduct).toMap()
          purchases.pushMap(purchase)
        }
      }

      promise.resolve(purchases)
    } catch (e: Throwable) {
      promise.reject("Getting products error!", e)
    }
  }

  @ReactMethod
  fun deleteRuStorePurchase(purchaseId: String, promise: Promise?) {
    if (!RuStoreBillingClientFactory.isSingletonInitialized) return;

    try {
      ruStoreBillingClient.purchases.deletePurchase(purchaseId)
        .addOnSuccessListener { response ->
          val deletePurchaseResponse = OnSuccessInfo(response).toMap()
          promise?.resolve(deletePurchaseResponse)
        }
        .addOnFailureListener {}
    } catch (e: Throwable) {
      promise?.reject("On delete purchase error!", e)
    }
  }

  @ReactMethod
  fun confirmPurchase(
    purchaseId: String,
    developerPayload: String?,
    promise: Promise
  ) {
    if (!RuStoreBillingClientFactory.isSingletonInitialized) return;

    ruStoreBillingClient.purchases.confirmPurchase(purchaseId, developerPayload)
      .addOnSuccessListener { response ->
        val confirmPurchaseResponse = OnSuccessInfo(response).toMap()
        promise.resolve(confirmPurchaseResponse)
      }
      .addOnFailureListener {
        promise.reject(it)
      }
  }

}

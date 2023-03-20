package com.reactnativerustoreiap

import android.os.Bundle
import android.os.PersistableBundle
import com.facebook.react.ReactActivity
import ru.rustore.sdk.billingclient.RuStoreBillingClient
import java.io.IOException

class RustoreActivity : ReactActivity() {

  override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
    super.onCreate(savedInstanceState, persistentState)
    val ruStoreAppId = getString(R.string.rustore_app_id)
    val ruStoreDeeplinkScheme = getString(R.string.rustore_deeplinkScheme)
    if (ruStoreAppId.isEmpty()) {
      throw IOException("RuStore application identifier is not set")
    }
    RuStoreBillingClient.init(
      application,
      ruStoreAppId,
      ruStoreDeeplinkScheme,
      externalPaymentLoggerFactory = { tag -> PaymentLogger(tag) },
      true,
    )
  }
}

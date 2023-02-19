package com.reactnativerustoreiap

import android.content.Intent
import com.facebook.react.ReactActivity
import ru.rustore.sdk.billingclient.RuStoreBillingClient

class RustoreIntent : ReactActivity() {
  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)

    RuStoreBillingClient.onNewIntent(intent)
  }
}

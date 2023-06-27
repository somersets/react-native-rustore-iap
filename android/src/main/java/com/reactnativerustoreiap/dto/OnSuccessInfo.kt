package com.reactnativerustoreiap.dto

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.reactnativerustoreiap.interfaces.JsOutput
import ru.rustore.sdk.billingclient.model.common.ResponseWithCode

internal data class OnSuccessInfo<T:ResponseWithCode>(val info: T): JsOutput {
  override fun toMap(): WritableMap {
    return Arguments.createMap().apply {
      val responseMeta = Arguments.createMap()
      responseMeta.putString("traceId", info.meta?.traceId)

      putInt("code", info.code)
      putString("errorDescription", info.errorDescription)
      putString("errorMessage", info.errorMessage)
      putMap("meta", responseMeta)
    }
  }

}

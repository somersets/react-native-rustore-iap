package com.reactnativerustoreiap.interfaces

import com.facebook.react.bridge.WritableMap

internal interface JsOutput {
  fun toMap(): WritableMap
}

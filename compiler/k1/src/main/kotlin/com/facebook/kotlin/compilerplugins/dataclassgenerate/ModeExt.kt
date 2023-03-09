// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode

fun Mode?.asBoolean(defaultIfParameterIsNotPassed: Boolean): Boolean =
    when (this) {
      Mode.KEEP -> true
      Mode.OMIT -> false
      // Remove after Kotlin 1.6
      else -> defaultIfParameterIsNotPassed
    }

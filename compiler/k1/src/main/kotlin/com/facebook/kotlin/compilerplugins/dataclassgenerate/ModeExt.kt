/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode

fun Mode?.asBoolean(defaultIfParameterIsNotPassed: Boolean): Boolean =
    when (this) {
      Mode.KEEP -> true
      Mode.OMIT -> false
      // Remove after Kotlin 1.6
      else -> defaultIfParameterIsNotPassed
    }

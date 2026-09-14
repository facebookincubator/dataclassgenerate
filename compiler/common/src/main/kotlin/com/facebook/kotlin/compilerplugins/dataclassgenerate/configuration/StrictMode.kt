/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration

import org.jetbrains.kotlin.name.FqName

class DataClassGenerateStrictModeViolationException(message: String) : RuntimeException(message)

fun generateStrictModeViolationMessage(fqName: FqName?): String {
  val shortName = fqName?.shortName() ?: fqName
  return """
     You are running DataClassGenerate compiler plugin in a STRICT mode, but $fqName is not annotated with @DataClassGenerate.

     Replace $shortName with @DataClassGenerate(toString=Mode.OMIT, equalsHashCode=Mode.KEEP)
     - If $shortName does not need a `toString()` method use `toString=Mode.OMIT`
     - If $shortName does not need `equals()` and hashCode()` use `equalsHashCode=Mode.OMIT` or
     consider replacing it with a regular class.

     Read more:
     1. What is DataClassGenerate? - https://fburl.com/dataclassgenerate_wiki
     2. How to configure @DataClassGenerate annotation? - https://fburl.com/dataclassgenerate
     3. What is STRICT mode? - https://fburl.com/dataclassgenerate_mode
    """
      .trimIndent()
}

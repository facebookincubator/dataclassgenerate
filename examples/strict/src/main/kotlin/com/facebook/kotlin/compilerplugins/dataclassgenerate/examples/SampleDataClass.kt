/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.DataClassGenerate
import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode.KEEP
import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode.OMIT

@DataClassGenerate
data class SampleDataClassWithExplicitOverrides(val str: String) {
  override fun toString() = "dummy"

  override fun equals(other: Any?) = true

  override fun hashCode() = 42
}

@DataClassGenerate data class SampleDataClass(val str: String)

@DataClassGenerate(OMIT, OMIT) data class SampleDataClassGenerationOff(val str: String)

@DataClassGenerate(toString = KEEP) data class SampleDataClassToString(val str: String)

@DataClassGenerate(equalsHashCode = KEEP) data class SampleDataClassEqualsHashcode(val str: String)

@DataClassGenerate(KEEP, KEEP)
data class SampleDataClassToStringEqualsHashcode(val a: Int, val b: String?)

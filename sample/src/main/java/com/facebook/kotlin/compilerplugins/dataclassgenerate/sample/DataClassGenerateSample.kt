/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.sample

import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.DataClassGenerate

data class GenerateSample(val foo: Int)

@DataClassGenerate data class DataClassGenerateSample(val foo: Int)

fun main() {
  // prints "GenerateSample(foo=10)"
  println(GenerateSample(10).toString())
  // prints "com.facebook.kotlin.compilerplugins.dataclassgenerate.sample.DataClassGenerateSample@a"
  println(DataClassGenerateSample(10).toString())
}

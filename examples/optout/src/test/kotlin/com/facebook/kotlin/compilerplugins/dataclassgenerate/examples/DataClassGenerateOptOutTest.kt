/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DataClassGenerateOptOutTest {

  private val nativeToStringRegex =
      "com.facebook.kotlin.compilerplugins.dataclassgenerate.examples.[A-Za-z]+@[0-9a-f]+"

  @Test
  fun `disabled plugin does not remove toString`() {
    val sampleDataClass = SampleDataClass("sample with toString")
    assertThat(sampleDataClass.toString()).doesNotMatch(nativeToStringRegex)
  }

  @Test
  fun `disabled plugin does not remove equals and hashCode`() {
    val sampleDataClass1 = SampleDataClass("Sample with equals and hashCode")
    val sampleDataClass2 = SampleDataClass("Sample with equals and hashCode")

    assertThat(sampleDataClass1).isEqualTo(sampleDataClass2)
    assertThat(sampleDataClass1.hashCode()).isEqualTo(sampleDataClass2.hashCode())
  }
}

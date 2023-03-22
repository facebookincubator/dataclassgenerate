/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class InnerDataClassTest {

  @Test
  fun `inner data class inside interface`() {
    val sampleDataClass1 = WrappingInterface.InnerDataClass("Sample")
    val sampleDataClass2 = WrappingInterface.InnerDataClass("Sample")

    assertThat(sampleDataClass1).isEqualTo(sampleDataClass2)
    assertThat(sampleDataClass1.hashCode()).isEqualTo(sampleDataClass2.hashCode())
  }

  @Test
  fun `inner sealed data class`() {
    val single: SuperWrapper.WrappingSealedClass = SuperWrapper.WrappingSealedClass.Single("sample")
    val s = single.toString()
  }
}

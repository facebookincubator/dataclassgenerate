/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DataClassGenerateTest {

  private val nativeToStringRegex =
      "com.facebook.kotlin.compilerplugins.dataclassgenerate.examples.[A-Za-z]+@[0-9a-f]+"

  @Test
  fun `remove toString if not specified`() {
    val sampleDataClass = SampleDataClass("Sample")
    assertThat(sampleDataClass.toString()).matches(nativeToStringRegex)
  }

  @Test
  fun `do not remove toString if specified`() {
    val sampleDataClass = SampleDataClassToString("sample with toString")
    assertThat(sampleDataClass.toString()).doesNotMatch(nativeToStringRegex)
  }

  @Test
  fun `do not remove equals and hashcode specified`() {
    val sampleDataClass1 = SampleDataClass("Sample")
    val sampleDataClass2 = SampleDataClass("Sample")

    assertThat(sampleDataClass1).isEqualTo(sampleDataClass2)
    assertThat(sampleDataClass1.hashCode()).isEqualTo(sampleDataClass2.hashCode())
  }

  @Test
  fun `remove equals and hashcode if not specified`() {
    val sampleDataClass1 = SampleDataClassGenerationOff("Sample")
    val sampleDataClass2 = SampleDataClassGenerationOff("Sample")

    assertThat(sampleDataClass1).isNotEqualTo(sampleDataClass2)
    assertThat(sampleDataClass1.hashCode()).isNotEqualTo(sampleDataClass2.hashCode())
  }

  @Test
  fun `do not remove equals and hashcode if specified`() {
    val sampleDataClass1 = SampleDataClassEqualsHashcode("Sample with equals and hashCode")
    val sampleDataClass2 = SampleDataClassEqualsHashcode("Sample with equals and hashCode")

    assertThat(sampleDataClass1).isEqualTo(sampleDataClass2)
    assertThat(sampleDataClass1.hashCode()).isEqualTo(sampleDataClass2.hashCode())
  }

  @Test
  fun `multiple data class specifications works together`() {
    val sampleDataClass = SampleDataClassToStringEqualsHashcode(42, null)
    assertThat(sampleDataClass.toString()).doesNotMatch(nativeToStringRegex)
    assertThat(sampleDataClass.copy()).isEqualTo(sampleDataClass)
  }

  /** TODO: T81258596 think about plugin customisation for all possible explicit override cases. */
  @Test
  fun `respect explicit overrides`() {
    val sampleDataClass = SampleDataClassWithExplicitOverrides("Sample")
    assertThat(sampleDataClass.toString()).doesNotMatch(nativeToStringRegex)

    assertThat(sampleDataClass.hashCode()).isEqualTo(42)
    assertThat(sampleDataClass).isEqualTo(null)
  }
}

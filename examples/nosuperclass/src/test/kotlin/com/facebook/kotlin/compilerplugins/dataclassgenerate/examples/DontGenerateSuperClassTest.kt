/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import com.facebook.kotlin.compilerplugins.dataclassgenerate.superclass.DataClassSuper
import org.assertj.core.api.Assertions
import org.junit.Test

class DontGenerateSuperClassTest {

  @Test
  fun `super class is generated`() {
    Assertions.assertThat(A(42, "")).isNotInstanceOf(DataClassSuper::class.java)
    Assertions.assertThat(B(42, "")).isNotInstanceOf(DataClassSuper::class.java)
    Assertions.assertThat(C(42, "")).isNotInstanceOf(DataClassSuper::class.java)
  }
}

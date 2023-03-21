/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperty
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.mock

class CompilerConfigurationPropertyTest {

  private val requiredCliOption = mock<CliOption>().apply { `when`(this.required).thenReturn(true) }
  private val notRequiredCliOption =
      mock<CliOption>().apply { `when`(this.required).thenReturn(false) }

  @Test
  fun `fail CompilerConfigurationProperty init if default is null and cli option not required`() {
    assertThatThrownBy {
          CompilerConfigurationProperty<Boolean>(notRequiredCliOption, mock(), null)
        }
        .isInstanceOf(IllegalArgumentException::class.java)
  }

  @Test
  fun `CompilerConfigurationProperty init if default is null and cli option required`() {
    CompilerConfigurationProperty<Boolean>(requiredCliOption, mock(), null)
  }

  @Test
  fun `CompilerConfigurationProperty init if default is not null and option required`() {
    CompilerConfigurationProperty<Boolean>(requiredCliOption, mock(), true)
  }
}

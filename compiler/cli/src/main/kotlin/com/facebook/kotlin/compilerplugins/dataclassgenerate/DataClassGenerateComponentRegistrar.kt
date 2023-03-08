/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties.ENABLED
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties.GENERATE_SUPER_CLASS
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties.MODE
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.DataClassGenerateExt
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.get
import com.intellij.mock.MockProject
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration

class DataClassGenerateComponentRegistrar : ComponentRegistrar {
  override fun registerProjectComponents(
      project: MockProject,
      configuration: CompilerConfiguration
  ) {
    if (configuration[ENABLED]) {
      DataClassGenerateExt.generateSuperClass = configuration[GENERATE_SUPER_CLASS]

      ClassBuilderInterceptorExtension.registerExtension(
          project, DataClassGenerateInterceptorExtension(configuration[MODE]))
    }
  }

  override val supportsK2
    get() = true
}

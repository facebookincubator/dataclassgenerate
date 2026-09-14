/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

@file:Suppress("DEPRECATION_ERROR")

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties.ENABLED
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties.GENERATE_SUPER_CLASS
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties.MODE
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.DataClassGenerateExt
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.PluginMode
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.get
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)
class DataClassGenerateComponentRegistrar : DataClassGenerateComponentRegistrarBase() {

  override val supportsK2: Boolean = true

  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    if (configuration[ENABLED]) {
      DataClassGenerateExt.generateSuperClass = configuration[GENERATE_SUPER_CLASS]
      val mode = configuration[MODE]

      if (!tryRegisterK1Extension(mode)) {
        IrGenerationExtension.registerExtension(
            DataClassGenerateIrGenerationExtension(mode),
        )
      }
    }
  }

  private fun ExtensionStorage.tryRegisterK1Extension(mode: PluginMode): Boolean {
    return try {
      K1ExtensionRegistrar.register(this, mode)
      true
    } catch (_: NoClassDefFoundError) {
      false
    }
  }
}

/**
 * Isolated into its own object so that referencing [ClassBuilderInterceptorExtension] only triggers
 * class loading when this object is actually accessed. On Kotlin 2.4.0+ where the class was
 * removed, the caller catches [NoClassDefFoundError] and falls back to K2.
 */
@Suppress("DEPRECATION_ERROR")
@OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)
private object K1ExtensionRegistrar {
  fun register(
      storage: org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar.ExtensionStorage,
      mode: PluginMode,
  ) {
    with(storage) {
      org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension.registerExtension(
          DataClassGenerateInterceptorExtension(mode),
      )
    }
  }
}

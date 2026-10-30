/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

@file:Suppress("DEPRECATION_ERROR")

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.PluginMode
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar.ExtensionStorage

/**
 * Registers the K1 class-builder interceptor; false when the running compiler has no K1 backend.
 */
@OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)
internal fun registerK1ExtensionCompat(storage: ExtensionStorage, mode: PluginMode): Boolean {
  return try {
    K1ExtensionRegistrar.register(storage, mode)
    true
  } catch (_: NoClassDefFoundError) {
    false
  }
}

/**
 * Isolated into its own object so that referencing [ClassBuilderInterceptorExtension] only triggers
 * class loading when this object is actually accessed. On Kotlin 2.4+ where the class was removed,
 * the caller catches [NoClassDefFoundError] and falls back to K2.
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

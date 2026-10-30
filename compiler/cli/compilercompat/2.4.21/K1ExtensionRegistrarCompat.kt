/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.PluginMode
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar.ExtensionStorage

/** Kotlin 2.4 removed the K1 JVM backend (`ClassBuilderInterceptorExtension`); K2 IR handles it. */
@Suppress("UNUSED_PARAMETER")
@OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)
internal fun registerK1ExtensionCompat(storage: ExtensionStorage, mode: PluginMode): Boolean = false

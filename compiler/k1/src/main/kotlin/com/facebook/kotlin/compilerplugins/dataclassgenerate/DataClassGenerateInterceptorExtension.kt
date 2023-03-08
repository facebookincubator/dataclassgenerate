/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.PluginMode
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin

class DataClassGenerateInterceptorExtension(
    val pluginMode: PluginMode,
) : ClassBuilderInterceptorExtension {
  override fun interceptClassBuilderFactory(
      interceptedFactory: ClassBuilderFactory,
      bindingContext: BindingContext,
      diagnostics: DiagnosticSink
  ): ClassBuilderFactory {
    return object : ClassBuilderFactory by interceptedFactory {
      override fun newClassBuilder(origin: JvmDeclarationOrigin): ClassBuilder =
          DataClassGenerateBuilder(origin, interceptedFactory.newClassBuilder(origin), pluginMode)

      // Overriding [asBytes] and [asText] methods to be compatible with other
      // android compiler plugins
      // Delegation here leads to ClassCastException if we work with Parcelize
      // or Android Extensions
      override fun asBytes(builder: ClassBuilder?): ByteArray {
        return interceptedFactory.asBytes((builder as DataClassGenerateBuilder).classBuilder)
      }

      override fun asText(builder: ClassBuilder?): String {
        return interceptedFactory.asText((builder as DataClassGenerateBuilder).classBuilder)
      }
    }
  }
}

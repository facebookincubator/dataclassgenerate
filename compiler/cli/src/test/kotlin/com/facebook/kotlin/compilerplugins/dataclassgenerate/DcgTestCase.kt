/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.PluginMode
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile

@OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)
open class DcgTestCase {
  companion object {
    fun compileWithK2(
        vararg srcs: SourceFile,
        dcgConfig: DcgTestConfiguration = DEFAULT_DCG_CONFIG,
    ): KotlinCompilation.Result {
      return makeCompilationContext(srcs, dcgConfig)
          .apply {
            useK2 = true
            supportsK2 = true
          }
          .compile()
    }

    fun compileWithK1(
        vararg srcs: SourceFile,
        dcgConfig: DcgTestConfiguration = DEFAULT_DCG_CONFIG,
    ): KotlinCompilation.Result {
      return makeCompilationContext(srcs, dcgConfig).compile()
    }

    private fun makeCompilationContext(
        code: Array<out SourceFile>,
        dcgConfig: DcgTestConfiguration
    ): KotlinCompilation =
        KotlinCompilation().apply {
          sources = code.asList()
          compilerPluginRegistrars = listOf(DataClassGenerateComponentRegistrar())
          messageOutputStream = System.out
          commandLineProcessors = listOf(DataClassGenerateCommandLineProcessor())
          pluginOptions = dcgConfig.asCliOptions()
        }

    val DEFAULT_DCG_CONFIG =
        DcgTestConfiguration(
            enabled = true, pluginMode = PluginMode.EXPLICIT, generateSuperClass = true)
  }
}

data class DcgTestConfiguration(
    val enabled: Boolean,
    val pluginMode: PluginMode,
    val generateSuperClass: Boolean
) {
  internal fun asCliOptions(): List<PluginOption> {
    val pluginId = DataClassGenerateCommandLineProcessor().pluginId
    return listOf(
        PluginOption(
            pluginId,
            CompilerConfigurationProperties.ENABLED.cliOption.optionName,
            enabled.toString()),
        PluginOption(
            pluginId, CompilerConfigurationProperties.MODE.cliOption.optionName, pluginMode.name),
        PluginOption(
            pluginId,
            CompilerConfigurationProperties.GENERATE_SUPER_CLASS.cliOption.optionName,
            generateSuperClass.toString()),
    )
  }
}

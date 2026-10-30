/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.PluginMode
import com.facebook.kotlin.compilerplugins.dataclassgenerate.superclass.DataClassSuper
import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import java.io.File

@OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)
open class DcgTestCase {
  companion object {
    fun compile(
        vararg srcs: SourceFile,
        dcgConfig: DcgTestConfiguration = DEFAULT_DCG_CONFIG,
    ): JvmCompilationResult {
      return makeCompilationContext(srcs, dcgConfig).compile()
    }

    private fun makeCompilationContext(
        code: Array<out SourceFile>,
        dcgConfig: DcgTestConfiguration,
    ): KotlinCompilation =
        KotlinCompilation().apply {
          sources = code.asList()
          compilerPluginRegistrars = listOf(DataClassGenerateComponentRegistrar())
          messageOutputStream = System.out
          commandLineProcessors = listOf(DataClassGenerateCommandLineProcessor())
          pluginOptions = dcgConfig.asCliOptions()
          // Kotlin 2.4's IR extension resolves `DataClassSuper`; DCG targets export it.
          classpaths =
              listOf(File(DataClassSuper::class.java.protectionDomain.codeSource.location.toURI()))
        }

    val DEFAULT_DCG_CONFIG: DcgTestConfiguration = DcgTestConfiguration(
        enabled = true,
        pluginMode = PluginMode.EXPLICIT,
        generateSuperClass = true,
    )
  }
}

data class DcgTestConfiguration(
    val enabled: Boolean,
    val pluginMode: PluginMode,
    val generateSuperClass: Boolean,
) {
  internal fun asCliOptions(): List<PluginOption> {
    val pluginId = DataClassGenerateCommandLineProcessor().pluginId
    return listOf(
        PluginOption(
            pluginId,
            CompilerConfigurationProperties.ENABLED.cliOption.optionName,
            enabled.toString(),
        ),
        PluginOption(
            pluginId,
            CompilerConfigurationProperties.MODE.cliOption.optionName,
            pluginMode.name,
        ),
        PluginOption(
            pluginId,
            CompilerConfigurationProperties.GENERATE_SUPER_CLASS.cliOption.optionName,
            generateSuperClass.toString(),
        ),
    )
  }
}

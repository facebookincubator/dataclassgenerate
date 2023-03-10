/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties.ENABLED
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties.GENERATE_SUPER_CLASS
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.CompilerConfigurationProperties.MODE
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.PluginMode
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

class DataClassGenerateCommandLineProcessor : CommandLineProcessor {
  override val pluginId: String = "com.facebook.kotlin.dataclassgenerate"
  override val pluginOptions: Collection<AbstractCliOption> =
      CompilerConfigurationProperties.all.map { it.cliOption }

  override fun processOption(
      option: AbstractCliOption,
      value: String,
      configuration: CompilerConfiguration
  ) {
    when (option) {
      ENABLED.cliOption -> configuration.put(ENABLED.configurationKey, value.toBoolean())
      MODE.cliOption -> configuration.put(MODE.configurationKey, PluginMode.valueOf(value))
      GENERATE_SUPER_CLASS.cliOption ->
          configuration.put(GENERATE_SUPER_CLASS.configurationKey, value.toBoolean())
      else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
    }
  }
}

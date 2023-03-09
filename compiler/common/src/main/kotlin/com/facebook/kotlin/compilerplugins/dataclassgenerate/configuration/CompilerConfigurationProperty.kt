// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration

import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

class CompilerConfigurationProperty<T>(
    val cliOption: CliOption,
    val configurationKey: CompilerConfigurationKey<T>,
    val default: T?
) {
  init {
    if (default == null) require(cliOption.required)
  }
}

operator fun <T> CompilerConfiguration.get(property: CompilerConfigurationProperty<T>): T =
    if (property.default != null) get(property.configurationKey, property.default)
    else get(property.configurationKey)!!

// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration

import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object CompilerConfigurationProperties {
  val ENABLED =
      CompilerConfigurationProperty(
          CliOption("enabled", "<true | false>", "whether plugin is enabled", required = false),
          CompilerConfigurationKey("enabled"),
          true)
  val MODE =
      CompilerConfigurationProperty(
          CliOption(
              "mode",
              PluginMode.values().joinToString(prefix = "<", postfix = ">", separator = " | "),
              "defines plugin mode, check ${PluginMode::name}",
              required = false),
          CompilerConfigurationKey("mode"),
          PluginMode.EXPLICIT)

  val GENERATE_SUPER_CLASS =
      CompilerConfigurationProperty(
          CliOption(
              "generateSuperClass",
              "<true | false>",
              "whether a super class should be generated for appropriate data classes",
              required = false),
          CompilerConfigurationKey("generateSuperClass"),
          true)
  val all
    get() = listOf(ENABLED, MODE, GENERATE_SUPER_CLASS)
}

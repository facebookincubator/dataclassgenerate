/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.gradle

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class DataClassGeneratePlugin : KotlinCompilerPluginSupportPlugin {
  override fun getCompilerPluginId() = "com.facebook.kotlin.compilerplugins.dataclassgenerate"

  override fun getPluginArtifact() =
      SubpluginArtifact(
          "com.facebook.kotlin.compilerplugins.dataclassgenerate", "compiler", "1.0.0")

  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
    return kotlinCompilation.target.project.plugins.hasPlugin(DataClassGeneratePlugin::class.java)
  }

  override fun applyToCompilation(
      kotlinCompilation: KotlinCompilation<*>
  ): Provider<List<SubpluginOption>> {
    kotlinCompilation.dependencies {
      compileOnly("com.facebook.kotlin.compilerplugins.dataclassgenerate:annotation:1.0.0")
    }
    return kotlinCompilation.target.project.provider { emptyList() }
  }
}

/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class DataClassGeneratePlugin : KotlinCompilerPluginSupportPlugin {
  override fun getCompilerPluginId() = "com.facebook.kotlin.dataclassgenerate"

  override fun apply(target: Project) {
    target.extensions.create("dataClassGenerate", DataClassGeneratePluginExtension::class.java)
  }

  override fun getPluginArtifact() =
      SubpluginArtifact(
          "com.facebook.kotlin.compilerplugins.dataclassgenerate",
          "cli",
          "$dcgVersion",
      )

  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
    return kotlinCompilation.target.project.plugins.hasPlugin(DataClassGeneratePlugin::class.java)
  }

  override fun applyToCompilation(
      kotlinCompilation: KotlinCompilation<*>
  ): Provider<List<SubpluginOption>> {
    val project = kotlinCompilation.target.project
    val extension = project.extensions.getByType(DataClassGeneratePluginExtension::class.java)

    kotlinCompilation.dependencies {
      compileOnly("com.facebook.kotlin.compilerplugins.dataclassgenerate:annotation:$dcgVersion")
    }

    if (extension.generateSuperClass.get()) {
      kotlinCompilation.dependencies {
        implementation(
            "com.facebook.kotlin.compilerplugins.dataclassgenerate:superclass:$dcgVersion"
        )
      }
    }
    return project.provider {
      listOf(
          SubpluginOption(key = "enabled", value = extension.enabled.get().toString()),
          SubpluginOption(key = "mode", value = extension.mode.get().toString()),
          SubpluginOption(
              key = "generateSuperClass",
              value = extension.generateSuperClass.get().toString(),
          ),
      )
    }
  }
}

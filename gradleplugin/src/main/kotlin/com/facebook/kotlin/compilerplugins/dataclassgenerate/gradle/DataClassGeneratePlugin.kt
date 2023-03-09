/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

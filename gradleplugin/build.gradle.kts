/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

plugins {
  alias(libs.plugins.kotlin)
  `java-gradle-plugin`
}

dependencies {
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin.api)
}

gradlePlugin {
  plugins {
    create("dataClassGeneratePlugin") {
      id = "com.facebook.kotlin.compilerplugins.dataclassgenerate"
      implementationClass =
          "com.facebook.kotlin.compilerplugins.dataclassgenerate.gradle.DataClassGeneratePlugin"
      displayName = "Data Class Generate"
      description =
          "Data Class Generate is a Kotlin compiler plugin that helps to address Kotlin data class binary size overhead"
    }
  }
}

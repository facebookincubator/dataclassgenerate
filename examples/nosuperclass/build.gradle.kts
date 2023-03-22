/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import com.facebook.kotlin.compilerplugins.dataclassgenerate.gradle.DataClassGenerateMode

plugins {
  kotlin("jvm")
  id("com.facebook.kotlin.compilerplugins.dataclassgenerate")
}

dataClassGenerate {
  mode.set(DataClassGenerateMode.IMPLICIT)
  generateSuperClass.set(false)
}

dependencies {
  implementation(project(":annotation"))
  implementation(project(":superclass"))

  testImplementation(libs.junit)
  testImplementation(libs.assertj.core)
}

configurations.configureEach {
  resolutionStrategy.dependencySubstitution {
    substitute(module("com.facebook.kotlin.compilerplugins.dataclassgenerate:annotation"))
        .using(project(":annotation"))
    substitute(module("com.facebook.kotlin.compilerplugins.dataclassgenerate:superclass"))
        .using(project(":superclass"))
    substitute(module("com.facebook.kotlin.compilerplugins.dataclassgenerate:compiler"))
        .using(project(":compiler:cli"))
  }
}

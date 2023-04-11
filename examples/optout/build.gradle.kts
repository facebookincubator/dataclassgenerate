/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

plugins {
  kotlin("jvm")
  id("com.facebook.kotlin.compilerplugins.dataclassgenerate")
}

dataClassGenerate { enabled.set(false) }

dependencies {
  testImplementation(libs.junit)
  testImplementation(libs.assertj.core)
}

configurations.configureEach {
  resolutionStrategy.dependencySubstitution {
    substitute(module("com.facebook.kotlin.compilerplugins.dataclassgenerate:annotation"))
        .using(project(":annotation"))
    substitute(module("com.facebook.kotlin.compilerplugins.dataclassgenerate:cli"))
        .using(project(":compiler:cli"))
  }
}

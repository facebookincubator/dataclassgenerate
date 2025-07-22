/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

plugins {
  id("java-library")
  kotlin("jvm")
  id("publish")
}

kotlin { jvmToolchain(8) }

java {
  withSourcesJar()
  withJavadocJar()
}

dependencies {
  implementation(project(":annotation"))
  implementation(project(":compiler:common"))
  compileOnly(libs.kotlin.compilerEmbeddable)

  testImplementation(libs.kotlin.compilerEmbeddable)
}

sourceSets {
  main {
    kotlin {
      exclude(
          "com/facebook/kotlin/compilerplugins/dataclassgenerate/misc/IdePsiElementAlias.kt",
      )
    }
  }
}

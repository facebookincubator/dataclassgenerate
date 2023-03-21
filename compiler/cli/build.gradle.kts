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

java {
  withSourcesJar()
  withJavadocJar()
}

sourceSets { main { resources { setSrcDirs(listOf("resources")) } } }

dependencies {
  implementation(project(":compiler:common"))
  implementation(project(":compiler:k1"))
  implementation(project(":compiler:k2"))
  compileOnly(libs.kotlin.compilerEmbeddable)

  testImplementation(libs.kotlin.compilerEmbeddable)
  testImplementation(libs.kotlin.stdlib)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.kotlin.compileTesting)
  testImplementation(libs.assertj.core)
  testImplementation(libs.junit)
  testImplementation(libs.junit)
  testImplementation(libs.byte.buddyAgent)
  testImplementation(libs.junit)
  testImplementation(libs.ow2.asm)
  testImplementation(libs.ow2.asmUtil)
  testImplementation(libs.mockito.kotlin)
}

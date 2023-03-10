/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

plugins {
  kotlin("jvm")
  kotlin("kapt")
}

dependencies {
  compileOnly(libs.kotlin.compilerEmbeddable)
  implementation(libs.autoService)
  kapt(libs.autoService.compiler)

  testImplementation(libs.kotlin.compilerEmbeddable)
}

/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

plugins { kotlin("jvm") }

dependencies {
  implementation(project(":annotation"))
  implementation(project(":compiler:common"))
  compileOnly(libs.kotlin.compilerEmbeddable)

  testImplementation(libs.kotlin.compilerEmbeddable)
}

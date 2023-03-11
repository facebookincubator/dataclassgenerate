/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

plugins { kotlin("jvm") }

sourceSets { main { resources { setSrcDirs(listOf("resources")) } } }

dependencies {
  implementation(project(":compiler:common"))
  implementation(project(":compiler:k1"))
  implementation(project(":compiler:k2"))
  compileOnly(libs.kotlin.compilerEmbeddable)

  testImplementation(libs.kotlin.compilerEmbeddable)
}

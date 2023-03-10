/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins { alias(libs.plugins.kotlin) apply false }

subprojects {
  repositories { mavenCentral() }

  tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
  }

  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
  }

  pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    project.tasks.withType<KotlinCompile>().configureEach {
      kotlinOptions.freeCompilerArgs = listOf("-progressive")
    }
  }
}

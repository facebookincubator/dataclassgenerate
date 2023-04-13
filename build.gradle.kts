/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  alias(libs.plugins.kotlin) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.publish.plugin)
  id("publish")
}

val nexusUsername = findProperty("NEXUS_USERNAME")?.toString()
val nexusPassword = findProperty("NEXUS_PASSWORD")?.toString()

nexusPublishing {
  repositories {
    sonatype {
      username.set(nexusUsername)
      password.set(nexusPassword)
    }
  }
}

subprojects {
  repositories {
    google()
    mavenCentral()
  }

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
  configurations.configureEach {
    resolutionStrategy.dependencySubstitution {
      substitute(module("com.facebook.kotlin.compilerplugins.dataclassgenerate:annotation"))
          .using(project(":annotation"))
      substitute(module("com.facebook.kotlin.compilerplugins.dataclassgenerate:superclass"))
          .using(project(":superclass"))
      substitute(module("com.facebook.kotlin.compilerplugins.dataclassgenerate:cli"))
          .using(project(":compiler:cli"))
    }
  }
}

tasks.register("publishAllToMavenLocal") {
  description = "Publish all the artifacts to be available inside Maven Local"
  dependsOn(gradle.includedBuild("gradleplugin").task(":publishToMavenLocal"))
  subprojects.forEach {
    if (it.project.plugins.hasPlugin("publish")) {
      dependsOn(it.tasks.named("publishToMavenLocal"))
    }
  }
}

tasks.register("publishAllToSonatype") {
  description =
      "Publish all the artifacts to be available on Sonatype (either Maven Central or Snapshot Repository)"
  dependsOn(gradle.includedBuild("gradleplugin").task(":publishToSonatype"))
  subprojects.forEach {
    if (it.project.plugins.hasPlugin("publish")) {
      dependsOn(it.tasks.named("publishToSonatype"))
    }
  }
}

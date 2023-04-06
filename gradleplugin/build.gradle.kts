/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.publish.plugin)
  id("java-gradle-plugin")
  id("publish")
}

group = "com.facebook.kotlin.compilerplugins.dataclassgenerate"

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

dependencies {
  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.kotlin.gradlePlugin.api)
}

gradlePlugin {
  website.set("https://github.com/facebookincubator/dataclassgenerate")
  vcsUrl.set("https://github.com/facebookincubator/dataclassgenerate")
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

val versionDirectory = "$buildDir/generated/version/"

sourceSets { main { java.srcDir(versionDirectory) } }

val pluginVersionTask =
    tasks.register("pluginVersion") {
      val outputDir = file(versionDirectory)
      inputs.property("version", version)
      outputs.dir(outputDir)
      doLast {
        val versionFile =
            file(
                "$outputDir/com/facebook/kotlin/compilerplugins/dataclassgenerata/gradle/version.kt")
        versionFile.parentFile.mkdirs()
        versionFile.writeText(
            """// Generated file. Do not edit!
package com.facebook.kotlin.compilerplugins.dataclassgenerate.gradle
internal const val dcgVersion = "$version"
""",
            Charsets.UTF_8)
      }
    }

tasks.getByName("compileKotlin").dependsOn(pluginVersionTask)

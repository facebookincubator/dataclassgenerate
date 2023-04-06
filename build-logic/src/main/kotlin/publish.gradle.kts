/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

plugins {
  id("maven-publish")
  id("signing")
}

if (!"USE_SNAPSHOT".byProperty.isNullOrBlank()) {
  version = "$VERSION_NAME-SNAPSHOT"
} else {
  version = VERSION_NAME
}

publishing {
  // We don't want to create pubblications for KMP and Gradle Plugin targets
  if (plugins.hasPlugin("org.jetbrains.kotlin.jvm") && name != "gradleplugin") {
    publications.register<MavenPublication>("DcgPublication") { from(components["java"]) }
  }

  publications.withType(MavenPublication::class) {
    groupId = "com.facebook.kotlin.compilerplugins.dataclassgenerate"
    artifactId = project.name
    pom {
      description.set(
          "A Kotlin compiler plugin that addresses an Android APK size overhead from Kotlin data classes.")
      name.set(project.name)
      url.set("https://github.com/facebookincubator/dataclassgenerate")
      licenses {
        license {
          name.set("MIT License")
          url.set("https://opensource.org/licenses/MIT")
          distribution.set("repo")
        }
      }
      developers {
        developer {
          id.set("Meta Open Source")
          name.set("Meta Open Source")
          email.set("opensource@meta.com")
        }
      }
      scm { url.set("https://github.com/facebookincubator/dataclassgenerate") }
    }
  }
}

val signingKey = "SIGNING_KEY".byProperty
val signingPwd = "SIGNING_PWD".byProperty

if (signingKey.isNullOrBlank() || signingPwd.isNullOrBlank()) {
  logger.info("Signing disabled as the GPG key was not found")
} else {
  logger.info("GPG Key found - Signing enabled")
}

signing {
  useInMemoryPgpKeys(signingKey, signingPwd)
  sign(publishing.publications)
  isRequired = !(signingKey.isNullOrBlank() || signingPwd.isNullOrBlank())
}

val String.byProperty: String?
  get() = providers.gradleProperty(this).orNull

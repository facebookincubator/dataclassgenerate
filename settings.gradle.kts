/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "dataclassgenerate"

include(":annotation")

include(":compiler:cli")

include(":compiler:common")

include(":compiler:k1")

include(":compiler:k2")

include(":superclass")

includeBuild("gradleplugin")

include(":sample")

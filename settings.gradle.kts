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

include(
    ":annotation",
    ":compiler:cli",
    ":compiler:common",
    ":compiler:k1",
    ":compiler:k2",
    ":superclass",
    ":sample")

includeBuild("gradleplugin")

includeBuild("build-logic")

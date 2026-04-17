/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate;

import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar;

/**
 * Java base class for DataClassGenerateComponentRegistrar to provide pluginId compatibility across
 * Kotlin versions. In Kotlin 2.3+, CompilerPluginRegistrar has an abstract pluginId property. In
 * Kotlin 2.1, it does not. Java's method resolution handles both: getPluginId() satisfies the
 * abstract property in 2.3+ and is harmless in 2.1.
 */
@SuppressWarnings({"deprecation", "PackageLocationMismatch"})
public abstract class DataClassGenerateComponentRegistrarBase extends CompilerPluginRegistrar {
  public String getPluginId() {
    return "com.facebook.kotlin.dataclassgenerate";
  }
}

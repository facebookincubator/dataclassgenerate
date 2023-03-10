/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation

enum class Mode {
  /** Express an intent to keep/generate a method(s) DataClassGenerate. */
  KEEP,
  /** Express an intent to omit method(s) generation. */
  OMIT
}

@Target(AnnotationTarget.CLASS)
/**
 * Configures Kotlin Data Classes code generation and bytecode optimizations. [@DataClassGenerate]
 * is applicable only to Kotlin Data Classes.
 *
 * DataClassGenerate Kotlin Compiler Plugin processes @DataClassGenerate annotation. We apply
 * DataClassGenerate plugin to all targets declared in `DCG_ALLOW_ON` list inside
 * [/tools/build_defs/android/kotlinc_plugin_decorator.bzl]
 *
 * If DataClassGenerate is turned on inside [/tools/build_defs/android/kotlinc_plugin_decorator.bzl]
 * (without any overrides), we default `toString: Mode = Mode.OMIT` and `equalsHashCode: Mode =
 * Mode.KEEP` As a result `@DataClassGenerate data class Foo(...)` will default to
 * `@DataClassGenerate(toString = Mode.OMIT, equalsHashCode = Mode.KEEP)`
 *
 * [@DataClassGenerate] annotation together with applied DataClassGenerate compiler plugin does 3
 * things:
 * 1. Configures code generation of `toString` method.
 * - `@DataClassGenerate(toString = Mode.KEEP)` will keep(generate) `toString` as in usual Data
 *   Class.
 * - `@DataClassGenerate(toString = Mode.OMIT)` will omit(NOT generate) `toString` for an annotated
 *   Data Class.
 * 1. Configures code generation of `equals` and `hashCode` methods.
 * - `@DataClassGenerate(equalsHashCode = Mode.KEEP)` will keep(generate) `equals` and `hashCode` as
 *   in usual Data Class.
 * - `@DataClassGenerate(equalsHashCode = Mode.OMIT)` will omit (NOT generate) `equals` and
 *   `hashCode` for an annotated Data Class.
 * 1. Adds a marker super class `DataClassSuper` for suitable Data Classes, to make them available
 *    for Redex Class Merging Optimization.
 *
 * On a BUCK module level DataClassGenerate compiler plugin has multiple modes:
 * - EXPLICIT - The default mode. If Data Class is annotated with @DataClassGenerate - processes it
 *   as declared in annotation. If Data Class is NOT annotated with @DataClassGenerate - does
 *   nothing.
 * - STRICT - Forces module developers to use Data Classes only with @DataClassGenerate. If Data
 *   Class is annotated with @DataClassGenerate - processes it as declared in annotation. If Data
 *   Class is NOT annotated with @DataClassGenerate - reports with a compilation error.
 * - IMPLICIT - The default mode. If Data Class is annotated with @DataClassGenerate - processes it
 *   as declared in annotation. If Data Class is NOT annotated with @DataClassGenerate - processes
 *   it as it is annotated with `@DataClassGenerate(toString=Mode.OMIT, equalsHashCode=Mode.KEEP)`
 *
 * DataClassGenerate compiler plugin has other configuration parameters. Check
 * `kotlinc_plugin_decorator.bzl` for more details.
 *
 * To apply/override DataClassGenerate in your module: <pre> fb_android_library(
 *
 * ```
 *    dataclass_generate = {
 *      // DataClassGenerate plugin mode
 *      // Required
 *      "mode": [ "IMPLICIT" (current default) | "EXPLICIT" | "STRICT" (will be default in a while) ],
 *
 *      // Whether DataClassGenerate should generate equals and hashCode by default
 *      // Optional
 *      "equalsHashCode": [ True (default) | False ],
 *
 *      // Whether DataClassGenerate should generate toString by default
 *      // Optional
 *      "toString": [ True | False (default) ],
 *
 *      // Whether DataClassGenerate should generate a marker super class for
 *      // Optional
 *      "generateSuperClass": [ True (default) | False ],
 *
 *      // Whether plugin is enabled
 *      // Optional
 *      "enabled": [ True (default) | False ],
 *    },
 * ```
 *
 * )
 */
annotation class DataClassGenerate(
    @get:JvmName("toString_uniqueJvmName") val toString: Mode = Mode.OMIT,
    val equalsHashCode: Mode = Mode.KEEP
)

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
  OMIT,
}

@Target(AnnotationTarget.CLASS)
/**
 * Configures Kotlin Data Classes code generation and bytecode optimizations. [@DataClassGenerate]
 * is applicable only to Kotlin Data Classes.
 *
 * DataClassGenerate Kotlin Compiler Plugin processes @DataClassGenerate annotation.
 *
 * [@DataClassGenerate] annotation together with applied DataClassGenerate compiler plugin does 3
 * things:
 * 1. Configures code generation of `toString` method.
 * - `@DataClassGenerate(toString = Mode.KEEP)` will keep(generate) `toString` as in usual Data
 *   Class.
 * - `@DataClassGenerate(toString = Mode.OMIT)` will omit(NOT generate) `toString` for an annotated
 *   Data Class.
 * 2. Configures code generation of `equals` and `hashCode` methods.
 * - `@DataClassGenerate(equalsHashCode = Mode.KEEP)` will keep(generate) `equals` and `hashCode` as
 *   in usual Data Class.
 * - `@DataClassGenerate(equalsHashCode = Mode.OMIT)` will omit (NOT generate) `equals` and
 *   `hashCode` for an annotated Data Class.
 * 3. Adds a marker super class `DataClassSuper` for suitable Data Classes, to make them available
 *    for Redex Class Merging Optimization.
 *
 * DataClassGenerate compiler plugin has multiple modes:
 * - EXPLICIT - The default mode. If Data Class is annotated with @DataClassGenerate - processes it
 *   as declared in annotation. If Data Class is NOT annotated with @DataClassGenerate - does
 *   nothing.
 * - STRICT - Forces module developers to use Data Classes only with @DataClassGenerate. If Data
 *   Class is annotated with @DataClassGenerate - processes it as declared in annotation. If Data
 *   Class is NOT annotated with @DataClassGenerate - reports with a compilation error.
 * - IMPLICIT - The default mode. If Data Class is annotated with @DataClassGenerate - processes it
 *   as declared in annotation. If Data Class is NOT annotated with @DataClassGenerate - processes
 *   it as it is annotated with `@DataClassGenerate(toString=Mode.OMIT, equalsHashCode=Mode.KEEP)`
 */
annotation class DataClassGenerate(
    @get:JvmName("toString_uniqueJvmName") val toString: Mode = Mode.OMIT,
    val equalsHashCode: Mode = Mode.KEEP,
)

/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration

enum class PluginMode {
  /** Plugin applies only to data classes annotated with [@DataClassGenerate] */
  EXPLICIT,

  /**
   * Same as [EXPLICIT], but Plugin will report a compilation error whenever it see a data class
   * without [@DataClassGenerate] annotation.
   *
   * Example 1: Plugin will generate `toString`, but omit `equals` and
   * `hashCode`. @DataClassGenerate(toString = Mode.KEEP, equalsHashCode = Mode.OMIT) data class
   * A(val i:Int)
   *
   * Example 2: Plugin will report a compilation error data class B(val l:Long)
   */
  STRICT,

  /**
   * Plugin applies to all data classes, if data class is not annotated with [@DataClassGenerate] it
   * will be treated as annotated with [@DataClassGenerate]
   *
   * Example: Plugin will generate a `toString` method for data class [A] Plugin will NOT generate
   * `equals` and `hashCode`. @DataClassGenerate(toString = Mode.KEEP, equalsHashCode = Mode.OMIT)
   * data class A(val i: Int)
   */
  IMPLICIT,
}

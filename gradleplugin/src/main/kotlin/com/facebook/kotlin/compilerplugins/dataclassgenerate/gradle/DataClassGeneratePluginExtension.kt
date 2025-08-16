/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.gradle

import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

public abstract class DataClassGeneratePluginExtension @Inject constructor(objects: ObjectFactory) {
  public val enabled: Property<Boolean> =
      objects.property(Boolean::class.javaObjectType).convention(true)

  public val mode: Property<DataClassGenerateMode> =
      objects.property(DataClassGenerateMode::class.java).convention(DataClassGenerateMode.EXPLICIT)

  /**
   * Adds a marker super class [DataClassSuper] for suitable data classes to make them available for
   * [Redex Class Merging Optimization](https://github.com/facebook/redex/blob/main/docs/passes.md#classmergingpass).
   */
  public val generateSuperClass: Property<Boolean> =
      objects.property(Boolean::class.javaObjectType).convention(false)
}

enum class DataClassGenerateMode {
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

/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

object DataClassGenerateExt {

  val annotationFqName =
      FqName("com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.DataClassGenerate")
  val tsAnnotationArg = Name.identifier("toString")
  val eqHcAnnotationArg = Name.identifier("equalsHashCode")

  /** This property could be updated by compiler plugin configs */
  var generateSuperClass: Boolean = true
}

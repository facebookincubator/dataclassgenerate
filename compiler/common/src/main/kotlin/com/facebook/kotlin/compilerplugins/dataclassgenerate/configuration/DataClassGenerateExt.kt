// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

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

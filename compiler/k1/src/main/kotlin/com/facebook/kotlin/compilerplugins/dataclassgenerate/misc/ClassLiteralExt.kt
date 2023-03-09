// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.facebook.kotlin.compilerplugins.dataclassgenerate.misc

const val OBJECT_LITERAL = "java/lang/Object"

typealias ClassLiteral = String

/**
 * We want to avoid extra super class generation for all synthetic classes If synthetic class is a
 * result of a compiler plugin's codegen we may wrongly inherit original classe's properties like
 * `isData` Patterns we want to avoid:
 * - kotlin Companion objects
 * - kotlinx-serialization $$serializer helpers
 * - fb-@Plugin $delegate
 */
fun ClassLiteral.isLikelySynthetic(): Boolean {
  /*
  data class Foo {
    // Do not want to process this companion object Foo$Companion
    companion object {
      // But want to process this data class Foo$Companion$Bar
      data class Bar
    }
  }
   */
  return endsWith("\$Companion")
  /*
  // Want to process Foo, but not Foo$$serializer
  @Serializable
  data class Foo(@SerialName("name") val bar: T)
   */
  || endsWith("\$\$serializer") || contains("\$delegate")
}

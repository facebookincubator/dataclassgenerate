/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.tschuchort.compiletesting.SourceFile
import org.assertj.core.api.Assertions.assertThat
import org.junit.BeforeClass
import org.junit.Test

@OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)
class DcgCompilerIdentityTest : DcgTestCase() {

  companion object {
    private lateinit var k1DcgMap: Map<String, DcgDump>
    private lateinit var k2DcgMap: Map<String, DcgDump>

    private val dataClasses =
        SourceFile.kotlin(
            "Container.kt",
            """
        | package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples
        |
        | import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.DataClassGenerate
        | import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode.KEEP
        | import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode.OMIT
        |
        | data class NonAnnotatedDataClass(val str: String)
        |
        | @DataClassGenerate
        | data class SampleDataClassWithExplicitOverrides(val str: String) {
        |   override fun toString() = "dummy"
        |   override fun equals(other: Any?) = true
        |   override fun hashCode() = 42
        | }
        |
        | @DataClassGenerate data class SampleDataClass(val str: String)
        |
        | @DataClassGenerate(OMIT, OMIT) data class SampleDataClassGenerationOff(val str: String)
        |
        | @DataClassGenerate(toString = KEEP) data class SampleDataClassToString(val str: String)
        |
        | @DataClassGenerate(equalsHashCode = KEEP) data class SampleDataClassEqualsHashcode(val str: String)
        |
        | @DataClassGenerate(KEEP, KEEP)
        | data class SampleDataClassToStringEqualsHashcode(val a: Int, val b: String?)
        |
        | open class Base
        | data class SampleWithSuper(val a: Int) : Base()
        """
                .trimMargin(),
        )

    @BeforeClass
    @JvmStatic
    fun compile() {
      val k1Compilation = compileWithK1(dataClasses, DCG_ANNOTATION)
      val k2Compilation = compileWithK2(dataClasses, DCG_ANNOTATION)
      k1DcgMap = k1Compilation.generatedFiles.asDcgMap()
      k2DcgMap = k2Compilation.generatedFiles.asDcgMap()
    }
  }

  @Test
  fun `non annotated data class`() {
    fun DcgDump.verify() {
      assertThat(superClass).isEqualTo(DCG_SUPER)
      assertThat(toStringDeclaration)
          .containsSubsequence(
              """
                ,     INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
              """
                  .trimIndent()
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
                ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
              """
                  .trimIndent()
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
                ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
              """
                  .trimIndent()
          )
    }

    with(k1DcgMap["NonAnnotatedDataClass.class"]!!) { verify() }
    with(k2DcgMap["NonAnnotatedDataClass.class"]!!) { verify() }
  }

  @Test
  fun `simple data class`() {
    fun DcgDump.verify() {
      assertThat(superClass).isEqualTo(DCG_SUPER)
      assertThat(toStringDeclaration)
          .containsSubsequence(
              """
                ,     INVOKESPECIAL java/lang/Object.toString ()Ljava/lang/String;
                ,     ARETURN
              """
                  .trimIndent()
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
            ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
          """
                  .trimIndent()
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
            ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
          """
                  .trimIndent()
          )
    }

    with(k1DcgMap["SampleDataClass.class"]!!) { verify() }
    with(k2DcgMap["SampleDataClass.class"]!!) { verify() }
  }

  @Test
  fun `simple data class without generation`() {
    fun DcgDump.verify() {
      assertThat(superClass).isEqualTo(DCG_SUPER)
      assertThat(toStringDeclaration)
          .containsSubsequence(
              """
            ,     INVOKESPECIAL java/lang/Object.toString ()Ljava/lang/String;
            ,     ARETURN
          """
                  .trimIndent()
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
            ,     INVOKESPECIAL java/lang/Object.hashCode ()I
            ,     IRETURN
          """
                  .trimIndent()
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
            ,     INVOKESPECIAL java/lang/Object.equals (Ljava/lang/Object;)Z
            ,     IRETURN
          """
                  .trimIndent()
          )
    }
    with(k1DcgMap["SampleDataClassGenerationOff.class"]!!) { verify() }
    with(k2DcgMap["SampleDataClassGenerationOff.class"]!!) { verify() }
  }

  @Test
  fun `simple data class with equals and hashCode`() {
    fun DcgDump.verify() {
      assertThat(superClass).isEqualTo(DCG_SUPER)
      assertThat(toStringDeclaration)
          .containsSubsequence(
              """
            ,     INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
          """
                  .trimIndent()
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
            ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
            ,     IRETURN
          """
                  .trimIndent()
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
            ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
          """
                  .trimIndent()
          )
    }

    with(k1DcgMap["SampleDataClassToString.class"]!!) { verify() }
    with(k2DcgMap["SampleDataClassToString.class"]!!) { verify() }
  }

  @Test
  fun `simple data class with toString`() {
    fun DcgDump.verify() {
      assertThat(superClass).isEqualTo(DCG_SUPER)
      assertThat(toStringDeclaration)
          .containsSubsequence(
              """
            ,     INVOKESPECIAL java/lang/Object.toString ()Ljava/lang/String;
            ,     ARETURN
          """
                  .trimIndent()
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
            ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
            ,     IRETURN
          """
                  .trimIndent()
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
            ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
          """
                  .trimIndent()
          )
    }

    with(k1DcgMap["SampleDataClassEqualsHashcode.class"]!!) { verify() }
    with(k2DcgMap["SampleDataClassEqualsHashcode.class"]!!) { verify() }
  }

  @Test
  fun `simple data class with toString, equals, and hashCode`() {

    fun DcgDump.verify() {
      assertThat(superClass).isEqualTo(DCG_SUPER)
      assertThat(toStringDeclaration)
          .containsSubsequence(
              """
            ,     INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
          """
                  .trimIndent()
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
            ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
          """
                  .trimIndent()
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
            ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
          """
                  .trimIndent()
          )
    }
    with(k1DcgMap["SampleDataClassToStringEqualsHashcode.class"]!!) { verify() }
    with(k2DcgMap["SampleDataClassToStringEqualsHashcode.class"]!!) { verify() }
  }

  @Test
  fun `class with an existing super`() {
    fun DcgDump.verify() {
      assertThat(superClass).isNotEqualTo(DCG_SUPER)
    }

    with(k1DcgMap["SampleWithSuper.class"]!!) { verify() }
    with(k2DcgMap["SampleWithSuper.class"]!!) { verify() }
  }
}

private val DCG_ANNOTATION =
    SourceFile.kotlin(
        "DataClassGenerate.kt",
        """
      | package com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation
      |
      | enum class Mode {
      |   KEEP,
      |   OMIT
      | }
      |
      | @Target(AnnotationTarget.CLASS)
      |
      | annotation class DataClassGenerate(
      |     @get:JvmName("toString_uniqueJvmName") val toString: Mode = Mode.OMIT,
      |     val equalsHashCode: Mode = Mode.KEEP
      | )
      """
            .trimMargin(),
    )

private const val DCG_SUPER =
    "com/facebook/kotlin/compilerplugins/dataclassgenerate/superclass/DataClassSuper"

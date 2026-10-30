/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import org.assertj.core.api.Assertions.assertThat
import org.junit.BeforeClass
import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.TraceClassVisitor

@OptIn(org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi::class)
class DcgCompilerIdentityTest : DcgTestCase() {

  companion object {
    private lateinit var dcgMap: Map<String, DcgDump>
    private lateinit var classFiles: Map<String, File>
    private lateinit var classLoader: ClassLoader

    private val dataClasses =
        SourceFile.kotlin(
            "Container.kt",
            """
            |package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples
            |
            |import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.DataClassGenerate
            |import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode.KEEP
            |import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode.OMIT
            |
            |data class NonAnnotatedDataClass(val str: String)
            |
            |@DataClassGenerate
            |data class SampleDataClassWithExplicitOverrides(val str: String) {
            |  override fun toString() = "dummy"
            |  override fun equals(other: Any?) = true
            |  override fun hashCode() = 42
            |}
            |
            |@DataClassGenerate data class SampleDataClass(val str: String)
            |
            |@DataClassGenerate(OMIT, OMIT) data class SampleDataClassGenerationOff(val str: String)
            |
            |@DataClassGenerate(toString = KEEP) data class SampleDataClassToString(val str: String)
            |
            |@DataClassGenerate(equalsHashCode = KEEP) data class SampleDataClassEqualsHashcode(val str: String)
            |
            |@DataClassGenerate(KEEP, KEEP)
            |data class SampleDataClassToStringEqualsHashcode(val a: Int, val b: String?)
            |
            |open class Base
            |data class SampleWithSuper(val a: Int) : Base()
            |
            |class PlainClass(val a: Int)
            |
            |data object SampleDataObject
            |
            |@DataClassGenerate
            |data class SampleWithClassesInConstructors(val r: Runnable) {
            |  constructor() : this(object : Runnable { override fun run() {} })
            |
            |  constructor(times: Int) : this() {
            |    class Local : Runnable { override fun run() {} }
            |    repeat(times) { Local().run() }
            |  }
            |}
            """
                .trimMargin(),
        )

    @BeforeClass
    @JvmStatic
    fun compile() {
      val compilation = compile(dataClasses, DCG_ANNOTATION)
      assertThat(compilation.exitCode)
          .describedAs(compilation.messages)
          .isEqualTo(KotlinCompilation.ExitCode.OK)
      dcgMap = compilation.generatedFiles.asDcgMap()
      classFiles =
          compilation.generatedFiles.filter { it.name.endsWith(".class") }.associateBy { it.name }
      classLoader = compilation.classLoader
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
                  .trimIndent(),
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
              ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
              """
                  .trimIndent(),
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
              ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
              """
                  .trimIndent(),
          )
    }

    with(dcgMap.getValue("NonAnnotatedDataClass.class")) { verify() }
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
                  .trimIndent(),
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
              ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
              """
                  .trimIndent(),
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
              ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
              """
                  .trimIndent(),
          )
    }

    with(dcgMap.getValue("SampleDataClass.class")) { verify() }
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
                  .trimIndent(),
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
              ,     INVOKESPECIAL java/lang/Object.hashCode ()I
              ,     IRETURN
              """
                  .trimIndent(),
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
              ,     INVOKESPECIAL java/lang/Object.equals (Ljava/lang/Object;)Z
              ,     IRETURN
              """
                  .trimIndent(),
          )
    }
    with(dcgMap.getValue("SampleDataClassGenerationOff.class")) { verify() }
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
                  .trimIndent(),
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
              ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
              ,     IRETURN
              """
                  .trimIndent(),
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
              ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
              """
                  .trimIndent(),
          )
    }

    with(dcgMap.getValue("SampleDataClassToString.class")) { verify() }
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
                  .trimIndent(),
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
              ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
              ,     IRETURN
              """
                  .trimIndent(),
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
              ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
              """
                  .trimIndent(),
          )
    }

    with(dcgMap.getValue("SampleDataClassEqualsHashcode.class")) { verify() }
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
                  .trimIndent(),
          )

      assertThat(hashCodeDeclaration)
          .containsSubsequence(
              """
              ,     INVOKEVIRTUAL java/lang/String.hashCode ()I
              """
                  .trimIndent(),
          )

      assertThat(equalsDeclaration)
          .containsSubsequence(
              """
              ,     INVOKESTATIC kotlin/jvm/internal/Intrinsics.areEqual (Ljava/lang/Object;Ljava/lang/Object;)Z
              """
                  .trimIndent(),
          )
    }
    with(dcgMap.getValue("SampleDataClassToStringEqualsHashcode.class")) { verify() }
  }

  @Test
  fun `class with an existing super`() {
    fun DcgDump.verify() {
      assertThat(superClass).isNotEqualTo(DCG_SUPER)
    }

    with(dcgMap.getValue("SampleWithSuper.class")) { verify() }
  }

  @Test
  fun `plain class and data object keep Object as their superclass`() {
    assertThat(dcgMap.getValue("PlainClass.class").superClass).isEqualTo(OBJECT)
    assertThat(dcgMap.getValue("SampleDataObject.class").superClass).isEqualTo(OBJECT)
  }

  @Test
  fun `classes declared in data class constructors keep their Object super call`() {
    assertThat(dcgMap.getValue("SampleWithClassesInConstructors.class").superClass)
        .isEqualTo(DCG_SUPER)
    assertThat(classFiles.getValue("SampleWithClassesInConstructors.class").bytecode())
        .contains("INVOKESPECIAL $DCG_SUPER.<init> ()V")

    val nested = classFiles.filterKeys { it.startsWith("SampleWithClassesInConstructors$") }
    assertThat(nested).hasSize(2)
    for ((name, file) in nested) {
      assertThat(dcgMap.getValue(name).superClass).isEqualTo(OBJECT)
      assertThat(file.bytecode())
          .contains("INVOKESPECIAL $OBJECT.<init> ()V")
          .doesNotContain(DCG_SUPER)
    }

    // Instantiating loads and verifies the anonymous object and the local class.
    classLoader
        .loadClass(
            "com.facebook.kotlin.compilerplugins.dataclassgenerate.examples.SampleWithClassesInConstructors",
        )
        .getConstructor(Int::class.javaPrimitiveType)
        .newInstance(1)
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

private const val OBJECT = "java/lang/Object"

private fun File.bytecode(): String {
  val text = StringWriter()
  ClassReader(readBytes()).accept(TraceClassVisitor(PrintWriter(text)), 0)
  return text.toString()
}

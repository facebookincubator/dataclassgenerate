/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.DataClassGenerateExt
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.PluginMode
import com.facebook.kotlin.compilerplugins.dataclassgenerate.misc.OBJECT_LITERAL
import com.facebook.kotlin.compilerplugins.dataclassgenerate.misc.findAnnotation
import com.facebook.kotlin.compilerplugins.dataclassgenerate.misc.getValueArgument
import com.facebook.kotlin.compilerplugins.dataclassgenerate.misc.isDataClass
import com.facebook.kotlin.compilerplugins.dataclassgenerate.misc.isLikelyMethodOfSyntheticClass
import com.facebook.kotlin.compilerplugins.dataclassgenerate.misc.isLikelySynthetic
import com.facebook.kotlin.compilerplugins.dataclassgenerate.misc.isSynthesized
import com.facebook.kotlin.compilerplugins.dataclassgenerate.misc.superClassLiteral
import com.facebook.kotlin.compilerplugins.dataclassgenerate.visitor.EqualsMethodVisitor
import com.facebook.kotlin.compilerplugins.dataclassgenerate.visitor.HashCodeMethodVisitor
import com.facebook.kotlin.compilerplugins.dataclassgenerate.visitor.SuperClassInitCallOverrideVisitor
import com.facebook.kotlin.compilerplugins.dataclassgenerate.visitor.ToStringMethodVisitor
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.isClass
import org.jetbrains.kotlin.ir.descriptors.IrBasedClassDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameSafe
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.MethodVisitor

const val GENERATED_SUPER =
    "com/facebook/kotlin/compilerplugins/dataclassgenerate/superclass/DataClassSuper"

class DataClassGenerateBuilder(
    // As of Kotlin 1.7.21 K2 compiler supplies [null] as a [PsiElement] origin in [defineClass]
    // method
    // https://youtrack.jetbrains.com/issue/KT-56814
    private val declarationOrigin: JvmDeclarationOrigin,
    internal val classBuilder: ClassBuilder,
    private val mode: PluginMode,
) : DelegatingClassBuilder() {

  override fun getDelegate() = classBuilder

  override fun defineClass(
      // We don't rely on [PsiElement] here as in K2 it will be [null] in this DCG builder
      // We provide a corresponding data with a [declarationOrigin] field in this class.
      // Depending on https://youtrack.jetbrains.com/issue/KT-56814 resolution we should change
      // K2 behaviour
      _doNotUseOnlyPassToSuperCall: DcgPsiElement?,
      version: Int,
      access: Int,
      name: String,
      signature: String?,
      superName: String,
      interfaces: Array<out String>
  ) {
    var effectiveSuperName = superName
    // K1 handling
    val originElement = declarationOrigin.element
    if (originElement is KtClass) {
      if (originElement.isData()) {
        if (mode == PluginMode.STRICT && !originElement.isAnnotatedWithDataClassGenerate()) {
          throw DataClassGenerateStrictModeViolationException(
              generateStrictModeViolationMessage(originElement.fqName))
        }

        if (!name.isLikelySynthetic()) {
          effectiveSuperName = adjustSuperClass(superName)
        }
      }
    }
    // K2 handling
    else {
      val originElement = declarationOrigin.descriptor
      if (originElement is IrBasedClassDescriptor) {
        if (originElement.isData && originElement.kind.isClass) {
          if (mode == PluginMode.STRICT && !originElement.isAnnotatedWithDataClassGenerate()) {
            throw DataClassGenerateStrictModeViolationException(
                generateStrictModeViolationMessage(originElement.fqNameSafe))
          }

          if (!name.isLikelySynthetic()) {
            effectiveSuperName = adjustSuperClass(superName)
          }
        }
      }
    }

    super.defineClass(
        _doNotUseOnlyPassToSuperCall,
        version,
        access,
        name,
        signature,
        effectiveSuperName,
        interfaces)
  }

  override fun newMethod(
      origin: JvmDeclarationOrigin,
      access: Int,
      name: String,
      desc: String,
      signature: String?,
      exceptions: Array<out String>?
  ): MethodVisitor {
    val originalVisitor = super.newMethod(origin, access, name, desc, signature, exceptions)
    if (origin.isDataClass() && !origin.isLikelyMethodOfSyntheticClass()) {
      val superClassLiteral = origin.superClassLiteral()
      val shouldGenerateSuperclass = shouldOverrideSuperClass(superClassLiteral)

      // We should override <init> call if we're generating a super class.
      if (name == INIT_METHOD_SIGNATURE) {
        return if (shouldGenerateSuperclass) SuperClassInitCallOverrideVisitor(originalVisitor)
        else originalVisitor
      }

      origin.findAnnotation(DataClassGenerateExt.annotationFqName).let { ad ->
        // We should not change methods if they were declared explicitly.
        if (origin.isSynthesized() && !shouldGenerate(name, ad)) {
          return when (name) {
            "equals" -> EqualsMethodVisitor(OBJECT_LITERAL, originalVisitor)
            "hashCode" -> HashCodeMethodVisitor(OBJECT_LITERAL, originalVisitor)
            "toString" -> ToStringMethodVisitor(OBJECT_LITERAL, originalVisitor)
            else -> originalVisitor
          }
        }
      }
    }

    return originalVisitor
  }

  fun shouldOverrideSuperClass(superClassLiteral: String): Boolean =
      DataClassGenerateExt.generateSuperClass && superClassLiteral == OBJECT_LITERAL

  fun adjustSuperClass(origin: String) =
      if (shouldOverrideSuperClass(origin)) GENERATED_SUPER else origin

  private fun KtClass.isAnnotatedWithDataClassGenerate(): Boolean {
    return annotationEntries
        .map { it.shortName }
        .contains(DataClassGenerateExt.annotationFqName.shortName())
  }

  private fun IrBasedClassDescriptor.isAnnotatedWithDataClassGenerate(): Boolean {
    return annotations.map { it.fqName }.contains(DataClassGenerateExt.annotationFqName)
  }

  private fun shouldGenerate(
      method: String,
      descriptor: AnnotationDescriptor?,
  ): Boolean {
    if (descriptor == null && mode != PluginMode.IMPLICIT) return true

    return when (method) {
      "equals",
      "hashCode" ->
          descriptor
              ?.getValueArgument<Mode>(DataClassGenerateExt.eqHcAnnotationArg)
              .asBoolean(defaultIfParameterIsNotPassed = true)
      "toString" -> {
        descriptor
            ?.getValueArgument<Mode>(DataClassGenerateExt.tsAnnotationArg)
            .asBoolean(defaultIfParameterIsNotPassed = false)
      }
      else -> true
    }
  }
}

fun generateStrictModeViolationMessage(fqName: FqName?): String =
    """
           You are running DataClassGenerate compiler plugin in a STRICT mode.
           But $fqName is not annotated with @DataClassGenerate.

           Replace $fqName with @DataClassGenerate(toString=Mode.OMIT, equalsHashCode=Mode.KEEP)
           - If $fqName does not need a `toString()` method use `toString=Mode.OMIT`
           - If $fqName does not need `equals()` and hashCode()` use `equalsHashCode=Mode.OMIT` or
           consider replacing it with a regular class.

           Read more:
           1. What is DataClassGenerate? - https://fburl.com/dataclassgenerate_wiki
           2. How to configure @DataClassGenerate annotation? - https://fburl.com/dataclassgenerate
           3. What is STRICT mode? - https://fburl.com/dataclassgenerate_mode
          """
        .trimIndent()

class DataClassGenerateStrictModeViolationException(message: String) : RuntimeException(message)

private const val INIT_METHOD_SIGNATURE = "<init>"

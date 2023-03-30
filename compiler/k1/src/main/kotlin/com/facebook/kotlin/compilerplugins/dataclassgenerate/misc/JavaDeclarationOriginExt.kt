/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.misc

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.descriptors.IrBasedClassDescriptor
import org.jetbrains.kotlin.ir.descriptors.IrBasedDeclarationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.resolve.descriptorUtil.getSuperClassNotAny
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.lazy.descriptors.LazyClassDescriptor

private val compilerVersion: KotlinVersion = KotlinVersion.CURRENT
private val kotlin150 = KotlinVersion(1, 5, 0)

internal val JvmDeclarationOrigin.containingClassDescriptor: ClassDescriptor?
  get() =
      when {
        compilerVersion < kotlin150 -> descriptor?.containingDeclaration as? LazyClassDescriptor
        else -> descriptor?.containingDeclaration as? IrBasedClassDescriptor
      }

internal fun JvmDeclarationOrigin.isDataClass(): Boolean =
    containingClassDescriptor?.isData ?: false

internal fun JvmDeclarationOrigin.superClassLiteral(): String {
  val superName = containingClassDescriptor?.getSuperClassNotAny()?.fqNameOrNull()
  return superName?.toString()?.replace(".", "/") ?: OBJECT_LITERAL
}

internal fun JvmDeclarationOrigin.isLikelyMethodOfSyntheticClass(): Boolean =
    containingClassDescriptor?.name?.asString()?.isLikelySynthetic() ?: false

internal fun JvmDeclarationOrigin.findAnnotation(fqName: FqName): AnnotationDescriptor? =
    containingClassDescriptor?.annotations?.findAnnotation(fqName)

internal fun JvmDeclarationOrigin.isSynthesized(): Boolean {
  return when {
    compilerVersion < kotlin150 -> {
      // Before Kotlin 1.5.0 it CallableMemberDescriptor::kind field was a reliable source
      (descriptor as? CallableMemberDescriptor)?.kind == CallableMemberDescriptor.Kind.SYNTHESIZED
    }
    else -> {
      // With JVM\IR it's no longer possible to check
      // IrBasedDeclarationDescriptor.kind == Kind.SYNTHESIZED, as field doesn't represent
      // a real situation with generated code
      (descriptor as? IrBasedDeclarationDescriptor<*>)?.owner?.origin ==
          IrDeclarationOrigin.GENERATED_DATA_CLASS_MEMBER
    }
  }
}

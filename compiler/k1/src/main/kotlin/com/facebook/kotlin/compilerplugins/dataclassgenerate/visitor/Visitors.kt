/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.visitor

import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes.ALOAD
import org.jetbrains.org.objectweb.asm.Opcodes.ARETURN
import org.jetbrains.org.objectweb.asm.Opcodes.ASM5
import org.jetbrains.org.objectweb.asm.Opcodes.INVOKESPECIAL
import org.jetbrains.org.objectweb.asm.Opcodes.IRETURN

class HashCodeMethodVisitor(private val owner: String, original: MethodVisitor) :
    MethodVisitor(ASM5, original) {
  /*
  descriptor: ()I
  Code:
       0: aload_0
       Method com/facebook/kotlin/compilerplugins/dataclassgenerate/superclass/DataClassSuper.hashCode:()I
       1: invokespecial
       4: ireturn
   */
  override fun visitCode() {
    mv.apply {
      visitVarInsn(ALOAD, 0)
      visitMethodInsn(INVOKESPECIAL, owner, "hashCode", "()I", false)
      visitInsn(IRETURN)
    }
  }
}

class EqualsMethodVisitor(private val owner: String, original: MethodVisitor) :
    MethodVisitor(ASM5, original) {
  /*
  descriptor: (Ljava/lang/Object;)Z
  Code:
       0: aload_0
       1: aload_1
       Method com/facebook/kotlin/compilerplugins/dataclassgenerate/superclass/DataClassSuper.equals:(Ljava/lang/Object;)Z
       2: invokespecial
       5: ireturn
   */
  override fun visitCode() {
    mv.apply {
      visitVarInsn(ALOAD, 0)
      visitVarInsn(ALOAD, 1)
      visitMethodInsn(INVOKESPECIAL, owner, "equals", "(Ljava/lang/Object;)Z", false)
      visitInsn(IRETURN)
    }
  }
}

class ToStringMethodVisitor(private val owner: String, original: MethodVisitor) :
    MethodVisitor(ASM5, original) {
  /*
  descriptor: ()Ljava/lang/String;
  Code:
       0: aload_0
       Method com/facebook/kotlin/compilerplugins/dataclassgenerate/superclass/DataClassSuper.toString:()Ljava/lang/String;
       1: invokespecial
       4: areturn
   */
  override fun visitCode() {
    mv.apply {
      visitVarInsn(ALOAD, 0)
      visitMethodInsn(INVOKESPECIAL, owner, "toString", "()Ljava/lang/String;", false)
      visitInsn(ARETURN)
    }
  }
}

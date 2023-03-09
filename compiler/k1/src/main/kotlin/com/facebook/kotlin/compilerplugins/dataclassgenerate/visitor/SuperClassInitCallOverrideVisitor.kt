// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.facebook.kotlin.compilerplugins.dataclassgenerate.visitor

import com.facebook.kotlin.compilerplugins.dataclassgenerate.GENERATED_SUPER
import com.facebook.kotlin.compilerplugins.dataclassgenerate.misc.OBJECT_LITERAL
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes

class SuperClassInitCallOverrideVisitor(original: MethodVisitor) :
    MethodVisitor(Opcodes.ASM5, original) {
  override fun visitMethodInsn(
      opcode: Int,
      owner: String?,
      name: String?,
      descriptor: String?,
      isInterface: Boolean
  ) {
    // Having:
    //    opcode                          owner          name
    //      \/                             \/             \/
    //  invokespecial #20   // Method java/lang/Object."<init>":()V
    //
    // Changing to:
    // invokespecial #20   // Method
    // com/facebook/kotlin/compilerplugins/dataclassgenerate/superclass/DataClassSuper."<init>":()V
    //
    // If we have multiple constructors in Data Class, only one will have an j/l/Object owner.
    //
    var effectiveOwner = owner
    if (owner == OBJECT_LITERAL && opcode == Opcodes.INVOKESPECIAL) {
      effectiveOwner = GENERATED_SUPER
    }
    super.visitMethodInsn(opcode, effectiveOwner, name, descriptor, isInterface)
  }
}

/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import com.facebook.kotlin.compilerplugins.dataclassgenerate.superclass.DataClassSuper
import org.assertj.core.api.Assertions
import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.signature.SignatureReader
import org.objectweb.asm.util.TraceSignatureVisitor

class GenerateSuperClassTest {

  @Test
  fun `super class is generated`() {
    Assertions.assertThat(A(42, "")).isInstanceOf(DataClassSuper::class.java)
    Assertions.assertThat(B(42, "")).isInstanceOf(DataClassSuper::class.java)
    Assertions.assertThat(C(42, "")).isInstanceOf(DataClassSuper::class.java)
  }

  @Test
  fun `super class for generic type`() {
    Assertions.assertThat(DGeneric<List<Int>, String>(emptyList(), ""))
        .isInstanceOf(DataClassSuper::class.java)
  }

  @Test
  fun `signature for generic type`() {
    val classReader = ClassReader(DGeneric::class.java.name)
    val traceSignatureVisitor = TraceSignatureVisitor(0)

    classReader.accept(
        object : ClassVisitor(Opcodes.ASM9) {
          override fun visit(
              version: Int,
              access: Int,
              name: String?,
              signature: String?,
              superName: String?,
              interfaces: Array<out String?>?,
          ) {
            SignatureReader(signature).accept(traceSignatureVisitor)
            super.visit(version, access, name, signature, superName, interfaces)
          }
        },
        0,
    )
    Assertions.assertThat(traceSignatureVisitor.declaration)
        .isEqualTo(
            "<T extends java.util.Collection<?>, U> extends com.facebook.kotlin.compilerplugins.dataclassgenerate.superclass.DataClassSuper implements java.util.concurrent.Callable<java.lang.Object>"
        )
  }
}

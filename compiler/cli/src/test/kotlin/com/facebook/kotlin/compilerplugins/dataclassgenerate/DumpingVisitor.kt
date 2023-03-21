/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import java.io.File
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.ASM5
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceMethodVisitor

class DcgDumpClassVisitor(val dcgDump: DcgDump) : ClassVisitor(ASM5) {
  override fun visitMethod(
      access: Int,
      name: String,
      desc: String,
      signature: String?,
      exceptions: Array<out String>?
  ): MethodVisitor {
    val original = super.visitMethod(access, name, desc, signature, exceptions)
    val methodContextDumper =
        object : Textifier(ASM5) {
          override fun visitMethodEnd() {
            when (name) {
              "toString" -> dcgDump.toStringDeclaration = text.toString()
              "hashCode" -> dcgDump.hashCodeDeclaration = text.toString()
              "equals" -> dcgDump.equalsDeclaration = text.toString()
            }
          }
        }
    return TraceMethodVisitor(original, methodContextDumper)
  }

  override fun visit(
      version: Int,
      access: Int,
      name: String,
      signature: String?,
      superName: String,
      interfaces: Array<out String>
  ) {
    dcgDump.superClass = superName
  }
}

data class DcgDump(
    var superClass: String? = null,
    var toStringDeclaration: String? = null,
    var hashCodeDeclaration: String? = null,
    var equalsDeclaration: String? = null,
)

fun dumpClassFileAbi(classFile: File): DcgDump {
  val cr = ClassReader(classFile.readBytes())
  val dump = DcgDump()
  cr.accept(DcgDumpClassVisitor(dump), 0)
  return dump
}

fun Collection<File>.asDcgMap(): Map<String, DcgDump> =
    filter { it.name.endsWith(".class") }.associate { it.name to dumpClassFileAbi(it) }

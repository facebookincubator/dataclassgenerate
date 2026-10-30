/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate

import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.DataClassGenerateExt
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.DataClassGenerateStrictModeViolationException
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.PluginMode
import com.facebook.kotlin.compilerplugins.dataclassgenerate.configuration.generateStrictModeViolationMessage
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrDelegatingConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.isAny
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class DataClassGenerateIrGenerationExtension(
    private val mode: PluginMode,
) : IrGenerationExtension {

  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    moduleFragment.transform(DataClassGenerateTransformer(pluginContext, mode), null)
  }
}

private val ANNOTATION_CLASS_ID =
    ClassId.topLevel(
        FqName(
            "com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.DataClassGenerate",
        ),
    )

private val DATA_CLASS_SUPER_CLASS_ID =
    ClassId.topLevel(
        FqName("com.facebook.kotlin.compilerplugins.dataclassgenerate.superclass.DataClassSuper"),
    )

private class DataClassGenerateTransformer(
    private val pluginContext: IrPluginContext,
    private val mode: PluginMode,
) : IrElementTransformerVoid() {

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  override fun visitClass(declaration: IrClass): IrStatement {
    // Data objects are out of scope, matching the class-builder path (`isData && kind.isClass`).
    if (!declaration.isData || declaration.kind != ClassKind.CLASS) {
      return super.visitClass(declaration)
    }

    val annotation =
        declaration.annotations.firstOrNull {
          it.symbol.owner.parentAsClass.classId == ANNOTATION_CLASS_ID
        }

    if (mode == PluginMode.STRICT && annotation == null) {
      throw DataClassGenerateStrictModeViolationException(
          generateStrictModeViolationMessage(declaration.classId?.asSingleFqName()),
      )
    }

    val shouldProcess = annotation != null || mode == PluginMode.IMPLICIT

    if (shouldProcess) {
      val toStringMode =
          annotation?.getEnumArgMode("toString_uniqueJvmName")
              ?: annotation?.getEnumArgMode("toString")
              ?: Mode.OMIT
      val equalsHashCodeMode = annotation?.getEnumArgMode("equalsHashCode") ?: Mode.KEEP

      for (function in declaration.functions.toList()) {
        if (function.origin != IrDeclarationOrigin.GENERATED_DATA_CLASS_MEMBER) {
          continue
        }

        when (function.name.asString()) {
          "toString" -> {
            if (toStringMode == Mode.OMIT) replaceWithSuperCall(function)
          }
          "equals" -> {
            if (equalsHashCodeMode == Mode.OMIT) replaceWithSuperCall(function)
          }
          "hashCode" -> {
            if (equalsHashCodeMode == Mode.OMIT) replaceWithSuperCall(function)
          }
        }
      }
    }

    injectSuperClass(declaration)

    return super.visitClass(declaration)
  }

  /**
   * Data classes that extend `Any` get `DataClassSuper` as their superclass, like the class-builder
   * path does for every data class. Runs after the `equals`/`hashCode`/`toString` rewrites, whose
   * super calls keep targeting `Any`.
   */
  @OptIn(UnsafeDuringIrConstructionAPI::class)
  private fun injectSuperClass(declaration: IrClass) {
    if (!DataClassGenerateExt.generateSuperClass) return
    // Like the class builder's `superClassLiteral == OBJECT_LITERAL`: the JVM superclass is
    // `Object` when no supertype is a class other than `Any` (interface-only classes list no
    // `Any` at all).
    val superClassIndex =
        declaration.superTypes.indexOfFirst { it.classOrNull?.owner?.kind == ClassKind.CLASS }
    if (superClassIndex >= 0 && !declaration.superTypes[superClassIndex].isAny()) return
    val superClass = pluginContext.findClassCompat(DATA_CLASS_SUPER_CLASS_ID) ?: return
    val superConstructor =
        superClass.owner.declarations.filterIsInstance<IrConstructor>().singleOrNull {
          it.parameters.isEmpty()
        } ?: return
    declaration.superTypes =
        if (superClassIndex >= 0) {
          declaration.superTypes.toMutableList().apply {
            set(superClassIndex, superClass.defaultType)
          }
        } else {
          listOf(superClass.defaultType) + declaration.superTypes
        }
    val anyClass = pluginContext.irBuiltIns.anyClass
    val retargetAnySuperCall =
        object : IrElementTransformerVoid() {
          // Anonymous objects and local classes keep their own `Any()` super calls.
          override fun visitClass(declaration: IrClass): IrStatement = declaration

          override fun visitDelegatingConstructorCall(
              expression: IrDelegatingConstructorCall,
          ): IrExpression {
            if (expression.symbol.owner.parentAsClass.symbol == anyClass) {
              expression.symbol = superConstructor.symbol
            }
            return super.visitDelegatingConstructorCall(expression)
          }
        }
    for (constructor in declaration.declarations.filterIsInstance<IrConstructor>()) {
      constructor.body?.transformChildrenVoid(retargetAnySuperCall)
    }
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  private fun replaceWithSuperCall(function: IrSimpleFunction) {
    val parentClass = function.parentAsClass
    val superClass = parentClass.superTypes.firstOrNull()?.classOrNull?.owner ?: return
    val regularParams = function.parameters.filter { it.kind == IrParameterKind.Regular }
    val superFunction =
        superClass.functions.firstOrNull { candidate ->
          candidate.name == function.name &&
              candidate.parameters.count { it.kind == IrParameterKind.Regular } ==
                  regularParams.size
        } ?: return

    val builder = DeclarationIrBuilder(pluginContext, function.symbol)
    function.body = builder.irBlockBody {
      val call =
          irCall(superFunction.symbol, function.returnType).apply {
            superQualifierSymbol = superClass.symbol
            function.parameters.forEachIndexed { index, param ->
              arguments[index] = irGet(param)
            }
          }
      +irReturn(call)
    }
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  private fun org.jetbrains.kotlin.ir.expressions.IrConstructorCall.getEnumArgMode(
      name: String,
  ): Mode? {
    val constructor = symbol.owner
    val paramIndex =
        constructor.parameters.indexOfFirst {
          it.kind == IrParameterKind.Regular && it.name == Name.identifier(name)
        }
    if (paramIndex < 0) return null
    val arg = arguments.getOrNull(paramIndex) ?: return null
    val enumEntry = (arg as? IrGetEnumValue) ?: return null
    return when (enumEntry.symbol.owner.name.asString()) {
      "KEEP" -> Mode.KEEP
      "OMIT" -> Mode.OMIT
      else -> null
    }
  }
}

/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.misc

import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.EnumValue
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

inline fun <reified T : Enum<T>> AnnotationDescriptor.getValueArgument(name: Name): T? {
  val arg = allValueArguments[name]
  // It is only possible to retrieve Enum element
  require(arg is EnumValue?)
  return arg?.safeAs<EnumValue>()?.let {
    java.lang.Enum.valueOf(T::class.java, it.enumEntryName.identifier)
  }
}

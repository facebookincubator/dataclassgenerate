// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

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

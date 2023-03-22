/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.DataClassGenerate
import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.Mode

@DataClassGenerate(toString = Mode.KEEP, equalsHashCode = Mode.KEEP)
data class A(val i: Int, val s: String)

@DataClassGenerate(toString = Mode.KEEP, equalsHashCode = Mode.KEEP)
data class B(val i: Int, val s: String)

@DataClassGenerate(toString = Mode.KEEP, equalsHashCode = Mode.KEEP)
data class C(val i: Int, val s: String)

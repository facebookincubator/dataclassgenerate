/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import android.os.Parcelable
import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.DataClassGenerate
import kotlinx.parcelize.Parcelize

@Parcelize @DataClassGenerate data class ParcelizeDataClass(val i: Int, val s: String) : Parcelable

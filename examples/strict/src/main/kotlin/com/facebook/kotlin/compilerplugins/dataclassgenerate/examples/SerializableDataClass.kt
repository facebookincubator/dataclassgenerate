/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import com.facebook.kotlin.compilerplugins.dataclassgenerate.annotation.DataClassGenerate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@DataClassGenerate
@Serializable
data class SerializableDataClass(@SerialName("name") val str: String)

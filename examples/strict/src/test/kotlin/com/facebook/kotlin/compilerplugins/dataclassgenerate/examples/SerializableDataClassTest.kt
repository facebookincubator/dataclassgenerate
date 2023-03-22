/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.kotlin.compilerplugins.dataclassgenerate.examples

import com.facebook.kotlin.compilerplugins.dataclassgenerate.GENERATED_SUPER
import org.assertj.core.api.Assertions
import org.junit.Test

private const val SERIALIZABLE_DATA_CLASS_LITERAL =
    "com.facebook.kotlin.compilerplugins.dataclassgenerate.examples.SerializableDataClass"
private val DCG_SUPER_LITERAL = GENERATED_SUPER.replace("/", ".")

class SerializableDataClassTest {
  @Test
  fun `DCG supplies DataClassSuper as a super class for non-synthetic`() {
    val serializerSuperClass = Class.forName(SERIALIZABLE_DATA_CLASS_LITERAL).superclass
    Assertions.assertThat(serializerSuperClass.canonicalName).isEqualTo(DCG_SUPER_LITERAL)
  }

  @Test
  fun `DCG does not supply DataClassSuper as a super class of synthetic $$serializer`() {
    val serializerSuperClass =
        Class.forName("$SERIALIZABLE_DATA_CLASS_LITERAL\$\$serializer").superclass
    Assertions.assertThat(serializerSuperClass.canonicalName).isNotEqualTo(DCG_SUPER_LITERAL)
  }

  @Test
  fun `DCG does not supply DataClassSuper as a super class of synthetic $Companion`() {
    val serializerSuperClass =
        Class.forName("$SERIALIZABLE_DATA_CLASS_LITERAL\$Companion").superclass
    Assertions.assertThat(serializerSuperClass.canonicalName).isNotEqualTo(DCG_SUPER_LITERAL)
  }
}

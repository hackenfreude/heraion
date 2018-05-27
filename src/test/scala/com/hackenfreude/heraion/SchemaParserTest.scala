/*
 * Copyright 2018 Hackenfreude
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hackenfreude.heraion

import org.scalatest.{ FunSuite, Inside, Matchers }

class SchemaParserTest extends FunSuite with Matchers with Inside {

  test("invalid json should throw SchemaException with message") {
    val input = "foo"
    val ex = intercept[SchemaException] {
      val _ = SchemaParser(input)
    }
    ex.msg should be(s"schema definition $input cannot be parsed to valid json")
  }

  test("valid json which cannot parse to schema should throw SchemaException with message") {
    val input = """{"foo": "bar"}"""
    val ex = intercept[SchemaException] {
      val _ = SchemaParser(input)
    }
    ex.msg should be(s"schema definition $input cannot be parsed to a valid json schema")
  }

  test("scalar type should be returned as schema type") {
    for (input_type <- JsonType.values) {
      val input = s"""{"type": "$input_type"}"""
      val result = SchemaParser(input)
      inside(result) {
        case ObjectSchema(objectSchema) =>
          objectSchema.types should contain theSameElementsAs List(input_type)
      }
    }
  }

  test("list type should be returned as schema type") {
    require(JsonType.values.length >= 2, "test assumes there is more than one JsonType")
    val input_type1 = JsonType.values.head
    val input_type2 = JsonType.values.tail.head
    val input = s"""{"type": ["$input_type1", "$input_type2"]}"""
    val result = SchemaParser(input)
    inside(result) {
      case ObjectSchema(objectSchema) =>
        objectSchema.types should contain theSameElementsAs List(input_type1, input_type2)
    }
  }

  test("boolean scalar schemas should be returned as scalar schema type") {
    val testCases = Seq(true, false)
    for (testCase <- testCases) {
      val result = SchemaParser(testCase.toString)
      inside(result) {
        case ScalarSchema(scalarSchema) =>
          scalarSchema should be(testCase)
      }
    }
  }
}

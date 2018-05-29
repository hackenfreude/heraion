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

import org.scalatest.{ FunSpec, Inside, Matchers }

class SchemaParserTest extends FunSpec with Matchers with Inside {

  describe("invalid json") {
    val input = "foo"
    it("throws SchemaException with detailed message") {
      val ex = intercept[SchemaException] {
        val _ = SchemaParser(input)
      }
      ex.msg should be(s"schema definition $input cannot be parsed to valid json")
    }
  }

  describe("valid json which cannot parse to schema") {
    val input = """{"foo": "bar"}"""
    it("throws SchemaException with detailed message") {
      val ex = intercept[SchemaException] {
        val _ = SchemaParser(input)
      }
      ex.msg should be(s"schema definition $input cannot be parsed to a valid json schema")
    }
  }

  describe("schema json with scalar type field") {
    for (input_type <- JsonType.values) {
      val typeField = s""""type": "$input_type""""
      val input = s"{$typeField}"

      describe(typeField) {
        it(s"parses type as $input_type") {
          val result = SchemaParser(input)
          //this first check leads to a clearer scalatest error, but perhaps there's a better way
          result should matchPattern { case ObjectSchema(_) => }
          inside(result) {
            case ObjectSchema(objectSchema) =>
              objectSchema.types should contain theSameElementsAs List(input_type)
          }
        }
      }
    }
  }

  describe("schema json with array type field") {
    val input_type1 = JsonType.values.head
    val input_type2 = JsonType.values.tail.head
    val input_types = s"""["$input_type1", "$input_type2"]"""
    val typeField = s""""type": $input_types"""
    val input = s"{$typeField}"

    describe(typeField) {
      it(s"parses type as $input_types") {
        val result = SchemaParser(input)
        //this first check leads to a clearer scalatest error, but perhaps there's a better way
        result should matchPattern { case ObjectSchema(_) => }
        inside(result) {
          case objectSchema @ ObjectSchema(_) =>
            objectSchema.`type`.types should contain theSameElementsAs List(input_type1, input_type2)
        }
      }
    }
  }

  describe("scalar schema with boolean value") {
    for (input <- Seq(true, false)) {
      describe(s"$input") {
        it(s"parses as $input") {
          val result = SchemaParser(s"$input")
          //this first check leads to a clearer scalatest error, but perhaps there's a better way
          result should matchPattern { case ScalarSchema(_) => }
          inside(result) {
            case scalarSchema @ ScalarSchema(_) =>
              scalarSchema.schema should be(input)
          }
        }
      }
    }
  }
}

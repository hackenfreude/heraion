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

import io.circe.{ Json, JsonObject }
import org.scalatest.prop.PropertyChecks
import org.scalatest.{ FunSpec, Matchers, OptionValues }

class TypeValidatorTest extends FunSpec with Matchers with OptionValues with PropertyChecks {

  //  val arbBoolean: Arbitrary[Json] = Arbitrary[Json](Gen.oneOf(
  //    Json.fromBoolean(true),
  //    Json.fromBoolean(false)
  //  ))

  //  def genBool = allOf(true, false)

  describe("TypeValidator") {

    describe("with null schema") {
      val nullSchema = ObjectSchema(Type(List(JsonType.`null`)))
      it("should return true with null input") {
        TypeValidator(Json.Null, nullSchema) should be(true)
      }
    }

    describe("with boolean schema") {
      val booleanSchema = ObjectSchema(Type(List(JsonType.boolean)))
      it("should return true with true input") {
        TypeValidator(Json.fromBoolean(true), booleanSchema) should be(true)
      }
      it("should return true with false input") {
        TypeValidator(Json.fromBoolean(false), booleanSchema) should be(true)
      }
    }

    describe("with string schema") {
      val stringSchema = ObjectSchema(Type(List(JsonType.string)))
      it("should return true for any string input") {
        forAll { string: String =>
          whenever(string.nonEmpty) {
            val input = Json.fromString(string)
            TypeValidator(input, stringSchema) should be(true)
          }
        }
      }
    }

    describe("with number schema") {
      val numberSchema = ObjectSchema(Type(List(JsonType.number)))
      it("should return true for any long input") {
        forAll { long: Long =>
          whenever(long.isInstanceOf[Long]) {
            val input = Json.fromLong(long)
            TypeValidator(input, numberSchema) should be(true)
          }
        }
      }

      it("should return true for any double input") {
        forAll { double: Double =>
          whenever(double.isInstanceOf[Double]) {
            val input = Json.fromDouble(double).value
            TypeValidator(input, numberSchema) should be(true)
          }
        }
      }
    }

    describe("with integer schema") {
      val integerSchema = ObjectSchema(Type(List(JsonType.integer)))
      it("should return true for any (mathematical) integer input") {
        forAll { long: Long =>
          whenever(long.isInstanceOf[Long]) {
            val input = Json.fromLong(long)
            TypeValidator(input, integerSchema) should be(true)
          }
        }
      }
    }

    describe("with object schema") {
      val objectSchema = ObjectSchema(Type(List(JsonType.`object`)))
      it("should return true with empty object") {
        TypeValidator(Json.fromJsonObject(JsonObject.empty), objectSchema) should be(true)
      }
      it("should return true with non-empty object") {
        TypeValidator(Json.fromJsonObject(JsonObject.singleton("foo", Json.fromString("bar"))), objectSchema) should be(true)
      }
    }
  }
}

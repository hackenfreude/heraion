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

import io.circe.Printer
import io.circe.parser._
import io.circe.syntax._
import org.scalatest._

import scala.io.Source
import scala.util.Try

abstract class JsonSchema6TestBase(testResourceFileName: String) extends FunSpec with BeforeAndAfterAll with Matchers with OptionValues with GivenWhenThen {

  private lazy val printer = Printer(preserveOrder = true, dropNullValues = true, indent = "")

  private lazy val testResourceRelativePath = s"json-schema-test-suite/tests/draft6/$testResourceFileName"

  private lazy val testCases: List[JsonSchemaTestCase] = {

    val fileContents = Try(Source.fromResource(testResourceRelativePath).mkString).getOrElse("")
    if (fileContents.isEmpty)
      fail(
        s"""Test harness could not read the test resource at $testResourceRelativePath.
           |This indicates a problem with the harness itself.
           |Good luck.""".stripMargin
      )

    decode[List[JsonSchemaTestCase]](fileContents) match {
      case Left(_) => fail(
        s"""Test harness could not read parse the contents of $testResourceRelativePath into test cases.
           |This indicates a problem with the harness itself.
           |Good luck.""".stripMargin
      )
      case Right(parsedSuites) => parsedSuites
    }
  }

  //this is a hacky way to force reading and fail early
  override def beforeAll(): Unit = {
    val _ = testCases
  }

  describe(s"test file: $testResourceRelativePath") {
    for (testCase <- testCases) {
      describe(s"test case: ${testCase.description}") {

        it("should be parsable schema") {
          Given(s"the schema:\n${testCase.schema}")

          When("the user parses the schema")
          val result = SchemaParser(testCase.schema.toString()).asJson

          Then("the schema should be valid")
          result.pretty(printer) should be(testCase.schema.pretty(printer))
        }

        for (test <- testCase.tests) {
          it(s"test: ${test.description}") {

            Given(s"the input:\n${test.data}")

            And(s"the schema:\n${testCase.schema}")

            When("the user validates the input")
            val parsedSchema = testCase.schema.as[Schema].toOption.value
            val result = Validator(test.data, parsedSchema)

            Then(s"the validation result should be ${test.valid}")
            result should be(test.valid)
          }
        }
      }
    }
  }
}

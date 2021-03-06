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

import io.circe.parser._

object SchemaParser {
  def apply(input: String): Schema = {
    parse(input) match {
      case Left(_) => throw SchemaException(s"schema definition $input cannot be parsed to valid json")
      case Right(json) => json.as[Schema].toOption match {
        case Some(schema) => schema
        case None         => throw SchemaException(s"schema definition $input cannot be parsed to a valid json schema")
      }
    }
  }
}

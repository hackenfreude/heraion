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

import io.circe.Json

object Validator {
  def apply(input: Json, schema: Schema): Boolean = (for (t <- schema.`type`.types) yield {
    t match {
      case "integer" if input.isNumber => input.as[Long].toOption.isDefined
      case "number"                    => input.isNumber
      case "string"                    => input.isString
      case "object"                    => input.isObject
      case "array"                     => input.isArray
      case "boolean"                   => input.isBoolean
      case "null"                      => input.isNull
      case _                           => false
    }
  }).contains(true)
}

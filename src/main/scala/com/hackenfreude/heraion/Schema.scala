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

import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{ Decoder, Encoder, HCursor, Json }

sealed trait Schema

object Schema {
  implicit val schemaDecoder: Decoder[Schema] = (c: HCursor) => {
    if (c.value.isObject) {
      c.value.as[ObjectSchema]
    } else {
      c.value.as[ScalarSchema]
    }
  }
  implicit val schemaEncoder: Encoder[Schema] = {
    case ObjectSchema(x) =>
      if (x.types.length == 1) {
        Json.obj("type" -> x.types.head.asJson)
      } else {
        Json.obj("type" -> x.types.asJson)
      }
    case ScalarSchema(x) =>
      Json.fromBoolean(x)
  }
}

case class ObjectSchema(`type`: Type) extends Schema

object ObjectSchema {
  implicit val objectSchemaDecoder: Decoder[ObjectSchema] = deriveDecoder[ObjectSchema]
  implicit val objectSchemaEncoder: Encoder[ObjectSchema] = deriveEncoder[ObjectSchema]
}

case class ScalarSchema(schema: Boolean) extends Schema

object ScalarSchema {
  implicit val scalarSchemaDecoder: Decoder[ScalarSchema] = Decoder.decodeBoolean.map(bool => {
    ScalarSchema(bool)
  })
  implicit val scalarSchemaEncoder: Encoder[ScalarSchema] = deriveEncoder[ScalarSchema]
}

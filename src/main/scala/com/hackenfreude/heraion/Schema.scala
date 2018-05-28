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
import io.circe.{ Decoder, Encoder }
import cats.syntax.functor._

sealed trait Schema

object Schema {
  implicit val schemaEncoder: Encoder[Schema] = Encoder.instance {
    case objectSchema @ ObjectSchema(_) => objectSchema.asJson
    case scalarSchema @ ScalarSchema(_) => scalarSchema.schema.asJson
  }

  implicit val schemaDecoder: Decoder[Schema] =
    List[Decoder[Schema]](
      Decoder[ObjectSchema].widen,
      Decoder[ScalarSchema].widen
    ).reduceLeft(_ or _)
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

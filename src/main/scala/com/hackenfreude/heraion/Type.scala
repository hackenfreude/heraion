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

import io.circe.{ Decoder, Encoder }

case class Type(types: List[JsonType])

object Type {

  implicit val typeDecoder: Decoder[Type] = Decoder[JsonType].map(str => Type(List(str)))
    .or(Decoder[List[JsonType]].map(list => Type(list)))

  implicit val typeEncoder: Encoder[Type] = (a: Type) => {
    if (a.types.length == 1) {
      Encoder.encodeString.apply(a.types.head.toString)
    } else {
      Encoder.encodeList[String].apply(a.types.map(_.toString))
    }
  }
}

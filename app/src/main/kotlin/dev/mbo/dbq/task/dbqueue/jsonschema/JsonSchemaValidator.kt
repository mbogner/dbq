/*
 * Copyright 2022 mbo.dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.mbo.dbq.task.dbqueue.jsonschema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component

@Component
class JsonSchemaValidator(
    private val jsonSchemaCache: JsonSchemaCache,
    private val objectMapper: ObjectMapper,
) {

    fun validatePayload(hash: String, schemaStr: String, payload: String): JsonNode {
        val schema = jsonSchemaCache.parse(hash, schemaStr)
        val json = objectMapper.readTree(payload)
        val validationMessages = schema.validate(json)
        if (validationMessages.isNotEmpty()) {
            throw ListedJsonSchemaException(validationMessages)
        }
        return json
    }

}
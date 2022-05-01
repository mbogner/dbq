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

package dev.mbo.dbq.task.dbqueue.impl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import dev.mbo.dbq.task.dbqueue.DbQueueProcessor

abstract class AbstractDbQueueProcessor<M>(
    private val objectMapper: ObjectMapper,
) : DbQueueProcessor<M> {

    /** Override this method if there is more than one processor for a type. */
    override fun order(): Int {
        return 0
    }

    /** Allows reading JsonNode into payload object. Workaround because generics didn't work in processor. */
    protected fun parseJsonNode(jsonNode: JsonNode): M {
        return objectMapper.treeToValue(jsonNode, payloadClass())
    }

}
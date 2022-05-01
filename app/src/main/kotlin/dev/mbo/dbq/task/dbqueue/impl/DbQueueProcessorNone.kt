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
import dev.mbo.dbq.db.model.DbQueue
import dev.mbo.dbq.db.model.types.PayloadType
import dev.mbo.dbq.task.dbqueue.impl.payload.NonePayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DbQueueProcessorNone(objectMapper: ObjectMapper) :
    AbstractDbQueueProcessor<NonePayload>(objectMapper) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun process(task: DbQueue, jsonNode: JsonNode): DbQueue {
        val payload: NonePayload = parseJsonNode(jsonNode)
        log.debug("payload: {}", payload)
        return task
    }

    override fun payloadClass(): Class<NonePayload> {
        return NonePayload::class.java
    }

    override fun supportedPayloadType(): PayloadType {
        return PayloadType.NONE
    }

}
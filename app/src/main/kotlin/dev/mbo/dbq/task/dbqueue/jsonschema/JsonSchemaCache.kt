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

import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class JsonSchemaCache(
    private val jsonSchemaFactory: JsonSchemaFactory,
    @Value("\${application.jsonschemcache.validitySeconds:25}") private val validitySeconds: Long,
    @Value("\${application.jsonschemcache.initialCapacity:32}") private val initialCapacity: Int,
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)
    private val data = HashMap<String, CachedJsonSchema>(initialCapacity)

    fun parse(sha: String, jsonSchemaStr: String): JsonSchema {
        var entry = data[sha]
        if (null == entry) {
            log.debug("cache miss")
            entry = CachedJsonSchema(
                Instant.now().plusSeconds(validitySeconds),
                jsonSchemaFactory.getSchema(jsonSchemaStr),
            )
            data[sha] = entry
        } else {
            log.debug("cache hit - valid until {}", entry.validUntil)
            entry.updateValidUntil(Instant.now().plusSeconds(validitySeconds))
        }
        return entry.jsonSchema
    }

    @Scheduled(cron = "\${application.jsonschemcache.cleanup.cron:*/30 * * * * *}")
    fun cleanup() {
        val now = Instant.now()
        val oldSize = data.size
        data.filter { it.value.validUntil.isBefore(now) }.keys.forEach { data.remove(it) }
        log.debug("cleaned up schema cache at {}, oldSize={}, newSize={}", now, oldSize, data.size)
    }
}
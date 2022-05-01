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

package dev.mbo.dbq.task.dbqueue

import dev.mbo.dbq.db.model.DbQueue
import dev.mbo.dbq.db.model.types.QueueStatus
import dev.mbo.dbq.db.repo.DbQueueRepository
import dev.mbo.dbq.db.repo.LockRepository
import dev.mbo.dbq.task.base.AbstractTask
import dev.mbo.dbq.task.dbqueue.jsonschema.JsonSchemaValidator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import javax.transaction.Transactional

@Component
class DbQueueTask constructor(
    lockRepository: LockRepository,
    private val dbQueueRepository: DbQueueRepository,
    private val dbQueueMap: DbQueueMap,
    private val jsonSchemaValidator: JsonSchemaValidator,
    @Value("\${application.task.DbQueueTask.enabled:true}") private val taskEnabled: Boolean,
    @Value("\${application.task.DbQueueTask.delaySec:4}") private val delaySec: Int,
) : AbstractTask<Long, DbQueue>(
    DbQueueTask::class.java.simpleName,
    taskEnabled,
    delaySec,
    lockRepository,
    dbQueueRepository
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${application.task.DbQueueTask.cron:*/5 * * * * *}")
    fun schedule() {
        super.execute()
    }

    override fun load(): List<DbQueue> {
        return dbQueueRepository.findScheduled(QueueStatus.nonFinalStatus(), Instant.now())
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    override fun process(task: DbQueue): DbQueue {
        val jsonNode = jsonSchemaValidator.validatePayload(
            task.schema.jsonSchemaHash!!, task.schema.jsonSchema, task.payload
        )
        var tmpTask = task
        dbQueueMap[task.schema.type]?.forEach {
            try {
                MDC.put("processor", it.javaClass.simpleName)
                log.debug("running processor")
                tmpTask = it.process(tmpTask, jsonNode)
                log.debug("processor done")
            } finally {
                MDC.remove("processor")
            }
        }
        return task
    }

}
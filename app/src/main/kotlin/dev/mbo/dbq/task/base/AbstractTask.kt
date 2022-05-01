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

package dev.mbo.dbq.task.base

import dev.mbo.dbq.db.model.base.QueueEntity
import dev.mbo.dbq.db.repo.LockRepository
import dev.mbo.dbq.db.repo.base.ExtendedJpaRepository
import dev.mbo.dbq.task.dbqueue.jsonschema.ListedJsonSchemaException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.io.Serializable
import javax.transaction.Transactional

/**
 * @param id The id of the task in the lock table.
 * @param enabled Display a warning instead of running the task.
 * @param delaySec Number of seconds between locks to handle time diffs between clocks.
 * @param lockRepo The instance of LockRepository to use.
 */
abstract class AbstractTask<I : Serializable, M : QueueEntity<I>> constructor(
    private val id: String,
    private val enabled: Boolean,
    private val delaySec: Int,
    private val lockRepo: LockRepository,
    private val repository: ExtendedJpaRepository<I, M>,
) {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    /**
     * Call this method in the inherited class from Scheduled method.
     */
    open fun execute() {
        if (!enabled) {
            log.warn("task {} disabled", id)
            return
        }
        MDC.put("task", id)
        if (lockRepo.lock(id, delaySec)) {
            log.debug("starting task $id")
            run()
            log.debug("task $id finished")
        } else {
            log.debug("couldn't get lock $id")
        }
        MDC.clear()
    }

    open fun run() {
        val tasks = load()
        log.debug("processing {} task(s)", tasks.size)
        for (task in tasks) {
            MDC.put("task_id", task.getIdentifier().toString())
            try {
                val processed = process(task)
                taskSucceeded(processed)
            } catch (exc: ListedJsonSchemaException) {
                taskFailed(task, exc)
            } catch (exc: Exception) {
                taskRetry(task, exc)
            }
            MDC.remove("task_id")
        }
    }

    /** Load data to process from db. */
    abstract fun load(): List<M>

    /** Process a task */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    abstract fun process(task: M): M

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected open fun taskRetry(task: M, exc: Exception): M {
        log.error("task {} failed - retry", task.getIdentifier(), exc)
        task.queueMeta.retry(exc)
        return repository.save(task)
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected open fun taskFailed(task: M, exc: Exception): M {
        log.error("task {} failed - no retry", task.getIdentifier(), exc)
        task.queueMeta.fail(exc)
        return repository.save(task)
    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    protected open fun taskSucceeded(task: M): M {
        log.debug("task {} succeeded", task.getIdentifier())
        task.queueMeta.success()
        return repository.save(task)
    }

}

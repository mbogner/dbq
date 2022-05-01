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

package dev.mbo.dbq.db.model.types.retrystrategy

import dev.mbo.dbq.db.model.types.QueueMeta
import dev.mbo.dbq.db.model.types.QueueStatus
import org.apache.commons.lang3.exception.ExceptionUtils
import java.time.Instant

abstract class AbstractRetryDelay : RetryDelay {

    companion object {
        private fun assertStatusNonFinal(queueMeta: QueueMeta) {
            if (queueMeta.status.final) {
                throw IllegalStateException("trying to process a final entry")
            }
        }

        fun sharedAttempt(queueMeta: QueueMeta, exc: Exception? = null) {
            assertStatusNonFinal(queueMeta)
            queueMeta.attempts += 1
            queueMeta.error = if (null == exc) null else ExceptionUtils.getStackTrace(exc)
        }
    }

    protected open fun limitRetryDelaySeconds(queueMeta: QueueMeta) {
        if (queueMeta.retryDelaySeconds > queueMeta.retryDelaySecondsMax) {
            queueMeta.retryDelaySeconds = queueMeta.retryDelaySecondsMax
        }
    }

    protected open fun failed(queueMeta: QueueMeta, nextStatus: QueueStatus = QueueStatus.FAILED) {
        queueMeta.status = nextStatus
        queueMeta.nextAfter = null
        queueMeta.failedAt = Instant.now()
    }

    override fun update(queueMeta: QueueMeta, exc: Exception?, overrideNextAfter: Instant?): QueueMeta {
        sharedAttempt(queueMeta, exc)
        if (queueMeta.attempts > queueMeta.maxAttempts) {
            failed(queueMeta, QueueStatus.MAX_RETRIES)
        } else {
            queueMeta.status = QueueStatus.RETRY
            calculateRetryDelaySeconds(queueMeta)
            limitRetryDelaySeconds(queueMeta)
            updateNextAfter(queueMeta, overrideNextAfter)
        }
        return queueMeta
    }

    protected open fun updateNextAfter(queueMeta: QueueMeta, overrideNextAfter: Instant?) {
        if (null == overrideNextAfter) {
            queueMeta.nextAfter = Instant.now().plusSeconds(queueMeta.retryDelaySeconds.toLong())
        } else {
            queueMeta.nextAfter = overrideNextAfter
        }
    }

    abstract fun calculateRetryDelaySeconds(queueMeta: QueueMeta)
}
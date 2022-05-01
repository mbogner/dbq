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
import dev.mbo.dbq.db.model.types.RetryDelayStrategy
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class RetryDelayProcessorTest {

    @Test
    fun testNone() {
        val queueMeta = QueueMeta(retryDelayStrategy = RetryDelayStrategy.NONE).retry()
        assertThat(queueMeta).isNotNull
        assertThat(queueMeta.status).isEqualTo(QueueStatus.FAILED)
        assertThat(queueMeta.attempts).isEqualTo(1)
        assertThat(queueMeta.nextAfter).isNull()
        assertThat(queueMeta.succeededAt).isNull()
        assertThat(queueMeta.failedAt).isNotNull
        assertThat(queueMeta.error).isNull()

        Assertions.assertThrows(IllegalStateException::class.java) {
            queueMeta.retry()
        }
        Assertions.assertThrows(IllegalStateException::class.java) {
            queueMeta.success()
        }
    }

    @Test
    fun testLinear() {
        var attempt = 1
        val queueMeta = QueueMeta(retryDelayStrategy = RetryDelayStrategy.LINEAR).retry()
        assertThat(queueMeta.status).isEqualTo(QueueStatus.RETRY)
        assertThat(queueMeta.nextAfter).isNotNull
        assertThat(queueMeta.succeededAt).isNull()
        assertThat(queueMeta.failedAt).isNull()
        assertThat(queueMeta.error).isNull()
        assertThat(queueMeta.attempts).isEqualTo(attempt++)
        assertThat(queueMeta.retryDelaySeconds).isEqualTo(2)

        queueMeta.retry(exc = Exception("test"))
        assertThat(queueMeta.status).isEqualTo(QueueStatus.RETRY)
        assertThat(queueMeta.nextAfter).isNotNull
        assertThat(queueMeta.succeededAt).isNull()
        assertThat(queueMeta.failedAt).isNull()
        assertThat(queueMeta.error).isNotNull
        assertThat(queueMeta.attempts).isEqualTo(attempt++)
        assertThat(queueMeta.retryDelaySeconds).isEqualTo(4)

        queueMeta.retry()
        assertThat(queueMeta.attempts).isEqualTo(attempt++)
        assertThat(queueMeta.retryDelaySeconds).isEqualTo(6)

        queueMeta.success()
        assertThat(queueMeta.status).isEqualTo(QueueStatus.DONE)
        assertThat(queueMeta.nextAfter).isNull()
        assertThat(queueMeta.succeededAt).isNotNull
        assertThat(queueMeta.failedAt).isNull()
        assertThat(queueMeta.error).isNull()
        assertThat(queueMeta.attempts).isEqualTo(attempt)
        assertThat(queueMeta.retryDelaySeconds).isEqualTo(6)

        Assertions.assertThrows(IllegalStateException::class.java) {
            queueMeta.success()
        }
        Assertions.assertThrows(IllegalStateException::class.java) {
            queueMeta.retry()
        }
    }

    @Test
    fun testSquare() {
        var attempt = 1
        val queueMeta = QueueMeta(retryDelayStrategy = RetryDelayStrategy.SQUARE).retry()
        assertThat(queueMeta).isNotNull
        assertThat(queueMeta.status).isEqualTo(QueueStatus.RETRY)
        assertThat(queueMeta.attempts).isEqualTo(attempt++)
        assertThat(queueMeta.nextAfter).isNotNull
        assertThat(queueMeta.succeededAt).isNull()
        assertThat(queueMeta.failedAt).isNull()
        assertThat(queueMeta.error).isNull()
        assertThat(queueMeta.retryDelaySeconds).isEqualTo(4)

        queueMeta.retry(exc = Exception("test"))
        assertThat(queueMeta.status).isEqualTo(QueueStatus.RETRY)
        assertThat(queueMeta.attempts).isEqualTo(attempt++)
        assertThat(queueMeta.nextAfter).isNotNull
        assertThat(queueMeta.succeededAt).isNull()
        assertThat(queueMeta.failedAt).isNull()
        assertThat(queueMeta.error).isNotNull
        assertThat(queueMeta.retryDelaySeconds).isEqualTo(4)

        queueMeta.retry()
        assertThat(queueMeta.error).isNull()
        assertThat(queueMeta.attempts).isEqualTo(attempt++)
        assertThat(queueMeta.retryDelaySeconds).isEqualTo(9)

        queueMeta.retry(exc = Exception("test2"))
        assertThat(queueMeta.error).isNotNull
        assertThat(queueMeta.attempts).isEqualTo(attempt++)
        assertThat(queueMeta.retryDelaySeconds).isEqualTo(16)

        queueMeta.success()
        assertThat(queueMeta.status).isEqualTo(QueueStatus.DONE)
        assertThat(queueMeta.nextAfter).isNull()
        assertThat(queueMeta.succeededAt).isNotNull
        assertThat(queueMeta.failedAt).isNull()
        assertThat(queueMeta.error).isNull()
        assertThat(queueMeta.attempts).isEqualTo(attempt)
        assertThat(queueMeta.retryDelaySeconds).isEqualTo(16)

        Assertions.assertThrows(IllegalStateException::class.java) {
            queueMeta.success()
        }
        Assertions.assertThrows(IllegalStateException::class.java) {
            queueMeta.retry()
        }
    }

}
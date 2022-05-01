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

package dev.mbo.dbq.db.model

import dev.mbo.dbq.db.model.types.PayloadType
import dev.mbo.dbq.db.model.types.QueueMeta
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import javax.validation.Validation

class Fixtures {

    @Test
    fun testFixtures() {
        randomQueueMeta()
        randomDbQueuePayloadSchema()
        randomDbQueue()
    }

    companion object {
        private val validator = Validation.buildDefaultValidatorFactory().validator

        fun randomQueueMeta(): QueueMeta {
            val res = QueueMeta()
            assertThat(validator.validate(res)).isEmpty()
            return res
        }

        fun randomDbQueuePayloadSchema(): DbQueuePayloadSchema {
            val res = DbQueuePayloadSchema(
                PayloadType.NONE,
                ThreadLocalRandom.current().nextInt(0, 1000),
                "{}",
            )
            assertThat(validator.validate(res)).isEmpty()
            return res
        }

        fun randomDbQueue(): DbQueue {
            val res = DbQueue(
                randomDbQueuePayloadSchema(),
                "{}",
                randomQueueMeta(),
            )
            assertThat(validator.validate(res)).isEmpty()
            return res
        }

        fun randomLock(): Lock {
            val res = Lock(
                "lock" + UUID.randomUUID()
            )
            assertThat(validator.validate(res)).isEmpty()
            return res
        }
    }

}
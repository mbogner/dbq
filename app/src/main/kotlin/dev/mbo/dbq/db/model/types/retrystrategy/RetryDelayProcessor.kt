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
import dev.mbo.dbq.db.model.types.RetryDelayStrategy
import dev.mbo.dbq.db.model.types.retrystrategy.impl.RetryDelayLinear
import dev.mbo.dbq.db.model.types.retrystrategy.impl.RetryDelayNone
import dev.mbo.dbq.db.model.types.retrystrategy.impl.RetryDelaySquare
import java.time.Instant

class RetryDelayProcessor {

    companion object : RetryDelay {
        private val map = retryDelayStrategiesMap()

        override fun update(queueMeta: QueueMeta, exc: Exception?, overrideNextAfter: Instant?): QueueMeta {
            val strategy = map[queueMeta.retryDelayStrategy]
                ?: throw IllegalStateException("no implementation for strategy ${queueMeta.retryDelayStrategy}")
            strategy.update(queueMeta, exc, overrideNextAfter)
            return queueMeta
        }

        private fun retryDelayStrategiesMap(): Map<RetryDelayStrategy, RetryDelay> {
            val strategies = listOf(
                RetryDelayNone(),
                RetryDelayLinear(),
                RetryDelaySquare(),
            )
            val response = HashMap<RetryDelayStrategy, RetryDelay>(strategies.size)
            for (strategy in strategies) {
                response[strategy.supportedStrategy()] = strategy
            }
            return response
        }

        override fun supportedStrategy(): RetryDelayStrategy {
            throw IllegalAccessError()
        }
    }


}
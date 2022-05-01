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

package dev.mbo.dbq.db.model.types.retrystrategy.impl

import dev.mbo.dbq.db.model.types.QueueMeta
import dev.mbo.dbq.db.model.types.RetryDelayStrategy
import dev.mbo.dbq.db.model.types.retrystrategy.AbstractRetryDelay
import dev.mbo.dbq.db.model.types.retrystrategy.RetryDelay
import org.springframework.stereotype.Component

@Component
class RetryDelaySquare : AbstractRetryDelay(), RetryDelay {

    override fun supportedStrategy(): RetryDelayStrategy {
        return RetryDelayStrategy.SQUARE
    }

    override fun calculateRetryDelaySeconds(queueMeta: QueueMeta) {
        val attempts = if (queueMeta.attempts < 2) 2 else queueMeta.attempts
        queueMeta.retryDelaySeconds = attempts * attempts
    }

}
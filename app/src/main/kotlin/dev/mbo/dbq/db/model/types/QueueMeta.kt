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

package dev.mbo.dbq.db.model.types

import dev.mbo.dbq.db.model.types.retrystrategy.AbstractRetryDelay
import dev.mbo.dbq.db.model.types.retrystrategy.RetryDelayProcessor
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

@Embeddable
class QueueMeta {
    companion object {
        val DEFAULT_STATUS = QueueStatus.CREATED
        const val DEFAULT_MAX_ATTEMPTS = 2147483647
        val DEFAULT_RETRY_DELAY_STRATEGY = RetryDelayStrategy.SQUARE
        const val DEFAULT_RETRY_DELAY_SECONDS_MAX = 604800 // 1 week
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "q_status", nullable = false)
    var status: QueueStatus = DEFAULT_STATUS

    @Column(name = "q_succeeded_at")
    var succeededAt: Instant? = null

    @Column(name = "q_failed_at")
    var failedAt: Instant? = null

    @Column(name = "q_next_after")
    var nextAfter: Instant? = null

    @NotNull
    @Column(name = "q_attempts", nullable = false)
    var attempts: Int = 0

    @NotNull
    @Min(1)
    @Column(name = "q_max_attempts", nullable = false)
    var maxAttempts: Int = DEFAULT_MAX_ATTEMPTS

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "q_retry_delay_strategy", nullable = false)
    var retryDelayStrategy: RetryDelayStrategy = DEFAULT_RETRY_DELAY_STRATEGY

    @NotNull
    @Min(0)
    @Column(name = "q_retry_delay_seconds", nullable = false)
    var retryDelaySeconds: Int = 0

    @NotNull
    @Min(1)
    @Column(name = "q_retry_delay_seconds_max", nullable = false)
    var retryDelaySecondsMax: Int = DEFAULT_RETRY_DELAY_SECONDS_MAX

    @Column(name = "q_error")
    var error: String? = null

    constructor()
    constructor(
        status: QueueStatus = DEFAULT_STATUS,
        succeededAt: Instant? = null,
        failedAt: Instant? = null,
        nextAfter: Instant? = null,
        attempts: Int = 0,
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
        retryDelayStrategy: RetryDelayStrategy = DEFAULT_RETRY_DELAY_STRATEGY,
        retryDelaySeconds: Int = 0,
        retryDelaySecondsMax: Int = DEFAULT_RETRY_DELAY_SECONDS_MAX,
        error: String? = null,
    ) {
        this.status = status
        this.succeededAt = succeededAt
        this.failedAt = failedAt
        this.nextAfter = nextAfter
        this.attempts = attempts
        this.maxAttempts = maxAttempts
        this.retryDelayStrategy = retryDelayStrategy
        this.retryDelaySeconds = retryDelaySeconds
        this.retryDelaySecondsMax = retryDelaySecondsMax
        this.error = error
    }

    fun success() {
        AbstractRetryDelay.sharedAttempt(this)
        status = QueueStatus.DONE
        succeededAt = Instant.now()
        failedAt = null
        nextAfter = null
    }

    fun fail(exc: Exception? = null) {
        AbstractRetryDelay.sharedAttempt(this, exc)
        status = QueueStatus.FAILED
        succeededAt = null
        failedAt = Instant.now()
        nextAfter = null
    }

    fun retry(exc: Exception? = null, overrideNextAfter: Instant? = null): QueueMeta {
        return RetryDelayProcessor.update(queueMeta = this, exc = exc, overrideNextAfter = overrideNextAfter)
    }

    override fun equals(other: Any?): Boolean {
        if (null == other) return false
        if (this === other) return true
        if (javaClass != other.javaClass) return false
        other as QueueMeta
        return EqualsBuilder()
            .append(status, other.status)
            .append(succeededAt, other.succeededAt)
            .append(failedAt, other.failedAt)
            .append(nextAfter, other.nextAfter)
            .append(attempts, other.attempts)
            .append(maxAttempts, other.maxAttempts)
            .append(retryDelayStrategy, other.retryDelayStrategy)
            .append(retryDelaySeconds, other.retryDelaySeconds)
            .append(retryDelaySecondsMax, other.retryDelaySecondsMax)
            .append(error, other.error)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(17, 37)
            .append(status)
            .append(succeededAt)
            .append(failedAt)
            .append(nextAfter)
            .append(attempts)
            .append(maxAttempts)
            .append(retryDelayStrategy)
            .append(retryDelaySeconds)
            .append(retryDelaySecondsMax)
            .append(error)
            .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this)
            .append("status", status)
            .append("succeededAt", succeededAt)
            .append("failedAt", failedAt)
            .append("nextAfter", nextAfter)
            .append("attempts", attempts)
            .append("maxAttempts", maxAttempts)
            .append("retryDelayStrategy", retryDelayStrategy)
            .append("retryDelaySeconds", retryDelaySeconds)
            .append("retryDelaySecondsMax", retryDelaySecondsMax)
            .append("error", error)
            .toString()
    }

}
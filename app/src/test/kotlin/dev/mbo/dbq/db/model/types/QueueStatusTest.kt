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

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class QueueStatusTest {

    @Test
    fun nonFinalStatus() {
        val res = QueueStatus.nonFinalStatus()
        val res2 = QueueStatus.nonFinalStatus()
        assertThat(res === res2).isTrue
        assertThat(res).isNotEmpty
        res.forEach { assertThat(it.final).isFalse() }
    }

    @Test
    fun finalStatus() {
        val res = QueueStatus.finalStatus()
        val res2 = QueueStatus.finalStatus()
        assertThat(res === res2).isTrue
        assertThat(res).isNotEmpty
        res.forEach { assertThat(it.final).isTrue() }
    }
}
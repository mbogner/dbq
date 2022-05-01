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

package dev.mbo.dbq.db.model.base

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class BaseEntityTest {

    @Test
    fun testToString() {
        assertThat(SampleEntity(null).toString()).isEqualTo("SampleEntity{id=null}")
        assertThat(SampleEntity(1).toString()).isEqualTo("SampleEntity{id=1}")
    }

    @Test
    fun testHashCode() {
        assertThat(SampleEntity(null).hashCode()).isEqualTo(SampleEntity::class.java.name.hashCode())
        assertThat(SampleEntity(1).hashCode()).isEqualTo(SampleEntity::class.java.name.hashCode())
    }

    @Test
    fun testEquals() {
        val sample1 = SampleEntity(1)
        val sample1Copy = SampleEntity(1)
        val sample2 = SampleEntity(2)
        val sampleNull = SampleEntity(null)
        val sampleNullCopy = SampleEntity(null)

        assertThat(sample1).isEqualTo(sample1)
        assertThat(sampleNull).isEqualTo(sampleNull)

        assertThat(sampleNull).isNotEqualTo(sampleNullCopy)
        assertThat(sampleNullCopy).isNotEqualTo(sampleNull)

        assertThat(sample1).isEqualTo(sample1Copy)
        assertThat(sample1Copy).isEqualTo(sample1)

        assertThat(sample1).isNotEqualTo(sample2)
        assertThat(sample2).isNotEqualTo(sample1)

        assertThat(sample1).isNotEqualTo(sampleNull)
        assertThat(sampleNull).isNotEqualTo(sample1)

        assertThat(sampleNull).isNotEqualTo("")
        assertThat("").isNotEqualTo(sampleNull)

        assertThat(sample1).isNotEqualTo("")
        assertThat("").isNotEqualTo(sample1)
    }

    class SampleEntity(
        var id: Long?
    ) : BaseEntity<Long>() {

        override fun getIdentifier(): Long? {
            return id
        }

    }

}
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

import java.io.Serializable
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity<T : Serializable> : Identifiable<T> {

    override fun toString(): String {
        return "${javaClass.simpleName}${loggedFields()}"
    }

    protected open fun loggedFields(): Map<String, *> {
        return mapOf("id" to getIdentifier())
    }

    override fun hashCode(): Int {
        return javaClass.name.hashCode()
    }


    override fun equals(other: Any?): Boolean {
        other ?: return false
        if (this === other) return true
        if (javaClass != other.javaClass) return false
        if (other !is Identifiable<*>) return false
        val id = getIdentifier()
        return id != null && id == other.getIdentifier()
    }

}
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

import dev.mbo.dbq.db.model.base.LockingEntity
import dev.mbo.dbq.db.model.types.PayloadType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(
    name = "db_queue_payload_schema", uniqueConstraints = [
        UniqueConstraint(
            name = "uc_db_queue_payload_schema__type_schema_version",
            columnNames = ["type", "schema_version"]
        )
    ]
)
class DbQueuePayloadSchema : LockingEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "db_queue_payload_schema_id_seq")
    @SequenceGenerator(
        name = "db_queue_payload_schema_id_seq", sequenceName = "db_queue_payload_schema_id_seq",
        initialValue = 1, allocationSize = 1
    )
    var id: Long? = null

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 32)
    var type: PayloadType = PayloadType.NONE

    @NotNull
    @Column(name = "schema_version", nullable = false)
    var schemaVersion: Int = 0

    @NotBlank
    @Column(name = "json_schema", nullable = false, columnDefinition = "text")
    var jsonSchema: String = "{}"

    // created in db
    @Column(name = "json_schema_hash", nullable = false, updatable = false, insertable = false, length = 96)
    var jsonSchemaHash: String? = null

    constructor() : super()
    constructor(type: PayloadType, schemaVersion: Int, jsonSchema: String) : super() {
        this.type = type
        this.schemaVersion = schemaVersion
        this.jsonSchema = jsonSchema
    }


    override fun getIdentifier(): Long? {
        return id
    }
}
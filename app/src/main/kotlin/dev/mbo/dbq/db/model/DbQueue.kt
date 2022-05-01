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

import dev.mbo.dbq.db.model.base.QueueEntity
import dev.mbo.dbq.db.model.types.QueueMeta
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ForeignKey
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "db_queue")
class DbQueue : QueueEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "db_queue_id_seq")
    @SequenceGenerator(
        name = "db_queue_id_seq", sequenceName = "db_queue_id_seq",
        initialValue = 1, allocationSize = 1
    )
    var id: Long? = null

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(
        name = "payload_schema_id",
        nullable = false,
        foreignKey = ForeignKey(name = "fk_db_queue__payload_schema_id")
    )
    var schema: DbQueuePayloadSchema = DbQueuePayloadSchema()

    @NotBlank
    @Column(name = "payload", nullable = false, columnDefinition = "text")
    var payload: String = "{}"

    constructor() : super()
    constructor(schema: DbQueuePayloadSchema, payload: String, queueMeta: QueueMeta) : super() {
        this.schema = schema
        this.payload = payload
        this.queueMeta = queueMeta
    }

    override fun getIdentifier(): Long? {
        return id
    }
}
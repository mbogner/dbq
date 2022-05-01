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

create sequence db_queue_id_seq as bigint start with 1 increment by 1;

create table db_queue
(
    id                        bigint                      not null default nextval('db_queue_id_seq')
        constraint pk_db_queue primary key,
    created_at                timestamp without time zone not null default now_utc(),
    updated_at                timestamp without time zone not null default now_utc(),
    lock_version              bigint                      not null default 0,

    payload_schema_id         bigint                      not null
        constraint fk_db_queue__payload_schema_id references db_queue_payload_schema (id),
    payload                   text                        not null,

    q_succeeded_at            timestamp without time zone,
    q_failed_at               timestamp without time zone,
    q_next_after              timestamp without time zone          default now_utc(),
    q_attempts                int                         not null default 0,
    q_max_attempts            int                         not null default 2147483647,
    q_retry_delay_strategy    varchar(32)                 not null default 'NONE',
    q_retry_delay_seconds     int                         not null default 1,
    q_retry_delay_seconds_max int                         not null default 604800, /** 1 week */
    q_status                  varchar(32)                 not null default 'CREATED',
    q_error                   text
) without oids;

create trigger trg_db_queue__check_created_at_unchanged
    before update
    on db_queue
    for each row
execute procedure trg_func__check_created_at_unchanged();

create trigger trg_db_queue__update_updated_at
    before update
    on db_queue
    for each row
execute procedure trg_func__update_updated_at();

create trigger trg_db_queue__increment_lock_version
    before update
    on db_queue
    for each row
execute procedure trg_func__increment_lock_version();
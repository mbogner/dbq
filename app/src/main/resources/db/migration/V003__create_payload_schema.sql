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

create sequence db_queue_payload_schema_id_seq as bigint start with 1 increment by 1;

create table db_queue_payload_schema
(
    id               bigint                      not null default nextval('db_queue_payload_schema_id_seq')
        constraint pk_db_queue_payload_schema primary key,
    created_at       timestamp without time zone not null default now_utc(),
    updated_at       timestamp without time zone not null default now_utc(),
    lock_version     bigint                      not null default 0,

    type             varchar(32)                 not null default 'NONE',
    schema_version   int                         not null default 0,
    json_schema      text                        not null,
    json_schema_hash varchar(96)                 not null,

    constraint uc_db_queue_payload_schema__type_schema_version unique (type, schema_version)
) without oids;

-- set json_schema_hash to sha256(json_schema)
create or replace function trg_func__create_json_schema_hash()
    returns trigger as
$$
begin
    new.json_schema_hash = sha256(new.json_schema::bytea);
    return new;
end;
$$ language plpgsql;

create trigger trg_db_queue_payload_schema__create_json_schema_hash
    before insert
    on db_queue_payload_schema
    for each row
execute procedure trg_func__create_json_schema_hash();
create trigger trg_db_queue_payload_schema__update_json_schema_hash
    before update
    on db_queue_payload_schema
    for each row
execute procedure trg_func__create_json_schema_hash();


create trigger trg_db_queue_payload_schema__check_created_at_unchanged
    before update
    on db_queue_payload_schema
    for each row
execute procedure trg_func__check_created_at_unchanged();

create trigger trg_db_queue_payload_schema__update_updated_at
    before update
    on db_queue_payload_schema
    for each row
execute procedure trg_func__update_updated_at();

create trigger trg_db_queue_payload_schema__increment_lock_version
    before update
    on db_queue_payload_schema
    for each row
execute procedure trg_func__increment_lock_version();
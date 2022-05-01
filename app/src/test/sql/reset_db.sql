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

drop table if exists locks;

drop table if exists db_queue;
drop sequence if exists db_queue_id_seq;

drop table if exists db_queue_payload_schema;
drop sequence if exists db_queue_payload_schema_id_seq;

/** shared */

delete from flyway_schema_history where 1=1;

drop function if exists trg_func__create_json_schema_hash();
drop function if exists trg_func__check_created_at_unchanged();
drop function if exists trg_func__increment_lock_version();
drop function if exists trg_func__update_updated_at();
drop function if exists now_utc();

drop extension "pgcrypto";
drop extension "uuid-ossp";
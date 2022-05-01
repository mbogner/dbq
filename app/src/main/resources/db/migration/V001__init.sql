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
create extension if not exists "pgcrypto";
create extension if not exists "uuid-ossp";

create or replace function now_utc() returns timestamp as
$$
select now() at time zone 'utc';
$$ language sql;

-- enforce that created at can't be changed
create or replace function trg_func__check_created_at_unchanged()
    returns trigger as
$$
begin
    if old.created_at != new.created_at then
        raise exception 'attempted to update created_at' using hint = 'updated_at field can not be changed';
    end if;
    return new;
end;
$$ language plpgsql;

-- set updated_at to now if not set for an update
create or replace function trg_func__update_updated_at()
    returns trigger as
$$
begin
    if new.updated_at is null or new.updated_at = old.updated_at then
        new.updated_at = now_utc();
    end if;
    if new.updated_at < old.updated_at then
        raise exception 'attempted to go back in history with updated_at' using hint = 'updated_at can only increment';
    end if;
    return new;
end;
$$ language plpgsql;

-- increment lock_version if not done
create or replace function trg_func__increment_lock_version()
    returns trigger as
$$
begin
    if new.lock_version = old.lock_version then
        new.lock_version = old.lock_version + 1;
    end if;
    return new;
end;
$$ language plpgsql;
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

delete
from db_queue
where id = -1;

delete
from db_queue_payload_schema
where id = -1;

insert into db_queue_payload_schema (id, type, schema_version, json_schema, json_schema_hash)
values (-1, 'NONE', 0, '{
    "$schema": "http://json-schema.org/draft-04/schema#",
    "title": "Product",
    "description": "A product from the catalog",
    "type": "object",
    "properties": {
        "id": {
            "description": "The unique identifier for a product",
            "type": "integer"
        },
        "name": {
            "description": "Name of the product",
            "type": "string"
        },
        "price": {
            "type": "number",
            "minimum": 0
        }
    },
    "required": ["id", "name", "price"]
}', '');


insert into db_queue (id, payload_schema_id, payload)
values (-1, -1, '{ "id": 1, "name": "test1", "price": 1.50 }');


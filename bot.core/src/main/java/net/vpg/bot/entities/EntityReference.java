/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.bot.entities;

import java.util.Map;
import java.util.function.Supplier;

public class EntityReference<T extends Entity> implements Supplier<T>, Entity {
    public final Map<String, T> map;
    public final String id;

    public EntityReference(EntityInfo<T> info, String id) {
        this.id = id;
        this.map = info.getMap();
    }

    @Override
    public T get() {
        return map.get(id);
    }

    @Override
    public String getId() {
        return id;
    }
}

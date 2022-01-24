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

import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.core.Bot;

import java.net.URL;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EntityInfo<T extends Entity> {
    public final String identifier;
    public final BiFunction<DataObject, Bot, T> entityConstructor;
    public final Map<String, T> entityMap;
    public final boolean isDBObject;

    public EntityInfo(URL fileUrl, Function<DataObject, T> entityConstructor, Map<String, T> entityMap) {
        this(fileUrl.toString(), (data, bot) -> entityConstructor.apply(data), entityMap, false);
    }

    public EntityInfo(URL fileUrl, BiFunction<DataObject, Bot, T> entityConstructor, Map<String, T> entityMap) {
        this(fileUrl.toString(), entityConstructor, entityMap, false);
    }

    public EntityInfo(String collectionId, BiFunction<DataObject, Bot, T> entityConstructor, Map<String, T> entityMap) {
        this(collectionId, entityConstructor, entityMap, true);
    }

    public EntityInfo(String identifier, BiFunction<DataObject, Bot, T> entityConstructor, Map<String, T> entityMap, boolean isDBObject) {
        this.identifier = identifier;
        this.entityConstructor = entityConstructor;
        this.entityMap = entityMap;
        this.isDBObject = isDBObject;
    }
}

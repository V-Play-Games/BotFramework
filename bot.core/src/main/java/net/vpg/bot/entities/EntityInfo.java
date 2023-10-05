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

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.Util;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EntityInfo<T extends Entity> {
    private final String identifier;
    private final BiFunction<DataObject, Bot, T> constructor;
    private final Map<String, T> map;
    private final boolean isDatabaseEntity;

    public EntityInfo(URL fileUrl, Function<DataObject, T> constructor, Map<String, T> map) {
        this(fileUrl.toString(), (data, bot) -> constructor.apply(data), map, false);
    }

    public EntityInfo(URL fileUrl, BiFunction<DataObject, Bot, T> constructor, Map<String, T> map) {
        this(fileUrl.toString(), constructor, map, false);
    }

    public EntityInfo(String collectionId, BiFunction<DataObject, Bot, T> constructor, Map<String, T> map) {
        this(collectionId, constructor, map, true);
    }

    public EntityInfo(String identifier, BiFunction<DataObject, Bot, T> constructor, Map<String, T> map, boolean isDatabaseEntity) {
        this.identifier = identifier;
        this.constructor = constructor;
        this.map = map;
        this.isDatabaseEntity = isDatabaseEntity;
    }

    public void load(Bot bot) {
        if (isDatabaseEntity) {
            if (!bot.isDatabaseEnabled()) return;
            bot.getDatabase().getCollection(identifier).find().forEach(document -> {
                T entity = constructor.apply(Util.toDataObject(document), bot);
                map.put(entity.getId(), entity);
            });
        } else {
            try (InputStream stream = new URL(identifier).openStream()) {
                DataArray.fromJson(stream)
                    .stream(DataArray::getObject)
                    .filter(data -> !data.keys().isEmpty())
                    .map(data -> constructor.apply(data, bot))
                    .forEach(entity -> map.put(entity.getId(), entity));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public BiFunction<DataObject, Bot, T> getConstructor() {
        return constructor;
    }

    public Map<String, T> getMap() {
        return map;
    }

    public boolean isDatabaseEntity() {
        return isDatabaseEntity;
    }
}

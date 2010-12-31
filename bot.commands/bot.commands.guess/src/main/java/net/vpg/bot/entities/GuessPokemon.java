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
import net.vpg.bot.core.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuessPokemon implements Entity {
    public static final Map<String, GuessPokemon> CACHE = new HashMap<>();
    public static final EntityInfo<GuessPokemon> INFO = new EntityInfo<>(GuessPokemon.class.getResource("guesses.json"), GuessPokemon::new, CACHE);
    private final String id;
    private final String name;
    private final List<String> flavorTexts;
    private final String sprite;
    private final String type;

    public GuessPokemon(DataObject data) {
        this.id = data.getString("id");
        this.name = Util.toProperCase(data.getString("name"));
        this.sprite = data.getString("sprite");
        this.flavorTexts = data.getArray("flavor_texts").stream(DataArray::getString).collect(Collectors.toList());
        String type = data.getString("type");
        int index = type.indexOf("/");
        this.type = index == -1
            ? Util.toProperCase(type)
            : Util.toProperCase(type.substring(0, index + 1)) + Util.toProperCase(type.substring(index + 1));
    }

    public static GuessPokemon get(String id) {
        return CACHE.get(id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getFlavorTexts() {
        return flavorTexts;
    }

    public String getSprite() {
        return sprite;
    }

    public String getType() {
        return type;
    }
}


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
import net.vpg.bot.database.DatabaseObject;

import java.util.HashMap;
import java.util.Map;

public class GuildSettings extends DatabaseObject {
    public static final String COLLECTION_NAME = "gSettings";
    public static final Map<String, GuildSettings> CACHE = new HashMap<>();
    public static final EntityInfo<GuildSettings> INFO = new EntityInfo<>(COLLECTION_NAME, GuildSettings::new, CACHE);
    protected String prefix;

    private GuildSettings(DataObject data, Bot bot) {
        super(data, bot);
        this.prefix = data.getString("prefix", bot.getPrefix());
    }

    private GuildSettings(String id, String prefix, Bot bot) {
        super(id, bot);
        this.prefix = prefix;
        this.data.put("prefix", prefix);
    }

    public static GuildSettings createNew(DataObject data, Bot bot) {
        GuildSettings settings = new GuildSettings(data, bot);
        settings.ensureInserted();
        return settings;
    }

    public static GuildSettings createNew(String id, Bot bot) {
        GuildSettings settings = new GuildSettings(id, bot.getPrefix(), bot);
        settings.ensureInserted();
        return settings;
    }

    public static GuildSettings createNew(String id, String prefix, Bot bot) {
        GuildSettings settings = new GuildSettings(id, prefix, bot);
        settings.ensureInserted();
        return settings;
    }

    public static GuildSettings get(String id, Bot bot) {
        return CACHE.computeIfAbsent(id, x -> createNew(id, bot));
    }

    public static GuildSettings get(String id, String prefix, Bot bot) {
        return CACHE.computeIfAbsent(id, x -> createNew(id, prefix, bot));
    }

    public String getPrefix() {
        return prefix;
    }

    public GuildSettings setPrefix(String prefix) {
        if (!this.prefix.equals(prefix)) {
            this.prefix = prefix;
            update("prefix", prefix);
        }
        return this;
    }

    @Override
    public String getCollectionName() {
        return COLLECTION_NAME;
    }
}

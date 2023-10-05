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
package net.vpg.bot.core;

import net.vpg.bot.entities.Entity;
import net.vpg.bot.entities.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityLoader.class);
    private InstanceLoader loader;
    private List<EntityInfo<?>> cache;

    public EntityLoader(InstanceLoader loader) {
        this.loader = loader;
    }

    public InstanceLoader getLoader() {
        return loader;
    }

    public void setLoader(InstanceLoader loader) {
        this.loader = loader;
    }

    public void loadAll(Bot bot) {
        loadCache();
        cache.forEach(info -> {
            info.load(bot);
            LOGGER.info("Loaded " + info.getIdentifier());
        });
    }

    private void loadCache() {
        if (cache != null) return;
        cache = loader.getAllClasses(Entity.class)
            .stream()
            .map(c -> {
                try {
                    try {
                        return c.getMethod("getInfo").invoke(null);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        try {
                            return c.getField("INFO").get(null);
                        } catch (NoSuchFieldException | IllegalAccessException ex) {
                            LOGGER.warn("Couldn't load EntityInfo for {}", c);
                            return null;
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Couldn't load EntityInfo for " + c, e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(x -> (EntityInfo<?>) x)
            .collect(Collectors.toList());
    }
}

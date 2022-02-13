package net.vpg.bot.core;

import net.dv8tion.jda.api.utils.data.DataArray;
import net.vpg.bot.entities.Entity;
import net.vpg.bot.entities.EntityInfo;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EntityLoader {
    protected static final Logger LOGGER = LoggerFactory.getLogger(EntityLoader.class);
    private InstanceLoader loader;
    private Map<Class<?>, EntityInfo<?>> cache;

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
        if (cache == null) cache = new HashMap<>();
        cache.putAll(loader.getAllClasses(Entity.class)
            .stream()
            .map(c -> {
                EntityInfo<?> info;
                try {
                    info = (EntityInfo<?>) c.getMethod("getInfo").invoke(null);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    try {
                        info = (EntityInfo<?>) c.getField("INFO").get(null);
                    } catch (NoSuchFieldException | IllegalAccessException ex) {
                        info = null;
                        LOGGER.warn("Couldn't load EntityInfo for {}", c);
                    }
                }
                return new DefaultKeyValue<>(c, info);
            })
            .filter(pair -> pair.getValue() != null)
            .collect(Collectors.toMap(DefaultKeyValue::getKey, DefaultKeyValue::getValue)));
        cache.values().forEach(info -> {
            if (info.isDatabaseEntity()) {
                loadDatabaseEntity(info, bot);
            } else {
                loadEntity(info, bot);
            }
            LOGGER.info("Loaded " + info.getIdentifier());
        });
    }

    protected <T extends Entity> void loadEntity(EntityInfo<T> info, Bot bot) {
        try (InputStream stream = new URL(info.getIdentifier()).openStream()) {
            DataArray.fromJson(stream)
                .stream(DataArray::getObject)
                .filter(data -> !data.keys().isEmpty())
                .map(data -> info.getConstructor().apply(data, bot))
                .forEach(entity -> info.getMap().put(entity.getId(), entity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected <T extends Entity> void loadDatabaseEntity(EntityInfo<T> info, Bot bot) {
        if (!bot.isDatabaseEnabled()) return;
        bot.getDatabase().getCollection(info.getIdentifier()).find().forEach(document -> {
            T entity = info.getConstructor().apply(Util.toDataObject(document), bot);
            info.getMap().put(entity.getId(), entity);
        });
    }
}

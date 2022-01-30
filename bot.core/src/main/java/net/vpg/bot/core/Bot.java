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

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.AllowedMentions;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.database.Database;
import net.vpg.bot.entities.Entity;
import net.vpg.bot.entities.EntityInfo;
import net.vpg.bot.event.handler.DefaultEventHandler;
import net.vpg.bot.event.handler.EventHandler;
import net.vpg.bot.event.handler.EventHandlerProxy;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class Bot implements Entity {
    private static final ScanResult scanResult = new ClassGraph().enableClassInfo().scan();
    private final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final String token;
    private final String prefix;
    private final long ownerId;
    private final long resourceServerId;
    private final long logCategoryId;
    private final int maxShards;
    private final AtomicLong lastCommandId = new AtomicLong(1);
    private final Map<String, ButtonHandler> buttonHandlers = new HashMap<>();
    private final Map<String, BotCommand> commands = new CaseInsensitiveMap<>();
    private final Map<Class<?>, EntityInfo<?>> entityInfoCache = new HashMap<>();
    private final Map<Integer, Long> loggers = new HashMap<>();
    private final AtomicInteger syncCount = new AtomicInteger(0);
    private final DataObject properties;
    private final ShardManager shardManager;
    private final Set<Long> managers = new HashSet<>();
    private final ClassFilter classFilter = new ClassFilter();
    private final EventHandlerProxy eventHandlerProxy = new EventHandlerProxy();
    private long syncMessageId;
    private String id;
    private Instant bootTime;
    private Database database;

    public Bot(DataObject properties) {
        this.properties = properties;
        id = properties.getString("token", "DEFAULT");
        token = properties.getString("token");
        prefix = properties.getString("prefix");
        ownerId = properties.getLong("ownerId");
        maxShards = properties.getInt("maxShards");
        resourceServerId = properties.getLong("resourceServer");
        logCategoryId = properties.getLong("logCategory");
        classFilter.enable("net.vpg.bot.*");
        eventHandlerProxy.setSubject(new DefaultEventHandler(this));
        try {
            this.shardManager = DefaultShardManagerBuilder.createDefault(token)
                .enableIntents(DIRECT_MESSAGES, GUILD_MEMBERS, GUILD_MESSAGES, GUILD_VOICE_STATES, GUILD_EMOJIS)
                .addEventListeners(eventHandlerProxy)
                .setShardsTotal(maxShards)
                .setActivity(Activity.watching("My Loading"))
                .build(false);
        } catch (LoginException e) {
            throw new InternalError(e);
        }
        AllowedMentions.setDefaultMentionRepliedUser(false);
    }

    public static ScanResult getScanResult() {
        return scanResult;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public boolean isDatabaseEnabled() {
        return database != null;
    }

    public String getProcessId(JDA jda) {
        return System.nanoTime() + "-" + jda.getShardInfo().getShardId();
    }

    public String getToken() {
        return token;
    }

    public EventHandler getEventHandler() {
        return eventHandlerProxy.getSubject();
    }

    public void setEventHandler(EventHandler eventHandler) {
        eventHandlerProxy.setSubject(eventHandler);
    }

    public String getPrefix() {
        return prefix;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public long getResourceServerId() {
        return resourceServerId;
    }

    public long getLogCategoryId() {
        return logCategoryId;
    }

    public int getMaxShards() {
        return maxShards;
    }

    public Map<String, ButtonHandler> getButtonHandlers() {
        return buttonHandlers;
    }

    public Map<String, BotCommand> getCommands() {
        return commands;
    }

    public Map<Class<?>, EntityInfo<?>> getEntityInfoCache() {
        return entityInfoCache;
    }

    public Map<Integer, Long> getLoggers() {
        return loggers;
    }

    public AtomicInteger getSyncCount() {
        return syncCount;
    }

    public Instant getBootTime() {
        return bootTime;
    }

    public void setBootTime(Instant bootTime) {
        this.bootTime = bootTime;
    }

    public DataObject getProperties() {
        return properties;
    }

    public String getProperty(String property) {
        return properties.getString(property);
    }

    public void registerCommand(String name, BotCommand command) {
        commands.put(name, command);
    }

    public void removeCommand(String name) {
        commands.remove(name);
    }

    public <T extends GenericEvent> void subscribeTo(String id, Class<T> type, Consumer<T> action) {
        eventHandlerProxy.addSubscriber(id, type, action);
    }

    public ClassFilter getClassFilter() {
        return classFilter;
    }

    public void login() throws LoginException {
        shardManager.login();
    }

    public void load() {
        bootTime = Instant.now();
        startSync();
        loadData();
        loadCommands();
        loadLoggers();
        setDefaultActivity();
    }

    public TextChannel getLogChannel(int id) {
        return shardManager.getTextChannelById(loggers.get(id));
    }

    public TextChannel getSyncChannel() {
        return getLogChannel(-1);
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public JDA getPrimaryShard() {
        return shardManager.getShardById(0);
    }

    private void loadData() {
        entityInfoCache.putAll(getScanResult()
            .getAllClasses()
            .stream()
            .filter(x -> !x.isAbstract() && x.isStandardClass() && x.implementsInterface(Entity.class.getName()))
            .map(ClassInfo::loadClass)
            .map(c -> {
                EntityInfo<?> info;
                try {
                    info = (EntityInfo<?>) c.getMethod("getInfo").invoke(null);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    try {
                        info = (EntityInfo<?>) c.getField("INFO").get(null);
                    } catch (NoSuchFieldException | IllegalAccessException ex) {
                        info = null;
                        logger.warn("Couldn't load EntityInfo for {}", c);
                    }
                }
                return new DefaultKeyValue<>(c, info);
            })
            .filter(pair -> pair.getValue() != null)
            .collect(Collectors.toMap(DefaultKeyValue::getKey, DefaultKeyValue::getValue)));
        entityInfoCache.values().forEach(info -> {
            if (info.isDatabaseEntity()) {
                loadDatabaseEntity(info);
            } else {
                loadEntity(info);
            }
            logger.info("Loaded " + info.getIdentifier());
        });
    }

    private <T extends Entity> void loadEntity(EntityInfo<T> info) {
        try (InputStream stream = new URL(info.getIdentifier()).openStream()) {
            DataArray.fromJson(stream)
                .stream(DataArray::getObject)
                .filter(data -> !data.keys().isEmpty())
                .map(data -> info.getConstructor().apply(data, this))
                .forEach(entity -> info.getMap().put(entity.getId(), entity));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private <T extends Entity> void loadDatabaseEntity(EntityInfo<T> info) {
        if (!isDatabaseEnabled()) return;
        getDatabase().getCollection(info.getIdentifier()).find().forEach(document -> {
            T entity = info.getConstructor().apply(Util.toDataObject(document), this);
            info.getMap().put(entity.getId(), entity);
        });
    }

    private void startSync() {
        getPrimaryShard().getRateLimitPool().scheduleAtFixedRate(() -> {
            syncCount.incrementAndGet();
            String message = "Sync [" + syncCount + "]";
            logger.info(message);
            if (syncMessageId == 0)
                getSyncChannel().sendMessage(message).queue(m -> syncMessageId = m.getIdLong());
            else
                getSyncChannel().editMessageById(syncMessageId, message).queue();
        }, 0, 1, TimeUnit.MINUTES);
    }

    private void loadLoggers() {
        Guild resources = shardManager.getGuildById(resourceServerId);
        if (resources == null) return;
        Category category = resources.getCategoryById(logCategoryId);
        if (category == null) return;
        shardManager.getShardCache()
            .stream()
            .map(JDA::getShardInfo)
            .map(JDA.ShardInfo::getShardId)
            .forEach(id -> {
                List<TextChannel> channels = resources.getTextChannelsByName("shard-" + id, true);
                if (channels.isEmpty()) {
                    category.createTextChannel("shard-" + id).queue(tc -> loggers.put(id, tc.getIdLong()));
                } else {
                    loggers.put(id, channels.get(0).getIdLong());
                }
            });
        List<TextChannel> channels = resources.getTextChannelsByName("sync", true);
        if (channels.isEmpty()) {
            category.createTextChannel("sync").queue(tc -> loggers.put(-1, tc.getIdLong()));
        } else {
            loggers.put(-1, channels.get(0).getIdLong());
        }
    }

    public void addManager(long id) {
        managers.add(id);
    }

    public void removeManager(long id) {
        managers.remove(id);
    }

    public Set<Long> getManagers() {
        return managers;
    }

    public boolean isManager(long id) {
        return id == ownerId || managers.contains(id);
    }

    private void loadCommands() {
        loadAllInstancesOf(BotCommand.class, command -> {
            command.register();
            logger.info("Loaded " + command + " Command");
        }, this);
        loadAllInstancesOf(ButtonHandler.class, handler -> {
            buttonHandlers.put(handler.getName(), handler);
            logger.info("Loaded " + handler.getName() + " button handler");
        });
        JDA primaryShard = getPrimaryShard();
        primaryShard.retrieveCommands().queue(commandList -> {
            Map<String, Command> commandMap = Util.group(commandList, Command::getName);
            commandMap.entrySet()
                .stream()
                .filter(e -> !commands.containsKey(e.getKey()))
                .map(Map.Entry::getValue)
                .map(Command::delete)
                .forEach(RestAction::queue);
            commands.values()
                .stream()
                .distinct()
                .map(BotCommand::toCommandData)
                .filter(data -> !Util.equals(data, commandMap.get(data.getName())))
                .map(primaryShard::upsertCommand)
                .forEach(action -> action.queue(command -> commands.get(command.getName()).finalizeCommand(command)));
        });
    }

    @SuppressWarnings("unchecked")
    public <T> void loadAllInstancesOf(Class<T> interfaceClass, Consumer<T> newInstanceProcessor, Object... parameters) {
        Class<?>[] paramTypes = Arrays.stream(parameters).map(Object::getClass).toArray(Class[]::new);
        Map<Class<?>, Throwable> errors = new HashMap<>();
        getScanResult()
            .getAllClasses()
            .stream()
            .filter(x -> !x.isAbstract() && !x.isInterface() && x.implementsInterface(interfaceClass.getName()))
            .map(ClassInfo::loadClass)
            .filter(classFilter.asPredicate())
            .forEach(c -> {
                try {
                    newInstanceProcessor.accept((T) c.getConstructor(paramTypes).newInstance(parameters));
                } catch (Throwable t) {
                    errors.put(c, t);
                }
            });
        errors.forEach((k, v) -> logger.error("Failed to load " + k.getName() + "\n", v));
    }

    private void setDefaultActivity() {
        String activity = "with " + shardManager.getGuildCache()
            .stream()
            .mapToInt(Guild::getMemberCount)
            .sum() + " people in " + shardManager.getGuildCache().size() + " servers";
        shardManager.setActivity(Activity.playing(activity));
    }

    @Override
    public String getId() {
        return id;
    }

    public Bot setId(String id) {
        this.id = id;
        return this;
    }

    @Nonnull
    @Override
    public DataObject toData() {
        return properties;
    }
}

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
package net.vpg.bot.framework;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.AllowedMentions;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.database.Database;
import net.vpg.bot.entities.Entity;
import net.vpg.bot.entities.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class Bot implements Entity {
    private static final ScanResult scanResult = new ClassGraph().enableClassInfo().scan();
    private final Logger logger = LoggerFactory.getLogger(Bot.class);
    private final String token;
    private final String prefix;
    private final long ownerId;
    private final long resourceServer;
    private final long logCategory;
    private final int maxShards;
    private final AtomicLong lastCommandId = new AtomicLong(1);
    private final Map<String, ButtonHandler> buttonHandlers = new HashMap<>();
    private final Map<String, BotCommand> commands = new HashMap<>();
    private final Map<Class<?>, EntityInfo<?>> entityInfoMap = new HashMap<>();
    private final Map<Integer, Long> loggers = new HashMap<>();
    private final AtomicInteger syncCount = new AtomicInteger(0);
    private final DataObject properties;
    private final ShardManager shardManager;
    private final List<Long> managers = new ArrayList<>();
    private long syncMessageId;
    private String id = "DEFAULT";
    private EventHandler eventHandler;
    private Instant bootTime;
    private Database database;

    public Bot(DataObject properties) {
        this.properties = properties;
        this.token = properties.getString("token");
        this.prefix = properties.getString("prefix");
        this.ownerId = properties.getLong("ownerId");
        this.maxShards = properties.getInt("maxShards");
        this.resourceServer = properties.getLong("resourceServer");
        this.logCategory = properties.getLong("logCategory");
        try {
            this.shardManager = DefaultShardManagerBuilder.createDefault(token)
                .enableIntents(DIRECT_MESSAGES, GUILD_MEMBERS, GUILD_MESSAGES, GUILD_VOICE_STATES, GUILD_EMOJIS)
                .addEventListeners(new EventHandler(this))
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

    public <T1, T2> Bot compute(T1 arg, BiFunction<T1, Bot, T2> function, BiConsumer<Bot, T2> action) {
        action.accept(this, function.apply(arg, this));
        return this;
    }

    public <T> Bot compute(Function<Bot, T> function, BiConsumer<Bot, T> action) {
        action.accept(this, function.apply(this));
        return this;
    }

    public Database getDatabase() {
        return database;
    }

    public Bot setDatabase(Database database) {
        this.database = database;
        return this;
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
        return eventHandler;
    }

    public Bot setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
        return this;
    }

    public String getPrefix() {
        return prefix;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public long getResourceServer() {
        return resourceServer;
    }

    public long getLogCategory() {
        return logCategory;
    }

    public int getMaxShards() {
        return maxShards;
    }

    public AtomicLong getLastCommandId() {
        return lastCommandId;
    }

    public Map<String, ButtonHandler> getButtonHandlers() {
        return buttonHandlers;
    }

    public Map<String, BotCommand> getCommands() {
        return commands;
    }

    public Map<Class<?>, EntityInfo<?>> getEntityInfoMap() {
        return entityInfoMap;
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

    public Bot setBootTime(Instant bootTime) {
        this.bootTime = bootTime;
        return this;
    }

    public DataObject getProperties() {
        return properties;
    }

    public String getProperty(String property) {
        return properties.getString(property);
    }

    public void registerCommand(String name, BotCommand command) {
        this.commands.put(name, command);
    }

    public void removeCommand(String name) {
        this.commands.remove(name);
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

    public TextChannel getLogChannel(JDA jda) {
        return getLogChannel(jda.getShardInfo().getShardId());
    }

    public TextChannel getLogChannel(int shardId) {
        return shardManager.getTextChannelById(loggers.get(shardId));
    }

    public TextChannel getSyncChannel() {
        return shardManager.getTextChannelById(loggers.get(-1));
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public JDA getPrimaryShard() {
        return shardManager.getShardById(0);
    }

    public void loadData() {
        entityInfoMap.putAll(getScanResult()
            .getAllClasses()
            .stream()
            .filter(x -> !x.isAbstract() && !x.isInterface() && x.implementsInterface(Entity.class.getName()))
            .map(ClassInfo::loadClass)
            .map(c -> {
                try {
                    return c.getMethod("getInfo");
                } catch (NoSuchMethodException e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toMap(Method::getDeclaringClass, m -> {
                try {
                    return (EntityInfo<?>) m.invoke(null);
                } catch (Exception e) {
                    throw new InternalError(e);
                }
            })));
        entityInfoMap.values().forEach(info -> {
            if (info.isDBObject) {
                loadDBEntity(info);
            } else {
                loadEntity(info);
            }
        });
    }

    public <T extends Entity> void loadEntity(EntityInfo<T> info) {
        try (InputStream stream = new URL(info.identifier).openStream()) {
            DataArray.fromJson(stream)
                .stream(DataArray::getObject)
                .filter(data -> !data.keys().isEmpty())
                .map(data -> info.entityConstructor.apply(data, this))
                .forEach(entity -> info.entityMap.put(entity.getId(), entity));
            logger.info("Loaded " + info.identifier);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T extends Entity> void loadDBEntity(EntityInfo<T> info) {
        if (!isDatabaseEnabled()) return;
        getDatabase().getCollection(info.identifier).find()
            .forEach(document -> {
                T entity = info.entityConstructor.apply(Util.toDataObject(document), this);
                info.entityMap.put(entity.getId(), entity);
            });
    }

    public void startSync() {
        getPrimaryShard().getRateLimitPool().scheduleWithFixedDelay(() -> {
            syncCount.incrementAndGet();
            String message = "Sync [" + syncCount + "]";
            logger.info(message);
            if (syncMessageId == 0)
                getSyncChannel().sendMessage(message).queue(m -> syncMessageId = m.getIdLong());
            else
                getSyncChannel().editMessageById(syncMessageId, message).queue();
        }, 1, 1, TimeUnit.MINUTES);
    }

    public void loadLoggers() {
        Guild resources = shardManager.getGuildById(resourceServer);
        if (resources == null) return;
        Category category = resources.getCategoryById(logCategory);
        if (category == null) return;
        shardManager.getShardCache()
            .stream()
            .forEach(shard -> {
                int id = shard.getShardInfo().getShardId();
                List<TextChannel> channels = resources.getTextChannelsByName("shard-" + id, true);
                if (channels.isEmpty()) {
                    category.createTextChannel("shard-" + id).queue(tc -> addLogger(shard, tc.getIdLong()));
                } else {
                    addLogger(shard, channels.get(0).getIdLong());
                }
            });
        List<TextChannel> channels = resources.getTextChannelsByName("sync", true);
        if (channels.isEmpty()) {
            category.createTextChannel("sync").queue(tc -> loggers.put(-1, tc.getIdLong()));
        } else {
            loggers.put(-1, channels.get(0).getIdLong());
        }
    }

    @SuppressWarnings("ConstantConditions")
    public void addLogger(JDA shard, long id) {
        loggers.put(shard.getShardInfo().getShardId(), id);
        SnowflakeCacheView<Guild> guildCache = shard.getGuildCache();
        if (!guildCache.isEmpty()) {
            TextChannel logChannel = shardManager.getTextChannelById(id);
            logChannel.sendMessage("GuildCache").queue();
            guildCache.stream()
                .map(Guild::toString)
                .map(logChannel::sendMessage)
                .forEach(RestAction::queue);
        }
    }

    public void addManager(long id) {
        managers.add(id);
    }

    public void removeManager(long id) {
        managers.remove(id);
    }

    public List<Long> getManagers() {
        return managers;
    }

    public boolean isManager(long id) {
        return id == ownerId || managers.contains(id);
    }

    public void loadCommands() {
        loadAllInstancesOf(BotCommand.class, command -> {
            this.registerCommand(command.getName(), command);
            for (String alias : command.getAliases()) {
                this.registerCommand(alias, command);
            }
            logger.info("Loaded " + command + " Command");
        }, this);
        loadAllInstancesOf(ButtonHandler.class, handler -> {
            buttonHandlers.put(handler.getName(), handler);
            logger.info("Loaded " + handler.getName() + " button handler");
        });
        getPrimaryShard().retrieveCommands().queue(commandList -> {
            Map<String, Command> commandMap = commandList.stream().collect(Util.groupingBy(Command::getName));
            getPrimaryShard().updateCommands()
                .addCommands(commands.values()
                    .stream()
                    .map(BotCommand::toCommandData)
                    .filter(data -> Util.equals(data, commandMap.get(data.getName())))
                    .collect(Collectors.toSet()))
                .queue(c -> c.forEach(command -> commands.get(command.getName()).finalizeCommand(command)));
        });
    }

    @SuppressWarnings("unchecked")
    public <T> void loadAllInstancesOf(Class<T> _interface, Consumer<T> newInstanceProcessor, Object... parameters) {
        Class<?>[] paramTypes = Arrays.stream(parameters).map(Object::getClass).toArray(Class[]::new);
        Map<Class<?>, Exception> errors = new HashMap<>();
        getScanResult()
            .getAllClasses()
            .stream()
            .filter(x -> !x.isAbstract() && !x.isInterface() && x.implementsInterface(_interface.getName()))
            .collect(Collectors.toSet())
            .forEach(x -> {
                try {
                    T newObject = (T) x.loadClass().getConstructor(paramTypes).newInstance(parameters);
                    newInstanceProcessor.accept(newObject);
                } catch (Exception e) {
                    errors.put(x.loadClass(), e);
                }
            });
        errors.forEach((k, v) -> {
            logger.info("Failed to load " + k.getSimpleName() + "\n");
            v.printStackTrace();
        });
    }

    public void setDefaultActivity() {
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

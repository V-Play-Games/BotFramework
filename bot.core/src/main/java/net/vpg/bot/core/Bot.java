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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.AllowedMentions;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.database.Database;
import net.vpg.bot.entities.Entity;
import net.vpg.bot.event.handler.DefaultEventHandler;
import net.vpg.bot.event.handler.EventHandler;
import net.vpg.bot.event.handler.EventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Bot implements Entity {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
    protected final String id;
    protected final String token;
    protected final String prefix;
    protected final long resourceServerId;
    protected final long logCategoryId;
    protected final int shardsTotal;
    protected final Map<String, ButtonHandler> buttonHandlers;
    protected final Map<String, BotCommand> commands;
    protected final Map<Integer, Long> loggers = new HashMap<>();
    protected final AtomicInteger syncCount = new AtomicInteger(0);
    protected final DataObject properties;
    protected final Set<Long> managers;
    protected final ClassFilter filter;
    protected final EventProcessor processor;
    protected final EntityLoader entityLoader;
    protected final Database database;
    protected long ownerId;
    protected Instant bootTime;

    protected Bot(BotBuilder builder) throws LoginException {
        this.properties = builder.toData();
        this.id = builder.id;
        this.token = builder.token;
        this.prefix = builder.prefix;
        this.shardsTotal = builder.shardsTotal;
        this.buttonHandlers = builder.buttonHandlers;
        this.commands = builder.commands;
        this.database = builder.database;
        this.processor = builder.processor;
        this.resourceServerId = properties.getLong("resourceServer", 0L);
        this.logCategoryId = properties.getLong("logCategory", 0L);
        this.filter = builder.filter == null ? ClassFilter.getDefault() : builder.filter;
        this.entityLoader = new EntityLoader(new InstanceLoader(filter));
        this.managers = new HashSet<>(builder.managers);
        processor.setSubject(Optional.ofNullable(builder.handlerProvider).map(p -> p.apply(this)).orElseGet(() -> new DefaultEventHandler(this)));
        AtomicInteger shardsInit = new AtomicInteger();
        processor.addListener(ReadyEvent.class, e -> {
            if (shardsInit.incrementAndGet() == e.getJDA().getShardInfo().getShardTotal())
                load();
        });
        AllowedMentions.setDefaultMentionRepliedUser(false);
    }

    public Database getDatabase() {
        return database;
    }

    public boolean isDatabaseEnabled() {
        return database != null;
    }

    public String getToken() {
        return token;
    }

    public EventHandler getEventHandler() {
        return processor.getSubject();
    }

    public void setEventHandler(EventHandler eventHandler) {
        processor.setSubject(eventHandler);
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

    public abstract Guild getResourceServer();

    public long getLogCategoryId() {
        return logCategoryId;
    }

    public int getShardsTotal() {
        return shardsTotal;
    }

    public Map<String, ButtonHandler> getButtonHandlers() {
        return buttonHandlers;
    }

    public Map<String, BotCommand> getCommands() {
        return commands;
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

    public DataObject getProperties() {
        return properties;
    }

    public String getProperty(String property) {
        return properties.getString(property);
    }

    public EntityLoader getEntityLoader() {
        return entityLoader;
    }

    public void registerCommand(String name, BotCommand command) {
        commands.put(name, command);
    }

    public void removeCommand(String name) {
        commands.remove(name);
    }

    public EventProcessor getEventProcessor() {
        return processor;
    }

    public ClassFilter getFilter() {
        return filter;
    }

    protected void load() {
        bootTime = Instant.now();
        startSync();
        loadData();
        loadCommands();
        loadLoggers();
        setDefaultActivity();
        setOwnerId();
    }

    public TextChannel getLogChannel(int id) {
        Guild resourceServer = getResourceServer();
        return resourceServer == null ? null : resourceServer.getTextChannelById(loggers.get(id));
    }

    public TextChannel getSyncChannel() {
        return getLogChannel(-1);
    }

    public abstract ShardManager getShardManager();

    public abstract JDA getPrimaryShard();

    protected void loadData() {
        entityLoader.loadAll(this);
    }

    protected void startSync() {
        AtomicLong syncMessageId = new AtomicLong();
        getPrimaryShard().getRateLimitPool().scheduleAtFixedRate(() -> {
            syncCount.incrementAndGet();
            String message = "Sync [" + syncCount + "]";
            LOGGER.info(message);
            if (syncMessageId.get() == 0)
                getSyncChannel().sendMessage(message).queue(m -> syncMessageId.set(m.getIdLong()));
            else
                getSyncChannel().editMessageById(syncMessageId.get(), message).queue();
        }, 0, 1, TimeUnit.MINUTES);
    }

    protected abstract void loadLoggers();

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

    protected void loadCommands() {
        entityLoader.getLoader().loadAllInstances(BotCommand.class, command -> {
            command.register();
            LOGGER.info("Loaded " + command + " Command");
        }, (c, t) -> LOGGER.error("Unable to load command from " + c, t), this);
        entityLoader.getLoader().loadAllInstances(ButtonHandler.class, handler -> {
            buttonHandlers.put(handler.getName(), handler);
            LOGGER.info("Loaded " + handler.getName() + " button handler");
        }, (c, t) -> LOGGER.error("Unable to load button handler from " + c, t));
        getPrimaryShard().updateCommands().addCommands(commands.values()).queue();
    }

    protected abstract void setDefaultActivity();

    protected void setOwnerId() {
        getPrimaryShard().retrieveApplicationInfo().queue(a -> ownerId = a.getOwner().getIdLong());
    }

    @Override
    public String getId() {
        return id;
    }

    @Nonnull
    @Override
    public DataObject toData() {
        return properties;
    }
}

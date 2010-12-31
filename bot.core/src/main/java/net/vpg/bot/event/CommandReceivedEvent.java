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

package net.vpg.bot.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.action.CommandReplyAction;
import net.vpg.bot.action.Sender;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class CommandReceivedEvent implements Sender {
    private final String prefix;
    private final Bot bot;
    private final String processId;
    private final MessageChannel channel;
    private final JDA jda;
    private final Guild guild;
    private final User user;
    private final Member member;
    private final OffsetDateTime timeCreated;
    private final BotCommand command;
    private final Member selfMember;
    private final Supplier<CommandReplyAction> actionSupplier;
    private boolean replySent;
    private boolean loggingAllowed;
    private CommandReplyAction action;
    private Throwable trouble;

    public CommandReceivedEvent(@Nonnull JDA jda,
                                @Nonnull MessageChannel channel,
                                @Nullable Guild guild,
                                @Nonnull User user,
                                @Nullable Member member,
                                @Nonnull BotCommand command,
                                @Nonnull OffsetDateTime timeCreated,
                                @Nonnull String prefix,
                                @Nonnull Supplier<CommandReplyAction> actionSupplier) {
        this.channel = channel;
        this.jda = jda;
        this.guild = guild;
        this.user = user;
        this.member = member;
        this.command = command;
        this.timeCreated = timeCreated;
        this.prefix = prefix;
        this.actionSupplier = actionSupplier;
        this.bot = command.getBot();
        this.processId = Util.getProcessId(jda);
        this.selfMember = guild != null ? guild.getMember(jda.getSelfUser()) : null;
    }

    public GuildMessageChannel getGuildChannel() {
        if (channel instanceof GuildMessageChannel)
            return (GuildMessageChannel) channel;
        throw conversionError("GuildMessageChannel");
    }

    public MessageChannel getMessageChannel() {
        return channel;
    }

    public TextChannel getTextChannel() {
        if (channel instanceof TextChannel)
            return (TextChannel) channel;
        throw conversionError("TextChannel");
    }

    public NewsChannel getNewsChannel() {
        if (channel instanceof NewsChannel)
            return (NewsChannel) channel;
        throw conversionError("NewsChannel");
    }

    public ThreadChannel getThreadChannel() {
        if (channel instanceof ThreadChannel)
            return (ThreadChannel) channel;
        throw conversionError("ThreadChannel");
    }

    public PrivateChannel getPrivateChannel() {
        if (channel instanceof PrivateChannel)
            return (PrivateChannel) channel;
        throw conversionError("PrivateChannel");
    }

    private IllegalStateException conversionError(String type) {
        return new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to " + type);
    }

    public Bot getBot() {
        return bot;
    }

    public String getProcessId() {
        return processId;
    }

    public OffsetDateTime getTimeCreated() {
        return timeCreated;
    }

    public BotCommand getCommand() {
        return command;
    }

    public boolean isLoggingAllowed() {
        return loggingAllowed;
    }

    public void setForceNotLog() {
        loggingAllowed = true;
    }

    public boolean isReplySent() {
        return replySent;
    }

    public Throwable getTrouble() {
        return trouble;
    }

    public void setTrouble(Throwable trouble) {
        this.trouble = trouble;
    }

    public String getPrefix() {
        return prefix;
    }

    public Member getSelfMember() {
        return selfMember;
    }

    public JDA getJDA() {
        return jda;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public boolean isFromType(ChannelType type) {
        return getChannelType() == type;
    }

    public boolean isFromGuild() {
        return getChannelType().isGuild();
    }

    public ChannelType getChannelType() {
        return channel.getType();
    }

    public Guild getGuild() {
        return guild;
    }

    public User getUser() {
        return user;
    }

    public Member getMember() {
        return member;
    }

    @Nonnull
    @Override
    public CommandReplyAction deferSend() {
        return action == null ? action = actionSupplier.get().setTask(this::log) : action;
    }

    protected abstract String getInput();

    public void log(CommandReplyAction action) {
        if (replySent) return;
        replySent = true;
        if (loggingAllowed) return;
        TextChannel logChannel = bot.getLogChannel(getJDA().getShardInfo().getShardId());
        if (logChannel == null) return;
        String in = getInput();
        String out = action.getContent();
        String error = trouble == null
            ? "None"
            : String.format("%s: %s\n\t at %s", trouble.getClass(), trouble.getMessage(), trouble.getStackTrace()[0]);
        DataObject log = DataObject.empty()
            .put("id", processId)
            .put("time", getTimeCreated().toString())
            .put("input", in)
            .put("output", out)
            .put("user", getUser().toString())
            .put("channel", getChannel().toString())
            .put("error", error)
            .put("guild", Objects.toString(getGuild(), null));
        logChannel.sendMessageEmbeds(new EmbedBuilder()
            .setTitle("Process id " + processId)
            .setDescription(String.format("Error: %s\nUsed in %s by %s", error, getChannel(), getUser()))
            .addField("Input", in.length() > 1024 ? in.substring(0, 1021) + "..." : in, false)
            .addField("Output", out.length() > 1024 ? out.substring(0, 1021) + "..." : out, false)
            .build())
            .addFile(Util.makeFileOf(log, "log-file-" + processId + ".json"))
            .queue();
        action.setContent("");
    }
}

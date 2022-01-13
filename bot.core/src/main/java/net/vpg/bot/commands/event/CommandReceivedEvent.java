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
package net.vpg.bot.commands.event;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.commands.action.CommandReplyAction;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.Sender;
import net.vpg.bot.framework.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;
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
        this.processId = bot.getProcessId(jda);
        this.selfMember = guild != null ? guild.getMember(jda.getSelfUser()) : null;
    }

    public static void run(MessageReceivedEvent e, List<String> args, BotCommand command, String prefix) {
        command.run(new TextCommandReceivedEvent(e, args, command, prefix));
    }

    public static void run(SlashCommandEvent e, BotCommand command) {
        command.run(new SlashCommandReceivedEvent(e, command));
    }

    public GuildChannel getGuildChannel() {
        if (channel instanceof GuildChannel)
            return (GuildChannel) channel;
        throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to GuildChannel");
    }

    public MessageChannel getMessageChannel() {
        return channel;
    }

    public TextChannel getTextChannel() {
        if (channel instanceof TextChannel)
            return (TextChannel) channel;
        throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to TextChannel");
    }

    public NewsChannel getNewsChannel() {
        if (channel instanceof NewsChannel)
            return (NewsChannel) channel;
        throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to NewsChannel");
    }

    public VoiceChannel getVoiceChannel() {
        if (channel instanceof VoiceChannel)
            return (VoiceChannel) channel;
        throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to VoiceChannel");
    }

    public PrivateChannel getPrivateChannel() {
        if (channel instanceof PrivateChannel)
            return (PrivateChannel) channel;
        throw new IllegalStateException("Cannot convert channel of type " + getChannelType() + " to PrivateChannel");
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

    @Override
    public CommandReplyAction deferSend() {
        return action == null ? action = actionSupplier.get().setTask(this::log) : action;
    }

    public void setTrouble(Throwable trouble) {
        this.trouble = trouble;
        trouble.printStackTrace();
    }

    protected abstract String getInput();

    public void log(CommandReplyAction action) {
        if (replySent) return;
        replySent = true;
        if (loggingAllowed) return;
        String in = getInput();
        String out = action.getContent();
        DataObject log = DataObject.empty()
            .put("id", processId)
            .put("time", getTimeCreated().toString())
            .put("input", in)
            .put("output", out)
            .put("user", getUser().toString())
            .put("channel", getChannel().toString())
            .put("trouble", trouble.toString())
            .put("guild", getGuild().toString());
        String error = trouble == null
            ? "None"
            : String.format("%s: %s\n\t at %s", trouble.getClass(), trouble.getMessage(), trouble.getStackTrace()[0]);
        bot.getLogChannel(getJDA()).sendMessageEmbeds(new EmbedBuilder()
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

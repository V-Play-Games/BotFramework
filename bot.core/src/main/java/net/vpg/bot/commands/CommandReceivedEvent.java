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
package net.vpg.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.Sender;
import net.vpg.bot.framework.Util;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class CommandReceivedEvent implements Sender {
    private final String prefix;
    private final long messageId;
    private final Bot bot;
    private final String processId;
    private final MessageChannel channel;
    private final JDA jda;
    private final Guild guild;
    private final User user;
    private final Member member;
    private final OffsetDateTime timeCreated;
    private final String content;
    private final BotCommand command;
    private final Member selfMember;
    private Message message;
    private SlashCommandEvent slash;
    private boolean isSlashCommand;
    private boolean forceNotLog;
    private Supplier<CommandReplyAction> actionSupplier;
    private Throwable trouble;
    private List<String> args;

    public CommandReceivedEvent(MessageReceivedEvent e, String[] args, BotCommand command, String prefix) {
        this(e.getJDA(),
            e.getMessageIdLong(),
            e.getChannel(),
            e.getGuild(),
            e.getAuthor(),
            e.getMember(),
            e.getMessage().getContentRaw(),
            command,
            e.getMessage().getTimeCreated(),
            prefix,
            false);
        this.args = Arrays.asList(args);
        this.message = e.getMessage();
        actionSupplier = () -> new CommandReplyAction(message, this::log);
    }

    @SuppressWarnings("ConstantConditions")
    public CommandReceivedEvent(SlashCommandEvent e, BotCommand command) {
        this(e.getJDA(),
            e.getIdLong(),
            e.getChannel(),
            e.getGuild(),
            e.getUser(),
            e.getMember(),
            e.getCommandString(),
            command,
            e.getTimeCreated(),
            "/",
            true);
        slash = e;
        actionSupplier = () -> new CommandReplyAction(e, this::log);
    }

    public CommandReceivedEvent(JDA jda,
                                long messageId,
                                MessageChannel channel,
                                Guild guild,
                                User user,
                                Member member,
                                String content,
                                BotCommand command,
                                OffsetDateTime timeCreated,
                                String prefix,
                                boolean isSlashCommand) {
        this.messageId = messageId;
        this.channel = channel;
        this.jda = jda;
        this.guild = guild;
        this.user = user;
        this.member = member;
        this.content = content;
        this.command = command;
        this.timeCreated = timeCreated;
        this.prefix = prefix;
        this.isSlashCommand = isSlashCommand;
        this.bot = command.getBot();
        this.processId = bot.getProcessId(jda);
        this.selfMember = guild.getMember(jda.getSelfUser());
    }

    public static void run(MessageReceivedEvent e, String[] args, BotCommand command, String prefix) {
        command.run(new CommandReceivedEvent(e, args, command, prefix));
    }

    public static void run(SlashCommandEvent e, BotCommand command) {
        command.run(new CommandReceivedEvent(e, command));
    }

    public String getCommandName() {
        return slash.getName();
    }

    public String getSubcommandName() {
        return slash.getSubcommandName();
    }

    public String getSubcommandGroup() {
        return slash.getSubcommandGroup();
    }

    public long getCommandIdLong() {
        return slash.getCommandIdLong();
    }

    public List<OptionMapping> getOptions() {
        return slash.getOptions();
    }

    public String getCommandString() {
        return slash.getCommandString();
    }

    public Interaction getInteraction() {
        return slash.getInteraction();
    }

    public String getToken() {
        return slash.getToken();
    }

    public int getTypeRaw() {
        return slash.getTypeRaw();
    }

    public InteractionHook getHook() {
        return slash.getHook();
    }

    public boolean isAcknowledged() {
        return slash.isAcknowledged();
    }

    public InteractionType getType() {
        return slash.getType();
    }

    public GuildChannel getGuildChannel() {
        return slash.getGuildChannel();
    }

    public MessageChannel getMessageChannel() {
        return slash.getMessageChannel();
    }

    public TextChannel getTextChannel() {
        return slash.getTextChannel();
    }

    public VoiceChannel getVoiceChannel() {
        return slash.getVoiceChannel();
    }

    public PrivateChannel getPrivateChannel() {
        return slash.getPrivateChannel();
    }

    public String getCommandPath() {
        return slash.getCommandPath();
    }

    public String getCommandId() {
        return slash.getCommandId();
    }

    public List<OptionMapping> getOptionsByName(String name) {
        return slash.getOptionsByName(name);
    }

    public List<OptionMapping> getOptionsByType(OptionType type) {
        return slash.getOptionsByType(type);
    }

    public OptionMapping getOption(String name) {
        return slash.getOption(name);
    }

    public <T> T getOption(String name, Function<OptionMapping, T> converter, T def) {
        return optOption(name).map(converter).orElse(def);
    }

    public String getString(String name) {
        return getString(name, null);
    }

    public String getString(String name, String def) {
        return getOption(name, OptionMapping::getAsString, def);
    }

    public User getUser(String name) {
        return getUser(name, null);
    }

    public User getUser(String name, User def) {
        return getOption(name, OptionMapping::getAsUser, def);
    }

    public long getLong(String name) {
        return getLong(name, 0);
    }

    public long getLong(String name, long def) {
        return getOption(name, OptionMapping::getAsLong, def);
    }

    public Optional<OptionMapping> optOption(String name) {
        return getOptions().stream().filter(opt -> opt.getName().equals(name)).findFirst();
    }

    public long getMessageId() {
        return messageId;
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

    public String getContent() {
        return content;
    }

    public BotCommand getCommand() {
        return command;
    }

    public SlashCommandEvent getSlash() {
        return slash;
    }

    public boolean isSlashCommand() {
        return isSlashCommand;
    }

    public boolean isForceNotLog() {
        return forceNotLog;
    }

    public Throwable getTrouble() {
        return trouble;
    }

    public String getPrefix() {
        return prefix;
    }

    public Message getMessage() {
        return message;
    }

    public Member getSelfMember() {
        return selfMember;
    }

    public List<String> getArgs() {
        return args;
    }

    public JDA getJDA() {
        return jda;
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public long getMessageIdLong() {
        return messageId;
    }

    public boolean isFromType(ChannelType type) {
        return channel.getType() == type;
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

    public List<String> getArgsFrom(int index) {
        return args.subList(index, args.size());
    }

    public String getArg(int index) {
        return args.get(index);
    }

    public CommandReplyAction deferSend() {
        return actionSupplier.get();
    }

    public void reportTrouble(Throwable t) {
        trouble = t;
        t.printStackTrace();
    }

    public void forceNotLog() {
        forceNotLog = true;
    }

    public void log(CommandReplyAction action) {
        String output = action.getContent();
        DataObject log = DataObject.empty();
        log.put("id", processId)
            .put("time", timeCreated.toEpochSecond())
            .put("input", isSlashCommand ? slash.getCommandString() : content)
            .put("output", output)
            .put("args", args)
            .put("command", command.toString())
            .put("userId", user.getIdLong())
            .put("channelId", channel.getIdLong())
            .put("channelName", channel.getName())
            .put("messageId", messageId)
            .put("trouble", trouble);
        if (isFromGuild()) {
            log.put("guild", guild.getIdLong());
            log.put("guildName", guild.getName());
        }
        if (!forceNotLog) {
            String error = trouble == null ? "None" : String.format("%s: %s\n\t at %s", trouble.getClass(), trouble.getMessage(), trouble.getStackTrace()[0]);
            bot.getLogChannel(getJDA()).sendMessageEmbeds(new EmbedBuilder()
                .setTitle("Process id " + processId)
                .setDescription(String.format("Error: %s\nUsed in %s by %s", error, !isFromGuild() ? "the DM" : "#" + channel.getName(), user.toString()))
                .addField("Input", this.content.length() > 1024 ? this.content.substring(0, 1021) + "..." : this.content, false)
                .addField("Output", output.length() > 1024 ? output.substring(0, 1021) + "..." : output, false)
                .build())
                .addFile(Util.makeFileOf(log, "log-file-" + processId + ".json"))
                .queue();
        }
        //noinspection ResultOfMethodCallIgnored
        action.content("");
    }
}

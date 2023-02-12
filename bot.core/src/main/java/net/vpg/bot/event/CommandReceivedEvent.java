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
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.Util;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CommandReceivedEvent extends SlashCommandInteractionEvent {
    private final Bot bot;
    private final String processId;
    private final BotCommand command;
    private final Member selfMember;
    private boolean loggingAllowed = true;
    private Throwable trouble;

    public CommandReceivedEvent(SlashCommandInteractionEvent e, BotCommand command) {
        super(e.getJDA(), e.getResponseNumber(), e.getInteraction());
        JDA jda = e.getJDA();
        Guild guild = e.getGuild();
        this.command = command;
        this.bot = command.getBot();
        this.processId = Util.getProcessId(jda);
        this.selfMember = guild != null ? guild.getMember(jda.getSelfUser()) : null;
    }

    public Bot getBot() {
        return bot;
    }

    public String getProcessId() {
        return processId;
    }

    public BotCommand getCommand() {
        return command;
    }

    public boolean isLoggingAllowed() {
        return loggingAllowed;
    }

    public void setLoggingAllowed() {
        loggingAllowed = false;
    }

    public Throwable getTrouble() {
        return trouble;
    }

    public void setTrouble(Throwable trouble) {
        this.trouble = trouble;
    }

    public Member getSelfMember() {
        return selfMember;
    }

    public <T> T getOption(String name, Function<OptionMapping, T> converter, T def) {
        return optOption(name).map(converter).orElse(def);
    }

    public Optional<OptionMapping> optOption(String name) {
        return getOptions().stream().filter(opt -> opt.getName().equals(name)).findFirst();
    }

    public Message.Attachment getAttachment(String name) {
        return getAttachment(name, null);
    }

    public Message.Attachment getAttachment(String name, Message.Attachment def) {
        return getOption(name, OptionMapping::getAsAttachment, def);
    }

    public String getString(String name) {
        return getString(name, null);
    }

    public String getString(String name, String def) {
        return getOption(name, OptionMapping::getAsString, def);
    }

    public boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    public boolean getBoolean(String name, boolean def) {
        return getOption(name, OptionMapping::getAsBoolean, def);
    }

    public long getLong(String name) {
        return getLong(name, 0L);
    }

    public long getLong(String name, long def) {
        return getOption(name, OptionMapping::getAsLong, def);
    }

    public int getInt(String name) {
        return getInt(name, 0);
    }

    public int getInt(String name, int def) {
        return getOption(name, OptionMapping::getAsInt, def);
    }

    public double getDouble(String name) {
        return getDouble(name, 0d);
    }

    public double getDouble(String name, double def) {
        return getOption(name, OptionMapping::getAsDouble, def);
    }

    public IMentionable getMentionable(String name) {
        return getMentionable(name, null);
    }

    public IMentionable getMentionable(String name, IMentionable def) {
        return getOption(name, OptionMapping::getAsMentionable, def);
    }

    public Member getMember(String name) {
        return getMember(name, null);
    }

    public Member getMember(String name, Member def) {
        return getOption(name, OptionMapping::getAsMember, def);
    }

    public User getUser(String name) {
        return getUser(name, null);
    }

    public User getUser(String name, User def) {
        return getOption(name, OptionMapping::getAsUser, def);
    }

    public Role getRole(String name) {
        return getRole(name, null);
    }

    public Role getRole(String name, Role def) {
        return getOption(name, OptionMapping::getAsRole, def);
    }

    public GuildChannelUnion getChannel(String name) {
        return getChannel(name, null);
    }

    public GuildChannelUnion getChannel(String name, GuildChannelUnion def) {
        return getOption(name, OptionMapping::getAsChannel, def);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Nonnull
    @Override
    public ReplyCallbackAction deferReply() {
        ReplyCallbackAction action = super.deferReply();
        action.onSuccess(hook -> log(action));
        return action;
    }

    public String getInput() {
        return getCommandString();
    }

    private void log(ReplyCallbackAction action) {
        if (!loggingAllowed) return;
        MessageChannel logChannel = bot.getLogChannel(getJDA().getShardInfo().getShardId());
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
            .addFiles(FileUpload.fromData(log.toJson(), "log-file-" + processId + ".json"))
            .queue();
        action.setContent("");
    }
}

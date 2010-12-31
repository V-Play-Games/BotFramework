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

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.InteractionType;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.action.CommandReplyAction;
import net.vpg.bot.commands.BotCommand;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SlashCommandReceivedEvent extends CommandReceivedEvent {
    private final SlashCommandInteractionEvent slash;

    public SlashCommandReceivedEvent(SlashCommandInteractionEvent e, BotCommand command) {
        super(e.getJDA(),
            e.getChannel(),
            e.getGuild(),
            e.getUser(),
            e.getMember(),
            command,
            e.getTimeCreated(),
            "/",
            () -> CommandReplyAction.reply(e));
        this.slash = e;
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

    public SlashCommandInteractionEvent getSlash() {
        return slash;
    }

    @Override
    protected String getInput() {
        return getCommandString();
    }
}

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

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;
import net.vpg.bot.ratelimit.Ratelimit;

import java.util.List;
import java.util.Map;

public interface BotCommand extends SlashCommandData {
    void register();

    Bot getBot();

    List<String> getAliases();

    int getMaxArgs();

    int getMinArgs();

    void run(CommandReceivedEvent e);

    void onTextCommandRun(TextCommandReceivedEvent e) throws Exception;

    void onSlashCommandRun(SlashCommandReceivedEvent e) throws Exception;

    void finalizeCommand(Command c);

    default void onInsufficientArgs(CommandReceivedEvent e) {
        e.send("Invalid Amount of Inputs!").queue();
    }

    default boolean runChecks(CommandReceivedEvent e) {
        return true;
    }

    Map<Long, Ratelimit> getRateLimited();

    SlashCommandData toCommandData();

    List<CommandPrivilege> getDefaultPrivileges();
}

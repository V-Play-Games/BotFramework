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
package net.vpg.bot.commands.manager;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.commands.ManagerCommand;
import net.vpg.bot.framework.Bot;

public abstract class RetrieveCommand extends BotCommandImpl implements ManagerCommand {
    public RetrieveCommand(Bot bot) {
        super(bot, "retrieve", "Request data from the bot");
        addOption(OptionType.STRING, "key", "Key of the information required", true);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, e.getArg(1));
    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        execute(e, e.getString("key"));
    }

    public abstract void execute(CommandReceivedEvent e, String arg);
}

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

import net.vpg.bot.framework.Bot;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.commands.ManagerCommand;

public abstract class WipeCommand extends BotCommandImpl implements ManagerCommand {
    public WipeCommand(Bot bot) {
        super(bot, "wipe", "Wipes data from the bot");
        setMinArgs(1);
    }

    @Override
    public abstract void onCommandRun(CommandReceivedEvent e);

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        e.send("Slash Command not supported for this function").queue();
    }
}

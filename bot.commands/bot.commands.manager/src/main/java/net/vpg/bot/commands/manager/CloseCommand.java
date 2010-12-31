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

import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.commands.NoArgsCommand;
import net.vpg.bot.framework.Bot;

public class CloseCommand extends BotCommandImpl implements NoArgsCommand, ManagerCommand {
    public CloseCommand(Bot bot) {
        super(bot, "close", "Closes the bot");
    }

    public void execute(CommandReceivedEvent e) {
        bot.getEventHandler().setClosed(true);
        e.send("Successfully Closed Event Manger!").queue();
    }
}

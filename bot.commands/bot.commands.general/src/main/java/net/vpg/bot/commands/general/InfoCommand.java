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
package net.vpg.bot.commands.general;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.commands.NoArgsCommand;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.Util;

public abstract class InfoCommand extends BotCommandImpl implements NoArgsCommand {
    public InfoCommand(Bot bot) {
        super(bot, "info", "info about the bot");
    }

    public InfoCommand(Bot bot, String description) {
        super(bot, "info", description);
    }

    public InfoCommand(Bot bot, String description, String... aliases) {
        super(bot, "info", description, aliases);
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        e.send("Prefix: " + Util.getPrefix(e)).setEmbeds(getEmbed(e)).queue();
    }

    protected abstract MessageEmbed getEmbed(CommandReceivedEvent e);
}

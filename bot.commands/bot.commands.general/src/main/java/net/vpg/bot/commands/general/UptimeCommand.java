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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.CommandReceivedEvent;

import java.time.Instant;

public class UptimeCommand extends BotCommandImpl {
    public UptimeCommand(Bot bot) {
        super(bot, "uptime", "Gives the uptime of the bot i.e. the amount of time the bot has been online since last startup");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        Instant bootTime = bot.getBootTime();
        e.replyEmbeds(new EmbedBuilder()
            .appendDescription("Online since: ")
            .appendDescription(TimeFormat.RELATIVE.atInstant(bootTime).toString())
            .setFooter("Last boot ")
            .setTimestamp(bootTime)
            .setColor(0x1abc9c).build()).queue();
    }
}

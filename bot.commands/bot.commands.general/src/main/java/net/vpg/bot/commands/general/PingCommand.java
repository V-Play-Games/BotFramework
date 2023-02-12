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

import net.dv8tion.jda.api.JDA;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.CommandReceivedEvent;

public class PingCommand extends BotCommandImpl {
    public static final String PING_FORMAT = "Pong!\n**Shard**: %d\n**Response Time**: %d ms\n**Heartbeat**: %d ms";

    public PingCommand(Bot bot) {
        super(bot, "ping", "gets the current ping (response time) of the command");
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        JDA jda = e.getJDA();
        jda.getRestPing()
            .flatMap(ping -> e.reply(String.format(PING_FORMAT, jda.getShardInfo().getShardId(), ping, jda.getGatewayPing())))
            .queue();
    }
}

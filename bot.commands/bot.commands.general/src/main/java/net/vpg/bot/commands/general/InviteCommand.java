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
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.commands.NoArgsCommand;
import net.vpg.bot.framework.Bot;

public abstract class InviteCommand extends BotCommandImpl implements NoArgsCommand {
    public InviteCommand(Bot bot) {
        super(bot, "invite", "Sends a link to add the bot in the server and other links");
    }

    public InviteCommand(Bot bot, String description) {
        super(bot, "invite", description);
    }

    public InviteCommand(Bot bot, String description, String... aliases) {
        super(bot, "invite", description, aliases);
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        e.sendEmbeds(getEmbed(e)).queue();
    }

    protected MessageEmbed getEmbed(CommandReceivedEvent e) {
        return new EmbedBuilder()
            .setDescription("[Add the bot to your server](" + e.getJDA().getInviteUrl() + "+applications.commands)\n[Join the bot's support server](" + bot.getProperty("support_server_invite") + ")")
            .build();
    }
}

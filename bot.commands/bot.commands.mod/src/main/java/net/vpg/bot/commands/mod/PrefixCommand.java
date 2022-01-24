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
package net.vpg.bot.commands.mod;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.Util;
import net.vpg.bot.entities.GuildSettings;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;

public class PrefixCommand extends BotCommandImpl implements ModCommand {
    public PrefixCommand(Bot bot) {
        super(bot, "prefix", "Set the custom prefix of the server");
        addOption(OptionType.STRING, "prefix", "The custom prefix to use", true);
        setMinArgs(1);
        setMaxArgs(1);
    }

    @Override
    public void register() {
        if (bot.isDatabaseEnabled()) {
            super.register();
        }
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public void onTextCommandRun(TextCommandReceivedEvent e) {
        execute(e, e.getArg(0));
    }

    @Override
    public void onSlashCommandRun(SlashCommandReceivedEvent e) {
        execute(e, e.getString("prefix"));
    }

    public void execute(CommandReceivedEvent e, String input) {
        if (input.isBlank()) {
            e.send("Provide a valid prefix!").setEphemeral(true).queue();
            return;
        }
        String prefix = Util.SPACE_WITH_LINE.split(input)[0];
        if (prefix.isBlank() || Util.containsAny(prefix, "`", "@everyone", "@here")) {
            e.send("Provide a valid prefix! `" + prefix.replaceAll("`", "\\`") + "` is not allowed.").setEphemeral(true).queue();
            return;
        }
        GuildSettings.get(e.getGuild().getId(), prefix, bot).setPrefix(prefix);
        e.send("Successfully set prefix to " + prefix).queue();
    }
}

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
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.GuildSettings;
import net.vpg.bot.framework.Util;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.commands.ModCommand;

public class PrefixCommand extends BotCommandImpl implements ModCommand {
    public PrefixCommand(Bot bot) {
        super(bot, "prefix", "Set the custom prefix of the server");
        addOption(OptionType.STRING, "prefix", "The custom prefix to use", true);
        setMinArgs(1);
    }

    @Override
    public Permission getRequiredPermission() {
        return Permission.MANAGE_SERVER;
    }

    @Override
    public boolean runChecks(CommandReceivedEvent e) {
        return bot.isDatabaseEnabled() && super.runChecks(e);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, e.getArg(1));
    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        execute(e, e.getString("prefix"));
    }

    public void execute(CommandReceivedEvent e, String input) {
        if (input.isBlank()) {
            e.send("Provide a valid prefix!").queue();
            return;
        }
        String prefix = Util.DELIMITER.split(input)[0];
        if (prefix.isBlank() || Util.containsAny(prefix, "@everyone", "@here")) {
            e.send("Provide a valid prefix! `" + prefix + "` is not allowed.").queue();
            return;
        }
        GuildSettings.get(e.getGuild().getId(), prefix, bot).setPrefix(prefix);
        e.send("Successfully set prefix to " + prefix).queue();
    }
}

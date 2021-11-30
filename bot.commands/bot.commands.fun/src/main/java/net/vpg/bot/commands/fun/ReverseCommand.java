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
package net.vpg.bot.commands.fun;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;

public class ReverseCommand extends BotCommandImpl {
    public ReverseCommand(Bot bot) {
        super(bot, "reverse", "Reverses the given text");
        addOption(OptionType.STRING, "text", "the text to reverse", true);
        setMinArgs(1);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, e.getContent().replace(e.getArg(0) + " ", ""));
    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        execute(e, e.getString("text"));
    }

    public void execute(CommandReceivedEvent e, String reverse) {
        e.send(new StringBuilder(reverse).reverse().toString()).queue();
    }
}

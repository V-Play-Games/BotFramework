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
import net.vpg.bot.framework.commands.BotCommandImpl;
import net.vpg.bot.framework.commands.CommandReceivedEvent;

public class ShiftCommand extends BotCommandImpl {
    public ShiftCommand(Bot bot) {
        super(bot, "shift", "Shift the alphabets of the given text aka Caeser Cipher");
        addOption(OptionType.STRING, "text", "the text to shift alphabets of", true);
        addOption(OptionType.INTEGER, "offset", "the amount of alphabets to shift", true);
        setMinArgs(2);
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        execute(e, String.join(" ", e.getArgsFrom(2)), Long.parseLong(e.getArg(1)));
    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        execute(e, e.getString("text"), e.getLong("offset"));
    }

    public void execute(CommandReceivedEvent e, String text, long offset) {
        int shift = (int) ((offset < 0 ? 26 : 0) + (offset % 26));
        if (shift == 0) {
            e.send(text).queue();
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean upperCase = Character.isUpperCase(c);
            char a = upperCase ? 'A' : 'a';
            char z = upperCase ? 'Z' : 'z';
            if (c <= z && c >= a) {
                int shifted = c + shift;
                c = (char) (shifted > z ? shifted - 1 - z + a : shifted);
            }
            sb.append(c);
        }
        e.send(sb.toString()).queue();
    }
}

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
package net.vpg.bot.event.handler;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.Util;
import net.vpg.bot.event.BotButtonEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class DefaultEventHandler extends EventHandler {
    private Pattern selfMentionPattern;
    private boolean closed;

    public DefaultEventHandler(Bot bot) {
        super(bot);
    }

    public Pattern getSelfMentionPattern() {
        return selfMentionPattern == null
            ? selfMentionPattern = Pattern.compile("<@!?" + bot.getPrimaryShard().getSelfUser().getIdLong() + ">")
            : selfMentionPattern;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent e) {
        if (closed) {
            if (e.getMessage().getContentRaw().equalsIgnoreCase(bot.getPrefix() + "activate")
                && bot.isManager(e.getAuthor().getIdLong())) {
                closed = false;
                e.getChannel().sendMessage("Thanks for activating me again!").queue();
            }
            return;
        }
        if (e.getAuthor().isBot() || (e.isFromGuild() && !e.getGuildChannel().canTalk())) {
            return;
        }
        String prefix = Util.getPrefix(e, bot);
        Message message = e.getMessage();
        String content = message.getContentRaw();
        String[] args = Util.SPACE.split(content);
        int argLen = args.length;
        if (content.regionMatches(true, 0, prefix, 0, prefix.length())) {
            boolean spaceAfterPrefix = args[0].length() == prefix.length();
            String command;
            int firstArg;
            if (spaceAfterPrefix) {
                if (argLen <= 1) return;
                int i = 1;
                while (args[i].isBlank()) i++;
                command = args[i];
                firstArg = i + 1;
            } else {
                command = args[0].substring(prefix.length());
                firstArg = 1;
            }
            BotCommand botCommand = bot.getCommands().get(command);
            if (botCommand != null) {
                List<String> finalArgs = firstArg == argLen
                    ? Collections.emptyList()
                    : Arrays.asList(args).subList(firstArg, argLen);
                botCommand.run(new TextCommandReceivedEvent(e, finalArgs, botCommand, prefix));
            }
        } else if (getSelfMentionPattern().matcher(content).find()) {
            e.getChannel().sendMessage("Prefix: " + prefix).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent e) {
        if (closed) {
            if (e.getName().equalsIgnoreCase("activate") && bot.isManager(e.getUser().getIdLong())) {
                closed = false;
                e.getChannel().sendMessage("Thanks for activating me again!").queue();
            }
        } else {
            BotCommand command = bot.getCommands().get(e.getName());
            if (command != null) {
                command.run(new SlashCommandReceivedEvent(e, command));
            }
        }
    }

    @Override
    public void onException(@Nonnull ExceptionEvent e) {
        e.getCause().printStackTrace();
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent e) {
        new BotButtonEvent(e, bot).execute();
    }
}

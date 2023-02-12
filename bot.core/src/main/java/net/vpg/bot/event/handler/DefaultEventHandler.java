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

import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.BotButtonEvent;
import net.vpg.bot.event.CommandReceivedEvent;

import javax.annotation.Nonnull;

public class DefaultEventHandler extends EventHandler {
    protected final Bot bot;

    public DefaultEventHandler(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent e) {
        if (closed) {
            if (e.getName().equalsIgnoreCase("activate") && bot.isManager(e.getUser().getIdLong())) {
                closed = false;
                e.getChannel().sendMessage("Thanks for activating me again!").queue();
            }
            return;
        }
        BotCommand command = bot.getCommands().get(e.getName());
        if (command != null) {
            command.run(new CommandReceivedEvent(e, command));
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

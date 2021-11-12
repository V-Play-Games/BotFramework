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
package net.vpg.bot.commands.fun.guess;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.vpg.bot.framework.*;
import net.vpg.bot.framework.commands.BotCommandImpl;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
import net.vpg.bot.framework.commands.NoArgsCommand;

public class GuessCommand extends BotCommandImpl implements NoArgsCommand {
    public GuessCommand(Bot bot) {
        super(bot, "guess", "Guess a Pokemon name by the given description of it");
        bot.getShardManager().addEventListener(Util.subscribeTo(GuildMessageReceivedEvent.class, this::checkGuess));
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (GuessGame.get(e.getUser().getId()) != null) return;
        GuessGame game = new GuessGame(e);
        e.sendEmbeds(new EmbedBuilder()
            .setTitle("Who's that Pokemon?")
            .setDescription("Guess the Pokemon based on its given description in 30 seconds or less!\n> " + game.getText())
            .build())
            .setActionRow(
                Button.primary("guess:" + game.getUserId() + ":h", "Get a hint"),
                Button.primary("guess:" + game.getUserId() + ":x", "Give up")
            )
            .queue(game::setMessage);
    }

    public void checkGuess(GuildMessageReceivedEvent e) {
        GuessGame game = GuessGame.get(e.getAuthor().getId());
        if (game.isCorrect(e.getMessage().getContentRaw())) {
            game.close(Sender.fromMessage(e.getMessage()), GuessGame.WON);
        } else {
            e.getMessage().addReaction("U+274C").queue();
        }
    }

    public static class GuessHandler implements ButtonHandler {
        @Override
        public String getName() {
            return "guess";
        }

        @Override
        public void handle(BotButtonEvent e) {
            String userId = e.getUser().getId();
            if (!e.getArg(0).equals(userId)) return;
            GuessGame game = GuessGame.get(userId);
            if (game == null) {
                e.deferEdit().setActionRows().queue();
                return;
            }
            switch (e.getArg(1)) {
                case "h":
                    e.editComponents(ActionRow.of(Button.primary("guess:" + game.getUserId() + ":x", "Give up")))
                        .setEmbeds(new EmbedBuilder()
                            .setTitle("Who's that Pokemon?")
                            .setDescription("Guess the Pokemon based on its given description in 30 seconds or less!" +
                                "\n> " + game.getText() +
                                "\nType: " + game.getPokemon().getType())
                            .build())
                        .queue();
                    break;
                case "x":
                    e.editComponents().queue();
                    game.close(Sender.fromChannel(e.getChannel()), GuessGame.FORFEIT);
                    break;
            }
        }
    }
}

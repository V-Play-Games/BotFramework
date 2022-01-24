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
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.vpg.bot.action.Sender;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.NoArgsCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.ButtonHandler;
import net.vpg.bot.event.BotButtonEvent;
import net.vpg.bot.event.CommandReceivedEvent;

public class GuessCommand extends BotCommandImpl implements NoArgsCommand {
    public GuessCommand(Bot bot) {
        super(bot, "guess", "Guess a Pokemon name by the given description of it");
        bot.subscribeTo("guess", MessageReceivedEvent.class, this::checkGuess);
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

    public void checkGuess(MessageReceivedEvent e) {
        GuessGame game = GuessGame.get(e.getAuthor().getId());
        if (game == null) return;
        if (game.isCorrect(e.getMessage().getContentRaw())) {
            game.close(Sender.of(e.getMessage()), GuessGame.WON);
        } else {
            e.getMessage().addReaction("U+274C").queue();
        }
    }

    public static class GuessButtonHandler implements ButtonHandler {
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
                            .setDescription(String.format("Guess the Pokemon based on its given description in 30 seconds or less!\n> %s\nType: %s", game.getText(), game.getPokemon().getType()))
                            .build())
                        .queue();
                    break;
                case "x":
                    e.editComponents().queue();
                    game.close(Sender.of(e.getChannel()), GuessGame.FORFEIT);
                    break;
            }
        }
    }
}

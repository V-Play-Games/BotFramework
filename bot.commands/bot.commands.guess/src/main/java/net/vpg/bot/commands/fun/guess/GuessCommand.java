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
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.ButtonHandler;
import net.vpg.bot.event.BotButtonEvent;
import net.vpg.bot.event.CommandReceivedEvent;

public class GuessCommand extends BotCommandImpl {
    public GuessCommand(Bot bot) {
        super(bot, "guess", "Guess a Pokemon name by the given description of it");
        bot.getEventProcessor().addListener(ModalInteractionEvent.class, this::checkGuess);
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        if (GuessGame.get(e.getUser().getId()) != null) return;
        GuessGame game = new GuessGame(e);
        e.replyEmbeds(new EmbedBuilder()
            .setTitle("Who's that Pokemon?")
            .setDescription("Guess the Pokemon based on its given description in 30 seconds or less!\n> " + game.getText())
            .build())
            .setActionRow(
                Button.primary("guess:" + game.getUserId() + ":h", "Get a hint"),
                Button.primary("guess:" + game.getUserId() + ":g", "Guess"),
                Button.primary("guess:" + game.getUserId() + ":x", "Give up")
            )
            .queue(x -> x.retrieveOriginal().queue(game::setMessage));
    }

    public void checkGuess(ModalInteractionEvent e) {
        if (!e.getId().equals("guess")) return;
        GuessGame game = GuessGame.get(e.getUser().getId());
        if (game == null) return;
        // noinspection ConstantConditions
        String input = e.getValue("input").getAsString();
        if (game.isCorrect(input)) {
            game.win(e);
        } else {
            e.reply("\"" + input + "\" is not the correct answer!").queue(); // CROSS
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
                e.deferEdit().setComponents().queue();
                return;
            }
            switch (e.getArg(1)) {
                case "h":
                    e.editComponents(ActionRow.of(Button.primary("guess:" + game.getUserId() + ":x", "Give up")))
                        .setEmbeds(new EmbedBuilder()
                            .setTitle("Who's that Pokemon?")
                            .setDescription("Guess the Pokemon based on its given description in 30 seconds or less!\n> " + game.getText() + "\nType: " + game.getPokemon().getType())
                            .build())
                        .queue();
                    break;
                case "g":
                    e.replyModal(Modal.create("guess", "Who's that Pokemon?")
                        .addActionRow(TextInput.create("input", "Enter your guess here", TextInputStyle.SHORT).build())
                        .build())
                        .queue();
                    break;
                case "x":
                    e.editComponents().queue();
                    game.forfeit(e);
                    break;
            }
        }
    }
}

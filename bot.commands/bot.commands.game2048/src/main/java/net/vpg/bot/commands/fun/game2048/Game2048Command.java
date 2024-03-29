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
package net.vpg.bot.commands.fun.game2048;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.ButtonHandler;
import net.vpg.bot.event.BotButtonEvent;
import net.vpg.bot.event.CommandReceivedEvent;

import java.util.List;

public class Game2048Command extends BotCommandImpl {
    public static DataObject emotes = DataObject.fromJson(Game2048Command.class.getResourceAsStream("emotes.json"));

    public Game2048Command(Bot bot) {
        super(bot, "2048", "Play the classic 2048 game in a 4x4 box");
    }

    private static ActionRow getButtons(String id) {
        return ActionRow.of(
            Button.primary("2048:" + id + ":l", Emoji.fromUnicode("\u2B05")),
            Button.primary("2048:" + id + ":u", Emoji.fromUnicode("\u2B06")),
            Button.primary("2048:" + id + ":d", Emoji.fromUnicode("\u2B07")),
            Button.primary("2048:" + id + ":r", Emoji.fromUnicode("\u27A1")),
            Button.danger("2048:" + id + ":x", Emoji.fromUnicode("\u274C"))
        );
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        e.replyEmbeds(new Board(4).spawn().spawn().toEmbed())
            .setComponents(getButtons(e.getUser().getId())).queue();
    }

    public static class ButtonHandler2048 implements ButtonHandler {
        @Override
        public String getName() {
            return "2048";
        }

        @Override
        public void handle(BotButtonEvent e) {
            if (!e.getArg(0).equals(e.getUser().getId())) {
                return;
            }
            Move move = Move.fromKey(e.getArg(1).charAt(0));
            if (move == null) {
                e.editMessage("The game was cancelled!").setActionRow().queue();
                return;
            }
            List<MessageEmbed> embeds = e.getMessage().getEmbeds();
            if (embeds.isEmpty()) {
                // Will this ever happen though? better safe than sorry I guess
                e.editComponents().queue();
                return;
            }
            Board board = Board.fromEmbed(embeds.get(0));
            board.move(move);
            MessageEditCallbackAction action = e.deferEdit().setEmbeds(board.toEmbed());
            if (board.checkWin()) {
                action.setContent("GG! You won!").setActionRow().queue();
            } else if (board.checkLose()) {
                action.setContent("You're out of moves! Game Over.").setActionRow().queue();
            } else {
                action.queue();
            }
        }
    }
}

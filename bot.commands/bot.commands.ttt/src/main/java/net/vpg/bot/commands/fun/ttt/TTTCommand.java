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

package net.vpg.bot.commands.fun.ttt;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.ButtonHandler;
import net.vpg.bot.event.BotButtonEvent;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;

import java.util.List;

public class TTTCommand extends BotCommandImpl {
    public TTTCommand(Bot bot) {
        super(bot, "ttt", "Play the classic TicTacToe game");
        addOptions(
            new OptionData(OptionType.USER, "opponent", "Mention who do you want to play with!"),
            new OptionData(OptionType.STRING, "play-as", "Choose who do you want to play ass")
                .addChoice("X", "X")
                .addChoice("O", "O")
        );
        setMaxArgs(2);
        setMinArgs(0);
    }

    public void execute(CommandReceivedEvent e, String play_as, String opponent) {
        String player1 = e.getUser().getId();
        String player2 = opponent == null || opponent.equals(player1) ? "ai" : opponent;
        boolean firstIsX = "X".equalsIgnoreCase(play_as);
        Board board = new Board(player1, player2, firstIsX);
        if (player2.equals("ai")) { // Single-player: no confirmation needed
            board.send(e);
        } else {
            e.send(e.getUser().getAsMention() + " has challenged <@" + player2 + "> to a TicTacToe Duel!")
                .setActionRow(
                    Button.primary("ttt:" + board.id + ":p", "Play"),
                    Button.primary("ttt:" + board.id + ":x", "Cancel")
                )
                .queue();
        }
    }

    @Override
    public void onTextCommandRun(TextCommandReceivedEvent e) {
        List<User> mentions = e.getMessage().getMentionedUsers();
        execute(e, e.getArgs().isEmpty() ? null : e.getArg(0), mentions.isEmpty() ? null : mentions.get(0).getId());
    }

    @Override
    public void onSlashCommandRun(SlashCommandReceivedEvent e) {
        execute(e, e.getString("play-as"), e.getUser("opponent").getId());
    }

    public static class TTTButtonHandler implements ButtonHandler {
        @Override
        public String getName() {
            return "ttt";
        }

        @Override
        public void handle(BotButtonEvent e) {
            String id = e.getArg(0);
            String clicker = e.getUser().getId();
            if (!id.contains(clicker)) return;
            Board board = Board.get(id);
            if (board == null) return;
            switch (e.getArg(1)) {
                case "p":
                    if (board.player2.id.equals(clicker))
                        board.send(e);
                    break;
                case "x":
                    e.editMessage("<@" + clicker + "> cancelled the challenge!").setActionRows().queue();
                    break;
                case "c":
                    Player thisTurn = board.getThisTurn();
                    if (!clicker.equals(thisTurn.id)) return;
                    assert CellType.forKey(e.getArg(4)) == CellType.BLANK; // should be blank to be clickable
                    int row = Integer.parseInt(e.getArg(2));
                    int column = Integer.parseInt(e.getArg(3));
                    board.getCell(row, column).setType(thisTurn.type);
                    board.switchSides();
                    Player winner = board.getWinner();
                    if (winner == null) {
                        if (board.checkTie()) {
                            e.editMessage("It's a tie! Nobody won.").setActionRows(board.getActionRows()).queue();
                        } else {
                            board.send(e);
                        }
                    } else {
                        e.editMessage("<@" + winner.id + "> won the match!").setActionRows(board.getActionRows()).queue();
                        board.remove();
                    }
                    break;
            }
        }
    }
}

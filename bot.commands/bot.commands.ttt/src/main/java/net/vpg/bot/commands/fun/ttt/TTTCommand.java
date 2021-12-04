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
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.BotButtonEvent;
import net.vpg.bot.framework.ButtonHandler;

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

    private static ActionRow confirmationButtons(String id) {
        return ActionRow.of(
            Button.primary("ttt:" + id + ":p", "Play"),
            Button.primary("ttt:" + id + ":x", "Cancel")
        );
    }

//    private static Button howToPlay(String id) {
//        return Button.primary("ttt:" + id + ":h", "How to Play");
//    }

    public static void start(CommandReceivedEvent e, Board board) {
        doAITurn(board);
        sendTurn(e, board);
    }

    private static void doAITurn(Board board) {
        Player thisTurn = board.getThisTurn();
        if (thisTurn.id.equals("ai")) {
            AI.makeMove(board, thisTurn.type);
            board.switchSides();
        }
    }

    private static void sendTurn(CommandReceivedEvent e, Board board) {
        Player thisTurn = board.getThisTurn();
        e.send(String.format("It is <@%s>'s turn! (Playing as %s)", thisTurn.id, thisTurn.type))
            .setActionRows(board.getActionRows())
            .queue();
    }

    private static void sendTurn(BotButtonEvent e, Board board) {
        Player thisTurn = board.getThisTurn();
        e.editMessage(String.format("It is <@%s>'s turn! (Playing as %s)", thisTurn.id, thisTurn.type))
            .setActionRows(board.getActionRows())
            .queue();
    }

    public void execute(CommandReceivedEvent e, String play_as, User opponent) {
        String player1 = e.getUser().getId();
        String player2 = opponent == null || opponent.getId().equals(player1) ? "ai" : opponent.getId();
        boolean firstIsX = "X".equalsIgnoreCase(play_as);
        Board board = new Board(player1, player2, firstIsX);
        if (player2.equals("ai")) { // Single-player: no confirmation needed
            start(e, board);
        } else {
            e.send(e.getUser().getAsMention() + " has challenged <@" + player2 + "> to a TicTacToe Duel!")
                .setActionRows(confirmationButtons(board.id))
                .queue();
        }
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) {
        List<User> mentions = e.getMessage().getMentionedUsers();
        execute(e, e.getArgs().isEmpty() ? null : e.getArg(0), mentions.isEmpty() ? null : mentions.get(0));
    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) {
        execute(e, e.getString("play-as"), e.getUser("opponent"));
    }

    public static class TTTHandler implements ButtonHandler {
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
                    if (board.player2.id.equals(clicker)) {
                        sendTurn(e, board);
                    }
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
                    doAITurn(board);
                    Player winner = board.getWinner();
                    if (winner == null) {
                        if (board.checkTie()) {
                            e.editMessage("It's a tie! Nobody won.").setActionRows(board.getActionRows()).queue();
                        } else {
                            sendTurn(e, board);
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

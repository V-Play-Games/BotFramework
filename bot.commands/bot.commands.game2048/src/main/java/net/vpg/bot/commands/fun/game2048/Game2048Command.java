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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.BotButtonEvent;
import net.vpg.bot.framework.ButtonHandler;
import net.vpg.bot.framework.commands.BotCommandImpl;
import net.vpg.bot.framework.commands.CommandReceivedEvent;
import net.vpg.bot.framework.commands.NoArgsCommand;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.entities.Message.MentionType.EMOTE;

public class Game2048Command extends BotCommandImpl implements NoArgsCommand {
    public static DataObject emotes = DataObject.fromJson(Game2048Command.class.getResourceAsStream("emotes.json"));
    private static Queue<Board> spareBoards = new LinkedBlockingQueue<>();

    public Game2048Command(Bot bot) {
        super(bot, "2048", "Play the classic 2048 game in a 4x4 box");
    }

    private static EmbedBuilder toEmbed(Board board) {
        return new EmbedBuilder().setDescription(
            Arrays.stream(board.getCells())
                .map(row -> Arrays.stream(row)
                    .map(Cell::getValue)
                    .map(Object::toString)
                    .map(emotes::getString)
                    .collect(Collectors.joining("")))
                .collect(Collectors.joining("\n"))
        ).setFooter("Score: " + board.getScore());
    }

    private static ActionRow getButtons(long id) {
        return ActionRow.of(
            Button.primary("2048:" + id + ":l", Emoji.fromUnicode("\u2B05")),
            Button.primary("2048:" + id + ":u", Emoji.fromUnicode("\u2B06")),
            Button.primary("2048:" + id + ":d", Emoji.fromUnicode("\u2B07")),
            Button.primary("2048:" + id + ":r", Emoji.fromUnicode("\u27A1")),
            Button.danger("2048:" + id + ":x", Emoji.fromUnicode("\u274C"))
        );
    }

    private static Board getBoard() {
        Board board = spareBoards.isEmpty() ? new Board(4) : spareBoards.poll();
        board.getCellsAsStream().forEach(cell -> cell.setType(CellType.C0));
        return board;
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        Board board = getBoard();
        board.setScore(0);
        board.spawn();
        board.spawn();
        e.sendEmbeds(toEmbed(board).build()).setActionRows(getButtons(e.getUser().getIdLong())).queue();
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
                e.editMessage("The game was cancelled!").setActionRows().queue();
                return;
            }
            MessageEmbed embed = e.getMessage().getEmbeds().get(0);
            Board board = boardFromString(embed.getDescription());
            //noinspection ConstantConditions
            board.setScore(Integer.parseInt(embed.getFooter().getText().replace("Score: ", "")));
            board.move(move);
            UpdateInteractionAction action = e.deferEdit().setEmbeds(toEmbed(board).build());
            if (board.checkWin()) {
                action.setContent("GG! You won!").setActionRows().queue();
            } else if (board.checkLose()) {
                action.setContent("You're out of moves! Game Over.").setActionRows().queue();
            } else {
                action.queue();
            }
            spareBoards.offer(board);
        }

        private Board boardFromString(String input) {
            Board board = getBoard();
            CellType[] values = EMOTE.getPattern()
                .matcher(input)
                .replaceAll(result -> result.group(1) + '\n')
                .lines()
                .filter(s -> !s.isBlank())
                .mapToInt(Integer::parseInt)
                .mapToObj(CellType::forValue)
                .toArray(CellType[]::new);
            AtomicInteger i = new AtomicInteger();
            board.getCellsAsStream().forEach(cell -> cell.setType(values[i.getAndIncrement()]));
            return board;
        }
    }
}

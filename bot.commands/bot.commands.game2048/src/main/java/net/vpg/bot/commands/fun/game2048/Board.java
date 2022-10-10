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
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.vpg.bot.core.Util;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.vpg.bot.commands.fun.game2048.Game2048Command.emotes;

public class Board {
    final int size;
    final Cell[][] cells;
    int score;

    public Board(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                new Cell(i, j, this);
            }
        }
    }

    private Board(int size, CellType[] types) {
        this.size = size;
        this.cells = new Cell[size][size];
        int k = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                new Cell(i, j, this, types[k++]);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static Board fromEmbed(MessageEmbed embed) {
        CellType[] values = Message.MentionType.EMOJI.getPattern()
            .matcher(embed.getDescription())
            .replaceAll(result -> result.group(1) + '\n')
            .lines()
            .filter(s -> !s.isBlank())
            .mapToInt(Integer::parseInt)
            .mapToObj(CellType::forValue)
            .toArray(CellType[]::new);
        Board board = new Board(4, values);
        board.score = Integer.parseInt(embed.getFooter().getText().replace("Score: ", ""));
        return board;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Stream<Cell> getCellsAsStream() {
        return Arrays.stream(cells).flatMap(Arrays::stream);
    }

    public void move(Move move) {
        int row = move.row * (size - 1);
        int column = move.column * (size - 1);
        for (int runs = 0; runs < size; runs++, row += move.rowChange, column += move.columnChange) {
            cells[row][column].move(move);
        }
        spawn();
        int moveScore = getCellsAsStream()
            .filter(Cell::isModified)
            .peek(Cell::removeModify)
            .mapToInt(Cell::getValue)
            .sum();
        score += moveScore;
    }

    public boolean checkLose() {
        return getCellsAsStream().noneMatch(Cell::canMove);
    }

    public boolean checkWin() {
        return getCellsAsStream().anyMatch(Cell::isFinal);
    }

    public Board spawn() {
        Cell[] emptyCells = getCellsAsStream().filter(Cell::isEmpty).toArray(Cell[]::new);
        if (emptyCells.length != 0) {
            Spawner.getInstance().spawn(Util.getRandom(emptyCells));
        }
        return this;
    }

    public int getScore() {
        return score;
    }

    public Board setScore(int score) {
        this.score = score;
        return this;
    }

    public String toString() {
        return Arrays.stream(cells)
            .map(row -> Arrays.stream(row)
                .map(Cell::getValue)
                .map(Object::toString)
                .map(emotes::getString)
                .collect(Collectors.joining("")))
            .collect(Collectors.joining("\n"));
    }

    public MessageEmbed toEmbed() {
        return new EmbedBuilder().setDescription(toString()).setFooter("Score: " + score).build();
    }
}

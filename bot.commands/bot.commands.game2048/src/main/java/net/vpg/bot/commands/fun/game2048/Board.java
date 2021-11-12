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

import net.vpg.bot.framework.Util;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

public class Board {
    final int size;
    final Random random;
    Cell[][] cells;
    int score;

    public Board(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                new Cell(i, j, this);
            }
        }
        this.random = new Random();
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Stream<Cell> getCellsAsStream() {
        return Arrays.stream(cells).flatMap(Arrays::stream);
    }

    public int move(Move move) {
        int row = move.row * (size - 1);
        int column = move.column * (size - 1);
        for (int runs = 0; runs < size; runs++, row += move.rowChange, column += move.columnChange) {
            cells[row][column].move(move);
        }
        spawn();
        int moveScore = getCellsAsStream()
            .filter(Cell::isModified)
            .peek(cell -> cell.setModified(false))
            .mapToInt(Cell::getValue)
            .sum();
        score += moveScore;
        return moveScore;
    }

    public boolean checkLose() {
        return getCellsAsStream().noneMatch(Cell::canMove);
    }

    public boolean checkWin() {
        return getCellsAsStream().anyMatch(Cell::isFinal);
    }

    public void spawn() {
        Optional.of(getCellsAsStream().filter(Cell::isEmpty).toArray(Cell[]::new))
            .filter(emptyCells -> emptyCells.length != 0)
            .ifPresent(emptyCells -> Spawner.getInstance().spawn(Util.getRandom(emptyCells, random)));
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public String toString() {
        StringBuilder tor = new StringBuilder();
        tor.append('+');
        tor.append("------+".repeat(size));
        int firstLineLen = tor.length();
        tor.append('\n');
        for (Cell[] row : cells) {
            tor.append('|');
            for (Cell cell : row) {
                tor.append(' ').append(cell.getFormatted()).append(' ').append('|');
            }
            tor.append('\n');
        }
        tor.append(tor, 0, firstLineLen);
        return tor.toString();
    }
}

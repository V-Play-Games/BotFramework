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

import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.vpg.bot.framework.Sender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Board {
    public static final Map<String, Board> boards = new HashMap<>();
    final String id;
    final Cell[][] cells;
    Player player1;
    Player player2;

    public Board(String player1, String player2, boolean firstIsX) {
        this.player1 = new Player(player1, firstIsX ? CellType.X : CellType.O);
        this.player2 = new Player(player2, firstIsX ? CellType.O : CellType.X);
        this.id = player1 + "-" + player2 + "-" + System.currentTimeMillis();
        this.cells = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = new Cell(i, j, this);
            }
        }
        boards.put(id, this);
        if (!firstIsX) switchSides();
    }

    public static Board get(String id) {
        return boards.get(id);
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Stream<Cell> getCellsAsStream() {
        return Arrays.stream(cells).flatMap(Arrays::stream);
    }

    public Cell getCell(int row, int column) {
        return cells[row][column];
    }

    public ActionRow[] getActionRows() {
        return Arrays.stream(cells)
            .map(row -> Arrays.stream(row)
                .map(Cell::getButton)
                .toArray(Button[]::new))
            .map(ActionRow::of)
            .toArray(ActionRow[]::new);
    }

    public Player getWinner() {
        // check rows
        for (int i = 0; i < 3; i++) {
            Cell[] row = cells[i];
            CellType mid = row[1].type;
            if (!mid.isBlank() && row[0].type == mid && row[2].type == mid)
                return playerOfType(mid);
        }
        // check columns
        for (int i = 0; i < 3; i++) {
            CellType mid = cells[1][i].type;
            if (!mid.isBlank() && cells[0][i].type == mid && cells[2][i].type == mid)
                return playerOfType(mid);
        }
        // check diagonals
        CellType mid = cells[1][1].type;
        if (!mid.isBlank() &&
            (cells[2][2].type == mid && cells[0][0].type == mid) ||
            (cells[0][2].type == mid && cells[2][0].type == mid))
            return playerOfType(mid);
        return null;
    }

    public boolean checkTie() {
        return getCellsAsStream().noneMatch(Cell::isBlank);
    }

    public Cell getWinningMoveFor(CellType winner) {
        // check rows
        for (int i = 0; i < 3; i++) {
            int count = 0;
            Cell emptyPosition = null;
            for (int j = 0; j < 3; j++) {
                Cell cell = cells[i][j];
                if (cell.isBlank()) {
                    emptyPosition = cell;
                } else if (cell.type == winner) {
                    count++;
                }
            }
            if (count == 2) {
                return emptyPosition;
            }
        }

        // check columns
        for (int i = 0; i < 3; i++) {
            int count = 0;
            Cell emptyPosition = null;
            for (int j = 0; j < 3; j++) {
                Cell cell = cells[j][i];
                if (cell.isBlank()) {
                    emptyPosition = cell;
                } else if (cell.type == winner) {
                    count++;
                }
            }
            if (count == 2) {
                return emptyPosition;
            }
        }

        // check diagonals
        int count = 0;
        Cell emptyPosition = null;
        for (int i = 0; i < 3; i++) {
            Cell cell = cells[i][i];
            if (cell.isBlank()) {
                emptyPosition = cell;
            } else if (cell.type == winner) {
                count++;
            }
            if (count == 2) {
                return emptyPosition;
            }
        }

        count = 0;
        emptyPosition = null;
        for (int i = 0; i < 3; i++) {
            Cell cell = cells[i][2 - i];
            if (cell.isBlank()) {
                emptyPosition = cell;
            } else if (cell.type == winner) {
                count++;
            }
            if (count == 2) {
                return emptyPosition;
            }
        }
        return null;
    }

    public Player getThisTurn() {
        return player1;
    }

    public Player switchSides() {
        Player next = player2;
        player2 = player1;
        player1 = next;
        if (player1.id.equals("ai")) {
            AI.makeMove(this, player1.type);
            return switchSides();
        }
        return player1;
    }

    public void send(Sender sender) {
        sender.send(String.format("It is <@%s>'s turn! (Playing as %s)", player1.id, player1.type))
            .setActionRows(getActionRows())
            .queue();
    }

    public Cell[] getCellsOfType(CellType type) {
        return getCellsAsStream().filter(cell -> cell.type == type).toArray(Cell[]::new);
    }

    public int getCellCountOfType(CellType type) {
        return (int) getCellsAsStream().filter(cell -> cell.type == type).count();
    }

    public Player playerOfType(CellType type) {
        if (type == player1.type)
            return player1;
        if (type == player2.type)
            return player2;
        return null;
    }

    public void remove() {
        boards.remove(id);
    }
}

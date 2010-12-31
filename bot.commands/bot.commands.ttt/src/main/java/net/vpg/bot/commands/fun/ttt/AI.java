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

public class AI {
    public static void makeMove(Board board, CellType type) {
        Cell bestPlace = getBestMove(board, type);
        if (bestPlace != null) {
            bestPlace.setType(type);
        }
    }

    private static Cell getBestMove(Board board, CellType ai) {
        Cell safeMove = getWinningMove(board, ai.other());
        if (safeMove != null) {
            return safeMove;
        }
        Cell bestMove = null;
        int bestScore = -100;
        for (Cell cell : board.getCellsOfType(CellType.BLANK)) {
            cell.setType(ai);
            int score = getScore(board, ai);
            cell.setType(CellType.BLANK);
            if (score > bestScore) {
                bestScore = score;
                bestMove = cell;
            }
        }
        return bestMove;
    }

    private static int getScore(Board board, CellType type) {
        Player winner = board.getWinner();
        if (winner != null) {
            return winner.type == type ? 1 : -1;
        } else if (board.checkTie()) {
            return 0;
        }
        CellType other = type.other();
        boolean losing = board.getCellCountOfType(other) > board.getCellCountOfType(type);
        int bestScore = losing ? -1 : 1;
        for (Cell cell : board.getCellsOfType(CellType.BLANK)) {
            cell.setType(losing ? type : other);
            int currentScore = getScore(board, type);
            cell.setType(CellType.BLANK);
            if (losing ? currentScore > bestScore : currentScore < bestScore) {
                bestScore = currentScore;
            }
        }
        return bestScore;
    }

    private static Cell getWinningMove(Board board, CellType type) {
        // check rows
        for (int i = 0; i < 3; i++) {
            int count = 0;
            Cell emptyPosition = null;
            for (int j = 0; j < 3; j++) {
                Cell cell = board.cells[i][j];
                if (cell.isBlank()) {
                    emptyPosition = cell;
                } else if (cell.type == type) {
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
                Cell cell = board.cells[j][i];
                if (cell.isBlank()) {
                    emptyPosition = cell;
                } else if (cell.type == type) {
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
            Cell cell = board.cells[i][i];
            if (cell.isBlank()) {
                emptyPosition = cell;
            } else if (cell.type == type) {
                count++;
            }
            if (count == 2) {
                return emptyPosition;
            }
        }

        count = 0;
        emptyPosition = null;
        for (int i = 0; i < 3; i++) {
            Cell cell = board.cells[i][2 - i];
            if (cell.isBlank()) {
                emptyPosition = cell;
            } else if (cell.type == type) {
                count++;
            }
            if (count == 2) {
                return emptyPosition;
            }
        }
        return null;
    }
}

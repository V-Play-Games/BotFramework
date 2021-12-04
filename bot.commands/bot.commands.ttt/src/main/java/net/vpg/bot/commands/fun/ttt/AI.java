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

    public static Cell getBestMove(Board board, CellType ai) {
        Cell safeMove = board.getWinningMoveFor(ai.other());
        if (safeMove != null) {
            return safeMove;
        }
        Cell bestMove = null;
        int bestScore = -100;
        for (Cell cell : board.getCellsOfType(CellType.BLANK)) {
            cell.setType(ai);
            int score = minimax(board, ai);
            cell.setType(CellType.BLANK);
            if (score > bestScore) {
                bestScore = score;
                bestMove = cell;
            }
        }
        return bestMove;
    }

    public static int minimax(Board board, CellType ai) {
        Player winner = board.getWinner();
        CellType human = ai.other();
        if (winner != null) {
            return winner.type == ai ? 1 : -1;
        } else if (board.checkTie()) {
            return 0;
        }

        boolean aiLosing = board.getCellCountOfType(human) > board.getCellCountOfType(ai);

        int bestScore = aiLosing ? -1 : 1;

        for (Cell cell : board.getCellsOfType(CellType.BLANK)) {
            cell.setType(aiLosing ? ai : human);
            int currentScore = minimax(board, ai);
            cell.setType(CellType.BLANK);
            if (aiLosing ? currentScore > bestScore : currentScore < bestScore) {
                bestScore = currentScore;
            }
        }
        return bestScore;
    }
}

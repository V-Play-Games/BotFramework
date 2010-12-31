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

public class Cell {
    final Board board;
    final int limit;
    Cell[][] cells;
    int row;
    int column;
    CellType type;
    boolean modified;

    public Cell(int row, int column, Board board) {
        this(row, column, board, CellType.C0);
    }

    public Cell(int row, int column, Board board, CellType type) {
        this.cells = board.cells;
        this.board = board;
        this.limit = board.size - 1;
        this.type = type;
        setCoordinates(row, column);
    }

    public void move(Move move) {
        switch (move) {
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
            case LEFT:
                moveLeft();
                break;
            case RIGHT:
                moveRight();
                break;
        }
    }

    public void moveDown() {
        int initial = row;
        while (row != limit && tryMerge(cells[row + 1][column])) ;
        if (initial != 0) cells[initial - 1][column].moveDown();
    }

    public void moveUp() {
        int initial = row;
        while (row != 0 && tryMerge(cells[row - 1][column])) ;
        if (initial != limit) cells[initial + 1][column].moveUp();
    }

    public void moveRight() {
        int initial = column;
        while (column != limit && tryMerge(cells[row][column + 1])) ;
        if (initial != 0) cells[row][initial - 1].moveRight();
    }

    public void moveLeft() {
        int initial = column;
        while (column != 0 && tryMerge(cells[row][column - 1])) ;
        if (initial != limit) cells[row][initial + 1].moveLeft();
    }

    public CellType getType() {
        return type;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void removeModify() {
        this.modified = false;
    }

    private boolean tryMerge(Cell target) {
        if (this.isEmpty()) return false;
        if (target.isEmpty()) {
            int targetRow = target.row;
            int targetCol = target.column;
            target.setCoordinates(this.row, this.column);
            this.setCoordinates(targetRow, targetCol);
            return true;
        }
        if (target.type == this.type && !target.modified) {
            target.type = CellType.forValue(this.type.getValue() * 2);
            target.modified = true;
            this.type = CellType.C0;
            return false;
        }
        return false;
    }

    void setCoordinates(int row, int column) {
        this.row = row;
        this.column = column;
        cells[row][column] = this;
    }

    public boolean canMove() {
        return (row != board.size - 1 && checkMove(cells[row + 1][column])) ||
            (row != 0 && checkMove(cells[row - 1][column])) ||
            (column != board.size - 1 && checkMove(cells[row][column + 1])) ||
            (column != 0 && checkMove(cells[row][column - 1]));
    }

    private boolean checkMove(Cell target) {
        return target.type.isEmpty() || this.type == target.type;
    }

    public boolean isEmpty() {
        return type.isEmpty();
    }

    public boolean isFinal() {
        return type.isFinal();
    }

    public int getValue() {
        return type.getValue();
    }
}

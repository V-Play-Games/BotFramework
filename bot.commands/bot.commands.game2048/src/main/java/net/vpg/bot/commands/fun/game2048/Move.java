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

public enum Move {
    //@formatter:off
    UP   (0, 0, 0, 1),
    DOWN (1, 0, 0, 1),
    LEFT (0, 0, 1, 0),
    RIGHT(0, 1, 1, 0);
    //@formatter:on

    final int row;
    final int column;
    final int rowChange;
    final int columnChange;

    Move(int row, int column, int rowChange, int columnChange) {
        this.row = row;
        this.column = column;
        this.rowChange = rowChange;
        this.columnChange = columnChange;
    }

    public static Move fromKey(char key) {
        switch (key) {
            case 'u':
            case 'U':
                return UP;
            case 'd':
            case 'D':
                return DOWN;
            case 'l':
            case 'L':
                return LEFT;
            case 'r':
            case 'R':
                return RIGHT;
            default:
                return null;
        }
    }
}

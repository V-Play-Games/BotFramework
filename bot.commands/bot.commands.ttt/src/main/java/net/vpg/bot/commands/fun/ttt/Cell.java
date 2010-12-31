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

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

public class Cell {
    private final String id;
    private final int row;
    private final int column;
    CellType type;

    public Cell(int row, int column, Board board) {
        this.id = board.id + ":c:" + row + ":" + column;
        this.row = row;
        this.column = column;
        this.type = CellType.BLANK;
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

    public int getColumn() {
        return column;
    }

    public Emoji getEmoji() {
        return type.getEmoji();
    }

    public boolean isBlank() {
        return type.isBlank();
    }

    // The returned button has id -> ttt:player1-player2-time:c:row:column:type
    public Button getButton() {
        return new ButtonImpl("ttt:" + id + ":" + type.identifier, null, ButtonStyle.PRIMARY, type != CellType.BLANK, getEmoji());
    }
}

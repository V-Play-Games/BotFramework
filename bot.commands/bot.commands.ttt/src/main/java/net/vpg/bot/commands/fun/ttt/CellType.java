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

import net.dv8tion.jda.api.entities.emoji.Emoji;

public enum CellType {
    BLANK("<:00:905666298648866846>", "b"),
    X("\u274C", "x"),
    O("\u2B55", "o");

    final Emoji emoji;
    final String identifier;

    CellType(String emoji, String identifier) {
        this.emoji = Emoji.fromFormatted(emoji);
        this.identifier = identifier;
    }

    public static CellType forKey(String key) {
        switch (key) {
            case "x":
                return X;
            case "o":
                return O;
            default:
                return BLANK;
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public Emoji getEmoji() {
        return emoji;
    }

    public boolean isBlank() {
        return this == BLANK;
    }

    public CellType other() {
        return this == X ? O : X;
    }
}

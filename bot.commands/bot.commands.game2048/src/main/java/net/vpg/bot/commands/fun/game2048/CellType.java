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

public enum CellType {
    //@formatter:off
    C0   (0,    false, 0),
    C2   (2,    true,  9),
    C4   (4,    true,  1),
    C8   (8,    false, 0),
    C16  (16,   false, 0),
    C32  (32,   false, 0),
    C64  (64,   false, 0),
    C128 (128,  false, 0),
    C256 (256,  false, 0),
    C512 (512,  false, 0),
    C1024(1024, false, 0),
    C2048(2048, false, 0);
    //@formatter:on

    static final CellType[] values = values();
    final int value;
    final boolean spawn;
    final int spawnRate;

    CellType(int value, boolean spawn, int spawnRate) {
        this.value = value;
        this.spawn = spawn;
        this.spawnRate = spawnRate;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static CellType forValue(int value) {
        int i = -1;
        while (i++ < values.length && values[i].value != value) ;
        return values[i];
    }

    public int getValue() {
        return value;
    }

    public boolean isSpawn() {
        return spawn;
    }

    public int getSpawnRate() {
        return spawnRate;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public boolean isFinal() {
        return value == 2048;
    }
}

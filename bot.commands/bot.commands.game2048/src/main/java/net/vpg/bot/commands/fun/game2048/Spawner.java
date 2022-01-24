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

import net.vpg.bot.core.Util;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Spawner {
    private static final Spawner instance = new Spawner();
    final Random random;
    final List<CellType> spawnables;
    final int size;

    private Spawner() {
        this.random = new Random();
        this.spawnables = Arrays.stream(CellType.values())
            .filter(CellType::isSpawn)
            .map(cell -> {
                CellType[] cells = new CellType[cell.getSpawnRate()];
                Arrays.fill(cells, cell);
                return cells;
            })
            .flatMap(Arrays::stream)
            .collect(Collectors.toList());
        size = spawnables.size();
    }

    public static Spawner getInstance() {
        return instance;
    }

    public CellType spawn() {
        return Util.getRandom(spawnables, random);
    }

    public void spawn(Cell cell) {
        cell.setType(spawn());
    }
}

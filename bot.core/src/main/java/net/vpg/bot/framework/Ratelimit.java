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
package net.vpg.bot.framework;

public class Ratelimit {
    private final long inflictedAt;
    private final long inflictedOn;
    private final long cooldown;
    private boolean informed;

    public Ratelimit(long i, long cooldown) {
        this.informed = false;
        this.inflictedAt = System.currentTimeMillis();
        this.inflictedOn = i;
        this.cooldown = cooldown;
    }

    public long getInflictedAt() {
        return inflictedAt;
    }

    public long getInflictedOn() {
        return inflictedOn;
    }

    public long getCooldown() {
        return cooldown;
    }

    public boolean isInformed() {
        return informed;
    }

    public void setInformed(boolean informed) {
        this.informed = informed;
    }

    public long getCooldownLeft() {
        return cooldown + inflictedAt - System.currentTimeMillis();
    }

    public String getCooldownString() {
        return Util.toString(getCooldownLeft());
    }
}

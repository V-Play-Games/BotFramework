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
package net.vpg.bot.ratelimit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRatelimiter implements Ratelimiter {
    protected final long cooldown;
    protected final Map<Long, Ratelimit> ratelimited = new HashMap<>();

    public AbstractRatelimiter(long cooldown) {
        this.cooldown = cooldown;
    }

    public AbstractRatelimiter(long cooldown, TimeUnit unit) {
        this(unit.toMillis(cooldown));
    }

    @Override
    public Map<Long, Ratelimit> getRatelimited() {
        return ratelimited;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }
}

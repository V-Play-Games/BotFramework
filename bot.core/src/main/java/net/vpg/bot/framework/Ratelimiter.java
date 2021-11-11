/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.bot.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface Ratelimiter {
    Map<Long, Ratelimit> ratelimited = new HashMap<>();

    default void ratelimit(long userId) {
        ratelimited.put(userId, new Ratelimit(userId));
    }

    default boolean isRatelimited(long userId) {
        return isRatelimited(ratelimited.get(userId));
    }

    default boolean isRatelimited(Ratelimit rl) {
        return rl != null && calculateCooldownLeft(rl.inflictedAt) >= 0;
    }

    default long calculateCooldownLeft(long inflictedAt) {
        return getCooldown() + inflictedAt - System.currentTimeMillis();
    }

    default boolean ifRatelimited(long userId, Consumer<Ratelimit> action) {
        Ratelimit ratelimit = ratelimited.get(userId);
        boolean isRatelimited = isRatelimited(ratelimit);
        if (isRatelimited) {
            action.accept(ratelimit);
        }
        return isRatelimited;
    }

    long getCooldown();

    void onRatelimit(Sender e, Ratelimit ratelimit);
}

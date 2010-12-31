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

import java.util.Map;
import java.util.function.Consumer;

public interface Ratelimiter {
    default void ratelimit(long id) {
        getRatelimited().put(id, new Ratelimit(id, getCooldown()));
    }

    default boolean isRatelimited(long userId) {
        return isRatelimited(getRatelimitFor(userId));
    }

    default boolean isRatelimited(Ratelimit rl) {
        return rl != null && rl.getCooldownLeft() >= 0;
    }

    default boolean ifRatelimited(long id, Consumer<Ratelimit> action) {
        return ifRatelimited(getRatelimitFor(id), action);
    }

    default boolean ifRatelimited(Ratelimit ratelimit, Consumer<Ratelimit> action) {
        boolean isRatelimited = isRatelimited(ratelimit);
        if (isRatelimited) {
            action.accept(ratelimit);
        }
        return isRatelimited;
    }

    default boolean checkRatelimited(long id, Sender sender) {
        return checkRatelimited(getRatelimitFor(id), sender);
    }

    default boolean checkRatelimited(Ratelimit ratelimit, Sender sender) {
        boolean isRatelimited = isRatelimited(ratelimit);
        if (isRatelimited) {
            onRatelimit(sender, ratelimit);
        }
        return isRatelimited;
    }

    default Ratelimit getRatelimitFor(long id) {
        return getRatelimited().get(id);
    }

    Map<Long, Ratelimit> getRatelimited();

    long getCooldown();

    void onRatelimit(Sender e, Ratelimit ratelimit);
}

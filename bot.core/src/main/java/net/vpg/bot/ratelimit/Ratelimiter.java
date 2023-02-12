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

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface Ratelimiter {
    default void ratelimit(long id) {
        getRatelimited().put(id, new Ratelimit(id, getCooldown()));
    }

    default boolean isRatelimited(long id) {
        Ratelimit ratelimit = getRatelimit(id);
        return ratelimit != null && ratelimit.isRatelimited();
    }

    default boolean ifRatelimited(long id, Consumer<Ratelimit> action) {
        Ratelimit ratelimit = getRatelimit(id);
        boolean isRatelimited = ratelimit != null && ratelimit.isRatelimited();
        if (isRatelimited) {
            action.accept(ratelimit);
        }
        return isRatelimited;
    }

    default boolean checkRatelimited(long id, IReplyCallback callback) {
        return ifRatelimited(id, rl -> onRatelimit(callback, rl));
    }

    default boolean checkRatelimited(long id, Supplier<IReplyCallback> supplier) {
        return ifRatelimited(id, rl -> onRatelimit(supplier.get(), rl));
    }

    default boolean checkRatelimited(long id, RestAction<IReplyCallback> action) {
        return ifRatelimited(id, rl -> action.queue(sender -> onRatelimit(sender, rl)));
    }

    default Ratelimit getRatelimit(long id) {
        return getRatelimited().get(id);
    }

    Map<Long, Ratelimit> getRatelimited();

    long getCooldown();

    void onRatelimit(IReplyCallback callback, Ratelimit ratelimit);
}

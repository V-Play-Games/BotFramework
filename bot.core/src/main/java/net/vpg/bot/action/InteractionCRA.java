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
package net.vpg.bot.action;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.interactions.InteractionCallbackAction;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public abstract class InteractionCRA<T extends InteractionCallbackAction<InteractionHook>> extends AbstractCRA<T> {
    protected InteractionCRA(T action) {
        super(action);
    }

    @Override
    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure) {
        action.queue(hook -> hook.retrieveOriginal().queue(success, failure), failure);
    }

    @Override
    public Message complete(boolean shouldQueue) throws RateLimitedException {
        return action.complete(shouldQueue).retrieveOriginal().complete(shouldQueue);
    }

    @Nonnull
    @Override
    public CompletableFuture<Message> submit(boolean shouldQueue) {
        return action.submit(shouldQueue).thenApply(hook -> hook.retrieveOriginal().complete());
    }
}

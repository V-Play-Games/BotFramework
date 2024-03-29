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
package net.vpg.bot.event.handler;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.internal.utils.ClassWalker;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public class EventProcessor implements EventListener {
    private final Map<Class<?>, List<Consumer<GenericEvent>>> hooks = new HashMap<>();
    private EventHandler subject;

    public EventHandler getSubject() {
        return subject;
    }

    public EventProcessor setSubject(EventHandler subject) {
        this.subject = subject;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends GenericEvent> void addListener(Class<T> type, Consumer<T> consumer) {
        hooks.computeIfAbsent(type, x -> new ArrayList<>()).add((Consumer<GenericEvent>) consumer);
    }

    @Override
    public void onEvent(@Nonnull GenericEvent e) {
        if (subject != null) {
            subject.onEvent(e);
        }
        StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                ClassWalker.range(e.getClass(), GenericEvent.class).iterator(),
                Spliterator.ORDERED
            ),
            false)
            .map(hooks::get)
            .filter(Objects::nonNull)
            .flatMap(List::stream)
            .forEach(c -> c.accept(e));
    }
}

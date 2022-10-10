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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventProcessor implements EventListener {
    private final Map<Class<? extends GenericEvent>, ListenerHook> hooks = new HashMap<>();
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
        hooks.computeIfAbsent(type, x -> new ListenerHook()).addListener((Consumer<GenericEvent>) consumer);
    }

    @Override
    public void onEvent(@Nonnull GenericEvent e) {
        if (subject != null) {
            subject.onEvent(e);
        }
        ClassWalker.range(e.getClass(), GenericEvent.class).forEach(clazz -> {
                ListenerHook hook = hooks.get(clazz);
                if (hook != null)
                    hook.execute(e);
            }
        );
    }

    private static class ListenerHook {
        private final List<Consumer<GenericEvent>> listeners = new ArrayList<>();

        private void addListener(Consumer<GenericEvent> action) {
            listeners.add(action);
        }

        private void execute(GenericEvent event) {
            listeners.forEach(c -> c.accept(event));
        }
    }
}

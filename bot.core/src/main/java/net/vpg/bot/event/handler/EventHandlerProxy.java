package net.vpg.bot.event.handler;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.internal.utils.ClassWalker;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class EventHandlerProxy implements EventListener {
    private final Map<Class<? extends GenericEvent>, EventHook<? extends GenericEvent>> hooks = new HashMap<>();
    private EventHandler subject;

    public EventHandler getSubject() {
        return subject;
    }

    public void setSubject(EventHandler subject) {
        this.subject = subject;
    }

    public <T extends GenericEvent> void addSubscriber(String id, Class<T> type, Consumer<T> consumer) {
        if (hooks.values()
            .stream()
            .map(EventHook::getSubscribers)
            .map(Map::keySet)
            .flatMap(Set::stream)
            .anyMatch(_id -> _id.equals(id))) {
            throw new IllegalArgumentException("Subscription with id " + id + " already exists!");
        }
        hooks.computeIfAbsent(type, x -> new EventHook<>()).addSubscriber(id, consumer);
    }

    public void removeSubscriber(String id) {
        hooks.values().forEach(hook -> hook.removeSubscriber(id));
    }

    @Override
    public void onEvent(@Nonnull GenericEvent e) {
        subject.onEvent(e);
        ClassWalker.range(e.getClass(), GenericEvent.class).forEach(clazz ->
            {
                EventHook<? extends GenericEvent> hook = hooks.get(clazz);
                if (hook != null)
                    hook.execute(e);
            }
        );
    }

    private static class EventHook<T extends GenericEvent> {
        private final Map<String, Consumer<T>> subscribers = new HashMap<>();

        @SuppressWarnings("unchecked")
        private void addSubscriber(String id, Consumer<?> action) {
            subscribers.put(id, (Consumer<T>) action);
        }

        private void removeSubscriber(String id) {
            subscribers.remove(id);
        }

        private Map<String, Consumer<T>> getSubscribers() {
            return subscribers;
        }

        @SuppressWarnings("unchecked")
        private void execute(GenericEvent event) {
            subscribers.values().forEach(c -> c.accept((T) event));
        }
    }
}

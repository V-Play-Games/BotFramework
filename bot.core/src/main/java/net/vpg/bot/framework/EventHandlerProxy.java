package net.vpg.bot.framework;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import javax.annotation.Nonnull;

public class EventHandlerProxy implements EventListener {
    private EventHandler subject;

    public EventHandler getSubject() {
        return subject;
    }

    public void setSubject(EventHandler subject) {
        this.subject = subject;
    }

    @Override
    public void onEvent(@Nonnull GenericEvent event) {
        subject.onEvent(event);
    }
}

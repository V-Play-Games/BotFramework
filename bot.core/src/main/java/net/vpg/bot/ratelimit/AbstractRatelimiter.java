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

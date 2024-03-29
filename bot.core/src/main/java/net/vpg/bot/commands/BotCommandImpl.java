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
package net.vpg.bot.commands;

import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.ratelimit.Ratelimit;
import net.vpg.bot.ratelimit.Ratelimiter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public abstract class BotCommandImpl extends CommandDataImpl implements BotCommand, Ratelimiter {
    protected final Map<Long, Ratelimit> ratelimited = new HashMap<>();
    protected final Bot bot;
    protected long cooldown;
    protected Predicate<CommandReceivedEvent> checks = e -> true;

    public BotCommandImpl(Bot bot, String name, String description) {
        super(name, description);
        this.bot = bot;
    }

    @Override
    public void register() {
        bot.registerCommand(getName(), this);
    }

    @Override
    public Map<Long, Ratelimit> getRatelimited() {
        return ratelimited;
    }

    @Override
    public Bot getBot() {
        return bot;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public void setCooldown(long cooldown, TimeUnit cooldownUnit) {
        this.cooldown = cooldownUnit == null ? cooldown : cooldownUnit.toMillis(cooldown);
    }

    @Override
    public void run(CommandReceivedEvent e) {
        try {
            long id = e.getUser().getIdLong();
            if (checkRatelimited(id, e) || !checks.test(e))
                return;
            execute(e);
            ratelimit(id);
            bot.getPrimaryShard()
                .getRateLimitPool()
                .schedule(() -> ratelimited.remove(id), cooldown, TimeUnit.MILLISECONDS);
        } catch (Exception exc) {
            e.setTrouble(exc);
            exc.printStackTrace();
            e.reply("There was some trouble processing your request. Please contact the developer.")
                .setEphemeral(true)
                .queue();
        }
    }

    public abstract void execute(CommandReceivedEvent e) throws Exception;

    @Override
    public void addCheck(Predicate<CommandReceivedEvent> check) {
        checks = checks.and(check);
    }

    @Override
    public void onRatelimit(IReplyCallback callback, Ratelimit ratelimit) {
        if (!ratelimit.isInformed()) {
            callback.reply("You have to wait for **")
                .addContent(ratelimit.getCooldownString())
                .addContent("** before using this command again.")
                .setEphemeral(true)
                .queue();
            ratelimit.setInformed(true);
        }
    }

    @Override
    public String toString() {
        return "/" + name;
    }

    @Override
    public Map<Long, Ratelimit> getRateLimited() {
        return ratelimited;
    }
}

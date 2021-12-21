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

import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.vpg.bot.framework.Bot;
import net.vpg.bot.framework.Ratelimit;
import net.vpg.bot.framework.Ratelimiter;
import net.vpg.bot.framework.Sender;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class BotCommandImpl extends CommandData implements BotCommand, Ratelimiter {
    protected final Map<Long, Ratelimit> ratelimited = new HashMap<>();
    protected long cooldown;
    protected int minArgs;
    protected int maxArgs;
    protected Bot bot;
    protected List<String> aliases;
    protected boolean isRegistered;

    public BotCommandImpl(Bot bot, String name, String description, String... aliases) {
        super(name, description);
        this.bot = bot;
        this.aliases = new ArrayList<>();
        Collections.addAll(this.aliases, aliases);
    }

    @Override
    public void register() {
        if (!isRegistered) {
            bot.registerCommand(getName(), this);
            aliases.forEach(alias -> bot.registerCommand(alias, this));
        }
    }

    @Override
    public Map<Long, Ratelimit> getRatelimited() {
        return ratelimited;
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void removeAlias(String alias) {
        if (aliases.remove(alias))
            bot.removeCommand(alias);
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
        this.cooldown = (cooldownUnit == null ? TimeUnit.MILLISECONDS : cooldownUnit).toMillis(cooldown);
    }

    @Override
    public int getMaxArgs() {
        return maxArgs;
    }

    public void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }

    @Override
    public int getMinArgs() {
        return minArgs;
    }

    public void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }

    @Override
    public void run(CommandReceivedEvent e) {
        long aid = e.getUser().getIdLong();
        if (ifRatelimited(aid, rl -> onRatelimit(e, rl)) || !runChecks(e)) {
            return;
        }
        if (!e.isSlashCommand()) { // Slash Commands bypass arg checks
            int args = e.getArgs().size();
            if (minArgs > args || (maxArgs != 0 && args > maxArgs)) {
                onInsufficientArgs(e);
                return;
            }
        }
        try {
            if (e.isSlashCommand()) onSlashCommandRun(e);
            else onCommandRun(e);
            ratelimit(aid);
        } catch (Exception exc) {
            e.reportTrouble(exc);
            e.send("There was some trouble processing your request. Please contact the developer.")
                .queue(null, x -> e.getChannel()
                    .sendMessage("There was some trouble processing your request. Please contact the developer.")
                    .queue()
                );
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent e, String input) {
    }

    @Override
    public void finalizeCommand(Command c) {
    }

    @Override
    public void onRatelimit(Sender e, Ratelimit ratelimit) {
        if (!ratelimit.isInformed()) {
            e.send("You have to wait for **")
                .append(ratelimit.getCooldownString())
                .append("** before using this command again.")
                .setEphemeral(true)
                .queue();
            ratelimit.setInformed(true);
        }
    }

    @Override
    public void onHelpNeeded(CommandReceivedEvent e) {
    }

    @Override
    public String toString() {
        return bot.getPrefix() + name;
    }

    @Override
    public Map<Long, Ratelimit> getRateLimited() {
        return ratelimited;
    }

    @Override
    public CommandData toCommandData() {
        return this;
    }
}

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

import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import net.vpg.bot.action.Sender;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.Util;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;
import net.vpg.bot.ratelimit.Ratelimit;
import net.vpg.bot.ratelimit.Ratelimiter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class BotCommandImpl extends CommandDataImpl implements BotCommand, Ratelimiter {
    protected final Map<Long, Ratelimit> ratelimited = new HashMap<>();
    protected final Bot bot;
    protected final List<String> aliases;
    protected List<CommandPrivilege> defaultPrivileges = null;
    protected long cooldown;
    protected int minArgs;
    protected int maxArgs;

    public BotCommandImpl(Bot bot, String name, String description, String... aliases) {
        super(name, description);
        this.bot = bot;
        this.aliases = List.of(aliases);
        bot.getPrimaryShard().getRateLimitPool().scheduleWithFixedDelay(() -> ratelimited.forEach((id, rl) -> {
            if (!rl.isRatelimited()) {
                ratelimited.remove(id);
            }
        }), 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void register() {
        bot.registerCommand(getName(), this);
        aliases.forEach(alias -> bot.registerCommand(alias, this));
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
        try {
            long id = e.getUser().getIdLong();
            if (checkRatelimited(id, e) || !runChecks(e)) {
                return;
            }
            if (e instanceof TextCommandReceivedEvent) {
                TextCommandReceivedEvent text = (TextCommandReceivedEvent) e;
                int args = text.getArgs().size();
                if (minArgs > args || (maxArgs != 0 && args > maxArgs)) {
                    onInsufficientArgs(e);
                    return;
                }
                onTextCommandRun(text);
            } else {
                onSlashCommandRun((SlashCommandReceivedEvent) e);
            }
            ratelimit(id);
        } catch (Exception exc) {
            e.setTrouble(exc);
            exc.printStackTrace();
            if (!e.isReplySent()) {
                e.send("There was some trouble processing your request. Please contact the developer.")
                    .setEphemeral(true)
                    .queue();
            }
        }
    }

    @Override
    public void finalizeCommand(Command c) {
        if (getDefaultPrivileges() == null) return;
        Map<Long, CommandPrivilege> defaultPrivilegeMap = Util.group(defaultPrivileges, CommandPrivilege::getIdLong);
        c.getJDA().getGuildCache().forEach(guild -> c.retrievePrivileges(guild).queue(privileges -> {
            if (!defaultPrivilegeMap.equals(Util.group(privileges, CommandPrivilege::getIdLong))) {
                c.updatePrivileges(guild, defaultPrivileges).queue();
            }
        }));
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
    public String toString() {
        return bot.getPrefix() + name;
    }

    @Override
    public Map<Long, Ratelimit> getRateLimited() {
        return ratelimited;
    }

    @Override
    public SlashCommandData toCommandData() {
        return this;
    }

    @Override
    public List<CommandPrivilege> getDefaultPrivileges() {
        return defaultPrivileges;
    }

    public void setDefaultPrivileges(List<CommandPrivilege> defaultPrivileges) {
        this.defaultPrivileges = defaultPrivileges;
    }
}

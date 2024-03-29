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

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.ratelimit.Ratelimit;

import java.util.Map;
import java.util.function.Predicate;

public interface BotCommand extends SlashCommandData {
    void register();

    void run(CommandReceivedEvent e);

    void addCheck(Predicate<CommandReceivedEvent> check);

    Map<Long, Ratelimit> getRateLimited();

    Bot getBot();

    enum Type {
        TEXT, SLASH, HYBRID;

        public boolean isText() {
            return this == TEXT || isHybrid();
        }

        public boolean isSlash() {
            return this == SLASH || isHybrid();
        }

        public boolean isHybrid() {
            return this == HYBRID;
        }
    }
}

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

import net.dv8tion.jda.api.Permission;

public interface ModCommand extends BotCommand {
    Permission getRequiredPermission();

    default boolean runChecks(CommandReceivedEvent e) {
        if (!e.isFromGuild()) {
            e.send("This command only works in guilds").queue();
            return false;
        }
        if (!e.getMember().hasPermission(getRequiredPermission())) {
            e.send("You do not have the required permission to do that!").queue();
            return false;
        }
        return true;
    }
}

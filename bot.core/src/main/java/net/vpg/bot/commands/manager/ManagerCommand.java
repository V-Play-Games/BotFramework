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
package net.vpg.bot.commands.manager;

import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.commands.event.CommandReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

public interface ManagerCommand extends BotCommand {
    @Override
    default List<CommandPrivilege> getDefaultPrivileges() {
        return getBot().getManagers().stream().map(CommandPrivilege::enableUser).collect(Collectors.toList());
    }

    default boolean runChecks(CommandReceivedEvent e) {
        if (!getBot().isManager(e.getUser().getIdLong())) {
            e.send("You do not have the permission to do that!").queue();
            return false;
        }
        return true;
    }
}

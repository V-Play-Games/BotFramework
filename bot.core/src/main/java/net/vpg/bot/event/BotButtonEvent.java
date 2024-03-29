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
package net.vpg.bot.event;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.Util;

public class BotButtonEvent extends ButtonInteractionEvent {
    private final String method;
    private final String[] args;
    private final Bot bot;

    public BotButtonEvent(ButtonInteractionEvent e, Bot bot) {
        super(e.getJDA(), e.getResponseNumber(), e.getInteraction());
        this.bot = bot;
        this.method = Util.getMethod(getComponentId());
        this.args = Util.getArgs(getComponentId()).split(":");
    }

    public String getMethod() {
        return method;
    }

    public String[] getArgs() {
        return args;
    }

    public Bot getBot() {
        return bot;
    }

    public void execute() {
        bot.getButtonHandlers().get(method).handle(this);
    }

    public String getArg(int index) {
        return args[index];
    }
}

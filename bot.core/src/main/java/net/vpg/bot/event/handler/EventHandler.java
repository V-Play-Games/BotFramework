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
package net.vpg.bot.event.handler;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.vpg.bot.core.Bot;

public class EventHandler extends ListenerAdapter {
    protected final Bot bot;
    protected boolean closed;

    public EventHandler(Bot bot) {
        this.bot = bot;
    }

    public Bot getBot() {
        return bot;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
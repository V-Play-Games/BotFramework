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

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.vpg.bot.action.CommandReplyAction;
import net.vpg.bot.commands.BotCommand;

import java.util.List;

public class TextCommandReceivedEvent extends CommandReceivedEvent {
    private final Message message;
    private final String content;
    private final List<String> args;

    public TextCommandReceivedEvent(MessageReceivedEvent e, List<String> args, BotCommand command, String prefix) {
        super(e.getJDA(),
            e.getChannel(),
            e.getGuild(),
            e.getAuthor(),
            e.getMember(),
            command,
            e.getMessage().getTimeCreated(),
            prefix,
            () -> CommandReplyAction.reply(e.getMessage()));
        this.message = e.getMessage();
        this.content = message.getContentRaw();
        this.args = args;
    }

    public String getContent() {
        return content;
    }

    public Message getMessage() {
        return message;
    }

    public List<String> getArgs() {
        return args;
    }

    public List<String> getArgsFrom(int index) {
        return args.subList(index, args.size());
    }

    public String getArgsFrom(int index, String delimiter) {
        return String.join(delimiter, getArgsFrom(index));
    }

    public String getArg(int index) {
        return args.get(index);
    }

    @Override
    protected String getInput() {
        return content;
    }
}

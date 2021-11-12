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
package net.vpg.bot.framework;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.Interaction;
import net.vpg.bot.framework.commands.CommandReplyAction;

import javax.annotation.CheckReturnValue;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Sender {
    static Sender fromInteraction(Interaction interaction) {
        return fromFunction(interaction, CommandReplyAction::new);
    }

    static Sender fromMessage(Message message) {
        return fromFunction(message, CommandReplyAction::new);
    }

    static Sender fromChannel(MessageChannel tc) {
        return fromFunction(tc, CommandReplyAction::new);
    }

    static <T> Sender fromFunction(T t, Function<T, CommandReplyAction> converter) {
        return fromSupplier(() -> converter.apply(t));
    }

    static Sender fromSupplier(Supplier<CommandReplyAction> supplier) {
        return supplier::get;
    }

    @CheckReturnValue
    default CommandReplyAction send(String message)  {
        return deferSend().content(message);
    }

    @CheckReturnValue
    default CommandReplyAction sendEmbeds(MessageEmbed... embeds)  {
        return deferSend().setEmbeds(embeds);
    }

    @CheckReturnValue
    CommandReplyAction deferSend();
}

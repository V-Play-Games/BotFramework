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
package net.vpg.bot.action;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Sender {
    static Sender of(Message message) {
        return ofReply(message);
    }

    static Sender ofReply(Message message) {
        return of(message, CommandReplyAction::reply);
    }

    static Sender ofEdit(Message message) {
        return of(message, CommandReplyAction::edit);
    }

    static Sender of(Interaction interaction) {
        return of(interaction, CommandReplyAction::reply);
    }

    static Sender ofReply(Interaction interaction) {
        return of(interaction, CommandReplyAction::reply);
    }

    static Sender of(ComponentInteraction interaction) {
        return ofReply(interaction);
    }

    static Sender ofReply(ComponentInteraction interaction) {
        return of(interaction, CommandReplyAction::reply);
    }

    static Sender ofEdit(ComponentInteraction interaction) {
        return of(interaction, CommandReplyAction::edit);
    }

    static Sender of(MessageChannel tc) {
        return of(tc, CommandReplyAction::send);
    }

    static <T> Sender of(T t, Function<T, CommandReplyAction> converter) {
        return of(() -> converter.apply(t));
    }

    static Sender of(Supplier<CommandReplyAction> supplier) {
        return new Sender() {
            CommandReplyAction action;

            @Nonnull
            @Override
            public CommandReplyAction deferSend() {
                return action == null ? action = supplier.get() : action;
            }
        };
    }

    @Nonnull
    @CheckReturnValue
    default CommandReplyAction send(String message) {
        return deferSend().setContent(message);
    }

    @Nonnull
    @CheckReturnValue
    default CommandReplyAction sendEmbeds(MessageEmbed... embeds) {
        return deferSend().setEmbeds(embeds);
    }

    @Nonnull
    @CheckReturnValue
    CommandReplyAction deferSend();
}

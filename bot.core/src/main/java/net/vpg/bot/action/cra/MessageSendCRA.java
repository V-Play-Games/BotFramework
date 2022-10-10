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
package net.vpg.bot.action.cra;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MessageSendCRA extends AbstractCRA<Message, MessageCreateAction> {
    public MessageSendCRA(MessageCreateAction action) {
        super(action);
    }

    @Nonnull
    @Override
    public MessageSendCRA setNonce(@Nullable String nonce) {
        action.setNonce(nonce);
        return this;
    }

    @Nonnull
    @Override
    public MessageSendCRA setMessageReference(@Nullable String messageId) {
        action.setMessageReference(messageId);
        return this;
    }

    @Nonnull
    @Override
    public MessageSendCRA failOnInvalidReply(boolean fail) {
        action.failOnInvalidReply(fail);
        return this;
    }

    @Nonnull
    @Override
    public MessageSendCRA setStickers(@Nullable Collection<? extends StickerSnowflake> stickers) {
        action.setStickers(stickers);
        return this;
    }
}

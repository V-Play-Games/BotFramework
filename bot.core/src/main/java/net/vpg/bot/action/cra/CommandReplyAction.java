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
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.entities.sticker.Sticker;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.requests.FluentRestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.requests.restaction.MessageCreateActionImpl;
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyCallbackActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;

public interface CommandReplyAction<T> extends MessageCreateRequest<CommandReplyAction<T>>, FluentRestAction<T, CommandReplyAction<T>> {
    static CommandReplyAction<Message> send(MessageChannel tc) {
        return new MessageSendCRA(new MessageCreateActionImpl(tc));
    }

    static CommandReplyAction<Message> reply(Message message) {
        return new MessageSendCRA(new MessageCreateActionImpl(message.getChannel()).setMessageReference(message.getId()));
    }

    static CommandReplyAction<InteractionHook> reply(IReplyCallback callback) {
        return new InteractionReplyCRA(new ReplyCallbackActionImpl((InteractionHookImpl) callback.getHook()));
    }

    /**
     * Sets the default value for {@link #failOnInvalidReply(boolean)}
     *
     * <p>Default: <b>false</b>
     *
     * @param fail True, to throw a exception if the referenced message does not exist
     */
    static void setDefaultFailOnInvalidReply(boolean fail) {
        MessageCreateActionImpl.setDefaultFailOnInvalidReply(fail);
    }

    /**
     * Unique string/number used to identify messages using {@link Message#getNonce()} in events.
     * <br>This can be useful to handle round-trip messages.
     *
     * <p>Discord also uses the nonce to dedupe messages for users, but this is not currently supported for bots.
     * However, for future proofing, it is highly recommended to use a unique nonce for each message.
     *
     * @param nonce The nonce string to use
     * @return The same instance for chaining
     * @throws IllegalArgumentException If the provided nonce is longer than {@value Message#MAX_NONCE_LENGTH} characters
     * @see <a href="https://en.wikipedia.org/wiki/Cryptographic_nonce" target="_blank">Cryptographic Nonce - Wikipedia</a>
     */
    @Nonnull
    CommandReplyAction<T> setNonce(@Nullable String nonce);

    /**
     * Message reference used for a reply.
     * <br>The client will show this message as a reply to the target message.
     *
     * <p>You can only reply to messages from the same channel.
     * By default, this will mention the author of the target message, this can be disabled using {@link #mentionRepliedUser(boolean)}.
     *
     * <p>This also requires {@link net.dv8tion.jda.api.Permission#MESSAGE_HISTORY Permission.MESSAGE_HISTORY} in the channel.
     * If this permission is missing, you receive {@link net.dv8tion.jda.api.requests.ErrorResponse#REPLY_FAILED_MISSING_MESSAGE_HISTORY_PERM ErrorResponse.REPLY_FAILED_MISSING_MESSAGE_HISTORY_PERM}.
     *
     * <p>If the target message does not exist, this will result in {@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE ErrorResponse.UNKNOWN_MESSAGE}.
     * You can use {@link #failOnInvalidReply(boolean)} to allow unknown or deleted messages.
     *
     * @param messageId The target message id to reply to
     * @return The same instance for chaining
     * @throws IllegalArgumentException If the message id is not a valid snowflake
     */
    @Nonnull
    CommandReplyAction<T> setMessageReference(@Nullable String messageId);

    /**
     * Message reference used for a reply.
     * <br>The client will show this message as a reply to the target message.
     *
     * <p>You can only reply to messages from the same channel.
     * By default, this will mention the author of the target message, this can be disabled using {@link #mentionRepliedUser(boolean)}.
     *
     * <p>This also requires {@link net.dv8tion.jda.api.Permission#MESSAGE_HISTORY Permission.MESSAGE_HISTORY} in the channel.
     * If this permission is missing, you receive {@link net.dv8tion.jda.api.requests.ErrorResponse#REPLY_FAILED_MISSING_MESSAGE_HISTORY_PERM ErrorResponse.REPLY_FAILED_MISSING_MESSAGE_HISTORY_PERM}.
     *
     * <p>If the target message does not exist, this will result in {@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE ErrorResponse.UNKNOWN_MESSAGE}.
     * You can use {@link #failOnInvalidReply(boolean)} to allow unknown or deleted messages.
     *
     * @param messageId The target message id to reply to
     * @return The same instance for chaining
     */
    @Nonnull
    default CommandReplyAction<T> setMessageReference(long messageId) {
        return setMessageReference(Long.toUnsignedString(messageId));
    }

    /**
     * Message reference used for a reply.
     * <br>The client will show this message as a reply to the target message.
     *
     * <p>You can only reply to messages from the same channel.
     * By default, this will mention the author of the target message, this can be disabled using {@link #mentionRepliedUser(boolean)}.
     *
     * <p>This also requires {@link net.dv8tion.jda.api.Permission#MESSAGE_HISTORY Permission.MESSAGE_HISTORY} in the channel.
     * If this permission is missing, you receive {@link net.dv8tion.jda.api.requests.ErrorResponse#REPLY_FAILED_MISSING_MESSAGE_HISTORY_PERM ErrorResponse.REPLY_FAILED_MISSING_MESSAGE_HISTORY_PERM}.
     *
     * <p>If the target message does not exist, this will result in {@link net.dv8tion.jda.api.requests.ErrorResponse#UNKNOWN_MESSAGE ErrorResponse.UNKNOWN_MESSAGE}.
     * You can use {@link #failOnInvalidReply(boolean)} to allow unknown or deleted messages.
     *
     * @param message The target message to reply to
     * @return The same instance for chaining
     */
    @Nonnull
    default CommandReplyAction<T> setMessageReference(@Nullable Message message) {
        return setMessageReference(message == null ? null : message.getId());
    }

    /**
     * Whether to throw a exception if the referenced message does not exist, when replying to a message.
     * <br>This only matters in combination with {@link #setMessageReference(Message)} and {@link #setMessageReference(long)}!
     *
     * <p>This is false by default but can be configured using {@link #setDefaultFailOnInvalidReply(boolean)}!
     *
     * @param fail True, to throw a exception if the referenced message does not exist
     * @return Updated MessageCreateAction for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    CommandReplyAction<T> failOnInvalidReply(boolean fail);

    /**
     * Set the stickers to send alongside this message.
     * <br>This is not supported for message edits.
     *
     * @param stickers The stickers to send, or null to not send any stickers
     * @return Updated MessageCreateAction for chaining convenience
     * @throws IllegalStateException    If this request is a message edit request
     * @throws IllegalArgumentException <ul>
     *                                  <li>If any of the provided stickers is a {@link GuildSticker},
     *                                  which is either {@link GuildSticker#isAvailable() unavailable} or from a different guild.</li>
     *                                  <li>If the collection has more than {@value Message#MAX_STICKER_COUNT} stickers</li>
     *                                  <li>If a collection with null entries is provided</li>
     *                                  </ul>
     * @see Sticker#fromId(long)
     */
    @Nonnull
    @CheckReturnValue
    CommandReplyAction<T> setStickers(@Nullable Collection<? extends StickerSnowflake> stickers);

    /**
     * Set the stickers to send alongside this message.
     * <br>This is not supported for message edits.
     *
     * @param stickers The stickers to send, or null to not send any stickers
     * @return Updated MessageCreateAction for chaining convenience
     * @throws IllegalStateException    If this request is a message edit request
     * @throws IllegalArgumentException <ul>
     *                                  <li>If any of the provided stickers is a {@link GuildSticker},
     *                                  which is either {@link GuildSticker#isAvailable() unavailable} or from a different guild.</li>
     *                                  <li>If the collection has more than {@value Message#MAX_STICKER_COUNT} stickers</li>
     *                                  <li>If a collection with null entries is provided</li>
     *                                  </ul>
     * @see Sticker#fromId(long)
     */
    @Nonnull
    @CheckReturnValue
    default CommandReplyAction<T> setStickers(@Nullable StickerSnowflake... stickers) {
        if (stickers != null)
            Checks.noneNull(stickers, "Sticker");
        return setStickers(stickers == null ? null : Arrays.asList(stickers));
    }

    @Nonnull
    CommandReplyAction<T> closeResources();

    /**
     * Set whether this message should be visible to other users.
     * <br>When a message is ephemeral, it will only be visible to the user that used the interaction.
     *
     * <p>Ephemeral messages have some limitations and will be removed once the user restarts their client.
     * <br>Limitations:
     * <ul>
     *     <li>Cannot be deleted by the bot</li>
     *     <li>Cannot contain any files/attachments</li>
     *     <li>Cannot be reacted to</li>
     *     <li>Cannot be retrieved</li>
     * </ul>
     *
     * @param ephemeral True, if this message should be invisible for other users
     * @return The same reply action, for chaining convenience
     */
    @Nonnull
    @CheckReturnValue
    CommandReplyAction<T> setEphemeral(boolean ephemeral);
}

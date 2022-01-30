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
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.interactions.InteractionHookImpl;
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl;
import net.dv8tion.jda.internal.requests.restaction.interactions.MessageEditCallbackActionImpl;
import net.dv8tion.jda.internal.requests.restaction.interactions.ReplyCallbackActionImpl;
import net.dv8tion.jda.internal.utils.Checks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public interface CommandReplyAction extends RestAction<Message>, Appendable {
    static CommandReplyAction send(MessageChannel tc) {
        return new MessageCRA(new MessageActionImpl(tc.getJDA(), null, tc));
    }

    static CommandReplyAction reply(Message message) {
        return new MessageCRA(new MessageActionImpl(message.getJDA(), null, message.getChannel()));
    }

    static CommandReplyAction reply(IReplyCallback callback) {
        return new InteractionReplyCRA(new ReplyCallbackActionImpl((InteractionHookImpl) callback.getHook()));
    }

    static CommandReplyAction edit(Message message) {
        return new MessageCRA(new MessageActionImpl(message.getJDA(), message.getId(), message.getChannel()));
    }

    static CommandReplyAction edit(IMessageEditCallback callback) {
        return new InteractionEditCRA(new MessageEditCallbackActionImpl((InteractionHookImpl) callback.getHook()));
    }

    @Nonnull
    @Override
    CommandReplyAction setCheck(@Nullable BooleanSupplier checks);

    @Nonnull
    @Override
    default CommandReplyAction timeout(long timeout, @Nonnull TimeUnit unit) {
        return (CommandReplyAction) RestAction.super.timeout(timeout, unit);
    }

    @Nonnull
    @Override
    CommandReplyAction deadline(long timestamp);

    @Nonnull
    default CommandReplyAction setEmbeds(MessageEmbed... embeds) {
        Checks.noneNull(embeds, "MessageEmbed");
        return setEmbeds(Arrays.asList(embeds));
    }

    @Nonnull
    CommandReplyAction setEmbeds(Collection<? extends MessageEmbed> embeds);

    @Nonnull
    default CommandReplyAction setActionRow(ActionComponent... components) {
        return setActionRows(ActionRow.of(components));
    }

    @Nonnull
    default CommandReplyAction setActionRow(Collection<? extends ActionComponent> components) {
        return setActionRows(ActionRow.of(components));
    }

    @Nonnull
    default CommandReplyAction setActionRows(Collection<? extends ActionRow> rows) {
        Checks.noneNull(rows, "ActionRows");
        return setActionRows(rows.toArray(new ActionRow[0]));
    }

    @Nonnull
    CommandReplyAction setActionRows(ActionRow... rows);

    @Nonnull
    CommandReplyAction setTTS(boolean tts);

    @Nonnull
    CommandReplyAction setEphemeral(boolean ephemeral);

    @Nonnull
    CommandReplyAction referenceById(long messageId);

    @Nonnull
    default CommandReplyAction referenceById(String messageId) {
        return referenceById(MiscUtil.parseSnowflake(messageId));
    }

    @Nonnull
    default CommandReplyAction reference(Message message) {
        Checks.notNull(message, "Message");
        return referenceById(message.getIdLong());
    }

    @Nonnull
    CommandReplyAction mentionRepliedUser(boolean mention);

    @Nonnull
    CommandReplyAction failOnInvalidReply(boolean fail);

    @Nonnull
    @Override
    default CommandReplyAction append(CharSequence csq) {
        return append(csq, 0, csq.length());
    }

    @Nonnull
    @Override
    default CommandReplyAction append(CharSequence csq, int start, int end) {
        return setContent(getContent() + csq.subSequence(start, end));
    }

    @Nonnull
    @Override
    default CommandReplyAction append(char c) {
        return setContent(getContent() + c);
    }

    @Nonnull
    default CommandReplyAction appendFormat(String format, Object... args) {
        return append(String.format(format, args));
    }

    @Nonnull
    CommandReplyAction addFile(InputStream data, String name, AttachmentOption... options);

    @Nonnull
    default CommandReplyAction addFile(byte[] data, String name, AttachmentOption... options) {
        Checks.notNull(data, "Data");
        final long maxSize = getJDA().getSelfUser().getAllowedFileSize();
        Checks.check(data.length <= maxSize, "File may not exceed the maximum file length of %d bytes!", maxSize);
        return addFile(new ByteArrayInputStream(data), name, options);
    }

    @Nonnull
    default CommandReplyAction addFile(File file, AttachmentOption... options) {
        Checks.notNull(file, "File");
        return addFile(file, file.getName(), options);
    }

    @Nonnull
    CommandReplyAction addFile(File file, String name, AttachmentOption... options);

    @Nullable
    Consumer<CommandReplyAction> getTask();

    @Nonnull
    CommandReplyAction setTask(Consumer<CommandReplyAction> task);

    @Nonnull
    default CommandReplyAction appendTask(Consumer<CommandReplyAction> tasks) {
        return appendTask(tasks, Consumer::andThen);
    }

    @Nonnull
    default CommandReplyAction appendTask(Consumer<CommandReplyAction> tasks, BinaryOperator<Consumer<CommandReplyAction>> combiner) {
        return setTask(getTask() == null ? tasks : combiner.apply(getTask(), tasks));
    }

    @Nonnull
    String getContent();

    @Nonnull
    CommandReplyAction setContent(String content);
}

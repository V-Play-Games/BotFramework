/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.vpg.bot.framework.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.utils.AttachmentOption;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import net.dv8tion.jda.internal.requests.restaction.MessageActionImpl;
import okhttp3.RequestBody;
import org.apache.commons.collections4.map.CaseInsensitiveMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CommandReplyAction extends MessageActionImpl {
    private Consumer<CommandReplyAction> tasks;
    private int flags;

    public CommandReplyAction(Message message) {
        this(message, null);
    }

    public CommandReplyAction(Message message, Consumer<CommandReplyAction> tasks) {
        this(message.getJDA(), message.getId(), message.getChannel(), tasks);
    }

    public CommandReplyAction(MessageChannel tc) {
        this(tc, null);
    }

    public CommandReplyAction(MessageChannel tc, Consumer<CommandReplyAction> tasks) {
        this(tc.getJDA(), null, tc, tasks);
    }

    public CommandReplyAction(Interaction interaction) {
        this(interaction, null);
    }

    public CommandReplyAction(Interaction interaction, Consumer<CommandReplyAction> tasks) {
        this(interaction.getJDA(), null, interaction.getMessageChannel(), interaction.getHook(), tasks);
    }

    public CommandReplyAction(JDA jda, String messageId, MessageChannel channel, Consumer<CommandReplyAction> tasks) {
        super(jda, messageId, channel);
        this.tasks = tasks;
    }

    public CommandReplyAction(JDA jda, String messageId, MessageChannel channel, InteractionHook hook, Consumer<CommandReplyAction> tasks) {
        this(jda, messageId, channel, tasks);
        this.withHook(hook);
    }

    public void setTasks(Consumer<CommandReplyAction> tasks) {
        this.tasks = tasks;
    }

    public String getContent() {
        return content.toString();
    }

    @Override
    public CommandReplyAction withHook(InteractionHook hook) {
        return (CommandReplyAction) super.withHook(hook);
    }

    @Override
    public Route.CompiledRoute finalizeRoute() {
        return super.finalizeRoute();
    }

    @Nonnull
    @Override
    public CommandReplyAction timeout(long timeout, @Nonnull TimeUnit unit) {
        return (CommandReplyAction) super.timeout(timeout, unit);
    }

    @Nonnull
    @Override
    public CommandReplyAction deadline(long timestamp) {
        return (CommandReplyAction) super.deadline(timestamp);
    }

    @Nonnull
    @Override
    public MessageChannel getChannel() {
        return super.getChannel();
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public boolean isEdit() {
        return super.isEdit();
    }

    @Nonnull
    @Override
    public CommandReplyAction apply(Message message) {
        return (CommandReplyAction) super.apply(message);
    }

    @Nonnull
    @Override
    public CommandReplyAction referenceById(long messageId) {
        return (CommandReplyAction) super.referenceById(messageId);
    }

    @Nonnull
    @Override
    public CommandReplyAction failOnInvalidReply(boolean fail) {
        return (CommandReplyAction) super.failOnInvalidReply(fail);
    }

    @Nonnull
    @Override
    public CommandReplyAction tts(boolean isTTS) {
        return (CommandReplyAction) super.tts(isTTS);
    }

    @Nonnull
    @Override
    public CommandReplyAction reset() {
        return (CommandReplyAction) super.reset();
    }

    @Nonnull
    @Override
    public CommandReplyAction nonce(String nonce) {
        return (CommandReplyAction) super.nonce(nonce);
    }

    @Nonnull
    @Override
    public CommandReplyAction content(String content) {
        return (CommandReplyAction) super.content(content);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public CommandReplyAction embed(MessageEmbed embed) {
        return (CommandReplyAction) super.embed(embed);
    }

    @Nonnull
    @Override
    public CommandReplyAction setEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
        return (CommandReplyAction) super.setEmbeds(embeds);
    }

    @Nonnull
    @Override
    public CommandReplyAction append(CharSequence csq, int start, int end) {
        return (CommandReplyAction) super.append(csq, start, end);
    }

    @Nonnull
    @Override
    public CommandReplyAction append(char c) {
        return (CommandReplyAction) super.append(c);
    }

    @Nonnull
    @Override
    public CommandReplyAction addFile(@Nonnull InputStream data, @Nonnull String name, @Nonnull AttachmentOption... options) {
        return (CommandReplyAction) super.addFile(data, name, options);
    }

    @Nonnull
    @Override
    public CommandReplyAction addFile(@Nonnull File file, @Nonnull String name, @Nonnull AttachmentOption... options) {
        return (CommandReplyAction) super.addFile(file, name, options);
    }

    @Nonnull
    @Override
    public CommandReplyAction clearFiles() {
        return (CommandReplyAction) super.clearFiles();
    }

    @Nonnull
    @Override
    public CommandReplyAction clearFiles(@Nonnull BiConsumer<String, InputStream> finalizer) {
        return (CommandReplyAction) super.clearFiles(finalizer);
    }

    @Nonnull
    @Override
    public CommandReplyAction clearFiles(@Nonnull Consumer<InputStream> finalizer) {
        return (CommandReplyAction) super.clearFiles(finalizer);
    }

    @Nonnull
    @Override
    public CommandReplyAction retainFilesById(@Nonnull Collection<String> ids) {
        return (CommandReplyAction) super.retainFilesById(ids);
    }

    @Nonnull
    @Override
    public CommandReplyAction setActionRows(@Nonnull ActionRow... rows) {
        return (CommandReplyAction) super.setActionRows(rows);
    }

    @Nonnull
    @Override
    public CommandReplyAction override(boolean bool) {
        return (CommandReplyAction) super.override(bool);
    }

    @Nonnull
    @Override
    public CommandReplyAction mentionRepliedUser(boolean mention) {
        return (CommandReplyAction) super.mentionRepliedUser(mention);
    }

    @Nonnull
    @Override
    public CommandReplyAction allowedMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
        return (CommandReplyAction) super.allowedMentions(allowedMentions);
    }

    @Nonnull
    @Override
    public CommandReplyAction mention(@Nonnull IMentionable... mentions) {
        return (CommandReplyAction) super.mention(mentions);
    }

    @Nonnull
    @Override
    public CommandReplyAction mentionUsers(@Nonnull String... userIds) {
        return (CommandReplyAction) super.mentionUsers(userIds);
    }

    @Nonnull
    @Override
    public CommandReplyAction mentionRoles(@Nonnull String... roleIds) {
        return (CommandReplyAction) super.mentionRoles(roleIds);
    }

    @Override
    public RequestBody asMultipart() {
        return super.asMultipart();
    }

    @Override
    public RequestBody asJSON() {
        return super.asJSON();
    }

    @Override
    public DataObject getJSON() {
        return super.getJSON().put("flags", flags);
    }

    @Override
    public void checkFileAmount() {
        super.checkFileAmount();
    }

    @Override
    public void checkEdit() {
        super.checkEdit();
    }

    @Override
    public void checkPermission(Permission perm) {
        super.checkPermission(perm);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return super.hasPermission(perm);
    }

    @Override
    public RequestBody finalizeData() {
        return super.finalizeData();
    }

    @Override
    public void handleSuccess(Response response, Request<Message> request) {
        super.handleSuccess(response, request);
    }

    @Override
    public void finalize() {
        super.finalize();
    }

    @Nonnull
    @Override
    public CommandReplyAction referenceById(@Nonnull String messageId) {
        return (CommandReplyAction) super.referenceById(messageId);
    }

    @Nonnull
    @Override
    public CommandReplyAction reference(@Nonnull Message message) {
        return (CommandReplyAction) super.reference(message);
    }

    @Nonnull
    @Override
    public CommandReplyAction setEmbeds(@Nonnull MessageEmbed... embeds) {
        return (CommandReplyAction) super.setEmbeds(embeds);
    }

    @Nonnull
    @Override
    public CommandReplyAction append(@Nonnull CharSequence csq) {
        return (CommandReplyAction) super.append(csq);
    }

    @Nonnull
    @Override
    public CommandReplyAction appendFormat(@Nonnull String format, Object... args) {
        return (CommandReplyAction) super.appendFormat(format, args);
    }

    @Nonnull
    @Override
    public CommandReplyAction addFile(@Nonnull byte[] data, @Nonnull String name, @Nonnull AttachmentOption... options) {
        return (CommandReplyAction) super.addFile(data, name, options);
    }

    @Nonnull
    @Override
    public CommandReplyAction addFile(@Nonnull File file, @Nonnull AttachmentOption... options) {
        return (CommandReplyAction) super.addFile(file, options);
    }

    @Nonnull
    @Override
    public CommandReplyAction retainFilesById(@Nonnull String... ids) {
        return (CommandReplyAction) super.retainFilesById(ids);
    }

    @Nonnull
    @Override
    public CommandReplyAction retainFilesById(long... ids) {
        return (CommandReplyAction) super.retainFilesById(ids);
    }

    @Nonnull
    @Override
    public CommandReplyAction retainFiles(@Nonnull Collection<? extends Message.Attachment> attachments) {
        return (CommandReplyAction) super.retainFiles(attachments);
    }

    @Nonnull
    @Override
    public CommandReplyAction setActionRows(@Nonnull Collection<? extends ActionRow> rows) {
        return (CommandReplyAction) super.setActionRows(rows);
    }

    @Nonnull
    @Override
    public CommandReplyAction setActionRow(@Nonnull Collection<? extends Component> components) {
        return (CommandReplyAction) super.setActionRow(components);
    }

    @Nonnull
    @Override
    public CommandReplyAction setActionRow(@Nonnull Component... components) {
        return (CommandReplyAction) super.setActionRow(components);
    }

    @Nonnull
    public CommandReplyAction setEphemeral(boolean ephemeral)
    {
        if (ephemeral)
            this.flags |= 64;
        else
            this.flags &= ~64;
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction mention(@Nonnull Collection<? extends IMentionable> mentions) {
        return (CommandReplyAction) super.mention(mentions);
    }

    @Nonnull
    @Override
    public CommandReplyAction mentionUsers(@Nonnull long... userIds) {
        return (CommandReplyAction) super.mentionUsers(userIds);
    }

    @Nonnull
    @Override
    public CommandReplyAction mentionRoles(@Nonnull long... roleIds) {
        return (CommandReplyAction) super.mentionRoles(roleIds);
    }

    @Override
    public RestActionImpl<Message> priority() {
        return super.priority();
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return super.getJDA();
    }

    @Nullable
    @Override
    public BooleanSupplier getCheck() {
        return super.getCheck();
    }

    @Nonnull
    @Override
    public CommandReplyAction setCheck(BooleanSupplier checks) {
        return (CommandReplyAction) super.setCheck(checks);
    }

    @Nonnull
    @Override
    public CompletableFuture<Message> submit(boolean shouldQueue) {
        return super.submit(shouldQueue);
    }

    @Override
    public Message complete(boolean shouldQueue) throws RateLimitedException {
        return super.complete(shouldQueue);
    }

    @Override
    public CaseInsensitiveMap<String, String> finalizeHeaders() {
        return super.finalizeHeaders();
    }

    @Override
    public BooleanSupplier finalizeChecks() {
        return super.finalizeChecks();
    }

    @Override
    public RequestBody getRequestBody(DataObject object) {
        return super.getRequestBody(object);
    }

    @Override
    public RequestBody getRequestBody(DataArray array) {
        return super.getRequestBody(array);
    }

    @Override
    public void handleResponse(Response response, Request<Message> request) {
        super.handleResponse(response, request);
    }

    @Override
    public void queue(Consumer<? super Message> success, Consumer<? super Throwable> failure) {
        super.queue(success, failure);
        tasks.accept(this);
    }
}
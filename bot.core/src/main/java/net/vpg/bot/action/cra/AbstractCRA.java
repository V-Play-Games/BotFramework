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

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class AbstractCRA<T, R extends RestAction<T> & MessageCreateRequest<R>> implements CommandReplyAction<T> {
    protected final R action;
    protected String content;

    protected AbstractCRA(R action) {
        this.action = action;
    }

    @Nonnull
    @Override
    public JDA getJDA() {
        return action.getJDA();
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setCheck(@Nullable BooleanSupplier checks) {
        action.setCheck(checks);
        return this;
    }

    @Override
    public void queue(@Nullable Consumer<? super T> success, @Nullable Consumer<? super Throwable> failure) {
        action.queue(success, failure);
    }

    @Override
    public T complete(boolean shouldQueue) throws RateLimitedException {
        return action.complete();
    }

    @Nonnull
    @Override
    public CompletableFuture<T> submit(boolean shouldQueue) {
        return action.submit(shouldQueue);
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setContent(@Nullable String content) {
        action.setContent(content);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
        action.setEmbeds(embeds);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setComponents(@Nonnull Collection<? extends LayoutComponent> components) {
        action.setComponents(components);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setSuppressEmbeds(boolean suppress) {
        action.setSuppressEmbeds(suppress);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setFiles(@Nullable Collection<? extends FileUpload> files) {
        action.setFiles(files);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> mentionRepliedUser(boolean mention) {
        action.mentionRepliedUser(mention);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setAllowedMentions(@Nullable Collection<Message.MentionType> allowedMentions) {
        action.setAllowedMentions(allowedMentions);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> mention(@Nonnull Collection<? extends IMentionable> mentions) {
        action.mention(mentions);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> mentionUsers(@Nonnull Collection<String> userIds) {
        action.mentionUsers(userIds);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> mentionRoles(@Nonnull Collection<String> roleIds) {
        action.mentionRoles(roleIds);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> applyMessage(@Nonnull Message message) {
        action.applyMessage(message);
        return this;
    }

    @Nonnull
    @Override
    public String getContent() {
        return action.getContent();
    }

    @Nonnull
    @Override
    public List<MessageEmbed> getEmbeds() {
        return action.getEmbeds();
    }

    @Nonnull
    @Override
    public List<LayoutComponent> getComponents() {
        return action.getComponents();
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> addContent(@Nonnull String content) {
        action.addContent(content);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> addEmbeds(@Nonnull Collection<? extends MessageEmbed> embeds) {
        action.addEmbeds(embeds);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> addComponents(@Nonnull Collection<? extends LayoutComponent> components) {
        action.addComponents(components);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> addFiles(@Nonnull Collection<? extends FileUpload> files) {
        action.addFiles(files);
        return this;
    }

    @Nonnull
    @Override
    public List<FileUpload> getAttachments() {
        return action.getAttachments();
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setTTS(boolean tts) {
        action.setTTS(tts);
        return this;
    }

    @Override
    public boolean isSuppressEmbeds() {
        return action.isSuppressEmbeds();
    }

    @Nonnull
    @Override
    public Set<String> getMentionedUsers() {
        return action.getMentionedUsers();
    }

    @Nonnull
    @Override
    public Set<String> getMentionedRoles() {
        return action.getMentionedRoles();
    }

    @Nonnull
    @Override
    public EnumSet<Message.MentionType> getAllowedMentions() {
        return action.getAllowedMentions();
    }

    @Override
    public boolean isMentionRepliedUser() {
        return action.isMentionRepliedUser();
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setNonce(@Nullable String nonce) {
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setMessageReference(@Nullable String messageId) {
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> failOnInvalidReply(boolean fail) {
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setStickers(@Nullable Collection<? extends StickerSnowflake> stickers) {
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> closeResources() {
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction<T> setEphemeral(boolean ephemeral) {
        return this;
    }
}

package net.vpg.bot.commands.action;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MessageCRA extends AbstractCRA<MessageAction> {
    public MessageCRA(MessageAction action) {
        super(action);
    }

    @Override
    protected void setContentImpl(String content) {
        action.content(content);
    }

    @Nonnull
    @Override
    public CommandReplyAction setEmbeds(Collection<? extends MessageEmbed> embeds) {
        action.setEmbeds(embeds);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction setActionRows(ActionRow... rows) {
        action.setActionRows(rows);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction setTTS(boolean tts) {
        action.tts(tts);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction setEphemeral(boolean ephemeral) {
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction referenceById(long messageId) {
        action.referenceById(messageId);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction mentionRepliedUser(boolean mention) {
        action.mentionRepliedUser(mention);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction failOnInvalidReply(boolean fail) {
        action.failOnInvalidReply(fail);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction addFile(InputStream data, String name, AttachmentOption... options) {
        action.addFile(data, name, options);
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction addFile(File file, String name, AttachmentOption... options) {
        action.addFile(file, name, options);
        return this;
    }

    @Override
    public void queue(@Nullable Consumer<? super Message> success, @Nullable Consumer<? super Throwable> failure) {
        action.queue(success, failure);
    }

    @Override
    public Message complete(boolean shouldQueue) throws RateLimitedException {
        return action.complete(shouldQueue);
    }

    @Nonnull
    @Override
    public CompletableFuture<Message> submit(boolean shouldQueue) {
        return action.submit(shouldQueue);
    }
}

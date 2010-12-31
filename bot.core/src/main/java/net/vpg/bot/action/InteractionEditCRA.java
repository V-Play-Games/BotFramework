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

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.AttachmentOption;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class InteractionEditCRA extends InteractionCRA<MessageEditCallbackAction> {
    public InteractionEditCRA(MessageEditCallbackAction action) {
        super(action);
    }

    @Override
    protected void setContentImpl(String content) {
        action.setContent(content);
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
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction mentionRepliedUser(boolean mention) {
        return this;
    }

    @Nonnull
    @Override
    public CommandReplyAction failOnInvalidReply(boolean fail) {
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
}

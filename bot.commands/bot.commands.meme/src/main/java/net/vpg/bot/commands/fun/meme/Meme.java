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

package net.vpg.bot.commands.fun.meme;

import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.api.utils.data.SerializableData;

import javax.annotation.Nonnull;

public class Meme implements SerializableData {
    private final String postLink;
    private final String subreddit;
    private final String title;
    private final String url;
    private final boolean nsfw;
    private final boolean spoiler;
    private final String author;
    private final int ups;

    public Meme(DataObject data) {
        this.postLink = data.getString("postLink");
        this.subreddit = data.getString("subreddit");
        this.title = data.getString("title");
        this.url = data.getString("url");
        this.author = data.getString("author");
        this.ups = data.getInt("ups");
        this.nsfw = data.getBoolean("nsfw");
        this.spoiler = data.getBoolean("spoiler");
    }

    @Nonnull
    @Override
    public DataObject toData() {
        return DataObject.empty();
    }

    public String getPostLink() {
        return postLink;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public boolean isSpoiler() {
        return spoiler;
    }

    public String getAuthor() {
        return author;
    }

    public int getUps() {
        return ups;
    }
}

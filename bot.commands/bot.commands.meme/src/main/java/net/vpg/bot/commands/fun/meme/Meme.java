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
    public final String postLink;
    public final String subreddit;
    public final String title;
    public final String url;
    public final boolean nsfw;
    public final boolean spoiler;
    public final String author;
    public final int ups;

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

    public String toJSONString() {
        return "{\"postLink\":\"" + postLink +
            "\",\"subreddit\":\" " + subreddit +
            "\",\"title\":\"" + title +
            "\",\"url\":\"" + url +
            "\",\"nsfw\":" + nsfw +
            ",\"spoiler\":" + spoiler +
            ",\"author\":\"" + author +
            "\",\"ups\":" + ups + "}";
    }

    @Nonnull
    @Override
    public DataObject toData() {
        return DataObject.empty();
    }
}

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

import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Connection {
    public final static String MEME_URL_ENDPOINT = "https://meme-api.herokuapp.com/gimme/";
    private final OkHttpClient client;

    public Connection(OkHttpClient client) {
        this.client = client;
    }

    public Meme getMeme() throws IOException {
        return getMeme("");
    }

    public Meme getMeme(String subReddit) throws IOException {
        return getMemes(0, subReddit).get(0);
    }

    public List<Meme> getMemes(int amount) throws IOException {
        return getMemes(amount, "");
    }

    public List<Meme> getMemes(int amount, String subReddit) throws IOException {
        String url = MEME_URL_ENDPOINT + ("".equals(subReddit) ? "" : subReddit + "/") + (amount < 2 ? "" : amount);
        DataObject data = DataObject.fromJson(requestData(url));
        if (data.hasKey("memes"))
            return data.getArray("memes")
                .stream(DataArray::getObject)
                .map(Meme::new)
                .collect(Collectors.toList());
        else
            return Collections.singletonList(new Meme(data));
    }

    private String requestData(String url) throws IOException {
        Response response = client.newCall(new Request.Builder().url(url).build()).execute();
        return response.body().string();
    }
}

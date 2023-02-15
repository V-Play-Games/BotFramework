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

import net.vpg.rawf.api.RestApi;
import net.vpg.rawf.api.requests.RestAction;
import net.vpg.rawf.api.utils.data.DataArray;
import net.vpg.rawf.internal.requests.RestActionImpl;
import net.vpg.rawf.internal.requests.Route;
import net.vpg.rawf.internal.utils.config.AuthorizationConfig;
import net.vpg.rawf.internal.utils.config.ConnectionConfig;
import okhttp3.OkHttpClient;

import java.util.List;
import java.util.stream.Collectors;

public class MemeApi extends RestApi {
    public final static String MEME_API_BASE_URL = "https://meme-api.com/";

    public MemeApi(OkHttpClient client) {
        super(new AuthorizationConfig(""),
            new ConnectionConfig(client == null ? new OkHttpClient() : client, MEME_API_BASE_URL, ""));
    }

    public RestAction<Meme> getMeme() {
        return new RestActionImpl<>(this,
            MemeApiRoute.RANDOM_MEME_SINGLE.compile(),
            (response, request) -> new Meme(response.getObject())
        );
    }

    public RestAction<Meme> getMeme(String subReddit) {
        return new RestActionImpl<>(this,
            MemeApiRoute.RANDOM_MEME_SUBREDDIT_SINGLE.compile(subReddit == null ? "" : subReddit),
            (response, request) -> new Meme(response.getObject())
        );
    }

    public RestAction<List<Meme>> getMemes(int amount) {
        return new RestActionImpl<>(this,
            MemeApiRoute.RANDOM_MEME_MULTIPLE.compile(String.valueOf(amount)),
            (response, request) -> response.getArray()
                .stream(DataArray::getObject)
                .map(Meme::new)
                .collect(Collectors.toList())
        );
    }

    public RestAction<List<Meme>> getMemes(String subReddit, int amount) {
        return new RestActionImpl<>(this,
            MemeApiRoute.RANDOM_MEME_SUBREDDIT_MULTIPLE.compile(subReddit, String.valueOf(amount)),
            (response, request) -> response.getArray()
                .stream(DataArray::getObject)
                .map(Meme::new)
                .collect(Collectors.toList())
        );
    }

    public static class MemeApiRoute {
        public static final Route RANDOM_MEME_SINGLE = Route.get("gimme", false);
        public static final Route RANDOM_MEME_MULTIPLE = Route.get("gimme/{count}", false);
        public static final Route RANDOM_MEME_SUBREDDIT_SINGLE = Route.get("gimme/{subreddit}", false);
        public static final Route RANDOM_MEME_SUBREDDIT_MULTIPLE = Route.get("gimme/{subreddit}/{count}", false);
    }
}

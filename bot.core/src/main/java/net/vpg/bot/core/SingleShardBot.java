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
package net.vpg.bot.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.data.DataObject;

import javax.security.auth.login.LoginException;
import java.util.List;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class SingleShardBot extends Bot {
    protected final JDA jda;

    SingleShardBot(BotBuilder builder) throws LoginException {
        super(builder);
        this.jda = (builder.light
            ? JDABuilder.createLight(token, builder.getIntents())
            : JDABuilder.createDefault(token, builder.getIntents()))
            .addEventListeners(processor)
            .setActivity(Activity.watching("My Loading"))
            .build();
    }

    @Override
    public Guild getResourceServer() {
        return jda.getGuildById(resourceServerId);
    }

    @Override
    public ShardManager getShardManager() {
        return null;
    }

    @Override
    public JDA getPrimaryShard() {
        return jda;
    }

    @Override
    protected void loadLoggers() {
        Guild resources = getResourceServer();
        if (resources == null) return;
        Category category = resources.getCategoryById(logCategoryId);
        if (category == null) return;
        List<TextChannel> channels = resources.getTextChannelsByName("shard-0", true);
        if (channels.isEmpty()) {
            category.createTextChannel("shard-0").queue(tc -> loggers.put(0, tc.getIdLong()));
        } else {
            loggers.put(0, channels.get(0).getIdLong());
        }
        List<TextChannel> syncChannels = resources.getTextChannelsByName("sync", true);
        if (syncChannels.isEmpty()) {
            category.createTextChannel("sync").queue(tc -> loggers.put(-1, tc.getIdLong()));
        } else {
            loggers.put(-1, syncChannels.get(0).getIdLong());
        }

    }

    @Override
    protected void setDefaultActivity() {
        jda.getPresence().setActivity(Activity.playing(String.format("with %d people in %d servers",
            jda.getGuildCache().stream().mapToInt(Guild::getMemberCount).sum(), jda.getGuildCache().size())));
    }
}

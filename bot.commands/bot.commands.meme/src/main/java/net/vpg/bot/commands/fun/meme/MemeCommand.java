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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.attribute.IAgeRestrictedChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.Check;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.CommandReceivedEvent;

import java.util.concurrent.TimeUnit;

public class MemeCommand extends BotCommandImpl {
    protected final MemeApi api;

    public MemeCommand(Bot bot) {
        super(bot, "meme", "Pulls a random meme from Reddit");
        api = new MemeApi(bot.getPrimaryShard().getHttpClient());
        addOption(OptionType.STRING, "subreddit", "The subreddit to pull a meme from");
        setCooldown(10, TimeUnit.SECONDS);
        addCheck(Check.requiresGuild());
    }

    @Override
    public void execute(CommandReceivedEvent e) {
        api.getMeme(e.getString("subreddit")).queue(meme -> {
            if (meme.isNsfw()) {
                MessageChannelUnion channel = e.getChannel();
                GuildChannel gc = channel.getType().isThread()
                    ? channel.asThreadChannel().getParentChannel()
                    : channel.asGuildMessageChannel();
                if (!(gc instanceof IAgeRestrictedChannel) || !((IAgeRestrictedChannel) gc).isNSFW()) {
                    e.reply("Encountered an NSFW meme which cannot be sent because the channel is not marked as age restricted.").queue();
                    return;
                }
            }
            e.replyEmbeds(new EmbedBuilder()
                .setTitle(meme.getTitle(), meme.getPostLink())
                .setDescription("Meme by u/" + meme.getAuthor() + " in r/" + meme.getSubreddit())
                .setImage(meme.getUrl())
                .setFooter(meme.getUps() + " Upvotes").build()).queue();
        });
    }
}

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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.event.CommandReceivedEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;
import net.vpg.bot.core.Bot;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MemeCommand extends BotCommandImpl {
    private final Connection connection = new Connection();

    public MemeCommand(Bot bot) {
        super(bot, "meme", "Pulls a random meme from Reddit");
        addOption(OptionType.STRING, "subreddit", "The subreddit to pull a meme from");
        setCooldown(10, TimeUnit.SECONDS);
        setMaxArgs(1);
    }

    @Override
    public boolean runChecks(CommandReceivedEvent e) {
        if (!e.isFromGuild()) {
            e.send("Sorry, but this command cannot be used in DMs.").queue();
            return false;
        }
        return true;
    }

    @Override
    public void onTextCommandRun(TextCommandReceivedEvent e) throws IOException {
        execute(e, e.getArgs().size() == 1 ? e.getArg(0) : "");
    }

    @Override
    public void onSlashCommandRun(SlashCommandReceivedEvent e) throws Exception {
        execute(e, e.getString("subreddit"));
    }

    public void execute(CommandReceivedEvent e, String subreddit) throws IOException {
        Meme meme = connection.getMeme(subreddit);
        if (meme.nsfw && !e.getTextChannel().isNSFW()) return;
        e.sendEmbeds(new EmbedBuilder()
            .setTitle(meme.title, meme.postLink)
            .setDescription("Meme by u/" + meme.author + " in r/" + meme.subreddit)
            .setImage(meme.url)
            .setFooter(meme.ups + " Upvotes").build()).queue();
    }
}

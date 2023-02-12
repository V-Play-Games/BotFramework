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
package net.vpg.bot.commands.fun.guess;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.vpg.bot.core.Util;
import net.vpg.bot.entities.GuessPokemon;
import net.vpg.bot.event.CommandReceivedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GuessGame {
    public static final Random RANDOM = new Random();
    public static final Map<String, GuessGame> games = new HashMap<>();
    private final String id;
    private final GuessPokemon pokemon;
    private final String text;
    private final ScheduledFuture<?> timeout;
    private boolean closed;
    private Message message;

    public GuessGame(CommandReceivedEvent e) {
        this.id = e.getUser().getId();
        this.pokemon = GuessPokemon.get(String.valueOf(RANDOM.nextInt(GuessPokemon.CACHE.values().size()) + 1));
        this.text = Util.getRandom(pokemon.getFlavorTexts(), RANDOM);
        this.timeout = e.getJDA().getRateLimitPool().schedule(() -> timeout(e.getChannel()), 30, TimeUnit.SECONDS);
        games.put(id, this);
    }

    public static GuessGame get(String key) {
        return games.get(key);
    }

    public GuessGame setMessage(Message message) {
        this.message = message;
        return this;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect(String guess) {
        return pokemon.getName().equalsIgnoreCase(guess);
    }

    public String getUserId() {
        return id;
    }

    public GuessPokemon getPokemon() {
        return pokemon;
    }

    public void cleanup() {
        if (closed) return;
        closed = true;
        games.remove(id);
        message.editMessageComponents().queue();
    }

    public void win(IReplyCallback callback) {
        cleanup();
        timeout.cancel(true);
        callback.replyEmbeds(getEmbed("Correct Answer!")).queue();
    }

    public void forfeit(IReplyCallback callback) {
        cleanup();
        timeout.cancel(true);
        callback.replyEmbeds(getEmbed("You forfeit the game!")).queue();
    }

    public void timeout(MessageChannel channel) {
        cleanup();
        channel.sendMessageEmbeds(getEmbed("Timeout!")).queue();
    }

    private MessageEmbed getEmbed(String reason) {
        return new EmbedBuilder()
            .setDescription("The answer was **" + pokemon.getName() + "**!")
            .setThumbnail(pokemon.getSprite())
            .setTitle(reason)
            .build();
    }
}

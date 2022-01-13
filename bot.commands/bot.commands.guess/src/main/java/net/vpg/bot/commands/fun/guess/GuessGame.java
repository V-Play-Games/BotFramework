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
import net.vpg.bot.commands.event.CommandReceivedEvent;
import net.vpg.bot.entities.GuessPokemon;
import net.vpg.bot.framework.Sender;
import net.vpg.bot.framework.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GuessGame {
    public static final Random random = new Random();
    public static final Map<String, GuessGame> games = new HashMap<>();
    public static final int TIMEOUT = 0;
    public static final int FORFEIT = 1;
    public static final int WON = 2;
    private final String id;
    private final GuessPokemon pokemon;
    private final String text;
    private final ScheduledFuture<?> timeout;
    private boolean closed;
    private Message message;

    public GuessGame(CommandReceivedEvent e) {
        this.id = e.getUser().getId();
        this.pokemon = GuessPokemon.get(String.valueOf(random.nextInt(GuessPokemon.CACHE.size()) + 1));
        this.text = Util.getRandom(pokemon.getFlavorTexts(), random);
        this.timeout = e.getJDA().getRateLimitPool()
            .schedule(() -> close(Sender.of(e.getChannel()), TIMEOUT), 30, TimeUnit.SECONDS);
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

    public void close(Sender sender, int reason) {
        if (closed) return;
        closed = true;
        games.remove(id);
        if (reason != TIMEOUT) {
            timeout.cancel(true);
        }
        message.editMessageComponents().queue();
        String toSend;
        switch (reason) {
            case TIMEOUT:
                toSend = "You took too long to reply!";
                break;
            case FORFEIT:
                toSend = "You forfeit the game!";
                break;
            case WON:
                toSend = "Correct Answer!";
                break;
            default:
                return;
        }
        sender.deferSend().setActionRows().setEmbeds(getEmbed(toSend)).queue();
    }

    public MessageEmbed getEmbed(String title) {
        return getEmbed().setTitle(title).build();
    }

    public EmbedBuilder getEmbed() {
        return new EmbedBuilder().setDescription("The answer was **" + pokemon.getName() + "**!").setThumbnail(pokemon.getSprite());
    }
}

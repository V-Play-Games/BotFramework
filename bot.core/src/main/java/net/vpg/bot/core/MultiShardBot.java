package net.vpg.bot.core;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;

import javax.security.auth.login.LoginException;
import java.util.List;

class MultiShardBot extends Bot {
    protected final ShardManager shardManager;

    public MultiShardBot(BotBuilder builder) throws LoginException {
        super(builder);
        this.shardManager = (builder.light
            ? DefaultShardManagerBuilder.createLight(token)
            : DefaultShardManagerBuilder.createDefault(token))
            .enableIntents(GatewayIntent.getIntents(builder.intents))
            .addEventListeners(processor)
            .setShardsTotal(shardsTotal)
            .setActivity(Activity.watching("My Loading"))
            .build();
    }

    @Override
    public Guild getResourceServer() {
        return shardManager.getGuildById(resourceServerId);
    }

    @Override
    public ShardManager getShardManager() {
        return shardManager;
    }

    @Override
    public JDA getPrimaryShard() {
        return shardManager.getShardById(0);
    }

    @Override
    protected void loadLoggers() {
        Guild resources = getResourceServer();
        if (resources == null) return;
        Category category = resources.getCategoryById(logCategoryId);
        if (category == null) return;
        shardManager.getShardCache()
            .stream()
            .map(JDA::getShardInfo)
            .map(JDA.ShardInfo::getShardId)
            .forEach(id -> {
                List<TextChannel> channels = resources.getTextChannelsByName("shard-" + id, true);
                if (channels.isEmpty()) {
                    category.createTextChannel("shard-" + id).queue(tc -> loggers.put(id, tc.getIdLong()));
                } else {
                    loggers.put(id, channels.get(0).getIdLong());
                }
            });
        List<TextChannel> channels = resources.getTextChannelsByName("sync", true);
        if (channels.isEmpty()) {
            category.createTextChannel("sync").queue(tc -> loggers.put(-1, tc.getIdLong()));
        } else {
            loggers.put(-1, channels.get(0).getIdLong());
        }
    }

    @Override
    protected void setDefaultActivity() {
        SnowflakeCacheView<Guild> guildCache = shardManager.getGuildCache();
        shardManager.setActivity(Activity.playing(String.format("with %d people in %d servers", guildCache.stream().mapToInt(Guild::getMemberCount).sum(), guildCache.size())));
    }
}

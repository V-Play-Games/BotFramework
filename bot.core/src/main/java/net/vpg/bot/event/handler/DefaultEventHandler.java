package net.vpg.bot.event.handler;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.core.Bot;
import net.vpg.bot.core.Util;
import net.vpg.bot.event.BotButtonEvent;
import net.vpg.bot.event.SlashCommandReceivedEvent;
import net.vpg.bot.event.TextCommandReceivedEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class DefaultEventHandler extends EventHandler {
    private final AtomicInteger shardsInit = new AtomicInteger();
    private Pattern selfMentionPattern;
    private boolean closed;

    public DefaultEventHandler(Bot bot) {
        super(bot);
    }

    public Pattern getSelfMentionPattern() {
        return selfMentionPattern == null
            ? selfMentionPattern = Pattern.compile("<@!?" + bot.getPrimaryShard().getSelfUser().getIdLong() + ">")
            : selfMentionPattern;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent e) {
        if (closed) {
            if (e.getMessage().getContentRaw().equalsIgnoreCase(bot.getPrefix() + "activate")
                && bot.isManager(e.getAuthor().getIdLong())) {
                closed = false;
                e.getChannel().sendMessage("Thanks for activating me again!").queue();
            }
            return;
        }
        if (e.getAuthor().isBot() || (e.isFromGuild() && !e.getGuildChannel().canTalk())) {
            return;
        }
        String prefix = Util.getPrefix(e, bot);
        Message message = e.getMessage();
        String content = message.getContentRaw();
        String[] args = Util.SPACE.split(content);
        int argLen = args.length;
        if (content.regionMatches(true, 0, prefix, 0, prefix.length())) {
            boolean spaceAfterPrefix = args[0].length() == prefix.length();
            String command;
            int firstArg;
            if (spaceAfterPrefix) {
                if (argLen <= 1) return;
                int i = 1;
                while (args[i].isBlank()) i++;
                command = args[i];
                firstArg = i + 1;
            } else {
                command = args[0].substring(prefix.length());
                firstArg = 1;
            }
            BotCommand botCommand = bot.getCommands().get(command);
            if (botCommand != null) {
                List<String> finalArgs = firstArg == argLen
                    ? Collections.emptyList()
                    : Arrays.asList(args).subList(firstArg, argLen);
                botCommand.run(new TextCommandReceivedEvent(e, finalArgs, botCommand, prefix));
            }
        } else if (getSelfMentionPattern().matcher(content).find()) {
            e.getChannel().sendMessage("Prefix: " + prefix).queue();
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        if (closed) {
            if (e.getName().equalsIgnoreCase("activate") && bot.isManager(e.getUser().getIdLong())) {
                closed = false;
                e.getChannel().sendMessage("Thanks for activating me again!").queue();
            }
        } else {
            BotCommand command = bot.getCommands().get(e.getName());
            if (command != null) {
                command.run(new SlashCommandReceivedEvent(e, command));
            }
        }
    }

    @Override
    public void onException(@Nonnull ExceptionEvent e) {
        e.getCause().printStackTrace();
    }

    @Override
    public void onReady(@Nonnull ReadyEvent e) {
        try {
            // init on last shard only
            if (shardsInit.incrementAndGet() == e.getJDA().getShardInfo().getShardTotal())
                bot.load();
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent e)  {
        new BotButtonEvent(e, bot).execute();
    }
}

package net.vpg.bot.commands.general;

import net.vpg.bot.commands.BotCommandImpl;
import net.vpg.bot.commands.CommandReceivedEvent;
import net.vpg.bot.framework.Bot;

public class HelpCommand extends BotCommandImpl {
    public HelpCommand(Bot bot) {
        super(bot, "help", "Get help for this bot");
    }

    @Override
    public void onCommandRun(CommandReceivedEvent e) throws Exception {

    }

    @Override
    public void onSlashCommandRun(CommandReceivedEvent e) throws Exception {

    }
}

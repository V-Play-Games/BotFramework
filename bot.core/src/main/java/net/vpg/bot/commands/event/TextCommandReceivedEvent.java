package net.vpg.bot.commands.event;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.vpg.bot.commands.BotCommand;
import net.vpg.bot.commands.action.CommandReplyAction;

import java.util.List;

public class TextCommandReceivedEvent extends CommandReceivedEvent {
    private final Message message;
    private final String content;
    private final List<String> args;

    public TextCommandReceivedEvent(MessageReceivedEvent e, List<String> args, BotCommand command, String prefix) {
        super(e.getJDA(),
            e.getChannel(),
            e.getGuild(),
            e.getAuthor(),
            e.getMember(),
            command,
            e.getMessage().getTimeCreated(),
            prefix,
            () -> CommandReplyAction.reply(e.getMessage()));
        this.message = e.getMessage();
        this.content = message.getContentRaw();
        this.args = args;
    }

    public String getContent() {
        return content;
    }

    public Message getMessage() {
        return message;
    }

    public List<String> getArgs() {
        return args;
    }

    public List<String> getArgsFrom(int index) {
        return args.subList(index, args.size());
    }

    public String getArgsFrom(int index, String delimiter) {
        return String.join(delimiter, getArgsFrom(index));
    }

    public String getArg(int index) {
        return args.get(index);
    }

    @Override
    protected String getInput() {
        return content;
    }
}

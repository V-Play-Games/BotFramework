package net.vpg.bot.commands;

import net.dv8tion.jda.api.Permission;
import net.vpg.bot.core.Bot;
import net.vpg.bot.event.CommandReceivedEvent;

import java.util.function.Predicate;

public class Check {
    public static Predicate<CommandReceivedEvent> requiresManager(Bot bot) {
        return e -> {
            if (!bot.isManager(e.getUser().getIdLong())) {
                e.reply("You do not have the permission to do that!").queue();
                return false;
            }
            return true;
        };
    }

    @SuppressWarnings("ConstantConditions")
    public static Predicate<CommandReceivedEvent> requiresPermission(Permission permission) {
        return requiresGuild().and(e -> {
            if (!e.getMember().hasPermission(permission)) {
                e.reply("You do not have the required permission to do that!").setEphemeral(true).queue();
                return false;
            }
            return true;
        });
    }

    public static Predicate<CommandReceivedEvent> requiresAdmin() {
        return requiresPermission(Permission.ADMINISTRATOR);
    }

    public static Predicate<CommandReceivedEvent> requiresGuild() {
        return e -> {
            if (!e.isFromGuild()) {
                e.reply("This command only works in guilds!").queue();
                return false;
            }
            return true;
        };
    }

    public static Predicate<CommandReceivedEvent> requiresDM() {
        return e -> {
            if (e.isFromGuild()) {
                e.reply("This command only works in Private Channels!").queue();
                return false;
            }
            return true;
        };
    }
}

package net.lordofthecraft.omniscience.command;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.command.result.CommandResult;
import net.lordofthecraft.omniscience.command.result.UseResult;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface OmniSubCommand {

    UseResult canRun(CommandSender sender);

    String getCommand();

    ImmutableList<String> getAliases();

    String getUsage();

    String getDescription();

    CommandResult run(CommandSender sender, ArrayList<String> args);

    default boolean isCommand(String command) {
        return command.equalsIgnoreCase(getCommand()) || getAliases().contains(command);
    }

    default UseResult hasPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) {
            return UseResult.SUCCESS;
        } else {
            return UseResult.NO_PERMISSION;
        }
    }
}

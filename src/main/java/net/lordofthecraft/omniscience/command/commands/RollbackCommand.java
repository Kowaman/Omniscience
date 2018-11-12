package net.lordofthecraft.omniscience.command.commands;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.command.OmniSubCommand;
import net.lordofthecraft.omniscience.command.result.CommandResult;
import net.lordofthecraft.omniscience.command.result.UseResult;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class RollbackCommand implements OmniSubCommand {

    @Override
    public UseResult canRun(CommandSender sender) {
        return hasPermission(sender, "omniscience.commands.rollback");
    }

    @Override
    public String getCommand() {
        return "rollback";
    }

    @Override
    public ImmutableList<String> getAliases() {
        return ImmutableList.of("rb", "roll", "restore");
    }

    @Override
    public String getUsage() {
        return GREEN + "<Lookup Params>";
    }

    @Override
    public String getDescription() {
        return "Rollback a set of changes based on the Parameters Provided";
    }

    @Override
    public CommandResult run(CommandSender sender, ArrayList<String> args) {
        //TODO rollback
        if (args.isEmpty()) {
            return CommandResult.failure(RED + "Error: " + GRAY + "Please specify search arguments.");
        }
        return CommandResult.success();
    }
}

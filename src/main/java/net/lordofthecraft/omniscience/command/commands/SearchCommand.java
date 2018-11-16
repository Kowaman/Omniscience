package net.lordofthecraft.omniscience.command.commands;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.command.OmniSubCommand;
import net.lordofthecraft.omniscience.command.result.CommandResult;
import net.lordofthecraft.omniscience.command.result.UseResult;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class SearchCommand implements OmniSubCommand {

    @Override
    public UseResult canRun(CommandSender sender) {
        return hasPermission(sender, "omniscience.commands.search");
    }

    @Override
    public String getCommand() {
        return "search";
    }

    @Override
    public ImmutableList<String> getAliases() {
        return ImmutableList.of("s", "sc", "lookup", "l");
    }

    @Override
    public String getUsage() {
        return GREEN + "<Lookup Params>";
    }

    @Override
    public String getDescription() {
        return "Search Data Records based on the parameters provided.";
    }

    @Override
    public CommandResult run(CommandSender sender, IOmniscience core, ArrayList<String> args) {
        if (args.isEmpty()) {
            return CommandResult.failure(RED + "Error: " + GRAY + "Please specify search arguments.");
        }
        Query query = new Query();
        //TODO parse parameters
        core.submitQuery(sender, query);
        return CommandResult.success();
    }
}

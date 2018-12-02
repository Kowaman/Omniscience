package net.lordofthecraft.omniscience.command.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.lordofthecraft.omniscience.command.result.CommandResult;
import net.lordofthecraft.omniscience.command.result.UseResult;
import net.lordofthecraft.omniscience.command.util.SearchParameterHelper;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RollbackCommand extends SimpleCommand {

    public RollbackCommand() {
        super(ImmutableList.of("rb", "roll", "restore"));
    }

    @Override
    public UseResult canRun(CommandSender sender) {
        return hasPermission(sender, "omniscience.commands.rollback");
    }

    @Override
    public String getCommand() {
        return "rollback";
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
    public CommandResult run(CommandSender sender, IOmniscience core, String[] args) {
        //TODO rollback
        if (args.length == 0) {
            return CommandResult.failure(RED + "Error: " + GRAY + "Please specify search arguments.");
        }
        return CommandResult.success();
    }

    @Override
    public void buildLiteralArgumentBuilder(LiteralArgumentBuilder<Object> builder) {
        builder.then(RequiredArgumentBuilder.argument("search-parameters", StringArgumentType.greedyString()));
    }

    @Override
    public List<String> getCommandSuggestions(String partial) {
        return SearchParameterHelper.suggestParameterCompletion(partial);
    }
}

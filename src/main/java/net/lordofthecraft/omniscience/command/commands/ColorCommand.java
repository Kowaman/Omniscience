package net.lordofthecraft.omniscience.command.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.lordofthecraft.omniscience.OmniConfig;
import net.lordofthecraft.omniscience.command.result.CommandResult;
import net.lordofthecraft.omniscience.command.result.UseResult;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ColorCommand extends SimpleCommand {

    public ColorCommand() {
        super(ImmutableList.of());
    }

    @Override
    public UseResult canRun(CommandSender sender) {
        return UseResult.SUCCESS;
    }

    @Override
    public String getCommand() {
        return "c";
    }

    @Override
    public String getUsage() {
        return LIGHT_PURPLE + "<colors>";
    }

    @Override
    public String getDescription() {
        return "change colors";
    }

    @Override
    public CommandResult run(CommandSender sender, IOmniscience core, String[] args) {
        if (args[0].equalsIgnoreCase("p")) {
            OmniConfig.INSTANCE.setPrimary(ChatColor.valueOf(args[1]));
        } else if (args[0].equalsIgnoreCase("s")) {
            OmniConfig.INSTANCE.setSecondary(ChatColor.valueOf(args[1]));
        }
        return CommandResult.success();
    }

    @Override
    public void buildLiteralArgumentBuilder(LiteralArgumentBuilder<Object> builder) {

    }

    @Override
    public List<String> getCommandSuggestions(String partial) {
        return null;
    }
}

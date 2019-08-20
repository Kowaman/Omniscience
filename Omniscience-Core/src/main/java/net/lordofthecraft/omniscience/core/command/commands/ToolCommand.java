package net.lordofthecraft.omniscience.core.command.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.lordofthecraft.omniscience.api.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.core.OmniConfig;
import net.lordofthecraft.omniscience.core.Omniscience;
import net.lordofthecraft.omniscience.core.command.result.CommandResult;
import net.lordofthecraft.omniscience.core.command.result.UseResult;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ToolCommand extends SimpleCommand {

    public ToolCommand() {
        super(ImmutableList.of("t", "inspect"));
    }

    @Override
    public UseResult canRun(CommandSender sender) {
        return sender instanceof Player ? hasPermission(sender, "omniscience.commands.tool") : UseResult.NO_COMMAND_SENDER;
    }

    @Override
    public String getCommand() {
        return "tool";
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Turn on or off the Omniscience search tool";
    }

    @Override
    public CommandResult run(CommandSender sender, IOmniscience core, String[] args) {
        if (sender instanceof Player) {
            Player pl = (Player) sender;

            if (Omniscience.hasActiveWand(pl)) {
                if (!pl.getInventory().contains(OmniConfig.INSTANCE.getWandMaterial())) {
                    pl.getInventory().addItem(new ItemStack(OmniConfig.INSTANCE.getWandMaterial()));
                    pl.sendMessage(GREEN + "Added the Omniscience data tool to your inventory. Happy Searching.");
                } else {
                    Omniscience.wandDeactivateFor(pl);
                    pl.sendMessage(GREEN + "Successfully deactivated the Omniscience Data Tool");
                }
            } else {
                Omniscience.wandActivateFor(pl);
                if (!pl.getInventory().contains(OmniConfig.INSTANCE.getWandMaterial())) {
                    pl.getInventory().addItem(new ItemStack(OmniConfig.INSTANCE.getWandMaterial()));
                    pl.sendMessage(GREEN + "Added the Omniscience data tool to your inventory. Happy Searching.");
                } else {
                    pl.sendMessage(GREEN + "Activated the Omniscience Data Tool " + GRAY + "(" + OmniConfig.INSTANCE.getWandMaterial().name() + ")");
                }
            }
        }
        return CommandResult.success();
    }

    @Override
    public void buildLiteralArgumentBuilder(LiteralArgumentBuilder<Object> builder) {
        // NO:OP
    }

    @Override
    public List<String> getCommandSuggestions(String partial) {
        return null;
    }
}

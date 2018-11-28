package net.lordofthecraft.omniscience.command.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OmniTeleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Error: This command can only be run by players.");
            return true;
        }
        // /omnitele world x y z
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Something went wrong trying to teleport you!");
            return true;
        }
        UUID worldId = UUID.fromString(args[0]);
        World world = Bukkit.getWorld(worldId);
        if (world == null) {
            sender.sendMessage(ChatColor.RED + "World not found");
            return true;
        }
        try {
            Location newLocation = new Location(world, Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]));
            ((Player) sender).teleport(newLocation);
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + "An unexpected error occurred while running this command. Are the coordinates right?");
        }
        return true;
    }
}

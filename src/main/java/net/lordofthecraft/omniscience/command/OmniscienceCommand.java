package net.lordofthecraft.omniscience.command;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.lucko.commodore.Commodore;
import net.lordofthecraft.omniscience.command.commands.PageCommand;
import net.lordofthecraft.omniscience.command.commands.RollbackCommand;
import net.lordofthecraft.omniscience.command.commands.SearchCommand;
import net.lordofthecraft.omniscience.command.commands.ToolCommand;
import net.lordofthecraft.omniscience.command.result.CommandResult;
import net.lordofthecraft.omniscience.command.result.UseResult;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OmniscienceCommand implements CommandExecutor {

    final static ImmutableSet<OmniSubCommand> subCommandSet;

    static {
        subCommandSet = ImmutableSet.of(
                new ToolCommand(),
                new SearchCommand(),
                new RollbackCommand(),
                new PageCommand()
        );
    }

    public OmniscienceCommand(IOmniscience omniscience) {
        this.omniscience = omniscience;
    }

    private final IOmniscience omniscience;

    public static void registerCompletions(Commodore commodore, PluginCommand command) {
        LiteralArgumentBuilder<Object> builder = LiteralArgumentBuilder.literal("omniscience");
        subCommandSet.forEach(cmd -> {
            LiteralArgumentBuilder<Object> subBuilder = LiteralArgumentBuilder.literal(cmd.getCommand());
            cmd.buildLiteralArgumentBuilder(subBuilder);
            builder.then(subBuilder);
        });
        commodore.register(command, builder);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length < 1 || isHelpArg(args[0])) {
            return sendHelp(commandSender, label);
        }
        Optional<OmniSubCommand> cOptional = subCommandSet.stream()
                .filter(cmd -> cmd.isCommand(args[0].toLowerCase()))
                .findFirst();
        if (cOptional.isPresent()) {
            OmniSubCommand subCommand = cOptional.get();
            String[] subArgs = new String[args.length - 1];
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
            UseResult result = subCommand.canRun(commandSender);
            if (result == UseResult.SUCCESS) {
                CommandResult cmdResult = subCommand.run(commandSender, omniscience, subArgs);
                if (!cmdResult.wasSuccessful()) {
                    commandSender.sendMessage(ChatColor.RED + cmdResult.getReason());
                }
                return true;
            } else {
                return sendError(commandSender, result);
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "Error: The command " + args[0] + " was not found.");
            return sendHelp(commandSender, label);
        }
    }

    private boolean isHelpArg(String arg) {
        return arg.equalsIgnoreCase("help") || arg.equalsIgnoreCase("h") || arg.equalsIgnoreCase("?");
    }

    private boolean sendHelp(CommandSender sender, String label) {
        List<OmniSubCommand> runnableSubCommands = subCommandSet.stream()
                .filter(cmd -> cmd.canRun(sender) == UseResult.SUCCESS)
                .collect(Collectors.toList());
        sender.sendMessage(ChatColor.DARK_AQUA + " -======= Omniscience =======-");
        sender.sendMessage(ChatColor.ITALIC + "" + ChatColor.GRAY + "For Powerful Searching");
        runnableSubCommands.forEach(cmd ->
                sender.sendMessage(colorAndReset(ChatColor.YELLOW, "/" + label)
                        + " " + colorAndReset(ChatColor.GOLD, cmd.getCommand())
                        + " " + colorAndReset(ChatColor.GOLD, cmd.getUsage())
                        + ChatColor.GOLD + ": " + ChatColor.GRAY + cmd.getDescription()));
        return true;
    }

    private boolean sendError(CommandSender sender, UseResult result) {
        switch (result) {
            case NO_COMMAND_SENDER:
                sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "This command cannot be run by non-players");
                break;
            case NO_PLAYER_SENDER:
                sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "This command cannot be run by players");
                break;
            case NO_PERMISSION:
                sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "You do not have permission to run this command");
                break;
            case OTHER_ERROR:
                sender.sendMessage(ChatColor.RED + "Error: " + ChatColor.GRAY + "Something went wrong during command execution.");
                break;
        }
        return true;
    }

    private String colorAndReset(ChatColor color, String string) {
        return color + string + ChatColor.RESET;
    }
}

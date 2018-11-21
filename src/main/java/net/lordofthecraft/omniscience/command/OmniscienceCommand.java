package net.lordofthecraft.omniscience.command;

import com.google.common.collect.ImmutableSet;
import net.lordofthecraft.omniscience.command.commands.RollbackCommand;
import net.lordofthecraft.omniscience.command.commands.SearchCommand;
import net.lordofthecraft.omniscience.command.result.CommandResult;
import net.lordofthecraft.omniscience.command.result.UseResult;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class OmniscienceCommand implements CommandExecutor {

    private final static ImmutableSet<OmniSubCommand> subCommandSet;

    static {
        subCommandSet = ImmutableSet.of(
                new SearchCommand(),
                new RollbackCommand()
        );
    }

    private final IOmniscience omniscience;
    private final Executor executorService;

    public OmniscienceCommand(IOmniscience omniscience, Executor executorService) {
        this.omniscience = omniscience;
        this.executorService = executorService;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length < 1 || isHelpArg(args[0])) {
            return sendHelp(commandSender);
        }
        Optional<OmniSubCommand> cOptional = subCommandSet.stream()
                .filter(cmd -> cmd.isCommand(label))
                .findFirst();
        if (cOptional.isPresent()) {
            OmniSubCommand subCommand = cOptional.get();
            String[] subArgs = new String[args.length - 2];
            System.arraycopy(args, 1, subArgs, 0, args.length - 1);
            UseResult result = subCommand.canRun(commandSender);
            if (result == UseResult.SUCCESS) {
                //Here we ship off the command to run async so that we don't block the current thread.
                executorService.execute(() -> {
                    CommandResult cmdResult = subCommand.run(commandSender, omniscience, subArgs);
                    if (!cmdResult.wasSuccessful()) {
                        commandSender.sendMessage(cmdResult.getReason());
                    }
                });
                return true;
            } else {
                return sendError(commandSender, result);
            }
        } else {
            return sendHelp(commandSender);
        }
    }

    private boolean isHelpArg(String arg) {
        return arg.equalsIgnoreCase("help") || arg.equalsIgnoreCase("h") || arg.equalsIgnoreCase("?");
    }

    private boolean sendHelp(CommandSender sender) {
        List<OmniSubCommand> runnableSubCommands = subCommandSet.stream()
                .filter(cmd -> cmd.canRun(sender) == UseResult.SUCCESS)
                .collect(Collectors.toList());
        sender.sendMessage(ChatColor.DARK_AQUA + " -======= Omniscience =======-");
        sender.sendMessage(ChatColor.ITALIC + "" + ChatColor.GRAY + "For Powerful Searching");
        runnableSubCommands.forEach(cmd ->
                sender.sendMessage(colorAndReset(cmd.getCommand())
                        + " " + colorAndReset(cmd.getUsage())
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

    private String colorAndReset(String string) {
        return ChatColor.GOLD + string + ChatColor.RESET;
    }
}

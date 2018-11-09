package net.lordofthecraft.omniscience.command;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.command.commands.SearchCommand;
import net.lordofthecraft.omniscience.command.result.CommandResult;
import net.lordofthecraft.omniscience.command.result.UseResult;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class OmniscienceCommand implements CommandExecutor {

    private final static ImmutableSet<OmniSubCommand> subCommandSet;

    static {
        subCommandSet = ImmutableSet.of(
                new SearchCommand()
        );
    }

    private final MongoConnectionHandler connectionHandler;


    public OmniscienceCommand(MongoConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return true;
        }
        Optional<OmniSubCommand> cOptional = subCommandSet.stream().filter(cmd -> cmd.isCommand(label)).findFirst();
        if (cOptional.isPresent()) {
            OmniSubCommand subCommand = cOptional.get();
            ArrayList<String> argsList = Lists.newArrayList();
            argsList.addAll(Arrays.asList(args).subList(1, args.length));
            UseResult result = subCommand.canRun(commandSender);
            if (result == UseResult.SUCCESS) {
                CommandResult cmdResult = subCommand.run(commandSender, argsList);
                if (!cmdResult.wasSuccessful()) {
                    commandSender.sendMessage(cmdResult.getReason());
                }
                return true;
            } else {

                return true;
            }
        } else {
            //TODO send help
        }
        return true;
    }
}

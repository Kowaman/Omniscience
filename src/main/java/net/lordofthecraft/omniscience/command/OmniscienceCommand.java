package net.lordofthecraft.omniscience.command;

import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class OmniscienceCommand implements CommandExecutor {

    private final MongoConnectionHandler connectionHandler;

    public OmniscienceCommand(MongoConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return true;
    }
}

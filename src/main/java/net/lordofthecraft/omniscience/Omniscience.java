package net.lordofthecraft.omniscience;

import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.api.parameter.EventParameter;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;
import net.lordofthecraft.omniscience.command.OmniscienceCommand;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Omniscience extends JavaPlugin implements IOmniscience {

    private static Omniscience INSTANCE;

    private List<ParameterHandler> parameterHandlerList = Lists.newArrayList();

    private MongoConnectionHandler connectionHandler;

    public static IOmniscience getInstance() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        INSTANCE = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        saveDefaultConfig();
        this.connectionHandler = MongoConnectionHandler.createHandler(getConfig());

        registerParameters();

        registerCommands(this.connectionHandler);
    }

    private void registerCommands(MongoConnectionHandler connectionHandler) {
        getCommand("omniscience").setExecutor(new OmniscienceCommand(connectionHandler));
    }

    private void registerParameters() {
        parameterHandlerList.add(new EventParameter());
    }
}

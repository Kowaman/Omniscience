package net.lordofthecraft.omniscience;

import com.google.common.collect.Lists;
import net.lordofthecraft.omniscience.api.parameter.EventParameter;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;
import net.lordofthecraft.omniscience.command.OmniscienceCommand;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;

import java.util.List;

final class OmniCore implements IOmniscience {

    private List<ParameterHandler> parameterHandlerList = Lists.newArrayList();

    private MongoConnectionHandler connectionHandler;

    OmniCore() {
    }

    void onEnable(Omniscience omniscience) {
        omniscience.saveDefaultConfig();
        this.connectionHandler = MongoConnectionHandler.createHandler(omniscience.getConfig());

        registerParameters();

        registerCommands(this.connectionHandler, omniscience);
    }

    void onLoad(Omniscience omniscience) {

    }

    void onDisable(Omniscience omniscience) {

    }

    private void registerCommands(MongoConnectionHandler connectionHandler, Omniscience omniscience) {
        omniscience.getCommand("omniscience").setExecutor(new OmniscienceCommand(connectionHandler));
    }

    private void registerParameters() {
        parameterHandlerList.add(new EventParameter());
    }
}

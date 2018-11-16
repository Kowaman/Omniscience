package net.lordofthecraft.omniscience;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.parameter.EventParameter;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;
import net.lordofthecraft.omniscience.api.query.Query;
import net.lordofthecraft.omniscience.api.query.QueryRunner;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.command.OmniscienceCommand;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class OmniCore implements IOmniscience {

    private List<ParameterHandler> parameterHandlerList = Lists.newArrayList();
    private Map<String, Class<? extends DataEntry>> eventMap = Maps.newHashMap();
    private Map<String, QuerySession> querySessions = Maps.newHashMap();
    private ExecutorService queryService;

    private MongoConnectionHandler connectionHandler;

    OmniCore() {
    }

    void onEnable(Omniscience omniscience) {
        omniscience.saveDefaultConfig();
        this.connectionHandler = MongoConnectionHandler.createHandler(omniscience.getConfig());
        this.queryService = Executors.newCachedThreadPool();

        registerParameters();

        registerCommands(omniscience);
    }

    void onLoad(Omniscience omniscience) {

    }

    void onDisable(Omniscience omniscience) {

    }

    private void registerCommands(Omniscience omniscience) {
        omniscience.getCommand("omniscience").setExecutor(new OmniscienceCommand(this));
    }

    private void registerParameters() {
        parameterHandlerList.add(new EventParameter());
    }

    public void registerParameter(ParameterHandler handler) {
        parameterHandlerList.add(handler);
    }

    public void registerEvent(String name, Class<? extends DataEntry> clazz) {
        eventMap.put(name, clazz);
    }

    @Override
    public Class<? extends DataEntry> getEventClass(String name) {
        return eventMap.get(name);
    }

    @Override
    public void submitQuery(CommandSender sender, Query query) {
        QuerySession session;
        if (querySessions.containsKey(sender.getName())) {
            session = querySessions.get(sender.getName());
        } else {
            session = new QuerySession();
            querySessions.put(sender.getName(), session);
        }
        queryService.submit(new QueryRunner(sender, session, query));
    }
}

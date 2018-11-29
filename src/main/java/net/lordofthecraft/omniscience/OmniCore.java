package net.lordofthecraft.omniscience;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.lordofthecraft.omniscience.api.entry.BlockEntry;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.entry.EntryQueueRunner;
import net.lordofthecraft.omniscience.api.flag.FlagExtended;
import net.lordofthecraft.omniscience.api.flag.FlagHandler;
import net.lordofthecraft.omniscience.api.flag.FlagNoGroup;
import net.lordofthecraft.omniscience.api.flag.FlagOrder;
import net.lordofthecraft.omniscience.api.parameter.*;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.command.OmniscienceCommand;
import net.lordofthecraft.omniscience.command.util.OmniTeleCommand;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.listener.BlockChangeListener;
import net.lordofthecraft.omniscience.listener.ChatListener;
import net.lordofthecraft.omniscience.listener.CraftBookSignListener;
import net.lordofthecraft.omniscience.listener.ItemListener;
import net.lordofthecraft.omniscience.mongo.MongoConnectionHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

final class OmniCore implements IOmniscience {

    private List<ParameterHandler> parameterHandlerList = Lists.newArrayList();
    private Map<String, Class<? extends DataEntry>> eventMap = Maps.newHashMap();
    private Map<String, QuerySession> querySessions = Maps.newHashMap();
    private List<FlagHandler> flagHandlerList = Lists.newArrayList();
    private ExecutorService queryService;

    private MongoConnectionHandler connectionHandler;

    OmniCore() {
    }

    void onEnable(Omniscience omniscience, BukkitScheduler scheduler) {
        omniscience.saveDefaultConfig();
        OmniConfig.INSTANCE.setup(omniscience.getConfig());
        this.connectionHandler = MongoConnectionHandler.createHandler(omniscience.getConfig());
        this.queryService = Executors.newCachedThreadPool();

        registerEventWrapperClasses();
        registerParameters();
        registerFlags();

        registerCommands(omniscience);
        registerEventHandlers(omniscience);

        scheduler.runTaskTimerAsynchronously(omniscience,
                new EntryQueueRunner(),
                20,
                20);

        if (omniscience.getConfig().getBoolean("integration.fastAsyncWorldEdit")
                && Bukkit.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {

        }

        omniscience.getLogger().log(Level.INFO, "Omniscience is Awake. None can escape.");
    }

    void onLoad(Omniscience omniscience) {
    }

    void onDisable(Omniscience omniscience) {

    }

    private void registerCommands(Omniscience omniscience) {
        omniscience.getCommand("omniscience").setExecutor(new OmniscienceCommand(this, queryService));
        //A simple command that will do what we expect every single time. Used for teleporting to locations that could be in different worlds. Shouldn't be, but could be.
        omniscience.getCommand("omnitele").setExecutor(new OmniTeleCommand());
    }

    private void registerEventWrapperClasses() {
        registerEvent("break", BlockEntry.class);
        registerEvent("place", BlockEntry.class);
        registerEvent("grow", BlockEntry.class);
        registerEvent("form", BlockEntry.class);
    }

    private void registerEventHandlers(Omniscience plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(new BlockChangeListener(), plugin);
        pm.registerEvents(new ItemListener(), plugin);
        pm.registerEvents(new ChatListener(), plugin);
        if (plugin.getConfig().getBoolean("integration.craftBookSigns")
                && Bukkit.getServer().getPluginManager().isPluginEnabled("CraftBook")) {
            pm.registerEvents(new CraftBookSignListener(), plugin);
        }
    }

    private void registerParameters() {
        parameterHandlerList.add(new EventParameter());
        parameterHandlerList.add(new PlayerParameter());
        parameterHandlerList.add(new MessageParameter());
        parameterHandlerList.add(new RadiusParameter());
        parameterHandlerList.add(new TimeParameter());
        parameterHandlerList.add(new CauseParameter());
        parameterHandlerList.add(new BlockParameter());
    }

    private void registerFlags() {
        flagHandlerList.add(new FlagExtended());
        flagHandlerList.add(new FlagNoGroup());
        flagHandlerList.add(new FlagOrder());

    }

    public void registerParameter(ParameterHandler handler) {
        parameterHandlerList.add(handler);
    }

    public void registerEvent(String name, Class<? extends DataEntry> clazz) {
        eventMap.put(name, clazz);
    }

    public MongoConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    Optional<ParameterHandler> getParameterHandler(String key) {
        return parameterHandlerList.stream().filter(ph -> ph.canHandle(key)).findFirst();
    }

    @Override
    public Optional<Class<? extends DataEntry>> getEventClass(String name) {
        return Optional.ofNullable(eventMap.get(name));
    }

    Optional<FlagHandler> getFlagHandler(String key) {
        return flagHandlerList.stream().filter(flagHandler -> flagHandler.handles(key)).findFirst();
    }
}

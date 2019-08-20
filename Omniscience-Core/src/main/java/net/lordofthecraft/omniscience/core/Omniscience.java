package net.lordofthecraft.omniscience.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.lordofthecraft.omniscience.api.display.DisplayHandler;
import net.lordofthecraft.omniscience.api.entry.ActionResult;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.flag.FlagHandler;
import net.lordofthecraft.omniscience.api.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;
import net.lordofthecraft.omniscience.core.io.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class Omniscience extends JavaPlugin {

    private static OmniCore INSTANCE;
    private static Omniscience PLUGIN_INSTANCE;

    public static IOmniscience getInstance() {
        return INSTANCE;
    }

    public static Omniscience getPluginInstance() {
        return PLUGIN_INSTANCE;
    }

    public static Optional<Class<? extends DataEntry>> getDataEntryClass(String identifier) {
        return INSTANCE.getEventClass(identifier);
    }

    public static Optional<DisplayHandler> getDisplayHandler(String key) {
        return INSTANCE.getDisplayHandler(key);
    }

    public static Optional<ParameterHandler> getParameterHandler(String key) {
        return INSTANCE.getParameterHandler(key);
    }

    public static Optional<FlagHandler> getFlagHandler(String key) {
        return INSTANCE.getFlagHandler(key);
    }

    public static ImmutableList<ParameterHandler> getParameters() {
        return ImmutableList.copyOf(INSTANCE.getParameterHandlerList());
    }

    public static ImmutableList<FlagHandler> getFlagHandlers() {
        return ImmutableList.copyOf(INSTANCE.getFlagHandlerList());
    }

    public static ImmutableSet<String> getEvents() {
        return ImmutableSet.copyOf(INSTANCE.getEventSet());
    }

    public static boolean hasActiveWand(Player player) {
        return INSTANCE.hasActiveWand(player);
    }

    public static void wandActivateFor(Player player) {
        INSTANCE.wandActivateFor(player);
    }

    public static void wandDeactivateFor(Player player) {
        INSTANCE.wandDeactivateFor(player);
    }

    public static StorageHandler getStorageHandler() {
        return INSTANCE.getStorageHandler();
    }

    public static void onWorldEditStatusChange(boolean status) {
        INSTANCE.onWorldEditStatusChange(status);
    }

    public static void addLastActionResults(UUID id, List<ActionResult> results) {
        INSTANCE.addLastActionResults(id, results);
    }

    public static Optional<List<ActionResult>> getLastActionResults(UUID id) {
        return INSTANCE.getLastActionResults(id);
    }

    /**
     * Register an event with a specific class that will be instantiated whenever the event is loaded from the database
     *
     * @param event The string name of the event
     * @param clazz The class that will be instantiated whenever we load an event of the specified name
     */
    public static void registerEvent(String event, Class<? extends DataEntry> clazz) {
        INSTANCE.registerEvent(event, clazz);
    }

    /**
     * Register an event with the syste,
     *
     * @param event The string name of the event
     */
    public static void registerEvent(String event, String pastTense) {
        INSTANCE.registerEvent(event, pastTense);
    }

    public static void registerDisplayHandler(DisplayHandler handler) {
        INSTANCE.registerDisplayHandler(handler);
    }

    public static void registerFlagHandler(FlagHandler handler) {
        INSTANCE.registerFlagHandler(handler);
    }

    public static void registerParameterHandler(ParameterHandler parameterHandler) {
        INSTANCE.registerParameterHandler(parameterHandler);
    }

    public static void logDebug(String message) {
        if (OmniConfig.INSTANCE.isDebugEnabled()) PLUGIN_INSTANCE.getLogger().info(message);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        INSTANCE.onDisable(this);
    }

    @Override
    public void onLoad() {
        PLUGIN_INSTANCE = this;
        if (INSTANCE == null) {
            INSTANCE = new OmniCore();
        }
        INSTANCE.onLoad(this);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        PLUGIN_INSTANCE = this;
        INSTANCE = new OmniCore();
        INSTANCE.onEnable(this, Bukkit.getScheduler());
    }
}

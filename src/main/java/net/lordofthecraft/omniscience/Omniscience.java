package net.lordofthecraft.omniscience;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.lordofthecraft.omniscience.api.display.DisplayHandler;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.flag.FlagHandler;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.io.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class Omniscience extends JavaPlugin {
    /*
    @TODO Implement events being turned on/off
    @TODO Fix up query search autocomplete to allow e: to specify all available events.
    @TODO figure out why timestamp parameter isn't working
    @TODO Ensure rollbacks work
    @TODO Save entity-related events
    @TODO Save inventory related events
    @TODO implement unimplemented config options
    @TODO investigate async rollback options
    @TODO world edit selection
    @TODO send message about what someone is searching when they use the tool

    @TODO Implement DynamoDB
     */

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

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        INSTANCE.onDisable(this);
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

    @Override
    public void onLoad() {
        PLUGIN_INSTANCE = this;
        if (INSTANCE == null) {
            INSTANCE = new OmniCore();
        }
        INSTANCE.onLoad(this);
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

    @Override
    public void onEnable() {
        // Plugin startup logic
        PLUGIN_INSTANCE = this;
        INSTANCE = new OmniCore();
        INSTANCE.onEnable(this, Bukkit.getScheduler());
    }
}

package net.lordofthecraft.omniscience;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.flag.FlagHandler;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;
import net.lordofthecraft.omniscience.interfaces.IOmniscience;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public final class Omniscience extends JavaPlugin {

    private static OmniCore INSTANCE;

    public static IOmniscience getInstance() {
        return INSTANCE;
    }

    public static Optional<Class<? extends DataEntry>> getDataEntryClass(String identifier) {
        return INSTANCE.getEventClass(identifier);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        INSTANCE.onDisable(this);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = new OmniCore();
        INSTANCE.onEnable(this, Bukkit.getScheduler());
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

    @Override
    public void onLoad() {
        if (INSTANCE == null) {
            INSTANCE = new OmniCore();
        }
        INSTANCE.onLoad(this);
    }
}

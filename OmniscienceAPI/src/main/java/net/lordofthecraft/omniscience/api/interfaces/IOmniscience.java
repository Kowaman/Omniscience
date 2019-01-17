package net.lordofthecraft.omniscience.api.interfaces;

import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.flag.FlagHandler;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public interface IOmniscience {

    Optional<Class<? extends DataEntry>> getEventClass(String name);

    void info(String info);

    void warning(String warning);

    void severe(String error);

    void log(Level level, String message, Throwable ex);

    boolean areDefaultsEnabled();

    List<ParameterHandler> getParameters();

    Map<String, Boolean> getEvents();

    Optional<FlagHandler> getFlagHandler(String flag);

    Optional<ParameterHandler> getParameterHandler(String name);

    String getDefaultTime();

    int getDefaultRadius();

    int getMaxRadius();

    String getSimpleDateFormat();

    void registerWorldEditHandler(WorldEditHandler handler);
}

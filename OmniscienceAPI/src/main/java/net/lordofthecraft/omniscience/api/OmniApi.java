package net.lordofthecraft.omniscience.api;

import net.lordofthecraft.omniscience.api.flag.FlagHandler;
import net.lordofthecraft.omniscience.api.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.api.interfaces.WorldEditHandler;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class OmniApi {

    private static IOmniscience omniscience;

    public static void setCore(IOmniscience omni) throws IllegalAccessException {
        if (omniscience != null) {
            throw new IllegalAccessException("Omniscience's instance cannot be replaced.");
        }
        omniscience = omni;
    }

    public static IOmniscience getOmniscience() {
        return omniscience;
    }

    public static void info(String info) {
        omniscience.info(info);
    }

    public static void warning(String warning) {
        omniscience.warning(warning);
    }

    public static void severe(String error) {
        omniscience.severe(error);
    }

    public static void log(Level level, String msg, Throwable ex) {
        omniscience.log(level, msg, ex);
    }

    public static boolean areDefaultsEnabled() {
        return omniscience.areDefaultsEnabled();
    }

    public static List<ParameterHandler> getParameters() {
        return omniscience.getParameters();
    }

    public static Optional<FlagHandler> getFlagHandler(String flag) {
        return omniscience.getFlagHandler(flag);
    }

    public static Optional<ParameterHandler> getParameterHandler(String parameter) {
        return omniscience.getParameterHandler(parameter);
    }

    public static String getDefaultTime() {
        return omniscience.getDefaultTime();
    }

    public static int getDefaultRadius() {
        return omniscience.getDefaultRadius();
    }

    public static int getRadiusLimit() {
        return omniscience.getMaxRadius();
    }

    public static Map<String, Boolean> getEvents() {
        return omniscience.getEvents();
    }

    public static List<String> getEnabledEvents() {
        return getEvents().entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public static boolean isEventEnabled(String event) {
        return getEvents().containsKey(event) && getEvents().get(event);
    }

    public static boolean isEventRegistered(String event) {
        return getEvents().containsKey(event);
    }

    public static String getSimpleDateFormat() {
        return omniscience.getSimpleDateFormat();
    }

    public static void registerWorldEditHandler(WorldEditHandler handler) {
        omniscience.registerWorldEditHandler(handler);
    }
}

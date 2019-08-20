package net.lordofthecraft.omniscience.api;

import net.lordofthecraft.omniscience.api.agnostic.Actor;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.entry.entrybuilder.EventBuilder;
import net.lordofthecraft.omniscience.api.flag.FlagHandler;
import net.lordofthecraft.omniscience.api.interfaces.IOmniscience;
import net.lordofthecraft.omniscience.api.interfaces.WorldEditHandler;
import net.lordofthecraft.omniscience.api.parameter.ParameterHandler;
import net.lordofthecraft.omniscience.api.util.PastTenseWithEnabled;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class OmniApi {

    private OmniApi() {
        throw new UnsupportedOperationException();
    }

    public static IOmniscience get() {
        return OmniscienceProvider.INSTANCE;
    }

    public static EventBuilder logBuilder(Object source) {
        return get().createLogBuilder(source);
    }

    public static EventBuilder logBuilder(Actor actor) {
        return get().createLogBuilder(actor);
    }

    public static Actor wrapActor(UUID actorId) {
        return get().wrapActor(actorId);
    }

    public static void info(String info) {
        get().info(info);
    }

    public static void warning(String warning) {
        get().warning(warning);
    }

    public static void severe(String error) {
        get().severe(error);
    }

    public static void log(Level level, String msg, Throwable ex) {
        get().log(level, msg, ex);
    }

    public static boolean areDefaultsEnabled() {
        return get().areDefaultsEnabled();
    }

    public static List<ParameterHandler> getParameters() {
        return get().getParameters();
    }

    public static Optional<FlagHandler> getFlagHandler(String flag) {
        return get().getFlagHandler(flag);
    }

    public static Optional<ParameterHandler> getParameterHandler(String parameter) {
        return get().getParameterHandler(parameter);
    }

    public static String getDefaultTime() {
        return get().getDefaultTime();
    }

    public static int getDefaultRadius() {
        return get().getDefaultRadius();
    }

    public static int getRadiusLimit() {
        return get().getMaxRadius();
    }

    public static Map<String, PastTenseWithEnabled> getEvents() {
        return get().getEvents();
    }

    public static List<String> getEnabledEvents() {
        return getEvents().entrySet().stream().filter((ent) -> ent.getValue().isEnabled()).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public static boolean isEventEnabled(String event) {
        return getEvents().containsKey(event) && getEvents().get(event).isEnabled();
    }

    public static boolean isEventRegistered(String event) {
        return getEvents().containsKey(event);
    }

    public static void registerEvent(String event, String pastTense) {
        get().registerEvent(event, pastTense);
    }

    public static void registerParameterHandler(ParameterHandler handler) {
        get().registerParameterHandler(handler);
    }

    public static void registerFlagHandler(FlagHandler handler) {
        get().registerFlagHandler(handler);
    }

    public static String getEventPastTense(String event) {
        if (!getEvents().containsKey(event)) {
            return event;
        }
        return getEvents().get(event).getPastTense();
    }

    public static String getSimpleDateFormat() {
        return get().getSimpleDateFormat();
    }

    public static void registerWorldEditHandler(WorldEditHandler handler) {
        get().registerWorldEditHandler(handler);
    }

    public static Optional<Class<? extends DataEntry>> getEventClass(String event) {
        return get().getEventClass(event);
    }

    public static String getDateFormat() {
        return get().getDateFormat();
    }
}

package net.lordofthecraft.omniscience;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Set;

public enum OmniEventRegistrar {
    INSTANCE;

    private final Map<String, Boolean> eventMapping = Maps.newHashMap();

    public Set<String> getEventNames() {
        return eventMapping.keySet();
    }

    public boolean isEventRegistered(String event) {
        return eventMapping.containsKey(event);
    }

    void addEvent(String name, boolean enabled) {
        eventMapping.put(name, enabled);
    }
}

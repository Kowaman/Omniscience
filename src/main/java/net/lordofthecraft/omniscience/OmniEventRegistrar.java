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

    public void addEvent(String name, boolean enabled) {
        eventMapping.put(name, enabled);
    }

    public String setEventName(String name) {
        if (!eventMapping.containsKey(name)) {
            eventMapping.put(name, true);
        }
        return name;
    }
}

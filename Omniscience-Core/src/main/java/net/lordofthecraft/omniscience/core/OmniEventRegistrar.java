package net.lordofthecraft.omniscience.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.lordofthecraft.omniscience.api.util.PastTenseWithEnabled;
import net.lordofthecraft.omniscience.core.listener.OmniListener;
import net.lordofthecraft.omniscience.core.listener.block.*;
import net.lordofthecraft.omniscience.core.listener.chat.EventCommandListener;
import net.lordofthecraft.omniscience.core.listener.chat.EventSayListener;
import net.lordofthecraft.omniscience.core.listener.entity.EventDeathListener;
import net.lordofthecraft.omniscience.core.listener.entity.EventHitListener;
import net.lordofthecraft.omniscience.core.listener.entity.EventInteractAtEntity;
import net.lordofthecraft.omniscience.core.listener.entity.EventMountListener;
import net.lordofthecraft.omniscience.core.listener.item.*;
import net.lordofthecraft.omniscience.core.listener.player.EventJoinListener;
import net.lordofthecraft.omniscience.core.listener.player.EventQuitListener;
import net.lordofthecraft.omniscience.core.listener.player.EventTeleportListener;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public enum OmniEventRegistrar {
    INSTANCE;

    private final Map<String, PastTenseWithEnabled> eventMapping = Maps.newHashMap();
    private List<OmniListener> listeners = Lists.newArrayList();

    OmniEventRegistrar() {
        //Block
        listeners.add(new EventBreakListener());
        listeners.add(new EventDecayListener());
        listeners.add(new EventFormListener());
        listeners.add(new EventIgniteListener());
        listeners.add(new EventPlaceListener());
        listeners.add(new EventUseListener());
        listeners.add(new EventGrowListener());

        //Chat
        listeners.add(new EventCommandListener());
        listeners.add(new EventSayListener());

        //Entity
        listeners.add(new EventDeathListener());
        listeners.add(new EventHitListener());
        listeners.add(new EventInteractAtEntity());
        listeners.add(new EventMountListener());

        //Item
        listeners.add(new EventContainerListener());
        listeners.add(new EventDropListener());
        listeners.add(new EventInventoryListener());
        listeners.add(new EventPickupListener());
        listeners.add(new EventEntityItemListener());

        //Player
        listeners.add(new EventJoinListener());
        listeners.add(new EventQuitListener());
        listeners.add(new EventTeleportListener());
    }

    public Set<String> getEventNames() {
        return eventMapping.keySet();
    }

    public boolean isEventRegistered(String event) {
        return eventMapping.containsKey(event);
    }

    public boolean isEventEnabled(String event) {
        if (!eventMapping.containsKey(event)) {
            return false;
        }
        return eventMapping.get(event).isEnabled();
    }

    public String getPastTense(String event) {
        if (!eventMapping.containsKey(event)) {
            return event;
        }
        return eventMapping.get(event).getPastTense();
    }

    public Map<String, PastTenseWithEnabled> getEventMapping() {
        return ImmutableMap.copyOf(eventMapping);
    }

    void addEvent(String name, String pastTense, boolean enabled) {
        eventMapping.put(name, new PastTenseWithEnabled(enabled, pastTense));
    }

    void enableEvents(PluginManager manager, Omniscience omniscience) {
        eventMapping.forEach((key, value) -> {
            Optional<OmniListener> listener = listeners.stream().filter(l -> l.handles(key)).findFirst();
            if (listener.isPresent() && value.isEnabled()) {
                OmniListener list = listener.get();
                if (!list.isEnabled()) {
                    manager.registerEvents(list, omniscience);
                    list.setEnabled(true);
                }
            }
        });
    }
}

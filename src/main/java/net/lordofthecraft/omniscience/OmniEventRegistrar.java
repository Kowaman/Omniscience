package net.lordofthecraft.omniscience;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.lordofthecraft.omniscience.listener.OmniListener;
import net.lordofthecraft.omniscience.listener.block.*;
import net.lordofthecraft.omniscience.listener.chat.EventCommandListener;
import net.lordofthecraft.omniscience.listener.chat.EventSayListener;
import net.lordofthecraft.omniscience.listener.entity.EventDeathListener;
import net.lordofthecraft.omniscience.listener.entity.EventHitListener;
import net.lordofthecraft.omniscience.listener.item.EventContainerListener;
import net.lordofthecraft.omniscience.listener.item.EventDropListener;
import net.lordofthecraft.omniscience.listener.item.EventInventoryListener;
import net.lordofthecraft.omniscience.listener.item.EventPickupListener;
import net.lordofthecraft.omniscience.listener.player.EventJoinListener;
import net.lordofthecraft.omniscience.listener.player.EventQuitListener;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public enum OmniEventRegistrar {
    INSTANCE;

    private final Map<String, Boolean> eventMapping = Maps.newHashMap();
    private List<OmniListener> listeners = Lists.newArrayList();

    OmniEventRegistrar() {
        //Block
        listeners.add(new EventBreakListener());
        listeners.add(new EventDecayListener());
        listeners.add(new EventFormListener());
        listeners.add(new EventIgniteListener());
        listeners.add(new EventPlaceListener());
        listeners.add(new EventUseListener());

        //Chat
        listeners.add(new EventCommandListener());
        listeners.add(new EventSayListener());

        //Entity
        listeners.add(new EventDeathListener());
        listeners.add(new EventHitListener());

        //Item
        listeners.add(new EventContainerListener());
        listeners.add(new EventDropListener());
        listeners.add(new EventInventoryListener());
        listeners.add(new EventPickupListener());

        //Player
        listeners.add(new EventJoinListener());
        listeners.add(new EventQuitListener());
    }

    public Set<String> getEventNames() {
        return eventMapping.keySet();
    }

    public boolean isEventRegistered(String event) {
        return eventMapping.containsKey(event);
    }

    public boolean isEventEnabled(String event) {
        return eventMapping.get(event);
    }

    void addEvent(String name, boolean enabled) {
        eventMapping.put(name, enabled);
    }

    void enableEvents(PluginManager manager, Omniscience omniscience) {
        eventMapping.forEach((key, value) -> {
            Optional<OmniListener> listener = listeners.stream().filter(l -> l.handles(key)).findFirst();
            if (listener.isPresent() && value) {
                OmniListener list = listener.get();
                if (!list.isEnabled()) {
                    manager.registerEvents(list, omniscience);
                    list.setEnabled(true);
                }
            }
        });
    }
}

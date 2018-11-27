package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class ItemListener implements Listener {

    public ItemListener() {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDropItem(EntityDropItemEvent event) {
        OEntry.create().source(event.getEntity()).dropped(event.getItemDrop()).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        OEntry.create().source(event.getEntity()).pickup(event.getItem()).save();
    }
}

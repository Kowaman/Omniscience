package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public final class ItemListener implements Listener {

    public ItemListener() {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        System.out.println("PlayerDropItemEvent triggered");
        OEntry.create().source(event.getPlayer()).dropped(event.getItemDrop()).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        OEntry.create().source(event.getPlayer()).pickup(event.getItem()).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDropItem(EntityDropItemEvent event) {
        System.out.println("EntityDropItemEvent triggered");
        OEntry.create().source(event.getEntity()).dropped(event.getItemDrop()).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        OEntry.create().source(event.getEntity()).pickup(event.getItem()).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event) {
        System.out.println("ItemSpawnEvent triggered");
        OEntry.create().environment().dropped(event.getEntity()).save();
    }
}

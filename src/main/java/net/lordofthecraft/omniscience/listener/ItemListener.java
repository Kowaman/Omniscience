package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

public final class ItemListener implements Listener {

    public ItemListener() {
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        System.out.println("PlayerDropItemEvent triggered");
        OEntry.create().source(event.getPlayer()).dropped(event.getItemDrop()).save();
        event.getItemDrop().setMetadata("omniTracked", new FixedMetadataValue(Omniscience.getPluginInstance(), "tracked"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDropItem(EntityDropItemEvent event) {
        System.out.println("EntityDropItemEvent triggered");
        if (!event.getItemDrop().hasMetadata("omniTracked")) {
            OEntry.create().source(event.getEntity()).dropped(event.getItemDrop()).save();
            event.getItemDrop().setMetadata("omniTracked", new FixedMetadataValue(Omniscience.getPluginInstance(), "tracked"));
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        System.out.println("EntityPickupItemEvent triggered");
        OEntry.create().source(event.getEntity()).pickup(event.getItem()).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemSpawn(ItemSpawnEvent event) {
        System.out.println("ItemSpawnEvent triggered");
        if (!event.getEntity().hasMetadata("omniTracked")) {
            OEntry.create().environment().dropped(event.getEntity()).save();
            event.getEntity().setMetadata("omniTracked", new FixedMetadataValue(Omniscience.getPluginInstance(), "tracked"));
        }
    }
}

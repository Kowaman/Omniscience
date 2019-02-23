package net.lordofthecraft.omniscience.listener.item;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EventDropListener extends OmniListener {

    public EventDropListener() {
        super(ImmutableList.of("drop"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        OEntry.create().source(event.getPlayer()).dropped(event.getItemDrop()).save();
        event.getItemDrop().setMetadata("omniTracked", new FixedMetadataValue(Omniscience.getPluginInstance(), "tracked"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDropItem(EntityDropItemEvent event) {
        if (!event.getItemDrop().hasMetadata("omniTracked")) {
            OEntry.create().source(event.getEntity()).dropped(event.getItemDrop()).save();
            event.getItemDrop().setMetadata("omniTracked", new FixedMetadataValue(Omniscience.getPluginInstance(), "tracked"));
        }
    }
}

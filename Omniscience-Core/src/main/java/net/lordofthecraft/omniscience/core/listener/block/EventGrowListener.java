package net.lordofthecraft.omniscience.core.listener.block;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.data.LocationTransaction;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.core.listener.OmniListener;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class EventGrowListener extends OmniListener {

    public EventGrowListener() {
        super(ImmutableList.of("grow"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onStructureGrow(StructureGrowEvent e) {
        if (isEnabled("grow")) {
            for (BlockState block : e.getBlocks()) {
                OEntry.create().source(e.getPlayer()).grewBlock(new LocationTransaction<>(block.getLocation(), null, block)).save();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockGrow(BlockGrowEvent e) {
        if (isEnabled("grow")) {
            OEntry.create().source(null).grewBlock(new LocationTransaction<>(e.getBlock().getLocation(), e.getBlock().getState(), e.getNewState())).save();
        }
    }
}

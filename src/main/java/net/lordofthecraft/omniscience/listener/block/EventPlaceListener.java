package net.lordofthecraft.omniscience.listener.block;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.data.BlockTransaction;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class EventPlaceListener extends OmniListener {

    public EventPlaceListener() {
        super(ImmutableList.of("place"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        // Escape out because we don't want to log sign placements twice.
        if (event.getBlock().getState() instanceof Sign) {
            return;
        }

        OEntry.create().source(event.getPlayer()).placedBlock(BlockTransaction.from(event.getBlock().getLocation(), event.getBlockReplacedState(), event.getBlock().getState())).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        //TODO absolutely verify this works
        event.getReplacedBlockStates().forEach(state -> OEntry.create().source(event.getPlayer()).placedBlock(BlockTransaction.from(state.getBlock().getLocation(), state, state.getBlock().getState())).save());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onSignChange(SignChangeEvent event) {
        OEntry.create().source(event.getPlayer()).placedBlock(BlockTransaction.from(event.getBlock().getLocation(), null, event.getBlock().getState())).save();
    }
}

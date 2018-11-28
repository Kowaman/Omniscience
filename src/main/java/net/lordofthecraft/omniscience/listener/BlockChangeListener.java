package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.api.data.BlockTransaction;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

public final class BlockChangeListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLeavesDecay(LeavesDecayEvent event) {
        OEntry.create().environment().decayedBlock(BlockTransaction.from(event.getBlock().getState(), null)).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        OEntry.create().source(event.getPlayer()).brokeBlock(BlockTransaction.from(event.getBlock().getState(), null)).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        OEntry.create().source(event.getPlayer()).placedBlock(BlockTransaction.from(event.getBlockReplacedState(), event.getBlock().getState())).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent event) {
        OEntry.create().environment().brokeBlock(BlockTransaction.from(event.getBlock().getState(), null));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockIgnite(BlockIgniteEvent event) {
        //TODO we need to track this... but it's complex.
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent event) {
        //TODO Man I'd LOVE to have a player source for this.
        event.blockList().forEach(block -> OEntry.create().environment().brokeBlock(BlockTransaction.from(block.getState(), null)).save());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockFade(BlockFadeEvent event) {
        OEntry.create().environment().decayedBlock(BlockTransaction.from(event.getBlock().getState(), event.getNewState())).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockForm(BlockFormEvent event) {
        OEntry.create().environment().formedBlock(BlockTransaction.from(event.getBlock().getState(), event.getNewState())).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        //TODO absolutely verify this works
        event.getReplacedBlockStates().forEach(state -> OEntry.create().source(event.getPlayer()).placedBlock(BlockTransaction.from(state, state.getBlock().getState())).save());
    }
}

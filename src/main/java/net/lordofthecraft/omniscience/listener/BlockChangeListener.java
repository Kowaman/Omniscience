package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.api.data.BlockTransaction;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

public class BlockChangeListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLeavesDecay(LeavesDecayEvent event) {
        OEntry.create().environment().decayedBlock(new BlockTransaction(event.getBlock().getState(), null)).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        OEntry.create().source(event.getPlayer()).brokeBlock(new BlockTransaction(event.getBlock().getState(), null)).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        OEntry.create().source(event.getPlayer()).brokeBlock(new BlockTransaction(event.getBlockReplacedState(), event.getBlock().getState())).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent event) {
        //TODO how to handle?
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent event) {
        //TODO handle
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockFade(BlockFadeEvent event) {
        //TODO handle
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockForm(BlockFormEvent event) {
        //TODO handle
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        //TODO handle?
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockFromTo(BlockFromToEvent event) {
        //TODO handle?
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPhysics(BlockPhysicsEvent event) {
        //TODO handle?
    }
}

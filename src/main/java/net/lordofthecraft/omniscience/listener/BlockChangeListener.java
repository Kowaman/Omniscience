package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.data.BlockTransaction;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Openable;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.UUID;

public final class BlockChangeListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onLeavesDecay(LeavesDecayEvent event) {
        OEntry.create().environment().decayedBlock(BlockTransaction.from(event.getBlock().getLocation(), event.getBlock().getState(), null)).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        OEntry.create().source(event.getPlayer()).brokeBlock(BlockTransaction.from(event.getBlock().getLocation(), event.getBlock().getState(), null)).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        OEntry.create().source(event.getPlayer()).placedBlock(BlockTransaction.from(event.getBlock().getLocation(), event.getBlockReplacedState(), event.getBlock().getState())).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent event) {
        OEntry.create().environment().brokeBlock(BlockTransaction.from(event.getBlock().getLocation(), event.getBlock().getState(), null)).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockIgnite(BlockIgniteEvent event) {
        //TODO we need to track this... but it's complex.
        if (event.getPlayer() != null) {
            OEntry.create().source(event.getPlayer()).ignited(event.getBlock()).save();
            event.getBlock().setMetadata("player-source", new FixedMetadataValue(Omniscience.getPluginInstance(), event.getPlayer().getUniqueId().toString()));
        } else if (event.getIgnitingBlock() != null && event.getIgnitingBlock().hasMetadata("player-source")) {
            OEntry.create().environment().ignited(event.getBlock()).save();
            List<MetadataValue> metadataValues = event.getIgnitingBlock().getMetadata("player-source");
            for (MetadataValue value : metadataValues) {
                if (value.getOwningPlugin() instanceof Omniscience) {
                    event.getBlock().setMetadata("player-source", value);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.getBlock().hasMetadata("player-source")) {
            List<MetadataValue> metadataValues = event.getBlock().getMetadata("player-source");
            for (MetadataValue value : metadataValues) {
                if (value.getOwningPlugin() instanceof Omniscience) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(value.asString()));
                    if (player != null) {
                        event.blockList().forEach(block -> OEntry.create().source(player).brokeBlock(BlockTransaction.from(event.getBlock().getLocation(), block.getState(), null)).save());
                        return;
                    }
                }
            }
        } else {
            event.blockList().forEach(block -> OEntry.create().environment().brokeBlock(BlockTransaction.from(event.getBlock().getLocation(), block.getState(), null)).save());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockFade(BlockFadeEvent event) {
        OEntry.create().environment().decayedBlock(BlockTransaction.from(event.getBlock().getLocation(), event.getBlock().getState(), event.getNewState())).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockForm(BlockFormEvent event) {
        OEntry.create().environment().formedBlock(BlockTransaction.from(event.getBlock().getLocation(), event.getBlock().getState(), event.getNewState())).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        //TODO absolutely verify this works
        event.getReplacedBlockStates().forEach(state -> OEntry.create().source(event.getPlayer()).placedBlock(BlockTransaction.from(state.getBlock().getLocation(), state, state.getBlock().getState())).save());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock().getState() instanceof Openable) {
            //TODO log opening a thing?
        }
    }
}

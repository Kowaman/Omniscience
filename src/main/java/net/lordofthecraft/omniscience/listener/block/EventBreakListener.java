package net.lordofthecraft.omniscience.listener.block;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.data.LocationTransaction;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.UUID;

public class EventBreakListener extends OmniListener {

    public EventBreakListener() {
        super(ImmutableList.of("break"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        OEntry.create().source(event.getPlayer()).brokeBlock(new LocationTransaction<>(event.getBlock().getLocation(), event.getBlock().getState(), null)).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockExplode(BlockExplodeEvent event) {
        if (event.getBlock().hasMetadata("player-source")) {
            List<MetadataValue> metadataValues = event.getBlock().getMetadata("player-source");
            for (MetadataValue value : metadataValues) {
                if (writeBlockBreakForMetaData(value, event.blockList())) {
                    return;
                }
            }
        } else {
            event.blockList().forEach(block -> OEntry.create().source(event.getBlock().getType().name()).brokeBlock(new LocationTransaction<>(block.getLocation(), block.getState(), null)).save());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityBreakDoor(EntityBreakDoorEvent e) {
        OEntry.create().source(e.getEntity()).brokeBlock(new LocationTransaction<>(e.getBlock().getLocation(), e.getBlock().getState(), null)).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.getEntity().hasMetadata("player-source")) {
            List<MetadataValue> metadataValues = event.getEntity().getMetadata("player-source");
            for (MetadataValue value : metadataValues) {
                if (writeBlockBreakForMetaData(value, event.blockList())) {
                    return;
                }
            }
        } else {
            event.blockList()
                    .stream()
                    .filter(block -> block.getType() == Material.CAVE_AIR)
                    .forEach(block -> OEntry.create().source(event.getEntity()).brokeBlock(new LocationTransaction<>(block.getLocation(), block.getState(), null)).save());
            event.blockList()
                    .stream()
                    .filter(block -> block.getType() != Material.CAVE_AIR)
                    .forEach(block -> OEntry.create().source(event.getEntity()).brokeBlock(new LocationTransaction<>(block.getLocation(), block.getState(), null)).save());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent event) {
        OEntry.create().environment().brokeBlock(new LocationTransaction<>(event.getBlock().getLocation(), event.getBlock().getState(), null)).save();
    }

    private boolean writeBlockBreakForMetaData(MetadataValue value, List<Block> blocks) {
        if (value.getOwningPlugin() instanceof Omniscience) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(value.asString()));
            if (player != null) {
                blocks.stream()
                        .filter(block -> block.getType() != Material.CAVE_AIR)
                        .forEach(block -> OEntry.create().source(player).brokeBlock(new LocationTransaction<>(block.getLocation(), block.getState(), null)).save());
            }
            return true;
        }
        return false;
    }
}

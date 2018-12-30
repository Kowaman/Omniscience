package net.lordofthecraft.omniscience.worldedit;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.api.data.LocationTransaction;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.List;
import java.util.logging.Level;

import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

public class WorldEditRunner implements Runnable {

    @Override
    public void run() {
        List<ChangeWrapper> batchWrappers = Lists.newArrayList();

        while (!WorldEditChangeQueue.getQueue().isEmpty()) {
            ChangeWrapper wrapper = WorldEditChangeQueue.getQueue().poll();
            if (wrapper != null) {
                batchWrappers.add(wrapper);
            }
        }

        if (batchWrappers.size() > 0) {
            for (ChangeWrapper change : batchWrappers) {
                try {
                    if (change.getAfter().getBlockType().getMaterial().isAir()) {
                        if (change.getBefore().getType() != Material.AIR && change.getBefore().getType() != Material.CAVE_AIR && change.getBefore().getType() != Material.VOID_AIR) {
                            OEntry.create().source(change.getPlayer() == null ? "Console" : change.getPlayer())
                                    .brokeBlock(new LocationTransaction<>(change.getVector(), change.getBefore(), null)).save();
                        }
                    } else {
                        DataWrapper wrapper = DataWrapper.createNew();
                        BlockData data = BukkitAdapter.adapt(change.getAfter());
                        wrapper.set(TARGET, data.getMaterial().name());
                        if (change.getBefore() != null && change.getBefore().getType() != Material.AIR && change.getBefore().getType() != Material.CAVE_AIR && change.getBefore().getType() != Material.VOID_AIR) {
                            wrapper.set(ORIGINAL_BLOCK, DataWrapper.ofBlock(change.getBefore()));
                        }
                        wrapper.set(NEW_BLOCK.then(MATERIAL_TYPE), data.getMaterial().name());
                        wrapper.set(NEW_BLOCK.then(BLOCK_DATA), data.getAsString());
                        //TODO save tile entity data
                        OEntry.create().source(change.getPlayer() == null ? "Console" : change.getPlayer()).customWithLocation("place", wrapper, change.getVector()).save();
                    }
                } catch (Exception e) {
                    Omniscience.getPluginInstance().getLogger().log(Level.SEVERE, "Failed to save a WorldEdit change: " + change, e);
                }
            }
        }
    }
}

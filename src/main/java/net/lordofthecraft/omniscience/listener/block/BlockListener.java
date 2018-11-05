package net.lordofthecraft.omniscience.listener.block;

import net.lordofthecraft.omniscience.consumer.EntryConsumer;
import net.lordofthecraft.omniscience.domain.actor.PlayerActor;
import net.lordofthecraft.omniscience.domain.actor.WorldActor;
import net.lordofthecraft.omniscience.domain.block.BlockEntry;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Date;

public class BlockListener implements Listener {

    private final EntryConsumer consumer;

    public BlockListener(EntryConsumer consumer) {
        this.consumer = consumer;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        consumer.addDataEntry(new BlockEntry(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID(), PlayerActor.fromPlayer(player), new Date(), block.getBlockData(), null));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        consumer.addDataEntry(new BlockEntry(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID(), PlayerActor.fromPlayer(player), new Date(), null, block.getBlockData()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBurn(BlockBurnEvent event) {
        Block block = event.getBlock();
        consumer.addDataEntry(new BlockEntry(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID(), WorldActor.get(), new Date(), block.getBlockData(), null));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockIgnite(BlockIgniteEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        consumer.addDataEntry(new BlockEntry(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID(), PlayerActor.fromPlayer(player), new Date(), null, block.getBlockData()));
    }
}

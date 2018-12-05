package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.OmniConfig;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.flag.Flag;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.api.query.SearchConditionGroup;
import net.lordofthecraft.omniscience.command.async.SearchCallback;
import net.lordofthecraft.omniscience.command.util.Async;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public final class WandInteractListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasItem()
                || event.getItem().getType() != OmniConfig.INSTANCE.getWandMaterial()
                || event.getHand() != EquipmentSlot.HAND
                || !Omniscience.hasActiveWand(event.getPlayer())) {
            return;
        }
        if (event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        event.setCancelled(true);

        QuerySession session = new QuerySession(event.getPlayer());
        session.addFlag(Flag.NO_GROUP);
        session.newQuery().addCondition(SearchConditionGroup.from(event.getClickedBlock().getLocation()));

        //TODO send info about searches created through the block tool

        Async.lookup(session, new SearchCallback(session));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced() == null
                || event.getBlockPlaced().getType() != OmniConfig.INSTANCE.getWandMaterial()
                || !Omniscience.hasActiveWand(event.getPlayer())) {
            return;
        }
        event.setCancelled(true);

        QuerySession session = new QuerySession(event.getPlayer());
        session.addFlag(Flag.NO_GROUP);
        session.newQuery().addCondition(SearchConditionGroup.from(event.getBlockPlaced().getLocation()));

        //TODO send info about searches created through the block tool

        Async.lookup(session, new SearchCallback(session));
    }
}

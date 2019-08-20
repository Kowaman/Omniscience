package net.lordofthecraft.omniscience.core.listener;

import com.google.common.collect.Range;
import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.flag.Flag;
import net.lordofthecraft.omniscience.api.query.FieldCondition;
import net.lordofthecraft.omniscience.api.query.MatchRule;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.api.query.SearchConditionGroup;
import net.lordofthecraft.omniscience.api.util.Formatter;
import net.lordofthecraft.omniscience.core.OmniConfig;
import net.lordofthecraft.omniscience.core.Omniscience;
import net.lordofthecraft.omniscience.core.command.async.SearchCallback;
import net.lordofthecraft.omniscience.core.command.util.Async;
import org.bukkit.ChatColor;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryHolder;

public final class WandInteractListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerLoginEvent e) {
        if (e.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        if (e.getPlayer().hasPermission("omniscience.commands.search.autotool")) {
            Omniscience.wandActivateFor(e.getPlayer());
        }
    }

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
        session.addFlag(Flag.NO_CHAT);

        Block b = event.getClickedBlock();
        if (b.getState() instanceof Container && ((Container) b.getState()).getInventory() instanceof DoubleChestInventory) {
            session.newQuery().addCondition(buildForDoubleChest(b));
        } else {
            session.newQuery().addCondition(SearchConditionGroup.from(b.getLocation()));
        }

        event.getPlayer().sendMessage(Formatter.prefix() + ChatColor.GREEN + "--- "
                + ChatColor.AQUA + b.getType().name()
                + ChatColor.WHITE + " at " + ChatColor.GREEN + b.getX() + " " + b.getY() + " " + b.getZ() + ChatColor.GREEN + " ---");

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
        session.addFlag(Flag.NO_CHAT);
        session.newQuery().addCondition(SearchConditionGroup.from(event.getBlockPlaced().getLocation()));
        BlockState b = event.getBlockReplacedState();

        event.getPlayer().sendMessage(Formatter.prefix() + ChatColor.GREEN + "--- "
                + ChatColor.AQUA + b.getType().name()
                + ChatColor.WHITE + " at " + ChatColor.GREEN + b.getX() + " " + b.getY() + " " + b.getZ() + ChatColor.GREEN + " ---");

        Async.lookup(session, new SearchCallback(session));
    }

    private SearchConditionGroup buildForDoubleChest(Block chest) {
        SearchConditionGroup group = new SearchConditionGroup(SearchConditionGroup.Operator.AND);
        if (chest.getState() instanceof Container) {
            InventoryHolder holder = ((Container) chest.getState()).getInventory().getHolder();
            if (holder instanceof DoubleChest) {
                DoubleChest dchest = (DoubleChest) holder;
                Chest left = (Chest) dchest.getLeftSide();
                Chest right = (Chest) dchest.getRightSide();

                int maxX = left.getX() > right.getX() ? left.getX() : right.getX();
                int minX = left.getX() < right.getX() ? left.getX() : right.getX();
                int maxZ = left.getZ() > right.getZ() ? left.getZ() : right.getZ();
                int minZ = left.getZ() < right.getZ() ? left.getZ() : right.getZ();

                if (left.getX() == right.getX()) {
                    group.add(FieldCondition.of(DataKeys.LOCATION.then(DataKeys.X), MatchRule.EQUALS, dchest.getX()));
                } else {
                    Range<Integer> rangeX = Range.open(minX, maxX);
                    group.add(FieldCondition.of(DataKeys.LOCATION.then(DataKeys.X), rangeX));
                }

                group.add(FieldCondition.of(DataKeys.LOCATION.then(DataKeys.Y), MatchRule.EQUALS, dchest.getY()));

                if (left.getZ() == right.getZ()) {
                    group.add(FieldCondition.of(DataKeys.LOCATION.then(DataKeys.Z), MatchRule.EQUALS, dchest.getZ()));
                } else {
                    Range<Integer> rangeZ = Range.open(minZ, maxZ);
                    group.add(FieldCondition.of(DataKeys.LOCATION.then(DataKeys.Z), rangeZ));
                }
            }
        }
        return group;
    }
}

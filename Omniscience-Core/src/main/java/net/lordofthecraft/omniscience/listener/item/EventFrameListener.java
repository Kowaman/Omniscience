package net.lordofthecraft.omniscience.listener.item;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class EventFrameListener extends OmniListener {

    public EventFrameListener() {
        super(ImmutableList.of("frame-withdraw", "frame-deposit"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ItemFrame
                && isEnabled("frame-deposit")) {
            ItemFrame frame = (ItemFrame) e.getRightClicked();
            ItemStack item = e.getHand() == EquipmentSlot.HAND ? e.getPlayer().getInventory().getItemInMainHand() : e.getPlayer().getInventory().getItemInOffHand();
            if ((frame.getItem() == null || frame.getItem().getType() == Material.AIR)
                    && item != null) {
                OEntry.create().source(e.getPlayer()).putIntoItemFrame(frame, item).save();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ItemFrame
                && isEnabled("frame-withdraw")) {
            ItemFrame frame = (ItemFrame) e.getEntity();
            if (frame.getItem() != null) {
                OEntry.create().source(e.getDamager()).removedFromItemFrame(frame, frame.getItem()).save();
            }
        }
    }
}

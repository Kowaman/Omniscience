package net.lordofthecraft.omniscience.listener.item;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class EventArmorStandListener extends OmniListener {

    public EventArmorStandListener() {
        super(ImmutableList.of("armor-withdraw", "armor-deposit"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        if ((e.getPlayerItem() != null && e.getPlayerItem().getType() != Material.AIR)
                && isEnabled("armor-deposit")) {
            OEntry.create().source(e.getPlayer()).putIntoArmorStand(e.getRightClicked(), e.getPlayerItem()).save();
        }
        if ((e.getArmorStandItem() != null && e.getArmorStandItem().getType() != Material.AIR)
                && isEnabled("armor-withdraw")) {
            OEntry.create().source(e.getPlayer()).removedFromArmorStand(e.getRightClicked(), e.getArmorStandItem()).save();
        }
    }
}

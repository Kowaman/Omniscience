package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.block.data.type.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CraftBookSignListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasBlock()
                && event.getClickedBlock().getBlockData() instanceof Sign) {
            OEntry.create().player(event.getPlayer()).signInteract(event.getClickedBlock().getLocation(), (Sign) event.getClickedBlock().getBlockData()).save();
        }
    }
}

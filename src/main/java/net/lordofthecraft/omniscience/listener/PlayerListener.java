package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (e.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        if (e.getPlayer().hasPermission("omniscience.commands.search.autotool")) {
            Omniscience.wandActivateFor(e.getPlayer());
        }
        OEntry.create().player((OfflinePlayer) e.getPlayer()).joined(e.getRealAddress().getHostAddress()).save();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        OEntry.create().player((OfflinePlayer) e.getPlayer()).quit().save();
    }
}

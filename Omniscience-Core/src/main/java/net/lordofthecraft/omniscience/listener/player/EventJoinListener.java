package net.lordofthecraft.omniscience.listener.player;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public class EventJoinListener extends OmniListener {

    public EventJoinListener() {
        super(ImmutableList.of("join"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent e) {
        if (e.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }
        OEntry.create().player((OfflinePlayer) e.getPlayer()).joined(e.getAddress().getHostAddress()).save();

    }
}

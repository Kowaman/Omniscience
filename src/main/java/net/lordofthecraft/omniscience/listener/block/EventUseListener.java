package net.lordofthecraft.omniscience.listener.block;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Openable;

public class EventUseListener extends OmniListener {

    public EventUseListener() {
        super(ImmutableList.of("use"));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock().getState() instanceof Openable) {
            //TODO log opening a thing?
        }
    }
}

package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Projectile && ((Projectile) e.getDamager()).getShooter() instanceof Player) {
            OEntry.create().source(((Projectile) e.getDamager()).getShooter()).shot(e.getEntity()).save();
            return;
        }
        if (e.getDamager() instanceof Player
                || e.getEntity() instanceof Player) {
            OEntry.create().source(e.getDamager()).hit(e.getEntity()).save();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            //TODO save an entity death record
        }
    }
}

package net.lordofthecraft.omniscience.core.agnostic;

import net.lordofthecraft.omniscience.api.agnostic.Actor;
import net.lordofthecraft.omniscience.api.agnostic.OmniBlock;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class BukkitActor implements Actor {

    private final OfflinePlayer offlinePlayer;

    public BukkitActor(OfflinePlayer offlinePlayer) {
        this.offlinePlayer = offlinePlayer;
    }

    @Override
    public boolean hasPermission(String permission) {
        if (offlinePlayer.getPlayer() != null) {
            return offlinePlayer.getPlayer().hasPermission(permission);
        }
        return false;
    }

    @Override
    public OmniBlock getLocation() {
        if (offlinePlayer.isOnline() && offlinePlayer.getPlayer() != null) {
            Location loc = offlinePlayer.getPlayer().getLocation();
            return new OmniBlock(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }
        return null;
    }

    @Override
    public UUID getUUID() {
        return offlinePlayer.getUniqueId();
    }

    @Override
    public String getName() {
        return offlinePlayer.getName();
    }
}

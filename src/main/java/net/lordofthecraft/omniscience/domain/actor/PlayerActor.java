package net.lordofthecraft.omniscience.domain.actor;

import net.lordofthecraft.omniscience.domain.Actor;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerActor implements Actor {

    private final UUID id;
    private String name;

    private PlayerActor(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PlayerActor fromPlayer(OfflinePlayer player) {
        return new PlayerActor(player.getUniqueId(), player.getName());
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getKey() {
        return id.toString();
    }
}

package net.lordofthecraft.omniscience.domain;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public interface DataEntry {

    int getX();

    int getY();

    int getZ();

    UUID getWorld();

    Date getTime();

    Actor getActor();

    String getDiscriminator();

    Document asDocument();

    void fromDocument(Document document);

    default Location getLocation() {
        return new Location(Bukkit.getWorld(getWorld()), getX(), getY(), getZ());
    }

    default boolean rollbackForPlayer(Player player) {
        return false;
    }
}

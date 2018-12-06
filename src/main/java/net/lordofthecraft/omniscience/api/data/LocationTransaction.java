package net.lordofthecraft.omniscience.api.data;

import org.bukkit.Location;

public class LocationTransaction<T> extends Transaction<T> {

    private final Location location;

    public LocationTransaction(Location location, T originalState, T finalState) {
        super(originalState, finalState);
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }
}

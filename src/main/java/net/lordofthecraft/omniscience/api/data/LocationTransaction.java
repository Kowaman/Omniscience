package net.lordofthecraft.omniscience.api.data;

import org.bukkit.Location;

/**
 * A {@link Transaction} that also stores a {@link Location} where the transaction occurred
 *
 * @param <T> The class of the object that is being changed
 */
public class LocationTransaction<T> extends Transaction<T> {

    private final WorldVector vector;

    public LocationTransaction(Location location, T originalState, T finalState) {
        super(originalState, finalState);
        this.vector = new WorldVector(location);
    }

    public LocationTransaction(WorldVector vector, T originalState, T finalState) {
        super(originalState, finalState);
        this.vector = vector;
    }

    public Location getLocation() {
        return vector.asLocation();
    }

    public WorldVector getVector() {
        return vector;
    }
}

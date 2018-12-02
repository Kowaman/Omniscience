package net.lordofthecraft.omniscience.api.data;

import org.bukkit.Location;
import org.bukkit.block.BlockState;

import java.util.Optional;

public final class BlockTransaction {

    private final Location location;

    private final BlockState before;
    private final BlockState after;

    private BlockTransaction(BlockState before, BlockState after, Location location) {
        this.before = before;
        this.after = after;
        this.location = location;
    }

    public static BlockTransaction from(Location location, BlockState before, BlockState after) {
        return new BlockTransaction(before, after, location);
    }

    public Optional<BlockState> getBefore() {
        return Optional.ofNullable(before);
    }

    public Optional<BlockState> getAfter() {
        return Optional.ofNullable(after);
    }

    public Location getLocation() {
        return location;
    }
}

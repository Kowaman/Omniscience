package net.lordofthecraft.omniscience.api.agnostic;

import java.util.Objects;
import java.util.UUID;

public final class OmniBlock {

    private final UUID world;
    private final int x;
    private final int y;
    private final int z;

    public OmniBlock(UUID world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public UUID getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OmniBlock omniBlock = (OmniBlock) o;
        return x == omniBlock.x &&
                y == omniBlock.y &&
                z == omniBlock.z &&
                world.equals(omniBlock.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, y, z);
    }

    @Override
    public String toString() {
        return "OmniBlock{" +
                "world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

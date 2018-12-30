package net.lordofthecraft.omniscience.worldedit;

import com.sk89q.worldedit.world.block.BaseBlock;
import net.lordofthecraft.omniscience.api.data.WorldVector;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;

import java.util.Objects;

public class ChangeWrapper {
    private final BaseBlock after;
    private final BlockState before;
    private final OfflinePlayer player;
    private final WorldVector vector;

    ChangeWrapper(OfflinePlayer player, WorldVector vector, BlockState before, BaseBlock after) {
        this.player = player;
        this.vector = vector;
        this.before = before;
        this.after = after;
    }

    public BlockState getBefore() {
        return before;
    }

    public BaseBlock getAfter() {
        return after;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public WorldVector getVector() {
        return vector;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, vector, before, after);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChangeWrapper)) return false;
        ChangeWrapper that = (ChangeWrapper) o;
        return player.equals(that.player) &&
                vector.equals(that.vector) &&
                Objects.equals(before, that.before) &&
                Objects.equals(after, that.after);
    }

    @Override
    public String toString() {
        return "ChangeWrapper{" +
                "player=" + player +
                ", vector=" + vector +
                ", before=" + before +
                ", after=" + after +
                '}';
    }
}

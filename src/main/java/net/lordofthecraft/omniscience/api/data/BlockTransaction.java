package net.lordofthecraft.omniscience.api.data;

import org.bukkit.block.BlockState;

import java.util.Optional;

public final class BlockTransaction {
    private final BlockState before;
    private final BlockState after;

    public BlockTransaction(BlockState before, BlockState after) {
        this.before = before;
        this.after = after;
    }

    public Optional<BlockState> getBefore() {
        return Optional.ofNullable(before);
    }

    public Optional<BlockState> getAfter() {
        return Optional.ofNullable(after);
    }
}

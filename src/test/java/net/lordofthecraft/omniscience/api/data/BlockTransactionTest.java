package net.lordofthecraft.omniscience.api.data;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class BlockTransactionTest {

    @Mock
    World world;

    @Test
    public void testGetterSetters() {
        BlockState beforeSpy = spy(BlockState.class);
        BlockState afterSpy = spy(BlockState.class);
        Location location = new Location(world, 1, 1, 1);
        BlockTransaction transaction = BlockTransaction.from(location, beforeSpy, afterSpy);
        Optional<BlockState> before = transaction.getBefore();
        Optional<BlockState> after = transaction.getAfter();
        assertTrue(before.isPresent());
        assertTrue(after.isPresent());
        assertEquals(beforeSpy, before.get());
        assertEquals(afterSpy, after.get());
        assertEquals(location, transaction.getLocation());
    }
}
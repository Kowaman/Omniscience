package net.lordofthecraft.omniscience.api.data;

import org.bukkit.block.BlockState;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class BlockTransactionTest {

    @Test
    public void testGetterSetters() {
        BlockState beforeSpy = spy(BlockState.class);
        BlockState afterSpy = spy(BlockState.class);
        BlockTransaction transaction = BlockTransaction.from(beforeSpy, afterSpy);
        Optional<BlockState> before = transaction.getBefore();
        Optional<BlockState> after = transaction.getAfter();
        assertTrue(before.isPresent());
        assertTrue(after.isPresent());
        assertEquals(beforeSpy, before.get());
        assertEquals(afterSpy, after.get());
    }
}
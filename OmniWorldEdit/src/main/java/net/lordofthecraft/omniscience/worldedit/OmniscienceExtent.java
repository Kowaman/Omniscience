package net.lordofthecraft.omniscience.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockStateHolder;

public class OmniscienceExtent extends AbstractDelegateExtent {

    /**
     * Create a new instance.
     *
     * @param extent the extent
     */
    protected OmniscienceExtent(Extent extent) {
        super(extent);
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        //TODO log
        return super.setBlock(location, block);
    }
}

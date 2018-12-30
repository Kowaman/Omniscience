package net.lordofthecraft.omniscience.worldedit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.AbstractPlayerActor;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import net.lordofthecraft.omniscience.api.data.WorldVector;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;

public class OmniscienceExtent extends AbstractDelegateExtent {

    private final org.bukkit.World bWorld;
    private OfflinePlayer player;

    public OmniscienceExtent(Extent extent, World world, Actor actor) {
        super(extent);
        bWorld = BukkitAdapter.adapt(world);
        if (actor instanceof AbstractPlayerActor) {
            AbstractPlayerActor pl = (AbstractPlayerActor) actor;
            this.player = Bukkit.getOfflinePlayer(pl.getUniqueId());
        }
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 location, T block) throws WorldEditException {
        WorldVector vector = new WorldVector(bWorld.getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
        BlockState bukkitBlock = bWorld.getBlockAt(vector.asLocation()).getState();
        boolean result = super.setBlock(location, block);

        if (result) {
            BaseBlock bBlock = (BaseBlock) block;
            WorldEditChangeQueue.submit(new ChangeWrapper(player, vector, bukkitBlock, bBlock));
        }
        return result;
    }
}

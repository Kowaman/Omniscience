package net.lordofthecraft.omniscience.blockedit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

public class StandardBlockEditor implements BlockEditor {

    @Override
    public void changeBlock(Vector location, World world, BlockData toChangeTo) {
        Location bukkitLocation = new Location(world, location.getX(), location.getY(), location.getZ());
        bukkitLocation.getBlock().setBlockData(toChangeTo);
    }
}

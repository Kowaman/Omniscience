package net.lordofthecraft.omniscience.blockedit;

import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

public interface BlockEditor {

    void changeBlock(Vector location, World world, BlockData toChangeTo);
}

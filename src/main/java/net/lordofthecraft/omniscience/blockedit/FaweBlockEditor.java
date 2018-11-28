package net.lordofthecraft.omniscience.blockedit;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.example.NMSMappedFaweQueue;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.util.Vector;

public class FaweBlockEditor implements BlockEditor {

    private NMSMappedFaweQueue queue;

    public FaweBlockEditor(World world) {
        this.queue = (NMSMappedFaweQueue) FaweAPI.createQueue(world.getName(), true);
    }

    @Override
    public void changeBlock(Vector location, World world, BlockData toChangeTo) {
        //TODO somehow map block data to the fawe queue? hell if I know
    }
}

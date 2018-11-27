package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.util.DataHelper;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.Optional;

import static net.lordofthecraft.omniscience.api.data.DataKeys.LOCATION;
import static net.lordofthecraft.omniscience.api.data.DataKeys.ORIGINAL_BLOCK;

public class BlockEntry extends DataEntryComplete implements Actionable {


    @Override
    public ActionResult rollback() throws Exception {
        Object original = data.get(ORIGINAL_BLOCK)
                .orElseThrow(() -> new IllegalAccessException("Rollback was called on an entry that doesn't support rollback!"));

        if (!(original instanceof DataWrapper)) {
            //TODO return invalid result
            return null;
        }

        DataWrapper originalBlock = (DataWrapper) original;

        Optional<BlockData> oData = DataHelper.getBlockDataFromWrapper(originalBlock);
        Optional<Location> oLocation = DataHelper.getLocationFromDataWrapper((DataWrapper) originalBlock.get(LOCATION)
                .orElseThrow(() -> new IllegalArgumentException("The data for this block doesn't have a location!")));
        if (oData.isPresent()
                && oLocation.isPresent()) {
            BlockData data = oData.get();
            Location location = oLocation.get();
            //TODO is this really the best place to do this?
            location.getBlock().setBlockData(data);
        }
        return null;
    }
}

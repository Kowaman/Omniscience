package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.data.LocationTransaction;
import net.lordofthecraft.omniscience.util.DataHelper;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemEntry extends DataEntryComplete implements Actionable {

    @Override
    public ActionResult rollback() throws Exception {
        Location location = DataHelper.getLocationFromDataWrapper(
                data.getWrapper(DataKeys.LOCATION).orElseThrow(() -> skipped(SkipReason.INVALID_LOCATION))
        ).orElseThrow(() -> skipped(SkipReason.INVALID_LOCATION));

        if (!(location.getBlock().getState() instanceof Container)) {
            throw skipped(SkipReason.INVALID);
        }

        int slotAffected = data.getInt(DataKeys.ITEM_SLOT)
                .orElseThrow(() -> skipped(SkipReason.INVALID));

        ItemStack item = DataHelper.loadFromString(
                data.getString(DataKeys.ITEMDATA).orElseThrow(() -> skipped(SkipReason.INVALID))
        ).orElseThrow(() -> skipped(SkipReason.INVALID));

        Container container = (Container) location.getBlock().getState();

        Inventory snapshot = container.getSnapshotInventory();

        container.getInventory().setItem(slotAffected, item);

        return ActionResult.success(new LocationTransaction<>(location, snapshot, container.getSnapshotInventory()));
    }

    @Override
    public ActionResult restore() throws Exception {
        return ActionResult.skipped(SkipReason.UNIMPLEMENTED);
    }
}

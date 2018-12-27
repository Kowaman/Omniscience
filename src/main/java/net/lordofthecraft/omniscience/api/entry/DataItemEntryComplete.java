package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.data.LocationTransaction;
import net.lordofthecraft.omniscience.util.DataHelper;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class DataItemEntryComplete extends DataEntryComplete {

    protected ActionResult rollbackEntry(boolean take) throws Exception {
        Location location = DataHelper.getLocationFromDataWrapper(data)
                .orElseThrow(() -> new ActionableException(ActionResult.skipped(SkipReason.INVALID_LOCATION)));

        if (!(location.getBlock().getState() instanceof Container)) {
            throw new ActionableException(ActionResult.skipped(SkipReason.INVALID));
        }

        int slotAffected = data.getInt(DataKeys.ITEM_SLOT)
                .orElseThrow(() -> new ActionableException(ActionResult.skipped(SkipReason.INVALID)));

        ItemStack item = (ItemStack) data.getConfigSerializable(DataKeys.ITEMSTACK).orElseThrow(() -> new ActionableException(ActionResult.skipped(SkipReason.INVALID)));

        Container container = (Container) location.getBlock().getState();

        Inventory snapshot = container.getSnapshotInventory();

        ItemStack targetItem = container.getInventory().getItem(slotAffected);

        if (take) {
            if (targetItem != null && targetItem.isSimilar(item)) {
                int targetAmount = targetItem.getAmount();
                int itemAmount = item.getAmount();
                if (targetAmount - itemAmount <= 0) {
                    container.getInventory().setItem(slotAffected, null);
                    return ActionResult.success(new LocationTransaction<>(location, snapshot, container.getSnapshotInventory()));
                }
                item.setAmount(targetAmount - itemAmount);
            }

            container.getInventory().setItem(slotAffected, item);
        } else {
            if (targetItem != null && targetItem.isSimilar(item)) {
                int targetAmount = targetItem.getAmount();
                int itemAmount = item.getAmount();
                if (targetAmount + itemAmount > targetItem.getType().getMaxStackSize()) {
                    Omniscience.getPluginInstance().getLogger().warning("Error: Attempted to rollback an inventory @ " + DataHelper.writeLocationToString(container.getLocation())
                            + " with an invalid itemstack count of " + (itemAmount + targetAmount)
                            + " for the slot " + slotAffected
                            + " for the item " + item);
                    throw new ActionableException(ActionResult.skipped(SkipReason.INVALID));
                }
                item.setAmount(targetAmount + itemAmount);
            }

            container.getInventory().setItem(slotAffected, item);
        }

        return ActionResult.success(new LocationTransaction<>(location, snapshot, container.getSnapshotInventory()));
    }
}

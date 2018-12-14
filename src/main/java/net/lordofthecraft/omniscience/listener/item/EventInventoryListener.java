package net.lordofthecraft.omniscience.listener.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.lordofthecraft.omniscience.Omniscience;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

public class EventInventoryListener extends OmniListener {

    public EventInventoryListener() {
        super(ImmutableList.of("withdraw", "deposit", "clone"));
    }

    /**
     * If this looks like a monster, it is.
     * <p>
     * We're going through and trying to filter down everything into either a WITHDRAW action or a DEPOSIT action. This means that sometimes, just sometimes,
     * we'll trigger both events - as is the case when we "swap" items on our cursor. There are some headaches we have to go and figure out - such as, wonderfully, how
     * in the world we handle when someone double clicks a stack to grab <i>everything</i> of that type. So here we are, trying to explain the abomination that is this
     * event handler in such a way that God won't suddenly decide we've gone too far and annihilate humanity as to prevent this work from ever seeing the light of day
     *
     * @param e Don't directly call this method for the love of all that is good and holy in the WORLD
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent e) {
        debugEvent(e);
        if (isEnabled("clone") && e.getAction() == InventoryAction.CLONE_STACK) {
            ItemStack cloned = e.getCurrentItem();
            OEntry.create().player(e.getWhoClicked()).cloned(cloned).save();
            return;
        }
        if (e.getInventory().getHolder() instanceof Container && (w() || d())) {
            Container container = (Container) e.getInventory().getHolder();
            boolean inInventory = e.getRawSlot() < e.getInventory().getSize();
            Omniscience.logDebug("inInventory? " + inInventory);
            switch (e.getAction()) {
                case NOTHING:
                    return;
                case PICKUP_ALL:
                    if (inInventory && w()) {
                        ItemStack is = e.getCurrentItem();
                        int clicked = e.getSlot();
                        OEntry.create().player(e.getWhoClicked()).withdrew(container, is, clicked).save();
                    }
                    break;
                case PICKUP_SOME:
                    if (inInventory && w()) {
                        ItemStack cursor = e.getCursor().clone();
                        int clicked = e.getSlot();
                        ItemStack is = e.getCurrentItem().clone();
                        if (cursor != null && is != null && cursor.isSimilar(is)) {
                            int neededForMax = cursor.getMaxStackSize() - cursor.getAmount();
                            int withdrew = is.getAmount() < neededForMax ? is.getAmount() : neededForMax;
                            is.setAmount(withdrew);
                            OEntry.create().player(e.getWhoClicked()).withdrew(container, is, clicked).save();
                        }
                    }
                    break;
                case PICKUP_HALF:
                    if (inInventory && w()) {
                        int clicked = e.getSlot();
                        ItemStack is = e.getCurrentItem().clone();
                        if (is != null && !is.getType().name().contains("AIR")) {
                            final int amount;
                            boolean uneven = false;
                            if (is.getAmount() == 1 || is.getAmount() == 2) {
                                amount = 1;
                            } else {
                                if (is.getAmount() % 2 == 1) {
                                    uneven = true;
                                    amount = (is.getAmount() - 1) / 2;
                                } else {
                                    amount = is.getAmount() / 2;
                                }
                            }
                            is.setAmount(uneven ? amount + 1 : amount);
                            OEntry.create().player(e.getWhoClicked()).withdrew(container, is, clicked).save();
                        }
                    }
                    break;
                case PICKUP_ONE:
                    if (inInventory && w()) {
                        int clicked = e.getSlot();
                        ItemStack is = e.getCurrentItem().clone();
                        //TODO we need to verify that this isnt called when the itemstack in the players hand is @ max capacity
                        if (is != null && !is.getType().name().contains("AIR")) {
                            is.setAmount(1);
                            OEntry.create().player(e.getWhoClicked()).withdrew(container, is, clicked).save();
                        }
                    }
                    break;
                case PLACE_SOME:
                    if (inInventory && d()) {
                        ItemStack cursor = e.getCursor().clone();
                        int clicked = e.getSlot();
                        ItemStack is = e.getCurrentItem().clone();
                        if (cursor != null && is != null && cursor.isSimilar(is)) {
                            int neededForMax = is.getMaxStackSize() - is.getAmount();
                            int deposited = cursor.getAmount() < neededForMax ? cursor.getAmount() : neededForMax;
                            cursor.setAmount(deposited);
                            //TODO This will work for showing how many of an item someone placed into a container but the rollback may be fucky.
                            // E.g. if I put 13 into an inventory to make 64, it might just roll it back to 13.
                            OEntry.create().player(e.getWhoClicked()).deposited(container, cursor, clicked).save();
                        }
                    }
                    break;
                case PLACE_ALL:
                    if (inInventory && d()) {
                        ItemStack is = e.getCursor();
                        int clicked = e.getSlot();
                        OEntry.create().player(e.getWhoClicked()).deposited(container, is, clicked).save();
                    }
                    break;
                case PLACE_ONE:
                    if (inInventory && d()) {
                        ItemStack cursor = e.getCursor().clone();
                        int clicked = e.getSlot();
                        ItemStack is = e.getCurrentItem().clone();
                        if (cursor != null) {
                            if (is != null && is.isSimilar(cursor)) {
                                if (is.getMaxStackSize() - is.getAmount() >= 1) {
                                    cursor.setAmount(1);
                                    OEntry.create().source(e.getWhoClicked()).deposited(container, cursor, clicked).save();
                                    return;
                                }
                            } else {
                                cursor.setAmount(1);
                                OEntry.create().player(e.getWhoClicked()).deposited(container, cursor, clicked).save();
                            }
                        }
                    }
                    break;
                case SWAP_WITH_CURSOR:
                    if (inInventory) {
                        ItemStack cursor = e.getCursor();
                        ItemStack toSwap = e.getCurrentItem();
                        int clicked = e.getSlot();
                        if (d()) {
                            OEntry.create().player(e.getWhoClicked()).deposited(container, cursor, clicked).save();
                        }
                        if (w()) {
                            OEntry.create().player(e.getWhoClicked()).withdrew(container, toSwap, clicked).save();
                        }
                    }
                    break;
                case DROP_ALL_CURSOR:
                case DROP_ONE_CURSOR:
                    //NO:OP
                    break;
                case DROP_ALL_SLOT:
                case DROP_ONE_SLOT:
                    if (inInventory) {
                        ItemStack item = e.getCurrentItem().clone();
                        OEntry.create().player(e.getWhoClicked()).withdrew(container, item, e.getSlot()).save();
                    }
                    break;
                case MOVE_TO_OTHER_INVENTORY:
                    //TODO Doesn't fire when inventory is full but stacks are mergable
                    if ((inInventory && !w()) || (!inInventory && !d())) {
                        return;
                    }
                    ItemStack is = e.getCurrentItem().clone();
                    Inventory tar = inInventory ? e.getWhoClicked().getInventory() : e.getInventory();
                    int leftOver = is.getAmount();
                    if (tar.all(e.getCurrentItem()).size() > 0) {
                        Map<Integer, ? extends ItemStack> items = tar.all(e.getCurrentItem());
                        Map<Integer, ItemStack> changedItems = Maps.newHashMap();

                        for (Map.Entry<Integer, ? extends ItemStack> entry : items.entrySet()) {
                            ItemStack invItem = entry.getValue().clone();
                            int diff = invItem.getMaxStackSize() - invItem.getAmount();
                            // Item amount = 16
                            // 64 - 61 = 3: diff is 3.
                            // 16 - 3 = 13, aka amt - diff = leftover
                            // 3 items were placed into the inventory at this location
                            if (diff > 0) {
                                if (leftOver - diff <= 0) {
                                    invItem.setAmount(leftOver);
                                    leftOver -= diff;
                                    changedItems.put(entry.getKey(), invItem);
                                    break;
                                } else {
                                    invItem.setAmount(diff);
                                    leftOver -= diff;
                                    changedItems.put(entry.getKey(), invItem);
                                }
                            }
                        }
                        Omniscience.logDebug("Changed Items: " + changedItems);
                        if (inInventory && w()) {
                            changedItems.forEach((key, value) -> OEntry.create().player(e.getWhoClicked()).withdrew(container, value, key).save());
                        } else if (!inInventory && d()) {
                            changedItems.forEach((key, value) -> OEntry.create().player(e.getWhoClicked()).deposited(container, value, key).save());
                        }
                    }
                    if (tar.firstEmpty() != -1 && leftOver > 0) {
                        is.setAmount(leftOver > is.getMaxStackSize() ? is.getMaxStackSize() : leftOver);
                        if (inInventory && w()) {
                            OEntry.create().player(e.getWhoClicked()).withdrew(container, is, tar.firstEmpty()).save();
                        } else if (!inInventory && d()) {
                            OEntry.create().player(e.getWhoClicked()).deposited(container, is, tar.firstEmpty()).save();
                        }
                    }
                    break;
                case HOTBAR_MOVE_AND_READD:
                    if (inInventory) {
                        int slot = e.getHotbarButton() - 1;
                        ItemStack item = e.getWhoClicked().getInventory().getItem(slot).clone();
                        ItemStack current = e.getCurrentItem().clone();
                        if (d()
                                && item.isSimilar(current)
                                && current.getAmount() < current.getMaxStackSize()) {
                            int toCap = current.getMaxStackSize() - current.getAmount();
                            if (toCap < item.getAmount()) {
                                item.setAmount(toCap);
                            }
                            OEntry.create().player(e.getWhoClicked()).deposited(container, item, e.getSlot()).save();
                        } else if (d() && current == null) {
                            OEntry.create().player(e.getWhoClicked()).deposited(container, item, e.getSlot()).save();
                        } else if (!current.isSimilar(item)) {
                            if (w()) {
                                OEntry.create().player(e.getWhoClicked()).withdrew(container, current, e.getSlot()).save();
                            }
                            if (d() && item != null && !item.getType().name().contains("AIR")) {
                                OEntry.create().player(e.getWhoClicked()).deposited(container, item, e.getSlot()).save();
                            }
                        }
                    }
                    break;
                case HOTBAR_SWAP:
                    if (inInventory) {
                        int slot = e.getHotbarButton() - 1;
                        ItemStack item = e.getWhoClicked().getInventory().getItem(slot).clone();
                        ItemStack toSwap = e.getCurrentItem().clone();
                        if (w() && toSwap != null && !toSwap.getType().name().contains("AIR")) {
                            OEntry.create().player(e.getWhoClicked()).withdrew(container, toSwap, e.getSlot()).save();
                        }
                        if (d() && item != null && !item.getType().name().contains("AIR")) {
                            OEntry.create().player(e.getWhoClicked()).deposited(container, item, e.getSlot()).save();
                        }
                    }
                    break;
                case CLONE_STACK:
                    //NO:OP
                    return;
                case COLLECT_TO_CURSOR:
                    //TODO doesn't fire at all when collecting items that are in the container
                    InventoryView view = e.getView();
                    ItemStack targetItem = e.getCurrentItem().clone();
                    int currentAmount = targetItem.getAmount();
                    int spaceLeft = targetItem.getMaxStackSize() - currentAmount;
                    Map<Integer, ? extends ItemStack> containerInventory = container.getInventory().all(targetItem);
                    Map<Integer, ? extends ItemStack> playerInventory = e.getWhoClicked().getInventory().all(targetItem);
                    Map<ItemWrapper, ItemStack> changedItems = Maps.newHashMap();
                    for (Map.Entry<Integer, ? extends ItemStack> entry : containerInventory.entrySet()) {
                        ItemStack invItem = entry.getValue().clone();
                        int itemAmount = invItem.getAmount();
                        if (spaceLeft - itemAmount <= 0) {
                            changedItems.put(new ItemWrapper(true, entry.getKey()), invItem);
                            spaceLeft -= itemAmount;
                            break;
                        } else {
                            spaceLeft -= itemAmount;
                            changedItems.put(new ItemWrapper(true, entry.getKey()), invItem);
                        }
                    }
                    if (spaceLeft > 0) {
                        for (Map.Entry<Integer, ? extends ItemStack> entry : playerInventory.entrySet()) {
                            ItemStack invItem = entry.getValue().clone();
                            int itemAmount = invItem.getAmount();
                            if (spaceLeft - itemAmount <= 0) {
                                changedItems.put(new ItemWrapper(false, entry.getKey()), invItem);
                                spaceLeft -= itemAmount;
                                break;
                            } else {
                                spaceLeft -= itemAmount;
                                changedItems.put(new ItemWrapper(false, entry.getKey()), invItem);
                            }
                        }
                    }
                    Omniscience.logDebug("ChangedItems: " + changedItems);
                    for (Map.Entry<ItemWrapper, ItemStack> item : changedItems.entrySet()) {
                        if (item.getKey().top && w()) {
                            OEntry.create().player(e.getWhoClicked()).withdrew(container, item.getValue(), item.getKey().slot).save();
                        }
                        //TODO I think you can put all the items in another inventory with another method? that's important.
                    }
                    break;
                case UNKNOWN:
                    break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent e) {
        //TODO new items are the items AFTER placement. May cause rollback issues.
        if (e.getInventory().getHolder() instanceof Container) {
            Container container = (Container) e.getInventory().getHolder();
            e.getNewItems().forEach((key, value) -> {
                if (key < container.getInventory().getSize()) {
                    OEntry.create().player(e.getWhoClicked()).deposited(container, value, key).save();
                }
            });
        }
    }

    private class ItemWrapper {
        private final boolean top;
        private final int slot;

        ItemWrapper(boolean top, int slot) {
            this.top = top;
            this.slot = slot;
        }

        public boolean isTop() {
            return top;
        }

        public int getSlot() {
            return slot;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemWrapper that = (ItemWrapper) o;
            return top == that.top &&
                    slot == that.slot;
        }

        @Override
        public int hashCode() {
            return Objects.hash(top, slot);
        }

        @Override
        public String toString() {
            return "ItemWrapper{" +
                    "top=" + top +
                    ", slot=" + slot +
                    '}';
        }
    }

    private boolean w() {
        return isEnabled("withdraw");
    }

    private boolean d() {
        return isEnabled("deposit");
    }

    private void debugEvent(InventoryClickEvent e) {
        Omniscience.logDebug("====== INVENTORY CLICK EVENT DEBUG ======");
        Omniscience.logDebug("Action: " + e.getAction());
        Omniscience.logDebug("Click: " + e.getClick());
        Omniscience.logDebug("Current Item: " + e.getCurrentItem());
        Omniscience.logDebug("Cursor Item: " + e.getCursor());
        Omniscience.logDebug("Hotbar button: " + e.getHotbarButton());
        Omniscience.logDebug("Slot: " + e.getSlot());
        Omniscience.logDebug("Slot Type: " + e.getSlotType());
        Omniscience.logDebug("Raw Slot: " + e.getRawSlot());
        Omniscience.logDebug("Inventory Clicked: " + e.getInventory());
        Omniscience.logDebug("Inventory in general: " + e.getInventory());
    }
}

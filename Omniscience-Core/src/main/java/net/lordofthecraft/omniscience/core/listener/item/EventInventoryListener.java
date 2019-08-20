package net.lordofthecraft.omniscience.core.listener.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.lordofthecraft.omniscience.api.data.Transaction;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.core.Omniscience;
import net.lordofthecraft.omniscience.core.listener.OmniListener;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
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
        if (isEnabled("clone") && e.getAction() == InventoryAction.CLONE_STACK) {
            ItemStack cloned = e.getCurrentItem();
            OEntry.create().player(e.getWhoClicked()).cloned(cloned).save();
            return;
        }
        if ((e.getInventory().getHolder() instanceof Container || e.getInventory().getHolder() instanceof DoubleChest) && (w() || d())) {
            InventoryHolder holder = e.getInventory().getHolder();
            boolean inInventory = e.getRawSlot() < e.getInventory().getSize();
            switch (e.getAction()) {
                case NOTHING:
                    return;
                case PICKUP_ALL:
                    if (inInventory && w()) {
                        ItemStack is = e.getCurrentItem().clone();
                        int clicked = e.getSlot();
                        OEntry.create().player(e.getWhoClicked()).withdrew(holder, is, clicked, new Transaction<>(is, null)).save();
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
                            ItemStack newIs = is.clone();
                            ItemStack leftOver = is.clone();
                            newIs.setAmount(withdrew);
                            if (is.getAmount() - withdrew > 0) {
                                leftOver.setAmount(is.getAmount() - withdrew);
                                OEntry.create().player(e.getWhoClicked()).withdrew(holder, newIs, clicked, new Transaction<>(is, leftOver)).save();
                            } else {
                                OEntry.create().player(e.getWhoClicked()).withdrew(holder, newIs, clicked, new Transaction<>(is, null)).save();
                            }
                        }
                    }
                    break;
                case PICKUP_HALF:
                    if (inInventory && w()) {
                        int clicked = e.getSlot();
                        ItemStack is = e.getCurrentItem().clone();
                        if (is != null && !is.getType().name().contains("AIR")) {
                            final int amount;
                            if (is.getAmount() == 1 || is.getAmount() == 2) {
                                amount = 1;
                            } else {
                                if (is.getAmount() % 2 == 1) {
                                    amount = ((is.getAmount() - 1) / 2) + 1;
                                } else {
                                    amount = is.getAmount() / 2;
                                }
                            }
                            ItemStack newIs = is.clone();
                            newIs.setAmount(amount);

                            if (is.getAmount() - amount > 0) {
                                ItemStack leftOver = is.clone();
                                leftOver.setAmount(is.getAmount() - amount);
                                OEntry.create().player(e.getWhoClicked()).withdrew(holder, newIs, clicked, new Transaction<>(is, leftOver)).save();
                            } else {
                                OEntry.create().player(e.getWhoClicked()).withdrew(holder, newIs, clicked, new Transaction<>(is, null)).save();
                            }

                        }
                    }
                    break;
                case PICKUP_ONE:
                    if (inInventory && w()) {
                        int clicked = e.getSlot();
                        ItemStack is = e.getCurrentItem().clone();
                        //TODO we need to verify that this isnt called when the itemstack in the players hand is @ max capacity
                        if (is != null
                                && !is.getType().name().contains("AIR")) {
                            ItemStack newIs = is.clone();
                            newIs.setAmount(1);
                            if (is.getAmount() - 1 > 0) {
                                ItemStack leftOver = is.clone();
                                leftOver.setAmount(is.getAmount() - 1);
                                OEntry.create().player(e.getWhoClicked()).withdrew(holder, newIs, clicked, new Transaction<>(is, leftOver)).save();
                            } else {
                                OEntry.create().player(e.getWhoClicked()).withdrew(holder, newIs, clicked, new Transaction<>(is, null)).save();
                            }

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
                            ItemStack newCursor = cursor.clone();
                            newCursor.setAmount(deposited);
                            ItemStack afterDeposit = is.clone();
                            afterDeposit.setAmount(is.getAmount() + deposited);
                            OEntry.create().player(e.getWhoClicked()).deposited(holder, newCursor, clicked, new Transaction<>(is, afterDeposit)).save();
                        }
                    }
                    break;
                case PLACE_ALL:
                    if (inInventory && d()) {
                        ItemStack is = e.getCursor();
                        int clicked = e.getSlot();
                        OEntry.create().player(e.getWhoClicked()).deposited(holder, is, clicked, new Transaction<>(null, is)).save();
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
                                    ItemStack newCursor = cursor.clone();
                                    newCursor.setAmount(1);
                                    ItemStack afterDeposit = is.clone();
                                    afterDeposit.setAmount(is.getAmount() + 1);
                                    OEntry.create().source(e.getWhoClicked()).deposited(holder, newCursor, clicked, new Transaction<>(is, afterDeposit)).save();
                                    return;
                                }
                            } else {
                                ItemStack newCursor = cursor.clone();
                                newCursor.setAmount(1);
                                OEntry.create().player(e.getWhoClicked()).deposited(holder, newCursor, clicked, new Transaction<>(null, newCursor)).save();
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
                            OEntry.create().player(e.getWhoClicked()).deposited(holder, cursor, clicked, new Transaction<>(toSwap, cursor)).save();
                        }
                        if (w()) {
                            OEntry.create().player(e.getWhoClicked()).withdrew(holder, toSwap, clicked, new Transaction<>(toSwap, cursor)).save();
                        }
                    }
                    break;
                case DROP_ALL_CURSOR:
                case DROP_ONE_CURSOR:
                    //NO:OP
                    break;
                case DROP_ALL_SLOT:
                    if (inInventory && w()) {
                        ItemStack item = e.getCurrentItem().clone();
                        OEntry.create().player(e.getWhoClicked()).withdrew(holder, item, e.getSlot(), new Transaction<>(item, null)).save();
                    }
                    break;
                case DROP_ONE_SLOT:
                    if (inInventory && w()) {
                        ItemStack item = e.getCurrentItem().clone();

                        if (item.getAmount() - 1 > 0) {
                            ItemStack leftOver = item.clone();
                            leftOver.setAmount(item.getAmount() - 1);
                            item.setAmount(1);
                            OEntry.create().player(e.getWhoClicked()).withdrew(holder, item, e.getSlot(), new Transaction<>(e.getCurrentItem().clone(), leftOver)).save();
                        } else {
                            item.setAmount(1);
                            OEntry.create().player(e.getWhoClicked()).withdrew(holder, item, e.getSlot(), new Transaction<>(e.getCurrentItem().clone(), null)).save();
                        }
                    }
                    break;
                case MOVE_TO_OTHER_INVENTORY:
                    if ((inInventory && !w()) || (!inInventory && !d())) {
                        return;
                    }
                    ItemStack is = e.getCurrentItem().clone();
                    Inventory tar = inInventory ? e.getWhoClicked().getInventory() : e.getInventory();
                    int leftOver = is.getAmount();
                    if (tar.all(e.getCurrentItem().getType()).size() > 0) {
                        Map<Integer, ? extends ItemStack> items = tar.all(e.getCurrentItem().getType());
                        Map<Integer, ItemTransaction> changedItems = Maps.newHashMap();

                        for (Map.Entry<Integer, ? extends ItemStack> entry : items.entrySet()) {
                            ItemStack invItem = entry.getValue().clone();
                            if (is.isSimilar(invItem)) {
                                int diff = invItem.getMaxStackSize() - invItem.getAmount();
                                // Item amount = 16
                                // 64 - 61 = 3: diff is 3.
                                // 16 - 3 = 13, aka amt - diff = leftover
                                // 3 items were placed into the inventory at this location
                                if (diff > 0) {
                                    if (leftOver - diff <= 0) {
                                        invItem.setAmount(leftOver);
                                        leftOver -= diff;
                                        ItemStack afterDeposit = invItem.clone();
                                        afterDeposit.setAmount(entry.getValue().getAmount() + leftOver);
                                        changedItems.put(entry.getKey(), new ItemTransaction(invItem, new Transaction<>(entry.getValue().clone(), afterDeposit)));
                                        break;
                                    } else {
                                        invItem.setAmount(diff);
                                        leftOver -= diff;
                                        ItemStack afterDeposit = invItem.clone();
                                        afterDeposit.setAmount(entry.getValue().getAmount() + diff);
                                        changedItems.put(entry.getKey(), new ItemTransaction(invItem, new Transaction<>(entry.getValue().clone(), afterDeposit)));
                                    }
                                }
                            }
                        }
                        if (inInventory && w()) {
                            changedItems.forEach((key, value) -> OEntry.create().player(e.getWhoClicked()).withdrew(holder, value.getChangedItem(), key, value.getItemTransaction()).save());
                        } else if (!inInventory && d()) {
                            changedItems.forEach((key, value) -> OEntry.create().player(e.getWhoClicked()).deposited(holder, value.getChangedItem(), key, value.getItemTransaction()).save());
                        }
                    }
                    if (tar.firstEmpty() != -1 && leftOver > 0) {
                        is.setAmount(leftOver > is.getMaxStackSize() ? is.getMaxStackSize() : leftOver);
                        if (inInventory && w()) {
                            OEntry.create().player(e.getWhoClicked()).withdrew(holder, is, e.getSlot(), new Transaction<>(is, null)).save();
                        } else if (!inInventory && d()) {
                            OEntry.create().player(e.getWhoClicked()).deposited(holder, is, tar.firstEmpty(), new Transaction<>(null, is)).save();
                        }
                    }
                    break;
                case HOTBAR_MOVE_AND_READD:
                    if (inInventory) {
                        int slot = e.getHotbarButton();
                        ItemStack item = e.getWhoClicked().getInventory().getItem(slot).clone();
                        ItemStack current = e.getCurrentItem().clone();
                        if (d()
                                && item.isSimilar(current)
                                && current.getAmount() < current.getMaxStackSize()) {
                            int toCap = current.getMaxStackSize() - current.getAmount();
                            ItemStack newStack = item.clone();
                            if (toCap < item.getAmount()) {
                                newStack.setAmount(toCap);
                            }
                            ItemStack afterDeposit = item.clone();
                            afterDeposit.setAmount(item.getAmount() + toCap);
                            OEntry.create().player(e.getWhoClicked()).deposited(holder, newStack, e.getSlot(), new Transaction<>(item, afterDeposit)).save();
                        } else if (d() && (current == null || current.getType().name().contains("AIR"))) {
                            OEntry.create().player(e.getWhoClicked()).deposited(holder, item, e.getSlot(), new Transaction<>(null, item)).save();
                        } else if (!current.isSimilar(item)) {
                            if (w()) {
                                OEntry.create().player(e.getWhoClicked()).withdrew(holder, current, e.getSlot(), new Transaction<>(current, item)).save();
                            }
                            if (d() && item != null && !item.getType().name().contains("AIR")) {
                                OEntry.create().player(e.getWhoClicked()).deposited(holder, item, e.getSlot(), new Transaction<>(current, item)).save();
                            }
                        }
                    }
                    break;
                case HOTBAR_SWAP:
                    if (inInventory) {
                        int slot = e.getHotbarButton();

                        ItemStack item = e.getWhoClicked().getInventory().getItem(slot);
                        ItemStack toSwap = e.getCurrentItem();
                        if (w() && toSwap != null && !toSwap.getType().name().contains("AIR")) {
                            toSwap = toSwap.clone();
                            OEntry.create().player(e.getWhoClicked()).withdrew(holder, toSwap, e.getSlot(), new Transaction<>(toSwap, item)).save();
                        }
                        if (d() && item != null && !item.getType().name().contains("AIR")) {
                            item = item.clone();
                            OEntry.create().player(e.getWhoClicked()).deposited(holder, item, e.getSlot(), new Transaction<>(toSwap, item)).save();
                        }
                    }
                    break;
                case CLONE_STACK:
                    //NO:OP
                    return;
                case COLLECT_TO_CURSOR:
                    InventoryView view = e.getView();
                    ItemStack targetItem = e.getCurrentItem().clone();
                    int currentAmount = targetItem.getAmount();
                    int spaceLeft = targetItem.getMaxStackSize() - currentAmount;
                    Map<Integer, ? extends ItemStack> containerInventory = holder.getInventory().all(targetItem);
                    Map<Integer, ? extends ItemStack> playerInventory = e.getWhoClicked().getInventory().all(targetItem);
                    Map<ItemWrapper, ItemTransaction> changedItems = Maps.newHashMap();
                    for (Map.Entry<Integer, ? extends ItemStack> entry : containerInventory.entrySet()) {
                        ItemStack invItem = entry.getValue().clone();
                        int itemAmount = invItem.getAmount();
                        if (spaceLeft - itemAmount <= 0) {
                            ItemStack afterWithdraw = invItem.clone();
                            afterWithdraw.setAmount(itemAmount - spaceLeft);
                            changedItems.put(new ItemWrapper(true, entry.getKey()), new ItemTransaction(invItem, new Transaction<>(invItem, afterWithdraw)));
                            spaceLeft -= itemAmount;
                            break;
                        } else {
                            spaceLeft -= itemAmount;
                            changedItems.put(new ItemWrapper(true, entry.getKey()), new ItemTransaction(invItem, new Transaction<>(invItem, null)));
                        }
                    }
                    if (spaceLeft > 0) {
                        for (Map.Entry<Integer, ? extends ItemStack> entry : playerInventory.entrySet()) {
                            ItemStack invItem = entry.getValue().clone();
                            int itemAmount = invItem.getAmount();
                            if (spaceLeft - itemAmount <= 0) {
                                ItemStack afterWithdraw = invItem.clone();
                                afterWithdraw.setAmount(itemAmount - spaceLeft);
                                changedItems.put(new ItemWrapper(false, entry.getKey()), new ItemTransaction(invItem, new Transaction<>(invItem, afterWithdraw)));
                                spaceLeft -= itemAmount;
                                break;
                            } else {
                                spaceLeft -= itemAmount;
                                changedItems.put(new ItemWrapper(false, entry.getKey()), new ItemTransaction(invItem, new Transaction<>(invItem, null)));
                            }
                        }
                    }
                    for (Map.Entry<ItemWrapper, ItemTransaction> item : changedItems.entrySet()) {
                        if (item.getKey().top && w()) {
                            OEntry.create().player(e.getWhoClicked()).withdrew(holder, item.getValue().getChangedItem(), item.getKey().slot, item.getValue().getItemTransaction()).save();
                        }
                    }
                    break;
                case UNKNOWN:
                    break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder() instanceof Container) {
            Container container = (Container) e.getInventory().getHolder();
            e.getNewItems().forEach((key, value) -> {
                if (key < container.getInventory().getSize()) {
                    ItemStack original = container.getInventory().getItem(key);
                    if (original == null || original.getType().name().contains("AIR")) {
                        OEntry.create().player(e.getWhoClicked()).deposited(container, value, key, new Transaction<>(null, value)).save();
                    } else {
                        int diff = value.getAmount() - original.getAmount();
                        if (diff > 0) {
                            ItemStack diffItem = value.clone();
                            diffItem.setAmount(diff);
                            OEntry.create().player(e.getWhoClicked()).deposited(container, diffItem, key, new Transaction<>(original, value)).save();
                        } else {
                            OEntry.create().player(e.getWhoClicked()).deposited(container, value, key, new Transaction<>(null, value)).save();
                        }
                    }

                }
            });
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

    private class ItemWrapper {
        private final int slot;
        private final boolean top;

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
        public int hashCode() {
            return Objects.hash(top, slot);
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
        public String toString() {
            return "ItemWrapper{" +
                    "top=" + top +
                    ", slot=" + slot +
                    '}';
        }
    }

    private class ItemTransaction {
        private final ItemStack changedItem;
        private final Transaction<ItemStack> itemTransaction;

        public ItemTransaction(ItemStack changedItem, Transaction<ItemStack> itemTransaction) {
            this.changedItem = changedItem;
            this.itemTransaction = itemTransaction;
        }

        public ItemStack getChangedItem() {
            return changedItem;
        }

        public Transaction<ItemStack> getItemTransaction() {
            return itemTransaction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(changedItem, itemTransaction);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemTransaction)) return false;
            ItemTransaction that = (ItemTransaction) o;
            return changedItem.equals(that.changedItem) &&
                    itemTransaction.equals(that.itemTransaction);
        }

        @Override
        public String toString() {
            return "ItemTransaction{" +
                    "changedItem=" + changedItem +
                    ", itemTransaction=" + itemTransaction +
                    '}';
        }
    }
}

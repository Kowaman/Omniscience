package net.lordofthecraft.omniscience.listener.item;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

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
                        if (cursor != null && is != null && cursor.getType().equals(is.getType())) {
                            int neededForMax = cursor.getType().getMaxStackSize() - cursor.getAmount();
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
                        if (is != null) {
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
                            //TODO find out which side gets the extra item
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
                        if (is != null) {
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
                        if (cursor != null && is != null && cursor.getType().equals(is.getType())) {
                            int neededForMax = is.getType().getMaxStackSize() - is.getAmount();
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
                            if (is != null && is.getType().equals(cursor.getType())) {
                                if (is.getType().getMaxStackSize() - is.getAmount() >= 1) {
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
                    break;
                case DROP_ALL_SLOT:
                case DROP_ONE_SLOT:
                    break;
                case MOVE_TO_OTHER_INVENTORY:
                    if (inInventory) { // Make sure there's room in our inventory for this
                        //TODO move to other inventory math is fucking awful. this todo is to make it not awful when I'm feeling up to it
                        if (w()) {
                            ItemStack is = e.getCurrentItem().clone();
                            Inventory tar = e.getWhoClicked().getInventory();
                            if (tar.all(e.getCurrentItem()).size() > 0) {
                                Map<Integer, ? extends ItemStack> items = tar.all(e.getCurrentItem());
                                int leftOver = is.getAmount();
                                for (Map.Entry<Integer, ? extends ItemStack> entry : items.entrySet()) {
                                    ItemStack invItem = entry.getValue();
                                    int diff = invItem.getType().getMaxStackSize() - invItem.getAmount();
                                    if (diff > 0) {
                                        leftOver -= diff;
                                        if (leftOver <= 0) {
                                            break;
                                        }
                                    }
                                }
                                if (leftOver <= 0) {
                                }
                            } else if (tar.firstEmpty() != -1) {

                            }

                        }
                    } else {
                        if (d()) {
                            ItemStack is = e.getCurrentItem();
                            int clicked = e.getWhoClicked().getInventory().firstEmpty();
                            OEntry.create().player(e.getWhoClicked()).deposited(container, is, clicked).save();
                        }
                    }
                    break;
                case HOTBAR_MOVE_AND_READD:
                    break;
                case HOTBAR_SWAP:
                    break;
                case CLONE_STACK:
                    //NO:OP
                    return;
                case COLLECT_TO_CURSOR:
                    //Oh dear sweet mother of fucking god I forgot this was a thing NO GOD NO NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO

                    break;
                case UNKNOWN:
                    break;
            }
        }
    }

    private boolean w() {
        return isEnabled("withdraw");
    }

    private boolean d() {
        return isEnabled("deposit");
    }

    private void debugEvent(InventoryClickEvent e) {
        System.out.println("====== INVENTORY CLICK EVENT DEBUG ======");
        System.out.println("Action: " + e.getAction());
        System.out.println("Click: " + e.getClick());
        System.out.println("Current Item: " + e.getCurrentItem());
        System.out.println("Cursor Item: " + e.getCursor());
        System.out.println("Hotbar button: " + e.getHotbarButton());
        System.out.println("Slot: " + e.getSlot());
        System.out.println("Slot Type: " + e.getSlotType());
        System.out.println("Raw Slot: " + e.getRawSlot());
        System.out.println("Inventory Clicked: " + e.getInventory());
        System.out.println("Inventory in general: " + e.getInventory());
    }
}

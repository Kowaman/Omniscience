package net.lordofthecraft.omniscience.listener;

import net.lordofthecraft.omniscience.api.entry.OEntry;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ContainerListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory().getHolder() instanceof Container) {
            OEntry.create().source(event.getPlayer()).opened((Container) event.getInventory().getHolder()).save();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof Container) {
            OEntry.create().source(event.getPlayer()).closed((Container) event.getInventory().getHolder()).save();
        }
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
        if (e.getAction() == InventoryAction.CLONE_STACK) {
            ItemStack cloned = e.getCurrentItem();
            OEntry.create().player(e.getWhoClicked()).cloned(cloned).save();
            return;
        }
        if (e.getInventory().getHolder() instanceof Container) {
            Container container = (Container) e.getInventory().getHolder();
            boolean inInventory = e.getRawSlot() < e.getInventory().getSize();
            switch (e.getAction()) {
                case NOTHING:
                    return;
                case PICKUP_ALL:
                    if (inInventory) {
                        ItemStack is = e.getCurrentItem();
                        int clicked = e.getSlot();
                        OEntry.create().player(e.getWhoClicked()).withdrew(container, is, clicked).save();
                    }
                    break;
                case PICKUP_SOME:
                case PICKUP_HALF:
                case PICKUP_ONE:
                    //WHAT THE HELL DO I DO FOR SPLIT STACKS? AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                    break;
                case PLACE_SOME:
                    break;
                case PLACE_ALL:
                    if (inInventory) {
                        ItemStack is = e.getCursor();
                        int clicked = e.getSlot();
                        OEntry.create().player(e.getWhoClicked()).deposited(container, is, clicked).save();
                    }
                    break;
                case PLACE_ONE:
                    if (inInventory) {

                    }
                    break;
                case SWAP_WITH_CURSOR:
                    if (inInventory) {
                        ItemStack cursor = e.getCursor();
                        ItemStack toSwap = e.getCurrentItem();
                        int clicked = e.getSlot();
                        OEntry.create().player(e.getWhoClicked()).deposited(container, cursor, clicked).save();
                        OEntry.create().player(e.getWhoClicked()).withdrew(container, toSwap, clicked).save();
                    }
                    break;
                case DROP_ALL_CURSOR:
                case DROP_ONE_CURSOR:
                    break;
                case DROP_ALL_SLOT:
                case DROP_ONE_SLOT:
                    break;
                case MOVE_TO_OTHER_INVENTORY:
                    if (inInventory) {
                        ItemStack is = e.getCurrentItem();
                        int clicked = e.getSlot();
                        OEntry.create().player(e.getWhoClicked()).withdrew(container, is, clicked).save();
                    } else {
                        ItemStack is = e.getCurrentItem();
                        int clicked = e.getWhoClicked().getInventory().firstEmpty();
                        OEntry.create().player(e.getWhoClicked()).deposited(container, is, clicked).save();
                    }
                    break;
                case HOTBAR_MOVE_AND_READD:
                    break;
                case HOTBAR_SWAP:
                    break;
                case CLONE_STACK:
                    //NO:OP
                    break;
                case COLLECT_TO_CURSOR:
                    //Oh dear sweet mother of fucking god I forgot this was a thing NO GOD NO NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
                    break;
                case UNKNOWN:
                    break;
            }
        }

    }

    private int getNextFreeSlot(Inventory inventory) {
        return inventory.firstEmpty();
    }
}

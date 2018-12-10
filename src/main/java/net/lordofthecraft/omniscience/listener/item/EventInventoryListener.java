package net.lordofthecraft.omniscience.listener.item;

import com.google.common.collect.ImmutableList;
import net.lordofthecraft.omniscience.api.entry.OEntry;
import net.lordofthecraft.omniscience.listener.OmniListener;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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
                case PICKUP_HALF:
                case PICKUP_ONE:
                    //WHAT THE HELL DO I DO FOR SPLIT STACKS? AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                    break;
                case PLACE_SOME:
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
                    if (inInventory) {
                        if (w()) {
                            ItemStack is = e.getCurrentItem();
                            int clicked = e.getSlot();
                            OEntry.create().player(e.getWhoClicked()).withdrew(container, is, clicked).save();
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

package net.lordofthecraft.omniscience.api.display;

import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.util.DataHelper;
import net.lordofthecraft.omniscience.util.reflection.ReflectionHandler;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public class ItemDisplayHandler extends SimpleDisplayHandler {

    public ItemDisplayHandler() {
        super("item");
    }

    @Override
    public Optional<String> buildTargetMessage(DataEntry entry, String target, QuerySession session) {
        return Optional.empty();
    }

    @Override
    public Optional<List<String>> buildAdditionalHoverData(DataEntry entry, QuerySession session) {
        return Optional.empty();
    }

    @Override
    public Optional<TextComponent> buildTargetSpecificHoverData(DataEntry entry, String target, QuerySession session) {
        System.out.println("Beginning the display stack of item data. {" + entry + "} ((" + target + "))");
        Optional<String> oConfig = entry.data.getString(DataKeys.ITEMDATA);
        System.out.println("Item Check 1");
        if (oConfig.isPresent()) {
            System.out.println("Item Check 2");
            Optional<ItemStack> oItem = DataHelper.loadFromString(oConfig.get());
            if (oItem.isPresent()) {
                System.out.println("Item Check 3");
                ItemStack is = oItem.get();
                TextComponent component = new TextComponent(target);
                ComponentBuilder hover = new ComponentBuilder("");
                hover.append(ReflectionHandler.getItemJson(is));
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hover.create()));
                System.out.println("Item Check 4");
                return Optional.of(component);
            }
        }
        System.out.println("Item Check Error");
        return Optional.empty();
    }
}

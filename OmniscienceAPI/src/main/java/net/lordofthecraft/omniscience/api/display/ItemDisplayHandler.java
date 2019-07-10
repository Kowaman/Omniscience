package net.lordofthecraft.omniscience.api.display;

import net.lordofthecraft.omniscience.api.entry.DataEntry;
import net.lordofthecraft.omniscience.api.query.QuerySession;
import net.lordofthecraft.omniscience.api.util.reflection.ReflectionHandler;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

public class ItemDisplayHandler extends SimpleDisplayHandler {

    public ItemDisplayHandler() {
        super("item");
    }

    @Override
    public Optional<String> buildTargetMessage(DataEntry entry, String target, QuerySession session) {
        Optional<String> entity = entry.data.getString(ENTITY_TYPE);
        Optional<String> event = entry.data.getString(EVENT_NAME);
        boolean withdraw = event.isPresent() && event.get().contains("withdraw");
        return entity.map(s -> target + (withdraw ? " from " : " into ") + s);
    }

    @Override
    public Optional<List<String>> buildAdditionalHoverData(DataEntry entry, QuerySession session) {
        return Optional.empty();
    }

    @Override
    public Optional<TextComponent> buildTargetSpecificHoverData(DataEntry entry, String target, QuerySession session) {
        Optional<ItemStack> oItemStack = entry.data.getConfigSerializable(DATA.then(ITEMSTACK));
        if (oItemStack.isPresent()) {
            ItemStack is = oItemStack.get();
            TextComponent component = new TextComponent(target);
            ComponentBuilder hover = new ComponentBuilder("");
            hover.append(ReflectionHandler.getItemJson(is));
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hover.create()));
            return Optional.of(component);
        }
        return Optional.empty();
    }
}

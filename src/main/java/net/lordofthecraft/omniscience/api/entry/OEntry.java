package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.OmniEventRegistrar;
import net.lordofthecraft.omniscience.api.data.DataKey;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.api.data.LocationTransaction;
import net.lordofthecraft.omniscience.util.DataHelper;
import net.lordofthecraft.omniscience.util.SerializeHelper;
import net.lordofthecraft.omniscience.util.reflection.ReflectionHandler;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.*;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Date;

import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

public final class OEntry {
    private final SourceBuilder sourceBuilder;
    private final EventBuilder eventBuilder;

    private OEntry(SourceBuilder sourceBuilder, EventBuilder eventBuilder) {
        this.sourceBuilder = sourceBuilder;
        this.eventBuilder = eventBuilder;
    }

    public static EntryBuilder create() {
        return new EntryBuilder();
    }

    public void save() {
        if (!OmniEventRegistrar.INSTANCE.isEventRegistered(eventBuilder.getEventName())) {
            throw new IllegalArgumentException(eventBuilder.getEventName() + " is not registered with Omniscience. This must be done to continue.");
        }
        eventBuilder.getWrapper().set(EVENT_NAME, eventBuilder.getEventName());
        eventBuilder.getWrapper().set(CREATED, new Date());

        DataKey cause = (sourceBuilder.getSource() instanceof Player) ? PLAYER_ID : CAUSE;

        String causeId = "environment";
        if (sourceBuilder.getSource() instanceof Player) {
            causeId = ((Player) sourceBuilder.getSource()).getUniqueId().toString();
        } else if (sourceBuilder.getSource() instanceof Entity) {
            causeId = ((Entity) sourceBuilder.getSource()).getType().name();
        } else if (sourceBuilder.getSource() instanceof Plugin) {
            causeId = "pl@" + ((Plugin) sourceBuilder.getSource()).getName().replace(' ', '_');
        } else if (sourceBuilder.getSource() instanceof ConsoleCommandSender) {
            causeId = "console";
        } else if (sourceBuilder.getSource() instanceof RemoteConsoleCommandSender) {
            causeId = "remote_console";
        } else if (sourceBuilder.getSource() instanceof BlockCommandSender) {
            BlockCommandSender sender = (BlockCommandSender) sourceBuilder.getSource();
            CommandBlock commandBlock = (CommandBlock) sender.getBlock().getState();
            eventBuilder.getWrapper().set(X, commandBlock.getX());
            eventBuilder.getWrapper().set(Y, commandBlock.getY());
            eventBuilder.getWrapper().set(Z, commandBlock.getZ());
            eventBuilder.getWrapper().set(WORLD, commandBlock.getWorld().getUID().toString());
            causeId = "command_block";
            if (commandBlock.getName() != null) {
                causeId = causeId + " (" + commandBlock.getName() + ")";
            }
        }

        eventBuilder.getWrapper().set(cause, causeId);

        EntryQueue.submit(eventBuilder.getWrapper());
    }

    public static class SourceBuilder {
        private final Object source;

        protected SourceBuilder(Object source) {
            this.source = source;
        }

        public Object getSource() {
            return source;
        }
    }

    public static class EventBuilder {
        protected final SourceBuilder sourceBuilder;
        protected String eventName;
        protected DataWrapper wrapper = DataWrapper.createNew();

        protected EventBuilder(SourceBuilder sourceBuilder) {
            this.sourceBuilder = sourceBuilder;
        }

        protected OEntry createOEntry(SourceBuilder builder) {
            return new OEntry(builder, this);
        }

        public DataWrapper getWrapper() {
            return wrapper;
        }

        public String getEventName() {
            return eventName;
        }

        public OEntry brokeBlock(LocationTransaction<BlockState> blockTransaction) {
            this.eventName = "break";
            blockTransaction.getOriginalState().ifPresent(block -> {
                wrapper.set(ORIGINAL_BLOCK, DataWrapper.ofBlock(block));
                wrapper.set(TARGET, block.getType().name());
                writeExtraStateData(ORIGINAL_BLOCK, block);
            });
            blockTransaction.getFinalState().ifPresent(block -> {
                wrapper.set(NEW_BLOCK, DataWrapper.ofBlock(block));
                writeExtraStateData(NEW_BLOCK, block);
            });
            writeLocationData(blockTransaction.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry placedBlock(LocationTransaction<BlockState> blockTransaction) {
            this.eventName = "place";
            blockTransaction.getOriginalState().ifPresent(block -> {
                wrapper.set(ORIGINAL_BLOCK, DataWrapper.ofBlock(block));
                writeExtraStateData(ORIGINAL_BLOCK, block);
            });
            blockTransaction.getFinalState().ifPresent(block -> {
                wrapper.set(NEW_BLOCK, DataWrapper.ofBlock(block));
                wrapper.set(TARGET, block.getType().name());
                writeExtraStateData(NEW_BLOCK, block);
            });
            writeLocationData(blockTransaction.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry decayedBlock(LocationTransaction<BlockState> blockTransaction) {
            this.eventName = "decay";
            blockTransaction.getOriginalState().ifPresent(block -> {
                wrapper.set(ORIGINAL_BLOCK, DataWrapper.ofBlock(block));
                wrapper.set(TARGET, block.getType().name());
                writeExtraStateData(ORIGINAL_BLOCK, block);
            });
            blockTransaction.getFinalState().ifPresent(block -> {
                wrapper.set(NEW_BLOCK, DataWrapper.ofBlock(block));
                writeExtraStateData(NEW_BLOCK, block);
            });
            writeLocationData(blockTransaction.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry formedBlock(LocationTransaction<BlockState> blockTransaction) {
            this.eventName = "form";
            blockTransaction.getOriginalState().ifPresent(block -> {
                wrapper.set(ORIGINAL_BLOCK, DataWrapper.ofBlock(block));
                writeExtraStateData(ORIGINAL_BLOCK, block);
            });
            blockTransaction.getFinalState().ifPresent(block -> {
                wrapper.set(NEW_BLOCK, DataWrapper.ofBlock(block));
                wrapper.set(TARGET, block.getType().name());
                writeExtraStateData(NEW_BLOCK, block);
            });
            writeLocationData(blockTransaction.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry dropped(Item item) {
            this.eventName = "drop";
            wrapper.set(ITEMSTACK, item.getItemStack());
            wrapper.set(QUANTITY, item.getItemStack().getAmount());
            wrapper.set(TARGET, item.getItemStack().getType().name());
            wrapper.set(DISPLAY_METHOD, "item");
            writeLocationData(item.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry pickup(Item item) {
            this.eventName = "pickup";
            wrapper.set(ITEMSTACK, item.getItemStack());
            wrapper.set(QUANTITY, item.getItemStack().getAmount());
            wrapper.set(TARGET, item.getItemStack().getType().name());
            wrapper.set(DISPLAY_METHOD, "item");
            writeLocationData(item.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry said(String message) {
            this.eventName = "say";
            wrapper.set(TARGET, "something to Everyone");
            wrapper.set(DISPLAY_METHOD, "message");
            wrapper.set(MESSAGE, message);
            if (sourceBuilder.getSource() instanceof Entity) {
                writeLocationData(((Entity) sourceBuilder.getSource()).getLocation());
            }
            return new OEntry(sourceBuilder, this);
        }

        public OEntry ranCommand(String command) {
            this.eventName = "command";
            wrapper.set(TARGET, command.split(" ")[0]);
            wrapper.set(DISPLAY_METHOD, "message");
            wrapper.set(MESSAGE, command);
            if (sourceBuilder.getSource() instanceof Entity) {
                writeLocationData(((Entity) sourceBuilder.getSource()).getLocation());
            }
            return new OEntry(sourceBuilder, this);
        }

        public OEntry hit(Entity target) {
            this.eventName = "hit";
            wrapper.set(TARGET, target.getType().name());
            if (target instanceof Player) {
                wrapper.set(TARGET, target.getName());
            }
            writeLocationData(target.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry shot(Entity shot) {
            this.eventName = "shot";
            wrapper.set(TARGET, shot.getType().name());
            if (shot instanceof Player) {
                wrapper.set(TARGET, shot.getName());
            }
            writeLocationData(shot.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry kill(Entity killed) {
            this.eventName = "death";
            wrapper.set(TARGET, killed.getType().name());
            if (killed instanceof Player) {
                wrapper.set(TARGET, killed.getName());
            }
            wrapper.set(ENTITY_TYPE, killed.getType().name());
            wrapper.set(ENTITY, ReflectionHandler.getEntityAsBytes(killed));
            writeLocationData(killed.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry removedFromItemFrame(ItemFrame frame, ItemStack itemStack) {
            this.eventName = "frame-withdraw";
            wrapper.set(TARGET, itemStack.getType().name());
            wrapper.set(DISPLAY_METHOD, "item");
            wrapper.set(ITEMSTACK, itemStack);
            writeLocationData(frame.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry putIntoItemFrame(ItemFrame frame, ItemStack itemStack) {
            this.eventName = "frame-deposit";
            wrapper.set(TARGET, itemStack.getType().name());
            wrapper.set(DISPLAY_METHOD, "item");
            wrapper.set(ITEMSTACK, itemStack);
            writeLocationData(frame.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry opened(Container container) {
            this.eventName = "open";
            wrapper.set(TARGET, container.getType().name());
            writeLocationData(container.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry closed(Container container) {
            this.eventName = "close";
            wrapper.set(TARGET, container.getType().name());
            writeLocationData(container.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry use(Block block) {
            this.eventName = "use";
            wrapper.set(TARGET, block.getType().name());
            writeLocationData(block.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        //TODO we should really say /what/ they put the item into.
        public OEntry deposited(Container container, ItemStack itemStack, int itemSlot) {
            this.eventName = "deposit";
            wrapper.set(TARGET, itemStack.getType().name());
            wrapper.set(ITEM_SLOT, itemSlot);
            wrapper.set(ITEMSTACK, itemStack);
            wrapper.set(DISPLAY_METHOD, "item");
            writeLocationData(container.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        //TODO we should really say /what/ they took the item from
        public OEntry withdrew(Container container, ItemStack itemStack, int itemSlot) {
            this.eventName = "withdraw";
            wrapper.set(TARGET, itemStack.getType().name());
            wrapper.set(ITEM_SLOT, itemSlot);
            wrapper.set(ITEMSTACK, itemStack);
            wrapper.set(DISPLAY_METHOD, "item");
            writeLocationData(container.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry ignited(Block block) {
            this.eventName = "ignite";
            wrapper.set(TARGET, block.getType().name());
            writeLocationData(block.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry named(Entity entity, String originalName, String newName) {
            this.eventName = "named";
            wrapper.set(TARGET, (originalName == null
                                ? entity.getType().name()
                                : originalName + " (" + entity.getType().name() +  ")") + " to " + (newName== null ? "" : newName));
            wrapper.set(ENTITY_TYPE, entity.getType().name());
            wrapper.set(ENTITY_ID, entity.getUniqueId());
            writeLocationData(entity.getLocation());

            if (originalName != null) wrapper.set(NAME.then(BEFORE), originalName);
            wrapper.set(NAME.then(AFTER), newName== null ? "" : newName);

            return new OEntry(sourceBuilder, this);
        }


        public OEntry custom(String eventName, DataWrapper wrapperData) {
            this.eventName = eventName;
            wrapperData.getKeys(false).forEach(key -> {
                wrapper.set(key, wrapperData.get(key));
            });
            return new OEntry(sourceBuilder, this);
        }

        public OEntry customWithLocation(String eventName, DataWrapper wrapperData, Location location) {
            this.eventName = eventName;
            wrapperData.getKeys(false).forEach(key -> {
                wrapper.set(key, wrapperData.get(key));
            });
            writeLocationData(location);
            return new OEntry(sourceBuilder, this);
        }

        protected void writeExtraStateData(DataKey keyToWrite, BlockState state) {
            if (state instanceof Sign) {
                wrapper.set(keyToWrite.then(SIGN_TEXT), SerializeHelper.serializeStringArray(((Sign) state).getLines()));
            } else if (state instanceof Container) {
                wrapper.set(keyToWrite.then(INVENTORY), DataHelper.convertItemList(((Container) state).getInventory().getContents())); //TODO let's implement this inventory saving
            } else if (state instanceof Banner) {
                //TODO save banner data?
            }
        }

        protected void writeLocationData(Location location) {
            wrapper.set(LOCATION.then(X), location.getBlockX());
            wrapper.set(LOCATION.then(Y), location.getBlockY());
            wrapper.set(LOCATION.then(Z), location.getBlockZ());
            wrapper.set(LOCATION.then(WORLD), location.getWorld().getUID().toString());
        }
    }

    public static final class EntryBuilder {

        public EventBuilder source(Object source) {

            if (source instanceof OfflinePlayer) {
                return new EventBuilder(new SourceBuilder(source));
            }

            if (source instanceof LivingEntity) {
                return new EventBuilder(new SourceBuilder(source));
            }

            if (source instanceof Projectile) {
                Projectile projectile = (Projectile) source;
                return new EventBuilder(new SourceBuilder(projectile.getShooter()));
            }

            if (source instanceof JavaPlugin) {
                return new EventBuilder(new SourceBuilder(source));
            }

            return new EventBuilder(new SourceBuilder("Environment"));
        }

        public PlayerEventBuilder player(OfflinePlayer player) {
            return new PlayerEventBuilder(new SourceBuilder(player));
        }

        public PlayerEventBuilder player(HumanEntity player) {
            return new PlayerEventBuilder(new SourceBuilder(player));
        }

        public EventBuilder entity(Entity entity) {
            return new EventBuilder(new SourceBuilder(entity));
        }

        public EventBuilder plugin(JavaPlugin plugin) {
            return new EventBuilder(new SourceBuilder(plugin));
        }

        public EventBuilder environment() {
            return new EventBuilder(new SourceBuilder(null));
        }
    }

    public static class PlayerEventBuilder extends EventBuilder {

        protected PlayerEventBuilder(SourceBuilder sourceBuilder) {
            super(sourceBuilder);
        }

        private Player player() {
            return (Player) sourceBuilder.getSource();
        }

        public OEntry signInteract(Location location, Sign sign) {
            this.eventName = "useSign";
            wrapper.set(TARGET, sign.getType().name());
            wrapper.set(ORIGINAL_BLOCK, sign.getBlockData().getAsString());
            wrapper.set(SIGN_TEXT, SerializeHelper.serializeStringArray(sign.getLines()));
            writeLocationData(location);
            return new OEntry(sourceBuilder, this);
        }

        public OEntry cloned(ItemStack itemStack) {
            this.eventName = "clone";
            wrapper.set(TARGET, itemStack.getType().name());
            wrapper.set(ITEMSTACK, itemStack);
            wrapper.set(DISPLAY_METHOD, "item");
            writeLocationData(player().getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry quit() {
            this.eventName = "quit";
            wrapper.set(TARGET, player().getAddress().getHostName());
            writeLocationData(player().getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry joined(String host) {
            this.eventName = "join";
            wrapper.set(TARGET, host);
            writeLocationData(player().getLocation());
            return new OEntry(sourceBuilder, this);
        }
    }
}

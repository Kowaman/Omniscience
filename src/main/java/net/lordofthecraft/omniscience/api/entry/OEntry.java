package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.api.data.BlockTransaction;
import net.lordofthecraft.omniscience.api.data.DataKey;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.util.DataHelper;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.type.Sign;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.*;
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
        eventBuilder.getWrapper().set(EVENT_NAME, eventBuilder.getEventName());
        eventBuilder.getWrapper().set(CREATED, new Date());

        DataKey cause = (sourceBuilder.getSource() instanceof Player) ? PLAYER_ID : CAUSE;

        String causeId = "environment";
        if (sourceBuilder.getSource() instanceof Player) {
            causeId = ((Player) sourceBuilder.getSource()).getUniqueId().toString();
        } else if (sourceBuilder.getSource() instanceof Entity) {
            causeId = ((Entity) sourceBuilder.getSource()).getType().name();
        } else if (sourceBuilder.getSource() instanceof Plugin) {
            causeId = "pl@" + ((Plugin) sourceBuilder.getSource()).getName();
        } else if (sourceBuilder.getSource() instanceof ConsoleCommandSender) {
            causeId = "console";
        } else if (sourceBuilder.getSource() instanceof RemoteConsoleCommandSender) {
            causeId = "remote console";
        } else if (sourceBuilder.getSource() instanceof BlockCommandSender) {
            BlockCommandSender sender = (BlockCommandSender) sourceBuilder.getSource();
            CommandBlock commandBlock = (CommandBlock) sender.getBlock().getState();
            eventBuilder.getWrapper().set(X, commandBlock.getX());
            eventBuilder.getWrapper().set(Y, commandBlock.getY());
            eventBuilder.getWrapper().set(Z, commandBlock.getZ());
            eventBuilder.getWrapper().set(WORLD, commandBlock.getWorld().getUID().toString());
            causeId = "command block";
            if (commandBlock.getName() != null) {
                causeId = causeId + " (" + commandBlock.getName() + ")";
            }
        }

        eventBuilder.getWrapper().set(cause, causeId);

        EntryQueue.submit(eventBuilder.getWrapper());
    }

    public static class SourceBuilder {
        private final Object source;

        SourceBuilder(Object source) {
            this.source = source;
        }

        public Object getSource() {
            return source;
        }
    }

    public static class EventBuilder {
        final SourceBuilder sourceBuilder;
        String eventName;
        DataWrapper wrapper = DataWrapper.createNew();

        EventBuilder(SourceBuilder sourceBuilder) {
            this.sourceBuilder = sourceBuilder;
        }

        public DataWrapper getWrapper() {
            return wrapper;
        }

        public String getEventName() {
            return eventName;
        }

        public OEntry brokeBlock(BlockTransaction blockTransaction) {
            this.eventName = "break";
            blockTransaction.getBefore().ifPresent(block -> {
                wrapper.set(ORIGINAL_BLOCK, DataWrapper.ofBlock(block));
                wrapper.set(TARGET, block.getType().name());
            });
            blockTransaction.getAfter().ifPresent(block -> wrapper.set(NEW_BLOCK, DataWrapper.ofBlock(block)));
            writeLocationData(blockTransaction.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry placedBlock(BlockTransaction blockTransaction) {
            this.eventName = "place";
            blockTransaction.getBefore().ifPresent(block -> wrapper.set(ORIGINAL_BLOCK, DataWrapper.ofBlock(block)));
            blockTransaction.getAfter().ifPresent(block -> {
                wrapper.set(NEW_BLOCK, DataWrapper.ofBlock(block));
                wrapper.set(TARGET, block.getType().name());
            });
            writeLocationData(blockTransaction.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry decayedBlock(BlockTransaction blockTransaction) {
            this.eventName = "decay";
            blockTransaction.getBefore().ifPresent(block -> {
                wrapper.set(ORIGINAL_BLOCK, DataWrapper.ofBlock(block));
                wrapper.set(TARGET, block.getType().name());
            });
            blockTransaction.getAfter().ifPresent(block -> wrapper.set(NEW_BLOCK, DataWrapper.ofBlock(block)));
            writeLocationData(blockTransaction.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry formedBlock(BlockTransaction blockTransaction) {
            this.eventName = "form";
            blockTransaction.getBefore().ifPresent(block -> wrapper.set(ORIGINAL_BLOCK, DataWrapper.ofBlock(block)));
            blockTransaction.getAfter().ifPresent(block -> {
                wrapper.set(NEW_BLOCK, DataWrapper.ofBlock(block));
                wrapper.set(TARGET, block.getType().name());
            });
            writeLocationData(blockTransaction.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry dropped(Item item) {
            this.eventName = "drop";
            wrapper.set(ITEMSTACK, DataWrapper.ofConfig(item.getItemStack()));
            wrapper.set(QUANTITY, item.getItemStack().getAmount());
            wrapper.set(TARGET, item.getItemStack().getType().name());
            wrapper.set(ITEMDATA, DataHelper.convertConfigurationSerializable(item.getItemStack()));
            wrapper.set(DISPLAY_METHOD, "item");
            writeLocationData(item.getLocation());
            return new OEntry(sourceBuilder, this);
        }

        public OEntry pickup(Item item) {
            this.eventName = "pickup";
            wrapper.set(ITEMSTACK, DataWrapper.ofConfig(item.getItemStack()));
            wrapper.set(QUANTITY, item.getItemStack().getAmount());
            wrapper.set(TARGET, item.getItemStack().getType().name());
            wrapper.set(ITEMDATA, DataHelper.convertConfigurationSerializable(item.getItemStack()));
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

        protected void writeLocationData(Location location) {
            wrapper.set(LOCATION.then(X), location.getBlockX());
            wrapper.set(LOCATION.then(Y), location.getBlockY());
            wrapper.set(LOCATION.then(Z), location.getBlockZ());
            wrapper.set(LOCATION.then(WORLD), location.getWorld().getUID().toString());
        }
    }

    public static class PlayerEventBuilder extends EventBuilder {

        PlayerEventBuilder(SourceBuilder sourceBuilder) {
            super(sourceBuilder);
        }

        private Player player() {
            return (Player) sourceBuilder.getSource();
        }

        public OEntry signInteract(Location location, Sign sign) {
            this.eventName = "useSign";
            wrapper.set(TARGET, sign.getMaterial().name());
            wrapper.set(ORIGINAL_BLOCK, sign.getAsString());
            writeLocationData(location);
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
}

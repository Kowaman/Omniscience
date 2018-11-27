package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.api.data.BlockTransaction;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;

import static net.lordofthecraft.omniscience.api.data.DataKeys.*;

public class OEntry {
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
            blockTransaction.getBefore().ifPresent(block -> wrapper.set(ORIGINAL_BLOCK, DataWrapper.of(block)));
            blockTransaction.getAfter().ifPresent(block -> wrapper.set(NEW_BLOCK, DataWrapper.of(block)));
            return new OEntry(sourceBuilder, this);
        }

        public OEntry placedBlock(BlockTransaction blockTransaction) {
            this.eventName = "place";
            blockTransaction.getBefore().ifPresent(block -> wrapper.set(ORIGINAL_BLOCK, DataWrapper.of(block)));
            blockTransaction.getAfter().ifPresent(block -> wrapper.set(NEW_BLOCK, DataWrapper.of(block)));
            return new OEntry(sourceBuilder, this);
        }

        public OEntry decayedBlock(BlockTransaction blockTransaction) {
            this.eventName = "decay";
            blockTransaction.getBefore().ifPresent(block -> wrapper.set(ORIGINAL_BLOCK, DataWrapper.of(block)));
            blockTransaction.getAfter().ifPresent(block -> wrapper.set(NEW_BLOCK, DataWrapper.of(block)));
            return new OEntry(sourceBuilder, this);
        }

        public OEntry dropped(Item item) {
            this.eventName = "drop";
            wrapper.set(ITEMSTACK, DataWrapper.of(item.getItemStack()));
            wrapper.set(LOCATION, DataWrapper.of(item.getLocation()));
            return new OEntry(sourceBuilder, this);
        }

        public OEntry pickup(Item item) {
            this.eventName = "pickup";
            wrapper.set(ITEMSTACK, DataWrapper.of(item.getItemStack()));
            wrapper.set(LOCATION, DataWrapper.of(item.getLocation()));
            return new OEntry(sourceBuilder, this);
        }
    }

    public static class PlayerEventBuilder extends EventBuilder {

        PlayerEventBuilder(SourceBuilder sourceBuilder) {
            super(sourceBuilder);
        }

        public OEntry quit() {
            this.eventName = "quit";
            //TODO write data
            return new OEntry(sourceBuilder, this);
        }

        public OEntry joined() {
            this.eventName = "join";
            //TODO write data
            return new OEntry(sourceBuilder, this);
        }
    }

    public static final class EntryBuilder {

        public EventBuilder source(Object source) {

            if (source instanceof Player) {
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

        public PlayerEventBuilder player(Player player) {
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

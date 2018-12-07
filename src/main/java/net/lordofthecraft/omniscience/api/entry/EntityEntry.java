package net.lordofthecraft.omniscience.api.entry;

import net.lordofthecraft.omniscience.api.data.DataKeys;
import net.lordofthecraft.omniscience.api.data.DataWrapper;
import net.lordofthecraft.omniscience.api.data.Transaction;
import net.lordofthecraft.omniscience.util.DataHelper;
import net.lordofthecraft.omniscience.util.reflection.ReflectionHandler;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class EntityEntry extends DataEntryComplete implements Actionable {

    @Override
    public ActionResult rollback() throws Exception {
        //Get the nbt data that was stored for this entry
        String entityData = data.getString(DataKeys.ENTITY)
                .orElseThrow(() -> skipped(SkipReason.INVALID));
        //Get the entity type string that was stored for this entry
        String entityType = data.getString(DataKeys.ENTITY_TYPE)
                .orElseThrow(() -> skipped(SkipReason.INVALID));

        //Fetch the type of entity that this is
        EntityType type = EntityType.valueOf(entityType);
        //Parse the location this entity was in (this will be overwritten later by nbt data but is needed for the process)
        DataWrapper locationWrapper = data.getWrapper(DataKeys.LOCATION)
                .orElseThrow(() -> skipped(SkipReason.INVALID_LOCATION));

        //Create a new entity based on the entity type of this event
        Entity baseEntity = DataHelper.getLocationFromDataWrapper(locationWrapper)
                .map(loc -> {
                    World world = loc.getWorld();
                    return world.spawnEntity(loc, type);
                }).orElseThrow(() -> skipped(SkipReason.INVALID_LOCATION));

        //TODO test this loading from entity method and make sure it isnt hot garbage
        //Now, complete the rollback by cloning our data into the entity.
        //This is an UNSAFE operation. Data can change from version to version.
        ReflectionHandler.loadEntityFromNBT(baseEntity, entityData);

        return ActionResult.success(new Transaction<>(null, baseEntity));
    }

    @Override
    public ActionResult restore() throws Exception {
        return ActionResult.skipped(SkipReason.UNIMPLEMENTED);
    }
}
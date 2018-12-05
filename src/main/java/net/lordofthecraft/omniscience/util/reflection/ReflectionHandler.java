package net.lordofthecraft.omniscience.util.reflection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.StringBufferInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * A class I basically stitched together from Sporadic's code. - 501warhead
 *
 * @author Sporadic
 */
public final class ReflectionHandler {

    final private static String CRAFTBUKKIT_PATH = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
    final private static String PATH = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
    private static Method asNMSCopy;
    private static Method getNMSEntity;
    private static Method loadEntityFromNBT;
    private static Constructor<?> compoundConstructor;
    private static Method nbtCompoundLoadFromString;
    private static Method saveToJson;
    private static Constructor<?> nbtReadLimiter;
    private static Method saveEntityToJson;

    static {
        try {
            Class<?> NBTTagCompound = Class.forName(PATH + "NBTTagCompound");
            Class<?> NMSItemStack = Class.forName(PATH + "ItemStack");
            Class<?> NMSEntity = Class.forName(PATH + "Entity");
            Class<?> craftBukkitEntity = Class.forName(CRAFTBUKKIT_PATH + "entity.CraftEntity");
            Class<?> craftBukkitItemStack = Class.forName(CRAFTBUKKIT_PATH + "inventory.CraftItemStack");
            Class<?> NBTReadLimiter = Class.forName(PATH + "NBTReadLimiter");
            nbtCompoundLoadFromString = NBTTagCompound.getMethod("load", DataInput.class, int.class, NBTReadLimiter);
            nbtReadLimiter = NBTReadLimiter.getConstructor(long.class);
            for (Method method : NMSEntity.getMethods()) {
                for (Type type : method.getGenericParameterTypes()) {
                    if (type.getTypeName().equalsIgnoreCase(NBTTagCompound.getTypeName())
                            && method.getReturnType().equals(Void.TYPE)) {
                        //TODO if we decide to load from nms/nbt - we need to change the uuid to a new random one.
                        loadEntityFromNBT = method;
                    }
                }
            }
            compoundConstructor = NBTTagCompound.getConstructor();

            saveToJson = NMSItemStack.getMethod("save", NBTTagCompound);
            saveEntityToJson = NMSEntity.getMethod("save", NBTTagCompound);

            getNMSEntity = craftBukkitEntity.getMethod("getHandle");
            asNMSCopy = craftBukkitItemStack.getMethod("asNMSCopy", ItemStack.class);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static String getItemJson(ItemStack is) {
        try {
            Object nmsStack = getMinecraftItemStack(is);
            Object compound = compoundConstructor.newInstance();

            saveToJson.invoke(nmsStack, compound);
            return compound.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static String getEntityJson(Entity entity) {
        try {
            Object nmsEntity = getMinecraftEntity(entity);
            Object compound = compoundConstructor.newInstance();

            saveEntityToJson.invoke(nmsEntity, compound);
            return compound.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static void loadEntityFromNBT(Entity entity, String nbt) {
        try {
            Object readLimiter = nbtReadLimiter.newInstance((long) nbt.length());
            Object compound = compoundConstructor.newInstance();
            DataInput input = new DataInputStream(new StringBufferInputStream(nbt));

            nbtCompoundLoadFromString.invoke(compound, input, (long) nbt.length(), readLimiter);
            System.out.println("Loaded compound: " + compound);
            Object nmsEntity = getMinecraftEntity(entity);
            //TODO we need to make sure the UUID is changed over
            loadEntityFromNBT.invoke(nmsEntity, compound);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static Object getMinecraftItemStack(ItemStack origin) throws Exception {
        return asNMSCopy.invoke(null, origin);
    }

    private static Object getMinecraftEntity(Entity entity) throws Exception {
        return getNMSEntity.invoke(entity);
    }
}

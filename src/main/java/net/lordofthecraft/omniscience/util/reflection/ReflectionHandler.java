package net.lordofthecraft.omniscience.util.reflection;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * A class I basically stitched together from Sporadic's code. - 501warhead
 *
 * @author Sporadic
 */
public final class ReflectionHandler {

    final private static String CRAFTBUKKIT_PATH = "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
    final private static String PATH = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ".";
    private static Method asNMSCopy;
    private static Constructor<?> compoundConstructor;
    private static Method saveToJson;

    static {
        try {
            Class<?> NBTTagCompound = Class.forName(PATH + "NBTTagCompound");
            Class<?> NMSItemStack = Class.forName(PATH + "ItemStack");
            Class<?> craftBukkitItemStack = Class.forName(CRAFTBUKKIT_PATH + "inventory.CraftItemStack");
            compoundConstructor = NBTTagCompound.getConstructor();

            saveToJson = NMSItemStack.getMethod("save", NBTTagCompound);
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

    private static Object getMinecraftItemStack(ItemStack origin) throws Exception {
        return asNMSCopy.invoke(null, origin);
    }
}

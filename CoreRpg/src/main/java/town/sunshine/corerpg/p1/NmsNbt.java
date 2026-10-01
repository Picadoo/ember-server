package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Minimal reflective NBT bridge for one custom sub-compound on an item (Paper 1.12.2, v1_12_R1 method names
 * checked against Paper-Server sources). NI's bundled NBT library is runtime-generated and cannot be
 * compiled against, so this stays independent of NI. Values are String or int.
 */
public final class NmsNbt {

    private static volatile boolean ready;
    private static volatile String error;
    private static Class<?> craftItemStack, nmsItemStack, nbtCompound, nbtBase;
    private static Method asNMSCopy, asBukkitCopy, hasTag, getTag, setTag;
    private static Method cHasKey, cGetCompound, cSet, cSetString, cSetInt, cGetString, cGetInt, cKeys, cGetTypeId, cGet, cRemove;
    private static Field handleField;

    private NmsNbt() {}

    private static synchronized void init() {
        if (ready || error != null) return;
        try {
            String pkg = Bukkit.getServer().getClass().getPackage().getName(); // org.bukkit.craftbukkit.v1_12_R1
            String ver = pkg.substring(pkg.lastIndexOf('.') + 1);
            craftItemStack = Class.forName(pkg + ".inventory.CraftItemStack");
            nmsItemStack = Class.forName("net.minecraft.server." + ver + ".ItemStack");
            nbtCompound = Class.forName("net.minecraft.server." + ver + ".NBTTagCompound");
            nbtBase = Class.forName("net.minecraft.server." + ver + ".NBTBase");
            asNMSCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            asBukkitCopy = craftItemStack.getMethod("asBukkitCopy", nmsItemStack);
            hasTag = nmsItemStack.getMethod("hasTag");
            getTag = nmsItemStack.getMethod("getTag");
            setTag = nmsItemStack.getMethod("setTag", nbtCompound);
            cHasKey = nbtCompound.getMethod("hasKey", String.class);
            cGetCompound = nbtCompound.getMethod("getCompound", String.class);
            cSet = nbtCompound.getMethod("set", String.class, nbtBase);
            cSetString = nbtCompound.getMethod("setString", String.class, String.class);
            cSetInt = nbtCompound.getMethod("setInt", String.class, int.class);
            cGetString = nbtCompound.getMethod("getString", String.class);
            cGetInt = nbtCompound.getMethod("getInt", String.class);
            cKeys = nbtCompound.getMethod("c"); // Set<String> keySet (1.12 obf name)
            cGet = nbtCompound.getMethod("get", String.class);
            cGetTypeId = nbtBase.getMethod("getTypeId");
            cRemove = nbtCompound.getMethod("remove", String.class);
            try {
                handleField = craftItemStack.getDeclaredField("handle");
                handleField.setAccessible(true);
            } catch (Throwable ignored) { handleField = null; }
            ready = true;
        } catch (Throwable t) {
            error = t.getClass().getSimpleName() + ": " + t.getMessage();
            Bukkit.getLogger().warning("[CoreRpg][" + EmberMode.MODE_ID + "] NBT bridge unavailable: " + error);
        }
    }

    public static boolean isReady() { init(); return ready; }
    public static String error() { return error; }

    /** Live NMS stack without copying when possible (CraftItemStack mirror), else a copy. */
    private static Object nms(ItemStack item) throws Exception {
        if (handleField != null && craftItemStack.isInstance(item)) {
            Object h = handleField.get(item);
            if (h != null) return h;
        }
        return asNMSCopy.invoke(null, item);
    }

    /** @return the sub-compound as a map, or null when absent / not a compound / bridge unavailable */
    public static Map<String, Object> read(ItemStack item, String key) {
        if (item == null || item.getType() == Material.AIR || !isReady()) return null;
        try {
            Object n = nms(item);
            if (n == null || !(Boolean) hasTag.invoke(n)) return null;
            Object tag = getTag.invoke(n);
            if (tag == null || !(Boolean) cHasKey.invoke(tag, key)) return null;
            Object sub = cGet.invoke(tag, key);
            if (sub == null || ((Number) cGetTypeId.invoke(sub)).intValue() != 10) return null; // 10 = TAG_Compound
            Map<String, Object> out = new LinkedHashMap<String, Object>();
            @SuppressWarnings("unchecked")
            Set<String> keys = (Set<String>) cKeys.invoke(sub);
            for (String k : keys) {
                Object v = cGet.invoke(sub, k);
                int type = v == null ? 0 : ((Number) cGetTypeId.invoke(v)).intValue();
                if (type == 8) out.put(k, cGetString.invoke(sub, k));
                else if (type >= 1 && type <= 4) out.put(k, cGetInt.invoke(sub, k)); // byte/short/int/long → int
            }
            return out;
        } catch (Throwable t) {
            return null;
        }
    }

    /** Returns a copy of {@code item} with the sub-compound replaced by {@code values} (null = remove). */
    public static ItemStack write(ItemStack item, String key, Map<String, Object> values) {
        if (item == null || !isReady()) return null;
        try {
            Object n = asNMSCopy.invoke(null, item);
            Object tag = (Boolean) hasTag.invoke(n) ? getTag.invoke(n) : null;
            if (tag == null) tag = nbtCompound.newInstance();
            if (values == null) {
                cRemove.invoke(tag, key);
            } else {
                Object sub = nbtCompound.newInstance();
                for (Map.Entry<String, Object> e : values.entrySet()) {
                    Object v = e.getValue();
                    if (v instanceof Number) cSetInt.invoke(sub, e.getKey(), ((Number) v).intValue());
                    else if (v != null) cSetString.invoke(sub, e.getKey(), String.valueOf(v));
                }
                cSet.invoke(tag, key, sub);
            }
            setTag.invoke(n, tag);
            return (ItemStack) asBukkitCopy.invoke(null, n);
        } catch (Throwable t) {
            Bukkit.getLogger().warning("[CoreRpg][" + EmberMode.MODE_ID + "] NBT write failed: " + t);
            return null;
        }
    }

    /** Cheap presence check (used by legacy services to refuse P1 items). */
    public static boolean has(ItemStack item, String key) {
        if (item == null || item.getType() == Material.AIR || !isReady()) return false;
        try {
            Object n = nms(item);
            if (n == null || !(Boolean) hasTag.invoke(n)) return false;
            Object tag = getTag.invoke(n);
            return tag != null && (Boolean) cHasKey.invoke(tag, key);
        } catch (Throwable t) {
            return false;
        }
    }
}

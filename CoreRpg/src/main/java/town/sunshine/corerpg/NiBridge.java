package town.sunshine.corerpg;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Soft-safe NeigeItems helpers (count / consume by NI id, lore fallback). */
public final class NiBridge {

    private final JavaPlugin plugin;
    private final Set<String> warnedMissing = new HashSet<String>();
    private Boolean niReady;

    public NiBridge(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isReady() {
        if (niReady != null) return niReady.booleanValue();
        niReady = Boolean.valueOf(Bukkit.getPluginManager().getPlugin("NeigeItems") != null);
        if (!niReady.booleanValue()) {
            plugin.getLogger().warning("NeigeItems not present — enhance mat checks by NI id disabled.");
        }
        return niReady.booleanValue();
    }

    public void warnMissingOnce(String niId) {
        if (niId == null || niId.isEmpty()) return;
        if (warnedMissing.add(niId)) {
            Logger log = plugin.getLogger();
            log.warning("NI item missing or unregistered: " + niId);
        }
    }

    public void warnMissingOnceIfAbsent(String niId) {
        if (niId == null || niId.isEmpty()) return;
        if (!isReady()) return;
        if (!hasNiDefinition(niId)) warnMissingOnce(niId);
    }

    public boolean hasNiDefinition(String niId) {
        if (!isReady() || niId == null || niId.isEmpty()) return false;
        try {
            return ItemManager.INSTANCE.hasItem(niId);
        } catch (Throwable t) {
            warnMissingOnce(niId);
            return false;
        }
    }

    /** Returns NI id or null. */
    public String getNiId(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || !isReady()) return null;
        try {
            ItemInfo info = ItemManager.INSTANCE.isNiItem(stack);
            return info == null ? null : info.getId();
        } catch (Throwable t) {
            return null;
        }
    }

    public boolean matchesNiId(ItemStack stack, String niId) {
        if (niId == null || niId.isEmpty() || stack == null || stack.getType() == Material.AIR) return false;
        String id = getNiId(stack);
        if (id != null && id.equals(niId)) return true;
        // lore fallback (id printed in lore by NI templates)
        ItemMeta meta = stack.getItemMeta();
        if (meta == null || !meta.hasLore()) return false;
        for (String line : meta.getLore()) {
            if (line == null) continue;
            String plain = ChatColor.stripColor(line);
            if (plain != null && plain.contains(niId)) return true;
        }
        return false;
    }

    public int countInInventory(Player player, String niId) {
        if (player == null || niId == null || niId.isEmpty()) return 0;
        if (isReady() && !hasNiDefinition(niId)) {
            warnMissingOnce(niId);
            // still scan inventory — player may hold items from older packs
        }
        int total = 0;
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) return 0;
        for (ItemStack stack : contents) {
            if (stack == null || stack.getType() == Material.AIR) continue;
            if (matchesNiId(stack, niId)) total += stack.getAmount();
        }
        return total;
    }

    /** Removes up to {@code amount} matching stacks. Returns actually removed. */
    public int consume(Player player, String niId, int amount) {
        if (player == null || niId == null || amount <= 0) return 0;
        int need = amount;
        ItemStack[] contents = player.getInventory().getContents();
        if (contents == null) return 0;
        for (int i = 0; i < contents.length && need > 0; i++) {
            ItemStack stack = contents[i];
            if (stack == null || stack.getType() == Material.AIR) continue;
            if (!matchesNiId(stack, niId)) continue;
            int take = Math.min(need, stack.getAmount());
            int left = stack.getAmount() - take;
            if (left <= 0) {
                player.getInventory().setItem(i, null);
            } else {
                stack.setAmount(left);
                player.getInventory().setItem(i, stack);
            }
            need -= take;
        }
        player.updateInventory();
        return amount - need;
    }

    public boolean consumeExact(Player player, String niId, int amount) {
        if (amount <= 0) return true;
        if (countInInventory(player, niId) < amount) return false;
        return consume(player, niId, amount) >= amount;
    }

    /**
     * Give NI item stacks into inventory (overflow drops at feet).
     * Falls back to console {@code ni give} if ItemManager.getItemStack fails.
     */
    /** 1.8.0: display name of an NI item (colors stripped), or the id. */
    public String displayName(String niId) {
        try {
            if (isReady() && hasNiDefinition(niId)) {
                ItemStack s = ItemManager.INSTANCE.getItemStack(niId);
                if (s != null && s.hasItemMeta() && s.getItemMeta().hasDisplayName())
                    return org.bukkit.ChatColor.stripColor(s.getItemMeta().getDisplayName());
            }
        } catch (Throwable ignored) { }
        return niId;
    }

    /** 1.12.0: fresh NI stack (amount 1) or null. */
    public ItemStack createNiItem(String niId) {
        try {
            if (isReady() && hasNiDefinition(niId)) {
                ItemStack s = ItemManager.INSTANCE.getItemStack(niId);
                if (s != null) { s = s.clone(); s.setAmount(1); return s; }
            }
        } catch (Throwable ignored) { }
        return null;
    }

    public boolean giveNiItem(Player player, String niId, int amount) {
        if (player == null || niId == null || niId.isEmpty() || amount <= 0) return amount <= 0;
        if (isReady() && hasNiDefinition(niId)) {
            try {
                int left = amount;
                while (left > 0) {
                    ItemStack stack = ItemManager.INSTANCE.getItemStack(niId);
                    if (stack == null) break;
                    stack = stack.clone();
                    int batch = Math.min(left, Math.max(1, stack.getMaxStackSize()));
                    stack.setAmount(batch);
                    Map<Integer, ItemStack> leftover = player.getInventory().addItem(stack);
                    if (leftover != null && !leftover.isEmpty()) {
                        for (ItemStack drop : leftover.values()) {
                            if (drop != null && drop.getType() != Material.AIR) {
                                player.getWorld().dropItemNaturally(player.getLocation(), drop);
                            }
                        }
                    }
                    left -= batch;
                }
                if (left <= 0) {
                    player.updateInventory();
                    return true;
                }
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "giveNiItem ItemManager failed: " + niId, t);
            }
        } else {
            warnMissingOnce(niId);
        }
        // Console fallback (NeigeItems command)
        try {
            boolean ok = Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "ni give " + player.getName() + " " + niId + " " + amount);
            if (ok) return true;
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "giveNiItem console fallback failed: " + niId, t);
        }
        return false;
    }
}

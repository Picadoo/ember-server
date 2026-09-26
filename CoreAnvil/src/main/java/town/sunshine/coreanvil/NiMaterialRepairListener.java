package town.sunshine.coreanvil;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Plugin-side NI material repair for Paper 1.12.2.
 * <p>
 * ContainerAnvil with {@code material_repair_only} treats only vanilla repair
 * materials ({@code isMatRepair}) as valid. Ember shards/fragments are REDSTONE
 * (not iron), so NMS early-returns with an empty result and forces
 * {@code levelCost = 0} <em>after</em> {@link PrepareAnvilEvent}. Hooks alone
 * cannot synthesize a non-vanilla repair without a Paper change.
 * <p>
 * This listener fills the result on PrepareAnvil. Cost is re-applied next tick
 * (and {@code ContainerAnvil.k} set via reflection) so the output is takeable;
 * NMS still zeros cost on the early-return path, so the client may briefly show 0.
 * Ideal path later: Paper lets {@code allowIng=TRUE} drive a synthetic material
 * repair so {@code extra_cost} flows through {@code adjustLevelCost} normally.
 */
public final class NiMaterialRepairListener implements Listener {

    /** Vanilla unit = maxDurability/4 damage repaired. */
    private static final int CORE_FRAGMENT_UNITS = 3;
    private static final String ID_CORE_FRAGMENT = "mat_ember_core_fragment";

    private final CoreAnvilPlugin plugin;

    public NiMaterialRepairListener(CoreAnvilPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!plugin.isPluginEnabled() || !plugin.isRequireNiRepair()) {
            return;
        }
        AnvilInventory inv = event.getInventory();
        ItemStack left = inv.getItem(0);
        ItemStack right = inv.getItem(1);
        if (left == null || right == null) {
            return;
        }
        short maxDur = left.getType().getMaxDurability();
        if (maxDur <= 0) {
            return;
        }
        int damage = left.getDurability() & 0xFFFF;
        if (damage <= 0) {
            return;
        }

        ItemInfo info = ItemManager.INSTANCE.isNiItem(right);
        if (info == null) {
            return;
        }
        String niId = info.getId();
        if (!plugin.getRepairNiIds().contains(niId)) {
            return;
        }

        int unitsPerItem = ID_CORE_FRAGMENT.equals(niId) ? CORE_FRAGMENT_UNITS : 1;
        int remaining = damage;
        int unitsApplied = 0;
        int itemsUsed = 0;
        int available = right.getAmount();
        int unitSize = Math.max(1, maxDur / 4);

        while (itemsUsed < available && remaining > 0) {
            itemsUsed++;
            int unitsLeftOnItem = unitsPerItem;
            while (unitsLeftOnItem > 0 && remaining > 0) {
                int repair = Math.min(remaining, unitSize);
                if (repair <= 0) {
                    break;
                }
                remaining -= repair;
                unitsApplied++;
                unitsLeftOnItem--;
            }
        }

        if (unitsApplied <= 0 || itemsUsed <= 0) {
            return;
        }

        ItemStack result = left.clone();
        result.setDurability((short) remaining);

        String rename = inv.getRenameText();
        boolean renamed = false;
        if (rename != null && !rename.isEmpty()) {
            String current = null;
            ItemMeta meta = result.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                current = meta.getDisplayName();
            }
            if (current == null || !rename.equals(current)) {
                if (meta == null) {
                    meta = Bukkit.getItemFactory().getItemMeta(result.getType());
                }
                if (meta != null) {
                    meta.setDisplayName(rename);
                    result.setItemMeta(meta);
                    renamed = true;
                }
            }
        }

        event.setResult(result);

        int cost = unitsApplied + plugin.getMaterialRepairExtra();
        if (renamed) {
            cost += plugin.getRenameCost();
        }
        if (cost > plugin.getMaxLevelCost()) {
            cost = plugin.getMaxLevelCost();
        }
        if (cost < 1) {
            cost = 1;
        }

        // Likely overwritten to 0 by ContainerAnvil early-return after this event.
        inv.setRepairCost(cost);

        final AnvilInventory anvilRef = inv;
        final int costFinal = cost;
        final int itemsFinal = itemsUsed;
        final ItemStack resultFinal = result.clone();
        Bukkit.getScheduler().runTask(plugin, new Runnable() {
            @Override
            public void run() {
                // Slots may have changed; only re-apply if result slot still matches intent.
                ItemStack curLeft = anvilRef.getItem(0);
                ItemStack curRight = anvilRef.getItem(1);
                if (curLeft == null || curRight == null) {
                    return;
                }
                anvilRef.setItem(2, resultFinal);
                anvilRef.setRepairCost(costFinal);
                applyNmsConsumeCount(anvilRef, itemsFinal, costFinal);
            }
        });
    }

    /**
     * Sets ContainerAnvil.k (materials consumed on take) and syncs levelCost to client.
     */
    private void applyNmsConsumeCount(AnvilInventory inv, int itemsUsed, int cost) {
        try {
            Field containerField = inv.getClass().getDeclaredField("container");
            containerField.setAccessible(true);
            Object container = containerField.get(inv);
            if (container == null) {
                return;
            }
            Field kField = container.getClass().getField("k");
            kField.setInt(container, itemsUsed);
            Field levelCostField = container.getClass().getField("levelCost");
            levelCostField.setInt(container, cost);
            Method sync = container.getClass().getMethod("b");
            sync.invoke(container);
        } catch (Throwable t) {
            plugin.getLogger().fine("[CoreAnvil] NI repair NMS reapply skipped: " + t.getMessage());
        }
    }
}

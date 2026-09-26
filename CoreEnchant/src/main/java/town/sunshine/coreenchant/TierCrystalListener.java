package town.sunshine.coreenchant;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import pers.coresystem.paper.EnchantNmsHooks;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

/**
 * Tier gating: Paper consumes the per-slot crystal cost (EnchantNmsHooks.resolveLapisCost) after
 * EnchantItemEvent. Tables with extra_crystals > 0 need that many more crystals in the catalyst
 * slot; we take them here by shrinking the same (mirrored) stack, or cancel if short.
 */
final class TierCrystalListener implements Listener {

    private final CoreEnchantPlugin plugin;

    TierCrystalListener(CoreEnchantPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        if (event.getEnchanter().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        ItemInfo info = ItemManager.INSTANCE.isNiItem(event.getItem());
        CoreEnchantPlugin.OfferTable table = plugin.tableForNi(info != null ? info.getId() : null);
        if (table == null || table.extraCrystals <= 0) {
            return;
        }
        if (!(event.getInventory() instanceof EnchantingInventory)) {
            return;
        }
        EnchantingInventory inv = (EnchantingInventory) event.getInventory();
        ItemStack secondary = inv.getSecondary();
        int slotCost = EnchantNmsHooks.resolveLapisCost(event.whichButton());
        int need = slotCost + table.extraCrystals;
        int have = secondary == null ? 0 : secondary.getAmount();
        if (have < need) {
            event.setCancelled(true);
            event.getEnchanter().sendMessage("§c[附魔] 该阶装备此档需要 §e" + need + " §c颗余烬附魔晶（当前 " + have + "）。");
            return;
        }
        // Mirror stack: shrinking it here is seen by Paper's later subtract(slotCost).
        secondary.setAmount(have - table.extraCrystals);
    }
}

package town.sunshine.coreenchant;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.Map;
import java.util.Set;

/**
 * Vanilla ItemStack.canEnchant() refuses any item that already carries an enchantment, and
 * Paper pre-cancels PrepareItemEnchantEvent in that case. Whitelisted NI gear ships with a
 * hidden base enchant (e.g. DURABILITY 1), so it could never be enchanted. Un-cancel only
 * when every enchant on the item is within the configured base allowance (i.e. the item has
 * not been table-enchanted yet) — one table enchant per item, like vanilla.
 */
final class BaseEnchantListener implements Listener {

    private final Set<String> whitelist;
    private final Map<Enchantment, Integer> baseAllowance;

    BaseEnchantListener(Set<String> whitelist, Map<Enchantment, Integer> baseAllowance) {
        this.whitelist = whitelist;
        this.baseAllowance = baseAllowance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPrepare(PrepareItemEnchantEvent event) {
        if (!event.isCancelled()) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null || item.getEnchantments().isEmpty()) {
            return;
        }
        ItemInfo info = ItemManager.INSTANCE.isNiItem(item);
        if (info == null || !whitelist.contains(info.getId())) {
            return;
        }
        for (Map.Entry<Enchantment, Integer> e : item.getEnchantments().entrySet()) {
            Integer max = baseAllowance.get(e.getKey());
            if (max == null || e.getValue() > max) {
                return;
            }
        }
        boolean anyOffer = false;
        for (Object o : event.getOffers()) {
            if (o != null) {
                anyOffer = true;
            }
        }
        if (anyOffer) {
            event.setCancelled(false);
        }
    }
}

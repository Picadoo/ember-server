package town.sunshine.coreenchant;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pers.coresystem.paper.EnchantNmsHooks;
import pers.neige.neigeitems.item.ItemInfo;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1.2.0: vanilla silently ignores an enchant-button click when the catalyst stack or the player's
 * level is too small (no Bukkit event fires). Watch the ENCHANT_ITEM packet and explain why.
 */
final class EnchantClickHint implements Listener {

    private final CoreEnchantPlugin plugin;
    private final Map<UUID, int[]> lastCosts = new ConcurrentHashMap<UUID, int[]>();

    EnchantClickHint(CoreEnchantPlugin plugin) {
        this.plugin = plugin;
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Client.ENCHANT_ITEM) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                final Player p = event.getPlayer();
                final int button;
                try { button = event.getPacket().getIntegers().read(1); } catch (Throwable t) { return; }
                Bukkit.getScheduler().runTask(EnchantClickHint.this.plugin, new Runnable() {
                    @Override public void run() { check(p, button); }
                });
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPrepare(PrepareItemEnchantEvent e) {
        try { lastCosts.put(e.getEnchanter().getUniqueId(), e.getExpLevelCostsOffered().clone()); } catch (Throwable ignored) { }
    }

    /** Runs on the main thread just before the packet itself is handled (the task queue is drained first). */
    private void check(Player p, int button) {
        if (p == null || !p.isOnline() || p.getGameMode() == GameMode.CREATIVE) return;
        if (button < 0 || button > 2) return;
        Inventory top = p.getOpenInventory().getTopInventory();
        if (!(top instanceof EnchantingInventory)) return;
        EnchantingInventory inv = (EnchantingInventory) top;
        ItemStack item = inv.getItem();
        if (item == null || item.getType().name().equals("AIR")) return;
        int[] costs = lastCosts.get(p.getUniqueId());
        int lvlCost = costs != null && costs.length > button ? costs[button] : 0;
        if (lvlCost <= 0) return; // no offer in that slot
        ItemInfo info = ItemManager.INSTANCE.isNiItem(item);
        CoreEnchantPlugin.OfferTable table = plugin.tableForNi(info != null ? info.getId() : null);
        int extra = table != null ? table.extraCrystals : 0;
        int need;
        try { need = EnchantNmsHooks.resolveLapisCost(button) + extra; } catch (Throwable t) { need = button + 1 + extra; }
        ItemStack sec = inv.getSecondary();
        int have = sec == null ? 0 : sec.getAmount();
        if (p.getLevel() < lvlCost) {
            p.sendMessage("§c[附魔] 等级不足：此档需要 §e" + lvlCost + " §c级（当前 " + p.getLevel() + "）。打怪/做任务可获得经验。");
        } else if (have < need) {
            p.sendMessage("§c[附魔] 附魔晶不足：此档需要 §e" + need + " §c颗余烬附魔晶放在右侧槽（当前 " + have + "）。"
                    + "附魔晶：工作台 3 碎片 + 1 骨尘 合成。");
        }
    }
}

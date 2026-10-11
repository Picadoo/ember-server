package town.sunshine.corerpg.p1;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D5793: after enhance fail, if next attempt is pity-guaranteed and mats ready,
 * short-delay auto re-enhance (player can undo first). Zero rate/pity/price change.
 * Not tip twin / not ConfirmTokens densify / not Path densify.
 */
public final class EmberEnhancePityChase {

    public static final String C_CHASE = "p1_enh_pity_chase";
    /** ticks after fail before auto sure-enhance */
    public static final long DELAY_TICKS = 50L;

    private EmberEnhancePityChase() {}

    public static boolean shouldChase(EmberItemData after) {
        return after != null && EmberUpgradeRules.guaranteed(after);
    }

    /**
     * Call from enhance commit path after fail undo window opens.
     */
    public static void maybeAfterFail(Player p, EmberItemData after, EmberEnhanceUndo.Snap snap) {
        if (p == null || after == null || snap == null) return;
        if (!shouldChase(after)) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return;
        final UUID id = p.getUniqueId();
        final String uid = after.uid;
        final long at = snap.at;
        p.sendMessage(EmberRunService.P + "§a保底追击就绪 · §f约 " + (DELAY_TICKS / 20) + "s §7后自动再强化（可先点撤销）");
        try {
            p.sendActionBar("§a保底追击 · §f" + (DELAY_TICKS / 20) + "s");
        } catch (Throwable ignored) { }
        Bukkit.getScheduler().runTaskLater(pl, () -> runChase(id, uid, at), DELAY_TICKS);
    }

    static void runChase(UUID id, String uid, long snapAt) {
        if (id == null || uid == null) return;
        Player p = Bukkit.getPlayer(id);
        if (p == null || !p.isOnline()) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberForge() == null) return;
        // undo first? cancel chase
        EmberEnhanceUndo.Snap cur = EmberEnhanceUndo.peek(id);
        if (cur == null) return; // already undone or cleared by success
        if (cur.at != snapAt || !uid.equals(cur.uid)) return;
        // validate held + mats BEFORE discarding undo (D5921)
        ItemStack held = p.getInventory().getItemInMainHand();
        EmberLoadoutService los = pl.getEmberRuns() == null ? null : pl.getEmberRuns().loadouts();
        if (los == null || held == null || !los.items().hasData(held)) {
            p.sendMessage(EmberRunService.P + "§e保底追击取消 · §7请手持该件后再强化（撤销窗仍开）");
            return;
        }
        EmberItems.Read r = los.items().read(held);
        if (r == null || !r.ok() || r.data == null || !uid.equals(r.data.uid)) {
            p.sendMessage(EmberRunService.P + "§e保底追击取消 · §7手持件已变（撤销窗仍开）");
            return;
        }
        if (!EmberUpgradeRules.guaranteed(r.data)) return;
        EmberUpgradeRules.Plan chk = EmberUpgradeRules.enhanceCheck(r.data);
        if (chk == null || !chk.ok()) {
            p.sendMessage(EmberRunService.P + "§e保底追击取消 · §7" + (chk == null ? "不可强化" : chk.error));
            return;
        }
        java.util.List<String> lack = pl.getEmberForge().lackingFor(p, chk.cost);
        if (lack != null && !lack.isEmpty()) {
            p.sendMessage(EmberRunService.P + "§e保底追击取消 · 缺料 §f" + String.join("，", lack)
                    + " §7（撤销窗仍开）");
            return;
        }
        // discard undo (no refund; pity stays), then sure-enhance
        EmberEnhanceUndo.clear(id);
        PlayerData d = pl.getDataStore().get(id);
        if (d != null) d.addPeriodCount(C_CHASE, "day", 1);
        p.sendMessage(EmberRunService.P + "§a保底追击 · 必成直办…");
        try {
            p.sendActionBar("§a保底追击 · 直办");
        } catch (Throwable ignored) { }
        p.performCommand("corerpg p1 enhance confirm");
    }

    private static CoreRpgPlugin plugin() {
        org.bukkit.plugin.Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}

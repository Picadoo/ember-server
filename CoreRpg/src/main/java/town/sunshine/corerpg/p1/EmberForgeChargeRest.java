package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D6561: worn piece with 0 forge charges left → restore 1 after mainline clear (per uid@day).
 * Zero MAX_CHARGES / price raise. Not sure-do / Path densify.
 */
public final class EmberForgeChargeRest {

    public static final String C_REST = "p1_afc_rest_";

    private EmberForgeChargeRest() {}

    public static boolean maybeRestOne(Player p, PlayerData d, EmberItemData it, EmberGrowthService g) {
        if (p == null || d == null || it == null || g == null) return false;
        if (it.uid == null || it.uid.isEmpty()) return false;
        int used = g.afcUsed(d, it);
        if (EmberBrandRules.chargesLeft(used) > 0) return false;
        String key = C_REST + it.uid;
        if (d.periodCount(key, "day") > 0) return false;
        if (!g.afcRest(p.getUniqueId(), d, it)) return false;
        d.addPeriodCount(key, "day", 1);
        int left = EmberBrandRules.chargesLeft(g.afcUsed(d, it));
        p.sendMessage(EmberRunService.P + "§a锻造回数 · §f" + it.shortLabel()
                + " §7回 1 次（剩 §f" + left + "§7 · 每件每日一次 · 零抬上限）");
        try {
            p.sendActionBar("§a锻造回数 · 剩 §f" + left);
        } catch (Throwable ignored) { }
        return true;
    }

    /** After settle: try blade then charm. */
    public static int maybeAfterSettle(Player p, PlayerData d, EmberLoadout lo, EmberGrowthService g) {
        if (p == null || d == null || lo == null || g == null) return 0;
        int n = 0;
        if (lo.blade != null && maybeRestOne(p, d, lo.blade, g)) n++;
        if (lo.charm != null && maybeRestOne(p, d, lo.charm, g)) n++;
        return n;
    }
}

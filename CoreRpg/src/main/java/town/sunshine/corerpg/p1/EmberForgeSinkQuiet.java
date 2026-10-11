package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

import java.util.List;

/**
 * D4009: quality/refine mat quiet tips (hub held) — no ConfirmTokens / Path / rate change.
 */
public final class EmberForgeSinkQuiet {

    public static final String C_QUALITY = "p1_forge_quality_quiet";
    public static final String C_REFINE = "p1_forge_refine_quiet";

    private EmberForgeSinkQuiet() {}

    public static boolean risingEdge(int latched, int target, boolean can) {
        if (!can || target < 1) return false;
        return latched != target;
    }

    public static void clearQuality(PlayerData d) {
        if (d == null) return;
        int c = d.periodCount(C_QUALITY, "all");
        if (c != 0) d.addPeriodCount(C_QUALITY, "all", -c);
    }

    public static void clearRefine(PlayerData d) {
        if (d == null) return;
        int c = d.periodCount(C_REFINE, "all");
        if (c != 0) d.addPeriodCount(C_REFINE, "all", -c);
    }

    /** @return true if any tip fired */
    public static boolean maybeQuietTips(Player p) {
        if (p == null || !p.isOnline()) return false;
        if (EmberMode.isP1World(p.getWorld())) return false;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return false;
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null || d.isLoadFailed()) return false;
        EmberForgeService forge = pl.getEmberForge();
        EmberLoadoutService ls = pl.getEmberLoadouts();
        if (forge == null || ls == null) return false;
        ItemStack main = p.getInventory().getItemInMainHand();
        if (main == null || !ls.items().hasData(main)) {
            clearQuality(d); clearRefine(d); flush(p); return false;
        }
        EmberItems.Read r = ls.items().read(main);
        if (r == null || !r.ok() || r.data == null || r.data.tier < 1) {
            clearQuality(d); clearRefine(d); flush(p); return false;
        }
        EmberItemData data = r.data;
        boolean any = false;
        any |= tipQuality(p, d, forge, data);
        any |= tipRefine(p, d, forge, data);
        if (any) flush(p);
        return any;
    }

    static boolean tipQuality(Player p, PlayerData d, EmberForgeService forge, EmberItemData data) {
        EmberUpgradeRules.Plan plan = EmberUpgradeRules.quality(data);
        int target = data.quality + 1; // 1..2
        int latched = d.periodCount(C_QUALITY, "all");
        if (!plan.ok() || plan.cost == null) { clearQuality(d); return false; }
        List<String> lack = forge.lackingFor(p, plan.cost);
        boolean can = lack == null || lack.isEmpty();
        if (!can) { clearQuality(d); return false; }
        if (!risingEdge(latched, target, true)) return false;
        d.addPeriodCount(C_QUALITY, "all", target - latched);
        String P = EmberRunService.P;
        String from = EmberItemData.qualityName(data.quality);
        String to = EmberItemData.qualityName(plan.after.quality);
        p.sendMessage(P + "§a成色材料够了：§f" + data.shortLabel()
                + " §7· " + from + "→" + to + " §8· /corerpg p1 quality");
        try { p.sendActionBar("§a成色就绪 · §f" + from + "→" + to); } catch (Throwable ignored) { }
        return true;
    }

    static boolean tipRefine(Player p, PlayerData d, EmberForgeService forge, EmberItemData data) {
        EmberUpgradeRules.Plan plan = EmberUpgradeRules.refine(data);
        int target = data.craft + 1; // 1..3
        int latched = d.periodCount(C_REFINE, "all");
        if (!plan.ok() || plan.cost == null) { clearRefine(d); return false; }
        List<String> lack = forge.lackingFor(p, plan.cost);
        boolean can = lack == null || lack.isEmpty();
        if (!can) { clearRefine(d); return false; }
        if (!risingEdge(latched, target, true)) return false;
        d.addPeriodCount(C_REFINE, "all", target - latched);
        String P = EmberRunService.P;
        int fromPct = data.craft * 2, toPct = plan.after.craft * 2;
        p.sendMessage(P + "§a精工材料够了：§f" + data.shortLabel()
                + " §7· " + fromPct + "%→" + toPct + "% §8· /corerpg p1 refine");
        try { p.sendActionBar("§a精工就绪 · §f" + fromPct + "%→" + toPct + "%"); } catch (Throwable ignored) { }
        return true;
    }

    private static void flush(Player p) {
        CoreRpgPlugin pl = plugin();
        if (pl != null && p != null) pl.getDataStore().flushMutation(p.getUniqueId());
    }

    private static CoreRpgPlugin plugin() {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}

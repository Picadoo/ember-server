package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D6177: town/hub — if bag has UP_AUTO-better piece vs worn blade/charm, auto-equip.
 * Honors EquipPath. Zero skill_mult / sure-do / path densify / ConfirmTokens.
 * Distinct from BagSeat (empty fill) and AwakenBreak (awk step).
 */
public final class EmberBagBetter {

    public static final String C_BETTER = "p1_bag_better";

    private EmberBagBetter() {}

    public static EmberItemData findAuto(Player p, EmberLoadout lo, EmberLoadoutService loadouts) {
        if (p == null || lo == null || loadouts == null) return null;
        EmberTables tables = EmberMode.tables();
        if (tables == null) return null;
        int lv = lo.level;
        String set = lo.activeSet;
        PlayerData d = null;
        CoreRpgPlugin pl = plugin();
        if (pl != null) d = pl.getDataStore().get(p.getUniqueId());
        EmberItemData best = null;
        double bestEdge = 0;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || !r.ok() || r.data == null) continue;
            EmberItemData c = r.data;
            if (!c.isBlade() && !c.isCharm()) continue;
            EmberItemData a = c.isBlade() ? lo.blade : lo.charm;
            if (a == null) continue; // empty → BagSeat
            if (c.uid.equals(a.uid)) continue;
            int v = EmberRunRules.upgradeVerdict(tables, c, a, set, lv);
            v = EmberEquipPath.adjust(v, d);
            if (v != EmberRunRules.UP_AUTO) continue;
            double edge = EmberRunRules.pieceValue(tables, c, c.enhance, lv)
                    - EmberRunRules.pieceValue(tables, a, a.enhance, lv);
            if (best == null || edge > bestEdge) {
                best = c;
                bestEdge = edge;
            }
        }
        return best;
    }

    /** @return true when swapped */
    public static boolean maybeSwap(Player p, PlayerData d, EmberLoadoutService loadouts, EmberRunService runs) {
        if (p == null || d == null || loadouts == null || runs == null) return false;
        if (EmberSkillKit.inDungeon(p, runs)) return false;
        EmberLoadout lo = loadouts.refresh(p);
        if (lo == null) return false;
        boolean any = false;
        for (int pass = 0; pass < 2; pass++) {
            lo = loadouts.refresh(p);
            if (lo == null) break;
            EmberItemData cand = findAuto(p, lo, loadouts);
            if (cand == null) break;
            EmberItemData prev = cand.isCharm() ? lo.charm : lo.blade;
            String where = runs.equipPiece(p, cand, prev);
            if (where == null) break;
            any = true;
            p.sendMessage(EmberRunService.P + "§a更优入座 · §f" + cand.shortLabel()
                    + " §7（换下 " + (prev == null ? "—" : ChatColor.stripColor(prev.shortLabel()))
                    + " · " + where + "）");
            try {
                p.sendActionBar("§a更优 · §f" + ChatColor.stripColor(cand.shortLabel()));
            } catch (Throwable ignored) { }
        }
        if (any) d.addPeriodCount(C_BETTER, "day", 1);
        return any;
    }

    public static void scheduleScan(Player p) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null) return;
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (!p.isOnline()) return;
            PlayerData d = pl.getDataStore().get(p.getUniqueId());
            // after BagSeat (80t) — run at 95t
            EmberBagBetter.maybeSwap(p, d, pl.getEmberRuns().loadouts(), pl.getEmberRuns());
        }, 95L);
    }

    private static CoreRpgPlugin plugin() {
        org.bukkit.plugin.Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}

package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.PlayerData;

/**
 * D4641/D4705: chase imprint auto when marks ready and an empty-sig fitting piece is worn or in bag.
 * Zero STAMP_RATE / insignia yield / ConfirmTokens (overwrite still manual). Not MarksBreak twin.
 */
public final class EmberImprintBreak {

    /** Latch: p1_imprint_auto_<code>@all — one auto per chase def */
    public static final String C_AUTO = "p1_imprint_auto_";

    private EmberImprintBreak() {}

    /** Town-only, no asset hold. */
    public static boolean townOk(Player p, EmberRunService runs) {
        if (p == null || runs == null) return false;
        if (EmberMode.isP1World(p.getWorld()) || runs.isRunWorld(p.getWorld())) return false;
        return EmberAssetGuard.hold(p) == null;
    }

    /**
     * Prefer worn empty-sig fitting piece; else first bag empty-sig fitting piece (not worn).
     */
    public static EmberItemData findEmptyTarget(Player p, PlayerData d, EmberLoadout lo,
                                               EmberSignature.Def def, EmberGrowthService g,
                                               EmberLoadoutService loadouts) {
        if (p == null || d == null || def == null || g == null) return null;
        EmberItemData worn = lo == null ? null : ("blade".equals(def.slot) ? lo.blade : lo.charm);
        if (worn != null && worn.tier >= 1 && EmberSignature.fits(def, worn.slot, worn.family)
                && g.sigOf(d, worn) == 0)
            return worn;
        if (loadouts == null) return null;
        EmberItemData best = null;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || !r.ok() || r.data == null) continue;
            EmberItemData x = r.data;
            if (!EmberSignature.fits(def, x.slot, x.family) || x.tier < 1) continue;
            if (g.sigOf(d, x) != 0) continue;
            if (worn != null && worn.uid.equals(x.uid)) continue;
            best = x;
        }
        return best;
    }

    public static boolean canAuto(Player p, PlayerData d, EmberLoadout lo, EmberSignature.Def def,
                                  EmberRunService runs) {
        if (d == null || def == null || runs == null) return false;
        if (!townOk(p, runs)) return false;
        EmberGrowthService g = EmberGrowthService.get();
        if (g == null) return false;
        EmberLoadoutService loadouts = runs.loadouts();
        EmberItemData t = findEmptyTarget(p, d, lo, def, g, loadouts);
        if (t == null) return false;
        int marks = d.periodCount(EmberSignature.C_MARK + def.map, "all");
        String why = EmberSignature.imprintCheck(def, t,
                EmberSkillKit.imprintFeatureUnlocked(d, runs),
                runs.progressFlag(d, def.map), marks, d.getCoin(), 0);
        return why == null;
    }

    /** @deprecated use canAuto with Player */
    public static boolean canAuto(PlayerData d, EmberLoadout lo, EmberSignature.Def def, EmberRunService runs) {
        return false; // need Player for town/bag — callers updated
    }

    /**
     * Rising-edge auto imprint for current chase when empty-sig piece available.
     * @return true when auto started
     */
    public static boolean maybeAuto(Player p, PlayerData d, EmberLoadout lo, EmberRunService runs) {
        if (p == null || d == null) return false;
        EmberSignature.Def def = EmberSigChase.get(d);
        if (def == null) return false;
        String key = C_AUTO + def.code;
        if (d.periodCount(key, "all") > 0) return false;
        EmberLoadoutService loadouts = runs == null ? null : runs.loadouts();
        if (lo == null && loadouts != null) lo = loadouts.refresh(p);
        if (!canAuto(p, d, lo, def, runs)) return false;
        EmberGrowthService g = EmberGrowthService.get();
        if (g == null) return false;

        EmberItemData target = findEmptyTarget(p, d, lo, def, g, loadouts);
        if (target == null) return false;

        // D4705: bag piece not worn → equip first
        EmberItemData worn = "blade".equals(def.slot) ? lo.blade : lo.charm;
        boolean needEquip = worn == null || !target.uid.equals(worn.uid);
        if (needEquip) {
            String where = runs.equipPiece(p, target, worn);
            if (where == null) return false;
            p.sendMessage(EmberRunService.P + "§a追烙自动 · 已换上空签件 §f"
                    + org.bukkit.ChatColor.stripColor(target.shortLabel())
                    + " §7（" + where + "）");
            lo = loadouts == null ? lo : loadouts.refresh(p);
            // re-resolve worn after equip
            worn = "blade".equals(def.slot) ? lo.blade : lo.charm;
            if (worn == null || !worn.uid.equals(target.uid)) {
                // charm select uses uid — equipPiece should have set it
                target = worn != null && g.sigOf(d, worn) == 0 ? worn : target;
            } else {
                target = worn;
            }
        }

        d.addPeriodCount(key, "all", 1);
        EmberSigChase.flush(p);
        boolean ok = g.autoImprintEmpty(p, def, target);
        if (!ok) {
            d.addPeriodCount(key, "all", -1);
            EmberSigChase.flush(p);
            return false;
        }
        return true;
    }
}

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

    /** True when {@code x} is currently worn on blade / charm / any armor slot. */
    private static boolean isWornUid(EmberLoadout lo, EmberItemData x) {
        if (lo == null || x == null || x.uid == null) return false;
        if (lo.blade != null && x.uid.equals(lo.blade.uid)) return true;
        if (lo.charm != null && x.uid.equals(lo.charm.uid)) return true;
        for (int i = 0; i < 4; i++) {
            EmberItemData a = lo.armor(i);
            if (a != null && x.uid.equals(a.uid)) return true;
        }
        return false;
    }

    /**
     * Prefer worn empty-sig fitting piece (blade, or defense-side charm then armor 0..3);
     * else first bag empty-sig fitting piece (not worn). Uses {@link EmberSignature#fitsDefense} (D12591).
     */
    public static EmberItemData findEmptyTarget(Player p, PlayerData d, EmberLoadout lo,
                                               EmberSignature.Def def, EmberGrowthService g,
                                               EmberLoadoutService loadouts) {
        if (p == null || d == null || def == null || g == null) return null;
        if (lo != null) {
            if ("blade".equals(def.slot)) {
                EmberItemData worn = lo.blade;
                if (worn != null && worn.tier >= 1 && EmberSignature.fitsDefense(def, worn.slot, worn.family)
                        && g.sigOf(d, worn) == 0)
                    return worn;
            } else {
                if (lo.charm != null && lo.charm.tier >= 1
                        && EmberSignature.fitsDefense(def, lo.charm.slot, lo.charm.family)
                        && g.sigOf(d, lo.charm) == 0)
                    return lo.charm;
                for (int i = 0; i < 4; i++) {
                    EmberItemData a = lo.armor(i);
                    if (a != null && a.tier >= 1 && EmberSignature.fitsDefense(def, a.slot, a.family)
                            && g.sigOf(d, a) == 0)
                        return a;
                }
            }
        }
        if (loadouts == null) return null;
        EmberItemData best = null;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || !r.ok() || r.data == null) continue;
            EmberItemData x = r.data;
            if (!EmberSignature.fitsDefense(def, x.slot, x.family) || x.tier < 1) continue;
            if (g.sigOf(d, x) != 0) continue;
            if (isWornUid(lo, x)) continue;
            best = x;
            break;
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

        // D4705/D12591: bag piece not worn → equip first (defense-side may be armor)
        boolean needEquip = !isWornUid(lo, target);
        if (needEquip) {
            EmberItemData displaced = null;
            if ("blade".equals(target.slot)) displaced = lo.blade;
            else if ("charm".equals(target.slot)) displaced = lo.charm;
            else {
                int ai = EmberItemData.armorIndex(target.slot);
                if (ai >= 0) displaced = lo.armor(ai);
            }
            String where = runs.equipPiece(p, target, displaced);
            if (where == null) return false;
            p.sendMessage(EmberRunService.P + "§a追烙自动 · 已换上空签件 §f"
                    + org.bukkit.ChatColor.stripColor(target.shortLabel())
                    + " §7（" + where + "）");
            lo = loadouts == null ? lo : loadouts.refresh(p);
            // re-resolve: prefer the equipped uid if present on any worn slot
            EmberItemData resolved = null;
            if (lo != null) {
                if (lo.blade != null && target.uid.equals(lo.blade.uid)) resolved = lo.blade;
                else if (lo.charm != null && target.uid.equals(lo.charm.uid)) resolved = lo.charm;
                else {
                    for (int i = 0; i < 4; i++) {
                        EmberItemData a = lo.armor(i);
                        if (a != null && target.uid.equals(a.uid)) { resolved = a; break; }
                    }
                }
            }
            if (resolved != null && g.sigOf(d, resolved) == 0) target = resolved;
            else if (resolved == null) {
                // equipPiece should have set it; fall back to empty-sig worn via fitsDefense
                EmberItemData again = findEmptyTarget(p, d, lo, def, g, null);
                if (again != null) target = again;
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

package town.sunshine.corerpg.p1;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D4385/D4449: awakening bump → auto-equip bag piece that raises awakening;
 * if old piece had higher enhance, free-swap enhance track (no ConfirmTokens).
 * Zero set coef / skill_mult. Not EquipPath sticky. Not K3.
 */
public final class EmberAwakenBreak {

    public static final String C_BREAK = "p1_awk_break_";

    private EmberAwakenBreak() {}

    /** Simulated awakening if blade/charm replaced by cand (same slot). */
    public static int awkIfReplace(EmberItemData blade, EmberItemData charm, EmberItemData cand) {
        if (blade == null || charm == null || cand == null) return 0;
        if (cand.isBlade()) return EmberLoadout.awakening(cand, charm);
        if (cand.isCharm()) return EmberLoadout.awakening(blade, cand);
        return 0;
    }

    /** True when donor enhance should free-swap onto newly equipped piece. */
    public static boolean shouldSwapEnhance(EmberItemData previous, EmberItemData equipped) {
        if (previous == null || equipped == null) return false;
        if (previous.uid != null && previous.uid.equals(equipped.uid)) return false;
        if (!previous.slot.equals(equipped.slot)) return false;
        return previous.enhance > equipped.enhance;
    }

    /**
     * Best bag piece same-family that strictly raises awakening vs current loadout.
     */
    public static EmberItemData findStepPiece(Player p, EmberLoadout lo, EmberLoadoutService loadouts) {
        if (p == null || lo == null || loadouts == null) return null;
        if (lo.blade == null || lo.charm == null) return null;
        if ("none".equals(lo.activeSet) || lo.activeSet == null) return null;
        if (lo.awakening >= 3) return null;
        String fam = lo.activeSet;
        int cur = lo.awakening;
        EmberItemData best = null;
        int bestAwk = cur;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || !r.ok() || r.data == null) continue;
            EmberItemData x = r.data;
            if (!fam.equals(x.family)) continue;
            if (lo.blade.uid.equals(x.uid)) continue;
            if (lo.charm.uid != null && lo.charm.uid.equals(x.uid)) continue;
            if (!x.isBlade() && !x.isCharm()) continue;
            int a = awkIfReplace(lo.blade, lo.charm, x);
            if (a < bestAwk) continue;
            if (a > bestAwk) {
                bestAwk = a;
                best = x;
            } else if (a > cur && best != null) {
                if (x.tier > best.tier || (x.tier == best.tier && x.enhance > best.enhance))
                    best = x;
            } else if (a > cur && best == null) {
                best = x;
            }
        }
        return bestAwk > cur ? best : null;
    }

    /**
     * After awakening bump: latch cue + auto-equip step piece + optional free enhance swap.
     * @return true when auto-equip happened
     */
    public static boolean maybeAfterAwkUp(Player p, PlayerData d, int prevAwk, EmberLoadout after,
                                         EmberLoadoutService loadouts, EmberRunService runs) {
        if (p == null || after == null || after.awakening <= prevAwk) return false;
        String key = C_BREAK + after.awakening;
        if (d != null && d.periodCount(key, "all") == 0) {
            d.addPeriodCount(key, "all", 1);
            String roman = after.awakening == 3 ? "III" : after.awakening == 2 ? "II" : "I";
            p.sendMessage(EmberRunService.P + "§a觉醒突破 · §f觉醒" + roman
                    + " §7· " + after.setLabel()
                    + (after.awakening < 3 ? " §8· " + after.awakeningRemain() : " §8· 已满"));
            try {
                p.sendActionBar("§a觉醒" + roman + " · §f" + after.setLabel());
            } catch (Throwable ignored) { }
            try { EmberAwakenBlankGift.maybeGift(p, d); } catch (Throwable ignored) { } // D10865
        }
        EmberItemData step = findStepPiece(p, after, loadouts);
        if (step == null || runs == null) return false;
        EmberItemData previous = step.isCharm() ? after.charm : after.blade;
        String where = runs.equipPiece(p, step, previous);
        if (where == null) return false;
        String lab = ChatColor.stripColor(step.shortLabel());
        p.sendMessage(EmberRunService.P + "§a觉醒追 · 已换上 §f" + step.shortLabel()
                + " §7（" + where + "）· 可再抬觉醒（无确认钮）");
        try {
            p.sendActionBar("§a觉醒追 · 已换上 §f" + lab);
        } catch (Throwable ignored) { }

        // D4449: free enhance track onto new piece when old had higher +
        if (shouldSwapEnhance(previous, step)) {
            CoreRpgPlugin pl = plugin();
            EmberForgeService forge = pl == null ? null : pl.getEmberForge();
            if (forge != null && previous.uid != null && step.uid != null) {
                boolean ok = forge.swapUids(p, previous.uid, step.uid, true,
                        "/corerpg p1 equip " + step.uid + " swap confirm");
                if (ok) {
                    p.sendMessage(EmberRunService.P + "§a觉醒追 · 强化轨已挪到新件 §f+"
                            + previous.enhance + " §7（免费互换 · 无确认钮）");
                    try {
                        p.sendActionBar("§a觉醒追 · 强化轨 §f+" + previous.enhance);
                    } catch (Throwable ignored) { }
                }
            }
        }
        loadouts.refresh(p);
        return true;
    }


    /**
     * D4449: after enhance win — if bag has awk-raising piece, auto-equip (+ enhance swap).
     * Does not require awk edge; closes gap toward next breakthrough.
     */
    public static boolean maybeAfterEnhance(Player p, PlayerData d, EmberLoadoutService loadouts, EmberRunService runs) {
        if (p == null || loadouts == null || runs == null) return false;
        EmberLoadout lo = loadouts.refresh(p);
        if (lo == null || lo.awakening >= 3) return false;
        EmberItemData step = findStepPiece(p, lo, loadouts);
        if (step == null) return false;
        EmberItemData previous = step.isCharm() ? lo.charm : lo.blade;
        String where = runs.equipPiece(p, step, previous);
        if (where == null) return false;
        p.sendMessage(EmberRunService.P + "§a觉醒追 · 强化后换上 §f" + step.shortLabel()
                + " §7（" + where + "）");
        try {
            p.sendActionBar("§a觉醒追 · §f" + ChatColor.stripColor(step.shortLabel()));
        } catch (Throwable ignored) { }
        if (shouldSwapEnhance(previous, step)) {
            CoreRpgPlugin pl = plugin();
            EmberForgeService forge = pl == null ? null : pl.getEmberForge();
            if (forge != null && previous != null && previous.uid != null && step.uid != null) {
                forge.swapUids(p, previous.uid, step.uid, true,
                        "/corerpg p1 equip " + step.uid + " swap confirm");
                p.sendMessage(EmberRunService.P + "§a觉醒追 · 强化轨已挪到新件 §f+" + previous.enhance);
            }
        }
        loadouts.refresh(p);
        return true;
    }

    private static CoreRpgPlugin plugin() {
        org.bukkit.plugin.Plugin pl = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}

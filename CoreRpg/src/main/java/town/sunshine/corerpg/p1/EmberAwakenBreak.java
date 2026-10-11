package town.sunshine.corerpg.p1;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import town.sunshine.corerpg.PlayerData;

/**
 * D4385: awakening bump → auto-equip bag piece that raises awakening further.
 * Zero set coef / skill_mult / ConfirmTokens. Not EquipPath sticky.
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
     * After awakening bump: latch cue + auto-equip step piece if any.
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
        }
        EmberItemData step = findStepPiece(p, after, loadouts);
        if (step == null || runs == null) return false;
        EmberItemData active = step.isCharm() ? after.charm : after.blade;
        String where = runs.equipPiece(p, step, active);
        if (where == null) return false;
        String lab = ChatColor.stripColor(step.shortLabel());
        p.sendMessage(EmberRunService.P + "§a觉醒追 · 已换上 §f" + step.shortLabel()
                + " §7（" + where + "）· 可再抬觉醒（无确认钮）");
        try {
            p.sendActionBar("§a觉醒追 · 已换上 §f" + lab);
        } catch (Throwable ignored) { }
        loadouts.refresh(p);
        return true;
    }
}

package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D4513/D4577: forge-mark stock → auto-redeem for next awakening <b>tier</b> gap.
 * Zero mark drop raise / ConfirmTokens / K3 / skill_mult. Not forge sure-do twin.
 */
public final class EmberMarksBreak {

    public static final String C_AUTO = "p1_marks_awk_auto_t";

    private EmberMarksBreak() {}

    /** Tier needed for next awakening (0 if full). */
    public static int needTier(int awakening) {
        if (awakening >= 3) return 0;
        if (awakening <= 0) return 1;
        return awakening + 1;
    }

    public static int needEnhance(int awakening) {
        int next = awakening + 1;
        if (next == 2) return 6;
        if (next == 3) return 9;
        return 0;
    }

    /** Family for auto-redeem: active set, else focus, else blade. */
    public static String pickFamily(EmberLoadout lo, PlayerData d) {
        if (lo != null && lo.activeSet != null && !"none".equals(lo.activeSet))
            return lo.activeSet;
        if (d != null) {
            String fk = EmberSetFocus.familyKey(EmberSetFocus.get(d));
            if (fk != null) return fk;
        }
        if (lo != null && lo.blade != null && !"none".equals(lo.blade.family))
            return lo.blade.family;
        if (lo != null && lo.charm != null && !"none".equals(lo.charm.family))
            return lo.charm.family;
        return null;
    }

    /**
     * Slot that lags on <b>tier</b> for next awk (marks exchange only raises tier to T).
     * Returns missing blade/charm when fam-known; null if only enhance-lag or full.
     */
    public static String pickSlot(EmberLoadout lo) {
        return pickSlotForTier(lo, lo == null ? 0 : needTier(lo.awakening));
    }

    /** @param needT next-awk tier floor; 0 → null */
    public static String pickSlotForTier(EmberLoadout lo, int needT) {
        if (lo == null || needT <= 0) return null;
        // missing piece — redeem fills the hole (D4577)
        if (lo.blade == null && lo.charm != null && !"none".equals(lo.charm.family))
            return "blade";
        if (lo.charm == null && lo.blade != null && !"none".equals(lo.blade.family))
            return "charm";
        if (lo.blade == null || lo.charm == null) return null;
        if ("none".equals(lo.activeSet)) return null;
        boolean bladeTierBad = lo.blade.tier < needT;
        boolean charmTierBad = lo.charm.tier < needT;
        if (!bladeTierBad && !charmTierBad) return null; // enhance-only gap → don't waste marks
        if (bladeTierBad && !charmTierBad) return "blade";
        if (charmTierBad && !bladeTierBad) return "charm";
        if (lo.blade.tier != lo.charm.tier)
            return lo.blade.tier < lo.charm.tier ? "blade" : "charm";
        return lo.blade.enhance <= lo.charm.enhance ? "blade" : "charm";
    }

    /** True if bag already has unequipped standard (or better tier) piece for fam/slot at ≥ tier. */
    public static boolean bagHasTierPiece(Player p, EmberLoadoutService loadouts, EmberLoadout lo,
                                          String fam, String slot, int tier) {
        if (p == null || loadouts == null || fam == null || slot == null || tier <= 0) return false;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || !r.ok() || r.data == null) continue;
            EmberItemData x = r.data;
            if (!fam.equals(x.family) || !slot.equals(x.slot) || x.tier < tier) continue;
            if (lo != null) {
                if (lo.blade != null && x.uid.equals(lo.blade.uid)) continue;
                if (lo.charm != null && x.uid.equals(lo.charm.uid)) continue;
            }
            return true;
        }
        return false;
    }

    /**
     * After mark grant: try gained tier first, then scan T3→T1 for awaken-tier gap.
     */
    public static boolean maybeAfterMarkGain(Player p, PlayerData d, int gainedTier,
                                            EmberLoadoutService loadouts, EmberRunService runs) {
        if (p == null || d == null || loadouts == null || runs == null) return false;
        if (tryRedeemTier(p, d, gainedTier, loadouts, runs)) return true;
        // D4577: other tiers already full while gain was on a non-matching tier
        for (int t = 3; t >= 1; t--) {
            if (t == gainedTier) continue;
            if (tryRedeemTier(p, d, t, loadouts, runs)) return true;
        }
        return false;
    }

    /** Hub/settle re-check without a specific gain tier. */
    public static boolean maybeScan(Player p, PlayerData d, EmberLoadoutService loadouts, EmberRunService runs) {
        if (p == null || d == null || loadouts == null || runs == null) return false;
        for (int t = 3; t >= 1; t--) {
            if (tryRedeemTier(p, d, t, loadouts, runs)) return true;
        }
        return false;
    }

    static boolean tryRedeemTier(Player p, PlayerData d, int tier,
                                 EmberLoadoutService loadouts, EmberRunService runs) {
        if (tier < 1 || tier > 3) return false;
        int need = EmberRunRules.marksPerExchange();
        if (runs.marks(d, tier) < need) return false;
        String day = town.sunshine.corerpg.DailyService.today();
        String latch = C_AUTO + tier;
        if (d.periodCount(latch, day) > 0) return false;

        EmberLoadout lo = loadouts.refresh(p);
        int awkNeedT = needTier(lo == null ? 0 : lo.awakening);
        if (awkNeedT <= 0 || tier != awkNeedT) return false;

        String fam = pickFamily(lo, d);
        String slot = pickSlotForTier(lo, awkNeedT);
        if (fam == null || slot == null) return false;

        if (bagHasTierPiece(p, loadouts, lo, fam, slot, tier)) return false;

        boolean gate = EmberSkillKit.directedForgeOpen(d, runs, tier);
        String err = EmberRunRules.exchangeCheck(runs.marks(d, tier), tier, tier, fam, slot, gate);
        if (err != null) return false;
        if (EmberRunService.freeSlots(p) <= 0) return false;

        d.addPeriodCount(latch, day, 1);
        String uid = runs.autoRedeemMarks(p, fam, slot, tier);
        if (uid == null) {
            d.addPeriodCount(latch, day, -1);
            return false;
        }
        p.sendMessage(EmberRunService.P + "§a印记觉醒追 · 已自动兑换 §fT" + tier + " "
                + EmberItemData.familyName(fam) + EmberItemData.slotName(slot)
                + " §7· 无确认钮（不改印记产量）");
        try {
            p.sendActionBar("§a印记兑换 · §fT" + tier + " " + EmberItemData.slotName(slot));
        } catch (Throwable ignored) { }

        scheduleEquip(p, loadouts, runs, uid, fam, slot, tier, 25L);
        scheduleEquip(p, loadouts, runs, uid, fam, slot, tier, 60L); // D4577 durable retry
        return true;
    }

    static void scheduleEquip(Player p, EmberLoadoutService loadouts, EmberRunService runs,
                              String uid, String fam, String slot, int tier, long delay) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable equip = () -> {
            if (!p.isOnline()) return;
            EmberLoadout cur = loadouts.refresh(p);
            EmberItemData neu = (uid != null && !uid.isEmpty())
                    ? findByUid(p, loadouts, uid)
                    : findFreshRedeem(p, loadouts, fam, slot, tier);
            if (neu == null) return;
            // already wearing this uid
            if (cur != null) {
                if (cur.blade != null && neu.uid.equals(cur.blade.uid)) return;
                if (cur.charm != null && neu.uid.equals(cur.charm.uid)) return;
            }
            EmberItemData prev = "charm".equals(slot) ? (cur == null ? null : cur.charm)
                    : (cur == null ? null : cur.blade);
            String where = runs.equipPiece(p, neu, prev);
            if (where == null) return;
            p.sendMessage(EmberRunService.P + "§a印记觉醒追 · 已换上 §f"
                    + ChatColor.stripColor(neu.shortLabel()) + " §7（" + where + "）");
            if (EmberAwakenBreak.shouldSwapEnhance(prev, neu)) {
                CoreRpgPlugin cpl = pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
                EmberForgeService forge = cpl == null ? null : cpl.getEmberForge();
                if (forge != null && prev != null && prev.uid != null) {
                    forge.swapUids(p, prev.uid, neu.uid, true,
                            "/corerpg p1 equip " + neu.uid + " swap confirm");
                    p.sendMessage(EmberRunService.P + "§a印记觉醒追 · 强化轨已挪 §f+" + prev.enhance);
                }
            }
            loadouts.refresh(p);
        };
        if (pl != null) Bukkit.getScheduler().runTaskLater(pl, equip, delay);
        else if (delay <= 0) equip.run();
    }

    static EmberItemData findByUid(Player p, EmberLoadoutService loadouts, String uid) {
        if (p == null || loadouts == null || uid == null || uid.isEmpty()) return null;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r != null && r.ok() && r.data != null && uid.equals(r.data.uid))
                return r.data;
        }
        return null;
    }

    static EmberItemData findFreshRedeem(Player p, EmberLoadoutService loadouts,
                                         String fam, String slot, int tier) {
        if (p == null || loadouts == null) return null;
        EmberLoadout lo = loadouts.get(p);
        EmberItemData best = null;
        for (ItemStack it : p.getInventory().getContents()) {
            if (it == null || !loadouts.items().hasData(it)) continue;
            EmberItems.Read r = loadouts.items().read(it);
            if (r == null || !r.ok() || r.data == null) continue;
            EmberItemData x = r.data;
            if (!fam.equals(x.family) || !slot.equals(x.slot) || x.tier != tier) continue;
            if (x.enhance != 0 || x.quality != 0 || x.craft != 0) continue;
            if (lo != null) {
                if (lo.blade != null && x.uid.equals(lo.blade.uid)) continue;
                if (lo.charm != null && x.uid.equals(lo.charm.uid)) continue;
            }
            best = x;
        }
        return best;
    }
}

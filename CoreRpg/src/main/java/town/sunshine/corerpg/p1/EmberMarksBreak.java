package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D4513: forge-mark stock → auto-redeem standard piece for next awakening gap.
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
        return null;
    }

    /**
     * Lagging slot for next awk (blade/charm), or null if both already meet tier/enhance floor
     * or cannot form set.
     */
    public static String pickSlot(EmberLoadout lo) {
        if (lo == null || lo.blade == null || lo.charm == null) return null;
        if ("none".equals(lo.activeSet)) return null;
        int awk = lo.awakening;
        if (awk >= 3) return null;
        int needT = needTier(awk);
        int needE = needEnhance(awk);
        boolean bladeBad = lo.blade.tier < needT || lo.blade.enhance < needE;
        boolean charmBad = lo.charm.tier < needT || lo.charm.enhance < needE;
        if (!bladeBad && !charmBad) return null;
        if (bladeBad && !charmBad) return "blade";
        if (charmBad && !bladeBad) return "charm";
        // both bad: prefer lower tier, then lower enhance
        if (lo.blade.tier != lo.charm.tier)
            return lo.blade.tier < lo.charm.tier ? "blade" : "charm";
        return lo.blade.enhance <= lo.charm.enhance ? "blade" : "charm";
    }

    /**
     * After mark grant: if stock ≥ exchange and awaken-gapped, auto-redeem once per tier@day.
     */
    public static boolean maybeAfterMarkGain(Player p, PlayerData d, int tier,
                                            EmberLoadoutService loadouts, EmberRunService runs) {
        if (p == null || d == null || loadouts == null || runs == null) return false;
        if (tier < 1 || tier > 3) return false;
        int need = EmberRunRules.marksPerExchange();
        if (runs.marks(d, tier) < need) return false;
        String day = town.sunshine.corerpg.DailyService.today();
        String latch = C_AUTO + tier;
        if (d.periodCount(latch, day) > 0) return false;

        EmberLoadout lo = loadouts.refresh(p);
        String fam = pickFamily(lo, d);
        String slot = pickSlot(lo);
        if (fam == null || slot == null) return false;
        // only auto when this mark tier matches next-awk tier need
        int awkNeedT = needTier(lo.awakening);
        if (awkNeedT <= 0 || tier != awkNeedT) return false;

        boolean gate = runs.progressFlag(d, EmberRunRules.directedForgeFlag(tier));
        String err = EmberRunRules.exchangeCheck(runs.marks(d, tier), tier, tier, fam, slot, gate);
        if (err != null) return false;
        if (EmberRunService.freeSlots(p) <= 0) return false;

        d.addPeriodCount(latch, day, 1);
        String uid = runs.autoRedeemMarks(p, fam, slot, tier);
        if (uid == null) {
            d.addPeriodCount(latch, day, -1); // rollback latch
            return false;
        }
        p.sendMessage(EmberRunService.P + "§a印记觉醒追 · 已自动兑换 §fT" + tier + " "
                + EmberItemData.familyName(fam) + EmberItemData.slotName(slot)
                + " §7· 无确认钮（不改印记产量）");
        try {
            p.sendActionBar("§a印记兑换 · §fT" + tier + " " + EmberItemData.slotName(slot));
        } catch (Throwable ignored) { }

        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        final String fUid = uid;
        final String fFam = fam;
        final String fSlot = slot;
        final int fTier = tier;
        Runnable equip = () -> {
            if (!p.isOnline()) return;
            EmberLoadout cur = loadouts.refresh(p);
            EmberItemData neu = (fUid != null && !fUid.isEmpty())
                    ? findByUid(p, loadouts, fUid)
                    : findFreshRedeem(p, loadouts, fFam, fSlot, fTier);
            if (neu == null) return;
            EmberItemData prev = "charm".equals(fSlot) ? cur.charm : cur.blade;
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
        if (pl != null) Bukkit.getScheduler().runTaskLater(pl, equip, 25L);
        else equip.run();
        return true;
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

    /** Prefer +0 standard redeem of fam/slot/tier not currently equipped. */
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
            best = x; // last match ok (fresh delivery often last)
        }
        return best;
    }
}

package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import town.sunshine.corerpg.PlayerData;

/**
 * B2.180 图录 · 装备 (book §19.5: 图鉴 gives display and one-time resources only, never a permanent combat stat).
 * 20 entries: the T0 blade / charm plus every family × slot × T1..T3. An entry is registered the first time the
 * player holds a verified P1 item of that kind (inventory scan in EmberLoadoutService.refresh, plus a DB backfill of
 * the player's own item rows on join); test items given by an admin (source "admin") never count. Stage rewards are
 * one-time coin grants through the run ledger (D42). Pure rules + PlayerData counters; no Bukkit.
 */
public final class EmberCodex {

    private EmberCodex() {}

    public static final String C_ENTRY = "p1_codex_";       // p1_codex_<family>_<slot>_t<tier>@all = 1
    public static final String C_CLAIM = "p1_codex_stage_";  // p1_codex_stage_<n>@all = 1 once claimed
    public static final String C_CLAIM_QUIET = "p1_codex_claim_quiet"; // D4049 bitmask of tipped stages
    public static final String LEDGER_RUN = "codex";

    /** D42: thresholds (registered entries) and their one-time coin rewards. D243: registered as economy row S33
     *  (at5/at10/at15/at20.coin, dual-asserted by EmberEconomyTest); the ledger coin is tagged S33 on delivery. */
    public static final int[] STAGE_AT = {5, 10, 15, 20};
    public static final int[] STAGE_COIN = {200, 400, 600, 1000}; // golden mirrors; prefer stageCoin(i)

    private static final String[] STAGE_COIN_KEYS = {"at5.coin", "at10.coin", "at15.coin", "at20.coin"};

    /** D1064: S33 stage coin from economy yml SoT (nums identical to STAGE_COIN). */
    public static int stageCoin(int stage) {
        if (stage < 0 || stage >= STAGE_COIN_KEYS.length) {
            throw new IllegalArgumentException("codex stage " + stage);
        }
        return EmberEconomy.amount("S33", STAGE_COIN_KEYS[stage]);
    }

    public static final class Entry {
        public final String family, slot;
        public final int tier;
        Entry(String family, String slot, int tier) { this.family = family; this.slot = slot; this.tier = tier; }
        public String key() { return family + "_" + slot + "_t" + tier; }
        public String label() { return "T" + tier + " " + EmberItemData.familyName(family) + EmberItemData.slotName(slot); }
    }

    public static final List<Entry> ENTRIES;
    static {
        List<Entry> l = new ArrayList<Entry>();
        l.add(new Entry("none", "blade", 0));
        l.add(new Entry("none", "charm", 0));
        for (String f : EmberRunRules.FAMILIES)
            for (String s : new String[] {"blade", "charm"})
                for (int t = 1; t <= 3; t++) l.add(new Entry(f, s, t));
        ENTRIES = Collections.unmodifiableList(l);
    }

    public static String key(String family, String slot, int tier) {
        return (tier == 0 ? "none" : family) + "_" + slot + "_t" + tier;
    }

    public static boolean counts(EmberItemData d) {
        return d != null && d.validate() == null && !"admin".equals(d.source);
    }

    public static boolean has(PlayerData pd, String key) { return pd != null && pd.periodCount(C_ENTRY + key, "all") > 0; }

    /** @return true when this call registered a new entry */
    public static boolean register(PlayerData pd, String family, String slot, int tier, String source) {
        if (pd == null || "admin".equals(source) || tier < 0 || tier > 3) return false;
        String k = key(family, slot, tier);
        boolean known = false;
        for (Entry e : ENTRIES) if (e.key().equals(k)) { known = true; break; }
        if (!known || has(pd, k)) return false;
        pd.addPeriodCount(C_ENTRY + k, "all", 1);
        return true;
    }

    public static boolean register(PlayerData pd, EmberItemData d) {
        return counts(d) && register(pd, d.family, d.slot, d.tier, d.source);
    }

    public static int size() { return ENTRIES.size(); }

    public static int count(PlayerData pd) {
        int n = 0;
        for (Entry e : ENTRIES) if (has(pd, e.key())) n++;
        return n;
    }

    public static boolean claimed(PlayerData pd, int stage) { return pd != null && pd.periodCount(C_CLAIM + stage, "all") > 0; }

    /** Stages reached and not yet claimed (0-based indices). */
    public static List<Integer> claimable(PlayerData pd) {
        List<Integer> out = new ArrayList<Integer>();
        int n = count(pd);
        for (int i = 0; i < STAGE_AT.length; i++) if (n >= STAGE_AT[i] && !claimed(pd, i)) out.add(i);
        return out;
    }

    public static String stageLabel(PlayerData pd, int stage) {
        if (claimed(pd, stage)) return "§a已领取";
        int n = count(pd);
        return n >= STAGE_AT[stage] ? "§e可领取" : "§7还差 " + (STAGE_AT[stage] - n) + " 种";
    }

    /**
     * D1362: meaty progress toward next stage coin claim — display only, never combat.
     */
    public static String progressRemain(PlayerData pd) {
        int n = count(pd);
        int total = size();
        java.util.List<Integer> can = claimable(pd);
        if (!can.isEmpty()) {
            int i = can.get(0);
            return "图录 " + n + "/" + total + " · 可领集齐" + STAGE_AT[i] + "（币" + stageCoin(i) + "）"
                    + (can.size() > 1 ? " · 另有" + (can.size() - 1) + "档待领" : "");
        }
        for (int i = 0; i < STAGE_AT.length; i++) {
            if (claimed(pd, i)) continue;
            int need = STAGE_AT[i];
            return "图录 " + n + "/" + total + " · 距集齐" + need + "还差" + Math.max(0, need - n) + "种";
        }
        return "图录 " + n + "/" + total + " · 阶段奖励已全领";
    }
    /** D4049: bitmask of stages already quiet-tipped. */
    public static int quietMask(PlayerData pd) {
        return pd == null ? 0 : pd.periodCount(C_CLAIM_QUIET, "all");
    }

    public static void markQuietStage(PlayerData pd, int stage) {
        if (pd == null || stage < 0 || stage > 30) return;
        int cur = quietMask(pd);
        int bit = 1 << stage;
        if ((cur & bit) != 0) return;
        pd.addPeriodCount(C_CLAIM_QUIET, "all", bit);
    }

    public static boolean stageQuietTold(PlayerData pd, int stage) {
        return (quietMask(pd) & (1 << stage)) != 0;
    }

    /** First claimable stage not yet quiet-tipped; -1 if none. */
    public static int nextQuietClaimable(PlayerData pd) {
        for (int i : claimable(pd)) if (!stageQuietTold(pd, i)) return i;
        return -1;
    }

}

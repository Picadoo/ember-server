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
    public static final String LEDGER_RUN = "codex";

    /** D42: thresholds (registered entries) and their one-time coin rewards. D243: registered as economy row S33
     *  (at5/at10/at15/at20.coin, dual-asserted by EmberEconomyTest); the ledger coin is tagged S33 on delivery. */
    public static final int[] STAGE_AT = {5, 10, 15, 20};
    public static final int[] STAGE_COIN = {200, 400, 600, 1000};

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
}

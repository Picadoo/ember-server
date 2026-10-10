package town.sunshine.corerpg.p1;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import town.sunshine.corerpg.PlayerData;

/**
 * D623 · mustfeel R · W2a soft pity：账户连续 {@link #NEED} 次合格花样局 {@code eventRoom} 空
 * → 下一合格局强制抽事件（房/kind 仍由 seed 定）。禁止叠第二事件；禁挑战/首通/团本/连战/深渊保底。
 * 纯逻辑，无 Bukkit。不改 {@code event_rate} / balance_version。
 */
public final class EmberEventSoftPity {

    /** period {@code all} dry-streak counter */
    public static final String C_DRY = "p1_ev_dry";
    /** consecutive empty-event variety settles before next force */
    public static final int NEED = 1;

    private static final String[] ROOMS = {"r1", "r2", "r3"};

    private EmberEventSoftPity() {}

    public static int get(PlayerData d) {
        return d == null ? 0 : Math.max(0, d.periodCount(C_DRY, "all"));
    }

    /** Short Chinese hope line (hub / PAPI). */
    public static String hope(PlayerData d) {
        int cur = get(d);
        if (cur >= NEED) return String.format(Locale.ROOT, "§a事件保底 %d/%d · 下次重打必有房间事件", NEED, NEED);
        return String.format(Locale.ROOT, "§7事件保底 §f%d§7/%d", cur, NEED);
    }

    /**
     * When dry is ready ({@code get >= NEED}) and {@code v[2]} empty and events non-empty,
     * fill room + kind from {@code seed}. Never adds a second event if one is already present.
     * Caller gates on dry readiness; this method also no-ops when slot already filled.
     *
     * @return true when this call forced an event into an empty slot
     */
    public static boolean ensureEvent(String[] v, List<String> events, long seed) {
        if (v == null || v.length < 4) return false;
        if (v[2] != null && !v[2].isEmpty()) return false; // already has event — no stack
        if (events == null || events.isEmpty()) return false;
        Random r = new Random(seed);
        String room = ROOMS[Math.floorMod(r.nextInt(), ROOMS.length)];
        String kind = pick(events, r);
        if (kind == null) return false;
        v[2] = room;
        v[3] = kind;
        return true;
    }

    /**
     * After a variety-eligible mainline settle: empty event room increments dry (cap {@link #NEED});
     * had event room resets dry to 0. Non-eligible → no-op.
     */
    public static void onSettle(PlayerData pd, boolean varietyEligible, boolean hadEventRoom) {
        if (pd == null || !varietyEligible) return;
        int cur = get(pd);
        if (hadEventRoom) {
            if (cur > 0) pd.addPeriodCount(C_DRY, "all", -cur);
            return;
        }
        if (cur < NEED) pd.addPeriodCount(C_DRY, "all", 1);
    }

    static String pick(List<String> pool, Random r) {
        if (pool == null || pool.isEmpty()) return null;
        java.util.ArrayList<String> clean = new java.util.ArrayList<String>();
        for (String id : pool) {
            if (id != null && !id.isEmpty() && !clean.contains(id)) clean.add(id);
        }
        if (clean.isEmpty()) return null;
        return clean.get(Math.floorMod(r.nextInt(), clean.size()));
    }
}

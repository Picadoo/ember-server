package town.sunshine.corerpg.p1;

import java.util.List;
import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * D6817: account-level drop-craft pity for mainline {@code base_item}.
 * Soft floor at craft≥1 after {@link #NEED} consecutive craft0 drops.
 * Never forces max craft. Zero {@link EmberRunRules#CRAFT_WEIGHTS} raise.
 * Not DropQualityPity densify (orthogonal craft axis).
 */
public final class EmberDropCraftPity {

    public static final String C_PITY = "p1_drop_c_pity";
    public static final int NEED = 5;
    public static final int FLOOR = 1;

    /** D12413: {@code drop_craft_pity.settle_remain}; default true when key / section absent. */
    private static volatile boolean settleRemain = true;

    private EmberDropCraftPity() {}

    public static int get(PlayerData d) {
        return d == null ? 0 : Math.max(0, d.periodCount(C_PITY, "all"));
    }

    public static boolean applyToGrants(PlayerData pd, List<EmberRunRules.Grant> grants) {
        if (pd == null || grants == null) return false;
        boolean forced = false;
        for (int i = 0; i < grants.size(); i++) {
            EmberRunRules.Grant g = grants.get(i);
            if (g == null || g.kind != EmberRunRules.Kind.ITEM || g.item == null) continue;
            if (!"base_item".equals(g.key)) continue;
            EmberRunRules.ItemRoll r = g.item;
            int c = r.craft;
            int cur = get(pd);
            if (cur >= NEED && c < FLOOR) {
                c = FLOOR;
                grants.set(i, new EmberRunRules.Grant(g.key, g.kind, g.id, g.amount,
                        new EmberRunRules.ItemRoll(r.family, r.slot, r.tier, r.quality, c)));
                forced = true;
            }
            if (c >= FLOOR) {
                if (cur > 0) pd.addPeriodCount(C_PITY, "all", -cur);
            } else {
                pd.addPeriodCount(C_PITY, "all", 1);
                try {
                    int extra = EmberDropCraftNudge.consumeExtra(pd); // D10737
                    if (extra > 0) pd.addPeriodCount(C_PITY, "all", extra);
                } catch (Throwable ignored) { }
            }
            break;
        }
        return forced;
    }

    public static boolean applicable(EmberRunSession s, EmberRunMaps.MapDef m) {
        return EmberDropQualityPity.applicable(s, m);
    }

    /** D12413 TIP-Q hot flag; default true. */
    public static boolean settleRemainEnabled() {
        return settleRemain;
    }

    /** Apply from ember-v1.yml root (or null); absent {@code drop_craft_pity.settle_remain} → true. */
    public static void loadSettleRemain(org.bukkit.configuration.ConfigurationSection root) {
        if (root == null) {
            settleRemain = true;
            return;
        }
        org.bukkit.configuration.ConfigurationSection sec = root.getConfigurationSection("drop_craft_pity");
        if (sec == null) {
            settleRemain = true;
            return;
        }
        settleRemain = sec.getBoolean("settle_remain", true);
    }

    /** Test hook; {@code null} restores default true. */
    public static void setSettleRemainEnabledForTest(Boolean enabled) {
        settleRemain = enabled == null || enabled.booleanValue();
    }

    /**
     * D12410–D12413: settle chat/AB remain only when applicable, not forced, and hot flag on.
     * Forced trigger path must never stack remain.
     */
    public static boolean shouldAnnounceSettleRemain(boolean applicable, boolean forced, boolean enabled) {
        return applicable && !forced && enabled;
    }

    /** D1323-style: clears remaining until craft floor (unary / no nudge). */
    public static int clearsLeft(int cur) {
        int c = Math.max(0, cur);
        if (c >= NEED) return 0;
        return NEED - c;
    }

    /**
     * D12780: when day-first craft pity nudge is still ready and more than one clear remains,
     * report one fewer clear (next craft0 tick will also add {@link EmberDropCraftNudge#BONUS}).
     */
    public static int clearsLeft(int cur, boolean nudgeReady) {
        int left = clearsLeft(cur);
        if (nudgeReady && left > 1) return left - 1;
        return left;
    }

    /** Unary remain — no nudge (tests / no PlayerData). */
    public static String remainLine(int cur) {
        return remainLine(cur, false);
    }

    /**
     * D12780: non-hope remain with optional day-first nudge honesty —
     * {@code 精工软保底 3/5 · 再连出精工0×1触发精工1底 · 今日加码待用}.
     */
    public static String remainLine(int cur, boolean nudgeReady) {
        int c = Math.max(0, cur);
        if (c >= NEED) return "精工软保底已满 · 下次主线装必≥精工1";
        int left = clearsLeft(c, nudgeReady);
        String base = "精工软保底 " + c + "/" + NEED + " · 再连出精工0×" + left + "触发精工1底";
        if (nudgeReady && c < NEED) return base + " · 今日加码待用";
        return base;
    }

    public static String remainLine(PlayerData d) {
        return remainLine(get(d), EmberDropCraftNudge.ready(d));
    }
}

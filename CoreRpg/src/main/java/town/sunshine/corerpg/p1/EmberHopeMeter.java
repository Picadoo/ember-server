package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * D601: unified visible hope meters for enhance + reroll pity.
 * Display only — does not change RATE / MAX_TRIES / affix pity math.
 */
public final class EmberHopeMeter {

    public static final String READY_ENHANCE = "下次必成";
    public static final String READY_REROLL = "这次必出上限档";

    private EmberHopeMeter() {}

    /**
     * Shared glance style (D458 / D601).
     * @param cur current progress (1-based attempt or pity count as caller defines)
     * @param need threshold at which the next outcome is forced
     * @param readyHint suffix when cur &gt;= need (e.g. {@link #READY_ENHANCE})
     */
    public static String glance(int cur, int need, String readyHint) {
        if (need <= 0) return "§8保底未配置";
        int p = Math.max(0, cur);
        String hint = readyHint == null || readyHint.isEmpty() ? "必成" : readyHint;
        if (p >= need) return String.format(Locale.ROOT, "§a保底 %d/%d · %s", need, need, hint);
        return String.format(Locale.ROOT, "§7保底 §f%d§7/%d", p, need);
    }

    /** Hub/gear empty half-line. */
    public static String emptySlot(String slotLabel) {
        return "§8" + slotLabel + "—";
    }

    /**
     * Enhance hope meter from live item counters ({@code d.pity} + {@link EmberUpgradeRules#maxTries}).
     * Progress = next attempt number ({@code pity + 1}); full / unforgeable → honest short line.
     */
    public static String enhance(EmberItemData d) {
        if (d == null) return emptySlot("无件");
        if (d.isArmor()) return emptySlot("甲不可强");
        EmberUpgradeRules.Plan c = EmberUpgradeRules.enhanceCheck(d);
        if (!c.ok()) {
            if (d.enhance >= EmberTables.MAX_ENHANCE) return "§8强化已满 +" + EmberTables.MAX_ENHANCE;
            return "§8" + (c.error == null ? "不可强化" : c.error);
        }
        int need = EmberUpgradeRules.maxTries(d.enhance + 1);
        int cur = EmberUpgradeRules.attemptNo(d);
        return glance(cur, need, READY_ENHANCE);
    }
}

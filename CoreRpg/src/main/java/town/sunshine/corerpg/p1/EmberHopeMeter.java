package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * D601–D604: hope meters for enhance / reroll / quality / refine / sig-chase.
 * Display only — does not change RATE / MAX_TRIES / affix pity / qualityCost / refineCost / STAMP_RATE / IMPRINT_* / drop weights / clear gates.
 */
public final class EmberHopeMeter {

    public static final String READY_ENHANCE = "下次必成";
    public static final String READY_REROLL = "这次必出上限档";

    /** Workshop deterministic quality cap (卓越). 极品 is drop-only. */
    public static final int QUALITY_FORGE_CAP = 2;

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

    /**
     * D602: quality deterministic-bypass hope meter.
     * Workshop can raise 标准→精良→卓越 ({@link #QUALITY_FORGE_CAP}); 极品 drop-only.
     * Progress cur = min(quality, cap) / need = cap; next step names the guaranteed target.
     */
    public static String quality(EmberItemData d) {
        if (d == null) return emptySlot("无件");
        if (d.isArmor()) return emptySlot("甲看掉落");
        int q = Math.max(0, d.quality);
        int cur = Math.min(q, QUALITY_FORGE_CAP);
        int need = QUALITY_FORGE_CAP;
        if (q >= 3) return String.format(Locale.ROOT, "§a成色 %d/%d · 已满极品", need, need);
        if (q >= QUALITY_FORGE_CAP) {
            return String.format(Locale.ROOT, "§7成色 %d/%d · 工坊顶 · 极品仅掉落", need, need);
        }
        // next guaranteed workshop step
        String next = EmberItemData.qualityName(q + 1);
        EmberUpgradeRules.Cost c = EmberUpgradeRules.qualityCost(q);
        String cost = c == null ? "" : " · " + EmberGearNextHint.costShort(c);
        return String.format(Locale.ROOT, "§a成色 %d/%d · 下次确定升%s%s", cur, need, next, cost);
    }

    /**
     * D603: refine deterministic-bypass hope meter.
     * Workshop raises craft 0→1→2→3 (0%→2%→4%→6%); always succeeds when paid.
     */
    public static String refine(EmberItemData d) {
        if (d == null) return emptySlot("无件");
        if (d.isArmor()) return emptySlot("甲看掉落");
        int craft = Math.max(0, d.craft);
        int need = EmberTables.MAX_CRAFT;
        int cur = Math.min(craft, need);
        if (craft >= need) {
            return String.format(Locale.ROOT, "§a精工 %d/%d · 已满 6%%", need, need);
        }
        EmberUpgradeRules.Cost c = EmberUpgradeRules.refineCost(craft);
        String cost = c == null ? "" : " · " + EmberGearNextHint.costShort(c);
        int nextPct = (craft + 1) * 2;
        return String.format(Locale.ROOT, "§a精工 %d/%d · 下次确定升%d%%%s", cur, need, nextPct, cost);
    }

    /**
     * D604: signature imprint-chase hope meter (徽记 → 烙印兑换).
     * {@code need} = {@link EmberSignature#IMPRINT_MARKS}; full = deterministic imprint available.
     * Never gates dungeon clears.
     */
    public static String sigChase(EmberSignature.Def def, int marks) {
        if (def == null) return "§8追烙未选 · /corerpg p1 sig chase";
        int need = EmberSignature.IMPRINT_MARKS;
        int m = Math.max(0, marks);
        String who = def.id + " " + def.name;
        String map = def.map == null ? "?" : def.map.toUpperCase(Locale.ROOT);
        if (m >= need) {
            return String.format(Locale.ROOT, "§a追烙就绪 %d/%d · %s · %s 满可烙印（不锁通关）", need, need, who, map);
        }
        return String.format(Locale.ROOT, "§e追烙 %d/%d · %s · %s 徽记 · 满可烙印（不锁通关）", m, need, who, map);
    }
}

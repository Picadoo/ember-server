package town.sunshine.corerpg.p1;

import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

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

    /**
     * D608: gem clear-pity hope (通关保底 → 下一颗石).
     * {@code need} = {@link EmberGemAcquire#PITY_NEED}; full = next clear grants a gem.
     */
    public static String gemPity(int cur, int need) {
        return glance(cur, need, "下一通关得石");
    }

    /**
     * D610: map gear-identity chase (图身份 · 图鉴进度). Never gates clears; never changes drop rates.
     * need = map tier × 2 (blade+charm) for {@code lootFamily}; have = codex registered.
     */
    public static String mapGearIdentity(EmberRunMaps.MapDef lm, PlayerData pd) {
        if (lm == null) return "§8本图身份未登记 · 每局仍必掉1装 · 不锁通关";
        if (lm.lootFamily == null || lm.lootFamily.isEmpty()) {
            return "§8本图无装备偏向 · §a每局1装·差装可分解·不锁通关";
        }
        int tier = 1;
        String dl = lm.dropLabel == null ? "" : lm.dropLabel.trim().toUpperCase(Locale.ROOT);
        if (dl.startsWith("T") && dl.length() >= 2) {
            try { tier = Math.max(1, Math.min(3, Integer.parseInt(dl.substring(1, 2)))); } catch (NumberFormatException ignored) {}
        }
        int have = 0, need = 0;
        for (int t = 1; t <= tier; t++) {
            for (String slot : new String[] {"blade", "charm"}) {
                need++;
                if (pd != null && EmberCodex.has(pd, EmberCodex.key(lm.lootFamily, slot, t))) have++;
            }
        }
        String label = EmberRunMaps.lootLabel(lm);
        String head = have >= need ? "§a" : "§e";
        return String.format(Locale.ROOT, "%s本图身份 · §f%s · 图鉴 %d/%d · §a每局1装·差装可分解·不锁通关",
                head, label, have, need);
    }


    /**
     * D611: drop-quality pity hope (连续标准 → 下次至少精良). Never forces 极品.
     */
    public static String dropQuality(int cur, int need) {
        return glance(cur, need, "下次掉落至少精良");
    }


    /**
     * D615: imprint spend hope — marks have/need + coin gate. Zero price change.
     */
    public static String imprintSpend(int marks, int need, int coinHave, int coinNeed) {
        int m = Math.max(0, marks), n = Math.max(1, need);
        String markLine = glance(m, n, "徽记够·可烙");
        boolean coinOk = coinHave >= coinNeed;
        String coinLine = coinOk
                ? String.format(Locale.ROOT, "§a币够 §f%d§7/§f%d", coinNeed, coinNeed)
                : String.format(Locale.ROOT, "§c币差 §f%d§7/§f%d", Math.max(0, coinHave), coinNeed);
        return markLine + " §8· " + coinLine;
    }

    /**
     * D615: attune unlock hope — marks have/need toward Shift-unlock. Zero ALT_MARKS change.
     */
    public static String attuneUnlock(int marks, int need) {
        return glance(Math.max(0, marks), Math.max(1, need), "Shift解锁调律");
    }


    /**
     * D616: one-line Q01–Q07 loot-identity board (readable bias). Zero drop-rate change.
     */
    public static String mapGearBoard(EmberRunMaps maps) {
        if (maps == null) return "§8图身份未载入";
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 7; i++) {
            String k = String.format(Locale.ROOT, "q0%d", i);
            EmberRunMaps.MapDef lm = maps.byKey(k);
            if (lm == null) continue;
            if (sb.length() > 0) sb.append(" §8· ");
            sb.append("§e").append(k.toUpperCase(Locale.ROOT)).append("§f ")
                    .append(EmberRunMaps.lootLabel(lm));
        }
        return sb.length() == 0 ? "§8无主线偏向" : sb.toString();
    }


    /**
     * D620: daily bounty hope — clears toward next tier. Zero reward-table change.
     * {@code nextNeed} = next tier clears threshold; {@code done} = today's settled clears.
     */
    public static String bountyHope(int done, int nextNeed, String rewardHint) {
        if (nextNeed <= 0) return "§a日委托 · 已全部完成";
        int d = Math.max(0, done), n = Math.max(1, nextNeed);
        String hint = rewardHint == null || rewardHint.isEmpty() ? "下一档奖励" : rewardHint;
        if (d >= n) return String.format(Locale.ROOT, "§a日委托 %d/%d · %s", n, n, hint);
        return String.format(Locale.ROOT, "§e日委托 %d/%d · 再通关 %d 局 → %s", d, n, n - d, hint);
    }

    /** D620: weekly featured imprint bonus hope. */
    public static String featuredHope(int left, int cap) {
        if (cap <= 0) return "§8本周精选印记奖励未开放";
        int used = Math.max(0, cap - Math.max(0, left));
        if (left <= 0) return String.format(Locale.ROOT, "§a精选印记 %d/%d · 本周已领满", cap, cap);
        return String.format(Locale.ROOT, "§e精选印记 %d/%d · 还剩 %d 次多发印记", used, cap, left);
    }


    /** D623: sign-in hope — next slot of the month. */
    public static String signHope(boolean signedToday, int count, String nextReward) {
        String rw = nextReward == null || nextReward.isEmpty() ? "奖励" : nextReward;
        if (signedToday) return String.format(Locale.ROOT, "§a签到希望 · 已签 §f%d§a 次 · 明天第 §f%d§a 次 · %s", count, count + 1, rw);
        return String.format(Locale.ROOT, "§e签到希望 · 今天第 §f%d§e 次 · %s", count + 1, rw);
    }

    /** D623: online milestone hope. */
    public static String onlineHope(int mins, int nextMin, String rewardHint) {
        if (nextMin <= 0) return String.format(Locale.ROOT, "§a在线希望 · 今日 %d 分 · 档位已齐", Math.max(0, mins));
        int left = Math.max(0, nextMin - Math.max(0, mins));
        String rw = rewardHint == null || rewardHint.isEmpty() ? "下一档" : rewardHint;
        return String.format(Locale.ROOT, "§e在线希望 · %d/%d 分 · 还差 %d · %s", Math.max(0, mins), nextMin, left, rw);
    }

    /** D624: abyss progress + next fee. */
    public static String abyssHope(int best, int maxStart, int nextFee, boolean canAfford) {
        if (maxStart <= 0) return "§8深渊希望 · 未开放";
        String fee = nextFee <= 0 ? "免费" : (nextFee + " 币");
        String pay = nextFee <= 0 ? "" : (canAfford ? " §a费用够" : " §c费用不足");
        if (best >= maxStart && best > 0)
            return String.format(Locale.ROOT, "§a深渊希望 · 最高 §f%d§a · 可开 §f%d§a · 下层 %s%s", best, maxStart, fee, pay);
        return String.format(Locale.ROOT, "§e深渊希望 · 最高 §f%d§e · 可开 §f%d§e · 下层 %s%s", best, maxStart, fee, pay);
    }

    /** D625: raid weekly hope. */
    public static String raidHope(int used, int cap) {
        if (cap <= 0) return "§8团本希望 · 本周无次数帽";
        if (used >= cap) return String.format(Locale.ROOT, "§a团本希望 · %d/%d · 本周已满（失败不扣）", cap, cap);
        return String.format(Locale.ROOT, "§e团本希望 · %d/%d · 还剩 %d 次通关名额", used, cap, cap - used);
    }

    /** D626: gearlib inventory hope. */
    public static String gearlibHope(int stored, int junk, int undoMin) {
        return String.format(Locale.ROOT, "§e装备库希望 · 库存 §f%d§e · 可批量 junk §f%d§e · 分解撤销 %d 分",
                Math.max(0, stored), Math.max(0, junk), Math.max(0, undoMin));
    }


    /** D628: weekly forge goal hope. */
    public static String forgeGoalHope(String label, String gap) {
        if (label == null || label.isEmpty() || label.contains("未选"))
            return "§8工坊希望 · 未选目标 · /corerpg p1 forgegoal";
        String g = gap == null ? "" : gap;
        return String.format(Locale.ROOT, "§e工坊希望 · §f%s §8· %s", label, g);
    }

    /** D629: stamina hope — how many daily runs left. */
    public static String staminaHope(int cur, int max, int dailyCost) {
        if (dailyCost <= 0) return String.format(Locale.ROOT, "§e体力希望 · %d/%d", cur, max);
        int runs = Math.max(0, cur) / dailyCost;
        if (runs <= 0) return String.format(Locale.ROOT, "§c体力希望 · %d/%d · 不够一场日常", Math.max(0, cur), max);
        return String.format(Locale.ROOT, "§e体力希望 · %d/%d · 约还能打 §f%d§e 场日常", Math.max(0, cur), max, runs);
    }

    /** D630: vault mat hope — shortest gap line. */
    public static String vaultHope(String shortestGap) {
        if (shortestGap == null || shortestGap.isEmpty()) return "§a材料希望 · 强化/精工材料够";
        return "§e材料希望 · " + shortestGap;
    }

    /** D631: next honor hope. */
    public static String honorHope(int earned, int total, String nextCond) {
        if (earned >= total && total > 0) return String.format(Locale.ROOT, "§a勋记希望 · %d/%d · 已齐", earned, total);
        String c = nextCond == null || nextCond.isEmpty() ? "下一勋记" : nextCond;
        return String.format(Locale.ROOT, "§e勋记希望 · %d/%d · 下一：%s", earned, total, c);
    }


    /** D633: codex hope. */
    public static String codexHope(int have, int total, String nextStage) {
        if (total <= 0) return "§8图录希望 · 未配置";
        if (have >= total) return String.format(Locale.ROOT, "§a图录希望 · %d/%d · 已齐", have, total);
        String n = nextStage == null || nextStage.isEmpty() ? "继续收集" : nextStage;
        return String.format(Locale.ROOT, "§e图录希望 · %d/%d · %s", have, total, n);
    }

    /** D634: short-expedition first-clear hope. */
    public static String sxFcHope(int unpaid) {
        if (unpaid <= 0) return "§a短征希望 · 首通奖励已齐";
        return String.format(Locale.ROOT, "§e短征希望 · 还有 §f%d§e 张首通未领", unpaid);
    }

    /** D635: today route hope. */
    public static String routeHope(String next) {
        if (next == null || next.isEmpty()) return "§8今日希望 · 暂无下一步";
        return "§e今日希望 · " + next;
    }

    /** D636: dual signature path hope. */
    public static String dualHope(String line) {
        if (line == null || line.isEmpty()) return "§8双签希望 · 未开启";
        return "§e双签希望 · " + line;
    }

}

package town.sunshine.corerpg.p1;

/**
 * D12256–D12263: forge choice-feel half-lines (brand / convert / reroll).
 * D12442–D12448 TIP-S: stable / gamble 「选的是」 preview lines (zero cost/rate/QUALITY_W).
 * Display only — zero damage mult, price, weekly-cap, or Affix Pack change.
 */
public final class EmberForgeChoice {

    /** D12448: {@code forge_choice.stable_gamble}; default true when key / section absent. */
    private static volatile boolean stableGamble = true;

    private EmberForgeChoice() {}

    /** D12257: brand pin preview — will-get feel, not damage. */
    public static String brandWillGet(String affixName) {
        String n = affixName == null || affixName.isEmpty() ? "烙纹" : affixName;
        return "§e将得到：§f" + n + " §7手感 · §8不加伤";
    }

    /**
     * D12258: convert success leftover.
     * {@code leftAfter} = WEEKLY_CAP - usedAfterIncrement; gap null/empty or left≤0 → 「齐」.
     */
    public static String convertLeftover(int leftAfter, String gapOrNull) {
        int left = Math.max(0, leftAfter);
        String gap = left <= 0 || gapOrNull == null || gapOrNull.isEmpty()
                ? "§a齐"
                : gapOrNull;
        return "§7本周还可转 §f" + left + " §7次 · 下缺口：" + gap;
    }

    /** D12259: reroll preview choice half-line. */
    public static String rerollChoice(boolean lock) {
        return lock ? "§7选的是：§f锁定·只重抽档" : "§7选的是：§f随机词条";
    }

    /**
     * D12442: stable-path preview choice half-line.
     * {@code kind} = 成色 / 精工 / 强化 → {@code §7选的是：§f稳线·{k}确定+1 §8· 非旁路赌锻}
     */
    public static String stableChoice(String kind) {
        String k = kind == null || kind.isEmpty() ? "稳办" : kind;
        return "§7选的是：§f稳线·" + k + "确定+1 §8· 非旁路赌锻";
    }

    /**
     * D12443: forgeroll preview choice half-line.
     * {@code floor} false → 旁路·随机锻（极品仍0）；true → 旁路·下限锻（成色≥精良·极品仍0）.
     */
    public static String gambleChoice(boolean floor) {
        return floor
                ? "§7选的是：§f旁路·下限锻（成色≥精良·极品仍0）"
                : "§7选的是：§f旁路·随机锻（极品仍0）";
    }

    /**
     * D12447: optional half-line when weekly forgeGoal is a stable/deterministic path
     * (ENHANCE / QUALITY / REFINE / UPGRADE / BRAND / GEM / LOCK) and player is previewing forgeroll.
     * ROLL / NONE / CONVERT → empty.
     */
    public static String goalBypassHint(int forgeGoal) {
        if (forgeGoal == EmberForgeGoal.ENHANCE
                || forgeGoal == EmberForgeGoal.QUALITY
                || forgeGoal == EmberForgeGoal.REFINE
                || forgeGoal == EmberForgeGoal.UPGRADE
                || forgeGoal == EmberForgeGoal.BRAND
                || forgeGoal == EmberForgeGoal.GEM
                || forgeGoal == EmberForgeGoal.LOCK) {
            return "§8本周目标是稳线/确定路 · 此为旁路";
        }
        return "";
    }

    /** D12448 hot flag. */
    public static boolean stableGambleEnabled() {
        return stableGamble;
    }

    /** Apply from ember-v1.yml root (or null); absent {@code forge_choice.stable_gamble} → true. */
    public static void loadStableGamble(org.bukkit.configuration.ConfigurationSection root) {
        if (root == null) {
            stableGamble = true;
            return;
        }
        org.bukkit.configuration.ConfigurationSection sec = root.getConfigurationSection("forge_choice");
        if (sec == null) {
            stableGamble = true;
            return;
        }
        stableGamble = sec.getBoolean("stable_gamble", true);
    }

    /** Test hook; {@code null} restores default true. */
    public static void setStableGambleEnabledForTest(Boolean enabled) {
        stableGamble = enabled == null || enabled.booleanValue();
    }
}

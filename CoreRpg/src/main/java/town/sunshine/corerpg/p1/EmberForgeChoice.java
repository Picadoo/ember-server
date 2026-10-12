package town.sunshine.corerpg.p1;

/**
 * D12256–D12263: forge choice-feel half-lines (brand / convert / reroll).
 * Display only — zero damage mult, price, weekly-cap, or Affix Pack change.
 */
public final class EmberForgeChoice {

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
}

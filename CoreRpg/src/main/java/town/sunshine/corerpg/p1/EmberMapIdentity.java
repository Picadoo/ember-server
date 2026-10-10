package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.PlayerData;

/**
 * D1341: map gear-identity remain (图鉴 have/need) — display only.
 * Never gates clears; never changes drop rates. Not a hope PAPI twin.
 */
public final class EmberMapIdentity {

    private EmberMapIdentity() {}

    public static int[] haveNeed(EmberRunMaps.MapDef lm, PlayerData pd) {
        if (lm == null || lm.lootFamily == null || lm.lootFamily.isEmpty()) return new int[]{0, 0};
        int tier = 1;
        String dl = lm.dropLabel == null ? "" : lm.dropLabel.trim().toUpperCase(java.util.Locale.ROOT);
        if (dl.startsWith("T") && dl.length() >= 2) {
            try { tier = Math.max(1, Math.min(3, Integer.parseInt(dl.substring(1, 2)))); }
            catch (NumberFormatException ignored) {}
        }
        int have = 0, need = 0;
        for (int t = 1; t <= tier; t++) {
            for (String slot : new String[] {"blade", "charm"}) {
                need++;
                if (pd != null && EmberCodex.has(pd, EmberCodex.key(lm.lootFamily, slot, t))) have++;
            }
        }
        return new int[]{have, need};
    }

    /** Plain remain: {@code 图身份 焚烬·T1 · 图鉴 1/2 · 还差1件 · 每局1装不锁通关}. */
    public static String remainLine(EmberRunMaps.MapDef lm, PlayerData pd) {
        if (lm == null) return "本图身份未登记 · 每局仍必掉1装 · 不锁通关";
        if (lm.lootFamily == null || lm.lootFamily.isEmpty())
            return "本图无装备偏向 · 每局1装·差装可分解·不锁通关";
        int[] hn = haveNeed(lm, pd);
        int have = hn[0], need = hn[1];
        String label = EmberRunMaps.lootLabel(lm);
        int gap = Math.max(0, need - have);
        if (gap == 0)
            return "图身份 " + label + " · 图鉴 " + have + "/" + need + " · 齐 · 每局1装不锁通关";
        return "图身份 " + label + " · 图鉴 " + have + "/" + need + " · 还差" + gap + "件 · 每局1装不锁通关";
    }

    /** First missing codex piece for this map identity (blade then charm, low tier first). */
    public static String nextMissing(EmberRunMaps.MapDef lm, PlayerData pd) {
        if (lm == null || lm.lootFamily == null || lm.lootFamily.isEmpty()) return "";
        int tier = 1;
        String dl = lm.dropLabel == null ? "" : lm.dropLabel.trim().toUpperCase(java.util.Locale.ROOT);
        if (dl.startsWith("T") && dl.length() >= 2) {
            try { tier = Math.max(1, Math.min(3, Integer.parseInt(dl.substring(1, 2)))); }
            catch (NumberFormatException ignored) {}
        }
        String fam = lm.lootFamily;
        for (int t = 1; t <= tier; t++) {
            for (String slot : new String[] {"blade", "charm"}) {
                if (pd == null || !EmberCodex.has(pd, EmberCodex.key(fam, slot, t)))
                    return "下一缺 · T" + t + " " + EmberItemData.familyName(fam) + EmberItemData.slotName(slot);
            }
        }
        return "本图身份图鉴已齐";
    }
}

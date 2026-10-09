package town.sunshine.corerpg;

/**
 * D388: %corerpg_life_*% — read-only life level / daily·weekly quota display for TrMenu.
 * True source: {@link LifeService} + {@link PlayerData}. Does not change life.yml prices/curves/caps.
 *
 * Count convention (STATUS-pinned):
 * - soul daily cap = 4 (soul_dust + soul_dust_bone each daily:2); left = max(0, 4 - used)
 * - hatch weekly cap = 2 (pet_ashling + pet_cinder each weekly:1); left = max(0, 2 - used)
 */
final class CorePapiLife {

    static final int SOUL_DAILY_CAP = 4;
    static final int HATCH_WEEKLY_CAP = 2;

    private CorePapiLife() {}

    /**
     * Resolve a life_* key. Known keys never return null (menus need a string);
     * unknown keys return null so PAPI leaves the placeholder as-is.
     */
    static String resolve(LifeService life, PlayerData data, String key) {
        if (key == null) return null;
        if (!CorePapi.LIFE.contains(key)) return null;
        if (life == null || data == null) {
            return apply(key, 1, 0, 20, 0, SOUL_DAILY_CAP, 0, HATCH_WEEKLY_CAP);
        }
        int lv = life.lifeLevel(data);
        int xp = life.lifeXp(data);
        int next = life.lifeXpNext(data);
        String day = DailyService.today();
        String week = DailyService.weekId();
        int soulUsed = data.periodCount("life_soul_dust", day)
                + data.periodCount("life_soul_dust_bone", day);
        int hatchUsed = data.periodCount("life_pet_ashling", week)
                + data.periodCount("life_pet_cinder", week);
        return apply(key, lv, xp, next, soulUsed, SOUL_DAILY_CAP, hatchUsed, HATCH_WEEKLY_CAP);
    }

    /**
     * Bukkit-free apply for unit tests. Conventions from DESIGN §2.1.
     *
     * @param xpNext cumulative xp for next level; 0 means already max
     * @param soulUsed count(soul_dust)+count(soul_dust_bone) today
     * @param hatchUsed count(pet_ashling)+count(pet_cinder) this week
     */
    static String apply(String key, int level, int xp, int xpNext,
                        int soulUsed, int soulCap, int hatchUsed, int hatchCap) {
        if (key == null) return null;
        int lv = Math.max(1, level);
        int x = Math.max(0, xp);
        int next = Math.max(0, xpNext);
        int sCap = Math.max(0, soulCap);
        int hCap = Math.max(0, hatchCap);
        int sUsed = Math.max(0, soulUsed);
        int hUsed = Math.max(0, hatchUsed);
        int sLeft = Math.max(0, sCap - sUsed);
        int hLeft = Math.max(0, hCap - hUsed);
        if ("life_level".equals(key)) return String.valueOf(lv);
        if ("life_xp".equals(key)) return String.valueOf(x);
        if ("life_xp_next".equals(key)) return String.valueOf(next);
        if ("life_level_line".equals(key)) {
            if (next <= 0) return "生活 Lv." + lv + "（已满级）";
            return "生活 Lv." + lv + "（经验 " + x + "/" + next + "）";
        }
        if ("life_soul_daily_left".equals(key)) return String.valueOf(sLeft);
        if ("life_soul_daily_line".equals(key)) {
            return "今日兑尘 已用 " + Math.min(sUsed, sCap) + "/" + sCap + " · 剩 " + sLeft;
        }
        if ("life_hatch_weekly_left".equals(key)) return String.valueOf(hLeft);
        if ("life_hatch_weekly_line".equals(key)) {
            return "本周孵化 已用 " + Math.min(hUsed, hCap) + "/" + hCap + " · 剩 " + hLeft;
        }
        return null;
    }
}

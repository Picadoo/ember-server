package town.sunshine.corerpg.p1;

/**
 * D306 hub daily-routing honesty: primary / secondary lines from real keys.
 * Pure helper (no Bukkit) — menus show {@code %corerpg_p1_route_pri%} / {@code route_sec}.
 *
 * <p>Priority (design W1a+W1c): stamina&lt;dailyCost → 挂机/补给; afkFull → 去冒险(D285);
 * q07+featuredLeft&gt;0 → 本周精选; else 主线 next + remaining runs. Never recommend abyss/raid when locked.
 */
public final class EmberHubRoute {

    private EmberHubRoute() {}

    /**
     * @param nextStep      live {@code p1_next} text (may contain color codes)
     * @param stamina       current stamina
     * @param staminaMax    max stamina
     * @param dailyCost     mainline cost (30)
     * @param abyssCost     abyss cost (30)
     * @param raidCost      raid cost (50)
     * @param afkFull       {@code afk_full==1}
     * @param q07done       first-cleared Q07
     * @param featuredLeft  weekly featured bonus clears remaining
     * @param featuredShort short featured label (no "剩次" fluff)
     */
    public static String primary(String nextStep, int stamina, int staminaMax, int dailyCost,
                                 int abyssCost, int raidCost, boolean afkFull, boolean q07done,
                                 int featuredLeft, String featuredShort) {
        int cost = Math.max(1, dailyCost);
        if (stamina < cost) {
            return "优先：挂机/补给 · 体力不足开主线";
        }
        if (afkFull) {
            return "优先：去冒险（挂机今日已满）";
        }
        if (q07done && featuredLeft > 0) {
            String f = featuredShort == null || featuredShort.isEmpty() || "无".equals(featuredShort)
                    ? "本周精选" : featuredShort;
            return "优先：本周精选 · " + f + "（剩 " + featuredLeft + " 次）";
        }
        String next = nextStep == null || nextStep.isEmpty() ? "主线本" : strip(nextStep);
        int left = Math.max(0, stamina) / cost;
        return "优先：" + next + "（主线）· 体力 " + stamina + "/" + staminaMax + "（约还能打 " + left + " 局）";
    }

    /**
     * Optional half-line. Abyss/raid only when Q07 open and stamina pays the cost.
     * Never points at locked abyss/raid (pre-Q07).
     */
    public static String secondary(int stamina, int dailyCost, int abyssCost, int raidCost,
                                   boolean afkFull, boolean q07done, int featuredLeft) {
        if (!q07done) {
            return "次选：先首通 Q07 再开挑战/深渊/团本";
        }
        if (stamina < Math.max(1, dailyCost)) {
            return "次选：体力回满后再打精选或深渊";
        }
        if (afkFull) {
            if (featuredLeft > 0) return "次选：本周精选还剩 " + featuredLeft + " 次印记";
            if (stamina >= raidCost) return "次选：团本（" + raidCost + " 体力）或深渊（" + abyssCost + "）";
            if (stamina >= abyssCost) return "次选：深渊（" + abyssCost + " 体力）";
            return "次选：冒险页看精选与花样";
        }
        if (featuredLeft > 0) {
            if (stamina >= raidCost) return "次选：团本（" + raidCost + "）/ 深渊（" + abyssCost + "）体力够再点";
            if (stamina >= abyssCost) return "次选：深渊（" + abyssCost + " 体力）";
            return "次选：冒险页看花样委托";
        }
        if (stamina >= raidCost) return "次选：团本（" + raidCost + " 体力）或深渊（" + abyssCost + "）";
        if (stamina >= abyssCost) return "次选：深渊（" + abyssCost + " 体力）";
        return "次选：冒险页看精选与花样";
    }

    static String strip(String s) {
        if (s == null) return "";
        return s.replaceAll("§[0-9a-fk-or]", "").replaceAll("\u00a7[0-9a-fk-or]", "").trim();
    }
}

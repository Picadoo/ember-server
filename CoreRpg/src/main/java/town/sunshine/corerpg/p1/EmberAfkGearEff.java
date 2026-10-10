package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * D454: AFK gear-efficiency clarity — compare loadout ATK/HP to soft recommend
 * bands already shown on TrMenu layer cards. <b>Zero AFK table raise</b>.
 */
public final class EmberAfkGearEff {

    /** Soft recommend (ATK, HP) matching ember_p1_afk.yml §7推荐 lines for tiers 1–4. */
    public static final double[][] REC = {
            null,
            {18.0, 55.0},   // T1 灰坡 · 首通 Q01 T1 稳挂
            {30.0, 85.0},   // T2 荒原
            {50.0, 145.0},  // T3 焦土
            {70.0, 200.0},  // T4 烬原深处
    };

    private EmberAfkGearEff() {}

    /** @return 够稳 | 勉强 | 偏弱 | 空 */
    public static String band(double atk, double hp, int tier) {
        if (tier < 1 || tier > 4) return "空";
        double ra = REC[tier][0], rh = REC[tier][1];
        double raOk = atk / ra, rhOk = hp / rh;
        double m = Math.min(raOk, rhOk);
        if (m >= 0.95) return "够稳";
        if (m >= 0.70) return "勉强";
        return "偏弱";
    }

    public static String line(double atk, double hp, int tier) {
        if (tier < 1 || tier > 4) return "§7装效：§8先解锁层";
        double ra = REC[tier][0], rh = REC[tier][1];
        String b = band(atk, hp, tier);
        String color = "够稳".equals(b) ? "§a" : ("勉强".equals(b) ? "§e" : "§c");
        return String.format(Locale.ROOT,
                "§7装效：§f攻%.0f·生%.0f §7vs 推荐 §e攻%.0f·生%.0f %s%s",
                atk, hp, ra, rh, color, b);
    }

    public static String youLine(double atk, double hp) {
        return String.format(Locale.ROOT, "§7你的装：§f攻击 %.0f · 生命 %.0f", atk, hp);
    }
}

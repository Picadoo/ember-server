package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D696: abyss layer target chase (numeric floor goal).
 * Zero fee / HP / AFK change. Chat+ActionBar only.
 */
public final class EmberAbyssTarget {

    public static final String C_TARGET = "p1_abyss_layer_target";
    public static final int MAX = 50;

    private EmberAbyssTarget() {}

    public static int get(PlayerData d) {
        if (d == null) return 0;
        int v = d.periodCount(C_TARGET, "all");
        return v < 1 || v > MAX ? 0 : v;
    }

    public static boolean set(PlayerData d, int floor) {
        if (d == null) return false;
        if (floor != 0 && (floor < 1 || floor > MAX)) return false;
        int cur = d.periodCount(C_TARGET, "all");
        if (cur == floor) return false;
        d.addPeriodCount(C_TARGET, "all", floor - cur);
        return true;
    }

    public static String glance(PlayerData d, int best) {
        int t = get(d);
        if (t <= 0) return "§8深渊层目标：未设 · /corerpg p1 abysstarget <层>";
        if (best >= t) return "§a深渊层目标 §f" + t + " §a已达成（最高 " + best + "）";
        return "§e深渊层目标 §f" + t + " §7· 最高 §f" + best + " §7· 还差 §e" + (t - best);
    }

    public static void enterCue(Player p, PlayerData d, int enteringFloor, int best) {
        if (p == null) return;
        int t = get(d);
        if (t <= 0) return;
        String line;
        if (enteringFloor >= t) line = "§a层目标 · 本局 §f" + enteringFloor + " §a≥ 目标 " + t;
        else line = "§e层目标 §f" + t + " §7· 本局 §f" + enteringFloor + " §7· 最高 " + best;
        p.sendMessage(EmberRunService.P + line);
        try { p.sendActionBar(line); } catch (Throwable ignored) { }
    }

    public static void afterBest(Player p, PlayerData d, int newBest) {
        if (p == null || d == null) return;
        int t = get(d);
        if (t <= 0) return;
        if (newBest >= t) {
            p.sendMessage(EmberRunService.P + "§a深渊层目标达成：§f" + t + " §7· 可改 /corerpg p1 abysstarget");
            try { p.sendActionBar("§a层目标达成 · §f" + t); } catch (Throwable ignored) { }
        } else {
            p.sendMessage(EmberRunService.P + glance(d, newBest));
        }
    }
}

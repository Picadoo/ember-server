package town.sunshine.corerpg.p1;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;

/**
 * D696: abyss layer target chase (numeric floor goal).
 * Zero fee / HP / AFK change. Chat+ActionBar only.
 * TIP-DB D13333: afterBest / glance / cmd 主路径开薄页 /ember_p1_abyss_settle；禁教手打 abysstarget|forge 主 UI。
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
        if (t <= 0) return "§8深渊层目标：未设 · 点下一环设目标"; // TIP-DB D13333 禁教手打
        if (best >= t) return "§a深渊层目标 §f" + t + " §a已达成（最高 " + best + "）";
        return "§e深渊层目标 §f" + t + " §7· 最高 §f" + best + " §7· 还差 §e" + (t - best);
    }

    public static void enterCue(Player p, PlayerData d, int enteringFloor, int best) {
        if (p == null) return;
        int t = get(d);
        if (t <= 0) return;
        String line;
        if (enteringFloor == t) line = "§c冲目标层 · 本局 §f" + t; // D748
        else if (enteringFloor > t) line = "§a层目标 · 本局 §f" + enteringFloor + " §a≥ 目标 " + t;
        else line = "§e层目标 §f" + t + " §7· 本局 §f" + enteringFloor + " §7· 最高 " + best;
        p.sendMessage(EmberRunService.P + line);
        // D709: target above what this entry can reach (best+1 typically)
        int canOpen = best + 1;
        if (t > canOpen && enteringFloor < t) {
            p.sendMessage(EmberRunService.P + "§7目标高于当前可开（可开至 §f" + canOpen + "§7）· 先冲纪录再抬目标");
        }
        try { p.sendActionBar(line); } catch (Throwable ignored) { }
    }

    public static void afterBest(Player p, PlayerData d, int newBest) {
        if (p == null || d == null) return;
        int t = get(d);
        if (t <= 0) return;
        if (newBest >= t) {
            p.sendMessage(EmberRunService.P + "§a深渊层目标达成：§f" + t + " §7· 点下一环改目标"); // TIP-DB 禁教手打
            int next = Math.min(MAX, newBest + 1);
            p.sendMessage(EmberRunService.P + "§7下一目标可设第 §f" + next + " §7层 · 点下一环"); // D719 TIP-DB
            try { p.sendActionBar("§a层目标达成 · §f" + t); } catch (Throwable ignored) { }
            town.sunshine.corerpg.ConfirmTokens.sendButton(p, EmberRunService.P + "§7下一档：", // D742 TIP-DB 开薄页
                    EmberAbyssSettle.SETTLE_NEXT_LOOP_LABEL, EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD,
                    "薄页设目标第 " + next + " 层");
        } else {
            p.sendMessage(EmberRunService.P + glance(d, newBest));
        }
    }
    public static boolean cmd(CommandSender s, String[] args, EmberRunService runs) {
        String P = EmberRunService.P;
        if (!(s instanceof Player)) { s.sendMessage(P + "仅玩家可用"); return true; }
        Player p = (Player) s;
        if (runs == null) { p.sendMessage(P + "主线本服务未加载"); return true; }
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (d == null) { p.sendMessage(P + "数据未就绪"); return true; }
        int best = runs.abyssBest(d);
        if (args.length < 3) {
            p.sendMessage(P + glance(d, best));
            // TIP-DB D13333: 主路径开薄页；菜单 actions 仍可调本命令，禁教手打为主 UI
            p.sendMessage(P + "§7点下一环设层目标（菜单可点）");
            int t = get(d);
            if (t <= 0 && best >= 0) { // D745
                int n = Math.min(MAX, Math.max(1, best + 1));
                town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§7设目标：",
                        EmberAbyssSettle.SETTLE_NEXT_LOOP_LABEL, EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD,
                        "薄页设第 " + n + " 层");
            } else if (t > 0 && best >= t) {
                int n = Math.min(MAX, best + 1);
                town.sunshine.corerpg.ConfirmTokens.sendButton(p, P + "§7下一档：",
                        EmberAbyssSettle.SETTLE_NEXT_LOOP_LABEL, EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD,
                        "薄页抬到 " + n);
            }
            return true;
        }
        String raw = args[2];
        if ("clear".equalsIgnoreCase(raw) || "none".equalsIgnoreCase(raw) || "取消".equals(raw)) {
            set(d, 0);
            runs.plugin().getDataStore().flushMutation(p.getUniqueId());
            p.sendMessage(P + "已清空深渊层目标");
            return true;
        }
        int floor;
        if ("next".equalsIgnoreCase(raw) || "下一".equals(raw) || "+".equals(raw)) { // D742
            int cur = get(d);
            floor = cur > 0 && cur > best ? Math.min(MAX, cur + 1) : Math.min(MAX, Math.max(1, best + 1));
        } else {
            try { floor = Integer.parseInt(raw); } catch (NumberFormatException e) {
                p.sendMessage(P + "层数须为 1～" + MAX + " 或 next"); return true;
            }
        }
        if (floor < 1 || floor > MAX) {
            p.sendMessage(P + "层数须为 1～" + MAX); return true;
        }
        set(d, floor);
        runs.plugin().getDataStore().flushMutation(p.getUniqueId());
        p.sendMessage(P + glance(d, best));
        try { p.sendActionBar("§e层目标 · §f" + floor); } catch (Throwable ignored) { }
        // D873 TIP-DB D13333: 主路径开薄页；禁教手打 abysstarget 主 UI
        town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P + "§7冲目标：",
                new String[]{EmberAbyssSettle.SETTLE_NEXT_LOOP_LABEL, EmberAbyssSettle.SETTLE_NEXT_LOOP_CMD,
                        "再深渊/选层/目标", "GREEN"},
                new String[]{"[冒险页]", EmberAbyssSettle.SETTLE_ADVENTURE_CMD, "看深渊入口", "AQUA"},
                new String[]{"[深渊页]", EmberAbyssSettle.SETTLE_ABYSS_CMD, "回选层", "GRAY"});
        return true;
    }

}

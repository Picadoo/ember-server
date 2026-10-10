package town.sunshine.corerpg.p1;

import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D553: extra-chest open path — auto / ask / mute.
 * NEW dungeon acquisition: sticky open of Extra CHEST when it spawns in a P1 run.
 * Distinct from ExtraPath (roll bias), ClaimPath, Sight/Charm/Grip/Armor/Sip/Prep.
 * Chat only. Zero drop tables / AFK / sx / forge / combo sticky / enter-card / ActionBar.
 */
public final class EmberChestPath {

    public static final String C_PATH = "p1_chest_path";
    public static final String C_OFFER = "p1_chest_path_offer";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    private EmberChestPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == AUTO || id == ASK || id == MUTE;
    }

    public static boolean set(PlayerData d, int id) {
        if (d == null) return false;
        if (id != NONE && !valid(id)) return false;
        int cur = d.periodCount(C_PATH, "all");
        if (cur == id) return false;
        d.addPeriodCount(C_PATH, "all", id - cur);
        return true;
    }

    public static int parse(String raw) {
        if (raw == null) return -1;
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if ("clear".equals(s) || "none".equals(s) || "取消".equals(s)) return NONE;
        if ("auto".equals(s) || "自动".equals(s) || "1".equals(s)) return AUTO;
        if ("ask".equals(s) || "提醒".equals(s) || "手点".equals(s) || "2".equals(s)) return ASK;
        if ("mute".equals(s) || "quiet".equals(s) || "静默".equals(s) || "3".equals(s)) return MUTE;
        return -1;
    }

    public static String key(int id) {
        if (id == AUTO) return "auto";
        if (id == ASK) return "ask";
        if (id == MUTE) return "mute";
        return "none";
    }

    public static String label(int id) {
        if (id == AUTO) return "开箱·自动";
        if (id == ASK) return "开箱·提醒";
        if (id == MUTE) return "开箱·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "局内出现额外宝箱时自动开启记奖";
        if (id == ASK) return "出现额外宝箱时聊天提醒，点按钮开启（默认）";
        if (id == MUTE) return "不刷提醒；自己右键末影箱";
        return "点选额外宝箱开启节奏（改副本获取 · 不改掉落表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8开箱路径：首通 Q01 后可选";
        if (!valid(id)) return "§8开箱路径：未选 · /corerpg p1 chestpath";
        return "§e开箱路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    public static void maybeAfterSpawn(EmberRunSession s) {
        if (s == null || s.extra != EmberRunRules.Extra.CHEST || s.extraDone) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null) return;
        EmberRunService runs = pl.getEmberRuns();
        for (UUID u : s.committed) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline()) continue;
            PlayerData d = dataOf(p);
            if (d == null) continue;
            int path = get(d);
            if (shouldAuto(path)) {
                Bukkit.getScheduler().runTaskLater(pl, () -> {
                    if (!p.isOnline()) return;
                    if (runs.pathOpenExtraChest(p)) {
                        p.sendMessage(EmberRunService.P + "§a开箱·自动 §7已开启额外宝箱");
                    }
                }, 10L);
                continue;
            }
            if (shouldMute(path)) continue;
            ConfirmTokens.sendButtons(p, EmberRunService.P + "§e侧边出现了额外宝箱 ",
                    new String[]{"[开箱]", "/corerpg p1 chestopen", "开启并记为待结算奖励", "GREEN"},
                    new String[]{"[改路径]", "/corerpg p1 chestpath", "自动/提醒/静默", "GRAY"});
        }
    }

    public static void maybeProbe(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        CoreRpgPlugin pl = plugin();
        EmberRunService runs = pl == null ? null : pl.getEmberRuns();
        int path = get(d);
        if (shouldAuto(path)) {
            if (runs != null && runs.pathOpenExtraChest(p)) {
                p.sendMessage(EmberRunService.P + "§a开箱·自动 §7已开启额外宝箱");
            } else {
                p.sendMessage(EmberRunService.P + "§a开箱·自动 §7待命（局内出现额外宝箱时自动开）");
            }
            return;
        }
        if (shouldMute(path)) return;
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e侧边出现了额外宝箱 ",
                new String[]{"[开箱]", "/corerpg p1 chestopen", "开启并记为待结算奖励", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 chestpath", "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选额外宝箱开启节奏（改获取 · 可随时改 · 不改掉落表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 chestpath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 chestpath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 chestpath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 chestpath clear", "清空偏好", "DARK_GRAY"});
    }

    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        flush(p);
        offerPick(p);
        return true;
    }

    public static void scheduleOfferAfterQ01(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 825L);
    }

    public static void applyAndReply(Player p, int id) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null) {
            p.sendMessage(EmberRunService.P + "主线本服务未加载");
            return;
        }
        EmberRunService runs = pl.getEmberRuns();
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(EmberRunService.P + "数据未就绪"); return; }
        if (!runs.progressFlag(d, "q01")) {
            p.sendMessage(EmberRunService.P + "§c开箱路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空开箱路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a开箱 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO) maybeProbe(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[主线]", "/corerpg p1", "看进度", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 chestpath", "重选", "GRAY"});
    }

    private static PlayerData dataOf(Player p) {
        CoreRpgPlugin pl = plugin();
        return pl == null || p == null ? null : pl.getDataStore().get(p.getUniqueId());
    }

    private static void flush(Player p) {
        CoreRpgPlugin pl = plugin();
        if (pl != null && p != null) pl.getDataStore().flushMutation(p.getUniqueId());
    }

    private static CoreRpgPlugin plugin() {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }
}

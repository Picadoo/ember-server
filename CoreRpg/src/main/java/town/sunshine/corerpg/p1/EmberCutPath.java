package town.sunshine.corerpg.p1;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D580: regen-cut path — auto / ask / mute.
 * NEW combat acquisition: when regen affix opens interrupt window, sticky warp to that
 * elite to land interrupt damage for affixShard / faster clear, chat ask, or DPS yourself.
 * Distinct from Rush (timed room mob), Streak (chain), telegraph pulls. Chat only.
 * Zero regenEvery/interrupt_hp/heal table / AFK / sx / forge / combo / enter-cue.
 */
public final class EmberCutPath {

    public static final String C_PATH = "p1_cut_path";
    public static final String C_OFFER = "p1_cut_path_offer";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    private static final long ASK_COOLDOWN_MS = 8000L;
    private static final Map<UUID, Long> lastAsk = new ConcurrentHashMap<UUID, Long>();

    private EmberCutPath() {}

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
        if (id == AUTO) return "打断·自动";
        if (id == ASK) return "打断·提醒";
        if (id == MUTE) return "打断·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "再生读条时自动传至该精英猛打打断";
        if (id == ASK) return "再生读条时聊天提醒，点按钮贴脸（默认）";
        if (id == MUTE) return "不刷提醒；自己盯读条（可选）";
        return "点选打断节奏（改战斗获取 · 不改掉落表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8打断路径：首通 Q01 后可选";
        if (!valid(id)) return "§8打断路径：未选 · /corerpg p1 cutpath";
        return "§e打断路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    /** After regen interrupt window opens. */
    public static void maybeAfterCut(EmberRunSession s) {
        if (s == null || !s.open()) return;
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
                    if (runs.pathCutRegen(p)) {
                        p.sendMessage(EmberRunService.P + "§a打断·自动 §7已传至再生精英");
                    }
                }, 3L);
                continue;
            }
            if (shouldMute(path)) continue;
            long now = System.currentTimeMillis();
            Long last = lastAsk.get(p.getUniqueId());
            if (last != null && now - last < ASK_COOLDOWN_MS) continue;
            lastAsk.put(p.getUniqueId(), now);
            ConfirmTokens.sendButtons(p, EmberRunService.P + "§6再生 ",
                    new String[]{"[打断]", "/corerpg p1 cut", "传至再生精英猛打", "GREEN"},
                    new String[]{"[改路径]", "/corerpg p1 cutpath", "自动/提醒/静默", "GRAY"});
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
            if (runs != null && runs.pathCutRegen(p)) {
                p.sendMessage(EmberRunService.P + "§a打断·自动 §7已传至再生精英");
            } else {
                p.sendMessage(EmberRunService.P + "§a打断·自动 §7待命（再生读条时贴脸）");
            }
            return;
        }
        if (shouldMute(path)) return;
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§6再生 ",
                new String[]{"[打断]", "/corerpg p1 cut", "传至再生精英猛打", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 cutpath", "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选打断节奏（改获取 · 可随时改 · 不改掉落表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 cutpath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 cutpath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 cutpath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 cutpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 1230L);
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
            p.sendMessage(EmberRunService.P + "§c打断路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空打断路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a打断 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO) maybeProbe(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[主线]", "/corerpg p1", "看进度", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 cutpath", "重选", "GRAY"});
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

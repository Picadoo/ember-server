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
 * D565: escort-guard path — auto / ask / mute.
 * NEW dungeon acquisition: when variety escort rabbit spawns, sticky warp beside it to protect
 * for eventCore, chat ask, or find it yourself. Distinct from Smash (crystals), Hunt (extra mob),
 * Burst/Rally/Thaw/Duck/Link/Call. Chat only. Zero eventCore table / AFK / sx / forge / combo / enter-cue.
 * TIP-EY D13382: offerPick / applyAndReply / 无参 guardpath / 周要约 →开 /ember_p1_guard_path；
 * 清 ConfirmTokens 教 guardpath 主 UI；零改伤害/CD表/掉落/体力价/D565 护兔语义.
 */
public final class EmberGuardPath {

    public static final String C_PATH = "p1_guard_path";
    public static final String C_OFFER = "p1_guard_path_offer";

    /** TIP-EY D13382 · OpenCommand · 选打薄页 id 同名 */
    public static final String PATH_PICK_CMD = "/ember_p1_guard_path";
    public static final String PATH_PICK_LABEL = "[护兔路径]";
    public static final String PATH_ADVENTURE_CMD = "/ember_p1_adventure";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    private EmberGuardPath() {}

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
        if (id == AUTO) return "护兔·自动";
        if (id == ASK) return "护兔·提醒";
        if (id == MUTE) return "护兔·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "护宝兔出现时自动传到小兔旁守护";
        if (id == ASK) return "护宝兔出现时聊天提醒，点按钮前往（默认）";
        if (id == MUTE) return "不刷提醒；自己找发光小兔（可选）";
        return "点选护兔节奏（改副本获取 · 不改掉落表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8护兔路径：首通 Q01 后可选";
        // TIP-EY D13382: 禁教手打 /corerpg p1 guardpath
        if (!valid(id)) return "§8护兔路径：未选 · 点护兔路径页";
        return "§e护兔路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** TIP-EY D13382 · 薄页 lore 一行（无 §）：当前护兔路径。 */
    public static String menuLine(int id) {
        return "护兔路径 · " + label(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    /** After escort rabbit is spawned. */
    public static void maybeAfterEscort(EmberRunSession s) {
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
                    if (runs.pathGuardEscort(p)) {
                        p.sendMessage(EmberRunService.P + "§a护兔·自动 §7已传至护宝兔旁");
                    }
                }, 5L);
                continue;
            }
            if (shouldMute(path)) continue;
            ConfirmTokens.sendButtons(p, EmberRunService.P + "§b护宝兔 ",
                    new String[]{"[护兔]", "/corerpg p1 guard", "传到小兔旁守护", "GREEN"},
                    new String[]{"[改路径]", PATH_PICK_CMD, "自动/提醒/静默", "GRAY"});
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
            if (runs != null && runs.pathGuardEscort(p)) {
                p.sendMessage(EmberRunService.P + "§a护兔·自动 §7已传至护宝兔旁");
            } else {
                p.sendMessage(EmberRunService.P + "§a护兔·自动 §7待命（护宝兔出现时传送）");
            }
            return;
        }
        if (shouldMute(path)) return;
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§b护宝兔 ",
                new String[]{"[护兔]", "/corerpg p1 guard", "传到小兔旁守护", "GREEN"},
                new String[]{"[改路径]", PATH_PICK_CMD, "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选护兔节奏（改获取 · 可随时改 · 不改掉落表）：");
        // TIP-EY D13382: 开选打薄页；禁 ConfirmTokens 教 guardpath 为主 UI
        openPathPick(p);
    }

    /** Open thin page /ember_p1_guard_path (TrMenu bind); fallback chat button if command fails. */
    public static void openPathPick(Player p) {
        if (p == null) return;
        try {
            p.performCommand("ember_p1_guard_path");
        } catch (Throwable t) {
            ConfirmTokens.sendButtons(p, EmberRunService.P,
                    new String[]{PATH_PICK_LABEL, PATH_PICK_CMD, "自动/提醒/静默/清空/技能组/冒险", "GREEN"});
        }
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
        Bukkit.getScheduler().runTaskLater(pl, r, 1005L);
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
            p.sendMessage(EmberRunService.P + "§c护兔路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空护兔路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a护兔 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO) maybeProbe(p);
        // TIP-EY D13382: 下一步→选打薄页/冒险（禁教裸 guardpath 为主 UI）
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{PATH_PICK_LABEL, PATH_PICK_CMD, "自动/提醒/静默/清空/技能组/冒险", "GREEN"},
                new String[]{"[冒险]", PATH_ADVENTURE_CMD, "回冒险", "AQUA"});
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

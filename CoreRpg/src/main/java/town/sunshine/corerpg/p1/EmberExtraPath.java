package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D501: run-extra path — treasure / elite / chest.
 * NEW acquisition+play change: biases §9.3 Extra roll (宝藏怪 / 奖励精英 / 额外宝箱),
 * distinct from D500 spice (variety affix/event). Not a combo sticky-path.
 * Chat buttons only. Zero dmg/CD / AFK / sx / stamp. Not forge / enter-card / ActionBar twin.
 */
public final class EmberExtraPath {

    public static final String C_PATH = "p1_extra_path";
    public static final String C_OFFER = "p1_extra_path_offer";

    public static final int NONE = 0;
    /** 宝藏怪 */
    public static final int TREASURE = 1;
    /** 奖励精英 */
    public static final int ELITE = 2;
    /** 额外宝箱 */
    public static final int CHEST = 3;

    private EmberExtraPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == TREASURE || id == ELITE || id == CHEST;
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
        if ("clear".equals(s) || "none".equals(s) || "off".equals(s) || "取消".equals(s)) return NONE;
        if ("treasure".equals(s) || "宝藏".equals(s) || "宝藏怪".equals(s) || "1".equals(s)) return TREASURE;
        if ("elite".equals(s) || "精英".equals(s) || "奖励精英".equals(s) || "2".equals(s)) return ELITE;
        if ("chest".equals(s) || "宝箱".equals(s) || "额外宝箱".equals(s) || "3".equals(s)) return CHEST;
        return -1;
    }

    public static String key(int id) {
        if (id == TREASURE) return "treasure";
        if (id == ELITE) return "elite";
        if (id == CHEST) return "chest";
        return "none";
    }

    public static String label(int id) {
        if (id == TREASURE) return "猎宝加料";
        if (id == ELITE) return "精英加料";
        if (id == CHEST) return "宝箱加料";
        return "未选";
    }

    public static String tip(int id) {
        if (id == TREASURE) return "进本偏向刷出宝藏怪（打掉多掉）";
        if (id == ELITE) return "进本偏向刷出奖励精英（变招+奖励）";
        if (id == CHEST) return "进本偏向刷出额外宝箱";
        return "点选本周加料偏好（真改 §9.3 Extra 卷轴）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8加料路径：首通 Q01 后可选";
        if (!valid(id)) return "§8加料路径：未选 · /corerpg p1 extrapath";
        return "§e加料路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static EmberRunRules.Extra toExtra(int id) {
        if (id == TREASURE) return EmberRunRules.Extra.TREASURE;
        if (id == ELITE) return EmberRunRules.Extra.ELITE;
        if (id == CHEST) return EmberRunRules.Extra.CHEST;
        return EmberRunRules.Extra.NONE;
    }

    /**
     * Bias a rolled Extra toward the path pick. Bukkit-free.
     * When path set: always commit to preferred type (player opted into that extra).
     */
    public static EmberRunRules.Extra applyBias(EmberRunRules.Extra rolled, int path) {
        EmberRunRules.Extra want = toExtra(path);
        if (want == EmberRunRules.Extra.NONE) return rolled == null ? EmberRunRules.Extra.NONE : rolled;
        return want;
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选本周加料偏好（真改进本额外事件卷轴 · 可随时改 · 不改数值表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[猎宝加料]", "/corerpg p1 extrapath treasure", tip(TREASURE), "GOLD"},
                new String[]{"[精英加料]", "/corerpg p1 extrapath elite", tip(ELITE), "RED"},
                new String[]{"[宝箱加料]", "/corerpg p1 extrapath chest", tip(CHEST), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 extrapath clear", "清空偏好（回加权随机）", "GRAY"});
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

    /** After Q01 — extras appear on all main maps. Delay past family/flex. */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 140L);
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
            p.sendMessage(EmberRunService.P + "§c加料路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空加料路径（进本 Extra 回加权随机）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a加料 → §f" + label(id) + " §7· " + tip(id));
        p.sendMessage(P + "§8队长偏好 · 下局起你带队的主线本生效（admin 强制加料仍优先）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/corerpg p1 runs", "去打一局体感加料", "GREEN"},
                new String[]{"[花样路径]", "/corerpg p1 spicepath", "词缀/事件偏好（另一套）", "GOLD"},
                new String[]{"[换一条]", "/corerpg p1 extrapath", "重选", "GRAY"});
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

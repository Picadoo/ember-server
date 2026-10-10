package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D488: weekly featured-map play path — challenge vs normal.
 * Map identity (名片 hint + loot family) + combat rule choice; chat buttons only.
 * Zero featured marks / rotation rule / AFK / sx change. Not a forge spend-chase.
 */
public final class EmberFeaturedPath {

    public static final String C_PATH = "p1_featured_path";
    public static final String C_OFFER = "p1_featured_path_offer";

    public static final int NONE = 0;
    public static final int CHALLENGE = 1; // 挑战版 · 周规则 + T3 精选印记
    public static final int NORMAL = 2;    // 普通版 · 练图 / 阶印记（Q07 前）

    private EmberFeaturedPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == CHALLENGE || id == NORMAL;
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
        if ("challenge".equals(s) || "挑战".equals(s) || "c".equals(s) || "1".equals(s)) return CHALLENGE;
        if ("normal".equals(s) || "普通".equals(s) || "n".equals(s) || "2".equals(s)) return NORMAL;
        return -1;
    }

    public static String label(int id) {
        if (id == CHALLENGE) return "精选·挑战";
        if (id == NORMAL) return "精选·普通";
        return "未选";
    }

    public static String tip(int id) {
        if (id == CHALLENGE) return "打本周精选挑战 · 吃周规则与精选印记";
        if (id == NORMAL) return "打本周精选普通 · 练名片/掉落偏 · 控强度";
        return "点选本周精选打法";
    }

    public static String glance(int id, boolean unlocked, String featuredLabel) {
        if (!unlocked) return "§8精选路径：首通 Q01 后可选";
        String fl = featuredLabel == null || featuredLabel.isEmpty() ? "本周精选" : featuredLabel;
        if (!valid(id)) return "§8精选路径：未选 · /corerpg p1 featurepath §7· " + fl;
        return "§e精选路径：§f" + label(id) + " §8· " + tip(id) + " §7· " + fl;
    }

    /** Bukkit-free identity line: 名片 + loot. */
    public static String identityLine(String name, String hint, String lootLabel) {
        String n = name == null || name.isEmpty() ? "?" : name;
        String h = hint == null || hint.isEmpty() ? "（无名片）" : hint;
        String L = lootLabel == null || lootLabel.isEmpty() ? "不偏向" : lootLabel;
        return "§b" + n + " §7名片：§d" + h + " §8· 掉落§7" + L;
    }

    public static String enterCmd(int id, String mapKey) {
        if (mapKey == null || mapKey.isEmpty()) return "/corerpg p1 runs";
        if (id == CHALLENGE) return "/corerpg p1 enter " + mapKey + " challenge";
        if (id == NORMAL) return "/corerpg p1 enter " + mapKey;
        return "/corerpg p1 runs";
    }

    public static String enterLabel(int id, String mapKey) {
        String k = mapKey == null ? "?" : mapKey.toUpperCase(Locale.ROOT);
        if (id == CHALLENGE) return "[进 " + k + " 挑战]";
        if (id == NORMAL) return "[进 " + k + " 普通]";
        return "[打开冒险]";
    }

    public static void offerPick(Player p, String featuredLabel) {
        if (p == null) return;
        String P = EmberRunService.P;
        String fl = featuredLabel == null || featuredLabel.isEmpty() ? "本周精选" : featuredLabel;
        p.sendMessage(P + "§e选本周精选打法（" + fl + " · 可随时改 · 不改印记帽/周规则）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[精选·挑战]", "/corerpg p1 featurepath challenge", tip(CHALLENGE), "RED"},
                new String[]{"[精选·普通]", "/corerpg p1 featurepath normal", tip(NORMAL), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 featurepath clear", "清空路径", "GRAY"});
    }

    public static boolean maybeOfferWeekly(Player p, PlayerData d, String week) {
        if (p == null || d == null || week == null || week.isEmpty()) return false;
        if (valid(get(d))) return false;
        if (d.periodCount(C_OFFER, week) > 0) return false;
        d.addPeriodCount(C_OFFER, week, 1);
        flush(p);
        String fl = featuredLabelLive(p);
        offerPick(p, fl);
        return true;
    }

    /** After Q01 first clear — featured board is meaningful. */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 55L);
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
            p.sendMessage(EmberRunService.P + "§c精选路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空精选路径");
            return;
        }
        if (!valid(id)) {
            offerPick(p, runs.featuredLabel(d));
            return;
        }
        set(d, id);
        flush(p);
        String key = runs.featured(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
        EmberRunMaps.MapDef m = key == null ? null : runs.maps().byKey(key);
        String name = m == null ? "本周精选" : (key.toUpperCase(Locale.ROOT) + " " + m.name);
        String hint = m == null ? "" : m.hint;
        String loot = EmberRunMaps.lootLabel(m);
        p.sendMessage(P + "§a精选路径 → §f" + label(id) + " §7· " + tip(id));
        p.sendMessage(P + identityLine(name, hint, loot));
        if (id == CHALLENGE && !runs.progressFlag(d, "q07")) {
            p.sendMessage(P + "§c挑战版需本人首通 Q07；可先改 [精选·普通] 或通关 Q07。");
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[改普通]", "/corerpg p1 featurepath normal", tip(NORMAL), "AQUA"},
                    new String[]{"[换一条]", "/corerpg p1 featurepath", "重选", "GRAY"});
            return;
        }
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{enterLabel(id, key), enterCmd(id, key), "开本周精选", "GREEN"},
                new String[]{"[冒险页]", "/corerpg p1 runs", "看精选与周帽", "GOLD"},
                new String[]{"[换一条]", "/corerpg p1 featurepath", "重选", "GRAY"});
    }

    private static String featuredLabelLive(Player p) {
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null || p == null) return "本周精选";
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        return pl.getEmberRuns().featuredLabel(d);
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

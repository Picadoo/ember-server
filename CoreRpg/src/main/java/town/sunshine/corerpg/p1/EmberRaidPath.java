package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D483: weekly raid focus path — R01 / R02 / R03.
 * Real combat + loot-family choice (名片 + raid_item fallback family); chat buttons only.
 * Zero raid HP/dmg/cap / AFK / sx change.
 */
public final class EmberRaidPath {

    public static final String C_PATH = "p1_raid_path";
    public static final String C_OFFER = "p1_raid_path_offer";

    public static final int NONE = 0;
    public static final int R01 = 1; // 锈轨 · burst · 冲撞撞墙
    public static final int R02 = 2; // 霜封 · sustain · 半血砸地
    public static final int R03 = 3; // 断塔 · scorch · 烬核分摊

    private EmberRaidPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == R01 || id == R02 || id == R03;
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
        if ("r01".equals(s) || "1".equals(s) || "锈轨".equals(s) || "burst".equals(s)) return R01;
        if ("r02".equals(s) || "2".equals(s) || "霜封".equals(s) || "sustain".equals(s)) return R02;
        if ("r03".equals(s) || "3".equals(s) || "断塔".equals(s) || "scorch".equals(s)) return R03;
        return -1;
    }

    public static String mapKey(int id) {
        if (id == R01) return "r01";
        if (id == R02) return "r02";
        if (id == R03) return "r03";
        return null;
    }

    public static String label(int id) {
        if (id == R01) return "R01 锈轨矿道";
        if (id == R02) return "R02 霜封哨所";
        if (id == R03) return "R03 断塔回廊";
        return "未选";
    }

    public static String tip(int id) {
        if (id == R01) return "名片冲撞撞墙 · 掉落偏向烬爆";
        if (id == R02) return "名片半血砸地 · 掉落偏向炽愈";
        if (id == R03) return "名片烬核分摊 · 掉落偏向焚烬";
        return "点选本周团本焦点";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8团本路径：首通 Q07 后可选";
        if (!valid(id)) return "§8团本路径：未选 · /corerpg p1 raidpath";
        return "§e团本路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static String enterCmd(int id) {
        String k = mapKey(id);
        return k == null ? "/corerpg p1 runs" : "/corerpg p1 enter " + k;
    }

    public static String enterLabel(int id) {
        if (id == R01) return "[进 R01]";
        if (id == R02) return "[进 R02]";
        if (id == R03) return "[进 R03]";
        return "[打开冒险]";
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选本周团本焦点（名片+掉落族 · 可随时改 · 不改团本数值/周帽）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[R01 锈轨]", "/corerpg p1 raidpath r01", tip(R01), "GOLD"},
                new String[]{"[R02 霜封]", "/corerpg p1 raidpath r02", tip(R02), "AQUA"},
                new String[]{"[R03 断塔]", "/corerpg p1 raidpath r03", tip(R03), "RED"},
                new String[]{"[取消]", "/corerpg p1 raidpath clear", "清空焦点", "GRAY"});
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

    /** After Q07 first clear — raids unlock. */
    public static void scheduleOfferAfterQ07(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 60L);
    }

    public static void applyAndReply(Player p, int id) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null) {
            p.sendMessage(EmberRunService.P + "主线本服务未加载");
            return;
        }
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(EmberRunService.P + "数据未就绪"); return; }
        if (!pl.getEmberRuns().progressFlag(d, "q07")) {
            p.sendMessage(EmberRunService.P + "§c团本需本人首通 Q07 断塔回廊。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空团本路径");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a团本路径 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{enterLabel(id), enterCmd(id), "开团本（3～5 人 · 共享周帽）", "GREEN"},
                new String[]{"[招募]", "/corerpg p1 recruit " + mapKey(id), "发招募", "GOLD"},
                new String[]{"[换一条]", "/corerpg p1 raidpath", "重选", "GRAY"});
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

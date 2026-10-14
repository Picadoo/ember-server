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
 * Real combat + loot-family choice (名片 + raid_item fallback family).
 * TIP-CZ D13331: glance/offer/下一环菜单优先（/ember_p1_raid_settle）；禁教手打 raidpath 主 UI.
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
        if (!valid(id)) return "§8团本路径：未选 · 点下一环选路径";
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
        // TIP-CZ D13331: 开薄页点选；禁教手打 raidpath 主 UI
        ConfirmTokens.sendButtons(p, P + "§7团本路径：",
                new String[]{EmberRaidSettle.SETTLE_NEXT_LOOP_LABEL, EmberRaidSettle.SETTLE_NEXT_LOOP_CMD,
                        "薄页点选 R01/R02/R03", "AQUA"},
                new String[]{"[冒险页]", EmberRaidSettle.SETTLE_ADVENTURE_CMD, "换内容", "GREEN"});
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
        String weekLine = ""; // D827
        EmberRunMaps.MapDef m = pl.getEmberRuns().maps().byKey(mapKey(id));
        if (m != null && m.weeklyCap > 0) {
            int used = EmberRaidService.weekCount(d, m, EmberPlayfeelTelemetry.weekKey());
            weekLine = " §8· 本周 " + used + "/" + m.weeklyCap;
        }
        p.sendMessage(P + "§a团本路径 → §f" + label(id) + " §7· " + tip(id) + weekLine);
        try {
            p.sendActionBar("§e团本路径 · §f" + label(id) + weekLine);
        } catch (Throwable ignored) { }
        // TIP-CZ D13331: 主钮开薄页；可保留再进；禁教手打 raidpath|recruit 主 UI
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{EmberRaidSettle.SETTLE_NEXT_LOOP_LABEL, EmberRaidSettle.SETTLE_NEXT_LOOP_CMD,
                        "再进/路径/冒险/工坊", "GREEN"},
                new String[]{enterLabel(id), enterCmd(id), "开路径团本（3～5 人 · 共享周帽）", "AQUA"},
                new String[]{"[工坊]", EmberRaidSettle.SETTLE_FORGE_CMD, "花团本料", "GOLD"});
    }


    /** D752: enter/settle path clarity. */
    public static void enterCue(Player p, PlayerData d, String mapKey) {
        if (p == null || d == null || mapKey == null) return;
        int id = get(d);
        if (!valid(id)) {
            p.sendMessage(EmberRunService.P + "§7未选团本路径 · 点下一环选路径"); // TIP-CZ D13331 禁教手打
            offerPick(p); // D814 → 薄页
            return;
        }
        String mk = mapKey(id);
        boolean hit = mk != null && mk.equalsIgnoreCase(mapKey);
        String line = hit
                ? "§a团本路径 · 本局 §f" + label(id)
                : "§e团本路径 §f" + label(id) + " §7· 本局 §f" + mapKey.toUpperCase(Locale.ROOT);
        // D814 week remain glance
        String week = EmberPlayfeelTelemetry.weekKey();
        CoreRpgPlugin pl = plugin();
        if (pl != null && pl.getEmberRuns() != null) {
            EmberRunMaps.MapDef m = pl.getEmberRuns().maps().byKey(mapKey);
            if (m != null && m.weeklyCap > 0) {
                int used = EmberRaidService.weekCount(d, m, week);
                line += " §8· 周 " + used + "/" + m.weeklyCap;
            }
        }
        p.sendMessage(EmberRunService.P + line);
        try { p.sendActionBar(line); } catch (Throwable ignored) { }
    }

    public static void afterClearHit(Player p, PlayerData d, String mapKey) {
        if (p == null || d == null || mapKey == null) return;
        int id = get(d);
        if (!valid(id)) return;
        String mk = mapKey(id);
        if (mk == null || !mk.equalsIgnoreCase(mapKey)) return;
        p.sendMessage(EmberRunService.P + "§a团本路径达成：§f" + label(id));
        try { p.sendActionBar("§a团本路径 · 达成"); } catch (Throwable ignored) { }
        // D822 path-hit next (RaidLoop may also offer; rising path-only)
        String week = EmberPlayfeelTelemetry.weekKey();
        CoreRpgPlugin pl = plugin();
        if (pl != null && pl.getEmberRuns() != null) {
            EmberRunMaps.MapDef m = pl.getEmberRuns().maps().byKey(mapKey);
            if (m != null && m.weeklyCap > 0) {
                int used = EmberRaidService.weekCount(d, m, week);
                if (used < m.weeklyCap) {
                    // TIP-CZ D13331: 路径达成下一环开薄页；禁教手打 forge/setchase/raidpath 主 UI
                    ConfirmTokens.sendButtons(p, EmberRunService.P + "§7路径团本再开：", // D897
                            new String[]{EmberRaidSettle.SETTLE_NEXT_LOOP_LABEL, EmberRaidSettle.SETTLE_NEXT_LOOP_CMD,
                                    "再进/路径/冒险 · 剩 " + (m.weeklyCap - used) + " 次", "GREEN"},
                            new String[]{enterLabel(id), enterCmd(id), "再开路径团本", "AQUA"},
                            new String[]{"[工坊]", EmberRaidSettle.SETTLE_FORGE_CMD, "花团本料", "GOLD"});
                }
            }
        }
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

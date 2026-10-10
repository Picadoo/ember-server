package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.FlexSkillService;
import town.sunshine.corerpg.PlayerData;

/**
 * D494: flex-step combat path — equip / unequip 余烬踏步.
 * Real play change (sneak+Q casts 身法 only when equipped); chat buttons only.
 * Zero step distance / CD table / AFK / sx change. Not forge chase / enter-card / ActionBar twin.
 */
public final class EmberFlexPath {

    public static final String C_PATH = "p1_flex_path";
    public static final String C_OFFER = "p1_flex_path_offer";

    public static final int NONE = 0;
    public static final int ON = 1;  // equip PILOT
    public static final int OFF = 2; // unequip

    private EmberFlexPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == ON || id == OFF;
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
        if ("on".equals(s) || "equip".equals(s) || "装配".equals(s) || "开".equals(s)
                || "踏步".equals(s) || "1".equals(s)) return ON;
        if ("off".equals(s) || "unequip".equals(s) || "卸下".equals(s) || "关".equals(s)
                || "空手".equals(s) || "2".equals(s)) return OFF;
        return -1;
    }

    public static String label(int id) {
        if (id == ON) return "装配踏步";
        if (id == OFF) return "卸下轻技";
        return "未选";
    }

    public static String tip(int id) {
        if (id == ON) return "潜行+Q 放身法（前冲/后撤）";
        if (id == OFF) return "潜行+Q 不放身法 · 留给丢弃/背包手感";
        return "点选轻技开/关（真改潜行+Q）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8轻技路径：首通 Q01 后可选";
        if (!valid(id)) return "§8轻技路径：未选 · /corerpg p1 flexpath";
        return "§e轻技路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选轻技路径（真改潜行+Q 是否放身法 · 可随时改 · 不改距离/CD 表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[装配踏步]", "/corerpg p1 flexpath on", tip(ON), "GREEN"},
                new String[]{"[卸下轻技]", "/corerpg p1 flexpath off", tip(OFF), "YELLOW"},
                new String[]{"[取消]", "/corerpg p1 flexpath clear", "清空路径", "GRAY"});
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

    /** After Q01 — starter kit has flex; delay past family/featured. */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 105L);
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
        if (!pl.getEmberRuns().progressFlag(d, "q01")) {
            p.sendMessage(EmberRunService.P + "§c轻技路径需本人首通 Q01。");
            return;
        }
        if (EmberSkillKit.inDungeon(p, pl.getEmberRuns())) {
            p.sendMessage(EmberRunService.P + "§c副本里不能改轻技装配，请回城后再选。");
            return;
        }
        FlexSkillService flex = pl.getFlexSkillService();
        if (flex == null) {
            p.sendMessage(EmberRunService.P + "轻技服务未加载");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空轻技路径（当前装配未改）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        if (id == ON) flex.cmdEquip(p, FlexSkillService.PILOT_ID);
        else flex.cmdUnequip(p);
        p.sendMessage(P + "§a轻技路径 → §f" + label(id) + " §7· " + tip(id)
                + " §8（已" + (id == ON ? "装配" : "卸下") + "）");
        if (id == ON) {
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[身法方向]", "/corerpg p1 steppath", "前冲 / 后撤", "AQUA"},
                    new String[]{"[技能组]", "trmenu open ember_skill_kit", "看按键", "GOLD"},
                    new String[]{"[换一条]", "/corerpg p1 flexpath", "重选", "GRAY"});
        } else {
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[再装配]", "/corerpg p1 flexpath on", tip(ON), "GREEN"},
                    new String[]{"[技能组]", "trmenu open ember_skill_kit", "看按键", "GOLD"},
                    new String[]{"[换一条]", "/corerpg p1 flexpath", "重选", "GRAY"});
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

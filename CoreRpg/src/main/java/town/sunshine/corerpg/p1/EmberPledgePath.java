package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D481: self-pledge combat path — lean / reverse / both.
 * Real difficulty choice on repeat Q clears the player leads; chat buttons only (no ActionBar HUD).
 * Zero stamp / AFK / boss-table change — only which {@code p1_pledge_*} rules are on.
 */
public final class EmberPledgePath {

    public static final String C_PATH = "p1_pledge_path";
    public static final String C_OFFER = "p1_pledge_path_offer";

    public static final int NONE = 0;
    public static final int LEAN = 1;     // 限药
    public static final int REVERSE = 2;  // 逆行
    public static final int BOTH = 3;

    private EmberPledgePath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == LEAN || id == REVERSE || id == BOTH;
    }

    public static int parse(String raw) {
        if (raw == null) return -1;
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if ("clear".equals(s) || "none".equals(s) || "off".equals(s) || "取消".equals(s)) return NONE;
        if ("lean".equals(s) || "限药".equals(s) || "1".equals(s)) return LEAN;
        if ("reverse".equals(s) || "逆行".equals(s) || "2".equals(s)) return REVERSE;
        if ("both".equals(s) || "双挂".equals(s) || "两条".equals(s) || "3".equals(s)) return BOTH;
        return -1;
    }

    public static String label(int id) {
        if (id == LEAN) return "限药誓约";
        if (id == REVERSE) return "逆行誓约";
        if (id == BOTH) return "限药+逆行";
        return "未选";
    }

    public static String tip(int id) {
        if (id == LEAN) return "每人最多 3 瓶回复药 · 重打多一枚徽记";
        if (id == REVERSE) return "第一间↔第三间敌群互换 · 开局最重";
        if (id == BOTH) return "两条都挂 · 徽记×2 · 压迫最强";
        return "点选自选誓约练法";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8誓约路径：首通 Q06 后可选";
        if (!valid(id)) return "§8誓约路径：未选 · /corerpg p1 pledgepath";
        return "§e誓约路径：§f" + label(id) + " §8· " + tip(id);
    }

    /**
     * Set path counter + sync live pledge toggles (clear pool, then enable chosen).
     * Caller flushes.
     */
    public static boolean apply(PlayerData d, int id, List<String> poolIds) {
        if (d == null) return false;
        if (id != NONE && !valid(id)) return false;
        int cur = d.periodCount(C_PATH, "all");
        if (cur != id) d.addPeriodCount(C_PATH, "all", id - cur);
        if (poolIds == null) poolIds = new ArrayList<String>();
        EmberPledgeService.applyOff(d, poolIds);
        if (id == LEAN || id == BOTH) ensureOn(d, "lean");
        if (id == REVERSE || id == BOTH) ensureOn(d, "reverse");
        return true;
    }

    private static void ensureOn(PlayerData d, String ruleId) {
        if (!EmberPledgeService.isOn(d, ruleId)) EmberPledgeService.applyToggle(d, ruleId);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选自选誓约路径（重打已首通图时生效 · 可随时改 · 不改数值表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[限药]", "/corerpg p1 pledgepath lean", tip(LEAN), "YELLOW"},
                new String[]{"[逆行]", "/corerpg p1 pledgepath reverse", tip(REVERSE), "RED"},
                new String[]{"[双挂]", "/corerpg p1 pledgepath both", tip(BOTH), "LIGHT_PURPLE"},
                new String[]{"[清空]", "/corerpg p1 pledgepath clear", "取消全部誓约", "GRAY"});
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

    /** After Q06 first clear — pledge unlock moment. */
    public static void scheduleOfferAfterQ06(Player p) {
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
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(EmberRunService.P + "数据未就绪"); return; }
        if (!pl.getEmberRuns().progressFlag(d, EmberPledgeService.UNLOCK)) {
            p.sendMessage(EmberRunService.P + "§c自选誓约需本人首通 Q06 霜封哨所。");
            return;
        }
        List<String> pool = new ArrayList<String>();
        EmberRunMaps maps = pl.getEmberRuns().maps();
        if (maps != null) for (EmberRunMaps.Modifier m : maps.pledgePool()) pool.add(m.id);
        apply(d, id, pool);
        flush(p);
        String P = EmberRunService.P;
        if (id == NONE) {
            p.sendMessage(P + "已清空誓约路径（全部规则关掉）");
            return;
        }
        p.sendMessage(P + "§a誓约路径 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[誓约页]", "/corerpg p1 pledge", "查看已挂规则", "AQUA"},
                new String[]{"[进阶模式]", "/corerpg p1 modes", "残响/前哨/誓约入口", "GOLD"},
                new String[]{"[换一条]", "/corerpg p1 pledgepath", "重选", "GRAY"});
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

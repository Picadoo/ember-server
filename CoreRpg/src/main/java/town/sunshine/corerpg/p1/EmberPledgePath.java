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
 * TIP-DK D13342: offerPick / applyAndReply / 无参 pledgepath / 周要约 →开 /ember_p1_pledge_path；
 * 清 ConfirmTokens 教 pledgepath/pledge/modes 主 UI；零改伤害/CD表/掉落/体力价/誓约规则语义(D481).
 */
public final class EmberPledgePath {

    public static final String C_PATH = "p1_pledge_path";
    public static final String C_OFFER = "p1_pledge_path_offer";

    /** TIP-DK D13342 · OpenCommand · 选打薄页 id 同名 */
    public static final String PATH_PICK_CMD = "/ember_p1_pledge_path";
    public static final String PATH_PICK_LABEL = "[誓约路径]";
    public static final String PATH_PLEDGE_CMD = "/ember_p1_pledge";
    public static final String PATH_MODES_CMD = "/ember_p1_modes";
    public static final String PATH_ADVENTURE_CMD = "/ember_p1_adventure";

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
        if (!unlocked) return "§8誓约路径：首进/首通 Q06 后可选";
        // TIP-DK D13342: 禁教手打 /corerpg p1 pledgepath
        if (!valid(id)) return "§8誓约路径：未选 · 点誓约路径页";
        return "§e誓约路径：§f" + label(id) + " §8· " + tip(id);
    }

    /**
     * TIP-DK D13342 · 薄页 lore 一行（无 §）：当前誓约路径。
     */
    public static String menuLine(int id) {
        return "誓约路径 · " + label(id);
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
        // TIP-DK D13342: 开选打薄页；禁 ConfirmTokens 教 pledgepath 为主 UI
        openPathPick(p);
    }

    /** Open thin page /ember_p1_pledge_path (TrMenu bind); fallback chat button if command fails. */
    public static void openPathPick(Player p) {
        if (p == null) return;
        try {
            p.performCommand("ember_p1_pledge_path");
        } catch (Throwable t) {
            ConfirmTokens.sendButtons(p, EmberRunService.P,
                    new String[]{PATH_PICK_LABEL, PATH_PICK_CMD, "限药/逆行/双挂/清空/誓约页/进阶/冒险", "GREEN"});
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
        if (!EmberSkillKit.pledgeUnlocked(d, pl.getEmberRuns())) {
            p.sendMessage(EmberRunService.P + "§c自选誓约需本人首进或首通 Q06 霜封哨所。");
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
        // TIP-DK D13342: 下一步→选打薄页/誓约页/冒险（禁教裸 pledgepath/pledge/modes 为主 UI）
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{PATH_PICK_LABEL, PATH_PICK_CMD, "限药/逆行/双挂/重选", "GREEN"},
                new String[]{"[誓约页]", PATH_PLEDGE_CMD, "查看已挂规则", "AQUA"},
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

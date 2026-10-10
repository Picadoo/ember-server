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
 * D496: combat trial path — cautious / aggressive / pressure.
 * Real play change: writes self-select pledge rules + counter practice sticky together.
 * Chat buttons only. Zero dmg/CD tables / AFK / sx. Not forge chase / enter-card / ActionBar twin.
 */
public final class EmberTrialPath {

    public static final String C_PATH = "p1_trial_path";
    public static final String C_OFFER = "p1_trial_path_offer";

    public static final int NONE = 0;
    /** 限药 + 落空破绽 */
    public static final int CAUTIOUS = 1;
    /** 逆行 + 撞墙破绽 */
    public static final int AGGRESSIVE = 2;
    /** 双挂誓约 + 破招通道 */
    public static final int PRESSURE = 3;

    private EmberTrialPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == CAUTIOUS || id == AGGRESSIVE || id == PRESSURE;
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
        if ("cautious".equals(s) || "稳慎".equals(s) || "careful".equals(s) || "1".equals(s)) return CAUTIOUS;
        if ("aggressive".equals(s) || "猛开".equals(s) || "aggro".equals(s) || "2".equals(s)) return AGGRESSIVE;
        if ("pressure".equals(s) || "加压".equals(s) || "press".equals(s) || "3".equals(s)) return PRESSURE;
        return -1;
    }

    public static String label(int id) {
        if (id == CAUTIOUS) return "稳慎试炼";
        if (id == AGGRESSIVE) return "猛开试炼";
        if (id == PRESSURE) return "加压试炼";
        return "未选";
    }

    public static String tip(int id) {
        if (id == CAUTIOUS) return "限药 + 落空破绽 · 省药站开练 Q01–Q05";
        if (id == AGGRESSIVE) return "逆行 + 撞墙破绽 · 开局最重练 Q02/Q07 撞墙";
        if (id == PRESSURE) return "限药+逆行 + 破招 · 双徽记加压练 Q06/Q07";
        return "点选试炼（一次写入誓约+破绽练法）";
    }

    public static int pledgeId(int id) {
        if (id == CAUTIOUS) return EmberPledgePath.LEAN;
        if (id == AGGRESSIVE) return EmberPledgePath.REVERSE;
        if (id == PRESSURE) return EmberPledgePath.BOTH;
        return EmberPledgePath.NONE;
    }

    public static int counterId(int id) {
        if (id == CAUTIOUS) return EmberCounterPath.WHIFF;
        if (id == AGGRESSIVE) return EmberCounterPath.WALL;
        if (id == PRESSURE) return EmberCounterPath.BREAK;
        return EmberCounterPath.NONE;
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8试炼路径：首通 Q06 后可选（需自选誓约）";
        if (!valid(id)) return "§8试炼路径：未选 · /corerpg p1 trialpath";
        return "§e试炼路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static String enterCmd(int id) {
        return EmberCounterPath.enterCmd(counterId(id));
    }

    public static String enterLabel(int id) {
        return EmberCounterPath.enterLabel(counterId(id));
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选试炼玩法（一次写入自选誓约+破绽练法 · 回城可换 · 不改伤害/CD 表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[稳慎试炼]", "/corerpg p1 trialpath cautious", tip(CAUTIOUS), "YELLOW"},
                new String[]{"[猛开试炼]", "/corerpg p1 trialpath aggressive", tip(AGGRESSIVE), "RED"},
                new String[]{"[加压试炼]", "/corerpg p1 trialpath pressure", tip(PRESSURE), "LIGHT_PURPLE"},
                new String[]{"[取消]", "/corerpg p1 trialpath clear", "清空试炼（清誓约+破绽粘性）", "GRAY"});
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

    /** After Q06 — pledge unlock. Delay past pledgepath. */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 110L);
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
            p.sendMessage(EmberRunService.P + "§c试炼需本人首通 Q06 霜封哨所（自选誓约解锁）。");
            return;
        }
        String P = EmberRunService.P;
        List<String> pool = new ArrayList<String>();
        EmberRunMaps maps = pl.getEmberRuns().maps();
        if (maps != null) for (EmberRunMaps.Modifier m : maps.pledgePool()) pool.add(m.id);

        if (id == NONE) {
            set(d, NONE);
            EmberPledgePath.apply(d, EmberPledgePath.NONE, pool);
            EmberCounterPath.set(d, EmberCounterPath.NONE);
            flush(p);
            p.sendMessage(P + "已清空试炼路径（誓约规则与破绽粘性已清）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        EmberPledgePath.apply(d, pledgeId(id), pool);
        EmberCounterPath.set(d, counterId(id));
        flush(p);
        p.sendMessage(P + "§a试炼 → §f" + label(id) + " §7· " + tip(id)
                + " §8（已写入 " + EmberPledgePath.label(pledgeId(id))
                + " + " + EmberCounterPath.label(counterId(id)) + "）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{enterLabel(id), enterCmd(id), "进图练这套试炼", "GREEN"},
                new String[]{"[誓约路径]", "/corerpg p1 pledgepath", "单改誓约", "YELLOW"},
                new String[]{"[破绽路径]", "/corerpg p1 counterpath", "单改破绽", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 trialpath", "重选", "GRAY"});
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

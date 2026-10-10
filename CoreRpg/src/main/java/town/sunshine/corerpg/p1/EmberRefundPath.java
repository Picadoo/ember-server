package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D505: death-potion refund path — full / light / bare.
 * NEW acquisition: clamps first-death heal-potion refund of the stamina day (D32).
 * Chat only. Zero death_refund.yml rewrite (personal clamp). Not prep auto-buy twin,
 * not combo sticky, not roll bias, not forge/enter/ActionBar cue. No AFK/sx20/K3.
 */
public final class EmberRefundPath {

    public static final String C_PATH = "p1_refund_path";
    public static final String C_OFFER = "p1_refund_path_offer";

    public static final int NONE = 0;
    /** Use table death_refund.max_potions */
    public static final int FULL = 1;
    /** Cap refund at 2 bottles */
    public static final int LIGHT = 2;
    /** No potion refund (still consumes the once-per-day latch) */
    public static final int BARE = 3;

    public static final int LIGHT_CAP = 2;

    private EmberRefundPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == FULL || id == LIGHT || id == BARE;
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
        if ("full".equals(s) || "满退".equals(s) || "1".equals(s)) return FULL;
        if ("light".equals(s) || "轻退".equals(s) || "2".equals(s)) return LIGHT;
        if ("bare".equals(s) || "off".equals(s) || "不退".equals(s) || "3".equals(s)) return BARE;
        return -1;
    }

    public static String key(int id) {
        if (id == FULL) return "full";
        if (id == LIGHT) return "light";
        if (id == BARE) return "bare";
        return "none";
    }

    public static String label(int id) {
        if (id == FULL) return "倒退·满退";
        if (id == LIGHT) return "倒退·轻退";
        if (id == BARE) return "倒退·不退";
        return "未选";
    }

    public static String tip(int id) {
        if (id == FULL) return "今日首次倒下：按表退还本局用掉的回复药";
        if (id == LIGHT) return "今日首次倒下：最多退 " + LIGHT_CAP + " 瓶回复药";
        if (id == BARE) return "今日首次倒下：不退回复药（仍占今日一次）";
        return "点选倒下退药偏好（改获取 · 不改价表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8倒退路径：首通 Q01 后可选";
        if (!valid(id)) return "§8倒退路径：未选 · /corerpg p1 refundpath";
        return "§e倒退路径：§f" + label(id) + " §8· " + tip(id);
    }

    /**
     * Effective max potions for death refund. NONE/FULL → tableMax; LIGHT → min(2, tableMax); BARE → 0.
     * Never raises above the table.
     */
    public static int effectiveMax(int path, int tableMax) {
        int table = Math.max(0, tableMax);
        if (path == BARE) return 0;
        if (path == LIGHT) return Math.min(LIGHT_CAP, table);
        return table; // FULL or NONE
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选倒下退药（今日首次倒下退本局用药 · 可随时改 · 不改价表/不抬上限）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[满退]", "/corerpg p1 refundpath full", tip(FULL), "GREEN"},
                new String[]{"[轻退]", "/corerpg p1 refundpath light", tip(LIGHT), "GOLD"},
                new String[]{"[不退]", "/corerpg p1 refundpath bare", tip(BARE), "GRAY"},
                new String[]{"[取消]", "/corerpg p1 refundpath clear", "清空偏好", "DARK_GRAY"});
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

    /** After Q01 — delay past twist (215L). */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 240L);
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
            p.sendMessage(EmberRunService.P + "§c倒退路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空倒退路径（按表满退）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        int table = EmberRunService.deathRefundMax();
        int eff = effectiveMax(id, table);
        p.sendMessage(P + "§a倒退 → §f" + label(id) + " §7· " + tip(id)
                + " §8（表上限 " + table + " · 你的上限 " + eff + "）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[打开冒险]", "/corerpg p1 runs", "进本倒下时按偏好退药", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 refundpath", "重选", "GRAY"});
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

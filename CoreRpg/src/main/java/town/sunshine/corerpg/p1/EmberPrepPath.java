package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D502: potion prep path — light / full / bare.
 * NEW acquisition+play: auto top-up heal potions before run start (town shop C14).
 * Not a combo sticky-path, not variety/Extra roll bias, not forge spend-chase twin.
 * Chat buttons only. Zero dmg/CD / AFK / sx. Not enter-card / ActionBar twin.
 */
public final class EmberPrepPath {

    public static final String C_PATH = "p1_prep_path";
    public static final String C_OFFER = "p1_prep_path_offer";

    public static final int NONE = 0;
    /** Auto top-up to 3 bottles */
    public static final int LIGHT = 1;
    /** Auto top-up to 5 bottles */
    public static final int FULL = 2;
    /** Never auto-buy */
    public static final int BARE = 3;

    public static final int TARGET_LIGHT = 3;
    public static final int TARGET_FULL = 5;

    private EmberPrepPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == LIGHT || id == FULL || id == BARE;
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
        if ("light".equals(s) || "轻备".equals(s) || "3".equals(s) || "1".equals(s)) return LIGHT;
        if ("full".equals(s) || "满备".equals(s) || "5".equals(s) || "2".equals(s)) return FULL;
        if ("bare".equals(s) || "off".equals(s) || "不备".equals(s) || "空手".equals(s)) return BARE;
        return -1;
    }

    public static int targetBottles(int id) {
        if (id == LIGHT) return TARGET_LIGHT;
        if (id == FULL) return TARGET_FULL;
        return 0;
    }

    public static String label(int id) {
        if (id == LIGHT) return "轻备药";
        if (id == FULL) return "满备药";
        if (id == BARE) return "不自动备";
        return "未选";
    }

    public static String tip(int id) {
        if (id == LIGHT) return "进本前自动补到 3 瓶回复药（花余烬币）";
        if (id == FULL) return "进本前自动补到 5 瓶回复药（花余烬币）";
        if (id == BARE) return "进本前不自动买药 · 自己补";
        return "点选备药偏好（进本前城内自动补货）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8备药路径：首通 Q01 后可选";
        if (!valid(id)) return "§8备药路径：未选 · /corerpg p1 preppath";
        return "§e备药路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Called from SessionService before stamina reserve (still in town). */
    public static void maybeTopUpBeforeRun(Player p, EmberRunService runs) {
        if (p == null || runs == null) return;
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (d == null) return;
        int id = get(d);
        int want = targetBottles(id);
        if (want <= 0) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return;
        EmberSupplyService supply = pl.getEmberSupplies();
        if (supply == null) return;
        supply.topUpTo(p, want, "prep");
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选备药偏好（进本前城内自动补回复药 · 可随时改 · 花余烬币不改价表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[轻备·3瓶]", "/corerpg p1 preppath light", tip(LIGHT), "GREEN"},
                new String[]{"[满备·5瓶]", "/corerpg p1 preppath full", tip(FULL), "GOLD"},
                new String[]{"[不自动备]", "/corerpg p1 preppath bare", tip(BARE), "GRAY"},
                new String[]{"[取消]", "/corerpg p1 preppath clear", "清空偏好", "DARK_GRAY"});
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

    /** After Q01 — delay past extrapath (140L). */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 165L);
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
            p.sendMessage(EmberRunService.P + "§c备药路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空备药路径（进本前不再自动补货）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a备药 → §f" + label(id) + " §7· " + tip(id));
        EmberSupplyService supply = pl.getEmberSupplies();
        int have = supply == null ? -1 : supply.countHealPotions(p);
        if (have >= 0) p.sendMessage(P + "§7当前回复药 §f" + have + " §7瓶 · 每瓶 " + EmberSupplyService.price() + " 余烬币");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[补给店]", "/corerpg p1 shop", "手买也行", "AQUA"},
                new String[]{"[打开冒险]", "/corerpg p1 runs", "进本会自动按偏好补", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 preppath", "重选", "GRAY"});
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

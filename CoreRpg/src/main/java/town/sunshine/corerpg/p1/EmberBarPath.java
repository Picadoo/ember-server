package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D512: heal-potion hotbar fill path — right / left / key5.
 * NEW play: chooses which hotbar slots potions land in (number-key muscle memory).
 * Distinct from D502 prep (how many to buy). Chat only. Zero shop price / AFK / sx.
 * Not combo sticky, not clamp twin, not forge/enter/ActionBar cue.
 */
public final class EmberBarPath {

    public static final String C_PATH = "p1_bar_path";
    public static final String C_OFFER = "p1_bar_path_offer";

    public static final int NONE = 0;
    /** Fill from slot 8 → 1 (stock) */
    public static final int RIGHT = 1;
    /** Fill from slot 1 → 8 */
    public static final int LEFT = 2;
    /** Prefer slot 4 (key 5), then outward */
    public static final int KEY5 = 3;

    private EmberBarPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == RIGHT || id == LEFT || id == KEY5;
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
        if ("right".equals(s) || "右栏".equals(s) || "1".equals(s)) return RIGHT;
        if ("left".equals(s) || "左栏".equals(s) || "2".equals(s)) return LEFT;
        if ("key5".equals(s) || "5".equals(s) || "五键".equals(s) || "中栏".equals(s) || "3".equals(s)) return KEY5;
        return -1;
    }

    public static String key(int id) {
        if (id == RIGHT) return "right";
        if (id == LEFT) return "left";
        if (id == KEY5) return "key5";
        return "none";
    }

    public static String label(int id) {
        if (id == RIGHT) return "药栏·右栏";
        if (id == LEFT) return "药栏·左栏";
        if (id == KEY5) return "药栏·五键";
        return "未选";
    }

    public static String tip(int id) {
        if (id == RIGHT) return "回复药优先放快捷栏右侧（数字 9→2）";
        if (id == LEFT) return "回复药优先放快捷栏左侧（数字 2→9）";
        if (id == KEY5) return "回复药优先放第 5 格（数字 5），再向两侧铺";
        return "点选回复药快捷栏落点（改手感 · 不改药价）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8药栏路径：首通 Q01 后可选";
        if (!valid(id)) return "§8药栏路径：未选 · /corerpg p1 barpath";
        return "§e药栏路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Hotbar slot scan order (indices 1–8; slot 0 reserved for blade). Bukkit-free. */
    public static int[] slotOrder(int path) {
        if (path == LEFT) return new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        if (path == KEY5) return new int[]{4, 5, 3, 6, 2, 7, 1, 8};
        return new int[]{8, 7, 6, 5, 4, 3, 2, 1}; // RIGHT / NONE
    }

    /** First empty hotbar index for path, or -1. */
    public static int emptyHotbar(Player p, int path) {
        if (p == null) return -1;
        for (int i : slotOrder(path)) {
            ItemStack s = p.getInventory().getItem(i);
            if (s == null || s.getType() == org.bukkit.Material.AIR) return i;
        }
        return -1;
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选回复药快捷栏落点（改按键手感 · 可随时改 · 不改药价）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[右栏]", "/corerpg p1 barpath right", tip(RIGHT), "GREEN"},
                new String[]{"[左栏]", "/corerpg p1 barpath left", tip(LEFT), "GOLD"},
                new String[]{"[五键]", "/corerpg p1 barpath key5", tip(KEY5), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 barpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 255L); // after prep ~165 / refund ~240
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
            p.sendMessage(EmberRunService.P + "§c药栏路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空药栏路径（按默认右栏落药）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a药栏 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[补给店]", "/corerpg p1 shop", "新买的药用新落点", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 barpath", "重选", "GRAY"});
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

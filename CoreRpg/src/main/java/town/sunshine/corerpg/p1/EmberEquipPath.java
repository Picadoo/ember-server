package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D517: better-piece equip path — keep / quick / bold.
 * NEW play: sticky policy for auto-equip vs ask when a strictly better drop arrives.
 * KEEP never auto; QUICK honors stock UP_AUTO; BOLD also auto on set-break UP_ASK (not enhance-swap).
 * Distinct from ClaimPath / forge spend. Chat only. Zero power table / AFK / sx.
 * Not combo sticky, not enter/card/ActionBar cue.
 */
public final class EmberEquipPath {

    public static final String C_PATH = "p1_equip_path";
    public static final String C_OFFER = "p1_equip_path_offer";

    public static final int NONE = 0;
    /** Never auto-equip — UP_AUTO becomes ask */
    public static final int KEEP = 1;
    /** Stock: auto when safe (UP_AUTO) */
    public static final int QUICK = 2;
    /** Also auto when better piece would break active set (UP_ASK → UP_AUTO); never auto UP_ASK_SWAP */
    public static final int BOLD = 3;

    private EmberEquipPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == KEEP || id == QUICK || id == BOLD;
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
        if ("keep".equals(s) || "稳妥".equals(s) || "总问".equals(s) || "1".equals(s)) return KEEP;
        if ("quick".equals(s) || "auto".equals(s) || "默认".equals(s) || "快换".equals(s) || "2".equals(s)) return QUICK;
        if ("bold".equals(s) || "大胆".equals(s) || "破套".equals(s) || "3".equals(s)) return BOLD;
        return -1;
    }

    public static String key(int id) {
        if (id == KEEP) return "keep";
        if (id == QUICK) return "quick";
        if (id == BOLD) return "bold";
        return "none";
    }

    public static String label(int id) {
        if (id == KEEP) return "换装·稳妥";
        if (id == QUICK) return "换装·快换";
        if (id == BOLD) return "换装·大胆";
        return "未选";
    }

    public static String tip(int id) {
        if (id == KEEP) return "更好的装备总先问你，绝不自动换上";
        if (id == QUICK) return "安全时自动换上（不破套、无强化投资）";
        if (id == BOLD) return "更强就自动换上，即使会暂时拆掉套装（强化互换仍要问）";
        return "点选掉落换装偏好（改手感 · 不改数值）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8换装路径：首通 Q01 后可选";
        if (!valid(id)) return "§8换装路径：未选 · /corerpg p1 equippath";
        return "§e换装路径：§f" + label(id) + " §8· " + tip(id);
    }

    /**
     * Adjust stock upgrade verdict for path. Bukkit-free.
     * KEEP: UP_AUTO → UP_ASK. BOLD: UP_ASK → UP_AUTO. UP_ASK_SWAP never auto.
     */
    public static int adjust(int verdict, int path) {
        if (verdict == EmberRunRules.UP_NONE || verdict == EmberRunRules.UP_ASK_SWAP) return verdict;
        if (path == KEEP && verdict == EmberRunRules.UP_AUTO) return EmberRunRules.UP_ASK;
        if (path == BOLD && verdict == EmberRunRules.UP_ASK) return EmberRunRules.UP_AUTO;
        return verdict; // QUICK / NONE / already matching
    }

    public static int adjust(int verdict, PlayerData d) {
        return adjust(verdict, get(d));
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选掉落换装偏好（改手感 · 可随时改 · 不改数值）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[稳妥]", "/corerpg p1 equippath keep", tip(KEEP), "GREEN"},
                new String[]{"[快换]", "/corerpg p1 equippath quick", tip(QUICK), "GOLD"},
                new String[]{"[大胆]", "/corerpg p1 equippath bold", tip(BOLD), "RED"},
                new String[]{"[取消]", "/corerpg p1 equippath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 315L); // after friend ~300
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
            p.sendMessage(EmberRunService.P + "§c换装路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空换装路径（按默认快换）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a换装 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[装备页]", "/ember_p1_equip", "看当前穿戴", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 equippath", "重选", "GRAY"});
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

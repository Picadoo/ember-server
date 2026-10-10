package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.StaminaService;

/**
 * D541: weekly free-entry credit path — spend / hold / ask.
 * NEW economy: sticky policy whether 周本/精英/团 本周免费抵扣 is spent first or held while paying stamina.
 * Distinct from DosePath/BankPath/TicketPath (potions/bank/old tickets). Chat only.
 * Zero stamina cost tables / AFK / sx / forge / combo sticky / enter-card / ActionBar cue.
 */
public final class EmberCreditPath {

    public static final String C_PATH = "p1_credit_path";
    public static final String C_OFFER = "p1_credit_path_offer";

    public static final int NONE = 0;
    /** Use weekly grant credit before paying stamina (stock) */
    public static final int SPEND = 1;
    /** Pay stamina when possible; keep free credits for later */
    public static final int HOLD = 2;
    /** Same spend as stock, but join tip when credits remain */
    public static final int ASK = 3;

    private EmberCreditPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == SPEND || id == HOLD || id == ASK;
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
        if ("spend".equals(s) || "auto".equals(s) || "消耗".equals(s) || "优先".equals(s) || "1".equals(s)) return SPEND;
        if ("hold".equals(s) || "keep".equals(s) || "保留".equals(s) || "攒着".equals(s) || "2".equals(s)) return HOLD;
        if ("ask".equals(s) || "提醒".equals(s) || "手点".equals(s) || "3".equals(s)) return ASK;
        return -1;
    }

    public static String key(int id) {
        if (id == SPEND) return "spend";
        if (id == HOLD) return "hold";
        if (id == ASK) return "ask";
        return "none";
    }

    public static String label(int id) {
        if (id == SPEND) return "周免·消耗";
        if (id == HOLD) return "周免·保留";
        if (id == ASK) return "周免·提醒";
        return "未选";
    }

    public static String tip(int id) {
        if (id == SPEND) return "进周本/精英/团时优先花掉本周免费抵扣（默认）";
        if (id == HOLD) return "有体力时先扣体力，免费抵扣留到缺体再花";
        if (id == ASK) return "优先消耗抵扣，进服提醒剩余免费次数";
        return "点选本周免费抵扣节奏（改周本经济 · 不改次数表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8周免路径：首通 Q01 后可选";
        if (!valid(id)) return "§8周免路径：未选 · /corerpg p1 creditpath";
        return "§e周免路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** When false, consumeForEnter skips credit and pays stamina (if HOLD). NONE/SPEND/ASK → use credit. */
    public static boolean shouldUseCredit(int path) {
        return path != HOLD;
    }

    public static int totalCredits(PlayerData d) {
        if (d == null) return 0;
        return Math.max(0, d.getWeeklyGrantCreditWeekly())
                + Math.max(0, d.getWeeklyGrantCreditElite())
                + Math.max(0, d.getWeeklyGrantCreditRaid());
    }

    public static void maybeAfterProgress(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        StaminaService st = stamina();
        if (st != null) st.ensure(d);
        int n = totalCredits(d);
        if (n <= 0) return;
        int path = get(d);
        if (path != ASK && path != NONE) return; // only ASK (and unset→treat as quiet) tips; SPEND/HOLD silent on join
        if (path == NONE) return; // unset: no tip spam
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e本周免费抵扣还剩 §f" + n + " §e次 ",
                new String[]{"[消耗优先]", "/corerpg p1 creditpath spend", tip(SPEND), "GREEN"},
                new String[]{"[保留]", "/corerpg p1 creditpath hold", tip(HOLD), "GOLD"},
                new String[]{"[改路径]", "/corerpg p1 creditpath", "重选", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选本周免费抵扣节奏（改经济 · 可随时改 · 不改次数）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[消耗]", "/corerpg p1 creditpath spend", tip(SPEND), "GREEN"},
                new String[]{"[保留]", "/corerpg p1 creditpath hold", tip(HOLD), "GOLD"},
                new String[]{"[提醒]", "/corerpg p1 creditpath ask", tip(ASK), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 creditpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 645L); // after bank ~630
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
            p.sendMessage(EmberRunService.P + "§c周免路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空周免路径（按默认消耗优先）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a周免 → §f" + label(id) + " §7· " + tip(id));
        if (id == ASK) maybeAfterProgress(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[体力]", "/corerpg stamina", "看免费抵扣", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 creditpath", "重选", "GRAY"});
    }

    private static StaminaService stamina() {
        CoreRpgPlugin pl = plugin();
        return pl == null ? null : pl.getStaminaService();
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

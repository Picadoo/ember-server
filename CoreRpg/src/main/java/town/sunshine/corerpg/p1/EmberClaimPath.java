package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D515: pending-reward claim path — auto / hold / brief.
 * NEW acquisition: chooses when ledger rewards hit the bag (auto vs hold-for-/claim; brief = quiet chat).
 * Distinct from DailyPath (bounty chase), Vault auto-deposit, Prep/Bar. Chat only.
 * Zero reward amounts / AFK / sx. Not combo sticky, not forge/enter/ActionBar cue.
 */
public final class EmberClaimPath {

    public static final String C_PATH = "p1_claim_path";
    public static final String C_OFFER = "p1_claim_path_offer";

    public static final int NONE = 0;
    /** Deliver on settle + ambient join/town (stock) */
    public static final int AUTO = 1;
    /** Defer settle+ambient deliver; player uses /corerpg p1 claim */
    public static final int HOLD = 2;
    /** Deliver normally but suppress settlement chat lines (quietDeliver) */
    public static final int BRIEF = 3;

    private EmberClaimPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == AUTO || id == HOLD || id == BRIEF;
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
        if ("auto".equals(s) || "自动".equals(s) || "到账".equals(s) || "1".equals(s)) return AUTO;
        if ("hold".equals(s) || "暂存".equals(s) || "待领".equals(s) || "2".equals(s)) return HOLD;
        if ("brief".equals(s) || "quiet".equals(s) || "静默".equals(s) || "3".equals(s)) return BRIEF;
        return -1;
    }

    public static String key(int id) {
        if (id == AUTO) return "auto";
        if (id == HOLD) return "hold";
        if (id == BRIEF) return "brief";
        return "none";
    }

    public static String label(int id) {
        if (id == AUTO) return "补领·自动";
        if (id == HOLD) return "补领·暂存";
        if (id == BRIEF) return "补领·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "通关与回城自动把暂存奖励发到背包（默认）";
        if (id == HOLD) return "通关奖励先暂存，点「补领」或 /corerpg p1 claim 再领";
        if (id == BRIEF) return "照常到账，但不刷「结算到账」长提示";
        return "点选奖励到账方式（改节奏 · 不改奖励内容）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8补领路径：首通 Q01 后可选";
        if (!valid(id)) return "§8补领路径：未选 · /corerpg p1 claimpath";
        return "§e补领路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Skip ambient join/town auto-deliver. Bukkit-free. */
    public static boolean shouldHoldAmbient(int path) {
        return path == HOLD;
    }

    public static boolean shouldHoldAmbient(PlayerData d) {
        return shouldHoldAmbient(get(d));
    }

    /** Defer settle deliver. Bukkit-free. */
    public static boolean shouldHoldSettle(int path) {
        return path == HOLD;
    }

    public static boolean shouldHoldSettle(PlayerData d) {
        return shouldHoldSettle(get(d));
    }

    /** Quiet settlement chat. Bukkit-free. */
    public static boolean shouldBrief(int path) {
        return path == BRIEF;
    }

    public static boolean shouldBrief(PlayerData d) {
        return shouldBrief(get(d));
    }

    /** Ambient deliver gate used by onJoin / world-change. */
    public static void maybeAmbientDeliver(Player p, EmberRunService runs) {
        if (p == null || runs == null) return;
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (shouldHoldAmbient(d)) {
            int n = runs.store().ledger(p.getUniqueId()).open().size();
            if (n > 0) {
                p.sendMessage(EmberRunService.P + "§7补领·暂存：有 §f" + n + " §7项待领 · /corerpg p1 claim");
                ConfirmTokens.sendButtons(p, EmberRunService.P,
                        new String[]{"[补领]", "/corerpg p1 claim", "领取暂存奖励", "GREEN"});
            }
            return;
        }
        runs.deliver(p);
    }

    /** Settle deliver with HOLD/BRIEF honor. */
    public static void settleDeliver(Player p, PlayerData d, EmberRunService runs) {
        if (p == null || runs == null) return;
        if (shouldHoldSettle(d)) {
            int n = runs.store().ledger(p.getUniqueId()).open().size();
            p.sendMessage(EmberRunService.P + "§e补领·暂存 §7本局奖励先挂账"
                    + (n > 0 ? "（现有 §f" + n + " §7项）" : "")
                    + " · 点补领或 /corerpg p1 claim");
            ConfirmTokens.sendButtons(p, EmberRunService.P,
                    new String[]{"[补领]", "/corerpg p1 claim", "领取暂存奖励", "GREEN"},
                    new String[]{"[改路径]", "/corerpg p1 claimpath", "自动/暂存/静默", "GRAY"});
            return;
        }
        if (shouldBrief(d)) {
            runs.deliverQuiet(p);
            return;
        }
        runs.deliver(p);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选奖励到账方式（改节奏 · 可随时改 · 不改奖励内容）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 claimpath auto", tip(AUTO), "GREEN"},
                new String[]{"[暂存]", "/corerpg p1 claimpath hold", tip(HOLD), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 claimpath brief", tip(BRIEF), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 claimpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 285L); // after daily ~270
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
            p.sendMessage(EmberRunService.P + "§c补领路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空补领路径（按默认自动到账）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a补领 → §f" + label(id) + " §7· " + tip(id));
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[补领]", "/corerpg p1 claim", "领取暂存", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 claimpath", "重选", "GRAY"});
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

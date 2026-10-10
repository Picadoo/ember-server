package town.sunshine.corerpg.p1;

import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D537: auction deal notify path — open / gate / busy.
 * NEW economy: sticky policy for 寄售成交 awareness (live ping vs join digest vs mute).
 * Distinct from TicketPath (stamina), MailPath (attachments), PingPath (friends). Chat only.
 * Zero tax/price tables / AFK / sx / forge / combo sticky / enter-card / ActionBar cue.
 */
public final class EmberDealPath {

    public static final String C_PATH = "p1_deal_path";
    public static final String C_OFFER = "p1_deal_path_offer";
    public static final String C_PEND_N = "p1_deal_pend_n";
    public static final String C_PEND_COIN = "p1_deal_pend_coin";

    public static final int NONE = 0;
    public static final int OPEN = 1;
    public static final int GATE = 2;
    public static final int BUSY = 3;

    private EmberDealPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == OPEN || id == GATE || id == BUSY;
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
        if ("open".equals(s) || "auto".equals(s) || "敞开".equals(s) || "实时".equals(s) || "1".equals(s)) return OPEN;
        if ("gate".equals(s) || "ask".equals(s) || "汇总".equals(s) || "摘要".equals(s) || "2".equals(s)) return GATE;
        if ("busy".equals(s) || "mute".equals(s) || "静拒".equals(s) || "静默".equals(s) || "3".equals(s)) return BUSY;
        return -1;
    }

    public static String key(int id) {
        if (id == OPEN) return "open";
        if (id == GATE) return "gate";
        if (id == BUSY) return "busy";
        return "none";
    }

    public static String label(int id) {
        if (id == OPEN) return "成交·敞开";
        if (id == GATE) return "成交·汇总";
        if (id == BUSY) return "成交·静拒";
        return "未选";
    }

    public static String tip(int id) {
        if (id == OPEN) return "寄售成交时立刻提醒（离线记入汇总）";
        if (id == GATE) return "不刷实时；进服汇总成交笔数与实收（默认）";
        if (id == BUSY) return "不提醒寄售成交";
        return "点选寄售成交提醒节奏（改经济感知 · 不改税率）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8成交路径：首通 Q01 后可选";
        if (!valid(id)) return "§8成交路径：未选 · /corerpg p1 dealpath";
        return "§e成交路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldLive(int path) { return path == OPEN; }
    public static boolean shouldMute(int path) { return path == BUSY; }

    /** After successful auction sale credit. NONE/GATE queue; OPEN live or queue if offline; BUSY silent. */
    public static void onSold(UUID sellerUuid, Player sellerOnline, int listingId, int receive,
                              String buyerName, String taxLabel, int listPrice) {
        if (sellerUuid == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return;
        PlayerData sellerData = pl.getDataStore().get(sellerUuid);
        if (sellerData == null) return;
        int path = get(sellerData);
        if (shouldMute(path)) return;
        boolean live = shouldLive(path) && sellerOnline != null && sellerOnline.isOnline();
        if (live) {
            sellerOnline.sendMessage(ChatColor.GOLD + "[寄售] " + ChatColor.GREEN + "寄售 #" + listingId + " 已成交"
                    + ChatColor.YELLOW + " · 实收 " + receive + " 币"
                    + ChatColor.DARK_GRAY + "（税 " + taxLabel + "，标价 " + listPrice + "）"
                    + ChatColor.GRAY + " · 买家 " + buyerName);
            return;
        }
        // GATE, NONE, or OPEN-while-offline
        sellerData.addPeriodCount(C_PEND_N, "all", 1);
        if (receive > 0) sellerData.addPeriodCount(C_PEND_COIN, "all", receive);
        pl.getDataStore().flushMutation(sellerUuid);
    }

    public static void maybeDigestOnJoin(Player p) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null) return;
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) return;
        if (shouldMute(get(d))) return;
        int n = d.periodCount(C_PEND_N, "all");
        int coin = d.periodCount(C_PEND_COIN, "all");
        if (n <= 0) return;
        d.addPeriodCount(C_PEND_N, "all", -n);
        if (coin > 0) d.addPeriodCount(C_PEND_COIN, "all", -coin);
        pl.getDataStore().flushMutation(p.getUniqueId());
        p.sendMessage(EmberRunService.P + "§e成交·汇总 §7寄售成交 §f" + n + " §7笔 · 实收 §e" + coin + " §7币（离线/汇总）");
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选寄售成交提醒节奏（改经济感知 · 可随时改 · 不改税率）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[敞开]", "/corerpg p1 dealpath open", tip(OPEN), "GREEN"},
                new String[]{"[汇总]", "/corerpg p1 dealpath gate", tip(GATE), "GOLD"},
                new String[]{"[静拒]", "/corerpg p1 dealpath busy", tip(BUSY), "RED"},
                new String[]{"[取消]", "/corerpg p1 dealpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 585L);
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
            p.sendMessage(EmberRunService.P + "§c成交路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空成交路径（按默认汇总）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a成交 → §f" + label(id) + " §7· " + tip(id));
        if (id == GATE || id == OPEN) maybeDigestOnJoin(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[寄售]", "/corerpg auction", "我的在售", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 dealpath", "重选", "GRAY"});
    }

    public static void adminSeedPending(PlayerData d, int n, int coin) {
        if (d == null || n <= 0) return;
        d.addPeriodCount(C_PEND_N, "all", n);
        if (coin > 0) d.addPeriodCount(C_PEND_COIN, "all", coin);
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

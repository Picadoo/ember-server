package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.MailService;
import town.sunshine.corerpg.PlayerData;

/**
 * D523: system-mail attachment claim path — auto / ask / mute.
 * NEW acquisition: sticky policy when mail has unclaimed attachments (join + new deliver).
 * Distinct from ClaimPath (run ledger), SignPath, OnlinePath, CodexPath. Chat only.
 * Zero power / AFK / sx. Not combo sticky, not forge/enter/ActionBar cue.
 */
public final class EmberMailPath {

    public static final String C_PATH = "p1_mail_path";
    public static final String C_OFFER = "p1_mail_path_offer";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    private EmberMailPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == AUTO || id == ASK || id == MUTE;
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
        if ("auto".equals(s) || "自动".equals(s) || "1".equals(s)) return AUTO;
        if ("ask".equals(s) || "提醒".equals(s) || "手领".equals(s) || "2".equals(s)) return ASK;
        if ("mute".equals(s) || "quiet".equals(s) || "静默".equals(s) || "3".equals(s)) return MUTE;
        return -1;
    }

    public static String key(int id) {
        if (id == AUTO) return "auto";
        if (id == ASK) return "ask";
        if (id == MUTE) return "mute";
        return "none";
    }

    public static String label(int id) {
        if (id == AUTO) return "邮件·自动";
        if (id == ASK) return "邮件·提醒";
        if (id == MUTE) return "邮件·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "有未领附件时自动一键领取";
        if (id == ASK) return "有附件时聊天提醒，点按钮领取（默认）";
        if (id == MUTE) return "不刷提醒；可 /corerpg mail claim all";
        return "点选邮件附件领取方式（改节奏 · 不改邮件表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8邮件路径：首通 Q01 后可选";
        if (!valid(id)) return "§8邮件路径：未选 · /corerpg p1 mailpath";
        return "§e邮件路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    /** After join / new mail deliver — honor path. */
    public static void maybeAfterMail(Player p, MailService mail) {
        if (p == null || mail == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        int can = mail.countClaimable(p.getUniqueId());
        if (can <= 0) return;
        int path = get(d);
        if (shouldAuto(path)) {
            int n = mail.claimAllAttachments(p);
            if (n > 0) p.sendMessage(EmberRunService.P + "§a邮件·自动 §7已领 " + n + " 封附件");
            return;
        }
        if (shouldMute(path)) return;
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e邮件有未领附件（" + can + "） ",
                new String[]{"[一键领取]", "/corerpg mail claim all", "领取全部附件", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 mailpath", "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选邮件附件领取方式（改节奏 · 可随时改 · 不改邮件表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 mailpath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 mailpath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 mailpath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 mailpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 375L); // after codex ~360
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
            p.sendMessage(EmberRunService.P + "§c邮件路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空邮件路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a邮件 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO && pl.getMailService() != null)
            maybeAfterMail(p, pl.getMailService());
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[邮箱]", "/corerpg mail", "看收件箱", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 mailpath", "重选", "GRAY"});
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

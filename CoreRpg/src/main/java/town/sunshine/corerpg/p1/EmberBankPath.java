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
 * D540: stamina bank draw path — auto / ask / mute.
 * NEW economy: sticky mid-day draw from 体力银行 when current < daily cost (bank otherwise only fills on day reset).
 * Distinct from DosePath (potion drink), TicketPath (ticket convert), PrepPath. Chat only.
 * Zero stamina tables / AFK / sx / forge / combo sticky / enter-card / ActionBar cue.
 * TIP-FS D13402: offerPick / applyAndReply / 无参 bankpath / 周要约 →开 /ember_p1_bank_path；
 * 清 ConfirmTokens 教 bankpath 主 UI；零改伤害/CD表/掉落/D540 银行上限.
 */
public final class EmberBankPath {

    public static final String C_PATH = "p1_bank_path";
    public static final String C_OFFER = "p1_bank_path_offer";

    /** TIP-FS D13402 · OpenCommand · 选打薄页 id 同名 */
    public static final String PATH_PICK_CMD = "/ember_p1_bank_path";
    public static final String PATH_PICK_LABEL = "[取银路径]";
    public static final String PATH_ADVENTURE_CMD = "/ember_p1_adventure";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    private EmberBankPath() {}

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
        if ("ask".equals(s) || "提醒".equals(s) || "手点".equals(s) || "2".equals(s)) return ASK;
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
        if (id == AUTO) return "取银·自动";
        if (id == ASK) return "取银·提醒";
        if (id == MUTE) return "取银·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "日中体力不足且银行有存时自动取出补到日常消耗";
        if (id == ASK) return "可取银行时聊天提醒，点按钮取出（默认）";
        if (id == MUTE) return "不刷提醒；可手点取银 / 等日切自动回填";
        return "点选体力银行日中取出节奏（改进本经济 · 不改银行上限）";
    }

    /**
     * TIP-FS D13402 · 薄页 lore 一行（无 §）：当前取银路径。
     */
    public static String menuLine(int id) {
        return "取银路径 · " + label(id);
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8取银路径：首通 Q01 后可选";
        if (!valid(id)) return "§8取银路径：未选 · 点取银路径页";
        return "§e取银路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    public static void maybeAfterProgress(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        StaminaService st = stamina();
        if (st == null) return;
        if (!st.needsBankDraw(d)) return;
        int path = get(d);
        int bank = d.getStaminaBank();
        if (shouldAuto(path)) {
            int got = st.pathDrawBank(p);
            if (got > 0) {
                p.sendMessage(EmberRunService.P + "§a取银·自动 §7从银行取出 §f+" + got
                        + " §7（现 " + st.getStamina(d) + "/" + st.resolveMax(d)
                        + " · 银行 " + d.getStaminaBank() + "）");
            }
            return;
        }
        if (shouldMute(path)) return;
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e体力不足且银行有 §f" + bank + " §e ",
                new String[]{"[取银]", "/corerpg p1 bank", "从银行取出补到日常消耗", "GREEN"},
                new String[]{"[改路径]", PATH_PICK_CMD, "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选体力银行日中取出节奏（改经济 · 可随时改 · 不改上限）：");
        // TIP-FS D13402: 开选打薄页；禁 ConfirmTokens 教 bankpath 为主 UI
        openPathPick(p);
    }

    /** Open thin page /ember_p1_bank_path (TrMenu bind); fallback chat button if command fails. */
    public static void openPathPick(Player p) {
        if (p == null) return;
        try {
            p.performCommand("ember_p1_bank_path");
        } catch (Throwable th) {
            ConfirmTokens.sendButtons(p, EmberRunService.P,
                    new String[]{PATH_PICK_LABEL, PATH_PICK_CMD, "自动/提醒/静默/清空/技能组/冒险", "GREEN"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 630L); // after dose ~615
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
        if (!EmberSkillKit.bankPathUnlocked(d, runs)) {
            p.sendMessage(EmberRunService.P + "§c取银路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空取银路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a取银 → §f" + label(id) + " §7· " + tip(id));
        try { // D1608
            StaminaService st = stamina();
            int bank = 0;
            if (st != null && d != null) bank = d.getStaminaBank();
            p.sendActionBar("§e取银 · §f" + label(id) + " §7· 银行 §f" + bank);
        } catch (Throwable ignored) { }
        if (id == AUTO) maybeAfterProgress(p);
        // TIP-FS D13402: 下一步→选打薄页/冒险（禁教裸 bankpath 为主 UI）
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{PATH_PICK_LABEL, PATH_PICK_CMD, "自动/提醒/静默/清空/技能组/冒险", "GREEN"},
                new String[]{"[冒险]", PATH_ADVENTURE_CMD, "回冒险", "AQUA"});
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

package town.sunshine.corerpg.p1;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D552: dungeon sight path — auto / ask / mute.
 * NEW dungeon combat loop: sticky Night Vision in P1 worlds so dark rooms stay readable.
 * Distinct from Grip/Armor/Charm/Sip/Prep/life-shop/Extra roll. Chat only.
 * Zero dmg/CD / AFK / sx / forge / combo sticky / enter-card / ActionBar / hunger / cosmetic trail.
 */
public final class EmberSightPath {

    public static final String C_PATH = "p1_sight_path";
    public static final String C_OFFER = "p1_sight_path_offer";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    /** ~5 minutes; refreshed by HUD tick while AUTO */
    public static final int NV_TICKS = 20 * 60 * 5;
    private static final long ASK_COOLDOWN_MS = 15_000L;
    private static final Map<UUID, Long> lastAsk = new ConcurrentHashMap<UUID, Long>();

    private EmberSightPath() {}

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
        if (id == AUTO) return "夜视·自动";
        if (id == ASK) return "夜视·提醒";
        if (id == MUTE) return "夜视·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "副本内自动保持夜视（暗房更好打）";
        if (id == ASK) return "副本内无夜视时聊天提醒，点按钮开启（默认）";
        if (id == MUTE) return "不刷提醒；自己带夜视";
        return "点选副本夜视节奏（改地下城可视 · 不改伤害表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8夜视路径：首通 Q01 后可选";
        if (!valid(id)) return "§8夜视路径：未选 · /corerpg p1 sightpath";
        return "§e夜视路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    public static boolean needsSight(Player p) {
        if (p == null || p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) return false;
        if (!EmberMode.active() || !EmberMode.isP1World(p.getWorld())) return false;
        return !p.hasPotionEffect(PotionEffectType.NIGHT_VISION);
    }

    public static boolean pathApplySight(Player p) {
        if (p == null || !needsSight(p)) return false;
        p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, NV_TICKS, 0, false, false), true);
        return true;
    }

    /** Called from set HUD tick while in P1 combat. */
    public static void maybeAfterProgress(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        if (!needsSight(p)) return;
        int path = get(d);
        if (shouldAuto(path)) {
            if (pathApplySight(p)) {
                // quiet refresh after first; only announce when newly applied from empty
                // (HUD may re-tick — only message if we just applied and remaining was 0: handled by needsSight)
                p.sendMessage(EmberRunService.P + "§a夜视·自动 §7已开启");
            }
            return;
        }
        if (shouldMute(path)) return;
        long now = System.currentTimeMillis();
        Long last = lastAsk.get(p.getUniqueId());
        if (last != null && now - last < ASK_COOLDOWN_MS) return;
        lastAsk.put(p.getUniqueId(), now);
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e副本偏暗，可开夜视 ",
                new String[]{"[夜视]", "/corerpg p1 sight", "开启夜视约 5 分钟", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 sightpath", "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选副本夜视节奏（改可视 · 可随时改 · 不改伤害表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 sightpath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 sightpath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 sightpath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 sightpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 810L);
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
            p.sendMessage(EmberRunService.P + "§c夜视路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空夜视路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a夜视 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO) maybeAfterProgress(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[主线]", "/corerpg p1", "看进度", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 sightpath", "重选", "GRAY"});
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

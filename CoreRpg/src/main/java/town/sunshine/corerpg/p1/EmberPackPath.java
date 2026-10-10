package town.sunshine.corerpg.p1;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D555: mid-run pack-relief path — auto / ask / mute.
 * NEW dungeon acquisition: when the backpack is nearly full inside a P1 instance and still holds
 * vault-stashable mats, sticky deposit frees slots so loot keeps flowing.
 * Distinct from StashPath (hub/settle, no bag-pressure / no in-run gate), Flee/Chest/Sight/Charm/Grip/Armor/Sip.
 * Chat only. Zero drop tables / AFK / sx / forge / combo sticky / enter-card / ActionBar.
 */
public final class EmberPackPath {

    public static final String C_PATH = "p1_pack_path";
    public static final String C_OFFER = "p1_pack_path_offer";

    public static final int NONE = 0;
    public static final int AUTO = 1;
    public static final int ASK = 2;
    public static final int MUTE = 3;

    /** Fire when this many storage slots (or fewer) are empty. */
    public static final int TIGHT_SLOTS = 1;

    private static final long ASK_COOLDOWN_MS = 20_000L;
    private static final Map<UUID, Long> lastAsk = new ConcurrentHashMap<UUID, Long>();

    private EmberPackPath() {}

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
        if (id == AUTO) return "腾包·自动";
        if (id == ASK) return "腾包·提醒";
        if (id == MUTE) return "腾包·静默";
        return "未选";
    }

    public static String tip(int id) {
        if (id == AUTO) return "局内背包将满且有可存材料时自动入库腾格";
        if (id == ASK) return "局内将满时聊天提醒，点按钮入库（默认）";
        if (id == MUTE) return "不刷提醒；自己 /corerpg p1 stash 或等结算存仓";
        return "点选局内腾包节奏（改副本获取 · 不改掉落表）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8腾包路径：首通 Q01 后可选";
        if (!valid(id)) return "§8腾包路径：未选 · /corerpg p1 packpath";
        return "§e腾包路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static boolean shouldAuto(int path) { return path == AUTO; }
    public static boolean shouldMute(int path) { return path == MUTE; }

    public static int emptyStorage(Player p) {
        if (p == null) return 0;
        int n = 0;
        ItemStack[] st = p.getInventory().getStorageContents();
        if (st == null) return 0;
        for (ItemStack x : st) {
            if (x == null || x.getType() == Material.AIR) n++;
        }
        return n;
    }

    public static boolean inPackWorld(Player p) {
        if (p == null) return false;
        return EmberRunService.blocksLegacy(p.getWorld()) || EmberMode.isP1World(p.getWorld());
    }

    public static boolean needsPack(Player p) {
        if (p == null || !p.isOnline()) return false;
        if (!inPackWorld(p)) return false;
        if (emptyStorage(p) > TIGHT_SLOTS) return false;
        return EmberStashPath.hasStashableMats(p);
    }

    public static boolean pathPack(Player p) {
        if (p == null || !needsPack(p)) return false;
        return depositMats(p);
    }

    /** ASK button: deposit if in-run with stashable (tight optional). */
    public static boolean pathPackClick(Player p) {
        if (p == null || !p.isOnline()) return false;
        if (!inPackWorld(p)) return false;
        if (!EmberStashPath.hasStashableMats(p)) return false;
        return depositMats(p);
    }

    private static boolean depositMats(Player p) {
        EmberVault v = EmberVault.get();
        if (v == null) return false;
        Map<String, Long> m = v.depositAll(p);
        return m != null && !m.isEmpty();
    }

    /** After a pickup (or admin probe) while bag is tight in-run. */
    public static void maybeAfterPressure(Player p) {
        if (p == null || !needsPack(p)) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        int path = get(d);
        if (shouldAuto(path)) {
            if (pathPack(p)) {
                p.sendMessage(EmberRunService.P + "§a腾包·自动 §7已入库腾格");
            }
            return;
        }
        if (shouldMute(path)) return;
        long now = System.currentTimeMillis();
        Long last = lastAsk.get(p.getUniqueId());
        if (last != null && now - last < ASK_COOLDOWN_MS) return;
        lastAsk.put(p.getUniqueId(), now);
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e背包将满 ",
                new String[]{"[腾包]", "/corerpg p1 pack", "材料入库腾格", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 packpath", "自动/提醒/静默", "GRAY"});
    }

    /** Admin / smoke: honor path even when not tight (AUTO still needs stashable+P1). */
    public static void maybeProbe(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        int path = get(d);
        if (shouldAuto(path)) {
            if (needsPack(p) && pathPack(p)) {
                p.sendMessage(EmberRunService.P + "§a腾包·自动 §7已入库腾格");
            } else {
                p.sendMessage(EmberRunService.P + "§a腾包·自动 §7待命（局内背包将满且有可存材料时入库）");
            }
            return;
        }
        if (shouldMute(path)) return;
        ConfirmTokens.sendButtons(p, EmberRunService.P + "§e背包将满 ",
                new String[]{"[腾包]", "/corerpg p1 pack", "材料入库腾格", "GREEN"},
                new String[]{"[改路径]", "/corerpg p1 packpath", "自动/提醒/静默", "GRAY"});
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选局内腾包节奏（改获取 · 可随时改 · 不改掉落表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[自动]", "/corerpg p1 packpath auto", tip(AUTO), "GREEN"},
                new String[]{"[提醒]", "/corerpg p1 packpath ask", tip(ASK), "GOLD"},
                new String[]{"[静默]", "/corerpg p1 packpath mute", tip(MUTE), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 packpath clear", "清空偏好", "DARK_GRAY"});
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
        Bukkit.getScheduler().runTaskLater(pl, r, 855L);
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
            p.sendMessage(EmberRunService.P + "§c腾包路径需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空腾包路径（按默认提醒）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        flush(p);
        p.sendMessage(P + "§a腾包 → §f" + label(id) + " §7· " + tip(id));
        if (id == AUTO) maybeProbe(p);
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[主线]", "/corerpg p1", "看进度", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 packpath", "重选", "GRAY"});
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

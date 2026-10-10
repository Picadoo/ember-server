package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D498: combat tune path — sharp / ward / full.
 * Real play change: writes DualLead sig-slots + Attune cost-side together.
 * Chat buttons only. Zero mark spend / dmg tables / AFK / sx. Not forge / enter-card / ActionBar twin.
 */
public final class EmberTunePath {

    public static final String C_PATH = "p1_tune_path";
    public static final String C_OFFER = "p1_tune_path_offer";

    public static final int NONE = 0;
    /** 刃优先 + 原版代价 */
    public static final int SHARP = 1;
    /** 护符优先 + 调律代价 */
    public static final int WARD = 2;
    /** 双开 + 调律代价 */
    public static final int FULL = 3;

    private EmberTunePath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == SHARP || id == WARD || id == FULL;
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
        if ("clear".equals(s) || "none".equals(s) || "off".equals(s) || "取消".equals(s)) return NONE;
        if ("sharp".equals(s) || "锋刃".equals(s) || "blade".equals(s) || "1".equals(s)) return SHARP;
        if ("ward".equals(s) || "护符".equals(s) || "charm".equals(s) || "2".equals(s)) return WARD;
        if ("full".equals(s) || "双签".equals(s) || "both".equals(s) || "3".equals(s)) return FULL;
        return -1;
    }

    public static String label(int id) {
        if (id == SHARP) return "锋刃签律";
        if (id == WARD) return "护符签律";
        if (id == FULL) return "双签签律";
        return "未选";
    }

    public static String tip(int id) {
        if (id == SHARP) return "只开刃签名 + 原版代价 · 干净刃向";
        if (id == WARD) return "只开护符签名 + 调律代价 · 符侧加压";
        if (id == FULL) return "刃+护符双开 + 调律代价 · 满签加压";
        return "点选签律（一次写入双签槽+调律侧）";
    }

    public static int leadId(int id) {
        if (id == SHARP) return EmberDualLead.BLADE;
        if (id == WARD) return EmberDualLead.CHARM;
        if (id == FULL) return EmberDualLead.BOTH;
        return EmberDualLead.NONE;
    }

    public static int attuneId(int id) {
        if (id == SHARP) return EmberAttunePath.ORIGIN;
        if (id == WARD || id == FULL) return EmberAttunePath.ALT;
        return EmberAttunePath.NONE;
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8签律路径：首通 Q07 后可选（需调律）";
        if (!valid(id)) return "§8签律路径：未选 · /corerpg p1 tunepath";
        return "§e签律路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选签律玩法（一次写入双签槽+调律代价侧 · 回城可换 · 不花徽记）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[锋刃签律]", "/corerpg p1 tunepath sharp", tip(SHARP), "GOLD"},
                new String[]{"[护符签律]", "/corerpg p1 tunepath ward", tip(WARD), "AQUA"},
                new String[]{"[双签签律]", "/corerpg p1 tunepath full", tip(FULL), "LIGHT_PURPLE"},
                new String[]{"[取消]", "/corerpg p1 tunepath clear", "清空签律（双签改双开 · 调律粘性清）", "GRAY"});
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

    /** After Q07 — attune unlock. Delay past attunepath (120L). */
    public static void scheduleOfferAfterQ07(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 150L);
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
        if (!runs.progressFlag(d, EmberSignature.ALT_UNLOCK)) {
            p.sendMessage(EmberRunService.P + "§c签律需本人首通 Q07（签名调律解锁）。");
            return;
        }
        if (!runs.progressFlag(d, EmberSignature.DUAL_UNLOCK)) {
            p.sendMessage(EmberRunService.P + "§c签律需本人首通 Q03（双签名解锁）。");
            return;
        }
        if (EmberSkillKit.inDungeon(p, runs)) {
            p.sendMessage(EmberRunService.P + "§c副本里不能改签律，请回城后再选。");
            return;
        }
        String P = EmberRunService.P;
        EmberGrowthService g = EmberGrowthService.get();

        if (id == NONE) {
            set(d, NONE);
            EmberDualLead.apply(d, EmberDualLead.BOTH);
            EmberAttunePath.set(d, EmberAttunePath.NONE);
            flush(p);
            p.sendMessage(P + "已清空签律路径（双签槽改双开 · 调律路径粘性清，各签名保持当前）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        EmberDualLead.apply(d, leadId(id));
        EmberAttunePath.set(d, attuneId(id));
        int have = EmberAttunePath.unlockedAltCount(d, g);
        int flipped = 0;
        if (g != null && have > 0) {
            flipped = EmberAttunePath.applyAlts(d, g, attuneId(id) == EmberAttunePath.ALT);
        }
        flush(p);
        boolean bOff = d.periodCount(EmberGrowthService.C_SIGOFF + "blade", "all") > 0;
        boolean cOff = d.periodCount(EmberGrowthService.C_SIGOFF + "charm", "all") > 0;
        p.sendMessage(P + "§a签律 → §f" + label(id) + " §7· " + tip(id)
                + " §8（双签=" + EmberDualLead.label(leadId(id))
                + " · 调律=" + EmberAttunePath.label(attuneId(id))
                + " · blade" + (bOff ? "关" : "开") + "/charm" + (cOff ? "关" : "开") + "）");
        if (have <= 0) {
            p.sendMessage(P + "§e还没有已解锁的调律版 · 槽已写好；解锁调律后可再 /corerpg p1 tunepath 对齐代价");
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[调律页]", "/corerpg p1 sig attune", "解锁调律版", "GOLD"},
                    new String[]{"[双签路径]", "/corerpg p1 duallead", "单改槽", "AQUA"},
                    new String[]{"[换一条]", "/corerpg p1 tunepath", "重选", "GRAY"});
            return;
        }
        p.sendMessage(P + "§7调律已对齐 §f" + flipped + "§7 / " + have + " 个已解锁签名 · 下一局起生效");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[调律路径]", "/corerpg p1 attunepath", "单改代价侧", "GOLD"},
                new String[]{"[双签路径]", "/corerpg p1 duallead", "单改槽", "AQUA"},
                new String[]{"[签名页]", "/corerpg p1 sig menu", "看生效签名", "GREEN"},
                new String[]{"[换一条]", "/corerpg p1 tunepath", "重选", "GRAY"});
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

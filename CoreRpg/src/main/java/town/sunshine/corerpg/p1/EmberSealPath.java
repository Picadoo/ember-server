package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D497: combat seal path — hunt / ember / bind.
 * Real play change: writes brand sticky + DualLead sig-slot on/off together.
 * Chat buttons only. Zero pin price / dmg / CD / AFK / sx. Not forge spend / enter-card / ActionBar twin.
 */
public final class EmberSealPath {

    public static final String C_PATH = "p1_seal_path";
    public static final String C_OFFER = "p1_seal_path_offer";

    public static final int NONE = 0;
    /** 猎缀 · 刃签开 / 护符关 */
    public static final int HUNT = 1;
    /** 余烬 · 刃签开 / 护符关 */
    public static final int EMBER = 2;
    /** 定身 · 护符开 / 刃关 */
    public static final int BIND = 3;

    private EmberSealPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == HUNT || id == EMBER || id == BIND;
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
        if ("hunt".equals(s) || "猎缀".equals(s) || "b_affix".equals(s) || "1".equals(s)) return HUNT;
        if ("ember".equals(s) || "余烬".equals(s) || "余烬纹".equals(s) || "b_set".equals(s) || "2".equals(s)) return EMBER;
        if ("bind".equals(s) || "定身".equals(s) || "c_tele".equals(s) || "3".equals(s)) return BIND;
        return -1;
    }

    public static String label(int id) {
        if (id == HUNT) return "猎缀纹印";
        if (id == EMBER) return "余烬纹印";
        if (id == BIND) return "定身纹印";
        return "未选";
    }

    public static String tip(int id) {
        if (id == HUNT) return "猎缀烙纹 + 只开刃签名 · 对词缀精英";
        if (id == EMBER) return "余烬烙纹 + 只开刃签名 · 套装事件向";
        if (id == BIND) return "定身烙纹 + 只开护符签名 · 扛预警招";
        return "点选纹印（一次写入烙纹路径+双签槽）";
    }

    public static int brandId(int id) {
        if (id == HUNT) return EmberBrandPath.HUNT;
        if (id == EMBER) return EmberBrandPath.EMBER;
        if (id == BIND) return EmberBrandPath.BIND;
        return EmberBrandPath.NONE;
    }

    public static int leadId(int id) {
        if (id == BIND) return EmberDualLead.CHARM;
        if (id == HUNT || id == EMBER) return EmberDualLead.BLADE;
        return EmberDualLead.NONE;
    }

    public static String glance(int id, boolean dualUnlocked) {
        if (!dualUnlocked) return "§8纹印路径：首通 Q03 后可选（需双签名）";
        if (!valid(id)) return "§8纹印路径：未选 · /corerpg p1 sealpath";
        return "§e纹印路径：§f" + label(id) + " §8· " + tip(id);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选纹印玩法（一次写入烙纹路径+双签槽开关 · 回城可换 · 不改价/伤害表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[猎缀纹印]", "/corerpg p1 sealpath hunt", tip(HUNT), "GOLD"},
                new String[]{"[余烬纹印]", "/corerpg p1 sealpath ember", tip(EMBER), "RED"},
                new String[]{"[定身纹印]", "/corerpg p1 sealpath bind", tip(BIND), "AQUA"},
                new String[]{"[取消]", "/corerpg p1 sealpath clear", "清空纹印（烙纹清 · 双签改双开）", "GRAY"});
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

    /** After Q03 dual unlock — delay past DualLead first offer (~50L). */
    public static void scheduleOfferAfterQ03(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        Runnable r = () -> {
            if (!p.isOnline()) return;
            PlayerData d = dataOf(p);
            if (d == null) return;
            maybeOfferWeekly(p, d, EmberPlayfeelTelemetry.weekKey());
        };
        if (pl == null) { r.run(); return; }
        Bukkit.getScheduler().runTaskLater(pl, r, 100L);
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
        if (!runs.progressFlag(d, EmberSignature.DUAL_UNLOCK)) {
            p.sendMessage(EmberRunService.P + "§c纹印需本人首通 Q03（双签名解锁）。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            EmberBrandPath.set(d, EmberBrandPath.NONE);
            EmberDualLead.apply(d, EmberDualLead.BOTH); // reopen both slots
            flush(p);
            p.sendMessage(P + "已清空纹印路径（烙纹清 · 双签槽改两条都开）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        set(d, id);
        EmberBrandPath.set(d, brandId(id));
        EmberDualLead.apply(d, leadId(id));
        flush(p);
        boolean bOff = d.periodCount(EmberGrowthService.C_SIGOFF + "blade", "all") > 0;
        boolean cOff = d.periodCount(EmberGrowthService.C_SIGOFF + "charm", "all") > 0;
        p.sendMessage(P + "§a纹印 → §f" + label(id) + " §7· " + tip(id)
                + " §8（烙纹=" + EmberBrandPath.label(brandId(id))
                + " · 双签=" + EmberDualLead.label(leadId(id))
                + " · blade" + (bOff ? "关" : "开") + "/charm" + (cOff ? "关" : "开") + "）");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{EmberBrandPath.pinLabel(brandId(id)), EmberBrandPath.pinCmd(brandId(id)),
                        "预览定向（不扣料直到确认）", "GREEN"},
                new String[]{"[烙纹路径]", "/corerpg p1 brandpath", "单改烙纹粘性", "GOLD"},
                new String[]{"[双签路径]", "/corerpg p1 duallead", "单改槽开关", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 sealpath", "重选", "GRAY"});
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

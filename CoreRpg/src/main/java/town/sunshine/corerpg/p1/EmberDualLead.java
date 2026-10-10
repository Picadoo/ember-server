package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D466: dual-signature first-path pick on Q03 (DUAL_UNLOCK).
 * Choose blade-lead / charm-lead / both — real slot path; zero stamp rate / power / AFK.
 */
public final class EmberDualLead {

    public static final String C_LEAD = "p1_dual_lead";
    public static final String C_OFFER = "p1_dual_lead_offer";

    public static final int NONE = 0;
    public static final int BLADE = 1;
    public static final int CHARM = 2;
    public static final int BOTH = 3;

    private EmberDualLead() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_LEAD, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == BLADE || id == CHARM || id == BOTH;
    }

    public static int parse(String raw) {
        if (raw == null) return -1;
        String s = raw.toLowerCase(Locale.ROOT).trim();
        if ("clear".equals(s) || "none".equals(s) || "off".equals(s) || "取消".equals(s)) return NONE;
        if ("blade".equals(s) || "刃".equals(s) || "刃优先".equals(s) || "1".equals(s)) return BLADE;
        if ("charm".equals(s) || "护符".equals(s) || "护符优先".equals(s) || "2".equals(s)) return CHARM;
        if ("both".equals(s) || "双开".equals(s) || "两条".equals(s) || "3".equals(s)) return BOTH;
        return -1;
    }

    public static String label(int id) {
        if (id == BLADE) return "刃签名优先";
        if (id == CHARM) return "护符签名优先";
        if (id == BOTH) return "两条都开";
        return "未选";
    }

    public static String tip(int id) {
        if (id == BLADE) return "只开刃签名 · 护符槽关掉（可随时改）";
        if (id == CHARM) return "只开护符签名 · 刃槽关掉（可随时改）";
        if (id == BOTH) return "刃+护符两条签名同时生效（双签默认）";
        return "点选双签名怎么开";
    }

    public static String glance(int id, boolean dualUnlocked) {
        if (!dualUnlocked) return "§8双签路径：首通 Q03 后可选";
        if (!valid(id)) return "§8双签路径：未选 · /corerpg p1 duallead";
        return "§e双签路径：§f" + label(id);
    }

    /** Apply lead preference + sig-off slots. Caller flushes. */
    public static boolean apply(PlayerData d, int id) {
        if (d == null) return false;
        if (id != NONE && !valid(id)) return false;
        int cur = d.periodCount(C_LEAD, "all");
        if (cur != id) d.addPeriodCount(C_LEAD, "all", id - cur);
        if (id == NONE) return true;
        // blade lead → charm off; charm lead → blade off; both → both on
        setOff(d, "blade", id == CHARM);
        setOff(d, "charm", id == BLADE);
        return true;
    }

    static void setOff(PlayerData d, String slot, boolean off) {
        int cur = d.periodCount(EmberGrowthService.C_SIGOFF + slot, "all");
        int want = off ? 1 : 0;
        if (cur != want) d.addPeriodCount(EmberGrowthService.C_SIGOFF + slot, "all", want - cur);
    }

    public static void scheduleOffer(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) { offer(p); return; }
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (p.isOnline()) offer(p);
        }, 50L); // after dual-unlock announce (~2.5s)
    }

    public static void offer(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        if (d.periodCount(C_OFFER, "all") > 0) return;
        d.addPeriodCount(C_OFFER, "all", 1);
        flush(p);
        String P = EmberRunService.P;
        p.sendMessage(P + "§e双签名已开 · 选怎么开槽（可随时改 · 不影响烙印与掉落）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[刃优先]", "/corerpg p1 duallead blade", tip(BLADE), "GOLD"},
                new String[]{"[护符优先]", "/corerpg p1 duallead charm", tip(CHARM), "AQUA"},
                new String[]{"[两条都开]", "/corerpg p1 duallead both", tip(BOTH), "GREEN"},
                new String[]{"[打开签名页]", "/corerpg p1 sig menu", "细调开关", "YELLOW"});
    }

    /** Force re-offer (admin / smoke). */
    public static void forceOffer(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d != null) {
            int cur = d.periodCount(C_OFFER, "all");
            if (cur > 0) d.addPeriodCount(C_OFFER, "all", -cur);
            flush(p);
        }
        offer(p);
    }

    private static PlayerData dataOf(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (!(pl instanceof town.sunshine.corerpg.CoreRpgPlugin)) return null;
        EmberRunService runs = ((town.sunshine.corerpg.CoreRpgPlugin) pl).getEmberRuns();
        return runs == null ? null : runs.dataOf(p.getUniqueId());
    }

    private static void flush(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin)
            ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().flushMutation(p.getUniqueId());
    }
}

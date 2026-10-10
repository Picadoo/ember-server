package town.sunshine.corerpg.p1;

import java.util.Locale;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D492: signature attune combat path — original vs 调律 bulk.
 * Real play change ({@code p1_sigalt_*} cost mods on unlocked alts); chat buttons only.
 * Zero stamp/unlock price / AFK / sx change. Not forge chase / enter-card / ActionBar twin.
 */
public final class EmberAttunePath {

    public static final String C_PATH = "p1_attune_path";
    public static final String C_OFFER = "p1_attune_path_offer";

    public static final int NONE = 0;
    public static final int ORIGIN = 1; // all unlocked alts → original
    public static final int ALT = 2;    // all unlocked alts → 调律

    private EmberAttunePath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == ORIGIN || id == ALT;
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
        if ("origin".equals(s) || "original".equals(s) || "原版".equals(s) || "1".equals(s)) return ORIGIN;
        if ("alt".equals(s) || "attune".equals(s) || "调律".equals(s) || "2".equals(s)) return ALT;
        return -1;
    }

    public static String label(int id) {
        if (id == ORIGIN) return "原版代价";
        if (id == ALT) return "调律代价";
        return "未选";
    }

    public static String tip(int id) {
        if (id == ORIGIN) return "已解锁调律的签名全部用原版代价";
        if (id == ALT) return "已解锁调律的签名全部用调律版代价";
        return "点选签名代价侧（真改战斗模组）";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8调律路径：首通 Q07 后可选";
        if (!valid(id)) return "§8调律路径：未选 · /corerpg p1 attunepath";
        return "§e调律路径：§f" + label(id) + " §8· " + tip(id);
    }

    /** Count unlocked alts (Bukkit-free via growth). */
    public static int unlockedAltCount(PlayerData d, EmberGrowthService g) {
        if (d == null || g == null) return 0;
        int n = 0;
        for (Map.Entry<String, EmberSignature.Alt> e : EmberSignature.ALTS.entrySet()) {
            EmberSignature.Def sd = EmberSignature.byId(e.getKey());
            if (sd != null && g.altUnlocked(d, sd)) n++;
        }
        return n;
    }

    /**
     * Apply ORIGIN/ALT to every unlocked 调律签名. Returns how many flipped.
     * Does not unlock or spend marks.
     */
    public static int applyAlts(PlayerData d, EmberGrowthService g, boolean useAlt) {
        if (d == null || g == null) return 0;
        int flipped = 0;
        for (Map.Entry<String, EmberSignature.Alt> e : EmberSignature.ALTS.entrySet()) {
            EmberSignature.Def sd = EmberSignature.byId(e.getKey());
            if (sd == null || !g.altUnlocked(d, sd)) continue;
            boolean cur = g.sigAlt(d, sd);
            if (cur == useAlt) continue;
            int want = useAlt ? 1 : 0;
            int now = d.periodCount(EmberSignature.C_ALT + sd.id, "all");
            d.addPeriodCount(EmberSignature.C_ALT + sd.id, "all", want - now);
            flipped++;
        }
        return flipped;
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选签名调律路径（真改代价模组 · 仅已解锁 · 回城可换 · 不花徽记）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[原版代价]", "/corerpg p1 attunepath origin", tip(ORIGIN), "GREEN"},
                new String[]{"[调律代价]", "/corerpg p1 attunepath alt", tip(ALT), "LIGHT_PURPLE"},
                new String[]{"[调律页]", "/corerpg p1 sig attune", "单件解锁/细调", "GOLD"},
                new String[]{"[取消]", "/corerpg p1 attunepath clear", "清空路径", "GRAY"});
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

    /** After Q07 — attune unlock. Delay past raid/abyss offers. */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 120L);
    }

    public static void applyAndReply(Player p, int id) {
        if (p == null) return;
        CoreRpgPlugin pl = plugin();
        if (pl == null || pl.getEmberRuns() == null) {
            p.sendMessage(EmberRunService.P + "主线本服务未加载");
            return;
        }
        PlayerData d = pl.getDataStore().get(p.getUniqueId());
        if (d == null) { p.sendMessage(EmberRunService.P + "数据未就绪"); return; }
        if (!pl.getEmberRuns().progressFlag(d, EmberSignature.ALT_UNLOCK)) {
            p.sendMessage(EmberRunService.P + "§c签名调律需本人首通 Q07。");
            return;
        }
        if (EmberSkillKit.inDungeon(p, pl.getEmberRuns())) {
            p.sendMessage(EmberRunService.P + "§c副本里不能改调律，请回城后再选。");
            return;
        }
        EmberGrowthService g = EmberGrowthService.get();
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空调律路径（各签名保持当前原版/调律）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        int have = unlockedAltCount(d, g);
        set(d, id);
        int flipped = 0;
        if (g != null && have > 0) flipped = applyAlts(d, g, id == ALT);
        flush(p);
        p.sendMessage(P + "§a调律路径 → §f" + label(id) + " §7· " + tip(id));
        if (have <= 0) {
            p.sendMessage(P + "§e还没有已解锁的调律版 · 先去调律页 Shift+解锁（花徽记，永久）");
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[调律页]", "/corerpg p1 sig attune", "解锁后再回来一键侧", "GOLD"},
                    new String[]{"[换一条]", "/corerpg p1 attunepath", "重选", "GRAY"});
            return;
        }
        p.sendMessage(P + "§7已对齐 §f" + flipped + "§7 / " + have + " 个已解锁调律签名 · 下一局起生效");
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[调律页]", "/corerpg p1 sig attune", "单件细看代价", "GOLD"},
                new String[]{"[签名页]", "/corerpg p1 sig menu", "看生效签名", "AQUA"},
                new String[]{"[换一条]", "/corerpg p1 attunepath", "重选", "GRAY"});
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

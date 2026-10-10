package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D493: set-family combat path — scorch / burst / sustain.
 * Real commitment: writes {@link EmberSetFocus} + loot {@code p1_target}, frames 身法变体 / 套装普攻身份.
 * Chat buttons only. Zero set power coef / AFK / sx / stamp change.
 * Not forge spend-chase / enter-card / ActionBar twin.
 */
public final class EmberFamilyPath {

    public static final String C_PATH = "p1_family_path";
    public static final String C_OFFER = "p1_family_path_offer";

    public static final int NONE = 0;
    public static final int SCORCH = 1;
    public static final int BURST = 2;
    public static final int SUSTAIN = 3;

    private EmberFamilyPath() {}

    public static int get(PlayerData d) {
        if (d == null) return NONE;
        int v = d.periodCount(C_PATH, "all");
        return valid(v) ? v : NONE;
    }

    public static boolean valid(int id) {
        return id == SCORCH || id == BURST || id == SUSTAIN;
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
        if ("scorch".equals(s) || "焚烬".equals(s) || "1".equals(s) || "huohen".equals(s) || "火痕".equals(s)) return SCORCH;
        if ("burst".equals(s) || "烬爆".equals(s) || "2".equals(s) || "baoshan".equals(s) || "爆闪".equals(s)) return BURST;
        if ("sustain".equals(s) || "炽愈".equals(s) || "承烬".equals(s) || "3".equals(s)
                || "chenghu".equals(s) || "承护".equals(s)) return SUSTAIN;
        return -1;
    }

    public static String familyKey(int id) {
        if (id == SCORCH) return "scorch";
        if (id == BURST) return "burst";
        if (id == SUSTAIN) return "sustain";
        return null;
    }

    public static int toFocusId(int id) {
        if (id == SCORCH) return EmberSetFocus.SCORCH;
        if (id == BURST) return EmberSetFocus.BURST;
        if (id == SUSTAIN) return EmberSetFocus.SUSTAIN;
        return EmberSetFocus.NONE;
    }

    public static String label(int id) {
        String f = familyKey(id);
        return f == null ? "未选" : EmberItemData.familyName(f);
    }

    /** Combat identity tip: step variant + set proc. */
    public static String tip(int id) {
        if (id == SCORCH) return "火痕步 · 普攻点燃打首领";
        if (id == BURST) return "爆闪步 · 普攻炸圈清杂";
        if (id == SUSTAIN) return "承护步 · 普攻回血站桩";
        return "点选套装打法（同步焦点+掉落族）";
    }

    public static String stepName(int id) {
        if (id == SCORCH) return EmberSkillKit.stepDisplayName(EmberSkillKit.STEP_VARIANT_HUOHEN, false);
        if (id == BURST) return EmberSkillKit.stepDisplayName(EmberSkillKit.STEP_VARIANT_BAOSHAN, false);
        if (id == SUSTAIN) return EmberSkillKit.stepDisplayName(EmberSkillKit.STEP_VARIANT_CHENGHU, false);
        return "踏步";
    }

    public static String glance(int id, boolean unlocked) {
        if (!unlocked) return "§8套装打法：首通 Q01 后可选";
        if (!valid(id)) return "§8套装打法：未选 · /corerpg p1 familypath";
        return "§e套装打法：§f" + label(id) + " §8· " + tip(id);
    }

    public static void offerPick(Player p) {
        if (p == null) return;
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选套装打法（同步套装焦点+掉落目标族 · 成套后改身法/普攻身份 · 不改倍率表）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[焚烬·火痕]", "/corerpg p1 familypath scorch", tip(SCORCH), "GOLD"},
                new String[]{"[烬爆·爆闪]", "/corerpg p1 familypath burst", tip(BURST), "RED"},
                new String[]{"[炽愈·承护]", "/corerpg p1 familypath sustain", tip(SUSTAIN), "GREEN"},
                new String[]{"[取消]", "/corerpg p1 familypath clear", "清空打法路径", "GRAY"});
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

    /** After Q01 — loot target / family identity matter. Delay past featured path. */
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
        Bukkit.getScheduler().runTaskLater(pl, r, 90L);
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
            p.sendMessage(EmberRunService.P + "§c套装打法需本人首通 Q01。");
            return;
        }
        String P = EmberRunService.P;
        if (id == NONE) {
            set(d, NONE);
            flush(p);
            p.sendMessage(P + "已清空套装打法路径（套装焦点/掉落目标未改）");
            return;
        }
        if (!valid(id)) {
            offerPick(p);
            return;
        }
        String fam = familyKey(id);
        set(d, id);
        EmberSetFocus.set(d, toFocusId(id));
        setLootTarget(d, fam);
        flush(p);
        p.sendMessage(P + "§a套装打法 → §f" + label(id) + " §7· " + tip(id));
        p.sendMessage(P + "§7已同步：套装焦点 §f" + label(id) + " §7· 掉落目标族 §f" + label(id)
                + " §8· 成 2 件套后身法→" + stepName(id));
        EmberLoadout lo = runs.loadouts() != null ? runs.loadouts().get(p) : null;
        String active = lo == null ? "none" : lo.activeSet;
        if (fam.equals(active)) {
            p.sendMessage(P + "§a现穿同族 · 战斗身份已在身：§f" + stepName(id) + " §7· "
                    + EmberItemData.familyBlurb(fam));
        } else if (active != null && !"none".equals(active)) {
            p.sendMessage(P + "§7现穿 §f" + EmberItemData.familyName(active)
                    + " §7· 凑齐 §f" + label(id) + " §7两件套后切换身法/普攻身份");
        } else {
            p.sendMessage(P + "§7还未成套 · " + EmberItemData.familyBlurb(fam));
        }
        ConfirmTokens.sendButtons(p, P + "§7下一步：",
                new String[]{"[冒险页]", "/corerpg p1 runs", "看掉落偏向", "GREEN"},
                new String[]{"[转化路径]", "/corerpg p1 convertpath " + fam, "跨族转化瞄这族", "GOLD"},
                new String[]{"[换一条]", "/corerpg p1 familypath", "重选", "GRAY"});
    }

    static void setLootTarget(PlayerData d, String fam) {
        if (d == null) return;
        int idx = 0;
        if (fam != null) {
            for (int i = 0; i < EmberRunRules.FAMILIES.length; i++) {
                if (EmberRunRules.FAMILIES[i].equals(fam)) { idx = i + 1; break; }
            }
        }
        int cur = d.periodCount(EmberRunService.C_TARGET, "all");
        if (cur != idx) d.addPeriodCount(EmberRunService.C_TARGET, "all", idx - cur);
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

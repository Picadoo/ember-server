package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

/**
 * D459: first-clear skill unlock path feel.
 * D460: on Q04/Q05 unlock, offer a one-shot playstyle pick (符文三选一 / 身法方向二选一).
 * D606: hope-ring Stage B2 — milestone lines name each set's F/副招/身法 fork (zero skill_mult / CD).
 */
public final class EmberSkillUnlock {

    /** Rising-edge offer latch (1 = buttons already sent for this character). */
    public static final String C_SHAPE_OFFERED = "p1_shape_pick_offered";
    public static final String C_STEP_OFFERED = "p1_step_pick_offered";

    private EmberSkillUnlock() {}

    /** @return display line without prefix, or null if this map unlocks no skill-kit key */
    public static String unlockLine(String mapKey) {
        if (mapKey == null) return null;
        if (EmberSkillKit.UNLOCK_DASH.equals(mapKey))
            return "烬突 · 潜行+F（与烬斩共享充能 · 挂机不自动烬突）";
        if (EmberSkillKit.UNLOCK_PARRY.equals(mapKey))
            return "余烬招架 · 单按 Q（首领预警砸中你后再按）";
        if (EmberSkillKit.UNLOCK_SHAPE.equals(mapKey))
            return "烬斩符文 · 扇形 / 直线 / 环斩（出本才可换）";
        if (EmberSkillKit.UNLOCK_STEP.equals(mapKey))
            return "身法 · 潜行+Q（前冲/后撤 · 两件套可变套装身法）";
        return null;
    }

    /**
     * D606: one-line set-fork hope under the unlock announce.
     * Names how 焚/爆/承 differ on this unlock — display only.
     */
    public static String setForkHope(String mapKey) {
        if (mapKey == null) return null;
        if (EmberSkillKit.UNLOCK_DASH.equals(mapKey))
            return "§e套装差：§7烬突落地 · 焚点燃 / 爆缓速 / 承微疗（不改 1.5B）";
        if (EmberSkillKit.UNLOCK_SHAPE.equals(mapKey))
            return "§e套装差：§7烬斩 · 推荐焚扇/爆直/承环 · 爆缓速/承微疗/焚余烬（可换形状）";
        if (EmberSkillKit.UNLOCK_STEP.equals(mapKey))
            return "§e套装差：§7身法 · 焚火痕点燃 / 爆闪缓速 / 承护微疗（两件套自动）";
        return null;
    }

    public static String shortName(String mapKey) {
        if (EmberSkillKit.UNLOCK_DASH.equals(mapKey)) return "烬突";
        if (EmberSkillKit.UNLOCK_PARRY.equals(mapKey)) return "余烬招架";
        if (EmberSkillKit.UNLOCK_SHAPE.equals(mapKey)) return "烬斩符文";
        if (EmberSkillKit.UNLOCK_STEP.equals(mapKey)) return "身法";
        return null;
    }

    /** Chat + button once per first-clear of a skill-unlock map; Q04/Q05 also offer playstyle pick. */
    public static void announce(Player p, String mapKey) {
        if (p == null) return;
        String line = unlockLine(mapKey);
        if (line == null) return;
        String P = EmberRunService.P;
        String sn = shortName(mapKey);
        try { p.sendActionBar("§6新技能 · §f" + (sn == null ? "解锁" : sn)); } catch (Throwable ignored) { } // D674
        ConfirmTokens.sendButton(p, P + "§6新技能解锁：§e" + line + " ",
                "[打开技能组]", "trmenu open ember_skill_kit",
                "查看按键 / 符文 / 身法 / 套装差");
        p.sendMessage(P + "§7不用打命令：主菜单 → 技能组");
        String hope = setForkHope(mapKey);
        if (hope != null) p.sendMessage(P + hope);
        if (EmberSkillKit.UNLOCK_SHAPE.equals(mapKey)) scheduleShapePick(p);
        if (EmberSkillKit.UNLOCK_STEP.equals(mapKey)) scheduleStepPick(p);
    }

    /** Admin / smoke: force re-offer without latch (clears then offers). */
    public static void forceOfferShape(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d != null) {
            int cur = d.periodCount(C_SHAPE_OFFERED, "all");
            if (cur > 0) d.addPeriodCount(C_SHAPE_OFFERED, "all", -cur);
        }
        offerShapePick(p);
    }

    public static void forceOfferStep(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d != null) {
            int cur = d.periodCount(C_STEP_OFFERED, "all");
            if (cur > 0) d.addPeriodCount(C_STEP_OFFERED, "all", -cur);
        }
        offerStepPick(p);
    }

    static void scheduleShapePick(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) { offerShapePick(p); return; }
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (p.isOnline()) offerShapePick(p);
        }, 40L); // ~2s — settle often still mid-exit
    }

    static void scheduleStepPick(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) { offerStepPick(p); return; }
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (p.isOnline()) offerStepPick(p);
        }, 40L);
    }

    /** One-shot 三选一 for 烬斩符文. Latched per character. */
    public static void offerShapePick(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        if (d.periodCount(C_SHAPE_OFFERED, "all") > 0) return;
        d.addPeriodCount(C_SHAPE_OFFERED, "all", 1);
        flush(p);
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选一个烬斩符文（玩法侧移 · 随时可回城再换）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[扇形]", "/corerpg skill shape fan", "近距扇面 · 默认", "GOLD"},
                new String[]{"[直线穿刺]", "/corerpg skill shape line", "中距直线", "AQUA"},
                new String[]{"[原地环斩]", "/corerpg skill shape ring", "环身近战", "LIGHT_PURPLE"});
    }

    /** One-shot 二选一 for 身法方向. Latched per character. */
    public static void offerStepPick(Player p) {
        if (p == null) return;
        PlayerData d = dataOf(p);
        if (d == null) return;
        if (d.periodCount(C_STEP_OFFERED, "all") > 0) return;
        d.addPeriodCount(C_STEP_OFFERED, "all", 1);
        flush(p);
        String P = EmberRunService.P;
        p.sendMessage(P + "§e选一个身法方向（前冲 / 后撤 · 随时可回城再换）：");
        ConfirmTokens.sendButtons(p, P,
                new String[]{"[前冲]", "/corerpg skill dir forward", "潜行+Q 向前", "GREEN"},
                new String[]{"[后撤]", "/corerpg skill dir back", "潜行+Q 向后", "YELLOW"});
    }

    private static PlayerData dataOf(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (!(pl instanceof town.sunshine.corerpg.CoreRpgPlugin)) return null;
        return ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().get(p.getUniqueId());
    }

    private static void flush(Player p) {
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl instanceof town.sunshine.corerpg.CoreRpgPlugin)
            ((town.sunshine.corerpg.CoreRpgPlugin) pl).getDataStore().flushMutation(p.getUniqueId());
    }

    /**
     * D617: next skill-unlock milestone hope (display only).
     * Order: Q02 烬突 → Q03 招架 → Q04 符文 → Q05 身法.
     */
    public static String nextHope(PlayerData d, EmberRunService runs) {
        if (d == null || runs == null) return "§8技能希望未载入";
        String[][] chain = new String[][] {
                { EmberSkillKit.UNLOCK_DASH, "Q02" },
                { EmberSkillKit.UNLOCK_PARRY, "Q03" },
                { EmberSkillKit.UNLOCK_SHAPE, "Q04" },
                { EmberSkillKit.UNLOCK_STEP, "Q05" },
        };
        for (String[] row : chain) {
            String map = row[0];
            if (runs.firstClearedKey(d, map)) continue;
            String name = shortName(map);
            String line = unlockLine(map);
            return "§e下一技能 · §f" + name + " §7需首通 " + row[1] + " §8· " + (line == null ? "" : line);
        }
        return "§a技能里程碑已齐 · 认套装差 / 形状 / 身法";
    }

    /**
     * D617 / D12282: worn-family set-feel hope for PAPI {@code %corerpg_kit_set_hope%}.
     * Display via {@link EmberSkillFeelSf#menuLine(EmberLoadout)} (short menu form).
     * Zero skill_mult.
     */
    public static String setHope(EmberLoadout lo) {
        return EmberSkillFeelSf.menuLine(lo);
    }

    /**
     * D617 long chat form (kept if callers need the older "套装手感 · 形状 · 落点" line).
     * Menus/PAPI use {@link #setHope} → menuLine.
     */
    public static String setHopeLong(EmberLoadout lo) {
        if (lo == null || lo.blade == null) return "§8拿手持刃看套装差与推荐形状";
        String fam = lo.blade.family;
        return "§e套装手感 · §f" + EmberItemData.familyName(fam) + " §8· " + EmberSkillFeelSf.setShapeHalf(fam);
    }

}

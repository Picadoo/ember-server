package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.SkillService;

/**
 * D5473: after picking row-1 talent, soft-apply D291 playstyle (posture+shape+step dir).
 * Zero growth mods / skill_mult / permanent dmg. Coeff track HOLD (T0'' ❌).
 */
public final class EmberTalentPlaystyle {

    /** Latch: p1_talent_style_<nodeId>@all — one soft apply per node learn */
    public static final String C_STYLE = "p1_talent_style_";

    private EmberTalentPlaystyle() {}
    public static void clearLatches(PlayerData d) {
        if (d == null) return;
        for (String id : new String[]{"t1a", "t1b", "t1c"}) {
            int v = d.periodCount(C_STYLE + id, "all");
            if (v > 0) d.addPeriodCount(C_STYLE + id, "all", -v);
        }
    }


    public static void schedulePending(Player p) {
        if (p == null) return;
        CoreRpgPlugin pl = (CoreRpgPlugin) Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null) return;
        Bukkit.getScheduler().runTaskLater(pl, () -> {
            if (!p.isOnline()) return;
            PlayerData d = pl.getDataStore().get(p.getUniqueId());
            maybePending(p, d);
        }, 110L);
    }


    /** Which row-1 node player has that still needs playstyle latch. */
    public static String pendingNode(PlayerData d) {
        if (d == null) return null;
        for (String id : new String[]{"t1a", "t1b", "t1c"}) {
            if (d.periodCount(C_STYLE + id, "all") > 0) continue;
            // growth pick stored as p1_growth_row_1 index; also C_LEARN
            if (d.periodCount("p4_spec_learn_" + id, "all") > 0
                    || hasRowPick(d, id)) {
                return id;
            }
        }
        return null;
    }

    static boolean hasRowPick(PlayerData d, String nodeId) {
        // C_ROW + 1 stores 1-based index in row; t1a=1 t1b=2 t1c=3
        int idx = d.periodCount("p4_spec_row1", "all");
        if (idx == 1 && "t1a".equals(nodeId)) return true;
        if (idx == 2 && "t1b".equals(nodeId)) return true;
        if (idx == 3 && "t1c".equals(nodeId)) return true;
        return false;
    }

    /** Call after Q04/Q05 enter unlock — one soft apply if talent already picked. */
    public static void maybePending(Player p, PlayerData d) {
        String id = pendingNode(d);
        if (id == null) return;
        maybeAfterPick(p, d, id);
    }



    /** Map t1a/t1b/t1c → posture id; else 0. */
    public static int postureForNode(String nodeId) {
        if ("t1a".equals(nodeId)) return EmberPosturePath.STRIKE;
        if ("t1b".equals(nodeId)) return EmberPosturePath.GUARD;
        if ("t1c".equals(nodeId)) return EmberPosturePath.SWEEP;
        return EmberPosturePath.NONE;
    }

    /**
     * Rising-edge soft playstyle after row-1 pick.
     * @return true when posture/shape/step written
     */
    public static boolean maybeAfterPick(Player p, PlayerData d, String nodeId) {
        if (p == null || d == null || nodeId == null) return false;
        int posture = postureForNode(nodeId);
        if (!EmberPosturePath.valid(posture)) return false;
        String key = C_STYLE + nodeId;
        if (d.periodCount(key, "all") > 0) return false;

        CoreRpgPlugin pl = (CoreRpgPlugin) Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl == null || pl.getEmberRuns() == null) return false;
        EmberRunService runs = pl.getEmberRuns();
        if (EmberSkillKit.inDungeon(p, runs)) return false;
        boolean shapeOk = EmberSkillKit.shapeUnlocked(d, runs);
        boolean stepOk = EmberSkillKit.stepVariantUnlocked(d, runs);
        if (!shapeOk || !stepOk) {
            p.sendMessage(EmberRunService.P + "§7「" + EmberPosturePath.label(posture)
                    + "」打法待 Q04 符文 + Q05 身后可用 · §8零改天赋系数");
            return false;
        }
        SkillService sk = pl.getSkillService();
        if (sk == null) return false;

        d.addPeriodCount(key, "all", 1);
        EmberPosturePath.set(d, posture);
        EmberShapePath.set(d, EmberPosturePath.shapePathId(posture));
        EmberStepPath.set(d, EmberPosturePath.stepPathId(posture));
        pl.getDataStore().flushMutation(p.getUniqueId());
        sk.cmdShape(p, EmberSkillKit.shapeKey(EmberPosturePath.shapeId(posture)));
        sk.cmdStepDir(p, EmberSkillKit.stepDirKey(EmberPosturePath.dirId(posture)));
        p.sendMessage(EmberRunService.P + "§a专精打法已挂 · §f" + EmberPosturePath.label(posture)
                + " §7· " + EmberSkillKit.shapeName(EmberPosturePath.shapeId(posture))
                + " + " + EmberSkillKit.stepDirName(EmberPosturePath.dirId(posture))
                + " §8（D291 · 零系数侧移）");
        try {
            p.sendActionBar("§a打法 · §f" + EmberPosturePath.label(posture));
        } catch (Throwable ignored) { }
        return true;
    }
}

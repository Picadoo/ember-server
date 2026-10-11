package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * D487 / D2062 UL1: upgrade commit → piece-aware deterministic next slot.
 * Prefer enhance → quality → refine → lock-reroll. Zero upgrade cost / AFK change.
 */
public final class EmberUpgradeLoop {

    private EmberUpgradeLoop() {}

    public static void afterUpgrade(Player p, List<TxnItem> list) {
        afterUpgrade(p, null, list);
    }

    /** D2062: with PlayerData for forgegoal + lock eligibility. */
    public static void afterUpgrade(Player p, PlayerData d, List<TxnItem> list) {
        if (p == null) return;
        EmberGemSharpLive.maybeAfterUpgrade(p, d, list); // D4209 sharp T2 live
        String piece = pieceHint(list);
        String P = EmberRunService.P;
        EmberItemData after = firstAfter(list);
        boolean goalHit = d != null && EmberForgeGoal.get(d) == EmberForgeGoal.UPGRADE;

        if (goalHit) {
            p.sendMessage(P + "§a本周工坊目标完成：§f升阶冲战力"
                    + (piece.isEmpty() ? "" : " §7· " + piece));
            try { p.sendActionBar("§a工坊目标 · 升阶完成"); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选强化/成色…", "GREEN"},
                    primaryBtn(after),
                    new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "AQUA"});
            return;
        }

        p.sendMessage(P + "§e升阶完成" + (piece.isEmpty() ? "" : " §7· " + piece)
                + " §7· 承诺同件下一槽（不改升阶价）：");
        try {
            p.sendActionBar(piece.isEmpty() ? "§e升阶 · §f继续同件养成" : ("§e升阶 · §f" + piece));
        } catch (Throwable ignored) { }
        String[] a = primaryBtn(after);
        String[] b = secondaryBtn(after);
        String[] c = new String[]{"[工坊]", "/corerpg p1 forge", "工坊总页", "AQUA"};
        ConfirmTokens.sendButtons(p, P + "§7升阶下一环：", a, b, c);
    }

    /** Primary deterministic next for the upgraded piece. */
    static String[] primaryBtn(EmberItemData after) {
        if (after == null)
            return new String[]{"[去强化]", "/corerpg p1 enhance", "同件继续强化", "GREEN"};
        EmberUpgradeRules.Plan enh = EmberUpgradeRules.enhanceCheck(after);
        if (enh != null && enh.ok())
            return new String[]{"[去强化]", "/corerpg p1 enhance", "同件继续强化（轨保留）", "GREEN"};
        Cost q = EmberUpgradeRules.qualityCost(after.quality);
        if (q != null)
            return new String[]{"[去成色]", "/corerpg p1 quality", "确定升成色", "YELLOW"};
        Cost r = EmberUpgradeRules.refineCost(after.craft);
        if (r != null)
            return new String[]{"[去精工]", "/corerpg p1 refine", "确定升精工", "AQUA"};
        String sl = "charm".equals(after.slot) ? "charm" : "blade";
        return new String[]{"[锁定洗练]", "/corerpg p1 reroll " + sl + " shard lock", "词条冲档", "BLUE"};
    }

    static String[] secondaryBtn(EmberItemData after) {
        if (after == null)
            return new String[]{"[成套追]", "/corerpg p1 setchase", "换上缺口", "GOLD"};
        Cost q = EmberUpgradeRules.qualityCost(after.quality);
        EmberUpgradeRules.Plan enh = EmberUpgradeRules.enhanceCheck(after);
        if (enh != null && enh.ok() && q != null)
            return new String[]{"[去成色]", "/corerpg p1 quality", "成色预览", "YELLOW"};
        Cost r = EmberUpgradeRules.refineCost(after.craft);
        if (r != null && (enh == null || !enh.ok()))
            return new String[]{"[去精工]", "/corerpg p1 refine", "精工预览", "AQUA"};
        return new String[]{"[成套追]", "/corerpg p1 setchase", "换上缺口", "GOLD"};
    }

    static EmberItemData firstAfter(List<TxnItem> list) {
        if (list == null) return null;
        for (TxnItem t : list) {
            if (t != null && t.after != null) return t.after;
        }
        return null;
    }

    public static void afterUpgrade(Player p) {
        afterUpgrade(p, null, null);
    }

    static String pieceHint(List<TxnItem> list) {
        if (list == null || list.isEmpty()) return "";
        for (TxnItem t : list) {
            if (t == null || t.after == null) continue;
            String before = t.before == null ? "?" : ("T" + t.before.tier);
            return before + "→" + t.after.shortLabel();
        }
        return "";
    }

    public static String followHint() {
        return "[去强化]|[去成色]|[去精工]|[锁定洗练]";
    }

    public static String chatLine() {
        return "§e升阶完成 §7· 承诺同件下一槽";
    }
}

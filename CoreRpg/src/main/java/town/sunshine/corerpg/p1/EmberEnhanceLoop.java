package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D485: enhance commit → forge-branch spend chase (continue / quality / refine).
 * D614: 下次必成 / 保底必成 celebration (ActionBar + chat). Zero enhance rate / pity / price change.
 */
public final class EmberEnhanceLoop {

    private EmberEnhanceLoop() {}

    /** After forge {@code enhance} commit OK (success or fail both chase). */
    public static void afterEnhance(Player p, List<TxnItem> list) {
        afterEnhance(p, null, list);
    }

    public static void afterEnhance(Player p, PlayerData d, List<TxnItem> list) {
        if (p == null) return;
        boolean won = leveled(list);
        String P = EmberRunService.P;
        celebrate(p, list, won);
        if (won) maybeGemHoleChase(p, d, list); // D731
        boolean goalHit = won && d != null && EmberForgeGoal.get(d) == EmberForgeGoal.ENHANCE;
        if (goalHit) { // D660
            p.sendMessage(P + "§a本周工坊目标完成：§f强化+1");
            try { p.sendActionBar("§a工坊目标 · 强化完成"); } catch (Throwable ignored) { }
            ConfirmTokens.sendButtons(p, P + "§7下一步：",
                    new String[]{"[换下周目标]", "/corerpg p1 forgegoal", "选精工/烙纹/转化…", "GREEN"},
                    new String[]{"[再强化]", "/corerpg p1 enhance", "手持件再冲一档", "GOLD"},
                    new String[]{"[去成色]", "/corerpg p1 quality", "成色预览", "AQUA"});
            return;
        }
        if (won) {
            p.sendMessage(P + "§e强化成功 §7· 承诺同件下一槽（不改强化/成色/精工价）：");
            EmberItemData after = firstAfter(list);
            // D2076: piece-aware quality/refine (same spirit as UpgradeLoop)
            String[] b = nextCraftBtn(after);
            String[] c = nextCraftBtn2(after);
            ConfirmTokens.sendButtons(p, P + "§7强化下一环：",
                    new String[]{"[再强化]", "/corerpg p1 enhance", "手持件再冲一档", "GREEN"},
                    b, c);
        } else {
            // D1232: fail remain honesty（非 celebration twin）
            String rem = "";
            if (list != null) {
                for (TxnItem ti : list) {
                    if (ti == null || ti.after == null) continue;
                    int u = EmberGearNextHint.enhanceAttemptsUntilSure(ti.after);
                    rem = u <= 1 ? "下次必成" : ("含下次≤" + u + "次必成");
                    break;
                }
            }
            p.sendMessage(P + "§e强化未升档 §7· 材料已扣"
                    + (rem.isEmpty() ? "" : (" · §f" + rem))
                    + " §7· 选下一步（不改保底规则）：");
            ConfirmTokens.sendButtons(p, P, // D886
                    new String[]{"[再试一次]", "/corerpg p1 enhance", "同件再强化（保底累计）", "GREEN"},
                    new String[]{"[成套追]", "/corerpg p1 setchase", "换件再冲", "GOLD"},
                    new String[]{"[去精工]", "/corerpg p1 refine", "先投精工", "AQUA"});
        }
    }

    /** D614: pity-ready / pity-win ActionBar + chat. */
    static void celebrate(Player p, List<TxnItem> list, boolean won) {
        if (p == null || list == null) return;
        for (TxnItem t : list) {
            if (t == null || t.before == null || t.after == null) continue;
            String P = EmberRunService.P;
            if (won && EmberUpgradeRules.guaranteed(t.before)) {
                String msg = "§a保底必成 · §f+" + t.before.enhance + " → +" + t.after.enhance;
                try { p.sendActionBar(msg); } catch (Throwable ignored) { }
                p.sendMessage(P + msg);
                try { p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.8f, 1.2f); } catch (Throwable ignored) { }
                return;
            }
            if (!won && EmberUpgradeRules.guaranteed(t.after)) {
                int max = EmberUpgradeRules.maxTries(t.after.enhance + 1);
                String msg = "§a下次必成 · §f保底 " + t.after.pity + "/" + max;
                try { p.sendActionBar(msg); } catch (Throwable ignored) { }
                p.sendMessage(P + msg + " §7· 再点一次确认强化即可升档");
                try { p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1f, 1.6f); } catch (Throwable ignored) { }
                return;
            }
        }
    }

    /** Admin smoke: success button set by default; pass fail for fail set. */
    public static void afterEnhance(Player p, boolean won) {
        if (p == null) return;
        if (won) afterEnhance(p, nullWon());
        else afterEnhance(p, nullFail());
    }


    /** D731/D754: after enhance win — new unlock or any empty unlocked hole → gem insert chase. */
    static void maybeGemHoleChase(Player p, PlayerData d, List<TxnItem> list) {
        if (p == null || list == null) return;
        for (TxnItem t : list) {
            if (t == null || t.before == null || t.after == null) continue;
            if (t.after.enhance <= t.before.enhance) continue;
            int holeNew = -1;
            for (int i = 0; i < t.after.sockMax(); i++) {
                if (t.after.sockUnlocked(i) && !t.before.sockUnlocked(i) && t.after.sockAt(i) == 0) {
                    holeNew = i; break;
                }
            }
            int holeEmpty = EmberGemInsertChase.firstEmptyHole(t.after); // D754
            int hole = holeNew >= 0 ? holeNew : holeEmpty;
            if (hole < 0) continue;
            if (holeNew >= 0) {
                p.sendMessage(P() + "§a新孔解锁 · §f孔" + holeNew + " §7· 可嵌宝石");
                try { p.sendActionBar("§a新孔 · §f" + holeNew + " §7· 镶嵌追"); } catch (Throwable ignored) { }
            } else {
                p.sendMessage(P() + "§e空孔可嵌 · §f孔" + holeEmpty + " §7· 强化后镶嵌追");
                try { p.sendActionBar("§e空孔 · §f" + holeEmpty + " §7· 镶嵌追"); } catch (Throwable ignored) { }
            }
            CoreRpgPlugin pl = plugin();
            if (pl == null) return;
            int path = EmberGemPath.get(d);
            if (!EmberGemPath.valid(path)) path = EmberGemPath.SHARP;
            EmberLoadoutService lo = pl.getEmberRuns() == null ? null : pl.getEmberRuns().loadouts();
            // D757: if no path gem in bag, point craft; else insert chase
            String gid = EmberGemPath.gemId(path);
            town.sunshine.corerpg.NiBridge ni = pl.getNiBridge();
            int have = (ni == null || gid == null) ? 0 : ni.countInInventory(p, gid);
            if (have <= 0) {
                p.sendMessage(P() + "§7空孔待命 · 背包无 §f" + EmberItemData.gemName(path)
                        + " §7· /corerpg p1 gem craft " + EmberGemPath.key(path));
                town.sunshine.corerpg.ConfirmTokens.sendButton(p, P() + "§7获取：",
                        "[合成" + EmberItemData.gemName(path) + "]",
                        "/corerpg p1 gem craft " + EmberGemPath.key(path),
                        "碎+骨+币 · 不走旧 socket");
            } else {
                EmberGemInsertChase.afterAcquire(p, path, lo, pl);
            }
            return;
        }
    }

    private static String P() { return EmberRunService.P; }

    private static CoreRpgPlugin plugin() {
        org.bukkit.plugin.Plugin pl = org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
        return pl instanceof CoreRpgPlugin ? (CoreRpgPlugin) pl : null;
    }


    static EmberItemData firstAfter(List<TxnItem> list) {
        if (list == null) return null;
        for (TxnItem t : list) {
            if (t != null && t.after != null) return t.after;
        }
        return null;
    }

    /** Prefer quality, else refine, else setchase. */
    static String[] nextCraftBtn(EmberItemData after) {
        if (after != null && EmberUpgradeRules.qualityCost(after.quality) != null)
            return new String[]{"[去成色]", "/corerpg p1 quality", "确定升成色", "YELLOW"};
        if (after != null && EmberUpgradeRules.refineCost(after.craft) != null)
            return new String[]{"[去精工]", "/corerpg p1 refine", "确定升精工", "AQUA"};
        return new String[]{"[成套追]", "/corerpg p1 setchase", "换装继续", "GOLD"};
    }

    static String[] nextCraftBtn2(EmberItemData after) {
        boolean q = after != null && EmberUpgradeRules.qualityCost(after.quality) != null;
        boolean r = after != null && EmberUpgradeRules.refineCost(after.craft) != null;
        if (q && r)
            return new String[]{"[去精工]", "/corerpg p1 refine", "成色后可精工", "AQUA"};
        if (q || r)
            return new String[]{"[成套追]", "/corerpg p1 setchase", "换装继续", "GOLD"};
        String sl = after != null && "charm".equals(after.slot) ? "charm" : "blade";
        return new String[]{"[锁定洗练]", "/corerpg p1 reroll " + sl + " shard lock", "词条冲档", "BLUE"};
    }

    static boolean leveled(List<TxnItem> list) {
        if (list == null) return true; // admin default = success set
        for (TxnItem t : list) {
            if (t == null || t.before == null || t.after == null) continue;
            if (t.after.enhance > t.before.enhance) return true;
        }
        return false;
    }

    private static List<TxnItem> nullWon() { return null; }
    private static List<TxnItem> nullFail() {
        return java.util.Collections.emptyList();
    }

    public static String followHint(boolean won) {
        return won ? "[再强化]|[去成色]|[去精工]|[锁定洗练]" : "[再试一次]|[去成色]|[去精工]";
    }

    public static String chatLine(boolean won) {
        return won ? "§e强化成功 §7· 选下一步花法" : "§e强化未升档 §7· 选下一步";
    }
}

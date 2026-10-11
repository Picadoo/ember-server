package town.sunshine.corerpg.p1;

import java.util.List;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberItemStore.TxnItem;

/**
 * D4209: blade upgrade crosses Sharp T2 floor with sharp gem seated → combat A activates.
 * Zero gem flat raise; one-shot chat/AB per piece uid (period latch).
 */
public final class EmberGemSharpLive {

    public static final String C_LIVE = "p1_gem_sharp_live_";

    private EmberGemSharpLive() {}

    /** True when before.tier &lt; SHARP_MIN_TIER ≤ after.tier and after has sharp in any unlocked hole. */
    public static boolean crossesSharpFloor(EmberItemData before, EmberItemData after) {
        if (before == null || after == null) return false;
        if (!after.isBlade()) return false;
        if (before.tier >= EmberGems.SHARP_MIN_TIER) return false;
        if (after.tier < EmberGems.SHARP_MIN_TIER) return false;
        return hasSharp(after);
    }

    public static boolean hasSharp(EmberItemData it) {
        if (it == null) return false;
        for (int i = 0; i < it.sockMax(); i++) {
            if (it.sockUnlocked(i) && it.sockAt(i) == EmberGems.CODE_SHARP) return true;
        }
        return false;
    }

    public static boolean maybeAfterUpgrade(Player p, PlayerData d, List<TxnItem> list) {
        if (p == null || list == null) return false;
        for (TxnItem t : list) {
            if (t == null || t.before == null || t.after == null) continue;
            if (!crossesSharpFloor(t.before, t.after)) continue;
            String key = C_LIVE + t.after.uid;
            if (d != null && d.periodCount(key, "all") > 0) continue;
            if (d != null) {
                d.addPeriodCount(key, "all", 1);
            }
            String P = EmberRunService.P;
            p.sendMessage(P + "§a锋利石生效 · §f刃达 T" + EmberGems.SHARP_MIN_TIER
                    + " §7· 镶嵌锋利开始计入攻击（不改宝石数值表）");
            try {
                p.sendActionBar("§a锋利石生效 · §fT" + EmberGems.SHARP_MIN_TIER + "+");
            } catch (Throwable ignored) { }
            return true;
        }
        return false;
    }
}

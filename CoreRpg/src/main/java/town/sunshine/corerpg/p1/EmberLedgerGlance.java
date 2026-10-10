package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;

/**
 * D940 / ARCH S2: one ledger pending-claim glance (chat + optional button).
 * Zero grant / sink / amount change — display only.
 */
public final class EmberLedgerGlance {

    private EmberLedgerGlance() {}

    /** Bukkit-free line. */
    /**
     * D1033: rows players can claim now — open minus first-clear {@link EmberRunRules#ST_AWAIT}
     * (same filter as {@code %corerpg_p1_pending%}).
     */
    public static int claimable(EmberRunService runs, java.util.UUID u) {
        if (runs == null || runs.store() == null || u == null) return 0;
        int n = 0;
        for (EmberRunRules.Row r : runs.store().ledger(u).open()) {
            if (!EmberRunRules.ST_AWAIT.equals(r.status)) n++;
        }
        return n;
    }

    public static String line(int open) {
        if (open <= 0) return "§8补领：无待领";
        return "§e补领·暂存：有 §f" + open + " §e项待领 · /corerpg p1 claim";
    }

    /** Ambient / settle hold cue with one claim button. */
    public static void tellPending(Player p, int open) {
        if (p == null || open <= 0) return;
        String P = EmberRunService.P;
        p.sendMessage(P + line(open));
        ConfirmTokens.sendButton(p, P, "[补领]", "/corerpg p1 claim", "领取暂存奖励");
    }

    /** After path set — show open count if any. */
    public static void tellIfAny(Player p, EmberRunService runs) {
        if (p == null || runs == null || runs.store() == null) return;
        int n = claimable(runs, p.getUniqueId());
        if (n > 0) tellPending(p, n);
    }

    /**
     * D944: inventory full — rows stayed in ledger. Same claim/stash/forge next as deliver waiting.
     * Zero grant change.
     */
    public static void bagFull(Player p, int waiting) {
        if (p == null || waiting <= 0) return;
        String P = EmberRunService.P;
        ConfirmTokens.sendButtons(p, P + org.bukkit.ChatColor.YELLOW + waiting
                        + " 项奖励因背包已满暂存（结果已锁定，不会重抽）。空出格子后：",
                new String[]{"[补领]", "/corerpg p1 claim", "领取暂存", "YELLOW"},
                new String[]{"[一键存入]", "/corerpg p1 stash", "腾背包", "GREEN"},
                new String[]{"[工坊]", "/corerpg p1 forge", "花料腾格", "GOLD"});
    }
}

package town.sunshine.corerpg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Pure rules for inventory snapshots (unit-tested): retention and when to skip an unchanged snapshot. */
public final class InvSnapRules {
    private InvSnapRules() {}

    public static final int KEEP_LAST = 50;
    public static final int KEEP_DAYS = 30;
    public static final long DAY = 86400000L;
    public static final long CST = 8L * 3600000L;

    /** one stored snapshot: id (db id or file key) + created time */
    public static final class Snap {
        public final String id;
        public final long at;
        public Snap(String id, long at) { this.id = id; this.at = at; }
    }

    /** CST calendar day number */
    public static long day(long ms) { return Math.floorDiv(ms + CST, DAY); }

    /**
     * Ids to delete: keep the newest {@code keepLast}, plus the newest snapshot of every CST day for the last
     * {@code keepDays} days (today included). Input order does not matter.
     */
    public static List<String> prune(List<Snap> all, long now, int keepLast, int keepDays) {
        List<Snap> s = new ArrayList<Snap>(all);
        s.sort((a, b) -> a.at != b.at ? Long.compare(b.at, a.at) : b.id.compareTo(a.id));
        Set<Long> daysSeen = new HashSet<Long>();
        long today = day(now);
        List<String> del = new ArrayList<String>();
        for (int i = 0; i < s.size(); i++) {
            Snap x = s.get(i);
            long d = day(x.at);
            boolean dailyKeep = today - d < keepDays && daysSeen.add(d);
            if (i < keepLast) { daysSeen.add(d); continue; }
            if (!dailyKeep) del.add(x.id);
        }
        return del;
    }

    /** automatic snapshots are skipped when nothing changed since the player's last stored one */
    public static boolean store(String reason, boolean sameAsLast) {
        if (!sameAsLast) return true;
        return "manual".equals(reason) || "pre-restore".equals(reason) || "death".equals(reason);
    }

    /** slot label for list/diff output; getContents() order of 1.12: 0-8 hotbar, 9-35 main, 36-39 boots..helmet, 40 offhand */
    public static String slotName(int i) {
        if (i < 0) return "?";
        if (i <= 8) return "快捷栏" + (i + 1);
        if (i <= 35) return "背包" + (i - 8);
        switch (i) {
            case 36: return "靴子";
            case 37: return "护腿";
            case 38: return "胸甲";
            case 39: return "头盔";
            case 40: return "副手";
            default: return "槽" + i;
        }
    }

    /** Default physical gacha ticket NI id (CoreGacha config tickets.ni_item). */
    public static final String GACHA_TICKET_NI = "ember_gacha_ticket";

    /**
     * D157: stacks whose NI id is a P1 vault whitelist material (incl. festival coin) or a physical gacha ticket
     * must not be recreated on inventory restore — those live in EmberVault / gacha_wallet ledgers.
     */
    public static boolean stripOnRestore(String niId, Set<String> vaultWhitelist, Set<String> extraSkip) {
        if (niId == null || niId.isEmpty()) return false;
        if (vaultWhitelist != null && vaultWhitelist.contains(niId)) return true;
        if (extraSkip != null && extraSkip.contains(niId)) return true;
        return GACHA_TICKET_NI.equals(niId);
    }

    /** how many of the given NI ids would be stripped (unit-test helper; order ignored) */
    public static int countStrip(Iterable<String> niIds, Set<String> vaultWhitelist, Set<String> extraSkip) {
        if (niIds == null) return 0;
        int n = 0;
        for (String id : niIds) if (stripOnRestore(id, vaultWhitelist, extraSkip)) n++;
        return n;
    }

    // ------------------------------------------------------------------ 1.64.1 ledger net-out (review round 2 #1)

    /**
     * How many of a warehouse-whitelisted material held in the snapshot ({@code snapAmt}) must NOT be restored.
     * {@code baseline} = the warehouse amount recorded in the snapshot (−1 = old snapshot without one);
     * {@code stashSince} = moved backpack → warehouse (一键存入) since the snapshot; {@code spendInvSince} = consumed
     * straight from the backpack since; {@code logOk} = the warehouse log could be read. Anything not provably still
     * "only in the backpack" is held back: with no baseline or no log the whole amount is held back.
     */
    public static long vaultDeduct(long snapAmt, long baseline, long vaultNow, long stashSince, long spendInvSince, boolean logOk) {
        if (snapAmt <= 0) return 0;
        if (baseline < 0 || !logOk) return snapAmt;
        long moved = Math.max(Math.max(0, vaultNow - baseline), Math.max(0, stashSince)) + Math.max(0, spendInvSince);
        return Math.min(snapAmt, moved);
    }

    /** physical gacha tickets: hold back what was redeemed into the wallet since the snapshot (all when the ledger is unreadable) */
    public static long ticketDeduct(long snapAmt, long redeemedSince, boolean ledgerOk) {
        if (snapAmt <= 0) return 0;
        if (!ledgerOk) return snapAmt;
        return Math.min(snapAmt, Math.max(0, redeemedSince));
    }

    /**
     * Removes {@code n} units from the given stack amounts, last stack first (amounts are modified; 0 = slot emptied).
     * Returns what was actually removed.
     */
    public static long takeFromStacks(int[] amounts, long n) {
        long left = n;
        for (int i = amounts.length - 1; i >= 0 && left > 0; i--) {
            int t = (int) Math.min(left, amounts[i]);
            amounts[i] -= t;
            left -= t;
        }
        return n - left;
    }

    /** admin line for one id: 「余烬碎片：快照 30 → 恢复 0（扣 30：…）」 */
    public static String netLine(String name, long snapAmt, long deduct, String why) {
        return name + "：快照 " + snapAmt + " → 恢复 " + (snapAmt - deduct) + (deduct > 0 ? "（扣 " + deduct + "：" + why + "）" : "（快照后没有存入 / 消耗记录，照常恢复）");
    }

    /** reason text for {@link #vaultDeduct} */
    public static String vaultWhy(long baseline, long vaultNow, long stashSince, long spendInvSince, boolean logOk) {
        if (baseline < 0) return "旧快照没有仓库基线，无法确认是否已存进仓库";
        if (!logOk) return "仓库流水读取失败";
        StringBuilder b = new StringBuilder();
        if (stashSince > 0) b.append("快照后一键存入仓库 ").append(stashSince);
        if (vaultNow - baseline > 0) b.append(b.length() > 0 ? "，" : "").append("仓库比快照时多 ").append(vaultNow - baseline);
        if (spendInvSince > 0) b.append(b.length() > 0 ? "，" : "").append("快照后从背包直接花掉 ").append(spendInvSince);
        return b.toString();
    }
}

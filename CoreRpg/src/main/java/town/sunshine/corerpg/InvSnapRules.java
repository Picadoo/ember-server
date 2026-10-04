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

}

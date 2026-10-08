package town.sunshine.corerpg.p1;

/**
 * D319 · invsnap restore guard for the six-slot migration (Bukkit-free; {@code EmberSixRestoreGuardTest}). A restore
 * overwrites the backpack + armor + ender chest but not the player's scoreboard tags nor the 待领 record, so restoring
 * across the migration puts the swap mark and the armor slots out of step (originals doubled or lost), or puts the
 * pre-migration armor back on while the same originals sit in 待领 (doubled).
 *
 * <p>Rules (first match wins):
 * <ol>
 *   <li>no {@code p1-six/<uuid>.yml} and no {@code ember_six_m_*} tag → allow (never migrated: the switch-off path,
 *       nothing changes);</li>
 *   <li>a journal is pending → refuse, <b>temporary</b> (the next hub visit resolves it);</li>
 *   <li>a swap tag without the migration flag → refuse, temporary (journal / tag out of step; check the log);</li>
 *   <li>a 待领 hand-out mark whose entry is still owed (claim interrupted) → refuse, temporary (one claim click settles it);</li>
 *   <li>migrated (flag): the snapshot was taken before the migration finished (or the finish time is unknown) → refuse,
 *       <b>permanent</b> (that snapshot can never be restored safely; a queued one is dropped); taken after → allow
 *       (a stale swap tag left after the flag is harmless and does not block).</li>
 * </ol>
 */
public final class EmberSixRestoreGuard {

    private EmberSixRestoreGuard() {}

    public static final String MANUAL = "docs/ops/OPS-ember-six-slot-migration.md「invsnap 恢复守卫」";

    public static final class Verdict {
        public final String reason;
        /** true = this snapshot can never be restored (a queued restore of it is dropped); false = retry later */
        public final boolean permanent;
        Verdict(String reason, boolean permanent) { this.reason = reason; this.permanent = permanent; }
        @Override public String toString() { return (permanent ? "permanent: " : "temporary: ") + reason; }
    }

    /**
     * @param recordExists {@code p1-six/<uuid>.yml} exists
     * @param flag         {@code p1_six_mig} = 1
     * @param journal      a migration journal is pending
     * @param doneAtSec    epoch seconds the migration finished (0 = unknown)
     * @param swapTag      the player carries an {@code ember_six_m_*} tag
     * @param claimOwed    an {@code ember_six_c_*} hand-out mark whose 待领 entry is still in the record
     * @param snapAtMs     the snapshot's time (epoch ms)
     * @return null = allow
     */
    public static Verdict check(boolean recordExists, boolean flag, boolean journal, long doneAtSec, boolean swapTag,
                                boolean claimOwed, long snapAtMs) {
        if (!recordExists && !swapTag) return null;
        if (journal) return new Verdict("六槽迁移未完成（journal 未决）：等玩家回到枢纽、迁移自动续上完成后再恢复", false);
        if (swapTag && !flag) return new Verdict("玩家身上有换装标签 ember_six_m_* 但迁移未完成：标签与记录不一致，先查日志 [P1 six]，不要手删标签", false);
        if (claimOwed) return new Verdict("待领领取中断未结清：让玩家在 装备 → 护甲 点一次「领取」后再恢复", false);
        if (flag) {
            if (doneAtSec <= 0) return new Verdict("六槽迁移完成时间未知：无法判断快照在迁移前还是迁移后，不恢复", true);
            if (snapAtMs < (doneAtSec + 1) * 1000L)
                return new Verdict("快照早于六槽迁移完成（" + InvSnapTime.fmt(doneAtSec * 1000L) + "）：恢复会让原甲位物品同时出现在身上和待领里（复制）；只能恢复迁移完成之后的快照", true);
        }
        return null;
    }

    /** time format shared with the invsnap messages (Asia/Shanghai) */
    static final class InvSnapTime {
        static String fmt(long ms) {
            java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("MM-dd HH:mm:ss", java.util.Locale.ROOT);
            f.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Shanghai"));
            return f.format(new java.util.Date(ms)) + " CST";
        }
    }
}

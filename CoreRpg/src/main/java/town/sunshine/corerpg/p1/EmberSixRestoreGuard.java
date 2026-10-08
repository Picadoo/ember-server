package town.sunshine.corerpg.p1;

/**
 * D319 · invsnap restore guard for the six-slot migration (Bukkit-free; {@code EmberSixRestoreGuardTest}). A restore
 * overwrites the backpack + armor + ender chest but neither the player's scoreboard tags nor the 待领 record, so
 * restoring inside the migration window puts the swap mark and the armor slots out of step (originals doubled or lost),
 * and restoring a migrated player to a pre-migration snapshot puts the old armor back on while the same originals sit in
 * 待领 (doubled).
 *
 * <p>The guard reads the record ({@code p1-six/<uuid>.yml}) only, never the scoreboard tags (总控 D319 (a): an orphan
 * {@code ember_six_m_*} tag left by a kill after the commit must not lock a player out of invsnap; the completed
 * migration clears it on the next hub visit). Rules, first match wins:
 * <ol>
 *   <li>no record file → allow (never migrated: the switch-off path, nothing changes);</li>
 *   <li>a journal is pending → refuse, <b>temporary</b> (the next hub visit resolves it; a queued restore stays queued);</li>
 *   <li>migrated ({@code p1_six_mig: 1}) and the snapshot was taken before the migration finished
 *       ({@code snapshot < done_at + 1 s}; {@code done_at} has seconds resolution) → refuse, <b>permanent</b> (a queued
 *       one is dropped); an old record without {@code done_at} → refuse every snapshot, permanent (cannot tell before /
 *       after: conservative);</li>
 *   <li>otherwise allow (migrated + snapshot after done_at; or a record without flag and without journal, e.g. a
 *       dropped journal / a self-check revert: the inventory went back to the pre-migration state).</li>
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
     * @param doneAtSec    {@code done_at}: epoch seconds the migration finished (0 = not recorded)
     * @param snapAtMs     the snapshot's time (epoch ms)
     * @return null = allow
     */
    public static Verdict check(boolean recordExists, boolean flag, boolean journal, long doneAtSec, long snapAtMs) {
        if (!recordExists) return null;
        if (journal) return new Verdict("六槽迁移未完成（journal 未决）：等玩家回到枢纽、迁移自动续上完成后再恢复", false);
        if (flag) {
            if (doneAtSec <= 0) return new Verdict("已六槽迁移但记录里没有完成时间（done_at）：无法判断快照在迁移前还是迁移后，按保守规则不恢复", true);
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

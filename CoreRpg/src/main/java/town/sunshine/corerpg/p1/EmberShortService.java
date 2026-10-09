package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * D391 短征 sx01：进本门闩 + S40 结算发放钩。地图 / MM / TrMenu 另号；本号保证
 * {@code p1 enter sx01} 路由与发放可单测。
 */
public final class EmberShortService {

    private final EmberRunService runs;

    EmberShortService(EmberRunService runs) {
        this.runs = runs;
    }

    private Logger log() { return runs.log(); }
    private static String P() { return EmberRunService.P; }

    int rewardedToday(PlayerData d, EmberRunMaps.MapDef m) {
        if (d == null || m == null) return 0;
        String claim = m.shortClaim == null || m.shortClaim.isEmpty() ? EmberShortRules.CLAIM : m.shortClaim;
        return Math.max(0, d.periodCount(claim, DailyService.today()));
    }

    int dailyCap(EmberRunMaps.MapDef m) {
        return m != null && m.shortDailyCap > 0 ? m.shortDailyCap : EmberShortRules.DAILY_CAP;
    }

    /** D391: Q01 首通门闩（日帽不挡进本）。 */
    String entryProblem(Player p, PlayerData d, EmberRunMaps.MapDef m) {
        String req = m == null || m.requires == null || m.requires.isEmpty() ? EmberShortRules.REQUIRES : m.requires;
        return EmberShortRules.entryProblemText(p.getName(), runs.progressFlag(d, req));
    }

    /**
     * Bukkit-free core: ledger rows + day claim bump + first-clear record.
     * @param dayKey Asia/Shanghai stamina day (tests pass a fixed key)
     */
    public static SettleResult applyGrants(EmberRunMaps.MapDef m, EmberRunRules.Ledger l, String runId,
            PlayerData pd, int rewardedBefore, boolean firstClearPaid, String dayKey, long now) {
        final boolean fresh = l.get(runId, EmberShortRules.G_CLEAR_COIN) == null
                && l.get(runId, EmberShortRules.G_PRACTICE) == null;
        int cap = m != null && m.shortDailyCap > 0 ? m.shortDailyCap : EmberShortRules.DAILY_CAP;
        String claim = m != null && m.shortClaim != null && !m.shortClaim.isEmpty() ? m.shortClaim : EmberShortRules.CLAIM;
        String ver = m != null && m.contentVersion != null ? m.contentVersion : "v1";
        String mapKey = m != null ? m.key : EmberShortRules.KEY;
        List<EmberRunRules.Grant> grants = fresh
                ? EmberShortRules.settleGrants(rewardedBefore, cap, firstClearPaid)
                : CollectionsEmpty();
        boolean pays = fresh && EmberShortRules.paysReward(rewardedBefore, cap);
        List<EmberRunRules.Row> changed = new ArrayList<EmberRunRules.Row>();
        boolean[] created = new boolean[1];
        if (fresh) {
            for (EmberRunRules.Grant g : grants) {
                boolean pending = g.amount > 0;
                EmberRunRules.Row r = l.record(runId, g.key, g.encode(),
                        pending ? EmberRunRules.ST_PENDING : EmberRunRules.ST_DELIVERED, now, created);
                if (created[0]) changed.add(r);
            }
            if (pays && pd != null) {
                pd.addPeriodCount(claim, dayKey, 1);
                // 首通包只挂在有奖通关上；日帽后无奖通关不记 paid（下次有奖仍可领首通包）
                if (!firstClearPaid) EmberFirstClear.record(pd, mapKey, ver);
            }
        }
        int after = rewardedBefore + (fresh && pays ? 1 : 0);
        return new SettleResult(fresh, pays, changed, after, !firstClearPaid && pays);
    }

    private static List<EmberRunRules.Grant> CollectionsEmpty() {
        return java.util.Collections.emptyList();
    }

    public static final class SettleResult {
        public final boolean fresh, pays, firstClearPaidNow;
        public final List<EmberRunRules.Row> changed;
        public final int rewardedAfter;
        SettleResult(boolean fresh, boolean pays, List<EmberRunRules.Row> changed, int rewardedAfter, boolean firstClearPaidNow) {
            this.fresh = fresh; this.pays = pays; this.changed = changed;
            this.rewardedAfter = rewardedAfter; this.firstClearPaidNow = firstClearPaidNow;
        }
    }

    void settle(EmberRunSession s, EmberRunMaps.MapDef m, UUID u) {
        PlayerData pd = runs.dataOf(u);
        EmberRunRules.Ledger l = runs.store().ledger(u);
        int before = rewardedToday(pd, m);
        boolean fcPaid = EmberFirstClear.paid(pd, m.key, m.contentVersion);
        SettleResult r = applyGrants(m, l, s.runId, pd, before, fcPaid, DailyService.today(), System.currentTimeMillis());
        runs.store().saveLedger(u, r.changed);
        runs.plugin().getDataStore().flushMutation(u);
        log().info("[P1 run] " + s.runId + " short settle " + u + " rows+" + r.changed.size()
                + (r.fresh ? (r.pays ? " reward" : " no-reward") : " (repeat)")
                + " day=" + r.rewardedAfter + "/" + dailyCap(m));
        Player p = Bukkit.getPlayer(u);
        if (p != null && p.isOnline()) {
            if (r.fresh && r.pays) {
                p.sendMessage(P() + "§e短征通关 §7· " + EmberShortRules.rewardLine()
                        + (r.firstClearPaidNow ? " §a（含生涯首通包）" : "")
                        + " §7· " + EmberShortRules.dayLine(r.rewardedAfter, dailyCap(m)));
            } else if (r.fresh) {
                p.sendMessage(P() + "§e短征通关（无奖）§7· " + EmberShortRules.dayLine(before, dailyCap(m))
                        + " · 0 点重置有奖次数");
            }
            runs.deliver(p);
        }
    }

    String enteringLine(Player p, EmberRunMaps.MapDef m, int cost) {
        PlayerData d = runs.dataOf(p.getUniqueId());
        int n = rewardedToday(d, m);
        int cap = dailyCap(m);
        boolean pays = EmberShortRules.paysReward(n, cap);
        return "§e短征 " + m.key.toUpperCase(Locale.ROOT) + " " + m.name
                + " §7正在创建实例……（已预留体力 " + cost
                + " · " + (pays ? "§a今日有奖 " + n + "/" + cap + "§7" : "§e今日有奖已满，本局无结算包§7") + "）";
    }
}

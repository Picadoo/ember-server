package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * D391–D428 短征：进本门闩 + S40–S58 结算发放钩；D401 有奖结算挂钩可选周目标 {@code short}；D409 W1a 有奖结算进仓/工坊半句。
 * 地图 / MM / TrMenu 另号；本号保证 {@code p1 enter sx01..sx19} 路由与发放可单测。
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
        String mapKey = m != null && m.key != null ? m.key : EmberShortRules.KEY;
        final boolean fresh = l.get(runId, EmberShortRules.gClearCoin(mapKey)) == null
                && l.get(runId, EmberShortRules.gPractice(mapKey)) == null;
        int cap = m != null && m.shortDailyCap > 0 ? m.shortDailyCap : EmberShortRules.DAILY_CAP;
        String claim = m != null && m.shortClaim != null && !m.shortClaim.isEmpty() ? m.shortClaim : EmberShortRules.CLAIM;
        String ver = m != null && m.contentVersion != null ? m.contentVersion : "v1";
        List<EmberRunRules.Grant> grants = fresh
                ? EmberShortRules.settleGrants(mapKey, rewardedBefore, cap, firstClearPaid)
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

    /** D401: only a fresh rewarded clear counts toward optional weekly goal {@code short} (4+ / fail → false). */
    public static boolean countsTowardShortGoal(SettleResult r) {
        return r != null && r.fresh && r.pays;
    }

    void settle(EmberRunSession s, EmberRunMaps.MapDef m, UUID u) {
        PlayerData pd = runs.dataOf(u);
        EmberRunRules.Ledger l = runs.store().ledger(u);
        int before = rewardedToday(pd, m);
        boolean fcPaid = EmberFirstClear.paid(pd, m.key, m.contentVersion);
        SettleResult r = applyGrants(m, l, s.runId, pd, before, fcPaid, DailyService.today(), System.currentTimeMillis());
        runs.store().saveLedger(u, r.changed);
        runs.plugin().getDataStore().flushMutation(u);
        // D401: optional weekly goal short — Q07 gate inside addGoal; 4+/fail (pays=false) skipped
        if (countsTowardShortGoal(r) && runs.season() != null) {
            runs.season().addGoal(u, pd, "short", 1);
        }
        log().info("[P1 run] " + s.runId + " short settle " + u + " rows+" + r.changed.size()
                + (r.fresh ? (r.pays ? " reward" : " no-reward") : " (repeat)")
                + " day=" + r.rewardedAfter + "/" + dailyCap(m));
        Player p = Bukkit.getPlayer(u);
        if (p != null && p.isOnline()) {
            if (r.fresh && r.pays) {
                p.sendMessage(P() + "§e短征通关 §7· " + EmberShortRules.rewardLine(m.key)
                        + (r.firstClearPaidNow ? " §a（含生涯首通包）" : "")
                        + " §7· " + EmberShortRules.dayLine(r.rewardedAfter, dailyCap(m)));
                try { p.sendActionBar("§e短征 · 日奖 " + r.rewardedAfter + "/" + dailyCap(m)); } catch (Throwable ignored) { }
                // D878 day-reward next (FC branch may add more)
                int left = dailyCap(m) - r.rewardedAfter;
                if (left > 0) {
                    town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P() + "§7短征下一环：",
                            new String[]{"[再进·" + m.key.toUpperCase() + "]", "/corerpg p1 enter " + m.key,
                                    "本日还可领 " + left + " 次", "GREEN"},
                            new String[]{"[工坊]", "/corerpg p1 forge", "花短征料", "GOLD"},
                            new String[]{"[短征路径]", "/corerpg p1 shortpath", "改追首通/日奖", "AQUA"});
                } else {
                    town.sunshine.corerpg.ConfirmTokens.sendButtons(p, P() + "§7本日短征日奖已满：",
                            new String[]{"[工坊]", "/corerpg p1 forge", "花材料", "GOLD"},
                            new String[]{"[冒险页]", "/ember_p1_adventure", "换内容", "GREEN"},
                            new String[]{"[短征路径]", "/corerpg p1 shortpath", "改追", "GRAY"});
                }
                if (r.firstClearPaidNow) { // D697/D743 FC claim → next
                    p.sendMessage(P() + "§a生涯首通包已入账 §7· 材料进仓");
                    try { p.sendActionBar("§a首通包已入账"); } catch (Throwable ignored) { }
                    int path = EmberShortPath.get(pd);
                    if (path == EmberShortPath.NONE) path = EmberShortPath.FC;
                    EmberRunMaps.MapDef next = EmberShortPath.resolve(runs, pd, path == EmberShortPath.DAY ? EmberShortPath.FC : path);
                    if (next != null && !next.key.equals(m.key)) {
                        String n = next.name == null || next.name.isEmpty() ? next.key : next.name;
                        p.sendMessage(P() + "§a下一首通短征：§f" + next.key.toUpperCase() + " " + n);
                        town.sunshine.corerpg.ConfirmTokens.sendButton(p, P() + "§7领下一本：",
                                "[进" + next.key.toUpperCase() + "]",
                                "/corerpg p1 enter " + next.key,
                                "下一本未领首通 · " + n);
                    } else if (next == null) {
                        p.sendMessage(P() + "§7短征首通已清 · 可改 /corerpg p1 shortpath day");
                    }
                    p.sendMessage(P() + EmberShortPath.unpaidFcLine(runs, pd)); // D715
                }
                // D409 W1a + D473: vault→forge chase tell + clickable [去工坊] + ActionBar
                String chase = EmberShortRules.spendChaseTell(r.rewardedAfter, dailyCap(m));
                p.sendMessage(P() + chase);
                try { p.sendActionBar(EmberShortRules.spendChaseActionBar(r.rewardedAfter, dailyCap(m))); }
                catch (Throwable ignored) { }
                ConfirmTokens.sendButton(p, P() + "§7下一步：",
                        EmberShortRules.spendChaseButtonLabel(),
                        EmberShortRules.spendChaseButtonCmd(),
                        EmberShortRules.spendChaseButtonHover());
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
        String base = "§e短征 " + m.key.toUpperCase(Locale.ROOT) + " " + m.name
                + " §7正在创建实例……（已预留体力 " + cost
                + " · " + (pays ? "§a今日有奖 " + n + "/" + cap + "§7" : "§e今日有奖已满，本局无结算包§7") + "）";
        // D699: if chasing FC and this map unpaid, or show path next
        int path = EmberShortPath.get(d);
        if (path == EmberShortPath.FC || path == EmberShortPath.LATE) {
            boolean unpaid = EmberShortPath.unpaidFc(d, m);
            int left = EmberShortPath.countUnpaidFc(runs, d); // D720
            if (unpaid) base += " §a· 本局首通追 §8· 余 " + left;
            else {
                EmberRunMaps.MapDef next = EmberShortPath.resolve(runs, d, EmberShortPath.FC);
                if (next != null) base += " §8· 下一首通 " + next.key.toUpperCase(Locale.ROOT) + " · 余 " + left;
            }
        }
        return base;
    }
}

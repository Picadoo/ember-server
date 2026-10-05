package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.PlayerData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * D230 / ARCH S3 step 1: rush / echo / outpost mode logic extracted from {@link EmberRunService}
 * with <b>no behaviour change</b>. Owns weekly claim counters, director stage hooks, settle grants,
 * {@code /corerpg p1 rush} menu, and text helpers. {@link EmberRunService} keeps thin delegates so
 * {@link EmberRunDirector}, the cmd router, and PAPI stay stable.
 * <p>{@link #applyGrants} is Bukkit-free so unit tests can pin ledger rows / claim bumps / badges
 * against the bundled {@code ember-v1-runs.yml} amounts (fixed week key, no RNG).
 */
public final class EmberRushService {

    static final String C_RUSH = "p4_rush";          // period = week key: rush attempts this week (stats only since D160)
    static final String C_RUSH_CLAIM = "p4_rush_claim"; // D160: period = week key: weekly rush REWARD claims (settled clears that paid)
    static final int RUSH_WEEKLY = 1;                 // D160: weekly reward claims (attempts are unlimited and free)

    private final EmberRunService runs;

    EmberRushService(EmberRunService runs) {
        this.runs = runs;
    }

    private Logger log() { return runs.log(); }
    private static String P() { return EmberRunService.P; }

    static String weekKey() {
        return EmberRunRules.rotationWeekKey(java.time.LocalDate.now(town.sunshine.corerpg.DailyService.zone()));
    }

    /** D160: reward claims this week; a pre-D160 clear this week (on the weekly time board) counts as the claim. */
    int week(PlayerData d, UUID u) {
        if (d == null) return 0;
        EmberSeason season = runs.season();
        boolean legacy = d.periodCount(C_RUSH, weekKey()) > 0 && season != null && u != null && season.weekRank(u, "time_rush") != null;
        return EmberRunRules.rushClaims(d.periodCount(C_RUSH_CLAIM, weekKey()), legacy);
    }

    /** D174 stage 2b: claims this week on the entry's own counter (the D144 rush keeps its legacy rule) */
    int week(PlayerData d, UUID u, EmberRunMaps.MapDef m) {
        if (m == null || m.mainRush()) return week(d, u);
        return d == null ? 0 : Math.max(0, d.periodCount(m.rushClaim, weekKey()));
    }

    static String ruleText(EmberRunMaps.MapDef m) {
        return m.rushWeekly <= 1 ? "每周首通领奖，失败可无限重试" : "每周前 " + m.rushWeekly + " 次通关领奖" + (m.mainRush() ? "" : "（同类共用）") + "，失败可无限重试";
    }

    static String rewardText(EmberRunMaps.MapDef m) {
        StringBuilder b = new StringBuilder();
        if (m.rushMarks > 0) b.append(" T").append(m.rushMarkTier).append(" 印记 +").append(m.rushMarks);
        if (m.rushSig > 0) b.append(b.length() == 0 ? "" : " ·").append(" ").append(m.chainKeys.size() > 1 ? "每张图" : m.chainKeys.get(0).toUpperCase(Locale.ROOT)).append("首领徽记 +").append(m.rushSig);
        if (m.rushBadges > 0) b.append(b.length() == 0 ? "" : " ·").append(" 余烬徽 +").append(m.rushBadges);
        if (!m.rushTitle.isEmpty()) b.append(b.length() == 0 ? "" : " ·").append(" 首通称号");
        return b.toString();
    }

    static String chainText(EmberRunMaps.MapDef m) {
        StringBuilder b = new StringBuilder();
        for (EmberRunMaps.Boss x : m.chain) b.append(b.length() == 0 ? "" : " → ").append(x.name);
        return b.toString();
    }

    void onStart(EmberRunSession s, EmberRunMaps.MapDef m, EmberRunMaps.Boss first) {
        runs.tellRun(s, "§c" + m.rushLabel + " §7· 5 秒后 §f" + first.name + " §7现身（1/" + m.chain.size() + "）· " + ruleText(m) + "；领完后是练习（无奖励）");
    }

    /** D144: chain boss {@code done} (0-based) fell; heal everyone standing, say who is next. */
    void onStage(EmberRunDirector d, int done, EmberRunMaps.Boss was, EmberRunMaps.Boss next, long secs) {
        EmberRunSession s = d.s;
        int healed = 0;
        for (UUID u : s.committed) {
            Player p = Bukkit.getPlayer(u);
            if (p == null || !p.isOnline() || p.isDead() || s.died.contains(u) || !p.getWorld().equals(d.w)) continue;
            double max = EmberHeal.maxHp(p);
            p.setHealth(Math.max(1.0, Math.min(max, p.getHealth() + max * d.def.rushHeal)));
            EmberHeal.rebase(p); // sanctioned HP change (B2.144 guard)
            p.setFireTicks(0);
            healed++;
        }
        runs.tellRun(s, "§a" + was.name + " 倒下 §7（" + (done + 1) + "/" + d.def.chain.size() + "，" + secs + " 秒）· 休息 " + Math.round(d.def.rushBreak)
                + " 秒，站着的人回复 " + Math.round(d.def.rushHeal * 100) + "% 生命 · 下一个：§c" + next.name);
        log().info("[P1 run] " + s.runId + " rush stage " + (done + 1) + " " + was.name + " down after " + secs + " s, healed " + healed);
    }

    /**
     * Bukkit-free core of rush settle: ledger rows + claim counter + badge.
     * Callers that need messaging / season board / deliver wrap this (see {@link #settle}).
     *
     * @param claimsBefore claims already used this week ({@link #week} / periodCount) before this settle
     * @param weekKey      rotation week key (tests pass a fixed seed; live uses {@link #weekKey()})
     */
    public static SettleResult applyGrants(EmberRunMaps.MapDef m, EmberRunRules.Ledger l, String runId,
            PlayerData pd, int claimsBefore, String weekKey, long now) {
        final boolean fresh = l.get(runId, "rush_mark") == null && l.get(runId, "rush_practice") == null && l.get(runId, "rush_paid") == null;
        // D160: only the week's first settled clear pays; later clears this week are practice (time board only)
        // D174 stage 2b: echo / outpost entries pay their first `weekly` clears on their own counter
        final boolean pays = fresh ? EmberRunRules.rushPaysReward(claimsBefore, m.rushWeekly) : l.get(runId, "rush_mark") != null || l.get(runId, "rush_paid") != null;
        List<EmberRunRules.Row> changed = new ArrayList<EmberRunRules.Row>();
        boolean[] created = new boolean[1];
        if (fresh && !pays) { // D160 practice: one marker row so a duplicate settle of this run stays a no-op
            EmberRunRules.Row r = l.record(runId, "rush_practice", new EmberRunRules.Grant("rush_practice", EmberRunRules.Kind.MARK, "3", 0, null).encode(),
                    EmberRunRules.ST_DELIVERED, now, created);
            if (created[0]) changed.add(r);
        }
        if (pays && m.rushMarks > 0) {
            EmberRunRules.Row r = l.record(runId, "rush_mark", new EmberRunRules.Grant("rush_mark", EmberRunRules.Kind.MARK, String.valueOf(m.rushMarkTier), m.rushMarks, null).encode(),
                    EmberRunRules.ST_PENDING, now, created);
            if (created[0]) changed.add(r);
        }
        if (pays && !m.mainRush()) { // D174 stage 2b: claim marker (idempotent re-settle) + insignia of each chain map (ledger, delivered by deliver())
            EmberRunRules.Row r = l.record(runId, "rush_paid", new EmberRunRules.Grant("rush_paid", EmberRunRules.Kind.MARK, String.valueOf(m.rushMarkTier), 0, null).encode(),
                    EmberRunRules.ST_DELIVERED, now, created);
            if (created[0]) changed.add(r);
            if (m.rushSig > 0) for (String ck : m.chainKeys) {
                if (!EmberSignature.hasMap(ck)) continue;
                EmberRunRules.Row g = l.record(runId, "rush_sig_" + ck, new EmberRunRules.Grant("rush_sig_" + ck, EmberRunRules.Kind.SIGMARK, ck, m.rushSig, null).encode(),
                        EmberRunRules.ST_PENDING, now, created);
                if (created[0]) changed.add(g);
            }
        }
        boolean badgeOk = false;
        int claimsAfter = claimsBefore;
        if (fresh && pays && pd != null) {
            pd.addPeriodCount(m.mainRush() ? C_RUSH_CLAIM : m.rushClaim, weekKey, 1);
            claimsAfter = claimsBefore + 1;
            if (m.rushBadges > 0) {
                badgeOk = EmberEconomy.grantBadge(pd, EmberEconomy.sourceForRushMode(m.rushMode), m.rushBadges);
            }
        }
        return new SettleResult(fresh, pays, changed, badgeOk, claimsAfter, m.rushBadges > 0 && fresh && pays);
    }

    /** Outcome of {@link #applyGrants} (Bukkit-free). */
    public static final class SettleResult {
        public final boolean fresh, pays;
        public final List<EmberRunRules.Row> changed;
        /** true when grantBadge was called and accepted; false when refused or not attempted */
        public final boolean badgeGranted;
        /** true when this settle attempted a badge grant (fresh+pays+rushBadges&gt;0) */
        public final boolean badgeAttempted;
        public final int claimsAfter;
        SettleResult(boolean fresh, boolean pays, List<EmberRunRules.Row> changed, boolean badgeGranted, int claimsAfter, boolean badgeAttempted) {
            this.fresh = fresh; this.pays = pays; this.changed = changed;
            this.badgeGranted = badgeGranted; this.claimsAfter = claimsAfter; this.badgeAttempted = badgeAttempted;
        }
    }

    /** D144: the rush settlement — T3 marks (ledger), 余烬徽, the first-clear title, the weekly time board. */
    void settle(EmberRunSession s, EmberRunMaps.MapDef m, UUID u) {
        PlayerData pd = runs.dataOf(u);
        EmberRunRules.Ledger l = runs.store().ledger(u);
        int claimsBefore = week(pd, u, m);
        SettleResult r = applyGrants(m, l, s.runId, pd, claimsBefore, weekKey(), System.currentTimeMillis());
        Player p = Bukkit.getPlayer(u);
        long t0 = s.fightStart > 0 ? s.fightStart : s.created;
        int secs = t0 > 0 ? (int) Math.max(1, (System.currentTimeMillis() - t0) / 1000L) : 0;
        if (r.fresh && r.pays) {
            if (r.badgeAttempted && !r.badgeGranted)
                log().warning("[P1 run] economy grantBadge refused " + m.rushMode + " " + m.rushBadges + " for " + u);
            EmberCosmetics cosmetics = runs.cosmetics();
            if (cosmetics != null && m.mainRush()) cosmetics.onRaidClear(p, pd, m.key); // first clear → the 连战不息 title (counts clears)
        }
        EmberSeason season = runs.season();
        if (r.fresh && m.mainRush() && season != null && secs > 0) season.onRush(u, Bukkit.getOfflinePlayer(u).getName(), secs); // D160: practice clears count on the time board too
        runs.store().saveLedger(u, r.changed);
        runs.plugin().getDataStore().flushMutation(u);
        log().info("[P1 run] " + s.runId + " rush settle " + u + " rows+" + r.changed.size() + " secs=" + secs + (r.fresh ? (r.pays ? " claim" : " practice") : " (repeat event)"));
        if (p != null && p.isOnline()) {
            String t = (secs / 60) + " 分 " + String.format(Locale.ROOT, "%02d", secs % 60) + " 秒";
            if (r.fresh && r.pays && m.mainRush()) p.sendMessage(P() + "§c余烬连战通关 §7· 用时 §f" + t
                    + " §7· 本周奖励：T3 印记 +" + m.rushMarks + " · 余烬徽 +" + m.rushBadges + "（共 " + EmberSeason.badges(pd) + "）· 之后本周再打是练习（无奖励）· 本周连战榜看 /corerpg p1 rush");
            else if (r.fresh && m.mainRush()) p.sendMessage(P() + "§c余烬连战通关（练习）§7· 用时 §f" + t + " §7· 本周奖励已经领过，这局不发奖励；成绩照样计入本周最快榜 · 周一 0 点重置");
            else if (r.fresh && r.pays) p.sendMessage(P() + "§c" + m.rushLabel + "通关 §7· 用时 §f" + t + " §7· 奖励：" + rewardText(m)
                    + " §7· 本周已领 " + r.claimsAfter + "/" + m.rushWeekly + "（周一 0 点重置）");
            else if (r.fresh) p.sendMessage(P() + "§c" + m.rushLabel + "通关（练习）§7· 用时 §f" + t + " §7· 本周 " + m.rushWeekly + " 次奖励已领完，这局不发奖励 · 周一 0 点重置");
            runs.deliver(p);
        }
    }

    /** D144 /corerpg p1 rush [go] — the rule, this week's entry, the weekly fastest board; go = enter. */
    boolean cmd(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        EmberRunMaps maps = runs.maps();
        EmberRunMaps.MapDef m = maps.rush.isEmpty() ? null : maps.rush.values().iterator().next();
        if (args.length >= 3 && maps.rush.containsKey(args[2].toLowerCase(Locale.ROOT))) { // D174 stage 2b: /corerpg p1 rush <entry> [go]
            m = maps.rush.get(args[2].toLowerCase(Locale.ROOT));
            String[] a = new String[args.length - 1];
            a[0] = args[0]; a[1] = args[1];
            System.arraycopy(args, 3, a, 2, args.length - 3);
            args = a;
        }
        if (m == null) { p.sendMessage(P() + "余烬连战未配置。"); return true; }
        if (args.length >= 3 && ("go".equalsIgnoreCase(args[2]) || "enter".equalsIgnoreCase(args[2]))) return runs.tryEnter(p, m.key);
        PlayerData d = runs.dataOf(p.getUniqueId());
        if (!m.mainRush()) { // D174 stage 2b 首领残响 / 连战·前哨: the rule + this week's claims (no time board)
            p.sendMessage(P() + "§c" + m.rushLabel + " §7— " + chainText(m) + (m.chain.size() > 1 ? "，同一个大厅连打" : "，单独在大厅里打") + "（招式和各自主线图一样）");
            p.sendMessage(P() + "§7首领生命 ×" + fmt2(m.rushHp) + "、伤害 ×" + fmt2(m.rushDmg) + "（组队另按人数加生命）· 1～" + maps.partyMax(m) + " 人 · 不耗体力 · 倒下观战，没有复活");
            p.sendMessage(P() + "§e" + ruleText(m) + " §7· 奖励：" + rewardText(m) + " · 不给余烬币、装备、碎片，也不算每日委托");
            if (!runs.progressFlag(d, m.requires)) { p.sendMessage(P() + "§c需本人首通 " + m.requires.toUpperCase(Locale.ROOT)); return true; }
            int used = week(d, p.getUniqueId(), m);
            p.sendMessage(P() + (EmberRunRules.rushPaysReward(used, m.rushWeekly) ? "§a本周已领 " : "§8本周已领 ") + used + "/" + m.rushWeekly);
            ConfirmTokens.sendButtons(p, P(), new String[]{EmberRunRules.rushPaysReward(used, m.rushWeekly) ? "[开始]" : "[练习一局]",
                    "/corerpg p1 rush " + m.key + " go", "组队时由队长开；失败不扣任何东西，可以无限重试", EmberRunRules.rushPaysReward(used, m.rushWeekly) ? "RED" : "GRAY"});
            return true;
        }
        p.sendMessage(P() + "§c余烬连战 §7— " + chainText(m) + "，同一个大厅连打（招式和各自主线图一样）");
        p.sendMessage(P() + "§7首领生命 ×" + fmt2(m.rushHp) + "、伤害 ×" + fmt2(m.rushDmg) + "（组队另按人数加生命）· 1～" + maps.partyMax(m) + " 人 · 不耗体力"
                + " · 每打倒一个休息 " + Math.round(m.rushBreak) + " 秒、回复 " + Math.round(m.rushHeal * 100) + "% 生命 · 倒下观战，没有复活");
        p.sendMessage(P() + "§e每周首通领奖，失败可无限重试；已领奖后可练习（无奖励）§7 · 失败不扣体力、不算次数");
        p.sendMessage(P() + "§7本周奖励：T3 印记 +" + m.rushMarks + " · 余烬徽 +" + m.rushBadges + " · 第一次通关得称号「" + (EmberCosmetics.byId(m.rushTitle) == null ? m.rushTitle : EmberCosmetics.byId(m.rushTitle).label)
                + "§7」· 不给余烬币、装备、碎片，也不算每日委托 · 本周最快榜（练习局也算成绩；赛季末前 3 名得「赛季疾行者」）");
        if (!runs.progressFlag(d, m.requires)) { p.sendMessage(P() + "§c需本人首通 " + m.requires.toUpperCase(Locale.ROOT)); return true; }
        int used = week(d, p.getUniqueId());
        p.sendMessage(P() + (EmberRunRules.rushPaysReward(used, RUSH_WEEKLY) ? "§a" : "§8") + EmberRunRules.rushWeekText(used, RUSH_WEEKLY));
        EmberSeason season = runs.season();
        if (season != null) {
            List<EmberSeason.Row> rows = season.weekTop("time_rush", 5);
            StringBuilder b = new StringBuilder("§e本周最快§7：");
            if (rows.isEmpty()) b.append("暂无");
            for (int i = 0; i < rows.size(); i++) b.append(i == 0 ? "" : " ｜ ").append("§f").append(i + 1).append(". ").append(rows.get(i).name).append(" §7").append(EmberSeason.rowText("time_rush", rows.get(i)));
            int[] me = season.weekRank(p.getUniqueId(), "time_rush");
            b.append(me != null ? " §8（你：第 " + me[0] + " 名）" : "");
            p.sendMessage(P() + b);
        }
        ConfirmTokens.sendButtons(p, P(), EmberRunRules.rushPaysReward(used, RUSH_WEEKLY)
                ? new String[]{"[开始连战]", "/corerpg p1 rush go", "组队时由队长开；失败不扣任何东西，可以无限重试", "RED"}
                : new String[]{"[练习一局]", "/corerpg p1 rush go", "本周奖励已领：这局无奖励，成绩计入本周最快榜", "GRAY"});
        return true;
    }

    private static String fmt2(double v) { return String.format(Locale.ROOT, "%.2f", v); }
}

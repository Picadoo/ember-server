package town.sunshine.corerpg.p1;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import town.sunshine.corerpg.PlayerData;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * D231 / ARCH S3 step 2: abyss · 余烬层 mode logic extracted from {@link EmberRunService}
 * with <b>no behaviour change</b>. Owns {@code p2_abyss_best}, fee-mark math (C08), floor-best
 * grants, fee spend/refund ledger shapes, {@code /corerpg p1 abyss} menu, and text helpers.
 * {@link EmberRunService} keeps thin delegates so entry/settle/PAPI stay stable; shared
 * {@code enter(...)} still lives there with abyss call-sites.
 * <p>{@link #feeMarks}, {@link #applyFloorGrant}, {@link #applyFeeSpend}, {@link #applyFeeRefund}
 * are Bukkit-free so unit tests can pin fee / floor grants against the bundled
 * {@code ember-v1-runs.yml} amounts (bv58 unchanged).
 */
public final class EmberAbyssService {

    static final String C_ABYSS_BEST = "p2_abyss_best";

    private final EmberRunService runs;

    EmberAbyssService(EmberRunService runs) {
        this.runs = runs;
    }

    private Logger log() { return runs.log(); }
    private static String P() { return EmberRunService.P; }

    // ------------------------------------------------------------------ progress

    public boolean open(PlayerData d) {
        EmberRunMaps maps = runs.maps();
        return maps.challenge != null && !maps.abyss.isEmpty() && runs.progressFlag(d, maps.abyssRequires);
    }

    /** highest fully cleared abyss tier of this character (0 = none) */
    public int best(PlayerData d) {
        return d == null ? 0 : d.periodCount(C_ABYSS_BEST, "all");
    }

    /** highest tier this character may start now (best + 1, capped by the table) */
    public int maxStart(PlayerData d) {
        EmberRunMaps maps = runs.maps();
        return Math.min(maps.abyss.size(), best(d) + 1);
    }

    // ------------------------------------------------------------------ Bukkit-free fee / floor

    /**
     * F-review #5 (D124): T3 marks that would pay this fee (0 = off / not enough surplus above the exchange reserve).
     * Pure arithmetic — callers pass owned T3 count and config {@code fee_mark_coin} / reserve.
     */
    public static int feeMarks(int markT3Owned, int fee, int feeMarkCoin, int reserve) {
        if (fee <= 0 || feeMarkCoin <= 0) return 0;
        int need = (fee + feeMarkCoin - 1) / feeMarkCoin;
        return markT3Owned - reserve >= need ? need : 0;
    }

    /** Live wrapper: owned T3 marks + bundled {@code abyss.fee_mark_coin} + {@link EmberCosmetics#MARK_RESERVE}. */
    int feeMarks(PlayerData d, int fee) {
        EmberRunMaps maps = runs.maps();
        if (d == null) return 0;
        return feeMarks(runs.marks(d, 3), fee, maps.abyssFeeMarkCoin, EmberCosmetics.MARK_RESERVE);
    }

    /**
     * Bukkit-free floor-best grant: bumps {@link #C_ABYSS_BEST} when {@code floor} exceeds the prior best.
     * Idempotent for equal/lower floors (no change).
     */
    public static FloorResult applyFloorGrant(PlayerData pd, int floor) {
        int oldBest = pd == null ? 0 : pd.periodCount(C_ABYSS_BEST, "all");
        boolean newBest = pd != null && floor > 0 && floor > oldBest;
        if (newBest) pd.addPeriodCount(C_ABYSS_BEST, "all", floor - oldBest);
        int bestAfter = pd == null ? 0 : pd.periodCount(C_ABYSS_BEST, "all");
        return new FloorResult(newBest, oldBest, bestAfter);
    }

    /** Outcome of {@link #applyFloorGrant}. */
    public static final class FloorResult {
        public final boolean newBest;
        public final int oldBest;
        public final int bestAfter;
        FloorResult(boolean newBest, int oldBest, int bestAfter) {
            this.newBest = newBest; this.oldBest = oldBest; this.bestAfter = bestAfter;
        }
    }

    /**
     * Bukkit-free C08 segment fee: prefer coin when the balance covers {@code fee}; otherwise surplus T3 marks
     * at {@code feeMarkCoin} 币/枚 (reserve left untouched). Mutates {@code pd} via {@link EmberEconomy}.
     *
     * @return ok=false when neither coin nor marks can pay (nothing spent); feeStored is the coin amount held
     *         on the session for refund (0 when marks paid); ledgerResult is the {@code cost_coin} row payload
     */
    public static FeeSpendResult applyFeeSpend(PlayerData pd, int fee, int feeMarkCoin) {
        return applyFeeSpend(pd, fee, feeMarkCoin, EmberFeePath.NONE);
    }

    /**
     * D511: {@code feePath} {@link EmberFeePath#MARK} tries surplus T3 marks before coin;
     * {@link EmberFeePath#COIN}/{@link EmberFeePath#AUTO}/{@link EmberFeePath#NONE} keep stock (coin when affordable).
     */
    public static FeeSpendResult applyFeeSpend(PlayerData pd, int fee, int feeMarkCoin, int feePath) {
        if (pd == null || fee <= 0) return FeeSpendResult.none();
        int owned = pd.periodCount(EmberEconomy.MARK_COUNTER + "3", "all");
        boolean markFirst = EmberFeePath.preferMark(feePath);
        if (markFirst) {
            int fm = feeMarks(owned, fee, feeMarkCoin, EmberCosmetics.MARK_RESERVE);
            if (fm > 0 && EmberEconomy.spendMark(pd, "C08", 3, fm)) {
                return new FeeSpendResult(true, 0, fm, "mark:3:" + fm);
            }
        }
        // stock / coin-first / mark fallback
        int fm = pd.getCoin() < fee
                ? feeMarks(owned, fee, feeMarkCoin, EmberCosmetics.MARK_RESERVE)
                : 0;
        if (fm > 0 && EmberEconomy.spendMark(pd, "C08", 3, fm)) {
            return new FeeSpendResult(true, 0, fm, "mark:3:" + fm);
        }
        if (!EmberEconomy.spendCoin(pd, "C08", fee)) {
            return FeeSpendResult.none();
        }
        return new FeeSpendResult(true, fee, 0, "coin:" + fee);
    }

    /** Outcome of {@link #applyFeeSpend}. */
    public static final class FeeSpendResult {
        public final boolean ok;
        /** coin amount stored on the session for refund (0 when marks paid) */
        public final int feeStored;
        public final int marksSpent;
        public final String ledgerResult;
        FeeSpendResult(boolean ok, int feeStored, int marksSpent, String ledgerResult) {
            this.ok = ok; this.feeStored = feeStored; this.marksSpent = marksSpent; this.ledgerResult = ledgerResult;
        }
        static FeeSpendResult none() { return new FeeSpendResult(false, 0, 0, null); }
    }

    /**
     * Bukkit-free C08 fee release (returns a spendMark or addCoin). Does not touch ledger status —
     * callers mark {@code cost_coin} RELEASED after this succeeds for a row that was reserved/committed.
     */
    public static FeeRefundResult applyFeeRefund(PlayerData pd, String ledgerResult, int feeCoinStored) {
        if (pd == null) return FeeRefundResult.none();
        EmberRunRules.Grant paid = EmberRunRules.Grant.decode("cost_coin", ledgerResult);
        if (paid != null && paid.kind == EmberRunRules.Kind.MARK) {
            pd.addPeriodCount(EmberEconomy.MARK_COUNTER + paid.id, "all", paid.amount); // econ-ok: C08 abyss fee release
            return new FeeRefundResult(true, 0, paid.amount);
        }
        if (feeCoinStored > 0) {
            pd.addCoin(feeCoinStored);
            return new FeeRefundResult(true, feeCoinStored, 0);
        }
        return FeeRefundResult.none();
    }

    /** Outcome of {@link #applyFeeRefund}. */
    public static final class FeeRefundResult {
        public final boolean refunded;
        public final int coinReturned;
        public final int marksReturned;
        FeeRefundResult(boolean refunded, int coinReturned, int marksReturned) {
            this.refunded = refunded; this.coinReturned = coinReturned; this.marksReturned = marksReturned;
        }
        static FeeRefundResult none() { return new FeeRefundResult(false, 0, 0); }
    }

    // ------------------------------------------------------------------ entry / menu

    /**
     * One abyss segment = one new entry (book §18.3): the seeded map at challenge values × tier factors.
     * Delegates instance create to {@link EmberRunService#enterAbyssSegment}.
     */
    boolean tryEnter(final Player leader, int tier) {
        EmberRunMaps maps = runs.maps();
        if (maps.abyssTier(tier) == null) {
            leader.sendMessage(P() + org.bukkit.ChatColor.RED + "深渊层数 1～" + maps.abyss.size());
            return true;
        }
        long seed = runs.nextSeed();
        EmberRunMaps.MapDef m = maps.abyssMap(seed);
        if (m == null) { leader.sendMessage(P() + "深渊未配置。"); return true; }
        EmberGoalPath.maybeGlance(leader, runs, "abyss"); // D521 weekly goal focus
        return runs.enterAbyssSegment(leader, m.key, tier, seed);
    }

    /** D235: abyss gate wording (Bukkit-free) — same text as the pre-extract {@code enter}. */
    static String lockedText(String name, String requires) {
        return name + " 未开放深渊（需本人首通 " + requires.toUpperCase(Locale.ROOT) + "）";
    }

    static String tierTooHighText(String name, int maxStart, int best) {
        return name + " 深渊最高只能开第 " + maxStart + " 层（先完整通关第 " + best + " 层）";
    }

    static String feeShortText(String name, int fee, int coin, int feeMarkCoin, int reserve) {
        return name + " 余烬币不足（这一层 " + fee + "，当前 " + coin + "）"
                + (feeMarkCoin > 0 ? "，多出来的 T3 印记也不够抵（1 枚抵 " + feeMarkCoin + " 币，留 " + reserve + " 枚）" : "");
    }

    /**
     * D235: one member's abyss entry gate for {@link EmberEntryService#admit} — open (own first clear), tier ≤ best + 1,
     * fee affordable in coin or surplus T3 marks. Appends 0..2 lines in the pre-extract order.
     */
    void entryProblems(Player p, PlayerData d, EmberRunMaps.AbyssTier at, int tier, java.util.List<String> out) {
        EmberRunMaps maps = runs.maps();
        if (!open(d)) out.add(lockedText(p.getName(), maps.abyssRequires));
        else if (tier > maxStart(d)) out.add(tierTooHighText(p.getName(), maxStart(d), best(d)));
        int fee0 = feeFor(p, at.fee); // D142 深渊行者
        if (d.getCoin() < fee0 && feeMarks(d, fee0) == 0)
            out.add(feeShortText(p.getName(), fee0, d.getCoin(), maps.abyssFeeMarkCoin, EmberCosmetics.MARK_RESERVE));
    }

    /** D142 深渊行者: this player's segment fee (honors may discount). */
    int feeFor(Player p, int fee) {
        EmberGrowthService g = EmberGrowthService.get();
        return g == null ? fee : g.abyssFee(p, fee);
    }

    /** /corerpg p1 abyss [层] — without a tier: the table and this character's state; with one: start that segment. */
    boolean cmd(CommandSender s, String[] args) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        PlayerData d = runs.dataOf(p.getUniqueId());
        EmberRunMaps maps = runs.maps();
        if (maps.abyss.isEmpty() || maps.challenge == null) { p.sendMessage(P() + "深渊 · 余烬层未配置。"); return true; }
        if (args.length >= 3) {
            int t;
            try { t = Integer.parseInt(args[2]); } catch (NumberFormatException e) {
                t = "next".equalsIgnoreCase(args[2]) ? maxStart(d) : -1;
            }
            return tryEnter(p, t);
        }
        p.sendMessage(P() + "§5深渊 · 余烬层 §7— 每层 = 随机一张主线图打一局（3 房 + 首领），打赢第 N 层开放第 N+1 层；共 "
                + maps.abyss.size() + " 层封顶");
        p.sendMessage(P() + "§7每层单独确认：" + maps.cost + " 体力 + 层费（余烬币）；没开打就退出（含服务器重启）全额退还；打完首领才结算，失败只丢这一层的花费（每天第一次失败退一半体力，层费不退），不掉装备不降强化");
        if (!open(d)) { p.sendMessage(P() + "§c需本人首通 " + maps.abyssRequires.toUpperCase(Locale.ROOT)); return true; }
        p.sendMessage(P() + "最高通关 第 " + best(d) + " 层 · 可开 1～" + maxStart(d) + " 层 · 余烬币 " + d.getCoin());
        for (EmberRunMaps.AbyssTier t : maps.abyss) p.sendMessage(P() + line(d, t));
        p.sendMessage(P() + "§7开始：冒险页 → 深渊，点要下潜的层");
        return true;
    }

    String line(PlayerData d, EmberRunMaps.AbyssTier t) {
        EmberRunMaps maps = runs.maps();
        String st = t.index <= best(d) ? "§a已通关" : t.index <= maxStart(d) ? "§e可开" : "§8未开放";
        return "§d第 " + t.index + " 层 " + st + " §7· 生命 ×" + String.format(Locale.ROOT, "%.2f", t.hp) + " 伤害 ×"
                + String.format(Locale.ROOT, "%.2f", t.dmg) + " · 掉落成色 " + EmberRunService.qualityLabel(t.quality) + " · 费 " + t.fee + " 币"
                + (t.index == 1 ? " §8（= 挑战版强度：挑战版能过就能下）" : t.index == maps.abyss.size() ? " §8（两件 T3 +10、卓越以上再来）" : "");
    }
}

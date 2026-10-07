package town.sunshine.corerpg;

/**
 * D201 / ARCH S0-5 (docs/design/AUDIT-ember-legacy-reachability-2026-10-05.md §5.2, root cause of LEAK L3):
 * arena match coin had no cap — two accounts could queue 1v1 and one forfeit at once, 25 + 10 coin per match,
 * forever. S0-3 (D199) already closes {@code /corerpg arena} to players while P1 is on; this caps the payout itself
 * so the arena cannot be reopened later with the hole still in it. Pure rules, no Bukkit (unit tested).
 *
 * <ul>
 *   <li>Each player is paid match coin (win, loss or draw) on at most {@code match.daily_coin_matches} matches per
 *       Asia/Shanghai day (period counter {@link #COUNTER}); after that the match still settles points / wins /
 *       losses, only the coin is 0. {@code <= 0} = no cap (legacy behaviour).</li>
 *   <li>The player who forfeits or disconnects gets no participation coin.</li>
 *   <li>A win by the opponent forfeiting / leaving pays win coin only when the match lasted at least
 *       {@code match.forfeit_min_coin_seconds}; otherwise points only.</li>
 * </ul>
 * Points, ranks and the daily box ({@code arena claim}) are unchanged.
 *
 * <p>D279 / ARCH R6: while P1 ({@code EmberMode.active()}) is on, {@link ArenaService} pays <b>0</b> match coin and
 * refuses {@code arena claim} coin — S0-3 already closes the route for players; this is payout-side depth so an OP
 * test match or a future reopen cannot mint unmodelled 余烬币 into the P1 economy.</p>
 */
public final class ArenaCoinRules {

    /** PlayerData period counter name (period = DailyService.today()). */
    public static final String COUNTER = "arena_coin_matches";

    public static final int DEFAULT_DAILY_COIN_MATCHES = 5;
    public static final int DEFAULT_FORFEIT_MIN_COIN_SECONDS = 30;

    private ArenaCoinRules() {}

    /** D279: true → ArenaService must pay 0 match/claim coin (P1 master switch on). */
    public static boolean p1BlocksCoin(boolean p1Active) { return p1Active; }

    /**
     * Coin to pay one participant for one settled match.
     *
     * @param base        configured coin for this outcome (win_coin or participate_coin)
     * @param quitter     this participant forfeited / disconnected
     * @param wonByQuit   this participant won because an opponent forfeited / left
     * @param durationMs  match length so far
     * @param paidToday   matches already paid coin today for this participant
     * @param dailyCap    {@code daily_coin_matches}; {@code <= 0} = no cap
     * @param minQuitSecs {@code forfeit_min_coin_seconds}; {@code <= 0} = no minimum
     */
    public static int coinFor(int base, boolean quitter, boolean wonByQuit, long durationMs,
                              int paidToday, int dailyCap, int minQuitSecs) {
        if (base <= 0) return 0;
        if (quitter) return 0;
        if (wonByQuit && minQuitSecs > 0 && durationMs < minQuitSecs * 1000L) return 0;
        if (dailyCap > 0 && paidToday >= dailyCap) return 0;
        return base;
    }

    /** Remaining coin-paying matches today ({@code -1} = unlimited). */
    public static int remaining(int paidToday, int dailyCap) {
        if (dailyCap <= 0) return -1;
        return Math.max(0, dailyCap - Math.max(0, paidToday));
    }
}

package town.sunshine.corerpg;

/**
 * D281 / AUDIT L4·L8·L10 + monthly join path: S0-3 already refuses {@code /corerpg vip|pass free|claim|monthly}
 * for players while P1 is on, but {@link CashService#processMonthlyLogin} still runs on every join and would
 * mint 余烬币 + stamina for anyone with an active monthly card (admin crystal → DEAD→LEAK). Vip/pass claim
 * remain callable by OP / console. This blocks the <b>payout</b> while {@code EmberMode.active()}.
 */
public final class CashCoinRules {

    private CashCoinRules() {}

    /** true → CashService / ProgressService must not grant legacy vip/pass/monthly coin (or monthly stamina). */
    public static boolean p1BlocksCoin(boolean p1Active) {
        return p1Active;
    }
}

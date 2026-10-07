package town.sunshine.corerpg;

/**
 * D281 / AUDIT L4·L8·L10: S0-3 already refuses {@code /corerpg vip|pass free|claim} for players while P1 is on;
 * OP / console can still call those paths — this blocks the <b>payout</b> while {@code EmberMode.active()}.
 *
 * <p>D282: monthly card login gifts ({@link CashService#processMonthlyLogin}) are <b>not</b> blocked — owner
 * decision「月卡照常发」. Only vip claim / pass free / pass claim use this gate.</p>
 */
public final class CashCoinRules {

    private CashCoinRules() {}

    /** true → vip claim / pass free / pass claim must not grant (monthly login gifts excluded — D282). */
    public static boolean p1BlocksCoin(boolean p1Active) {
        return p1Active;
    }
}

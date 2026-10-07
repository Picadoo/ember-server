package town.sunshine.corerpg;

import org.junit.Test;

import static org.junit.Assert.*;

/** D281: P1 master switch blocks legacy cash/vip/pass/monthly payouts. */
public class CashCoinRulesTest {

    @Test public void p1BlocksCoinWhenModeOn() {
        assertTrue(CashCoinRules.p1BlocksCoin(true));
        assertFalse(CashCoinRules.p1BlocksCoin(false));
    }
}

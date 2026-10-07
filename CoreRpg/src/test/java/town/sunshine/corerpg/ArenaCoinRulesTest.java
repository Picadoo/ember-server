package town.sunshine.corerpg;

import org.junit.Test;

import static org.junit.Assert.*;

/** D201 / ARCH S0-5: arena match coin daily cap + no coin for instant forfeits. */
public class ArenaCoinRulesTest {

    @Test public void normalMatchPaysUntilDailyCap() {
        for (int used = 0; used < 5; used++) {
            assertEquals(25, ArenaCoinRules.coinFor(25, false, false, 60_000L, used, 5, 30));
            assertEquals(10, ArenaCoinRules.coinFor(10, false, false, 60_000L, used, 5, 30));
        }
        assertEquals(0, ArenaCoinRules.coinFor(25, false, false, 60_000L, 5, 5, 30));
        assertEquals(0, ArenaCoinRules.coinFor(10, false, false, 60_000L, 99, 5, 30));
    }

    @Test public void quitterNeverGetsParticipationCoin() {
        assertEquals(0, ArenaCoinRules.coinFor(10, true, false, 0L, 0, 5, 30));
        assertEquals(0, ArenaCoinRules.coinFor(10, true, false, 600_000L, 0, 0, 0));
    }

    @Test public void instantForfeitWinPaysNoCoin() {
        assertEquals(0, ArenaCoinRules.coinFor(25, false, true, 0L, 0, 5, 30));
        assertEquals(0, ArenaCoinRules.coinFor(25, false, true, 29_999L, 0, 5, 30));
        assertEquals(25, ArenaCoinRules.coinFor(25, false, true, 30_000L, 0, 5, 30));
        assertEquals(0, ArenaCoinRules.coinFor(25, false, true, 30_000L, 5, 5, 30)); // cap still applies
    }

    @Test public void zeroConfigMeansLegacyBehaviour() {
        assertEquals(25, ArenaCoinRules.coinFor(25, false, false, 0L, 10_000, 0, 0));
        assertEquals(25, ArenaCoinRules.coinFor(25, false, true, 0L, 10_000, 0, 0));
        assertEquals(0, ArenaCoinRules.coinFor(0, false, false, 60_000L, 0, 5, 30));
    }

    @Test public void forfeitFarmIsBoundedPerDay() {
        // two accounts alternating instant forfeits for a whole day: winner coin 0 every time (duration < min)
        int total = 0, paid = 0;
        for (int i = 0; i < 1000; i++) {
            int c = ArenaCoinRules.coinFor(25, false, true, 1_000L, paid, 5, 30);
            if (c > 0) paid++;
            total += c;
            total += ArenaCoinRules.coinFor(10, true, false, 1_000L, 0, 5, 30);
        }
        assertEquals(0, total);
        // slow-walked 30 s forfeits: still at most cap × win_coin per account per day
        total = 0; paid = 0;
        for (int i = 0; i < 1000; i++) {
            int c = ArenaCoinRules.coinFor(25, false, true, 31_000L, paid, 5, 30);
            if (c > 0) paid++;
            total += c;
        }
        assertEquals(125, total);
    }

    @Test public void remaining() {
        assertEquals(-1, ArenaCoinRules.remaining(3, 0));
        assertEquals(2, ArenaCoinRules.remaining(3, 5));
        assertEquals(0, ArenaCoinRules.remaining(9, 5));
    }

    @Test
    public void p1BlocksCoinWhenModeOn() {
        assertTrue(ArenaCoinRules.p1BlocksCoin(true));
        assertFalse(ArenaCoinRules.p1BlocksCoin(false));
    }
}

package town.sunshine.corerpg;

import org.junit.Test;
import town.sunshine.corerpg.p1.EmberCounters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** D207 (ARCH S1-5 · REG-counter-registry §4-5): a server clock set back must not re-open periodic claims. */
public class PlayerDataClockGuardTest {

    @Test
    public void laterPeriodParsesEachKind() {
        assertTrue(EmberCounters.laterPeriod(EmberCounters.Period.DAY, "2026-10-06", "2026-10-05"));
        assertFalse(EmberCounters.laterPeriod(EmberCounters.Period.DAY, "2026-10-05", "2026-10-06"));
        assertFalse(EmberCounters.laterPeriod(EmberCounters.Period.DAY, "2026-10-05", "2026-10-05"));
        assertTrue(EmberCounters.laterPeriod(EmberCounters.Period.PWEEK, "w100", "w99"));
        assertFalse(EmberCounters.laterPeriod(EmberCounters.Period.PWEEK, "w99", "w100"));
        assertTrue(EmberCounters.laterPeriod(EmberCounters.Period.LWEEK, "2027-W01", "2026-W53"));
        assertTrue(EmberCounters.laterPeriod(EmberCounters.Period.MONTH, "2026-11", "2026-10"));
        // unknown shapes and non-periodic kinds are never guarded
        assertFalse(EmberCounters.laterPeriod(EmberCounters.Period.DAY, "zzz", "2026-10-05"));
        assertFalse(EmberCounters.laterPeriod(EmberCounters.Period.PWEEK, "w12x", "w1"));
        assertFalse(EmberCounters.laterPeriod(EmberCounters.Period.ALL, "b", "a"));
        assertFalse(EmberCounters.laterPeriod(EmberCounters.Period.VER, "v2", "v1"));
    }

    @Test
    public void onlyPeriodicClaimsAreGuarded() {
        assertTrue(EmberCounters.clockGuarded(EmberCounters.lookup("p2_bounty")));
        assertTrue(EmberCounters.clockGuarded(EmberCounters.lookup("p1_on_claim")));
        assertTrue(EmberCounters.clockGuarded(EmberCounters.lookup("p2_raid_EmberQ05R")));
        assertTrue(EmberCounters.clockGuarded(EmberCounters.lookup("p1_sign_mk")));
        assertFalse(EmberCounters.clockGuarded(EmberCounters.lookup("p1_on_min")));      // progress
        assertFalse(EmberCounters.clockGuarded(EmberCounters.lookup("p1_afk_acc_t1")));  // asset carry
        assertFalse(EmberCounters.clockGuarded(EmberCounters.lookup("p1_mark_t1")));     // asset @all
        assertFalse(EmberCounters.clockGuarded(EmberCounters.lookup("p1_fcpay_q01")));   // content version
        assertFalse(EmberCounters.clockGuarded(EmberCounters.lookup("no_such_key")));
    }

    @Test
    public void dailyClaimCannotBeReopenedByRollback() {
        PlayerData d = new PlayerData();
        assertEquals(1, d.addPeriodCount("p2_bounty", "2026-10-06", 1));
        // clock set back one day: yesterday reads as saturated, a write is refused and the later key stays
        assertEquals(PlayerData.CLOCK_SATURATED, d.periodCount("p2_bounty", "2026-10-05"));
        assertEquals(PlayerData.CLOCK_SATURATED, d.addPeriodCount("p2_bounty", "2026-10-05", 1));
        assertEquals(1, d.periodCount("p2_bounty", "2026-10-06"));
        assertFalse(d.getCounters().containsKey("p2_bounty@2026-10-05"));
        // clock moves forward again: normal roll
        assertEquals(0, d.periodCount("p2_bounty", "2026-10-07"));
        assertEquals(1, d.addPeriodCount("p2_bounty", "2026-10-07", 1));
        assertFalse(d.getCounters().containsKey("p2_bounty@2026-10-06"));
    }

    @Test
    public void bitmapClaimReadsAllClaimed() {
        PlayerData d = new PlayerData();
        d.addPeriodCount("p1_on_claim", "2026-10-06", 1);
        int mask = d.periodCount("p1_on_claim", "2026-10-05");
        for (int i = 0; i < 30; i++) assertTrue("bit " + i, (mask & (1 << i)) != 0);
        // "used + n > cap" must not overflow
        assertTrue(mask + 1000000 > 0);
    }

    @Test
    public void weeklyClaimGuardedNumerically() {
        PlayerData d = new PlayerData();
        d.addPeriodCount("p4_rush_claim", "w100", 1);
        assertEquals(PlayerData.CLOCK_SATURATED, d.periodCount("p4_rush_claim", "w99"));
        assertEquals(0, d.periodCount("p4_rush_claim", "w101"));
        d.addPeriodCount("calamity_weekly_first", "2026-W41", 1);
        assertEquals(PlayerData.CLOCK_SATURATED, d.periodCount("calamity_weekly_first", "2026-W40"));
    }

    @Test
    public void unguardedFamiliesKeepOldBehaviour() {
        PlayerData d = new PlayerData();
        d.addPeriodCount("p1_on_min", "2026-10-06", 30);
        assertEquals(0, d.periodCount("p1_on_min", "2026-10-05"));
        assertEquals(5, d.addPeriodCount("p1_on_min", "2026-10-05", 5));      // progress: old roll-and-drop behaviour
        assertFalse(d.getCounters().containsKey("p1_on_min@2026-10-06"));
        d.addPeriodCount("p1_mark_t1", "all", 3);
        assertEquals(3, d.periodCount("p1_mark_t1", "all"));
    }

    @Test
    public void sameAndLaterPeriodWritesUnaffected() {
        PlayerData d = new PlayerData();
        d.addPeriodCount("p1_afk_kill", "2026-10-05", 10);
        assertEquals(15, d.addPeriodCount("p1_afk_kill", "2026-10-05", 5));
        assertEquals(10, d.addPeriodCount("p1_afk_kill", "2026-10-05", -5));
        assertEquals(2, d.addPeriodCount("p1_afk_kill", "2026-10-06", 2));
    }
}

package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.junit.Test;

/** D32 / B2.172: first P1 main-run death of the day refunds the potions used in that run, once per (player, day). */
public class EmberDeathRefundTest {

    @Test
    public void countIsUsedCappedByConfig() {
        assertEquals(3, EmberRunRules.deathRefundCount(3, 5));
        assertEquals(5, EmberRunRules.deathRefundCount(9, 5));
        assertEquals(0, EmberRunRules.deathRefundCount(0, 5));
        assertEquals(0, EmberRunRules.deathRefundCount(4, 0));   // 0 = off
        assertEquals(0, EmberRunRules.deathRefundCount(-2, 5));
    }

    @Test
    public void potionGrantRoundTrip() {
        EmberRunRules.Grant g = EmberRunRules.Grant.decode("potions", "potion:4:q03-abc");
        assertEquals(EmberRunRules.Kind.POTION, g.kind);
        assertEquals(4, g.amount);
        assertEquals("q03-abc", g.id);
        assertEquals("potion:4:q03-abc", g.encode());
        assertEquals(2, EmberRunRules.Grant.decode("potions", "potion:2").amount);
        assertNull(EmberRunRules.Grant.decode("potions", "potion:x"));
    }

    @Test
    public void onceperDayInTheLedgerAcrossReload() {
        EmberRunRules.Ledger l = new EmberRunRules.Ledger();
        String run = EmberRunRules.deathRefundRun("2026-10-02");
        boolean[] c = new boolean[1];
        l.record(run, EmberRunRules.DEATH_REFUND_KEY, "potion:3:r1", EmberRunRules.ST_PENDING, 1, c);
        assertTrue(c[0]);
        // second death the same day: nothing new, first result kept
        EmberRunRules.Row r2 = l.record(run, EmberRunRules.DEATH_REFUND_KEY, "potion:5:r2", EmberRunRules.ST_PENDING, 2, c);
        assertFalse(c[0]);
        assertEquals("potion:3:r1", r2.result);
        // relog / restart = ledger reloaded from its stored map: still there, still not re-created
        l.mark(run, EmberRunRules.DEATH_REFUND_KEY, EmberRunRules.ST_DELIVERED, 3);
        EmberRunRules.Ledger back = EmberRunRules.Ledger.fromMap(l.toMap());
        back.record(run, EmberRunRules.DEATH_REFUND_KEY, "potion:5:r3", EmberRunRules.ST_PENDING, 4, c);
        assertFalse(c[0]);
        assertTrue(back.open().isEmpty());
        // next day is a new row
        back.record(EmberRunRules.deathRefundRun("2026-10-03"), EmberRunRules.DEATH_REFUND_KEY, "potion:1:r4", EmberRunRules.ST_PENDING, 5, c);
        assertTrue(c[0]);
        assertEquals(1, back.open().size());
    }

    @Test
    public void sessionKeepsPotionCountsAcrossRestart() {
        EmberRunSession s = new EmberRunSession();
        s.runId = "q03-x"; s.mapKey = "q03"; s.state = EmberRunSession.FIGHTING;
        UUID u = UUID.randomUUID();
        s.potions.put(u, 4);
        Map<String, Object> m = s.toMap();
        EmberRunSession b = EmberRunSession.fromMap(m);
        assertEquals(Integer.valueOf(4), b.potions.get(u));
    }
}

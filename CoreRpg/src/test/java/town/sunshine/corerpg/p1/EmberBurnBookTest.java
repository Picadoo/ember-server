package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/** C08: 焚烬 refresh, retarget, sixth target, snapshot. */
public class EmberBurnBookTest {

    private static int ticksFor(EmberBurnBook b, String target, long from, long to) {
        int n = 0;
        for (long t = from; t <= to; t += 50) for (EmberBurnBook.Tick k : b.due(t)) if (k.target.equals(target)) n++;
        return n;
    }

    @Test public void fourTicksAtOneSecond() {
        EmberBurnBook b = new EmberBurnBook();
        b.ignite("m", 10, 0);
        assertEquals(0, b.due(999).size());
        List<EmberBurnBook.Tick> t1 = b.due(1000);
        assertEquals(1, t1.size());
        assertEquals(10, t1.get(0).amount, 1e-9);
        assertEquals(3, ticksFor(b, "m", 1050, 10000));
        assertTrue(b.isEmpty());
    }

    @Test public void reigniteOnlyExtendsEnd_noTickReset_noBackfill_snapshotKept() {
        EmberBurnBook b = new EmberBurnBook();
        b.ignite("m", 10, 0);
        assertEquals(2, ticksFor(b, "m", 0, 2400)); // ticks at 1000, 2000
        b.ignite("m", 99, 2500);                    // stronger re-ignite
        EmberBurnBook.Burn burn = b.get("m");
        assertEquals(3000, burn.nextTickAt());      // not reset to 3500
        assertEquals(6500, burn.endAt());
        List<EmberBurnBook.Tick> t = b.due(3000);
        assertEquals(10, t.get(0).amount, 1e-9);   // snapshot fixed at first application
        assertEquals(3, ticksFor(b, "m", 3050, 20000)); // 4000, 5000, 6000 → total 6 ticks, none back-filled
        assertTrue(b.isEmpty());
    }

    @Test public void lagSpikeDoesNotCatchUp() {
        EmberBurnBook b = new EmberBurnBook();
        b.ignite("m", 10, 0);
        assertEquals(1, b.due(3900).size()); // one tick even though three were due
        assertEquals(1, b.due(3950).size()); // next one (nextTickAt was 2000)
        int rest = ticksFor(b, "m", 4000, 9000);
        assertTrue("never more than 4 ticks in total", 2 + rest <= 4);
    }

    @Test public void differentTargetsBurnIndependently() {
        EmberBurnBook b = new EmberBurnBook();
        b.ignite("a", 10, 0);
        b.ignite("b", 20, 500);
        assertEquals(2, b.size());
        assertEquals(1, b.due(1000).size()); // a only
        assertEquals(1, b.due(1500).size()); // b
    }

    @Test public void sixthTargetEndsTheOldest() {
        EmberBurnBook b = new EmberBurnBook();
        for (int i = 0; i < 5; i++) assertNull(b.ignite("t" + i, 10, i * 100));
        b.ignite("t0", 10, 600);                // t0 refreshed → now t1 is the oldest application
        assertEquals("t1", b.ignite("t5", 10, 700));
        assertEquals(5, b.size());
        assertNull(b.get("t1"));
        assertNotNull(b.get("t0"));
        for (int i = 6; i < 30; i++) b.ignite("t" + i, 10, i * 100);
        assertEquals(5, b.size());
    }

    @Test public void badSnapshotIgnored() {
        EmberBurnBook b = new EmberBurnBook();
        b.ignite("a", Double.NaN, 0);
        b.ignite("b", -1, 0);
        b.ignite(null, 10, 0);
        assertTrue(b.isEmpty());
    }
}

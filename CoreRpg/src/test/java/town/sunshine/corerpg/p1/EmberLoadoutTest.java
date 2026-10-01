package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;

/** EquippedLoadout: set rule (§4.1), awakening (§4.3), sustain H, one entry per uid. */
public class EmberLoadoutTest {

    private static final double EPS = 1e-9;
    private final EmberTables t = EmberTables.defaults();

    private static EmberItemData blade(String fam, int tier, int q, int f, int e) {
        return EmberItemData.create(fam, "blade", tier, q, f, e, true, "admin");
    }

    private static EmberItemData charm(String fam, int tier, int q, int f, int e) {
        return EmberItemData.create(fam, "charm", tier, q, f, e, true, "admin");
    }

    @Test public void bookExample1ThroughLoadout() {
        EmberLoadout l = EmberLoadout.compute(t, blade("burst", 2, 0, 0, 6), charm("burst", 2, 0, 0, 6), 30);
        assertEquals(58.6, l.b, EPS);
        assertEquals(163.5, l.h, EPS);
        assertEquals(10.0, l.d, EPS);
        assertEquals(204.375, l.ehp(), EPS);
        assertEquals("burst", l.activeSet);
        assertEquals(2, l.awakening);
    }

    @Test public void bookExample1SustainThroughLoadout() {
        EmberLoadout l = EmberLoadout.compute(t, blade("sustain", 2, 0, 0, 6), charm("sustain", 2, 0, 0, 6), 30);
        assertEquals(183.12, l.h, EPS);
        assertEquals(228.9, l.ehp(), EPS);
    }

    @Test public void bookExample2SustainThroughLoadout() {
        EmberLoadout l = EmberLoadout.compute(t, blade("sustain", 3, 3, 3, 10), charm("sustain", 3, 3, 3, 10), 60);
        assertEquals(134.6, l.b, EPS);
        assertEquals(407.344, l.h, EPS);
        assertEquals(549.9144, l.ehp(), 1e-6);
        assertEquals(3, l.awakening);
    }

    @Test public void mixedFamiliesNoSetButKeepBaseStats() {
        EmberLoadout l = EmberLoadout.compute(t, blade("scorch", 3, 0, 0, 0), charm("sustain", 3, 0, 0, 0), 10);
        assertEquals("none", l.activeSet);
        assertEquals(0, l.awakening);
        assertEquals(70.0, l.b, EPS);
        assertEquals(185.0, l.h, EPS); // no ×1.12 without the set
        assertEquals(14.0, l.d, EPS);
    }

    @Test public void t0NeverFormsASet() {
        EmberLoadout l = EmberLoadout.compute(t, blade("none", 0, 0, 0, 0), charm("none", 0, 0, 0, 0), 10);
        assertEquals("none", l.activeSet);
    }

    @Test public void awakeningUsesLowerTierAndLowerEnhance() {
        // C03: T1/T3 mixed → I; +5/+6 → not II; +8/+9 at T3 → II not III
        assertEquals(1, EmberLoadout.compute(t, blade("burst", 1, 0, 0, 10), charm("burst", 3, 0, 0, 10), 10).awakening);
        assertEquals(1, EmberLoadout.compute(t, blade("burst", 2, 0, 0, 5), charm("burst", 2, 0, 0, 6), 10).awakening);
        assertEquals(2, EmberLoadout.compute(t, blade("burst", 3, 0, 0, 8), charm("burst", 3, 0, 0, 9), 10).awakening);
        assertEquals(3, EmberLoadout.compute(t, blade("burst", 3, 0, 0, 9), charm("burst", 3, 0, 0, 9), 10).awakening);
        assertEquals(2, EmberLoadout.compute(t, blade("burst", 2, 0, 0, 10), charm("burst", 3, 0, 0, 10), 10).awakening);
    }

    @Test public void missingPieces() {
        EmberLoadout none = EmberLoadout.compute(t, null, null, 30);
        assertEquals(4.0, none.b, EPS);      // level part only
        assertEquals(40.0, none.h, EPS);     // 20 + (30-10)
        assertEquals(1.0, none.m, EPS);
        EmberLoadout bladeOnly = EmberLoadout.compute(t, blade("scorch", 1, 0, 0, 0), null, 10);
        assertEquals(24.0, bladeOnly.b, EPS);
        assertEquals("none", bladeOnly.activeSet);
    }

    @Test public void wrongSlotsAreIgnored() {
        // a charm in the blade position and a blade as "charm" both count as nothing
        EmberLoadout l = EmberLoadout.compute(t, charm("burst", 3, 0, 0, 0), blade("burst", 3, 0, 0, 0), 10);
        assertNull(l.blade);
        assertNull(l.charm);
        assertEquals(1.0, l.b, EPS);
    }

    @Test public void sameUidCannotFillTwoSlots() {
        EmberItemData b = blade("burst", 2, 0, 0, 0);
        EmberItemData fake = new EmberItemData(b.uid, "ember_v1_burst_charm_t2", "burst", "charm", 2, 0, 0, 0, 0, true, "admin", 1, 0);
        EmberLoadout l = EmberLoadout.compute(t, b, fake, 10);
        assertNull(l.charm);
        assertEquals("none", l.activeSet);
    }
}

package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.p1.encounter.RevivePoint;

import org.junit.Test;

import static org.junit.Assert.*;

/** D236: D106 revive-point why strings stay byte-identical to the former literals. */
public class RevivePointTest {

    @Test public void whyStringsMatchFormerLiterals_D106() {
        assertEquals("新房间开打", RevivePoint.ROOM_OPEN.why);
        assertEquals("首领现身", RevivePoint.BOSS_SPAWN.why);
        assertEquals("首领进入半血", RevivePoint.HALF_HP.why);
        assertEquals("首领半血转阶段", RevivePoint.ADDS_PHASE.why);
        assertEquals("最后阶段额外复活", RevivePoint.LAST_PHASE.why);
    }

    @Test public void byWhyRoundTrip() {
        for (RevivePoint p : RevivePoint.values()) {
            assertSame(p, RevivePoint.byWhy(p.why));
        }
        assertNull(RevivePoint.byWhy(null));
        assertNull(RevivePoint.byWhy("unknown"));
    }

    @Test public void fivePointsCoverRaidLifecycle() {
        assertEquals(5, RevivePoint.values().length);
    }
}

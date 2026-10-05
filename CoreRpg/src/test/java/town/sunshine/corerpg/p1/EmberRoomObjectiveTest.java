package town.sunshine.corerpg.p1;

import town.sunshine.corerpg.p1.encounter.EmberBossMove;
import town.sunshine.corerpg.p1.encounter.EmberRoomObjective;
import town.sunshine.corerpg.p1.encounter.RoomObjective;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * D239: pins RoomObjective adapter family labels + D179/D191 helpers moved from EmberRunDirector
 * (same numbers EmberRunRulesTest already asserts via Director thin delegates).
 */
public class EmberRoomObjectiveTest {

    @Test public void familyOfMapsKnownKinds() {
        assertEquals("clear", EmberRoomObjective.familyOf(null));
        assertEquals("clear", EmberRoomObjective.familyOf("clear"));
        assertEquals("clear", EmberRoomObjective.familyOf("timed"));
        assertEquals("hold", EmberRoomObjective.familyOf("hold"));
        assertEquals("escort", EmberRoomObjective.familyOf("escort"));
        assertEquals("chain", EmberRoomObjective.familyOf("chain"));
        assertEquals("event", EmberRoomObjective.familyOf("breach"));
        assertEquals("event", EmberRoomObjective.familyOf("crystal"));
        assertEquals("event", EmberRoomObjective.familyOf("beacon"));
        assertEquals("event", EmberRoomObjective.familyOf("relay"));
        assertEquals("event", EmberRoomObjective.familyOf("unscathed"));
        assertEquals("event", EmberRoomObjective.familyOf("weird"));
    }

    @Test public void ofSetsIdFamilyAndCompleteFlag() {
        RoomObjective o = EmberRoomObjective.of("breach");
        assertEquals("breach", o.id());
        assertEquals("event", o.family());
        assertFalse(o.complete());
        EmberRoomObjective live = EmberRoomObjective.of("chain");
        live.markComplete();
        assertTrue(live.complete());
        live.reset();
        assertFalse(live.complete());
    }

    @Test public void holdAndRelayHelpers_D179() {
        assertTrue(EmberRoomObjective.holdCounts(true, true));
        assertFalse(EmberRoomObjective.holdCounts(true, false));
        assertFalse(EmberRoomObjective.holdCounts(false, true));
        assertEquals(2, EmberRoomObjective.relayAdvance(1, 1, 3));
        assertEquals(1, EmberRoomObjective.relayAdvance(1, 0, 3)); // wrong press
        assertEquals(1, EmberRoomObjective.relayAdvance(1, 1, 1)); // at end
        assertFalse(EmberRoomObjective.beaconIsTreasureExtra());
        assertFalse(EmberRoomObjective.beaconBlocksRoomClear());
    }

    @Test public void breachHelpers_D191() {
        assertEquals(4.25, EmberRoomObjective.breachStep(5.0, 5.0, 0.15), 1e-9);
        assertEquals(5.0, EmberRoomObjective.breachStep(5.0, -1.0, 0.15), 1e-9);
        assertEquals(0.0, EmberRoomObjective.breachStep(0.5, 100.0, 0.15), 1e-9);
        assertEquals(5.05, EmberRoomObjective.breachGrow(4.25, 0.8, 7.0), 1e-9);
        assertEquals(7.0, EmberRoomObjective.breachGrow(6.8, 0.8, 7.0), 1e-9);
        assertTrue(EmberRoomObjective.breachCollapsed(1.5, 1.5));
        assertFalse(EmberRoomObjective.breachCollapsed(1.51, 1.5));
        assertTrue(EmberRoomObjective.breachInside(3.0, 4.0, 5.0));
        assertFalse(EmberRoomObjective.breachInside(3.0, 4.1, 5.0));
    }

    @Test public void chainAndUnscathedHelpers_D191() {
        assertEquals(1, EmberRoomObjective.chainNext(0, 0, 1000, 2.0));
        assertEquals(2, EmberRoomObjective.chainNext(1, 500, 1000, 2.0));
        assertEquals(1, EmberRoomObjective.chainNext(3, 500, 3000, 2.0)); // gap exceeded
        assertEquals(3, EmberRoomObjective.chainNeedFor(5, 3));
        assertEquals(1, EmberRoomObjective.chainNeedFor(0, 10));
        assertEquals(5, EmberRoomObjective.unscathedBudget(5, 2, 1));
        assertEquals(9, EmberRoomObjective.unscathedBudget(5, 2, 3)); // 5 + 2*2
        assertTrue(EmberRoomObjective.unscathedOk(3, 5));
        assertFalse(EmberRoomObjective.unscathedOk(6, 5));
        assertFalse(EmberRoomObjective.unscathedOk(0, -1));
    }

    @Test public void directorDelegatesMatchAdapter() {
        // thin-delegate contract: EmberRunDirector still compiles for older tests
        assertEquals(EmberRoomObjective.breachStep(5, 1, 0.15), EmberRunDirector.breachStep(5, 1, 0.15), 0);
        assertEquals(EmberRoomObjective.chainNext(2, 100, 200, 1.0), EmberRunDirector.chainNext(2, 100, 200, 1.0));
        assertEquals(EmberBossMove.dueSkill(new long[]{5, 1}, 3), EmberRunDirector.dueSkill(new long[]{5, 1}, 3));
    }
}

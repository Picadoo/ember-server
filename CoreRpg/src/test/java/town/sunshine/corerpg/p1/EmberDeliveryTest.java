package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/** D162: pure parts of the delivery / fault-injection plumbing */
public class EmberDeliveryTest {

    @Test
    public void faultHooksAreInertOutsideTheTestEnv() {
        if (EmberFaults.enabled()) return; // only meaningful without CORERPG_TEST_FAULTS=1
        UUID u = UUID.randomUUID();
        assertFalse(EmberFaults.arm(u, EmberFaults.AFTER_COMMIT));
        assertFalse(EmberFaults.fire(u, EmberFaults.AFTER_COMMIT));
        assertNull(EmberFaults.armedFor(u));
    }

    @Test
    public void markersAreDistinctPerRowAndRequest() {
        assertNotEquals(EmberDelivery.marker(1), EmberDelivery.marker(2));
        assertEquals(EmberDelivery.paidMarker("enh:abc:3"), EmberDelivery.paidMarker("enh:abc:3"));
        assertNotEquals(EmberDelivery.paidMarker("enh:abc:3"), EmberDelivery.paidMarker("enh:abc:4"));
        // counters are name@period and addPeriodCount drops other periods of the SAME name → one name per row
        assertFalse(EmberDelivery.marker(7).contains("@"));
    }

    @Test
    public void holdIdIsTheRefundPrefixOfTheRequest() {
        assertEquals("refund:enh:abc:3", EmberItemStore.holdId("enh:abc:3"));
        // reconcileHolds joins cr_p1_txn on SUBSTRING(request_id, 8): the prefix is exactly 7 chars
        assertEquals(7, "refund:".length());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 120; i++) sb.append('x');
        assertEquals(96, EmberItemStore.holdId(sb.toString()).length());
    }

    @Test
    public void assetGuardFreezeThaw() {
        UUID u = UUID.randomUUID();
        assertFalse(EmberAssetGuard.frozen(u));
        EmberAssetGuard.freeze(u);
        assertTrue(EmberAssetGuard.frozen(u));
        EmberAssetGuard.thaw(u);
        assertFalse(EmberAssetGuard.frozen(u));
        EmberAssetGuard.saveStreak(3);
        assertTrue(EmberAssetGuard.paused());
        EmberAssetGuard.saveStreak(0);
        assertFalse(EmberAssetGuard.paused());
    }
}

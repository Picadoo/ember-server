package town.sunshine.corerpg.p1;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** D205 (ARCH S1 · REG §4-3): first-clear fact @all + package paid @ver; a version bump never rolls progress back. */
public class EmberFirstClearTest {

    @Test
    public void freshCharacterHasNothing() {
        PlayerData d = new PlayerData();
        assertFalse(EmberFirstClear.fact(d, "q01"));
        assertFalse(EmberFirstClear.paid(d, "q01", "v1"));
    }

    @Test
    public void recordSetsBothAndIsIdempotent() {
        PlayerData d = new PlayerData();
        EmberFirstClear.record(d, "q01", "v1");
        EmberFirstClear.record(d, "q01", "v1");
        assertTrue(EmberFirstClear.fact(d, "q01"));
        assertTrue(EmberFirstClear.paid(d, "q01", "v1"));
        assertEquals(1, d.periodCount("p1_first_clear_q01", "all"));
        assertEquals(1, d.periodCount("p1_fcpay_q01", "v1"));
        assertFalse(EmberFirstClear.fact(d, "q02"));
        assertFalse(EmberFirstClear.fact(d, "q0")); // prefix of q01 must not match
    }

    @Test
    public void versionBumpKeepsFactAndOnlyRepaysPackage() {
        PlayerData d = new PlayerData();
        EmberFirstClear.record(d, "q03", "v1");
        assertTrue(EmberFirstClear.fact(d, "q03"));
        assertFalse(EmberFirstClear.paid(d, "q03", "v2")); // new version: package may pay again
        EmberFirstClear.record(d, "q03", "v2");
        assertTrue(EmberFirstClear.fact(d, "q03"));
        assertTrue(EmberFirstClear.paid(d, "q03", "v2"));
        assertEquals(1, d.periodCount("p1_first_clear_q03", "all"));
    }

    @Test
    public void legacySingleKeyCountsAsBothAndMigrates() {
        PlayerData d = new PlayerData();
        d.addPeriodCount("p1_first_clear_q02", "v1", 1); // pre-D205 write
        assertTrue(EmberFirstClear.fact(d, "q02"));
        assertTrue(EmberFirstClear.paid(d, "q02", "v1"));
        assertFalse(EmberFirstClear.paid(d, "q02", "v2"));
        assertTrue(EmberFirstClear.migrate(d, "q02"));
        assertEquals(0, d.periodCount("p1_first_clear_q02", "v1"));
        assertEquals(1, d.periodCount("p1_first_clear_q02", "all"));
        assertEquals(1, d.periodCount("p1_fcpay_q02", "v1"));
        assertTrue(EmberFirstClear.fact(d, "q02"));
        assertTrue(EmberFirstClear.paid(d, "q02", "v1"));
        assertFalse(EmberFirstClear.migrate(d, "q02"));
    }

    @Test
    public void adminStubOnLegacyKeyNoLongerRepaysPackage() {
        // pre-D205 the stub wrote @all through addPeriodCount and dropped the real @v1 record (package paid twice)
        PlayerData d = new PlayerData();
        d.addPeriodCount("p1_first_clear_q04", "v1", 1);
        EmberFirstClear.setFact(d, "q04", true);
        assertTrue(EmberFirstClear.fact(d, "q04"));
        assertTrue(EmberFirstClear.paid(d, "q04", "v1"));
    }

    @Test
    public void adminFactOnlyDoesNotMarkPackagePaid() {
        PlayerData d = new PlayerData();
        EmberFirstClear.setFact(d, "q07", true);
        assertTrue(EmberFirstClear.fact(d, "q07"));
        assertFalse(EmberFirstClear.paid(d, "q07", "v1"));
        EmberFirstClear.setFact(d, "q07", false);
        assertFalse(EmberFirstClear.fact(d, "q07"));
    }

    @Test
    public void adminSetBothAndClearBoth() {
        PlayerData d = new PlayerData();
        EmberFirstClear.setBoth(d, "q05", "v1", true);
        assertTrue(EmberFirstClear.fact(d, "q05"));
        assertTrue(EmberFirstClear.paid(d, "q05", "v1"));
        EmberFirstClear.setBoth(d, "q05", "v1", false);
        assertFalse(EmberFirstClear.fact(d, "q05"));
        assertFalse(EmberFirstClear.paid(d, "q05", "v1"));
        d.addPeriodCount("p1_first_clear_q06", "v1", 1); // legacy then clear
        EmberFirstClear.setBoth(d, "q06", "v1", false);
        assertFalse(EmberFirstClear.fact(d, "q06"));
        assertFalse(EmberFirstClear.paid(d, "q06", "v1"));
    }

    @Test
    public void zeroLegacyRowIsIgnored() {
        PlayerData d = new PlayerData();
        java.util.Map<String, Integer> m = new java.util.LinkedHashMap<String, Integer>();
        m.put("p1_first_clear_q01@v1", 0);
        d.setCounters(m);
        assertFalse(EmberFirstClear.fact(d, "q01"));
        assertFalse(EmberFirstClear.paid(d, "q01", "v1"));
    }
}

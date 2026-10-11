package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberSigChaseEnterPinTest {
    @Test
    public void picksUnderMarkOnMap() {
        PlayerData d = new PlayerData();
        EmberSignature.Def first = EmberSignature.byId("L01");
        assertNotNull(first);
        assertEquals("q01", first.map);
        // marks 0 < need → pick L01
        EmberSignature.Def pick = EmberSigChase.pickUnderMarkForMap(d, "q01");
        assertNotNull(pick);
        assertEquals("q01", pick.map);
        // fill marks past need → no pick
        int need = EmberSignature.imprintMarks();
        d.addPeriodCount(EmberSignature.C_MARK + "q01", "all", need);
        assertNull(EmberSigChase.pickUnderMarkForMap(d, "q01"));
    }

    @Test
    public void setChaseIdempotent() {
        PlayerData d = new PlayerData();
        EmberSignature.Def def = EmberSignature.byId("L03");
        assertTrue(EmberSigChase.set(d, def));
        assertFalse(EmberSigChase.set(d, def));
        assertEquals(def.code, EmberSigChase.get(d).code);
    }
}

package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;

public class EmberImprintNextPinTest {
    @Test
    public void pickUnderMarkSkipsFullMaps() {
        PlayerData d = new PlayerData();
        int need = EmberSignature.imprintMarks();
        d.addPeriodCount(EmberSignature.C_MARK + "q01", "all", need);
        EmberSignature.Def pick = EmberSigChase.pickUnderMarkForMap(d, "q01");
        assertNull(pick);
        EmberSignature.Def q02 = EmberSigChase.pickUnderMarkForMap(d, "q02");
        // q02 marks 0 → should pick if DEFS has q02
        if (q02 != null) assertEquals("q02", q02.map);
    }
}

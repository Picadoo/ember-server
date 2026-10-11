package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberImprintMarkSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        int base = EmberSignature.imprintMarks();
        if (base <= 0) return;
        assertEquals(Math.max(1, base / 2), EmberImprintMarkSoft.need(d, true));
        assertEquals(base, EmberImprintMarkSoft.need(d, true));
    }
}

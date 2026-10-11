package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberExchangeMarkSoftTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        int base = EmberRunRules.marksPerExchange();
        if (base <= 0) return;
        int peek = EmberExchangeMarkSoft.need(d, false);
        assertEquals(Math.max(1, base / 2), peek);
        EmberExchangeMarkSoft.need(d, true);
        assertEquals(base, EmberExchangeMarkSoft.need(d, true));
    }
}

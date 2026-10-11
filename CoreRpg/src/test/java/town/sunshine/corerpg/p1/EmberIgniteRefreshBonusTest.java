package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
import town.sunshine.corerpg.PlayerData;
public class EmberIgniteRefreshBonusTest {
    @Test public void once() {
        PlayerData d = new PlayerData();
        assertEquals(1, EmberIgniteRefreshBonus.consumeExtraTicks(d));
        assertEquals(0, EmberIgniteRefreshBonus.consumeExtraTicks(d));
    }
    @Test public void burnExtend() {
        EmberBurnBook book = new EmberBurnBook();
        book.ignite("t", 1.0, 1000L);
        assertTrue(book.extendEnd("t", 1));
        EmberBurnBook.Burn b = book.get("t");
        assertNotNull(b);
        assertTrue(b.endAt() > 1000L + EmberSetRules.BURN_TICKS * EmberSetRules.BURN_INTERVAL_MS);
    }
}

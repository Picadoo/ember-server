package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberUnscathedBudgetBoostTest {
    @Test public void once() {
        EmberUnscathedBudgetBoost.clearAll();
        assertEquals(1, EmberUnscathedBudgetBoost.consumeExtra("r1"));
        assertEquals(0, EmberUnscathedBudgetBoost.consumeExtra("r1"));
    }
}

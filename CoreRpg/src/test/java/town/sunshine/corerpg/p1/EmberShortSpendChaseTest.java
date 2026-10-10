package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberShortSpendChaseTest {
    @Test public void spend_chase_button_and_day_full() {
        assertTrue(EmberShortRules.spendChaseTell(1, 3).contains("进仓"));
        assertTrue(EmberShortRules.spendChaseTell(3, 3).contains("有奖已满"));
        assertEquals("[去工坊]", EmberShortRules.spendChaseButtonLabel());
        assertTrue(EmberShortRules.spendChaseButtonCmd().contains("forge"));
    }
}

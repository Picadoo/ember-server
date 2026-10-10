package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberExtraPathTest {
    @Test public void parse_and_bias() {
        assertEquals(EmberExtraPath.TREASURE, EmberExtraPath.parse("宝藏"));
        assertEquals(EmberExtraPath.ELITE, EmberExtraPath.parse("elite"));
        assertEquals(EmberExtraPath.CHEST, EmberExtraPath.parse("宝箱"));
        assertEquals(EmberRunRules.Extra.TREASURE, EmberExtraPath.toExtra(EmberExtraPath.TREASURE));
        assertEquals(EmberRunRules.Extra.ELITE,
                EmberExtraPath.applyBias(EmberRunRules.Extra.NONE, EmberExtraPath.ELITE));
        assertEquals(EmberRunRules.Extra.CHEST,
                EmberExtraPath.applyBias(EmberRunRules.Extra.TREASURE, EmberExtraPath.CHEST));
        assertEquals(EmberRunRules.Extra.ELITE,
                EmberExtraPath.applyBias(EmberRunRules.Extra.ELITE, EmberExtraPath.NONE));
        assertTrue(EmberExtraPath.tip(EmberExtraPath.TREASURE).contains("宝藏"));
    }
}

package town.sunshine.corerpg.p1;
import org.junit.Test;
import static org.junit.Assert.*;
public class EmberDosePathTest {
    @Test public void policies() {
        assertTrue(EmberDosePath.shouldAuto(EmberDosePath.AUTO));
        assertTrue(EmberDosePath.shouldMute(EmberDosePath.MUTE));
        assertEquals(EmberDosePath.ASK, EmberDosePath.parse("提醒"));
    }
    @Test public void tipFwOfferPickConstants() {
        assertEquals("/ember_p1_dose_path", EmberDosePath.PATH_PICK_CMD);
        assertEquals("[补体路径]", EmberDosePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberDosePath.PATH_ADVENTURE_CMD);
        assertEquals("补体路径 · 补体·自动", EmberDosePath.menuLine(EmberDosePath.AUTO));
        String g = EmberDosePath.glance(EmberDosePath.NONE, true);
        assertTrue(g.contains("点补体路径页"));
    }
}

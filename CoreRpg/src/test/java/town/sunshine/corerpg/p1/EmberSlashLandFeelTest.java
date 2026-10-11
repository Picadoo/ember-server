package town.sunshine.corerpg.p1;
import static org.junit.Assert.*;
import org.junit.Test;
public class EmberSlashLandFeelTest {
    @Test public void denserThanOld() {
        assertTrue(EmberSlashLandFeel.lineSamples() > 6);
        assertTrue(EmberSlashLandFeel.ringPoints() > 12);
        assertTrue(EmberSlashLandFeel.fanArms() > 9);
    }
    @Test public void soundsDiffer() {
        assertNotEquals(EmberSlashLandFeel.landSound(EmberSkillKit.SHAPE_LINE),
            EmberSlashLandFeel.landSound(EmberSkillKit.SHAPE_RING));
    }
}

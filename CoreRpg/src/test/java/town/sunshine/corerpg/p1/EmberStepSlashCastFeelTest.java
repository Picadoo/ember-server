package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.junit.Test;

public class EmberStepSlashCastFeelTest {
    @Test
    public void stepTrailDenserThanOld() {
        assertTrue(EmberStepFeel.trailPrimary(EmberSkillKit.STEP_VARIANT_HUOHEN) >= 5);
        assertTrue(EmberStepFeel.trailHead(EmberSkillKit.STEP_VARIANT_PLAIN) >= 12);
        assertEquals(1, EmberStepFeel.footprintPerSample());
    }

    @Test
    public void stepPitchBackLower() {
        float fwd = EmberStepFeel.landPitch(EmberSkillKit.STEP_VARIANT_PLAIN, false);
        float back = EmberStepFeel.landPitch(EmberSkillKit.STEP_VARIANT_PLAIN, true);
        assertTrue(back < fwd);
    }

    @Test
    public void slashTelegraphDenser() {
        assertTrue(EmberSlashCastFeel.lineSamples() > 6);
        assertTrue(EmberSlashCastFeel.ringPoints() > 16);
        assertTrue(EmberSlashCastFeel.fanRays() > 3);
        assertEquals(5, EmberSlashCastFeel.fanDegrees().length);
    }

    @Test
    public void slashCastSoundsDiffer() {
        assertNotEquals(
            EmberSlashCastFeel.castSound(EmberSkillKit.SHAPE_LINE),
            EmberSlashCastFeel.castSound(EmberSkillKit.SHAPE_RING));
    }
}

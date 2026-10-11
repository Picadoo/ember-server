package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;
import org.bukkit.Sound;
import org.junit.Test;

public class EmberSparkDashFeelTest {
    @Test
    public void fanArmsCrit() {
        assertTrue(EmberMeleeFeel.fanArms(true) > EmberMeleeFeel.fanArms(false));
    }

    @Test
    public void hitSounds() {
        assertEquals(Sound.ENTITY_PLAYER_ATTACK_CRIT, EmberMeleeFeel.hitSound(true));
        assertEquals(Sound.ENTITY_PLAYER_ATTACK_STRONG, EmberMeleeFeel.hitSound(false));
    }

    @Test
    public void dashTrailDenserThanOldBaseline() {
        assertTrue(EmberDashFeel.trailPrimary("burst") >= 7);
        assertTrue(EmberDashFeel.trailSecondary("burst") >= 4);
        assertTrue(EmberDashFeel.trailHead("scorch") >= 16);
    }

    @Test
    public void dashLandSoundsDiffer() {
        assertNotEquals(EmberDashFeel.landSound("burst"), EmberDashFeel.landSound("scorch"));
        assertNotEquals(EmberDashFeel.landPitch("burst"), EmberDashFeel.landPitch("sustain"), 1e-9);
    }
}

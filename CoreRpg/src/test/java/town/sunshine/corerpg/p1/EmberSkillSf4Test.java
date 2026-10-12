package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import java.util.Collections;
import org.junit.Test;

/** TIP-BG / D13025 · DESIGN §4 最小包。 */
public class EmberSkillSf4Test {

    @Test
    public void constants() {
        assertEquals(20, EmberSkillSf4.MARK_TICKS);
        assertEquals(0, EmberSkillSf4.SLOW_AMP);
        assertEquals(3, EmberSkillSf4.HIT_LIM);
    }

    @Test
    public void shapeOk_burst_line() {
        assertTrue(EmberSkillSf4.shapeOk("burst", EmberSkillKit.SHAPE_LINE));
    }

    @Test
    public void shapeOk_burst_fan() {
        assertFalse(EmberSkillSf4.shapeOk("burst", EmberSkillKit.SHAPE_FAN));
    }

    @Test
    public void shapeOk_scorch_fan() {
        assertTrue(EmberSkillSf4.shapeOk("scorch", EmberSkillKit.SHAPE_FAN));
    }

    @Test
    public void shapeOk_none_line() {
        assertTrue(EmberSkillSf4.shapeOk("none", EmberSkillKit.SHAPE_LINE));
    }

    @Test
    public void shapeOk_none_ring() {
        assertFalse(EmberSkillSf4.shapeOk("none", EmberSkillKit.SHAPE_RING));
    }

    @Test
    public void shapeOk_empty_and_null_fam() {
        assertTrue(EmberSkillSf4.shapeOk("", EmberSkillKit.SHAPE_LINE));
        assertFalse(EmberSkillSf4.shapeOk("", EmberSkillKit.SHAPE_FAN));
        assertTrue(EmberSkillSf4.shapeOk(null, EmberSkillKit.SHAPE_LINE));
        assertFalse(EmberSkillSf4.shapeOk(null, EmberSkillKit.SHAPE_FAN));
    }

    @Test
    public void maybeApply_emptyHit_returnsFalse() {
        assertFalse(EmberSkillSf4.maybeApply(null, Collections.<org.bukkit.entity.LivingEntity>emptyList(),
                EmberSkillKit.SHAPE_LINE, "none"));
        assertFalse(EmberSkillSf4.maybeApply(null, null, EmberSkillKit.SHAPE_LINE, "none"));
    }
}

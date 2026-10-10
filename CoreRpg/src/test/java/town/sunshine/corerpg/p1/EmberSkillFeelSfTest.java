package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

/** D1157 SF1–SF3 display helpers — zero combat numeric. */
public class EmberSkillFeelSfTest {

    @Test
    public void sf1ShapeForkCopy() {
        assertEquals("扇形烬斩命中", EmberSkillFeelSf.slashConfirm(EmberSkillKit.SHAPE_FAN, 1));
        assertEquals("直线烬斩命中", EmberSkillFeelSf.slashConfirm(EmberSkillKit.SHAPE_LINE, 1));
        assertEquals("环斩烬斩命中", EmberSkillFeelSf.slashConfirm(EmberSkillKit.SHAPE_RING, 1));
        assertEquals("", EmberSkillFeelSf.slashConfirm(EmberSkillKit.SHAPE_FAN, 0));
    }

    @Test
    public void sf2HitCountDisplay() {
        assertEquals("扇形烬斩命中 ×3", EmberSkillFeelSf.slashConfirm(EmberSkillKit.SHAPE_FAN, 3));
        assertEquals("直线烬斩命中 ×2", EmberSkillFeelSf.slashConfirm(EmberSkillKit.SHAPE_LINE, 2));
    }

    @Test
    public void sf3SetShapeHalf() {
        String sc = EmberSkillFeelSf.setShapeHalf("scorch");
        assertTrue(sc.contains("扇形") || sc.contains("扇"));
        assertTrue(sc.contains("点燃") || sc.contains("火痕"));
        String bu = EmberSkillFeelSf.setShapeHalf("burst");
        assertTrue(bu.contains("直线"));
        assertTrue(bu.contains("缓速"));
        String su = EmberSkillFeelSf.setShapeHalf("sustain");
        assertTrue(su.contains("环"));
        assertTrue(EmberSkillFeelSf.slashConfirmWithSet(0, 1, " · 烬刃余烬").endsWith("烬刃余烬"));
    }

    @Test public void slashMiss_shapeOnly() {
        assertEquals("扇形烬斩落空", EmberSkillFeelSf.slashMiss(EmberSkillKit.SHAPE_FAN));
        assertEquals("直线烬斩落空", EmberSkillFeelSf.slashMiss(EmberSkillKit.SHAPE_LINE));
        assertEquals("环斩烬斩落空", EmberSkillFeelSf.slashMiss(EmberSkillKit.SHAPE_RING));
    }

    @Test public void dashBlocked_label() {
        assertEquals("烬突受阻", EmberSkillFeelSf.dashBlocked());
    }
}

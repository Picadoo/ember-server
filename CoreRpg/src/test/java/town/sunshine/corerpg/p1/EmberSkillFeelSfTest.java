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

    @Test public void chargeCdLine_shape() {
        assertEquals("扇形充能 3s", EmberSkillFeelSf.chargeCdLine(EmberSkillKit.SHAPE_FAN, 3));
        assertEquals("直线充能 0s", EmberSkillFeelSf.chargeCdLine(EmberSkillKit.SHAPE_LINE, 0));
    }
    @Test
    public void menuLine_knownFamilies() {
        assertEquals("§e焚烬烬斩 §f扇形 §8· 点燃/火痕", EmberSkillFeelSf.menuLine("scorch"));
        assertEquals("§e烬爆烬斩 §f直线 §8· 缓速/闪", EmberSkillFeelSf.menuLine("burst"));
        assertEquals("§e炽愈烬斩 §f环斩 §8· 微疗/护", EmberSkillFeelSf.menuLine("sustain"));
    }

    @Test
    public void menuLine_unset() {
        String u = "§8未成套 · 成套后烬斩形状与落点随族变";
        assertEquals(u, EmberSkillFeelSf.menuLine((String) null));
        assertEquals(u, EmberSkillFeelSf.menuLine(""));
        assertEquals(u, EmberSkillFeelSf.menuLine("none"));
        assertEquals(u, EmberSkillFeelSf.menuLine("unknown"));
        assertEquals(u, EmberSkillFeelSf.menuLine((EmberLoadout) null));
    }

    @Test
    public void menuLine_loadoutBladeFamily() {
        EmberTables tables = EmberTables.defaults();
        EmberItemData blade = EmberItemData.create("scorch", "blade", 1, 0, 0, 0, true, "admin");
        EmberLoadout lo = EmberLoadout.compute(tables, blade, null, 10);
        assertEquals("§e焚烬烬斩 §f扇形 §8· 点燃/火痕", EmberSkillFeelSf.menuLine(lo));
    }

}

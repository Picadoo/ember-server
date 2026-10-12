package town.sunshine.corerpg.p1;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** D1157 SF1–SF3 + D12320–D12325 TIP-K skill feel pack — zero combat numeric. */
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

    // —— D12322 shape role ——
    @Test
    public void shapeRoleLine_perShape() {
        assertEquals("扇形·清近", EmberSkillFeelSf.shapeRoleLine(EmberSkillKit.SHAPE_FAN));
        assertEquals("直线·穿远", EmberSkillFeelSf.shapeRoleLine(EmberSkillKit.SHAPE_LINE));
        assertEquals("环斩·围身", EmberSkillFeelSf.shapeRoleLine(EmberSkillKit.SHAPE_RING));
        assertEquals("扇形·清近", EmberSkillFeelSf.shapeRoleLine(-1)); // default fan
    }

    @Test
    public void shapeRole_sessionLatchOnce() {
        UUID id = UUID.randomUUID();
        EmberSkillFeelSf.clearSession(id);
        assertTrue(EmberSkillFeelSf.tryMarkShapeRole(id, EmberSkillKit.SHAPE_FAN));
        assertFalse(EmberSkillFeelSf.tryMarkShapeRole(id, EmberSkillKit.SHAPE_FAN));
        assertTrue(EmberSkillFeelSf.tryMarkShapeRole(id, EmberSkillKit.SHAPE_LINE));
        assertFalse(EmberSkillFeelSf.tryMarkShapeRole(id, EmberSkillKit.SHAPE_LINE));
        assertTrue(EmberSkillFeelSf.tryMarkShapeRole(id, EmberSkillKit.SHAPE_RING));
        EmberSkillFeelSf.clearSession(id);
        assertTrue(EmberSkillFeelSf.tryMarkShapeRole(id, EmberSkillKit.SHAPE_FAN));
        EmberSkillFeelSf.clearSession(id);
    }

    @Test
    public void wouldMarkShapeRole_pure() {
        assertTrue(EmberSkillFeelSf.wouldMarkShapeRole(null, 0));
        Set<Integer> seen = new HashSet<Integer>();
        seen.add(0);
        assertFalse(EmberSkillFeelSf.wouldMarkShapeRole(seen, 0));
        assertTrue(EmberSkillFeelSf.wouldMarkShapeRole(seen, 1));
    }

    // —— D12323 wrong-shape tip ——
    @Test
    public void wrongShapeTip_string() {
        assertEquals("荐：扇形·点燃/火痕", EmberSkillFeelSf.wrongShapeTip("scorch"));
        assertEquals("荐：直线·缓速/闪", EmberSkillFeelSf.wrongShapeTip("burst"));
        assertEquals("荐：环斩·微疗/护", EmberSkillFeelSf.wrongShapeTip("sustain"));
        assertEquals("", EmberSkillFeelSf.wrongShapeTip(null));
        assertEquals("", EmberSkillFeelSf.wrongShapeTip("none"));
        assertEquals("", EmberSkillFeelSf.wrongShapeTip("unknown"));
    }

    @Test
    public void wrongShape_oncePerSetLatch() {
        UUID id = UUID.randomUUID();
        EmberSkillFeelSf.clearSession(id);
        // preferred FAN for scorch — no tip
        assertEquals("", EmberSkillFeelSf.maybeConsumeWrongShapeTip(id, "scorch", EmberSkillKit.SHAPE_FAN));
        // wrong shape → tip once
        String tip = EmberSkillFeelSf.maybeConsumeWrongShapeTip(id, "scorch", EmberSkillKit.SHAPE_LINE);
        assertEquals("荐：扇形·点燃/火痕", tip);
        assertEquals("", EmberSkillFeelSf.maybeConsumeWrongShapeTip(id, "scorch", EmberSkillKit.SHAPE_RING));
        // set change clears latch
        EmberSkillFeelSf.noteActiveSet(id, "burst");
        tip = EmberSkillFeelSf.maybeConsumeWrongShapeTip(id, "burst", EmberSkillKit.SHAPE_FAN);
        assertEquals("荐：直线·缓速/闪", tip);
        assertEquals("", EmberSkillFeelSf.maybeConsumeWrongShapeTip(id, "burst", EmberSkillKit.SHAPE_RING));
        // unequip clears
        EmberSkillFeelSf.noteActiveSet(id, "none");
        EmberSkillFeelSf.noteActiveSet(id, "sustain");
        tip = EmberSkillFeelSf.maybeConsumeWrongShapeTip(id, "sustain", EmberSkillKit.SHAPE_FAN);
        assertEquals("荐：环斩·微疗/护", tip);
        EmberSkillFeelSf.clearSession(id);
    }

    // —— D12324 step role ——
    @Test
    public void stepRoleLine_variants() {
        assertEquals("火痕步·点燃起跳", EmberSkillFeelSf.stepVariantRoleLine(EmberSkillKit.STEP_VARIANT_HUOHEN));
        assertEquals("爆闪步·拉开", EmberSkillFeelSf.stepVariantRoleLine(EmberSkillKit.STEP_VARIANT_BAOSHAN));
        assertEquals("承护步·站桩", EmberSkillFeelSf.stepVariantRoleLine(EmberSkillKit.STEP_VARIANT_CHENGHU));
        assertEquals("", EmberSkillFeelSf.stepVariantRoleLine(EmberSkillKit.STEP_VARIANT_PLAIN));
        assertEquals("后撤·拉开", EmberSkillFeelSf.stepDirRoleLine(EmberSkillKit.DIR_BACK));
        assertEquals("前冲·突进", EmberSkillFeelSf.stepDirRoleLine(EmberSkillKit.DIR_FORWARD));
        assertEquals("火痕步·点燃起跳", EmberSkillFeelSf.stepRoleLine(EmberSkillKit.STEP_VARIANT_HUOHEN, true));
        assertEquals("后撤·拉开", EmberSkillFeelSf.stepRoleLine(EmberSkillKit.STEP_VARIANT_PLAIN, true));
        assertEquals("", EmberSkillFeelSf.stepRoleLine(EmberSkillKit.STEP_VARIANT_PLAIN, false));
    }

    @Test
    public void stepRole_sessionLatchOnce() {
        UUID id = UUID.randomUUID();
        EmberSkillFeelSf.clearSession(id);
        assertTrue(EmberSkillFeelSf.tryMarkStepVariant(id, EmberSkillKit.STEP_VARIANT_HUOHEN));
        assertFalse(EmberSkillFeelSf.tryMarkStepVariant(id, EmberSkillKit.STEP_VARIANT_HUOHEN));
        assertTrue(EmberSkillFeelSf.tryMarkStepVariant(id, EmberSkillKit.STEP_VARIANT_BAOSHAN));
        assertFalse(EmberSkillFeelSf.tryMarkStepVariant(id, EmberSkillKit.STEP_VARIANT_PLAIN));
        assertTrue(EmberSkillFeelSf.tryMarkStepDir(id, EmberSkillKit.DIR_BACK));
        assertFalse(EmberSkillFeelSf.tryMarkStepDir(id, EmberSkillKit.DIR_BACK));
        assertTrue(EmberSkillFeelSf.tryMarkStepDir(id, EmberSkillKit.DIR_FORWARD));
        EmberSkillFeelSf.clearSession(id);
        assertTrue(EmberSkillFeelSf.tryMarkStepVariant(id, EmberSkillKit.STEP_VARIANT_HUOHEN));
        EmberSkillFeelSf.clearSession(id);
    }

    // —— D12325 slashConfirmWithSet empty path ——
    @Test
    public void slashConfirmWithSet_emptyNullHalf_noFamily() {
        assertEquals("扇形烬斩命中", EmberSkillFeelSf.slashConfirmWithSet(0, 1, null));
        assertEquals("扇形烬斩命中", EmberSkillFeelSf.slashConfirmWithSet(0, 1, ""));
        assertEquals("", EmberSkillFeelSf.slashConfirmWithSet(0, 0, " · 烬刃余烬"));
    }

    @Test
    public void slashConfirmWithSet_emptyHalf_withFamily_fallsBack() {
        // empty/null half + known fam → action half
        assertEquals("扇形烬斩命中 · 烬刃余烬",
                EmberSkillFeelSf.slashConfirmWithSet(EmberSkillKit.SHAPE_FAN, 1, null, "scorch"));
        assertEquals("直线烬斩命中 · 烬刃缓速",
                EmberSkillFeelSf.slashConfirmWithSet(EmberSkillKit.SHAPE_LINE, 1, "", "burst"));
        assertEquals("环斩烬斩命中 ×2 · 烬刃微疗",
                EmberSkillFeelSf.slashConfirmWithSet(EmberSkillKit.SHAPE_RING, 2, null, "sustain"));
        // unknown fam + empty half → base only
        assertEquals("扇形烬斩命中",
                EmberSkillFeelSf.slashConfirmWithSet(0, 1, null, "none"));
        assertEquals("扇形烬斩命中",
                EmberSkillFeelSf.slashConfirmWithSet(0, 1, null, null));
        // non-empty half wins even with fam
        assertEquals("扇形烬斩命中 · 自定义",
                EmberSkillFeelSf.slashConfirmWithSet(0, 1, " · 自定义", "scorch"));
    }

}

package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * TIP-CJ D13315: afk_rec_flower — 四态 kind + 护甲跳过 2–4 + empty / ok 边界。
 */
public class EmberAfkRecFlowerTest {

    @Test
    public void preferEnhanceWhenReady() {
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(false, true, true, true, true);
        assertEquals(EmberAfkRecFlower.KIND_ENHANCE, p.kind);
        assertEquals(EmberAfkRecFlower.PREVIEW_ENHANCE, p.previewOp);
        assertEquals("1", EmberAfkRecFlower.ok(p));
        assertEquals("强化", EmberAfkRecFlower.name(p));
        assertTrue(EmberAfkRecFlower.line(p).contains("强化"));
    }

    @Test
    public void upgradeWhenEnhanceNotReady() {
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(false, false, true, true, true);
        assertEquals(EmberAfkRecFlower.KIND_UPGRADE, p.kind);
        assertEquals(EmberAfkRecFlower.PREVIEW_UPGRADE, p.previewOp);
        assertEquals("1", EmberAfkRecFlower.ok(p));
        assertEquals("升阶", EmberAfkRecFlower.name(p));
    }

    @Test
    public void refineWhenEnhanceAndUpgradeNotReady() {
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(false, false, false, true, true);
        assertEquals(EmberAfkRecFlower.KIND_REFINE, p.kind);
        assertEquals(EmberAfkRecFlower.PREVIEW_REFINE, p.previewOp);
        assertEquals("精工", EmberAfkRecFlower.name(p));
    }

    @Test
    public void qualityWhenOnlyQualityReady() {
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(false, false, false, false, true);
        assertEquals(EmberAfkRecFlower.KIND_QUALITY, p.kind);
        assertEquals(EmberAfkRecFlower.PREVIEW_QUALITY, p.previewOp);
        assertEquals("成色", EmberAfkRecFlower.name(p));
        assertEquals("1", EmberAfkRecFlower.ok(p));
    }

    @Test
    public void armorSkipsUpgradeRefineQuality() {
        // upgrade/refine/quality flags true but armor → empty (enhance also false)
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(true, false, true, true, true);
        assertEquals(EmberAfkRecFlower.KIND_EMPTY, p.kind);
        assertFalse(p.present());
        assertEquals("0", EmberAfkRecFlower.ok(p));
        assertEquals("", EmberAfkRecFlower.name(p));
        assertTrue(EmberAfkRecFlower.line(p).contains("暂无"));
    }

    @Test
    public void armorStillAllowsEnhanceIfReady() {
        // DESIGN: armor skips 2–4 only; enhance if somehow ready wins first
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(true, true, true, true, true);
        assertEquals(EmberAfkRecFlower.KIND_ENHANCE, p.kind);
        assertEquals("1", EmberAfkRecFlower.ok(p));
    }

    @Test
    public void allEmpty() {
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(false, false, false, false, false);
        assertEquals(EmberAfkRecFlower.KIND_EMPTY, p.kind);
        assertEquals("", p.previewOp);
        assertFalse(p.present());
        assertEquals("0", EmberAfkRecFlower.ok(p));
        assertEquals("§8暂无仓够花", EmberAfkRecFlower.line(p));
    }

    @Test
    public void okAndKindBoundary() {
        assertEquals("0", EmberAfkRecFlower.ok((EmberAfkRecFlower.Pick) null));
        assertEquals("", EmberAfkRecFlower.name(null));
        assertEquals("§8暂无仓够花", EmberAfkRecFlower.line(null));

        EmberAfkRecFlower.Pick empty = EmberAfkRecFlower.pick(false, false, false, false, false);
        assertEquals(EmberAfkRecFlower.KIND_EMPTY, empty.kind);
        assertEquals("0", EmberAfkRecFlower.ok(empty));

        EmberAfkRecFlower.Pick enh = EmberAfkRecFlower.pick(false, true, false, false, false);
        assertTrue(enh.present());
        assertEquals(EmberAfkRecFlower.KIND_ENHANCE, enh.kind);
    }

    @Test
    public void priorityEnhanceBeatsAll() {
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(false, true, true, true, true);
        assertEquals(EmberAfkRecFlower.KIND_ENHANCE, p.kind);
    }

    @Test
    public void priorityUpgradeBeatsRefineAndQuality() {
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(false, false, true, true, true);
        assertEquals(EmberAfkRecFlower.KIND_UPGRADE, p.kind);
    }

    @Test
    public void priorityRefineBeatsQuality() {
        EmberAfkRecFlower.Pick p = EmberAfkRecFlower.pick(false, false, false, true, true);
        assertEquals(EmberAfkRecFlower.KIND_REFINE, p.kind);
    }
}

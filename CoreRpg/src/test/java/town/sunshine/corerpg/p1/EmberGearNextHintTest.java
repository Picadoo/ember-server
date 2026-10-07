package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;

/** D299 · 再刷短反馈近档文案 + 相对穿着对照（零经济）。 */
public class EmberGearNextHintTest {

    private static EmberItemData blade(int q, int craft) {
        return EmberItemData.create("burst", "blade", 2, q, craft, 0, false, "drop");
    }

    @Test public void qualityLine_stepsAndMax() {
        assertEquals("成色 标准 → 精良（胚8 骨8 币800）", EmberGearNextHint.qualityLine(blade(0, 0)));
        assertEquals("成色 精良 → 卓越（胚16 骨16 币1600）", EmberGearNextHint.qualityLine(blade(1, 0)));
        assertEquals("成色 卓越 → 极品仅掉落", EmberGearNextHint.qualityLine(blade(2, 0)));
        assertEquals("成色 极品 · 已满", EmberGearNextHint.qualityLine(blade(3, 0)));
        assertEquals("", EmberGearNextHint.qualityLine(null));
    }

    @Test public void craftLine_stepsAndMax() {
        assertEquals("精工 0% → 2%（胚3 骨5 币300）", EmberGearNextHint.craftLine(blade(0, 0)));
        assertEquals("精工 2% → 4%（胚6 骨10 币600）", EmberGearNextHint.craftLine(blade(0, 1)));
        assertEquals("精工 4% → 6%（胚12 骨20 币1200）", EmberGearNextHint.craftLine(blade(0, 2)));
        assertEquals("精工 6% · 已满", EmberGearNextHint.craftLine(blade(0, 3)));
    }

    @Test public void relative_silenceWhenSame() {
        assertNull(EmberGearNextHint.relativeLine(blade(1, 1), blade(1, 1)));
        assertNull(EmberGearNextHint.relativeStats(1, 1, blade(1, 1)));
        assertNull(EmberGearNextHint.relativeLine(blade(1, 1), null));
    }

    @Test public void relative_upsAndBelow() {
        assertEquals("相对穿着：成色↑", EmberGearNextHint.relativeLine(blade(2, 1), blade(1, 1)));
        assertEquals("相对穿着：精工↑", EmberGearNextHint.relativeLine(blade(1, 2), blade(1, 1)));
        assertEquals("相对穿着：成色↑ · 精工↑", EmberGearNextHint.relativeLine(blade(2, 2), blade(1, 1)));
        assertEquals("相对穿着：低于", EmberGearNextHint.relativeLine(blade(0, 0), blade(1, 1)));
        assertEquals("相对穿着：成色↑ · 精工↓", EmberGearNextHint.relativeLine(blade(2, 0), blade(1, 1)));
    }

    @Test public void costShort_compact() {
        assertEquals("胚8 骨8 币800", EmberGearNextHint.costShort(EmberUpgradeRules.qualityCost(0)));
        assertEquals("胚3 骨5 币300", EmberGearNextHint.costShort(EmberUpgradeRules.refineCost(0)));
    }
}

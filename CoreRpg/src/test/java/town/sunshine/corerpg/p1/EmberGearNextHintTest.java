package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;

/** D299 近档 + D307 工坊费用行 / 缺料半行（零改价）。 */
public class EmberGearNextHintTest {

    private static EmberItemData blade(int q, int craft) {
        return EmberItemData.create("burst", "blade", 2, q, craft, 0, false, "drop");
    }

    private static EmberItemData bladeEnh(int enh, int pity) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2", "burst", "blade", 2, 0, 0, enh, pity, false, "drop", 1, 0);
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

    @Test public void enhanceLine_stepsAndPity() {
        assertEquals("+0→+1 · 碎片4 币40 · 100%（1/1必成）", EmberGearNextHint.enhanceLine(bladeEnh(0, 0)));
        assertEquals("+3→+4 · 碎片12 币120 · 85%（1/3）", EmberGearNextHint.enhanceLine(bladeEnh(3, 0)));
        assertEquals("+3→+4 · 碎片12 币120 · 85%（3/3必成）", EmberGearNextHint.enhanceLine(bladeEnh(3, 2)));
        assertTrue(EmberGearNextHint.enhanceLine(bladeEnh(10, 0)).contains("已是"));
    }

    @Test public void upgradeLine_gateAndCost() {
        EmberItemData t1 = EmberItemData.create("burst", "blade", 1, 0, 0, 0, false, "drop");
        EmberItemData t2 = EmberItemData.create("burst", "blade", 2, 0, 0, 0, false, "drop");
        EmberItemData t3 = EmberItemData.create("burst", "blade", 3, 0, 0, 0, false, "drop");
        assertEquals("需首通 Q04 · 碎片60 核心12 胚6 币1500", EmberGearNextHint.upgradeLine(t1, false));
        assertEquals("T1→T2 · 碎片60 核心12 胚6 币1500", EmberGearNextHint.upgradeLine(t1, true));
        assertEquals("需首通 Q07 · 碎片60 核心15 胚6 币1800", EmberGearNextHint.upgradeLine(t2, false));
        assertEquals("T2→T3 · 碎片60 核心15 胚6 币1800", EmberGearNextHint.upgradeLine(t2, true));
        assertTrue(EmberGearNextHint.upgradeLine(t3, true).contains("最高阶"));
    }

    @Test public void swapAndDismantleAndLack() {
        assertEquals("免费 · 交换强化等级+失败计数", EmberGearNextHint.swapLine());
        EmberItemData drop = EmberItemData.create("burst", "blade", 2, 0, 0, 0, false, "drop");
        assertEquals("得胚×2", EmberGearNextHint.dismantleYieldLine(drop));
        EmberItemData quest = EmberItemData.create("burst", "blade", 1, 0, 0, 0, false, "quest");
        String why = EmberGearNextHint.dismantleYieldLine(quest);
        assertNotNull(why);
        assertFalse(why.startsWith("得胚"));
        assertEquals("", EmberGearNextHint.lackHalf(java.util.Collections.emptyList()));
        assertEquals("缺少：胚料 0/3，余烬币 10/300",
                EmberGearNextHint.lackHalf(java.util.Arrays.asList("胚料 0/3", "余烬币 10/300")));
    }

    @Test public void costShort_shardsCores() {
        assertEquals("碎片4 币40", EmberGearNextHint.costShort(EmberUpgradeRules.enhanceCost(0)));
        assertEquals("碎片60 核心12 胚6 币1500", EmberGearNextHint.costShort(EmberUpgradeRules.upgradeCost(1)));
    }
}

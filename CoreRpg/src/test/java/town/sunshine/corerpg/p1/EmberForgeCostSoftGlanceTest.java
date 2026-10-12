package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * TIP-AS DESIGN §2.3: CostSoft glance honesty — soft peek Lines / HeldNext canPay / Sink bits.
 * Zero MULT / economy / pay-path change.
 */
public class EmberForgeCostSoftGlanceTest {

    private static EmberItemData bladeEnh(int enh, int pity) {
        return new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2",
                "burst", "blade", 2, 0, 0, enh, pity, false, "drop", 1, 0);
    }

    private static EmberItemData blade(int q, int craft, int enh) {
        return new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1, q, craft, enh, 0, true, "drop", 1, 1);
    }

    private static EmberItemData armor(String slot) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId("scorch", slot, 2),
                "scorch", slot, 2, 1, 1, 0, 0, true, "migrate", EmberItemData.DATA_VERSION, 0);
    }

    private static PlayerData softReady() {
        return new PlayerData(); // periodCount 0 → ready
    }

    private static PlayerData softUsedEnhance() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberEnhanceCostSoft.C_USED, "day", 1);
        return d;
    }

    /** enhance 全额 碎12 · soft ready → 半价 + 今日减负待用 */
    @Test
    public void enhanceLine_softReady_halfAndTail() {
        EmberItemData d = bladeEnh(3, 0);
        Cost full = EmberUpgradeRules.enhanceCheck(d).cost;
        assertEquals(12, full.shards);
        assertEquals(120, full.coins);
        PlayerData pd = softReady();
        String line = EmberGearNextHint.enhanceLine(d, pd);
        assertTrue(line.contains("碎片6"));
        assertTrue(line.contains("币60"));
        assertTrue(line.contains(EmberGearNextHint.SOFT_TAIL));
        assertFalse(line.contains("碎片12"));
    }

    /** soft 已用 → 全额；无减负尾 */
    @Test
    public void enhanceLine_softUsed_fullNoTail() {
        EmberItemData d = bladeEnh(3, 0);
        String line = EmberGearNextHint.enhanceLine(d, softUsedEnhance());
        assertTrue(line.contains("碎片12"));
        assertTrue(line.contains("币120"));
        assertFalse(line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** 一元 ≡ D307 旧断言 */
    @Test
    public void enhanceLine_unary_matchesD307() {
        assertEquals("+3→+4 · 碎片12 币120 · 85%（第1/3 · 含本次≤3次必成）",
                EmberGearNextHint.enhanceLine(bladeEnh(3, 0)));
        assertEquals(EmberGearNextHint.enhanceLine(bladeEnh(3, 0)),
                EmberGearNextHint.enhanceLine(bladeEnh(3, 0), null));
    }

    @Test
    public void qualityCraftUpgrade_softReady_halfAndTail() {
        PlayerData pd = softReady();
        EmberItemData q0 = EmberItemData.create("burst", "blade", 2, 0, 0, 0, false, "drop");
        String ql = EmberGearNextHint.qualityLine(q0, pd);
        assertTrue(ql.contains("胚4")); // full 8 → half 4
        assertTrue(ql.contains("骨4"));
        assertTrue(ql.contains("币400"));
        assertTrue(ql.contains(EmberGearNextHint.SOFT_TAIL));

        String cl = EmberGearNextHint.craftLine(q0, pd);
        assertTrue(cl.contains("胚1") || cl.contains("胚2")); // full 胚3 → max(1,floor(1.5))=1? floor(3*0.5)=1
        assertTrue(cl.contains(EmberGearNextHint.SOFT_TAIL));

        EmberItemData t1 = EmberItemData.create("burst", "blade", 1, 0, 0, 0, false, "drop");
        String ul = EmberGearNextHint.upgradeLine(t1, true, pd);
        assertTrue(ul.contains(EmberGearNextHint.SOFT_TAIL));
        // full shards 60 → 30
        assertTrue(ul.contains("碎片30"));
        assertFalse(ul.contains("碎片60"));
    }

    /** HeldNext：袋料只够半价 · soft ready → 料够（非还差料） */
    @Test
    public void heldNext_softReady_halfBag_enough() {
        // q2 craft3 → only enhance left; full enhance +3 = 碎12 币120 → soft 6/60
        EmberItemData d = blade(2, 3, 3);
        Cost full = EmberUpgradeRules.enhanceCheck(d).cost;
        assertNotNull(full);
        assertTrue(full.shards >= 2);
        Cost soft = EmberEnhanceCostSoft.apply(softReady(), full, false);
        PlayerData pd = softReady();
        // bag: only soft amount (not full)
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(soft.shards, 0, 0, soft.coins);
        assertFalse("full would shortfall", EmberForgeHeldNext.canPay(full, halfBag));
        assertTrue("soft payable", EmberForgeHeldNext.canPay(soft, halfBag));
        String line = EmberForgeHeldNext.line(d, halfBag, true, EmberForgeGoal.ENHANCE, pd);
        assertTrue(line, line.contains("料够"));
        assertFalse(line, line.contains("还差料"));
        assertTrue(line, line.contains(EmberGearNextHint.SOFT_TAIL) || line.contains("enhance"));
    }

    /** soft 已用 + 半价袋 → 还差料 */
    @Test
    public void heldNext_softUsed_halfBag_short() {
        EmberItemData d = blade(2, 3, 3);
        Cost full = EmberUpgradeRules.enhanceCheck(d).cost;
        Cost half = EmberEnhanceCostSoft.half(full);
        PlayerData used = softUsedEnhance();
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(half.shards, 0, 0, half.coins);
        String line = EmberForgeHeldNext.line(d, halfBag, true, EmberForgeGoal.ENHANCE, used);
        assertTrue(line, line.contains("还差料"));
        assertFalse(line, line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** 甲早退回归 TIP-AK */
    @Test
    public void heldNext_armor_refuse() {
        EmberForgeHeldNext.Mats rich = new EmberForgeHeldNext.Mats(9999, 9999, 9999, 9999);
        String line = EmberForgeHeldNext.line(armor("chest"), rich, true, EmberForgeGoal.ENHANCE, softReady());
        assertTrue(line.contains(EmberUpgradeRules.ARMOR_REFUSE) || line.contains("护甲随护符"));
        assertFalse(line.contains("料够"));
        assertFalse(line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** SinkGlance 非甲 soft ready → 费用串含减负尾 */
    @Test
    public void sinkCostBits_softReady_hasTail() {
        EmberItemData d = blade(0, 0, 3);
        List<String> bits = EmberSinkGlance.costBits(d, softReady());
        String joined = String.join(" · ", bits);
        assertTrue(joined, joined.contains(EmberGearNextHint.SOFT_TAIL));
        assertTrue(joined, joined.contains("成色") || joined.contains("精工") || joined.contains("强化"));
    }

    @Test
    public void hopeQualityRefine_softReady_tail() {
        PlayerData pd = softReady();
        EmberItemData d = EmberItemData.create("burst", "blade", 2, 0, 0, 0, false, "drop");
        String q = EmberHopeMeter.quality(d, pd);
        assertTrue(q, q.contains(EmberGearNextHint.SOFT_TAIL));
        String r = EmberHopeMeter.refine(d, pd);
        assertTrue(r, r.contains(EmberGearNextHint.SOFT_TAIL));
        // unary no tail
        assertFalse(EmberHopeMeter.quality(d).contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** peek never consumes */
    @Test
    public void softCost_peekNeverConsumes() {
        PlayerData pd = softReady();
        Cost full = new Cost(12, 0, 0, 0, 120);
        Cost a = EmberGearNextHint.softCost("enhance", pd, full);
        assertEquals(6, a.shards);
        assertTrue(EmberEnhanceCostSoft.ready(pd));
        EmberGearNextHint.enhanceLine(bladeEnh(3, 0), pd);
        EmberGearNextHint.qualityLine(EmberItemData.create("burst", "blade", 2, 0, 0, 0, false, "drop"), pd);
        assertTrue(EmberEnhanceCostSoft.ready(pd));
        assertTrue(EmberQualityCostSoft.ready(pd));
    }
}

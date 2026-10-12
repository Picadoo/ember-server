package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * TIP-AT DESIGN §2.3: Brand/Gem/Convert/Roll(+Pin) CostSoft glance honesty.
 * Zero MULT / economy / pay-path / LOCK soft invent.
 */
public class EmberBrandGemConvertRollCostSoftGlanceTest {

    /** q2 craft3 enh10 — quality/refine/enhance full so goal sinks surface. */
    private static EmberItemData blade() {
        return new EmberItemData("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1,
                2, 3, EmberTables.MAX_ENHANCE, 0, true, "drop", 1, 1);
    }

    private static EmberItemData armor(String slot) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId("scorch", slot, 2),
                "scorch", slot, 2, 1, 1, 0, 0, true, "migrate", EmberItemData.DATA_VERSION, 0);
    }

    private static PlayerData softReady() {
        return new PlayerData();
    }

    private static PlayerData used(String counter) {
        PlayerData d = new PlayerData();
        d.addPeriodCount(counter, "day", 1);
        return d;
    }

    /** BrandCraft soft ready · 袋碎仅够半价 → 料够 + 减负尾 */
    @Test
    public void brandHeldNext_softReady_halfBag_enough() {
        int full = EmberBrandRules.craftShards();
        assertTrue(full >= 2);
        int half = EmberBrandCraftSoft.apply(softReady(), full, false);
        assertTrue(half < full);
        assertEquals(Math.max(1, (int) Math.floor(full * EmberBrandCraftSoft.MULT)), half);
        PlayerData pd = softReady();
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(half, 0, 0, 0);
        assertFalse(EmberForgeHeldNext.canPay(new Cost(full, 0, 0, 0, 0), halfBag));
        String line = EmberForgeHeldNext.line(blade(), halfBag, true, EmberForgeGoal.BRAND, pd);
        assertTrue(line, line.contains("合成烙纹"));
        assertTrue(line, line.contains("料够"));
        assertFalse(line, line.contains("还差"));
        assertTrue(line, line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** soft 已用 + 半价袋 → 还差碎；无减负尾 */
    @Test
    public void brandHeldNext_softUsed_halfBag_short() {
        int full = EmberBrandRules.craftShards();
        int half = Math.max(1, (int) Math.floor(full * EmberBrandCraftSoft.MULT));
        PlayerData used = used(EmberBrandCraftSoft.C_USED);
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(half, 0, 0, 0);
        String line = EmberForgeHeldNext.line(blade(), halfBag, true, EmberForgeGoal.BRAND, used);
        assertTrue(line, line.contains("还差碎") || line.contains("还差"));
        assertFalse(line, line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** Gem soft ready → HeldNext 料够；recipe_gap_gem 半价差 */
    @Test
    public void gemHeldNextAndGap_softReady_half() {
        Cost full = EmberGemAcquire.craftCost();
        Cost soft = EmberGemCraftCostSoft.peek(softReady(), full);
        assertTrue(soft.shards < full.shards || soft.bone < full.bone || soft.coins < full.coins);
        PlayerData pd = softReady();
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(soft.shards, 0, soft.bone, soft.coins);
        assertFalse(EmberForgeHeldNext.canPay(full, halfBag));
        String line = EmberForgeHeldNext.line(blade(), halfBag, true, EmberForgeGoal.GEM, pd);
        assertTrue(line, line.contains("合成宝石"));
        assertTrue(line, line.contains("料够"));
        assertTrue(line, line.contains(EmberGearNextHint.SOFT_TAIL));

        // bag has soft shards only — gap at soft need = 0; full need would leave gap
        String gapSoft = EmberRunPapi.recipeGapGemCraft(soft.shards, soft.bone, soft.coins, pd);
        assertEquals("碎差0·骨差0·币差0", gapSoft);
        String gapFull = EmberRunPapi.recipeGapGemCraft(soft.shards, soft.bone, soft.coins, null);
        assertFalse("full gap should show shortfall", gapFull.equals("碎差0·骨差0·币差0"));
    }

    /** Convert / Roll soft ready → forge_gap 半价 */
    @Test
    public void convertRollGap_softReady_half() {
        PlayerData pd = softReady();
        Cost convFull = EmberConvertRules.cost(1);
        Cost convSoft = EmberGearNextHint.softCost("convert", pd, convFull);
        assertTrue(convSoft.blanks < convFull.blanks || convSoft.coins < convFull.coins);
        String cv = EmberRunPapi.forgeGapConvert(1, convSoft.blanks, convSoft.coins, pd);
        assertEquals("胚差0·币差0", cv);
        String cvFull = EmberRunPapi.forgeGapConvert(1, convSoft.blanks, convSoft.coins, null);
        assertFalse(cvFull.equals("胚差0·币差0"));

        Cost rollFull = EmberForgeRollRules.matCost();
        Cost rollSoft = EmberGearNextHint.softCost("roll", pd, rollFull);
        String ro = EmberRunPapi.forgeGapRoll(rollSoft.blanks, rollSoft.coins, pd);
        assertEquals("胚差0·币差0", ro);
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(0, rollSoft.blanks, 0, rollSoft.coins);
        String line = EmberForgeHeldNext.line(blade(), halfBag, true, EmberForgeGoal.ROLL, pd);
        assertTrue(line, line.contains("随机锻"));
        assertTrue(line, line.contains("料够"));
        assertTrue(line, line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** Pin soft ready → forge_gap_pin 半价差 */
    @Test
    public void pinGap_softReady_half() {
        PlayerData pd = softReady();
        int brands = EmberBrandRules.pinBrands(1);
        int coins = EmberBrandRules.pinCoins(1);
        int[] soft = EmberGearNextHint.softPin(pd, brands, coins);
        assertTrue(soft[0] < brands || soft[1] < coins);
        assertEquals("烙差0·币差0", EmberRunPapi.forgeGapPin(1, soft[0], soft[1], pd));
        assertFalse(EmberRunPapi.forgeGapPin(1, soft[0], soft[1], null).equals("烙差0·币差0"));
    }

    /** LOCK / 一元无 pd → 全额回归 */
    @Test
    public void lockAndNullPd_fullPrice() {
        Cost lock = EmberForgeHeldNext.lockRerollCost(1);
        // half lock bag — still short under LOCK (no soft invent)
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(
                Math.max(1, lock.shards / 2), 0, 0, Math.max(1, lock.coins / 2));
        assertFalse(EmberForgeHeldNext.canPay(lock, halfBag));
        String line = EmberForgeHeldNext.line(blade(), halfBag, true, EmberForgeGoal.LOCK, softReady());
        assertTrue(line, line.contains("锁定洗练"));
        assertTrue(line, line.contains("还差"));
        assertFalse(line, line.contains(EmberGearNextHint.SOFT_TAIL));

        // unary forgeGapBrand ≡ full
        assertEquals(EmberRunPapi.forgeGapBrand(0), EmberRunPapi.forgeGapBrand(0, null));
        assertEquals(String.valueOf(EmberBrandRules.craftShards()), EmberRunPapi.forgeGapBrand(0));
        // softCost null pd → full
        Cost full = new Cost(EmberBrandRules.craftShards(), 0, 0, 0, 0);
        assertSame(full, EmberGearNextHint.softCost("brand", null, full));
    }

    /** 甲早退回归 TIP-AK */
    @Test
    public void armor_refuse_regression() {
        EmberForgeHeldNext.Mats rich = new EmberForgeHeldNext.Mats(9999, 9999, 9999, 9999);
        String line = EmberForgeHeldNext.line(armor("chest"), rich, true, EmberForgeGoal.BRAND, softReady());
        assertTrue(line.contains(EmberUpgradeRules.ARMOR_REFUSE) || line.contains("护甲随护符"));
        assertFalse(line.contains("料够"));
        assertFalse(line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** peek never consumes any soft counter */
    @Test
    public void softCost_peekNeverConsumes() {
        PlayerData pd = softReady();
        Cost brand = new Cost(40, 0, 0, 0, 0);
        assertEquals(20, EmberGearNextHint.softCost("brand", pd, brand).shards);
        assertTrue(EmberBrandCraftSoft.ready(pd));
        Cost gem = EmberGemAcquire.craftCost();
        EmberGearNextHint.softCost("gem", pd, gem);
        assertTrue(EmberGemCraftCostSoft.ready(pd));
        EmberGearNextHint.softCost("convert", pd, EmberConvertRules.cost(1));
        assertTrue(EmberConvertCostSoft.ready(pd));
        EmberGearNextHint.softCost("roll", pd, EmberForgeRollRules.matCost());
        assertTrue(EmberForgeRollCostSoft.ready(pd));
        EmberGearNextHint.softPin(pd, 2, 300);
        assertTrue(EmberBrandPinCostSoft.ready(pd));
    }

    /** softCost brand/gem/convert/roll kinds wired */
    @Test
    public void softCost_kinds_halfWhenReady() {
        PlayerData pd = softReady();
        assertEquals(20, EmberGearNextHint.softCost("brand", pd, new Cost(40, 0, 0, 0, 0)).shards);
        Cost gem = EmberGemAcquire.craftCost();
        Cost gs = EmberGearNextHint.softCost("gem", pd, gem);
        assertEquals(Math.max(1, (int) Math.floor(gem.shards * 0.5)), gs.shards);
        Cost conv = EmberConvertRules.cost(2);
        Cost cs = EmberGearNextHint.softCost("convert", pd, conv);
        assertEquals(Math.max(1, (int) Math.floor(conv.blanks * 0.5)), cs.blanks);
        Cost roll = EmberForgeRollRules.matCost();
        Cost rs = EmberGearNextHint.softCost("roll", pd, roll);
        assertEquals(Math.max(1, (int) Math.floor(roll.blanks * 0.5)), rs.blanks);
    }
}

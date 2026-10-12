package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.EmberUpgradeRules.Cost;

/**
 * TIP-AV DESIGN §2.3: LOCK LockShard soft glance honesty.
 * Reuses RerollLockShardSoft / RerollCoinSoft — zero invent second soft / MULT / C11.
 */
public class EmberLockShardSoftGlanceTest {

    /** q2 craft3 enh10 — quality/refine/enhance full so LOCK goal surfaces. */
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

    private static PlayerData usedBothSofts() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberRerollLockShardSoft.C_USED, "day", 1);
        d.addPeriodCount(EmberRerollCoinSoft.C_USED, "day", 1);
        return d;
    }

    /** soft ready · 袋料仅够基础碎+半锁碎(+半币) → 料够 + 今日减负待用 */
    @Test
    public void lockHeldNext_softReady_halfBag_enough() {
        PlayerData pd = softReady();
        Cost soft = EmberForgeHeldNext.lockRerollCost(1, pd);
        Cost full = EmberForgeHeldNext.lockRerollCost(1);
        assertTrue("soft shards < full", soft.shards < full.shards);
        assertTrue("soft coins < full", soft.coins < full.coins);
        // T1: shard40 + lock40 → soft lock20 → shards60; coin300→150
        assertEquals(60, soft.shards);
        assertEquals(150, soft.coins);
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(soft.shards, 0, 0, soft.coins);
        assertFalse(EmberForgeHeldNext.canPay(full, halfBag));
        assertTrue(EmberForgeHeldNext.canPay(soft, halfBag));
        String line = EmberForgeHeldNext.line(blade(), halfBag, true, EmberForgeGoal.LOCK, pd);
        assertTrue(line, line.contains("锁定洗练"));
        assertTrue(line, line.contains("料够"));
        assertFalse(line, line.contains("还差"));
        assertTrue(line, line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** soft 已用 / 无 pd → 全额；半袋还差；无 softTail */
    @Test
    public void lockHeldNext_usedOrNull_fullNoTail() {
        Cost full = EmberForgeHeldNext.lockRerollCost(1);
        Cost softUsed = EmberForgeHeldNext.lockRerollCost(1, usedBothSofts());
        assertEquals(full.shards, softUsed.shards);
        assertEquals(full.coins, softUsed.coins);
        assertEquals(full.shards, EmberForgeHeldNext.lockRerollCost(1, null).shards);

        // bag = soft half amounts — short when soft already used
        Cost wouldSoft = EmberForgeHeldNext.lockRerollCost(1, softReady());
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(
                wouldSoft.shards, 0, 0, wouldSoft.coins);
        String usedLine = EmberForgeHeldNext.line(blade(), halfBag, true, EmberForgeGoal.LOCK, usedBothSofts());
        assertTrue(usedLine, usedLine.contains("还差"));
        assertFalse(usedLine, usedLine.contains(EmberGearNextHint.SOFT_TAIL));

        String nullLine = EmberForgeHeldNext.line(blade(), halfBag, true, EmberForgeGoal.LOCK, null);
        assertTrue(nullLine, nullLine.contains("还差"));
        assertFalse(nullLine, nullLine.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** peek never consumes LockShard / Coin soft counters */
    @Test
    public void lockRerollCost_peekNeverConsumes() {
        PlayerData pd = softReady();
        EmberForgeHeldNext.lockRerollCost(1, pd);
        EmberForgeHeldNext.lockRerollCost(2, pd);
        assertTrue(EmberRerollLockShardSoft.ready(pd));
        assertTrue(EmberRerollCoinSoft.ready(pd));
    }

    /** 菜单/邀请 lock_shard 展示：半碎 + 减负尾 */
    @Test
    public void lockShardExtra_softReady_halfAndTail() {
        int full = EmberEconomy.amount("C11", "t1.lock_shard");
        assertEquals(40, full);
        String extra = EmberForgeHeldNext.lockShardExtra(softReady(), full);
        assertTrue(extra, extra.startsWith("20 碎片"));
        assertTrue(extra, extra.contains(EmberGearNextHint.SOFT_TAIL));
        String used = EmberForgeHeldNext.lockShardExtra(usedBothSofts(), full);
        assertEquals("40 碎片", used);
        assertFalse(used.contains(EmberGearNextHint.SOFT_TAIL));
        assertEquals("40 碎片", EmberForgeHeldNext.lockShardExtra(null, full));
    }

    /** Sink LOCK soft Cost == soft lockRerollCost（lack 按半锁碎） */
    @Test
    public void sinkLockCost_softReady_matchesHeldNext() {
        PlayerData pd = softReady();
        Cost soft = EmberForgeHeldNext.lockRerollCost(1, pd);
        // SinkGlance wires lockRerollCost(tier, pd) — same SoT
        assertEquals(60, soft.shards);
        assertEquals(150, soft.coins);
        Cost used = EmberForgeHeldNext.lockRerollCost(1, usedBothSofts());
        assertEquals(80, used.shards); // 40+40
        assertEquals(300, used.coins);
    }

    /** 甲早退回归 TIP-AK */
    @Test
    public void armor_refuse_regression() {
        EmberForgeHeldNext.Mats rich = new EmberForgeHeldNext.Mats(9999, 9999, 9999, 9999);
        String line = EmberForgeHeldNext.line(armor("chest"), rich, true, EmberForgeGoal.LOCK, softReady());
        assertTrue(line.contains(EmberUpgradeRules.ARMOR_REFUSE) || line.contains("护甲随护符"));
        assertFalse(line.contains("料够"));
        assertFalse(line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** AT BRAND soft / AS enhance soft 回归不破 */
    @Test
    public void atBrand_asEnhance_regression() {
        int full = EmberBrandRules.craftShards();
        int half = EmberBrandCraftSoft.apply(softReady(), full, false);
        EmberForgeHeldNext.Mats halfBag = new EmberForgeHeldNext.Mats(half, 0, 0, 0);
        String brand = EmberForgeHeldNext.line(blade(), halfBag, true, EmberForgeGoal.BRAND, softReady());
        assertTrue(brand, brand.contains("料够"));
        assertTrue(brand, brand.contains(EmberGearNextHint.SOFT_TAIL));

        EmberItemData enh = new EmberItemData(EmberItemData.newUid(), "ember_v1_burst_blade_t2",
                "burst", "blade", 2, 0, 0, 3, 0, false, "drop", 1, 0);
        String el = EmberGearNextHint.enhanceLine(enh, softReady());
        assertTrue(el.contains(EmberGearNextHint.SOFT_TAIL));
        assertTrue(el.contains("碎片6"));
    }

    /** unary lockRerollCost ≡ full; MULT/C_USED unchanged */
    @Test
    public void unary_and_softConstants_unchanged() {
        assertEquals(0.5, EmberRerollLockShardSoft.MULT, 0.0);
        assertEquals(0.5, EmberRerollCoinSoft.MULT, 0.0);
        assertEquals("p1_reroll_lock_shard_soft", EmberRerollLockShardSoft.C_USED);
        assertEquals("p1_reroll_coin_soft", EmberRerollCoinSoft.C_USED);
        Cost a = EmberForgeHeldNext.lockRerollCost(1);
        Cost b = EmberForgeHeldNext.lockRerollCost(1, null);
        assertEquals(a.shards, b.shards);
        assertEquals(a.coins, b.coins);
        assertEquals(80, a.shards);
        assertEquals(300, a.coins);
    }
}

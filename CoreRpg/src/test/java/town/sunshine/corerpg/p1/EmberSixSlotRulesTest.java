package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * D318 六槽 T1-3（取整一致 · K0 · 白板 ×0.1 零头）and T1-7（掉落口径：只有完整通关 +1 甲、四部位均匀、其它来源 0、来源行）.
 */
public class EmberSixSlotRulesTest {

    static EmberItemData piece(String fam, String slot, int tier, int q, int f, int e, String src) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId(tier == 0 ? "none" : fam, slot, tier),
                tier == 0 ? "none" : fam, slot, tier, q, f, e, 0, true, src, EmberItemData.DATA_VERSION, 0);
    }

    // ------------------------------------------------------------------ T1-3

    /** charm costs = today's integer Cost (×1.0, price table untouched) */
    @Test public void charmCostsAreTodaysIntegers() {
        EmberItemData c = piece("burst", "charm", 2, 0, 0, 0, "drop");
        assertEquals(new EmberUpgradeRules.Cost(4, 0, 0, 0, 40), EmberUpgradeRules.enhanceCheck(c).cost);
        assertEquals(new EmberUpgradeRules.Cost(60, 15, 6, 0, 1800), EmberUpgradeRules.upgrade(c, true).cost);
        assertEquals(new EmberUpgradeRules.Cost(0, 0, 3, 5, 300), EmberUpgradeRules.refine(c).cost);
        assertEquals(new EmberUpgradeRules.Cost(0, 0, 8, 8, 800), EmberUpgradeRules.quality(c).cost);
        int[] shards = {0, 4, 6, 8, 12, 16, 22, 30, 40, 54, 72};
        int[] coins = {0, 40, 60, 80, 120, 180, 260, 380, 540, 760, 1080};
        for (int e = 0; e < 10; e++) {
            EmberUpgradeRules.Cost k = EmberUpgradeRules.enhanceCost(e);
            assertEquals(shards[e + 1], k.shards);
            assertEquals(coins[e + 1], k.coins);
        }
        assertEquals(new EmberUpgradeRules.Cost(0, 0, 6, 10, 600), EmberUpgradeRules.refineCost(1));
        assertEquals(new EmberUpgradeRules.Cost(0, 0, 12, 20, 1200), EmberUpgradeRules.refineCost(2));
        assertEquals(new EmberUpgradeRules.Cost(0, 0, 16, 16, 1600), EmberUpgradeRules.qualityCost(1));
        assertEquals(2, EmberUpgradeRules.dismantleYield(piece("burst", "charm", 2, 0, 0, 0, "drop"))); // blade / charm yield unchanged
        assertEquals(3, EmberUpgradeRules.dismantleYield(piece("burst", "blade", 3, 0, 0, 0, "drop")));
    }

    /** K0: every forge track on armor is refused with nothing to pay */
    @Test public void armorForgeRefusedWithoutCost() {
        for (String slot : EmberItemData.ARMOR_SLOTS) {
            EmberItemData a = piece("scorch", slot, 2, 1, 1, 0, "drop");
            for (EmberUpgradeRules.Plan p : new EmberUpgradeRules.Plan[]{EmberUpgradeRules.enhanceCheck(a), EmberUpgradeRules.enhance(a, 0.1),
                    EmberUpgradeRules.upgrade(a, true), EmberUpgradeRules.refine(a), EmberUpgradeRules.quality(a)}) {
                assertFalse(p.ok());
                assertEquals(EmberUpgradeRules.ARMOR_REFUSE, p.error);
                assertEquals(EmberUpgradeRules.Cost.NONE, p.cost);
                assertNull(p.after);
            }
            EmberUpgradeRules.SwapPlan sw = EmberUpgradeRules.swap(a, piece("scorch", slot, 2, 0, 0, 0, "drop"));
            assertFalse(sw.ok());
            assertEquals(EmberUpgradeRules.ARMOR_REFUSE, sw.error);
            assertNull(sw.a);
            assertFalse(EmberUpgradeRules.swap(piece("scorch", "charm", 2, 0, 0, 3, "drop"), a).ok());
            assertNull("armor stays dismantlable (§2.3)", EmberUpgradeRules.dismantleCheck(a));
            assertEquals("whole blanks only through the tenths ledger", 0, EmberUpgradeRules.dismantleYield(a));
        }
        assertTrue(EmberUpgradeRules.ARMOR_REFUSE.contains("护符"));
    }

    /** 0.1 × tier, ten times tier 1 = exactly one blank (integer tenths, no float accumulation) */
    @Test public void tenthsLedgerIsExact() {
        EmberItemData t1 = piece("burst", "boots", 1, 0, 0, 0, "drop");
        int have = 0, blanks = 0;
        for (int i = 0; i < 10; i++) {
            int[] r = EmberUpgradeRules.addTenths(have, EmberUpgradeRules.armorDismantleTenths(t1));
            blanks += r[0];
            have = r[1];
            if (i < 9) assertEquals(0, blanks);
        }
        assertEquals(1, blanks);
        assertEquals(0, have);
        // conservation over a long random sequence: 10 × blanks + rest == Σ tenths
        java.util.Random rnd = new java.util.Random(7);
        int sum = 0; have = 0; blanks = 0;
        for (int i = 0; i < 100000; i++) {
            int tier = 1 + rnd.nextInt(3);
            int add = EmberUpgradeRules.armorDismantleTenths(piece("scorch", "head", tier, 0, 0, 0, "drop"));
            assertEquals(tier, add);
            sum += add;
            int[] r = EmberUpgradeRules.addTenths(have, add);
            blanks += r[0]; have = r[1];
            assertTrue(have >= 0 && have <= 9);
        }
        assertEquals(sum, 10 * blanks + have);
        assertEquals(0, EmberUpgradeRules.armorDismantleTenths(piece("scorch", "head", 2, 0, 0, 0, "migrate")));
        assertEquals(0, EmberUpgradeRules.armorDismantleTenths(piece("scorch", "head", 2, 0, 0, 0, "quest")));
        assertEquals(0, EmberUpgradeRules.armorDismantleTenths(piece("none", "head", 0, 0, 0, 0, "drop")));
        assertEquals(0, EmberUpgradeRules.armorDismantleTenths(piece("scorch", "charm", 2, 0, 0, 0, "drop")));
        assertNotNull("migrated armor is not dismantlable", EmberUpgradeRules.dismantleCheck(piece("scorch", "head", 2, 0, 0, 0, "migrate")));
        assertNotNull(EmberCounters.byKey(EmberForgeService.C_BLANK_TENTHS));
        assertTrue(EmberCounters.byKey(EmberForgeService.C_BLANK_TENTHS).asset());
    }

    // ------------------------------------------------------------------ T1-7

    static EmberRunRules.SettleInput input(long seed, boolean six, EmberRunRules.FirstClear fc) {
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = "q03-k3x9a-" + Long.toString(seed, 36);
        in.player = "00000000-0000-0000-0000-00000000000" + (seed % 10);
        in.seed = seed;
        in.tier = 2;
        in.target = "burst";
        in.bossKilled = true;
        in.firstClear = fc;
        in.sixArmor = six;
        return in;
    }

    @Test public void switchOffSettleIsUnchanged() {
        for (long seed = 1; seed < 300; seed++) {
            EmberRunRules.SettleInput a = input(seed, false, null);
            List<EmberRunRules.Grant> g = EmberRunRules.settle(a);
            for (EmberRunRules.Grant x : g) {
                assertFalse(x.key.startsWith("six_"));
                if (x.item != null) assertFalse(EmberItemData.isArmorSlot(x.item.slot));
            }
        }
    }

    @Test public void onePieceUniformOverSlotsOthersUntouched() {
        int[] slots = new int[4];
        int n = 40000;
        for (long seed = 1; seed <= n; seed++) {
            List<EmberRunRules.Grant> off = EmberRunRules.settle(input(seed, false, null));
            List<EmberRunRules.Grant> on = EmberRunRules.settle(input(seed, true, null));
            assertEquals(off.size() + 1, on.size());
            List<String> offEnc = new ArrayList<String>(), onEnc = new ArrayList<String>();
            EmberRunRules.Grant armor = null;
            for (EmberRunRules.Grant x : off) offEnc.add(x.toString());
            for (EmberRunRules.Grant x : on) { if (EmberRunRules.G_SIX_ARMOR.equals(x.key)) armor = x; else onEnc.add(x.toString()); }
            assertEquals("every other grant identical (own seeded stream)", offEnc, onEnc);
            assertNotNull(armor);
            assertEquals(EmberRunRules.Kind.ITEM, armor.kind);
            assertEquals(2, armor.item.tier);
            slots[EmberItemData.armorIndex(armor.item.slot)]++;
            assertEquals(armor.toString(), EmberRunRules.settle(input(seed, true, null)).get(on.indexOf(armor)).toString()); // deterministic
        }
        for (int c : slots) assertEquals(n / 4.0, c, n * 0.01); // ±1 pp of 25 %
        assertEquals("S39", EmberProvenance.itemSource(EmberRunRules.G_SIX_ARMOR, "q03-k3x9a-1b2c"));
        assertEquals("S39", EmberEconomy.byId("S39").id);
        EmberRunRules.SettleInput noBoss = input(5, true, null);
        noBoss.bossKilled = false;
        assertTrue("no boss kill → nothing, armor included", EmberRunRules.settle(noBoss).isEmpty());
    }

    @Test public void q01FirstClearGivesFourT1StarterPieces() {
        EmberRunRules.FirstClear fc = new EmberRunRules.FirstClear("q01", "charm", 1, 0, 0, 0, "q02");
        EmberRunRules.SettleInput in = input(11, true, fc);
        in.sixStarter = true;
        List<EmberRunRules.Grant> g = EmberRunRules.settle(in);
        List<String> got = new ArrayList<String>();
        for (EmberRunRules.Grant x : g) if (x.key.startsWith("fc_q01_armor_")) {
            got.add(x.item.slot);
            assertEquals("burst", x.item.family);
            assertEquals(1, x.item.tier);
            assertEquals(0, x.item.quality);
            assertEquals(0, x.item.craft);
            assertEquals("S06", EmberProvenance.itemSource(x.key, in.runId));
        }
        assertEquals(EmberItemData.ARMOR_SLOTS, got);
        in.sixStarter = false;
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) assertFalse(x.key.contains("_armor_"));
        in.target = null;
        in.sixStarter = true;
        String fam = null;
        for (EmberRunRules.Grant x : EmberRunRules.settle(in)) if (x.key.startsWith("fc_q01_armor_")) {
            if (fam == null) fam = x.item.family;
            assertEquals("no target: one family for all four", fam, x.item.family);
            assertTrue(EmberRunRules.validFamily(fam));
        }
    }
}

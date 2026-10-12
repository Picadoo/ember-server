package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-AU DESIGN §2.3: imprint/attune spend Hope soft honesty.
 * Zero MULT / IMPRINT_MARKS / ALT_MARKS / coin table / STAMP / new PAPI.
 */
public class EmberImprintAttuneSpendHopeSoftTest {

    private static PlayerData softReady() {
        return new PlayerData();
    }

    private static PlayerData used(String counter) {
        PlayerData d = new PlayerData();
        d.addPeriodCount(counter, "day", 1);
        return d;
    }

    /** soft ready · imprint hope: half marks/coin + 今日减负待用; enough marks not false-neg coin */
    @Test
    public void imprintHope_softReady_halfAndTail() {
        PlayerData d = softReady();
        d.setCoin(150); // half of T1 imprint coin (300) — enough under soft, short under full
        int fullM = EmberSignature.imprintMarks();
        int fullC = EmberSignature.imprintCoin(1);
        assertTrue(fullM >= 2);
        assertTrue(fullC >= 2);
        int needM = EmberImprintMarkSoft.need(d, false);
        int needC = EmberImprintCoinSoft.apply(d, fullC, false);
        assertEquals(Math.max(1, (int) Math.floor(fullM * EmberImprintMarkSoft.MULT)), needM);
        assertEquals(Math.max(1, (int) Math.floor(fullC * EmberImprintCoinSoft.MULT)), needC);
        assertTrue(needM < fullM);
        assertTrue(needC < fullC);
        // marks at soft need → mark ready; coin at soft need → coin ok (no 币差假负)
        String hope = EmberHopeMeter.imprintSpend(needM, needM, d.getCoin(), needC)
                + EmberGearNextHint.softTail(EmberImprintMarkSoft.ready(d) || EmberImprintCoinSoft.ready(d));
        assertTrue(hope, hope.contains("徽记够") || hope.contains("可烙"));
        assertFalse(hope, hope.contains("币差"));
        assertTrue(hope, hope.contains(EmberGearNextHint.SOFT_TAIL));
        // full-price hope with same bag would false-neg coin
        String fullHope = EmberHopeMeter.imprintSpend(needM, fullM, d.getCoin(), fullC);
        assertTrue(fullHope, fullHope.contains("币差") || fullHope.contains("/" + fullC));
    }

    /** soft 已用 / 无 pd → 全额回归 */
    @Test
    public void imprintHope_usedOrNull_full() {
        int fullM = EmberSignature.imprintMarks();
        int fullC = EmberSignature.imprintCoin(1);
        PlayerData usedM = used(EmberImprintMarkSoft.C_USED);
        PlayerData usedC = used(EmberImprintCoinSoft.C_USED);
        assertEquals(fullM, EmberImprintMarkSoft.need(usedM, false));
        assertEquals(fullC, EmberImprintCoinSoft.apply(usedC, fullC, false));
        assertEquals(fullM, EmberImprintMarkSoft.need(null, false));
        assertEquals(fullC, EmberImprintCoinSoft.apply(null, fullC, false));
        String hope = EmberHopeMeter.imprintSpend(0, fullM, 0, fullC)
                + EmberGearNextHint.softTail(false);
        assertFalse(hope.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** impPapi cost numbers ≡ imprintCheck soft peek */
    @Test
    public void impCost_matchesImprintCheckSoft() {
        PlayerData d = softReady();
        EmberItemData blade = new EmberItemData("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1,
                1, 1, 0, 0, true, "drop", 1, 1);
        int needM = EmberImprintMarkSoft.need(d, false);
        int needC = EmberImprintCoinSoft.apply(d, EmberSignature.imprintCoin(blade.tier), false);
        // cost display uses same needM/needC; check with soft numbers passes when bag matches soft
        d.setCoin(needC);
        // marks via period — imprintCheck takes marks arg
        String why = EmberSignature.imprintCheck(
                EmberSignature.byId("L01"), blade, true, true, needM, d.getCoin(), 0, needM, needC);
        assertNull("soft bag should pass imprintCheck", why);
        // full need with soft bag → fail
        String whyFull = EmberSignature.imprintCheck(
                EmberSignature.byId("L01"), blade, true, true, needM, d.getCoin(), 0,
                EmberSignature.imprintMarks(), EmberSignature.imprintCoin(blade.tier));
        assertNotNull(whyFull);
        String costLike = "§7花费：§f" + needM + " §7枚 Q01 首领徽记（有 §a" + needM
                + "§7）+ §f" + needC + " §7余烬币（有 §a" + needC + "§7）"
                + EmberGearNextHint.softTail(true);
        assertTrue(costLike.contains(String.valueOf(needM)));
        assertTrue(costLike.contains(String.valueOf(needC)));
        assertTrue(costLike.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** attune_hope soft ready → half marks + softTail */
    @Test
    public void attuneHope_softReady_halfAndTail() {
        PlayerData d = softReady();
        int full = EmberSignature.altMarks();
        assertTrue(full >= 2);
        int needA = EmberAttuneUnlockSoft.need(d, false);
        assertEquals(Math.max(1, (int) Math.floor(full * EmberAttuneUnlockSoft.MULT)), needA);
        String hope = EmberHopeMeter.attuneUnlock(needA, needA)
                + EmberGearNextHint.softTail(EmberAttuneUnlockSoft.ready(d));
        assertTrue(hope, hope.contains("Shift解锁调律"));
        assertTrue(hope, hope.contains(EmberGearNextHint.SOFT_TAIL));
        // under soft marks vs full need → not ready
        String under = EmberHopeMeter.attuneUnlock(needA, full);
        assertFalse(under, under.contains("Shift解锁调律"));
    }

    /** SigChase remain soft ready · marks=softNeed → 徽记够 / ready */
    @Test
    public void sigChaseRemain_softReady_marksEnough() {
        EmberSignature.Def def = EmberSignature.byId("L01");
        assertNotNull(def);
        PlayerData d = softReady();
        int softNeed = EmberImprintMarkSoft.need(d, false);
        assertTrue(softNeed < EmberSignature.imprintMarks());
        String rem = EmberSigChase.remainLine(def, softNeed, null, softNeed);
        assertTrue(rem, rem.contains("徽记够"));
        assertTrue(rem, rem.contains("可烙"));
        String prog = EmberSigChase.progressLine(def, softNeed, null, softNeed);
        assertTrue(prog, prog.contains("追烙就绪"));
        // tip with soft need
        assertTrue(EmberSigChase.tip(def, softNeed).contains(softNeed + " 枚可烙"));
        // full remain with soft marks still gap
        String fullRem = EmberSigChase.remainLine(def, softNeed);
        assertTrue(fullRem, fullRem.contains("还差"));
    }

    /** autoAttuneUnlock soft ready → 扣半徽记 (AttuneUnlockSoft.need consume) */
    @Test
    public void autoAttune_softReady_halfCharge() {
        PlayerData d = softReady();
        int full = EmberSignature.altMarks();
        int pay = EmberAttuneUnlockSoft.need(d, true);
        assertEquals(Math.max(1, (int) Math.floor(full * EmberAttuneUnlockSoft.MULT)), pay);
        assertTrue(pay < full);
        // second consume = full (day latch)
        assertEquals(full, EmberAttuneUnlockSoft.need(d, true));
        assertFalse(EmberAttuneUnlockSoft.ready(d));
    }

    /** AT HeldNext brand soft regression */
    @Test
    public void at_brandSoft_regression() {
        PlayerData pd = softReady();
        int full = EmberBrandRules.craftShards();
        int half = EmberBrandCraftSoft.apply(pd, full, false);
        EmberItemData blade = new EmberItemData("cccccccccccccccccccccccccccccccc",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1,
                2, 3, EmberTables.MAX_ENHANCE, 0, true, "drop", 1, 1);
        EmberForgeHeldNext.Mats bag = new EmberForgeHeldNext.Mats(half, 0, 0, 0);
        String line = EmberForgeHeldNext.line(blade, bag, true, EmberForgeGoal.BRAND, pd);
        assertTrue(line, line.contains(EmberGearNextHint.SOFT_TAIL));
        assertTrue(line, line.contains("料够"));
    }

    /** AO remain readyTail regression */
    @Test
    public void ao_remainReadyTail_regression() {
        EmberSignature.Def l01 = EmberSignature.byId("L01");
        int need = EmberSignature.imprintMarks();
        EmberTables tables = EmberTables.defaults();
        EmberItemData blade = EmberItemData.create("scorch", "blade", 1, 1, 0, 0, true, "drop");
        EmberLoadout blo = EmberLoadout.compute(tables, blade, null, 10);
        String rem = EmberSigChase.remainLine(l01, need, blo);
        assertTrue(rem, rem.contains("可烙刃") || rem.contains("可烙"));
    }

    /** AK armor refuse regression */
    @Test
    public void ak_armorRefuse_regression() {
        EmberItemData chest = new EmberItemData(EmberItemData.newUid(),
                EmberItemData.templateId("scorch", "chest", 2),
                "scorch", "chest", 2, 1, 1, 0, 0, true, "migrate", EmberItemData.DATA_VERSION, 0);
        EmberForgeHeldNext.Mats rich = new EmberForgeHeldNext.Mats(9999, 9999, 9999, 9999);
        String line = EmberForgeHeldNext.line(chest, rich, true, EmberForgeGoal.ENHANCE, softReady());
        assertTrue(line.contains(EmberUpgradeRules.ARMOR_REFUSE) || line.contains("护甲随护符"));
        assertFalse(line.contains(EmberGearNextHint.SOFT_TAIL));
    }

    /** peek never consumes imprint/attune soft counters */
    @Test
    public void softPeek_neverConsumes() {
        PlayerData d = softReady();
        int m1 = EmberImprintMarkSoft.need(d, false);
        int m2 = EmberImprintMarkSoft.need(d, false);
        assertEquals(m1, m2);
        assertTrue(EmberImprintMarkSoft.ready(d));
        int c1 = EmberImprintCoinSoft.apply(d, 300, false);
        int c2 = EmberImprintCoinSoft.apply(d, 300, false);
        assertEquals(c1, c2);
        assertTrue(EmberImprintCoinSoft.ready(d));
        int a1 = EmberAttuneUnlockSoft.need(d, false);
        int a2 = EmberAttuneUnlockSoft.need(d, false);
        assertEquals(a1, a2);
        assertTrue(EmberAttuneUnlockSoft.ready(d));
    }
}

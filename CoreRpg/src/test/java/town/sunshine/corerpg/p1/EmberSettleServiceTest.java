package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * D238 / ARCH S3-9: settlement helpers moved from {@code EmberRunService} into {@link EmberSettleService}.
 * Pins fail-refund / not-eligible texts, rotation mark amount, and grant-key order/amounts for a plain
 * Q01 clear (+ featured rotation / variety / bounty) against the pre-extract concatenations (bv60 · D296 event_rate).
 */
public final class EmberSettleServiceTest {

    private static EmberRunMaps bundled() {
        InputStream in = EmberSettleServiceTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    @Test public void failRefundTexts_matchPreExtract() {
        EmberRunMaps maps = bundled();
        assertEquals(0.5, maps.failRefund, 1e-9);
        int pct = EmberSettleService.failRefundPct(maps.failRefund);
        assertEquals(50, pct);
        int amount = EmberRunRules.failRefundAmount(maps.cost, maps.failRefund);
        assertEquals(15, amount);
        assertEquals("§a每天第一次失败退还 50% 体力（15 点；不给掉落和币）",
                EmberSettleService.failRefundAvailableText(pct, amount));
        assertEquals("§8今天的失败退还已用过（明天 0 点再有）", EmberSettleService.failRefundUsedText());
        assertEquals("§a · 今天第一次挑战失败：退还 15 体力（花费的 50%，每天一次；不给掉落和币）",
                EmberSettleService.failRefundGrantedSuffix(15, 50, false));
        assertEquals("§a · 今天第一次挑战失败：退还 15 体力（花费的 50%，每天一次；不给掉落和币，层费不退）",
                EmberSettleService.failRefundGrantedSuffix(15, 50, true));
        assertEquals("§c（今天的失败退还已经用过，明天再有；未结算的额外奖励作废）",
                EmberSettleService.failRefundAlreadyUsedSuffix());
        assertEquals("§c（已开战不退体力；未结算的额外奖励作废）",
                EmberSettleService.failRefundIneligibleSuffix());
    }

    @Test public void notEligibleAndBossCleared_matchPreExtract() {
        assertTrue(EmberSettleService.notEligibleText().contains("本局没有你的结算资格"));
        assertEquals("§a烬核守卫 已击败 · 结算完成，实例稍后关闭",
                EmberSettleService.bossClearedText("烬核守卫"));
    }

    @Test public void rotationMarkAmount_challengeVsNormal() {
        EmberRunMaps maps = bundled();
        assertEquals(maps.rotationBonusMarks,
                EmberSettleService.rotationMarkAmount(true, maps.rotationBonusMarks, maps.rotationNormalBonusMarks));
        assertEquals(maps.rotationNormalBonusMarks,
                EmberSettleService.rotationMarkAmount(false, maps.rotationBonusMarks, maps.rotationNormalBonusMarks));
    }

    @Test public void appendBonuses_q01FirstClear_orderAndAmounts() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef q01 = maps.byKey("q01");
        assertNotNull(q01);
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = "q01-test-fc";
        in.player = UUID.randomUUID().toString();
        in.seed = 42L;
        in.tier = q01.tier;
        in.target = "scorch";
        in.bossKilled = true;
        in.extra = EmberRunRules.Extra.NONE;
        in.extraDone = false;
        in.firstClear = q01.firstClear;
        in.loot = maps.lootBias(q01);

        List<EmberRunRules.Grant> grants = new ArrayList<EmberRunRules.Grant>(EmberRunRules.settle(in));
        List<String> baseKeys = EmberSettleService.grantKeys(grants);
        assertTrue(baseKeys.contains("base_coin"));
        assertTrue(baseKeys.contains("base_item"));
        boolean fcPkg = false;
        for (String k : baseKeys) if (k.startsWith("fc_q01_")) { fcPkg = true; break; }
        assertTrue("first clear package present", fcPkg);

        // first clear of signature map → fc_sigmark (no rot / variety / bounty in this pin)
        EmberSettleService.appendBonuses(grants, in, q01, maps.raidItemQualityFloor, q01.tier,
                false, 0, false, null, 0, true,
                true, false, maps.variety.affixShard, false, maps.variety.eventCore, "timed",
                Collections.<EmberRunRules.Grant>emptyList());
        List<String> keys = EmberSettleService.grantKeys(grants);
        assertTrue(keys.contains("fc_sigmark"));
        // fc_sigmark after base (+ first-clear package); no rot_mark / raid / sig_mark on first clear path
        assertFalse(keys.contains("rot_mark"));
        assertFalse(keys.contains("raid_item"));
        assertFalse(keys.contains("sig_mark"));
        EmberRunRules.Grant fc = null;
        for (EmberRunRules.Grant g : grants) if ("fc_sigmark".equals(g.key)) fc = g;
        assertNotNull(fc);
        assertEquals(EmberSignature.FC_MARKS, fc.amount);
        assertEquals("q01", fc.id);
    }

    @Test public void appendBonuses_q01Repeat_rotationVarietyBountyOrder() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef q01 = maps.byKey("q01");
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = "q01-test-rep";
        in.player = UUID.randomUUID().toString();
        in.seed = 99L;
        in.tier = q01.tier;
        in.target = "scorch";
        in.bossKilled = true;
        in.extra = EmberRunRules.Extra.NONE;
        in.extraDone = false;
        in.firstClear = null;
        in.loot = maps.lootBias(q01);

        List<EmberRunRules.Grant> grants = new ArrayList<EmberRunRules.Grant>(EmberRunRules.settle(in));
        EmberRunRules.Grant baseItem = null;
        for (EmberRunRules.Grant g : grants) if ("base_item".equals(g.key)) baseItem = g;
        assertNotNull(baseItem);

        int rotMarks = EmberSettleService.rotationMarkAmount(false, maps.rotationBonusMarks, maps.rotationNormalBonusMarks);
        List<EmberRunRules.BountyTier> tiers = EmberRunRules.bountyTiers(null);
        // use bundled bounty via maps path — EmberRunService.bountyTiers needs live service; build from settle rules empty
        // Pin with a synthetic 1-clear bounty crossing: prev=0 now=1
        List<EmberRunRules.BountyTier> one = Arrays.asList(new EmberRunRules.BountyTier(1, 50, 0, 0, 0));
        List<EmberRunRules.Grant> bountyPaid = EmberRunRules.bountyGrants(one, 0, 1);
        assertEquals(1, bountyPaid.size());
        assertEquals("bounty_coin_1", bountyPaid.get(0).key);
        assertEquals(50, bountyPaid.get(0).amount);

        EmberSettleService.appendBonuses(grants, in, q01, maps.raidItemQualityFloor, q01.tier,
                true, rotMarks, true, baseItem, 0, false,
                true, true, maps.variety.affixShard, true, maps.variety.eventCore, "timed",
                bountyPaid);

        List<String> keys = EmberSettleService.grantKeys(grants);
        int rotAt = keys.indexOf("rot_mark");
        int sigAt = keys.indexOf("sig_mark");
        int affixAt = keys.indexOf("var_affix_shard");
        int eventAt = keys.indexOf("var_event_core");
        int bountyAt = keys.indexOf("bounty_coin_1");
        assertTrue("rot_mark present", rotAt >= 0);
        assertTrue("sig_mark present", sigAt >= 0);
        assertTrue("var_affix_shard present", affixAt >= 0);
        assertTrue("var_event_core present", eventAt >= 0);
        assertTrue("bounty present", bountyAt >= 0);
        // order: rot → signature → variety → bounty (pledge skipped; no fc)
        assertTrue(rotAt < sigAt);
        assertTrue(sigAt < affixAt);
        assertTrue(affixAt < eventAt);
        assertTrue(eventAt < bountyAt);

        EmberRunRules.Grant rot = null;
        for (EmberRunRules.Grant g : grants) if ("rot_mark".equals(g.key)) rot = g;
        assertNotNull(rot);
        assertEquals(String.valueOf(q01.tier), rot.id);
        assertEquals(rotMarks, rot.amount);
        assertEquals(maps.rotationNormalBonusMarks, rot.amount);

        EmberRunRules.Grant aff = null, ev = null;
        for (EmberRunRules.Grant g : grants) {
            if ("var_affix_shard".equals(g.key)) aff = g;
            if ("var_event_core".equals(g.key)) ev = g;
        }
        assertNotNull(aff);
        assertNotNull(ev);
        assertEquals(maps.variety.affixShard, aff.amount);
        assertEquals(maps.variety.eventCore, ev.amount);
    }

    @Test public void appendBonuses_raidGrantsBeforeRotation() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef r01 = maps.byKey("r01");
        assertNotNull(r01);
        assertTrue(r01.raid);
        EmberRunRules.SettleInput in = new EmberRunRules.SettleInput();
        in.runId = "r01-test";
        in.player = UUID.randomUUID().toString();
        in.seed = 7L;
        in.tier = 3;
        in.target = "scorch";
        in.bossKilled = true;
        in.firstClear = null;
        List<EmberRunRules.Grant> grants = new ArrayList<EmberRunRules.Grant>(EmberRunRules.settle(in));
        EmberSettleService.appendBonuses(grants, in, r01, maps.raidItemQualityFloor, 3,
                true, 1, false, null, 0, false,
                false, false, 0, false, 0, "timed",
                Collections.<EmberRunRules.Grant>emptyList());
        List<String> keys = EmberSettleService.grantKeys(grants);
        int raidItem = keys.indexOf("raid_item");
        int raidMark = keys.indexOf("raid_mark");
        int rot = keys.indexOf("rot_mark");
        assertTrue(raidItem >= 0 && raidMark >= 0 && rot >= 0);
        assertTrue(raidItem < raidMark);
        assertTrue(raidMark < rot);
    }

    @Test public void bundledBalanceVersion_unchanged() {
        EmberRunMaps maps = bundled();
        assertEquals(79, maps.balanceVersion);
        assertEquals(30, maps.cost);
        assertEquals(0.5, maps.failRefund, 1e-9);
    }

    @Test public void playfeelSummary_D283_line() {
        assertEquals("", EmberSettleService.playfeelSummary(0, 0, 0, false, "", false, false, ""));
        String s = EmberSettleService.playfeelSummary(1, 2, 0, true, "blazing", true, true, "timed");
        assertTrue(s.contains("本局："));
        assertTrue(s.contains("撞墙破绽 ×1"));
        assertTrue(s.contains("落空破绽 ×2"));
        assertTrue(s.contains("词缀「炽热」✔"));
        assertTrue(s.contains("限时清房 ✔"));
        String fail = EmberSettleService.playfeelSummary(0, 0, 1, false, "", true, false, "crystal");
        assertTrue(fail.contains("破招 ×1"));
        assertTrue(fail.contains("砸余烬晶 ✘"));
    }

    @Test public void varietyBountyTip_D292_leadsWithPlusOne() {
        assertEquals("§e花样委托 §a+1", EmberSettleService.varietyBountyTip(java.util.Collections.<String>emptyList(), ""));
        assertEquals("§e花样委托 §a+1 §7· §a完成：击败词缀精英（20 余烬币） §7· §e击败词缀精英 1/2",
                EmberSettleService.varietyBountyTip(
                        java.util.Collections.singletonList("击败词缀精英（20 余烬币）"),
                        "§e击败词缀精英 1/2"));
    }

    @Test public void failPlayfeelLine_D295_usesSessionCounters() {
        EmberRunSession s = new EmberRunSession();
        s.wallHits = 2;
        s.whiffHits = 0;
        s.breakHits = 1;
        s.affix = "blazing";
        s.affixDone = true;
        s.eventRoom = "r2";
        s.eventKind = "timed";
        s.eventDone = false;
        String line = EmberSettleService.failPlayfeelLine(s);
        assertTrue(line.contains("撞墙破绽 ×2"));
        assertTrue(line.contains("破招 ×1"));
        assertTrue(line.contains("✔"));
        assertTrue(line.contains("✘"));
        assertEquals("", EmberSettleService.failPlayfeelLine(null));
        assertEquals("", EmberSettleService.failPlayfeelLine(new EmberRunSession()));
    }
}

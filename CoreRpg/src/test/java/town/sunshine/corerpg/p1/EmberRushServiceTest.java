package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import town.sunshine.corerpg.PlayerData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * D230 / ARCH S3-1: fixed-seed (no RNG, fixed week key) proofs that rush settle grants match
 * the pre-extract behaviour and the bundled {@code ember-v1-runs.yml} amounts (bv58).
 */
public final class EmberRushServiceTest {

    private static final String WEEK = "w230seed";
    private static final long NOW = 1_000_000_000_000L;

    private static EmberRunMaps bundled() {
        InputStream in = EmberRushServiceTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Object root = new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return EmberRunMaps.parse((Map<?, ?>) root);
    }

    private static EmberRunRules.Ledger ledger() { return new EmberRunRules.Ledger(); }

    @Test public void mainRushFirstClearPaysT3MarkAndBadges_unchanged() {
        EmberRunMaps.MapDef m = bundled().byKey("rush");
        assertNotNull(m);
        assertTrue(m.mainRush());
        assertEquals(1, m.rushMarks);
        assertEquals(3, m.rushMarkTier);
        assertEquals(20, m.rushBadges);
        assertEquals(1, m.rushWeekly);

        PlayerData pd = new PlayerData();
        EmberRunRules.Ledger l = ledger();
        EmberRushService.SettleResult r = EmberRushService.applyGrants(m, l, "rush-seed-a", pd, 0, WEEK, NOW);

        assertTrue(r.fresh && r.pays);
        assertTrue(r.badgeAttempted && r.badgeGranted);
        assertEquals(1, r.claimsAfter);
        assertEquals(1, pd.periodCount(EmberRushService.C_RUSH_CLAIM, WEEK));
        assertEquals(20, EmberSeason.badges(pd));

        EmberRunRules.Row mark = l.get("rush-seed-a", "rush_mark");
        assertNotNull(mark);
        assertEquals(EmberRunRules.ST_PENDING, mark.status);
        assertEquals("mark:3:1", mark.result);
        assertNull(l.get("rush-seed-a", "rush_practice"));
        assertNull(l.get("rush-seed-a", "rush_paid"));
        assertEquals(1, r.changed.size());
    }

    @Test public void mainRushSecondClearIsPractice_noGrants() {
        EmberRunMaps.MapDef m = bundled().byKey("rush");
        PlayerData pd = new PlayerData();
        pd.addPeriodCount(EmberRushService.C_RUSH_CLAIM, WEEK, 1);
        EmberRunRules.Ledger l = ledger();
        EmberRushService.SettleResult r = EmberRushService.applyGrants(m, l, "rush-seed-b", pd, 1, WEEK, NOW);

        assertTrue(r.fresh);
        assertFalse(r.pays);
        assertFalse(r.badgeAttempted);
        assertEquals(1, r.claimsAfter); // unchanged
        assertEquals(1, pd.periodCount(EmberRushService.C_RUSH_CLAIM, WEEK));
        assertEquals(0, EmberSeason.badges(pd));

        EmberRunRules.Row prac = l.get("rush-seed-b", "rush_practice");
        assertNotNull(prac);
        assertEquals(EmberRunRules.ST_DELIVERED, prac.status);
        assertEquals("mark:3:0", prac.result);
        assertNull(l.get("rush-seed-b", "rush_mark"));
        assertEquals(1, r.changed.size());
    }

    @Test public void mainRushReSettleIsNoOp_idempotent() {
        EmberRunMaps.MapDef m = bundled().byKey("rush");
        PlayerData pd = new PlayerData();
        EmberRunRules.Ledger l = ledger();
        EmberRushService.SettleResult first = EmberRushService.applyGrants(m, l, "rush-seed-c", pd, 0, WEEK, NOW);
        assertTrue(first.fresh && first.pays);
        assertEquals(20, EmberSeason.badges(pd));

        EmberRushService.SettleResult again = EmberRushService.applyGrants(m, l, "rush-seed-c", pd, 1, WEEK, NOW);
        assertFalse(again.fresh);
        assertTrue("re-settle keeps pays from existing rush_mark", again.pays);
        assertFalse(again.badgeAttempted);
        assertEquals(1, pd.periodCount(EmberRushService.C_RUSH_CLAIM, WEEK));
        assertEquals(20, EmberSeason.badges(pd)); // not double-granted
        assertEquals(0, again.changed.size());
        assertEquals(1, l.size());
    }

    @Test public void outpostFirstClearPaysT2MarkAndChainSigmarks_unchanged() {
        EmberRunMaps.MapDef m = bundled().byKey("outpost");
        assertNotNull(m);
        assertFalse(m.mainRush());
        assertEquals("outpost", m.rushMode);
        assertEquals(1, m.rushMarks);
        assertEquals(2, m.rushMarkTier);
        assertEquals(2, m.rushSig);
        assertEquals(0, m.rushBadges);
        assertEquals("[q01, q02, q03]", m.chainKeys.toString());

        PlayerData pd = new PlayerData();
        EmberRunRules.Ledger l = ledger();
        EmberRushService.SettleResult r = EmberRushService.applyGrants(m, l, "outpost-seed-a", pd, 0, WEEK, NOW);

        assertTrue(r.fresh && r.pays);
        assertFalse(r.badgeAttempted);
        assertEquals(1, pd.periodCount(m.rushClaim, WEEK));
        assertEquals(0, pd.periodCount(EmberRushService.C_RUSH_CLAIM, WEEK));

        EmberRunRules.Row mark = l.get("outpost-seed-a", "rush_mark");
        assertNotNull(mark);
        assertEquals("mark:2:1", mark.result);
        assertEquals(EmberRunRules.ST_PENDING, mark.status);
        EmberRunRules.Row paid = l.get("outpost-seed-a", "rush_paid");
        assertNotNull(paid);
        assertEquals(EmberRunRules.ST_DELIVERED, paid.status);
        for (String ck : m.chainKeys) {
            EmberRunRules.Row sig = l.get("outpost-seed-a", "rush_sig_" + ck);
            assertNotNull(ck, sig);
            assertEquals("sigmark:" + ck + ":2", sig.result);
            assertEquals(EmberRunRules.ST_PENDING, sig.status);
        }
        assertEquals(5, r.changed.size()); // mark + paid + 3 sig
    }

    @Test public void echoSharedWeeklyCapThree_unchanged() {
        EmberRunMaps.MapDef e = bundled().byKey("echo_q01");
        assertNotNull(e);
        assertEquals("echo", e.rushMode);
        assertEquals(3, e.rushWeekly);
        assertEquals(2, e.rushSig);
        assertEquals(0, e.rushMarks); // echo pays sig only
        assertEquals("p4_echo_claim", e.rushClaim);

        PlayerData pd = new PlayerData();
        // three paid clears across different run ids share the weekly counter
        for (int i = 0; i < 3; i++) {
            int before = pd.periodCount(e.rushClaim, WEEK);
            EmberRunRules.Ledger l = ledger();
            EmberRushService.SettleResult r = EmberRushService.applyGrants(e, l, "echo-seed-" + i, pd, before, WEEK, NOW);
            assertTrue("claim " + i, r.fresh && r.pays);
            assertEquals(before + 1, pd.periodCount(e.rushClaim, WEEK));
            assertNull(l.get("echo-seed-" + i, "rush_mark")); // marks 0
            assertNotNull(l.get("echo-seed-" + i, "rush_paid"));
            assertNotNull(l.get("echo-seed-" + i, "rush_sig_q01"));
            assertEquals("sigmark:q01:2", l.get("echo-seed-" + i, "rush_sig_q01").result);
        }
        // fourth is practice
        EmberRunRules.Ledger l4 = ledger();
        EmberRushService.SettleResult r4 = EmberRushService.applyGrants(e, l4, "echo-seed-3", pd, 3, WEEK, NOW);
        assertTrue(r4.fresh);
        assertFalse(r4.pays);
        assertNotNull(l4.get("echo-seed-3", "rush_practice"));
        assertNull(l4.get("echo-seed-3", "rush_sig_q01"));
        assertEquals(3, pd.periodCount(e.rushClaim, WEEK));
    }

    @Test public void textHelpersMatchPreExtractCopy() {
        EmberRunMaps maps = bundled();
        EmberRunMaps.MapDef rush = maps.byKey("rush");
        assertEquals("每周首通领奖，失败可无限重试", EmberRushService.ruleText(rush));
        assertTrue(EmberRushService.rewardText(rush).contains("T3 印记 +1"));
        assertTrue(EmberRushService.rewardText(rush).contains("余烬徽 +20"));
        assertTrue(EmberRushService.chainText(rush).contains("→"));

        EmberRunMaps.MapDef out = maps.byKey("outpost");
        assertEquals("每周首通领奖，失败可无限重试", EmberRushService.ruleText(out));
        assertTrue(EmberRushService.rewardText(out).contains("T2 印记 +1"));
        assertTrue(EmberRushService.rewardText(out).contains("每张图首领徽记 +2"));

        EmberRunMaps.MapDef echo = maps.byKey("echo_q05");
        assertTrue(EmberRushService.ruleText(echo).contains("每周前 3 次通关领奖（同类共用）"));
    }

    @Test public void economySourcesStillTagRushModes() {
        assertEquals("S16", EmberEconomy.sourceForRushMode("rush"));
        assertEquals("S17", EmberEconomy.sourceForRushMode("outpost"));
        assertEquals("S18", EmberEconomy.sourceForRushMode("echo"));
        assertEquals("S16", EmberEconomy.sourceForGrant("rush_mark", "rush-seed-a"));
        assertEquals("S17", EmberEconomy.sourceForGrant("rush_mark", "outpost-seed-a"));
        assertEquals("S17", EmberEconomy.sourceForGrant("rush_sig_q01", "outpost-seed-a"));
        assertEquals("S18", EmberEconomy.sourceForGrant("rush_sig_q01", "echo_q01-seed-a"));
    }
}

package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import town.sunshine.corerpg.PlayerData;

/**
 * D391–D397: short expedition sx01–sx04 — entry gate + S40–S43 settle grants.
 */
public class EmberShortRulesTest {

    @Before
    public void loadEconomy() throws Exception {
        try (InputStream in = EmberEconomy.class.getResourceAsStream("/ember-v1-economy.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            assertEquals(java.util.Collections.emptyList(), EmberEconomy.loadEconomyYml(y));
        }
    }

    @Test
    public void entryRequiresQ01() {
        assertNull(EmberShortRules.entryProblemText("A", true));
        assertTrue(EmberShortRules.entryProblemText("A", false).contains("Q01"));
    }

    @Test
    public void dayLineAndLeftForMenuPapi() {
        assertEquals("今日有奖 0/3", EmberShortRules.dayLine(0, 3));
        assertEquals("今日有奖 2/3", EmberShortRules.dayLine(2, 3));
        assertEquals("今日有奖已满 3/3（再通关无结算包，仍耗体力）", EmberShortRules.dayLine(3, 3));
        assertEquals(3, EmberShortRules.dayLeft(0, 3));
        assertEquals(1, EmberShortRules.dayLeft(2, 3));
        assertEquals(0, EmberShortRules.dayLeft(3, 3));
        assertEquals(0, EmberShortRules.dayLeft(5, 3));
        assertEquals("p1_sx01_day", EmberShortRules.claimKey("sx01"));
        assertEquals("p1_sx02_day", EmberShortRules.claimKey("sx02"));
        assertEquals("p1_sx03_day", EmberShortRules.claimKey("SX03"));
        assertEquals("p1_sx04_day", EmberShortRules.claimKey("sx04"));
        assertEquals(EmberShortRules.CLAIM, EmberShortRules.claimKey(null));
    }

    @Test
    public void rewardedClearPaysBaseline() {
        List<EmberRunRules.Grant> g = EmberShortRules.settleGrants(0, 3, true);
        Map<String, Integer> by = byKey(g);
        assertEquals(80, (int) by.get(EmberShortRules.G_CLEAR_COIN));
        assertEquals(4, (int) by.get(EmberShortRules.G_CLEAR_SHARD));
        assertEquals(3, (int) by.get(EmberShortRules.G_CLEAR_BONE));
        assertFalse(by.containsKey(EmberShortRules.G_FC_COIN));
        assertFalse(by.containsKey(EmberShortRules.G_PRACTICE));
    }

    @Test
    public void firstClearAddsPackage() {
        List<EmberRunRules.Grant> g = EmberShortRules.settleGrants(0, 3, false);
        Map<String, Integer> by = byKey(g);
        assertEquals(80, (int) by.get(EmberShortRules.G_CLEAR_COIN));
        assertEquals(200, (int) by.get(EmberShortRules.G_FC_COIN));
        assertEquals(8, (int) by.get(EmberShortRules.G_FC_SHARD));
        assertEquals(1, (int) by.get(EmberShortRules.G_FC_BLANK));
    }

    @Test
    public void dayCapFourthIsNoReward() {
        assertTrue(EmberShortRules.paysReward(2, 3));
        assertFalse(EmberShortRules.paysReward(3, 3));
        List<EmberRunRules.Grant> g = EmberShortRules.settleGrants(3, 3, false);
        assertEquals(1, g.size());
        assertEquals(EmberShortRules.G_PRACTICE, g.get(0).key);
        assertEquals(0, g.get(0).amount);
    }

    @Test
    public void applyGrantsBumpsDayAndFirstClear() {
        EmberRunRules.Ledger l = new EmberRunRules.Ledger();
        PlayerData pd = new PlayerData();
        EmberRunMaps.MapDef m = stubMap();
        EmberShortService.SettleResult r1 = EmberShortService.applyGrants(m, l, "sx01-a", pd, 0, false, "2026-10-10", 1L);
        assertTrue(r1.fresh);
        assertTrue(r1.pays);
        assertTrue(r1.firstClearPaidNow);
        assertEquals(1, r1.rewardedAfter);
        assertEquals(1, pd.periodCount(EmberShortRules.CLAIM, "2026-10-10"));
        assertTrue(EmberFirstClear.paid(pd, "sx01", "v1"));

        EmberShortService.SettleResult r2 = EmberShortService.applyGrants(m, l, "sx01-b", pd, 1, true, "2026-10-10", 2L);
        assertTrue(r2.pays);
        assertFalse(r2.firstClearPaidNow);
        assertEquals(2, r2.rewardedAfter);

        EmberShortService.SettleResult r4 = EmberShortService.applyGrants(m, l, "sx01-d", pd, 3, true, "2026-10-10", 4L);
        assertTrue(r4.fresh);
        assertFalse(r4.pays);
        assertEquals(3, r4.rewardedAfter); // no bump
        assertEquals(EmberShortRules.G_PRACTICE, l.get("sx01-d", EmberShortRules.G_PRACTICE).key);
    }

    @Test
    public void sourceTagsS40() {
        assertEquals("S40", EmberEconomy.sourceForGrantKey(EmberShortRules.G_CLEAR_COIN));
        assertEquals("S40", EmberEconomy.sourceForGrantKey(EmberShortRules.G_FC_BLANK));
        assertEquals("S40", EmberEconomy.sourceForGrantKey(EmberShortRules.G_PRACTICE));
    }

    @Test
    public void runsYmlDefinesSx01() throws Exception {
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            @SuppressWarnings("unchecked")
            Map<String, Object> shortSec = (Map<String, Object>) y.get("short");
            assertTrue(shortSec.containsKey("sx01"));
            @SuppressWarnings("unchecked")
            Map<String, Object> sx = (Map<String, Object>) shortSec.get("sx01");
            assertEquals(30, ((Number) sx.get("cost")).intValue());
            assertEquals("q01", sx.get("requires"));
            assertEquals(3, ((Number) sx.get("daily_reward_cap")).intValue());
            assertEquals("p1_sx01_day", sx.get("claim"));
            assertEquals("EmberSx01", sx.get("dungeon"));
        }
    }


    @Test
    public void sx01HasRoomsAndBoss() throws Exception {
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            EmberRunMaps maps = EmberRunMaps.parse(y);
            EmberRunMaps.MapDef m = maps.byKey("sx01");
            assertNotNull(m);
            assertTrue(m.shortExpedition);
            assertFalse(m.rooms.isEmpty());
            assertEquals(3, m.rooms.size());
            assertNotNull(m.boss);
            assertEquals("EmberSx01Warden", m.boss.mm);
            assertNotNull(m.boss.at);
            assertNull(m.validate());
            assertTrue(maps.validate().stream().noneMatch(e -> e.startsWith("sx01:")));
        }
    }


    @Test
    public void sx02RewardedClearPaysBaseline() {
        List<EmberRunRules.Grant> g = EmberShortRules.settleGrants("sx02", 0, 3, true);
        Map<String, Integer> by = byKey(g);
        assertEquals(80, (int) by.get(EmberShortRules.gClearCoin("sx02")));
        assertEquals(4, (int) by.get(EmberShortRules.gClearShard("sx02")));
        assertEquals(3, (int) by.get(EmberShortRules.gClearBone("sx02")));
        assertFalse(by.containsKey(EmberShortRules.gFcCoin("sx02")));
    }

    @Test
    public void sx02FirstClearThinnerThanSx01() {
        List<EmberRunRules.Grant> g = EmberShortRules.settleGrants("sx02", 0, 3, false);
        Map<String, Integer> by = byKey(g);
        assertEquals(180, (int) by.get(EmberShortRules.gFcCoin("sx02")));
        assertEquals(6, (int) by.get(EmberShortRules.gFcShard("sx02")));
        assertEquals(1, (int) by.get(EmberShortRules.gFcBlank("sx02")));
        // sx01 first-clear stays thicker
        Map<String, Integer> sx01 = byKey(EmberShortRules.settleGrants("sx01", 0, 3, false));
        assertEquals(200, (int) sx01.get(EmberShortRules.G_FC_COIN));
    }

    @Test
    public void sourceTagsS41ForSx02() {
        assertEquals("S41", EmberEconomy.sourceForGrantKey(EmberShortRules.gClearCoin("sx02")));
        assertEquals("S41", EmberEconomy.sourceForGrantKey(EmberShortRules.gFcBlank("sx02")));
        assertEquals("S41", EmberEconomy.sourceForGrantKey(EmberShortRules.gPractice("sx02")));
        assertEquals("S40", EmberEconomy.sourceForGrantKey(EmberShortRules.G_CLEAR_COIN));
    }

    @Test
    public void runsYmlDefinesSx02() throws Exception {
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            @SuppressWarnings("unchecked")
            Map<String, Object> shortSec = (Map<String, Object>) y.get("short");
            assertTrue(shortSec.containsKey("sx02"));
            @SuppressWarnings("unchecked")
            Map<String, Object> sx = (Map<String, Object>) shortSec.get("sx02");
            assertEquals(30, ((Number) sx.get("cost")).intValue());
            assertEquals("q01", sx.get("requires"));
            assertEquals(3, ((Number) sx.get("daily_reward_cap")).intValue());
            assertEquals("p1_sx02_day", sx.get("claim"));
            assertEquals("EmberSx02", sx.get("dungeon"));
            assertFalse("p1_sx01_day".equals(sx.get("claim")));
        }
    }

    @Test
    public void sx02HasRoomsAndBoss() throws Exception {
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            EmberRunMaps maps = EmberRunMaps.parse(y);
            EmberRunMaps.MapDef m = maps.byKey("sx02");
            assertNotNull(m);
            assertTrue(m.shortExpedition);
            assertEquals(3, m.rooms.size());
            assertNotNull(m.boss);
            assertEquals("EmberSx02Warden", m.boss.mm);
            assertEquals("p1_sx02_day", m.shortClaim);
            assertNull(m.validate());
            assertTrue(maps.validate().stream().noneMatch(e -> e.startsWith("sx02:")));
        }
    }

    @Test
    public void applyGrantsSx02UsesOwnDayClaim() {
        EmberRunRules.Ledger l = new EmberRunRules.Ledger();
        PlayerData pd = new PlayerData();
        EmberRunMaps.MapDef m = stubMap("sx02");
        EmberShortService.SettleResult r1 = EmberShortService.applyGrants(m, l, "sx02-a", pd, 0, false, "2026-10-10", 1L);
        assertTrue(r1.pays);
        assertEquals(1, pd.periodCount("p1_sx02_day", "2026-10-10"));
        assertEquals(0, pd.periodCount(EmberShortRules.CLAIM, "2026-10-10")); // sx01 untouched
        assertTrue(EmberFirstClear.paid(pd, "sx02", "v1"));
    }


    @Test
    public void sx03RewardedClearPaysBaseline() {
        List<EmberRunRules.Grant> g = EmberShortRules.settleGrants("sx03", 0, 3, true);
        Map<String, Integer> by = byKey(g);
        assertEquals(80, (int) by.get(EmberShortRules.gClearCoin("sx03")));
        assertEquals(4, (int) by.get(EmberShortRules.gClearShard("sx03")));
        assertEquals(3, (int) by.get(EmberShortRules.gClearBone("sx03")));
        assertFalse(by.containsKey(EmberShortRules.gFcCoin("sx03")));
    }

    @Test
    public void sx03FirstClearThinnerThanSx02() {
        List<EmberRunRules.Grant> g = EmberShortRules.settleGrants("sx03", 0, 3, false);
        Map<String, Integer> by = byKey(g);
        assertEquals(160, (int) by.get(EmberShortRules.gFcCoin("sx03")));
        assertEquals(8, (int) by.get(EmberShortRules.gFcShard("sx03")));
        assertEquals(1, (int) by.get(EmberShortRules.gFcBlank("sx03")));
        Map<String, Integer> sx02 = byKey(EmberShortRules.settleGrants("sx02", 0, 3, false));
        assertEquals(180, (int) sx02.get(EmberShortRules.gFcCoin("sx02")));
    }

    @Test
    public void sourceTagsS42ForSx03() {
        assertEquals("S42", EmberEconomy.sourceForGrantKey(EmberShortRules.gClearCoin("sx03")));
        assertEquals("S42", EmberEconomy.sourceForGrantKey(EmberShortRules.gFcBlank("sx03")));
        assertEquals("S42", EmberEconomy.sourceForGrantKey(EmberShortRules.gPractice("sx03")));
        assertEquals("S41", EmberEconomy.sourceForGrantKey(EmberShortRules.gClearCoin("sx02")));
        assertEquals("S40", EmberEconomy.sourceForGrantKey(EmberShortRules.G_CLEAR_COIN));
    }

    @Test
    public void runsYmlDefinesSx03() throws Exception {
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            @SuppressWarnings("unchecked")
            Map<String, Object> shortSec = (Map<String, Object>) y.get("short");
            assertTrue(shortSec.containsKey("sx03"));
            @SuppressWarnings("unchecked")
            Map<String, Object> sx = (Map<String, Object>) shortSec.get("sx03");
            assertEquals(30, ((Number) sx.get("cost")).intValue());
            assertEquals("q01", sx.get("requires"));
            assertEquals(3, ((Number) sx.get("daily_reward_cap")).intValue());
            assertEquals("p1_sx03_day", sx.get("claim"));
            assertEquals("EmberSx03", sx.get("dungeon"));
            assertFalse("p1_sx01_day".equals(sx.get("claim")));
            assertFalse("p1_sx02_day".equals(sx.get("claim")));
        }
    }

    @Test
    public void sx03HasRoomsAndBoss() throws Exception {
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            EmberRunMaps maps = EmberRunMaps.parse(y);
            EmberRunMaps.MapDef m = maps.byKey("sx03");
            assertNotNull(m);
            assertTrue(m.shortExpedition);
            assertEquals(3, m.rooms.size());
            assertNotNull(m.boss);
            assertEquals("EmberSx03Warden", m.boss.mm);
            assertEquals("p1_sx03_day", m.shortClaim);
            assertNull(m.validate());
            assertTrue(maps.validate().stream().noneMatch(e -> e.startsWith("sx03:")));
        }
    }

    @Test
    public void applyGrantsSx03UsesOwnDayClaim() {
        EmberRunRules.Ledger l = new EmberRunRules.Ledger();
        PlayerData pd = new PlayerData();
        EmberRunMaps.MapDef m = stubMap("sx03");
        EmberShortService.SettleResult r1 = EmberShortService.applyGrants(m, l, "sx03-a", pd, 0, false, "2026-10-10", 1L);
        assertTrue(r1.pays);
        assertEquals(1, pd.periodCount("p1_sx03_day", "2026-10-10"));
        assertEquals(0, pd.periodCount("p1_sx02_day", "2026-10-10"));
        assertEquals(0, pd.periodCount(EmberShortRules.CLAIM, "2026-10-10"));
        assertTrue(EmberFirstClear.paid(pd, "sx03", "v1"));
    }

    @Test
    public void sx04RewardedClearPaysBaseline() {
        List<EmberRunRules.Grant> g = EmberShortRules.settleGrants("sx04", 0, 3, true);
        Map<String, Integer> by = byKey(g);
        assertEquals(80, (int) by.get(EmberShortRules.gClearCoin("sx04")));
        assertEquals(4, (int) by.get(EmberShortRules.gClearShard("sx04")));
        assertEquals(3, (int) by.get(EmberShortRules.gClearBone("sx04")));
        assertFalse(by.containsKey(EmberShortRules.gFcCoin("sx04")));
    }

    @Test
    public void sx04FirstClearThinnerThanSx03() {
        List<EmberRunRules.Grant> g = EmberShortRules.settleGrants("sx04", 0, 3, false);
        Map<String, Integer> by = byKey(g);
        assertEquals(150, (int) by.get(EmberShortRules.gFcCoin("sx04")));
        assertEquals(6, (int) by.get(EmberShortRules.gFcShard("sx04")));
        assertEquals(1, (int) by.get(EmberShortRules.gFcBlank("sx04")));
        Map<String, Integer> sx03 = byKey(EmberShortRules.settleGrants("sx03", 0, 3, false));
        assertEquals(160, (int) sx03.get(EmberShortRules.gFcCoin("sx03")));
    }

    @Test
    public void sourceTagsS43ForSx04() {
        assertEquals("S43", EmberEconomy.sourceForGrantKey(EmberShortRules.gClearCoin("sx04")));
        assertEquals("S43", EmberEconomy.sourceForGrantKey(EmberShortRules.gFcBlank("sx04")));
        assertEquals("S43", EmberEconomy.sourceForGrantKey(EmberShortRules.gPractice("sx04")));
        assertEquals("S42", EmberEconomy.sourceForGrantKey(EmberShortRules.gClearCoin("sx03")));
        assertEquals("S40", EmberEconomy.sourceForGrantKey(EmberShortRules.G_CLEAR_COIN));
    }

    @Test
    public void runsYmlDefinesSx04() throws Exception {
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            @SuppressWarnings("unchecked")
            Map<String, Object> shortSec = (Map<String, Object>) y.get("short");
            assertTrue(shortSec.containsKey("sx04"));
            @SuppressWarnings("unchecked")
            Map<String, Object> sx = (Map<String, Object>) shortSec.get("sx04");
            assertEquals(30, ((Number) sx.get("cost")).intValue());
            assertEquals("q01", sx.get("requires"));
            assertEquals(3, ((Number) sx.get("daily_reward_cap")).intValue());
            assertEquals("p1_sx04_day", sx.get("claim"));
            assertEquals("EmberSx04", sx.get("dungeon"));
            assertFalse("p1_sx01_day".equals(sx.get("claim")));
            assertFalse("p1_sx03_day".equals(sx.get("claim")));
        }
    }

    @Test
    public void sx04HasRoomsAndBoss() throws Exception {
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            EmberRunMaps maps = EmberRunMaps.parse(y);
            EmberRunMaps.MapDef m = maps.byKey("sx04");
            assertNotNull(m);
            assertTrue(m.shortExpedition);
            assertEquals(3, m.rooms.size());
            assertNotNull(m.boss);
            assertEquals("EmberSx04Warden", m.boss.mm);
            assertEquals("p1_sx04_day", m.shortClaim);
            assertNull(m.validate());
            assertTrue(maps.validate().stream().noneMatch(e -> e.startsWith("sx04:")));
        }
    }

    @Test
    public void applyGrantsSx04UsesOwnDayClaim() {
        EmberRunRules.Ledger l = new EmberRunRules.Ledger();
        PlayerData pd = new PlayerData();
        EmberRunMaps.MapDef m = stubMap("sx04");
        EmberShortService.SettleResult r1 = EmberShortService.applyGrants(m, l, "sx04-a", pd, 0, false, "2026-10-10", 1L);
        assertTrue(r1.pays);
        assertEquals(1, pd.periodCount("p1_sx04_day", "2026-10-10"));
        assertEquals(0, pd.periodCount("p1_sx03_day", "2026-10-10"));
        assertEquals(0, pd.periodCount(EmberShortRules.CLAIM, "2026-10-10"));
        assertTrue(EmberFirstClear.paid(pd, "sx04", "v1"));
    }


    private static Map<String, Integer> byKey(List<EmberRunRules.Grant> g) {
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (EmberRunRules.Grant x : g) m.put(x.key, x.amount);
        return m;
    }

    private static EmberRunMaps.MapDef stubMap() {
        return stubMap("sx01");
    }

    private static EmberRunMaps.MapDef stubMap(String key) {
        try (InputStream in = EmberShortRulesTest.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            EmberRunMaps.MapDef m = EmberRunMaps.parse(y).byKey(key);
            assertNotNull(m);
            assertTrue(m.shortExpedition);
            return m;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

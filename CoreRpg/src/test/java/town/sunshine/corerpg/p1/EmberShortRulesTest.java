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
 * D391: short expedition sx01 — entry gate + S40 settle grants (rewarded / first-clear / post-cap).
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

    private static Map<String, Integer> byKey(List<EmberRunRules.Grant> g) {
        Map<String, Integer> m = new HashMap<String, Integer>();
        for (EmberRunRules.Grant x : g) m.put(x.key, x.amount);
        return m;
    }

    private static EmberRunMaps.MapDef stubMap() {
        try (InputStream in = EmberShortRulesTest.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            EmberRunMaps.MapDef m = EmberRunMaps.parse(y).byKey("sx01");
            assertNotNull(m);
            assertTrue(m.shortExpedition);
            return m;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

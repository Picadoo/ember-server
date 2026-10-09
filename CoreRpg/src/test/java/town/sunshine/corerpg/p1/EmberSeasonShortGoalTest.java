package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import town.sunshine.corerpg.PlayerData;

/**
 * D401: optional weekly goal {@code short} — OPTIONAL + name/open + rewarded-clear hook gate.
 */
public class EmberSeasonShortGoalTest {

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
    public void shortIsOptionalNotRequired() {
        assertTrue(EmberSeason.OPTIONAL.contains("short"));
        assertTrue(EmberSeason.OPTIONAL.contains("core"));
        assertFalse(EmberSeason.GOALS.contains("short"));
        assertEquals(4, EmberSeason.GOALS.size());
    }

    @Test
    public void goalNameAndOpenForShort() {
        assertEquals("周目标 · 短征（可选）", EmberSeason.goalName("short"));
        assertEquals("/ember_p1_short", EmberSeason.goalOpen("short"));
        assertEquals("/ember_p1_adventure", EmberSeason.goalOpen("core"));
    }

    @Test
    public void ymlTargetsShortFive() throws Exception {
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            @SuppressWarnings("unchecked")
            Map<String, Object> wg = (Map<String, Object>) y.get("weekly_goals");
            @SuppressWarnings("unchecked")
            Map<String, Object> targets = (Map<String, Object>) wg.get("targets");
            assertEquals(5, ((Number) targets.get("short")).intValue());
            assertEquals(15, ((Number) wg.get("reward")).intValue());
            assertEquals(20, ((Number) wg.get("bonus")).intValue());
            assertEquals(1, ((Number) targets.get("featured")).intValue());
            assertEquals(3, ((Number) targets.get("abyss")).intValue());
            assertEquals(1, ((Number) targets.get("raid")).intValue());
            assertEquals(3, ((Number) targets.get("bounty")).intValue());
            assertEquals(2, ((Number) targets.get("core")).intValue());
            assertEquals(69, ((Number) y.get("balance_version")).intValue());
        }
    }

    @Test
    public void countsTowardShortGoalOnlyWhenPays() throws Exception {
        EmberRunMaps.MapDef m;
        try (InputStream in = EmberRunMaps.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            m = EmberRunMaps.parse(y).byKey("sx01");
        }
        assertNotNull(m);
        EmberRunRules.Ledger l = new EmberRunRules.Ledger();
        PlayerData pd = new PlayerData();

        EmberShortService.SettleResult pay = EmberShortService.applyGrants(m, l, "r-pay", pd, 0, false, "2026-10-10", 1L);
        assertTrue(pay.pays);
        assertTrue(EmberShortService.countsTowardShortGoal(pay));

        EmberShortService.SettleResult full = EmberShortService.applyGrants(m, l, "r-full", pd, 3, true, "2026-10-10", 2L);
        assertFalse(full.pays);
        assertFalse(EmberShortService.countsTowardShortGoal(full));

        EmberShortService.SettleResult again = EmberShortService.applyGrants(m, l, "r-pay", pd, 1, true, "2026-10-10", 3L);
        assertFalse(again.fresh);
        assertFalse(EmberShortService.countsTowardShortGoal(again));
    }
}

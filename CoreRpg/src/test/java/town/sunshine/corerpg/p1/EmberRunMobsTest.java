package town.sunshine.corerpg.p1;

import org.junit.Assume;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.Assert.*;

/** Repo consistency: every MythicMobs id used by ember-v1-runs.yml exists with the book HP / raw damage. */
public class EmberRunMobsTest {

    private static Map<?, ?> load(String path) throws Exception {
        File f = new File(path);
        Assume.assumeTrue("repo file " + path + " not present", f.isFile());
        try (InputStreamReader r = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) {
            return (Map<?, ?>) new Yaml().load(r);
        }
    }

    @Test public void mythicMobsMatchRunTable() throws Exception {
        Map<?, ?> mm = load("../plugins/MythicMobs/Mobs/EmberP1Main.yml");
        EmberRunMaps maps = EmberRunMaps.parse(load("src/main/resources/ember-v1-runs.yml"));
        assertEquals(EmberRunMaps.parse(load("../plugins/CoreRpg/ember-v1-runs.yml")).maps.keySet(), maps.maps.keySet());
        int n = 0;
        for (EmberRunMaps.MapDef m : maps.maps.values()) {
            for (EmberRunMaps.Role r : m.roles.values()) {
                Map<?, ?> mob = (Map<?, ?>) mm.get(r.mm);
                assertNotNull("missing MM mob " + r.mm, mob);
                assertEquals(r.mm + " health", r.hp, ((Number) mob.get("Health")).doubleValue(), 0);
                assertEquals(r.mm + " damage", r.atk, ((Number) mob.get("Damage")).doubleValue(), 0);
                assertEquals(r.mm + " sunburn", Boolean.TRUE, ((Map<?, ?>) mob.get("Options")).get("PreventSunburn"));
                n++;
            }
            Map<?, ?> boss = (Map<?, ?>) mm.get(m.boss.mm);
            assertNotNull("missing MM boss " + m.boss.mm, boss);
            assertEquals(m.boss.hp, ((Number) boss.get("Health")).doubleValue(), 0);
            assertEquals(m.boss.atk, ((Number) boss.get("Damage")).doubleValue(), 0);
            n++;
        }
        assertEquals(mm.size(), n); // no stray ids in the file
    }

    @Test public void scopePrefixCoversRunWorlds() throws Exception {
        Map<?, ?> cfg = load("src/main/resources/ember-v1.yml");
        Object pre = ((Map<?, ?>) cfg.get("scope")).get("world_prefixes");
        assertTrue(String.valueOf(pre), String.valueOf(pre).contains("dungeon_EmberQ0"));
        Map<?, ?> live = load("../plugins/CoreRpg/ember-v1.yml");
        assertEquals(pre, ((Map<?, ?>) live.get("scope")).get("world_prefixes"));
    }
}

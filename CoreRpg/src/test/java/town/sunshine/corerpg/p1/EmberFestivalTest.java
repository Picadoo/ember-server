package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.Assert.*;

/** D139 国庆 event: the bundled festival file parses as an event MapDef next to the main maps; the charm stays capped. */
public class EmberFestivalTest {

    private static Map<?, ?> load(String res) {
        InputStream in = EmberFestivalTest.class.getResourceAsStream(res);
        assertNotNull(res, in);
        return (Map<?, ?>) new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
    }

    @Test public void eventDungeonParsesAndValidates() {
        Map<?, ?> fest = load("/ember-v1-festival.yml");
        Map<?, ?> dg = (Map<?, ?>) fest.get("dungeon");
        Map<Object, Object> root = new LinkedHashMap<Object, Object>(load("/ember-v1-runs.yml"));
        Map<String, Object> ev = new LinkedHashMap<String, Object>();
        ev.put(String.valueOf(dg.get("key")), dg);
        root.put("events", ev);
        EmberRunMaps m = EmberRunMaps.parse(root);
        assertEquals("[]", m.validate().toString());
        EmberRunMaps.MapDef g = m.byKey("gq26");
        assertNotNull(g);
        assertTrue(g.event);
        assertFalse(g.raid);
        assertEquals(0, m.cost(g)); // free, not the file default 30
        assertEquals(7, m.maps.size()); // never part of the main-line order / abyss pool
        assertSame(g, m.byWorld("dungeon_EmberQ0F1_ab12"));
        assertNotSame(g, m.byWorld("dungeon_EmberQ01_ab12"));
        assertSame(g, m.byDungeon("EmberQ0F1"));
        assertEquals(3, g.rooms.size());
        assertNotNull(g.boss);
    }

    @Test public void windowComesFromTheFileAndEndsBeforeOct8() {
        Map<?, ?> fest = load("/ember-v1-festival.yml");
        long start = EmberFestival.time(fest.get("start"), -1), end = EmberFestival.time(fest.get("end"), -1);
        assertEquals(java.time.OffsetDateTime.parse("2026-10-08T00:00:00+08:00").toInstant().toEpochMilli(), end);
        assertTrue(start < end);
        assertTrue(start <= java.time.OffsetDateTime.parse("2026-10-07T00:00:00+08:00").toInstant().toEpochMilli());
    }

    @Test public void festivalCharmStatsAreCappedAtAStandardT3Charm() {
        EmberTables t = EmberTables.defaults();
        EmberLoadout base = EmberLoadout.compute(t, null, null, 20);
        EmberLoadout huge = EmberLoadout.compute(t, null, null, 20, 1e6, 1e6);
        assertEquals(t.charmH(3), huge.festHp, 1e-9);
        assertEquals(t.charmD(3), huge.festDef, 1e-9);
        assertEquals(base.h0 + t.charmH(3), huge.h0, 1e-9);
        assertEquals(base.d + t.charmD(3), huge.d, 1e-9);
        EmberLoadout neg = EmberLoadout.compute(t, null, null, 20, -5, -5);
        assertEquals(base.h, neg.h, 1e-9);
        Map<?, ?> ch = (Map<?, ?>) load("/ember-v1-festival.yml").get("charm");
        assertTrue(((Number) ch.get("hp")).doubleValue() <= t.charmH(3));
        assertTrue(((Number) ch.get("def")).doubleValue() <= t.charmD(3));
    }
}

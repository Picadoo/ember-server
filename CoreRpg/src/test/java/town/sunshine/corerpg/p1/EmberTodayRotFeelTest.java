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

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * TIP-CO D13320: todayRot predicate / room throttle / copy / persist — Bukkit-free.
 */
public class EmberTodayRotFeelTest {

    @Test
    public void isTodayRot_matchesIgnoreCase() {
        assertTrue(EmberTodayRotFeel.isTodayRot(true, "sx03", "sx03"));
        assertTrue(EmberTodayRotFeel.isTodayRot(true, "SX03", "sx03"));
        assertFalse(EmberTodayRotFeel.isTodayRot(true, "sx04", "sx03"));
        assertFalse(EmberTodayRotFeel.isTodayRot(false, "sx03", "sx03"));
        assertFalse(EmberTodayRotFeel.isTodayRot(true, null, "sx03"));
        assertFalse(EmberTodayRotFeel.isTodayRot(true, "sx03", null));
        assertFalse(EmberTodayRotFeel.isTodayRot(true, "", "sx03"));
    }

    @Test
    public void markIfToday_setsSessionFlag() {
        EmberRunMaps.MapDef sx07 = shortMap("sx07");
        EmberRunSession s = new EmberRunSession();
        assertTrue(EmberTodayRotFeel.markIfToday(s, sx07, "sx07"));
        assertTrue(s.todayRot);

        EmberRunSession s2 = new EmberRunSession();
        assertFalse(EmberTodayRotFeel.markIfToday(s2, sx07, "sx08"));
        assertFalse(s2.todayRot);

        EmberRunMaps.MapDef q01 = mainMap("q01");
        EmberRunSession s3 = new EmberRunSession();
        assertFalse(EmberTodayRotFeel.markIfToday(s3, q01, "q01"));
        assertFalse(s3.todayRot);
    }

    @Test
    public void roomAb_and_throttle() {
        assertEquals("§6今日推 · 第1房", EmberTodayRotFeel.roomAb(1));
        assertEquals("§6今日推 · 第2房", EmberTodayRotFeel.roomAb(2));
        assertEquals("§6今日推 · 第1房", EmberTodayRotFeel.roomAb(0)); // clamp

        long t0 = 1_000_000L;
        assertTrue(EmberTodayRotFeel.shouldRoomCue(true, "r1", "", 0L, t0));
        assertFalse(EmberTodayRotFeel.shouldRoomCue(true, "r1", "r1", t0, t0 + 14_999L));
        assertTrue(EmberTodayRotFeel.shouldRoomCue(true, "r1", "r1", t0, t0 + 15_000L));
        assertTrue(EmberTodayRotFeel.shouldRoomCue(true, "r2", "r1", t0, t0 + 100L));
        assertFalse(EmberTodayRotFeel.shouldRoomCue(false, "r1", "", 0L, t0));
    }

    @Test
    public void copy_constants() {
        assertTrue(EmberTodayRotFeel.ENTER_TELL.contains("今日推"));
        assertTrue(EmberTodayRotFeel.ENTER_AB.contains("今日推"));
        assertTrue(EmberTodayRotFeel.SETTLE_DONE.contains("今日推完成"));
        assertEquals("[再进今日推]", EmberTodayRotFeel.REENTER_LABEL);
        assertTrue(EmberTodayRotFeel.REENTER_CMD.contains("enter short-today"));
        assertEquals(15_000L, EmberTodayRotFeel.ROOM_THROTTLE_MS);
    }

    @Test
    public void session_persist_todayRot() {
        EmberRunSession s = new EmberRunSession();
        s.todayRot = true;
        s.runId = "t1";
        s.mapKey = "sx01";
        Map<String, Object> map = s.toMap();
        assertEquals(Boolean.TRUE, map.get("today_rot"));
        assertTrue(EmberRunSession.fromMap(map).todayRot);

        EmberRunSession plain = new EmberRunSession();
        plain.runId = "t2";
        plain.mapKey = "sx02";
        Map<String, Object> map2 = plain.toMap();
        assertFalse(map2.containsKey("today_rot"));
        assertFalse(EmberRunSession.fromMap(map2).todayRot);
    }

    private static EmberRunMaps.MapDef shortMap(String key) {
        EmberRunMaps.MapDef m = maps().byKey(key);
        assertNotNull(m);
        assertTrue(m.shortExpedition);
        return m;
    }

    private static EmberRunMaps.MapDef mainMap(String key) {
        EmberRunMaps.MapDef m = maps().byKey(key);
        assertNotNull(m);
        assertFalse(m.shortExpedition);
        return m;
    }

    private static EmberRunMaps maps() {
        try (InputStream in = EmberTodayRotFeelTest.class.getResourceAsStream("/ember-v1-runs.yml");
             Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> y = (Map<String, Object>) new Yaml().load(r);
            return EmberRunMaps.parse(y);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

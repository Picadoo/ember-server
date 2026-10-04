package town.sunshine.corerpg.p1;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** D180 每日签到 + 在线时长: calendar bits, make-up rule (HoYoLAB), milestone states, idle rule, shipped config. */
public class EmberSignServiceTest {

    @Test public void calendarBits() {
        int m = 0;
        assertFalse(EmberSignService.signedOn(m, 1));
        m |= EmberSignService.bit(1); m |= EmberSignService.bit(31); m |= EmberSignService.bit(4);
        assertTrue(EmberSignService.signedOn(m, 31));
        assertEquals(3, EmberSignService.count(m));
        assertTrue("31 days fit a positive int", m > 0);
        assertEquals(0, EmberSignService.bit(0));
        assertEquals(0, EmberSignService.bit(32));
    }

    @Test public void makeupFillsEarliestMissed() {
        int m = EmberSignService.bit(1) | EmberSignService.bit(2) | EmberSignService.bit(5);
        assertEquals(3, EmberSignService.earliestMissed(m, 5));
        assertEquals(0, EmberSignService.earliestMissed(m, 3));       // only days before today
        assertEquals(0, EmberSignService.earliestMissed(0, 1));        // the 1st: nothing to make up
        assertEquals(1, EmberSignService.earliestMissed(0, 2));
    }

    @Test public void makeupRule() {
        int today = 10, m = EmberSignService.bit(10) | EmberSignService.bit(1);
        assertNull(EmberSignService.makeupBlock(m, today, 0, 3, 0, 60, 60));
        assertNotNull("today first", EmberSignService.makeupBlock(EmberSignService.bit(1), today, 0, 3, 0, 60, 60));
        assertNotNull("3 a month", EmberSignService.makeupBlock(m, today, 3, 3, 0, 60, 60));
        assertNotNull("1 a day", EmberSignService.makeupBlock(m, today, 1, 3, 1, 60, 60));
        assertNotNull("needs 60 counted minutes today", EmberSignService.makeupBlock(m, today, 0, 3, 0, 59, 60));
        int full = 0; for (int d = 1; d <= 10; d++) full |= EmberSignService.bit(d);
        assertNotNull("nothing missed", EmberSignService.makeupBlock(full, today, 0, 3, 0, 120, 60));
    }

    @Test public void milestones() {
        assertEquals(0, EmberSignService.milestoneState(14, 0, 0, 15));
        assertEquals(1, EmberSignService.milestoneState(15, 0, 0, 15));
        assertEquals(2, EmberSignService.milestoneState(15, 1, 0, 15));
        assertEquals(1, EmberSignService.milestoneState(200, 1, 1, 30)); // claiming #0 leaves #1
        assertEquals(2, EmberSignService.milestoneState(0, 1 << 3, 3, 120)); // claimed stays claimed
    }

    @Test public void idleAndAfkWorld() {
        long now = 10_000_000L;
        assertTrue(EmberSignService.counts(false, now - 4 * 60000L, now, 5));
        assertTrue(EmberSignService.counts(false, now - 5 * 60000L, now, 5));
        assertFalse("idle > 5 min stops the clock", EmberSignService.counts(false, now - 5 * 60000L - 1, now, 5));
        assertFalse("an excluded world never counts", EmberSignService.counts(true, now, now, 5));
        // D180 rev 2: AFK auto-combat counts as activity even with no input for longer than idle_minutes
        assertTrue("auto-combat keeps the clock running", EmberSignService.counts(false, true, now - 30 * 60000L, now, 5));
        assertTrue("auto-combat right after join", EmberSignService.counts(false, true, null, now, 5));
        assertFalse("auto-combat off + idle stops", EmberSignService.counts(false, false, now - 6 * 60000L, now, 5));
        assertFalse("excluded world wins over auto-combat", EmberSignService.counts(true, true, now, now, 5));
        assertFalse("no input since join", EmberSignService.counts(false, null, now, 5));
    }

    @Test public void days() {
        assertEquals(20261004, EmberSignService.dayInt(LocalDate.of(2026, 10, 4)));
        assertEquals("2026-10-04", EmberSignService.dayStr(20261004));
        assertEquals("2026-10", EmberSignService.month(LocalDate.of(2026, 10, 31)));
        assertTrue("next day sorts after", EmberSignService.dayInt(LocalDate.of(2026, 11, 1)) > EmberSignService.dayInt(LocalDate.of(2026, 10, 31)));
    }

    private static YamlConfiguration res() {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(
                EmberSignServiceTest.class.getResourceAsStream("/ember-v1.yml"), StandardCharsets.UTF_8));
    }

    /** = DESIGN-ember-signin-online §4/§5 and tools/p1sim/signin.py (which reads plugins/CoreRpg/ember-v1.yml). */
    @Test public void shippedConfig() {
        YamlConfiguration y = res();
        ConfigurationSection s = y.getConfigurationSection("signin"), o = y.getConfigurationSection("online");
        assertNotNull(s); assertNotNull(o);
        assertTrue(s.getBoolean("enabled")); assertTrue(o.getBoolean("enabled"));
        assertEquals(3, s.getInt("makeup_per_month"));
        assertEquals(60, s.getInt("makeup_needs_online"));
        assertEquals(5, o.getInt("idle_minutes"));
        assertTrue("D180 rev 2: the AFK world counts", o.getStringList("exclude_worlds").isEmpty());
        assertTrue(o.getBoolean("afk_combat_counts"));
        ConfigurationSection sp = s.getConfigurationSection("special");
        int monthCoin = 0, marks = 0, sig = 0;
        for (int n = 1; n <= 30; n++) {
            ConfigurationSection c = sp.isConfigurationSection(String.valueOf(n)) ? sp.getConfigurationSection(String.valueOf(n))
                    : n > 28 ? s.getConfigurationSection("extra") : s.getConfigurationSection("daily");
            monthCoin += c.getInt("coin"); marks += c.getInt("mark"); sig += c.getInt("sigmark");
            for (String k : c.getKeys(false)) assertTrue("account-bound keys only: " + k, k.equals("coin") || k.equals("xp") || k.equals("mark") || k.equals("sigmark"));
        }
        assertTrue("a full month of sign-ins < 3 clears of base coin", monthCoin < 3 * EmberRunRules.BASE_COIN);
        assertTrue("≤ 2 forge marks a month", marks <= 2);
        assertTrue("≤ 2 insignia a month", sig <= 2);
        List<Map<?, ?>> ms = o.getMapList("milestones");
        assertEquals(4, ms.size());
        int prev = 0, day = 0;
        int[] want = {15, 30, 60, 120};
        for (int i = 0; i < ms.size(); i++) {
            Map<?, ?> m = ms.get(i);
            int min = Integer.parseInt(String.valueOf(m.get("min")));
            assertEquals(want[i], min);
            assertTrue(min > prev); prev = min;
            day += Integer.parseInt(String.valueOf(m.get("coin")));
            for (Object k : m.keySet()) assertTrue("account-bound keys only: " + k, "min".equals(k) || "coin".equals(k) || "xp".equals(k) || "mark".equals(k) || "sigmark".equals(k));
        }
        assertTrue("a full online day pays less coin than one clear's base", day < EmberRunRules.BASE_COIN);
    }

    /** the live copy (plugins/CoreRpg/ember-v1.yml, read by p1sim) carries the same signin / online sections */
    @Test public void pluginCopyMatches() {
        File f = new File("../plugins/CoreRpg/ember-v1.yml");
        if (!f.isFile()) return; // jar-only checkout
        YamlConfiguration live = YamlConfiguration.loadConfiguration(f), r = res();
        for (String sec : new String[]{"signin", "online"})
            assertEquals(sec, r.getConfigurationSection(sec).getValues(true).toString(), live.getConfigurationSection(sec).getValues(true).toString());
    }
}

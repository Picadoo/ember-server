package town.sunshine.corerpg.p1;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/** D177 P1 挂机庭: offline conversion, caps, and the shipped config against the design / p1sim numbers. */
public class EmberAfkServiceTest {

    @Test public void offlineRounds() {
        assertEquals(0, EmberAfkService.offlineRoundsFor(0, 4, 10));
        assertEquals(0, EmberAfkService.offlineRoundsFor(39, 4, 10));   // 39 min / 4 = 9.75 counted → no round
        assertEquals(1, EmberAfkService.offlineRoundsFor(40, 4, 10));
        assertEquals(12, EmberAfkService.offlineRoundsFor(8 * 60, 4, 10)); // one night = 120 counted minutes (capped later)
        assertEquals(0, EmberAfkService.offlineRoundsFor(-5, 4, 10));    // clock went backwards → nothing
        assertEquals(0, EmberAfkService.offlineRoundsFor(100, 0, 10));
    }

    @Test public void caps() {
        // online: only the daily cap
        assertEquals(1, EmberAfkService.grantable(1, 0, 12, false, 0, 6));
        assertEquals(0, EmberAfkService.grantable(1, 12, 12, false, 0, 6));
        // offline: daily cap AND the offline sub-cap
        assertEquals(6, EmberAfkService.grantable(12, 0, 12, true, 0, 6));
        assertEquals(2, EmberAfkService.grantable(12, 0, 12, true, 4, 6));
        assertEquals(1, EmberAfkService.grantable(12, 11, 12, true, 0, 6));
        assertEquals(0, EmberAfkService.grantable(12, 3, 12, true, 6, 6));
        assertEquals(0, EmberAfkService.grantable(0, 0, 12, true, 0, 6));
        assertEquals(0, EmberAfkService.grantable(-3, 0, 12, false, 0, 6));
    }

    private static ConfigurationSection afk() {
        YamlConfiguration y = YamlConfiguration.loadConfiguration(new InputStreamReader(
                EmberAfkServiceTest.class.getResourceAsStream("/ember-v1.yml"), StandardCharsets.UTF_8));
        ConfigurationSection s = y.getConfigurationSection("afk");
        assertNotNull("ember-v1.yml afk:", s);
        assertFalse("legacy auction stays closed in P1 by default", y.getBoolean("legacy_auction", true));
        return s;
    }

    /** = tools/p1sim/afk.py TIERS (daily coin / xp per tier) and DESIGN-ember-afk-p1 §3; idle never out-pays one clear. */
    @Test public void shippedConfigMatchesDesign() {
        ConfigurationSection s = afk();
        int rounds = s.getInt("daily_rounds");
        assertEquals(12, rounds);
        assertEquals(10, s.getInt("round_minutes"));
        assertEquals(4, s.getInt("offline_ratio"));
        assertEquals(6, s.getInt("offline_rounds"));
        assertFalse(s.getBoolean("legacy_payouts", true));
        int[][] want = {{96, 36}, {144, 60}, {192, 72}, {300, 120}};
        String[] req = {"q01", "q03", "q05", "q07"};
        List<Map<?, ?>> ts = s.getMapList("tiers");
        assertEquals(4, ts.size());
        for (int i = 0; i < 4; i++) {
            Map<?, ?> t = ts.get(i);
            assertEquals(req[i], String.valueOf(t.get("requires")));
            int coin = Integer.parseInt(String.valueOf(t.get("coin"))) * rounds, xp = Integer.parseInt(String.valueOf(t.get("xp"))) * rounds;
            assertEquals("tier " + (i + 1) + " coin/day", want[i][0], coin);
            assertEquals("tier " + (i + 1) + " xp/day", want[i][1], xp);
            assertTrue("a full AFK day never pays more coin than one clear's base", coin <= EmberRunRules.BASE_COIN);
            assertTrue("a full AFK day never pays more xp than one clear's base", xp <= EmberRunRules.BASE_XP);
        }
    }
}

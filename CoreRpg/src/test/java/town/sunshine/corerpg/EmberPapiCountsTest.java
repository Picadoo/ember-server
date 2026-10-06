package town.sunshine.corerpg;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/** D241 / Job A: %ember_daily_left% / %ember_weekly_left% come from the S0 stamina + weekly free credit counters. */
public final class EmberPapiCountsTest {

    private static StaminaService st() { return new StaminaService(null, null, null); } // defaults(): daily 30, base max 90

    private static PlayerData fresh(StaminaService st) {
        PlayerData d = new PlayerData();
        st.ensure(d); // daily reset → stamina = max (90), week credits = 1
        return d;
    }

    @Test public void dailyLeftIsEntriesCurrentStaminaPaysFor() {
        StaminaService st = st();
        PlayerData d = fresh(st);
        assertEquals("3", EmberPapiCounts.resolve(st, d, "daily_left"));   // 90 / 30 — the design's "今日剩余 3/3"
        d.setStamina(60); assertEquals("2", EmberPapiCounts.resolve(st, d, "daily_left"));
        d.setStamina(59); assertEquals("1", EmberPapiCounts.resolve(st, d, "daily_left"));
        d.setStamina(29); assertEquals("0", EmberPapiCounts.resolve(st, d, "daily_left"));
        d.setStamina(0);  assertEquals("0", EmberPapiCounts.resolve(st, d, "daily_left"));
        d.setStaminaBank(30); // bank is not spendable on entry (consumeForEnter checks current stamina only)
        assertEquals("0", EmberPapiCounts.resolve(st, d, "daily_left"));
    }

    @Test public void dailyLeftArithmetic() {
        assertEquals("4", EmberPapiCounts.dailyLeftText(120, 30)); // monthly card max 120
        assertEquals("0", EmberPapiCounts.dailyLeftText(-5, 30));
        assertEquals("∞", EmberPapiCounts.dailyLeftText(10, 0));
    }

    @Test public void weeklyLeftIsTheWeeklyFreeCredit() {
        StaminaService st = st();
        PlayerData d = fresh(st);
        assertEquals("1", EmberPapiCounts.resolve(st, d, "weekly_left"));   // "本周剩余 1/1"
        d.setWeeklyGrantCreditWeekly(0);
        assertEquals("0", EmberPapiCounts.resolve(st, d, "weekly_left"));   // same week → no refill
        d.setWeeklyGrantCreditWeekId("2000-W01");                           // new week → rolls over to 1
        assertEquals("1", EmberPapiCounts.resolve(st, d, "weekly_left"));
        // same counter as %corerpg_stamina_credit_weekly%
        assertEquals(CorePapiStamina.resolve(st, d, "stamina_credit_weekly"), EmberPapiCounts.resolve(st, d, "weekly_left"));
        d.setStamina(0); // weekly_left is the free entry only; stamina does not change it
        assertEquals("1", EmberPapiCounts.resolve(st, d, "weekly_left"));
    }

    @Test public void unknownOrMissing() {
        StaminaService st = st();
        PlayerData d = fresh(st);
        assertNull(EmberPapiCounts.resolve(st, d, "monthly_left"));
        assertNull(EmberPapiCounts.resolve(null, d, "daily_left"));
        assertNull(EmberPapiCounts.resolve(st, null, "weekly_left"));
        assertEquals(new TreeSet<String>(Arrays.asList("daily_left", "weekly_left")), new TreeSet<String>(EmberPapiCounts.KEYS));
    }

    /** Every %ember_*% key in live configs (TrMenu / DP / holograms / resources, .yml and the menu README) is provided. */
    @Test public void everyConfigEmberKeyIsProvided() throws IOException {
        Set<String> personal = new HashSet<String>(Arrays.asList("power_score", "abyss_best", "weekly_best_sec"));
        Pattern ladder = Pattern.compile("ladder_(power|abyss|speed)_\\d+_(name|value)");
        Pattern p = Pattern.compile("%ember_([a-z0-9_]+)%");
        TreeSet<String> stray = new TreeSet<String>();
        for (String dir : Arrays.asList("src/main/resources", "../plugins")) {
            Path root = Paths.get(dir);
            if (!Files.isDirectory(root)) continue;
            try (Stream<Path> s = Files.walk(root)) {
                for (Path f : (Iterable<Path>) s.filter(x -> { String n = x.toString();
                        return (n.endsWith(".yml") || n.endsWith(".md")) && !n.contains("/MythicMobs/SavedData/"); })::iterator) {
                    Matcher m = p.matcher(new String(Files.readAllBytes(f), StandardCharsets.UTF_8));
                    while (m.find()) {
                        String k = m.group(1);
                        if (personal.contains(k) || EmberPapiCounts.KEYS.contains(k) || ladder.matcher(k).matches()) continue;
                        stray.add(k + " @ " + f);
                    }
                }
            }
        }
        assertEquals(Collections.<String>emptySet(), stray);
    }
}

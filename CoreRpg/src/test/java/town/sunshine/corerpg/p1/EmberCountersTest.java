package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/** D206 (ARCH S1-1): every PlayerData counter key in src/main/java is registered in {@link EmberCounters}. */
public class EmberCountersTest {
    /** P1 key literals: p<n>_… plus the delivery / payment markers. */
    private static final Pattern P1_LITERAL = Pattern.compile("\"(p[0-9]_[a-z0-9_]*|p1dlv_|p1paid_)\"");
    /** Literal first argument of periodCount / addPeriodCount (catches legacy keys). */
    private static final Pattern CALL_LITERAL = Pattern.compile("[pP]eriodCount\\(\\s*\"([A-Za-z0-9_]+)\"");
    /** Literals that look like counter keys but are not (file:literal → reason). */
    private static final Set<String> NOT_COUNTERS = new HashSet<String>(Arrays.asList(
        "EmberRunService.java:p1_",     // mail id prefix "p1_" + base36 time
        "CorePapi.java:p1_",            // PlaceholderAPI key namespace (D240: routed in CorePapi)
        "EmberRunMaps.java:p1_",        // short-map key namespace / comments (not a counter family)
        "EmberRunMaps.java:p4_",        // rush claim validation: config claim keys must start with p4_
        "EmberShortRules.java:p1_",     // short claim key builder prefix in comments/helpers
        "call:life_pet_ashling",        // life pet display ids (LifeService; not EmberCounters)
        "call:life_pet_cinder",
        "call:life_soul_dust",
        "call:life_soul_dust_bone"));

    private static List<Path> sources() throws IOException {
        try (Stream<Path> s = Files.walk(Paths.get("src/main/java"))) {
            return s.filter(p -> p.toString().endsWith(".java") && !p.getFileName().toString().equals("EmberCounters.java")) // the registry itself does not count as a use
                .collect(Collectors.toList());
        }
    }

    private static Set<String> literals(Pattern pat, boolean withFile) throws IOException {
        Set<String> out = new TreeSet<String>();
        for (Path p : sources()) {
            Matcher m = pat.matcher(new String(Files.readAllBytes(p), StandardCharsets.UTF_8));
            while (m.find()) out.add(withFile ? p.getFileName() + ":" + m.group(1) : m.group(1));
        }
        return out;
    }

    @Test public void everyCounterLiteralIsRegistered() throws IOException {
        List<String> missing = new ArrayList<String>();
        for (String fl : literals(P1_LITERAL, true)) {
            if (NOT_COUNTERS.contains(fl)) continue;
            String key = fl.substring(fl.indexOf(':') + 1);
            if (EmberCounters.byKey(key) == null) missing.add(fl);
        }
        for (String key : literals(CALL_LITERAL, false)) {
            String tag = "call:" + key;
            if (NOT_COUNTERS.contains(tag)) continue;
            if (EmberCounters.byKey(key) == null) missing.add(tag);
        }
        assertEquals("register these counter families in EmberCounters", new ArrayList<String>(), missing);
    }

    @Test public void noDeadEntries() throws IOException {
        StringBuilder all = new StringBuilder();
        for (Path p : sources()) all.append(new String(Files.readAllBytes(p), StandardCharsets.UTF_8));
        String runs = new String(Files.readAllBytes(Paths.get("src/main/resources/ember-v1-runs.yml")), StandardCharsets.UTF_8);
        List<String> dead = new ArrayList<String>();
        for (EmberCounters.Family f : EmberCounters.all()) {
            if (f.config) { if (!runs.contains("claim: " + f.key)) dead.add(f.key); }
            else if (!all.toString().contains("\"" + f.key + "\"")) dead.add(f.key);
        }
        assertEquals(new ArrayList<String>(), dead);
    }

    @Test public void noUnexpectedPrefixOverlap() {
        assertEquals(new ArrayList<String>(), EmberCounters.prefixConflicts());
    }

    @Test public void lookupUsesExactThenLongestPrefix() {
        assertEquals("p1_codex_stage_", EmberCounters.lookup("p1_codex_stage_3@all").key);
        assertEquals("p1_codex_", EmberCounters.lookup("p1_codex_ember_blade_t2").key);
        assertEquals("p1_sigmark_", EmberCounters.lookup("p1_sigmark_q03").key);
        assertEquals("p1_sig_", EmberCounters.lookup("p1_sig_abc123").key);
        assertEquals("p1_sign_mkd", EmberCounters.lookup("p1_sign_mkd@2026-10-05").key);
        assertEquals("p4_rush_claim", EmberCounters.lookup("p4_rush_claim@w40").key);
        assertEquals("p4_rush", EmberCounters.lookup("p4_rush").key);
        assertEquals("afk_coin", EmberCounters.lookup("afk_coin").key);
        assertEquals("afk_", EmberCounters.lookup("afk_bone").key);
        assertEquals("p4_spec_row", EmberCounters.lookup("p4_spec_row2").key);
        assertNull(EmberCounters.lookup("p1_sig_"));      // bare prefix is not a key of the family
        assertNull(EmberCounters.lookup("p9_nothing"));
        assertNull(EmberCounters.lookup(null));
    }

    @Test public void assetAndItemSetsMatchRegDoc() {
        Set<String> assets = new LinkedHashSet<String>(), items = new LinkedHashSet<String>();
        for (EmberCounters.Family f : EmberCounters.all()) {
            if (f.asset()) assets.add(f.key);
            if (f.item()) items.add(f.key);
        }
        assertEquals(new TreeSet<String>(Arrays.asList("p1_mark_t", "p1_sigmark_", "p3_badge", "p1_afk_acc_", "p1_vbound_", "p1_blank_tenths")), new TreeSet<String>(assets)); // D318: armor dismantle tenths
        assertEquals(new TreeSet<String>(Arrays.asList("p4_af_", "p4_afp_", "p1_sig_", "p4_rrn_", "p4_rro_")), new TreeSet<String>(items));
    }

    @Test public void constantsAgreeWithRegistry() {
        assertNotNull(EmberCounters.byKey(EmberFirstClear.C_FACT));
        assertNotNull(EmberCounters.byKey(EmberFirstClear.C_PAID));
        assertNotNull(EmberCounters.byKey(EmberPayRules.MARK_COUNTER));
        assertNotNull(EmberCounters.byKey(EmberSeason.C_BADGE));
        assertNotNull(EmberCounters.byKey(EmberCodex.C_CLAIM));
        assertEquals(EmberCounters.Category.ASSET, EmberCounters.lookup(EmberSignature.C_MARK + "q01").category);
        assertTrue(EmberCounters.lookup(EmberDelivery.paidMarker("rid-1")).key.equals("p1paid_"));
    }

    @Test public void rushClaimCheck() {
        assertNull(EmberCounters.rushClaimError("p4_rush_claim"));
        assertNull(EmberCounters.rushClaimError("p4_echo_claim"));
        assertNull(EmberCounters.rushClaimError("p4_outpost_claim"));
        assertNull(EmberCounters.rushClaimError("p4_newhall_claim"));   // unregistered p4_* is allowed at load
        assertNotNull(EmberCounters.rushClaimError("p4_af_x"));         // would write into item affix keys
        assertNotNull(EmberCounters.rushClaimError("p4_spec_resets"));  // another family's exact key
        assertNotNull(EmberCounters.rushClaimError("p4_x@w1"));
        assertNotNull(EmberCounters.rushClaimError(""));
    }

    @Test public void shippedRunsConfigPassesClaimCheck() throws IOException {
        for (String f : new String[] {"src/main/resources/ember-v1-runs.yml", "../plugins/CoreRpg/ember-v1-runs.yml"}) {
            String yml = new String(Files.readAllBytes(Paths.get(f)), StandardCharsets.UTF_8);
            Matcher m = Pattern.compile("(?m)^\\s+claim:\\s*(\\S+)").matcher(yml);
            while (m.find()) {
                String claim = m.group(1);
                // rushClaimError only green-lights PWEEK rush claims; short-day CLAIM rows (p1_sxNN_day) are
                // registered DAY+CLAIM and must be accepted for config claim: lines.
                EmberCounters.Family fam = EmberCounters.byKey(claim);
                if (fam != null && fam.category == EmberCounters.Category.CLAIM) continue;
                assertNull(f + " " + claim, EmberCounters.rushClaimError(claim));
            }
        }
    }
}

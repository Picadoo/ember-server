package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * D241 / ARCH S3-12: fixed-seed damage replay — every affix × {@link #SEEDS} scripted fights (seed 0 = bundled
 * ember-v1-runs.yml numbers, other seeds perturb every variety number × 0.4–1.8 inside the yml clamps) through the
 * pre-D241 code ({@link LegacyAffixPath}) and through the primitives ({@link PrimitiveAffixPath}); the two traces must be
 * identical line by line. Traces + SHA-256 land in {@code target/affix-replay/}.
 */
public final class EmberAffixReplayTest {

    static final int SEEDS = 200;

    static Map<?, ?> bundledVariety() {
        InputStream in = EmberAffixReplayTest.class.getResourceAsStream("/ember-v1-runs.yml");
        assertNotNull(in);
        Map<?, ?> root = (Map<?, ?>) new Yaml().load(new InputStreamReader(in, StandardCharsets.UTF_8));
        return (Map<?, ?>) root.get("variety");
    }

    private static String sha(List<String> lines) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (String l : lines) { md.update(l.getBytes(StandardCharsets.UTF_8)); md.update((byte) '\n'); }
        StringBuilder sb = new StringBuilder();
        for (byte b : md.digest()) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    @Test public void replayIsIdenticalForEveryAffixAndSeed() throws Exception {
        Map<?, ?> bv = bundledVariety();
        AffixPath legacy = new LegacyAffixPath(), prim = new PrimitiveAffixPath();
        List<String> all0 = new ArrayList<String>(), all1 = new ArrayList<String>();
        Map<String, int[]> stats = new LinkedHashMap<String, int[]>(); // affix → {lines, hits, casts, other}
        List<String> known = EmberRunMaps.Variety.KNOWN;
        for (long seed = 0; seed < SEEDS; seed++) {
            EmberRunMaps.Variety v = AffixReplay.variety(bv, seed);
            for (int ai = 0; ai < known.size(); ai++) {
                String a = known.get(ai);
                AffixReplay.Script sc = AffixReplay.Script.of(seed * 7919L + ai);
                List<String> t0 = AffixReplay.run(legacy, a, sc, v), t1 = AffixReplay.run(prim, a, sc, v);
                if (!t0.equals(t1)) {
                    int n = Math.min(t0.size(), t1.size()), i = 0;
                    while (i < n && t0.get(i).equals(t1.get(i))) i++;
                    fail("affix " + a + " seed " + seed + " diverges at line " + i + ":\n legacy: "
                            + (i < t0.size() ? t0.get(i) : "<end>") + "\n prim:   " + (i < t1.size() ? t1.get(i) : "<end>"));
                }
                int[] st = stats.get(a);
                if (st == null) stats.put(a, st = new int[4]);
                for (String l : t0) {
                    st[0]++;
                    String ev = l.split("\t")[2];
                    if (ev.startsWith("HIT ")) st[1]++;
                    else if (ev.startsWith("CAST ")) st[2]++;
                    else if (!ev.startsWith("PROMOTE") && !ev.startsWith("DIE")) st[3]++;
                }
                for (String l : t0) all0.add(seed + "\t" + l);
                for (String l : t1) all1.add(seed + "\t" + l);
            }
        }
        // the scripts must actually exercise every behaviour
        for (String a : new String[]{"blazing", "charge", "mortar", "venom", "jailer", "arcane", "firechain", "molten"})
            assertTrue(a + " never hit anyone", stats.get(a)[1] > 0);
        assertTrue("regen never resolved", stats.get("regen")[3] > 0);
        assertTrue("frost never pulsed", stats.get("frost")[3] > 0);
        assertTrue("split never spawned", stats.get("split")[3] > 0);
        String h0 = sha(all0), h1 = sha(all1);
        assertEquals(h0, h1);
        File dir = new File("target/affix-replay");
        dir.mkdirs();
        Files.write(new File(dir, "legacy-46736db.tsv").toPath(), all0, StandardCharsets.UTF_8);
        Files.write(new File(dir, "primitives-d241.tsv").toPath(), all1, StandardCharsets.UTF_8);
        List<String> sum = new ArrayList<String>();
        sum.add("D241 affix damage replay: seeds " + SEEDS + " × affixes " + known.size() + " → " + all0.size() + " trace lines");
        sum.add("legacy-46736db sha256 " + h0);
        sum.add("primitives-d241 sha256 " + h1);
        sum.add("affix\tlines\thits\tcasts\tother");
        for (Map.Entry<String, int[]> e : stats.entrySet())
            sum.add(e.getKey() + "\t" + e.getValue()[0] + "\t" + e.getValue()[1] + "\t" + e.getValue()[2] + "\t" + e.getValue()[3]);
        Files.write(new File(dir, "summary.txt").toPath(), sum, StandardCharsets.UTF_8);
        for (String l : sum) System.out.println("[affix-replay] " + l);
    }

    /** sanity: the replay is sensitive — a one-ULP change in a damage packet shows up as a divergence */
    @Test public void replayDetectsADamageChange() throws IOException {
        Map<?, ?> bv = bundledVariety();
        EmberRunMaps.Variety v = AffixReplay.variety(bv, 0);
        AffixPath legacy = new LegacyAffixPath();
        AffixPath bumped = new PrimitiveAffixPath() {
            @Override public EmberRunMaps.Skill venom(double atk, EmberRunMaps.Variety vv) { return super.venom(Math.nextUp(atk), vv); }
        };
        boolean differs = false;
        for (long seed = 0; seed < 20 && !differs; seed++) {
            AffixReplay.Script sc = AffixReplay.Script.of(seed * 7919L + 8);
            differs = !AffixReplay.run(legacy, "venom", sc, v).equals(AffixReplay.run(bumped, "venom", sc, v));
        }
        assertTrue(differs);
    }
}

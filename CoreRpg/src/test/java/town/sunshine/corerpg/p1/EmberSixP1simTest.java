package town.sunshine.corerpg.p1;

import org.junit.Assume;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.*;

/**
 * D318 六槽 T1-2 · Java ↔ p1sim same-parameter cross-check (spec §4): every cell of the export
 * {@code tools/p1sim/six_t1_export.py} (F arm, follow all, charm cost ×1.0) — the T0″ G6 grid (101,376, armor = the
 * charm's quality / craft) and the same grid with seeded armor quality / craft incl. above-the-charm values and empty
 * slots (101,376) — is recomputed by {@link EmberLoadout#compute} (six-slot overload) with tables built from the export
 * header, and B / H / M / D must match with {@link Double#doubleToLongBits} (exact; no ulp tolerance).
 * p1sim has no "no charm" state, so the no-charm arm is Java-only: six-slot == 2-slot bit for bit.
 *
 * <p>Offline. The export (≈1.9 MB gz, 2–3 s) is regenerated into {@code target/} with the local python3 when missing;
 * {@code -Dsix.p1sim.file=…} uses a given file. No python3 → the test is skipped (reported as SKIP, never as PASS).
 */
public class EmberSixP1simTest {

    static File export() throws Exception {
        String given = System.getProperty("six.p1sim.file");
        if (given != null) return new File(given);
        File out = new File("target/six-t1-p1sim.tsv.gz");
        if (out.isFile() && out.length() > 0) return out;
        File script = new File("../tools/p1sim/six_t1_export.py");
        Assume.assumeTrue("tools/p1sim/six_t1_export.py missing", script.isFile());
        try {
            Process p = new ProcessBuilder("nice", "-n", "19", "python3", script.getPath(), out.getPath()).redirectErrorStream(true)
                    .redirectOutput(new File("target/six-t1-p1sim.log")).start();
            boolean done = p.waitFor(180, TimeUnit.SECONDS);
            if (!done) p.destroyForcibly();
            Assume.assumeTrue("python3 export failed (see target/six-t1-p1sim.log)", done && p.exitValue() == 0 && out.isFile());
        } catch (java.io.IOException e) {
            Assume.assumeTrue("python3 not available: " + e.getMessage(), false);
        }
        return out;
    }

    static double hx(Object o) { return Double.parseDouble(String.valueOf(o)); }

    static List<Number> row(Object o) {
        List<Number> l = new ArrayList<Number>();
        for (Object x : (List<?>) o) l.add(hx(x));
        return l;
    }

    static int seq;
    static String uid() { return String.format("%032x", ++seq); }

    static EmberItemData item(String fam, String slot, int tier, int q, int f, int e) {
        String fm = tier == 0 ? "none" : fam;
        return new EmberItemData(uid(), EmberItemData.templateId(fm, slot, tier), fm, slot, tier, q, f, e, 0, true, "drop",
                EmberItemData.DATA_VERSION, 0);
    }

    @Test public void javaMatchesP1simBitForBit() throws Exception {
        File f = export();
        Assume.assumeTrue(f.isFile());
        EmberTables t = null;
        int[] cells = new int[2];
        int[] bad = new int[4];
        int noCharm = 0, noCharmBad = 0;
        String firstBad = null;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(f)), StandardCharsets.US_ASCII))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.startsWith("#cfg ")) {
                    Map<?, ?> c = (Map<?, ?>) new Yaml().load(line.substring(5));
                    t = EmberTables.of(row(c.get("A")), row(c.get("h")), row(c.get("D")), row(c.get("q")), row(c.get("f")), row(c.get("e")),
                            EmberTables.defaults().critRate, EmberTables.defaults().critMult, hx(c.get("def_k")), hx(c.get("def_floor")), hx(c.get("sustain_hp")));
                    // the parts EmberTables.of keeps from the Java defaults must be what p1sim used
                    assertEquals(Double.doubleToLongBits(t.levelAtkPer), Double.doubleToLongBits(hx(c.get("lvl_atk"))));
                    assertEquals(Double.doubleToLongBits(t.levelHpPer), Double.doubleToLongBits(hx(c.get("lvl_hp"))));
                    assertEquals(t.levelBase, ((Number) c.get("lvl_base")).intValue());
                    assertEquals(t.levelCap, ((Number) c.get("lvl_cap")).intValue());
                    List<?> w = (List<?>) c.get("w");
                    assertEquals(Double.doubleToLongBits(EmberSixSlot.CHARM_W), Double.doubleToLongBits(hx(w.get(0))));
                    double[] aw = EmberSixSlot.armorWeights();
                    for (int i = 0; i < 4; i++) assertEquals(Double.doubleToLongBits(aw[i]), Double.doubleToLongBits(hx(w.get(i + 1))));
                    assertEquals("all", c.get("follow"));
                    continue;
                }
                assertNotNull("header first", t);
                String[] x = line.split("\t");
                String arm = x[0], fam = x[1];
                int bt = Integer.parseInt(x[2]), ct = Integer.parseInt(x[3]), be = Integer.parseInt(x[4]), ce = Integer.parseInt(x[5]);
                int q = Integer.parseInt(x[6]), cf = Integer.parseInt(x[7]), lv = Integer.parseInt(x[8]);
                int empty = Integer.parseInt(x[17]);
                EmberItemData blade = item(fam, "blade", bt, 0, 0, be);
                EmberItemData charm = item(fam, "charm", ct, q, cf, ce);
                EmberItemData[] armor = new EmberItemData[4];
                for (int i = 0; i < 4; i++) {
                    if ((empty & (1 << i)) != 0) continue;
                    armor[i] = item(fam, EmberItemData.ARMOR_SLOTS.get(i), ct, Integer.parseInt(x[9 + i]), Integer.parseInt(x[13 + i]), 0);
                }
                EmberLoadout l = EmberLoadout.compute(t, blade, charm, lv, 0, 0, armor);
                double[] want = {hx(x[18]), hx(x[19]), hx(x[20]), hx(x[21])};
                double[] got = {l.b, l.h, l.m, l.d};
                boolean ok = true;
                for (int k = 0; k < 4; k++) if (Double.doubleToLongBits(want[k]) != Double.doubleToLongBits(got[k])) { bad[k]++; ok = false; }
                if (!ok && firstBad == null) firstBad = line + " → java B " + l.b + " H " + l.h + " M " + l.m + " D " + l.d;
                cells["g6".equals(arm) ? 0 : 1]++;
                if ("var".equals(arm)) { // Java-only no-charm arm: armor adds nothing
                    EmberLoadout six = EmberLoadout.compute(t, blade, null, lv, 0, 0, armor), two = EmberLoadout.compute(t, blade, null, lv, 0, 0);
                    noCharm++;
                    if (!java.util.Arrays.equals(EmberSixMigration.bits(six), EmberSixMigration.bits(two)) || Double.doubleToLongBits(six.d) != Double.doubleToLongBits(two.d)) noCharmBad++;
                }
            }
        }
        int mism = bad[0] + bad[1] + bad[2] + bad[3];
        System.out.println(String.format("[T1-2] p1sim export %s · g6 %d · var %d · mismatches B %d H %d M %d D %d (exact doubleToLongBits, no tolerance) · no-charm %d (six == 2-slot) bad %d",
                f.getName(), cells[0], cells[1], bad[0], bad[1], bad[2], bad[3], noCharm, noCharmBad));
        assertEquals(101376, cells[0]);
        assertEquals(101376, cells[1]);
        assertEquals("first mismatch: " + firstBad, 0, mism);
        assertEquals(0, noCharmBad);
    }

    /** offline (no python): pySum is CPython ≥ 3.12 sum(), and all-zero differences stay exactly +0.0 */
    @Test public void pySumMatchesCpython() {
        assertEquals(Double.doubleToLongBits(0.6), Double.doubleToLongBits(EmberFormula.pySum(new double[]{0.1, 0.2, 0.3}))); // python3.13: sum([.1,.2,.3]) == 0.6
        assertNotEquals(Double.doubleToLongBits(0.6), Double.doubleToLongBits(0.1 + 0.2 + 0.3));                           // naive: 0.6000000000000001
        assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(EmberFormula.pySum(new double[]{0.0, 0.0, 0.0, 0.0})));
        assertEquals(Double.doubleToLongBits(0.0), Double.doubleToLongBits(EmberFormula.pySum(new double[]{-0.0, 0.0})));     // int 0 + -0.0 → +0.0
        assertEquals(Double.doubleToLongBits(2.0), Double.doubleToLongBits(EmberFormula.pySum(new double[]{1.0, 1e100, 1.0, -1e100}))); // python3.13: 2.0
    }
}

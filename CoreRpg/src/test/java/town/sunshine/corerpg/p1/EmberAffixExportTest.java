package town.sunshine.corerpg.p1;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import town.sunshine.corerpg.p1.encounter.*;

import java.io.Reader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

/**
 * D244 (ARCH S4-3): the affix numbers p1sim needs, exported from the D241 primitives ({@code p1.encounter.Affix*} +
 * {@link AffixCycle}) applied to the live {@code variety} block of {@code plugins/CoreRpg/ember-v1-runs.yml} (through
 * {@link EmberRunMaps.Variety}, so the yml clamps apply). The checked-in table is {@code tools/p1sim/affix-table.json};
 * p1sim reads it instead of re-deriving cadence / damage by hand.
 *
 * <p>Drift: this test regenerates the table and fails when it differs from the checked-in file (code or yml changed
 * without re-export). Re-export: {@code mvn -o test -Dtest=EmberAffixExportTest -Daffix.export=write} (or
 * {@code AFFIX_EXPORT=write}). p1sim's {@code rules.py} additionally refuses to run when the table's {@code yml}
 * inputs no longer match the live variety block.
 *
 * <p>Timing model (wall clock from promotion, the Director's tick): first cast attempt at {@code AffixCycle.firstNext}
 * (1.5 s + firstEvery), the warning runs {@code warn}, the hit lands, the next attempt is {@code every} after landing →
 * period = warn + every. 旋光: the target stands in the middle of the swept arc → hit at warn + spin/2; next attempt
 * {@code every} after the spin ends → period = warn + spin + every. 火链: link at firstNext (1.5 s + warn), burns from
 * {@code liveAt} (+ warn), at most once per {@code tick} per player while touching.
 */
public final class EmberAffixExportTest {

    static final Path ROOT = Paths.get("..");
    static final Path TABLE = ROOT.resolve("tools/p1sim/affix-table.json");
    static final Path RUNS = ROOT.resolve("plugins/CoreRpg/ember-v1-runs.yml");
    static final String GENERATOR = "CoreRpg/src/test/java/town/sunshine/corerpg/p1/EmberAffixExportTest.java";

    static Map<?, ?> liveVariety() throws Exception {
        try (Reader r = Files.newBufferedReader(RUNS, StandardCharsets.UTF_8)) {
            Map<?, ?> root = (Map<?, ?>) new Yaml().load(r);
            return (Map<?, ?>) root.get("variety");
        }
    }

    static int balanceVersion() throws Exception {
        try (Reader r = Files.newBufferedReader(RUNS, StandardCharsets.UTF_8)) {
            Object bv = ((Map<?, ?>) new Yaml().load(r)).get("balance_version");
            return bv instanceof Number ? ((Number) bv).intValue() : -1;
        }
    }

    private static double s(long ms) { return ms / 1000.0; }

    /** one periodic telegraphed cast (CIRCLE / STRIP / CROSS): arm at firstNext, land after warn, re-arm every after */
    private static void periodic(Map<String, Object> o, AffixBehavior b, EmberRunMaps.Variety v, double warn, double every) {
        long arm = AffixCycle.firstNext(0, b.firstEvery(v));
        long land = AffixCycle.after(arm, warn);
        long next = AffixCycle.after(land, every);
        long land2 = AffixCycle.after(next, warn);
        o.put("first_arm_s", s(arm));
        o.put("first_hit_s", s(land));
        o.put("period_s", s(land2 - land));
    }

    private static Map<String, Object> shape(EmberRunMaps.Skill k) {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("type", k.type);
        if ("circle".equals(k.type)) { m.put("radius", k.radius); m.put("ahead", k.ahead); }
        else { m.put("length", k.length); m.put("width", k.width); }
        return m;
    }

    /** the table (ordered maps → deterministic JSON) */
    static Map<String, Object> table(Map<?, ?> yml, int bv) {
        EmberRunMaps.Variety v = new EmberRunMaps.Variety(yml);
        Map<String, Object> out = new LinkedHashMap<String, Object>();
        out.put("generated_by", GENERATOR);
        out.put("note", "GENERATED — do not edit. Re-export: cd CoreRpg && mvn -o test -Dtest=EmberAffixExportTest -Daffix.export=write");
        out.put("balance_version", bv);
        out.put("grace_s", s(AffixCycle.firstNext(0, 0)));
        Map<String, Object> all = new LinkedHashMap<String, Object>();
        for (AffixBehavior b : EmberAffixes.all().values()) {
            Map<String, Object> o = new LinkedHashMap<String, Object>();
            o.put("family", b.family().name());
            o.put("label", b.label());
            String id = b.id();
            if ("blazing".equals(id)) {
                EmberRunMaps.Skill k = AffixBlazing.skill(1.0, v);
                o.put("engage", AffixBlazing.ENGAGE);
                periodic(o, b, v, k.warn, v.blazeEvery);
                o.put("warn_s", k.warn); o.put("dmg_atk", k.dmg); o.put("hits_per_cast", 1); o.put("shape", shape(k));
            } else if ("split".equals(id)) {
                o.put("count", AffixSplit.count(v));
                o.put("add_hp", AffixSplit.addMaxHp(1000.0, v) / 1000.0);
            } else if ("shield".equals(id)) {
                o.put("hp_mult", AffixShield.maxHp(1000.0, v) / 1000.0);
            } else if ("regen".equals(id)) {
                long arm = AffixCycle.firstNext(0, b.firstEvery(v));
                long end = AffixRegen.windowEnd(arm, v);
                long next = AffixCycle.after(end, v.regenEvery);
                o.put("engage", AffixRegen.ENGAGE);
                o.put("first_arm_s", s(arm)); o.put("window_s", s(end - arm)); o.put("period_s", s(AffixRegen.windowEnd(next, v) - end));
                o.put("heal_maxhp", AffixRegen.heal(0, 1.0, v)); o.put("interrupt_maxhp", v.regenInterruptHp);
            } else if ("charge".equals(id)) {
                EmberRunMaps.Skill k = AffixCharge.skill(1.0, v, null);
                o.put("engage", AffixCharge.ENGAGE);
                periodic(o, b, v, k.warn, v.chargeEvery);
                o.put("warn_s", k.warn); o.put("dmg_atk", k.dmg); o.put("hits_per_cast", 1); o.put("shape", shape(k));
            } else if ("frost".equals(id)) {
                o.put("first_arm_s", s(AffixCycle.firstNext(0, b.firstEvery(v))));
                o.put("pulse_s", s(AffixFrost.nextTick(0, v))); o.put("radius", v.frostRadius);
                o.put("amplifier", v.frostAmplifier); o.put("slow_ticks", AffixFrost.slowTicks(v));
            } else if ("mortar".equals(id)) {
                EmberRunMaps.Skill k = AffixMortar.skill(1.0, v);
                o.put("reach", AffixMortar.REACH);
                periodic(o, b, v, k.warn, v.mortarEvery);
                o.put("warn_s", k.warn); o.put("dmg_atk", k.dmg); o.put("hits_per_cast", 1); o.put("shape", shape(k));
            } else if ("molten".equals(id)) {
                EmberRunMaps.Skill k = AffixMolten.skill(1.0, v);
                long warnAt = AffixMolten.warnAt(0, v);
                o.put("warn_after_death_s", s(warnAt)); o.put("hit_after_death_s", s(AffixMolten.boomAt(warnAt, v)));
                o.put("dmg_atk", k.dmg); o.put("split_adds_blast", AffixMolten.triggers(true)); o.put("shape", shape(k));
            } else if ("venom".equals(id)) {
                EmberRunMaps.Skill k = AffixVenom.skill(1.0, v);
                o.put("engage", AffixVenom.ENGAGE);
                periodic(o, b, v, k.warn, v.venomEvery);
                o.put("warn_s", k.warn); o.put("dmg_atk", k.dmg); o.put("hits_per_cast", 1);
                Map<String, Object> sh = shape(k); sh.put("arms", AffixVenom.dirs(false).length); sh.put("alternates", "+ / x");
                o.put("shape", sh);
            } else if ("jailer".equals(id)) {
                EmberRunMaps.Skill k = AffixJailer.skill(1.0, v);
                periodic(o, b, v, k.warn, v.jailerEvery);
                o.put("warn_s", k.warn); o.put("dmg_atk", k.dmg); o.put("hits_per_cast", 1);
                o.put("root_s", AffixJailer.rootTicks(v) / 20.0); o.put("shape", shape(k));
            } else if ("arcane".equals(id)) {
                long arm = AffixCycle.firstNext(0, b.firstEvery(v));
                long warnEnd = AffixCycle.after(arm, v.arcaneWarn);
                double mid = 0.5 * v.arcaneSpin; // target in the middle of the arc (startAngle) → angle(start, ±, sweep, spin, mid)
                double sweep = AffixArcane.sweepRad(v);
                assertEquals(sweep / 2, Math.abs(AffixArcane.angle(0, 1, sweep, v.arcaneSpin, mid)), 1e-9);
                long spinEnd = warnEnd + (long) Math.ceil(v.arcaneSpin * 1000); // spun(elapsed) once elapsed ≥ spin
                long next = AffixCycle.after(spinEnd, v.arcaneEvery);
                o.put("engage", AffixArcane.ENGAGE);
                o.put("first_arm_s", s(arm)); o.put("first_hit_s", s(warnEnd) + mid);
                o.put("period_s", s(AffixCycle.after(next, v.arcaneWarn) - warnEnd));
                o.put("warn_s", v.arcaneWarn); o.put("spin_s", v.arcaneSpin); o.put("sweep_deg", v.arcaneSweep);
                o.put("dmg_atk", AffixArcane.dmg(1.0, v)); o.put("hits_per_cast", 1);
                Map<String, Object> sh = new LinkedHashMap<String, Object>();
                sh.put("type", "beam"); sh.put("length", v.arcaneLength); sh.put("width", v.arcaneWidth);
                o.put("shape", sh);
            } else if ("firechain".equals(id)) {
                long link = AffixCycle.firstNext(0, b.firstEvery(v));
                long live = AffixFirechain.liveAt(link, v);
                o.put("first_link_s", s(link)); o.put("first_burn_s", s(live));
                o.put("burn_every_s", v.chainLinkTick); o.put("dmg_atk", AffixFirechain.dmg(1.0, v));
                o.put("relink_warn_s", v.chainLinkWarn); o.put("relink_retry_s", s(AffixFirechain.RELINK_RETRY_MS));
                Map<String, Object> sh = new LinkedHashMap<String, Object>();
                sh.put("type", "tether"); sh.put("width", v.chainLinkWidth); sh.put("range", v.chainLinkRange);
                o.put("shape", sh);
            }
            Object raw = yml.get(id);
            Map<String, Object> in = new TreeMap<String, Object>();
            if (raw instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) raw).entrySet()) in.put(String.valueOf(e.getKey()), e.getValue());
            o.put("yml", in);
            all.put(id, o);
        }
        out.put("affixes", all);
        return out;
    }

    static String num(Object x) {
        if (x instanceof Integer || x instanceof Long) return x.toString();
        double d = ((Number) x).doubleValue();
        BigDecimal b = new BigDecimal(d).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros();
        String t = b.toPlainString();
        return t.contains(".") ? t : t + ".0";
    }

    static void json(StringBuilder sb, Object o, String ind) {
        if (o instanceof Map) {
            Map<?, ?> m = (Map<?, ?>) o;
            if (m.isEmpty()) { sb.append("{}"); return; }
            boolean flat = true;
            for (Object v : m.values()) if (v instanceof Map) flat = false;
            sb.append('{');
            int i = 0;
            for (Map.Entry<?, ?> e : m.entrySet()) {
                if (i++ > 0) sb.append(',');
                sb.append(flat ? " " : "\n" + ind + "  ");
                sb.append('"').append(e.getKey()).append("\": ");
                json(sb, e.getValue(), ind + "  ");
            }
            sb.append(flat ? " }" : "\n" + ind + "}");
        } else if (o instanceof Number) sb.append(num(o));
        else if (o instanceof Boolean) sb.append(o);
        else sb.append('"').append(String.valueOf(o).replace("\\", "\\\\").replace("\"", "\\\"")).append('"');
    }

    static String render(Map<String, Object> t) {
        StringBuilder sb = new StringBuilder();
        json(sb, t, "");
        return sb.append('\n').toString();
    }

    @Test public void exportMatchesCheckedInTable() throws Exception {
        String now = render(table(liveVariety(), balanceVersion()));
        boolean write = "write".equals(System.getProperty("affix.export")) || "write".equals(System.getenv("AFFIX_EXPORT"));
        if (write) Files.write(TABLE, now.getBytes(StandardCharsets.UTF_8));
        assertTrue("missing " + TABLE + " — run with -Daffix.export=write", Files.exists(TABLE));
        String have = new String(Files.readAllBytes(TABLE), StandardCharsets.UTF_8);
        assertEquals("tools/p1sim/affix-table.json drifted from the affix primitives / live variety yml — re-export: "
                + "cd CoreRpg && mvn -o test -Dtest=EmberAffixExportTest -Daffix.export=write", now, have);
    }

    @Test public void everyPrimitiveAndEveryLiveAffixIsExported() throws Exception {
        Map<?, ?> yml = liveVariety();
        Map<?, ?> aff = (Map<?, ?>) table(yml, balanceVersion()).get("affixes");
        assertEquals(new ArrayList<Object>(EmberAffixes.all().keySet()), new ArrayList<Object>(aff.keySet()));
        for (Object id : (List<?>) yml.get("affixes")) assertTrue("live pool affix not exported: " + id, aff.containsKey(String.valueOf(id)));
        for (Object id : aff.keySet()) assertTrue("exported affix has no yml block: " + id, yml.get(id) instanceof Map);
    }

    /** the exported first cast attempt is what the replay harness (Director state machine on the primitives) schedules */
    @Test public void firstArmMatchesTheReplayHarness() throws Exception {
        Map<?, ?> yml = liveVariety();
        EmberRunMaps.Variety v = new EmberRunMaps.Variety(yml);
        Map<?, ?> aff = (Map<?, ?>) table(yml, balanceVersion()).get("affixes");
        for (String id : new String[]{"blazing", "regen", "charge", "mortar", "venom", "jailer", "arcane", "firechain"}) {
            List<String> log = AffixReplay.run(new PrimitiveAffixPath(), id, AffixReplay.Script.of(1L), v);
            String promote = null;
            for (String l : log) if (l.contains("PROMOTE")) { promote = l; break; }
            assertNotNull(id, promote);
            long next = Long.parseLong(promote.replaceAll(".*next=(-?\\d+).*", "$1"));
            Map<?, ?> o = (Map<?, ?>) aff.get(id);
            double want = ((Number) (o.containsKey("first_arm_s") ? o.get("first_arm_s") : o.get("first_link_s"))).doubleValue();
            assertEquals(id, want, next / 1000.0, 1e-9);
        }
    }

    /** spot values vs the yml and the primitives (catches a broken exporter, not just drift) */
    @Test public void spotValues() throws Exception {
        Map<?, ?> yml = liveVariety();
        Map<?, ?> aff = (Map<?, ?>) table(yml, balanceVersion()).get("affixes");
        Map<?, ?> blaze = (Map<?, ?>) aff.get("blazing"), by = (Map<?, ?>) yml.get("blazing");
        double every = ((Number) by.get("every")).doubleValue(), warn = ((Number) by.get("warn")).doubleValue();
        assertEquals(1.5 + every, ((Number) blaze.get("first_arm_s")).doubleValue(), 1e-9);
        assertEquals(1.5 + every + warn, ((Number) blaze.get("first_hit_s")).doubleValue(), 1e-9);
        assertEquals(every + warn, ((Number) blaze.get("period_s")).doubleValue(), 1e-9);
        Map<?, ?> arc = (Map<?, ?>) aff.get("arcane"), ay = (Map<?, ?>) yml.get("arcane");
        double ae = ((Number) ay.get("every")).doubleValue(), aw = ((Number) ay.get("warn")).doubleValue(), sp = ((Number) ay.get("spin")).doubleValue();
        assertEquals(ae + aw + sp, ((Number) arc.get("period_s")).doubleValue(), 1e-9);
        assertEquals(1.5 + ae + aw + sp / 2, ((Number) arc.get("first_hit_s")).doubleValue(), 1e-9);
        assertEquals(((Number) ((Map<?, ?>) yml.get("shield")).get("hp")).doubleValue(), ((Number) ((Map<?, ?>) aff.get("shield")).get("hp_mult")).doubleValue(), 1e-9);
    }
}

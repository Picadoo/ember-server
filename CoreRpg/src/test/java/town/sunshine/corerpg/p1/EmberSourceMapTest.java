package town.sunshine.corerpg.p1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

/**
 * D242 / ARCH S4-1 — guards docs/design/ember-source-map.yml (the machine-readable gear / material / currency source map).
 * Fails when a live drop source, economy key, ledger / grant key, run prefix, map, signature, affix, item source or
 * material is missing from the map, when the map lists something that is gone, or when a code / config ref is stale.
 * Read-only: nothing in the plugin reads the map; this test only reads the map, the live configs and p1 sources.
 */
@SuppressWarnings("unchecked")
public class EmberSourceMapTest {

    private static final Path ROOT = Paths.get("..");
    private static final Path MAP_FILE = ROOT.resolve("docs/design/ember-source-map.yml");
    private static final Path P1 = Paths.get("src/main/java/town/sunshine/corerpg/p1");
    private static final String[] SECTIONS = {"sources", "legacy", "sinks"};

    private static Map<String, Object> map;
    private static Path codeRoot, configRoot;
    private static final Map<String, String> SOURCES_TEXT = new TreeMap<String, String>();

    @BeforeClass
    public static void load() throws IOException {
        assertTrue("missing " + MAP_FILE, Files.isRegularFile(MAP_FILE));
        map = yaml(MAP_FILE);
        codeRoot = ROOT.resolve(str(map.get("code_root")));
        configRoot = ROOT.resolve(str(map.get("config_root")));
        DirectoryStream<Path> ds = Files.newDirectoryStream(P1, "*.java");
        try {
            for (Path p : ds) SOURCES_TEXT.put(p.getFileName().toString(), new String(Files.readAllBytes(p), StandardCharsets.UTF_8));
        } finally { ds.close(); }
        assertTrue("p1 sources not found", SOURCES_TEXT.size() > 50);
    }

    // ------------------------------------------------------------------------------------------------ helpers

    private static Map<String, Object> yaml(Path p) throws IOException {
        Reader r = Files.newBufferedReader(p, StandardCharsets.UTF_8);
        try { Object o = new Yaml().load(r); return o == null ? new LinkedHashMap<String, Object>() : (Map<String, Object>) o; }
        finally { r.close(); }
    }

    private static Map<String, Object> config(String file) throws IOException {
        Path p = configRoot.resolve(file);
        assertTrue("config missing: " + p, Files.isRegularFile(p));
        return yaml(p);
    }

    private static Map<String, Object> m(Object o) {
        return o instanceof Map ? (Map<String, Object>) o : Collections.<String, Object>emptyMap();
    }

    private static List<String> l(Object o) {
        List<String> out = new ArrayList<String>();
        if (o instanceof List) for (Object x : (List<Object>) o) out.add(String.valueOf(x));
        return out;
    }

    private static String str(Object o) { return o == null ? null : String.valueOf(o); }

    /** Every entry of sources / legacy / sinks, keyed "section.id". */
    private static Map<String, Map<String, Object>> entries() {
        Map<String, Map<String, Object>> out = new LinkedHashMap<String, Map<String, Object>>();
        for (String s : SECTIONS) for (Map.Entry<String, Object> e : m(map.get(s)).entrySet()) out.put(s + "." + e.getKey(), m(e.getValue()));
        return out;
    }

    /** Pattern "fc_<map>_coin" → normalized "fc_<>_coin". */
    private static String norm(String pattern) { return pattern.replaceAll("<[a-z0-9_]*>", "<>"); }

    private static Pattern regex(String normalized) {
        StringBuilder b = new StringBuilder();
        for (String part : normalized.split("<>", -1)) { if (b.length() > 0) b.append("[a-z0-9_]+"); b.append(Pattern.quote(part)); }
        return Pattern.compile(b.toString());
    }

    private static Object path(Map<String, Object> root, String dotted) {
        Object cur = root;
        String rest = dotted;
        while (rest.length() > 0) {
            Map<String, Object> mm = m(cur);
            if (mm.isEmpty()) return null;
            // longest key match first (economy keys contain dots)
            String hit = null;
            for (String k : mm.keySet()) if ((rest.equals(k) || rest.startsWith(k + ".")) && (hit == null || k.length() > hit.length())) hit = k;
            if (hit == null) return null;
            cur = mm.get(hit);
            rest = rest.length() == hit.length() ? "" : rest.substring(hit.length() + 1);
        }
        return cur;
    }

    // ------------------------------------------------------------------------------------------------ (a) economy rows

    @Test
    public void everyEconomyRowIsMapped() {
        Set<String> mapped = new TreeSet<String>();
        List<String> bad = new ArrayList<String>();
        for (Map.Entry<String, Map<String, Object>> e : entries().entrySet()) {
            String row = str(e.getValue().get("econ_row"));
            if (row == null) { if (!e.getKey().startsWith("sources.X")) bad.add(e.getKey() + ": econ_row null (only X* entries may be unregistered)"); continue; }
            if (EmberEconomy.byId(row) == null) bad.add(e.getKey() + ": econ_row " + row + " not in EmberEconomy");
            if (!mapped.add(row)) bad.add(e.getKey() + ": econ_row " + row + " mapped twice");
        }
        for (EmberEconomy.Row r : EmberEconomy.all()) if (!mapped.contains(r.id)) bad.add("EmberEconomy row " + r.id + " missing from ember-source-map.yml");
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    // ------------------------------------------------------------------------------------------------ (b) economy yml keys

    @Test
    public void everyEconomyYmlKeyIsMapped() throws IOException {
        Map<String, Object> eco = config("ember-v1-economy.yml");
        Set<String> live = new TreeSet<String>();
        for (String section : new String[] {"sources", "sinks"})
            for (Map.Entry<String, Object> row : m(eco.get(section)).entrySet())
                for (String k : m(row.getValue()).keySet()) live.add(row.getKey() + "." + k);
        assertTrue("economy yml has no keys", live.size() > 30);
        Set<String> listed = new TreeSet<String>();
        List<String> bad = new ArrayList<String>();
        for (Map.Entry<String, Map<String, Object>> e : entries().entrySet())
            for (String k : l(e.getValue().get("economy_keys"))) {
                if (!listed.add(k)) bad.add(e.getKey() + ": economy key " + k + " listed twice");
                String row = str(e.getValue().get("econ_row"));
                if (row != null && !k.startsWith(row + ".")) bad.add(e.getKey() + ": economy key " + k + " does not belong to row " + row);
            }
        for (String k : live) if (!listed.contains(k)) bad.add("ember-v1-economy.yml key " + k + " is not in any economy_keys");
        for (String k : listed) if (!live.contains(k)) bad.add("economy_keys " + k + " no longer exists in ember-v1-economy.yml");
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    // ------------------------------------------------------------------------------------------------ (c) code scan

    private static final String TOKEN = "(?:\"[a-z0-9_]*\"|[A-Za-z_][A-Za-z0-9_.]*)";
    private static final String CONCAT = "(" + TOKEN + "(?:\\s*\\+\\s*" + TOKEN + ")*)";
    private static final Pattern P_GRANT = Pattern.compile("Grant\\(\\s*" + CONCAT + "\\s*,");
    private static final Pattern P_LROW = Pattern.compile("ledgerRow\\(\\s*[^,]+?,\\s*([^,]+?),\\s*" + CONCAT + "\\s*,");
    private static final Pattern P_RECORD = Pattern.compile("\\.record\\(\\s*[^,]+?,\\s*" + CONCAT + "\\s*,");
    private static final Pattern P_GRANTROW_SUFFIX = Pattern.compile("grantRow\\(\\s*[^,]+,\\s*[^,]+,\\s*[a-z]+\\s*\\+\\s*\"([a-z0-9_]+)\"");
    private static final Pattern P_VAR_EVENT = Pattern.compile("\\bk = \"(var_event_[a-z0-9_]+)\"");
    private static final Pattern P_RUN_CONST = Pattern.compile("\\b(?:LEDGER_PREFIX|LEDGER_RUN|SIGN_RUN|ON_RUN)\\s*=\\s*\"([a-z0-9]+-?)\"");
    private static final Pattern P_RUN_AT = Pattern.compile("\"([a-z]+@)\"");
    private static final Pattern P_RUN_VAR = Pattern.compile("String\\s+run\\s*=\\s*\"([a-z0-9]+-)\"");

    private static final Pattern P_ITEM = Pattern.compile("\\b(?:item|raidItem)\\(\\s*[a-z]+\\s*,\\s*" + CONCAT + "\\s*[,)]");
    private static final Pattern P_DECL = Pattern.compile("\\bString\\s+([A-Za-z_][A-Za-z0-9_]*)\\s*=\\s*([^;]*);");
    private static final Pattern P_PURE = Pattern.compile(CONCAT);

    /** Initializer of the nearest declaration of {@code name} before {@code pos} (constants: anywhere), if it is a pure literal/identifier concat. */
    private static String decl(String text, String name, int pos) {
        boolean constant = name.equals(name.toUpperCase(java.util.Locale.ROOT));
        String best = null;
        Matcher d = P_DECL.matcher(text);
        while (d.find()) {
            if (!d.group(1).equals(name)) continue;
            if (d.start() >= pos && !constant) break;
            best = d.group(2).trim();
        }
        return best != null && P_PURE.matcher(best).matches() ? best : null;
    }

    /** Normalizes a Java concat expression of string literals and identifiers; identifiers resolve through simple String declarations. */
    private static String expr(String e, String text, int pos, int depth) {
        StringBuilder b = new StringBuilder();
        boolean lit = false;
        for (String t : e.split("\\s*\\+\\s*")) {
            t = t.trim();
            if (t.startsWith("\"")) { b.append(t.substring(1, t.length() - 1)); lit = true; continue; }
            String name = t.contains(".") ? t.substring(t.lastIndexOf('.') + 1) : t;
            String init = depth < 3 ? decl(text, name, pos) : null;
            String v = init == null ? null : expr(init, text, pos, depth + 1);
            if (v == null) b.append("<>"); else { b.append(v); lit = true; }
        }
        String out = b.toString().replaceAll("(<>)+", "<>");
        return lit ? out : null; // pure identifier expressions carry no key information
    }

    private static void put(Map<String, String> keys, String k, String file) { if (k != null && !keys.containsKey(k)) keys.put(k, file); }

    /** Scanned grant / ledger keys (normalized, "<>" = dynamic) → first file seen. Keys starting with "<>" are suffix-only. */
    static Map<String, String> scanKeys() {
        Map<String, String> keys = new TreeMap<String, String>();
        for (Map.Entry<String, String> f : SOURCES_TEXT.entrySet()) {
            String text = f.getValue();
            for (Pattern p : new Pattern[] {P_GRANT, P_RECORD, P_ITEM}) {
                Matcher mm = p.matcher(text);
                while (mm.find()) put(keys, expr(mm.group(1), text, mm.start(), 0), f.getKey());
            }
            Matcher lr = P_LROW.matcher(text);
            while (lr.find()) put(keys, expr(lr.group(2), text, lr.start(), 0), f.getKey());
            Matcher sx = P_GRANTROW_SUFFIX.matcher(text);
            while (sx.find()) put(keys, "<>" + sx.group(1), f.getKey());
            Matcher ve = P_VAR_EVENT.matcher(text);
            while (ve.find()) put(keys, ve.group(1), f.getKey());
            // first-clear choice → item: EmberRunRules.choiceItem derives the item key from the choice key
            if (text.contains(".replace(\"_choice\", \"_item\")"))
                for (String k : new ArrayList<String>(keys.keySet())) if (k.endsWith("_choice")) put(keys, k.substring(0, k.length() - 7) + "_item", f.getKey());
        }
        return keys;
    }

    /** Scanned ledger run ids / prefixes → first file seen. */
    static Map<String, String> scanRuns() {
        Map<String, String> runs = new TreeMap<String, String>();
        for (Map.Entry<String, String> f : SOURCES_TEXT.entrySet()) {
            String text = f.getValue();
            for (Pattern p : new Pattern[] {P_RUN_CONST, P_RUN_AT, P_RUN_VAR}) {
                Matcher mm = p.matcher(text);
                while (mm.find()) if (!runs.containsKey(mm.group(1))) runs.put(mm.group(1), f.getKey());
            }
            Matcher lr = P_LROW.matcher(text);
            while (lr.find()) {
                String a = lr.group(1).trim();
                if (a.startsWith("\"") && a.endsWith("\"")) { String r = a.substring(1, a.length() - 1); if (!runs.containsKey(r)) runs.put(r, f.getKey()); }
            }
        }
        return runs;
    }

    @Test
    public void scannerFindsKnownKeys() {
        Map<String, String> keys = scanKeys();
        for (String k : new String[] {"base_item", "extra_chest_item", "fc_<>_coin", "fc_<>_item", "rush_sig_<>", "vb_<>_shard",
                "stage<>", "starter_<>", "mark_item", "var_event_core", "cost", "refund_coin", "<>s"})
            assertTrue("scanner lost " + k + " (scanner regex drift?) found=" + keys.keySet(), keys.containsKey(k));
        Map<String, String> runs = scanRuns();
        for (String r : new String[] {"p1afk-", "p1sign-", "p1online-", "mark-", "failrefund@", "deathrefund@", "codex", "starter"})
            assertTrue("scanner lost run " + r + " found=" + runs.keySet(), runs.containsKey(r));
    }

    @Test
    public void everyCodeLedgerKeyIsMapped() {
        Map<String, String> scanned = scanKeys();
        Map<String, String> patterns = new TreeMap<String, String>(); // normalized → entry
        Set<String> runtime = new TreeSet<String>();
        for (Map.Entry<String, Map<String, Object>> e : entries().entrySet()) {
            List<String> ks = l(e.getValue().get("grant_keys"));
            ks.addAll(l(e.getValue().get("ledger_keys")));
            for (String k : ks) {
                patterns.put(norm(k), e.getKey());
                if (Boolean.TRUE.equals(e.getValue().get("runtime_keys"))) runtime.add(norm(k));
            }
        }
        List<String> bad = new ArrayList<String>();
        for (Map.Entry<String, String> s : scanned.entrySet()) {
            String k = s.getKey();
            boolean ok = false;
            for (String p : patterns.keySet()) {
                if (k.startsWith("<>")) { if (p.endsWith(k.substring(2)) && runtime.contains(p)) { ok = true; break; } }
                else if (p.equals(k) || regex(p).matcher(k.replace("<>", "x")).matches()) { ok = true; break; }
            }
            if (!ok) bad.add("code key " + k + " (" + s.getValue() + ") is not covered by any grant_keys / ledger_keys in ember-source-map.yml");
        }
        for (Map.Entry<String, String> p : patterns.entrySet()) {
            if (runtime.contains(p.getKey())) continue;
            boolean ok = scanned.containsKey(p.getKey());
            if (!ok) for (String k : scanned.keySet()) if (!k.startsWith("<>") && regex(p.getKey()).matcher(k.replace("<>", "x")).matches()) { ok = true; break; }
            if (!ok) bad.add(p.getValue() + ": key pattern " + p.getKey() + " matches nothing written by p1/*.java (stale? or set runtime_keys: true)");
        }
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    @Test
    public void everyLedgerRunIsMapped() {
        Map<String, String> scanned = scanRuns();
        Set<String> listed = new TreeSet<String>();
        for (Map.Entry<String, Map<String, Object>> e : entries().entrySet())
            for (String r : l(e.getValue().get("ledger_runs"))) listed.add(r.contains("<") ? r.substring(0, r.indexOf('<')) : r);
        List<String> bad = new ArrayList<String>();
        for (Map.Entry<String, String> r : scanned.entrySet())
            if (!listed.contains(r.getKey())) bad.add("ledger run " + r.getKey() + " (" + r.getValue() + ") not in any ledger_runs");
        for (String r : listed) if (!scanned.containsKey(r)) bad.add("ledger_runs " + r + " not found in p1/*.java");
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    // ------------------------------------------------------------------------------------------------ (d) content

    @Test
    public void contentMatchesConfigs() throws IOException {
        Map<String, Object> content = m(map.get("content"));
        Map<String, Object> runs = config("ember-v1-runs.yml");
        List<String> bad = new ArrayList<String>();
        Set<String> expected = new TreeSet<String>();
        for (String sec : new String[] {"maps", "raids", "rush"})
            for (Map.Entry<String, Object> e : m(runs.get(sec)).entrySet()) {
                expected.add(e.getKey());
                Map<String, Object> cfg = m(e.getValue()), c = m(content.get(e.getKey()));
                if (c.isEmpty()) { bad.add("ember-v1-runs.yml " + sec + "." + e.getKey() + " missing from content"); continue; }
                cmp(bad, e.getKey() + ".name", cfg.get("name"), c.get("name"));
                cmp(bad, e.getKey() + ".tier", cfg.get("tier"), c.get("tier"));
                if (!"rush".equals(sec)) {
                    cmp(bad, e.getKey() + ".boss", m(cfg.get("boss")).get("name"), c.get("boss"));
                    for (Map.Entry<String, Object> le : m(cfg.get("loot")).entrySet())
                        if (le.getValue() instanceof String) cmp(bad, e.getKey() + ".loot." + le.getKey(), le.getValue(), m(c.get("loot")).get(le.getKey()));
                    cmp(bad, e.getKey() + ".mode", "maps".equals(sec) ? "normal" : "raid", c.get("mode"));
                } else {
                    String mode = str(cfg.get("mode"));
                    cmp(bad, e.getKey() + ".mode", mode == null ? "rush" : mode, c.get("mode"));
                }
            }
        // challenge / abyss
        for (String k : new String[] {"challenge", "abyss"}) {
            expected.add(k);
            if (!runs.containsKey(k)) bad.add("ember-v1-runs.yml has no " + k);
            if (!content.containsKey(k)) bad.add("content " + k + " missing");
        }
        if (runs.containsKey("challenge")) cmp(bad, "challenge.tier", m(runs.get("challenge")).get("tier"), m(content.get("challenge")).get("tier"));
        // festival dungeon
        Map<String, Object> fest = m(config("ember-v1-festival.yml").get("dungeon"));
        String fk = str(fest.get("key"));
        expected.add(fk);
        Map<String, Object> fc = m(content.get(fk));
        if (fc.isEmpty()) bad.add("festival dungeon " + fk + " missing from content");
        else { cmp(bad, fk + ".name", fest.get("name"), fc.get("name")); cmp(bad, fk + ".tier", fest.get("tier"), fc.get("tier")); }
        // afk tiers
        Object tiers = m(config("ember-v1.yml").get("afk")).get("tiers");
        assertTrue("afk.tiers not a list", tiers instanceof List);
        for (Object t : (List<Object>) tiers) {
            Map<String, Object> tm = m(t);
            String k = "afk_t" + tm.get("n");
            expected.add(k);
            if (!content.containsKey(k)) bad.add("afk tier " + k + " missing from content");
            else cmp(bad, k + ".name", tm.get("name"), m(content.get(k)).get("name"));
        }
        expected.add("account");
        for (String k : content.keySet()) if (!expected.contains(k)) bad.add("content " + k + " has no live config counterpart");
        // cross-links
        Map<String, Object> sources = m(map.get("sources"));
        for (Map.Entry<String, Object> c : content.entrySet()) {
            for (String s : l(m(c.getValue()).get("sources"))) {
                if (!sources.containsKey(s)) bad.add("content " + c.getKey() + " lists unknown source " + s);
                else if (!l(m(sources.get(s)).get("content")).contains(c.getKey())) bad.add("content " + c.getKey() + " lists " + s + " but " + s + ".content lacks it");
            }
        }
        for (Map.Entry<String, Object> s : sources.entrySet())
            for (String c : l(m(s.getValue()).get("content"))) {
                if (!content.containsKey(c)) bad.add("source " + s.getKey() + " lists unknown content " + c);
                else if (!l(m(content.get(c)).get("sources")).contains(s.getKey())) bad.add("source " + s.getKey() + " lists content " + c + " but " + c + ".sources lacks it");
            }
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    private static void cmp(List<String> bad, String what, Object cfg, Object mapped) {
        if (cfg == null) return;
        if (!String.valueOf(cfg).equals(String.valueOf(mapped))) bad.add(what + ": config/code '" + cfg + "' vs source map '" + mapped + "'");
    }

    // ------------------------------------------------------------------------------------------------ (e) signatures

    @Test
    public void signaturesMatchCode() {
        Map<String, Object> sigs = m(map.get("signatures"));
        List<String> bad = new ArrayList<String>();
        assertEquals("signature count", EmberSignature.DEFS.size(), sigs.size());
        Map<String, Object> content = m(map.get("content"));
        for (EmberSignature.Def d : EmberSignature.DEFS) {
            Map<String, Object> s = m(sigs.get(d.id));
            if (s.isEmpty()) { bad.add("signature " + d.id + " missing"); continue; }
            cmp(bad, d.id + ".code", d.code, s.get("code"));
            cmp(bad, d.id + ".map", d.map, s.get("map"));
            cmp(bad, d.id + ".slot", d.slot, s.get("slot"));
            cmp(bad, d.id + ".family", d.family, s.get("family"));
            cmp(bad, d.id + ".name", d.name, s.get("name"));
            cmp(bad, d.id + ".boss", d.boss, s.get("boss"));
            cmp(bad, d.id + ".alt", EmberSignature.ALTS.containsKey(d.id), s.get("alt"));
            if (!l(m(content.get(d.map)).get("signatures")).contains(d.id)) bad.add("content " + d.map + ".signatures lacks " + d.id);
        }
        for (Map.Entry<String, Object> c : content.entrySet())
            for (String id : l(m(c.getValue()).get("signatures")))
                if (!c.getKey().equals(str(m(sigs.get(id)).get("map")))) bad.add("content " + c.getKey() + " lists signature " + id + " of another map");
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    // ------------------------------------------------------------------------------------------------ (f) affixes

    @Test
    public void itemAffixesMatchGrowthYml() throws IOException {
        Map<String, Object> aff = m(m(config("ember-v1-growth.yml").get("reroll")).get("affixes"));
        Map<String, Object> mapped = m(map.get("item_affixes"));
        List<String> bad = new ArrayList<String>();
        Set<String> seen = new TreeSet<String>();
        for (Map.Entry<String, Object> slot : aff.entrySet())
            for (Object o : (List<Object>) slot.getValue()) {
                Map<String, Object> a = m(o);
                String id = str(a.get("id"));
                seen.add(id);
                Map<String, Object> x = m(mapped.get(id));
                if (x.isEmpty()) { bad.add("affix " + id + " missing from item_affixes"); continue; }
                cmp(bad, id + ".code", a.get("code"), x.get("code"));
                cmp(bad, id + ".slot", slot.getKey(), x.get("slot"));
                cmp(bad, id + ".name", a.get("name"), x.get("name"));
                cmp(bad, id + ".rollable", a.containsKey("rollable") ? a.get("rollable") : Boolean.TRUE, x.get("rollable"));
            }
        for (String id : mapped.keySet()) if (!seen.contains(id)) bad.add("item_affixes " + id + " not in ember-v1-growth.yml reroll.affixes");
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    // ------------------------------------------------------------------------------------------------ (g, h) item sources, accounts, materials

    @Test
    public void itemSourcesAccountsAndMaterials() throws Exception {
        assertEquals("item_sources == EmberItemData.SOURCES", new TreeSet<String>(EmberItemData.SOURCES), new TreeSet<String>(m(map.get("item_sources")).keySet()));
        Map<String, Object> accounts = m(map.get("accounts"));
        List<String> bad = new ArrayList<String>();
        Set<String> econ = new TreeSet<String>(), ni = new TreeSet<String>();
        for (Map.Entry<String, Object> a : accounts.entrySet()) {
            String e = str(m(a.getValue()).get("econ"));
            try { EmberEconomy.Account.valueOf(e); econ.add(e); } catch (Exception ex) { bad.add("account " + a.getKey() + ": econ " + e + " is not an EmberEconomy.Account"); }
            if (m(a.getValue()).get("ni") != null) ni.add(str(m(a.getValue()).get("ni")));
        }
        for (EmberEconomy.Account a : EmberEconomy.Account.values()) if (!econ.contains(a.name())) bad.add("EmberEconomy.Account " + a + " has no accounts entry");
        for (Map.Entry<String, Map<String, Object>> e : entries().entrySet()) {
            List<String> ks = l(e.getValue().get("pays"));
            ks.addAll(l(e.getValue().get("takes")));
            for (String k : ks) if (!accounts.containsKey(k)) bad.add(e.getKey() + ": unknown account " + k);
        }
        for (String w : l(path(m(config("ember-v1.yml")), "storage.vault.whitelist")))
            if (!ni.contains(w)) bad.add("vault whitelist " + w + " has no accounts.ni");
        for (Field f : EmberUpgradeRules.class.getDeclaredFields())
            if (f.getName().startsWith("MAT_") && Modifier.isStatic(f.getModifiers()) && f.getType() == String.class) {
                f.setAccessible(true);
                String v = (String) f.get(null);
                if (!ni.contains(v)) bad.add("EmberUpgradeRules." + f.getName() + " = " + v + " has no accounts.ni");
            }
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    // ------------------------------------------------------------------------------------------------ (D245) item provenance

    @Test
    public void itemProvenanceMatchesCode() throws IOException {
        Map<String, Object> pv = m(map.get("item_provenance"));
        List<String> bad = new ArrayList<String>();
        EmberItemData d = new EmberItemData("0123456789abcdef0123456789abcdef", "ember_v1_scorch_blade_t2", "scorch", "blade", 2, 1, 2, 0, 0,
                false, "drop", EmberItemData.DATA_VERSION, 0, 0, 0, 0, 0, EmberItemData.Origin.of("q01", "S01", "q01-a-b", 1700000000L));
        Set<String> nbt = new TreeSet<String>(d.toMap().keySet());
        nbt.removeAll(d.withOrigin(EmberItemData.Origin.NONE).toMap().keySet());
        assertEquals("item_provenance.nbt == the keys EmberItemData.toMap adds for an origin", nbt, new TreeSet<String>(m(pv.get("nbt")).keySet()));
        assertEquals("packed shape", "q01|S01|q01-a-b|1700000000", d.origin.packed());
        assertEquals("packed field order", "map|src|run|at", str(pv.get("packed")));
        assertEquals(EmberProvenance.SRC_UNKNOWN, str(pv.get("unknown_source")));
        String col = str(pv.get("column"));
        String store = new String(Files.readAllBytes(codeRoot.resolve("p1/EmberItemStore.java")), StandardCharsets.UTF_8);
        if (!col.startsWith("cr_p1_item.origin ") || !store.contains("\"" + col.substring("cr_p1_item.origin ".length()) + "\""))
            bad.add("column " + col + " does not match the EmberItemStore schema");
        Map<String, Object> rules = m(pv.get("rules"));
        for (Map.Entry<String, Object> e : rules.entrySet()) {
            Map<String, Object> r = m(e.getValue());
            List<String> ex = l(r.get("example"));
            if (r.get("map") == null || r.get("src") == null) bad.add(e.getKey() + ": map / src missing");
            if (ex.isEmpty()) continue;
            EmberItemData.Origin o = EmberProvenance.forReward(ex.get(0), ex.get(1), 1700000000000L);
            cmp(bad, e.getKey() + ".map", r.get("map"), o.map);
            cmp(bad, e.getKey() + ".src", r.get("src"), o.src);
            if (!m(map.get("sources")).containsKey(o.src)) bad.add(e.getKey() + ": source " + o.src + " is not a sources entry");
        }
        EmberItemData.Origin f = EmberProvenance.forRedeem("rid", 0L), a = EmberProvenance.forAdmin("give", 0L);
        cmp(bad, "mark_redeem.map", m(rules.get("mark_redeem")).get("map"), f.map);
        cmp(bad, "mark_redeem.src", m(rules.get("mark_redeem")).get("src"), f.src);
        cmp(bad, "admin.map", m(rules.get("admin")).get("map"), a.map);
        cmp(bad, "admin.src", m(rules.get("admin")).get("src"), a.src);
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    // ------------------------------------------------------------------------------------------------ (i, j) refs and counters

    @Test
    public void codeAndConfigRefsResolve() throws IOException {
        List<String> bad = new ArrayList<String>();
        List<Map.Entry<String, Map<String, Object>>> all = new ArrayList<Map.Entry<String, Map<String, Object>>>(entries().entrySet());
        for (String s : new String[] {"content", "item_sources"})
            for (Map.Entry<String, Object> e : m(map.get(s)).entrySet())
                all.add(new java.util.AbstractMap.SimpleEntry<String, Map<String, Object>>(s + "." + e.getKey(), m(e.getValue())));
        all.add(new java.util.AbstractMap.SimpleEntry<String, Map<String, Object>>("item_provenance", m(map.get("item_provenance"))));
        for (Map.Entry<String, Object> e : m(m(map.get("item_provenance")).get("rules")).entrySet())
            all.add(new java.util.AbstractMap.SimpleEntry<String, Map<String, Object>>("item_provenance.rules." + e.getKey(), m(e.getValue())));
        Map<String, Map<String, Object>> cfgCache = new TreeMap<String, Map<String, Object>>();
        int n = 0;
        for (Map.Entry<String, Map<String, Object>> e : all) {
            for (String ref : l(e.getValue().get("code"))) {
                n++;
                int h = ref.indexOf('#');
                String file = h < 0 ? ref : ref.substring(0, h);
                Path p = codeRoot.resolve(file);
                if (!Files.isRegularFile(p)) { bad.add(e.getKey() + ": code file missing " + file); continue; }
                if (h >= 0 && !new String(Files.readAllBytes(p), StandardCharsets.UTF_8).contains(ref.substring(h + 1)))
                    bad.add(e.getKey() + ": code ref text not found: " + ref);
            }
            for (String ref : l(e.getValue().get("config"))) {
                n++;
                int h = ref.indexOf('#');
                String file = h < 0 ? ref : ref.substring(0, h);
                if (!Files.isRegularFile(configRoot.resolve(file))) { bad.add(e.getKey() + ": config file missing " + file); continue; }
                if (h < 0) continue;
                if (!cfgCache.containsKey(file)) cfgCache.put(file, config(file));
                if (path(cfgCache.get(file), ref.substring(h + 1)) == null) bad.add(e.getKey() + ": config key not found: " + ref);
            }
        }
        assertTrue("too few refs checked: " + n, n > 100);
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    @Test
    public void countersAreRegistered() {
        List<String> bad = new ArrayList<String>();
        for (Map.Entry<String, Map<String, Object>> e : entries().entrySet())
            for (String c : l(e.getValue().get("counters")))
                if (EmberCounters.byKey(c) == null && EmberCounters.lookup(c) == null) bad.add(e.getKey() + ": counter " + c + " not in EmberCounters");
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    @Test
    public void headerMatchesBalanceVersion() throws IOException {
        Object bv = path(config("ember-v1-runs.yml"), "balance_version");
        if (bv == null) bv = path(config("ember-v1.yml"), "balance_version");
        if (bv != null) assertEquals("source map balance_version (update the map with the balance change)", String.valueOf(bv), str(map.get("balance_version")));
        if (bv == null) fail("balance_version not found in ember-v1-runs.yml / ember-v1.yml");
    }

    /** D243 (G5): the bundled ember-v1*.yml resources are the live configs byte for byte (no silent first-start drift). */
    @Test
    public void bundledConfigsMatchLive() throws IOException {
        List<String> bad = new ArrayList<String>();
        for (String f : new String[] {"ember-v1.yml", "ember-v1-runs.yml", "ember-v1-growth.yml", "ember-v1-festival.yml", "ember-v1-economy.yml"}) {
            Path live = configRoot.resolve(f), res = Paths.get("src/main/resources", f);
            if (!Files.isRegularFile(res)) { bad.add("missing bundled " + res); continue; }
            if (!java.util.Arrays.equals(Files.readAllBytes(live), Files.readAllBytes(res)))
                bad.add("bundled " + res + " differs from live " + live + " — copy the live file into the resources in the same commit");
        }
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }

    /**
     * D244 (G10): item-level stocks — each stock lists exactly the EmberEconomy rows touching its account (sinks only in
     * rows_out); life.yml / CoreFish items, CoreGacha ticket grants and gacha item kinds are all accounted for.
     */
    @Test
    public void stocksMatchEconomyAndItemConfigs() throws IOException {
        List<String> bad = new ArrayList<String>();
        Map<String, Object> stocks = m(map.get("stocks"));
        Set<String> seen = new TreeSet<String>();
        for (Map.Entry<String, Object> e : stocks.entrySet()) {
            Map<String, Object> st = m(e.getValue());
            EmberEconomy.Account a = EmberEconomy.Account.valueOf(str(st.get("econ")));
            seen.add(a.name());
            Set<String> want = new TreeSet<String>(), have = new TreeSet<String>(l(st.get("rows_in")));
            for (EmberEconomy.Row r : EmberEconomy.touching(a)) want.add(r.id);
            for (String id : l(st.get("rows_out"))) {
                have.add(id);
                EmberEconomy.Row r = EmberEconomy.byId(id);
                if (r == null || !r.sink) bad.add("stocks." + e.getKey() + ".rows_out " + id + " is not a sink row");
            }
            if (!want.equals(have)) bad.add("stocks." + e.getKey() + ": rows " + have + " != EmberEconomy.touching(" + a + ") " + want);
        }
        for (String need : new String[]{"INSIGNIA", "BADGE", "LIFE_ITEM", "GACHA_TICKET", "COSMETIC"})
            if (!seen.contains(need)) bad.add("no stock for " + need);
        // life items: every item in life.yml (offers give / inputs, cook) and CoreFish tables, minus other accounts' ni
        Set<String> otherNi = new TreeSet<String>();
        for (Object a : m(map.get("accounts")).values()) if (m(a).get("ni") != null) otherNi.add(str(m(a).get("ni")));
        Set<String> items = new TreeSet<String>();
        Map<String, Object> life = config("life.yml");
        for (Object o : m(life.get("offers")).values()) {
            items.addAll(m(m(o).get("give")).keySet());
            items.addAll(m(m(o).get("inputs")).keySet());
        }
        Map<String, Object> cook = m(life.get("cook"));
        items.addAll(l(cook.get("inputs")));
        items.add(str(cook.get("output")));
        Map<String, Object> fish = config("../CoreFish/config.yml");
        for (Object t : m(fish.get("tables")).values())
            for (Object row : (List<?>) t) items.add(str(m(row).get("ni_id")));
        items.removeAll(otherNi);
        Set<String> listed = new TreeSet<String>(l(m(stocks.get("life_item")).get("items")));
        if (!items.equals(listed)) bad.add("stocks.life_item.items " + listed + " != life.yml + CoreFish items " + items);
        // gacha: ticket grants == CoreGacha config tickets keys; gacha items are cosmetics only
        Set<String> grants = new TreeSet<String>(m(config("../CoreGacha/config.yml").get("tickets")).keySet());
        Set<String> lg = new TreeSet<String>(l(m(stocks.get("gacha_ticket")).get("grants")));
        if (!grants.equals(lg)) bad.add("stocks.gacha_ticket.grants " + lg + " != CoreGacha tickets " + grants);
        Set<String> kinds = new TreeSet<String>(l(m(stocks.get("cosmetic")).get("gacha_kinds")));
        for (Map.Entry<String, Object> it : m(config("../CoreGacha/gacha.yml").get("items")).entrySet())
            if (!kinds.contains(str(m(it.getValue()).get("kind")))) bad.add("gacha item " + it.getKey() + " kind " + m(it.getValue()).get("kind") + " is not a cosmetic kind");
        assertTrue(String.join("\n", bad), bad.isEmpty());
    }
}

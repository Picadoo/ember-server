package town.sunshine.corerpg.p1;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Trusted item identity (策划书 §4.1 / §20.3 ItemInstance; source table G07): item_uid, NI id, family, slot,
 * tier, quality, craft, enhance, pity, bound, source, data version, revision. Stored as the NBT compound
 * {@value #NBT_KEY} on the item and mirrored in MySQL {@code cr_p1_item}. Display name and lore are never
 * identity. Immutable value object + pure codec/validation (no Bukkit), unit-tested offline.
 *
 * <p>D208 (ARCH S1-4 · REG §4-4): data version 2 also carries the four item keys that used to live in the owner's
 * PlayerData counters — affix ({@code code*10+tier}, was {@code p4_af_<uid>}), reroll pity ({@code p4_afp_}), signature
 * code ({@code p1_sig_}) and the committed reroll sequence ({@code p4_rrn_}). They are part of {@link #canonical()} (HMAC)
 * and of the {@code cr_p1_item} row. A version 1 item still verifies with the old canonical shape and carries none of
 * them (its values are read from the legacy counters until its next durable write folds them in as version 2).</p>
 *
 * <p>D245 (ARCH S4 · item provenance): a version 2 item may also carry its {@link Origin} — map / mode id, REG source row
 * and run id + time of the reward that created it (NBT {@code om / os / or / ot}, column {@code cr_p1_item.origin}). It is
 * optional: an item without it (every item made before 1.65.70, OP test items made by older builds) keeps exactly its old
 * canonical shape and signature. When present it is appended to {@link #canonical()} (HMAC), never changes after creation,
 * and every copy ({@link #withRev}, {@link #withItemKeys}, forge {@code EmberUpgradeRules.copy}) keeps it. Not shown in lore.</p>
 */
public final class EmberItemData {

    public static final String NBT_KEY = "ember_v1";
    public static final int DATA_VERSION = 2;
    /** D208: the pre-item-keys shape (no affix / pity / signature / reroll sequence on the item) */
    public static final int V1 = 1;
    public static final int MAX_AFFIX = 9999, MAX_AF_PITY = 999, MAX_SIG = 999;
    public static final int MAX_PITY = 12; // §6.1 largest 本档最多尝试 (+10: 12)

    public static final Set<String> FAMILIES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("scorch", "burst", "sustain", "none")));
    public static final Set<String> SLOTS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("blade", "charm")));
    public static final Set<String> SOURCES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("drop", "quest", "admin", "reissue", "migrate")));

    private static final Pattern UID = Pattern.compile("[0-9a-f]{32}");

    public final String uid;
    public final String ni;
    public final String family;
    public final String slot;
    public final int tier;
    public final int quality;
    public final int craft;
    public final int enhance;
    public final int pity;
    public final boolean bound;
    public final String source;
    public final int version;
    public final int rev;
    /** D208 (v2 only; always 0 on v1): affix code*10+tier (0 = empty slot) */
    public final int affix;
    /** D208: reroll tries in a row below the quality cap (affix pity) */
    public final int afPity;
    /** D208: signature legend {@code EmberSignature.Def.code} (0 = none) */
    public final int sigCode;
    /** D208: sequence n of the last committed paid reroll (request {@code afx:<uid>:<n>:…}) */
    public final int rerollN;
    /** D245: where the piece came from ({@link Origin#NONE} = not recorded: older items) */
    public final Origin origin;

    public EmberItemData(String uid, String ni, String family, String slot, int tier, int quality, int craft,
                         int enhance, int pity, boolean bound, String source, int version, int rev) {
        this(uid, ni, family, slot, tier, quality, craft, enhance, pity, bound, source, version, rev, 0, 0, 0, 0);
    }

    public EmberItemData(String uid, String ni, String family, String slot, int tier, int quality, int craft,
                         int enhance, int pity, boolean bound, String source, int version, int rev,
                         int affix, int afPity, int sigCode, int rerollN) {
        this(uid, ni, family, slot, tier, quality, craft, enhance, pity, bound, source, version, rev, affix, afPity, sigCode, rerollN, Origin.NONE);
    }

    /** D245: with provenance */
    public EmberItemData(String uid, String ni, String family, String slot, int tier, int quality, int craft,
                         int enhance, int pity, boolean bound, String source, int version, int rev,
                         int affix, int afPity, int sigCode, int rerollN, Origin origin) {
        this.origin = origin == null ? Origin.NONE : origin;
        this.uid = uid; this.ni = ni; this.family = family; this.slot = slot; this.tier = tier;
        this.quality = quality; this.craft = craft; this.enhance = enhance; this.pity = pity;
        this.bound = bound; this.source = source; this.version = version; this.rev = rev;
        this.affix = affix; this.afPity = afPity; this.sigCode = sigCode; this.rerollN = rerollN;
    }

    /** D208: the item keys live on the item (v2); false = read them from the legacy PlayerData counters */
    public boolean itemKeys() { return version >= 2; }

    /** New item with a fresh server-generated uid, the template id derived from family/slot/tier, rev 0. */
    public static EmberItemData create(String family, String slot, int tier, int quality, int craft, int enhance,
                                       boolean bound, String source) {
        String fam = tier == 0 ? "none" : family;
        return new EmberItemData(newUid(), templateId(fam, slot, tier), fam, slot, tier, quality, craft, enhance, 0,
                bound, source, DATA_VERSION, 0);
    }

    public static String newUid() { return UUID.randomUUID().toString().replace("-", ""); }

    /** §5.4 naming: ember_v1_{family}_{slot}_t{tier}; the two T0 templates are ember_v1_t0_{slot}. */
    public static String templateId(String family, String slot, int tier) {
        if (tier == 0) return "ember_v1_t0_" + slot;
        return "ember_v1_" + family + "_" + slot + "_t" + tier;
    }

    public boolean isBlade() { return "blade".equals(slot); }
    public boolean isCharm() { return "charm".equals(slot); }

    /** @return null when valid, else the first reason it is not trustworthy */
    public String validate() {
        if (uid == null || !UID.matcher(uid).matches()) return "bad uid";
        if (version != V1 && version != DATA_VERSION) return "unsupported version " + version;
        if (version == V1 && (affix != 0 || afPity != 0 || sigCode != 0 || rerollN != 0)) return "v1 carries item keys";
        if (affix < 0 || affix > MAX_AFFIX) return "bad affix " + affix;
        if (afPity < 0 || afPity > MAX_AF_PITY) return "bad affix pity " + afPity;
        if (sigCode < 0 || sigCode > MAX_SIG) return "bad signature " + sigCode;
        if (rerollN < 0) return "bad reroll sequence " + rerollN;
        if (version == V1 && origin.present()) return "v1 carries origin";
        String ob = origin.validate();
        if (ob != null) return ob;
        if (!SLOTS.contains(slot)) return "bad slot " + slot;
        if (!FAMILIES.contains(family)) return "bad family " + family;
        if (tier < 0 || tier > EmberTables.MAX_TIER) return "bad tier " + tier;
        if ((tier == 0) != "none".equals(family)) return "family/tier mismatch";
        if (quality < 0 || quality > EmberTables.MAX_QUALITY) return "bad quality " + quality;
        if (craft < 0 || craft > EmberTables.MAX_CRAFT) return "bad craft " + craft;
        if (enhance < 0 || enhance > EmberTables.MAX_ENHANCE) return "bad enhance " + enhance;
        if (pity < 0 || pity > MAX_PITY) return "bad pity " + pity;
        if (!SOURCES.contains(source)) return "bad source " + source;
        if (rev < 0) return "bad rev";
        if (ni == null || !ni.equals(templateId(family, slot, tier))) return "ni id " + ni + " != template";
        return null;
    }

    /**
     * Stable field order used for the signature; changing it invalidates every signed item. Version 1 keeps its
     * original shape exactly (so every v1 item ever signed still verifies); version 2 appends the item keys.
     */
    public String canonical() {
        String base = "v" + version + "|" + uid + "|" + ni + "|" + family + "|" + slot + "|" + tier + "|" + quality + "|"
                + craft + "|" + enhance + "|" + pity + "|" + (bound ? 1 : 0) + "|" + source + "|" + rev;
        if (version < 2) return base;
        String v2 = base + "|" + affix + "|" + afPity + "|" + sigCode + "|" + rerollN;
        return origin.present() ? v2 + "|o:" + origin.packed() : v2; // D245: items without provenance keep their old shape
    }

    /** HMAC-SHA256 over {@link #canonical()}, first 128 bits as lowercase hex. */
    public String sign(byte[] key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            byte[] h = mac.doFinal(canonical().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (int i = 0; i < 16; i++) sb.append(String.format(Locale.ROOT, "%02x", h[i] & 0xff));
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("HmacSHA256 unavailable", e);
        }
    }

    /** Constant-time compare. */
    public boolean verify(byte[] key, String sig) {
        if (sig == null || sig.length() != 32) return false;
        String want = sign(key);
        int diff = 0;
        for (int i = 0; i < 32; i++) diff |= want.charAt(i) ^ sig.charAt(i);
        return diff == 0;
    }

    // ------------------------------------------------------------------ codec (NBT ↔ map)

    /** Short NBT keys; values are String or Integer (bytes/shorts widened). Signature added by the caller. */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("uid", uid);
        m.put("ni", ni);
        m.put("fam", family);
        m.put("slot", slot);
        m.put("tier", tier);
        m.put("q", quality);
        m.put("craft", craft);
        m.put("enh", enhance);
        m.put("pity", pity);
        m.put("bound", bound ? 1 : 0);
        m.put("src", source);
        m.put("ver", version);
        m.put("rev", rev);
        if (version >= 2) { // D208: v1 NBT stays byte-identical
            m.put("af", affix);
            m.put("afp", afPity);
            m.put("sigc", sigCode);
            m.put("rrn", rerollN);
            if (origin.present()) { // D245
                m.put("om", origin.map);
                m.put("os", origin.src);
                m.put("or", origin.run);
                m.put("ot", (int) origin.at);
            }
        }
        return m;
    }

    /** Reverse of {@link #toMap()}; missing/garbled fields produce values that fail {@link #validate()}. */
    public static EmberItemData fromMap(Map<String, ?> m) {
        if (m == null) return null;
        return new EmberItemData(str(m, "uid"), str(m, "ni"), str(m, "fam"), str(m, "slot"), num(m, "tier"),
                num(m, "q"), num(m, "craft"), num(m, "enh"), num(m, "pity"), num(m, "bound") != 0, str(m, "src"),
                num(m, "ver"), num(m, "rev"), opt(m, "af"), opt(m, "afp"), opt(m, "sigc"), opt(m, "rrn"), originOf(m));
    }

    /** D245: none of om / os / or / ot → {@link Origin#NONE}; otherwise read as stored (garbled values fail validate) */
    private static Origin originOf(Map<String, ?> m) {
        if (!m.containsKey("om") && !m.containsKey("os") && !m.containsKey("or") && !m.containsKey("ot")) return Origin.NONE;
        String om = str(m, "om"), os = str(m, "os"), or = str(m, "or");
        return new Origin(om == null ? "" : om, os == null ? "" : os, or == null ? "" : or, m.containsKey("ot") ? num(m, "ot") : -1);
    }

    /** D208: an item key absent from the compound (every v1 item) is 0; garbled = -1 (fails validate) */
    private static int opt(Map<String, ?> m, String k) { return m.containsKey(k) ? num(m, k) : 0; }

    private static String str(Map<String, ?> m, String k) {
        Object o = m.get(k);
        return o == null ? null : String.valueOf(o);
    }

    private static int num(Map<String, ?> m, String k) {
        Object o = m.get(k);
        if (o instanceof Number) return ((Number) o).intValue();
        if (o == null) return -1;
        try { return Integer.parseInt(String.valueOf(o).trim()); } catch (NumberFormatException e) { return -1; }
    }

    /** same data and version (the gear library moves rows without touching the item keys) */
    public EmberItemData withRev(int newRev) {
        return new EmberItemData(uid, ni, family, slot, tier, quality, craft, enhance, pity, bound, source, version, newRev,
                affix, afPity, sigCode, rerollN, origin);
    }

    /** D208: same piece as version 2 with these item keys (rev unchanged; the caller bumps it in the transaction) */
    public EmberItemData withItemKeys(int newAffix, int newAfPity, int newSig, int newRerollN) {
        return new EmberItemData(uid, ni, family, slot, tier, quality, craft, enhance, pity, bound, source, DATA_VERSION, rev,
                newAffix, newAfPity, newSig, newRerollN, origin);
    }

    /** D245: the same piece (version 2) with this provenance — only for a piece being created (never re-stamps a known one) */
    public EmberItemData withOrigin(Origin o) {
        return new EmberItemData(uid, ni, family, slot, tier, quality, craft, enhance, pity, bound, source, DATA_VERSION, rev,
                affix, afPity, sigCode, rerollN, o);
    }

    /**
     * D245 (ARCH S4 · item provenance): which content / reward created a piece. {@code map} = map or mode id (q01…q07,
     * q03c challenge, q05a2 abyss floor, r01 raid, starter, forge, admin…), {@code src} = REG source row (S01, S06, S12,
     * S13, S28, S34, S35…; X03 = OP test item, X00 = not registered), {@code run} = run id / request id, {@code at} = epoch
     * seconds of the reward. Immutable; {@link #NONE} = not recorded.
     */
    public static final class Origin {
        public static final Origin NONE = new Origin("", "", "", 0L);
        private static final Pattern MAP = Pattern.compile("[a-z0-9_]{1,24}");
        private static final Pattern SRC = Pattern.compile("[SX]\\d{2}");
        private static final Pattern RUN = Pattern.compile("[A-Za-z0-9_@:.\\-]{0,48}");
        public final String map, src, run;
        public final long at;

        Origin(String map, String src, String run, long at) { this.map = map; this.src = src; this.run = run; this.at = at; }

        /** Normalised: map lower-case [a-z0-9_] ≤ 24 (empty → "unknown"), src as given (S## / X##, else X00), run ≤ 48 safe chars. */
        public static Origin of(String map, String src, String run, long atSec) {
            String m = map == null ? "" : map.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_]", "");
            if (m.length() > 24) m = m.substring(0, 24);
            if (m.isEmpty()) m = "unknown";
            String s = src != null && SRC.matcher(src).matches() ? src : "X00";
            String r = run == null ? "" : run.replaceAll("[^A-Za-z0-9_@:.\\-]", "");
            if (r.length() > 48) r = r.substring(0, 48);
            long t = Math.max(0L, Math.min(Integer.MAX_VALUE, atSec));
            return new Origin(m, s, r, t);
        }

        public boolean present() { return !(map.isEmpty() && src.isEmpty() && run.isEmpty() && at == 0L); }

        /** {@code map|src|run|at}; "" when absent (the DB column value) */
        public String packed() { return present() ? map + "|" + src + "|" + run + "|" + at : ""; }

        /** Reverse of {@link #packed()}; "" / null → NONE; garbled → an origin that fails {@link #validate()} */
        public static Origin parse(String packed) {
            if (packed == null || packed.isEmpty()) return NONE;
            String[] p = packed.split("\\|", -1);
            if (p.length != 4) return new Origin("?", "?", "", -1L);
            long t;
            try { t = Long.parseLong(p[3]); } catch (NumberFormatException e) { t = -1L; }
            return new Origin(p[0], p[1], p[2], t);
        }

        /** null when absent or well-formed */
        public String validate() {
            if (!present()) return null;
            if (map == null || !MAP.matcher(map).matches()) return "bad origin map " + map;
            if (src == null || !SRC.matcher(src).matches()) return "bad origin source " + src;
            if (run == null || !RUN.matcher(run).matches()) return "bad origin run " + run;
            if (at < 0 || at > Integer.MAX_VALUE) return "bad origin time " + at;
            return null;
        }

        @Override public String toString() { return present() ? packed() : "-"; }
        @Override public boolean equals(Object o) { return o instanceof Origin && packed().equals(((Origin) o).packed()); }
        @Override public int hashCode() { return packed().hashCode(); }
    }

    public EmberItemData withAffix(int newAffix) { return withItemKeys(newAffix, afPity, sigCode, rerollN); }
    public EmberItemData withSig(int newSig) { return withItemKeys(affix, afPity, newSig, rerollN); }

    // ------------------------------------------------------------------ display helpers

    /** D98: the one shared one-line description of each set family (chat buttons, help page and gear page say the same) */
    public static String familyBlurb(String fam) {
        if ("scorch".equals(fam)) return "焚烬：每 3 次普攻点燃敌人（持续伤害），适合打首领";
        if ("burst".equals(fam)) return "烬爆：每 5 次普攻炸开一圈（3 格范围伤害），适合清一群";
        if ("sustain".equals(fam)) return "炽愈：生命 ×1.12，每 5 次普攻回最大生命 2.5%（觉醒后最多 4%，6 秒一次），最稳";
        return "";
    }

    public static String familyName(String fam) {
        if ("scorch".equals(fam)) return "焚烬";
        if ("burst".equals(fam)) return "烬爆";
        if ("sustain".equals(fam)) return "炽愈";
        return "无族";
    }

    public static String slotName(String slot) { return "blade".equals(slot) ? "刃" : ("charm".equals(slot) ? "护符" : slot); }

    public static String qualityName(int q) {
        switch (q) {
            case 1: return "精良";
            case 2: return "卓越";
            case 3: return "极品";
            default: return "标准";
        }
    }

    public String shortLabel() {
        // D101 (midgame #4): one name per property — the forge calls them 成色 / 精工, so the item does too
        return "T" + tier + " " + familyName(family) + slotName(slot) + "｜成色" + qualityName(quality) + "｜精工"
                + (craft * 2) + "%｜+" + enhance;
    }

    @Override public String toString() { return canonical(); }

    @Override public boolean equals(Object o) {
        return o instanceof EmberItemData && canonical().equals(((EmberItemData) o).canonical());
    }

    @Override public int hashCode() { return canonical().hashCode(); }
}

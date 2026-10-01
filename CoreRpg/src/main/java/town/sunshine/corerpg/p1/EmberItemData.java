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
 */
public final class EmberItemData {

    public static final String NBT_KEY = "ember_v1";
    public static final int DATA_VERSION = 1;
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

    public EmberItemData(String uid, String ni, String family, String slot, int tier, int quality, int craft,
                         int enhance, int pity, boolean bound, String source, int version, int rev) {
        this.uid = uid; this.ni = ni; this.family = family; this.slot = slot; this.tier = tier;
        this.quality = quality; this.craft = craft; this.enhance = enhance; this.pity = pity;
        this.bound = bound; this.source = source; this.version = version; this.rev = rev;
    }

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
        if (version != DATA_VERSION) return "unsupported version " + version;
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

    /** Stable field order used for the signature; changing it invalidates every signed item. */
    public String canonical() {
        return "v" + version + "|" + uid + "|" + ni + "|" + family + "|" + slot + "|" + tier + "|" + quality + "|"
                + craft + "|" + enhance + "|" + pity + "|" + (bound ? 1 : 0) + "|" + source + "|" + rev;
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
        return m;
    }

    /** Reverse of {@link #toMap()}; missing/garbled fields produce values that fail {@link #validate()}. */
    public static EmberItemData fromMap(Map<String, ?> m) {
        if (m == null) return null;
        return new EmberItemData(str(m, "uid"), str(m, "ni"), str(m, "fam"), str(m, "slot"), num(m, "tier"),
                num(m, "q"), num(m, "craft"), num(m, "enh"), num(m, "pity"), num(m, "bound") != 0, str(m, "src"),
                num(m, "ver"), num(m, "rev"));
    }

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

    public EmberItemData withRev(int newRev) {
        return new EmberItemData(uid, ni, family, slot, tier, quality, craft, enhance, pity, bound, source, version, newRev);
    }

    // ------------------------------------------------------------------ display helpers

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
        return "T" + tier + " " + familyName(family) + slotName(slot) + "｜" + qualityName(quality) + "｜"
                + (isBlade() ? "锋刃" : "护心") + (craft * 2) + "%｜+" + enhance;
    }

    @Override public String toString() { return canonical(); }

    @Override public boolean equals(Object o) {
        return o instanceof EmberItemData && canonical().equals(((EmberItemData) o).canonical());
    }

    @Override public int hashCode() { return canonical().hashCode(); }
}

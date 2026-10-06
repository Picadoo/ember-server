package town.sunshine.corerpg.p1.encounter;

import town.sunshine.corerpg.p1.EmberRunMaps;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * D241 / ARCH S3-12: registry of the 12 affix primitives, in {@link EmberRunMaps.Variety#KNOWN} order. Unknown id → null
 * (the Director then keeps its old fallbacks: blazing cadence, END_ROD glow, the raw id as intro).
 */
public final class EmberAffixes {

    private EmberAffixes() { }

    private static final Map<String, AffixBehavior> ALL;
    static {
        Map<String, AffixBehavior> m = new LinkedHashMap<String, AffixBehavior>();
        for (AffixBehavior b : new AffixBehavior[]{AffixBlazing.INSTANCE, AffixSplit.INSTANCE, AffixShield.INSTANCE,
                AffixRegen.INSTANCE, AffixCharge.INSTANCE, AffixFrost.INSTANCE, AffixMortar.INSTANCE, AffixMolten.INSTANCE,
                AffixVenom.INSTANCE, AffixJailer.INSTANCE, AffixArcane.INSTANCE, AffixFirechain.INSTANCE}) m.put(b.id(), b);
        ALL = Collections.unmodifiableMap(m);
    }

    /** primitive for a yml id, or null */
    public static AffixBehavior of(String id) { return id == null ? null : ALL.get(id); }

    /** id → primitive, KNOWN order */
    public static Map<String, AffixBehavior> all() { return ALL; }

    /** cadence used at promotion (unknown id → blazing cadence, as before) */
    public static double firstEvery(String id, EmberRunMaps.Variety v) {
        AffixBehavior b = of(id);
        return b == null ? v.blazeEvery : b.firstEvery(v);
    }

    /** intro line (unknown id → the id itself, as before) */
    public static String how(String id, EmberRunMaps.Variety v) {
        AffixBehavior b = of(id);
        return b == null ? id : b.how(v);
    }

    /** idle glow particle name (unknown id → END_ROD, as before) */
    public static String fx(String id) {
        AffixBehavior b = of(id);
        return b == null ? "END_ROD" : b.fx();
    }
}

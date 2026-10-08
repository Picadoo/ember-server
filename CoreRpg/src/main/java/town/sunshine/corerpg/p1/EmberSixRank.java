package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * D318 六槽 T1-8 · armor page ranking (spec §2.3): for each slot the backpack candidate with the largest whole-loadout
 * H change, computed by the real formula ({@link EmberLoadout#compute} six-slot overload, the same code T1-2 checks
 * against p1sim). Ties: same family as the piece worn in that slot, then the charm's family, then higher drop tier,
 * then uid (stable). Pure; the Bukkit side ({@link EmberSixSlotService}) only collects the verified pieces.
 */
public final class EmberSixRank {

    private EmberSixRank() {}

    public static final class Pick {
        public final EmberItemData piece;
        /** whole-loadout H with this piece in its slot minus H now */
        public final double delta;
        Pick(EmberItemData piece, double delta) { this.piece = piece; this.delta = delta; }
    }

    public static final class View {
        public final EmberTables t;
        public final EmberItemData blade, charm;
        public final int level;
        public final double festHp, festDef;
        public final EmberItemData[] worn;
        public final Pick[] best = new Pick[4];
        public final int[] count = new int[4];
        public final double h;
        View(EmberTables t, EmberItemData blade, EmberItemData charm, int level, double festHp, double festDef, EmberItemData[] worn, double h) {
            this.t = t; this.blade = blade; this.charm = charm; this.level = level; this.festHp = festHp; this.festDef = festDef;
            this.worn = worn; this.h = h;
        }
        public double hWith(EmberItemData[] armor) {
            return EmberLoadout.compute(t, blade, charm, level, festHp, festDef, armor).h;
        }
    }

    public static View view(EmberTables t, EmberItemData blade, EmberItemData charm, int level, double festHp, double festDef,
                            EmberItemData[] worn, List<EmberItemData> cands) {
        final EmberItemData[] w = worn == null ? new EmberItemData[4] : worn.clone();
        double h = EmberLoadout.compute(t, blade, charm, level, festHp, festDef, w).h;
        final View v = new View(t, blade, charm, level, festHp, festDef, w, h);
        Map<String, Integer> seen = new HashMap<String, Integer>();
        for (EmberItemData d : cands) seen.put(d.uid, seen.containsKey(d.uid) ? seen.get(d.uid) + 1 : 1);
        for (EmberItemData d : w) if (d != null) seen.put(d.uid, seen.containsKey(d.uid) ? seen.get(d.uid) + 1 : 1);
        for (int i = 0; i < 4; i++) {
            final int slot = i;
            List<Pick> ps = new ArrayList<Pick>();
            for (EmberItemData d : cands) {
                if (d == null || !d.isArmor() || EmberItemData.armorIndex(d.slot) != i || seen.get(d.uid) > 1) continue;
                if (blade != null && d.uid.equals(blade.uid) || charm != null && d.uid.equals(charm.uid)) continue;
                EmberItemData[] a = w.clone();
                a[i] = d;
                ps.add(new Pick(d, v.hWith(a) - h));
                v.count[i]++;
            }
            if (ps.isEmpty()) continue;
            Collections.sort(ps, new Comparator<Pick>() {
                @Override public int compare(Pick x, Pick y) { return order(x, y, w[slot], v.charm); }
            });
            v.best[i] = ps.get(0);
        }
        return v;
    }

    /** negative = x first */
    static int order(Pick x, Pick y, EmberItemData worn, EmberItemData charm) {
        int c = Double.compare(y.delta, x.delta);
        if (c != 0) return c;
        if (worn != null) {
            c = Boolean.compare(worn.family.equals(y.piece.family), worn.family.equals(x.piece.family));
            if (c != 0) return c;
        }
        if (charm != null) {
            c = Boolean.compare(charm.family.equals(y.piece.family), charm.family.equals(x.piece.family));
            if (c != 0) return c;
        }
        c = Integer.compare(y.piece.tier, x.piece.tier);
        if (c != 0) return c;
        return x.piece.uid.compareTo(y.piece.uid);
    }

    /** 「全部换上」: slots whose best candidate raises H, or fills an empty slot (ties keep the current piece and family) */
    public static List<Integer> allPlan(View v) {
        List<Integer> out = new ArrayList<Integer>();
        for (int i = 0; i < 4; i++) {
            Pick p = v.best[i];
            if (p == null) continue;
            if (v.worn[i] == null || p.delta > 0) out.add(i);
        }
        return out;
    }

    public static double allDelta(View v, List<Integer> slots) {
        EmberItemData[] a = v.worn.clone();
        for (int i : slots) a[i] = v.best[i].piece;
        return v.hWith(a) - v.h;
    }

    /** "生命 +0.5" / "生命 不变" / "生命 -0.3" (one decimal; tiny non-zero shows as ±<0.1) */
    public static String deltaText(double d) {
        if (d == 0.0) return "生命 不变";
        if (Math.abs(d) < 0.05) return d > 0 ? "生命 +<0.1" : "生命 -<0.1";
        return String.format(Locale.ROOT, d > 0 ? "生命 +%.1f" : "生命 %.1f", d);
    }

    /** 四件套 progress (Stage 2): the family with most worn pieces → {family, count, minTier}; null when nothing worn */
    public static Object[] setProgress(EmberItemData[] worn) {
        Map<String, int[]> m = new LinkedHashMap<String, int[]>();
        for (EmberItemData d : worn) {
            if (d == null || d.tier < 1 || !EmberRunRules.validFamily(d.family)) continue;
            int[] c = m.get(d.family);
            if (c == null) m.put(d.family, c = new int[]{0, Integer.MAX_VALUE});
            c[0]++;
            c[1] = Math.min(c[1], d.tier);
        }
        String best = null;
        for (Map.Entry<String, int[]> e : m.entrySet()) if (best == null || e.getValue()[0] > m.get(best)[0]) best = e.getKey();
        return best == null ? null : new Object[]{best, m.get(best)[0], m.get(best)[1]};
    }
}

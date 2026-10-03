package town.sunshine.coregacha.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A banner: own weighted items plus (standard banners) the retired items of ended limited banners. */
public final class Banner {
    public final String id, name, type, pityGroup, retireTo, icon, desc;
    public final long start, end; // epoch ms; 0 = open / never ends
    public final int retireWeight;
    public final Map<String, Double> weights; // own items (insertion order kept)

    public Banner(String id, String name, String type, String pityGroup, long start, long end, String retireTo, int retireWeight,
                  Map<String, Double> weights, String icon, String desc) {
        this.id = id; this.name = name; this.type = type; this.pityGroup = pityGroup; this.start = start; this.end = end;
        this.retireTo = retireTo; this.retireWeight = retireWeight; this.weights = Collections.unmodifiableMap(new LinkedHashMap<String, Double>(weights));
        this.icon = icon; this.desc = desc;
    }

    public boolean limited() { return "limited".equals(type); }

    public boolean open(long now) { return (start <= 0 || now >= start) && (end <= 0 || now < end); }

    public boolean ended(long now) { return end > 0 && now >= end; }

    /** The pool at {@code now}: tier → (item → weight). */
    public static final class Pool {
        public final Map<Tier, LinkedHashMap<Item, Double>> byTier = new EnumMap<Tier, LinkedHashMap<Item, Double>>(Tier.class);
        public List<Item> all() {
            List<Item> l = new ArrayList<Item>();
            for (LinkedHashMap<Item, Double> m : byTier.values()) l.addAll(m.keySet());
            return l;
        }
        public LinkedHashMap<Item, Double> tier(Tier t) {
            LinkedHashMap<Item, Double> m = byTier.get(t);
            return m == null ? new LinkedHashMap<Item, Double>() : m;
        }
        public boolean contains(String id) { for (Item i : all()) if (i.id.equals(id)) return true; return false; }
    }
}

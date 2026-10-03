package town.sunshine.coregacha;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** In-memory mirror of one online player's gacha rows (menus / PAPI read this; only DB results write it). */
final class PCache {
    static final class BState { int spark, pulls; String lastLegend; BState(int s, int p, String l) { spark = s; pulls = p; lastLegend = l; } }

    static final class Hist {
        final long id, at; final String banner, tier, item; final boolean dup; final int shards; final String rule;
        Hist(long id, long at, String banner, String tier, String item, boolean dup, int shards, String rule) {
            this.id = id; this.at = at; this.banner = banner; this.tier = tier; this.item = item; this.dup = dup; this.shards = shards; this.rule = rule;
        }
    }

    final String name;
    volatile int tickets, shards;
    volatile boolean welcomed;
    final Map<String, int[]> pity = new ConcurrentHashMap<String, int[]>();     // group → {pity5, pity4}
    final Map<String, BState> banners = new ConcurrentHashMap<String, BState>();
    final Set<String> owned = ConcurrentHashMap.newKeySet();                    // gacha_owned rows (native + CoreRpg ones won here)
    final Map<String, String> wear = new ConcurrentHashMap<String, String>();    // kind → item
    volatile List<Hist> hist = Collections.emptyList();                          // newest first, ≤ 100
    final Map<String, Integer> daily = new ConcurrentHashMap<String, Integer>(); // today's keys
    volatile String day;
    volatile List<Hist> reveal = Collections.emptyList();                        // last batch, in pull order
    volatile String revealBanner;
    volatile String view = "standard";                                           // rates page banner
    // online-minute tracking (main thread only)
    org.bukkit.Location lastLoc;
    int onlineMin;
    boolean onlineDirty;

    PCache(String name) { this.name = name; }

    int dailyGet(String k, String today) { if (!today.equals(day)) return 0; Integer v = daily.get(k); return v == null ? 0 : v; }

    void pushHist(List<Hist> fresh) {
        List<Hist> l = new ArrayList<Hist>(fresh.size() + hist.size());
        for (int i = fresh.size() - 1; i >= 0; i--) l.add(fresh.get(i));
        l.addAll(hist);
        hist = Collections.unmodifiableList(l.size() > 100 ? new ArrayList<Hist>(l.subList(0, 100)) : l);
    }
}

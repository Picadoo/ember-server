package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * §20.3 RunSession: run_id, map / rule version, participants, seed, target-family snapshot taken at entry and the extra
 * event fixed at instance creation. States follow §20.5 (prepare → entered → fighting → settling → complete, or failed /
 * aborted). Persisted as one small YAML file per run so a restart can end it deterministically.
 */
public final class EmberRunSession {

    public static final String PREPARE = "prepare";
    public static final String ENTERED = "entered";
    public static final String FIGHTING = "fighting";
    public static final String SETTLING = "settling";
    public static final String COMPLETE = "complete";
    public static final String FAILED = "failed";
    public static final String ABORTED = "aborted";

    public String runId;
    public String mapKey;
    public String dungeon;
    public String mapVersion;
    public String ruleVersion;
    public String contentVersion;
    public int tier = 1;
    /** §18.1 challenge difficulty (T3 drops / marks, challenge HP / damage overrides, no first clear) */
    public boolean challenge;
    /** P2-2 abyss tier 1..10 (0 = not an abyss segment); an abyss segment is also {@link #challenge} */
    public int abyss;
    /** P2-8 rotation modifier id fixed at entry (featured challenge map only; "" = none) */
    public String modifier = "";
    /** P2-2 余烬币 fee reserved per participant at entry (refunded exactly like the stamina cost) */
    public final Map<UUID, Integer> fee = new LinkedHashMap<UUID, Integer>();
    public long seed;
    public long created;
    public transient long fightStart; // D116: first room started (raid clear time; not persisted)
    public long updated;
    public String state = PREPARE;
    public String world;          // bound DP instance world
    public UUID leader;
    public final List<UUID> participants = new ArrayList<UUID>();
    public final Set<UUID> committed = new LinkedHashSet<UUID>();
    public final Map<UUID, Integer> cost = new LinkedHashMap<UUID, Integer>();
    public final Map<UUID, String> target = new LinkedHashMap<UUID, String>();
    public int partySize = 1;
    public double hpFactor = 1.0;
    /** P2-5 raid enemy damage factor (1.0 for every other run), locked with hpFactor at entry */
    public double dmgFactor = 1.0;
    public EmberRunRules.Extra extra = EmberRunRules.Extra.NONE;
    public boolean extraDone;
    public final Set<UUID> acted = new LinkedHashSet<UUID>();
    public final Set<UUID> died = new LinkedHashSet<UUID>();
    /** D32: P1 heal potions each participant drank in this run (for the first-death-of-the-day refund) */
    public final Map<UUID, Integer> potions = new LinkedHashMap<UUID, Integer>();
    public final Set<UUID> left = new LinkedHashSet<UUID>();
    public final List<String> cleared = new ArrayList<String>();
    public String reason = "";

    public boolean open() {
        return PREPARE.equals(state) || ENTERED.equals(state) || FIGHTING.equals(state) || SETTLING.equals(state);
    }

    public boolean fightStarted() { return FIGHTING.equals(state) || SETTLING.equals(state) || COMPLETE.equals(state); }

    /** Seeded A/B per room (book §10.I: the seed picks the variant). */
    public boolean variantB(String roomId) {
        return (EmberRunRules.subSeed(seed, "variant", roomId) & 1L) == 1L;
    }

    public long roomSeed(String roomId) { return EmberRunRules.subSeed(seed, "points", roomId); }

    public String targetOf(UUID id) {
        String t = target.get(id);
        return t == null || t.isEmpty() ? null : t;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("run_id", runId);
        m.put("map", mapKey);
        m.put("dungeon", dungeon);
        m.put("map_version", mapVersion);
        m.put("rule_version", ruleVersion);
        m.put("content_version", contentVersion);
        m.put("tier", tier);
        m.put("seed", String.valueOf(seed));
        m.put("created", created);
        m.put("updated", updated);
        m.put("state", state);
        m.put("world", world == null ? "" : world);
        m.put("leader", leader == null ? "" : leader.toString());
        m.put("participants", ids(participants));
        m.put("committed", ids(committed));
        Map<String, Object> c = new LinkedHashMap<String, Object>();
        for (Map.Entry<UUID, Integer> e : cost.entrySet()) c.put(e.getKey().toString(), e.getValue());
        m.put("cost", c);
        Map<String, Object> t = new LinkedHashMap<String, Object>();
        for (Map.Entry<UUID, String> e : target.entrySet()) t.put(e.getKey().toString(), e.getValue() == null ? "" : e.getValue());
        m.put("target", t);
        m.put("party_size", partySize);
        m.put("hp_factor", hpFactor);
        m.put("dmg_factor", dmgFactor);
        m.put("challenge", challenge);
        m.put("abyss", abyss);
        if (modifier != null && !modifier.isEmpty()) m.put("modifier", modifier);
        Map<String, Object> f = new LinkedHashMap<String, Object>();
        for (Map.Entry<UUID, Integer> e : fee.entrySet()) f.put(e.getKey().toString(), e.getValue());
        m.put("fee", f);
        m.put("extra", extra.id);
        m.put("extra_done", extraDone);
        m.put("acted", ids(acted));
        m.put("died", ids(died));
        Map<String, Object> pu = new LinkedHashMap<String, Object>();
        for (Map.Entry<UUID, Integer> e : potions.entrySet()) pu.put(e.getKey().toString(), e.getValue());
        m.put("potions", pu);
        m.put("left", ids(left));
        m.put("cleared", new ArrayList<String>(cleared));
        m.put("reason", reason == null ? "" : reason);
        return m;
    }

    public static EmberRunSession fromMap(Map<?, ?> m) {
        EmberRunSession s = new EmberRunSession();
        s.runId = str(m.get("run_id"));
        s.mapKey = str(m.get("map"));
        s.dungeon = str(m.get("dungeon"));
        s.mapVersion = str(m.get("map_version"));
        s.ruleVersion = str(m.get("rule_version"));
        s.contentVersion = str(m.get("content_version"));
        s.tier = (int) EmberRunMaps.num(m.get("tier"), 1);
        try { s.seed = Long.parseLong(str(m.get("seed"))); } catch (NumberFormatException ignored) { }
        s.created = (long) EmberRunMaps.num(m.get("created"), 0);
        s.updated = (long) EmberRunMaps.num(m.get("updated"), 0);
        s.state = str(m.get("state"));
        s.world = str(m.get("world"));
        if (s.world.isEmpty()) s.world = null;
        s.leader = uuid(m.get("leader"));
        readIds(m.get("participants"), s.participants);
        readIds(m.get("committed"), s.committed);
        if (m.get("cost") instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) m.get("cost")).entrySet()) {
            UUID u = uuid(e.getKey());
            if (u != null) s.cost.put(u, (int) EmberRunMaps.num(e.getValue(), 0));
        }
        if (m.get("target") instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) m.get("target")).entrySet()) {
            UUID u = uuid(e.getKey());
            if (u != null) s.target.put(u, str(e.getValue()));
        }
        s.partySize = (int) EmberRunMaps.num(m.get("party_size"), 1);
        s.hpFactor = EmberRunMaps.num(m.get("hp_factor"), 1.0);
        s.dmgFactor = EmberRunMaps.num(m.get("dmg_factor"), 1.0);
        s.challenge = Boolean.TRUE.equals(m.get("challenge"));
        s.abyss = (int) EmberRunMaps.num(m.get("abyss"), 0);
        s.modifier = m.get("modifier") == null ? "" : String.valueOf(m.get("modifier"));
        if (m.get("fee") instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) m.get("fee")).entrySet()) {
            UUID u = uuid(e.getKey());
            if (u != null) s.fee.put(u, (int) EmberRunMaps.num(e.getValue(), 0));
        }
        s.extra = EmberRunRules.Extra.parse(str(m.get("extra")));
        s.extraDone = Boolean.TRUE.equals(m.get("extra_done"));
        readIds(m.get("acted"), s.acted);
        readIds(m.get("died"), s.died);
        if (m.get("potions") instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) m.get("potions")).entrySet()) {
            UUID u = uuid(e.getKey());
            if (u != null) s.potions.put(u, (int) EmberRunMaps.num(e.getValue(), 0));
        }
        readIds(m.get("left"), s.left);
        if (m.get("cleared") instanceof List) for (Object o : (List<?>) m.get("cleared")) s.cleared.add(String.valueOf(o));
        s.reason = str(m.get("reason"));
        return s;
    }

    private static List<String> ids(java.util.Collection<UUID> c) {
        List<String> out = new ArrayList<String>();
        for (UUID u : c) out.add(u.toString());
        return out;
    }

    private static void readIds(Object o, java.util.Collection<UUID> into) {
        if (!(o instanceof List)) return;
        for (Object x : (List<?>) o) { UUID u = uuid(x); if (u != null && !into.contains(u)) into.add(u); }
    }

    private static UUID uuid(Object o) {
        if (o == null) return null;
        try { return UUID.fromString(String.valueOf(o)); } catch (IllegalArgumentException e) { return null; }
    }

    private static String str(Object o) { return o == null ? "" : String.valueOf(o); }
}

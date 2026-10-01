package town.sunshine.corerpg.p1;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * G04 pure settlement rules (book §9.1–9.4, §20.5). No Bukkit: the run service feeds facts in and records the
 * returned grants in the {@link Ledger}. Every random draw comes from a sub-seed of (run seed, player, reward key), so a
 * replay produces the same result, and the ledger keeps the first result anyway (E10: retries never re-roll).
 */
public final class EmberRunRules {

    private EmberRunRules() { }

    // ------------------------------------------------------------------ §9.1 numbers

    public static final int BASE_COIN = 300;
    public static final int BASE_SHARD = 24;
    public static final int BASE_BONE = 6;
    public static final int BASE_CORE = 2;
    public static final int BASE_XP = 120;
    public static final int BASE_MARK = 1;
    /** §9.3 extra event payouts */
    public static final int TREASURE_COIN = 100;
    public static final int ELITE_SHARD = 10;
    public static final int ELITE_CORE = 1;
    /** §9.1 forge marks: 8 same-tier marks → one standard item of a chosen family + slot */
    public static final int MARKS_PER_EXCHANGE = 8;

    public static final String[] FAMILIES = {"scorch", "burst", "sustain"};
    public static final double TARGET_WEIGHT = 0.60;
    /** §5.1 标准/精良/卓越/极品 */
    public static final int[] QUALITY_WEIGHTS = {70, 23, 6, 1};
    /** §5.2 精工 0/2/4/6 % */
    public static final int[] CRAFT_WEIGHTS = {70, 20, 9, 1};
    /** §9.3 none / treasure mob / reward elite / extra chest */
    public static final int[] EXTRA_WEIGHTS = {70, 15, 10, 5};
    /** §7.5 / A18 party HP: 1 + 0.65 (n − 1), n = 1..3, locked at start */
    public static final double HP_PER_EXTRA = 0.65;
    public static final int PARTY_MAX = 3;

    public enum Extra {
        NONE("none", "无"), TREASURE("treasure", "宝藏怪"), ELITE("elite", "奖励精英"), CHEST("chest", "额外宝箱");
        public final String id;
        public final String label;
        Extra(String id, String label) { this.id = id; this.label = label; }
        public static Extra parse(String s) {
            for (Extra e : values()) if (e.id.equalsIgnoreCase(s) || e.name().equalsIgnoreCase(s)) return e;
            return NONE;
        }
    }

    // ------------------------------------------------------------------ weighted picks

    /** Index of the bucket that u ∈ [0,1) falls into for integer weights. */
    public static int pick(int[] weights, double u) {
        int total = 0;
        for (int w : weights) total += w;
        double x = Math.max(0.0, Math.min(0.999999999, u)) * total;
        int acc = 0;
        for (int i = 0; i < weights.length; i++) {
            acc += weights[i];
            if (x < acc) return i;
        }
        return weights.length - 1;
    }

    public static Extra rollExtra(double u) { return Extra.values()[pick(EXTRA_WEIGHTS, u)]; }

    public static boolean validFamily(String f) {
        for (String x : FAMILIES) if (x.equals(f)) return true;
        return false;
    }

    /** Target family 60 %, each other family 20 %; without a target each family 1/3. */
    public static String pickFamily(String target, double u) {
        u = Math.max(0.0, Math.min(0.999999999, u));
        if (!validFamily(target)) return FAMILIES[Math.min(2, (int) (u * 3))];
        if (u < TARGET_WEIGHT) return target;
        List<String> others = new ArrayList<String>(2);
        for (String f : FAMILIES) if (!f.equals(target)) others.add(f);
        double rest = (1.0 - TARGET_WEIGHT) / 2.0;
        return u < TARGET_WEIGHT + rest ? others.get(0) : others.get(1);
    }

    public static double familyProbability(String target, String fam) {
        if (!validFamily(fam)) return 0.0;
        if (!validFamily(target)) return 1.0 / 3.0;
        return fam.equals(target) ? TARGET_WEIGHT : (1.0 - TARGET_WEIGHT) / 2.0;
    }

    public static String pickSlot(double u) { return u < 0.5 ? "blade" : "charm"; }

    public static int pickQuality(double u) { return pick(QUALITY_WEIGHTS, u); }

    public static int pickCraft(double u) { return pick(CRAFT_WEIGHTS, u); }

    public static double hpFactor(int partySize) {
        int n = Math.max(1, Math.min(PARTY_MAX, partySize));
        return 1.0 + HP_PER_EXTRA * (n - 1);
    }

    // ------------------------------------------------------------------ seeds

    /** Stable 64-bit mix of the run seed with a text key (FNV-1a + splitmix64 finaliser). */
    public static long subSeed(long seed, String... parts) {
        long h = 0xcbf29ce484222325L ^ seed;
        for (String p : parts) {
            byte[] b = (p == null ? "" : p).getBytes(StandardCharsets.UTF_8);
            for (byte x : b) { h ^= (x & 0xff); h *= 0x100000001b3L; }
            h ^= 0x1f; h *= 0x100000001b3L;
        }
        h += 0x9E3779B97F4A7C15L;
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        return h ^ (h >>> 31);
    }

    /** Deterministic 32-hex item uid for one reward row (re-delivery reuses the same identity). */
    public static String rewardUid(long seed, String player, String runId, String key) {
        long a = subSeed(seed, "uid-a", player, runId, key);
        long b = subSeed(seed, "uid-b", player, runId, key);
        return String.format(Locale.ROOT, "%016x%016x", a, b);
    }

    // ------------------------------------------------------------------ item roll

    public static final class ItemRoll {
        public final String family, slot;
        public final int tier, quality, craft;
        public ItemRoll(String family, String slot, int tier, int quality, int craft) {
            this.family = family; this.slot = slot; this.tier = tier; this.quality = quality; this.craft = craft;
        }
        @Override public String toString() { return family + ":" + slot + ":" + tier + ":" + quality + ":" + craft; }
    }

    /** §9.1 random item: tier = the map tier, target family 60/20/20, blade/charm 50/50, quality and craft independent. */
    public static ItemRoll rollItem(int tier, String target, Random r) {
        String fam = pickFamily(target, r.nextDouble());
        String slot = pickSlot(r.nextDouble());
        int q = pickQuality(r.nextDouble());
        int c = pickCraft(r.nextDouble());
        return new ItemRoll(fam, slot, tier, q, c);
    }

    // ------------------------------------------------------------------ grants

    public enum Kind { COIN, XP, MAT, MARK, ITEM, CHOICE, UNLOCK, STAMINA }

    /** One reward row. {@link #encode()} is what the ledger stores; {@link #decode(String, String)} reads it back. */
    public static final class Grant {
        public final String key;
        public final Kind kind;
        public final String id;      // MAT: NI id · MARK: tier · CHOICE: slot · UNLOCK: map key · ITEM: uid
        public final int amount;     // COIN / XP / MAT / MARK amount; CHOICE: tier
        public final ItemRoll item;  // ITEM only

        public Grant(String key, Kind kind, String id, int amount, ItemRoll item) {
            this.key = key; this.kind = kind; this.id = id; this.amount = amount; this.item = item;
        }

        public String encode() {
            switch (kind) {
                case COIN: return "coin:" + amount;
                case XP: return "xp:" + amount;
                case MAT: return "mat:" + id + ":" + amount;
                case MARK: return "mark:" + id + ":" + amount;
                case CHOICE: return "choice:" + id + ":" + amount;
                case UNLOCK: return "unlock:" + id;
                case STAMINA: return "stamina:" + amount;
                default: return "item:" + id + ":" + item;
            }
        }

        public static Grant decode(String key, String s) {
            if (s == null) return null;
            String[] p = s.split(":");
            try {
                switch (p[0]) {
                    case "coin": return new Grant(key, Kind.COIN, null, Integer.parseInt(p[1]), null);
                    case "xp": return new Grant(key, Kind.XP, null, Integer.parseInt(p[1]), null);
                    case "mat": return new Grant(key, Kind.MAT, p[1], Integer.parseInt(p[2]), null);
                    case "mark": return new Grant(key, Kind.MARK, p[1], Integer.parseInt(p[2]), null);
                    case "choice": return new Grant(key, Kind.CHOICE, p[1], Integer.parseInt(p[2]), null);
                    case "unlock": return new Grant(key, Kind.UNLOCK, p[1], 0, null);
                    case "stamina": return new Grant(key, Kind.STAMINA, null, Integer.parseInt(p[1]), null);
                    case "item": return new Grant(key, Kind.ITEM, p[1], 1, new ItemRoll(p[2], p[3],
                            Integer.parseInt(p[4]), Integer.parseInt(p[5]), Integer.parseInt(p[6])));
                    default: return null;
                }
            } catch (RuntimeException e) {
                return null;
            }
        }

        @Override public String toString() { return key + "=" + encode(); }
    }

    /** §9.4 first-clear package of one map (content-versioned; per character). */
    public static final class FirstClear {
        public final String mapKey;
        public final String choiceSlot; // blade / charm / null
        public final int choiceTier;
        public final int shard, core, coin, bone, blank;
        public final String unlocks;    // next map key or null
        public FirstClear(String mapKey, String choiceSlot, int choiceTier, int shard, int core, int coin, String unlocks) {
            this(mapKey, choiceSlot, choiceTier, shard, core, coin, 0, 0, unlocks);
        }
        /** §9.4: Q04 胚料 6·核心 6·币 900; Q05 骨尘 20·胚料 6 */
        public FirstClear(String mapKey, String choiceSlot, int choiceTier, int shard, int core, int coin, int bone, int blank, String unlocks) {
            this.mapKey = mapKey; this.choiceSlot = choiceSlot; this.choiceTier = choiceTier;
            this.shard = shard; this.core = core; this.coin = coin; this.bone = bone; this.blank = blank; this.unlocks = unlocks;
        }
    }

    /** Everything settlement needs for one player of one run. */
    public static final class SettleInput {
        public String runId = "";
        public String player = "";
        public long seed;
        public int tier = 1;
        public String target;           // entry snapshot (may be null → uniform families)
        public boolean bossKilled;
        public Extra extra = Extra.NONE;
        public boolean extraDone;       // event completed during the run (pending until the boss dies)
        public FirstClear firstClear;   // null when this character already has the first clear of this content version
    }

    /**
     * §9.1/§9.3/§9.4 grant list for one eligible player. Empty unless the boss died (E11). Keys are stable, so
     * recording the list twice in the ledger is a no-op (E02/E10).
     */
    public static List<Grant> settle(SettleInput in) {
        if (in == null || !in.bossKilled) return Collections.emptyList();
        List<Grant> out = new ArrayList<Grant>();
        out.add(new Grant("base_coin", Kind.COIN, null, BASE_COIN, null));
        out.add(new Grant("base_shard", Kind.MAT, EmberUpgradeRules.MAT_SHARD, BASE_SHARD, null));
        out.add(new Grant("base_bone", Kind.MAT, EmberUpgradeRules.MAT_BONE, BASE_BONE, null));
        out.add(new Grant("base_core", Kind.MAT, EmberUpgradeRules.MAT_CORE, BASE_CORE, null));
        out.add(new Grant("base_xp", Kind.XP, null, BASE_XP, null));
        out.add(new Grant("base_mark", Kind.MARK, String.valueOf(in.tier), BASE_MARK, null));
        out.add(item(in, "base_item"));
        if (in.extraDone) {
            switch (in.extra) {
                case TREASURE:
                    out.add(new Grant("extra_treasure_coin", Kind.COIN, null, TREASURE_COIN, null));
                    break;
                case ELITE:
                    out.add(new Grant("extra_elite_shard", Kind.MAT, EmberUpgradeRules.MAT_SHARD, ELITE_SHARD, null));
                    out.add(new Grant("extra_elite_core", Kind.MAT, EmberUpgradeRules.MAT_CORE, ELITE_CORE, null));
                    break;
                case CHEST:
                    out.add(item(in, "extra_chest_item")); // same tier, same target pool (E04: never above the map tier)
                    break;
                default:
                    break;
            }
        }
        FirstClear fc = in.firstClear;
        if (fc != null) {
            String p = "fc_" + fc.mapKey + "_";
            if (fc.choiceSlot != null) out.add(new Grant(p + "choice", Kind.CHOICE, fc.choiceSlot, fc.choiceTier, null));
            if (fc.shard > 0) out.add(new Grant(p + "shard", Kind.MAT, EmberUpgradeRules.MAT_SHARD, fc.shard, null));
            if (fc.core > 0) out.add(new Grant(p + "core", Kind.MAT, EmberUpgradeRules.MAT_CORE, fc.core, null));
            if (fc.coin > 0) out.add(new Grant(p + "coin", Kind.COIN, null, fc.coin, null));
            if (fc.bone > 0) out.add(new Grant(p + "bone", Kind.MAT, EmberUpgradeRules.MAT_BONE, fc.bone, null));
            if (fc.blank > 0) out.add(new Grant(p + "blank", Kind.MAT, EmberUpgradeRules.MAT_BLANK, fc.blank, null));
            if (fc.unlocks != null && !fc.unlocks.isEmpty()) out.add(new Grant(p + "unlock", Kind.UNLOCK, fc.unlocks, 0, null));
        }
        return out;
    }

    private static Grant item(SettleInput in, String key) {
        Random r = new Random(subSeed(in.seed, in.player, in.runId, key));
        return new Grant(key, Kind.ITEM, rewardUid(in.seed, in.player, in.runId, key), 1, rollItem(in.tier, in.target, r));
    }

    /** First-clear free choice → the bound standard quest item (q0, craft 0, +0). */
    public static Grant choiceItem(long seed, String player, String runId, String choiceKey, String family, String slot, int tier) {
        String key = choiceKey.replace("_choice", "_item");
        return new Grant(key, Kind.ITEM, rewardUid(seed, player, runId, key), 1, new ItemRoll(family, slot, tier, 0, 0));
    }

    // ------------------------------------------------------------------ eligibility (§20.5)

    /**
     * A participant qualifies when the cost was committed for him, he acted in the run (hit / was hit by a run mob or
     * triggered a room) and he is still in the instance at the kill — or died there earlier (§20.5: a legal participant
     * who died keeps the settlement). An idle alt that never acted does not qualify, someone who left without dying
     * does not either, and nothing is split by damage share.
     */
    public static boolean eligible(boolean committed, boolean acted, boolean presentAtKill, boolean diedInRun) {
        return committed && acted && (presentAtKill || diedInRun);
    }

    // ------------------------------------------------------------------ forge marks (§9.1, E03/E04)

    /** @return null when the exchange is allowed, else the reason. */
    public static String exchangeCheck(int haveOfTier, int markTier, int wantTier, String family, String slot) {
        if (!validFamily(family)) return "族无效";
        if (!"blade".equals(slot) && !"charm".equals(slot)) return "部位无效";
        if (markTier < 1 || markTier > EmberTables.MAX_TIER) return "印记阶无效";
        if (wantTier != markTier) return "T" + markTier + " 印记只能兑换 T" + markTier + " 装备（升阶走配方）";
        if (haveOfTier < MARKS_PER_EXCHANGE) return "T" + markTier + " 印记不足（" + haveOfTier + "/" + MARKS_PER_EXCHANGE + "）";
        return null;
    }

    // ------------------------------------------------------------------ ledger (§20.3 RewardLedger)

    public static final String ST_PENDING = "pending";
    public static final String ST_DELIVERED = "delivered";
    public static final String ST_MAILED = "mailed";
    public static final String ST_AWAIT = "await_choice";
    /** cost rows (§20.5 reserve → commit / release) */
    public static final String ST_RESERVED = "reserved";
    public static final String ST_COMMITTED = "committed";
    public static final String ST_RELEASED = "released";

    public static final class Row {
        public final String runId, key, result;
        public String status;
        public final long created;
        public long updated;
        public Row(String runId, String key, String result, String status, long created, long updated) {
            this.runId = runId; this.key = key; this.result = result; this.status = status;
            this.created = created; this.updated = updated;
        }
        public boolean open() { return ST_PENDING.equals(status) || ST_AWAIT.equals(status); }
    }

    /**
     * One player's reward ledger: one row per (run_id, reward_key). {@link #record} never overwrites, so a second
     * settlement (retry, duplicate MM/DP event, reconnect) returns the first result and creates nothing (E02/E10).
     */
    public static final class Ledger {
        private final Map<String, Row> rows = new LinkedHashMap<String, Row>();

        public static String id(String runId, String key) { return runId + "/" + key; }

        /** @return the stored row; {@code created[0]} tells whether this call created it. */
        public Row record(String runId, String key, String result, String status, long now, boolean[] created) {
            String id = id(runId, key);
            Row r = rows.get(id);
            if (r != null) {
                if (created != null) created[0] = false;
                return r;
            }
            r = new Row(runId, key, result, status, now, now);
            rows.put(id, r);
            if (created != null) created[0] = true;
            return r;
        }

        public Row get(String runId, String key) { return rows.get(id(runId, key)); }

        public boolean has(String runId, String key) { return rows.containsKey(id(runId, key)); }

        public void mark(String runId, String key, String status, long now) {
            Row r = rows.get(id(runId, key));
            if (r != null) { r.status = status; r.updated = now; }
        }

        public List<Row> open() {
            List<Row> out = new ArrayList<Row>();
            for (Row r : rows.values()) if (r.open()) out.add(r);
            return out;
        }

        public List<Row> all() { return new ArrayList<Row>(rows.values()); }

        public int size() { return rows.size(); }

        /** Drops closed rows older than {@code keepMs}; open rows are kept forever. */
        public int prune(long now, long keepMs) {
            int n = 0;
            java.util.Iterator<Row> it = rows.values().iterator();
            while (it.hasNext()) {
                Row r = it.next();
                if (!r.open() && !ST_RESERVED.equals(r.status) && now - r.updated > keepMs) { it.remove(); n++; }
            }
            return n;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> m = new LinkedHashMap<String, Object>();
            for (Map.Entry<String, Row> e : rows.entrySet()) {
                Row r = e.getValue();
                Map<String, Object> x = new LinkedHashMap<String, Object>();
                x.put("run", r.runId);
                x.put("key", r.key);
                x.put("result", r.result);
                x.put("status", r.status);
                x.put("created", r.created);
                x.put("updated", r.updated);
                m.put(e.getKey().replace('.', '_'), x);
            }
            return m;
        }

        public static Ledger fromMap(Map<String, ?> m) {
            Ledger l = new Ledger();
            if (m == null) return l;
            for (Object o : m.values()) {
                if (!(o instanceof Map)) continue;
                Map<?, ?> x = (Map<?, ?>) o;
                String run = String.valueOf(x.get("run"));
                String key = String.valueOf(x.get("key"));
                Row r = new Row(run, key, String.valueOf(x.get("result")), String.valueOf(x.get("status")),
                        num(x.get("created")), num(x.get("updated")));
                l.rows.put(id(run, key), r);
            }
            return l;
        }

        private static long num(Object o) { return o instanceof Number ? ((Number) o).longValue() : 0L; }
    }
}

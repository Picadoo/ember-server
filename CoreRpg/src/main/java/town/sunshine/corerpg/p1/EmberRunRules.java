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
    /**
     * P2-1 weekly challenge rotation (docs/design/design-ember-v1.1-P2-draft.md §3): one of the seven challenge maps is featured
     * each Monday-based week (Asia/Shanghai); its first rotation.weekly_cap challenge clears per character and week add
     * rotation.bonus_marks forge marks of the run tier (parameter source: ember-v1-runs.yml, D66). No coin, no item,
     * no multiplier (§23.3).
     */

    /** Monday-based week number since the epoch (1970-01-05 is week 1's Monday; 1970-01-01 was a Thursday). */
    public static long weekIndex(java.time.LocalDate day) {
        return Math.floorDiv(day.toEpochDay() + 3, 7);
    }

    /** The featured challenge map of the week containing {@code day}; null for an empty map list. */
    public static String featuredChallenge(List<String> order, java.time.LocalDate day) {
        if (order == null || order.isEmpty()) return null;
        return order.get((int) Math.floorMod(weekIndex(day), (long) order.size()));
    }

    /** Ledger / counter key of the rotation week ("w" + index). */
    public static String rotationWeekKey(java.time.LocalDate day) {
        return "w" + weekIndex(day);
    }

    /** §9.1 forge marks: 8 same-tier marks → one standard item of a chosen family + slot */
    public static final int MARKS_PER_EXCHANGE = 8;

    /** F-review #1 (D120): auto-equip verdicts for a new piece against the active piece of the same slot */
    public static final int UP_NONE = 0, UP_AUTO = 1, UP_ASK = 2, UP_ASK_SWAP = 3;
    /** same tier: the new piece must be at least this much stronger to count as "clearly better" */
    public static final double UP_SAME_TIER = 1.05;

    /** the slot value of a piece (blade attack B / charm life H0), optionally with another enhance level */
    public static double pieceValue(EmberTables t, EmberItemData d, int enhance, int level) {
        return d.isBlade() ? EmberFormula.baseAttack(t, d.tier, d.quality, d.craft, enhance, level)
                : EmberFormula.baseHp(t, d.tier, d.quality, d.craft, enhance, level);
    }

    /**
     * D120: is {@code c} strictly better than the active {@code a} (higher tier, or same tier and ≥ 5 % more value)?
     * The active piece's enhance counts as moveable (the free swap), so the comparison uses c at a's enhance level.
     * Verdict: UP_AUTO when a has no investment (+0) and c does not break the active set; UP_ASK when it would break
     * the set; UP_ASK_SWAP when a is enhanced (offer [换上] + [免费互换强化]); UP_NONE when c is not better.
     */
    public static int upgradeVerdict(EmberTables t, EmberItemData c, EmberItemData a, String activeSet, int level) {
        if (c == null || a == null || !c.slot.equals(a.slot) || c.uid.equals(a.uid)) return UP_NONE;
        double vc = pieceValue(t, c, Math.max(c.enhance, a.enhance), level), va = pieceValue(t, a, a.enhance, level);
        boolean better = c.tier > a.tier ? vc >= va : c.tier == a.tier && vc >= va * UP_SAME_TIER;
        if (!better) return UP_NONE;
        if (a.enhance > c.enhance) return UP_ASK_SWAP;
        boolean breaks = activeSet != null && !"none".equals(activeSet) && !activeSet.equals(c.family);
        return breaks ? UP_ASK : UP_AUTO;
    }

    /** D120 "strictly better": higher tier with at least the value, or same tier with ≥ UP_SAME_TIER × the value (each at its own enhance) */
    public static boolean betterThan(EmberTables t, EmberItemData x, EmberItemData y, int level) {
        if (x == null) return false;
        if (y == null) return true;
        double vx = pieceValue(t, x, x.enhance, level), vy = pieceValue(t, y, y.enhance, level);
        return x.tier > y.tier ? vx >= vy : x.tier == y.tier && vx >= vy * UP_SAME_TIER;
    }

    /**
     * Recheck #2 (D132): the piece to offer when {@code fresh} arrives — the best real (T1+) piece of that slot among the
     * fresh one and the bag, skipping the active one; the fresh piece wins ties. {@code family} non-null = only that family
     * counts for bag pieces (keeps a set together); the fresh piece always counts.
     */
    public static EmberItemData bestCandidate(EmberTables t, EmberItemData fresh, List<EmberItemData> bag, EmberItemData active,
                                              String family, int level) {
        EmberItemData best = fresh;
        for (EmberItemData b : bag) {
            if (b == null || !b.slot.equals(fresh.slot) || b.tier < 1 || b.uid.equals(fresh.uid) || (active != null && b.uid.equals(active.uid))) continue;
            if (family != null && !family.equals(b.family)) continue;
            if (betterThan(t, b, best, level)) best = b;
        }
        return best;
    }

    public static final String[] FAMILIES = {"scorch", "burst", "sustain"};
    public static final double TARGET_WEIGHT = 0.60;
    /** §5.1 标准/精良/卓越/极品 */
    public static final int[] QUALITY_WEIGHTS = {70, 23, 6, 1};
    /** §18.1 challenge 成色 标准/精良/卓越/极品 (精工 unchanged) */
    public static final int[] CHALLENGE_QUALITY_WEIGHTS = {60, 28, 10, 2};
    /** §18.1 challenge items and marks are all T3 */
    public static final int CHALLENGE_TIER = 3;
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

    /** D166: told once when the last room is cleared, before the boss hall */
    public static final String PRE_BOSS_HINT = "§e首领前留 2 瓶药 §7· 首领战更长、伤害更高；清房时生命够就别急着喝";

    /** D89: player-facing room line, e.g. "近战 ×3 · 远程 ×1 §8（远程会站远放箭，先清）" from layout rows {role, point}. */
    public static String compositionLabel(List<String[]> layout) {
        String[] order = {"melee", "ranged", "caster", "heavy", "elite"};
        String[] names = {"近战", "远程", "术者", "重甲", "精英"};
        int[] n = new int[order.length];
        for (String[] e : layout) for (int i = 0; i < order.length; i++) if (order[i].equals(e[0])) n[i]++;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < order.length; i++) if (n[i] > 0) sb.append(sb.length() == 0 ? "§f" : " §7· §f").append(names[i]).append(" ×").append(n[i]);
        String tip = n[2] > 0 ? "术者地面亮火线后会打一条直线，横移躲开" : n[1] > 0 ? "远程站远放箭，先清掉" : n[3] > 0 ? "重甲血厚、出手慢、不怕击退，绕着打、别硬吃" : "";
        if (!tip.isEmpty()) sb.append(" §8（").append(tip).append("）");
        return sb.toString();
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

    public static int pickQuality(int[] weights, double u) { return pick(weights == null ? QUALITY_WEIGHTS : weights, u); }

    public static int pickCraft(double u) { return pick(CRAFT_WEIGHTS, u); }

    public static double hpFactor(int partySize) {
        int n = Math.max(1, Math.min(PARTY_MAX, partySize));
        return 1.0 + HP_PER_EXTRA * (n - 1);
    }

    // ------------------------------------------------------------------ seeds

    /** Stable 64-bit mix of the run seed with a text key (FNV-1a + splitmix64 finaliser). */
    /**
     * D138 repeat-run variety rewards (existing types only): killing the affixed elite → shards, finishing the timed
     * room → cores. Never on a first clear (the caller passes firstClear = the run carried the first-clear package).
     */
    public static List<Grant> varietyGrants(boolean firstClear, boolean affixDone, int affixShard, boolean eventDone, int eventCore) {
        return varietyGrants(firstClear, affixDone, affixShard, eventDone, eventCore, "timed");
    }

    /** D171: eventKind selects the settle source key (var_event_core / var_event_crystal / var_event_escort). */
    public static List<Grant> varietyGrants(boolean firstClear, boolean affixDone, int affixShard, boolean eventDone, int eventCore, String eventKind) {
        if (firstClear) return Collections.emptyList();
        List<Grant> out = new ArrayList<Grant>();
        if (affixDone && affixShard > 0) out.add(new Grant("var_affix_shard", Kind.MAT, EmberUpgradeRules.MAT_SHARD, affixShard, null));
        if (eventDone && eventCore > 0) {
            String k = "crystal".equals(eventKind) ? "var_event_crystal" : "escort".equals(eventKind) ? "var_event_escort" : "var_event_core";
            out.add(new Grant(k, Kind.MAT, EmberUpgradeRules.MAT_CORE, eventCore, null));
        }
        return out;
    }

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
    public static ItemRoll rollItem(int tier, String target, Random r) { return rollItem(tier, target, r, null); }

    /** same draw order as the normal roll; {@code qualityWeights} null = §5.1 normal table (challenge: 60/28/10/2) */
    public static ItemRoll rollItem(int tier, String target, Random r, int[] qualityWeights) {
        String fam = pickFamily(target, r.nextDouble());
        String slot = pickSlot(r.nextDouble());
        int q = pickQuality(qualityWeights, r.nextDouble());
        int c = pickCraft(r.nextDouble());
        return new ItemRoll(fam, slot, tier, q, c);
    }

    /**
     * P2-9 (D81) per-map loot identity. Target family keeps {@link #TARGET_WEIGHT} unless the map family IS the target
     * ({@code ownFamily}); on other maps the map family takes {@code mapShare} of the non-target rest. No target: map
     * family 50 %, each other 25 %. The map slot gets {@code slotWeight}. Same four draws as the plain roll.
     */
    public static final class LootBias {
        public final String family, slot;
        public final double ownFamily, mapShare, slotWeight;
        public LootBias(String family, String slot, double ownFamily, double mapShare, double slotWeight) {
            this.family = family; this.slot = slot; this.ownFamily = ownFamily; this.mapShare = mapShare; this.slotWeight = slotWeight;
        }
    }

    public static String pickFamily(String target, LootBias lb, double u) {
        if (lb == null || lb.family == null) return pickFamily(target, u);
        u = Math.max(0.0, Math.min(0.999999999, u));
        List<String> others = new ArrayList<String>(2);
        if (!validFamily(target)) {
            if (u < 0.5) return lb.family;
            for (String f : FAMILIES) if (!f.equals(lb.family)) others.add(f);
            return u < 0.75 ? others.get(0) : others.get(1);
        }
        for (String f : FAMILIES) if (!f.equals(target)) others.add(f);
        if (lb.family.equals(target)) {
            double tw = lb.ownFamily;
            if (u < tw) return target;
            return u < tw + (1.0 - tw) / 2.0 ? others.get(0) : others.get(1);
        }
        if (u < TARGET_WEIGHT) return target;
        String third = others.get(0).equals(lb.family) ? others.get(1) : others.get(0);
        return u < TARGET_WEIGHT + (1.0 - TARGET_WEIGHT) * lb.mapShare ? lb.family : third;
    }

    public static double familyProbability(String target, LootBias lb, String fam) {
        if (lb == null || lb.family == null) return familyProbability(target, fam);
        if (!validFamily(fam)) return 0.0;
        if (!validFamily(target)) return fam.equals(lb.family) ? 0.5 : 0.25;
        if (lb.family.equals(target)) return fam.equals(target) ? lb.ownFamily : (1.0 - lb.ownFamily) / 2.0;
        if (fam.equals(target)) return TARGET_WEIGHT;
        return (1.0 - TARGET_WEIGHT) * (fam.equals(lb.family) ? lb.mapShare : 1.0 - lb.mapShare);
    }

    public static String pickSlot(LootBias lb, double u) {
        if (lb == null || lb.slot == null) return pickSlot(u);
        return u < lb.slotWeight ? lb.slot : ("blade".equals(lb.slot) ? "charm" : "blade");
    }

    public static ItemRoll rollItem(int tier, String target, Random r, int[] qualityWeights, LootBias lb) {
        String fam = pickFamily(target, lb, r.nextDouble());
        String slot = pickSlot(lb, r.nextDouble());
        int q = pickQuality(qualityWeights, r.nextDouble());
        int c = pickCraft(r.nextDouble());
        return new ItemRoll(fam, slot, tier, q, c);
    }

    // ------------------------------------------------------------------ D174 签名传奇 grants

    /**
     * D174: grants of a repeat NORMAL clear of a signature map — 1 insignia, and with {@link EmberSignature#STAMP_RATE}
     * (seeded per run + player) the base item becomes one of the map's fitting signatures. {@code base} = the
     * "base_item" grant (null = none). Caller decides the run is a repeat normal main-story clear.
     */
    public static List<Grant> signatureGrants(String map, SettleInput in, Grant base) {
        List<Grant> out = new ArrayList<Grant>(2);
        if (!EmberSignature.hasMap(map)) return out;
        out.add(new Grant("sig_mark", Kind.SIGMARK, map, EmberSignature.CLEAR_MARKS, null));
        if (base != null && base.kind == Kind.ITEM && base.item != null && base.item.tier >= 1) {
            double u = new Random(subSeed(in.seed, in.player, in.runId, "sig_stamp")).nextDouble();
            EmberSignature.Def d = EmberSignature.rollStamp(map, base.item.slot, base.item.family, u);
            if (d != null) out.add(new Grant("sig_stamp", Kind.SIG, base.id + "/" + d.id, 0, null));
        }
        return out;
    }

    // ------------------------------------------------------------------ grants

    public enum Kind { COIN, XP, MAT, MARK, ITEM, CHOICE, UNLOCK, STAMINA, POTION, SIGMARK, SIG } // D174: 首领徽记 (id = map) / 签名 stamp (id = uid/Lxx)

    /** D32 / B2.172: ledger run id of the once-per-day death refund (day = stamina day, DailyService.today()). */
    public static String deathRefundRun(String day) { return "deathrefund@" + day; }
    public static final String DEATH_REFUND_KEY = "potions";

    /**
     * Recheck #4 (D131): where the T0 starter blade goes when the first real blade takes its hotbar slot — the first free
     * backpack slot (9..35); only with a full backpack does it take the slot the new blade landed in. {@code used} = the
     * 36 storage slots (true = occupied), {@code newAt} = where the new blade landed.
     */
    public static int starterTarget(boolean[] used, int newAt) {
        for (int i = 9; i < Math.min(36, used.length); i++) if (!used[i]) return i;
        return newAt;
    }

    /** Endgame #6 (D128): ledger run id of the once-per-day failed-challenge stamina refund (stamina day). */
    public static String failRefundRun(String day) { return "failrefund@" + day; }
    public static final String FAIL_REFUND_KEY = "stamina";

    /** D128: stamina given back for the day's first failed challenge / abyss run = floor(cost × share), share in [0, 1]. */
    public static int failRefundAmount(int cost, double share) {
        if (cost <= 0 || !(share > 0)) return 0;
        return (int) Math.floor(cost * Math.min(1.0, share) + 1e-9);
    }

    /**
     * D160 余烬连战 (boss rush): the WEEKLY REWARD is claimed once, attempts are unlimited and free. {@code claims} = reward
     * claims settled this week (the {@code p4_rush_claim} counter), {@code legacyClear} = a pre-D160 clear this week (the
     * old rule counted entries, not clears; its clear is on the weekly time board), so the switch-over week never pays twice.
     */
    public static int rushClaims(int claims, boolean legacyClear) { return Math.max(Math.max(0, claims), legacyClear ? 1 : 0); }

    /** D160: does a settled rush clear pay the weekly reward (marks / 余烬徽)? false = practice clear (no reward). */
    public static boolean rushPaysReward(int claimsThisWeek, int weeklyClaims) { return weeklyClaims > 0 && claimsThisWeek < weeklyClaims; }

    /** D160: one-line weekly state for menus / chat, e.g. 「本周奖励未领 · 失败可无限重试」. */
    public static String rushWeekText(int claimsThisWeek, int weeklyClaims) {
        return rushPaysReward(claimsThisWeek, weeklyClaims) ? "本周奖励未领 · 失败可无限重试，不扣任何东西"
                : "本周奖励已领 · 可以继续练习（无奖励），周一 0 点重置";
    }

    /** D32: potions refunded for the first death of the day = used in that run, capped by config (max <= 0 = off). */
    public static int deathRefundCount(int used, int max) { return max <= 0 ? 0 : Math.max(0, Math.min(used, max)); }

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
                case POTION: return "potion:" + amount + (id == null ? "" : ":" + id);
                case SIGMARK: return "sigmark:" + id + ":" + amount;
                case SIG: return "sig:" + id;
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
                    case "potion": return new Grant(key, Kind.POTION, p.length > 2 ? p[2] : null, Integer.parseInt(p[1]), null);
                    case "sigmark": return new Grant(key, Kind.SIGMARK, p[1], Integer.parseInt(p[2]), null);
                    case "sig": return p.length > 1 && p[1].contains("/") ? new Grant(key, Kind.SIG, p[1], 0, null) : null;
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
        /** §9.4: Q04 胚料 6·核心 6·币 2100 (D31); Q05 骨尘 20·胚料 6·币 900 (D104) */
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
        public int[] qualityWeights;    // null = normal §5.1; challenge §18.1 60/28/10/2
        public LootBias loot;           // P2-9 map loot identity (null = plain 60/20/20, 50/50)
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

    /** P2-5 raids: one more roll with the same tier / target / quality table (stable key → idempotent). */
    public static Grant extraItem(SettleInput in, String key) { return item(in, key); }

    /**
     * P2-9 (D82) raid_item: the player's entry target family (fallback {@code raidFamily}, then a plain draw), slot
     * 50/50, the run's quality table with {@code qualityFloor} (标准 → 精良 at floor 1; 极品 odds unchanged).
     */
    public static Grant raidItem(SettleInput in, String key, String raidFamily, int qualityFloor) {
        Random r = new Random(subSeed(in.seed, in.player, in.runId, key));
        double uf = r.nextDouble();
        String fam = validFamily(in.target) ? in.target : validFamily(raidFamily) ? raidFamily : pickFamily(null, uf);
        String slot = pickSlot(r.nextDouble());
        int q = Math.max(qualityFloor, pickQuality(in.qualityWeights, r.nextDouble()));
        int c = pickCraft(r.nextDouble());
        return new Grant(key, Kind.ITEM, rewardUid(in.seed, in.player, in.runId, key), 1, new ItemRoll(fam, slot, in.tier, Math.min(3, q), c));
    }

    // ------------------------------------------------------------------ P2-7 daily bounty (D79)

    /** One bounty tier: paid once on the {@code clears}-th settled clear of the stamina day (existing currencies only). */
    public static final class BountyTier {
        public final int clears, coin, shard, bone, core;
        public BountyTier(int clears, int coin, int shard, int bone, int core) {
            this.clears = clears; this.coin = coin; this.shard = shard; this.bone = bone; this.core = core;
        }
        public String rewardText() {
            List<String> l = new ArrayList<String>();
            if (coin > 0) l.add(coin + " 余烬币");
            if (shard > 0) l.add("余烬碎片 ×" + shard);
            if (bone > 0) l.add("余烬骨尘 ×" + bone);
            if (core > 0) l.add("余烬核心碎片 ×" + core);
            return l.isEmpty() ? "无" : String.join(" + ", l);
        }
    }

    /** Parses {@code bounty.daily}; drops rows without a positive clears count or any reward; sorted by clears. */
    public static List<BountyTier> bountyTiers(List<? extends Map<?, ?>> raw) {
        List<BountyTier> out = new ArrayList<BountyTier>();
        if (raw == null) return out;
        for (Map<?, ?> m : raw) {
            int c = intOf(m.get("clears")), coin = intOf(m.get("coin")), sh = intOf(m.get("shard")), bo = intOf(m.get("bone")), co = intOf(m.get("core"));
            if (c <= 0 || coin < 0 || sh < 0 || bo < 0 || co < 0 || coin + sh + bo + co <= 0) continue;
            out.add(new BountyTier(c, coin, sh, bo, co));
        }
        Collections.sort(out, (a, b) -> Integer.compare(a.clears, b.clears));
        return out;
    }

    /** D144 花样委托: one daily goal over repeat-run variety outcomes (kind affix / timed), paid once when count is reached. */
    public static final class VarietyBounty {
        public final String kind;
        public final int count, coin, shard;
        public VarietyBounty(String kind, int count, int coin, int shard) { this.kind = kind; this.count = count; this.coin = coin; this.shard = shard; }
        /** timed = any room event success (timed / crystal / escort); id kept for save compat (D144/D171). */
        public String label() { return "affix".equals(kind) ? "击败词缀精英" : "房间事件达标"; }
        public String rewardText() { return (coin > 0 ? coin + " 余烬币" : "") + (coin > 0 && shard > 0 ? " + " : "") + (shard > 0 ? "余烬碎片 ×" + shard : ""); }
    }

    public static List<VarietyBounty> varietyBounties(List<? extends Map<?, ?>> raw) {
        List<VarietyBounty> out = new ArrayList<VarietyBounty>();
        if (raw == null) return out;
        for (Map<?, ?> m : raw) {
            String k = String.valueOf(m.get("kind"));
            int c = intOf(m.get("count")), coin = intOf(m.get("coin")), sh = intOf(m.get("shard"));
            if (!("affix".equals(k) || "timed".equals(k)) || c <= 0 || coin < 0 || sh < 0 || coin + sh <= 0) continue;
            out.add(new VarietyBounty(k, c, coin, sh));
        }
        return out;
    }

    /** D144: the grants of one goal when this run moves it from prev to prev + 1 (empty unless it crosses count) */
    public static List<Grant> varietyBountyGrants(VarietyBounty b, int prev, boolean hit) {
        if (b == null || !hit || prev >= b.count || prev + 1 < b.count) return Collections.emptyList();
        List<Grant> g = new ArrayList<Grant>();
        if (b.coin > 0) g.add(new Grant("vb_" + b.kind, Kind.COIN, null, b.coin, null));
        if (b.shard > 0) g.add(new Grant("vb_" + b.kind + "_shard", Kind.MAT, EmberUpgradeRules.MAT_SHARD, b.shard, null));
        return g;
    }

    private static int intOf(Object o) {
        if (o instanceof Number) return ((Number) o).intValue();
        try { return o == null ? 0 : Integer.parseInt(String.valueOf(o).trim()); } catch (NumberFormatException e) { return -1; }
    }

    /** Grants for the n-th settled clear of the day (empty unless some tier has clears == n). */
    public static List<Grant> bountyGrants(List<BountyTier> tiers, int n) {
        List<Grant> out = new ArrayList<Grant>();
        for (BountyTier t : tiers) {
            if (t.clears != n) continue;
            if (t.coin > 0) out.add(new Grant("bounty_coin_" + n, Kind.COIN, null, t.coin, null));
            if (t.shard > 0) out.add(new Grant("bounty_shard_" + n, Kind.MAT, EmberUpgradeRules.MAT_SHARD, t.shard, null));
            if (t.bone > 0) out.add(new Grant("bounty_bone_" + n, Kind.MAT, EmberUpgradeRules.MAT_BONE, t.bone, null));
            if (t.core > 0) out.add(new Grant("bounty_core_" + n, Kind.MAT, EmberUpgradeRules.MAT_CORE, t.core, null));
        }
        return out;
    }

    /** D105: grants for every tier crossed when today's count goes from {@code prev} to {@code now} (a raid clear counts 2). */
    public static List<Grant> bountyGrants(List<BountyTier> tiers, int prev, int now) {
        List<Grant> out = new ArrayList<Grant>();
        for (int n = prev + 1; n <= now; n++) out.addAll(bountyGrants(tiers, n));
        return out;
    }

    /** Status line after {@code done} settled clears today. */
    public static String bountyLine(List<BountyTier> tiers, int done) {
        if (tiers.isEmpty()) return "未开放";
        for (BountyTier t : tiers) {
            if (t.clears > done) return "今日通关 " + done + " 局 · 再通关 " + (t.clears - done) + " 局 → " + t.rewardText();
        }
        return "今日委托已全部完成（通关 " + done + " 局）· 明天 0 点刷新";
    }

    private static Grant item(SettleInput in, String key) {
        Random r = new Random(subSeed(in.seed, in.player, in.runId, key));
        return new Grant(key, Kind.ITEM, rewardUid(in.seed, in.player, in.runId, key), 1, rollItem(in.tier, in.target, r, in.qualityWeights, in.loot));
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

    /**
     * B2.147 (§23.2 "开放 T2 定向锻造", §6.4): directed forging (8 marks → chosen family + slot) of T2 needs the
     * player's own Q04 first clear, T3 their own Q07 first clear — the same gates as the T1→T2 / T2→T3 upgrade.
     * @return the first-clear key gating {@code tier}, or null when the tier is always open (T1).
     */
    public static String directedForgeFlag(int tier) {
        return tier == 2 ? "q04" : tier == 3 ? "q07" : null;
    }

    /** {@link #exchangeCheck(int, int, int, String, String)} plus the directed-forge gate of the mark tier. */
    public static String exchangeCheck(int haveOfTier, int markTier, int wantTier, String family, String slot, boolean gateOpen) {
        String err = exchangeCheck(haveOfTier, markTier, wantTier, family, slot);
        if (err != null) return err;
        String flag = directedForgeFlag(markTier);
        if (flag != null && !gateOpen) return "T" + markTier + " 定向锻造需本人首通 " + flag.toUpperCase(Locale.ROOT);
        return null;
    }

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

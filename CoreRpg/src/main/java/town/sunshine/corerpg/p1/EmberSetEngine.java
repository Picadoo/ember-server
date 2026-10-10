package town.sunshine.corerpg.p1;

import java.util.LinkedHashSet;
import java.util.Iterator;

/**
 * G02 SetRuntime core for one player (策划书 §4.2–4.4, §8). Pure state machine; the Bukkit side feeds
 * {@link Hit}s from the damage MONITOR and executes the returned trigger. Times are monotonic ms.
 */
public final class EmberSetEngine {

    public enum Kind { MELEE_MAIN, SWEEP, SKILL, BURN, EXPLOSION, THORNS, SUMMON, PROJECTILE, ENVIRONMENT }

    public enum Trigger { NONE, IGNITE, EXPLODE, HEAL }

    /** One damage event as seen at MONITOR. */
    public static final class Hit {
        public Kind kind = Kind.MELEE_MAIN;
        public long rootId;
        public double charge;
        public double finalDamage;
        public boolean cancelled;
        public boolean targetAlive = true;
        public boolean targetEnemy = true;
        public boolean targetInvulnerable;
        public String targetId;
        /** D174 L12 低血回涌: the attacker's current HP / max HP when the hit lands (1 = unknown / full) */
        public double hpFrac = 1.0;

        public static Hit melee(long root, double charge, double dmg, String target) {
            Hit h = new Hit();
            h.rootId = root; h.charge = charge; h.finalDamage = dmg; h.targetId = target;
            return h;
        }
        public Hit kind(Kind k) { this.kind = k; return this; }
    }

    public static final class Outcome {
        public final boolean counted;
        public final String reason;
        public final Trigger trigger;
        /** IGNITE: per-tick damage; EXPLODE: per-target damage; HEAL: HP */
        public final double amount;
        public final double radius;
        public final int counter;
        Outcome(boolean counted, String reason, Trigger trigger, double amount, double radius, int counter) {
            this.counted = counted; this.reason = reason; this.trigger = trigger;
            this.amount = amount; this.radius = radius; this.counter = counter;
        }
        static Outcome no(String why, int counter) { return new Outcome(false, why, Trigger.NONE, 0, 0, counter); }
    }

    /**
     * D141 set tuning from the player's talents (only the active family's fields matter): trigger interval deltas,
     * coefficient multipliers, extra burn ticks, burn spread. {@link #NONE} = the book's rules.
     */
    public static final class Tune {
        public static final Tune NONE = new Tune(0, 0, 1.0, 1.0, 1.0, 0, 0, 0);
        public final int burstEveryDelta, sustainEveryDelta, burnTicksExtra;
        public final double burnMult, burstMult, sustainMult;
        /** D141 燎原: seconds of burn passed on when a burning enemy dies (0 = off) */
        public final double burnSpread;
        /** D141 燎原: at most one spread per this many seconds */
        public final double spreadIcd;
        /** D174 L12 低血回涌: below this HP fraction the 炽愈 interval is SUSTAIN_EVERY + sustainLowEvery instead (0 = off) */
        public final double sustainLow;
        public final int sustainLowEvery;
        public Tune(int burstEveryDelta, int sustainEveryDelta, double burnMult, double burstMult, double sustainMult, int burnTicksExtra, double burnSpread, double spreadIcd) {
            this(burstEveryDelta, sustainEveryDelta, burnMult, burstMult, sustainMult, burnTicksExtra, burnSpread, spreadIcd, 0.0, 0);
        }
        public Tune(int burstEveryDelta, int sustainEveryDelta, double burnMult, double burstMult, double sustainMult, int burnTicksExtra, double burnSpread, double spreadIcd,
                    double sustainLow, int sustainLowEvery) {
            this.burstEveryDelta = burstEveryDelta; this.sustainEveryDelta = sustainEveryDelta; this.burnMult = burnMult;
            this.burstMult = burstMult; this.sustainMult = sustainMult; this.burnTicksExtra = burnTicksExtra; this.burnSpread = burnSpread; this.spreadIcd = spreadIcd;
            this.sustainLow = sustainLow; this.sustainLowEvery = sustainLowEvery;
        }
        @Override public boolean equals(Object o) {
            if (!(o instanceof Tune)) return false;
            Tune t = (Tune) o;
            return t.burstEveryDelta == burstEveryDelta && t.sustainEveryDelta == sustainEveryDelta && t.burnTicksExtra == burnTicksExtra
                    && t.burnMult == burnMult && t.burstMult == burstMult && t.sustainMult == sustainMult && t.burnSpread == burnSpread && t.spreadIcd == spreadIcd
                    && t.sustainLow == sustainLow && t.sustainLowEvery == sustainLowEvery;
        }
        @Override public int hashCode() { return burstEveryDelta * 31 + sustainEveryDelta * 7 + burnTicksExtra + (int) (burnSpread * 1000); }
    }

    private Tune tune = Tune.NONE;

    /** D141: talents changed (or the set did); the counter is clamped to the new interval */
    public void setTune(Tune t) {
        tune = t == null ? Tune.NONE : t;
        burns.setTicks(EmberSetRules.BURN_TICKS + Math.max(0, tune.burnTicksExtra));
        int e = every();
        if (e > 0 && counter > e) counter = e;
    }

    public Tune tune() { return tune; }

    /** D141 反震 (and the unused dodge_burst key): a boss telegraph moves the 烬爆 counter forward by {@code n} swings (capped at the interval). */
    public boolean primeBurst(int n) {
        if (!"burst".equals(family) || n <= 0) return false;
        counter = Math.min(every(), counter + n);
        return true;
    }

    private String family = "none";
    private int awakening;
    private int counter;
    private long lastCombatAt = Long.MIN_VALUE / 2;
    private long burstCdUntil = Long.MIN_VALUE / 2;
    private long sustainCdUntil = Long.MIN_VALUE / 2;
    private final LinkedHashSet<Long> roots = new LinkedHashSet<Long>();
    private final EmberBurnBook burns = new EmberBurnBook();

    public String family() { return family; }
    public int awakening() { return awakening; }
    public int counter() { return counter; }
    public EmberBurnBook burns() { return burns; }

    /** Applies the current loadout. A different family (incl. to/from none) clears counter and burns; cooldowns stay. */
    public boolean setLoadout(String fam, int awk) {
        String f = fam == null ? "none" : fam;
        boolean changed = !f.equals(family);
        if (changed) {
            counter = 0;
            burns.clear();
        }
        family = f;
        awakening = "none".equals(f) ? 0 : awk;
        return changed;
    }

    /** Death / leaving the instance / quit: counters and unfinished burns go, cooldowns stay. */
    public void clearCombat() {
        counter = 0;
        burns.clear();
        roots.clear();
    }

    /** Any combat activity (enemy damage taken, damage dealt) keeps the 8 s window open. */
    public void touch(long now) { lastCombatAt = now; }

    /** Lazily applies the 8 s out-of-combat clear; @return true when it cleared a non-zero counter */
    public boolean expire(long now) {
        if (now - lastCombatAt > EmberSetRules.COMBAT_TIMEOUT_MS && counter != 0) {
            counter = 0;
            return true;
        }
        return false;
    }

    public long burstCdRemaining(long now) { return Math.max(0, burstCdUntil - now); }
    public long sustainCdRemaining(long now) { return Math.max(0, sustainCdUntil - now); }

    /** Reconnect: monotonic remaining durations saved at quit are restored relative to the new clock. */
    public void restoreCooldowns(long burstRemain, long sustainRemain, long now) {
        if (burstRemain > 0) burstCdUntil = Math.max(burstCdUntil, now + Math.min(burstRemain, EmberSetRules.BURST_ICD_MS));
        if (sustainRemain > 0) sustainCdUntil = Math.max(sustainCdUntil, now + Math.min(sustainRemain, EmberSetRules.SUSTAIN_ICD_MS));
    }

    public int every() {
        if ("scorch".equals(family)) return EmberSetRules.SCORCH_EVERY;
        if ("burst".equals(family)) return Math.max(2, EmberSetRules.BURST_EVERY + tune.burstEveryDelta);
        if ("sustain".equals(family)) return Math.max(2, EmberSetRules.SUSTAIN_EVERY + tune.sustainEveryDelta);
        return 0;
    }

    /** the interval at this HP fraction (D174 L12: below tune.sustainLow the 炽愈 interval moves by sustainLowEvery instead; p1sim sustain_every) */
    public int everyAt(double hpFrac) {
        if ("sustain".equals(family) && tune.sustainLow > 0 && hpFrac < tune.sustainLow)
            return Math.max(2, EmberSetRules.SUSTAIN_EVERY + tune.sustainLowEvery);
        return every();
    }

    /**
     * Counts a hit if it is a valid direct main-target melee (§4.4) and returns the trigger it causes.
     * @param b     current base hit B (for 烬爆 / 焚烬 snapshot)
     * @param maxHp current effective max HP (for 炽愈)
     */
    public Outcome onHit(Hit h, long now, double b, double maxHp) {
        if (h == null) return Outcome.no("null", counter);
        if (h.kind != Kind.MELEE_MAIN) return Outcome.no("source " + h.kind, counter);
        if (h.cancelled) return Outcome.no("cancelled", counter);
        if (!(h.finalDamage > 0) || Double.isInfinite(h.finalDamage)) return Outcome.no("final damage " + h.finalDamage, counter);
        if (!h.targetAlive || !h.targetEnemy) return Outcome.no("not a living enemy", counter);
        if (h.targetInvulnerable) return Outcome.no("invulnerable", counter);
        if (!(h.charge >= EmberSetRules.MIN_CHARGE) || h.charge > 1.0 + 1e-6) return Outcome.no("charge " + h.charge, counter);
        if (h.rootId <= 0) return Outcome.no("no server root id", counter);
        if (roots.contains(h.rootId)) return Outcome.no("root already counted", counter);
        if (!(b > 0) || Double.isInfinite(b) || !(maxHp > 0) || Double.isInfinite(maxHp)) return Outcome.no("bad B/H", counter);
        if ("none".equals(family) || every() == 0) return Outcome.no("no active set", counter);

        expire(now);
        lastCombatAt = now;
        roots.add(h.rootId);
        if (roots.size() > EmberSetRules.ROOT_MEMORY) { Iterator<Long> it = roots.iterator(); it.next(); it.remove(); }

        if ("scorch".equals(family)) {
            counter++;
            if (counter >= EmberSetRules.SCORCH_EVERY) {
                counter = 0;
                return new Outcome(true, "ignite", Trigger.IGNITE, EmberSetRules.burnCoef(awakening) * tune.burnMult * b, 0, counter);
            }
            return new Outcome(true, "count", Trigger.NONE, 0, 0, counter);
        }
        int ev = every();
        if ("burst".equals(family)) {
            counter = Math.min(ev, counter + 1);
            if (counter >= ev && now >= burstCdUntil) {
                counter = 0;
                burstCdUntil = now + EmberSetRules.BURST_ICD_MS;
                return new Outcome(true, "explode", Trigger.EXPLODE, EmberSetRules.burstCoef(awakening) * tune.burstMult * b,
                        EmberSetRules.burstRadius(awakening), counter);
            }
            return new Outcome(true, counter >= ev ? "held (icd)" : "count", Trigger.NONE, 0, 0, counter);
        }
        // sustain
        ev = everyAt(h.hpFrac);
        counter = Math.min(Math.max(ev, every()), counter + 1);
        if (counter >= ev && now >= sustainCdUntil) {
            counter = 0;
            sustainCdUntil = now + EmberSetRules.SUSTAIN_ICD_MS;
            return new Outcome(true, "heal", Trigger.HEAL, EmberSetRules.sustainPct(awakening) * tune.sustainMult * maxHp, 0, counter);
        }
        return new Outcome(true, counter >= ev ? "held (icd)" : "count", Trigger.NONE, 0, 0, counter);
    }

    /** Explosion candidate: stable entity id + distance from the main target. */
    public static final class Candidate {
        public final String id;
        public final int entityId;
        public final double distance;
        public Candidate(String id, int entityId, double distance) { this.id = id; this.entityId = entityId; this.distance = distance; }
    }

    /**
     * §4.4: up to 5 targets including the main one, within the radius, sorted by distance then entity id —
     * independent of the order the world query returned them.
     */
    public static java.util.List<Candidate> pickTargets(java.util.List<Candidate> in, double radius) {
        java.util.List<Candidate> ok = new java.util.ArrayList<Candidate>();
        for (Candidate c : in) if (c != null && c.distance >= 0 && c.distance <= radius) ok.add(c);
        java.util.Collections.sort(ok, new java.util.Comparator<Candidate>() {
            @Override public int compare(Candidate a, Candidate b) {
                int d = Double.compare(a.distance, b.distance);
                return d != 0 ? d : Integer.compare(a.entityId, b.entityId);
            }
        });
        return ok.size() > EmberSetRules.BURST_MAX_TARGETS ? new java.util.ArrayList<Candidate>(ok.subList(0, EmberSetRules.BURST_MAX_TARGETS)) : ok;
    }

    /** ActionBar text, e.g. 「烬爆 4/5 · 冷却 1.8s」; null when there is nothing worth showing. */
    public String hud(long now) {
        return hud(now, false);
    }

    /**
     * D444: when {@code forceIdle}, still render {@code family counter/every} (+ CD/burn suffixes)
     * even if the set is idle at 0/every — used for post-proc HUD handback after a short flash.
     */
    public String hud(long now, boolean forceIdle) {
        if ("none".equals(family)) return null;
        String name = EmberItemData.familyName(family);
        StringBuilder sb = new StringBuilder(name).append(' ').append(counter).append('/').append(every());
        long cd = "burst".equals(family) ? burstCdRemaining(now) : "sustain".equals(family) ? sustainCdRemaining(now) : 0;
        if (cd > 0) sb.append(" · 冷却 ").append(String.format(java.util.Locale.ROOT, "%.1f", cd / 1000.0)).append('s');
        if ("scorch".equals(family) && !burns.isEmpty()) sb.append(" · 燃烧 ").append(burns.size()).append(" 目标");
        boolean idle = counter == 0 && cd == 0 && burns.isEmpty();
        return idle && !forceIdle ? null : sb.toString();
    }
}

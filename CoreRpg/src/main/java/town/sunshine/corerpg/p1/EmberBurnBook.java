package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One player's 焚烬 burns (策划书 §4.4, C08). Pure; times are monotonic milliseconds supplied by the caller.
 * <ul>
 *   <li>One burn per target. A new burn ticks at +1s, +2s, +3s, +4s with the snapshot fixed at application.</li>
 *   <li>Re-igniting only moves the end time to now+4s: the next tick time and the snapshot stay, missed ticks
 *       are never back-filled.</li>
 *   <li>At most 5 burning targets; adding a 6th ends the one ignited (or re-ignited) longest ago.</li>
 * </ul>
 */
public final class EmberBurnBook {

    public static final class Burn {
        public final String target;
        public final double perTick;
        long nextTickAt;
        long endAt;
        long lastApplied;
        int ticksDone;
        Burn(String target, double perTick, long now) {
            this.target = target; this.perTick = perTick;
            this.nextTickAt = now + EmberSetRules.BURN_INTERVAL_MS;
            this.endAt = now;
            this.lastApplied = now;
        }
        public long nextTickAt() { return nextTickAt; }
        public long endAt() { return endAt; }
        public int ticksDone() { return ticksDone; }
    }

    public static final class Tick {
        public final String target;
        public final double amount;
        Tick(String target, double amount) { this.target = target; this.amount = amount; }
    }

    private final LinkedHashMap<String, Burn> burns = new LinkedHashMap<String, Burn>();
    /** D141: ticks per burn (book 4; a talent may add some) */
    private int ticks = EmberSetRules.BURN_TICKS;
    public void setTicks(int t) { ticks = Math.max(1, t); }
    public int ticks() { return ticks; }

    /**
     * D141 燎原: the burn of a target that died moves to {@code to} with what was left of it (same snapshot, at most
     * {@code maxMs} of it; a target that is already burning keeps its own burn). @return true when it moved
     */
    public boolean transfer(String from, String to, long now, long maxMs) {
        Burn b = from == null ? null : burns.remove(from);
        if (b == null || to == null || burns.containsKey(to) || b.endAt <= now) return false;
        Burn n = new Burn(to, b.perTick, now);
        n.endAt = Math.min(b.endAt, now + Math.max(0, maxMs));
        n.nextTickAt = Math.min(b.nextTickAt, b.endAt);
        burns.put(to, n);
        return true;
    }

    /** @return the target evicted to make room, or null */
    public String ignite(String target, double perTick, long now) {
        if (target == null || !(perTick > 0) || Double.isInfinite(perTick)) return null;
        Burn b = burns.get(target);
        if (b != null) {
            b.endAt = now + ticks * EmberSetRules.BURN_INTERVAL_MS; // only the end moves
            b.lastApplied = now;
            return null;
        }
        String evicted = null;
        if (burns.size() >= EmberSetRules.BURN_MAX_TARGETS) {
            Burn oldest = null;
            for (Burn x : burns.values()) if (oldest == null || x.lastApplied < oldest.lastApplied) oldest = x;
            if (oldest != null) { burns.remove(oldest.target); evicted = oldest.target; }
        }
        Burn nb = new Burn(target, perTick, now);
        nb.endAt = now + ticks * EmberSetRules.BURN_INTERVAL_MS;
        burns.put(target, nb);
        return evicted;
    }

    /**
     * Ticks due at {@code now}: at most one per burn per call (no catch-up burst after a lag spike);
     * finished burns are dropped.
     */
    public List<Tick> due(long now) {
        List<Tick> out = new ArrayList<Tick>();
        Iterator<Burn> it = burns.values().iterator();
        while (it.hasNext()) {
            Burn b = it.next();
            if (b.nextTickAt <= now && b.nextTickAt <= b.endAt) {
                out.add(new Tick(b.target, b.perTick));
                b.ticksDone++;
                b.nextTickAt += EmberSetRules.BURN_INTERVAL_MS;
            }
            if (b.nextTickAt > b.endAt) it.remove();
        }
        return out;
    }

    public void remove(String target) { burns.remove(target); }
    public void clear() { burns.clear(); }
    public int size() { return burns.size(); }
    public boolean isEmpty() { return burns.isEmpty(); }
    public Burn get(String target) { return burns.get(target); }
    public Map<String, Burn> view() { return java.util.Collections.unmodifiableMap(burns); }
}

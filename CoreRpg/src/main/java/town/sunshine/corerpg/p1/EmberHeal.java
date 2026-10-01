package town.sunshine.corerpg.p1;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * HealLedger: the only way HP goes up for a P1 player (EntityRegainHealthEvent is cancelled in P1 worlds).
 * Uses setHealth (no event) and writes a trace line with the registered source tag.
 * <p>B2.144 guard: some heals never fire EntityRegainHealthEvent (MythicMobs {@code heal{}} and any plugin that
 * calls setHealth directly). A 1-tick guard remembers the last HP each P1 player was left at by damage or by this
 * ledger and reverts any unregistered rise (D09/D10).
 */
public final class EmberHeal {

    private EmberHeal() {}

    public static double maxHp(Player p) {
        AttributeInstance ai = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        return ai == null ? 20.0 : ai.getValue();
    }

    /** @return HP actually restored */
    public static double heal(Player p, double amount, String tag) {
        if (p == null || p.isDead() || !(amount > 0)) return 0;
        double max = maxHp(p);
        double before = p.getHealth();
        double after = Math.min(max, before + amount);
        if (after <= before) return 0;
        p.setHealth(after);
        GUARD.put(p.getUniqueId(), after);
        EmberDamageTrace.heal(p, String.format(Locale.ROOT, "%s +%.2f (%.2f → %.2f / %.2f)", tag, after - before, before, after, max));
        return after - before;
    }

    public static double full(Player p, String tag) { return heal(p, maxHp(p), tag); }

    // ------------------------------------------------------------------ B2.144 direct-setHealth guard

    private static final Map<UUID, Double> GUARD = new ConcurrentHashMap<UUID, Double>();

    /** Accept the current HP as legitimate (after a sanctioned setHealth outside {@link #heal}). */
    public static void rebase(Player p) { if (p != null) GUARD.put(p.getUniqueId(), p.getHealth()); }

    public static void forget(UUID id) { if (id != null) GUARD.remove(id); }

    /**
     * Called every tick for each online player. Non-P1 / dead players are dropped from the guard, so the first P1
     * tick after a world change, respawn or mode toggle takes the current HP as the baseline.
     * @return HP reverted (0 when nothing happened)
     */
    public static double guardTick(Player p, boolean p1) {
        UUID id = p.getUniqueId();
        if (!p1 || p.isDead()) { GUARD.remove(id); return 0; }
        double cur = p.getHealth();
        Double last = GUARD.get(id);
        if (last == null || cur <= last + 1e-6) { GUARD.put(id, cur); return 0; }
        double back = Math.max(0.5, Math.min(last, maxHp(p)));
        if (back >= cur) { GUARD.put(id, cur); return 0; }
        p.setHealth(back);
        GUARD.put(id, back);
        EmberDamageTrace.heal(p, String.format(Locale.ROOT, "未登记回复（直接改血）+%.2f → 回退 (%.2f → %.2f)", cur - back, cur, back));
        return cur - back;
    }
}

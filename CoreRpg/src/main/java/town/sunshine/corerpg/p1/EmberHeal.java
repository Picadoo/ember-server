package town.sunshine.corerpg.p1;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * HealLedger: the only way HP goes up for a P1 player (EntityRegainHealthEvent is cancelled in P1 worlds).
 * Uses setHealth (no event) and writes a trace line with the registered source tag.
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
        EmberDamageTrace.heal(p, String.format(Locale.ROOT, "%s +%.2f (%.2f → %.2f / %.2f)", tag, after - before, before, after, max));
        return after - before;
    }

    public static double full(Player p, String tag) { return heal(p, maxHp(p), tag); }
}

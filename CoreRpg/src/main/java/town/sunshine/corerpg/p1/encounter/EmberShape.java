package town.sunshine.corerpg.p1.encounter;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import town.sunshine.corerpg.p1.EmberRunMaps;

/**
 * D241: telegraph-shape hit test (moved verbatim from {@code EmberRunDirector.inShape}, which now delegates here) so
 * affix primitives and boss moves share one geometry. Book: the real area never exceeds the drawn warning.
 */
public final class EmberShape {

    private EmberShape() { }

    /** Is {@code p} inside {@code sk} cast from {@code o} facing {@code dir}? (±2.5 blocks of height) */
    public static boolean inShape(EmberRunMaps.Skill sk, Location o, Vector dir, Location p) {
        if (Math.abs(p.getY() - o.getY()) > 2.5) return false;
        double dx = p.getX() - o.getX(), dz = p.getZ() - o.getZ();
        switch (sk.type) {
            case "circle": {
                double cx = dir.getX() * sk.ahead, cz = dir.getZ() * sk.ahead;
                double ex = dx - cx, ez = dz - cz;
                return ex * ex + ez * ez <= sk.radius * sk.radius;
            }
            case "line":
            case "charge": {
                double along = dx * dir.getX() + dz * dir.getZ();
                double perp = Math.abs(-dx * dir.getZ() + dz * dir.getX());
                return along >= sk.stripFrom() && along <= sk.stripTo() && perp <= sk.width / 2.0;
            }
            default: { // cone
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > sk.range) return false;
                if (dist < 0.6) return true;
                double cos = (dx * dir.getX() + dz * dir.getZ()) / dist;
                return cos >= Math.cos(Math.toRadians(sk.angle / 2.0));
            }
        }
    }
}

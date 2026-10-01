package town.sunshine.corerpg.p1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * B2.167: run boss skill hits and boss melee must not add vanilla knockback on top of the P1 push (book ch.15
 * 径向冲击「水平击退最多 1 格」, 横扫「可轻推」; §I「击退玩家不能把玩家送过外墙或下层」). Measured on 1.20.2: a 横扫
 * moved the player 0.5 (P1 push) + ~1.85 blocks with a 0.96 hop from the vanilla knockback of {@code damage(dmg, src)}.
 * A hit marks the player; the next PlayerVelocityEvent inside the window is cancelled.
 */
final class KnockbackGuard {
    static final long WINDOW_MS = 150L;
    private final Map<UUID, Long> until = new HashMap<UUID, Long>();

    void mark(UUID p, long now) { until.put(p, now + WINDOW_MS); }

    /** true = cancel this velocity change (consumes the mark) */
    boolean consume(UUID p, long now) {
        Long u = until.remove(p);
        return u != null && now <= u;
    }

    void prune(long now) {
        for (Iterator<Long> it = until.values().iterator(); it.hasNext(); ) if (it.next() < now) it.remove();
    }

    int size() { return until.size(); }
}

package town.sunshine.corerpg.p1;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D162 test hook (review A01–A04 fault injection). Only when the server was started with {@code CORERPG_TEST_FAULTS=1};
 * otherwise {@link #fire} is always false and {@code /corerpg p1 fault} refuses. One armed point per player, consumed by
 * the next operation that reaches it:
 * <ul>
 *   <li>{@code before_commit} — the next P1 store transaction (or auto-stash) throws just before COMMIT → rolled back</li>
 *   <li>{@code after_commit} — the next transaction commits, its main-thread callback runs 10 s late: disconnect in that
 *   window = the player left between COMMIT and the callback; kill -9 in that window = crash after commit</li>
 *   <li>{@code after_deliver} — the next delivery is applied and saved, its DB ack is skipped (= crash before ack)</li>
 *   <li>{@code after_pay} — D172: the next durable payment ({@link EmberPay}) is taken and saved, then the transaction
 *   that settles it starts 10 s late: kill -9 in that window = crash between payment and commit (→ refund at join);
 *   disconnect in that window = the player left with the payment taken (→ the op still commits or is refunded)</li>
 * </ul>
 */
public final class EmberFaults {
    public static final String BEFORE_COMMIT = "before_commit", AFTER_COMMIT = "after_commit", AFTER_DELIVER = "after_deliver", AFTER_PAY = "after_pay";
    private static final Map<UUID, String> armed = new ConcurrentHashMap<UUID, String>();

    private EmberFaults() {}

    public static boolean enabled() { return "1".equals(System.getenv("CORERPG_TEST_FAULTS")); }

    public static boolean arm(UUID id, String point) {
        if (!enabled() || id == null) return false;
        if (point == null || "clear".equals(point)) { armed.remove(id); return true; }
        if (!BEFORE_COMMIT.equals(point) && !AFTER_COMMIT.equals(point) && !AFTER_DELIVER.equals(point) && !AFTER_PAY.equals(point)) return false;
        armed.put(id, point);
        return true;
    }

    public static String armedFor(UUID id) { return id == null ? null : armed.get(id); }

    /** true once when {@code point} is armed for {@code id} (and disarms it) */
    public static boolean fire(UUID id, String point) {
        if (id == null || armed.isEmpty() || !enabled()) return false;
        return armed.remove(id, point);
    }
}

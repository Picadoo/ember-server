package town.sunshine.corerpg.p1;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/**
 * D455: signature playstyle feel — ActionBar confirm when a worn signature's discrete
 * behaviour fires, plus run-start identity line. Zero power / drop / ALTS change.
 */
public final class EmberSigFeel {

    /** Debounce same verb per player (ms). */
    public static final long DEBOUNCE_MS = 800L;

    private static final Map<UUID, long[]> LAST = new ConcurrentHashMap<UUID, long[]>(); // {verbHash, atMs}

    private EmberSigFeel() {}

    /** First active Def whose mods contain {@code key} with non-zero value. */
    public static EmberSignature.Def owner(List<EmberSignature.Def> active, String key) {
        if (active == null || key == null) return null;
        for (EmberSignature.Def d : active) {
            if (d == null || d.mods == null) continue;
            Double v = d.mods.get(key);
            if (v != null && v != 0.0) return d;
        }
        return null;
    }

    public static String procLine(EmberSignature.Def d, String verb) {
        if (d == null) return "";
        String v = verb == null ? "" : verb;
        return "§d签名 · §f" + d.name + (v.isEmpty() ? "" : " §7" + v);
    }

    /** Run-start / identity: 「本局签名：A · B」 or empty. */
    public static String runLine(List<EmberSignature.Def> active) {
        if (active == null || active.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("§d本局签名：");
        for (int i = 0; i < active.size(); i++) {
            EmberSignature.Def d = active.get(i);
            if (d == null) continue;
            if (i > 0) sb.append(" §7· ");
            sb.append("§f").append(d.name);
        }
        return sb.toString();
    }

    /** @return true if allowed to flash this verb now (and records the attempt). */
    public static boolean allow(UUID id, String verb, long nowMs) {
        if (id == null || verb == null) return false;
        int h = verb.hashCode();
        long[] prev = LAST.get(id);
        if (prev != null && prev[0] == h && nowMs - prev[1] < DEBOUNCE_MS) return false;
        LAST.put(id, new long[]{h, nowMs});
        return true;
    }

    public static void clear(UUID id) { if (id != null) LAST.remove(id); }

    /** Pure: whether debounce would allow (does not mutate). For tests. */
    public static boolean wouldAllow(long lastAt, long nowMs) {
        return nowMs - lastAt >= DEBOUNCE_MS;
    }

    static String debugOwnerId(List<EmberSignature.Def> active, String key) {
        EmberSignature.Def d = owner(active, key);
        return d == null ? "-" : d.id;
    }

    /**
     * D12289: hub/gear one-line signature feel (display only).
     * Priority: active names → worn-but-off/inactive → chase remain → empty hint.
     * Only Def.name (+ optional 改打法·不加伤 half); no mods / stamp / new codes.
     */
    public static String menuLine(List<EmberSignature.Def> active, EmberSignature.Def chaseDef, int chaseMarks) {
        return menuLine(active, null, chaseDef, chaseMarks, null);
    }

    public static String menuLine(List<EmberSignature.Def> active, List<EmberSignature.Def> wornOff,
                                  EmberSignature.Def chaseDef, int chaseMarks) {
        return menuLine(active, wornOff, chaseDef, chaseMarks, null);
    }

    /**
     * D12734 / TIP-AO: full-tier chase branch uses {@link EmberSigChase#readyTail};
     * active / wornOff / under-mark chase unchanged.
     */
    public static String menuLine(List<EmberSignature.Def> active, List<EmberSignature.Def> wornOff,
                                  EmberSignature.Def chaseDef, int chaseMarks, EmberLoadout lo) {
        return menuLine(active, wornOff, chaseDef, chaseMarks, lo, EmberSignature.imprintMarks());
    }

    /**
     * D12830 / TIP-AU: chase branch may use soft imprint need (caller peeks ImprintMarkSoft).
     */
    public static String menuLine(List<EmberSignature.Def> active, List<EmberSignature.Def> wornOff,
                                  EmberSignature.Def chaseDef, int chaseMarks, EmberLoadout lo, int need) {
        if (active != null && !active.isEmpty()) {
            StringBuilder sb = new StringBuilder("§d签名 ");
            int n = 0;
            for (EmberSignature.Def d : active) {
                if (d == null || d.name == null) continue;
                if (n > 0) sb.append("§7·");
                sb.append("§f").append(d.name);
                n++;
                if (n >= 2) break;
            }
            if (n == 0) {
                // fall through
            } else {
                return sb.append(" §8改打法·不加伤").toString();
            }
        }
        if (wornOff != null && !wornOff.isEmpty()) {
            for (EmberSignature.Def d : wornOff) {
                if (d == null || d.name == null) continue;
                return "§c签名关闭 §7· §f" + d.name + " §8点页重开";
            }
        }
        if (chaseDef != null) {
            int n = need > 0 ? need : EmberSignature.imprintMarks();
            int m = Math.max(0, chaseMarks);
            String nm = chaseDef.name == null ? chaseDef.id : chaseDef.name;
            if (m >= n) {
                return "§e追烙就绪 §f" + chaseDef.id + " " + nm + " §7· " + EmberSigChase.readyTail(lo, chaseDef);
            }
            return "§e追烙 §f" + chaseDef.id + " " + nm + " §7· 徽记 " + m + "/" + n;
        }
        return "§8无签名 · 点左开页 · 右选追烙";
    }
}

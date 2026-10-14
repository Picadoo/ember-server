package town.sunshine.corerpg.p1;

import java.util.List;
import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-CY D13330 · 短征花样结算回看记忆（short_var_*）。
 * <p>settle 时从 {@link EmberRunSession} 取本局 {@code affix}/{@code eventKind}（空=无），
 * 写入 period 索引仓（对齐 {@link EmberShortSettle} int 编码）；PAPI 供结算薄页 V 读。
 * <b>零发</b> event_core / affix_shard / NI；<b>零改</b> rate / variety yaml / ShortSettle map 语义。
 * 纯逻辑，无 Bukkit。
 */
public final class EmberShortVarietySettle {

    /** period counter: 1..KNOWN.size → affix id；0 / absent → none */
    public static final String C_AFFIX = "p1_short_var_affix";
    /** period counter: 1..EVENTS.size → event kind；0 / absent → none */
    public static final String C_EVENT = "p1_short_var_event";

    public static final String LINE_NONE = "本局无花样";

    private EmberShortVarietySettle() {}

    /** 1-based index into {@link EmberRunMaps.Variety#KNOWN}, or 0. */
    public static int indexOfAffix(String id) {
        if (id == null || id.isEmpty()) return 0;
        String k = id.toLowerCase(Locale.ROOT).trim();
        if (k.isEmpty()) return 0;
        List<String> known = EmberRunMaps.Variety.KNOWN;
        for (int i = 0; i < known.size(); i++) {
            if (known.get(i).equals(k)) return i + 1;
        }
        return 0;
    }

    /** 1-based index into {@link EmberRunMaps.Variety#EVENTS}, or 0. */
    public static int indexOfEvent(String id) {
        if (id == null || id.isEmpty()) return 0;
        String k = id.toLowerCase(Locale.ROOT).trim();
        if (k.isEmpty()) return 0;
        List<String> ev = EmberRunMaps.Variety.EVENTS;
        for (int i = 0; i < ev.size(); i++) {
            if (ev.get(i).equals(k)) return i + 1;
        }
        return 0;
    }

    public static String affixOf(int index) {
        List<String> known = EmberRunMaps.Variety.KNOWN;
        if (index < 1 || index > known.size()) return "";
        return known.get(index - 1);
    }

    public static String eventOf(int index) {
        List<String> ev = EmberRunMaps.Variety.EVENTS;
        if (index < 1 || index > ev.size()) return "";
        return ev.get(index - 1);
    }

    /** Overwrite both slots from session (null/empty → clear). Always covers prior settle. */
    public static boolean remember(PlayerData d, EmberRunSession s) {
        if (d == null) return false;
        String a = s == null || s.affix == null ? "" : s.affix.trim();
        String e = s == null || s.eventKind == null ? "" : s.eventKind.trim();
        return remember(d, a, e);
    }

    /** Test/helper: remember raw ids (trim; unknown known-pool miss → stored as none). */
    public static boolean remember(PlayerData d, String affix, String eventKind) {
        if (d == null) return false;
        String a = affix == null ? "" : affix.trim();
        String e = eventKind == null ? "" : eventKind.trim();
        write(d, C_AFFIX, indexOfAffix(a));
        write(d, C_EVENT, indexOfEvent(e));
        return true;
    }

    private static void write(PlayerData d, String name, int idx) {
        int cur = d.periodCount(name, "all");
        if (cur == idx) return;
        d.addPeriodCount(name, "all", idx - cur);
    }

    public static String affixId(PlayerData d) {
        if (d == null) return "";
        return affixOf(d.periodCount(C_AFFIX, "all"));
    }

    public static String eventId(PlayerData d) {
        if (d == null) return "";
        return eventOf(d.periodCount(C_EVENT, "all"));
    }

    /** affix or event non-empty → true. */
    public static boolean hit(PlayerData d) {
        return !affixId(d).isEmpty() || !eventId(d).isEmpty();
    }

    /** TrMenu bit: hit → {@code "1"}, else {@code "0"}. */
    public static String hitBit(PlayerData d) {
        return hit(d) ? "1" : "0";
    }

    /**
     * Chinese affix label via {@link EmberRunMaps.Variety#label}; empty → {@code ""};
     * unknown id falls back to id (Variety.label already does).
     */
    public static String affixLabel(PlayerData d) {
        String id = affixId(d);
        if (id.isEmpty()) return "";
        String lab = EmberRunMaps.Variety.label(id);
        return lab == null || lab.isEmpty() ? id : lab;
    }

    /**
     * Chinese event label via {@link EmberRunMaps.Variety#eventLabel}; empty → {@code ""}.
     * Does not call eventLabel on empty (that defaults to 限时清房).
     */
    public static String eventLabel(PlayerData d) {
        String id = eventId(d);
        if (id.isEmpty()) return "";
        String lab = EmberRunMaps.Variety.eventLabel(id);
        return lab == null || lab.isEmpty() ? id : lab;
    }

    /**
     * hit → join non-empty labels with {@code " / "}; no hit → {@link #LINE_NONE}.
     */
    public static String line(PlayerData d) {
        if (!hit(d)) return LINE_NONE;
        String a = affixLabel(d);
        String e = eventLabel(d);
        if (!a.isEmpty() && !e.isEmpty()) return a + " / " + e;
        return !a.isEmpty() ? a : e;
    }
}

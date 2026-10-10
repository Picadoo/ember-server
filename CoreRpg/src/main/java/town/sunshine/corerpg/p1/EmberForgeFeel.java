package town.sunshine.corerpg.p1;

import java.util.Locale;

import org.bukkit.entity.Player;

/**
 * D475: forge result ActionBar feel (enhance hit/miss, refine, quality, upgrade).
 * Zero enhance rates / costs / AFK change.
 */
public final class EmberForgeFeel {

    private EmberForgeFeel() {}

    /** Compact ActionBar line from forge kind + commit note; empty = skip. */
    public static String line(String kind, String note) {
        if (note == null || note.isEmpty()) return "";
        String k = kind == null ? "" : kind.toLowerCase(Locale.ROOT);
        if ("enhance".equals(k)) {
            if (note.startsWith("强化成功") || note.contains("强化成功"))
                return "§a工坊 · §f强化成功 §7· " + trimNote(note);
            if (note.startsWith("强化失败") || note.contains("强化失败"))
                return "§e工坊 · §f强化失败 §7· " + trimNote(note);
            return "§6工坊 · §f强化 §7· " + trimNote(note);
        }
        if ("refine".equals(k) || "craft".equals(k))
            return "§a工坊 · §f精工 §7· " + trimNote(note);
        if ("quality".equals(k))
            return "§a工坊 · §f成色 §7· " + trimNote(note);
        if ("upgrade".equals(k))
            return "§a工坊 · §f升阶 §7· " + trimNote(note);
        return "";
    }

    static String trimNote(String note) {
        String n = note;
        // strip leading kind words already in prefix
        if (n.startsWith("强化成功 ")) n = n.substring(5).trim();
        else if (n.startsWith("强化失败，")) n = n.substring(5).trim();
        else if (n.startsWith("强化失败")) n = n.substring(4).trim();
        if (n.length() > 40) n = n.substring(0, 40) + "…";
        return n;
    }

    public static void flash(Player p, String kind, String note) {
        if (p == null) return;
        String msg = line(kind, note);
        if (msg.isEmpty()) return;
        try { p.sendActionBar(msg); } catch (Throwable ignored) { }
    }
}

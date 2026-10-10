package town.sunshine.corerpg.p1;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * D477: map-enter ActionBar cue when a run commits (map quality feel).
 * Zero stamina / fee / drop / AFK change.
 */
public final class EmberMapEnterFeel {

    private EmberMapEnterFeel() {}

    /** Mode tag for ActionBar (Bukkit-free flags). */
    public static String modeTag(boolean shortExpedition, boolean rush, String rushMode,
                                 boolean raid, boolean challenge, int abyss) {
        if (shortExpedition) return "短征";
        if (rush) {
            if ("echo".equals(rushMode)) return "残响";
            if ("outpost".equals(rushMode)) return "前哨";
            return "连战";
        }
        if (raid) return "团本";
        if (abyss > 0) return "深渊" + abyss;
        if (challenge) return "挑战";
        return "主线";
    }

    public static String modeTag(EmberRunMaps.MapDef m, boolean challenge, int abyss) {
        if (m == null) return modeTag(false, false, null, false, challenge, abyss);
        return modeTag(m.shortExpedition, m.rush, m.rushMode, m.raid, challenge, abyss);
    }

    public static String line(String mapName, String mode) {
        String name = mapName == null || mapName.isEmpty() ? "副本" : mapName;
        String tag = mode == null || mode.isEmpty() ? "副本" : mode;
        return "§a入场 · §f" + name + " §7· " + tag;
    }

    public static String line(EmberRunMaps.MapDef m, boolean challenge, int abyss) {
        String name = "副本";
        if (m != null) {
            if (m.name != null && !m.name.isEmpty()) name = m.name;
            else if (m.key != null && !m.key.isEmpty()) name = m.key;
        }
        return line(name, modeTag(m, challenge, abyss));
    }

    public static void flash(Player p, EmberRunMaps.MapDef m, EmberRunSession s) {
        if (p == null) return;
        boolean ch = s != null && s.challenge;
        int ab = s == null ? 0 : s.abyss;
        String msg = line(m, ch, ab);
        try { p.sendActionBar(msg); } catch (Throwable ignored) { }
        try {
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 1.2f);
        } catch (Throwable ignored) { }
    }

    /** Admin / unit path: flash a prebuilt line. */
    public static void flashLine(Player p, String msg) {
        if (p == null || msg == null || msg.isEmpty()) return;
        try { p.sendActionBar(msg); } catch (Throwable ignored) { }
        try {
            p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 1.2f);
        } catch (Throwable ignored) { }
    }
}

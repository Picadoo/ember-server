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
        flash(p, m, s, null);
    }

    /** D1397: optional PlayerData → append map-identity nextMissing（不锁通关）. */
    public static void flash(Player p, EmberRunMaps.MapDef m, EmberRunSession s, town.sunshine.corerpg.PlayerData pd) {
        if (p == null) return;
        // D2279: Q04 first enter unlocks 烬斩符文 (non-clear-gate)
        if (pd != null && m != null && "q04".equalsIgnoreCase(m.key)) {
            if (EmberSkillKit.markShapeEnter(pd)) {
                try {
                    p.sendMessage(EmberRunService.P + "§a烬斩符文已解锁 · §f扇/直/环 §7（首进 Q04 · 不锁通关 · /corerpg skill shape）");
                    p.sendActionBar("§a符文解锁 · §f/corerpg skill shape");
                } catch (Throwable ignored) { }
            }
        }
        boolean ch = s != null && s.challenge;
        int ab = s == null ? 0 : s.abyss;
        String msg = line(m, ch, ab);
        if (m != null && m.lootFamily != null && !m.lootFamily.isEmpty() && ab == 0 && !ch
                && (s == null || (!s.challenge && s.abyss == 0))) {
            // D1816 EM1-A: short miss/齐 on AB; full methodLine once in chat (display only)
            String meth = EmberMapIdentity.methodLine(m, pd);
            String miss = EmberMapIdentity.nextMissing(m, pd);
            if (miss != null && !miss.isEmpty() && !miss.contains("已齐"))
                msg = msg + " §8· §f" + miss;
            else if (meth != null && (meth.contains("身份齐") || meth.contains("齐·")))
                msg = msg + " §a· 身份齐";
            if (meth != null && !meth.isEmpty()) {
                try { p.sendMessage(EmberRunService.P + "§7" + meth); } catch (Throwable ignored) { }
            }
            // D1999 SC3: chase map match → remain half-line (non-hope)
            if (pd != null && m.key != null && EmberSigChase.chaseMap(pd, m.key)) {
                String cg = EmberSigChase.statusGlance(pd);
                if (cg != null && !cg.isEmpty()) {
                    try { p.sendMessage(EmberRunService.P + "§e" + cg); } catch (Throwable ignored) { }
                    msg = msg + " §8· §e追烙";
                }
            }
        }
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

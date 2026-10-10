package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

import town.sunshine.corerpg.CoreRpgPlugin;

/**
 * D489: map-enter chat cue — 名片 + loot identity (map quality feel).
 * Chat only — not ActionBar HUD family. Zero stamina / fee / drop / AFK / sx change.
 * Complements D477 mode ActionBar without stacking another ready-style bar.
 */
public final class EmberMapCardCue {

    private EmberMapCardCue() {}

    /** Bukkit-free chat line. */
    public static String chatLine(String mapName, String mode, String hint, String lootLabel) {
        String name = mapName == null || mapName.isEmpty() ? "副本" : mapName;
        String tag = mode == null || mode.isEmpty() ? "副本" : mode;
        String h = hint == null || hint.isEmpty() ? "（无名片）" : hint;
        String L = lootLabel == null || lootLabel.isEmpty() ? "不偏向" : lootLabel;
        return "§e入场名片 · §f" + name + " §7· " + tag + " §8｜ §d" + h + " §8· 掉落§7" + L;
    }

    public static String chatLine(EmberRunMaps.MapDef m, boolean challenge, int abyss) {
        String name = "副本";
        String hint = "";
        String loot = "不偏向";
        if (m != null) {
            if (m.name != null && !m.name.isEmpty()) name = m.name;
            else if (m.key != null && !m.key.isEmpty()) name = m.key;
            if (m.hint != null) hint = m.hint;
            loot = EmberRunMaps.lootLabel(m);
        }
        return chatLine(name, EmberMapEnterFeel.modeTag(m, challenge, abyss), hint, loot);
    }

    /** After verifyEntry commit — chat map identity (no ActionBar). */
    public static void cue(Player p, EmberRunMaps.MapDef m, EmberRunSession s) {
        if (p == null) return;
        boolean ch = s != null && s.challenge;
        int ab = s == null ? 0 : s.abyss;
        String line = chatLine(m, ch, ab);
        // D1466: append nextMissing when identity incomplete（chat · 非 PAPI）
        if (m != null && ab == 0 && !ch) {
            try {
                CoreRpgPlugin pl = (CoreRpgPlugin) org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
                if (pl != null && pl.getDataStore() != null) {
                    town.sunshine.corerpg.PlayerData pd = pl.getDataStore().get(p.getUniqueId());
                    String miss = EmberMapIdentity.nextMissing(m, pd);
                    if (miss != null && !miss.isEmpty() && !miss.contains("已齐"))
                        line = line + " §8· §f" + miss;
                }
            } catch (Throwable ignored) { }
        }
        p.sendMessage(EmberRunService.P + line);
        // D1829 EM1-A
        try {
            CoreRpgPlugin pl = (CoreRpgPlugin) org.bukkit.Bukkit.getPluginManager().getPlugin("CoreRpg");
            if (pl != null && pl.getDataStore() != null && m != null) {
                town.sunshine.corerpg.PlayerData pd = pl.getDataStore().get(p.getUniqueId());
                String meth = EmberMapIdentity.methodLine(m, pd);
                if (meth != null && !meth.isEmpty())
                    p.sendMessage(EmberRunService.P + "§7" + meth); // chat only — MapCardCue bans AB HUD
            }
        } catch (Throwable ignored) { }
    }

    /** Admin smoke: cue from map key flags. */
    public static void cueSample(Player p, EmberRunMaps.MapDef m, boolean challenge, int abyss) {
        if (p == null) return;
        p.sendMessage(EmberRunService.P + chatLine(m, challenge, abyss));
    }

    public static String followHint() {
        return "入场名片";
    }
}

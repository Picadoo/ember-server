package town.sunshine.corerpg.p1;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import town.sunshine.corerpg.ConfirmTokens;
import town.sunshine.corerpg.CoreRpgPlugin;
import town.sunshine.corerpg.PlayerData;

/**
 * D679: map gear farm loop — one chat line naming the next chase piece vs worn.
 * Not a PAPI hope-meter twin. Zero drop rate / clear-gate change.
 */
public final class EmberMapFarmCue {

    private EmberMapFarmCue() {}

    /**
     * Bukkit-free: next target description.
     * @param mapFam map loot family (may be null)
     * @param mapSlot map loot slot blade|charm|null
     * @param wornFam worn piece family on that slot (or any worn if slot null)
     * @param wornSlot worn slot name for display
     */
    public static String nextLine(String mapFam, String mapSlot, String wornFam, String wornSlot,
                                  int wornTier, int wornEnhance) {
        if (mapFam == null || !EmberRunRules.validFamily(mapFam)) {
            return "§8下一件追：本图无固定族偏向 · 每局仍必掉 1 装";
        }
        String famName = EmberItemData.familyName(mapFam);
        String slotName = mapSlot == null ? "刃/护符"
                : ("charm".equals(mapSlot) ? "护符" : "刃");
        String chase = famName + " · " + slotName;
        if (wornFam == null || wornFam.isEmpty() || "none".equals(wornFam)) {
            return "§e下一件追：§f" + chase + " §7· 现穿空 · 刷同族成套";
        }
        String wornName = EmberItemData.familyName(wornFam);
        String ws = wornSlot == null ? "" : wornSlot;
        String wear = wornName + (ws.isEmpty() ? "" : "·" + ws)
                + (wornTier > 0 ? " T" + wornTier : "")
                + (wornEnhance > 0 ? " +" + wornEnhance : "");
        if (mapFam.equals(wornFam) && (mapSlot == null || mapSlot.equals(slotKey(wornSlot)))) {
            return "§e下一件追：§f" + chase + " §a· 现穿同族同部位 §7" + wear + " · 差装可分解";
        }
        if (mapFam.equals(wornFam)) {
            return "§e下一件追：§f" + chase + " §7· 现穿同族异位 §8" + wear;
        }
        return "§e下一件追：§f" + chase + " §7· 现穿 §c" + wear + " §8→ 刷本图换同族";
    }

    static String slotKey(String slotName) {
        if (slotName == null) return null;
        if (slotName.contains("护符") || "charm".equals(slotName)) return "charm";
        if (slotName.contains("刃") || "blade".equals(slotName)) return "blade";
        return null;
    }

    /** D682: settle delivery matched map family — chase hit (no button twin). */
    public static String hitLine(String mapFam, String dropFam, String dropSlot) {
        if (mapFam == null || dropFam == null || !mapFam.equals(dropFam)) return null;
        String fam = EmberItemData.familyName(dropFam);
        String slot = dropSlot == null ? "装" : EmberItemData.slotName(dropSlot);
        return "§a图装追到：§f" + fam + " · " + slot + " §7· 本图偏向命中";
    }

    /**
     * D687: after settle deliver — refresh chase vs worn (may have changed).
     * Quiet if map has no identity; no button twin.
     */
    public static void afterSettle(Player p, EmberRunMaps.MapDef m, EmberLoadout lo) {
        if (p == null || m == null) return;
        if (m.lootFamily == null || !EmberRunRules.validFamily(m.lootFamily)) return;
        PlayerData pd = dataOf(p);
        if (pd != null) { remember(pd, m); flush(p); } // D763/D765
        cue(p, m, lo);
    }

    public static void cue(Player p, EmberRunMaps.MapDef m, EmberLoadout lo) {
        if (p == null || m == null) return;
        String mapFam = m.lootFamily;
        String mapSlot = m.lootSlot;
        EmberItemData worn = null;
        if (lo != null) {
            if ("charm".equals(mapSlot)) worn = lo.charm;
            else if ("blade".equals(mapSlot)) worn = lo.blade;
            else worn = lo.blade != null ? lo.blade : lo.charm;
        }
        String wornFam = worn == null ? null : worn.family;
        String wornSlot = worn == null ? null : EmberItemData.slotName(worn.slot);
        int wt = worn == null ? 0 : worn.tier;
        int we = worn == null ? 0 : worn.enhance;
        String line = nextLine(mapFam, mapSlot, wornFam, wornSlot, wt, we);
        p.sendMessage(EmberRunService.P + line);
        // D704: if set focus matches map family, append set-completion gap (no button)
        PlayerData pd = dataOf(p);
        if (pd != null && lo != null && mapFam != null && EmberRunRules.validFamily(mapFam)) {
            int focus = EmberSetFocus.get(pd);
            String fk = EmberSetFocus.familyKey(focus);
            if (mapFam.equals(fk)) {
                String gap = EmberSetChase.focusGap(pd, lo);
                if (gap != null && !gap.isEmpty()) p.sendMessage(EmberRunService.P + gap);
                else if (!"none".equals(lo.activeSet) && mapFam.equals(lo.activeSet))
                    p.sendMessage(EmberRunService.P + "§7" + lo.nextAwakeningHint());
            } else if (!EmberSetFocus.valid(focus)) { // D725
                p.sendMessage(EmberRunService.P + "§7本图族可作焦点：/corerpg p1 setfocus "
                        + EmberSetFocus.parseKey(mapFam));
            }
        }
        try {
            String shortBar = mapFam != null && EmberRunRules.validFamily(mapFam)
                    ? "§e下一件 · §f" + EmberItemData.familyName(mapFam)
                    : "§e下一件 · §7本图装";
            p.sendActionBar(shortBar);
        } catch (Throwable ignored) { }
    }


    /** D763: encoded last farm map for logout resume (1=q01…7=q07; 101+=sx). */
    public static final String C_RESUME_MAP = "p1_map_farm_resume";
    public static final String C_RESUME_LAT = "p1_map_farm_resume_lat";

    static int encodeMap(String key) {
        if (key == null) return 0;
        if (key.length() == 3 && key.charAt(0) == 'q') {
            try { int n = Integer.parseInt(key.substring(1)); return n >= 1 && n <= 7 ? n : 0; }
            catch (NumberFormatException e) { return 0; }
        }
        if (key.startsWith("sx") && key.length() >= 4) {
            try { int n = Integer.parseInt(key.substring(2)); return n > 0 ? 100 + n : 0; }
            catch (NumberFormatException e) { return 0; }
        }
        return 0;
    }

    static String decodeMap(int code) {
        if (code >= 1 && code <= 7) return "q0" + code;
        if (code > 100) {
            int n = code - 100;
            return n < 10 ? "sx0" + n : "sx" + n;
        }
        return null;
    }

    /** Remember last map with loot identity (settle / enter). */
    public static void remember(PlayerData d, EmberRunMaps.MapDef m) {
        if (d == null || m == null) return;
        if (m.lootFamily == null || !EmberRunRules.validFamily(m.lootFamily)) return;
        int code = encodeMap(m.key);
        if (code <= 0) return;
        int cur = d.periodCount(C_RESUME_MAP, "all");
        if (cur == code) return;
        d.addPeriodCount(C_RESUME_MAP, "all", code - cur);
        // clear latch so next join can cue again
        int lat = d.periodCount(C_RESUME_LAT, "all");
        if (lat != 0) d.addPeriodCount(C_RESUME_LAT, "all", -lat);
    }

    /**
     * D763: after login (hub) — one resume cue for last farm map + enter button.
     * Rising edge per remember cycle (latched to map code).
     */
    public static void maybeResumeAfterJoin(Player p, PlayerData d, EmberLoadout lo, EmberRunMaps maps) {
        if (p == null || d == null || maps == null) return;
        if (maps.byWorld(p.getWorld().getName()) != null) return; // still in an instance
        int code = d.periodCount(C_RESUME_MAP, "all");
        String key = decodeMap(code);
        if (key == null) return;
        int lat = d.periodCount(C_RESUME_LAT, "all");
        if (lat == code) return;
        EmberRunMaps.MapDef m = maps.byKey(key);
        if (m == null || m.lootFamily == null || !EmberRunRules.validFamily(m.lootFamily)) return;
        d.addPeriodCount(C_RESUME_LAT, "all", code - lat);
        flush(p); // D765
        cue(p, m, lo);
        // D768: if hard-path prefers challenge, resume into challenge
        boolean chal = EmberChallengePath.get(d) == EmberChallengePath.PREFER;
        String cmd = "/corerpg p1 enter " + m.key + (chal ? " challenge" : "");
        String label = "[再进·" + m.key.toUpperCase() + (chal ? "·挑战" : "") + "]";
        ConfirmTokens.sendButton(p, EmberRunService.P + "§7登出恢复：",
                label, cmd,
                "继续刷 " + EmberItemData.familyName(m.lootFamily) + " 图装");
    }

    private static PlayerData dataOf(Player p) {
        if (p == null) return null;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (!(pl instanceof CoreRpgPlugin)) return null;
        return ((CoreRpgPlugin) pl).getDataStore().get(p.getUniqueId());
    }

    /** D765: persist resume counters. */
    private static void flush(Player p) {
        if (p == null) return;
        Plugin pl = Bukkit.getPluginManager().getPlugin("CoreRpg");
        if (pl instanceof CoreRpgPlugin)
            ((CoreRpgPlugin) pl).getDataStore().flushMutation(p.getUniqueId());
    }
}

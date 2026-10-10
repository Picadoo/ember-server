package town.sunshine.corerpg.p1;

import org.bukkit.entity.Player;

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
        try {
            String shortBar = mapFam != null && EmberRunRules.validFamily(mapFam)
                    ? "§e下一件 · §f" + EmberItemData.familyName(mapFam)
                    : "§e下一件 · §7本图装";
            p.sendActionBar(shortBar);
        } catch (Throwable ignored) { }
    }
}

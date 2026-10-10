package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * D457: set / four-piece enter-run playstyle cue. Zero set-power / set_bonus / AFK change.
 */
public final class EmberSetFeel {

    private EmberSetFeel() {}

    /** Short verb for ActionBar / chat (not the long familyBlurb). */
    public static String familyCue(String fam) {
        if ("scorch".equals(fam)) return "点燃打首领";
        if ("burst".equals(fam)) return "炸圈清群";
        if ("sustain".equals(fam)) return "回血站桩";
        return "";
    }

    /**
     * @param activeSet loadout activeSet (none / scorch / burst / sustain)
     * @param awakening 1..3
     * @param armorN    same-family armor pieces counting toward 4pc (0..2)
     * @param armorOn   four-piece active
     * @return empty if no two-piece set
     */
    public static String runLine(String activeSet, int awakening, int armorN, boolean armorOn) {
        if (activeSet == null || "none".equals(activeSet) || activeSet.isEmpty()) return "";
        String name = EmberItemData.familyName(activeSet);
        String cue = familyCue(activeSet);
        String awk = awakening >= 3 ? "III" : awakening == 2 ? "II" : "I";
        StringBuilder sb = new StringBuilder();
        sb.append("§6本局套装：§f").append(name);
        if (!cue.isEmpty()) sb.append(" §7（").append(cue).append("）");
        sb.append(" §8觉醒").append(awk);
        if (armorOn) sb.append(" §a· 四件套受伤−3%");
        else if (armorN > 0) sb.append(String.format(Locale.ROOT, " §e· 护甲四件套 %d/2", Math.min(2, armorN)));
        return sb.toString();
    }
}

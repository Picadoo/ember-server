package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * D457: set / four-piece enter-run playstyle cue. Zero set-power / set_bonus / AFK change.
 * D12394–D12397 TIP-P: awaken tier delta vernacular (read EmberSetRules; never mutate arrays).
 */
public final class EmberSetFeel {

    /** D12397: {@code set_feel.awk_delta}; default true when key / section absent. */
    private static volatile boolean awkDelta = true;

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

    /** D12397 TIP-P hot flag; default true. */
    public static boolean awkDeltaEnabled() {
        return awkDelta;
    }

    /** Apply from ember-v1.yml root (or null); absent {@code set_feel.awk_delta} → true. */
    public static void loadAwkDelta(org.bukkit.configuration.ConfigurationSection root) {
        if (root == null) {
            awkDelta = true;
            return;
        }
        org.bukkit.configuration.ConfigurationSection sf = root.getConfigurationSection("set_feel");
        if (sf == null) {
            awkDelta = true;
            return;
        }
        awkDelta = sf.getBoolean("awk_delta", true);
    }

    /** Test hook; {@code null} restores default true. */
    public static void setAwkDeltaEnabledForTest(Boolean enabled) {
        awkDelta = enabled == null || enabled.booleanValue();
    }

    private static String normalizeFamily(String family) {
        if (family == null) return "";
        if ("scorch".equals(family) || "焚烬".equals(family)) return "scorch";
        if ("burst".equals(family) || "烬爆".equals(family)) return "burst";
        if ("sustain".equals(family) || "炽愈".equals(family)) return "sustain";
        return family;
    }

    /**
     * D12394 TIP-P: plain-language awaken tier delta from EmberSetRules (read-only; never mutates arrays).
     * @param family scorch / burst / sustain (also 焚烬 / 烬爆 / 炽愈)
     * @param fromAwk previous awakening (1..3)
     * @param toAwk new awakening (1..3)
     * @return ≤1 line with color codes, or empty when no rise / unknown family
     */
    public static String awkDeltaLine(String family, int fromAwk, int toAwk) {
        String fam = normalizeFamily(family);
        if (fam.isEmpty() || "none".equals(fam)) return "";
        if (toAwk <= fromAwk) return "";
        if (toAwk < 1 || toAwk > 3) return "";
        int from = fromAwk < 1 ? 1 : Math.min(3, fromAwk);
        if ("scorch".equals(fam)) {
            if (EmberSetRules.burnCoef(toAwk) > EmberSetRules.burnCoef(from)) return "§e燃跳更烫";
            return "";
        }
        if ("burst".equals(fam)) {
            boolean thicker = EmberSetRules.burstCoef(toAwk) > EmberSetRules.burstCoef(from);
            boolean wider = EmberSetRules.burstRadius(toAwk) > EmberSetRules.burstRadius(from);
            if (!thicker && !wider) return "";
            if (thicker && wider) return "§e圈伤更厚 §7· 半径略扩";
            if (thicker) return "§e圈伤更厚";
            return "§7半径略扩";
        }
        if ("sustain".equals(fam)) {
            if (EmberSetRules.sustainPct(toAwk) > EmberSetRules.sustainPct(from)) return "§e回血略增";
            return "";
        }
        return "";
    }
}

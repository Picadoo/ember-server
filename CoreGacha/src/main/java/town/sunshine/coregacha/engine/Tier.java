package town.sunshine.coregacha.engine;

import java.util.Locale;

/** Rarity tiers, best first. {@link #code} is what menus compare (4 = 传说 … 1 = 普通). */
public enum Tier {
    LEGEND(4, "传说", "§6", "orange"), EPIC(3, "史诗", "§d", "magenta"), RARE(2, "稀有", "§b", "light_blue"), COMMON(1, "普通", "§7", "gray");

    public final int code;
    public final String label, color, glass;

    Tier(int code, String label, String color, String glass) { this.code = code; this.label = label; this.color = color; this.glass = glass; }

    public String colored() { return color + label; }

    public static Tier parse(String s) {
        if (s == null) return null;
        String k = s.trim().toLowerCase(Locale.ROOT);
        switch (k) {
            case "legend": case "legendary": case "传说": return LEGEND;
            case "epic": case "史诗": return EPIC;
            case "rare": case "稀有": return RARE;
            case "common": case "普通": return COMMON;
            default: return null;
        }
    }

    public String key() { return name().toLowerCase(Locale.ROOT); }
}

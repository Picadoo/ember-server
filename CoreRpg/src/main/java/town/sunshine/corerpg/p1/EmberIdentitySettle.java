package town.sunshine.corerpg.p1;

import java.util.Locale;

import town.sunshine.corerpg.PlayerData;

/**
 * TIP-DC D13334 · 图身份首通结算「下一环」菜单化冲钉（identity_settle_* + 入口常量）。
 * <p>firstClear + 有效 family 后开薄页 {@code /ember_p1_identity_settle}；period 记住 settle 图/族/槽；
 * PAPI 供 TrMenu 工坊/装备/冒险/图装追。
 * <b>零改</b>掉落 / 体力价 / 首通包 / 图身份语义 / 短征·团本·挑战·深渊 settle / variety / soft-reward / D794。
 * 纯逻辑，无 Bukkit。
 */
public final class EmberIdentitySettle {

    /** OpenCommand · 薄页 id 同名 */
    public static final String SETTLE_NEXT_LOOP_CMD = "/ember_p1_identity_settle";
    public static final String SETTLE_NEXT_LOOP_LABEL = "[下一环]";
    public static final String SETTLE_ADVENTURE_CMD = "/ember_p1_adventure";
    public static final String SETTLE_FORGE_CMD = "/ember_p1_forge";
    public static final String SETTLE_GEAR_CMD = "/ember_p1_gear";

    /** period counter: 1..7 → q01..q07；0 / absent → none */
    public static final String C_LAST = "p1_identity_settle_last";
    /** period counter: 1=scorch 2=burst 3=sustain；0 → none */
    public static final String C_FAM = "p1_identity_settle_fam";
    /** period counter: 1=blade 2=charm 3=head 4=chest 5=legs 6=boots；0 → none */
    public static final String C_SLOT = "p1_identity_settle_slot";

    /** Mainline maps that carry map-identity first-clear package. */
    public static final String[] MAINLINE_KEYS = {
            "q01", "q02", "q03", "q04", "q05", "q06", "q07"
    };

    public static final String[] FAMILIES = {"scorch", "burst", "sustain"};
    public static final String[] SLOTS = {"blade", "charm", "head", "chest", "legs", "boots"};

    private EmberIdentitySettle() {}

    /** 1-based index into {@link #MAINLINE_KEYS}, or 0 if unknown. */
    public static int indexOf(String mapKey) {
        if (mapKey == null || mapKey.isEmpty()) return 0;
        String k = mapKey.toLowerCase(Locale.ROOT).trim();
        for (int i = 0; i < MAINLINE_KEYS.length; i++) {
            if (MAINLINE_KEYS[i].equals(k)) return i + 1;
        }
        return 0;
    }

    public static String keyOf(int index) {
        if (index < 1 || index > MAINLINE_KEYS.length) return "";
        return MAINLINE_KEYS[index - 1];
    }

    public static int familyIndex(String family) {
        if (family == null || family.isEmpty()) return 0;
        String f = family.toLowerCase(Locale.ROOT).trim();
        for (int i = 0; i < FAMILIES.length; i++) {
            if (FAMILIES[i].equals(f)) return i + 1;
        }
        return 0;
    }

    public static String familyOf(int index) {
        if (index < 1 || index > FAMILIES.length) return "";
        return FAMILIES[index - 1];
    }

    public static int slotIndex(String slot) {
        if (slot == null || slot.isEmpty()) return 0;
        String s = slot.toLowerCase(Locale.ROOT).trim();
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i].equals(s)) return i + 1;
        }
        return 0;
    }

    public static String slotOf(int index) {
        if (index < 1 || index > SLOTS.length) return "";
        return SLOTS[index - 1];
    }

    /**
     * Remember last identity-FC settle map/family/slot (overwrite).
     * Returns true if any field changed. Requires valid mainline map + valid family.
     */
    public static boolean remember(PlayerData d, String mapKey, String family, String slot) {
        if (d == null) return false;
        int mIdx = indexOf(mapKey);
        int fIdx = familyIndex(family);
        if (mIdx <= 0 || fIdx <= 0) return false;
        int sIdx = slotIndex(slot); // 0 ok if unknown/null
        boolean changed = false;
        int curM = d.periodCount(C_LAST, "all");
        if (curM != mIdx) {
            d.addPeriodCount(C_LAST, "all", mIdx - curM);
            changed = true;
        }
        int curF = d.periodCount(C_FAM, "all");
        if (curF != fIdx) {
            d.addPeriodCount(C_FAM, "all", fIdx - curF);
            changed = true;
        }
        int curS = d.periodCount(C_SLOT, "all");
        if (curS != sIdx) {
            d.addPeriodCount(C_SLOT, "all", sIdx - curS);
            changed = true;
        }
        return changed;
    }

    /** Last settled map key, or empty. */
    public static String lastMap(PlayerData d) {
        if (d == null) return "";
        return keyOf(d.periodCount(C_LAST, "all"));
    }

    /** Last settled family key, or empty. */
    public static String lastFamily(PlayerData d) {
        if (d == null) return "";
        return familyOf(d.periodCount(C_FAM, "all"));
    }

    /** Last settled slot key, or empty. */
    public static String lastSlot(PlayerData d) {
        if (d == null) return "";
        return slotOf(d.periodCount(C_SLOT, "all"));
    }

    public static String displayName(EmberRunMaps.MapDef m, String mapKey) {
        if (m != null && m.name != null && !m.name.isEmpty()) return m.name;
        return mapKey == null ? "" : mapKey;
    }

    /** Display family name（焚烬/烬爆/炽愈）；无→空。 */
    public static String familyDisplay(String family) {
        if (familyIndex(family) <= 0) return "";
        return EmberItemData.familyName(family);
    }

    /** Display slot name（刃/护符/…）；无→空。 */
    public static String slotDisplay(String slot) {
        if (slotIndex(slot) <= 0) return "";
        return EmberItemData.slotName(slot);
    }

    /** Lore line: 图名 · 族 · 槽；无图→空。 */
    public static String line(EmberRunMaps.MapDef m, String mapKey, String family, String slot) {
        String name = displayName(m, mapKey);
        if (name.isEmpty()) return "";
        String fam = familyDisplay(family);
        String sl = slotDisplay(slot);
        if (fam.isEmpty() && sl.isEmpty()) return name;
        if (sl.isEmpty()) return name + " · " + fam;
        if (fam.isEmpty()) return name + " · " + sl;
        return name + " · " + fam + " · " + sl;
    }
}

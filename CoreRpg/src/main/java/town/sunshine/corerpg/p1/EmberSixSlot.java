package town.sunshine.corerpg.p1;

import java.util.Locale;

/**
 * D318 六槽护甲 T1（规格 docs/design/DESIGN-ember-six-slot-t1-spec-revision-2026-10-08.md，已批 A · K0 · 白板 ×0.1）.
 * Switch + constants + pure helpers; no Bukkit types (unit-tested offline).
 *
 * <p><b>Switches</b> (ember-v1.yml, read with a code default of {@code false}; the keys are deliberately <i>not</i> in the
 * bundled / live yml yet, so nothing changes until a test server adds them):</p>
 * <ul>
 *   <li>{@value #KEY_ENABLED} — master switch. Off (default): EmberLoadoutService passes no armor, every formula, menu,
 *   drop and forge path is the 2-slot one bit for bit (T1-1 golden). Requires the P1 mode itself to be active.</li>
 *   <li>{@value #KEY_MIGRATE} — the one-time migration of old characters (spec §3.2). Off (default): not executed even when
 *   the master switch is on.</li>
 *   <li>{@value #KEY_SET_BONUS} — D325 Stage2 four-piece set bonus (tier C: taken ×0.97). Off (default): no set-bonus
 *   damage mult; Stage1 observation baseline bit for bit. Requires the master switch.</li>
 *   <li>{@value #KEY_K3_REFINE} — D341 K3 same-slot armor refine (craft=max, destroy material, zero cost). Off (default):
 *   no refine entry / always refuse. Requires the master switch. Gate-closed offline prep only — do not enable live
 *   until Stage2 green exit or an explicit parallel-build sign-off.</li>
 * </ul>
 * <p><b>F 共鸣:</b> armor has no tier / enhance of its own in the formula — it follows the selected charm; quality / craft /
 * family are the piece's own. Shares w = [charm .80, head / chest / legs / boots .05 each] (T0″ F arm, {@code arms.json}).
 * Rollback = switch off: the issued armor becomes inert, nothing is deleted.</p>
 */
public final class EmberSixSlot {

    private EmberSixSlot() {}

    public static final String KEY_ENABLED = "gear.six_slot.enabled";
    public static final String KEY_MIGRATE = "gear.six_slot.migrate";
    /** D325 Stage2 四件套效果（档 C）；默认关；关 = 与观察期基线逐位一致（无减伤） */
    public static final String KEY_SET_BONUS = "gear.six_slot.set_bonus";
    /** D341 K3 同部位熔炼；默认关；关 = 与现网一致（无熔炼入口 / 一律拒绝）；须 enabled */
    public static final String KEY_K3_REFINE = "gear.six_slot.k3_refine";

    /** Stage2 C: incoming P1 damage × this when four-piece active (never enters Formula B/H) */
    public static final double SET_BONUS_TAKEN_MULT = 0.97;
    /** D169: need this many same-family armor pieces with drop tier ≥ {@link #SET_MIN_TIER} */
    public static final int SET_NEED = 2;
    public static final int SET_MIN_TIER = 2;

    /** F: charm share (informational: under follow-all only the armor shares enter the difference term) */
    public static final double CHARM_W = 0.8;
    /** F: head, chest, legs, boots */
    private static final double[] ARMOR_W = {0.05, 0.05, 0.05, 0.05};

    public static double[] armorWeights() { return ARMOR_W.clone(); }

    /** test hook (null = follow config) */
    static volatile Boolean testEnabled, testMigrate, testSetBonus, testK3Refine;

    /** master switch: P1 active + {@value #KEY_ENABLED} (default false) */
    public static boolean enabled() {
        Boolean t = testEnabled;
        if (t != null) return t;
        EmberMode m = EmberMode.get();
        return m != null && m.isActive() && m.b(KEY_ENABLED, false);
    }

    /** migration switch: master switch + {@value #KEY_MIGRATE} (default false) */
    public static boolean migrateEnabled() {
        Boolean t = testMigrate;
        if (t != null) return t && enabled();
        EmberMode m = EmberMode.get();
        return enabled() && m != null && m.b(KEY_MIGRATE, false);
    }

    /** D325 Stage2 set-bonus switch: master switch + {@value #KEY_SET_BONUS} (default false) */
    public static boolean setBonusEnabled() {
        Boolean t = testSetBonus;
        if (t != null) return t && enabled();
        EmberMode m = EmberMode.get();
        return enabled() && m != null && m.b(KEY_SET_BONUS, false);
    }

    /** D341 K3 refine switch: master switch + {@value #KEY_K3_REFINE} (default false) */
    public static boolean k3RefineEnabled() {
        Boolean t = testK3Refine;
        if (t != null) return t && enabled();
        EmberMode m = EmberMode.get();
        return enabled() && m != null && m.b(KEY_K3_REFINE, false);
    }

    /**
     * Bukkit 1.12 {@code PlayerInventory#getArmorContents()} order is boots, legs, chest, head; P1 code uses head, chest,
     * legs, boots ({@link EmberItemData#ARMOR_SLOTS}). @return the P1 index for an armor-contents index (-1 if out of range).
     */
    public static int fromArmorContents(int bukkitIndex) {
        return bukkitIndex < 0 || bukkitIndex > 3 ? -1 : 3 - bukkitIndex;
    }

    public static int toArmorContents(int p1Index) {
        return p1Index < 0 || p1Index > 3 ? -1 : 3 - p1Index;
    }

    /**
     * Which worn pieces count (spec §3.3 T1: "只认本人绑定 v2 P1 甲，其余当空"). {@code verified[i]} = the stack in armor slot
     * i read ok + signed + trusted (DB owner / rev / state, done by the caller); null = empty / not a P1 piece / not trusted.
     * A piece counts only when it is armor of exactly that slot, bound, data version 2, its uid is neither the blade nor the
     * charm and appears once in the whole inventory ({@code uidCount}). Everything else is treated as an empty slot (q0 f0).
     */
    public static EmberItemData[] wornPieces(EmberItemData[] verified, EmberItemData blade, EmberItemData charm,
                                             java.util.Map<String, Integer> uidCount) {
        EmberItemData[] out = new EmberItemData[4];
        if (verified == null) return out;
        java.util.Set<String> seen = new java.util.HashSet<String>();
        for (int i = 0; i < 4 && i < verified.length; i++) {
            EmberItemData d = verified[i];
            if (d == null || !d.isArmor() || EmberItemData.armorIndex(d.slot) != i) continue;
            if (!d.bound || d.version < EmberItemData.DATA_VERSION) continue;
            if ((blade != null && d.uid.equals(blade.uid)) || (charm != null && d.uid.equals(charm.uid))) continue;
            Integer c = uidCount == null ? null : uidCount.get(d.uid);
            if (c != null && c > 1) continue;
            if (!seen.add(d.uid)) continue; // §8.2 one uid fills one slot
            out[i] = d;
        }
        return out;
    }

    /** "头盔 / 胸甲 / 护腿 / 靴子" */
    public static String slotLabel(int i) {
        return i < 0 || i > 3 ? "?" : EmberItemData.slotName(EmberItemData.ARMOR_SLOTS.get(i));
    }

    /** short form used in menus: 头 / 胸 / 腿 / 靴 */
    public static String slotShort(int i) {
        switch (i) {
            case 0: return "头";
            case 1: return "胸";
            case 2: return "腿";
            case 3: return "靴";
            default: return "?";
        }
    }

    /** "head" → 0 … (also accepts 头 / 胸 / 腿 / 靴 and helmet / chestplate / leggings) */
    public static int parseSlot(String s) {
        if (s == null) return -1;
        String l = s.toLowerCase(Locale.ROOT);
        int i = EmberItemData.armorIndex(l);
        if (i >= 0) return i;
        switch (l) {
            case "头": case "helmet": return 0;
            case "胸": case "chestplate": return 1;
            case "腿": case "leggings": return 2;
            case "靴": return 3;
            default: return -1;
        }
    }

    /** material the NI template is expected to use for a drop tier (§2.1 ③: T0 leather, T1 chain, T2 iron, T3 diamond) */
    public static String materialFor(int tier, int slot) {
        String m = tier <= 0 ? "LEATHER" : tier == 1 ? "CHAINMAIL" : tier == 2 ? "IRON" : "DIAMOND";
        String[] part = {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};
        return m + "_" + part[Math.max(0, Math.min(3, slot))];
    }

    /**
     * §2.2 armor lore (display only, never read back; at most 5 lines, one level of brackets). Uses neither the legacy
     * StatService lore keys (物理伤害 / 生命力 / 物理防御) nor any stat number that would go stale when the charm changes.
     */
    public static java.util.List<String> armorLore(EmberItemData d) {
        java.util.List<String> l = new java.util.ArrayList<String>(5);
        l.add("§6" + d.shortLabel());
        l.add("§7共鸣：阶级和强化随已选护符");
        EmberTables t = EmberMode.tables();
        l.add("§7成色 §f" + EmberItemData.qualityName(d.quality) + "§7（+" + Math.round(t.quality(d.quality) * 100) + "%） · 精工 §f"
                + Math.round(t.craft(d.craft) * 100) + "%");
        l.add("§8掉落阶 T" + d.tier + " · 四件套看这一行（同族≥T2 计件）");
        l.add("§8" + (d.bound ? "绑定" : "未绑定") + " · " + EmberCompare.sourceName(d.source));
        return l;
    }
}

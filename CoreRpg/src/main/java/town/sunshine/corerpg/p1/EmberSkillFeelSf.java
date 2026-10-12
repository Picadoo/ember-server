package town.sunshine.corerpg.p1;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * D1154 · skill Stage2 SF1–SF3: shape feel clarity (display only).
 * D12320–D12325 · TIP-K skill feel pack: shape/step role AB once + wrong-shape tip + slashConfirm empty path.
 * Zero skill_mult / CD / damage / sticky.
 */
public final class EmberSkillFeelSf {

    /** Session: shapeIds that already flashed shapeRoleLine for this UUID. */
    private static final Map<UUID, Set<Integer>> SHAPE_ROLE_SEEN = new ConcurrentHashMap<UUID, Set<Integer>>();
    /** Per-set-period: family that already got wrong-shape tip (cleared when active set changes). */
    private static final Map<UUID, String> WRONG_SHAPE_MARKED = new ConcurrentHashMap<UUID, String>();
    private static final Map<UUID, String> LAST_ACTIVE_SET = new ConcurrentHashMap<UUID, String>();
    /** Session: step variants that already flashed stepVariantRoleLine. */
    private static final Map<UUID, Set<Integer>> STEP_VARIANT_SEEN = new ConcurrentHashMap<UUID, Set<Integer>>();
    /** Session: step dirs that already flashed stepDirRoleLine. */
    private static final Map<UUID, Set<Integer>> STEP_DIR_SEEN = new ConcurrentHashMap<UUID, Set<Integer>>();

    private EmberSkillFeelSf() {}

    /** SF1: short shape label for ActionBar. */
    public static String shapeShort(int shapeId) {
        if (shapeId == EmberSkillKit.SHAPE_LINE) return "直线";
        if (shapeId == EmberSkillKit.SHAPE_RING) return "环斩";
        return "扇形";
    }

    /**
     * D12322: shape role one-liner (display only).
     * FAN → 扇形·清近 / LINE → 直线·穿远 / RING → 环斩·围身.
     */
    public static String shapeRoleLine(int shapeId) {
        if (shapeId == EmberSkillKit.SHAPE_LINE) return "直线·穿远";
        if (shapeId == EmberSkillKit.SHAPE_RING) return "环斩·围身";
        return "扇形·清近";
    }

    /**
     * Optional family half-word after shape role (e.g. {@code · 焚烬}). Empty when unknown/none.
     */
    public static String shapeRoleWithFamily(int shapeId, String family) {
        String base = shapeRoleLine(shapeId);
        if (family == null || family.isEmpty() || "none".equals(family)) return base;
        String name = EmberItemData.familyName(family);
        if (name == null || name.isEmpty() || name.equals(family)) return base;
        return base + " · " + name;
    }

    /**
     * Session latch: first time this UUID sees this shapeId → true (and records).
     * Same shape must not spam.
     */
    public static boolean tryMarkShapeRole(UUID id, int shapeId) {
        if (id == null) return false;
        Set<Integer> set = SHAPE_ROLE_SEEN.get(id);
        if (set == null) {
            set = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
            Set<Integer> raced = SHAPE_ROLE_SEEN.putIfAbsent(id, set);
            if (raced != null) set = raced;
        }
        return set.add(Integer.valueOf(shapeId));
    }

    /** Pure: whether shape role would still flash (does not mutate). */
    public static boolean wouldMarkShapeRole(Set<Integer> seen, int shapeId) {
        return seen == null || !seen.contains(Integer.valueOf(shapeId));
    }

    /**
     * D12323: wrong-shape half-line {@code 荐：{shapeShort}·{setFalloutLabel}}.
     * Empty when family unknown / unset.
     */
    public static String wrongShapeTip(String family) {
        if (family == null || family.isEmpty() || "none".equals(family)) return "";
        String fall = setFalloutLabel(family);
        if (fall.isEmpty()) return "";
        return "荐：" + shapeShort(EmberSkillKit.preferredShape(family)) + "·" + fall;
    }

    /**
     * Track active set; clears wrong-shape latch when family changes / unequip.
     */
    public static void noteActiveSet(UUID id, String family) {
        if (id == null) return;
        String f = family == null ? "" : family;
        String prev = LAST_ACTIVE_SET.put(id, f);
        if (prev != null && !prev.equals(f)) {
            WRONG_SHAPE_MARKED.remove(id);
        }
        if (f.isEmpty() || "none".equals(f)) {
            WRONG_SHAPE_MARKED.remove(id);
        }
    }

    /**
     * Once per set period: mark wrong-shape tip consumed for this family.
     * @return true if this is the first tip for the current set period
     */
    public static boolean tryMarkWrongShape(UUID id, String family) {
        if (id == null || family == null || family.isEmpty() || "none".equals(family)) return false;
        if (setFalloutLabel(family).isEmpty()) return false;
        noteActiveSet(id, family);
        return WRONG_SHAPE_MARKED.putIfAbsent(id, family) == null;
    }

    /**
     * D12323 consume: if activeSet present and shape ≠ preferred, return tip once per set period; else "".
     */
    public static String maybeConsumeWrongShapeTip(UUID id, String family, int shapeId) {
        if (id == null) return "";
        noteActiveSet(id, family);
        if (family == null || family.isEmpty() || "none".equals(family)) return "";
        if (setFalloutLabel(family).isEmpty()) return "";
        if (shapeId == EmberSkillKit.preferredShape(family)) return "";
        if (!tryMarkWrongShape(id, family)) return "";
        return wrongShapeTip(family);
    }

    /**
     * D12324: family step role one-liner (display only). Empty for plain / unknown.
     */
    public static String stepVariantRoleLine(int variant) {
        if (variant == EmberSkillKit.STEP_VARIANT_HUOHEN) return "火痕步·点燃起跳";
        if (variant == EmberSkillKit.STEP_VARIANT_BAOSHAN) return "爆闪步·拉开";
        if (variant == EmberSkillKit.STEP_VARIANT_CHENGHU) return "承护步·站桩";
        return "";
    }

    /**
     * D12324: step direction role one-liner.
     * back → 后撤·拉开; forward → 前冲·突进.
     */
    public static String stepDirRoleLine(int dirId) {
        if (dirId == EmberSkillKit.DIR_BACK) return "后撤·拉开";
        return "前冲·突进";
    }

    /** Combined step role: prefer variant line; if plain + backward → dir line. */
    public static String stepRoleLine(int variant, boolean backward) {
        String v = stepVariantRoleLine(variant);
        if (!v.isEmpty()) return v;
        if (backward) return stepDirRoleLine(EmberSkillKit.DIR_BACK);
        return "";
    }

    /** Session latch: first time this UUID sees this step variant → true. */
    public static boolean tryMarkStepVariant(UUID id, int variant) {
        if (id == null) return false;
        if (variant == EmberSkillKit.STEP_VARIANT_PLAIN) return false;
        Set<Integer> set = STEP_VARIANT_SEEN.get(id);
        if (set == null) {
            set = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
            Set<Integer> raced = STEP_VARIANT_SEEN.putIfAbsent(id, set);
            if (raced != null) set = raced;
        }
        return set.add(Integer.valueOf(variant));
    }

    /** Session latch: first time this UUID sees this step dir → true. */
    public static boolean tryMarkStepDir(UUID id, int dirId) {
        if (id == null) return false;
        Set<Integer> set = STEP_DIR_SEEN.get(id);
        if (set == null) {
            set = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
            Set<Integer> raced = STEP_DIR_SEEN.putIfAbsent(id, set);
            if (raced != null) set = raced;
        }
        return set.add(Integer.valueOf(dirId));
    }

    /** Clear all session / per-set latches for a player (quit / tests). */
    public static void clearSession(UUID id) {
        if (id == null) return;
        SHAPE_ROLE_SEEN.remove(id);
        WRONG_SHAPE_MARKED.remove(id);
        LAST_ACTIVE_SET.remove(id);
        STEP_VARIANT_SEEN.remove(id);
        STEP_DIR_SEEN.remove(id);
    }

    /**
     * SF1 + SF2: 烬斩命中确认文案（命中数 ≥2 时带 ×N，不改伤害）。
     * @return empty when hits &lt; 1
     */
    public static String slashConfirm(int shapeId, int hits) {
        if (hits < 1) return "";
        String s = shapeShort(shapeId) + "烬斩命中";
        if (hits > 1) s = s + " ×" + hits;
        return s;
    }

    /** D1208: 落空反馈（展示 only · 非 SF4）. */
    public static String slashMiss(int shapeId) {
        return shapeShort(shapeId) + "烬斩落空";
    }

    /** D1240: 烬突受阻（展示 only）. */
    public static String dashBlocked() {
        return "烬突受阻";
    }

    /** D1272: charge CD line with shape short (display only). */
    public static String chargeCdLine(int shapeId, int secLeft) {
        int s = Math.max(0, secLeft);
        return shapeShort(shapeId) + "充能 " + s + "s";
    }

    /**
     * SF1–SF3: confirm + optional set-fallout half (e.g. {@code " · 烬刃余烬"}).
     * Empty/null half → base only (no family to fall back).
     */
    public static String slashConfirmWithSet(int shapeId, int hits, String setFalloutHalf) {
        String base = slashConfirm(shapeId, hits);
        if (base.isEmpty()) return "";
        if (setFalloutHalf == null || setFalloutHalf.isEmpty()) return base;
        return base + setFalloutHalf;
    }

    /**
     * D12325: confirm + fallout half; when half null/empty but family known, fall back to
     * {@link #setFalloutActionHalf} or {@code " · "+setFalloutLabel}. Zero coef.
     */
    public static String slashConfirmWithSet(int shapeId, int hits, String setFalloutHalf, String family) {
        String base = slashConfirm(shapeId, hits);
        if (base.isEmpty()) return "";
        String half = setFalloutHalf;
        if (half == null || half.isEmpty()) {
            if (family != null && !family.isEmpty() && !"none".equals(family)) {
                half = setFalloutActionHalf(family);
                if (half == null || half.isEmpty()) {
                    String lab = setFalloutLabel(family);
                    if (!lab.isEmpty()) half = " · " + lab;
                }
            }
        }
        if (half == null || half.isEmpty()) return base;
        return base + half;
    }

    /** SF3: 形状与套装落点同屏半行（展示 only）。 */
    public static String setShapeHalf(String family) {
        String shape = EmberSkillKit.preferredShapeLabel(family);
        String fall = setFalloutLabel(family);
        if (fall.isEmpty()) return "§e形状 §f" + shape;
        return "§e形状 §f" + shape + " §8· 落点 §7" + fall;
    }

    /** Worn-shape variant for in-combat / kit when actual shape id known. */
    public static String setShapeHalf(String family, int wornShapeId) {
        String shape = EmberSkillKit.shapeName(wornShapeId);
        String fall = setFalloutLabel(family);
        if (fall.isEmpty()) return "§e形状 §f" + shape;
        return "§e形状 §f" + shape + " §8· 落点 §7" + fall;
    }

    public static String setFalloutLabel(String family) {
        if ("scorch".equals(family)) return "点燃/火痕";
        if ("burst".equals(family)) return "缓速/闪";
        if ("sustain".equals(family)) return "微疗/护";
        return "";
    }

    /** ActionBar-safe set half matching {@link town.sunshine.corerpg.SkillService} slashSetTip style. */
    public static String setFalloutActionHalf(String family) {
        if ("scorch".equals(family)) return " · 烬刃余烬";
        if ("burst".equals(family)) return " · 烬刃缓速";
        if ("sustain".equals(family)) return " · 烬刃微疗";
        return "";
    }
    /**
     * D12281: TrMenu one-line set-feel (display only).
     * Known family → {@code §e焚烬烬斩 §f扇形 §8· 点燃/火痕}; unset/unknown → 未成套半行.
     * Reuses familyName + shapeShort(preferredShape) + setFalloutLabel — zero coef/geometry.
     */
    public static String menuLine(String family) {
        if (family == null || family.isEmpty() || "none".equals(family)) {
            return "§8未成套 · 成套后烬斩形状与落点随族变";
        }
        String fall = setFalloutLabel(family);
        if (fall.isEmpty()) {
            return "§8未成套 · 成套后烬斩形状与落点随族变";
        }
        String name = EmberItemData.familyName(family);
        String shape = shapeShort(EmberSkillKit.preferredShape(family));
        return "§e" + name + "烬斩 §f" + shape + " §8· " + fall;
    }

    /** D12281: loadout overload — blade family when present, else unset. */
    public static String menuLine(EmberLoadout lo) {
        if (lo == null || lo.blade == null) return menuLine((String) null);
        return menuLine(lo.blade.family);
    }

}

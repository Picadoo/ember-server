package town.sunshine.corerpg;

/**
 * D384: %corerpg_pet_*% — read-only pet level / feed display for TrMenu.
 * True source: {@link PetService} + {@link PlayerData}. Does not change feed.* formulas.
 */
final class CorePapiPet {

    private CorePapiPet() {}

    /**
     * Resolve a pet_* key. Known keys never return null (menus need a string);
     * unknown keys return null so PAPI leaves the placeholder as-is.
     */
    static String resolve(PetService pets, PlayerData data, String key) {
        if (key == null) return null;
        if (!CorePapi.PET.contains(key)) return null;
        if (pets == null || data == null) {
            return apply(key, "", "未选定", 0, 10, 0, 0, 0);
        }
        String active = data.getActivePet();
        boolean has = active != null && !active.isEmpty();
        int max = Math.max(1, pets.getFeedMaxLevel());
        int unlocked = data.getPetsUnlocked() == null ? 0 : data.getPetsUnlocked().size();
        if (!has) {
            return apply(key, "", "未选定", 0, max, 0, 0, unlocked);
        }
        PetService.PetDef def = pets.getDef(active);
        String name = def != null ? def.display : active;
        int level = data.getPetLevel(active);
        int cost = level >= max ? 0 : pets.feedCostFor(level);
        int power = pets.getPowerBonus(data);
        return apply(key, active, name, level, max, cost, power, unlocked);
    }

    /**
     * Bukkit-free apply for unit tests. Conventions from DESIGN §2.1.
     *
     * @param level 0 when no active pet; otherwise getPetLevel
     * @param nextCost already short-circuited to 0 when full
     */
    static String apply(String key, String activeId, String activeName,
                        int level, int maxLevel, int nextCost, int powerBonus, int unlocked) {
        if (key == null) return null;
        boolean has = activeId != null && !activeId.isEmpty();
        int max = Math.max(1, maxLevel);
        if ("pet_active_id".equals(key)) return has ? activeId : "";
        if ("pet_active_name".equals(key)) return has ? (activeName == null ? activeId : activeName) : "未选定";
        if ("pet_level".equals(key)) return String.valueOf(has ? Math.max(0, level) : 0);
        if ("pet_max_level".equals(key)) return String.valueOf(max);
        if ("pet_level_line".equals(key)) {
            if (!has) return "尚无使魔";
            if (level >= max) return "已满级 Lv." + level;
            return "Lv." + level + "/" + max;
        }
        if ("pet_feed_cost".equals(key)) {
            if (!has) return "0";
            if (level >= max) return "0";
            return String.valueOf(Math.max(0, nextCost));
        }
        if ("pet_feed_hint".equals(key)) {
            if (!has) return "先解锁使魔";
            if (level >= max) return "已满级";
            return "下一级需要魂尘×" + Math.max(0, nextCost);
        }
        if ("pet_power_bonus".equals(key)) return String.valueOf(has ? Math.max(0, powerBonus) : 0);
        if ("pet_unlocked_count".equals(key)) return String.valueOf(Math.max(0, unlocked));
        return null;
    }
}

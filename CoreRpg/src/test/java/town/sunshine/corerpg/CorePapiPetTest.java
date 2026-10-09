package town.sunshine.corerpg;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static town.sunshine.corerpg.CorePapi.Section.PET;

/** D384: pet_* PAPI empty/with-pet conventions (Bukkit-free via CorePapiPet.apply). */
public final class CorePapiPetTest {

    @Test public void noPetConventions() {
        assertEquals("", CorePapiPet.apply("pet_active_id", "", "x", 0, 10, 0, 0, 0));
        assertEquals("未选定", CorePapiPet.apply("pet_active_name", "", "x", 0, 10, 0, 0, 0));
        assertEquals("0", CorePapiPet.apply("pet_level", "", "x", 0, 10, 0, 0, 0));
        assertEquals("10", CorePapiPet.apply("pet_max_level", "", "x", 0, 10, 0, 0, 0));
        assertEquals("尚无使魔", CorePapiPet.apply("pet_level_line", "", "x", 0, 10, 0, 0, 0));
        assertEquals("0", CorePapiPet.apply("pet_feed_cost", "", "x", 0, 10, 3, 0, 0));
        assertEquals("先解锁使魔", CorePapiPet.apply("pet_feed_hint", "", "x", 0, 10, 3, 0, 0));
        assertEquals("0", CorePapiPet.apply("pet_power_bonus", "", "x", 0, 10, 0, 9, 0));
        assertEquals("0", CorePapiPet.apply("pet_unlocked_count", "", "x", 0, 10, 0, 0, 0));
    }

    @Test public void withPetMidLevel() {
        String id = "pet_ember_ashling";
        String name = "§a余烬灰灵";
        assertEquals(id, CorePapiPet.apply("pet_active_id", id, name, 3, 10, 3, 7, 2));
        assertEquals(name, CorePapiPet.apply("pet_active_name", id, name, 3, 10, 3, 7, 2));
        assertEquals("3", CorePapiPet.apply("pet_level", id, name, 3, 10, 3, 7, 2));
        assertEquals("10", CorePapiPet.apply("pet_max_level", id, name, 3, 10, 3, 7, 2));
        assertEquals("Lv.3/10", CorePapiPet.apply("pet_level_line", id, name, 3, 10, 3, 7, 2));
        assertEquals("3", CorePapiPet.apply("pet_feed_cost", id, name, 3, 10, 3, 7, 2));
        assertEquals("下一级需要魂尘×3", CorePapiPet.apply("pet_feed_hint", id, name, 3, 10, 3, 7, 2));
        assertEquals("7", CorePapiPet.apply("pet_power_bonus", id, name, 3, 10, 3, 7, 2));
        assertEquals("2", CorePapiPet.apply("pet_unlocked_count", id, name, 3, 10, 3, 7, 2));
    }

    @Test public void withPetMaxLevel() {
        String id = "pet_ember_cinder";
        assertEquals("已满级 Lv.10", CorePapiPet.apply("pet_level_line", id, "烬火", 10, 10, 99, 14, 1));
        assertEquals("0", CorePapiPet.apply("pet_feed_cost", id, "烬火", 10, 10, 99, 14, 1));
        assertEquals("已满级", CorePapiPet.apply("pet_feed_hint", id, "烬火", 10, 10, 99, 14, 1));
    }

    @Test public void unknownKeyNull() {
        assertNull(CorePapiPet.apply("pet_nosuch", "a", "n", 1, 10, 1, 0, 1));
        assertNull(CorePapiPet.apply(null, "a", "n", 1, 10, 1, 0, 1));
    }

    @Test public void resolveNullServiceUsesEmptyConventions() {
        assertEquals("尚无使魔", CorePapiPet.resolve(null, new PlayerData(), "pet_level_line"));
        assertEquals("先解锁使魔", CorePapiPet.resolve(null, new PlayerData(), "pet_feed_hint"));
        assertEquals("0", CorePapiPet.resolve(null, new PlayerData(), "pet_level"));
        assertNull(CorePapiPet.resolve(null, new PlayerData(), "not_a_pet_key"));
    }

    @Test public void everyPetKeyRoutes() {
        for (String k : CorePapi.PET) {
            assertEquals(k, PET, CorePapi.route(k));
        }
    }
}

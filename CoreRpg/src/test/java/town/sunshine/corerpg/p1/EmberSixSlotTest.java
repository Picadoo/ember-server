package town.sunshine.corerpg.p1;

import org.junit.After;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.*;

/** D318 六槽 T1: data model, switch default, F formula ("护符 + 差项"), armor sanitising, slot order, lore. */
public class EmberSixSlotTest {

    private final EmberTables t = EmberTables.defaults();

    @After public void reset() { EmberSixSlot.testEnabled = null; EmberSixSlot.testMigrate = null; }

    static EmberItemData piece(String fam, String slot, int tier, int q, int f, int e) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId(tier == 0 ? "none" : fam, slot, tier),
                tier == 0 ? "none" : fam, slot, tier, q, f, e, 0, true, "drop", EmberItemData.DATA_VERSION, 0);
    }

    static EmberItemData[] armorLike(EmberItemData charm) {
        EmberItemData[] a = new EmberItemData[4];
        for (int i = 0; i < 4; i++) a[i] = piece(charm.family, EmberItemData.ARMOR_SLOTS.get(i), charm.tier, charm.quality, charm.craft, 0);
        return a;
    }

    @Test public void switchesDefaultOffWithoutConfig() {
        assertNull(EmberMode.get() == null ? null : "mode present in unit test");
        assertFalse(EmberSixSlot.enabled());
        assertFalse(EmberSixSlot.migrateEnabled());
        EmberSixSlot.testMigrate = true; // migrate alone never runs without the master switch
        assertFalse(EmberSixSlot.migrateEnabled());
    }

    @Test public void armorSlotsAreValidItemSlots() {
        for (String s : EmberItemData.ARMOR_SLOTS) {
            EmberItemData d = piece("scorch", s, 2, 1, 2, 0);
            assertNull(d.validate());
            assertTrue(d.isArmor());
            assertFalse(d.isBlade() || d.isCharm());
            assertEquals("ember_v1_scorch_" + s + "_t2", d.ni);
            EmberItemData z = piece("none", s, 0, 0, 0, 0);
            assertNull(z.validate());
            assertEquals("ember_v1_t0_" + s, z.ni);
        }
        assertEquals(-1, EmberItemData.armorIndex("blade"));
        assertEquals("胸甲", EmberItemData.slotName("chest"));
        assertEquals("刃", EmberItemData.slotName("blade"));
        assertEquals("护符", EmberItemData.slotName("charm"));
        assertNotNull(piece("scorch", "chest", 2, 0, 0, 0).withRev(0).ni);
        assertEquals("bad slot ring", new EmberItemData(EmberItemData.newUid(), "x", "scorch", "ring", 1, 0, 0, 0, 0, true, "drop", 2, 0).validate());
    }

    @Test public void bukkitArmorContentsOrder() {
        // 1.12 getArmorContents(): boots, legs, chest, head
        assertEquals(3, EmberSixSlot.fromArmorContents(0));
        assertEquals(2, EmberSixSlot.fromArmorContents(1));
        assertEquals(1, EmberSixSlot.fromArmorContents(2));
        assertEquals(0, EmberSixSlot.fromArmorContents(3));
        for (int i = 0; i < 4; i++) assertEquals(i, EmberSixSlot.fromArmorContents(EmberSixSlot.toArmorContents(i)));
        assertEquals(-1, EmberSixSlot.fromArmorContents(4));
    }

    /** §3.1 invariant: armor with the charm's quality / craft → H0 / H / D / M / B bit-identical to the 2-slot loadout. */
    @Test public void sameQualityCraftArmorIsBitIdentical() {
        Random r = new Random(318);
        String[] fams = {"scorch", "burst", "sustain"};
        for (int n = 0; n < 20000; n++) {
            String fam = fams[r.nextInt(3)];
            EmberItemData b = piece(fam, "blade", r.nextInt(4), r.nextInt(4), r.nextInt(4), r.nextInt(11));
            EmberItemData c = piece(r.nextBoolean() ? fam : fams[r.nextInt(3)], "charm", r.nextInt(4), r.nextInt(4), r.nextInt(4), r.nextInt(11));
            int lv = 1 + r.nextInt(70);
            double fh = r.nextBoolean() ? 0 : r.nextInt(200), fd = r.nextBoolean() ? 0 : r.nextInt(20);
            EmberLoadout two = EmberLoadout.compute(t, b, c, lv, fh, fd);
            EmberLoadout six = EmberLoadout.compute(t, b, c, lv, fh, fd, armorLike(c));
            assertTrue(six.sixSlot());
            assertEquals(Double.doubleToLongBits(two.b), Double.doubleToLongBits(six.b));
            assertEquals(Double.doubleToLongBits(two.h0), Double.doubleToLongBits(six.h0));
            assertEquals(Double.doubleToLongBits(two.h), Double.doubleToLongBits(six.h));
            assertEquals(Double.doubleToLongBits(two.d), Double.doubleToLongBits(six.d));
            assertEquals(Double.doubleToLongBits(two.m), Double.doubleToLongBits(six.m));
            assertEquals(Double.doubleToLongBits(two.ehp()), Double.doubleToLongBits(six.ehp()));
        }
    }

    @Test public void emptySlotsCountAsQ0F0AndNoCharmMeansNoArmor() {
        EmberItemData c = piece("burst", "charm", 3, 2, 3, 7);
        EmberLoadout empty = EmberLoadout.compute(t, null, c, 30, 0, 0, new EmberItemData[4]);
        EmberLoadout two = EmberLoadout.compute(t, null, c, 30);
        // each empty slot = −w·h·(q_c + f_c) = −0.05 × 165 × (0.08 + 0.06) → four slots ≈ −4.62
        assertEquals(two.h0 - 4 * 0.05 * 165 * (0.08 + 0.06), empty.h0, 1e-9);
        EmberItemData[] high = armorLike(c);
        high[1] = piece("burst", "chest", 1, 3, 3, 0); // armor above the charm counts upward (+0.05·165·(0.12+0.06−0.14))
        assertEquals(two.h0 + 0.05 * 165 * (0.12 + 0.06 - 0.14), EmberLoadout.compute(t, null, c, 30, 0, 0, high).h0, 1e-9);
        EmberLoadout none = EmberLoadout.compute(t, null, null, 30, 0, 0, high);
        assertEquals(Double.doubleToLongBits(EmberLoadout.compute(t, null, null, 30).h0), Double.doubleToLongBits(none.h0));
    }

    @Test public void armorOfTheWrongSlotOrDuplicateUidIsIgnored() {
        EmberItemData c = piece("burst", "charm", 2, 1, 1, 3);
        EmberItemData[] a = armorLike(c);
        EmberItemData two = EmberLoadout.compute(t, null, c, 10).charm;
        assertNotNull(two);
        EmberItemData[] wrong = a.clone();
        wrong[0] = a[1]; // chest in the head slot (and its uid twice)
        EmberLoadout l = EmberLoadout.compute(t, null, c, 10, 0, 0, wrong);
        assertNull(l.armor(0));
        assertNotNull(l.armor(1));
        EmberItemData[] notArmor = a.clone();
        notArmor[2] = c; // the charm itself in an armor slot
        assertNull(EmberLoadout.compute(t, null, c, 10, 0, 0, notArmor).armor(2));
        Map<String, Integer> count = new HashMap<String, Integer>();
        count.put(a[3].uid, 2);
        EmberItemData[] worn = EmberSixSlot.wornPieces(a, null, c, count);
        assertNull("uid seen twice in the inventory", worn[3]);
        assertNotNull(worn[0]);
        EmberItemData unbound = new EmberItemData(EmberItemData.newUid(), a[0].ni, a[0].family, "head", a[0].tier, 1, 1, 0, 0, false, "drop", 2, 0);
        assertNull(EmberSixSlot.wornPieces(new EmberItemData[]{unbound, null, null, null}, null, c, null)[0]);
        EmberItemData v1 = new EmberItemData(EmberItemData.newUid(), a[0].ni, a[0].family, "head", a[0].tier, 1, 1, 0, 0, true, "drop", 1, 0);
        assertNull(EmberSixSlot.wornPieces(new EmberItemData[]{v1, null, null, null}, null, c, null)[0]);
    }

    @Test public void armorLoreHasNoLegacyStatKeysAndAtMostFiveLines() {
        EmberItemData d = piece("sustain", "legs", 2, 2, 1, 0);
        java.util.List<String> l = EmberSixSlot.armorLore(d);
        assertTrue(l.size() <= 5);
        for (String s : l) {
            assertFalse(s, s.contains("物理伤害") || s.contains("生命力") || s.contains("物理防御"));
            assertFalse("no typed command in lore", s.contains("/corerpg") || s.contains("/ember"));
        }
        assertEquals("T2 炽愈护腿｜成色卓越｜精工2%", d.shortLabel());
        assertTrue(l.get(2).contains("+8%"));
        assertEquals("T2 炽愈刃｜成色卓越｜精工2%｜+5", piece("sustain", "blade", 2, 2, 1, 5).shortLabel()); // blade label unchanged
    }

    @Test public void parseSlotAndMaterials() {
        assertEquals(0, EmberSixSlot.parseSlot("head"));
        assertEquals(1, EmberSixSlot.parseSlot("胸"));
        assertEquals(3, EmberSixSlot.parseSlot("BOOTS"));
        assertEquals(-1, EmberSixSlot.parseSlot("blade"));
        assertEquals("LEATHER_HELMET", EmberSixSlot.materialFor(0, 0));
        assertEquals("DIAMOND_BOOTS", EmberSixSlot.materialFor(3, 3));
    }
}

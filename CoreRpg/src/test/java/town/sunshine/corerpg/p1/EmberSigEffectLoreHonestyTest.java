package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TIP-BH D13046: signature effect held/armor/lib lore honesty — Def.good/bad display only.
 * Zero mods / ALTS / STAMP.
 */
public class EmberSigEffectLoreHonestyTest {

    private static EmberItemData piece(String fam, String slot, int tier, int q, int f, int e) {
        return new EmberItemData(EmberItemData.newUid(), EmberItemData.templateId(tier == 0 ? "none" : fam, slot, tier),
                tier == 0 ? "none" : fam, slot, tier, q, f, e, 0, true, "drop", EmberItemData.DATA_VERSION, 0);
    }

    @Test
    public void l07BladeHelperHasNameGoodCostThreeLines() {
        EmberSignature.Def l07 = EmberSignature.byCode(7);
        assertNotNull(l07);
        assertEquals("潮闸长杆", l07.name);
        assertTrue(l07.good.contains("直线"));
        assertFalse("无".equals(l07.bad));

        List<String> lore = new ArrayList<String>();
        EmberSignature.appendHeldEffectLore(lore, 7);
        assertEquals(3, lore.size());
        assertTrue(lore.get(0).contains("潮闸长杆"));
        assertTrue(lore.get(0).contains("潮闸重卫"));
        assertFalse("name line must not glue good", lore.get(0).contains(l07.good));
        assertTrue(lore.get(1).startsWith("§a得 · §f"));
        assertTrue(lore.get(1).contains("直线"));
        assertTrue(lore.get(2).startsWith("§c代价 · §7"));
        assertTrue(lore.get(2).contains(l07.bad));
    }

    @Test
    public void armorLoreWithSigShowsNameAndGood() {
        EmberItemData d = piece("scorch", "head", 2, 1, 1, 0).withSig(8);
        assertEquals(8, d.sigCode);
        List<String> l = EmberSixSlot.armorLore(d);
        EmberSignature.Def sg = EmberSignature.byCode(8);
        assertNotNull(sg);
        boolean hasName = false, hasGood = false;
        for (String s : l) {
            if (s.contains(sg.name)) hasName = true;
            if (s.contains("§a得 · §f") && s.contains(sg.good.substring(0, Math.min(4, sg.good.length())))) hasGood = true;
        }
        assertTrue("armor lore has signature name", hasName);
        assertTrue("armor lore has 得 line", hasGood);
    }

    @Test
    public void sigCodeZeroNoSignatureBlock() {
        List<String> lore = new ArrayList<String>();
        EmberSignature.appendHeldEffectLore(lore, 0);
        assertTrue(lore.isEmpty());

        EmberItemData d = piece("sustain", "legs", 2, 2, 1, 0);
        assertEquals(0, d.sigCode);
        List<String> armor = EmberSixSlot.armorLore(d);
        for (String s : armor) {
            assertFalse(s, s.contains("签名 ·") || s.contains("§a得 ·") || s.contains("§c代价 ·"));
        }
        assertTrue(armor.size() <= 5);
    }

    @Test
    public void badEqualsWuOmitsCostLine() {
        EmberSignature.Def l05 = EmberSignature.byCode(5);
        assertNotNull(l05);
        assertEquals("无", l05.bad);

        List<String> lore = new ArrayList<String>();
        EmberSignature.appendHeldEffectLore(lore, 5);
        assertEquals(2, lore.size());
        assertTrue(lore.get(0).contains(l05.name));
        assertTrue(lore.get(1).startsWith("§a得 · §f"));
        for (String s : lore) assertFalse(s, s.contains("§c代价 ·"));
    }

    @Test
    public void gearLibFormatIsMultiLineNotSticky() {
        // sticky old format glued name+good on one line after ）; helper keeps them apart
        EmberSignature.Def l07 = EmberSignature.byCode(7);
        List<String> lore = new ArrayList<String>();
        EmberSignature.appendHeldEffectLore(lore, l07.code);
        assertTrue(lore.size() >= 2);
        String sticky = "§6签名：" + l07.name + " §7（" + l07.boss + "）" + l07.good;
        for (String s : lore) {
            assertFalse("must not be sticky glued line", s.equals(sticky));
            assertFalse("name line must not append good without separator",
                    s.contains(l07.name) && s.contains(l07.good) && !s.contains("得"));
        }
        assertTrue(lore.get(0).contains("签名 ·"));
        assertFalse(lore.get(0).contains(l07.good));
    }

    @Test
    public void nullLoreAndUnknownCodeAreNoOps() {
        EmberSignature.appendHeldEffectLore(null, 7);
        List<String> lore = new ArrayList<String>();
        EmberSignature.appendHeldEffectLore(lore, 999);
        assertTrue(lore.isEmpty());
        EmberSignature.appendHeldEffectLore(lore, -1);
        assertTrue(lore.isEmpty());
    }
}

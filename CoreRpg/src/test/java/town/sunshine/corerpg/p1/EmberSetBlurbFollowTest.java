package town.sunshine.corerpg.p1;

import org.junit.Test;

import static org.junit.Assert.*;
import static town.sunshine.corerpg.p1.EmberRunPapi.Section.LOADOUT;

/**
 * TIP-BI D13061: gear page set blurb follows activeSet; sustain ×1.12 lie fixed.
 * Zero SetRules / Formula / sustain_hp_mult.
 */
public class EmberSetBlurbFollowTest {

    @Test
    public void familyBlurbSustainNo112HasHeal() {
        String s = EmberItemData.familyBlurb("sustain");
        assertNotNull(s);
        assertFalse("must not claim ×1.12", s.contains("1.12"));
        assertTrue(s.contains("1.00") || s.contains("×1.00"));
        assertTrue("heal sentence", s.contains("回") || s.contains("2.5%"));
        assertTrue(s.contains("最稳"));
        assertTrue(s.contains("炽愈"));
    }

    @Test
    public void familyBlurbScorchBurstTheme() {
        String sc = EmberItemData.familyBlurb("scorch");
        String bu = EmberItemData.familyBlurb("burst");
        assertFalse(sc.isEmpty());
        assertFalse(bu.isEmpty());
        assertTrue(sc.contains("焚烬") || sc.contains("点燃"));
        assertTrue(bu.contains("烬爆") || bu.contains("炸"));
    }

    @Test
    public void setBlurbScorchNotSustainMain() {
        String b = EmberItemData.setBlurb("scorch");
        assertTrue(b.contains("焚烬") || b.contains("点燃"));
        assertFalse("scorch blurb must not lead with 炽愈", b.contains("炽愈"));
    }

    @Test
    public void setBlurbNoneOrEmptyHintsSameFamily() {
        assertTrue(EmberItemData.setBlurb("none").contains("同族启用"));
        assertTrue(EmberItemData.setBlurb(null).contains("同族启用"));
        assertTrue(EmberItemData.setBlurb("").contains("同族启用"));
    }

    @Test
    public void setBlurbRoutesLoadout() {
        assertEquals(LOADOUT, EmberRunPapi.route("set_blurb"));
    }
}

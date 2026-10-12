package town.sunshine.corerpg.p1;

import static org.junit.Assert.*;

import org.junit.Test;

import town.sunshine.corerpg.DailyService;
import town.sunshine.corerpg.PlayerData;

/**
 * TIP-AW DESIGN §2.3: CapSoft week remain honesty — peek vs consume + weekLine fork.
 * Zero WEEKLY_CAP / C_CONV / C_USED key change.
 */
public class EmberConvertCapSoftWeekAlignTest {

    private static PlayerData atCapSoftReady() {
        PlayerData d = new PlayerData();
        d.addPeriodCount(EmberConvertRules.C_CONV, DailyService.weekId(), EmberConvertRules.WEEKLY_CAP);
        assertTrue(EmberConvertCapSoft.ready(d));
        return d;
    }

    private static PlayerData atCapSoftUsed() {
        PlayerData d = atCapSoftReady();
        assertTrue(EmberConvertCapSoft.consume(d));
        assertFalse(EmberConvertCapSoft.ready(d));
        return d;
    }

    @Test
    public void effectiveExtra_fullReady() {
        PlayerData d = atCapSoftReady();
        assertEquals(1, EmberConvertCapSoft.effectiveExtra(d, EmberConvertRules.WEEKLY_CAP));
        assertTrue(EmberConvertCapSoft.ready(d)); // peek — still ready
    }

    @Test
    public void effectiveExtra_underCap_zero() {
        PlayerData d = new PlayerData();
        assertEquals(0, EmberConvertCapSoft.effectiveExtra(d, 0));
        assertTrue(EmberConvertCapSoft.ready(d)); // unused soft stays ready but not "extra" yet
    }

    @Test
    public void effectiveExtra_fullSoftUsed_zero() {
        PlayerData d = atCapSoftUsed();
        assertEquals(0, EmberConvertCapSoft.effectiveExtra(d, EmberConvertRules.WEEKLY_CAP));
    }

    @Test
    public void tryWeekCapSoft_previewPeek_noConsume() {
        PlayerData d = atCapSoftReady();
        assertTrue(EmberConvertCapSoft.tryWeekCapSoft(d, false));
        assertTrue(EmberConvertCapSoft.ready(d));
        assertFalse(EmberConvertCapSoft.usedThisWeek(d));
        // second preview still peeks
        assertTrue(EmberConvertCapSoft.tryWeekCapSoft(d, false));
        assertTrue(EmberConvertCapSoft.ready(d));
    }

    @Test
    public void tryWeekCapSoft_confirmConsume_once() {
        PlayerData d = atCapSoftReady();
        assertTrue(EmberConvertCapSoft.tryWeekCapSoft(d, true));
        assertFalse(EmberConvertCapSoft.ready(d));
        assertFalse(EmberConvertCapSoft.tryWeekCapSoft(d, true));
        assertFalse(EmberConvertCapSoft.tryWeekCapSoft(d, false));
    }

    @Test
    public void convertSurePath_confirmAfterPreview_stillConsumesOnce() {
        // Simulate ConvertSure: preview peek then confirm consume — no "preview swallow → confirm refuse"
        PlayerData d = atCapSoftReady();
        assertTrue(EmberConvertCapSoft.tryWeekCapSoft(d, false)); // preview
        assertTrue(EmberConvertCapSoft.ready(d));
        assertTrue(EmberConvertCapSoft.tryWeekCapSoft(d, true)); // ConvertSure / confirm
        assertFalse(EmberConvertCapSoft.ready(d));
        assertFalse(EmberConvertCapSoft.tryWeekCapSoft(d, true));
    }

    @Test
    public void weekLine_fullReady_softCopy() {
        String s = EmberConvertPath.weekLineFor(EmberConvertRules.WEEKLY_CAP, true);
        assertTrue(s, s.contains("容错待用"));
        assertTrue(s, s.contains("还可 1 次"));
        assertFalse(s, s.contains("环已完成"));
        assertFalse(s, s.contains("剩余：§f0"));
    }

    @Test
    public void weekLine_fullUsed_doneCopy() {
        String s = EmberConvertPath.weekLineFor(EmberConvertRules.WEEKLY_CAP, false);
        assertTrue(s, s.contains("环已完成"));
        assertFalse(s, s.contains("容错待用"));
    }

    @Test
    public void weekLine_underCap_oldCopy() {
        String s = EmberConvertPath.weekLineFor(0, true);
        assertTrue(s, s.contains("本周转化剩余"));
        assertTrue(s, s.contains("1/" + EmberConvertRules.WEEKLY_CAP));
        assertFalse(s, s.contains("容错待用"));
    }

    @Test
    public void weekLine_livePlayerData_fork() {
        PlayerData ready = atCapSoftReady();
        String a = EmberConvertPath.weekLine(ready);
        assertTrue(a, a.contains("容错待用"));
        assertTrue(EmberConvertCapSoft.ready(ready)); // weekLine is peek

        PlayerData used = atCapSoftUsed();
        String b = EmberConvertPath.weekLine(used);
        assertTrue(b, b.contains("环已完成"));
    }

    @Test
    public void weekRemainFrag_andAb_soft() {
        String frag = EmberConvertPath.weekRemainFrag(EmberConvertRules.WEEKLY_CAP, true);
        assertTrue(frag, frag.contains("容错待用"));
        assertTrue(frag, frag.contains("还可 1 次"));
        assertFalse(frag, frag.contains("剩余 §f0"));

        String ab = EmberConvertPath.weekRemainAb(EmberConvertRules.WEEKLY_CAP, true);
        assertTrue(ab, ab.contains("容错待用"));
        assertTrue(ab, ab.contains("还可 1 次"));

        String after = EmberConvertPath.weekRemainAbAfterSet(EmberConvertRules.WEEKLY_CAP, true);
        assertTrue(after, after.contains("容错待用"));

        // under cap — old
        assertTrue(EmberConvertPath.weekRemainFrag(0, true).contains("剩余 §f1"));
        assertTrue(EmberConvertPath.weekRemainAb(0, false).contains("周余 §f1"));
    }

    @Test
    public void refusal_softOpensGate() {
        EmberItemData blade = new EmberItemData(
                "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                EmberItemData.templateId("scorch", "blade", 1), "scorch", "blade", 1,
                1, 1, 0, 0, true, "drop", 1, 1);
        int used = EmberConvertRules.WEEKLY_CAP;
        assertNotNull(EmberConvertRules.refusal(blade, "burst", used, true));
        assertNull(EmberConvertRules.refusal(blade, "burst", used - 1, true));
    }

    @Test
    public void non_constantsUnchanged() {
        assertEquals(1, EmberConvertRules.WEEKLY_CAP);
        assertEquals("p4_conv", EmberConvertRules.C_CONV);
        assertEquals("p1_convert_cap_soft", EmberConvertCapSoft.C_USED);
    }
}

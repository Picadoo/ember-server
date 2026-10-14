package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DK D13342 W1a: PledgePath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 pledgepath/pledge/modes 主 UI / 零改伤害CD掉落体力价/誓约语义.
 */
public class EmberPledgePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBarePledgepathOrModes() {
        assertEquals("/ember_p1_pledge_path", EmberPledgePath.PATH_PICK_CMD);
        assertEquals("[誓约路径]", EmberPledgePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_pledge", EmberPledgePath.PATH_PLEDGE_CMD);
        assertEquals("/ember_p1_modes", EmberPledgePath.PATH_MODES_CMD);
        assertEquals("/ember_p1_adventure", EmberPledgePath.PATH_ADVENTURE_CMD);
        assertFalse(EmberPledgePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberPledgePath.PATH_PICK_CMD.contains("pledgepath"));
        assertFalse(EmberPledgePath.PATH_PLEDGE_CMD.contains("/corerpg p1 pledge"));
        assertFalse(EmberPledgePath.PATH_MODES_CMD.contains("/corerpg p1 modes"));
    }

    @Test
    public void glance_noTeachBarePledgepath() {
        String locked = EmberPledgePath.glance(EmberPledgePath.NONE, false);
        assertTrue(locked.contains("Q06"));
        assertFalse(locked.contains("/corerpg p1 pledgepath"));
        String none = EmberPledgePath.glance(EmberPledgePath.NONE, true);
        assertTrue(none.contains("点誓约路径页"));
        assertFalse(none.contains("/corerpg p1 pledgepath"));
        String lean = EmberPledgePath.glance(EmberPledgePath.LEAN, true);
        assertTrue(lean.contains("限药"));
        assertFalse(lean.contains("/corerpg p1 pledgepath"));
        String rev = EmberPledgePath.glance(EmberPledgePath.REVERSE, true);
        assertTrue(rev.contains("逆行"));
        assertFalse(rev.contains("/corerpg p1 pledgepath"));
        String both = EmberPledgePath.glance(EmberPledgePath.BOTH, true);
        assertTrue(both.contains("限药+逆行") || both.contains("双挂") || both.contains("限药"));
        assertFalse(both.contains("/corerpg p1 pledgepath"));
    }

    @Test
    public void menuLine_labelTipReuse() {
        assertEquals("誓约路径 · 未选", EmberPledgePath.menuLine(EmberPledgePath.NONE));
        assertEquals("誓约路径 · 限药誓约", EmberPledgePath.menuLine(EmberPledgePath.LEAN));
        assertEquals("誓约路径 · 逆行誓约", EmberPledgePath.menuLine(EmberPledgePath.REVERSE));
        assertEquals("誓约路径 · 限药+逆行", EmberPledgePath.menuLine(EmberPledgePath.BOTH));
        assertEquals("限药誓约", EmberPledgePath.label(EmberPledgePath.LEAN));
        assertEquals("逆行誓约", EmberPledgePath.label(EmberPledgePath.REVERSE));
        assertEquals("限药+逆行", EmberPledgePath.label(EmberPledgePath.BOTH));
        assertEquals("未选", EmberPledgePath.label(EmberPledgePath.NONE));
        assertTrue(EmberPledgePath.tip(EmberPledgePath.LEAN).contains("3 瓶"));
        assertTrue(EmberPledgePath.tip(EmberPledgePath.REVERSE).contains("互换"));
        assertTrue(EmberPledgePath.tip(EmberPledgePath.BOTH).contains("两条"));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBarePledgeModesMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPledgePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_pledge_path"));
        assertTrue(src.contains("TIP-DK"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 pledgepath"));
        assertFalse(offerBody.contains("pledgepath lean"));
        assertFalse(offerBody.contains("pledgepath reverse"));
        assertFalse(offerBody.contains("pledgepath both"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_PLEDGE_CMD") || applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 pledgepath"));
        assertFalse(applyBody.contains("\"/corerpg p1 pledge\""));
        assertFalse(applyBody.contains("\"/corerpg p1 modes\""));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPledgePath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static void scheduleOfferAfterQ06", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ06");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inPledgePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberPledgePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DK"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBarePledgepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("if (\"pledgepath\".equals(sub)");
        assertTrue(block > 0);
        int end = src.indexOf("if (\"duallead\".equals(sub)", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点誓约路径页"));
        assertFalse(body.contains("改路径再 /corerpg p1 pledgepath"));
        assertFalse(body.contains("用法：/corerpg p1 pledgepath <lean|reverse|both|clear>"));
        assertTrue(body.contains("用法：点誓约路径页选限药/逆行/双挂/清空"));
    }
}

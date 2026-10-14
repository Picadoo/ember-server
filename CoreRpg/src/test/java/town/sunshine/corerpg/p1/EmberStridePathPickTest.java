package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DM D13344 W1a: StridePath.offerPick / applyAndReply / glance 选打菜单化 —
 * 开薄页 / 禁 ConfirmTokens 教 stridepath/enter/flexpath/steppath 主 UI / 零改伤害CD掉落体力价/步态规则语义(D499).
 */
public class EmberStridePathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareStridepathOrEnter() {
        assertEquals("/ember_p1_stride_path", EmberStridePath.PATH_PICK_CMD);
        assertEquals("[步态路径]", EmberStridePath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberStridePath.PATH_ADVENTURE_CMD);
        assertFalse(EmberStridePath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberStridePath.PATH_PICK_CMD.contains("stridepath"));
        assertFalse(EmberStridePath.PATH_ADVENTURE_CMD.contains("/corerpg p1"));
    }

    @Test
    public void glance_noTeachBareStridepath() {
        String locked = EmberStridePath.glance(EmberStridePath.NONE, false);
        assertTrue(locked.contains("Q05"));
        assertFalse(locked.contains("/corerpg p1 stridepath"));
        String none = EmberStridePath.glance(EmberStridePath.NONE, true);
        assertTrue(none.contains("点步态路径页"));
        assertFalse(none.contains("/corerpg p1 stridepath"));
        String push = EmberStridePath.glance(EmberStridePath.PUSH, true);
        assertTrue(push.contains("突进踏"));
        assertFalse(push.contains("/corerpg p1 stridepath"));
        String bail = EmberStridePath.glance(EmberStridePath.BAIL, true);
        assertTrue(bail.contains("后撤踏"));
        assertFalse(bail.contains("/corerpg p1 stridepath"));
        String bare = EmberStridePath.glance(EmberStridePath.BARE, true);
        assertTrue(bare.contains("卸轻技"));
        assertFalse(bare.contains("/corerpg p1 stridepath"));
    }

    @Test
    public void menuLine_labelTipReuse_flexStepSemanticsUnchanged() {
        assertEquals("步态路径 · 未选", EmberStridePath.menuLine(EmberStridePath.NONE));
        assertEquals("步态路径 · 突进踏", EmberStridePath.menuLine(EmberStridePath.PUSH));
        assertEquals("步态路径 · 后撤踏", EmberStridePath.menuLine(EmberStridePath.BAIL));
        assertEquals("步态路径 · 卸轻技", EmberStridePath.menuLine(EmberStridePath.BARE));
        assertEquals("突进踏", EmberStridePath.label(EmberStridePath.PUSH));
        assertEquals("后撤踏", EmberStridePath.label(EmberStridePath.BAIL));
        assertEquals("卸轻技", EmberStridePath.label(EmberStridePath.BARE));
        assertEquals("未选", EmberStridePath.label(EmberStridePath.NONE));
        assertTrue(EmberStridePath.tip(EmberStridePath.PUSH).contains("前冲"));
        assertTrue(EmberStridePath.tip(EmberStridePath.BAIL).contains("后撤"));
        assertTrue(EmberStridePath.tip(EmberStridePath.BARE).contains("卸下轻技"));
        // D499 flex+step write mapping must stay
        assertEquals(EmberFlexPath.ON, EmberStridePath.flexId(EmberStridePath.PUSH));
        assertEquals(EmberStepPath.FORWARD, EmberStridePath.stepId(EmberStridePath.PUSH));
        assertEquals(EmberFlexPath.ON, EmberStridePath.flexId(EmberStridePath.BAIL));
        assertEquals(EmberStepPath.BACK, EmberStridePath.stepId(EmberStridePath.BAIL));
        assertEquals(EmberFlexPath.OFF, EmberStridePath.flexId(EmberStridePath.BARE));
        assertEquals(EmberStepPath.FORWARD, EmberStridePath.stepId(EmberStridePath.BARE));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noBareStrideEnterFlexStepMainUi() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStridePath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_stride_path"));
        assertTrue(src.contains("TIP-DM"));

        int offer = src.indexOf("public static void offerPick(Player p)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 stridepath"));
        assertFalse(offerBody.contains("stridepath push"));
        assertFalse(offerBody.contains("stridepath bail"));
        assertFalse(offerBody.contains("stridepath bare"));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("private static PlayerData dataOf", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 stridepath"));
        assertFalse(applyBody.contains("enterCmd(id)"));
        assertFalse(applyBody.contains("\"/corerpg p1 enter"));
        assertFalse(applyBody.contains("\"/corerpg p1 flexpath"));
        assertFalse(applyBody.contains("\"/corerpg p1 steppath"));
        // D499 write still present
        assertTrue(applyBody.contains("EmberFlexPath.set"));
        assertTrue(applyBody.contains("EmberStepPath.set"));
        assertTrue(applyBody.contains("flexId(id)"));
        assertTrue(applyBody.contains("stepId(id)"));
    }

    @Test
    public void maybeOfferWeekly_stillCallsOfferPick_zeroChangeSchedule() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStridePath.java")),
                StandardCharsets.UTF_8);
        int weekly = src.indexOf("public static boolean maybeOfferWeekly");
        assertTrue(weekly > 0);
        String weeklyBody = src.substring(weekly, src.indexOf("public static void scheduleOfferAfterQ05", weekly));
        assertTrue(weeklyBody.contains("offerPick(p)"));
        int sched = src.indexOf("public static void scheduleOfferAfterQ05");
        assertTrue(sched > 0);
        String schedBody = src.substring(sched, src.indexOf("public static void applyAndReply", sched));
        assertTrue(schedBody.contains("maybeOfferWeekly"));
    }

    @Test
    public void dmgCdDropStaminaUntouched_inStridePath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberStridePath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("hpMul"));
        assertFalse(src.contains("dmgMul"));
        assertFalse(src.contains("ECONOMY_BV"));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DM"));
        assertTrue(src.contains("flexId"));
        assertTrue(src.contains("stepId"));
        assertEquals(123, EmberEconomy.ECONOMY_BV);
    }

    @Test
    public void commandSource_nudgeUsageNoTeachBareStridepath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberCommand.java")),
                StandardCharsets.UTF_8);
        int block = src.indexOf("if (\"stridepath\".equals(sub)");
        assertTrue(block > 0);
        int end = src.indexOf("if (\"tunepath\".equals(sub)", block);
        assertTrue(end > block);
        String body = src.substring(block, end);
        assertTrue(body.contains("点步态路径页"));
        assertFalse(body.contains("用法：/corerpg p1 stridepath <push|bail|bare|clear>"));
        assertTrue(body.contains("用法：点步态路径页选突进踏/后撤踏/卸轻技/清空"));
        assertFalse(body.contains("改路径再 /corerpg p1 stridepath"));
    }
}

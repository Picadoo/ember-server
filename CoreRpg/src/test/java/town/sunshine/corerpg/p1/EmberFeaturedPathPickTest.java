package town.sunshine.corerpg.p1;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * TIP-DE D13336 W1a: FeaturedPath.offerPick / applyAndReply / glance / enterCue 选打菜单化 —
 * 开薄页 / 清裸指令主 UI / 零改掉落体力价周帽.
 */
public class EmberFeaturedPathPickTest {

    @Test
    public void pathPickConstants_menuOpen_notBareFeaturepathEnter() {
        assertEquals("/ember_p1_featured_path", EmberFeaturedPath.PATH_PICK_CMD);
        assertEquals("[精选路径]", EmberFeaturedPath.PATH_PICK_LABEL);
        assertEquals("/ember_p1_adventure", EmberFeaturedPath.PATH_ADVENTURE_CMD);
        assertEquals("/ember_p1_forge", EmberFeaturedPath.PATH_FORGE_CMD);
        assertFalse(EmberFeaturedPath.PATH_PICK_CMD.contains("corerpg p1"));
        assertFalse(EmberFeaturedPath.PATH_PICK_CMD.contains("featurepath"));
        assertFalse(EmberFeaturedPath.PATH_PICK_CMD.contains("enter"));
        assertFalse(EmberFeaturedPath.PATH_ADVENTURE_CMD.contains("/corerpg p1 runs"));
        assertFalse(EmberFeaturedPath.PATH_FORGE_CMD.contains("/corerpg p1 forge"));
    }

    @Test
    public void glance_noTeachBareFeaturepath() {
        String locked = EmberFeaturedPath.glance(EmberFeaturedPath.NONE, false, "本周精选");
        assertTrue(locked.contains("Q01"));
        assertFalse(locked.contains("/corerpg p1 featurepath"));
        String none = EmberFeaturedPath.glance(EmberFeaturedPath.NONE, true, "Q03 试");
        assertTrue(none.contains("点精选路径页"));
        assertFalse(none.contains("/corerpg p1 featurepath"));
        assertTrue(none.contains("Q03 试"));
        String chal = EmberFeaturedPath.glance(EmberFeaturedPath.CHALLENGE, true, "Q05");
        assertTrue(chal.contains("精选·挑战"));
        assertFalse(chal.contains("/corerpg p1 featurepath"));
    }

    @Test
    public void menuLine_labelTipReuse() {
        assertEquals("本周精选 · 未选", EmberFeaturedPath.menuLine(EmberFeaturedPath.NONE, null));
        assertEquals("本周精选 · 未选", EmberFeaturedPath.menuLine(EmberFeaturedPath.NONE, ""));
        assertEquals("Q02 锈轨 · 精选·挑战",
                EmberFeaturedPath.menuLine(EmberFeaturedPath.CHALLENGE, "Q02 锈轨"));
        assertEquals("Q04 · 精选·普通",
                EmberFeaturedPath.menuLine(EmberFeaturedPath.NORMAL, "Q04"));
        assertEquals("精选·挑战", EmberFeaturedPath.label(EmberFeaturedPath.CHALLENGE));
        assertEquals("精选·普通", EmberFeaturedPath.label(EmberFeaturedPath.NORMAL));
        assertEquals("未选", EmberFeaturedPath.label(EmberFeaturedPath.NONE));
        assertTrue(EmberFeaturedPath.tip(EmberFeaturedPath.CHALLENGE).contains("挑战")
                || EmberFeaturedPath.tip(EmberFeaturedPath.CHALLENGE).contains("印记"));
    }

    @Test
    public void offerPickAndApplySource_opensThinPage_noTeachBareFeaturepathEnter() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFeaturedPath.java")),
                StandardCharsets.UTF_8);
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("openPathPick"));
        assertTrue(src.contains("ember_p1_featured_path"));

        int offer = src.indexOf("public static void offerPick(Player p, String featuredLabel)");
        assertTrue(offer > 0);
        int offerEnd = src.indexOf("public static boolean maybeOfferWeekly", offer);
        assertTrue(offerEnd > offer);
        String offerBody = src.substring(offer, offerEnd);
        assertTrue(offerBody.contains("openPathPick"));
        assertFalse(offerBody.contains("\"/corerpg p1 featurepath challenge\""));
        assertFalse(offerBody.contains("\"/corerpg p1 featurepath normal\""));
        assertFalse(offerBody.contains("\"/corerpg p1 featurepath clear\""));

        int apply = src.indexOf("public static void applyAndReply(Player p, int id)");
        assertTrue(apply > 0);
        int applyEnd = src.indexOf("/** D790: enter featured map", apply);
        assertTrue(applyEnd > apply);
        String applyBody = src.substring(apply, applyEnd);
        assertTrue(applyBody.contains("PATH_PICK_CMD"));
        assertTrue(applyBody.contains("PATH_ADVENTURE_CMD"));
        assertFalse(applyBody.contains("\"/corerpg p1 featurepath"));
        assertFalse(applyBody.contains("\"/corerpg p1 enter "));
        assertFalse(applyBody.contains("\"/corerpg p1 runs\""));

        int cue = src.indexOf("public static void enterCue(");
        assertTrue(cue > 0);
        int cueEnd = src.indexOf("/**\n     * D790: after featured-map clear", cue);
        if (cueEnd < 0) cueEnd = src.indexOf("public static void afterSettle", cue);
        assertTrue(cueEnd > cue);
        String cueBody = src.substring(cue, cueEnd);
        assertTrue(cueBody.contains("点精选路径页"));
        assertFalse(cueBody.contains("/corerpg p1 featurepath"));
    }

    @Test
    public void dropStaminaCapUntouched_inFeaturedPath() throws Exception {
        String src = new String(Files.readAllBytes(
                Paths.get("src/main/java/town/sunshine/corerpg/p1/EmberFeaturedPath.java")),
                StandardCharsets.UTF_8);
        assertFalse(src.contains("staminaCost"));
        assertFalse(src.contains("rotationWeeklyCap"));
        assertFalse(src.contains("lootFamily ="));
        assertTrue(src.contains("PATH_PICK_CMD"));
        assertTrue(src.contains("TIP-DE"));
    }
}

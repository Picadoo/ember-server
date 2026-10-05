package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/** Boss / caster telegraph shapes: the damage area never exceeds the drawn warning (book ch. 11–13 §7). */
public class EmberRunShapeTest {

    private static EmberRunMaps.Skill skill(Object... kv) {
        Map<String, Object> m = new HashMap<String, Object>();
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return new EmberRunMaps.Skill(m);
    }

    private static final Location O = new Location(null, 0, 65, 0);
    private static final Vector NORTH = new Vector(0, 0, 1);

    private static boolean hit(EmberRunMaps.Skill s, double x, double z) {
        return EmberRunDirector.inShape(s, O, NORTH, new Location(null, x, 65, z));
    }

    @Test public void q01CleaveIs100DegreesFourBlocks() {
        EmberRunMaps.Skill s = skill("type", "cone", "angle", 100, "range", 4);
        assertTrue(hit(s, 0, 3.9));
        assertTrue(hit(s, Math.sin(Math.toRadians(49)) * 3, Math.cos(Math.toRadians(49)) * 3));
        assertFalse(hit(s, Math.sin(Math.toRadians(52)) * 3, Math.cos(Math.toRadians(52)) * 3));
        assertFalse(hit(s, 0, 4.2));
        assertFalse(hit(s, 0, -2));
        assertFalse(EmberRunDirector.inShape(s, O, NORTH, new Location(null, 0, 68, 2))); // 3 blocks above
    }

    @Test public void q02SmashIsRadiusThreeTwoAhead() {
        EmberRunMaps.Skill s = skill("type", "circle", "radius", 3, "ahead", 2);
        assertTrue(hit(s, 0, 4.9));
        assertTrue(hit(s, 0, -0.9));
        assertFalse(hit(s, 0, 5.2));
        assertFalse(hit(s, 3.1, 2));
    }

    @Test public void q03LineIsFiveByThree() {
        EmberRunMaps.Skill s = skill("type", "line", "length", 5, "width", 3);
        assertTrue(hit(s, 1.4, 4.9));
        assertFalse(hit(s, 1.6, 2));
        assertFalse(hit(s, 0, 5.2));
        assertFalse(hit(s, 0, -1));
        EmberRunMaps.Skill caster = skill("type", "line", "length", 6, "width", 1.5);
        assertTrue(hit(caster, 0.7, 5.9));
        assertFalse(hit(caster, 0.8, 3));
    }

    @Test public void q04ImpactCircleIsCentredOnTheLockedPoint() {
        EmberRunMaps.Skill s = skill("type", "circle", "target", "player", "radius", 2.5, "every", 10, "warn", 1.2);
        assertEquals("player", s.target);
        assertEquals(0, s.ahead, 0);
        assertTrue(hit(s, 0, 2.4));
        assertTrue(hit(s, -1.7, -1.7));
        assertFalse(hit(s, 0, 2.6));
    }

    @Test public void q05RadialBurstAndSweep() {
        EmberRunMaps.Skill burst = skill("type", "circle", "radius", 3, "kb", 1);
        assertTrue(hit(burst, 0, -2.9));
        assertFalse(hit(burst, 2.2, 2.2));
        EmberRunMaps.Skill sweep = skill("type", "cone", "angle", 120, "range", 5, "kb", 0.5);
        assertTrue(hit(sweep, Math.sin(Math.toRadians(59)) * 4.9, Math.cos(Math.toRadians(59)) * 4.9));
        assertFalse(hit(sweep, Math.sin(Math.toRadians(62)) * 4, Math.cos(Math.toRadians(62)) * 4));
        assertFalse(hit(sweep, 0, 5.1));
    }

    @Test public void knockbackIsCappedAtOneBlock() {
        assertEquals(1.0, skill("kb", 3).kb, 0);
        assertEquals(0.0, skill("kb", -1).kb, 0);
        assertEquals(0.5, skill("kb", 0.5).kb, 0);
        assertEquals(0.0, skill().kb, 0);
        // push target must have a floor and two free blocks
        assertTrue(EmberRunDirector.standableIds(true, false, false));
        assertFalse(EmberRunDirector.standableIds(false, false, false)); // ledge / stair shaft
        assertFalse(EmberRunDirector.standableIds(true, true, false));   // wall
        assertFalse(EmberRunDirector.standableIds(true, false, true));
    }

    /** §10.I: cooldowns from the fight start; same-tick → first listed wins, the other stays due (not skipped). */
    @Test public void skillSchedulerPriorityAndAnchoring() {
        long t0 = 0;
        long[] next = {t0 + 10000, t0 + 14000}; // Q04: circle every 10 s, push every 14 s
        assertEquals(-1, EmberRunDirector.dueSkill(next, 9999));
        assertEquals(0, EmberRunDirector.dueSkill(next, 10000));
        next[0] = EmberRunDirector.nextDue(next[0], 10000, 10000);
        assertEquals(20000, next[0]);
        assertEquals(1, EmberRunDirector.dueSkill(next, 14000));
        next[1] = EmberRunDirector.nextDue(next[1], 14000, 14000);
        assertEquals(28000, next[1]);
        // t = 70 s: both due → circle first; push still due right after, keeps its 14 s grid (84 s next)
        next[0] = 70000; next[1] = 70000;
        assertEquals(0, EmberRunDirector.dueSkill(next, 70000));
        next[0] = EmberRunDirector.nextDue(next[0], 10000, 70000);
        assertEquals(1, EmberRunDirector.dueSkill(next, 71700));
        assertEquals(84000, EmberRunDirector.nextDue(next[1], 14000, 71700));
        // a long stall skips missed slots instead of firing a burst of casts
        assertEquals(100000, EmberRunDirector.nextDue(70000, 10000, 95000));
    }

    /** D194: a half-HP gated skill (every 25 s, spawn grid 25/50/75…) whose gate opens at 68 s fires then and next at
     *  93 s — not on the stale grid at 75 s (the 2nd 炉心聚爆 ~7 s after the 1st in the 09:44 smoke). Later casts keep the grid. */
    @Test public void gatedSkillReanchorsOnFirstCast() {
        assertEquals(75000, EmberRunDirector.nextDue(25000, 25000, 68000));           // old behaviour
        assertEquals(93000, EmberRunDirector.gatedNext(25000, 25000, 68000, true));   // first cast after the gate
        assertEquals(118000, EmberRunDirector.gatedNext(93000, 25000, 93000, false)); // then its own 25 s grid
        assertEquals(20000, EmberRunDirector.gatedNext(10000, 10000, 10000, false));  // ungated: unchanged §10.I grid
        assertEquals(Long.MAX_VALUE / 4, EmberRunDirector.gatedNext(0, 0, 5000, true));
    }

    /** Q06 两段刀气 (book ch. 16 §7): band 6 long × 3 wide, 1..7 ahead; second band 4 to the right, left stays safe. */
    @Test public void q06BladeBandsStartOneAheadAndShiftRight() {
        EmberRunMaps.Skill s = skill("type", "line", "start", 1, "length", 6, "width", 3);
        assertTrue(hit(s, 0, 1.1));
        assertTrue(hit(s, 1.4, 6.9));
        assertFalse(hit(s, 0, 0.5));   // the first block in front of the boss is outside the band
        assertFalse(hit(s, 0, 7.2));
        assertFalse(hit(s, 1.6, 4));
        // facing +z (south): right-hand side is −x (west)
        Vector r = EmberRunDirector.rightOf(NORTH);
        assertEquals(-1, r.getX(), 1e-9);
        assertEquals(0, r.getZ(), 1e-9);
        Location o2 = O.clone().add(r.clone().multiply(4));
        assertTrue(EmberRunDirector.inShape(s, o2, NORTH, new Location(null, -4, 65, 4)));
        assertFalse(EmberRunDirector.inShape(s, o2, NORTH, new Location(null, 0, 65, 4))); // first band's centre
        assertFalse(EmberRunDirector.inShape(s, O, NORTH, new Location(null, 2, 65, 4)));   // left of band 1
        assertFalse(EmberRunDirector.inShape(s, o2, NORTH, new Location(null, 2, 65, 4)));  // … and of band 2
        // the YAML follow keeps the direction and only slides
        Map<String, Object> f = new HashMap<String, Object>();
        f.put("type", "line"); f.put("start", 1); f.put("length", 6); f.put("width", 3); f.put("shift", 4); f.put("delay", 1.0);
        EmberRunMaps.Skill both = skill("type", "line", "start", 1, "length", 6, "width", 3, "follow", f);
        assertEquals(4, both.follow.shift, 0);
        assertTrue("follow without below fires at full HP", 1.0 < both.follow.below);
        assertTrue(Double.isNaN(s.shift));
    }

    /** Q07 冲撞: ≤ 8-block strip 4 wide from the boss; clipped length = strip length; 重砸 own 1.5 s recovery. */
    @Test public void q07ChargeStripAndSlamRecovery() {
        EmberRunMaps.Skill c = skill("type", "charge", "length", 8, "width", 4);
        assertTrue(hit(c, 1.9, 7.9));
        assertFalse(hit(c, 2.1, 4));
        assertFalse(hit(c, 0, 8.3));
        EmberRunMaps.Skill clipped = c.withLength(3.5);
        assertTrue(EmberRunDirector.inShape(clipped, O, NORTH, new Location(null, 0, 65, 3.4)));
        assertFalse(EmberRunDirector.inShape(clipped, O, NORTH, new Location(null, 0, 65, 3.6)));
        EmberRunMaps.Skill slam = skill("type", "circle", "target", "player", "radius", 3.5, "recover", 1.5);
        assertEquals(1.5, slam.recover, 0);
        assertTrue(Double.isNaN(c.recover));
        assertTrue(hit(slam, 0, 3.4));
        assertFalse(hit(slam, 2.6, 2.6));
    }

    @Test public void challengeLightFlagAndDamageCopy() {
        EmberRunMaps.Skill heavy = skill("type", "cone", "dmg", 44);
        EmberRunMaps.Skill light = skill("type", "circle", "dmg", 26, "light", true);
        assertFalse(heavy.light);
        assertTrue(light.light);
        assertEquals(72, heavy.withDmg(72).dmg, 0);
        assertEquals(44, heavy.dmg, 0);
    }

    @Test public void chargeRunKeepsTheBossBoxOutOfWalls_B2165() {
        // Q07 hall: floor z >= 41 (wall z = 40), x <= 5 (wall x = 6); boss at the hall point (0.5, 46.5)
        EmberRunDirector.GroundTest hall = (x, z) -> z >= 41 && x < 6 && x >= -5;
        assertEquals(5.0, EmberRunDirector.clearRunGrid(hall, 0.5, 46.5, 0, -1, 8, EmberRunDirector.BOSS_HALF_WIDTH), 1e-9);
        assertEquals(5.5, EmberRunDirector.clearRunGrid(hall, 0.5, 46.5, 0, -1, 8, 0), 1e-9); // old centre-only check
        assertEquals(5.0, EmberRunDirector.clearRunGrid(hall, 0.5, 46.5, 1, 0, 8, EmberRunDirector.BOSS_HALF_WIDTH), 1e-9);
        // hugging the side wall: the sideways edge is already in the wall → no run at all
        assertEquals(0, EmberRunDirector.clearRunGrid(hall, 5.75, 46.5, 0, 1, 8, EmberRunDirector.BOSS_HALF_WIDTH), 1e-9);
        assertTrue(EmberRunDirector.CHARGE_MIN >= 2.0);
    }

    @Test public void wallCrashOnlyAtARealWall_D188() {
        // same Q07 hall: solid wall blocks at z <= 40 and x >= 6; west side x < -5 is a ledge (no floor, not solid)
        EmberRunDirector.GroundTest wall = (x, z) -> z < 41 || x >= 6;
        double h = EmberRunDirector.BOSS_HALF_WIDTH;
        // charge south into the z = 40 wall: run 5.0 of 8 → the next step's front probe is in the wall → crash
        assertTrue(EmberRunDirector.crashGrid(wall, 0.5, 46.5, 0, -1, 5.0, 8, h));
        // the full length ran out → no crash
        assertFalse(EmberRunDirector.crashGrid(wall, 0.5, 46.5, 0, -1, 8.0, 8, h));
        // stopped at the west ledge (floor ends, nothing solid) → no crash
        assertFalse(EmberRunDirector.crashGrid(wall, 0.5, 46.5, -1, 0, 5.0, 8, h));
        // stopped by the invisible boss-area edge in open floor → no crash
        assertFalse(EmberRunDirector.crashGrid(wall, 0.5, 52.5, 0, 1, 3.0, 8, h));
    }

    @Test public void wallStunParsesOnChargeOnlyAndIsCapped_D188() {
        assertEquals(2.0, skill("type", "charge", "wall_stun", 2.0).wallStun, 0);
        assertEquals(0.0, skill("type", "charge").wallStun, 0);
        assertEquals(0.0, skill("type", "cone", "wall_stun", 2.0).wallStun, 0); // not a dash → never a crash
        assertEquals(3.0, skill("type", "charge", "wall_stun", 9).wallStun, 0);  // ≤ 3 s
        EmberRunMaps.Skill c = skill("type", "charge", "wall_stun", 2.0, "length", 8);
        assertEquals(2.0, c.withLength(5).wallStun, 0); // clipped copy keeps it
        assertEquals(2.0, c.withDmg(79).wallStun, 0);   // challenge copy keeps it
    }

    @Test public void whiffStunParsesOffChargeAndShareAndIsCapped_D192() {
        assertEquals(1.0, skill("type", "cone", "whiff_stun", 1.0).whiffStun, 0);
        assertEquals(0.0, skill("type", "cone").whiffStun, 0);
        assertEquals(0.0, skill("type", "charge", "whiff_stun", 1.0).whiffStun, 0); // a charge has the wall stun instead
        assertEquals(0.0, skill("type", "circle", "share", true, "whiff_stun", 1.0).whiffStun, 0); // share wants people in
        assertEquals(2.0, skill("type", "line", "whiff_stun", 9).whiffStun, 0); // ≤ 2 s
        EmberRunMaps.Skill c = skill("type", "circle", "whiff_stun", 1.0);
        assertEquals(1.0, c.withDmg(79).whiffStun, 0); // challenge copy keeps it
    }

    @Test public void whiffOnlyWhenArmedAndNobodyHit_D192() {
        EmberRunMaps.Skill sk = skill("type", "cone", "whiff_stun", 1.0);
        assertTrue(EmberRunDirector.whiffs(true, 0, sk));
        assertFalse(EmberRunDirector.whiffs(true, 1, sk));  // someone got hit
        assertFalse(EmberRunDirector.whiffs(false, 0, sk)); // nobody was in it at warn start (standing far away ≠ dodge)
        assertFalse(EmberRunDirector.whiffs(true, 0, skill("type", "cone")));
        assertFalse(EmberRunDirector.whiffs(true, 0, null));
    }

    @Test public void breakParsesOffChargeAndShareAndIsCapped_D193() {
        EmberRunMaps.Skill c = skill("type", "circle", "break_hp", 0.065, "break_stun", 0.5);
        assertEquals(0.065, c.breakHp, 1e-9);
        assertEquals(0.5, c.breakStun, 0);
        assertEquals(1.0, skill("type", "circle", "break_hp", 0.1).breakStun, 0); // default stun 1 s
        assertEquals(0.0, skill("type", "circle").breakHp, 0);
        assertEquals(0.0, skill("type", "circle", "break_stun", 1.0).breakStun, 0); // no break_hp → no stun
        assertEquals(0.0, skill("type", "charge", "break_hp", 0.1).breakHp, 0);
        assertEquals(0.0, skill("type", "circle", "share", true, "break_hp", 0.1).breakHp, 0);
        assertEquals(0.5, skill("type", "circle", "break_hp", 9).breakHp, 0);       // ≤ half the boss
        assertEquals(2.0, skill("type", "circle", "break_hp", 0.1, "break_stun", 9).breakStun, 0); // ≤ 2 s
        assertEquals(0.065, c.withDmg(61).breakHp, 1e-9); // challenge copy keeps it
        assertEquals(0.5, c.withDmg(61).breakStun, 0);
    }

    @Test public void brokenOnlyWhenArmedAndNeedReached_D193() {
        assertTrue(EmberRunDirector.broken(200, 200));
        assertTrue(EmberRunDirector.broken(200, 250));
        assertFalse(EmberRunDirector.broken(200, 199.9));
        assertFalse(EmberRunDirector.broken(0, 500)); // nothing armed
    }
}

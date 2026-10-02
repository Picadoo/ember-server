package town.sunshine.corerpg.p1.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class P1MapLayoutTest {

    private final P1MapLayout l = P1MapLayout.spireV1();

    @Test public void noProblems() { assertTrue(l.problems.toString(), l.problems.isEmpty()); }

    @Test public void everyCellReachableFromSpawnWithoutJumping() {
        assertEquals(1, l.maxStep());
        assertEquals(0, l.unreachableFrom(0, 5));
    }

    @Test public void bookNodes() {
        assertEquals(64, l.cell(0, 5).f);       // spawn (0,64,5)
        assertEquals(80, l.cell(0, 146).f);     // boss (0,80,146)
        assertEquals(64, l.cell(40, 33).f);     // C12 lower end
        assertEquals(72, l.cell(40, 47).f);     // C12 upper end
        assertEquals(72, l.cell(40, 79).f);
        assertEquals(80, l.cell(40, 95).f);
        assertEquals(72, l.cell(66, 63).f);     // E centre
        // ≥ 3 flat cells at both ends of each stair
        for (int z = 33; z <= 36; z++) assertEquals(64, l.cell(40, z).f);
        for (int z = 45; z <= 47; z++) assertEquals(72, l.cell(40, z).f);
        assertEquals(3, l.cell(40, 40).stair);   // ascending +z
        assertEquals(0, l.cell(40, 36).stair);
    }

    @Test public void corridorsAreSevenWide() {
        for (int x = 37; x <= 43; x++) assertTrue(l.cavity(x, 40));
        assertTrue(!l.cavity(36, 40) && !l.cavity(44, 40));
    }

    @Test public void doorPlanesInsideCavity() {
        for (int x = -3; x <= 3; x++) { assertTrue(l.cavity(x, 14)); assertTrue(l.cavity(x, 121)); assertTrue(l.cavity(37 + x + 3, 48)); }
        for (int z = 95; z <= 101; z++) assertTrue(l.cavity(14, z));
    }

    /** D15: every book map (ch. 11–17) builds without overlap, walks without jumping, and has the book rooms. */
    @Test public void allBookMapsWalkable() {
        for (String k : new String[] {"q01", "q02", "q03", "q04", "q05", "q06", "q07"}) {
            java.util.Map<String, Object> m = P1MapLayout.bookMap(k);
            assertTrue(k, m != null);
            P1MapLayout b = P1MapLayout.fromBook(m);
            assertTrue(k + " " + b.problems, b.problems.isEmpty());
            assertTrue(k, b.maxStep() <= 1);
            assertEquals(k, 0, b.unreachableFrom(b.spawnX, b.spawnZ));
            assertEquals(k, b.spawnF, b.cell(b.spawnX, b.spawnZ).f);
            assertTrue(k, b.boxX0 % 16 == 0 && (b.boxX1 + 1) % 16 == 0);
        }
        // Q03 descends 64 → 60 → 56 on stairs; RB hall 33×31 at F 56
        P1MapLayout q3 = P1MapLayout.fromBook(P1MapLayout.bookMap("q03"));
        assertEquals(56, q3.cell(0, 123).f);
        assertEquals(4, q3.cell(0, 16).stair);
        // the generic Q05 equals the hand-checked spire on every cell
        P1MapLayout g = P1MapLayout.fromBook(P1MapLayout.bookMap("q05"));
        for (int x = -40; x <= 90; x++) for (int z = -20; z <= 180; z++) {
            assertEquals(x + "," + z, l.cavity(x, z), g.cavity(x, z));
            if (l.cavity(x, z)) { assertEquals(l.cell(x, z).f, g.cell(x, z).f); assertEquals(l.cell(x, z).stair, g.cell(x, z).stair); }
        }
    }
}

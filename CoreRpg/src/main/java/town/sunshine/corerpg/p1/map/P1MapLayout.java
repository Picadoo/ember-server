package town.sunshine.corerpg.p1.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Book §10.B white-box rules as a pure height map (no Bukkit): the walkable cavity is the union of the room net
 * ranges and 7-wide corridor volumes; every cell has a foot level F (floor block at F−1) and a clear height. Corridor
 * segments between nodes of different F get one 1-block rise per cell, centred, leaving ≥ 3 flat cells at both ends
 * (book: "楼梯在上下端各留至少 3 格水平过渡"); rise cells are marked so the builder places stair blocks (no jumping).
 * Turn nodes get a full 7×7 landing because each segment rectangle is extended by the half width at both ends.
 */
public final class P1MapLayout {

    public static final int NONE = Integer.MIN_VALUE;

    public static final class Room {
        public final String id;
        public final int x0, x1, z0, z1, f, h;
        public final boolean roofed, lowGuard;
        public Room(String id, int x0, int x1, int z0, int z1, int f, int h, boolean roofed, boolean lowGuard) {
            this.id = id; this.x0 = x0; this.x1 = x1; this.z0 = z0; this.z1 = z1; this.f = f; this.h = h;
            this.roofed = roofed; this.lowGuard = lowGuard;
        }
    }

    public static final class Cell {
        public int f = NONE, h;
        public boolean roofed, lowGuard;
        /** 0 none, else ascending direction: 1 +x, 2 −x, 3 +z, 4 −z */
        public int stair;
        public String owner;
    }

    public final int minX, maxX, minZ, maxZ;
    private final Map<Long, Cell> cells = new HashMap<Long, Cell>();
    public final List<Room> rooms = new ArrayList<Room>();
    public final List<String> problems = new ArrayList<String>();
    /** Decor outside the walkable area, never enterable: {kind, a, b, c, d, y0, y1}; kind 0 = solid box x a..b z c..d with
     *  crenellated top, kind 1 = hollow round tube centre (a, c) radius b, kind 2 = window bars in existing wall x a..b z c..d (even z);
     *  {3|6|7, x0, x1, z0, z1, y0, y1, id, data}: 3 = block box anywhere (also inside the cavity: covers, beams), 6 = cap on
     *  wall tops at y0 (non-cavity cells with a block below), 7 = gable roof along z, ridge y1 at the x centre, eaves y0. */
    public final List<int[]> decor = new ArrayList<int[]>();
    /** Builder fill box (chunk aligned) and base terrain level. */
    public int boxX0, boxX1, boxZ0, boxZ1, ground = 62;
    public int spawnX, spawnF, spawnZ;

    public P1MapLayout(int minX, int maxX, int minZ, int maxZ) {
        this.minX = minX; this.maxX = maxX; this.minZ = minZ; this.maxZ = maxZ;
    }

    private static long k(int x, int z) { return ((long) x << 32) ^ (z & 0xffffffffL); }

    public Cell cell(int x, int z) { return cells.get(k(x, z)); }

    public boolean cavity(int x, int z) { Cell c = cell(x, z); return c != null && c.f != NONE; }

    private void put(int x, int z, int f, int h, boolean roofed, boolean lowGuard, int stair, String owner) {
        Cell c = cells.get(k(x, z));
        if (c == null) { c = new Cell(); cells.put(k(x, z), c); }
        if (c.f != NONE && c.f != f) {
            problems.add(owner + " overlaps " + c.owner + " at (" + x + "," + z + ") with F " + f + " vs " + c.f);
            return;
        }
        boolean wasRoom = c.f != NONE && c.owner != null && c.owner.startsWith("R");
        c.f = f;
        if (!wasRoom) { // rooms win over corridors for height / roof style
            c.h = Math.max(c.h, h); c.roofed = roofed; c.lowGuard = lowGuard; c.owner = owner;
        } else {
            c.h = Math.max(c.h, h);
        }
        if (stair != 0) c.stair = stair;
    }

    public P1MapLayout room(Room r) {
        rooms.add(r);
        for (int x = r.x0; x <= r.x1; x++)
            for (int z = r.z0; z <= r.z1; z++) put(x, z, r.f, r.h, r.roofed, r.lowGuard, 0, r.id);
        return this;
    }

    /** nodes: {x, F, z} foot nodes; width 7 (half 3), clear height h; consecutive nodes share x or z. */
    public P1MapLayout corridor(String id, int h, boolean roofed, int[]... nodes) {
        int half = 3;
        for (int i = 0; i + 1 < nodes.length; i++) {
            int[] a = nodes[i], b = nodes[i + 1];
            boolean alongZ = a[0] == b[0];
            if (!alongZ && a[2] != b[2]) { problems.add(id + " segment " + i + " is diagonal"); continue; }
            // landings only at the ends and at turns; a collinear continuation shares the node cell
            boolean turnIn = i == 0 || (nodes[i - 1][0] == a[0]) != alongZ;
            boolean turnOut = i + 2 >= nodes.length || (b[0] == nodes[i + 2][0]) != alongZ;
            int s0 = alongZ ? a[2] : a[0], s1 = alongZ ? b[2] : b[0];
            int dir = Integer.signum(s1 - s0);
            if (dir == 0) dir = 1;
            int len = Math.abs(s1 - s0), rise = b[1] - a[1], n = Math.abs(rise);
            int f0 = n == 0 ? 0 : (len - n) / 2; // flat cells before the first rise
            // a pure ramp segment (len == n) takes its ≥ 3 flat cells from the collinear neighbours (book Q03 C01 / C12)
            if (n > 0 && len > n && (f0 < 3 || len - n - f0 < 3)) problems.add(id + " segment " + i + ": " + n + " rise over " + len + " cells leaves < 3 flat at an end");
            if (n > len) problems.add(id + " segment " + i + ": rise " + n + " over " + len + " cells");
            int stairDir = n == 0 ? 0 : (alongZ ? (dir * Integer.signum(rise) > 0 ? 3 : 4) : (dir * Integer.signum(rise) > 0 ? 1 : 2));
            for (int t = turnIn ? -half : 0; t <= len + (turnOut ? half : 0); t++) {
                int s = s0 + dir * t;
                int f, st = 0;
                if (n == 0 || t <= f0) f = a[1];
                else if (t >= f0 + n) f = b[1];
                else f = a[1] + Integer.signum(rise) * (t - f0);
                // stair block on the higher cell of every 1-block rise, ascending toward it (no jumping)
                if (n > 0 && rise > 0 && t > f0 && t <= f0 + n) st = stairDir;
                if (n > 0 && rise < 0 && t >= f0 && t < f0 + n) st = stairDir;
                int c = alongZ ? a[0] : a[2];
                for (int w = -half; w <= half; w++) {
                    int x = alongZ ? c + w : s, z = alongZ ? s : c + w;
                    put(x, z, f, h, roofed, false, st, id);
                }
            }
        }
        return this;
    }

    /** Book M02: every cavity cell reachable from {@code from} by steps of at most one block (stairs) — returns unreachable count. */
    public int unreachableFrom(int x, int z) {
        java.util.Set<Long> seen = new java.util.HashSet<Long>();
        ArrayDeque<int[]> q = new ArrayDeque<int[]>();
        if (!cavity(x, z)) return -1;
        q.add(new int[] {x, z});
        seen.add(k(x, z));
        int[][] d = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!q.isEmpty()) {
            int[] p = q.poll();
            Cell c = cell(p[0], p[1]);
            for (int[] e : d) {
                int nx = p[0] + e[0], nz = p[1] + e[1];
                Cell o = cell(nx, nz);
                if (o == null || o.f == NONE || Math.abs(o.f - c.f) > 1 || !seen.add(k(nx, nz))) continue;
                q.add(new int[] {nx, nz});
            }
        }
        int total = 0;
        for (Cell c : cells.values()) if (c.f != NONE) total++;
        return total - seen.size();
    }

    /** Max |ΔF| between neighbouring cavity cells (must be ≤ 1). */
    public int maxStep() {
        int m = 0;
        for (Map.Entry<Long, Cell> e : cells.entrySet()) {
            if (e.getValue().f == NONE) continue;
            int x = (int) (e.getKey() >> 32), z = (int) (long) e.getKey().longValue();
            Cell r = cell(x + 1, z), s = cell(x, z + 1);
            if (r != null && r.f != NONE) m = Math.max(m, Math.abs(r.f - e.getValue().f));
            if (s != null && s.f != NONE) m = Math.max(m, Math.abs(s.f - e.getValue().f));
        }
        return m;
    }

    /**
     * Generic book map (ch. 11–17 tables, generated into p1-book-maps.yml by tools/p1map/gen.py): rooms open-topped
     * unless {@code roofed}, corridors roofed 7×6, RB backdrop 23 wide behind the north wall, box = bounds + margin.
     */
    @SuppressWarnings("unchecked")
    public static P1MapLayout fromBook(Map<String, Object> m) {
        List<Map<String, Object>> rs = (List<Map<String, Object>>) m.get("rooms");
        List<Map<String, Object>> cs = (List<Map<String, Object>>) m.get("corridors");
        boolean roofed = Boolean.TRUE.equals(m.get("roofed"));
        java.util.Set<String> guards = new java.util.HashSet<String>();
        if (m.get("low_guard") instanceof List) for (Object o : (List<Object>) m.get("low_guard")) guards.add(String.valueOf(o));
        java.util.Set<String> roofs = new java.util.HashSet<String>();
        if (m.get("roofed_rooms") instanceof List) for (Object o : (List<Object>) m.get("roofed_rooms")) roofs.add(String.valueOf(o));
        int x0 = Integer.MAX_VALUE, x1 = Integer.MIN_VALUE, z0 = Integer.MAX_VALUE, z1 = Integer.MIN_VALUE;
        for (Map<String, Object> r : rs) {
            x0 = Math.min(x0, n(r.get("x0"))); x1 = Math.max(x1, n(r.get("x1"))); z0 = Math.min(z0, n(r.get("z0"))); z1 = Math.max(z1, n(r.get("z1")));
        }
        P1MapLayout l = new P1MapLayout(x0 - 4, x1 + 4, z0 - 12, z1 + 4);
        Map<String, Object> rb = null;
        for (Map<String, Object> r : rs) {
            String id = String.valueOf(r.get("id"));
            if ("RB".equals(id)) rb = r;
            boolean roof = roofed && !"R0".equals(id) || "E".equals(id) || roofs.contains(id);
            l.room(new Room(id, n(r.get("x0")), n(r.get("x1")), n(r.get("z0")), n(r.get("z1")), n(r.get("f")), n(r.get("h")), roof, guards.contains(id)));
        }
        for (Map<String, Object> c : cs) {
            List<List<Object>> ns = (List<List<Object>>) c.get("nodes");
            int[][] nodes = new int[ns.size()][];
            for (int i = 0; i < ns.size(); i++) nodes[i] = new int[] {n(ns.get(i).get(0)), n(ns.get(i).get(1)), n(ns.get(i).get(2))};
            l.corridor(String.valueOf(c.get("id")), 6, true, nodes);
        }
        if (rb != null) { // north backdrop (残门楼 / 冠冕 / 门楼): 23 wide, 16 above F, 5 deep, outside the wall, not climbable
            int cx = (n(rb.get("x0")) + n(rb.get("x1"))) / 2, zb = n(rb.get("z1")) + 3, f = n(rb.get("f"));
            l.decor.add(new int[] {0, cx - 11, cx + 11, zb, zb + 4, Math.min(l.ground, f - 1), f + 16});
        }
        if (m.get("decor") instanceof List) for (Object o : (List<Object>) m.get("decor")) { // B2.181 book §5 shapes
            List<Object> d = (List<Object>) o;
            int[] a = new int[Math.max(9, d.size())];
            for (int i = 0; i < d.size(); i++) a[i] = n(d.get(i));
            l.decor.add(a);
        }
        List<Object> sp = (List<Object>) m.get("spawn");
        l.spawnX = n(sp.get(0)); l.spawnF = n(sp.get(1)); l.spawnZ = n(sp.get(2));
        l.boxX0 = Math.floorDiv(x0 - 20, 16) * 16; l.boxX1 = Math.floorDiv(x1 + 20, 16) * 16 + 15;
        l.boxZ0 = Math.floorDiv(z0 - 20, 16) * 16; l.boxZ1 = Math.floorDiv(z1 + 28, 16) * 16 + 15;
        return l;
    }

    /** One map from the bundled p1-book-maps.yml (generated from the book), or null. */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> bookMap(String key) {
        java.io.InputStream in = P1MapLayout.class.getClassLoader().getResourceAsStream("p1-book-maps.yml");
        if (in == null) return null;
        try {
            Object y = new org.yaml.snakeyaml.Yaml().load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
            Object maps = y instanceof Map ? ((Map<String, Object>) y).get("maps") : null;
            Object m = maps instanceof Map ? ((Map<String, Object>) maps).get(key) : null;
            return m instanceof Map ? (Map<String, Object>) m : null;
        } finally {
            try { in.close(); } catch (java.io.IOException ignored) { }
        }
    }

    private static int n(Object o) { return o instanceof Number ? ((Number) o).intValue() : Integer.parseInt(String.valueOf(o).trim()); }

    /** Q05 断塔回廊 v1 (book ch. 15 §3–§4). */
    public static P1MapLayout spireV1() {
        P1MapLayout l = new P1MapLayout(-30, 84, -12, 178);
        l.room(new Room("R0", -6, 6, 0, 10, 64, 8, false, false));
        l.room(new Room("R1", -16, 16, 18, 42, 64, 8, false, false));
        l.room(new Room("R2", 28, 52, 50, 76, 72, 8, false, true));
        l.room(new Room("R3", -18, 10, 86, 112, 80, 8, true, false));
        l.room(new Room("RB", -16, 16, 130, 162, 80, 14, false, true));
        l.room(new Room("E", 60, 72, 57, 69, 72, 7, true, false));
        l.corridor("C01", 6, true, new int[] {0, 64, 10}, new int[] {0, 64, 18});
        l.corridor("C12", 6, true, new int[] {16, 64, 30}, new int[] {40, 64, 30}, new int[] {40, 64, 33}, new int[] {40, 72, 47}, new int[] {40, 72, 50});
        l.corridor("C23", 6, true, new int[] {40, 72, 76}, new int[] {40, 72, 79}, new int[] {40, 80, 95}, new int[] {40, 80, 98}, new int[] {10, 80, 98});
        l.corridor("C3B", 6, true, new int[] {0, 80, 112}, new int[] {0, 80, 130});
        l.corridor("CE", 6, true, new int[] {52, 72, 63}, new int[] {60, 72, 63});
        l.decor.add(new int[] {1, -24, 4, 30, 0, 62, 86});        // R1 west tower tube: diameter 9, 22 above F
        l.decor.add(new int[] {0, -11, 11, 165, 167, 62, 100});    // RB north crown: width 23, 20 above F
        l.decor.add(new int[] {2, -20, -19, 88, 110, 82, 84});     // R3 windows toward the far view (iron bars in the west wall)
        l.boxX0 = -48; l.boxX1 = 95; l.boxZ0 = -32; l.boxZ1 = 191;
        l.spawnX = 0; l.spawnF = 64; l.spawnZ = 5;
        return l;
    }
}

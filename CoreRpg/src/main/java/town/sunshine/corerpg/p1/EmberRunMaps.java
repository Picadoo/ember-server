package town.sunshine.corerpg.p1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * G04 map / encounter definitions parsed from ember-v1-runs.yml (plain maps, so tests can load the bundled file with
 * SnakeYAML). Pure data + validation of the §10.I composition caps (≤ 8 alive, ≤ 2 ranged, ≤ 1 caster per encounter).
 */
public final class EmberRunMaps {

    public static final class Pt {
        public final double x, y, z;
        public Pt(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
        @Override public String toString() { return (int) x + "," + (int) y + "," + (int) z; }
    }

    /** Inclusive block box. */
    public static final class Box {
        public final int x0, y0, z0, x1, y1, z1;
        public Box(int ax, int ay, int az, int bx, int by, int bz) {
            x0 = Math.min(ax, bx); y0 = Math.min(ay, by); z0 = Math.min(az, bz);
            x1 = Math.max(ax, bx); y1 = Math.max(ay, by); z1 = Math.max(az, bz);
        }
        public boolean contains(double x, double y, double z) {
            return x >= x0 && x < x1 + 1 && y >= y0 && y < y1 + 1 && z >= z0 && z < z1 + 1;
        }
        public boolean containsGrown(double x, double y, double z, double g) {
            return x >= x0 - g && x < x1 + 1 + g && y >= y0 - g && y < y1 + 1 + g && z >= z0 - g && z < z1 + 1 + g;
        }
        @Override public String toString() { return x0 + "," + y0 + "," + z0 + ".." + x1 + "," + y1 + "," + z1; }
    }

    public static final class Role {
        public final String id, mm;
        public final double hp, atk, interval, range, lineLength, lineWidth, warn;
        private Role(Role o, double hp, double atk) {
            id = o.id; mm = o.mm; this.hp = hp; this.atk = atk; interval = o.interval; range = o.range;
            lineLength = o.lineLength; lineWidth = o.lineWidth; warn = o.warn;
        }
        /** same role (MM id, timings, caster line) with challenge HP / raw damage */
        public Role with(double hp, double atk) { return new Role(this, hp, atk); }
        Role(String id, Map<?, ?> m) {
            this.id = id;
            this.mm = str(m.get("mm"), "");
            this.hp = num(m.get("hp"), 20);
            this.atk = num(m.get("atk"), 1);
            this.interval = num(m.get("interval"), 2.5);
            this.range = num(m.get("range"), 12);
            this.lineLength = num(m.get("line_length"), 6);
            this.lineWidth = num(m.get("line_width"), 1.5);
            this.warn = num(m.get("warn"), 1.0);
        }
        public boolean ranged() { return "ranged".equals(id); }
        public boolean caster() { return "caster".equals(id); }
    }

    public static final class Room {
        public final String id, label;
        public final Box trigger;
        public final Box door; // null for the last room (boss shares the hall)
        public final List<Pt> points;
        public final Map<String, Integer> a, b;
        Room(String id, Map<?, ?> m) {
            this.id = id;
            this.label = str(m.get("label"), id);
            this.trigger = box(m.get("trigger"));
            this.door = m.get("door") == null ? null : box(m.get("door"));
            this.points = pts(m.get("points"));
            this.a = counts(m.get("a"));
            this.b = counts(m.get("b"));
        }
        public Map<String, Integer> variant(boolean useB) { return useB ? b : a; }
    }

    public static final class Skill {
        public final String type, name;
        public final double every, warn, dmg, angle, range, radius, ahead, length, width, below, delay, kb;
        /** "player": a circle locked on one participant's current ground (Q04 冲击圈); "" = relative to the boss */
        public final String target;
        public final Skill follow;
        /**
         * line / charge strip start (blocks ahead of the boss); NaN = the old caster/boss line from −0.5 to length.
         * Q06 刀气: start 1, length 6 → the band covers 1..7 blocks ahead (book ch. 16 §7).
         */
        public final double start;
        /**
         * follow only: NaN = re-aim at the nearest player when the follow starts; a number = keep the first part's
         * locked origin + direction and slide the shape this many blocks to the boss's RIGHT (Q06 second band, +4).
         */
        public final double shift;
        /** recovery after THIS skill (Q07 重砸 1.5 s); NaN = the boss default */
        public final double recover;
        /** §18.1 challenge: the lighter second skill uses the light override (44), every other skill the heavy one (72) */
        public final boolean light;
        Skill(Map<?, ?> m) {
            type = str(m.get("type"), "cone");
            target = str(m.get("target"), "");
            kb = Math.max(0, Math.min(1.0, num(m.get("kb"), 0))); // §10.I: horizontal push ≤ 1 block, never through walls / off ledges
            name = str(m.get("name"), type);
            every = num(m.get("every"), 8);
            warn = num(m.get("warn"), 1.0);
            dmg = num(m.get("dmg"), 1);
            angle = num(m.get("angle"), 90);
            range = num(m.get("range"), 3);
            radius = num(m.get("radius"), 3);
            ahead = num(m.get("ahead"), 0);
            length = num(m.get("length"), 5);
            width = num(m.get("width"), 3);
            // follow-up gate: fires while boss HP ratio < below; default = always (full HP is ratio 1.0)
            below = num(m.get("below"), 1.01);
            delay = num(m.get("delay"), 0);
            start = num(m.get("start"), Double.NaN);
            shift = num(m.get("shift"), Double.NaN);
            recover = num(m.get("recover"), Double.NaN);
            light = Boolean.TRUE.equals(m.get("light")) || "true".equals(String.valueOf(m.get("light")));
            follow = m.get("follow") instanceof Map ? new Skill((Map<?, ?>) m.get("follow")) : null;
        }

        private Skill(Skill o, double length, double dmg) {
            type = o.type; name = o.name; every = o.every; warn = o.warn; this.dmg = dmg; angle = o.angle; range = o.range;
            radius = o.radius; ahead = o.ahead; this.length = length; width = o.width; below = o.below; delay = o.delay;
            kb = o.kb; target = o.target; follow = o.follow; start = o.start; shift = o.shift; recover = o.recover; light = o.light;
        }

        /** same skill with another strip length (charge clipped at a wall / the boss area edge) */
        public Skill withLength(double len) { return new Skill(this, len, dmg); }

        /** same skill with another raw damage (challenge overrides) */
        public Skill withDmg(double d) { return new Skill(this, length, d); }

        /** strip along-range [from, to] for line / charge */
        public double stripFrom() { return Double.isNaN(start) ? -0.5 : start; }
        public double stripTo() { return Double.isNaN(start) ? length : start + length; }
        public boolean strip() { return "line".equals(type) || "charge".equals(type); }
    }

    public static final class Adds {
        public final double atHp, warn;
        public final String role;
        public final List<Pt> points;
        Adds(Map<?, ?> m) {
            atHp = num(m.get("at_hp"), 0.5);
            warn = num(m.get("warn"), 1.0);
            role = str(m.get("role"), "melee");
            points = pts(m.get("points"));
        }
    }

    public static final class Boss {
        public final String mm, name;
        public final double hp, atk, interval, recover;
        public final Pt at;
        public final Box area;
        /** spawn only once a participant stands in the area (or 20 s after the last room): far boss halls (B2.151) */
        public final boolean waitInArea;
        public final List<Skill> skills;
        public final Adds adds;
        Boss(Map<?, ?> m) {
            mm = str(m.get("mm"), "");
            name = str(m.get("name"), "首领");
            hp = num(m.get("hp"), 100);
            atk = num(m.get("atk"), 1);
            interval = num(m.get("interval"), 3);
            recover = num(m.get("recover"), 0.5); // §10.I 收招: pause after one skill before the next may start
            at = pt(m.get("at"));
            area = box(m.get("area"));
            waitInArea = Boolean.TRUE.equals(m.get("wait_in_area"));
            List<Skill> s = new ArrayList<Skill>();
            if (m.get("skills") instanceof List) for (Object o : (List<?>) m.get("skills")) if (o instanceof Map) s.add(new Skill((Map<?, ?>) o));
            skills = Collections.unmodifiableList(s);
            adds = m.get("adds") instanceof Map ? new Adds((Map<?, ?>) m.get("adds")) : null;
        }
    }

    /**
     * A one-way passage opened once room {@code after} is cleared: a participant inside {@code from} is moved to
     * {@code to}. Used where the template's own stairs cannot be walked (spire), see source table §11.
     */
    public static final class Link {
        public final String after, label;
        public final Box from;
        public final Pt to;
        public final float yaw;
        Link(Map<?, ?> m) {
            after = str(m.get("after"), "");
            label = str(m.get("label"), "");
            from = box(m.get("from"));
            to = pt(m.get("to"));
            yaw = (float) num(m.get("yaw"), 0);
        }
    }

    public static final class MapDef {
        public final String key, name, dungeon, template, dropLabel, contentVersion, mapVersion, requires, unlocks, purpose;
        public final int tier;
        public final double bRef;
        public final Pt spawn;
        public final EmberRunRules.FirstClear firstClear;
        public final Map<String, Role> roles;
        public final List<Room> rooms;
        public final Boss boss;
        public final String eventAfter;
        public final Pt eventAnchor;
        public final Box eventArea;
        /** §9 / §20.5 safe points r0..r3, rb (reconnect within 120 s → last cleared room; boss alive → rb) */
        public final Map<String, Pt> safe;
        /** §9 hologram anchors: entry (name + goal), event (only with an extra event), exit (after the clear) */
        public final Map<String, Pt> holo;
        /** extra standing points for party members 2..n (member 1 keeps the DP spawn) */
        public final List<Pt> spread;
        public final List<Link> links;
        /** guard rails: iron bars placed in AIR only, when the instance is attached (template untouched) */
        public final List<Box> rails;
        /** single blocks set to AIR in the instance at attach (e.g. the tide template's stray sign for Q04) */
        public final List<Pt> clear;
        /** P2-5 raid overrides (−1 / 0 = the file-wide value / off): party size, stamina, weekly cap, party scaling */
        public final int partyMin, partyMax, cost, weeklyCap;
        public final double hpPerMember, dmgPerMember;
        /** true for entries of the runs-yml `raids:` section (never in the main-line order, featured or abyss) */
        public boolean raid;

        MapDef(String key, Map<?, ?> m) {
            this.key = key;
            name = str(m.get("name"), key);
            dungeon = str(m.get("dungeon"), "");
            template = str(m.get("template"), "");
            tier = (int) num(m.get("tier"), 1);
            dropLabel = str(m.get("drop_label"), "T" + tier);
            contentVersion = str(m.get("content_version"), "v1");
            mapVersion = str(m.get("map_version"), "");
            requires = str(m.get("requires"), "");
            unlocks = str(m.get("unlocks"), "");
            partyMin = (int) num(m.get("party_min"), -1);
            partyMax = (int) num(m.get("party_max"), -1);
            cost = (int) num(m.get("cost"), -1);
            weeklyCap = (int) num(m.get("weekly_cap"), 0);
            hpPerMember = num(m.get("hp_per_member"), -1);
            dmgPerMember = num(m.get("dmg_per_member"), 0);
            purpose = str(m.get("purpose"), "");
            bRef = num(m.get("b_ref"), 12);
            spawn = pt(m.get("spawn"));
            Map<?, ?> fc = m.get("first_clear") instanceof Map ? (Map<?, ?>) m.get("first_clear") : Collections.emptyMap();
            firstClear = new EmberRunRules.FirstClear(key, fc.get("choice") == null ? null : String.valueOf(fc.get("choice")),
                    (int) num(fc.get("tier"), tier), (int) num(fc.get("shard"), 0), (int) num(fc.get("core"), 0),
                    (int) num(fc.get("coin"), 0), (int) num(fc.get("bone"), 0), (int) num(fc.get("blank"), 0),
                    unlocks.isEmpty() ? null : unlocks);
            Map<String, Role> r = new LinkedHashMap<String, Role>();
            if (m.get("mobs") instanceof Map) {
                for (Map.Entry<?, ?> e : ((Map<?, ?>) m.get("mobs")).entrySet()) {
                    if (e.getValue() instanceof Map) r.put(String.valueOf(e.getKey()), new Role(String.valueOf(e.getKey()), (Map<?, ?>) e.getValue()));
                }
            }
            roles = Collections.unmodifiableMap(r);
            List<Room> rs = new ArrayList<Room>();
            if (m.get("rooms") instanceof Map) {
                for (Map.Entry<?, ?> e : ((Map<?, ?>) m.get("rooms")).entrySet()) {
                    if (e.getValue() instanceof Map) rs.add(new Room(String.valueOf(e.getKey()), (Map<?, ?>) e.getValue()));
                }
            }
            rooms = Collections.unmodifiableList(rs);
            boss = m.get("boss") instanceof Map ? new Boss((Map<?, ?>) m.get("boss")) : null;
            Map<?, ?> ev = m.get("event") instanceof Map ? (Map<?, ?>) m.get("event") : Collections.emptyMap();
            eventAfter = str(ev.get("after"), rs.isEmpty() ? "" : rs.get(Math.min(1, rs.size() - 1)).id);
            eventAnchor = ev.get("anchor") == null ? null : pt(ev.get("anchor"));
            eventArea = ev.get("area") == null ? null : box(ev.get("area"));
            safe = ptMap(m.get("safe"));
            holo = ptMap(m.get("holo"));
            spread = pts(m.get("spread"));
            List<Link> ls = new ArrayList<Link>();
            if (m.get("links") instanceof List) for (Object o : (List<?>) m.get("links")) if (o instanceof Map) ls.add(new Link((Map<?, ?>) o));
            links = Collections.unmodifiableList(ls);
            List<Box> rl = new ArrayList<Box>();
            if (m.get("rails") instanceof List) for (Object o : (List<?>) m.get("rails")) { Box b = box(o); if (b != null) rl.add(b); }
            rails = Collections.unmodifiableList(rl);
            clear = pts(m.get("clear"));
        }

        /** role of this map, with the §18.1 challenge override when {@code ch} is set (else the table value) */
        public Role role(String id, Challenge ch) {
            Role r = roles.get(id);
            if (r == null || ch == null) return r;
            double[] o = ch.mobs.get(id);
            return o == null ? r : r.with(o[0], o[1]);
        }

        /** member 0 keeps the spawn; member i ≥ 1 gets spread[(i-1) % n]; null = stay. */
        public Pt spreadPoint(int idx) {
            if (idx < 1 || spread.isEmpty()) return null;
            return spread.get((idx - 1) % spread.size());
        }

        /** every point a player or mob is placed on (rails must never cover one) */
        public List<Pt> standPoints() {
            List<Pt> out = new ArrayList<Pt>();
            if (spawn != null) out.add(spawn);
            out.addAll(spread);
            for (Room r : rooms) out.addAll(r.points);
            for (Link l : links) if (l.to != null) out.add(l.to);
            if (boss != null && boss.at != null) out.add(boss.at);
            if (eventAnchor != null) out.add(eventAnchor);
            return out;
        }

        public Room room(String id) {
            for (Room r : rooms) if (r.id.equals(id)) return r;
            return null;
        }

        public int roomIndex(String id) {
            for (int i = 0; i < rooms.size(); i++) if (rooms.get(i).id.equals(id)) return i;
            return -1;
        }

        public String firstClearLabel() {
            EmberRunRules.FirstClear f = firstClear;
            if (f.choiceSlot != null) return "自选族 T" + f.choiceTier + " 标准" + EmberItemData.slotName(f.choiceSlot) + "（绑定、不可分解）";
            StringBuilder sb = new StringBuilder();
            if (f.shard > 0) sb.append("碎片 ").append(f.shard).append(' ');
            if (f.core > 0) sb.append("核心 ").append(f.core).append(' ');
            if (f.blank > 0) sb.append("胚料 ").append(f.blank).append(' ');
            if (f.bone > 0) sb.append("骨尘 ").append(f.bone).append(' ');
            if (f.coin > 0) sb.append("币 ").append(f.coin);
            return sb.toString().trim();
        }

        /** @return null when valid, else the first problem (unknown roles, too few points, §10.I caps). */
        public String validate() {
            if (dungeon.isEmpty()) return key + ": dungeon missing";
            if (rooms.isEmpty()) return key + ": no rooms";
            if (boss == null || boss.mm.isEmpty() || boss.at == null) return key + ": boss missing";
            for (int i = 0; i < rooms.size(); i++) {
                Room r = rooms.get(i);
                if (r.trigger == null) return key + "." + r.id + ": trigger missing";
                if (i < rooms.size() - 1 && r.door == null) return key + "." + r.id + ": door missing";
                for (int v = 0; v < 2; v++) {
                    Map<String, Integer> c = r.variant(v == 1);
                    String err = checkComposition(c, r.points.size());
                    if (err != null) return key + "." + r.id + "." + (v == 1 ? "b" : "a") + ": " + err;
                    for (String role : c.keySet()) if (!roles.containsKey(role)) return key + "." + r.id + ": unknown role " + role;
                }
            }
            for (String need : new String[]{"treasure", "elite"}) if (!roles.containsKey(need)) return key + ": role " + need + " missing";
            if (eventAnchor == null) return key + ": event anchor missing";
            if (room(eventAfter) == null) return key + ": event.after " + eventAfter + " is not a room";
            if (boss.adds != null && !roles.containsKey(boss.adds.role)) return key + ": adds role " + boss.adds.role + " missing";
            for (Skill sk : boss.skills) {
                if (sk.every <= sk.warn) return key + ": skill " + sk.name + " every ≤ warn";
                if (!sk.target.isEmpty() && !("player".equals(sk.target) && "circle".equals(sk.type)))
                    return key + ": skill " + sk.name + " target " + sk.target + " (only circle/player)";
                if (!java.util.Arrays.asList("cone", "circle", "line", "charge").contains(sk.type))
                    return key + ": skill " + sk.name + " unknown type " + sk.type;
                if ("charge".equals(sk.type) && (sk.length <= 0 || sk.length > 8))
                    return key + ": skill " + sk.name + " charge length must be 0..8 (book ch. 17 §7)";
                if (sk.follow != null && !Double.isNaN(sk.follow.shift) && !sk.follow.strip())
                    return key + ": follow of " + sk.name + " uses shift but is not a line";
            }
            for (Link l : links) {
                if (room(l.after) == null) return key + ": link.after " + l.after + " is not a room";
                if (l.from == null || l.to == null) return key + ": link from/to missing";
                if (l.from.contains(l.to.x + 0.5, l.to.y, l.to.z + 0.5)) return key + ": link target inside its own entry box";
            }
            for (Map.Entry<String, Pt> e : safe.entrySet()) {
                Pt p = e.getValue();
                Room r = room(e.getKey());
                if (r != null && !r.trigger.contains(p.x + 0.5, p.y, p.z + 0.5)) return key + ": safe." + e.getKey() + " outside the room";
                if ("rb".equals(e.getKey()) && boss.area != null && !boss.area.contains(p.x + 0.5, p.y, p.z + 0.5)) return key + ": safe.rb outside the boss area";
            }
            for (Box b : rails) for (Pt p : standPoints())
                if (b.contains(p.x + 0.5, p.y, p.z + 0.5)) return key + ": rail covers point " + p;
            return null;
        }
    }

    /** §10.I: one encounter ≤ 8 alive, ≤ 2 ranged, ≤ 1 caster, and every mob needs its own point. */
    public static String checkComposition(Map<String, Integer> c, int points) {
        int total = 0, ranged = 0, caster = 0;
        for (Map.Entry<String, Integer> e : c.entrySet()) {
            int n = e.getValue();
            if (n < 0) return "negative count";
            total += n;
            if ("ranged".equals(e.getKey())) ranged += n;
            if ("caster".equals(e.getKey())) caster += n;
        }
        if (total <= 0) return "empty encounter";
        if (total > 8) return "more than 8 mobs (" + total + ")";
        if (ranged > 2) return "more than 2 ranged (" + ranged + ")";
        if (caster > 1) return "more than 1 caster (" + caster + ")";
        if (total > points) return total + " mobs but only " + points + " points";
        return null;
    }

    /** Spawn order for one encounter: roles expanded (stable order), point offset by the room seed. */
    public static List<String[]> layout(Room r, boolean useB, long roomSeed) {
        List<String[]> out = new ArrayList<String[]>();
        Map<String, Integer> c = r.variant(useB);
        int n = r.points.size();
        int off = n == 0 ? 0 : (int) Math.floorMod(roomSeed, (long) n);
        int i = 0;
        for (Map.Entry<String, Integer> e : c.entrySet()) {
            for (int k = 0; k < e.getValue(); k++) {
                out.add(new String[]{e.getKey(), String.valueOf((off + i) % Math.max(1, n))});
                i++;
            }
        }
        return out;
    }

    /**
     * §18.1 challenge difficulty, one shared T3 reference for all seven maps (not normal HP × something): mob HP /
     * raw damage, boss HP / normal hit, heavy (重斩/刀气/重砸/冲撞) and light (second, lighter) skill damage, T3 drops
     * and marks with the 60/28/10/2 quality table, opened by the player's own Q07 first clear.
     */
    public static final class Challenge {
        public final String requires;
        public final int tier;
        public final double bRef, bossHp, bossAtk, heavy, light;
        public final int[] quality;
        public final Map<String, double[]> mobs; // role → {hp, atk}
        Challenge(Map<?, ?> m) {
            requires = str(m.get("requires"), "q07");
            tier = (int) num(m.get("tier"), 3);
            bRef = num(m.get("b_ref"), 90);
            Map<?, ?> b = m.get("boss") instanceof Map ? (Map<?, ?>) m.get("boss") : Collections.emptyMap();
            bossHp = num(b.get("hp"), 8000);
            bossAtk = num(b.get("atk"), 44);
            heavy = num(b.get("heavy"), 72);
            light = num(b.get("light"), 44);
            int[] q = EmberRunRules.CHALLENGE_QUALITY_WEIGHTS.clone();
            if (m.get("quality") instanceof List && ((List<?>) m.get("quality")).size() == 4)
                for (int i = 0; i < 4; i++) q[i] = (int) num(((List<?>) m.get("quality")).get(i), q[i]);
            quality = q;
            Map<String, double[]> mm = new LinkedHashMap<String, double[]>();
            if (m.get("mobs") instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) m.get("mobs")).entrySet()) {
                if (!(e.getValue() instanceof Map)) continue;
                Map<?, ?> x = (Map<?, ?>) e.getValue();
                mm.put(String.valueOf(e.getKey()), new double[]{num(x.get("hp"), 1), num(x.get("atk"), 0)});
            }
            mobs = Collections.unmodifiableMap(mm);
        }
        private Challenge(Challenge c, double hpMul, double dmgMul, int[] q) {
            requires = c.requires; tier = c.tier; bRef = c.bRef;
            bossHp = c.bossHp * hpMul; bossAtk = c.bossAtk * dmgMul; heavy = c.heavy * dmgMul; light = c.light * dmgMul;
            quality = q.clone();
            Map<String, double[]> mm = new LinkedHashMap<String, double[]>();
            for (Map.Entry<String, double[]> e : c.mobs.entrySet())
                mm.put(e.getKey(), new double[]{e.getValue()[0] * hpMul, e.getValue()[1] * dmgMul});
            mobs = Collections.unmodifiableMap(mm);
        }
        /** P2-2: this challenge × an abyss tier (HP / damage factors, the tier quality table). */
        public Challenge scaled(AbyssTier t) { return new Challenge(this, t.hp, t.dmg, t.quality); }
        public double skillDmg(Skill sk) { return sk.light ? light : heavy; }
        public String validate() {
            for (String r : new String[]{"melee", "ranged", "heavy", "caster", "treasure", "elite"})
                if (!mobs.containsKey(r)) return "challenge: mobs." + r + " missing";
            int t = 0;
            for (int x : quality) { if (x < 0) return "challenge: negative quality weight"; t += x; }
            if (t != 100) return "challenge: quality weights sum " + t + " ≠ 100";
            if (tier < 1 || tier > EmberTables.MAX_TIER) return "challenge: tier " + tier;
            return null;
        }
    }

    /** P2-2 (D70) one row of the capped abyss tier table (book §18.3): factors on the challenge values. */
    public static final class AbyssTier {
        public final int index;          // 1-based
        public final double hp, dmg;
        public final int[] quality;      // 标准/精良/卓越/极品
        public final int fee;            // 余烬币 per segment (reserved at entry like stamina)
        AbyssTier(int index, Map<?, ?> m) {
            this.index = index;
            hp = num(m.get("hp"), 1.0);
            dmg = num(m.get("dmg"), 1.0);
            fee = (int) num(m.get("fee"), 0);
            int[] q = EmberRunRules.CHALLENGE_QUALITY_WEIGHTS.clone();
            if (m.get("quality") instanceof List && ((List<?>) m.get("quality")).size() == 4)
                for (int i = 0; i < 4; i++) q[i] = (int) num(((List<?>) m.get("quality")).get(i), q[i]);
            quality = q;
        }
        String validate(AbyssTier prev) {
            int t = 0;
            for (int x : quality) { if (x < 0) return "abyss T" + index + ": negative quality weight"; t += x; }
            if (t != 100) return "abyss T" + index + ": quality weights sum " + t + " ≠ 100";
            if (hp < 1.0 || dmg < 1.0 || fee < 0) return "abyss T" + index + ": factors below the challenge values";
            if (prev != null && (hp < prev.hp || dmg < prev.dmg || fee < prev.fee)) return "abyss T" + index + ": not monotonic";
            if (hp > ABYSS_MAX_HP || dmg > ABYSS_MAX_DMG) return "abyss T" + index + ": above the table cap";
            return null;
        }
    }

    /** book §18.3 「必须有表列上限」: never more than ten tiers, never above these factors */
    public static final int ABYSS_MAX_TIERS = 10;
    public static final double ABYSS_MAX_HP = 1.6, ABYSS_MAX_DMG = 1.3;

    public final int version;
    /** rule_version + "/b" + balance_version — what cr_p1_run.rule_version records for every run (book §23.2 / D60) */
    public final String ruleVersion;
    /** §23.2: bumped on every parameter change (1 = book initial values incl. D31, 2 = §2c plan B / D60) */
    public final int balanceVersion;
    /** null when the file has no challenge section (challenge entry refused) */
    public final Challenge challenge;
    public final int cost, partyMin, partyMax, passSeconds;
    /** P2-2 abyss: opening flag (own first clear of this map) and the tier table; empty = abyss off */
    public final String abyssRequires;
    public final List<AbyssTier> abyss;
    private final List<Challenge> abyssCh;
    /** P2-1 (D66) weekly featured challenge: extra marks of the run tier, and how many clears per week get them */
    public final int rotationBonusMarks, rotationWeeklyCap;
    public final String worldPrefix;
    public final Map<String, MapDef> maps;
    /** P2-5 raids (runs yml `raids:`), keyed like maps (r01 …) */
    public final Map<String, MapDef> raids;

    private EmberRunMaps(Map<?, ?> root) {
        version = (int) num(root.get("version"), 1);
        balanceVersion = (int) num(root.get("balance_version"), 1);
        ruleVersion = str(root.get("rule_version"), "g04-1") + "/b" + balanceVersion;
        cost = (int) num(root.get("cost"), 30);
        partyMin = (int) num(root.get("party_min"), 1);
        partyMax = (int) num(root.get("party_max"), 3);
        passSeconds = (int) num(root.get("pass_seconds"), 30);
        worldPrefix = str(root.get("world_prefix"), "dungeon_EmberQ0");
        Map<?, ?> rot = root.get("rotation") instanceof Map ? (Map<?, ?>) root.get("rotation") : java.util.Collections.emptyMap();
        rotationBonusMarks = Math.max(0, (int) num(rot.get("bonus_marks"), 0));
        rotationWeeklyCap = Math.max(0, (int) num(rot.get("weekly_cap"), 0));
        challenge = root.get("challenge") instanceof Map ? new Challenge((Map<?, ?>) root.get("challenge")) : null;
        Map<?, ?> ab = root.get("abyss") instanceof Map ? (Map<?, ?>) root.get("abyss") : Collections.emptyMap();
        abyssRequires = str(ab.get("requires"), "q07");
        List<AbyssTier> at = new ArrayList<AbyssTier>();
        List<Challenge> ac = new ArrayList<Challenge>();
        if (challenge != null && ab.get("tiers") instanceof List) for (Object o : (List<?>) ab.get("tiers")) {
            if (!(o instanceof Map) || at.size() >= ABYSS_MAX_TIERS) continue;
            AbyssTier t = new AbyssTier(at.size() + 1, (Map<?, ?>) o);
            at.add(t);
            ac.add(challenge.scaled(t));
        }
        abyss = Collections.unmodifiableList(at);
        abyssCh = ac;
        Map<String, MapDef> m = new LinkedHashMap<String, MapDef>();
        if (root.get("maps") instanceof Map) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) root.get("maps")).entrySet()) {
                if (e.getValue() instanceof Map) {
                    String k = String.valueOf(e.getKey()).toLowerCase(Locale.ROOT);
                    m.put(k, new MapDef(k, (Map<?, ?>) e.getValue()));
                }
            }
        }
        maps = Collections.unmodifiableMap(m);
        Map<String, MapDef> rr = new LinkedHashMap<String, MapDef>();
        if (root.get("raids") instanceof Map) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) root.get("raids")).entrySet()) {
                if (e.getValue() instanceof Map) {
                    String k = String.valueOf(e.getKey()).toLowerCase(Locale.ROOT);
                    MapDef d = new MapDef(k, (Map<?, ?>) e.getValue());
                    d.raid = true;
                    rr.put(k, d);
                }
            }
        }
        raids = Collections.unmodifiableMap(rr);
    }

    public int partyMin(MapDef m) { return m != null && m.partyMin > 0 ? m.partyMin : partyMin; }
    public int partyMax(MapDef m) { return m != null && m.partyMax > 0 ? m.partyMax : partyMax; }
    public int cost(MapDef m) { return m != null && m.cost > 0 ? m.cost : cost; }

    /** enemy HP factor: A18 1 + 0.65 (n − 1) for main maps; raids use their own hp_per_member (P2-5, D76) */
    public static double hpFactor(MapDef m, int n) {
        if (m == null || m.hpPerMember < 0) return EmberRunRules.hpFactor(n);
        return 1.0 + m.hpPerMember * (Math.max(1, Math.min(m.partyMax > 0 ? m.partyMax : n, n)) - 1);
    }

    /** enemy damage factor: 1 for main maps (§18.1 damage does not scale); raids 1 + dmg_per_member (n − 1) */
    public static double dmgFactor(MapDef m, int n) {
        if (m == null || m.dmgPerMember <= 0) return 1.0;
        return 1.0 + m.dmgPerMember * (Math.max(1, Math.min(m.partyMax > 0 ? m.partyMax : n, n)) - 1);
    }

    public static EmberRunMaps parse(Map<?, ?> root) { return new EmberRunMaps(root == null ? Collections.emptyMap() : root); }

    /** 1-based; null when the tier does not exist */
    public AbyssTier abyssTier(int t) { return t >= 1 && t <= abyss.size() ? abyss.get(t - 1) : null; }

    /** challenge values × tier t (what the director uses for an abyss segment); null when the tier does not exist */
    public Challenge abyssChallenge(int t) { return t >= 1 && t <= abyssCh.size() ? abyssCh.get(t - 1) : null; }

    /** the segment's map: seeded pick over the runs-yml map order (book §18.3 uses accepted rooms only) */
    public MapDef abyssMap(long seed) {
        if (maps.isEmpty()) return null;
        List<MapDef> l = new ArrayList<MapDef>(maps.values());
        return l.get((int) Math.floorMod(EmberRunRules.subSeed(seed, "abyss_map"), (long) l.size()));
    }

    public MapDef byKey(String key) {
        if (key == null) return null;
        String k = key.toLowerCase(Locale.ROOT);
        MapDef d = maps.get(k);
        return d != null ? d : raids.get(k);
    }

    public MapDef byDungeon(String dungeonId) {
        for (MapDef d : maps.values()) if (d.dungeon.equalsIgnoreCase(dungeonId)) return d;
        for (MapDef d : raids.values()) if (d.dungeon.equalsIgnoreCase(dungeonId)) return d;
        return null;
    }

    /** DP instance world dungeon_&lt;DungeonId&gt;_&lt;HEX&gt; → map, else null. */
    public MapDef byWorld(String worldName) {
        if (worldName == null) return null;
        for (MapDef d : maps.values()) {
            String p = "dungeon_" + d.dungeon + "_";
            if (worldName.regionMatches(true, 0, p, 0, p.length())) return d;
        }
        for (MapDef d : raids.values()) {
            String p = "dungeon_" + d.dungeon + "_";
            if (worldName.regionMatches(true, 0, p, 0, p.length())) return d;
        }
        return null;
    }

    public List<String> validate() {
        List<String> out = new ArrayList<String>();
        if (maps.isEmpty()) out.add("no maps");
        if (challenge != null) { String e = challenge.validate(); if (e != null) out.add(e); }
        for (int i = 0; i < abyss.size(); i++) { String e = abyss.get(i).validate(i == 0 ? null : abyss.get(i - 1)); if (e != null) out.add(e); }
        for (MapDef d : maps.values()) {
            String e = d.validate();
            if (e != null) out.add(e);
            if (!("dungeon_" + d.dungeon).toLowerCase(Locale.ROOT).startsWith(worldPrefix.toLowerCase(Locale.ROOT)))
                out.add(d.key + ": dungeon " + d.dungeon + " outside world_prefix " + worldPrefix);
        }
        for (MapDef d : raids.values()) {
            String e = d.validate();
            if (e != null) out.add(e);
            if (!("dungeon_" + d.dungeon).toLowerCase(Locale.ROOT).startsWith(worldPrefix.toLowerCase(Locale.ROOT)))
                out.add(d.key + ": dungeon " + d.dungeon + " outside world_prefix " + worldPrefix);
            if (maps.containsKey(d.key)) out.add(d.key + ": raid key collides with a main map");
            if (d.partyMin < 1 || d.partyMax < d.partyMin || d.partyMax > 5) out.add(d.key + ": raid party " + d.partyMin + ".." + d.partyMax);
            if (d.hpPerMember > 1.5 || d.dmgPerMember > 0.5) out.add(d.key + ": raid party scaling above the cap");
        }
        return out;
    }

    // ------------------------------------------------------------------ parsing helpers

    static String str(Object o, String def) { return o == null ? def : String.valueOf(o); }

    static double num(Object o, double def) {
        if (o instanceof Number) return ((Number) o).doubleValue();
        if (o != null) try { return Double.parseDouble(String.valueOf(o)); } catch (NumberFormatException ignored) { }
        return def;
    }

    static Pt pt(Object o) {
        if (!(o instanceof List) || ((List<?>) o).size() < 3) return null;
        List<?> l = (List<?>) o;
        return new Pt(num(l.get(0), 0), num(l.get(1), 0), num(l.get(2), 0));
    }

    static Map<String, Pt> ptMap(Object o) {
        Map<String, Pt> out = new java.util.LinkedHashMap<String, Pt>();
        if (o instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) { Pt p = pt(e.getValue()); if (p != null) out.put(String.valueOf(e.getKey()), p); }
        return Collections.unmodifiableMap(out);
    }

    static List<Pt> pts(Object o) {
        List<Pt> out = new ArrayList<Pt>();
        if (o instanceof List) for (Object x : (List<?>) o) { Pt p = pt(x); if (p != null) out.add(p); }
        return Collections.unmodifiableList(out);
    }

    static Box box(Object o) {
        if (!(o instanceof List) || ((List<?>) o).size() < 6) return null;
        List<?> l = (List<?>) o;
        return new Box((int) num(l.get(0), 0), (int) num(l.get(1), 0), (int) num(l.get(2), 0),
                (int) num(l.get(3), 0), (int) num(l.get(4), 0), (int) num(l.get(5), 0));
    }

    static Map<String, Integer> counts(Object o) {
        Map<String, Integer> out = new LinkedHashMap<String, Integer>();
        if (o instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) o).entrySet()) out.put(String.valueOf(e.getKey()), (int) num(e.getValue(), 0));
        return Collections.unmodifiableMap(out);
    }
}

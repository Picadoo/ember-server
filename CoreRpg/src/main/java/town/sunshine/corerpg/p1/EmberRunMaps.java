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
        /** D166: one-line tip shown when this room starts ("" = none), e.g. Q01 r1 「进门后退到门口打，别站进怪堆」 */
        public final String hint;
        public final Box trigger;
        public final Box door; // null for the last room (boss shares the hall)
        /** D300: seconds after clear before the door opens (0 = immediate; capped at 1.5) */
        public final double doorDelay;
        public final List<Pt> points;
        public final Map<String, Integer> a, b;
        Room(String id, Map<?, ?> m) {
            this.id = id;
            this.label = str(m.get("label"), id);
            this.hint = str(m.get("hint"), "");
            this.trigger = box(m.get("trigger"));
            this.door = m.get("door") == null ? null : box(m.get("door"));
            this.doorDelay = Math.max(0, Math.min(1.5, num(m.get("door_delay"), 0)));
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
        /** R03 (D137) 烬核分摊: everyone inside the circle when it lands shares dmg equally (alone = all of it) */
        public final boolean share;
        /** D188 撞墙破绽: charge only — when a real wall (not the invisible boss-area edge, not a ledge) cuts the strip
         *  short, the boss is stunned this many seconds after it lands (no skills, no melee, rooted); 0 = off */
        public final double wallStun;
        /** D192 落空破绽: not a charge — when someone stood inside the telegraph as the warning started and nobody is
         *  inside when it lands (everyone dodged), the boss staggers this many seconds (no skills, no melee, rooted); 0 = off */
        public final double whiffStun;
        /** D193 破招: not a charge, not a share — the party deals breakHp × the boss's max HP during the warning → the cast
         *  is broken (it does not land) and the boss staggers breakStun s; 0 = off (≤ 0.5 of max HP, stun ≤ 2 s) */
        public final double breakHp, breakStun;
        /** D241: public factory for {@code p1.encounter} affix primitives (same parsing as the package constructor). */
        public static Skill of(Map<?, ?> m) { return new Skill(m); }
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
            // phase gate: a follow-up (or, since P2-6, a top-level skill) fires only while boss HP ratio < below;
            // default = always (full HP is ratio 1.0)
            below = num(m.get("below"), 1.01);
            delay = num(m.get("delay"), 0);
            start = num(m.get("start"), Double.NaN);
            shift = num(m.get("shift"), Double.NaN);
            recover = num(m.get("recover"), Double.NaN);
            light = Boolean.TRUE.equals(m.get("light")) || "true".equals(String.valueOf(m.get("light")));
            share = Boolean.TRUE.equals(m.get("share")) || "true".equals(String.valueOf(m.get("share")));
            wallStun = "charge".equals(type) ? Math.max(0, Math.min(3.0, num(m.get("wall_stun"), 0))) : 0; // D188: ≤ 3 s
            whiffStun = "charge".equals(type) || share ? 0 : Math.max(0, Math.min(2.0, num(m.get("whiff_stun"), 0))); // D192: ≤ 2 s
            breakHp = "charge".equals(type) || share ? 0 : Math.max(0, Math.min(0.5, num(m.get("break_hp"), 0))); // D193
            breakStun = breakHp > 0 ? Math.max(0, Math.min(2.0, num(m.get("break_stun"), 1.0))) : 0;
            follow = m.get("follow") instanceof Map ? new Skill((Map<?, ?>) m.get("follow")) : null;
        }

        private Skill(Skill o, double length, double dmg) {
            type = o.type; name = o.name; every = o.every; warn = o.warn; this.dmg = dmg; angle = o.angle; range = o.range;
            radius = o.radius; ahead = o.ahead; this.length = length; width = o.width; below = o.below; delay = o.delay;
            kb = o.kb; target = o.target; follow = o.follow; start = o.start; shift = o.shift; recover = o.recover; light = o.light; share = o.share; wallStun = o.wallStun; whiffStun = o.whiffStun;
            breakHp = o.breakHp; breakStun = o.breakStun;
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
        /** D283: player-facing map card (adventure lore); "" = none. Pure copy — not a room hint. */
        public final String hint;
        /** D166: party advice shown at the raid start and in the recruit call ("" = none), e.g. R01 「建议队伍里有炽愈」 */
        public final String partyHint;
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
        /** F-review Q04 (D122): feet below this Y = fell out of the rooms (pool / outside ground) → put back; NaN = off */
        public final double fallCatchY;
        /** single blocks set to AIR in the instance at attach (e.g. the tide template's stray sign for Q04) */
        public final List<Pt> clear;
        /** P2-5 raid overrides (−1 / 0 = the file-wide value / off): party size, stamina, weekly cap, party scaling */
        public final int partyMin, partyMax, cost, weeklyCap;
        public final double hpPerMember, dmgPerMember;
        /** P2-6 (D78): raids with the same cap_group share one weekly counter ("" = own counter) */
        public final String capGroup;
        /** P2-9 (D81) loot identity: family / slot this map's random items lean to (null = none) */
        public final String lootFamily, lootSlot;
        /** true for entries of the runs-yml `raids:` section (never in the main-line order, featured or abyss) */
        public boolean raid;
        /** D139: the limited-time event dungeon (ember-v1-festival.yml `dungeon:`; never main-line / raid / abyss) */
        public boolean event;
        /** D144 余烬连战 (runs yml `rush:`): bosses fought back-to-back in one hall; {@link #boss} = chain.get(0) */
        public boolean rush;
        public List<Boss> chain = Collections.emptyList();
        public List<String> chainKeys = Collections.emptyList();
        /** D144: boss HP / damage multipliers on the chain bosses, rest between bosses, heal share at each break */
        public double rushHp = 1.0, rushDmg = 1.0, rushBreak = 10, rushHeal = 0.3;
        /** D144 reward per settled clear (T3 marks, 余烬徽) and the cosmetic id of the first clear ever */
        public int rushMarks, rushBadges;
        public String rushTitle = "";
        /**
         * D174 stage 2b: more entries on the same engine. mode rush (D144, default) / echo 首领残响 (one boss, Q04) /
         * outpost 连战·前哨 (Q05). label = the player-facing name; claim = the weekly claim counter (entries sharing it
         * share the weekly claims); weekly = claims per week; mark_tier = tier of reward.marks; sigmarks = 首领徽记 of
         * EACH chain map per paid clear.
         */
        public String rushMode = "rush", rushLabel = "余烬连战", rushClaim = "p4_rush_claim";
        public int rushWeekly = 1, rushMarkTier = 3, rushSig;
        public boolean mainRush() { return "rush".equals(rushMode); }
        /** D391–D417 短征（runs yml {@code short:}）：P1 原生短本，日有奖帽，结算走 S40–S50 */
        public boolean shortExpedition;
        public int shortDailyCap;
        public String shortClaim = "";

        MapDef(String key, Map<?, ?> m) {
            this.key = key;
            name = str(m.get("name"), key);
            dungeon = str(m.get("dungeon"), "");
            template = str(m.get("template"), "");
            tier = (int) num(m.get("tier"), 1);
            dropLabel = str(m.get("drop_label"), "T" + tier);
            Map<?, ?> lo = m.get("loot") instanceof Map ? (Map<?, ?>) m.get("loot") : Collections.emptyMap();
            String lf = str(lo.get("family"), ""), lsl = str(lo.get("slot"), "");
            lootFamily = EmberRunRules.validFamily(lf) ? lf : null;
            lootSlot = "blade".equals(lsl) || "charm".equals(lsl) ? lsl : null;
            contentVersion = str(m.get("content_version"), "v1");
            mapVersion = str(m.get("map_version"), "");
            requires = str(m.get("requires"), "");
            unlocks = str(m.get("unlocks"), "");
            partyMin = (int) num(m.get("party_min"), -1);
            partyMax = (int) num(m.get("party_max"), -1);
            cost = (int) num(m.get("cost"), -1);
            weeklyCap = (int) num(m.get("weekly_cap"), 0);
            capGroup = str(m.get("cap_group"), "");
            hpPerMember = num(m.get("hp_per_member"), -1);
            dmgPerMember = num(m.get("dmg_per_member"), 0);
            purpose = str(m.get("purpose"), "");
            hint = str(m.get("hint"), ""); // D283 card lore (map-level; rooms keep their own hint)
            partyHint = str(m.get("party_hint"), "");
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
            fallCatchY = m.get("fall_catch_y") == null ? Double.NaN : num(m.get("fall_catch_y"), Double.NaN);
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
            if ("piece".equals(f.choiceSlot)) return "一次免费定向兑换：T" + f.choiceTier + " 标准件，族和部位（刃 / 护符）都自己选（绑定、不可分解）"; // E-review #6
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
        return layout(r.variant(useB), r.points.size(), roomSeed);
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
    // D104: the challenge values were eased (HP ×0.70, damage ×0.85) and the tier factors raised by the same ratio, so
    // the caps keep the same ABSOLUTE ceiling as before (1.6 ÷ 0.70 ≈ 2.3, 1.3 × 1.20 ≈ 1.56)
    public static final double ABYSS_MAX_HP = 2.3, ABYSS_MAX_DMG = 1.56;

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
    /** F-review #5 (D124): 余烬币 per surplus T3 mark when the coins cannot pay an abyss fee (0 = coins only) */
    public final int abyssFeeMarkCoin;
    /** Endgame #6 (D128): share of the stamina cost refunded for the first failed challenge / abyss run of the day */
    public final double failRefund;
    public final List<AbyssTier> abyss;
    private final List<Challenge> abyssCh;
    /** P2-1 (D66) weekly featured challenge: extra marks of the run tier, and how many clears per week get them */
    public final int rotationBonusMarks, rotationWeeklyCap;
    /** D108: marks of the map tier on a repeat NORMAL clear of the featured T1/T2 map (shares rotationWeeklyCap) */
    public final int rotationNormalBonusMarks;
    /** D116 seasons (runs yml `season:`): first Monday, weeks per season, threshold tier, places that get the top title */
    public final String seasonAnchor;
    public final int seasonWeeks, seasonDeepTier, seasonTop;
    /** D117 weekly goals (runs yml `weekly_goals:`): target per goal id, 余烬徽 per goal, bonus for all of them */
    public final Map<String, Integer> goalTargets;
    public final int goalReward, goalBonus;
    /** D118 raid revive (runs yml `raid_revive:`): extra last-phase revive at this boss HP ratio (0 = off), delay s */
    public final double raidLastReviveHp, raidReviveDelay;
    /** D138 repeat-run variety (runs yml `variety:`); never null (rates 0 when the block is missing) */
    public final Variety variety;
    /** D182 Extra.ELITE light moves (runs yml `elite_twists:`); never null (enabled false when missing) */
    public final EliteTwists eliteTwists;
    public final String worldPrefix;
    public final Map<String, MapDef> maps;
    /** P2-5 raids (runs yml `raids:`), keyed like maps (r01 …) */
    public final Map<String, MapDef> raids;
    /** D139 limited-time event dungeons (festival yml `dungeon:`, injected by EmberRunService.load as root `events:`) */
    public final Map<String, MapDef> events;
    /** D144 余烬连战 (runs yml `rush:`), keyed like maps (rush) */
    public final Map<String, MapDef> rush;
    /** D391–D417 短征 (runs yml `short:`), keyed like maps (sx01..sx11) */
    public final Map<String, MapDef> shortMaps;
    /** P2-9 (D81) loot bias weights; P2-9 (D82) raid_item quality floor */
    public final double lootOwnFamily, lootMapShare, lootSlotWeight;
    public final int raidItemQualityFloor;
    /** P2-8 weekly rotation modifiers (on the featured challenge map only; no multipliers, rewards unchanged) */
    public final List<Modifier> modifiers;

    /**
     * P2-8 (D80): one weekly rule on the featured challenge map that changes how the fight plays, never how much it pays
     * and never a multiplier: a heal-potion cap per run, an enemy-role substitution (only to roles the map defines),
     * or the first and third rooms trading their enemy groups.
     */
    /**
     * D138 repeat-run variety: on a normal Q01–Q07 run where every member already has the map's first clear, one room
     * gets an affixed elite (blazing / split / shield) and, sometimes, one room becomes a timed optional event. Pays
     * existing reward types only (余烬碎片 / 余烬核心) at settlement.
     */
    public static final class Variety {
        /** D138 + D171 Pack 2 + D181 Pack 3 + D189 Pack 4 + D196 Pack 5. reflect/vortex intentionally absent (rejected). Events: D138/D171/D179 + D191 Pack 4. */
        public static final List<String> KNOWN = Collections.unmodifiableList(java.util.Arrays.asList(
                "blazing", "split", "shield", "regen", "charge", "frost", "mortar", "molten", "venom", "jailer", "arcane", "firechain"));
        public static final List<String> EVENTS = Collections.unmodifiableList(java.util.Arrays.asList(
                "timed", "crystal", "escort", "hold", "beacon", "relay", "breach", "chain", "unscathed"));
        public final double affixRate, eventRate;
        public final List<String> affixes, events;
        public final int affixShard, eventCore, eventSecs;
        public final double blazeEvery, blazeRadius, blazeWarn, blazeDmg;
        public final int splitCount;
        public final double splitHp, shieldHp;
        // D171 regen / charge / frost
        public final double regenEvery, regenHeal, regenInterruptWindow, regenInterruptHp;
        public final double chargeEvery, chargeWarn, chargeLength, chargeWidth, chargeDmg;
        public final double frostRadius, frostTick;
        public final int frostAmplifier;
        // D181 mortar / molten
        public final double mortarEvery, mortarWarn, mortarRadius, mortarAhead, mortarDmg;
        public final double moltenDelay, moltenWarn, moltenRadius, moltenDmg;
        // D189 venom (毒十字) / jailer (禁锢)
        public final double venomEvery, venomWarn, venomArm, venomWidth, venomDmg;
        public final double jailerEvery, jailerWarn, jailerRadius, jailerRoot, jailerDmg;
        // D196 arcane (旋光: rotating beam, D3 Arcane Enchanted) / firechain (火链: tether to a room mob, D3 Fire Chains)
        public final double arcaneEvery, arcaneWarn, arcaneLength, arcaneWidth, arcaneSweep, arcaneSpin, arcaneDmg;
        public final double chainLinkWarn, chainLinkWidth, chainLinkTick, chainLinkDmg, chainLinkRange;
        // D171 crystal / escort / timed
        public final int crystalCount, crystalSecs, timedSecs;
        public final double escortHp;
        // D179 hold / beacon / relay
        public final double holdRadius, holdNeed;
        public final int holdSecs;
        public final double beaconHp, beaconAggroR, beaconTick, beaconBite;
        public final int relayCount, relaySecs;
        public final double relayRadius;
        // D191 breach (裂隙) / chain (连斩) / unscathed (无伤)
        public final double breachRadius, breachMin, breachMax, breachShrink, breachGrow;
        public final int chainNeed;
        public final double chainGap;
        public final int unscathedHits, unscathedPerMember;

        Variety(Map<?, ?> m) {
            affixRate = clamp01(num(m.get("affix_rate"), 0.0));
            eventRate = clamp01(num(m.get("event_rate"), 0.0));
            List<String> a = new ArrayList<String>();
            if (m.get("affixes") instanceof List) for (Object o : (List<?>) m.get("affixes")) {
                String id = String.valueOf(o).trim();
                if (KNOWN.contains(id) && !a.contains(id)) a.add(id);
            }
            affixes = Collections.unmodifiableList(a);
            List<String> ev = new ArrayList<String>();
            if (m.get("events") instanceof List) for (Object o : (List<?>) m.get("events")) {
                String id = String.valueOf(o).trim();
                if (EVENTS.contains(id) && !ev.contains(id)) ev.add(id);
            }
            if (ev.isEmpty() && eventRate > 0) ev.add("timed"); // D138 compat: no events list → timed only
            events = Collections.unmodifiableList(ev);
            affixShard = Math.max(0, (int) num(m.get("affix_shard"), 0));
            eventCore = Math.max(0, (int) num(m.get("event_core"), 0));
            eventSecs = Math.max(5, (int) num(m.get("event_secs"), 30));
            Map<?, ?> bz = m.get("blazing") instanceof Map ? (Map<?, ?>) m.get("blazing") : Collections.emptyMap();
            blazeEvery = Math.max(1.0, num(bz.get("every"), 3.0));
            blazeRadius = Math.max(0.5, num(bz.get("radius"), 1.5));
            blazeWarn = Math.max(0.5, num(bz.get("warn"), 1.0));
            blazeDmg = Math.max(0.0, num(bz.get("dmg"), 1.0));
            Map<?, ?> sp = m.get("split") instanceof Map ? (Map<?, ?>) m.get("split") : Collections.emptyMap();
            splitCount = Math.max(0, Math.min(4, (int) num(sp.get("count"), 2)));
            splitHp = Math.max(0.05, Math.min(1.0, num(sp.get("hp"), 0.5)));
            Map<?, ?> sh = m.get("shield") instanceof Map ? (Map<?, ?>) m.get("shield") : Collections.emptyMap();
            shieldHp = Math.max(1.0, num(sh.get("hp"), 1.6));
            Map<?, ?> rg = m.get("regen") instanceof Map ? (Map<?, ?>) m.get("regen") : Collections.emptyMap();
            regenEvery = Math.max(1.0, num(rg.get("every"), 4.0));
            regenHeal = Math.max(0.0, Math.min(1.0, num(rg.get("heal"), 0.10)));
            regenInterruptWindow = Math.max(0.5, num(rg.get("interrupt_window"), 1.5));
            regenInterruptHp = Math.max(0.0, Math.min(1.0, num(rg.get("interrupt_hp"), 0.05)));
            Map<?, ?> ch = m.get("charge") instanceof Map ? (Map<?, ?>) m.get("charge") : Collections.emptyMap();
            chargeEvery = Math.max(1.0, num(ch.get("every"), 5.5));
            chargeWarn = Math.max(1.2, num(ch.get("warn"), 1.2));
            chargeLength = Math.max(1.0, Math.min(8.0, num(ch.get("length"), 7)));
            chargeWidth = Math.max(0.5, num(ch.get("width"), 2.5));
            chargeDmg = Math.max(0.0, num(ch.get("dmg"), 1.0));
            Map<?, ?> fr = m.get("frost") instanceof Map ? (Map<?, ?>) m.get("frost") : Collections.emptyMap();
            frostRadius = Math.max(0.5, num(fr.get("radius"), 3.0));
            frostAmplifier = Math.max(0, Math.min(4, (int) num(fr.get("amplifier"), 1)));
            frostTick = Math.max(0.2, num(fr.get("tick"), 0.5));
            Map<?, ?> mo = m.get("mortar") instanceof Map ? (Map<?, ?>) m.get("mortar") : Collections.emptyMap();
            mortarEvery = Math.max(1.0, num(mo.get("every"), 5.0));
            mortarWarn = Math.max(1.2, num(mo.get("warn"), 1.2));
            mortarRadius = Math.max(0.5, num(mo.get("radius"), 2.0));
            mortarAhead = num(mo.get("ahead"), 0.0);
            mortarDmg = Math.max(0.0, num(mo.get("dmg"), 1.0));
            Map<?, ?> ml = m.get("molten") instanceof Map ? (Map<?, ?>) m.get("molten") : Collections.emptyMap();
            moltenDelay = Math.max(0.0, num(ml.get("delay"), 0.4));
            moltenWarn = Math.max(1.2, num(ml.get("warn"), 1.3));
            moltenRadius = Math.max(0.5, num(ml.get("radius"), 2.5));
            moltenDmg = Math.max(0.0, num(ml.get("dmg"), 1.2));
            Map<?, ?> vn = m.get("venom") instanceof Map ? (Map<?, ?>) m.get("venom") : Collections.emptyMap();
            venomEvery = Math.max(2.0, num(vn.get("every"), 6.0));
            venomWarn = Math.max(1.2, num(vn.get("warn"), 1.3));
            venomArm = Math.max(1.0, Math.min(6.0, num(vn.get("arm"), 4.0)));
            venomWidth = Math.max(0.5, Math.min(3.0, num(vn.get("width"), 1.5)));
            venomDmg = Math.max(0.0, num(vn.get("dmg"), 1.0));
            Map<?, ?> jl = m.get("jailer") instanceof Map ? (Map<?, ?>) m.get("jailer") : Collections.emptyMap();
            jailerEvery = Math.max(3.0, num(jl.get("every"), 7.0));
            jailerWarn = Math.max(1.2, num(jl.get("warn"), 1.2));
            jailerRadius = Math.max(0.5, Math.min(3.0, num(jl.get("radius"), 1.6)));
            jailerRoot = Math.max(0.0, Math.min(1.5, num(jl.get("root"), 1.0))); // root never longer than 1.5 s
            jailerDmg = Math.max(0.0, num(jl.get("dmg"), 0.5));
            Map<?, ?> ar = m.get("arcane") instanceof Map ? (Map<?, ?>) m.get("arcane") : Collections.emptyMap();
            arcaneEvery = Math.max(4.0, num(ar.get("every"), 8.0));
            arcaneWarn = Math.max(1.2, num(ar.get("warn"), 1.5));
            arcaneLength = Math.max(2.0, Math.min(6.0, num(ar.get("length"), 5.0)));
            arcaneWidth = Math.max(0.5, Math.min(2.0, num(ar.get("width"), 1.2)));
            arcaneSweep = Math.max(30.0, Math.min(180.0, num(ar.get("sweep"), 180.0))); // never more than a half turn
            arcaneSpin = Math.max(1.5, Math.min(6.0, num(ar.get("spin"), 3.0)));         // ≥ 1.5 s → ≤ 120°/s
            arcaneDmg = Math.max(0.0, num(ar.get("dmg"), 1.0));
            Map<?, ?> fc = m.get("firechain") instanceof Map ? (Map<?, ?>) m.get("firechain") : Collections.emptyMap();
            chainLinkWarn = Math.max(1.2, num(fc.get("warn"), 1.2));
            chainLinkWidth = Math.max(0.5, Math.min(2.0, num(fc.get("width"), 1.0)));
            chainLinkTick = Math.max(0.5, num(fc.get("tick"), 1.0));                     // per player, ≤ 2 burns / s
            chainLinkDmg = Math.max(0.0, Math.min(1.0, num(fc.get("dmg"), 0.3)));
            chainLinkRange = Math.max(3.0, Math.min(12.0, num(fc.get("range"), 10.0)));
            Map<?, ?> tm = m.get("timed") instanceof Map ? (Map<?, ?>) m.get("timed") : Collections.emptyMap();
            timedSecs = Math.max(5, (int) num(tm.get("secs"), eventSecs));
            Map<?, ?> cr = m.get("crystal") instanceof Map ? (Map<?, ?>) m.get("crystal") : Collections.emptyMap();
            crystalCount = Math.max(1, Math.min(8, (int) num(cr.get("count"), 3)));
            crystalSecs = Math.max(5, (int) num(cr.get("secs"), 35));
            Map<?, ?> es = m.get("escort") instanceof Map ? (Map<?, ?>) m.get("escort") : Collections.emptyMap();
            escortHp = Math.max(0.05, Math.min(1.0, num(es.get("hp"), 0.35)));
            Map<?, ?> ho = m.get("hold") instanceof Map ? (Map<?, ?>) m.get("hold") : Collections.emptyMap();
            holdRadius = Math.max(0.5, num(ho.get("radius"), 2.5));
            holdNeed = Math.max(1.0, num(ho.get("need"), 12.0));
            holdSecs = Math.max(5, (int) num(ho.get("secs"), 40));
            Map<?, ?> be = m.get("beacon") instanceof Map ? (Map<?, ?>) m.get("beacon") : Collections.emptyMap();
            beaconHp = Math.max(0.05, Math.min(1.0, num(be.get("hp"), 0.45)));
            beaconAggroR = Math.max(0.5, num(be.get("aggro_r"), 4.0));
            beaconTick = Math.max(0.2, num(be.get("tick"), 1.0));
            beaconBite = Math.max(0.0, Math.min(1.0, num(be.get("bite"), 0.08)));
            Map<?, ?> re = m.get("relay") instanceof Map ? (Map<?, ?>) m.get("relay") : Collections.emptyMap();
            relayCount = Math.max(2, Math.min(3, (int) num(re.get("count"), 3)));
            relayRadius = Math.max(0.5, num(re.get("radius"), 1.6));
            relaySecs = Math.max(5, (int) num(re.get("secs"), 40));
            Map<?, ?> br = m.get("breach") instanceof Map ? (Map<?, ?>) m.get("breach") : Collections.emptyMap();
            breachMin = Math.max(0.5, Math.min(4.0, num(br.get("min"), 1.5)));
            breachMax = Math.max(breachMin + 1.0, Math.min(10.0, num(br.get("max"), 7.0)));
            breachRadius = Math.max(breachMin + 0.5, Math.min(breachMax, num(br.get("radius"), 5.0)));
            breachShrink = Math.max(0.01, Math.min(1.0, num(br.get("shrink"), 0.15)));
            breachGrow = Math.max(0.0, Math.min(3.0, num(br.get("grow"), 0.8)));
            Map<?, ?> cn = m.get("chain") instanceof Map ? (Map<?, ?>) m.get("chain") : Collections.emptyMap();
            chainNeed = Math.max(2, Math.min(8, (int) num(cn.get("need"), 4)));
            chainGap = Math.max(1.0, Math.min(10.0, num(cn.get("gap"), 4.0)));
            Map<?, ?> us = m.get("unscathed") instanceof Map ? (Map<?, ?>) m.get("unscathed") : Collections.emptyMap();
            unscathedHits = Math.max(0, Math.min(20, (int) num(us.get("hits"), 4)));
            unscathedPerMember = Math.max(0, Math.min(10, (int) num(us.get("per_member"), 2)));
        }

        public boolean on() { return (affixRate > 0 && !affixes.isEmpty()) || eventRate > 0; }

        /** Chinese tag shown on the elite's name and in chat */
        public static String label(String id) {
            if ("blazing".equals(id)) return "炽热";
            if ("split".equals(id)) return "分裂";
            if ("shield".equals(id)) return "厚甲"; // D147: was 护盾 (it only has more HP, no shield bar)
            if ("regen".equals(id)) return "再生";
            if ("charge".equals(id)) return "冲锋";
            if ("frost".equals(id)) return "凝霜";
            if ("mortar".equals(id)) return "投弹";
            if ("molten".equals(id)) return "亡爆";
            if ("venom".equals(id)) return "毒十字";
            if ("jailer".equals(id)) return "禁锢";
            if ("arcane".equals(id)) return "旋光";
            if ("firechain".equals(id)) return "火链";
            return id;
        }

        /** Chinese name of a room event kind (timed / crystal / escort / hold / beacon / relay / breach / chain / unscathed). */
        public static String eventLabel(String kind) {
            if ("breach".equals(kind)) return "裂隙";
            if ("chain".equals(kind)) return "连斩";
            if ("unscathed".equals(kind)) return "无伤";
            if ("crystal".equals(kind)) return "砸余烬晶";
            if ("escort".equals(kind)) return "护宝兔";
            if ("hold".equals(kind)) return "占点";
            if ("beacon".equals(kind)) return "护灯";
            if ("relay".equals(kind)) return "传火";
            return "限时清房";
        }

        /** Soft time limit (seconds) for the given event kind. */
        public int eventLimit(String kind) {
            if ("crystal".equals(kind)) return crystalSecs;
            if ("escort".equals(kind) || "beacon".equals(kind)) return 0; // no countdown; lives until room clear or fail
            if ("breach".equals(kind) || "chain".equals(kind) || "unscathed".equals(kind)) return 0; // D191: no countdown
            if ("hold".equals(kind)) return holdSecs;
            if ("relay".equals(kind)) return relaySecs;
            return timedSecs;
        }

        /**
         * {affix room, affix id, event room, event kind} for this seed; entries are "" when not rolled.
         * Deterministic. Slot [3] defaults to "timed" when an event room is rolled (D138 callers that ignore it stay safe).
         */
        public String[] roll(long seed) {
            java.util.Random r = new java.util.Random(seed);
            String[] rooms = {"r1", "r2", "r3"};
            String ar = "", at = "", er = "", ek = "";
            if (r.nextDouble() < affixRate && !affixes.isEmpty()) {
                ar = rooms[r.nextInt(3)];
                at = affixes.get(r.nextInt(affixes.size()));
            }
            if (r.nextDouble() < eventRate && !events.isEmpty()) {
                er = rooms[r.nextInt(3)];
                ek = events.get(r.nextInt(events.size()));
            }
            return new String[]{ar, at, er, ek};
        }
    }

    /**
     * D182 奖励精英变招 Pack 1: Extra.ELITE (EmberQ0xElite) gets one fixed telegraphed light move per main map.
     * {@code dmg} in yml is a multiplier of the elite's atk (absolute damage = atk × dmg); shapes reuse the boss
     * telegraph system ({@code light: true}, warn ≥ 1.2). Rewards unchanged.
     */
    public static final class EliteTwists {
        public static final List<String> MAPS = Collections.unmodifiableList(java.util.Arrays.asList(
                "q01", "q02", "q03", "q04", "q05", "q06", "q07"));
        public final boolean enabled;
        /** seconds after spawn before the first cast may start (design §6: stagger vs affix elite) */
        public final double openDelay;
        public final Map<String, Twist> byMap;

        public static final class Twist {
            public final String move, name, hint;
            /** template skill: {@code dmg} is the atk multiplier until {@link #skill(double)} */
            public final Skill template;
            /** D185 Pack 2 second light move (null when absent) */
            public final Twist alt;
            Twist(String move, Map<?, ?> raw) {
                this(move, raw, true);
            }
            private Twist(String move, Map<?, ?> raw, boolean allowAlt) {
                this.move = move;
                String[] nh = names(move);
                this.name = nh[0];
                this.hint = nh[1];
                Map<Object, Object> m = new LinkedHashMap<Object, Object>();
                // shape → type (boss skills use type=); force light
                String shape = str(raw.get("shape"), str(raw.get("type"), "line"));
                m.put("type", shape);
                m.put("name", this.name);
                m.put("every", num(raw.get("every"), 14));
                m.put("warn", Math.max(1.2, num(raw.get("warn"), 1.2)));
                m.put("dmg", Math.max(0.0, num(raw.get("dmg"), 1.0))); // multiplier
                m.put("light", true);
                if (raw.containsKey("length")) m.put("length", raw.get("length"));
                if (raw.containsKey("width")) m.put("width", raw.get("width"));
                if (raw.containsKey("radius")) m.put("radius", raw.get("radius"));
                if (raw.containsKey("ahead")) m.put("ahead", raw.get("ahead"));
                if (raw.containsKey("angle")) m.put("angle", raw.get("angle"));
                if (raw.containsKey("range")) m.put("range", raw.get("range"));
                if (raw.containsKey("kb")) m.put("kb", raw.get("kb"));
                if (raw.containsKey("target")) m.put("target", raw.get("target"));
                if (raw.containsKey("start")) m.put("start", raw.get("start"));
                this.template = new Skill(m);
                if (allowAlt && raw.get("alt") instanceof Map) {
                    Map<?, ?> altRaw = (Map<?, ?>) raw.get("alt");
                    this.alt = new Twist(str(altRaw.get("move"), move + "_alt"), altRaw, false);
                } else {
                    this.alt = null;
                }
            }
            /** absolute-damage skill for an elite whose atk is {@code atk} */
            public Skill skill(double atk) { return template.withDmg(atk * template.dmg); }
        }

        EliteTwists(Map<?, ?> m) {
            enabled = Boolean.TRUE.equals(m.get("enabled")) || "true".equals(String.valueOf(m.get("enabled")));
            openDelay = Math.max(0.0, num(m.get("open_delay"), 3.0));
            Map<String, Twist> t = new LinkedHashMap<String, Twist>();
            for (String k : MAPS) {
                if (m.get(k) instanceof Map) {
                    Map<?, ?> raw = (Map<?, ?>) m.get(k);
                    String move = str(raw.get("move"), k);
                    t.put(k, new Twist(move, raw));
                }
            }
            byMap = Collections.unmodifiableMap(t);
        }

        /** null when disabled or the map has no twist row */
        public Twist forMap(String mapKey) {
            if (!enabled || mapKey == null) return null;
            return byMap.get(mapKey.toLowerCase(Locale.ROOT));
        }

        /** Chinese display name + one-line dodge hint (design §4) */
        static String[] names(String move) {
            if ("shove".equals(move)) return new String[]{"门廊推", "会放正前亮带，侧移再打"};
            if ("stomp".equals(move)) return new String[]{"焦焰踏", "会放脚下亮圈，走开再打"};
            if ("sweep".equals(move)) return new String[]{"誓印扫", "会放正前扇形，走到扇外再打"};
            if ("barge".equals(move)) return new String[]{"闸冲", "会放直线冲撞条带，离开再打"};
            if ("dust".equals(move)) return new String[]{"落尘", "会在你脚附近亮圈，提前走开再打"};
            if ("breath".equals(move)) return new String[]{"霜息", "会放正前扇形，侧移再打"};
            if ("slag".equals(move)) return new String[]{"矿渣劈", "会放正前亮带，侧移再打"};
            // D185 Pack 2
            if ("ashfan".equals(move)) return new String[]{"灰烬扇", "还会放正前扇形，走到扇外再打"};
            if ("sear".equals(move)) return new String[]{"焦线", "还会放正前亮带，侧移再打"};
            if ("oathstomp".equals(move)) return new String[]{"誓踏", "还会放脚下亮圈，走开再打"};
            if ("tidefan".equals(move)) return new String[]{"潮扇", "还会放正前扇形，走到扇外再打"};
            if ("rubble".equals(move)) return new String[]{"碎带", "还会放正前亮带，侧移再打"};
            if ("frostring".equals(move)) return new String[]{"霜环踏", "还会放脚下亮圈，走开再打"};
            if ("forgefan".equals(move)) return new String[]{"炉扇", "还会放正前扇形，走到扇外再打"};
            return new String[]{move, "有预警招式，躲开再打"};
        }
    }


    public static final class Modifier {
        public final String id, name, text;
        public final int potionCap;                 // 0 = no cap
        public final Map<String, String> remap;     // role → role
        public final boolean swapRooms;             // r1 ↔ r3 compositions
        public final boolean normal;                // D94: also on repeat NORMAL runs of the featured map
        /** D158 (review round 2 #5): multipliers for the mobs this rule converted (remap), so each swap has its own feel */
        public final double convHp, convAtk, convInterval, convSpeed;
        Modifier(Map<?, ?> m) {
            id = str(m.get("id"), "");
            name = str(m.get("name"), id);
            text = str(m.get("text"), "");
            potionCap = Math.max(0, (int) num(m.get("potion_cap"), 0));
            Map<String, String> r = new LinkedHashMap<String, String>();
            if (m.get("remap") instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) m.get("remap")).entrySet()) r.put(String.valueOf(e.getKey()), String.valueOf(e.getValue()));
            remap = Collections.unmodifiableMap(r);
            swapRooms = Boolean.TRUE.equals(m.get("swap_rooms")) || "true".equals(String.valueOf(m.get("swap_rooms")));
            normal = Boolean.TRUE.equals(m.get("normal")) || "true".equals(String.valueOf(m.get("normal")));
            Map<?, ?> cv = m.get("converted") instanceof Map ? (Map<?, ?>) m.get("converted") : Collections.emptyMap();
            convHp = clampMult(num(cv.get("hp"), 1.0));
            convAtk = clampMult(num(cv.get("atk"), 1.0));
            convInterval = clampMult(num(cv.get("interval"), 1.0));
            convSpeed = clampMult(num(cv.get("speed"), 1.0));
        }
        private static double clampMult(double v) { return Math.max(0.25, Math.min(4.0, v)); }
        /** true when converted mobs get any multiplier */
        public boolean tweaksConverted() { return convHp != 1.0 || convAtk != 1.0 || convInterval != 1.0 || convSpeed != 1.0; }
        /** damage per second factor of a converted mob vs. its plain role (atk / interval) */
        public double convDpsFactor() { return convAtk / convInterval; }
        /** the role actually spawned on map d (a target role the map does not define keeps the original) */
        public String role(String role, MapDef d) {
            String to = remap.get(role);
            return to != null && d != null && d.roles.containsKey(to) ? to : role;
        }
    }

    /** The modifier of the rotation week containing {@code day} (null when none are configured). */
    public Modifier modifierFor(java.time.LocalDate day) {
        if (modifiers.isEmpty()) return null;
        return modifiers.get(modifierIndex(EmberRunRules.weekIndex(day), modifiers.size(), maps.size()));
    }

    /**
     * D187: rule index of rotation week {@code w} for {@code n} rules and {@code k} featured maps (map = w mod k).
     * A pool size coprime with k keeps the plain w mod n (all n×k pairs in n×k weeks). When they share a factor
     * (14 rules × 7 maps) plain w mod n would pin each map to n/k rules, so the index is skewed:
     * (w mod k + w div k) mod n — consecutive weeks still differ and any n×k/gcd·k weeks meet every map×rule pair.
     */
    static int modifierIndex(long w, int n, int k) {
        if (n <= 0) return 0;
        if (k <= 1 || gcd(n, k) == 1) return (int) Math.floorMod(w, (long) n);
        return (int) Math.floorMod(Math.floorMod(w, (long) k) + Math.floorDiv(w, (long) k), (long) n);
    }

    private static int gcd(int a, int b) { return b == 0 ? Math.abs(a) : gcd(b, a % b); }

    private static double clamp01(double v) { return Math.max(0.0, Math.min(1.0, v)); }

    /** P2-9: the loot pool of map {@code d} for settlement (null when the map has no identity). */
    public EmberRunRules.LootBias lootBias(MapDef d) {
        if (d == null || (d.lootFamily == null && d.lootSlot == null)) return null;
        return new EmberRunRules.LootBias(d.lootFamily, d.lootSlot, lootOwnFamily, lootMapShare, lootSlotWeight);
    }

    /** P2-9 label for menus / codex, e.g. 「焚烬 · 刃」 or 「烬爆」 */
    public static String lootLabel(MapDef d) {
        if (d == null || d.lootFamily == null) return "不偏向";
        return EmberItemData.familyName(d.lootFamily) + (d.lootSlot == null ? "（部位不偏）" : " · " + ("blade".equals(d.lootSlot) ? "刃" : "护符"));
    }

    public Modifier modifier(String id) {
        if (id == null || id.isEmpty()) return null;
        if (id.startsWith(PLEDGE)) return pledgeModifier(id);
        for (Modifier m : modifiers) if (m.id.equals(id)) return m;
        return null;
    }

    // ---- D174 stage 2b 自选誓约 (Q06): the leader's own pick of the D94 normal-run rules, stored in the session as
    // "pledge:lean+reverse" (one modifier slot; the combined rule = potion cap min, swap_rooms or, remaps merged)
    public static final String PLEDGE = "pledge:";
    private final Map<String, Modifier> pledgeCache = new java.util.concurrent.ConcurrentHashMap<String, Modifier>();

    /** the rules a pledge may pick: the ones already allowed on repeat NORMAL runs (normal: true, D94 model: rewards only move down) */
    public List<Modifier> pledgePool() {
        List<Modifier> out = new ArrayList<Modifier>();
        for (Modifier m : modifiers) if (m.normal) out.add(m);
        return out;
    }

    /** ids of a pledge session modifier ("pledge:a+b" → [a, b]); empty for a weekly rule / none. Unknown ids are kept (counted by the caller against the pool). */
    public static List<String> pledgeIds(String modifier) {
        List<String> out = new ArrayList<String>();
        if (modifier == null || !modifier.startsWith(PLEDGE)) return out;
        for (String id : modifier.substring(PLEDGE.length()).split("\\+")) if (!id.isEmpty() && !out.contains(id)) out.add(id);
        return out;
    }

    private Modifier pledgeModifier(String key) {
        Modifier c = pledgeCache.get(key);
        if (c != null) return c;
        List<Modifier> parts = new ArrayList<Modifier>();
        for (String id : pledgeIds(key)) for (Modifier m : pledgePool()) if (m.id.equals(id)) parts.add(m);
        if (parts.isEmpty()) return null;
        Map<Object, Object> raw = new LinkedHashMap<Object, Object>();
        StringBuilder name = new StringBuilder(), text = new StringBuilder();
        int cap = 0;
        boolean swap = false;
        Map<String, String> remap = new LinkedHashMap<String, String>();
        double hp = 1, atk = 1, iv = 1, sp = 1;
        for (Modifier m : parts) {
            if (name.length() > 0) { name.append("+"); text.append("；"); }
            name.append(m.name);
            text.append(m.text);
            if (m.potionCap > 0) cap = cap == 0 ? m.potionCap : Math.min(cap, m.potionCap);
            swap |= m.swapRooms;
            remap.putAll(m.remap);
            hp *= m.convHp; atk *= m.convAtk; iv *= m.convInterval; sp *= m.convSpeed;
        }
        raw.put("id", key);
        raw.put("name", name.toString());
        raw.put("text", text.toString());
        if (cap > 0) raw.put("potion_cap", cap);
        if (swap) raw.put("swap_rooms", true);
        raw.put("normal", true);
        if (!remap.isEmpty()) raw.put("remap", remap);
        Map<Object, Object> cv = new LinkedHashMap<Object, Object>();
        cv.put("hp", hp); cv.put("atk", atk); cv.put("interval", iv); cv.put("speed", sp);
        raw.put("converted", cv);
        c = new Modifier(raw);
        pledgeCache.put(key, c);
        return c;
    }

    /** P2-8: layout from an explicit composition (the swap-rooms modifier spawns r3's group on r1's points and back). */
    public static List<String[]> layout(Map<String, Integer> c, int points, long roomSeed) {
        List<String[]> out = new ArrayList<String[]>();
        int off = points == 0 ? 0 : (int) Math.floorMod(roomSeed, (long) points);
        int i = 0;
        for (Map.Entry<String, Integer> e : c.entrySet()) {
            for (int k = 0; k < e.getValue(); k++) {
                out.add(new String[]{e.getKey(), String.valueOf((off + i) % Math.max(1, points))});
                i++;
            }
        }
        return out;
    }

    private EmberRunMaps(Map<?, ?> root) {
        version = (int) num(root.get("version"), 1);
        balanceVersion = (int) num(root.get("balance_version"), 1);
        ruleVersion = str(root.get("rule_version"), "g04-1") + "/b" + balanceVersion;
        cost = (int) num(root.get("cost"), 30);
        failRefund = Math.max(0.0, Math.min(1.0, num(root.get("fail_refund"), 0)));
        partyMin = (int) num(root.get("party_min"), 1);
        partyMax = (int) num(root.get("party_max"), 3);
        passSeconds = (int) num(root.get("pass_seconds"), 30);
        worldPrefix = str(root.get("world_prefix"), "dungeon_EmberQ0");
        Map<?, ?> rot = root.get("rotation") instanceof Map ? (Map<?, ?>) root.get("rotation") : java.util.Collections.emptyMap();
        rotationBonusMarks = Math.max(0, (int) num(rot.get("bonus_marks"), 0));
        rotationWeeklyCap = Math.max(0, (int) num(rot.get("weekly_cap"), 0));
        rotationNormalBonusMarks = Math.max(0, (int) num(rot.get("normal_bonus_marks"), 0));
        Map<?, ?> sea = root.get("season") instanceof Map ? (Map<?, ?>) root.get("season") : Collections.emptyMap();
        seasonAnchor = str(sea.get("anchor"), "2026-09-28");
        seasonWeeks = Math.max(1, (int) num(sea.get("weeks"), 4));
        seasonDeepTier = Math.max(1, (int) num(sea.get("deep_tier"), 8));
        seasonTop = Math.max(1, (int) num(sea.get("top"), 3));
        Map<?, ?> wg = root.get("weekly_goals") instanceof Map ? (Map<?, ?>) root.get("weekly_goals") : Collections.emptyMap();
        Map<String, Integer> gt = new LinkedHashMap<String, Integer>();
        if (wg.get("targets") instanceof Map) for (Map.Entry<?, ?> e : ((Map<?, ?>) wg.get("targets")).entrySet())
            gt.put(String.valueOf(e.getKey()), Math.max(1, (int) num(e.getValue(), 1)));
        goalTargets = Collections.unmodifiableMap(gt);
        goalReward = Math.max(0, (int) num(wg.get("reward"), 0));
        goalBonus = Math.max(0, (int) num(wg.get("bonus"), 0));
        Map<?, ?> rv = root.get("raid_revive") instanceof Map ? (Map<?, ?>) root.get("raid_revive") : Collections.emptyMap();
        raidLastReviveHp = clamp01(num(rv.get("last_phase_hp"), 0.0));
        raidReviveDelay = Math.max(0.0, num(rv.get("delay"), 10.0));
        variety = new Variety(root.get("variety") instanceof Map ? (Map<?, ?>) root.get("variety") : Collections.emptyMap());
        eliteTwists = new EliteTwists(root.get("elite_twists") instanceof Map ? (Map<?, ?>) root.get("elite_twists") : Collections.emptyMap());
        Map<?, ?> lb = root.get("loot_bias") instanceof Map ? (Map<?, ?>) root.get("loot_bias") : Collections.emptyMap();
        lootOwnFamily = clamp01(num(lb.get("own_family"), EmberRunRules.TARGET_WEIGHT));
        lootMapShare = clamp01(num(lb.get("map_share"), 0.5));
        lootSlotWeight = clamp01(num(lb.get("slot"), 0.5));
        Map<?, ?> ri = root.get("raid_item") instanceof Map ? (Map<?, ?>) root.get("raid_item") : Collections.emptyMap();
        raidItemQualityFloor = Math.max(0, Math.min(3, (int) num(ri.get("quality_floor"), 0)));
        List<Modifier> mods = new ArrayList<Modifier>();
        if (rot.get("modifiers") instanceof List) for (Object o : (List<?>) rot.get("modifiers")) if (o instanceof Map) mods.add(new Modifier((Map<?, ?>) o));
        modifiers = Collections.unmodifiableList(mods);
        challenge = root.get("challenge") instanceof Map ? new Challenge((Map<?, ?>) root.get("challenge")) : null;
        Map<?, ?> ab = root.get("abyss") instanceof Map ? (Map<?, ?>) root.get("abyss") : Collections.emptyMap();
        abyssRequires = str(ab.get("requires"), "q07");
        abyssFeeMarkCoin = Math.max(0, (int) num(ab.get("fee_mark_coin"), 0));
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
        Map<String, MapDef> ev = new LinkedHashMap<String, MapDef>();
        if (root.get("events") instanceof Map) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) root.get("events")).entrySet()) {
                if (e.getValue() instanceof Map) {
                    String k = String.valueOf(e.getKey()).toLowerCase(Locale.ROOT);
                    MapDef d = new MapDef(k, (Map<?, ?>) e.getValue());
                    d.event = true;
                    ev.put(k, d);
                }
            }
        }
        events = Collections.unmodifiableMap(ev);
        Map<String, MapDef> ru = new LinkedHashMap<String, MapDef>();
        if (root.get("rush") instanceof Map) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) root.get("rush")).entrySet()) {
                if (e.getValue() instanceof Map) {
                    String k = String.valueOf(e.getKey()).toLowerCase(Locale.ROOT);
                    MapDef d = rushDef(k, (Map<?, ?>) e.getValue(), root.get("maps") instanceof Map ? (Map<?, ?>) root.get("maps") : Collections.emptyMap());
                    if (d != null) ru.put(k, d);
                }
            }
        }
        rush = Collections.unmodifiableMap(ru);
        Map<String, MapDef> sh = new LinkedHashMap<String, MapDef>();
        if (root.get("short") instanceof Map) {
            for (Map.Entry<?, ?> e : ((Map<?, ?>) root.get("short")).entrySet()) {
                if (e.getValue() instanceof Map) {
                    String k = String.valueOf(e.getKey()).toLowerCase(Locale.ROOT);
                    MapDef d = shortDef(k, (Map<?, ?>) e.getValue());
                    if (d != null) sh.put(k, d);
                }
            }
        }
        shortMaps = Collections.unmodifiableMap(sh);
    }

    /**
     * D391/D392: one short-expedition entry. Requires full rooms/boss (Director FIGHTING + settle).
     */
    static MapDef shortDef(String key, Map<?, ?> m) {
        MapDef d = new MapDef(key, m);
        d.shortExpedition = true;
        d.shortDailyCap = (int) num(m.get("daily_reward_cap"), EmberShortRules.DAILY_CAP);
        d.shortClaim = str(m.get("claim"), EmberShortRules.CLAIM);
        // reward / first_clear amounts live in EmberEconomy S40–S50; yml mirrors for readability / source-map
        return d;
    }

    /**
     * D144: one rush entry. Each chain key names a main map whose boss block (mm / name / HP / attack / moves) is
     * reused as-is, moved to the rush hall (boss.at / boss.area of the rush entry, never waiting in the area).
     * Returns null when a chain map or its boss is missing (validate() then reports the empty rush).
     */
    static MapDef rushDef(String key, Map<?, ?> m, Map<?, ?> mapsRaw) {
        Map<?, ?> hall = m.get("boss") instanceof Map ? (Map<?, ?>) m.get("boss") : Collections.emptyMap();
        List<Boss> chain = new ArrayList<Boss>();
        List<String> keys = new ArrayList<String>();
        if (m.get("chain") instanceof List) for (Object o : (List<?>) m.get("chain")) {
            String ck = String.valueOf(o).toLowerCase(Locale.ROOT);
            Object src = mapsRaw.get(ck);
            if (!(src instanceof Map) || !(((Map<?, ?>) src).get("boss") instanceof Map)) return null;
            Map<Object, Object> b = new LinkedHashMap<Object, Object>((Map<?, ?>) ((Map<?, ?>) src).get("boss"));
            if (hall.get("at") != null) b.put("at", hall.get("at"));
            if (hall.get("area") != null) b.put("area", hall.get("area"));
            b.put("wait_in_area", false);
            chain.add(new Boss(b));
            keys.add(ck);
        }
        if (chain.isEmpty()) return null;
        Map<Object, Object> mm = new LinkedHashMap<Object, Object>(m);
        Map<Object, Object> first = new LinkedHashMap<Object, Object>((Map<?, ?>) ((Map<?, ?>) mapsRaw.get(keys.get(0))).get("boss"));
        if (hall.get("at") != null) first.put("at", hall.get("at"));
        if (hall.get("area") != null) first.put("area", hall.get("area"));
        first.put("wait_in_area", false);
        mm.put("boss", first);
        MapDef d = new MapDef(key, mm);
        d.rush = true;
        d.chain = Collections.unmodifiableList(chain);
        d.chainKeys = Collections.unmodifiableList(keys);
        d.rushHp = num(m.get("boss_hp"), 1.0);
        d.rushDmg = num(m.get("boss_dmg"), 1.0);
        d.rushBreak = num(m.get("break_secs"), 10);
        d.rushHeal = num(m.get("heal"), 0.3);
        Map<?, ?> rw = m.get("reward") instanceof Map ? (Map<?, ?>) m.get("reward") : Collections.emptyMap();
        d.rushMarks = (int) num(rw.get("marks"), 0);
        d.rushBadges = (int) num(rw.get("badges"), 0);
        d.rushTitle = str(rw.get("title"), "");
        d.rushMarkTier = (int) num(rw.get("mark_tier"), 3);
        d.rushSig = (int) num(rw.get("sigmarks"), 0);
        d.rushMode = str(m.get("mode"), "rush").toLowerCase(Locale.ROOT);
        d.rushLabel = str(m.get("label"), "rush".equals(d.rushMode) ? "余烬连战" : d.name);
        d.rushClaim = str(m.get("claim"), "p4_rush_claim");
        d.rushWeekly = (int) num(m.get("weekly"), 1);
        return d;
    }

    public int partyMin(MapDef m) { return m != null && m.partyMin > 0 ? m.partyMin : partyMin; }
    public int partyMax(MapDef m) { return m != null && m.partyMax > 0 ? m.partyMax : partyMax; }
    public int cost(MapDef m) {
        if (m != null && (m.event || m.rush)) return Math.max(0, m.cost); // D139 / D144: 0 = free (not the file default)
        return m != null && m.cost > 0 ? m.cost : cost;
    }

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
        if (d == null) d = raids.get(k);
        if (d == null) d = rush.get(k);
        if (d == null) d = shortMaps.get(k);
        return d != null ? d : events.get(k);
    }

    public MapDef byDungeon(String dungeonId) {
        for (MapDef d : maps.values()) if (d.dungeon.equalsIgnoreCase(dungeonId)) return d;
        for (MapDef d : raids.values()) if (d.dungeon.equalsIgnoreCase(dungeonId)) return d;
        for (MapDef d : events.values()) if (d.dungeon.equalsIgnoreCase(dungeonId)) return d;
        for (MapDef d : rush.values()) if (d.dungeon.equalsIgnoreCase(dungeonId)) return d;
        for (MapDef d : shortMaps.values()) if (d.dungeon.equalsIgnoreCase(dungeonId)) return d;
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
        for (MapDef d : events.values()) {
            String p = "dungeon_" + d.dungeon + "_";
            if (worldName.regionMatches(true, 0, p, 0, p.length())) return d;
        }
        for (MapDef d : rush.values()) {
            String p = "dungeon_" + d.dungeon + "_";
            if (worldName.regionMatches(true, 0, p, 0, p.length())) return d;
        }
        for (MapDef d : shortMaps.values()) {
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
        for (MapDef d : events.values()) {
            String e = d.validate();
            if (e != null) out.add(e);
            if (!("dungeon_" + d.dungeon).toLowerCase(Locale.ROOT).startsWith(worldPrefix.toLowerCase(Locale.ROOT)))
                out.add(d.key + ": dungeon " + d.dungeon + " outside world_prefix " + worldPrefix);
            if (maps.containsKey(d.key) || raids.containsKey(d.key)) out.add(d.key + ": event key collides with a map / raid");
        }
        for (MapDef d : rush.values()) { // D144: no rooms by design; the chain bosses come from the main maps
            if (d.dungeon.isEmpty()) out.add(d.key + ": dungeon missing");
            if (d.chain.size() < ("echo".equals(d.rushMode) ? 1 : 2)) out.add(d.key + ": rush chain needs 2+ bosses (echo: 1)");
            if (!java.util.Arrays.asList("rush", "echo", "outpost").contains(d.rushMode)) out.add(d.key + ": rush mode rush / echo / outpost");
            if (d.rushWeekly < 1 || d.rushWeekly > 3 || d.rushSig < 0 || d.rushSig > 3 || d.rushMarks > 1 || d.rushMarkTier < 1 || d.rushMarkTier > 3)
                out.add(d.key + ": rush weekly 1..3 / sigmarks 0..3 / marks ≤ 1 / mark_tier 1..3");
            if (!d.mainRush() && (d.rushClaim.equals("p4_rush_claim") || !d.rushClaim.startsWith("p4_"))) out.add(d.key + ": a stage-2b entry needs its own p4_* claim counter");
            String claimErr = EmberCounters.rushClaimError(d.rushClaim); // D206: config claim keys may not reuse a registered counter family
            if (claimErr != null) out.add(d.key + ": " + claimErr);
            for (MapDef o : rush.values()) if (o != d && o.rushClaim.equals(d.rushClaim) && o.rushWeekly != d.rushWeekly) out.add(d.key + ": entries sharing " + d.rushClaim + " need the same weekly");
            if (d.boss == null || d.boss.at == null || d.boss.area == null) out.add(d.key + ": rush hall boss.at / area missing");
            if (maps.containsKey(d.key) || raids.containsKey(d.key)) out.add(d.key + ": rush key collides with a map / raid");
            if (d.mainRush() && (d.rushHp < 1.0 || d.rushHp > 3.0 || d.rushDmg < 1.0 || d.rushDmg > 2.0)) out.add(d.key + ": rush boss_hp 1..3 / boss_dmg 1..2");
            // D174 stage 2b: echo / outpost lift T1–T2 bosses to the player's T2 level (rushsim-style tuning, DESIGN §4)
            if (!d.mainRush() && (d.rushHp < 1.0 || d.rushHp > 15.0 || d.rushDmg < 1.0 || d.rushDmg > 3.0)) out.add(d.key + ": echo / outpost boss_hp 1..15 / boss_dmg 1..3");
            if (d.rushHeal < 0 || d.rushHeal > 1 || d.rushBreak < 3) out.add(d.key + ": rush heal 0..1 / break_secs ≥ 3");
            if (!("dungeon_" + d.dungeon).toLowerCase(Locale.ROOT).startsWith(worldPrefix.toLowerCase(Locale.ROOT)))
                out.add(d.key + ": dungeon " + d.dungeon + " outside world_prefix " + worldPrefix);
        }
        for (MapDef d : shortMaps.values()) { // D391 FAIL: rooms/boss required (same shape as main maps)
            String e = d.validate();
            if (e != null) out.add(e);
            if (d.dungeon.isEmpty()) out.add(d.key + ": dungeon missing");
            if (d.requires == null || d.requires.isEmpty()) out.add(d.key + ": requires missing");
            if (d.shortDailyCap < 1 || d.shortDailyCap > 10) out.add(d.key + ": daily_reward_cap 1..10");
            if (d.shortClaim == null || d.shortClaim.isEmpty() || !d.shortClaim.startsWith("p1_")) out.add(d.key + ": claim must be p1_* day counter");
            if (d.cost >= 0 && d.cost != 30 && d.cost != 0) out.add(d.key + ": short cost should be 30 (or 0 for free test)");
            if (maps.containsKey(d.key) || raids.containsKey(d.key) || rush.containsKey(d.key) || events.containsKey(d.key))
                out.add(d.key + ": short key collides with a map / raid / rush / event");
            // EmberSx* is outside world_prefix dungeon_EmberQ0; scope.world_prefixes must list dungeon_EmberSx
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

package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * G04 encounter runtime of ONE P1 run instance (book §10.I, ch. 11–13, adapted to the real ember_daily* rooms):
 * room trigger by entering the room box (once), seeded A/B composition on the configured points, doors open when the
 * room's live enemies reach zero, boss after the last room, telegraphed boss skills, the extra event at its fixed anchor.
 * It never pays anything; boss death / extra completion are reported to {@link EmberRunService}.
 */
final class EmberRunDirector {

    static final class Tracked {
        final LivingEntity le;
        final String role;          // melee / ranged / heavy / caster / treasure / elite / boss / add
        final String roomId;        // r1.. / boss / event
        final EmberRunMaps.Pt home;
        final EmberRunMaps.Box leash;
        final double atk, interval, range;
        final EmberRunMaps.Role def;
        long lastHit;
        int stuck;
        long nextCast;
        long castAt;
        Vector castDir;
        Location castOrigin;
        String affix;               // D138 repeat-run variety: blazing / split / shield (null = plain)
        boolean splitAdd;           // D141: spawned by a split elite (counts as the affixed elite for 破缀 / 拆分)
        long affixNext, affixAt;
        Location affixOrigin;
        Tracked(LivingEntity le, String role, String roomId, EmberRunMaps.Pt home, EmberRunMaps.Box leash,
                double atk, double interval, double range, EmberRunMaps.Role def) {
            this.le = le; this.role = role; this.roomId = roomId; this.home = home; this.leash = leash;
            this.atk = atk; this.interval = interval; this.range = range; this.def = def;
        }
        boolean caster() { return "caster".equals(role); }
        boolean boss() { return "boss".equals(role); }
    }

    private final EmberRunService svc;
    final EmberRunSession s;
    final EmberRunMaps.MapDef def;
    final World w;
    /** §18.1 challenge overrides of this run (null = normal) */
    final EmberRunMaps.Challenge ch;
    final Map<UUID, Tracked> mobs = new HashMap<UUID, Tracked>();

    private int next;                 // index of the next room to trigger
    private String activeRoom;        // room currently fighting
    private long bossAt;              // boss spawn time (after the warning)
    Tracked boss;
    private boolean bossDead;
    /** D144 余烬连战: index of the current chain boss (0 for every other run) */
    int chainIdx;
    private boolean rushArmed;
    /** D144: the boss of this stage (rush: the chain boss; else the map's boss) */
    EmberRunMaps.Boss bossDef() { return def.chain.isEmpty() ? def.boss : def.chain.get(Math.min(chainIdx, def.chain.size() - 1)); }
    private EmberRunMaps.Skill pending;
    private long pendingAt;
    private Location lockOrigin;
    private Vector lockDir;
    private long[] nextAt = new long[0]; // per skill, anchored at the boss spawn (§10.I: cooldowns from fight start)
    private long recoverUntil;           // §10.I 收招: no new skill before this
    private int casts;
    private EmberRunMaps.Skill follow;
    private long followStart;
    private boolean addsDone;
    private long addsAt;
    private boolean extraSpawned;
    UUID extraMob;
    Location chest;
    private boolean finished;
    final List<String> anomalies = new ArrayList<String>();

    /** P2-8 weekly rotation modifier of this run (null = none) */
    private final EmberRunMaps.Modifier mod;

    EmberRunDirector(EmberRunService svc, EmberRunSession s, EmberRunMaps.MapDef def, World w) {
        this.svc = svc;
        this.s = s;
        this.def = def;
        this.w = w;
        this.ch = !s.challenge || svc.maps() == null ? null
                : s.abyss > 0 && svc.maps().abyssChallenge(s.abyss) != null ? svc.maps().abyssChallenge(s.abyss) // P2-2
                : svc.maps().challenge;
        this.mod = svc.maps() == null ? null : svc.maps().modifier(s.modifier);
        for (String c : s.cleared) if (def.roomIndex(c) >= next) next = def.roomIndex(c) + 1;
    }

    /** Doors closed as in the template (cached instance worlds are reused by DP pre-folder). */
    void attach() {
        for (int i = 0; i < def.rooms.size(); i++) {
            EmberRunMaps.Room r = def.rooms.get(i);
            if (r.door != null && i >= next) setBox(r.door, Material.IRON_FENCE, Material.AIR);
        }
        for (EmberRunMaps.Box b : def.rails) setBox(b, Material.IRON_FENCE, Material.AIR);
        for (EmberRunMaps.Pt c : def.clear) {
            Block bl = w.getBlockAt((int) Math.floor(c.x), (int) Math.floor(c.y), (int) Math.floor(c.z));
            if (bl.getType() != Material.AIR) bl.setType(Material.AIR);
        }
        holo("entry", "§6" + def.name, def.rush ? "§7" + def.chain.size() + " 个首领依次现身 · 全部击败后统一结算" : "§7清空三处房间开门 · 击败首领后统一结算");
        int items = 0;
        for (org.bukkit.Chunk c : w.getLoadedChunks()) items += purgeItems(c);
        if (items > 0) svc.log().info("[P1 run] " + s.runId + " removed " + items + " template item entities at attach");
    }

    /**
     * B2.157: the ember_daily* templates were saved with dropped-torch item entities lying in and around the rooms
     * (builder scrub leftovers, e.g. 11 torches in ember_daily_frost) — players picked them up as "old drops".
     * Instance only: every item entity in a P1 run chunk is removed when the chunk loads / the run attaches.
     */
    static int purgeItems(org.bukkit.Chunk c) {
        int n = 0;
        for (Entity e : c.getEntities()) if (e instanceof org.bukkit.entity.Item) { e.remove(); n++; }
        return n;
    }

    boolean finished() { return finished; }

    // ------------------------------------------------------------------ §9 holograms / safe points

    private final Map<String, List<Entity>> holos = new HashMap<String, List<Entity>>();

    /** §9: at most two lines at a book anchor (marker armor stands, instance only; the template has none). */
    private void holo(String key, String... lines) {
        unholo(key);
        EmberRunMaps.Pt a = def.holo.get(key);
        if (a == null) return;
        List<Entity> out = new ArrayList<Entity>();
        for (int i = 0; i < lines.length; i++) {
            Location l = new Location(w, a.x + 0.5, a.y - 0.3 * i, a.z + 0.5);
            org.bukkit.entity.ArmorStand st = w.spawn(l, org.bukkit.entity.ArmorStand.class);
            st.setVisible(false);
            st.setGravity(false);
            st.setMarker(true);
            st.setSmall(true);
            st.setInvulnerable(true);
            st.setCustomName(lines[i]);
            st.setCustomNameVisible(true);
            out.add(st);
        }
        holos.put(key, out);
    }

    private void unholo(String key) {
        List<Entity> l = holos.remove(key);
        if (l != null) for (Entity e : l) e.remove();
    }

    /** §20.5: reconnect target — the last cleared room's safe point; once the boss is up (or dead) the RB entry side. */
    Location safePoint() {
        EmberRunMaps.Pt p;
        if (boss != null || bossDead || def.rush) p = def.safe.get("rb"); // D144: the rush is the boss hall only
        else p = def.safe.get(next == 0 ? "r0" : def.rooms.get(Math.min(next, def.rooms.size()) - 1).id);
        if (p == null) return null;
        return new Location(w, p.x + 0.5, p.y, p.z + 0.5, 0f, 0f);
    }

    // ------------------------------------------------------------------ tick (every 5 ticks)

    private long leashAt;

    void tick(long now) {
        if (finished || EmberRunSession.PREPARE.equals(s.state)) return; // rooms wait until the entry is committed
        if (def.raid && now >= leashAt) { leashAt = now + 1000L; svc.leashFallen(this); } // D106
        // room trigger: a committed participant walks into the next room box
        if (activeRoom == null && boss == null && bossAt == 0 && next < def.rooms.size()) {
            EmberRunMaps.Room r = def.rooms.get(next);
            for (Player p : participantsHere()) {
                Location l = p.getLocation();
                if (r.trigger.contains(l.getX(), l.getY(), l.getZ())) { spawnRoom(r); break; }
            }
        }
        // passages (spire): once the room is cleared, stepping into the passage box moves the player up
        for (EmberRunMaps.Link k : def.links) {
            if (!s.cleared.contains(k.after)) continue;
            for (Player p : participantsHere()) {
                Location l = p.getLocation();
                if (!k.from.contains(l.getX(), l.getY(), l.getZ())) continue;
                p.teleport(new Location(w, k.to.x + 0.5, k.to.y, k.to.z + 0.5, k.yaw, 0f));
                w.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 0.8f, 1.2f);
                if (!k.label.isEmpty()) p.sendMessage("§7" + k.label);
            }
        }
        // F-review Q04 (D122): a player who drops below the map's floors (pool, ground outside) goes back to the room
        if (!Double.isNaN(def.fallCatchY)) {
            for (Player p : participantsHere()) {
                if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead() || p.getLocation().getY() >= def.fallCatchY) continue;
                EmberRunMaps.Pt sp = activeRoom != null ? def.safe.get(activeRoom) : null;
                Location to = sp != null ? new Location(w, sp.x + 0.5, sp.y, sp.z + 0.5, p.getLocation().getYaw(), 0f) : safePoint();
                if (to == null) continue;
                p.setFallDistance(0f);
                p.teleport(to);
                p.sendMessage("§7掉出房间了，已送回房间（下面是水渠，不是路）");
                svc.log().info("[P1 run] " + s.runId + " fall-catch " + p.getName() + " → " + (activeRoom != null ? activeRoom : "safe"));
            }
        }
        // mob upkeep: leash, stuck, void
        Iterator<Map.Entry<UUID, Tracked>> it = mobs.entrySet().iterator();
        while (it.hasNext()) {
            Tracked t = it.next().getValue();
            if (t.le.isDead()) continue; // death event handles it
            if (!t.le.isValid()) { // unloaded / removed by someone else
                anomaly(t, "entity vanished");
                it.remove();
                continue;
            }
            Location l = t.le.getLocation();
            if (t.leash != null && l.getY() < t.leash.y0 - 4) {
                if (t.boss()) { // B2.155: removing the boss would leave the run without a kill → no settlement ever
                    String line = String.format(Locale.ROOT, "%s %s@%s: fell below the room → back to its point", t.roomId, t.role, t.home);
                    anomalies.add(line);
                    svc.log().warning("[P1 run] " + s.runId + " anomaly " + line);
                    t.le.setFallDistance(0f);
                    t.le.teleport(new Location(w, t.home.x + 0.5, t.home.y, t.home.z + 0.5, l.getYaw(), l.getPitch()));
                    continue;
                }
                anomaly(t, "fell below the room");
                t.le.remove();
                it.remove();
                continue;
            }
            // B2.165: also "out" when standing well above its own floor (on a roof / wall top it glitched onto)
            boolean out = t.leash != null && !t.leash.containsGrown(l.getX(), l.getY(), l.getZ(), 2.0)
                    || l.getY() > t.home.y + 4.5
                    || !Double.isNaN(def.fallCatchY) && l.getY() < def.fallCatchY; // D122: in the pool / outside → home
            Block head = l.clone().add(0, Math.min(1.5, t.le.getEyeHeight()), 0).getBlock();
            boolean inWall = head.getType().isOccluding();
            if (out || inWall) {
                t.stuck++;
                if (t.stuck >= 6 && inWall) { // repeatedly inside a wall even after going home: drop it, keep a record
                    anomaly(t, "stuck in block " + head.getType());
                    t.le.remove();
                    it.remove();
                    continue;
                }
                if (out || t.stuck >= (t.boss() ? 1 : 3)) t.le.teleport(new Location(w, t.home.x + 0.5, t.home.y, t.home.z + 0.5, l.getYaw(), l.getPitch()));
            } else if (t.stuck > 0) {
                t.stuck--;
            }
            if (t.caster()) casterTick(t, now);
            if (t.affix != null) affixTick(t, now);
        }
        if (eventStart > 0 && !eventWarned && activeRoom != null && activeRoom.equals(s.eventRoom)
                && now >= eventStart + (svc.maps().variety.eventSecs - 10) * 1000L) {
            eventWarned = true;
            svc.tellRun(s, "§b限时清房 §7还剩 §e10 秒");
        }
        // room cleared → door, event, next
        if (activeRoom != null && aliveIn(activeRoom) == 0) roomCleared(def.room(activeRoom));
        // D144 余烬连战: no rooms — the first boss shows up a few seconds after the entry is committed
        if (def.rush && !rushArmed && boss == null && bossAt == 0 && !bossDead) {
            rushArmed = true;
            bossAt = now + 5000L;
            svc.onRushStart(s, def, bossDef());
        }
        // boss
        if (bossAt > 0 && boss == null && !bossDead) {
            if (now >= bossAt && (!bossDef().waitInArea || bossDef().area == null || now >= bossAt + 20000L || anyoneIn(bossDef().area))) spawnBoss(now);
            else warnCircle(new Location(w, bossDef().at.x + 0.5, bossDef().at.y + 0.1, bossDef().at.z + 0.5), 1.5, Particle.FLAME);
        }
        if (boss != null && !bossDead) bossTick(now);
    }

    /** D106: when the next raid revive happens (null = no revive point left in this run). */
    String nextRevive() {
        if (bossDead || finished) return null;
        if (boss == null) {
            if (activeRoom != null) return def.roomIndex(activeRoom) + 1 < def.rooms.size() ? "下一个房间开打时" : "首领现身时";
            return next < def.rooms.size() ? "下一个房间开打时" : "首领现身时";
        }
        EmberRunMaps.Boss b = bossDef();
        boolean gated = false;
        for (EmberRunMaps.Skill sk : b.skills) if (sk.below <= 1.0) gated = true;
        if (gated && !phaseTold) return "首领进入第二阶段时";
        if (b.adds != null && !addsDone) return "首领半血转阶段时";
        double hp = svc.maps().raidLastReviveHp; // D118: one extra revive in the last phase
        if (hp > 0 && !lastRevDone) {
            if (lastRevAt > 0) return "约 " + Math.max(1, (lastRevAt - System.currentTimeMillis() + 999) / 1000) + " 秒后（最后阶段额外一次）";
            return "首领降到 " + Math.round(hp * 100) + "% 生命后约 " + Math.round(svc.maps().raidReviveDelay) + " 秒（最后阶段额外一次）";
        }
        return null;
    }

    /** D118: every phase change of this boss has happened */
    private boolean lastPhase() {
        EmberRunMaps.Boss b = bossDef();
        boolean gated = false;
        for (EmberRunMaps.Skill sk : b.skills) if (sk.below <= 1.0) gated = true;
        return (!gated || phaseTold) && (b.adds == null || addsDone);
    }

    private long lastRevAt;
    private boolean lastRevDone;

    private List<Player> participantsHere() {
        List<Player> out = new ArrayList<Player>();
        // D106: a fallen member watching in spectator mode never triggers rooms / passages / boss halls
        for (Player p : w.getPlayers()) if (s.committed.contains(p.getUniqueId()) && !p.isDead()
                && p.getGameMode() != org.bukkit.GameMode.SPECTATOR) out.add(p);
        return out;
    }

    private boolean anyoneIn(EmberRunMaps.Box b) {
        for (Player p : participantsHere()) { Location l = p.getLocation(); if (b.contains(l.getX(), l.getY(), l.getZ())) return true; }
        return false;
    }

    private int aliveIn(String room) {
        int n = 0;
        for (Tracked t : mobs.values()) if (room.equals(t.roomId) && !t.le.isDead()) n++;
        return n;
    }

    // ------------------------------------------------------------------ rooms

    private void spawnRoom(EmberRunMaps.Room r) {
        activeRoom = r.id;
        boolean b = s.variantB(r.id);
        List<String[]> lay;
        EmberRunMaps.Room swap = mod == null || !mod.swapRooms ? null
                : "r1".equals(r.id) ? def.room("r3") : "r3".equals(r.id) ? def.room("r1") : null;
        lay = swap == null ? EmberRunMaps.layout(r, b, s.roomSeed(r.id))
                : EmberRunMaps.layout(swap.variant(b), r.points.size(), s.roomSeed(r.id)); // P2-8 逆行
        java.util.Set<Integer> converted = new java.util.HashSet<Integer>();
        if (mod != null) {
            StringBuilder roles = new StringBuilder();
            for (int i = 0; i < lay.size(); i++) {
                String[] e = lay.get(i);
                String was = e[0];
                e[0] = mod.role(e[0], def); // P2-8 换防
                if (!was.equals(e[0])) converted.add(i);
                roles.append(roles.length() == 0 ? "" : ",").append(e[0]);
            }
            svc.log().info("[P1 run] " + s.runId + " " + r.id + " rule " + mod.id + (swap != null ? " (group of " + swap.id + ")" : "") + ": " + roles
                    + (mod.tweaksConverted() && !converted.isEmpty() ? " (converted x" + converted.size() + " hp*" + mod.convHp + " atk*" + mod.convAtk
                    + " interval*" + mod.convInterval + " speed*" + mod.convSpeed + ")" : ""));
        }
        int ok = 0;
        List<Tracked> spawned = new ArrayList<Tracked>();
        for (int i = 0; i < lay.size(); i++) {
            String[] e = lay.get(i);
            EmberRunMaps.Role role = def.role(e[0], ch);
            EmberRunMaps.Pt pt = r.points.get(Integer.parseInt(e[1]));
            Tracked t = spawn(role, e[0], r.id, pt, r.trigger, converted.contains(i) ? mod : null);
            if (t != null) { ok++; spawned.add(t); }
        }
        svc.onRoomStarted(s, r, b, ok, lay.size(), EmberRunRules.compositionLabel(lay));
        if (r.id.equals(s.affixRoom) && !s.affix.isEmpty() && !s.affixDone && !spawned.isEmpty()) promote(spawned);
        if (r.id.equals(s.eventRoom) && !s.eventDone) {
            eventStart = System.currentTimeMillis();
            eventWarned = false;
            svc.tellRun(s, "§b限时清房 §7· " + svc.maps().variety.eventSecs + " 秒内清完这个房间 → 结算时 §f余烬核心碎片 +" + svc.maps().variety.eventCore + " §7（可选）");
        }
        if (ok == 0) {
            anomalies.add(r.id + ": no mob could be spawned (MythicMobs ids " + def.roles.keySet() + ")");
            svc.onBroken(s, "房间 " + r.label + " 无法生成怪物");
        }
    }

    private void roomCleared(EmberRunMaps.Room r) {
        activeRoom = null;
        if (r == null) return;
        if (r.id.equals(s.eventRoom) && eventStart > 0) { // D138 timed room event
            double secs = (System.currentTimeMillis() - eventStart) / 1000.0;
            eventStart = 0;
            svc.onEventResult(s, secs <= svc.maps().variety.eventSecs, secs);
        }
        if (!s.cleared.contains(r.id)) s.cleared.add(r.id);
        next = Math.max(next, def.roomIndex(r.id) + 1);
        if (r.door != null) {
            setBox(r.door, Material.AIR, Material.IRON_FENCE);
            w.playSound(center(r.door), Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 0.8f);
        }
        for (EmberRunMaps.Link k : def.links) if (k.after.equals(r.id)) svc.tellRun(s, "§7通道已开启：" + k.label);
        if (r.id.equals(def.eventAfter)) spawnExtra();
        boolean last = next >= def.rooms.size();
        svc.onRoomCleared(s, r, last);
        if (last) {
            bossAt = System.currentTimeMillis() + 1500L;
            svc.tellRun(s, bossDef().waitInArea ? "§c" + bossDef().name + " §7在前方首领厅等候，走进大厅即现身"
                    : "§c" + bossDef().name + " §7即将在大厅中央现身（1.5 秒）");
        }
    }

    // ------------------------------------------------------------------ spawning

    private Tracked spawn(EmberRunMaps.Role role, String roleId, String roomId, EmberRunMaps.Pt pt, EmberRunMaps.Box leash) {
        return spawn(role, roleId, roomId, pt, leash, null);
    }

    /** {@code conv} != null: this mob was converted by the week's rule — apply its D158 multipliers */
    private Tracked spawn(EmberRunMaps.Role role, String roleId, String roomId, EmberRunMaps.Pt pt, EmberRunMaps.Box leash, EmberRunMaps.Modifier conv) {
        if (role == null) return null;
        Location loc = new Location(w, pt.x + 0.5, pt.y, pt.z + 0.5);
        Entity e = EmberRunBridges.spawnMythic(role.mm, loc, svc.log());
        if (!(e instanceof LivingEntity)) return null;
        LivingEntity le = (LivingEntity) e;
        boolean cv = conv != null && conv.tweaksConverted();
        scaleHealth(le, role.hp * (cv ? conv.convHp : 1.0));
        if (cv && conv.convSpeed != 1.0) {
            AttributeInstance sp = le.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (sp != null) sp.setBaseValue(sp.getBaseValue() * conv.convSpeed);
        }
        le.setRemoveWhenFarAway(false);
        Tracked t = new Tracked(le, roleId, roomId, pt, leash, role.atk * (cv ? conv.convAtk : 1.0), role.interval * (cv ? conv.convInterval : 1.0), role.range, role);
        if (t.caster()) t.nextCast = System.currentTimeMillis() + 2500L;
        mobs.put(le.getUniqueId(), t);
        svc.index(le.getUniqueId(), this);
        return t;
    }

    // ------------------------------------------------------------------ D138 repeat-run variety

    private long eventStart;
    private boolean eventWarned;

    /** The room's toughest mob (heavy, else melee, else the first) becomes the affixed elite. */
    static Tracked affixPick(List<Tracked> ts) {
        for (Tracked t : ts) if ("heavy".equals(t.role)) return t;
        for (Tracked t : ts) if ("melee".equals(t.role)) return t;
        return ts.get(0);
    }

    private void promote(List<Tracked> spawned) {
        EmberRunMaps.Variety v = svc.maps().variety;
        Tracked t = affixPick(spawned);
        t.affix = s.affix;
        if ("shield".equals(t.affix)) {
            AttributeInstance a = t.le.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (a != null) {
                a.setBaseValue(a.getBaseValue() * v.shieldHp);
                t.le.setHealth(t.le.getMaxHealth());
            }
        }
        t.affixNext = System.currentTimeMillis() + 1500L + (long) (v.blazeEvery * 1000);
        String tag = EmberRunMaps.Variety.label(t.affix);
        String old = t.le.getCustomName();
        t.le.setCustomName("§6[" + tag + "] §r" + (old == null ? t.le.getName() : old));
        t.le.setCustomNameVisible(true);
        t.le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 600, 0, false, false), true);
        String how = "blazing".equals(t.affix) ? "脚下每 " + fmt(v.blazeEvery) + " 秒落一圈火（半径 " + fmt(v.blazeRadius) + "，" + fmt(v.blazeWarn) + " 秒预警，看到火圈就退开）"
                : "split".equals(t.affix) ? "死后分裂成 " + v.splitCount + " 个小怪（门要等它们也倒下）"
                : "生命 ×" + fmt(v.shieldHp);
        svc.tellRun(s, "§6词缀精英「" + tag + "」§7出现在这个房间（发光的那只）：" + how + " · 击败 → 结算时 §f余烬碎片 +" + v.affixShard);
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s %s affix %s on %s hp=%.0f", s.runId, t.roomId, t.affix, t.role, t.le.getMaxHealth()));
    }

    private void affixTick(Tracked t, long now) {
        Particle fx = "blazing".equals(t.affix) ? Particle.FLAME : "split".equals(t.affix) ? Particle.SPELL_WITCH : Particle.END_ROD;
        w.spawnParticle(fx, t.le.getLocation().add(0, 1.0, 0), 3, 0.3, 0.5, 0.3, 0.01);
        if (!"blazing".equals(t.affix)) return;
        EmberRunMaps.Variety v = svc.maps().variety;
        if (t.affixAt > 0) {
            warnCircle(t.affixOrigin.clone().add(0, 0.15, 0), v.blazeRadius, Particle.FLAME);
            if (now >= t.affixAt) {
                execute(blazeSkill(t, v), t.affixOrigin, new Vector(1, 0, 0), t.le);
                t.affixAt = 0;
                t.affixNext = now + (long) (v.blazeEvery * 1000);
            }
            return;
        }
        if (now < t.affixNext || nearest(t.le.getLocation(), 6) == null) return;
        t.affixOrigin = t.le.getLocation().clone();
        t.affixAt = now + (long) (v.blazeWarn * 1000);
    }

    static EmberRunMaps.Skill blazeSkill(Tracked t, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "炽热火圈");
        m.put("radius", v.blazeRadius);
        m.put("warn", v.blazeWarn);
        m.put("dmg", t.atk * v.blazeDmg);
        return new EmberRunMaps.Skill(m);
    }

    private void splitAdds(Tracked t) {
        EmberRunMaps.Variety v = svc.maps().variety;
        EmberRunMaps.Role role = def.role("melee", ch);
        if (role == null || v.splitCount <= 0) return;
        Location l = t.le.getLocation();
        EmberRunMaps.Pt pt = new EmberRunMaps.Pt(Math.floor(l.getX()), Math.floor(l.getY()), Math.floor(l.getZ()));
        int n = 0;
        for (int i = 0; i < v.splitCount; i++) {
            Tracked a = spawn(role, "melee", t.roomId, pt, t.leash);
            if (a == null) continue;
            a.splitAdd = true;
            AttributeInstance at = a.le.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (at != null) { at.setBaseValue(Math.max(1.0, at.getBaseValue() * v.splitHp)); a.le.setHealth(a.le.getMaxHealth()); }
            String old = a.le.getCustomName();
            a.le.setCustomName("§7[分身] §r" + (old == null ? a.le.getName() : old));
            n++;
        }
        svc.tellRun(s, "§6「分裂」§7精英裂成了 " + n + " 个分身！");
    }

    /** A18: base HP × (1 + 0.65 (n − 1)), locked when the run was committed. */
    private void scaleHealth(LivingEntity le, double base) {
        double hp = Math.max(1.0, base * s.hpFactor);
        AttributeInstance a = le.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (a != null) a.setBaseValue(hp);
        le.setHealth(Math.min(hp, le.getMaxHealth()));
        if (le.getMaxHealth() < hp - 0.5 && !hpCapWarned) { // spigot.yml settings.attribute.maxHealth.max clamps silently
            hpCapWarned = true;
            svc.log().warning(String.format(Locale.ROOT, "[P1 run] %s max HP clamped: wanted %.0f, got %.0f — raise spigot.yml attribute.maxHealth.max",
                    s.runId, hp, le.getMaxHealth()));
        }
    }
    private boolean hpCapWarned;

    private void spawnBoss(long now) {
        EmberRunMaps.Boss b = bossDef();
        EmberRunMaps.Role pseudo = null;
        Location loc = new Location(w, b.at.x + 0.5, b.at.y, b.at.z + 0.5);
        Entity e = EmberRunBridges.spawnMythic(b.mm, loc, svc.log());
        if (!(e instanceof LivingEntity)) {
            bossAt = 0;
            svc.onBroken(s, "首领 " + b.mm + " 无法生成");
            return;
        }
        LivingEntity le = (LivingEntity) e;
        scaleHealth(le, ch != null ? ch.bossHp : b.hp);
        le.setRemoveWhenFarAway(false);
        boss = new Tracked(le, "boss", "boss", b.at, b.area, ch != null ? ch.bossAtk : b.atk, b.interval, 3.0, pseudo);
        mobs.put(le.getUniqueId(), boss);
        svc.index(le.getUniqueId(), this);
        nextAt = new long[b.skills.size()];
        for (int i = 0; i < nextAt.length; i++) nextAt[i] = now + (long) (b.skills.get(i).every * 1000);
        bossSpawnedAt = now;
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s boss spawned hp=%.0f party=%d", s.runId, le.getMaxHealth(), s.partySize));
        svc.onBossSpawned(s, b);
    }

    /** epoch ms of the boss spawn (0 = not yet) — kill-time measurements in the log */
    long bossSpawnedAt;

    private void spawnExtra() {
        if (extraSpawned || def.eventAnchor == null) return;
        extraSpawned = true;
        EmberRunMaps.Pt a = def.eventAnchor;
        switch (s.extra) {
            case TREASURE:
            case ELITE: {
                String role = s.extra == EmberRunRules.Extra.TREASURE ? "treasure" : "elite";
                Tracked t = spawn(def.role(role, ch), role, "event", a, def.eventArea);
                if (t != null) extraMob = t.le.getUniqueId();
                break;
            }
            case CHEST: {
                Block bl = w.getBlockAt((int) Math.floor(a.x), (int) Math.floor(a.y), (int) Math.floor(a.z));
                if (bl.getType() == Material.AIR) {
                    bl.setType(Material.ENDER_CHEST);
                    chest = bl.getLocation();
                }
                break;
            }
            default:
                return;
        }
        holo("event", "§e额外事件：" + (s.extra == EmberRunRules.Extra.TREASURE ? "宝藏怪" : s.extra == EmberRunRules.Extra.ELITE ? "奖励精英" : "额外宝箱"));
        svc.onExtraSpawned(s);
    }

    // ------------------------------------------------------------------ deaths

    /** @return true when this was the boss */
    boolean onDeath(Tracked t) {
        mobs.remove(t.le.getUniqueId());
        if (t.boss() && def.rush && chainIdx < def.chain.size() - 1) { // D144: next boss after a short break
            int done = chainIdx;
            EmberRunMaps.Boss was = bossDef();
            chainIdx++;
            boss = null;
            pending = null;
            follow = null;
            recoverUntil = 0;
            addsDone = false;
            addsAt = 0;
            phaseTold = false;
            belowCache = null;
            lastRevAt = 0;
            lastRevDone = false;
            cleanupMobs();
            long secs = bossSpawnedAt > 0 ? (System.currentTimeMillis() - bossSpawnedAt) / 1000L : 0;
            bossAt = System.currentTimeMillis() + (long) (def.rushBreak * 1000);
            svc.onRushStage(this, done, was, bossDef(), secs);
            return false;
        }
        if (t.boss()) {
            bossDead = true;
            cleanupMobs(); // §13: adds die with the boss; an unfinished extra mob is gone too
            unholo("event");
            holo("exit", "§a" + def.name + " 已通关", "§7结算已发放 · /dp leave 离开");
            return true;
        }
        if ("event".equals(t.roomId) && t.le.getUniqueId().equals(extraMob)) { unholo("event"); svc.onExtraDone(s); }
        if (t.affix != null) { // D138
            if ("split".equals(t.affix)) splitAdds(t);
            svc.onAffixDone(s, t.affix);
        }
        return false;
    }

    boolean clickChest(Block b) {
        if (chest == null || !b.getLocation().equals(chest)) return false;
        b.setType(Material.AIR);
        chest = null;
        unholo("event");
        svc.onExtraDone(s);
        return true;
    }

    void finish() {
        finished = true;
        cleanupMobs();
        for (String k : new ArrayList<String>(holos.keySet())) if (!"exit".equals(k)) unholo(k); // exit stays until the instance closes
        if (chest != null && chest.getBlock().getType() == Material.ENDER_CHEST) chest.getBlock().setType(Material.AIR);
        chest = null;
    }

    private void cleanupMobs() {
        for (Tracked t : new ArrayList<Tracked>(mobs.values())) {
            if (!t.le.isDead()) t.le.remove();
            svc.unindex(t.le.getUniqueId());
        }
        mobs.clear();
    }

    private void anomaly(Tracked t, String why) {
        String line = String.format(Locale.ROOT, "%s %s@%s: %s", t.roomId, t.role, t.home, why);
        anomalies.add(line);
        svc.unindex(t.le.getUniqueId());
        svc.log().warning("[P1 run] " + s.runId + " anomaly " + line);
    }

    // ------------------------------------------------------------------ boss skills

    private void bossTick(long now) {
        LivingEntity le = boss.le;
        if (le.isDead()) return;
        double ratio = le.getHealth() / Math.max(1.0, le.getMaxHealth());
        EmberRunMaps.Boss b = bossDef();
        // §13 adds: once at 50 % after a 1 s warning
        if (b.adds != null && !addsDone && ratio <= b.adds.atHp) {
            addsDone = true;
            addsAt = now + (long) (b.adds.warn * 1000);
            svc.tellRun(s, "§c" + b.name + " §7高举誓印——两侧将出现援兵！");
            svc.onBossPhase(s, "首领半血转阶段"); // D106 raid revive point
        }
        if (addsAt > 0) {
            if (now >= addsAt) {
                addsAt = 0;
                for (EmberRunMaps.Pt p : b.adds.points) spawn(def.role(b.adds.role, ch), "add", "boss", safe(p, b.at), b.area);
            } else {
                for (EmberRunMaps.Pt p : b.adds.points) warnCircle(new Location(w, p.x + 0.5, p.y + 0.1, p.z + 0.5), 1.0, Particle.SPELL_WITCH);
            }
        }
        if (pending != null) {
            drawShape(pending, lockOrigin, lockDir);
            if (now >= pendingAt) {
                execute(pending, lockOrigin, lockDir, le);
                EmberRunMaps.Skill done = pending;
                pending = null;
                recoverUntil = now + (long) ((Double.isNaN(done.recover) ? b.recover : done.recover) * 1000);
                if (done.follow != null && ratio < done.follow.below) {
                    follow = done.follow;
                    followStart = now + (long) (done.follow.delay * 1000);
                }
            }
            return;
        }
        if (follow != null && now >= followStart) {
            if (!Double.isNaN(follow.shift) && lockOrigin != null && lockDir != null) startShifted(follow, now, le);
            else startWarn(follow, now, le);
            follow = null;
            return;
        }
        if (follow != null || now < recoverUntil) return;
        int due = dueSkill(nextAt, now, belowOf(b.skills), ratio); // P2-6: top-level skills may be phase-gated
        if (!phaseTold && due >= 0 && b.skills.get(due).below <= 1.0) {
            phaseTold = true;
            svc.tellRun(s, "§c" + b.name + " §7进入第二阶段：新招式「" + b.skills.get(due).name + "」！");
            svc.log().info(String.format(Locale.ROOT, "[P1 run] %s boss phase 2 at %.0f%% (%s)", s.runId, ratio * 100, b.skills.get(due).name));
            svc.onBossPhase(s, "首领进入第二阶段"); // D106 raid revive point
        }
        double lrh = svc.maps().raidLastReviveHp; // D118: armed at <= lrh with someone down, once per raid
        if (lrh > 0 && !lastRevDone && svc.isRaid(s) && lastPhase() && ratio <= lrh) {
            if (lastRevAt == 0L && !s.died.isEmpty()) {
                lastRevAt = now + (long) (svc.maps().raidReviveDelay * 1000);
                svc.tellRun(s, "§e首领只剩 " + Math.round(lrh * 100) + "% 生命§7：倒下的队友约 " + Math.round(svc.maps().raidReviveDelay) + " 秒后再复活一次（本局仅此一次）");
            }
            if (lastRevAt > 0 && now >= lastRevAt) {
                lastRevDone = true;
                svc.onBossPhase(s, "最后阶段额外复活");
            }
        }
        if (due >= 0) {
            EmberRunMaps.Skill sk = b.skills.get(due);
            startWarn(sk, now, le);
            nextAt[due] = nextDue(nextAt[due], (long) (sk.every * 1000), now);
        }
    }

    /**
     * §10.I: the first-listed skill wins when several are due in the same tick; the other waits until the current
     * action and its recovery are over (it stays due, it is not skipped). @return index or −1
     */
    static int dueSkill(long[] nextAt, long now) {
        for (int i = 0; i < nextAt.length; i++) if (now >= nextAt[i]) return i;
        return -1;
    }

    /** P2-6: like dueSkill, but a skill with below[i] fires only while the boss HP ratio is under it (a gated skill whose
     *  slot passes waits, so it opens the second phase as soon as the threshold is crossed). */
    static int dueSkill(long[] nextAt, long now, double[] below, double ratio) {
        for (int i = 0; i < nextAt.length; i++) if (now >= nextAt[i] && (below == null || ratio < below[i])) return i;
        return -1;
    }

    private double[] belowCache;
    private boolean phaseTold;

    private double[] belowOf(List<EmberRunMaps.Skill> skills) {
        if (belowCache == null || belowCache.length != skills.size()) {
            belowCache = new double[skills.size()];
            for (int i = 0; i < belowCache.length; i++) belowCache[i] = skills.get(i).below;
        }
        return belowCache;
    }

    /** Keeps the schedule anchored at the fight start: next slot strictly after {@code now}. */
    static long nextDue(long at, long every, long now) {
        if (every <= 0) return Long.MAX_VALUE / 4;
        long n = at;
        while (n <= now) n += every;
        return n;
    }

    /**
     * True while a telegraphed skill is winding up or a combo is between its parts: the boss's normal hit is
     * suppressed (施法时停止普攻; Q06 两段刀气「组合期间停止普攻」).
     */
    boolean bossCasting() { return pending != null || follow != null; }

    /** Right-hand side of a horizontal facing (Minecraft: +x east, +z south; facing south → right is west). */
    static Vector rightOf(Vector dir) { return new Vector(-dir.getZ(), 0, dir.getX()); }

    /** Q06 second band: same locked direction, origin slid {@code shift} blocks to the boss's right; no re-aim. */
    private void startShifted(EmberRunMaps.Skill sk, long now, LivingEntity le) {
        lockOrigin = lockOrigin.clone().add(rightOf(lockDir).multiply(sk.shift));
        pending = skillFor(sk);
        pendingAt = now + (long) (sk.warn * 1000);
        casts++;
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (sk.warn * 20) + 6, 10, false, false), true);
        w.playSound(le.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 0.7f);
        svc.tellRun(s, "§c" + bossDef().name + " §e第二段「" + sk.name + "」§7— 右移 " + fmt(sk.shift) + " 格 · 左侧安全（" + sk.warn + " 秒）");
    }

    /** challenge overrides hook (§18.1); normal runs use the table value. */
    EmberRunMaps.Skill skillFor(EmberRunMaps.Skill sk) {
        return ch == null ? sk : sk.withDmg(ch.skillDmg(sk));
    }

    private void startWarn(EmberRunMaps.Skill sk, long now, LivingEntity le) {
        Location o = le.getLocation();
        Player target = "player".equals(sk.target) ? pickTarget(o, 24) : nearest(o, 24);
        Vector dir = target != null ? target.getLocation().toVector().subtract(o.toVector()) : o.getDirection();
        dir.setY(0);
        if (dir.lengthSquared() < 1e-6) dir = new Vector(0, 0, 1);
        dir.normalize();
        // Q04 冲击圈: the circle is locked on the chosen player's ground position now and lands after the warning
        double run = 0;
        if ("charge".equals(sk.type)) { // §17: strip = the real path; blocked → charge back toward the hall centre
            run = clearRun(o, dir, sk.length);
            if (run < CHARGE_MIN) {
                Vector home = new Vector(bossDef().at.x + 0.5 - o.getX(), 0, bossDef().at.z + 0.5 - o.getZ());
                if (home.lengthSquared() > 1.0) {
                    home.normalize();
                    double r2 = clearRun(o, home, sk.length);
                    if (r2 > run) { run = r2; dir = home; }
                }
            }
            if (run < CHARGE_MIN) { // B2.165: no 0-block "charge" telegraphs; the cooldown is spent, the slam stays on schedule
                svc.log().fine("[P1 run] " + s.runId + " " + sk.name + " skipped (no room: " + fmt(run) + ")");
                return;
            }
        }
        lockOrigin = "player".equals(sk.target) && target != null ? target.getLocation().clone() : o.clone();
        lockDir = dir;
        pending = skillFor(sk);
        if ("charge".equals(sk.type)) pending = pending.withLength(run);
        pendingAt = now + (long) (sk.warn * 1000);
        casts++;
        Location face = o.clone();
        face.setDirection(dir);
        le.teleport(face);
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (sk.warn * 20) + 6, 10, false, false), true);
        w.playSound(o, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 0.6f);
        // D147 (review 10-04 #6): the locked circle also says its radius, like the other shapes
        String who = "player".equals(sk.target) && target != null ? "锁定 " + target.getName() + " 脚下"
                + ("circle".equals(sk.type) && sk.radius > 0 ? " · 半径 " + fmt(sk.radius) + " 格" : "") : shapeHint(pending);
        if (sk.share) who += " §6· 全队靠拢进圈分摊（人越多每人越少，一个人扛会很痛）";
        svc.tellRun(s, "§c" + bossDef().name + " §e蓄力「" + sk.name + "」§7— " + who + "（" + sk.warn + " 秒）");
    }

    /** One committed participant in range, chosen from the run seed + cast number (reproducible, not always the tank). */
    private Player pickTarget(Location l, double max) {
        List<Player> c = new ArrayList<Player>();
        for (Player p : participantsHere()) {
            if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
            if (p.getLocation().distanceSquared(l) <= max * max) c.add(p);
        }
        if (c.isEmpty()) return null;
        c.sort((a, b) -> a.getUniqueId().compareTo(b.getUniqueId()));
        long h = EmberRunRules.subSeed(s.seed, "skill", String.valueOf(casts));
        return c.get((int) Math.floorMod(h, (long) c.size()));
    }

    private void casterTick(Tracked t, long now) {
        if (t.castAt > 0) {
            EmberRunMaps.Skill line = casterSkill(t);
            drawShape(line, t.castOrigin, t.castDir);
            if (now >= t.castAt) {
                execute(line, t.castOrigin, t.castDir, t.le);
                t.castAt = 0;
                t.nextCast = now + (long) (t.interval * 1000);
            }
            return;
        }
        if (now < t.nextCast) return;
        Player p = nearest(t.le.getLocation(), 10);
        if (p == null || !t.le.hasLineOfSight(p)) return;
        Vector d = p.getLocation().toVector().subtract(t.le.getLocation().toVector()).setY(0);
        if (d.lengthSquared() < 1e-6) return;
        t.castDir = d.normalize();
        t.castOrigin = t.le.getLocation().clone();
        t.castAt = now + (long) (t.def.warn * 1000);
        t.le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (t.def.warn * 20) + 4, 10, false, false), true);
    }

    private EmberRunMaps.Skill casterSkill(Tracked t) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "誓印直线");
        m.put("length", t.def.lineLength);
        m.put("width", t.def.lineWidth);
        m.put("warn", t.def.warn);
        m.put("dmg", t.atk);
        return new EmberRunMaps.Skill(m);
    }

    private void execute(EmberRunMaps.Skill sk, Location o, Vector dir, LivingEntity src) {
        int hit = 0;
        List<Player> inside = new ArrayList<Player>();
        for (Player p : w.getPlayers()) {
            if (p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
            if (inShape(sk, o, dir, p.getLocation())) inside.add(p);
        }
        double each = sk.share ? shareDamage(sk.dmg, inside.size()) : sk.dmg; // R03 (D137) 烬核分摊
        EmberGrowthService g = EmberGrowthService.get();
        double[] share = null;
        if (sk.share && !inside.isEmpty()) { // D141 扛核: weights per player (1 each by default), × own share_taken
            double[] w = new double[inside.size()];
            for (int i = 0; i < w.length; i++) w[i] = g == null ? 1.0 : g.shareWeight(inside.get(i));
            share = shareSplit(sk.dmg, w);
            for (int i = 0; i < share.length; i++) if (g != null) share[i] *= g.shareTaken(inside.get(i));
        }
        if (sk.share) {
            svc.log().info(String.format(Locale.ROOT, "[P1 run] %s share %s n=%d each=%.1f", s.runId, sk.name, inside.size(), each));
            if (!inside.isEmpty()) svc.tellRun(s, "§6「" + sk.name + "」§7落下：" + inside.size() + " 人分摊，每人 " + Math.round(each * s.dmgFactor)
                    + (inside.size() == 1 ? " §c（只有一个人扛！）" : ""));
            // D144 烬核同心: everyone still standing was inside (2+) — counts once per run toward the optional weekly goal
            if (inside.size() >= 2 && inside.containsAll(participantsHere())) svc.onCleanShare(s, inside);
        }
        boolean bossSkill = src != null && boss != null && src == boss.le;
        String kind = sk.share ? "share" : bossSkill ? "tele" : "mob";
        for (int i = 0; i < inside.size(); i++) {
            Player p = inside.get(i);
            svc.skillHit(s, p, src, share != null ? share[i] : each, kind);
            if (bossSkill && !sk.share && g != null && !p.isDead()) g.onTeleHit(p); // D141 反震
            if (sk.kb > 0 && !p.isDead()) push(p, src == null ? o : src.getLocation(), sk.kb);
            hit++;
        }
        if (bossSkill && !sk.share && g != null) { // D141: a boss telegraph that missed a player near it = a dodge
            double reach = reach(sk) + 3.0;
            for (Player p : participantsHere()) {
                if (inside.contains(p) || p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
                if (p.getLocation().distanceSquared(o) <= reach * reach) g.onDodge(p);
            }
        }
        Location fx = o.clone().add(dir.clone().multiply(sk.type.equals("circle") ? sk.ahead
                : sk.strip() ? (sk.stripFrom() + sk.stripTo()) / 2.0 : Math.min(2.0, sk.range)));
        w.spawnParticle(Particle.EXPLOSION_NORMAL, fx.add(0, 0.3, 0), 8, 1.0, 0.2, 1.0, 0.01);
        w.playSound(o, Sound.ENTITY_GENERIC_EXPLODE, 0.6f, 1.4f);
        if ("charge".equals(sk.type) && src != null && boss != null && src == boss.le && sk.length > 0.25) {
            // §17 冲撞: the boss ends at the end of the drawn strip (never past a wall / the area edge: clearRun)
            Location end = o.clone().add(dir.clone().multiply(sk.length));
            end.setYaw(o.getYaw());
            end.setDirection(dir);
            src.teleport(end);
        }
        if (hit == 0 && src == (boss == null ? null : boss.le)) svc.log().fine("[P1 run] " + sk.name + " missed");
    }

    /** R03 (D137): the whole hit split equally between everyone standing in the circle (alone = all of it). */
    static double shareDamage(double total, int inside) { return inside <= 0 ? 0 : total / inside; }

    /** D141 扛核: the hit split by weight (a weight-2 player carries two portions; all weights 1 = {@link #shareDamage}). */
    static double[] shareSplit(double total, double[] w) {
        double sum = 0;
        for (double x : w) sum += x;
        double[] out = new double[w.length];
        for (int i = 0; i < w.length; i++) out[i] = sum <= 0 ? 0 : total * w[i] / sum;
        return out;
    }

    /** D141: how far a shape reaches from its origin (circle: offset + radius; line / charge: far end; cone: range) */
    static double reach(EmberRunMaps.Skill sk) {
        if ("circle".equals(sk.type)) return Math.abs(sk.ahead) + sk.radius;
        if ("line".equals(sk.type) || "charge".equals(sk.type)) return Math.max(sk.stripTo(), sk.length);
        return sk.range;
    }

    /** Shapes never exceed the drawn warning (book: 实际范围不超过预警). */
    static boolean inShape(EmberRunMaps.Skill sk, Location o, Vector dir, Location p) {
        if (Math.abs(p.getY() - o.getY()) > 2.5) return false;
        double dx = p.getX() - o.getX(), dz = p.getZ() - o.getZ();
        switch (sk.type) {
            case "circle": {
                double cx = dir.getX() * sk.ahead, cz = dir.getZ() * sk.ahead;
                double ex = dx - cx, ez = dz - cz;
                return ex * ex + ez * ez <= sk.radius * sk.radius;
            }
            case "line":
            case "charge": {
                double along = dx * dir.getX() + dz * dir.getZ();
                double perp = Math.abs(-dx * dir.getZ() + dz * dir.getX());
                return along >= sk.stripFrom() && along <= sk.stripTo() && perp <= sk.width / 2.0;
            }
            default: { // cone
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > sk.range) return false;
                if (dist < 0.6) return true;
                double cos = (dx * dir.getX() + dz * dir.getZ()) / dist;
                return cos >= Math.cos(Math.toRadians(sk.angle / 2.0));
            }
        }
    }

    private static String shapeHint(EmberRunMaps.Skill sk) {
        switch (sk.type) {
            case "circle": return (sk.ahead <= 0 ? "以首领为中心半径 " : "前方 " + (int) sk.ahead + " 格处半径 ") + fmt(sk.radius) + " 圆形"
                    + (sk.kb > 0 ? " · 击退 ≤" + fmt(sk.kb) + " 格" : "");
            case "line": return Double.isNaN(sk.start) ? "正前直线 长 " + fmt(sk.length) + " 宽 " + fmt(sk.width)
                    : "正前 " + fmt(sk.stripFrom()) + "～" + fmt(sk.stripTo()) + " 格条带 宽 " + fmt(sk.width);
            case "charge": return "直线冲撞 " + fmt(sk.length) + " 格 · 条带宽 " + fmt(sk.width);
            default: return "正前 " + (int) sk.angle + "° 扇形 " + fmt(sk.range) + " 格" + (sk.kb > 0 ? " · 击退 ≤" + fmt(sk.kb) + " 格" : "");
        }
    }

    private static String fmt(double v) { return v == Math.rint(v) ? String.valueOf((int) v) : String.valueOf(v); }

    private void drawShape(EmberRunMaps.Skill sk, Location o, Vector dir) {
        double y = o.getY() + 0.15;
        switch (sk.type) {
            case "circle": {
                double cx = o.getX() + dir.getX() * sk.ahead, cz = o.getZ() + dir.getZ() * sk.ahead;
                warnCircle(new Location(w, cx, y, cz), sk.radius, sk.share ? Particle.VILLAGER_HAPPY : Particle.FLAME); // D137: green = stand in it together
                break;
            }
            case "line":
            case "charge": {
                Vector side = new Vector(-dir.getZ(), 0, dir.getX());
                for (double a = Math.max(0, sk.stripFrom()); a <= sk.stripTo() + 1e-9; a += 0.5) {
                    for (double sgn : new double[]{-1, 1}) {
                        Location l = o.clone().add(dir.clone().multiply(a)).add(side.clone().multiply(sgn * sk.width / 2.0));
                        l.setY(y);
                        w.spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0);
                    }
                }
                break;
            }
            default: {
                double half = Math.toRadians(sk.angle / 2.0);
                double base = Math.atan2(dir.getZ(), dir.getX());
                for (double a = -half; a <= half + 1e-9; a += half / 6) {
                    double ang = base + a;
                    Location l = new Location(w, o.getX() + Math.cos(ang) * sk.range, y, o.getZ() + Math.sin(ang) * sk.range);
                    w.spawnParticle(Particle.FLAME, l, 1, 0, 0, 0, 0);
                }
                for (double r = 0.5; r <= sk.range; r += 0.5) {
                    for (double sgn : new double[]{-1, 1}) {
                        double ang = base + sgn * half;
                        w.spawnParticle(Particle.FLAME, new Location(w, o.getX() + Math.cos(ang) * r, y, o.getZ() + Math.sin(ang) * r), 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }

    private void warnCircle(Location c, double r, Particle p) {
        int n = Math.max(12, (int) (r * 10));
        for (int i = 0; i < n; i++) {
            double a = 2 * Math.PI * i / n;
            w.spawnParticle(p, c.getX() + Math.cos(a) * r, c.getY(), c.getZ() + Math.sin(a) * r, 1, 0, 0, 0, 0);
        }
    }

    private Player nearest(Location l, double max) {
        Player best = null;
        double bd = max * max;
        for (Player p : w.getPlayers()) {
            if (p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
            double d = p.getLocation().distanceSquared(l);
            if (d <= bd) { bd = d; best = p; }
        }
        return best;
    }

    /**
     * §10.I knockback: at most {@code max} blocks horizontally away from {@code from}, walked in 0.25 steps; stops
     * before a wall, a missing floor (no pushing off a ledge / into the stair shaft) or the boss area edge.
     */
    private void push(Player p, Location from, double max) {
        Location at = p.getLocation();
        Vector d = at.toVector().subtract(from.toVector()).setY(0);
        if (d.lengthSquared() < 1e-6) d = lockDir == null ? new Vector(0, 0, 1) : lockDir.clone();
        d.normalize();
        Location best = null;
        for (double k = 0.25; k <= max + 1e-9; k += 0.25) {
            Location c = at.clone().add(d.clone().multiply(k));
            if (!standable(c)) break;
            if (bossDef().area != null && !bossDef().area.contains(c.getX(), c.getY(), c.getZ())) break;
            best = c;
        }
        if (best != null) p.teleport(best);
    }

    /**
     * §17 冲撞 path: how far (≤ max, 0.25 steps) the boss can move along {@code dir} from {@code o} with a floor under
     * it, two free blocks and still inside the boss area — the drawn strip and the hit box use this length.
     */
    private double clearRun(final Location o, Vector dir, double max) {
        return clearRunGrid((x, z) -> {
            Location c = new Location(w, x, o.getY(), z);
            return standable(c) && (bossDef().area == null || bossDef().area.contains(x, o.getY(), z));
        }, o.getX(), o.getZ(), dir.getX(), dir.getZ(), max, BOSS_HALF_WIDTH);
    }

    /** half of the boss's footprint (zombie 0.6) plus a margin: the charge end must not put its box into a wall */
    static final double BOSS_HALF_WIDTH = 0.4;

    interface GroundTest { boolean ok(double x, double z); }

    /**
     * B2.165: how far the boss can charge along (dx, dz): every 0.25 step the centre AND the box edges (± half
     * sideways, + half ahead) must be standable ground inside the boss area. The centre-only check let the Q07 boss
     * end a charge with half its box in the hall wall (z=40); a mob inside a block climbs it when it jumps and walked
     * on the roof (y70) for the rest of the fight.
     */
    static double clearRunGrid(GroundTest g, double ox, double oz, double dx, double dz, double max, double half) {
        double best = 0, px = -dz, pz = dx;
        for (double k = 0.25; k <= max + 1e-9; k += 0.25) {
            double cx = ox + dx * k, cz = oz + dz * k;
            if (!g.ok(cx, cz) || !g.ok(cx + px * half, cz + pz * half) || !g.ok(cx - px * half, cz - pz * half)
                    || !g.ok(cx + dx * half, cz + dz * half)) break;
            best = k;
        }
        return best;
    }

    /** a charge shorter than this is not worth a telegraph (book: 冲撞 = up to 8 blocks) */
    static final double CHARGE_MIN = 2.0;

    static boolean standableIds(boolean belowSolid, boolean feetSolid, boolean headSolid) {
        return belowSolid && !feetSolid && !headSolid;
    }

    private boolean standable(Location c) {
        Block feet = c.getBlock();
        return standableIds(feet.getRelative(0, -1, 0).getType().isSolid(), feet.getType().isSolid(),
                feet.getRelative(0, 1, 0).getType().isSolid());
    }

    /** Clearance check for runtime points (adds): feet + head passable, else fall back to the boss point. */
    private EmberRunMaps.Pt safe(EmberRunMaps.Pt p, EmberRunMaps.Pt fallback) {
        Block feet = w.getBlockAt((int) Math.floor(p.x), (int) Math.floor(p.y), (int) Math.floor(p.z));
        Block head = feet.getRelative(0, 1, 0);
        if (!feet.getType().isSolid() && !head.getType().isSolid()) return p;
        anomalies.add("add point " + p + " blocked → boss point");
        return fallback;
    }

    private void setBox(EmberRunMaps.Box b, Material to, Material only) {
        for (int x = b.x0; x <= b.x1; x++)
            for (int y = b.y0; y <= b.y1; y++)
                for (int z = b.z0; z <= b.z1; z++) {
                    Block bl = w.getBlockAt(x, y, z);
                    if (bl.getType() == only || bl.getType() == Material.AIR || bl.getType() == Material.IRON_FENCE) bl.setType(to);
                }
    }

    private Location center(EmberRunMaps.Box b) {
        return new Location(w, (b.x0 + b.x1) / 2.0 + 0.5, (b.y0 + b.y1) / 2.0, (b.z0 + b.z1) / 2.0 + 0.5);
    }

    String describe() {
        StringBuilder sb = new StringBuilder();
        if (ch != null) sb.append(s.abyss > 0 ? "abyss T" + s.abyss + " " : "challenge ");
        sb.append("next=").append(next < def.rooms.size() ? def.rooms.get(next).id : "boss");
        sb.append(" active=").append(activeRoom == null ? "-" : activeRoom);
        sb.append(" alive=").append(mobs.size());
        sb.append(" boss=").append(boss == null ? (bossDead ? "dead" : "-") : String.format(Locale.ROOT, "%.0f/%.0f", boss.le.getHealth(), boss.le.getMaxHealth()));
        sb.append(" extra=").append(s.extra.id).append(s.extraDone ? "(done)" : extraSpawned ? "(spawned)" : "");
        if (!anomalies.isEmpty()) sb.append(" anomalies=").append(anomalies.size());
        return sb.toString();
    }
}

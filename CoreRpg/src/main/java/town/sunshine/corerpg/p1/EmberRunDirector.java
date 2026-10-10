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

import town.sunshine.corerpg.p1.encounter.AffixArcane;
import town.sunshine.corerpg.p1.encounter.AffixBlazing;
import town.sunshine.corerpg.p1.encounter.AffixCharge;
import town.sunshine.corerpg.p1.encounter.AffixCycle;
import town.sunshine.corerpg.p1.encounter.AffixFirechain;
import town.sunshine.corerpg.p1.encounter.AffixFrost;
import town.sunshine.corerpg.p1.encounter.AffixJailer;
import town.sunshine.corerpg.p1.encounter.AffixMolten;
import town.sunshine.corerpg.p1.encounter.AffixMortar;
import town.sunshine.corerpg.p1.encounter.AffixRegen;
import town.sunshine.corerpg.p1.encounter.AffixShield;
import town.sunshine.corerpg.p1.encounter.AffixSplit;
import town.sunshine.corerpg.p1.encounter.AffixVenom;
import town.sunshine.corerpg.p1.encounter.EmberAffixes;
import town.sunshine.corerpg.p1.encounter.EmberBossMove;
import town.sunshine.corerpg.p1.encounter.EmberShape;
import town.sunshine.corerpg.p1.encounter.CounterplayKind;
import town.sunshine.corerpg.p1.encounter.EmberCounterplay;
import town.sunshine.corerpg.p1.encounter.EmberEventTeach;
import town.sunshine.corerpg.PlayerData;
import town.sunshine.corerpg.p1.encounter.EmberRoomObjective;
import town.sunshine.corerpg.p1.encounter.RevivePoint;

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
        String affix;               // D138/D171/D181/D189/D196: blazing/split/shield/regen/charge/frost/mortar/molten/venom/jailer/arcane/firechain (null = plain)
        boolean splitAdd;           // D141: spawned by a split elite (counts as the affixed elite for 破缀 / 守缀 / 破甲)
        boolean varietyEscort;      // D171: escort rabbit — must not pay treasure coin; excluded from room-clear count
        long affixNext, affixAt;
        Location affixOrigin;
        Vector affixDir;            // D171 charge telegraph direction
        long regenWindowEnd;        // D171 regen interrupt window end (0 = idle)
        double regenHurt;           // D171 damage taken during the current interrupt window
        long frostNext;             // D171 next frost aura tick
        boolean venomDiag;          // D189 毒十字: false = "+" (axis), true = "x" (diagonal); flips after every cast
        // D196 旋光 (rotating beam): start angle (rad), turn sign (+1 / −1, flips every cast), last angle checked,
        // players already hit by this cast (once per cast)
        double arcaneStart, arcanePrev;
        int arcaneSign = 1;
        java.util.Set<UUID> arcaneHit = new java.util.HashSet<UUID>();
        // D196 火链 (tether to another room mob): partner, when the link turns live (after its warning), last burn per player
        Tracked chainTo;
        long chainLiveAt;
        Map<UUID, Long> chainBurnAt = new HashMap<UUID, Long>();
        EmberRunMaps.Skill twist;   // D182 Extra.ELITE light move (null = plain stump)
        EmberRunMaps.Skill twistAlt; // D185 Pack 2 second light move (null = Pack 1 only)
        boolean twistUseAlt;         // D185: true → fire twistAlt next
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
    /** D300: deferred door open (0 = none pending) */
    private long doorOpenAt;
    private EmberRunMaps.Room doorPending;
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
    private boolean[] gateOpened = new boolean[0]; // D194: a phase-gated skill re-anchors its cooldown at its first cast
    private long recoverUntil;           // §10.I 收招: no new skill before this
    private int casts;
    private EmberRunMaps.Skill follow;
    private long followStart;
    private boolean pendingCrash;        // D188 撞墙破绽: the pending charge was cut short by a real wall
    private boolean pendingArmed;        // D192 落空破绽: someone stood inside the pending telegraph when its warning began
    private long stunUntil;              // D188: boss stunned (no skills, no melee) until this
    private long lastCounterplayFlashAt; // D448: debounce clock for every-success ActionBar (0 = never)
    private double breakNeed;            // D193 破招: player damage needed to break the pending channel (0 = none armed)
    private double breakDone;            // D193: player damage dealt to the boss since that channel's warning began
    private long breakBarAt;             // D193: next action-bar progress line
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
        // D300: deferred door open (door_delay breath between clear and passage)
        if (doorOpenAt > 0 && doorPending != null && now >= doorOpenAt) {
            openDoorNow(doorPending);
            doorPending = null;
            doorOpenAt = 0;
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
            if (t.twist != null) twistTick(t, now);
        }
        moltenTick(now);
        if (!crystalBlocks.isEmpty()) pollCrystals();
        if (eventStart > 0 && activeRoom != null && activeRoom.equals(s.eventRoom) && !eventFailed && !s.eventDone) {
            tickPack3Events(now);
        }
        if (eventStart > 0 && !eventWarned && activeRoom != null && activeRoom.equals(s.eventRoom)) {
            String kind = s.eventKind == null || s.eventKind.isEmpty() ? "timed" : s.eventKind;
            int lim = svc.maps().variety.eventLimit(kind);
            if (lim > 10 && now >= eventStart + (lim - 10) * 1000L) {
                eventWarned = true;
                svc.tellRun(s, "§b" + EmberRunMaps.Variety.eventLabel(kind) + " §7还剩 §e10 秒");
            }
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
        for (Tracked t : mobs.values()) if (room.equals(t.roomId) && !t.le.isDead() && !t.varietyEscort) n++;
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
            eventFailed = false;
            String kind = s.eventKind == null || s.eventKind.isEmpty() ? "timed" : s.eventKind;
            EmberRunMaps.Variety v = svc.maps().variety;
            lastEventTickMs = eventStart;
            if ("crystal".equals(kind)) {
                placeCrystals(r, v.crystalCount);
                svc.tellRun(s, "§b砸余烬晶 §7· 砸掉本房 " + v.crystalCount + " 块发光晶再清完 → 结算时 §f余烬核心碎片 +" + v.eventCore + " §7（可选）");
                EmberSmashPath.maybeAfterPlace(s); // D564
            } else if ("escort".equals(kind)) {
                spawnEscort(r, v);
                svc.tellRun(s, "§b护宝兔 §7· 清房前别让发光小兔倒下 → 结算时 §f余烬核心碎片 +" + v.eventCore + " §7（可选）");
                EmberGuardPath.maybeAfterEscort(s); // D565
            } else if ("hold".equals(kind)) {
                placeHold(r, v);
                svc.tellRun(s, "§b占点 §7· 站进发光圈攒满进度再清完 → 结算时 §f余烬核心碎片 +" + v.eventCore + " §7（可选）");
                EmberHoldPath.maybeAfterHold(s); // D566
            } else if ("beacon".equals(kind)) {
                placeBeacon(r, v);
                svc.tellRun(s, "§b护灯 §7· 清房前别让发光灯柱被砸碎 → 结算时 §f余烬核心碎片 +" + v.eventCore + " §7（可选）");
                EmberBeaconPath.maybeAfterBeacon(s); // D567
            } else if ("relay".equals(kind)) {
                placeRelay(r, v);
                svc.tellRun(s, "§b传火 §7· 按 1→2→3 点亮标记再清完 → 结算时 §f余烬核心碎片 +" + v.eventCore + " §7（可选）");
                EmberRelayPath.maybeAfterRelay(s); // D568
            } else if ("breach".equals(kind)) {
                placeBreach(r, v);
                svc.tellRun(s, "§b裂隙 §7· 紫圈会慢慢收拢，在圈里杀怪能把它撑开；清房时圈没合上 → 结算时 §f余烬核心碎片 +" + v.eventCore + " §7（可选）");
                EmberBreachPath.maybeAfterBreach(s); // D569
            } else if ("chain".equals(kind)) {
                chainReset(ok, v);
                svc.tellRun(s, "§b连斩 §7· 连续击杀 " + chainNeedNow + " 只怪，每两次击杀间隔不超过 "
                        + String.format(Locale.ROOT, "%.0f", v.chainGap) + " 秒 → 结算时 §f余烬核心碎片 +" + v.eventCore + " §7（可选）");
                EmberStreakPath.maybeAfterChain(s); // D577
            } else if ("unscathed".equals(kind)) {
                unscathedReset(participantsHere().size(), v);
                svc.tellRun(s, "§b无伤 §7· 清完这个房间时全队被怪打中不超过 " + unscathedBudgetNow + " 次 → 结算时 §f余烬核心碎片 +" + v.eventCore + " §7（可选）");
                EmberCleanPath.maybeAfterClean(s); // D578
            } else {
                svc.tellRun(s, "§b限时清房 §7· " + v.eventLimit(kind) + " 秒内清完这个房间 → 结算时 §f余烬核心碎片 +" + v.eventCore + " §7（可选）");
                EmberRushPath.maybeAfterRush(s); // D579
            }
            // D296 W1c: per-run first event-room ActionBar short name (chat still fires every time)
            if (!s.eventHudShown) {
                s.eventHudShown = true;
                flashActionBar(EmberEventTeach.openHud(kind));
            }
            // D296 W1b: account first-of-kind teach flash (ledger p1_evteach_<kind>)
            String teach = EmberEventTeach.teachFlash(kind);
            if (!teach.isEmpty()) {
                for (Player p : participantsHere()) {
                    PlayerData pd = svc.dataOf(p.getUniqueId());
                    if (EmberEventTeach.tryMarkTeach(pd, kind)) {
                        try { p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(teach)); } catch (Throwable ignored) { }
                        try { p.sendTitle("", teach, 5, 35, 10); } catch (Throwable ignored) { }
                        svc.flushData(p.getUniqueId());
                    }
                }
            }
        }
        if (ok == 0) {
            anomalies.add(r.id + ": no mob could be spawned (MythicMobs ids " + def.roles.keySet() + ")");
            svc.onBroken(s, "房间 " + r.label + " 无法生成怪物");
        }
    }

    /** D300: remove the iron fence door and play the open cue. */
    private void openDoorNow(EmberRunMaps.Room r) {
        if (r == null || r.door == null) return;
        setBox(r.door, Material.AIR, Material.IRON_FENCE);
        w.playSound(center(r.door), Sound.BLOCK_IRON_DOOR_OPEN, 1.0f, 0.8f);
    }

    /** D585: any living boss half-HP add still up. */
    boolean hasPendingWave() {
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if ("add".equals(t.role)) return true;
        }
        return false;
    }

    /** D585: warp a committed player next to the nearest living boss add. */
    boolean pathWaveAdd(Player p) {
        if (p == null || !p.isOnline() || !hasPendingWave()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location pl = p.getLocation();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if (!"add".equals(t.role)) continue;
            double d = t.le.getLocation().distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Location at = best.le.getLocation();
        Location to = at.clone().add(1.2, 0, 0);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.7f);
        return true;
    }

    /** D584: boss share-soak circle currently armed. */
    boolean hasPendingSoak() {
        return pending != null && pending.share && lockOrigin != null
                && boss != null && boss.le != null && !boss.le.isDead();
    }

    /** D584: warp a committed player into the share circle center. */
    boolean pathSoakIn(Player p) {
        if (p == null || !p.isOnline() || !hasPendingSoak()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location to = lockOrigin.clone();
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.65f);
        return true;
    }

    /** D583: any living shield-affix elite still up. */
    boolean hasPendingShell() {
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if ("shield".equals(t.affix)) return true;
        }
        return false;
    }

    /** D583: warp a committed player next to the nearest living shield elite. */
    boolean pathShellElite(Player p) {
        if (p == null || !p.isOnline() || !hasPendingShell()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location pl = p.getLocation();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if (!"shield".equals(t.affix)) continue;
            double d = t.le.getLocation().distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Location at = best.le.getLocation();
        Location to = at.clone().add(1.2, 0, 0);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.6f);
        return true;
    }

    /** D582: any living split-add still up. */
    boolean hasPendingClone() {
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if (t.splitAdd) return true;
        }
        return false;
    }

    /** D582: warp a committed player next to the nearest living split-add. */
    boolean pathCloneAdd(Player p) {
        if (p == null || !p.isOnline() || !hasPendingClone()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location pl = p.getLocation();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if (!t.splitAdd) continue;
            double d = t.le.getLocation().distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Location at = best.le.getLocation();
        Location to = at.clone().add(1.2, 0, 0);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.55f);
        return true;
    }

    /** D581: boss cast armed with a break-damage channel. */
    boolean hasPendingPress() {
        return boss != null && boss.le != null && !boss.le.isDead()
                && pending != null && breakNeed > 0
                && !broken(breakNeed, breakDone);
    }

    /** D581: warp a committed player next to the boss to pile break damage. */
    boolean pathPressBoss(Player p) {
        if (p == null || !p.isOnline() || !hasPendingPress()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location at = boss.le.getLocation();
        Location to = at.clone().add(1.2, 0, 0);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.5f);
        return true;
    }

    /** D580: any regen elite currently channeling interrupt window. */
    boolean hasPendingCut() {
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if (!"regen".equals(t.affix)) continue;
            if (t.regenWindowEnd > 0) return true;
        }
        return false;
    }

    /** D580: warp a committed player next to the nearest regen-channeling elite. */
    boolean pathCutRegen(Player p) {
        if (p == null || !p.isOnline() || !hasPendingCut()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location pl = p.getLocation();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if (!"regen".equals(t.affix) || t.regenWindowEnd <= 0) continue;
            double d = t.le.getLocation().distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Location at = best.le.getLocation();
        Location to = at.clone().add(1.2, 0, 0);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.45f);
        return true;
    }

    /** D579: timed-clear event still live and unmet. */
    boolean hasRushLive() {
        if (s == null || eventStart <= 0 || eventFailed || s.eventDone) return false;
        if (activeRoom == null || !activeRoom.equals(s.eventRoom)) return false;
        String kind = s.eventKind == null || s.eventKind.isEmpty() ? "timed" : s.eventKind;
        return "timed".equals(kind);
    }

    /** D579: warp a committed player next to the nearest living event-room mob for timed clear. */
    boolean pathRushMob(Player p) {
        if (p == null || !p.isOnline() || !hasRushLive()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        if (s.eventRoom == null) return false;
        Location pl = p.getLocation();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if (t.varietyEscort) continue;
            if (!s.eventRoom.equals(t.roomId)) continue;
            double d = t.le.getLocation().distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Location at = best.le.getLocation();
        Location to = at.clone().add(1.2, 0, 0);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.4f);
        return true;
    }

    /** D578: unscathed event still live and not failed. */
    boolean hasCleanLive() {
        return eventLive("unscathed") && unscathedBudgetNow >= 0
                && unscathedOk(unscathedTaken, unscathedBudgetNow);
    }

    /** D578: pull a committed player away from the nearest event-room mob. */
    boolean pathCleanSpace(Player p) {
        if (p == null || !p.isOnline() || !hasCleanLive()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        if (s.eventRoom == null) return false;
        Location pl = p.getLocation();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if (t.varietyEscort) continue;
            if (!s.eventRoom.equals(t.roomId)) continue;
            double d = t.le.getLocation().distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Location o = best.le.getLocation();
        double dx = pl.getX() - o.getX(), dz = pl.getZ() - o.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double safe = 5.5;
        if (dist >= safe) return false; // already spaced
        Vector dir;
        if (dist < 0.15) {
            dir = pl.getDirection().clone();
            dir.setY(0);
            if (dir.lengthSquared() < 1e-6) dir = new Vector(1, 0, 0);
            else dir.normalize();
        } else {
            dir = new Vector(dx, 0, dz).normalize();
        }
        Location to = new Location(w, o.getX() + dir.getX() * safe, pl.getY(), o.getZ() + dir.getZ() * safe,
                pl.getYaw(), pl.getPitch());
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.2f);
        return true;
    }

    /** D577: chain-kill event still live and unmet. */
    boolean hasStreakLive() {
        return eventLive("chain") && chainNeedNow > 0 && chainBest < chainNeedNow;
    }

    /** D577: warp a committed player next to the nearest living event-room mob. */
    boolean pathStreakMob(Player p) {
        if (p == null || !p.isOnline() || !hasStreakLive()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        if (s.eventRoom == null) return false;
        Location pl = p.getLocation();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        for (Tracked t : mobs.values()) {
            if (t == null || t.le == null || t.le.isDead()) continue;
            if (t.varietyEscort) continue;
            if (!s.eventRoom.equals(t.roomId)) continue;
            double d = t.le.getLocation().distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Location at = best.le.getLocation();
        Location to = at.clone().add(1.2, 0, 0);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.35f);
        return true;
    }

    /** D576: any blazing circle warn still armed (not yet landed). */
    boolean hasPendingBlaze() {
        long now = System.currentTimeMillis();
        for (Tracked t : mobs.values()) {
            if (t == null || !"blazing".equals(t.affix)) continue;
            if (t.affixOrigin == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue;
            return true;
        }
        return false;
    }

    /** D576: pull a committed player out of the nearest blazing warn circle. */
    boolean pathBlazeOut(Player p) {
        if (p == null || !p.isOnline() || !hasPendingBlaze()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        EmberRunMaps.Variety v = svc.maps() == null ? null : svc.maps().variety;
        double r = v == null ? 2.5 : v.blazeRadius;
        long now = System.currentTimeMillis();
        Location best = null;
        double bestD = Double.MAX_VALUE;
        Location pl = p.getLocation();
        for (Tracked t : mobs.values()) {
            if (t == null || !"blazing".equals(t.affix)) continue;
            if (t.affixOrigin == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue;
            double d = t.affixOrigin.distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t.affixOrigin; }
        }
        if (best == null) return false;
        double dx = pl.getX() - best.getX(), dz = pl.getZ() - best.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double safe = r + 2.0;
        if (dist >= safe) return false;
        Vector dir;
        if (dist < 0.15) {
            dir = pl.getDirection().clone();
            dir.setY(0);
            if (dir.lengthSquared() < 1e-6) dir = new Vector(1, 0, 0);
            else dir.normalize();
        } else {
            dir = new Vector(dx, 0, dz).normalize();
        }
        Location to = new Location(w, best.getX() + dir.getX() * safe, pl.getY(), best.getZ() + dir.getZ() * safe,
                pl.getYaw(), pl.getPitch());
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.4f);
        return true;
    }

    /** D575: any arcane beam warn still armed (not yet spinning). */
    boolean hasPendingArcane() {
        long now = System.currentTimeMillis();
        for (Tracked t : mobs.values()) {
            if (t == null || !"arcane".equals(t.affix)) continue;
            if (t.affixOrigin == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue; // spinning already
            return true;
        }
        return false;
    }

    /** D575: pull a committed player outside the nearest arcane warn radius. */
    boolean pathSpinOut(Player p) {
        if (p == null || !p.isOnline() || !hasPendingArcane()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        EmberRunMaps.Variety v = svc.maps() == null ? null : svc.maps().variety;
        if (v == null) return false;
        long now = System.currentTimeMillis();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        Location pl = p.getLocation();
        for (Tracked t : mobs.values()) {
            if (t == null || !"arcane".equals(t.affix)) continue;
            if (t.affixOrigin == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue;
            double d = t.affixOrigin.distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Location o = best.affixOrigin;
        double dx = pl.getX() - o.getX(), dz = pl.getZ() - o.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double safe = v.arcaneLength + 2.0;
        if (dist >= safe) return false; // already clear of beam reach
        Vector dir;
        if (dist < 0.15) {
            // on pivot: step opposite mid-sweep
            double mid = best.arcaneStart + (best.arcaneSign >= 0 ? 1 : -1) * AffixArcane.sweepRad(v) / 2.0;
            dir = new Vector(Math.cos(mid + Math.PI), 0, Math.sin(mid + Math.PI));
        } else {
            dir = new Vector(dx, 0, dz).normalize();
        }
        Location to = new Location(w, o.getX() + dir.getX() * safe, pl.getY(), o.getZ() + dir.getZ() * safe,
                pl.getYaw(), pl.getPitch());
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.5f);
        return true;
    }

    /** D574: any firechain tether (smoke warn or live burn) is linked. */
    boolean hasPendingFirechain() {
        for (Tracked t : mobs.values()) {
            if (t == null || !"firechain".equals(t.affix)) continue;
            if (t.chainTo == null || t.le == null || t.chainTo.le == null) continue;
            if (t.le.isDead() || t.chainTo.le.isDead()) continue;
            return true;
        }
        return false;
    }

    /** D574: pull a committed player off the nearest firechain segment. */
    boolean pathSnapChain(Player p) {
        if (p == null || !p.isOnline() || !hasPendingFirechain()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        EmberRunMaps.Variety v = svc.maps() == null ? null : svc.maps().variety;
        if (v == null) return false;
        Tracked best = null;
        Location aBest = null, bBest = null;
        double bestD = Double.MAX_VALUE;
        Location pl = p.getLocation();
        for (Tracked t : mobs.values()) {
            if (t == null || !"firechain".equals(t.affix)) continue;
            Tracked q = t.chainTo;
            if (q == null || t.le == null || q.le == null || t.le.isDead() || q.le.isDead()) continue;
            Location a = t.le.getLocation(), b = q.le.getLocation();
            if (!chainTouches(a, b, v.chainLinkWidth + 1.0, pl)) continue; // near or on the tether
            double mx = (a.getX() + b.getX()) * 0.5, mz = (a.getZ() + b.getZ()) * 0.5;
            double d = (pl.getX() - mx) * (pl.getX() - mx) + (pl.getZ() - mz) * (pl.getZ() - mz);
            if (d < bestD) { bestD = d; best = t; aBest = a; bBest = b; }
        }
        if (best == null || aBest == null || bBest == null) return false;
        double dx = bBest.getX() - aBest.getX(), dz = bBest.getZ() - aBest.getZ();
        Vector along = new Vector(dx, 0, dz);
        Vector side;
        if (along.lengthSquared() < 1e-6) {
            side = new Vector(1, 0, 0);
        } else {
            along.normalize();
            side = new Vector(-along.getZ(), 0, along.getX());
            if (side.lengthSquared() < 1e-6) side = new Vector(1, 0, 0);
            else side.normalize();
        }
        Vector from = new Vector(pl.getX() - aBest.getX(), 0, pl.getZ() - aBest.getZ());
        if (from.dot(side) < 0) side.multiply(-1);
        double safe = v.chainLinkWidth * 0.5 + 2.5;
        Location to = new Location(w, pl.getX() + side.getX() * safe, pl.getY(), pl.getZ() + side.getZ() * safe,
                pl.getYaw(), pl.getPitch());
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.45f);
        return true;
    }

    /** D573: any venom cross warn still armed (not yet landed). */
    boolean hasPendingVenom() {
        long now = System.currentTimeMillis();
        for (Tracked t : mobs.values()) {
            if (t == null || !"venom".equals(t.affix)) continue;
            if (t.affixOrigin == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue;
            return true;
        }
        return false;
    }

    /** D573: pull a committed player off the nearest venom cross. */
    boolean pathVeilVenom(Player p) {
        if (p == null || !p.isOnline() || !hasPendingVenom()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        EmberRunMaps.Variety v = svc.maps() == null ? null : svc.maps().variety;
        if (v == null) return false;
        long now = System.currentTimeMillis();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        Location pl = p.getLocation();
        for (Tracked t : mobs.values()) {
            if (t == null || !"venom".equals(t.affix)) continue;
            if (t.affixOrigin == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue;
            EmberRunMaps.Skill arm = venomSkill(t, v);
            Vector[] dirs = venomDirs(t.venomDiag);
            if (!venomHits(arm, t.affixOrigin, dirs, pl)) continue;
            double d = t.affixOrigin.distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Location o = best.affixOrigin;
        double dx = pl.getX() - o.getX(), dz = pl.getZ() - o.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        Vector dir;
        if (dist < 0.15) {
            // on center: diagonal away from + or x arms
            dir = best.venomDiag ? new Vector(1, 0, 0) : new Vector(1, 0, 1);
            if (dir.lengthSquared() > 1e-6) dir.normalize();
        } else {
            dir = new Vector(dx, 0, dz).normalize();
        }
        double safe = v.venomArm + 2.0;
        Location to = new Location(w, o.getX() + dir.getX() * safe, pl.getY(), o.getZ() + dir.getZ() * safe,
                pl.getYaw(), pl.getPitch());
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.65f);
        return true;
    }

    /** D572: any charge strip warn still armed (not yet landed). */
    boolean hasPendingCharge() {
        long now = System.currentTimeMillis();
        for (Tracked t : mobs.values()) {
            if (t == null || !"charge".equals(t.affix)) continue;
            if (t.affixOrigin == null || t.affixDir == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue;
            return true;
        }
        return false;
    }

    /** D572: pull a committed player sideways out of the nearest charge strip. */
    boolean pathSidestepCharge(Player p) {
        if (p == null || !p.isOnline() || !hasPendingCharge()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        EmberRunMaps.Variety v = svc.maps() == null ? null : svc.maps().variety;
        if (v == null) return false;
        long now = System.currentTimeMillis();
        Tracked best = null;
        double bestD = Double.MAX_VALUE;
        Location pl = p.getLocation();
        for (Tracked t : mobs.values()) {
            if (t == null || !"charge".equals(t.affix)) continue;
            if (t.affixOrigin == null || t.affixDir == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue;
            EmberRunMaps.Skill sk = chargeSkill(t, v, t.affixDir);
            if (!inShape(sk, t.affixOrigin, t.affixDir, pl)) continue;
            double d = t.affixOrigin.distanceSquared(pl);
            if (d < bestD) { bestD = d; best = t; }
        }
        if (best == null) return false;
        Vector dir = best.affixDir.clone().normalize();
        // perpendicular horizontal
        Vector side = new Vector(-dir.getZ(), 0, dir.getX());
        if (side.lengthSquared() < 1e-6) side = new Vector(1, 0, 0);
        else side.normalize();
        // pick the side farther from strip centerline projection
        Location o = best.affixOrigin;
        Vector from = new Vector(pl.getX() - o.getX(), 0, pl.getZ() - o.getZ());
        if (from.dot(side) < 0) side.multiply(-1);
        double safe = v.chargeWidth * 0.5 + 2.0;
        Location to = new Location(w, pl.getX() + side.getX() * safe, pl.getY(), pl.getZ() + side.getZ() * safe,
                pl.getYaw(), pl.getPitch());
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.7f);
        return true;
    }

    /** D571: any mortar warn circle still armed (not yet landed). */
    boolean hasPendingMortar() {
        long now = System.currentTimeMillis();
        for (Tracked t : mobs.values()) {
            if (t == null || !"mortar".equals(t.affix)) continue;
            if (t.affixOrigin == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue;
            return true;
        }
        return false;
    }

    /** Nearest armed mortar warn origin, or null. */
    private Location nearestMortarOrigin(Location from) {
        if (from == null) return null;
        long now = System.currentTimeMillis();
        Location best = null;
        double bestD = Double.MAX_VALUE;
        for (Tracked t : mobs.values()) {
            if (t == null || !"mortar".equals(t.affix)) continue;
            if (t.affixOrigin == null || !AffixCycle.armed(t.affixAt)) continue;
            if (AffixCycle.lands(now, t.affixAt)) continue;
            double d = t.affixOrigin.distanceSquared(from);
            if (d < bestD) { bestD = d; best = t.affixOrigin; }
        }
        return best;
    }

    /** D571: pull a committed player out of the nearest mortar warn circle. */
    boolean pathDodgeMortar(Player p) {
        if (p == null || !p.isOnline() || !hasPendingMortar()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        EmberRunMaps.Variety v = svc.maps() == null ? null : svc.maps().variety;
        double r = v == null ? 2.5 : v.mortarRadius;
        Location o = nearestMortarOrigin(p.getLocation());
        if (o == null) return false;
        Location pl = p.getLocation();
        double dx = pl.getX() - o.getX(), dz = pl.getZ() - o.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double safe = r + 2.0;
        if (dist >= safe) return false;
        Vector dir;
        if (dist < 0.15) {
            dir = pl.getDirection().clone();
            dir.setY(0);
            if (dir.lengthSquared() < 1e-6) dir = new Vector(1, 0, 0);
            else dir.normalize();
        } else {
            dir = new Vector(dx, 0, dz).normalize();
        }
        Location to = new Location(w, o.getX() + dir.getX() * safe, pl.getY(), o.getZ() + dir.getZ() * safe,
                pl.getYaw(), pl.getPitch());
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.55f);
        return true;
    }

    /** D570: jailer root (JUMP 128 + SLOW 6) is active on player. */
    boolean hasJailerRoot(Player p) {
        if (p == null) return false;
        for (PotionEffect cur : p.getActivePotionEffects()) {
            if (cur.getType().equals(PotionEffectType.JUMP) && cur.getAmplifier() == 128) return true;
        }
        return false;
    }

    /** D570: strip jailer root from a committed player. */
    boolean pathFreeJailer(Player p) {
        if (p == null || !p.isOnline() || s == null || !s.open()) return false;
        if (!s.committed.contains(p.getUniqueId())) return false;
        if (!hasJailerRoot(p)) return false;
        p.removePotionEffect(PotionEffectType.JUMP);
        // jailer pairs JUMP 128 with SLOW amp 6 — strip that amp only if present
        for (PotionEffect cur : p.getActivePotionEffects()) {
            if (cur.getType().equals(PotionEffectType.SLOW) && cur.getAmplifier() == 6) {
                p.removePotionEffect(PotionEffectType.SLOW);
                break;
            }
        }
        return true;
    }

    /** D569: breach circle still open. */
    boolean hasBreachCircle() {
        return breachCenter != null && s != null && "breach".equals(s.eventKind);
    }

    /** D569: warp a committed player into the breach circle. */
    boolean pathBreachCenter(Player p) {
        if (p == null || !p.isOnline() || !hasBreachCircle()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location to = breachCenter.clone();
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.15f);
        return true;
    }

    /** D568: next relay marker still unlit. */
    boolean hasRelayNext() {
        return s != null && "relay".equals(s.eventKind)
                && !relayLocs.isEmpty() && relayNext >= 0 && relayNext < relayLocs.size();
    }

    /** D568: warp a committed player to the next relay to light. */
    boolean pathRelayNext(Player p) {
        if (p == null || !p.isOnline() || !hasRelayNext()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location to = relayLocs.get(relayNext).clone();
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.35f);
        return true;
    }

    /** D567: beacon still alive. */
    boolean hasBeaconLive() {
        return beaconLoc != null && beaconHp > 0 && s != null && "beacon".equals(s.eventKind);
    }

    /** D567: warp a committed player next to the beacon. */
    boolean pathBeaconGuard(Player p) {
        if (p == null || !p.isOnline() || !hasBeaconLive()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location to = beaconLoc.clone().add(1.0, 0, 0);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.25f);
        return true;
    }

    /** D566: hold circle is active. */
    boolean hasHoldCircle() {
        return holdCenter != null && s != null && "hold".equals(s.eventKind);
    }

    /** D566: warp a committed player into the hold circle. */
    boolean pathHoldCenter(Player p) {
        if (p == null || !p.isOnline() || !hasHoldCircle()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location to = holdCenter.clone();
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.2f);
        return true;
    }

    /** D565: escort rabbit still alive. */
    boolean hasGuardEscort() {
        if (escortId == null) return false;
        Tracked t = mobs.get(escortId);
        return t != null && t.varietyEscort && t.le != null && !t.le.isDead();
    }

    /** D565: warp a committed player next to the escort rabbit. */
    boolean pathGuardEscort(Player p) {
        if (p == null || !p.isOnline() || !hasGuardEscort()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Tracked t = mobs.get(escortId);
        Location at = t.le.getLocation();
        Location to = at.clone().add(1.2, 0, 0);
        to.setYaw(p.getLocation().getYaw());
        to.setPitch(0f);
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.3f);
        return true;
    }

    /** D564: variety crystals still standing. */
    boolean hasSmashCrystals() {
        return crystalBlocks != null && !crystalBlocks.isEmpty();
    }

    /** D564: warp a committed player to the nearest remaining crystal. */
    boolean pathSmashCrystal(Player p) {
        if (p == null || !p.isOnline() || !hasSmashCrystals()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        Location best = null;
        double bestD = Double.MAX_VALUE;
        Location pl = p.getLocation();
        for (Location c : crystalBlocks) {
            if (c == null || c.getWorld() == null) continue;
            if (c.getBlock().getType() != Material.SEA_LANTERN) continue;
            double dsq = c.distanceSquared(pl);
            if (dsq < bestD) { bestD = dsq; best = c; }
        }
        if (best == null) return false;
        Location to = best.clone().add(0.5, 0, 0.5);
        to.setYaw(pl.getYaw());
        to.setPitch(pl.getPitch());
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.5f);
        return true;
    }

    /** D561: player currently carries frost-affix slow. */
    boolean hasFrostSlow(Player p) {
        if (p == null || svc.maps() == null) return false;
        EmberRunMaps.Variety v = svc.maps().variety;
        if (v == null) return false;
        for (PotionEffect cur : p.getActivePotionEffects()) {
            if (cur.getType().equals(PotionEffectType.SLOW)
                    && AffixFrost.ownSlow(cur.getAmplifier(), cur.getDuration(), v)) return true;
        }
        return false;
    }

    /** D561: strip frost-affix slow from a committed player. */
    boolean pathThawFrost(Player p) {
        if (p == null || !p.isOnline() || s == null || !s.open()) return false;
        if (!s.committed.contains(p.getUniqueId())) return false;
        if (svc.maps() == null) return false;
        EmberRunMaps.Variety v = svc.maps().variety;
        if (v == null) return false;
        for (PotionEffect cur : p.getActivePotionEffects()) {
            if (cur.getType().equals(PotionEffectType.SLOW)
                    && AffixFrost.ownSlow(cur.getAmplifier(), cur.getDuration(), v)) {
                p.removePotionEffect(PotionEffectType.SLOW);
                return true;
            }
        }
        return false;
    }

    /** D560: molten corpse blast is armed and not yet landed. */
    boolean hasPendingMolten() {
        return moltenOrigin != null && moltenWarnAt > 0 && moltenBoomAt > System.currentTimeMillis();
    }

    /** D560: pull a committed player out of the molten blast radius. */
    boolean pathDuckBlast(Player p) {
        if (p == null || !p.isOnline() || !hasPendingMolten()) return false;
        if (s == null || !s.open() || !s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        EmberRunMaps.Variety v = svc.maps() == null ? null : svc.maps().variety;
        double r = v == null ? 2.5 : v.moltenRadius;
        Location o = moltenOrigin;
        Location pl = p.getLocation();
        double dx = pl.getX() - o.getX(), dz = pl.getZ() - o.getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        double safe = r + 2.0;
        if (dist >= safe) return false; // already clear
        Vector dir;
        if (dist < 0.15) {
            dir = pl.getDirection().clone();
            dir.setY(0);
            if (dir.lengthSquared() < 1e-6) dir = new Vector(1, 0, 0);
            else dir.normalize();
        } else {
            dir = new Vector(dx, 0, dz).normalize();
        }
        Location to = new Location(w, o.getX() + dir.getX() * safe, pl.getY(), o.getZ() + dir.getZ() * safe,
                pl.getYaw(), pl.getPitch());
        p.setFallDistance(0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 1.6f);
        return true;
    }

    /** D559: side extra (treasure/elite) is live and hunt-able. */
    boolean hasHuntExtra() {
        if (s == null || !s.open() || s.extraDone) return false;
        if (s.extra != EmberRunRules.Extra.TREASURE && s.extra != EmberRunRules.Extra.ELITE) return false;
        if (def == null || def.eventAnchor == null || !extraSpawned) return false;
        if (extraMob != null) {
            Tracked t = mobs.get(extraMob);
            if (t != null && t.le != null && !t.le.isDead()) return true;
            return false;
        }
        return true;
    }

    /** D559: warp a committed player to the side-extra anchor. */
    boolean pathHuntExtra(Player p) {
        if (p == null || !p.isOnline() || !hasHuntExtra()) return false;
        if (!s.committed.contains(p.getUniqueId())) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        EmberRunMaps.Pt a = def.eventAnchor;
        Location to = new Location(w, a.x + 0.5, a.y, a.z + 0.5, p.getLocation().getYaw(), 0f);
        p.teleport(to);
        w.playSound(to, Sound.ENTITY_ENDERMEN_TELEPORT, 0.6f, 1.4f);
        return true;
    }

    /** D558: true if any cleared-room passage link is available. */
    boolean hasOpenLinks() {
        if (def == null || def.links == null || s == null) return false;
        for (EmberRunMaps.Link k : def.links) {
            if (k.after != null && s.cleared.contains(k.after) && k.to != null) return true;
        }
        return false;
    }

    /** D558: teleport one player through links opened by {@code afterRoom} (null = any open). */
    boolean pathTakeLinks(Player p, String afterRoom) {
        if (p == null || !p.isOnline() || def == null || def.links == null || s == null) return false;
        if (!s.committed.contains(p.getUniqueId()) || !s.open()) return false;
        if (p.getGameMode() == org.bukkit.GameMode.SPECTATOR || p.isDead()) return false;
        boolean any = false;
        for (EmberRunMaps.Link k : def.links) {
            if (k.after == null || k.to == null) continue;
            if (afterRoom != null && !afterRoom.equals(k.after)) continue;
            if (!s.cleared.contains(k.after)) continue;
            p.teleport(new Location(w, k.to.x + 0.5, k.to.y, k.to.z + 0.5, k.yaw, 0f));
            w.playSound(p.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 0.8f, 1.2f);
            if (!k.label.isEmpty()) p.sendMessage("§7" + k.label);
            any = true;
        }
        return any;
    }

    /** D557: boss arm timer running, not yet spawned. */
    boolean hasPendingCall() {
        return bossAt > 0 && boss == null && !bossDead;
    }

    /** D557: spawn the armed boss immediately (AUTO / ASK). */
    boolean pathCallBoss() {
        if (!hasPendingCall()) return false;
        spawnBoss(System.currentTimeMillis());
        return boss != null;
    }

    /** D556: true while a door_delay breath is waiting. */
    boolean hasPendingDoor() {
        return doorPending != null && doorOpenAt > 0;
    }

    /** D556: open the pending door immediately (AUTO / ASK). */
    boolean pathForceOpenDoor() {
        if (!hasPendingDoor()) return false;
        EmberRunMaps.Room r = doorPending;
        doorPending = null;
        doorOpenAt = 0;
        openDoorNow(r);
        return true;
    }

    private void roomCleared(EmberRunMaps.Room r) {
        activeRoom = null;
        if (r == null) return;
        if (r.id.equals(s.eventRoom) && eventStart > 0) { // D138/D171 room event
            double secs = (System.currentTimeMillis() - eventStart) / 1000.0;
            eventStart = 0;
            String kind = s.eventKind == null || s.eventKind.isEmpty() ? "timed" : s.eventKind;
            EmberRunMaps.Variety v = svc.maps().variety;
            boolean ok;
            if ("crystal".equals(kind)) {
                ok = !eventFailed && crystalsBroken >= v.crystalCount && secs <= v.crystalSecs;
            } else if ("escort".equals(kind)) {
                ok = !eventFailed && escortId != null && mobs.containsKey(escortId);
            } else if ("hold".equals(kind)) {
                ok = !eventFailed && holdAccum >= v.holdNeed && secs <= v.holdSecs;
            } else if ("beacon".equals(kind)) {
                ok = !eventFailed && beaconHp > 0 && beaconStand != null && !beaconStand.isDead();
            } else if ("relay".equals(kind)) {
                ok = !eventFailed && relayNext >= v.relayCount && secs <= v.relaySecs;
            } else if ("breach".equals(kind)) {
                ok = !eventFailed && breachCenter != null && !breachCollapsed(breachR, v.breachMin);
            } else if ("chain".equals(kind)) {
                ok = !eventFailed && chainBest >= chainNeedNow && chainNeedNow > 0;
            } else if ("unscathed".equals(kind)) {
                ok = !eventFailed && unscathedOk(unscathedTaken, unscathedBudgetNow);
            } else {
                ok = secs <= v.eventLimit(kind);
            }
            clearCrystals();
            removeEscort();
            clearHold();
            clearBeacon();
            clearRelay();
            clearBreach();
            chainNeedNow = 0;
            unscathedBudgetNow = -1;
            clearFrostSlow();
            svc.onEventResult(s, ok, secs);
        }
        if (!s.cleared.contains(r.id)) s.cleared.add(r.id);
        next = Math.max(next, def.roomIndex(r.id) + 1);
        if (r.door != null) {
            if (r.doorDelay > 0) {
                doorPending = r;
                doorOpenAt = System.currentTimeMillis() + (long) (r.doorDelay * 1000.0);
                svc.tellRun(s, "§7门扇缓缓开启…");
                EmberGatePath.maybeAfterPending(s); // D556
            } else {
                openDoorNow(r);
            }
        }
        boolean linkOpened = false;
        for (EmberRunMaps.Link k : def.links) if (k.after.equals(r.id)) {
            svc.tellRun(s, "§7通道已开启：" + k.label);
            linkOpened = true;
        }
        if (linkOpened) EmberLinkPath.maybeAfterOpen(s, r.id); // D558
        if (r.id.equals(def.eventAfter)) spawnExtra();
        boolean last = next >= def.rooms.size();
        svc.onRoomCleared(s, r, last);
        if (last) {
            bossAt = System.currentTimeMillis() + 1500L;
            svc.tellRun(s, bossDef().waitInArea ? "§c" + bossDef().name + " §7在前方首领厅等候，走进大厅即现身"
                    : "§c" + bossDef().name + " §7即将在大厅中央现身（1.5 秒）");
            svc.tellRun(s, EmberRunRules.PRE_BOSS_HINT); // D166 (real-gear kite playtest: Q04 boss lost after the potions ran out)
            EmberCallPath.maybeAfterArmed(s); // D557
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
    private boolean eventFailed; // D171/D179: escort/beacon died / soft fail already told
    private final List<Location> crystalBlocks = new ArrayList<Location>();
    private int crystalsBroken;
    private UUID escortId;
    // D179 hold / beacon / relay (markers are ArmorStands + optional SEA_LANTERN; never in mobs / Extra.TREASURE)
    private Location holdCenter;
    private double holdAccum;
    private int holdToldPct; // last progress chat milestone (0/25/50/75)
    private org.bukkit.entity.ArmorStand holdMarker;
    private Location holdLantern;
    private Location beaconLoc;
    private org.bukkit.entity.ArmorStand beaconStand;
    private Location beaconLantern;
    private double beaconHp, beaconMaxHp;
    private long beaconNextBite;
    private final List<org.bukkit.entity.ArmorStand> relayMarkers = new ArrayList<org.bukkit.entity.ArmorStand>();
    private final List<Location> relayLocs = new ArrayList<Location>();
    private final List<Location> relayLanterns = new ArrayList<Location>();
    private int relayNext; // next index to light (0-based)
    private long lastEventTickMs; // hold/beacon/relay/breach dt anchor
    // D191 breach (裂隙): shrinking circle, kills inside push it back out; markers never in mobs / Extra.TREASURE
    private Location breachCenter;
    private double breachR;
    private org.bukkit.entity.ArmorStand breachMarker;
    private Location breachLantern;
    // D191 chain (连斩): kill streak inside the event room
    private int chainNeedNow, chainStreak, chainBest;
    private long chainLastMs;
    // D191 unscathed (无伤): landed run-mob hits on the party while the event room is live (-1 = no budget armed)
    private int unscathedTaken;
    private int unscathedBudgetNow = -1;
    // D181 molten: corpse blast survives the Tracked (cleared on finish / unload like crystals)
    private Location moltenOrigin;
    private long moltenWarnAt;   // when telegraph starts (0 = idle)
    private long moltenBoomAt;   // when damage lands
    private double moltenDmg;    // absolute damage (atk × mult at death)

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
                a.setBaseValue(AffixShield.maxHp(a.getBaseValue(), v));
                t.le.setHealth(t.le.getMaxHealth());
            }
        }
        // D241: cadence / intro / glow come from the affix primitive (p1.encounter.Affix*; unknown id → old fallbacks)
        double every = EmberAffixes.firstEvery(t.affix, v); // molten has no live tick; split/shield idle glow only
        t.affixNext = AffixCycle.firstNext(System.currentTimeMillis(), every);
        String tag = EmberRunMaps.Variety.label(t.affix);
        String old = t.le.getCustomName();
        t.le.setCustomName("§6[" + tag + "] §r" + (old == null ? t.le.getName() : old));
        t.le.setCustomNameVisible(true);
        t.le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 600, 0, false, false), true);
        String how = EmberAffixes.how(t.affix, v);
        svc.tellRun(s, "§6词缀精英「" + tag + "」§7出现：" + how + " · 击败 → 结算时 §f余烬碎片 +" + v.affixShard);
        if ("shield".equals(t.affix)) EmberShellPath.maybeAfterShell(s); // D583
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s %s affix %s on %s hp=%.0f", s.runId, t.roomId, t.affix, t.role, t.le.getMaxHealth()));
    }

    /** D241: idle glow per affix primitive ({@code AffixBehavior.fx()}), resolved once; unknown id → END_ROD. */
    private static final Map<String, Particle> AFFIX_FX = new HashMap<String, Particle>();
    static {
        for (String id : EmberAffixes.all().keySet()) AFFIX_FX.put(id, Particle.valueOf(EmberAffixes.fx(id)));
    }

    static Particle affixFx(String affix) {
        Particle p = affix == null ? null : AFFIX_FX.get(affix);
        return p == null ? Particle.END_ROD : p;
    }

    private void affixTick(Tracked t, long now) {
        Particle fx = affixFx(t.affix);
        w.spawnParticle(fx, t.le.getLocation().add(0, 1.0, 0), 3, 0.3, 0.5, 0.3, 0.01);
        EmberRunMaps.Variety v = svc.maps().variety;
        if ("blazing".equals(t.affix)) {
            if (AffixCycle.armed(t.affixAt)) {
                warnCircle(t.affixOrigin.clone().add(0, 0.15, 0), v.blazeRadius, Particle.FLAME);
                if (AffixCycle.lands(now, t.affixAt)) {
                    execute(blazeSkill(t, v), t.affixOrigin, new Vector(1, 0, 0), t.le);
                    t.affixAt = 0;
                    t.affixNext = AffixCycle.after(now, v.blazeEvery);
                }
                return;
            }
            if (!AffixCycle.ready(now, t.affixNext) || nearest(t.le.getLocation(), AffixBlazing.ENGAGE) == null) return;
            t.affixOrigin = t.le.getLocation().clone();
            t.affixAt = AffixCycle.after(now, v.blazeWarn);
            EmberBlazePath.maybeAfterBlaze(s); // D576
            return;
        }
        if ("regen".equals(t.affix)) {
            if (t.regenWindowEnd > 0) {
                // flash name during interrupt window
                if ((now / 200) % 2 == 0) t.le.setCustomNameVisible(true);
                if (AffixCycle.lands(now, t.regenWindowEnd)) {
                    if (!AffixRegen.interrupted(t.regenHurt, t.le.getMaxHealth(), v)) {
                        double heal = AffixRegen.heal(t.le.getHealth(), t.le.getMaxHealth(), v);
                        if (heal > 0) t.le.setHealth(Math.min(t.le.getMaxHealth(), t.le.getHealth() + heal));
                        svc.tellRun(s, "§6「再生」§7回血了（未打断）");
                    } else {
                        svc.tellRun(s, "§a「再生」§7被打断！");
                    }
                    t.regenWindowEnd = 0;
                    t.regenHurt = 0;
                    t.affixNext = AffixCycle.after(now, v.regenEvery);
                }
                return;
            }
            if (!AffixCycle.ready(now, t.affixNext) || nearest(t.le.getLocation(), AffixRegen.ENGAGE) == null) return;
            t.regenWindowEnd = AffixRegen.windowEnd(now, v);
            t.regenHurt = 0;
            svc.tellRun(s, "§6「再生」§7读条中 · 猛打可打断");
            EmberCutPath.maybeAfterCut(s); // D580
            return;
        }
        if ("charge".equals(t.affix)) {
            // D171: telegraph strip only — no body dash out of leash (execute skips teleport when src != boss)
            if (AffixCycle.armed(t.affixAt)) {
                EmberRunMaps.Skill sk = chargeSkill(t, v, t.affixDir == null ? new Vector(1, 0, 0) : t.affixDir);
                drawShape(sk, t.affixOrigin, t.affixDir == null ? new Vector(1, 0, 0) : t.affixDir);
                if (AffixCycle.lands(now, t.affixAt)) {
                    execute(sk, t.affixOrigin, t.affixDir == null ? new Vector(1, 0, 0) : t.affixDir, t.le);
                    t.affixAt = 0;
                    t.affixNext = AffixCycle.after(now, v.chargeEvery);
                }
                return;
            }
            Player tgt = nearest(t.le.getLocation(), AffixCharge.ENGAGE);
            if (!AffixCycle.ready(now, t.affixNext) || tgt == null) return;
            Location o = t.le.getLocation().clone();
            Vector dir = AffixCharge.aim(o.getX(), o.getZ(), tgt.getLocation().getX(), tgt.getLocation().getZ());
            double run = clearRun(o, dir, v.chargeLength);
            if (!AffixCharge.roomFor(run)) return; // no room — skip this attempt, keep cooldown
            t.affixOrigin = o;
            t.affixDir = dir;
            t.affixAt = AffixCycle.after(now, v.chargeWarn);
            EmberSidestepPath.maybeAfterCharge(s); // D572
            return;
        }
        if ("frost".equals(t.affix)) {
            if (!AffixCycle.ready(now, t.frostNext)) return;
            t.frostNext = AffixFrost.nextTick(now, v);
            Location c = t.le.getLocation();
            warnCircle(c.clone().add(0, 0.15, 0), v.frostRadius, Particle.SNOW_SHOVEL);
            for (Player p : participantsHere()) {
                if (p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
                if (AffixFrost.inAura(p.getLocation().distanceSquared(c), v)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, AffixFrost.slowTicks(v), v.frostAmplifier, false, true), true);
                    EmberThawPath.maybeAfterFrost(p); // D561
                } else {
                    for (PotionEffect cur : p.getActivePotionEffects()) {
                        if (cur.getType().equals(PotionEffectType.SLOW) && AffixFrost.ownSlow(cur.getAmplifier(), cur.getDuration(), v)) {
                            p.removePotionEffect(PotionEffectType.SLOW);
                            break;
                        }
                    }
                }
            }
            return;
        }
        if ("mortar".equals(t.affix)) {
            // D181: periodic circle near nearest in-run player feet (onGround Y); kb=0
            if (AffixCycle.armed(t.affixAt)) {
                warnCircle(t.affixOrigin.clone().add(0, 0.15, 0), v.mortarRadius, Particle.FLAME);
                if (AffixCycle.lands(now, t.affixAt)) {
                    execute(mortarSkill(t, v), t.affixOrigin, new Vector(1, 0, 0), t.le);
                    t.affixAt = 0;
                    t.affixNext = AffixCycle.after(now, v.mortarEvery);
                }
                return;
            }
            if (!AffixCycle.ready(now, t.affixNext)) return;
            Location feet = mortarTargetFeet(t, v);
            if (feet == null) return;
            t.affixOrigin = feet;
            t.affixAt = AffixCycle.after(now, v.mortarWarn);
            EmberDodgePath.maybeAfterMortar(s); // D571
            return;
        }
        if ("venom".equals(t.affix)) {
            // D189 毒十字: two lines crossing at the elite's feet ("+" then "x"); a player is hit at most once per cast
            EmberRunMaps.Skill arm = venomSkill(t, v);
            if (AffixCycle.armed(t.affixAt)) {
                for (Vector d : venomDirs(t.venomDiag)) drawShape(arm, t.affixOrigin, d);
                if (AffixCycle.lands(now, t.affixAt)) {
                    Vector[] dirs = venomDirs(t.venomDiag);
                    int hit = 0;
                    for (Player p : participantsHere()) {
                        if (p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
                        if (!venomHits(arm, t.affixOrigin, dirs, p.getLocation())) continue;
                        svc.skillHit(s, p, t.le, arm.dmg, "mob");
                        hit++;
                    }
                    w.spawnParticle(Particle.SPELL_MOB, t.affixOrigin.clone().add(0, 0.4, 0), 24, v.venomArm / 2.0, 0.2, v.venomArm / 2.0, 0.0);
                    w.playSound(t.affixOrigin, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.6f);
                    svc.log().info(String.format(Locale.ROOT, "[P1 run] %s venom %s hit=%d", s.runId, t.venomDiag ? "x" : "+", hit));
                    t.venomDiag = !t.venomDiag;
                    t.affixAt = 0;
                    t.affixNext = AffixCycle.after(now, v.venomEvery);
                }
                return;
            }
            if (!AffixCycle.ready(now, t.affixNext) || nearest(t.le.getLocation(), AffixVenom.ENGAGE) == null) return;
            Location o = t.le.getLocation().clone();
            o.setY(Math.floor(o.getY()));
            t.affixOrigin = o;
            t.affixAt = AffixCycle.after(now, v.venomWarn);
            EmberVeilPath.maybeAfterVenom(s); // D573
            return;
        }
        if ("jailer".equals(t.affix)) {
            // D189 禁锢: small circle at the nearest player's feet; inside when it lands = light hit + rooted (≤1.5 s)
            if (AffixCycle.armed(t.affixAt)) {
                warnCircle(t.affixOrigin.clone().add(0, 0.15, 0), v.jailerRadius, Particle.CRIT_MAGIC);
                warnCircle(t.affixOrigin.clone().add(0, 0.15, 0), v.jailerRadius * 0.5, Particle.CRIT_MAGIC);
                if (AffixCycle.lands(now, t.affixAt)) {
                    EmberRunMaps.Skill sk = jailerSkill(t, v);
                    int ticks = jailerRootTicks(v);
                    int rooted = 0;
                    for (Player p : participantsHere()) {
                        if (p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
                        if (!inShape(sk, t.affixOrigin, new Vector(1, 0, 0), p.getLocation())) continue;
                        if (sk.dmg > 0) svc.skillHit(s, p, t.le, sk.dmg, "mob");
                        if (ticks > 0 && !p.isDead()) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, ticks, 6, false, true), true);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, ticks, 128, false, false), true);
                            p.sendMessage("§5「禁锢」§7你被定住 " + fmt(v.jailerRoot) + " 秒");
                            EmberFreePath.maybeAfterJailer(p); // D570
                            rooted++;
                        }
                    }
                    w.playSound(t.affixOrigin, Sound.BLOCK_ANVIL_LAND, 0.5f, 1.4f);
                    svc.log().info(String.format(Locale.ROOT, "[P1 run] %s jailer rooted=%d", s.runId, rooted));
                    t.affixAt = 0;
                    t.affixNext = AffixCycle.after(now, v.jailerEvery);
                }
                return;
            }
            if (!AffixCycle.ready(now, t.affixNext)) return;
            Location feet = mortarTargetFeet(t, v, 0.0);
            if (feet == null) return;
            t.affixOrigin = feet;
            t.affixAt = AffixCycle.after(now, v.jailerWarn);
            return;
        }
        if ("arcane".equals(t.affix)) {
            // D196 旋光 (D3 Arcane Enchanted): a beam from the elite's (locked) feet; the warning shows the start line and the
            // swept half; then it turns `sweep`° in `spin` s. A player is hit at most once per cast (swept-interval check, so a
            // fast step between two ticks cannot jump over the beam). The nearest player sits in the middle of the swept arc.
            double sweep = AffixArcane.sweepRad(v);
            if (AffixCycle.armed(t.affixAt)) {
                if (!AffixCycle.lands(now, t.affixAt)) {
                    drawBeam(t.affixOrigin, t.arcaneStart, v.arcaneLength, Particle.SPELL_WITCH);
                    drawArc(t.affixOrigin, t.arcaneStart, t.arcaneSign * sweep, v.arcaneLength, Particle.SPELL_WITCH);
                    return;
                }
                double el = (now - t.affixAt) / 1000.0;
                double cur = arcaneAngle(t.arcaneStart, t.arcaneSign, sweep, v.arcaneSpin, el);
                drawBeam(t.affixOrigin, cur, v.arcaneLength, Particle.END_ROD);
                double dmg = AffixArcane.dmg(t.atk, v);
                for (Player p : participantsHere()) {
                    if (p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
                    if (t.arcaneHit.contains(p.getUniqueId())) continue;
                    if (!arcaneSwept(t.affixOrigin, t.arcanePrev, cur, v.arcaneLength, v.arcaneWidth, p.getLocation())) continue;
                    t.arcaneHit.add(p.getUniqueId());
                    if (dmg > 0) svc.skillHit(s, p, t.le, dmg, "mob");
                }
                t.arcanePrev = cur;
                if (AffixArcane.spun(el, v)) {
                    svc.log().info(String.format(Locale.ROOT, "[P1 run] %s arcane %s hit=%d", s.runId, t.arcaneSign > 0 ? "ccw" : "cw", t.arcaneHit.size()));
                    t.arcaneSign = -t.arcaneSign;
                    t.arcaneHit.clear();
                    t.affixAt = 0;
                    t.affixNext = AffixCycle.after(now, v.arcaneEvery);
                }
                return;
            }
            Player tgt = nearest(t.le.getLocation(), AffixArcane.ENGAGE);
            if (!AffixCycle.ready(now, t.affixNext) || tgt == null) return;
            Location o = t.le.getLocation().clone();
            o.setY(Math.floor(o.getY()));
            t.affixOrigin = o;
            t.arcaneStart = arcaneStartAngle(o, tgt.getLocation(), t.arcaneSign, sweep);
            t.arcanePrev = t.arcaneStart;
            t.arcaneHit.clear();
            t.affixAt = AffixCycle.after(now, v.arcaneWarn);
            w.playSound(o, Sound.BLOCK_NOTE_PLING, 0.6f, 0.8f);
            EmberSpinPath.maybeAfterArcane(s); // D575
            return;
        }
        if ("firechain".equals(t.affix)) {
            // D196 火链 (D3 Fire Chains): a burning tether between the elite and its nearest room mob; touching it burns
            // (atk × dmg, at most once per `tick` s per player). Partner dead / gone → the chain re-forms on the next nearest
            // after a `warn` s smoke line. No partner left → no chain.
            Tracked q = t.chainTo;
            if (q != null && (q.le.isDead() || !q.le.isValid() || !mobs.containsKey(q.le.getUniqueId())
                    || AffixFirechain.tooFar(q.le.getLocation().distanceSquared(t.le.getLocation()), v))) {
                t.chainTo = null;
                q = null;
                t.affixNext = now; // re-link right away; the new link still waits its warning before it burns
            }
            if (q == null) {
                if (!AffixCycle.ready(now, t.affixNext)) return;
                Tracked best = chainPartner(t, mobs.values(), v.chainLinkRange);
                if (best == null) { t.affixNext = now + AffixFirechain.RELINK_RETRY_MS; return; }
                t.chainTo = best;
                t.chainLiveAt = AffixFirechain.liveAt(now, v);
                svc.log().info(String.format(Locale.ROOT, "[P1 run] %s firechain link %s", s.runId, best.role));
                EmberSnapPath.maybeAfterLink(s); // D574
                return;
            }
            Location a = t.le.getLocation(), b = q.le.getLocation();
            boolean live = AffixCycle.lands(now, t.chainLiveAt);
            drawLink(a, b, live ? Particle.FLAME : Particle.SMOKE_NORMAL);
            if (!live) return;
            double dmg = AffixFirechain.dmg(t.atk, v);
            for (Player p : participantsHere()) {
                if (p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
                if (!chainTouches(a, b, v.chainLinkWidth, p.getLocation())) continue;
                if (!chainBurnReady(t.chainBurnAt.get(p.getUniqueId()), now, v.chainLinkTick)) continue;
                t.chainBurnAt.put(p.getUniqueId(), now);
                if (dmg > 0) svc.skillHit(s, p, t.le, dmg, "mob");
            }
        }
    }

    /** D196 旋光: beam angle (rad) {@code elapsed} s into the spin; stops at the end of the sweep. */
    /** D196 旋光: beam angle (rad) {@code elapsed} s into the spin. D241: {@link AffixArcane#angle}. */
    static double arcaneAngle(double start, int sign, double sweepRad, double spin, double elapsed) {
        return AffixArcane.angle(start, sign, sweepRad, spin, elapsed);
    }

    /** D196 旋光: start angle so the target sits in the middle of the swept arc. D241: {@link AffixArcane#startAngle}. */
    static double arcaneStartAngle(Location o, Location target, int sign, double sweepRad) {
        return AffixArcane.startAngle(o, target, sign, sweepRad);
    }

    /**
     * D196 旋光: did the beam (length {@code len}, width {@code width}) pass over {@code p} while turning from {@code a0} to
     * {@code a1}? Swept-interval test on the player's polar angle, widened by the beam's half width at that distance.
     */
    /** D196 旋光: swept-interval beam hit test. D241: {@link AffixArcane#swept}. */
    static boolean arcaneSwept(Location o, double a0, double a1, double len, double width, Location p) {
        return AffixArcane.swept(o, a0, a1, len, width, p);
    }

    /** D196 火链: is {@code p} touching the chain segment a–b (horizontal distance ≤ width/2, within the two mobs' heights)? */
    /** D196 火链: touching the chain segment a–b? D241: {@link AffixFirechain#touches}. */
    static boolean chainTouches(Location a, Location b, double width, Location p) {
        return AffixFirechain.touches(a, b, width, p);
    }

    /** D196 火链: per-player burn cooldown. D241: {@link AffixFirechain#burnReady}. */
    static boolean chainBurnReady(Long last, long now, double tick) {
        return AffixFirechain.burnReady(last, now, tick);
    }

    /** D196 火链: nearest living mob of the same room (never the boss, the escort rabbit or itself) within {@code range}. */
    static Tracked chainPartner(Tracked t, Iterable<Tracked> all, double range) {
        Tracked best = null;
        double bd = range * range;
        Location l = t.le.getLocation();
        for (Tracked o : all) {
            if (o == t || o.boss() || o.varietyEscort || o.le == null || o.le.isDead() || !o.le.isValid()) continue;
            if (t.roomId == null || !t.roomId.equals(o.roomId)) continue;
            double d = o.le.getLocation().distanceSquared(l);
            if (d <= bd) { bd = d; best = o; }
        }
        return best;
    }

    private void drawBeam(Location o, double ang, double len, Particle fx) {
        double y = o.getY() + 0.6;
        for (double r = 0.5; r <= len + 1e-9; r += 0.5)
            w.spawnParticle(fx, o.getX() + Math.cos(ang) * r, y, o.getZ() + Math.sin(ang) * r, 1, 0, 0, 0, 0);
    }

    private void drawArc(Location o, double start, double sweep, double r, Particle fx) {
        double y = o.getY() + 0.15;
        int n = Math.max(6, (int) (Math.abs(sweep) * r * 2));
        for (int i = 0; i <= n; i++) {
            double a = start + sweep * i / n;
            w.spawnParticle(fx, o.getX() + Math.cos(a) * r, y, o.getZ() + Math.sin(a) * r, 1, 0, 0, 0, 0);
        }
    }

    private void drawLink(Location a, Location b, Particle fx) {
        double dx = b.getX() - a.getX(), dy = b.getY() - a.getY(), dz = b.getZ() - a.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        int n = Math.max(2, (int) (len * 2));
        for (int i = 0; i <= n; i++) {
            double f = (double) i / n;
            w.spawnParticle(fx, a.getX() + dx * f, a.getY() + 1.0 + dy * f, a.getZ() + dz * f, 1, 0, 0, 0, 0);
        }
    }

    /** D189 毒十字 arm: one line through the centre, {@code -arm .. +arm}; kb 0. */
    /** D189 毒十字 arm. D241: {@link AffixVenom#skill}. */
    static EmberRunMaps.Skill venomSkill(Tracked t, EmberRunMaps.Variety v) { return AffixVenom.skill(t.atk, v); }

    /** D189: "+" / "x" arm directions. D241: {@link AffixVenom#dirs}. */
    static Vector[] venomDirs(boolean diag) { return AffixVenom.dirs(diag); }

    /** D189: inside either arm. D241: {@link AffixVenom#hits}. */
    static boolean venomHits(EmberRunMaps.Skill arm, Location o, Vector[] dirs, Location p) { return AffixVenom.hits(arm, o, dirs, p); }

    /** D189 禁锢 circle. D241: {@link AffixJailer#skill}. */
    static EmberRunMaps.Skill jailerSkill(Tracked t, EmberRunMaps.Variety v) { return AffixJailer.skill(t.atk, v); }

    /** D189: root length in ticks (config clamps to ≤ 1.5 s). D241: {@link AffixJailer#rootTicks}. */
    static int jailerRootTicks(EmberRunMaps.Variety v) { return AffixJailer.rootTicks(v); }

    /** D138 炽热 circle. D241: {@link AffixBlazing#skill}. */
    static EmberRunMaps.Skill blazeSkill(Tracked t, EmberRunMaps.Variety v) { return AffixBlazing.skill(t.atk, v); }

    /** D171 charge affix strip (src != boss → execute will not teleport the body). D241: {@link AffixCharge#skill}. */
    static EmberRunMaps.Skill chargeSkill(Tracked t, EmberRunMaps.Variety v, Vector dir) { return AffixCharge.skill(t.atk, v, dir); }

    /** D181 mortar circle at player feet; kb 0. D241: {@link AffixMortar#skill}. */
    static EmberRunMaps.Skill mortarSkill(Tracked t, EmberRunMaps.Variety v) { return AffixMortar.skill(t.atk, v); }

    /** D181 molten corpse blast (atk snapped at death). kb=0. D241: {@link AffixMolten#skill}. */
    static EmberRunMaps.Skill moltenSkill(double atk, EmberRunMaps.Variety v) { return AffixMolten.skill(atk, v); }

    /** Nearest participant feet on ground Y; fallback elite feet. null only when nobody is in the instance. */
    private Location mortarTargetFeet(Tracked t, EmberRunMaps.Variety v) { return mortarTargetFeet(t, v, v.mortarAhead); }

    private Location mortarTargetFeet(Tracked t, EmberRunMaps.Variety v, double ahead) {
        Player tgt = nearest(t.le.getLocation(), AffixMortar.REACH);
        Location base;
        if (tgt != null) {
            Location l = tgt.getLocation();
            double y = tgt.isOnGround() ? l.getY() : Math.floor(l.getY());
            base = new Location(w, l.getX(), y, l.getZ());
        } else if (participantsHere().isEmpty()) {
            return null;
        } else {
            base = t.le.getLocation().clone();
            base.setY(Math.floor(base.getY()));
        }
        if (Math.abs(ahead) > 1e-6) {
            Vector dir = t.le.getLocation().toVector().subtract(base.toVector());
            dir.setY(0);
            if (dir.lengthSquared() > 1e-6) {
                dir.normalize().multiply(ahead);
                base.add(dir.getX(), 0, dir.getZ());
            }
        }
        return base;
    }

    private void scheduleMolten(Tracked t) {
        if (t == null || !AffixMolten.triggers(t.splitAdd)) return; // splitAdds must NOT trigger molten
        EmberRunMaps.Variety v = svc.maps().variety;
        Location at = t.le.getLocation().clone();
        at.setY(Math.floor(at.getY()));
        moltenOrigin = at;
        moltenDmg = AffixMolten.blastDmg(t.atk, v); // store absolute for execute path via skill
        long now = System.currentTimeMillis();
        moltenWarnAt = AffixMolten.warnAt(now, v);
        moltenBoomAt = AffixMolten.boomAt(moltenWarnAt, v);
        svc.tellRun(s, "§c亡爆 §7· 尸体要炸，退后！");
        EmberDuckPath.maybeAfterMolten(s); // D560
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s molten scheduled at %.1f %.1f %.1f",
                s.runId, at.getX(), at.getY(), at.getZ()));
    }

    private void moltenTick(long now) {
        if (moltenWarnAt <= 0 || moltenOrigin == null) return;
        EmberRunMaps.Variety v = svc.maps().variety;
        if (now < moltenWarnAt) return;
        warnCircle(moltenOrigin.clone().add(0, 0.15, 0), v.moltenRadius, Particle.LAVA);
        if (AffixCycle.lands(now, moltenBoomAt)) {
            // rebuild skill with stored absolute dmg (atk already folded)
            execute(AffixMolten.blast(moltenDmg, v), moltenOrigin, new Vector(1, 0, 0), null);
            clearMolten();
        }
    }

    private void clearMolten() {
        moltenOrigin = null;
        moltenWarnAt = 0;
        moltenBoomAt = 0;
        moltenDmg = 0;
    }

    /** D171: damage taken by a regen elite during its interrupt window. */
    void noteRegenHurt(Tracked t, double dmg) {
        if (t == null || !"regen".equals(t.affix) || !AffixRegen.counts(t.regenWindowEnd, dmg)) return;
        t.regenHurt += dmg;
    }

    private void splitAdds(Tracked t) {
        EmberRunMaps.Variety v = svc.maps().variety;
        EmberRunMaps.Role role = def.role("melee", ch);
        if (role == null || AffixSplit.count(v) <= 0) return;
        Location l = t.le.getLocation();
        EmberRunMaps.Pt pt = new EmberRunMaps.Pt(Math.floor(l.getX()), Math.floor(l.getY()), Math.floor(l.getZ()));
        int n = 0;
        for (int i = 0; i < AffixSplit.count(v); i++) {
            Tracked a = spawn(role, "melee", t.roomId, pt, t.leash);
            if (a == null) continue;
            a.splitAdd = true;
            AttributeInstance at = a.le.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (at != null) { at.setBaseValue(AffixSplit.addMaxHp(at.getBaseValue(), v)); a.le.setHealth(a.le.getMaxHealth()); }
            String old = a.le.getCustomName();
            a.le.setCustomName("§7[分身] §r" + (old == null ? a.le.getName() : old));
            n++;
        }
        svc.tellRun(s, "§6「分裂」§7精英裂成了 " + n + " 个分身！");
        if (n > 0) EmberClonePath.maybeAfterClone(s); // D582
    }

    // ------------------------------------------------------------------ D171 crystal / escort helpers

    private void placeCrystals(EmberRunMaps.Room r, int count) {
        clearCrystals();
        crystalsBroken = 0;
        if (r == null || r.points == null || r.points.isEmpty()) return;
        int n = Math.min(count, r.points.size());
        for (int i = 0; i < n; i++) {
            EmberRunMaps.Pt pt = r.points.get(i % r.points.size());
            // stand on the spawn point floor (y), prefer AIR above ground
            Block bl = w.getBlockAt((int) Math.floor(pt.x), (int) Math.floor(pt.y), (int) Math.floor(pt.z));
            if (bl.getType() != Material.AIR) bl = bl.getRelative(0, 1, 0);
            if (bl.getType() != Material.AIR && bl.getRelative(0, 1, 0).getType() == Material.AIR) bl = bl.getRelative(0, 1, 0);
            if (bl.getType() != Material.AIR) continue;
            bl.setType(Material.SEA_LANTERN);
            crystalBlocks.add(bl.getLocation());
        }
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s %s crystals placed=%d", s.runId, r.id, crystalBlocks.size()));
    }


    /** Count crystals already dug (creative/instant dig may skip BlockBreakEvent); always leave AIR. */
    private void pollCrystals() {
        if (crystalBlocks.isEmpty()) return;
        for (int i = crystalBlocks.size() - 1; i >= 0; i--) {
            Location loc = crystalBlocks.get(i);
            if (loc == null || loc.getWorld() == null) { crystalBlocks.remove(i); continue; }
            Block bl = loc.getBlock();
            if (bl.getType() == Material.SEA_LANTERN) continue;
            crystalBlocks.remove(i);
            crystalsBroken++;
            if (bl.getType() != Material.AIR) bl.setType(Material.AIR);
            svc.tellRun(s, "§b余烬晶碎裂 §7（" + crystalsBroken + "/" + svc.maps().variety.crystalCount + "）");
            svc.log().info("[P1 run] " + s.runId + " crystal " + crystalsBroken + "/" + svc.maps().variety.crystalCount + " (poll)");
        }
    }

    private void clearCrystals() {
        for (Location loc : crystalBlocks) {
            if (loc == null || loc.getWorld() == null) continue;
            Block bl = loc.getBlock();
            if (bl.getType() == Material.SEA_LANTERN) bl.setType(Material.AIR);
        }
        crystalBlocks.clear();
        crystalsBroken = 0;
    }

    /** @return true when this break was a variety crystal (caller suppresses drops; do not cancel the event). */
    boolean breakCrystal(Block b, Player p) {
        if (b == null || crystalBlocks.isEmpty()) return false;
        Location at = b.getLocation();
        for (int i = 0; i < crystalBlocks.size(); i++) {
            Location c = crystalBlocks.get(i);
            if (c.getBlockX() != at.getBlockX() || c.getBlockY() != at.getBlockY() || c.getBlockZ() != at.getBlockZ()) continue;
            crystalBlocks.remove(i);
            crystalsBroken++;
            svc.tellRun(s, "§b余烬晶碎裂 §7（" + crystalsBroken + "/" + svc.maps().variety.crystalCount + "）");
            svc.log().info("[P1 run] " + s.runId + " crystal " + crystalsBroken + "/" + svc.maps().variety.crystalCount
                    + (p == null ? "" : " by " + p.getName()));
            return true;
        }
        return false;
    }


    private void spawnEscort(EmberRunMaps.Room r, EmberRunMaps.Variety v) {
        EmberRunMaps.Role role = def.role("treasure", ch);
        if (role == null) role = def.role("melee", ch);
        if (role == null || r.points == null || r.points.isEmpty()) return;
        EmberRunMaps.Pt pt = r.points.get(0);
        Tracked t = spawn(role, "treasure", r.id, pt, r.trigger);
        if (t == null) return;
        t.varietyEscort = true;
        AttributeInstance a = t.le.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (a != null) {
            a.setBaseValue(Math.max(1.0, a.getBaseValue() * v.escortHp));
            t.le.setHealth(t.le.getMaxHealth());
        }
        // low threat: zero atk so it never hits (atk is final — zero interval via huge lastHit already set; rely on atk from role)
        String old = t.le.getCustomName();
        t.le.setCustomName("§e[护宝兔] §r" + (old == null ? "余烬小兔" : old));
        t.le.setCustomNameVisible(true);
        t.le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 600, 0, false, false), true);
        escortId = t.le.getUniqueId();
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s %s escort spawned hp=%.0f", s.runId, r.id, t.le.getMaxHealth()));
    }

    private void removeEscort() {
        if (escortId == null) return;
        Tracked t = mobs.get(escortId);
        if (t != null && t.varietyEscort) {
            mobs.remove(escortId);
            svc.unindex(escortId);
            if (t.le != null && !t.le.isDead()) t.le.remove();
        }
        escortId = null;
    }

    // ------------------------------------------------------------------ D179 hold / beacon / relay

    /** D179 unit helper → EmberRoomObjective (D239). */
    static boolean holdCounts(boolean inRadius, boolean onGround) {
        return EmberRoomObjective.holdCounts(inRadius, onGround);
    }

    /** D179 unit helper → EmberRoomObjective (D239). */
    static int relayAdvance(int nextIndex, int pressedIndex, int count) {
        return EmberRoomObjective.relayAdvance(nextIndex, pressedIndex, count);
    }

    /** D179 → EmberRoomObjective (D239). */
    static boolean beaconIsTreasureExtra() { return EmberRoomObjective.beaconIsTreasureExtra(); }
    static boolean beaconBlocksRoomClear() { return EmberRoomObjective.beaconBlocksRoomClear(); }

    private EmberRunMaps.Pt eventAnchorPt(EmberRunMaps.Room r) {
        if (r == null) return new EmberRunMaps.Pt(0, 64, 0);
        if (r.points != null && !r.points.isEmpty()) return r.points.get(0);
        if (r.trigger != null) {
            EmberRunMaps.Box b = r.trigger;
            return new EmberRunMaps.Pt((b.x0 + b.x1) / 2.0, b.y0, (b.z0 + b.z1) / 2.0);
        }
        return new EmberRunMaps.Pt(0, 64, 0);
    }

    private org.bukkit.entity.ArmorStand spawnEventMarker(Location loc, String name, boolean glowing) {
        org.bukkit.entity.ArmorStand st = w.spawn(loc, org.bukkit.entity.ArmorStand.class);
        st.setVisible(true);
        st.setGravity(false);
        st.setMarker(false);
        st.setSmall(true);
        st.setInvulnerable(true);
        st.setBasePlate(false);
        st.setArms(false);
        st.setCustomName(name);
        st.setCustomNameVisible(true);
        if (glowing) st.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 600, 0, false, false), true);
        return st;
    }

    private Location placeLanternAt(EmberRunMaps.Pt pt) {
        Block bl = w.getBlockAt((int) Math.floor(pt.x), (int) Math.floor(pt.y), (int) Math.floor(pt.z));
        if (bl.getType() != Material.AIR) bl = bl.getRelative(0, 1, 0);
        if (bl.getType() != Material.AIR && bl.getRelative(0, 1, 0).getType() == Material.AIR) bl = bl.getRelative(0, 1, 0);
        if (bl.getType() != Material.AIR) return null;
        bl.setType(Material.SEA_LANTERN);
        return bl.getLocation();
    }

    private void clearLantern(Location loc) {
        if (loc == null || loc.getWorld() == null) return;
        Block bl = loc.getBlock();
        if (bl.getType() == Material.SEA_LANTERN) bl.setType(Material.AIR);
    }

    private void placeHold(EmberRunMaps.Room r, EmberRunMaps.Variety v) {
        clearHold();
        holdAccum = 0;
        holdToldPct = 0;
        EmberRunMaps.Pt pt = eventAnchorPt(r);
        holdLantern = placeLanternAt(pt);
        Location loc = new Location(w, pt.x + 0.5, (holdLantern != null ? holdLantern.getY() + 1.0 : pt.y + 1.0), pt.z + 0.5);
        holdCenter = loc.clone();
        holdMarker = spawnEventMarker(loc, "§b[占点] §f站进圈内", true);
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s %s hold placed r=%.1f need=%.1f", s.runId, r.id, v.holdRadius, v.holdNeed));
    }

    private void clearHold() {
        if (holdMarker != null) { holdMarker.remove(); holdMarker = null; }
        clearLantern(holdLantern);
        holdLantern = null;
        holdCenter = null;
        holdAccum = 0;
        holdToldPct = 0;
    }

    private void placeBeacon(EmberRunMaps.Room r, EmberRunMaps.Variety v) {
        clearBeacon();
        EmberRunMaps.Pt pt = eventAnchorPt(r);
        EmberRunMaps.Role melee = def.role("melee", ch);
        double base = melee == null ? 40.0 : melee.hp;
        beaconMaxHp = Math.max(1.0, base * v.beaconHp);
        beaconHp = beaconMaxHp;
        beaconNextBite = System.currentTimeMillis() + (long) (v.beaconTick * 1000);
        beaconLantern = placeLanternAt(pt);
        Location loc = new Location(w, pt.x + 0.5, (beaconLantern != null ? beaconLantern.getY() + 1.0 : pt.y + 1.0), pt.z + 0.5);
        beaconLoc = loc.clone();
        beaconStand = spawnEventMarker(loc, "§e[余烬灯] §f护卫", true);
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s %s beacon placed hp=%.0f aggro=%.1f", s.runId, r.id, beaconMaxHp, v.beaconAggroR));
    }

    private void clearBeacon() {
        if (beaconStand != null) { beaconStand.remove(); beaconStand = null; }
        clearLantern(beaconLantern);
        beaconLantern = null;
        beaconLoc = null;
        beaconHp = 0;
        beaconMaxHp = 0;
        beaconNextBite = 0;
    }

    private void placeRelay(EmberRunMaps.Room r, EmberRunMaps.Variety v) {
        clearRelay();
        relayNext = 0;
        if (r == null || r.points == null || r.points.isEmpty()) return;
        int n = Math.min(v.relayCount, r.points.size());
        for (int i = 0; i < n; i++) {
            EmberRunMaps.Pt pt = r.points.get(i % r.points.size());
            Location lan = placeLanternAt(pt);
            relayLanterns.add(lan);
            Location loc = new Location(w, pt.x + 0.5, (lan != null ? lan.getY() + 1.0 : pt.y + 1.0), pt.z + 0.5);
            relayLocs.add(loc.clone());
            org.bukkit.entity.ArmorStand st = spawnEventMarker(loc, "§b[传火 " + (i + 1) + "/" + n + "] §7待点亮", i == 0);
            relayMarkers.add(st);
        }
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s %s relay placed count=%d", s.runId, r.id, relayMarkers.size()));
    }

    private void clearRelay() {
        for (org.bukkit.entity.ArmorStand st : relayMarkers) if (st != null) st.remove();
        relayMarkers.clear();
        for (Location loc : relayLanterns) clearLantern(loc);
        relayLanterns.clear();
        relayLocs.clear();
        relayNext = 0;
    }

    private void tickPack3Events(long now) {
        String kind = s.eventKind == null || s.eventKind.isEmpty() ? "timed" : s.eventKind;
        EmberRunMaps.Variety v = svc.maps().variety;
        double dt = lastEventTickMs > 0 ? Math.max(0.0, Math.min(1.0, (now - lastEventTickMs) / 1000.0)) : 0.25;
        lastEventTickMs = now;
        if ("hold".equals(kind) && holdCenter != null) {
            boolean present = false;
            for (Player p : participantsHere()) {
                Location l = p.getLocation();
                double dx = l.getX() - holdCenter.getX(), dz = l.getZ() - holdCenter.getZ();
                boolean in = dx * dx + dz * dz <= v.holdRadius * v.holdRadius;
                if (holdCounts(in, p.isOnGround())) { present = true; break; }
            }
            if (present) {
                holdAccum += dt; // multiplayer does NOT speed fill
                int pct = (int) Math.min(100, (holdAccum / v.holdNeed) * 100.0);
                int mile = (pct / 25) * 25;
                if (mile >= 25 && mile > holdToldPct) {
                    holdToldPct = mile;
                    svc.tellRun(s, "§b占点进度 §e" + mile + "% §7（"
                            + String.format(Locale.ROOT, "%.1f", Math.min(holdAccum, v.holdNeed))
                            + "/" + String.format(Locale.ROOT, "%.0f", v.holdNeed) + " 秒）");
                }
            }
            if (holdCenter != null) warnCircle(holdCenter.clone().add(0, 0.1, 0), v.holdRadius, Particle.VILLAGER_HAPPY);
        } else if ("beacon".equals(kind) && beaconLoc != null && beaconHp > 0) {
            boolean guarded = false;
            for (Player p : participantsHere()) {
                Location l = p.getLocation();
                double dx = l.getX() - beaconLoc.getX(), dy = l.getY() - beaconLoc.getY(), dz = l.getZ() - beaconLoc.getZ();
                if (dx * dx + dy * dy + dz * dz <= v.beaconAggroR * v.beaconAggroR) { guarded = true; break; }
            }
            if (!guarded && now >= beaconNextBite) {
                beaconNextBite = now + (long) (v.beaconTick * 1000);
                beaconHp -= v.beaconBite * beaconMaxHp;
                if (beaconStand != null) {
                    int pctHp = (int) Math.max(0, Math.round(beaconHp / beaconMaxHp * 100));
                    beaconStand.setCustomName("§e[余烬灯] §f" + pctHp + "%");
                }
                if (beaconHp <= 0) {
                    beaconHp = 0;
                    eventFailed = true;
                    clearBeacon();
                    svc.tellRun(s, "§c余烬灯碎了 §7· 这次没有额外核心（门照开）");
                    svc.log().info("[P1 run] " + s.runId + " beacon died");
                }
            } else if (guarded) {
                beaconNextBite = now + (long) (v.beaconTick * 1000);
            }
        } else if ("breach".equals(kind)) {
            tickBreach(now, dt, v); // D191
        } else if ("relay".equals(kind) && !relayLocs.isEmpty() && relayNext < relayLocs.size()) {
            for (Player p : participantsHere()) {
                Location l = p.getLocation();
                for (int i = 0; i < relayLocs.size(); i++) {
                    Location mloc = relayLocs.get(i);
                    double dx = l.getX() - mloc.getX(), dz = l.getZ() - mloc.getZ();
                    if (dx * dx + dz * dz > v.relayRadius * v.relayRadius) continue;
                    if (i != relayNext) {
                        if (i > relayNext) svc.tellRun(s, "§7先点亮上一处传火标记（现在是 §e" + (relayNext + 1) + "§7）");
                        break;
                    }
                    int was = relayNext;
                    relayNext = relayAdvance(relayNext, i, relayLocs.size());
                    if (relayNext > was) {
                        org.bukkit.entity.ArmorStand st = relayMarkers.get(i);
                        if (st != null) {
                            st.setCustomName("§a[传火 " + (i + 1) + "/" + relayLocs.size() + "] §f已点亮");
                            st.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 600, 0, false, false), true);
                        }
                        if (relayNext < relayMarkers.size()) {
                            org.bukkit.entity.ArmorStand nx = relayMarkers.get(relayNext);
                            if (nx != null) nx.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 600, 0, false, false), true);
                        }
                        svc.tellRun(s, "§b传火点亮 §e" + relayNext + "/" + relayLocs.size());
                        svc.log().info("[P1 run] " + s.runId + " relay " + relayNext + "/" + relayLocs.size());
                    }
                    break;
                }
            }
        }
    }

    // ------------------------------------------------------------------ D191 breach / chain / unscathed

    /** D191 → EmberRoomObjective (D239). */
    static double breachStep(double r, double dt, double shrink) { return EmberRoomObjective.breachStep(r, dt, shrink); }

    /** D191 → EmberRoomObjective (D239). */
    static double breachGrow(double r, double grow, double max) { return EmberRoomObjective.breachGrow(r, grow, max); }

    /** D191 → EmberRoomObjective (D239). */
    static boolean breachCollapsed(double r, double min) { return EmberRoomObjective.breachCollapsed(r, min); }

    /** D191 → EmberRoomObjective (D239). */
    static boolean breachInside(double dx, double dz, double r) { return EmberRoomObjective.breachInside(dx, dz, r); }

    /** D191 → EmberRoomObjective (D239). */
    static int chainNext(int streak, long lastMs, long nowMs, double gapSecs) {
        return EmberRoomObjective.chainNext(streak, lastMs, nowMs, gapSecs);
    }

    /** D191 → EmberRoomObjective (D239). */
    static int chainNeedFor(int need, int spawned) { return EmberRoomObjective.chainNeedFor(need, spawned); }

    /** D191 → EmberRoomObjective (D239). */
    static int unscathedBudget(int hits, int perMember, int party) { return EmberRoomObjective.unscathedBudget(hits, perMember, party); }

    /** D191 → EmberRoomObjective (D239). */
    static boolean unscathedOk(int taken, int budget) { return EmberRoomObjective.unscathedOk(taken, budget); }

    private boolean eventLive(String kind) {
        return eventStart > 0 && !eventFailed && !s.eventDone && activeRoom != null && activeRoom.equals(s.eventRoom)
                && kind.equals(s.eventKind);
    }

    private void placeBreach(EmberRunMaps.Room r, EmberRunMaps.Variety v) {
        clearBreach();
        EmberRunMaps.Pt pt = eventAnchorPt(r);
        breachLantern = placeLanternAt(pt);
        Location loc = new Location(w, pt.x + 0.5, (breachLantern != null ? breachLantern.getY() + 1.0 : pt.y + 1.0), pt.z + 0.5);
        breachCenter = loc.clone();
        breachR = v.breachRadius;
        breachMarker = spawnEventMarker(loc, breachName(breachR), true);
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s %s breach placed r=%.1f min=%.1f shrink=%.2f grow=%.1f",
                s.runId, r.id, breachR, v.breachMin, v.breachShrink, v.breachGrow));
    }

    private static String breachName(double r) {
        return "§5[裂隙] §f半径 " + String.format(Locale.ROOT, "%.1f", r);
    }

    private void clearBreach() {
        if (breachMarker != null) { breachMarker.remove(); breachMarker = null; }
        clearLantern(breachLantern);
        breachLantern = null;
        breachCenter = null;
        breachR = 0;
    }

    private void chainReset(int spawned, EmberRunMaps.Variety v) {
        chainNeedNow = chainNeedFor(v.chainNeed, spawned);
        chainStreak = 0;
        chainBest = 0;
        chainLastMs = 0;
    }

    private void unscathedReset(int party, EmberRunMaps.Variety v) {
        unscathedTaken = 0;
        unscathedBudgetNow = unscathedBudget(v.unscathedHits, v.unscathedPerMember, Math.max(1, party));
    }

    /** D191: a tracked room mob of the live event room died (escort / boss / extra never get here). */
    private void noteEventKill(Tracked t) {
        if (t == null || s.eventRoom == null || !s.eventRoom.equals(t.roomId)) return;
        EmberRunMaps.Variety v = svc.maps().variety;
        if (eventLive("breach") && breachCenter != null) {
            Location l = t.le.getLocation();
            if (breachInside(l.getX() - breachCenter.getX(), l.getZ() - breachCenter.getZ(), breachR)) {
                breachR = breachGrow(breachR, v.breachGrow, v.breachMax);
                if (breachMarker != null) breachMarker.setCustomName(breachName(breachR));
            }
        } else if (eventLive("chain") && chainNeedNow > 0 && chainBest < chainNeedNow) {
            long now = System.currentTimeMillis();
            chainStreak = chainNext(chainStreak, chainLastMs, now, v.chainGap);
            chainLastMs = now;
            chainBest = Math.max(chainBest, chainStreak);
            if (chainBest >= chainNeedNow) {
                svc.tellRun(s, "§b连斩 ×" + chainStreak + " §a达成 §7· 清完这个房间就算完成");
                svc.log().info("[P1 run] " + s.runId + " chain " + chainStreak + "/" + chainNeedNow);
            } else if (chainStreak >= 2) {
                svc.tellRun(s, "§b连斩 §e×" + chainStreak + " §7/ " + chainNeedNow);
            }
        }
    }

    /** D191 无伤: one landed run-mob hit on a committed member while the event room is live. */
    void noteHitTaken(Player p) {
        if (!eventLive("unscathed") || unscathedBudgetNow < 0) return;
        unscathedTaken++;
        if (!unscathedOk(unscathedTaken, unscathedBudgetNow)) {
            eventFailed = true;
            svc.tellRun(s, "§c无伤失败 §7· 全队被打中 " + unscathedTaken + " 次（限 " + unscathedBudgetNow + "）· 这次没有额外核心（门照开）");
            svc.log().info("[P1 run] " + s.runId + " unscathed failed " + unscathedTaken + "/" + unscathedBudgetNow);
        } else if (unscathedTaken == unscathedBudgetNow || unscathedTaken * 2 == unscathedBudgetNow) {
            svc.tellRun(s, "§e无伤 §7· 已被打中 " + unscathedTaken + "/" + unscathedBudgetNow + " 次");
        }
    }

    /** D191 裂隙: shrink the rift; collapse = fail (door still opens with the room). */
    private void tickBreach(long now, double dt, EmberRunMaps.Variety v) {
        if (breachCenter == null) return;
        breachR = breachStep(breachR, dt, v.breachShrink);
        if (breachCollapsed(breachR, v.breachMin)) {
            eventFailed = true;
            clearBreach();
            svc.tellRun(s, "§c裂隙合上了 §7· 这次没有额外核心（门照开）");
            svc.log().info("[P1 run] " + s.runId + " breach collapsed");
            return;
        }
        if (breachMarker != null) breachMarker.setCustomName(breachName(breachR));
        warnCircle(breachCenter.clone().add(0, 0.1, 0), breachR, Particle.SPELL_WITCH);
    }

    int chainBestForTest() { return chainBest; }
    int unscathedTakenForTest() { return unscathedTaken; }
    double breachRForTest() { return breachR; }

        private void clearFrostSlow() {
        EmberRunMaps.Variety v = svc.maps().variety;
        for (Player p : participantsHere()) {
            for (PotionEffect cur : p.getActivePotionEffects()) {
                if (cur.getType().equals(PotionEffectType.SLOW) && cur.getAmplifier() == v.frostAmplifier
                        && cur.getDuration() <= (int) (v.frostTick * 20) + 40) {
                    p.removePotionEffect(PotionEffectType.SLOW);
                    break;
                }
            }
        }
    }

    /** Static test helper: crystal list empty after clear. */
    List<Location> crystalBlocksForTest() { return crystalBlocks; }
    double holdAccumForTest() { return holdAccum; }
    int relayNextForTest() { return relayNext; }
    double beaconHpForTest() { return beaconHp; }
    List<Location> relayLocsForTest() { return relayLocs; }

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
        // D193: 余烬连战 keeps its own tuned move table — the break channel never comes due in a rush
        for (int i = 0; i < nextAt.length; i++) if (def.rush && b.skills.get(i).breakHp > 0) nextAt[i] = Long.MAX_VALUE / 4;
        gateOpened = new boolean[nextAt.length];
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
                if (t != null) {
                    extraMob = t.le.getUniqueId();
                    // D182: attach the map's fixed light move (dmg = elite.atk × yml multiplier)
                    if (s.extra == EmberRunRules.Extra.ELITE && svc.maps() != null) {
                        EmberRunMaps.EliteTwists.Twist tw = svc.maps().eliteTwists.forMap(def.key);
                        if (tw != null) {
                            // D504: party-leader twist path picks primary / alt / rotate
                            PlayerData ld = s.leader == null ? null : svc.dataOf(s.leader);
                            int tpath = EmberTwistPath.get(ld);
                            EmberRunMaps.Skill[] sk = EmberTwistPath.applySkills(tw, t.atk, tpath);
                            t.twist = sk[0];
                            t.twistAlt = sk[1];
                            t.twistUseAlt = false;
                            if (EmberTwistPath.valid(tpath)) {
                                svc.log().info("[P1 run] " + s.runId + " twistpath " + EmberTwistPath.key(tpath)
                                        + " elite " + (t.twistAlt != null ? "rotate" : "single"));
                            }
                            t.nextCast = System.currentTimeMillis() + (long) (svc.maps().eliteTwists.openDelay * 1000);
                        }
                    }
                }
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
            // D510: party-leader break path clamps between-boss rest (never above table)
            PlayerData ld = s.leader == null ? null : svc.dataOf(s.leader);
            int bpath = EmberBreakPath.get(ld);
            double br = EmberBreakPath.effectiveBreak(bpath, def.rushBreak);
            bossAt = System.currentTimeMillis() + (long) (br * 1000);
            if (EmberBreakPath.valid(bpath) && br != def.rushBreak) {
                svc.log().info("[P1 run] " + s.runId + " breakpath " + EmberBreakPath.key(bpath)
                        + " break " + def.rushBreak + "→" + br);
            }
            svc.onRushStage(this, done, was, bossDef(), secs, br);
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
        if (t.varietyEscort) { // D171: rabbit died → event fail; door still opens with the room
            escortId = null;
            if (!eventFailed && !s.eventDone && eventStart > 0) {
                eventFailed = true;
                svc.tellRun(s, "§c护宝兔倒下了 §7· 这次没有额外核心（门照开）");
                svc.log().info("[P1 run] " + s.runId + " escort died");
            }
            return false;
        }
        noteEventKill(t); // D191 裂隙 / 连斩
        if (t.affix != null) { // D138/D171/D181
            if ("frost".equals(t.affix)) clearFrostSlow();
            if ("split".equals(t.affix)) splitAdds(t);
            if ("molten".equals(t.affix) && !t.splitAdd) scheduleMolten(t);
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
        doorPending = null;
        doorOpenAt = 0;
        finished = true;
        clearCrystals();
        clearHold();
        clearBeacon();
        clearRelay();
        clearBreach();
        clearFrostSlow();
        clearMolten();
        cleanupMobs();
        for (String k : new ArrayList<String>(holos.keySet())) if (!"exit".equals(k)) unholo(k); // exit stays until the instance closes
        if (chest != null && chest.getBlock().getType() == Material.ENDER_CHEST) chest.getBlock().setType(Material.AIR);
        chest = null;
        escortId = null;
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
        // D173: half-HP team line when any top-level or follow skill is gated at below≤1.0 (Pack 2 + existing 踏地/横扫)
        if (!phaseTold && ratio < 0.5 && hasBelowPressure(b)) {
            phaseTold = true;
            svc.tellRun(s, halfHpTellLine()); // D303 raid / D304 story
            svc.log().info(String.format(Locale.ROOT, "[P1 run] %s boss half-HP phase at %.0f%%", s.runId, ratio * 100));
            svc.onBossPhase(s, RevivePoint.HALF_HP); // D106 raid revive point
        }
        // §13 adds: once at 50 % after a 1 s warning
        if (b.adds != null && !addsDone && ratio <= b.adds.atHp) {
            addsDone = true;
            addsAt = now + (long) (b.adds.warn * 1000);
            String addCue = addsTellLine(b);
            // D304: skip duplicate when phase already told the same adds-themed line this tick
            if (!phaseTold || !addCue.equals(halfHpTellLine())) {
                svc.tellRun(s, addCue);
            }
            svc.onBossPhase(s, RevivePoint.ADDS_PHASE); // D106 raid revive point
        }
        if (addsAt > 0) {
            if (now >= addsAt) {
                addsAt = 0;
                for (EmberRunMaps.Pt p : b.adds.points) spawn(def.role(b.adds.role, ch), "add", "boss", safe(p, b.at), b.area);
                EmberWavePath.maybeAfterWave(s); // D585
            } else {
                for (EmberRunMaps.Pt p : b.adds.points) warnCircle(new Location(w, p.x + 0.5, p.y + 0.1, p.z + 0.5), 1.0, Particle.SPELL_WITCH);
            }
        }
        if (pending != null) {
            drawShape(pending, lockOrigin, lockDir);
            if (broken(breakNeed, breakDone) && !le.isDead()) { breakCast(pending, now, le, b); return; }
            if (breakNeed > 0 && now >= breakBarAt) { breakBarAt = now + 250; breakBar(); }
            if (now >= pendingAt) {
                breakNeed = 0;
                int landed = execute(pending, lockOrigin, lockDir, le);
                EmberRunMaps.Skill done = pending;
                pending = null;
                recoverUntil = now + (long) ((Double.isNaN(done.recover) ? b.recover : done.recover) * 1000);
                if (done.follow != null && ratio < done.follow.below) {
                    follow = done.follow;
                    followStart = now + (long) (done.follow.delay * 1000);
                }
                if (pendingCrash && done.wallStun > 0 && !le.isDead()) wallStun(done, now, le);
                else if (whiffs(pendingArmed, landed, done) && !le.isDead()) whiffStun(done, now, le);
                pendingCrash = false;
                pendingArmed = false;
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
        // D173: half-HP tell already fired above when ratio < 0.5; keep fallback if a below-gated skill comes due first frame
        if (!phaseTold && due >= 0 && b.skills.get(due).below <= 1.0) {
            phaseTold = true;
            svc.tellRun(s, halfHpTellLine()); // D303 raid / D304 story
            svc.log().info(String.format(Locale.ROOT, "[P1 run] %s boss phase 2 at %.0f%% (%s)", s.runId, ratio * 100, b.skills.get(due).name));
            svc.onBossPhase(s, RevivePoint.HALF_HP); // D106 raid revive point
        }
        double lrh = svc.maps().raidLastReviveHp; // D118: armed at <= lrh with someone down, once per raid
        if (lrh > 0 && !lastRevDone && svc.isRaid(s) && lastPhase() && ratio <= lrh) {
            if (lastRevAt == 0L && !s.died.isEmpty()) {
                lastRevAt = now + (long) (svc.maps().raidReviveDelay * 1000);
                svc.tellRun(s, "§e首领只剩 " + Math.round(lrh * 100) + "% 生命§7：倒下的队友约 " + Math.round(svc.maps().raidReviveDelay) + " 秒后再复活一次（本局仅此一次）");
            }
            if (lastRevAt > 0 && now >= lastRevAt) {
                lastRevDone = true;
                svc.onBossPhase(s, RevivePoint.LAST_PHASE);
            }
        }
        if (due >= 0) {
            EmberRunMaps.Skill sk = b.skills.get(due);
            startWarn(sk, now, le);
            // D194: the first cast of a phase-gated skill (below ≤ 1.0) re-anchors at now + every; the spawn grid it waited
            // on is stale by then, so the old grid let the 2nd cast follow the 1st after only a few seconds (smoke 09:44)
            boolean first = sk.below <= 1.0 && due < gateOpened.length && !gateOpened[due];
            if (first) gateOpened[due] = true;
            nextAt[due] = gatedNext(nextAt[due], (long) (sk.every * 1000), now, first);
        }
    }

    /**
     * §10.I → EmberBossMove (D239). @return index or −1
     */
    static int dueSkill(long[] nextAt, long now) {
        return EmberBossMove.dueSkill(nextAt, now);
    }

    /** P2-6 → EmberBossMove (D239). */
    static int dueSkill(long[] nextAt, long now, double[] below, double ratio) {
        return EmberBossMove.dueSkill(nextAt, now, below, ratio);
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

    /** D173 / P2-6 → EmberBossMove (D239). */
    static boolean hasBelowPressure(EmberRunMaps.Boss b) {
        return EmberBossMove.hasBelowPressure(b);
    }

    /** D194 → EmberBossMove (D239). */
    static long gatedNext(long at, long every, long now, boolean firstGated) {
        return EmberBossMove.gatedNext(at, every, now, firstGated);
    }

    /** → EmberBossMove (D239). */
    static long nextDue(long at, long every, long now) {
        return EmberBossMove.nextDue(at, every, now);
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
        pendingArmed = false; // D192: a shifted band is never a whiff window
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

    /**
     * D304 W1b: Q01–Q07 half-HP honest ≤1 line by real hooks (adds / break / below).
     * Raids keep {@link EmberRaidService#halfHpCue}; R03 share stays on warn line.
     */
    static String storyHalfHpCue(String mapKey) {
        if ("q03".equals(mapKey)) return "§e半血增援 §7· 两侧加怪";
        if ("q06".equals(mapKey)) return "§e半血破招 §7· 霜潮汲取可打断";
        if ("q07".equals(mapKey)) return "§e半血破招 §7· 炉心聚爆可打断";
        return "§e半血 · 招式变强"; // q01 / q02 / q04 / q05 + default
    }

    /** D304 / D303: resolve half-HP chat line for current run map. */
    private String halfHpTellLine() {
        if (svc.isRaid(s)) {
            String r = EmberRaidService.halfHpCue(s.mapKey);
            return r != null ? r : "§e首领进入半血 · 招式变强，盯紧预警";
        }
        return storyHalfHpCue(s.mapKey);
    }

    /** D304: adds-phase line (raid cue / story cue / sworn-seal fallback). */
    private String addsTellLine(EmberRunMaps.Boss b) {
        if (svc.isRaid(s)) {
            String r = EmberRaidService.halfHpCue(s.mapKey);
            if (r != null) return r;
        } else {
            String st = storyHalfHpCue(s.mapKey);
            if (st != null && st.contains("增援")) return st;
        }
        return "§c" + b.name + " §7高举誓印——两侧将出现援兵！";
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
        pendingCrash = false;
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
            pendingCrash = EmberCounterplay.armWallCrash(sk.wallStun, crashesIntoWall(o, dir, run, sk.length));
        }
        lockOrigin = "player".equals(sk.target) && target != null ? target.getLocation().clone() : o.clone();
        lockDir = dir;
        pending = skillFor(sk);
        if ("charge".equals(sk.type)) pending = pending.withLength(run);
        pendingAt = now + (long) (sk.warn * 1000);
        pendingArmed = EmberCounterplay.armWhiff(sk.whiffStun, anyoneInside(pending, lockOrigin, lockDir)); // D192: only a real dodge counts
        breakNeed = EmberCounterplay.armBreakNeed(le.getMaxHealth(), sk.breakHp); // D193 破招
        breakDone = 0;
        breakBarAt = now;
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
        who += EmberCounterplay.wallHint(sk.wallStun, fmt(sk.wallStun));
        who += EmberCounterplay.whiffHint(sk.whiffStun, pendingArmed, fmt(sk.whiffStun));
        who += EmberCounterplay.breakHint(breakNeed, sk.breakStun, fmt(sk.breakStun));
        svc.tellRun(s, "§c" + bossDef().name + " §e蓄力「" + sk.name + "」§7— " + who + "（" + sk.warn + " 秒）");
        // D304 W1a: cast-start ActionBar each cast (chat kept); charge 0-cell already returned above
        flashActionBar(EmberCounterplay.castStartBar(sk.name, sk.type, sk.warn, sk.wallStun, sk.whiffStun, breakNeed, sk.share));
        if (sk.share) EmberSoakPath.maybeAfterSoak(s); // D584
        if (breakNeed > 0) EmberPressPath.maybeAfterPress(s); // D581
    }

    /** D193 破招: a channel is armed (need > 0) and the party's damage since its warning began reached the need. */
    static boolean broken(double need, double done) {
        return EmberCounterplay.broken(need, done);
    }

    /** D193: player damage on this director's boss while a break channel winds up (Service MONITOR hook, finalDamage). */
    void noteBossHurt(org.bukkit.entity.Entity e, double dmg) {
        if (boss == null || e != boss.le || pending == null || breakNeed <= 0 || dmg <= 0) return;
        breakDone += dmg;
    }

    /** D283: first-of-kind ActionBar + subtitle (title empty); chat tellRun stays every time. */
    private void flashCounterplayTip(String msg) {
        if (msg == null || msg.isEmpty()) return;
        for (Player p : participantsHere()) {
            try { p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(msg)); } catch (Throwable ignored) { }
            try { p.sendTitle("", msg, 5, 35, 10); } catch (Throwable ignored) { }
        }
    }

    /** D296 W1c: ActionBar only (short event name). */
    private void flashActionBar(String msg) {
        if (msg == null || msg.isEmpty()) return;
        for (Player p : participantsHere()) {
            try { p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(msg)); } catch (Throwable ignored) { }
        }
    }

    /**
     * D448 / D283: first success of each kind → ActionBar+subtitle tip; later successes → ActionBar only
     * (debounce ≥ {@link EmberCounterplay#SUCCESS_FLASH_DEBOUNCE_MS}). Always notes the counterplay hit.
     */
    private void flashCounterplaySuccess(CounterplayKind k) {
        boolean first = s.noteCounterplay(k);
        long now = System.currentTimeMillis();
        if (first) {
            flashCounterplayTip(EmberCounterplay.firstFlash(k));
            lastCounterplayFlashAt = now;
        } else if (EmberCounterplay.shouldFlashSuccess(lastCounterplayFlashAt, now)) {
            flashActionBar(EmberCounterplay.successFlash(k));
            lastCounterplayFlashAt = now;
        }
    }

    private void breakBar() {
        int pct = (int) Math.min(100, Math.floor(100.0 * breakDone / breakNeed));
        String bar = "§b破招 §f" + pct + "% §7（" + Math.round(breakDone) + " / " + Math.round(breakNeed) + "）";
        for (Player p : participantsHere()) {
            try { p.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR, net.md_5.bungee.api.chat.TextComponent.fromLegacyText(bar)); } catch (Throwable ignored) { }
        }
    }

    /** D193 破招: the channel never lands; the boss staggers breakStun s (same stunUntil as D188 / D192). */
    private void breakCast(EmberRunMaps.Skill sk, long now, LivingEntity le, EmberRunMaps.Boss b) {
        double need = breakNeed, done = breakDone;
        pending = null;
        breakNeed = 0;
        pendingCrash = false;
        pendingArmed = false;
        recoverUntil = now + (long) ((Double.isNaN(sk.recover) ? b.recover : sk.recover) * 1000);
        long ms = EmberCounterplay.stunMs(sk.breakStun);
        le.removePotionEffect(PotionEffectType.SLOW); // the wind-up root ends with the cast
        if (ms > 0) {
            long[] bb = EmberCounterplay.applyStunBounds(now, ms, recoverUntil, followStart);
            stunUntil = bb[0]; recoverUntil = bb[1]; if (follow != null) followStart = bb[2];
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, EmberCounterplay.stunPotionTicks(sk.breakStun), 10, false, false), true);
        }
        Location at = le.getLocation();
        w.spawnParticle(Particle.CRIT_MAGIC, at.clone().add(0, 2.2, 0), 16, 0.5, 0.3, 0.5, 0.1);
        w.playSound(at, Sound.BLOCK_GLASS_BREAK, 1.0f, 0.8f);
        svc.tellRun(s, "§a破招！§c" + bossDef().name + " §7的「" + sk.name + "」被打断"
                + (ms > 0 ? " §e踉跄 " + fmt(sk.breakStun) + " 秒 §7· 趁现在输出" : ""));
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s break %s %.0f/%.0f stun %.1fs", s.runId, sk.name, done, need, sk.breakStun));
        EmberBurstPath.maybeAfterStagger(s); // D563
        flashCounterplaySuccess(CounterplayKind.BREAK);
    }

    /** D192 落空破绽: armed at warn start (someone inside) and the hit landed on nobody → the boss staggers. */
    static boolean whiffs(boolean armed, int landed, EmberRunMaps.Skill done) {
        return EmberCounterplay.whiffs(armed, landed, done);
    }

    /** D192: a living, non-spectator participant stands inside {@code sk} as drawn from {@code o} / {@code dir} now. */
    private boolean anyoneInside(EmberRunMaps.Skill sk, Location o, Vector dir) {
        if (o == null || dir == null) return false;
        for (Player p : participantsHere()) {
            if (p.isDead() || p.getGameMode() == org.bukkit.GameMode.SPECTATOR) continue;
            if (inShape(sk, o, dir, p.getLocation())) return true;
        }
        return false;
    }

    /** D192 落空破绽: same stagger as the wall stun (rooted, no skill, no melee; a queued follow-up waits), shorter. */
    private void whiffStun(EmberRunMaps.Skill done, long now, LivingEntity le) {
        long ms = EmberCounterplay.stunMs(done.whiffStun);
        long[] b = EmberCounterplay.applyStunBounds(now, ms, recoverUntil, followStart);
        stunUntil = b[0]; recoverUntil = b[1]; if (follow != null) followStart = b[2];
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, EmberCounterplay.stunPotionTicks(done.whiffStun), 10, false, false), true);
        Location at = le.getLocation();
        w.spawnParticle(Particle.CRIT, at.clone().add(0, 2.2, 0), 10, 0.4, 0.2, 0.4, 0.05);
        w.playSound(at, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 0.6f);
        svc.tellRun(s, "§a落空！§c" + bossDef().name + " §e踉跄 " + fmt(done.whiffStun) + " 秒 §7· 破绽，趁现在输出");
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s whiff stun %s %.1fs", s.runId, done.name, done.whiffStun));
        EmberBurstPath.maybeAfterStagger(s); // D563
        flashCounterplaySuccess(CounterplayKind.WHIFF);
    }

    /** D188 撞墙破绽: rooted, no skill and no melee for {@code done.wallStun} s; a queued follow-up waits until it ends. */
    private void wallStun(EmberRunMaps.Skill done, long now, LivingEntity le) {
        long ms = EmberCounterplay.stunMs(done.wallStun);
        long[] b = EmberCounterplay.applyStunBounds(now, ms, recoverUntil, followStart);
        stunUntil = b[0]; recoverUntil = b[1]; if (follow != null) followStart = b[2];
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, EmberCounterplay.stunPotionTicks(done.wallStun), 10, false, false), true);
        Location at = le.getLocation();
        w.spawnParticle(Particle.CRIT, at.clone().add(0, 2.2, 0), 16, 0.4, 0.2, 0.4, 0.05);
        w.playSound(at, Sound.BLOCK_ANVIL_LAND, 0.8f, 0.7f);
        svc.tellRun(s, "§a撞墙！§c" + bossDef().name + " §e眩晕 " + fmt(done.wallStun) + " 秒 §7· 破绽，趁现在输出");
        svc.log().info(String.format(Locale.ROOT, "[P1 run] %s wall stun %s %.1fs", s.runId, done.name, done.wallStun));
        EmberBurstPath.maybeAfterStagger(s); // D563
        flashCounterplaySuccess(CounterplayKind.WALL);
    }

    /** D188: this director's boss is in its wall-crash stun right now (its melee is cancelled). */
    boolean bossStunned(org.bukkit.entity.Entity e) {
        return boss != null && e != null && e == boss.le && System.currentTimeMillis() < stunUntil;
    }

    /** D188: the charge strip stopped short of {@code max} because a solid block stands at feet / head height (a wall). */
    private boolean crashesIntoWall(final Location o, Vector dir, double run, double max) {
        return crashGrid((x, z) -> {
            org.bukkit.block.Block feet = new Location(w, x, o.getY(), z).getBlock();
            return feet.getType().isSolid() || feet.getRelative(0, 1, 0).getType().isSolid();
        }, o.getX(), o.getZ(), dir.getX(), dir.getZ(), run, max, BOSS_HALF_WIDTH);
    }

    /**
     * D188: the step right after the clipped strip end (same centre + box-edge probes as {@link #clearRunGrid}) hits a
     * wall. A strip that ran its full length, or stopped at a ledge / the invisible boss-area edge, is no crash.
     */
    static boolean crashGrid(GroundTest wall, double ox, double oz, double dx, double dz, double run, double max, double half) {
        return EmberCounterplay.crashGrid(wall, ox, oz, dx, dz, run, max, half);
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


    /** D182/D185: Extra.ELITE light move(s) — same shapes as boss telegraphs; src != boss so charge does not dash.
     *  When Pack 2 alt is present, primary and alt alternate after each successful cast. */
    private void twistTick(Tracked t, long now) {
        EmberRunMaps.Skill sk = (t.twistUseAlt && t.twistAlt != null) ? t.twistAlt : t.twist;
        if (sk == null) return;
        if (t.castAt > 0) {
            EmberRunMaps.Skill drawn = twistDrawn(t, sk);
            drawShape(drawn, t.castOrigin, t.castDir == null ? new Vector(1, 0, 0) : t.castDir);
            if (now >= t.castAt) {
                execute(drawn, t.castOrigin, t.castDir == null ? new Vector(1, 0, 0) : t.castDir, t.le);
                t.castAt = 0;
                t.nextCast = now + (long) (sk.every * 1000);
                if (t.twistAlt != null) t.twistUseAlt = !t.twistUseAlt; // D185 alternate
            }
            return;
        }
        if (now < t.nextCast) return;
        Location o = t.le.getLocation().clone();
        Player target = "player".equals(sk.target) ? pickTarget(o, 16) : nearest(o, 16);
        if (target == null) return;
        Vector dir = target.getLocation().toVector().subtract(o.toVector());
        dir.setY(0);
        if (dir.lengthSquared() < 1e-6) dir = new Vector(0, 0, 1);
        dir.normalize();
        if ("charge".equals(sk.type) && clearRun(o, dir, sk.length) < CHARGE_MIN) return; // no room — skip, keep CD
        // circle locked on the player's feet (Q05 落尘); others aim from the elite
        t.castOrigin = "player".equals(sk.target) ? target.getLocation().clone() : o;
        t.castDir = dir;
        t.castAt = now + (long) (sk.warn * 1000);
        Location face = o.clone();
        face.setDirection(dir);
        t.le.teleport(face);
        t.le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) (sk.warn * 20) + 6, 10, false, false), true);
    }

    /** charge strip clipped to clearRun (B2.165); other shapes unchanged */
    private EmberRunMaps.Skill twistDrawn(Tracked t, EmberRunMaps.Skill sk) {
        if (!"charge".equals(sk.type) || t.castOrigin == null || t.castDir == null) return sk;
        double run = clearRun(t.castOrigin, t.castDir, sk.length);
        return run < CHARGE_MIN ? sk : sk.withLength(run);
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

    /** @return how many players the shape landed on (D192 落空破绽 reads 0 = everyone dodged) */
    private int execute(EmberRunMaps.Skill sk, Location o, Vector dir, LivingEntity src) {
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
            // D301: open 余烬招架 window only for players the boss tele actually hit (non-share)
            if (bossSkill && !sk.share && src != null && !p.isDead()) {
                EmberParry parry = EmberParry.get();
                if (parry != null) parry.openWindow(p, src);
            }
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
        return hit;
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

    /** Shapes never exceed the drawn warning (book: 实际范围不超过预警). D241: geometry in {@link EmberShape#inShape}. */
    static boolean inShape(EmberRunMaps.Skill sk, Location o, Vector dir, Location p) { return EmberShape.inShape(sk, o, dir, p); }

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
    static final double BOSS_HALF_WIDTH = EmberCounterplay.BOSS_HALF_WIDTH;

    /** D236: alias of {@link EmberCounterplay.GroundTest} so existing shape tests keep compiling. */
    interface GroundTest extends EmberCounterplay.GroundTest { }

    /**
     * B2.165: how far the boss can charge along (dx, dz): every 0.25 step the centre AND the box edges (± half
     * sideways, + half ahead) must be standable ground inside the boss area. The centre-only check let the Q07 boss
     * end a charge with half its box in the hall wall (z=40); a mob inside a block climbs it when it jumps and walked
     * on the roof (y70) for the rest of the fight.
     */
    static double clearRunGrid(GroundTest g, double ox, double oz, double dx, double dz, double max, double half) {
        return EmberCounterplay.clearRunGrid(g, ox, oz, dx, dz, max, half);
    }

    /** a charge shorter than this is not worth a telegraph (book: 冲撞 = up to 8 blocks) */
    static final double CHARGE_MIN = EmberCounterplay.CHARGE_MIN;

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

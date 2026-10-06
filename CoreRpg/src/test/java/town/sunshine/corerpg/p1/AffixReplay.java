package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * D241 fixed-seed damage replay for the 12 affixes. A scripted fight (elite + 1–3 moving players + two tether partners
 * + party damage + wall clear-runs + an outside long Slowness, all drawn up front from the seed, so the script never
 * depends on the code under test) is ticked every 50 ms through the affix state machine of {@code EmberRunDirector.affixTick}
 * / promote / scheduleMolten / moltenTick / splitAdds / noteRegenHurt, asking an {@link AffixPath} for every number and
 * predicate. Output: one line per promotion, hit (with the full damage packet), root, heal / interrupt, slow, link,
 * split add and death blast — the same facts {@code EmberDamageTrace} would log, in a diff-able form.
 */
final class AffixReplay {

    static final long T0 = 1_759_700_000_000L;
    static final long DT = 50L;
    static final int TICKS = 640;
    static final int DEATH_TICK = 520;

    /** everything random about one fight, generated from the seed only */
    static final class Script {
        int n, ticks = TICKS;
        double atk, maxHp, meleeBase, eliteY;
        double[][] px, py, pz;
        boolean[][] ground;
        double[] ex, ez;
        double[][] mx, mz;
        int[] mobDeath = new int[2];
        double[] party, clear;
        int[][] outsideSlowStart;

        static Script of(long seed) {
            Random r = new Random(seed);
            Script s = new Script();
            s.n = 1 + r.nextInt(3);
            s.atk = 5 + r.nextDouble() * 55;
            s.maxHp = 40 + r.nextDouble() * 360;
            s.meleeBase = 10 + r.nextDouble() * 60;
            s.eliteY = r.nextInt(3) == 0 ? 64.5 : 64.0;
            s.px = new double[s.n][TICKS]; s.py = new double[s.n][TICKS]; s.pz = new double[s.n][TICKS];
            s.ground = new boolean[s.n][TICKS];
            s.ex = new double[TICKS]; s.ez = new double[TICKS];
            s.mx = new double[2][TICKS]; s.mz = new double[2][TICKS];
            s.party = new double[TICKS]; s.clear = new double[TICKS];
            s.outsideSlowStart = new int[s.n][];
            double x = 0.5, z = 0.5, intensity = r.nextDouble() * r.nextDouble(); // party DPS varies: some regen casts heal
            for (int t = 0; t < TICKS; t++) {
                x = Math.max(-3, Math.min(3, x + (r.nextDouble() - 0.5) * 0.1));
                z = Math.max(-3, Math.min(3, z + (r.nextDouble() - 0.5) * 0.1));
                s.ex[t] = x; s.ez[t] = z;
                s.party[t] = r.nextDouble() < 0.35 * intensity ? r.nextDouble() * s.maxHp * 0.03 : 0.0;
                s.clear[t] = r.nextDouble() * 9.0;
            }
            for (int i = 0; i < s.n; i++) {
                double a = r.nextDouble() * 2 * Math.PI, rad = 0.3 + r.nextDouble() * 9;
                double qx = Math.cos(a) * rad, qz = Math.sin(a) * rad, h = r.nextDouble() * 2 * Math.PI, sp = r.nextDouble() * 0.3;
                int jump = 0, ledge = 0;
                for (int t = 0; t < TICKS; t++) {
                    if (r.nextDouble() < 0.08) { h = r.nextDouble() * 2 * Math.PI; sp = r.nextDouble() * 0.3; }
                    qx += Math.cos(h) * sp; qz += Math.sin(h) * sp;
                    double dx = qx - s.ex[t], dz = qz - s.ez[t], d = Math.sqrt(dx * dx + dz * dz);
                    if (d > 12) { qx = s.ex[t] + dx * 12 / d; qz = s.ez[t] + dz * 12 / d; h += Math.PI; }
                    if (jump == 0 && ledge == 0 && r.nextDouble() < 0.012) jump = 8;
                    if (jump == 0 && ledge == 0 && r.nextDouble() < 0.003) ledge = 20;
                    double y = 64.0;
                    boolean g = true;
                    if (jump > 0) { y = 64.0 + 1.1 * Math.sin(Math.PI * (8 - jump) / 8.0) + 0.05; g = false; jump--; }
                    else if (ledge > 0) { y = 67.2; ledge--; }
                    s.px[i][t] = qx; s.py[i][t] = y; s.pz[i][t] = qz; s.ground[i][t] = g;
                }
                s.outsideSlowStart[i] = new int[]{r.nextInt(TICKS), r.nextInt(TICKS)};
            }
            for (int k = 0; k < 2; k++) {
                double a = r.nextDouble() * 2 * Math.PI, rad = 1 + r.nextDouble() * 7;
                double qx = Math.cos(a) * rad, qz = Math.sin(a) * rad;
                for (int t = 0; t < TICKS; t++) {
                    qx += (r.nextDouble() - 0.5) * 0.2; qz += (r.nextDouble() - 0.5) * 0.2;
                    s.mx[k][t] = qx; s.mz[k][t] = qz;
                }
                s.mobDeath[k] = 60 + r.nextInt(TICKS - 60);
            }
            return s;
        }
    }

    /** bundled variety numbers (seed 0) or every numeric leaf × U(0.4, 1.8) (the Variety constructor clamps them) */
    @SuppressWarnings("unchecked")
    static EmberRunMaps.Variety variety(Map<?, ?> bundledVariety, long seed) {
        if (seed == 0) return new EmberRunMaps.Variety(bundledVariety);
        Random r = new Random(seed ^ 0x5DEECE66DL);
        Map<String, Object> out = new LinkedHashMap<String, Object>();
        for (Map.Entry<?, ?> e : bundledVariety.entrySet()) {
            Object val = e.getValue();
            if (val instanceof Map) {
                Map<String, Object> sub = new LinkedHashMap<String, Object>();
                for (Map.Entry<?, ?> f : ((Map<?, ?>) val).entrySet()) {
                    Object fv = f.getValue();
                    sub.put(String.valueOf(f.getKey()), fv instanceof Number ? ((Number) fv).doubleValue() * (0.4 + 1.4 * r.nextDouble()) : fv);
                }
                out.put(String.valueOf(e.getKey()), sub);
            } else out.put(String.valueOf(e.getKey()), val);
        }
        return new EmberRunMaps.Variety(out);
    }

    // ------------------------------------------------------------------ replay

    private final AffixPath P;
    private final String affix;
    private final Script sc;
    private final EmberRunMaps.Variety v;
    private final List<String> out = new ArrayList<String>();
    private int tick;
    private long now;
    // elite state (mirrors EmberRunDirector.Tracked affix fields)
    private long affixNext, affixAt, regenWindowEnd, frostNext, chainLiveAt;
    private Location affixOrigin;
    private Vector affixDir;
    private double regenHurt, hp, arcaneStart, arcanePrev;
    private boolean venomDiag, dead;
    private int arcaneSign = 1;
    private final Set<Integer> arcaneHit = new HashSet<Integer>();
    private int chainTo = -1;
    private final Map<Integer, Long> chainBurnAt = new HashMap<Integer, Long>();
    // molten (Director fields)
    private Location moltenOrigin;
    private long moltenWarnAt, moltenBoomAt;
    private double moltenDmg;
    // per player Slowness: amp, ticks left (−1 none)
    private int[] slowAmp, slowLeft;

    private AffixReplay(AffixPath p, String affix, Script sc, EmberRunMaps.Variety v) {
        this.P = p; this.affix = affix; this.sc = sc; this.v = v;
        slowAmp = new int[sc.n]; slowLeft = new int[sc.n];
        java.util.Arrays.fill(slowLeft, -1);
    }

    static List<String> run(AffixPath p, String affix, Script sc, EmberRunMaps.Variety v) {
        AffixReplay r = new AffixReplay(p, affix, sc, v);
        r.go();
        return r.out;
    }

    private static String d(double x) { return Double.toString(x); }

    private static String sk(EmberRunMaps.Skill s) {
        return s.type + "|" + s.name + "|r=" + d(s.radius) + "|ah=" + d(s.ahead) + "|len=" + d(s.length) + "|w=" + d(s.width)
                + "|st=" + d(s.start) + "|from=" + d(s.stripFrom()) + "|to=" + d(s.stripTo()) + "|warn=" + d(s.warn)
                + "|dmg=" + d(s.dmg) + "|kb=" + d(s.kb) + "|share=" + s.share;
    }

    private void log(String what) { out.add(affix + "\t" + (now - T0) + "\t" + what); }

    private Location elite() { return new Location(null, sc.ex[tick], sc.eliteY, sc.ez[tick]); }
    private Location player(int i) { return new Location(null, sc.px[i][tick], sc.py[i][tick], sc.pz[i][tick]); }
    private Location mob(int k) { return new Location(null, sc.mx[k][tick], 64.0, sc.mz[k][tick]); }
    private boolean mobAlive(int k) { return tick < sc.mobDeath[k]; }

    /** Bukkit Location.distanceSquared (same formula; the Bukkit one refuses a null world) */
    private static double d2(Location a, Location b) {
        double x = a.getX() - b.getX(), y = a.getY() - b.getY(), z = a.getZ() - b.getZ();
        return x * x + y * y + z * z;
    }

    private int nearest(Location l, double max) {
        int best = -1;
        double bd = max * max;
        for (int i = 0; i < sc.n; i++) {
            double dd = d2(player(i), l);
            if (dd <= bd) { bd = dd; best = i; }
        }
        return best;
    }

    private void execute(EmberRunMaps.Skill s, Location o, Vector dir) {
        int hit = 0;
        for (int i = 0; i < sc.n; i++) if (P.inShape(s, o, dir, player(i))) { log("HIT p" + i + " " + sk(s)); hit++; }
        log("CAST " + s.name + " hit=" + hit);
    }

    private Location targetFeet(double ahead) {
        Location el = elite();
        int tgt = nearest(el, P.mortarReach());
        Location base;
        if (tgt >= 0) {
            Location l = player(tgt);
            double y = sc.ground[tgt][tick] ? l.getY() : Math.floor(l.getY());
            base = new Location(null, l.getX(), y, l.getZ());
        } else {
            base = el.clone();
            base.setY(Math.floor(base.getY()));
        }
        if (Math.abs(ahead) > 1e-6) {
            Vector dir = el.toVector().subtract(base.toVector());
            dir.setY(0);
            if (dir.lengthSquared() > 1e-6) {
                dir.normalize().multiply(ahead);
                base.add(dir.getX(), 0, dir.getZ());
            }
        }
        return base;
    }

    private void go() {
        now = T0; tick = 0;
        hp = sc.maxHp;
        // promote
        double maxHp = sc.maxHp;
        if ("shield".equals(affix)) { maxHp = P.shieldHp(sc.maxHp, v); hp = maxHp; }
        affixNext = P.firstNext(now, P.firstEvery(affix, v));
        log("PROMOTE fx=" + P.fx(affix) + " next=" + (affixNext - T0) + " maxHp=" + d(maxHp) + " how=" + P.how(affix, v));
        for (tick = 0; tick < sc.ticks; tick++) {
            now = T0 + tick * DT;
            // outside long Slowness (amplifier 3, 10 s) the frost aura must never strip
            for (int i = 0; i < sc.n; i++) for (int st : sc.outsideSlowStart[i]) if (st == tick) { slowAmp[i] = 3; slowLeft[i] = 200; }
            if (!dead) {
                double dmg = sc.party[tick];
                if ("regen".equals(affix) && P.regenCounts(regenWindowEnd, dmg)) regenHurt += dmg;
                hp = Math.max(1.0, hp - dmg * 0.05);
                if (tick == DEATH_TICK) die(maxHp);
                else tickAffix(maxHp);
            }
            moltenTick();
            for (int i = 0; i < sc.n; i++) if (slowLeft[i] >= 0 && --slowLeft[i] < 0) slowAmp[i] = 0;
        }
    }

    private void die(double maxHp) {
        dead = true;
        log("DIE");
        if ("split".equals(affix)) {
            int n = 0;
            for (int i = 0; i < P.splitCount(v); i++) { log("SPLIT_ADD maxHp=" + d(P.splitAddHp(sc.meleeBase, v))); n++; }
            log("SPLIT n=" + n);
        }
        if ("molten".equals(affix)) {
            Location at = elite();
            at.setY(Math.floor(at.getY()));
            moltenOrigin = at;
            moltenDmg = P.moltenDmg(sc.atk, v);
            moltenWarnAt = P.moltenWarnAt(now, v);
            moltenBoomAt = P.moltenBoomAt(moltenWarnAt, v);
            log("MOLTEN warn=" + (moltenWarnAt - T0) + " boom=" + (moltenBoomAt - T0) + " ref=" + sk(P.molten(sc.atk, v)));
        }
    }

    private void moltenTick() {
        if (moltenWarnAt <= 0 || moltenOrigin == null) return;
        if (now < moltenWarnAt) return;
        if (now >= moltenBoomAt) {
            execute(P.moltenBlast(moltenDmg, v), moltenOrigin, new Vector(1, 0, 0));
            moltenOrigin = null; moltenWarnAt = 0; moltenBoomAt = 0; moltenDmg = 0;
        }
    }

    private void tickAffix(double maxHp) {
        Location el = elite();
        if ("blazing".equals(affix)) {
            if (affixAt > 0) {
                if (now >= affixAt) {
                    execute(P.blaze(sc.atk, v), affixOrigin, new Vector(1, 0, 0));
                    affixAt = 0;
                    affixNext = P.after(now, v.blazeEvery);
                }
                return;
            }
            if (now < affixNext || nearest(el, P.engage(affix)) < 0) return;
            affixOrigin = el.clone();
            affixAt = P.after(now, v.blazeWarn);
            log("ARM blazing at=" + (affixAt - T0));
            return;
        }
        if ("regen".equals(affix)) {
            if (regenWindowEnd > 0) {
                if (now >= regenWindowEnd) {
                    if (!P.regenInterrupted(regenHurt, maxHp, v)) {
                        double heal = P.regenHeal(hp, maxHp, v);
                        if (heal > 0) hp = Math.min(maxHp, hp + heal);
                        log("HEAL " + d(heal) + " hp=" + d(hp) + " hurt=" + d(regenHurt));
                    } else log("INTERRUPT hurt=" + d(regenHurt));
                    regenWindowEnd = 0;
                    regenHurt = 0;
                    affixNext = P.after(now, v.regenEvery);
                }
                return;
            }
            if (now < affixNext || nearest(el, P.engage(affix)) < 0) return;
            regenWindowEnd = P.regenWindowEnd(now, v);
            regenHurt = 0;
            log("CHANNEL end=" + (regenWindowEnd - T0));
            return;
        }
        if ("charge".equals(affix)) {
            if (affixAt > 0) {
                EmberRunMaps.Skill s = P.charge(sc.atk, v, affixDir == null ? new Vector(1, 0, 0) : affixDir);
                if (now >= affixAt) {
                    execute(s, affixOrigin, affixDir == null ? new Vector(1, 0, 0) : affixDir);
                    affixAt = 0;
                    affixNext = P.after(now, v.chargeEvery);
                }
                return;
            }
            int tgt = nearest(el, P.engage(affix));
            if (now < affixNext || tgt < 0) return;
            Location o = el.clone();
            Vector dir = P.chargeAim(o, player(tgt));
            double run = Math.min(sc.clear[tick], v.chargeLength);
            if (!P.chargeRoom(run)) { log("NOROOM run=" + d(run)); return; }
            affixOrigin = o;
            affixDir = dir;
            affixAt = P.after(now, v.chargeWarn);
            log("ARM charge dir=" + d(dir.getX()) + "," + d(dir.getZ()) + " at=" + (affixAt - T0));
            return;
        }
        if ("frost".equals(affix)) {
            if (now < frostNext) return;
            frostNext = P.frostNext(now, v);
            for (int i = 0; i < sc.n; i++) {
                if (P.frostIn(d2(player(i), el), v)) {
                    slowAmp[i] = v.frostAmplifier; slowLeft[i] = P.frostSlowTicks(v);
                    log("SLOW p" + i + " ticks=" + slowLeft[i] + " amp=" + slowAmp[i]);
                } else if (slowLeft[i] >= 0 && P.frostOwnSlow(slowAmp[i], slowLeft[i], v)) {
                    slowLeft[i] = -1; slowAmp[i] = 0;
                    log("UNSLOW p" + i);
                }
            }
            return;
        }
        if ("mortar".equals(affix)) {
            if (affixAt > 0) {
                if (now >= affixAt) {
                    execute(P.mortar(sc.atk, v), affixOrigin, new Vector(1, 0, 0));
                    affixAt = 0;
                    affixNext = P.after(now, v.mortarEvery);
                }
                return;
            }
            if (now < affixNext) return;
            affixOrigin = targetFeet(v.mortarAhead);
            affixAt = P.after(now, v.mortarWarn);
            log("ARM mortar o=" + d(affixOrigin.getX()) + "," + d(affixOrigin.getY()) + "," + d(affixOrigin.getZ()));
            return;
        }
        if ("venom".equals(affix)) {
            EmberRunMaps.Skill arm = P.venom(sc.atk, v);
            if (affixAt > 0) {
                if (now >= affixAt) {
                    Vector[] dirs = P.venomDirs(venomDiag);
                    int hit = 0;
                    for (int i = 0; i < sc.n; i++) {
                        if (!P.venomHits(arm, affixOrigin, dirs, player(i))) continue;
                        log("HIT p" + i + " " + sk(arm));
                        hit++;
                    }
                    log("CAST venom " + (venomDiag ? "x" : "+") + " hit=" + hit);
                    venomDiag = !venomDiag;
                    affixAt = 0;
                    affixNext = P.after(now, v.venomEvery);
                }
                return;
            }
            if (now < affixNext || nearest(el, P.engage(affix)) < 0) return;
            Location o = el.clone();
            o.setY(Math.floor(o.getY()));
            affixOrigin = o;
            affixAt = P.after(now, v.venomWarn);
            return;
        }
        if ("jailer".equals(affix)) {
            if (affixAt > 0) {
                if (now >= affixAt) {
                    EmberRunMaps.Skill s = P.jailer(sc.atk, v);
                    int ticks = P.jailerRootTicks(v);
                    int rooted = 0;
                    for (int i = 0; i < sc.n; i++) {
                        if (!P.inShape(s, affixOrigin, new Vector(1, 0, 0), player(i))) continue;
                        if (s.dmg > 0) log("HIT p" + i + " " + sk(s));
                        if (ticks > 0) { log("ROOT p" + i + " ticks=" + ticks); rooted++; }
                    }
                    log("CAST jailer rooted=" + rooted);
                    affixAt = 0;
                    affixNext = P.after(now, v.jailerEvery);
                }
                return;
            }
            if (now < affixNext) return;
            affixOrigin = targetFeet(0.0);
            affixAt = P.after(now, v.jailerWarn);
            return;
        }
        if ("arcane".equals(affix)) {
            double sweep = P.sweepRad(v);
            if (affixAt > 0) {
                if (now < affixAt) return;
                double el2 = (now - affixAt) / 1000.0;
                double cur = P.arcaneAngle(arcaneStart, arcaneSign, sweep, v.arcaneSpin, el2);
                double dmg = P.arcaneDmg(sc.atk, v);
                for (int i = 0; i < sc.n; i++) {
                    if (arcaneHit.contains(i)) continue;
                    if (!P.arcaneSwept(affixOrigin, arcanePrev, cur, v.arcaneLength, v.arcaneWidth, player(i))) continue;
                    arcaneHit.add(i);
                    if (dmg > 0) log("HIT p" + i + " beam dmg=" + d(dmg) + " ang=" + d(cur));
                }
                arcanePrev = cur;
                if (P.arcaneSpun(el2, v)) {
                    log("CAST arcane " + (arcaneSign > 0 ? "ccw" : "cw") + " hit=" + arcaneHit.size());
                    arcaneSign = -arcaneSign;
                    arcaneHit.clear();
                    affixAt = 0;
                    affixNext = P.after(now, v.arcaneEvery);
                }
                return;
            }
            int tgt = nearest(el, P.engage(affix));
            if (now < affixNext || tgt < 0) return;
            Location o = el.clone();
            o.setY(Math.floor(o.getY()));
            affixOrigin = o;
            arcaneStart = P.arcaneStartAngle(o, player(tgt), arcaneSign, sweep);
            arcanePrev = arcaneStart;
            arcaneHit.clear();
            affixAt = P.after(now, v.arcaneWarn);
            log("ARM arcane start=" + d(arcaneStart));
            return;
        }
        if ("firechain".equals(affix)) {
            int q = chainTo;
            if (q >= 0 && (!mobAlive(q) || P.chainTooFar(d2(mob(q), el), v))) {
                chainTo = -1;
                q = -1;
                affixNext = now;
                log("UNLINK");
            }
            if (q < 0) {
                if (now < affixNext) return;
                int best = -1;
                double bd = v.chainLinkRange * v.chainLinkRange;
                for (int k = 0; k < 2; k++) {
                    if (!mobAlive(k)) continue;
                    double dd = d2(mob(k), el);
                    if (dd <= bd) { bd = dd; best = k; }
                }
                if (best < 0) { affixNext = now + P.relinkRetryMs(); return; }
                chainTo = best;
                chainLiveAt = P.chainLiveAt(now, v);
                log("LINK m" + best + " live=" + (chainLiveAt - T0));
                return;
            }
            Location a = el, b = mob(q);
            if (now < chainLiveAt) return;
            double dmg = P.chainDmg(sc.atk, v);
            for (int i = 0; i < sc.n; i++) {
                if (!P.chainTouches(a, b, v.chainLinkWidth, player(i))) continue;
                if (!P.chainBurnReady(chainBurnAt.get(i), now, v.chainLinkTick)) continue;
                chainBurnAt.put(i, now);
                if (dmg > 0) log("HIT p" + i + " chain dmg=" + d(dmg));
            }
        }
        // split / shield / molten: idle glow only while alive
    }

    static String fmtLine(String s) { return String.format(Locale.ROOT, "%s", s); }
}

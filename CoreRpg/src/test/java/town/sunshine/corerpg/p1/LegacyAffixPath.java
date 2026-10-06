package town.sunshine.corerpg.p1;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * D241 replay — the OLD code path: the affix statics of {@code EmberRunDirector} at 46736db (CoreRpg 1.65.66) copied
 * verbatim (only {@code Tracked t} → {@code double atk}), plus the inline promote / tick arithmetic of that version.
 * Frozen on purpose: do not "fix" anything here, it is the reference the primitives are replayed against.
 */
final class LegacyAffixPath implements AffixPath {

    @Override public String name() { return "legacy-46736db"; }

    private static String fmt(double v) { return v == Math.rint(v) ? String.valueOf((int) v) : String.valueOf(v); }

    @Override public double firstEvery(String affix, EmberRunMaps.Variety v) {
        return "blazing".equals(affix) ? v.blazeEvery
                : "regen".equals(affix) ? v.regenEvery
                : "charge".equals(affix) ? v.chargeEvery
                : "frost".equals(affix) ? v.frostTick
                : "mortar".equals(affix) ? v.mortarEvery
                : "venom".equals(affix) ? v.venomEvery
                : "jailer".equals(affix) ? v.jailerEvery
                : "arcane".equals(affix) ? v.arcaneEvery
                : "firechain".equals(affix) ? v.chainLinkWarn
                : v.blazeEvery; // molten has no live tick; split/shield idle glow only
    }

    @Override public String how(String affix, EmberRunMaps.Variety v) {
        String how;
        if ("blazing".equals(affix)) how = "脚下每 " + fmt(v.blazeEvery) + " 秒落一圈火（半径 " + fmt(v.blazeRadius) + "，" + fmt(v.blazeWarn) + " 秒预警，看到火圈就退开）";
        else if ("split".equals(affix)) how = "死后分裂成 " + v.splitCount + " 个小怪（门要等它们也倒下）";
        else if ("shield".equals(affix)) how = "生命 ×" + fmt(v.shieldHp);
        else if ("regen".equals(affix)) how = "发光读条时猛打可打断回血";
        else if ("charge".equals(affix)) how = "看见脚下亮带就躲开";
        else if ("frost".equals(affix)) how = "别站在它身边的霜圈里（出圈即解除）";
        else if ("mortar".equals(affix)) how = "脚下附近会亮圈，走开再打";
        else if ("molten".equals(affix)) how = "杀掉后尸体要炸，立刻退开";
        else if ("venom".equals(affix)) how = "身上会亮十字（+ 和 × 轮换），站到两条线之间的空隙里";
        else if ("jailer".equals(affix)) how = "脚下亮小圈就走开，被罩住会定身 " + fmt(v.jailerRoot) + " 秒";
        else if ("arcane".equals(affix)) how = "脚下会亮起一道光束并转半圈（紫色预警标出起点和扫过的半边），退到 " + fmt(v.arcaneLength) + " 格外或站到另半边";
        else if ("firechain".equals(affix)) how = "和身边一只怪连着一条火链，别站在两只怪之间；先杀掉被连的那只，火链会换人（换之前有 " + fmt(v.chainLinkWarn) + " 秒烟线预警）";
        else how = affix;
        return how;
    }

    @Override public String fx(String affix) {
        return "blazing".equals(affix) ? "FLAME"
                : "split".equals(affix) ? "SPELL_WITCH"
                : "regen".equals(affix) ? "HEART"
                : "charge".equals(affix) ? "CRIT"
                : "frost".equals(affix) ? "SNOW_SHOVEL"
                : "mortar".equals(affix) ? "FLAME"
                : "molten".equals(affix) ? "LAVA"
                : "venom".equals(affix) ? "SPELL_MOB"
                : "jailer".equals(affix) ? "CRIT_MAGIC"
                : "arcane".equals(affix) ? "SPELL_WITCH"
                : "firechain".equals(affix) ? "FLAME"
                : "END_ROD";
    }

    @Override public long firstNext(long now, double every) { return now + 1500L + (long) (every * 1000); }
    @Override public long after(long now, double secs) { return now + (long) (secs * 1000); }
    @Override public double shieldHp(double base, EmberRunMaps.Variety v) { return base * v.shieldHp; }
    @Override public double engage(String id) { // the literal ranges of the old affixTick nearest(…) calls
        return "blazing".equals(id) ? 6 : "regen".equals(id) ? 8 : "charge".equals(id) ? 10
                : "venom".equals(id) ? 8 : "arcane".equals(id) ? 8 : 0;
    }
    @Override public double mortarReach() { return 16; }
    @Override public long relinkRetryMs() { return 1000L; }

    @Override public EmberRunMaps.Skill blaze(double atk, EmberRunMaps.Variety v) { return blazeSkill(atk, v); }
    @Override public EmberRunMaps.Skill charge(double atk, EmberRunMaps.Variety v, Vector dir) { return chargeSkill(atk, v, dir); }
    @Override public EmberRunMaps.Skill mortar(double atk, EmberRunMaps.Variety v) { return mortarSkill(atk, v); }
    @Override public EmberRunMaps.Skill molten(double atk, EmberRunMaps.Variety v) { return moltenSkill(atk, v); }
    @Override public EmberRunMaps.Skill moltenBlast(double moltenDmg, EmberRunMaps.Variety v) {
        // old moltenTick: rebuild skill with stored absolute dmg (atk already folded)
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "亡爆圈");
        m.put("radius", v.moltenRadius);
        m.put("warn", v.moltenWarn);
        m.put("dmg", moltenDmg);
        m.put("kb", 0);
        return new EmberRunMaps.Skill(m);
    }
    @Override public double moltenDmg(double atk, EmberRunMaps.Variety v) { return atk * v.moltenDmg; }
    @Override public long moltenWarnAt(long now, EmberRunMaps.Variety v) { return now + (long) (v.moltenDelay * 1000); }
    @Override public long moltenBoomAt(long moltenWarnAt, EmberRunMaps.Variety v) { return moltenWarnAt + (long) (v.moltenWarn * 1000); }
    @Override public EmberRunMaps.Skill venom(double atk, EmberRunMaps.Variety v) { return venomSkill(atk, v); }
    @Override public EmberRunMaps.Skill jailer(double atk, EmberRunMaps.Variety v) { return jailerSkill(atk, v); }
    @Override public int jailerRootTicks(EmberRunMaps.Variety v) { return jailerRootTicks0(v); }
    @Override public double arcaneDmg(double atk, EmberRunMaps.Variety v) { return atk * v.arcaneDmg; }
    @Override public double chainDmg(double atk, EmberRunMaps.Variety v) { return atk * v.chainLinkDmg; }

    @Override public boolean inShape(EmberRunMaps.Skill sk, Location o, Vector dir, Location p) { return inShape0(sk, o, dir, p); }
    @Override public Vector[] venomDirs(boolean diag) { return venomDirs0(diag); }
    @Override public boolean venomHits(EmberRunMaps.Skill arm, Location o, Vector[] dirs, Location p) { return venomHits0(arm, o, dirs, p); }
    @Override public Vector chargeAim(Location o, Location tgt) {
        Vector dir = tgt.toVector().subtract(o.toVector());
        dir.setY(0);
        if (dir.lengthSquared() < 1e-6) dir = new Vector(0, 0, 1);
        dir.normalize();
        return dir;
    }
    @Override public boolean chargeRoom(double run) { return !(run < EmberRunDirector.CHARGE_MIN); }
    @Override public double sweepRad(EmberRunMaps.Variety v) { return Math.toRadians(v.arcaneSweep); }
    @Override public double arcaneAngle(double start, int sign, double sweepRad, double spin, double elapsed) { return arcaneAngle0(start, sign, sweepRad, spin, elapsed); }
    @Override public double arcaneStartAngle(Location o, Location target, int sign, double sweepRad) { return arcaneStartAngle0(o, target, sign, sweepRad); }
    @Override public boolean arcaneSwept(Location o, double a0, double a1, double len, double width, Location p) { return arcaneSwept0(o, a0, a1, len, width, p); }
    @Override public boolean arcaneSpun(double el, EmberRunMaps.Variety v) { return el >= v.arcaneSpin; }
    @Override public boolean chainTouches(Location a, Location b, double width, Location p) { return chainTouches0(a, b, width, p); }
    @Override public boolean chainBurnReady(Long last, long now, double tick) { return chainBurnReady0(last, now, tick); }
    @Override public boolean chainTooFar(double d2, EmberRunMaps.Variety v) { return d2 > (v.chainLinkRange + 4) * (v.chainLinkRange + 4); }
    @Override public long chainLiveAt(long now, EmberRunMaps.Variety v) { return now + (long) (v.chainLinkWarn * 1000); }

    @Override public long regenWindowEnd(long now, EmberRunMaps.Variety v) { return now + (long) (v.regenInterruptWindow * 1000); }
    @Override public boolean regenInterrupted(double regenHurt, double max, EmberRunMaps.Variety v) { return !(regenHurt < max * v.regenInterruptHp); }
    @Override public double regenHeal(double hp, double max, EmberRunMaps.Variety v) { return Math.min(max - hp, max * v.regenHeal); }
    @Override public boolean regenCounts(long regenWindowEnd, double dmg) { return !(regenWindowEnd <= 0 || dmg <= 0); }
    @Override public long frostNext(long now, EmberRunMaps.Variety v) { return now + (long) (v.frostTick * 1000); }
    @Override public boolean frostIn(double d2, EmberRunMaps.Variety v) { double r2 = v.frostRadius * v.frostRadius; return d2 <= r2; }
    @Override public int frostSlowTicks(EmberRunMaps.Variety v) { return (int) (v.frostTick * 20) + 10; }
    @Override public boolean frostOwnSlow(int amp, int dur, EmberRunMaps.Variety v) { return amp == v.frostAmplifier && dur <= (int) (v.frostTick * 20) + 15; }
    @Override public int splitCount(EmberRunMaps.Variety v) { return v.splitCount; }
    @Override public double splitAddHp(double base, EmberRunMaps.Variety v) { return Math.max(1.0, base * v.splitHp); }

    // ------------------------------------------------------------------ verbatim from EmberRunDirector @ 46736db

    static boolean inShape0(EmberRunMaps.Skill sk, Location o, Vector dir, Location p) {
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

    static double arcaneAngle0(double start, int sign, double sweepRad, double spin, double elapsed) {
        double f = spin <= 0 ? 1.0 : Math.max(0.0, Math.min(1.0, elapsed / spin));
        return start + (sign >= 0 ? 1 : -1) * sweepRad * f;
    }

    static double arcaneStartAngle0(Location o, Location target, int sign, double sweepRad) {
        double base = Math.atan2(target.getZ() - o.getZ(), target.getX() - o.getX());
        return base - (sign >= 0 ? 1 : -1) * sweepRad / 2.0;
    }

    static boolean arcaneSwept0(Location o, double a0, double a1, double len, double width, Location p) {
        if (Math.abs(p.getY() - o.getY()) > 2.5) return false;
        double dx = p.getX() - o.getX(), dz = p.getZ() - o.getZ();
        double r = Math.sqrt(dx * dx + dz * dz);
        if (r > len) return false;
        if (r < 0.6) return true; // standing on the elite's feet: the pivot
        double lo = Math.min(a0, a1), span = Math.abs(a1 - a0);
        double half = Math.asin(Math.min(1.0, (width / 2.0) / r));
        double d = (Math.atan2(dz, dx) - lo + half) % (2 * Math.PI);
        if (d < 0) d += 2 * Math.PI;
        return d <= span + 2 * half;
    }

    static boolean chainTouches0(Location a, Location b, double width, Location p) {
        double lo = Math.min(a.getY(), b.getY()) - 1.0, hi = Math.max(a.getY(), b.getY()) + 2.5;
        if (p.getY() < lo || p.getY() > hi) return false;
        double ax = a.getX(), az = a.getZ(), bx = b.getX() - ax, bz = b.getZ() - az;
        double px = p.getX() - ax, pz = p.getZ() - az;
        double l2 = bx * bx + bz * bz;
        double f = l2 < 1e-9 ? 0.0 : Math.max(0.0, Math.min(1.0, (px * bx + pz * bz) / l2));
        double ex = px - f * bx, ez = pz - f * bz;
        return ex * ex + ez * ez <= (width / 2.0) * (width / 2.0);
    }

    static boolean chainBurnReady0(Long last, long now, double tick) {
        return last == null || now - last >= (long) (tick * 1000);
    }

    static EmberRunMaps.Skill venomSkill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "line");
        m.put("name", "毒十字");
        m.put("start", -v.venomArm);
        m.put("length", 2 * v.venomArm);
        m.put("width", v.venomWidth);
        m.put("warn", v.venomWarn);
        m.put("dmg", atk * v.venomDmg);
        m.put("kb", 0);
        return new EmberRunMaps.Skill(m);
    }

    static Vector[] venomDirs0(boolean diag) {
        if (!diag) return new Vector[]{new Vector(1, 0, 0), new Vector(0, 0, 1)};
        double c = Math.sqrt(0.5);
        return new Vector[]{new Vector(c, 0, c), new Vector(c, 0, -c)};
    }

    static boolean venomHits0(EmberRunMaps.Skill arm, Location o, Vector[] dirs, Location p) {
        for (Vector d : dirs) if (inShape0(arm, o, d, p)) return true;
        return false;
    }

    static EmberRunMaps.Skill jailerSkill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "禁锢圈");
        m.put("radius", v.jailerRadius);
        m.put("warn", v.jailerWarn);
        m.put("ahead", 0.0);
        m.put("dmg", atk * v.jailerDmg);
        m.put("kb", 0);
        return new EmberRunMaps.Skill(m);
    }

    static int jailerRootTicks0(EmberRunMaps.Variety v) { return (int) Math.round(v.jailerRoot * 20); }

    static EmberRunMaps.Skill blazeSkill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "炽热火圈");
        m.put("radius", v.blazeRadius);
        m.put("warn", v.blazeWarn);
        m.put("dmg", atk * v.blazeDmg);
        return new EmberRunMaps.Skill(m);
    }

    static EmberRunMaps.Skill chargeSkill(double atk, EmberRunMaps.Variety v, Vector dir) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "charge");
        m.put("name", "冲锋条带");
        m.put("length", v.chargeLength);
        m.put("width", v.chargeWidth);
        m.put("warn", v.chargeWarn);
        m.put("dmg", atk * v.chargeDmg);
        EmberRunMaps.Skill sk = new EmberRunMaps.Skill(m);
        // clip to clearRun length when origin known — caller already clipped via clearRun; length stays config max
        return sk;
    }

    static EmberRunMaps.Skill mortarSkill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "投弹圈");
        m.put("radius", v.mortarRadius);
        m.put("warn", v.mortarWarn);
        m.put("ahead", v.mortarAhead);
        m.put("dmg", atk * v.mortarDmg);
        m.put("kb", 0);
        return new EmberRunMaps.Skill(m);
    }

    static EmberRunMaps.Skill moltenSkill(double atk, EmberRunMaps.Variety v) {
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("type", "circle");
        m.put("name", "亡爆圈");
        m.put("radius", v.moltenRadius);
        m.put("warn", v.moltenWarn);
        m.put("dmg", atk * v.moltenDmg);
        m.put("kb", 0);
        return new EmberRunMaps.Skill(m);
    }
}

#!/usr/bin/env python3
"""Read-only check: every DP spawn/teleport/mob point of the Ember dungeons must be standable in its map template.

B2.123 (2026-10-01): the live plugins/DungeonPlus/map templates for ember_weekly/raid/abyss/daily were the 09-21 base
world (gitignored, never refreshed) while monster.yml used the rebuilt P2-P5 rooms → every $mob point sat inside stone,
mobs suffocated and DP's $kill counted it → idle players got weekly/raid clears + loot. Run this after copying maps.

usage: scripts/check-dp-spawns.py [dungeonDir ...]     (default: every plugins/DungeonPlus/dungeon/Ember*/)
P1 G04: for EmberQ0x dungeons the CoreRpg run director spawns the mobs, so the points in
plugins/CoreRpg/ember-v1-runs.yml (rooms.*.points, boss.at, boss.adds.points, event.anchor, spawn, spread, links.to) of the map whose
`dungeon:` matches are checked too, every door block listed there must be a closed door (iron bars) in the template
(a door preceded by a `# runtime:` comment line must be open there instead: the director closes it at attach),
and no runtime rail box (rails:) may cover a point's feet or head.
       DP_MAP_ROOT=/path/with/<mapname>/ dirs to check other map copies (e.g. a release or a backup)
exit 1 = some point has its head inside a full opaque block (mob suffocates → DP counts the death) or no chunk;
WARN (exit 0) = feet in the floor, head in a non-full block (bars/glass/fence/slab/stairs: stuck, no suffocation), floating.
No dependencies (own minimal NBT + anvil reader, MC 1.12 format).
"""
import glob, io, os, re, struct, sys, zlib

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'plugins', 'DungeonPlus')
RUNS = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'plugins', 'CoreRpg', 'ember-v1-runs.yml')


def p1_points(dungeon):
    """(label, x, y, z) points and door boxes of the ember-v1-runs.yml map whose dungeon id is `dungeon`."""
    if not os.path.exists(RUNS): return [], []
    text = open(RUNS, encoding='utf-8').read()
    blocks = re.split(r'(?m)^  (q\d\d):\s*$', text)
    for i in range(1, len(blocks) - 1, 2):
        body = blocks[i + 1]
        if not re.search(r'(?m)^\s+dungeon:\s*%s\s*$' % re.escape(dungeon), body): continue
        pts, doors, room, rails, lst, runtime = [], [], '', [], '', False
        for line in body.splitlines():
            r = re.match(r'^      (r\d|boss|event|adds):', line) or re.match(r'^        (adds):', line)
            if r: room = r.group(1)
            top = re.match(r'^    (\w+):', line)
            if top: lst = top.group(1); room = '' if lst in ('links', 'rails', 'spread', 'clear') else room
            if lst == 'links':  # - {after: r1, from: [..], to: [x, y, z], ...}: the arrival point must be standable
                t = re.search(r'\bto:\s*\[\s*(-?\d+),\s*(-?\d+),\s*(-?\d+)\s*\]', line)
                if t: pts.append(('p1:%s:link:to' % blocks[i], int(t.group(1)), int(t.group(2)), int(t.group(3))))
                continue
            if lst == 'rails':
                d = re.search(r'\[\s*(-?\d+),\s*(-?\d+),\s*(-?\d+),\s*(-?\d+),\s*(-?\d+),\s*(-?\d+)\s*\]', line)
                if d: rails.append([int(v) for v in d.groups()])
                continue
            if re.match(r'^\s*#\s*runtime:', line): runtime = True  # next door: template opening closed at attach
            key = re.match(r'^\s*(\w+):', line)
            if not key: continue
            k = key.group(1)
            if k == 'door':
                d = re.search(r'\[\s*(-?\d+),\s*(-?\d+),\s*(-?\d+),\s*(-?\d+),\s*(-?\d+),\s*(-?\d+)\s*\]', line)
                if d: doors.append((blocks[i] + ':' + room + ':door' + (':runtime' if runtime else ''), [int(v) for v in d.groups()]))
                runtime = False
                continue
            runtime = False
            if k not in ('points', 'at', 'anchor', 'spawn', 'spread'): continue
            for m in re.finditer(r'\[\s*(-?\d+),\s*(-?\d+),\s*(-?\d+)\s*\]', line):
                pts.append(('p1:%s:%s:%s' % (blocks[i], room or '-', k), int(m.group(1)), int(m.group(2)), int(m.group(3))))
        P1_RAILS[dungeon] = rails
        return pts, doors
    return [], []


P1_RAILS = {}  # dungeon -> rail boxes (iron bars placed into AIR at attach): must not cover any point
# non-solid ids a mob can stand in: air, sapling, water, lava, tall grass, dead bush, flowers, torch, fire, rails, signs,
# ladder, lever, plates, redstone torch, buttons, snow layer, sugar cane, vines, carpet, double plants, banners
PASSABLE = {0, 6, 8, 9, 10, 11, 27, 28, 30, 31, 32, 37, 38, 39, 40, 50, 51, 55, 59, 63, 65, 66, 68, 69, 70, 72, 75, 76,
            77, 78, 83, 106, 131, 132, 141, 142, 143, 147, 148, 157, 171, 175, 176, 177}

# blocks that are solid but not full opaque cubes: an entity inside gets stuck but does not suffocate (1.12)
NONCUBE = {20, 26, 44, 53, 54, 64, 67, 71, 79, 81, 85, 88, 92, 93, 94, 95, 96, 101, 102, 107, 108, 109, 113, 114, 116, 117,
           118, 120, 126, 127, 128, 130, 134, 135, 136, 138, 139, 140, 144, 145, 146, 149, 150, 151, 154, 156, 160, 163,
           164, 165, 167, 178, 180, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 203, 205}


def _nbt(buf, t):
    if t == 1: return struct.unpack('>b', buf.read(1))[0]
    if t == 2: return struct.unpack('>h', buf.read(2))[0]
    if t == 3: return struct.unpack('>i', buf.read(4))[0]
    if t == 4: return struct.unpack('>q', buf.read(8))[0]
    if t == 5: return struct.unpack('>f', buf.read(4))[0]
    if t == 6: return struct.unpack('>d', buf.read(8))[0]
    if t == 7: n = struct.unpack('>i', buf.read(4))[0]; return buf.read(n)
    if t == 8: n = struct.unpack('>H', buf.read(2))[0]; return buf.read(n).decode('utf-8', 'replace')
    if t == 9:
        et = buf.read(1)[0]; n = struct.unpack('>i', buf.read(4))[0]
        return [_nbt(buf, et) for _ in range(n)]
    if t == 10:
        d = {}
        while True:
            ct = buf.read(1)[0]
            if ct == 0: return d
            n = struct.unpack('>H', buf.read(2))[0]; k = buf.read(n).decode('utf-8', 'replace')
            d[k] = _nbt(buf, ct)
    if t == 11: n = struct.unpack('>i', buf.read(4))[0]; return struct.unpack('>%di' % n, buf.read(4 * n))
    if t == 12: n = struct.unpack('>i', buf.read(4))[0]; return struct.unpack('>%dq' % n, buf.read(8 * n))
    raise ValueError('nbt tag %d' % t)


class Map:
    def __init__(self, path): self.path, self.regions, self.chunks = path, {}, {}

    def _chunk(self, cx, cz):
        if (cx, cz) in self.chunks: return self.chunks[(cx, cz)]
        rx, rz = cx >> 5, cz >> 5
        if (rx, rz) not in self.regions:
            f = os.path.join(self.path, 'region', 'r.%d.%d.mca' % (rx, rz))
            self.regions[(rx, rz)] = open(f, 'rb').read() if os.path.exists(f) else None
        r, lv = self.regions[(rx, rz)], None
        if r:
            i = 4 * ((cx & 31) + (cz & 31) * 32); off = int.from_bytes(r[i:i + 3], 'big') * 4096
            if off:
                ln = struct.unpack('>I', r[off:off + 4])[0]
                b = io.BytesIO(zlib.decompress(r[off + 5:off + 4 + ln])); b.read(1); b.read(struct.unpack('>H', b.read(2))[0])
                lv = {}
                for s in _nbt(b, 10)['Level'].get('Sections', []): lv[s['Y']] = s['Blocks']
        self.chunks[(cx, cz)] = lv
        return lv

    def block(self, x, y, z):
        lv = self._chunk(x >> 4, z >> 4)
        if lv is None: return None
        sec = lv.get(y >> 4)
        return 0 if sec is None else sec[((y & 15) * 16 + (z & 15)) * 16 + (x & 15)]


def check(ddir):
    opt = open(os.path.join(ddir, 'option.yml'), encoding='utf-8').read()
    m = re.search(r'setmap\{name=(\w+)', opt)
    if not m: return 0, [], []
    mp = Map(os.path.join(os.environ.get('DP_MAP_ROOT') or os.path.join(ROOT, 'map'), m.group(1)))
    if not os.path.isdir(mp.path): return 1, ['  map %s missing' % m.group(1)], []
    text = opt + (open(os.path.join(ddir, 'monster.yml'), encoding='utf-8').read() if os.path.exists(os.path.join(ddir, 'monster.yml')) else '')
    pts = set()
    for mm in re.finditer(r'\$(mob|teleport|setspawn)\{([^}]*)\}', text):
        loc = re.search(r'location=(-?\d+(?:\.\d+)?),(-?\d+(?:\.\d+)?),(-?\d+(?:\.\d+)?)', mm.group(2))
        if not loc: continue
        name = re.search(r'name=([\w]+)', mm.group(2))
        x, y, z = (int(float(v) // 1) for v in loc.groups())
        pts.add((mm.group(1) + (':' + name.group(1) if name and mm.group(1) == 'mob' else ''), x, y, z))
    bad, warn = [], []
    p1pts, p1doors = p1_points(os.path.basename(ddir.rstrip('/')))
    pts.update(p1pts)
    for what, (x0, y0, z0, x1, y1, z1) in p1doors:
        for x in range(min(x0, x1), max(x0, x1) + 1):
            for y in range(min(y0, y1), max(y0, y1) + 1):
                for z in range(min(z0, z1), max(z0, z1) + 1):
                    b = mp.block(x, y, z)
                    if what.endswith(':runtime'):  # the director closes it at attach: the template must be open there
                        if b not in (0, 101): bad.append('  %s @ %d,%d,%d: block id %s, expected air (runtime door)' % (what, x, y, z, b))
                    elif b != 101: bad.append('  %s @ %d,%d,%d: block id %s, expected closed iron bars (101)' % (what, x, y, z, b))
    for rb in P1_RAILS.get(os.path.basename(ddir.rstrip('/')), []):
        x0, y0, z0, x1, y1, z1 = rb
        for what, x, y, z in pts:
            if min(x0, x1) <= x <= max(x0, x1) and min(z0, z1) <= z <= max(z0, z1) and min(y0, y1) <= y + 1 and y <= max(y0, y1):
                bad.append('  rail %s covers %s @ %d,%d,%d' % (rb, what, x, y, z))
    for what, x, y, z in sorted(pts, key=lambda p: (p[3], p[1], p[2])):
        below, feet, head = mp.block(x, y - 1, z), mp.block(x, y, z), mp.block(x, y + 1, z)
        at = '  %s @ %d,%d,%d: ' % (what, x, y, z)
        if feet is None: bad.append(at + 'no chunk in map')
        elif head not in PASSABLE and head not in NONCUBE: bad.append(at + 'head inside block id %s → suffocates' % head)
        elif head not in PASSABLE: warn.append(at + 'head inside non-full block id %s (stuck, no suffocation)' % head)
        elif feet not in PASSABLE: warn.append(at + 'feet inside block id %s (stuck in floor)' % feet)
        elif below in PASSABLE: warn.append(at + 'floating (block below id %s)' % below)
    return len(pts), bad, warn


def main(argv):
    dirs = argv or sorted(d for d in glob.glob(os.path.join(ROOT, 'dungeon', 'Ember*')) if os.path.isdir(d))
    fails = 0
    for d in dirs:
        n, bad, warn = check(d)
        print('%-4s %-18s %d points' % ('FAIL' if bad else 'WARN' if warn else 'ok', os.path.basename(d.rstrip('/')), n))
        for b in bad: print(b)
        for w in warn: print(w + '  [warn]')
        fails += bool(bad)
    print('check-dp-spawns: %d dungeon(s) with bad points' % fails)
    return 1 if fails else 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))

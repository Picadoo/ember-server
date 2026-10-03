"""D141–D143: run p1sim or p2econ with a growth build switched on (row-3 talent follows the worn set, like growthraid.py).
Usage (repo root):
  python3 tools/p1sim/growthrun.py p1sim  '{"talents":["t1b","t2a"],"honors":"all","affixes":{...},"affix_tier":4}' --players 600
  python3 tools/p1sim/growthrun.py p2econ '{...}' --players 600 --weeks 12 --abyss --raid --goals --every-week --dodge 0.5
  python3 tools/p1sim/growthrun.py weeks out.md   # two-极品 30% week (linear between weeks), fastest column
Build "off" = growth off (baseline with the same code).
"""
import sys, json, os, re
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))


def two_week(path, col='两件极品', at=30.0):
    rows = {}
    head = None
    for line in open(path, encoding='utf-8'):
        if line.startswith('| 周 |'):
            head = [x.strip() for x in line.strip().strip('|').split('|')]
            continue
        if head and re.match(r'^\| \d+ \|', line):
            c = [x.strip() for x in line.strip().strip('|').split('|')]
            w, mode, v = int(c[0]), c[1], float(c[head.index(col)].rstrip('%'))
            rows.setdefault(mode, []).append((w, v))
    best = {}
    for mode, l in rows.items():
        l.sort()
        prev = (0, 0.0)
        for w, v in l:
            if v >= at:
                best[mode] = prev[0] + (w - prev[0]) * (at - prev[1]) / max(1e-9, v - prev[1])
                break
            prev = (w, v)
    return best


if __name__ == '__main__':
    tool = sys.argv[1]
    if tool == 'weeks':
        for p in sys.argv[2:]:
            b = two_week(p)
            print('%s fastest %.2f (%s)' % (p, min(b.values()) if b else float('nan'), ', '.join('%s %.2f' % kv for kv in sorted(b.items(), key=lambda x: x[1]))))
        sys.exit(0)
    build = sys.argv[2]
    rest = sys.argv[3:]
    import p1sim, growth
    if build != 'off':
        b = json.loads(build)
        g = growth.load()
        row3 = {'scorch': 't3a', 'burst': 't3b', 'sustain': 't3c'}
        def fn(info, ctx):
            t = list(b.get('talents', []))
            if len(t) >= 2 and row3.get(info.get('set')):
                t.append(row3[info.get('set')])
            return growth.build(g, t, b.get('honors', ()), b.get('affixes'), b.get('affix_tier', 4))(info, ctx)
        p1sim.GROWTH = fn
    if tool == 'p1sim':
        p1sim.main(rest)
    else:
        import p2econ
        sys.argv = ['p2econ'] + rest
        p2econ.main()

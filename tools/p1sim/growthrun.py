"""D141–D143 growth runs. Two DIFFERENT questions (review B03, 2026-10-04):

  (1) 上限效果检查 / upper-bound effect check — `p1sim` / `p2econ` with a growth build switched ON FOR EVERYONE FROM THE
      START at no cost (talents, honors, affix tier as given; row-3 talent follows the worn set). Answers "how much can
      the finished build move clear rates / progression at most". It is NOT a growth simulation: nobody pays for anything.
  (2) 真实成本成长模拟 / real-cost growth sim — `p2econ-real` / `p1sim-real`: every player EARNS talent points (q03 / q05 /
      q07 first clears, first challenge clear, first raid clear, abyss floor 5), BUYS rows with coins (800 / 2000 / 4000),
      earns honors by their conditions, and REROLLS affixes on its final T3 target-family pieces paying coin + shards
      (+ lock shards) with the real tier weights, quality cap and pity (EmberAffix.roll); a replaced item loses its affix.
      Policies: gear-first (spend on growth only from coins above the reserve after the normal gear investment, never
      while saving for a tier upgrade) and growth-first (growth before gear).

Usage (repo root):
  python3 tools/p1sim/growthrun.py p1sim  '{"talents":["t1b","t2a"],"honors":"all","affixes":{...},"affix_tier":4}' --players 600
  python3 tools/p1sim/growthrun.py p2econ '{...}' --players 600 --weeks 12 --abyss --raid --goals --every-week --dodge 0.5
  python3 tools/p1sim/growthrun.py p2econ-real '{"talents":["t1b","t2a"],"affixes":{"blade":"b_set","charm":"c_tele"},"policy":"gear"}' …
  python3 tools/p1sim/growthrun.py weeks out.md …   # two-极品 ownership weeks (B04 names, see own_weeks)
Build "off" = growth off (baseline with the same code).
"""
import sys, json, os, re
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))


def own_rows(path, col='两件极品'):
    """{route (方案): [(week, ownership %), …]} from a p2econ table"""
    rows, head = {}, None
    for line in open(path, encoding='utf-8'):
        if line.startswith('| 周 |'):
            head = [x.strip() for x in line.strip().strip('|').split('|')]
            continue
        if head and re.match(r'^\| \d+ \|', line):
            c = [x.strip() for x in line.strip().strip('|').split('|')]
            rows.setdefault(c[1], []).append((int(c[0]), float(c[head.index(col)].rstrip('%'))))
    for l in rows.values():
        l.sort()
    return rows


def cross(l, at):
    """first week the ownership share reaches `at` %, linear between whole weeks (week 0 = 0 %); None = not by the cutoff"""
    prev = (0, 0.0)
    for w, v in l:
        if v >= at:
            return prev[0] + (w - prev[0]) * (at - prev[1]) / max(1e-9, v - prev[1])
        prev = (w, v)
    return None


def own_weeks(path, col='两件极品'):
    """B04 (review 2026-10-04): the old "two-极品 fastest week" is really
    W30 = 候选策略中达到 30% 双极品拥有率的最早插值周 (earliest interpolated week at which ANY candidate route has 30 % of
    its players owning two 极品; weeks count from the player's own Q07 first clear, table rows are whole weeks).
    Per route also: P50 / P90 = weeks until 50 % / 90 % of players own two 极品 (= median / 90th percentile of the
    per-player time, from the same weekly ownership curve), and the share still without two 极品 at the cutoff."""
    out = {}
    for mode, l in own_rows(path, col).items():
        out[mode] = {'w30': cross(l, 30.0), 'p50': cross(l, 50.0), 'p90': cross(l, 90.0),
                     'cutoff': l[-1][0], 'unreached': 100.0 - l[-1][1]}
    return out


def two_week(path, col='两件极品', at=30.0):
    """legacy name kept for old scripts: {route: interpolated week the ownership reaches `at` %} (see own_weeks)"""
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
        f = lambda x: '未达' if x is None else '%.2f' % x
        for p in sys.argv[2:]:
            ow = own_weeks(p)
            w30 = [v['w30'] for v in ow.values() if v['w30'] is not None]
            print('%s  W30（候选策略中达到 30%% 双极品拥有率的最早插值周）= %s' % (p, f(min(w30)) if w30 else '未达'))
            for mode, v in sorted(ow.items(), key=lambda kv: (kv[1]['w30'] is None, kv[1]['w30'] or 0)):
                print('  %s: W30 %s · P50 %s · P90 %s · 第 %d 周仍未拥有双极品 %.0f%%' % (mode, f(v['w30']), f(v['p50']), f(v['p90']), v['cutoff'], v['unreached']))
        sys.exit(0)
    if tool in ('p2econ-real', 'p1sim-real'):
        import realcost
        realcost.install(json.loads(sys.argv[2]))
        rest = sys.argv[3:]
        if tool == 'p1sim-real':
            import p1sim
            p1sim.main(rest)
        else:
            import p2econ
            sys.argv = ['p2econ'] + rest
            p2econ.main()
            realcost.report()
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

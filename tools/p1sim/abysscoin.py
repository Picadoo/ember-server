"""Abyss coin check (2026-10-04): are abyss players coin-starved by the tier fees once the §5.3 craft/quality
sinks are on? Runs the same p2econ phase 2 as `p2econ.py --abyss` (modes 无轮换 / P2-2 深渊, same seeds) with a
per-week coin ledger: income (settle + bounty), fees (coin; T3 marks counted apart), enhance + tier upgrade,
potions, craft/quality (forge). Also per player: weeks where a worn T3 piece still has a craft/quality step to buy
but the coins after the week cannot pay the cheapest one above the 1000 reserve ("starved"), the first week with
two 极品, and the first week with craft 3 + quality ≥ 卓越 on both pieces ("forged").
Usage: python3 tools/p1sim/abysscoin.py --players 60 --weeks 8 --dodge 0.5 [--abyss-fees 0,60,…]
"""
import argparse, os, random, statistics, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, p2econ

CATS = ('income', 'fee', 'fee_marks', 'enhance', 'potion', 'forge')


def week_of(p):
    d = getattr(p, 'day', None)
    return (d - 1000) // 7 if isinstance(d, int) and d >= 1000 else None


def book(p, cat, v):
    w = week_of(p)
    if w is None or not hasattr(p, '_led'):
        return
    p._led.setdefault(w, dict.fromkeys(CATS, 0))[cat] += v


def patch():
    P = p1sim.Player
    o_settle, o_invest, o_pot = P.settle, P.invest, P.buy_potions

    def settle(self, *a, **k):
        c0 = self.coin; r = o_settle(self, *a, **k); book(self, 'income', self.coin - c0); return r

    def invest(self, *a, **k):
        c0 = self.coin; r = o_invest(self, *a, **k); book(self, 'enhance', c0 - self.coin); return r

    def pot(self, *a, **k):
        c0 = self.coin; r = o_pot(self, *a, **k); book(self, 'potion', c0 - self.coin); return r
    P.settle, P.invest, P.buy_potions = settle, invest, pot
    o_fee, o_forge = p2econ.pay_fee, p2econ.forge_sink

    def pay_fee(p, fee, reserve):
        c0, m0 = p.coin, p.marks[3]
        o_fee(p, fee, reserve)
        book(p, 'fee', c0 - p.coin); book(p, 'fee_marks', m0 - p.marks[3])

    def forge(p, reserve=None):
        s = o_forge(p, reserve); book(p, 'forge', s); return s
    p2econ.pay_fee, p2econ.forge_sink = pay_fee, forge


def next_step(p):
    """cheapest craft/quality step still open on a worn T3 target piece (None = forged out)"""
    c = []
    for it in (p.blade, p.charm):
        if it['tier'] < 3 or it['fam'] == 'none':
            continue
        if it['f'] < 3:
            c.append(p2econ.CRAFT_COIN[it['f']])
        if it['q'] < 2:
            c.append(p2econ.QUALITY_COIN[it['q']])
    return min(c) if c else None


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--players', type=int, default=60)
    ap.add_argument('--weeks', type=int, default=8)
    ap.add_argument('--dodge', type=float, default=0.5)
    ap.add_argument('--abyss-fees')
    ap.add_argument('--forge-first', action='store_true', help='abyss players forge each morning before tier fees')
    a = ap.parse_args()
    p2econ.ABYSS_FORGE_FIRST = a.forge_first
    if a.abyss_fees:
        p2econ.set_abyss(fees=a.abyss_fees)
    patch()
    cfg = p1config.load()
    ccfg = p2econ.challenge_cfg(cfg)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    res = {'base': [], 'abyss': []}
    for i in range(a.players):
        for mode in res:
            kn = p1sim.Knobs(a.dodge, normal_mods=None, feat_normal='config', feat_farm=False)
            p, runs, rng = p2econ.to_q07(cfg, kn, 5000 + i)
            if p is None:
                continue
            kn.swap = True
            kn.quality_chase = p2econ.FORGE_SINK
            p._led = {}
            # wrap the weekly forge call to snapshot per-week state after it
            snaps = {}
            o_forge = p2econ.forge_sink

            def forge(pp, reserve=None, _o=o_forge, _p=p, _s=snaps):
                s = _o(pp, reserve)
                if pp is _p and week_of(pp) is not None:
                    ns = next_step(pp)
                    _s[week_of(pp)] = ({'coin': pp.coin, 'next': ns, 'starved': ns is not None and pp.coin - ns < p2econ.FORGE_RESERVE,
                               'two': pp.blade['q'] >= 3 and pp.charm['q'] >= 3, 'forged': ns is None and pp.blade['tier'] == 3 and pp.charm['tier'] == 3,
                               'craft': (pp.blade['f'] + pp.charm['f']) / 2, 'B': pp.st()['B']})
                return s
            p2econ.forge_sink = forge
            if mode == 'abyss':
                out = p2econ.phase2_abyss(cfg, ccfg, kn, p, random.Random(9000 + i), a.weeks, per_day)
            else:
                out = p2econ.phase2(cfg, ccfg, kn, p, random.Random(9000 + i), a.weeks, False, per_day, runs)
            p2econ.forge_sink = o_forge
            res[mode].append({'out': out, 'led': p._led, 'snap': [snaps[k] for k in sorted(snaps)]})
    name = {'base': '无轮换（不打深渊）', 'abyss': 'P2-2 深渊'}
    print('# abysscoin: %d players, dodge %.2f, %d weeks%s, fees %s' % (len(res['base']), a.dodge, a.weeks, ' · abyss forge-first' if a.forge_first else '',
          '/'.join(str(r['fee']) for r in p2econ.ABYSS)))
    print()
    print('| 周 | 方案 | 币（中位） | P25 | 本周收入 | 层费（币） | 层费（印记） | 强化+升阶 | 药水 | 精工/成色 | 精工均值 | 还差精工/成色且币不够下一步（占比） | 两件极品 | 精工成色做满 | 卡币天数（深渊，累计均值） |')
    print('|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|')
    for w in range(a.weeks):
        for mode in res:
            rs = res[mode]
            n = len(rs)
            med = lambda f: statistics.median(f(r) for r in rs)
            led = lambda r, c: r['led'].get(w, {}).get(c, 0)
            coins = sorted(r['snap'][w]['coin'] for r in rs)
            print('| %d | %s | %d | %d | %d | %d | %d | %d | %d | %d | %.1f | %d%% | %d%% | %d%% | %s |' % (
                w + 1, name[mode], med(lambda r: r['snap'][w]['coin']), coins[n // 4],
                med(lambda r: led(r, 'income')), med(lambda r: led(r, 'fee')), med(lambda r: led(r, 'fee_marks')),
                med(lambda r: led(r, 'enhance')), med(lambda r: led(r, 'potion')), med(lambda r: led(r, 'forge')),
                med(lambda r: r['snap'][w]['craft']),
                round(100 * sum(r['snap'][w]['starved'] for r in rs) / n), round(100 * sum(r['snap'][w]['two'] for r in rs) / n),
                round(100 * sum(r['snap'][w]['forged'] for r in rs) / n),
                ('%.1f' % statistics.mean(r['out'][w]['blocked'] for r in rs)) if mode == 'abyss' else '—'))
    print()
    for mode in res:
        rs = res[mode]
        sw = [sum(s['starved'] for s in r['snap']) for r in rs]
        run = []
        for r in rs:
            best = cur = 0
            for s in r['snap']:
                cur = cur + 1 if s['starved'] else 0
                best = max(best, cur)
            run.append(best)
        fw = [next((k + 1 for k, s in enumerate(r['snap']) if s['forged']), a.weeks + 1) for r in rs]
        tw = [next((k + 1 for k, s in enumerate(r['snap']) if s['two']), a.weeks + 1) for r in rs]
        tot = {c: statistics.median(sum(v.get(c, 0) for v in r['led'].values()) for r in rs) for c in CATS}
        print('- %s：卡精工周数中位 %d（均值 %.1f），最长连续 %d 周中位（≥2 周的人 %d%%）；精工成色做满周中位 %s；两件极品周中位 %s；'
              '%d 周累计（中位）收入 %d · 层费 %d 币 + %d 印记 · 强化/升阶 %d · 药水 %d · 精工/成色 %d；第 %d 周 B 中位 %.1f' % (
                  name[mode], statistics.median(sw), statistics.mean(sw), statistics.median(run),
                  round(100 * sum(x >= 2 for x in run) / len(run)),
                  _wk(statistics.median(fw), a.weeks), _wk(statistics.median(tw), a.weeks), a.weeks, tot['income'], tot['fee'],
                  tot['fee_marks'], tot['enhance'], tot['potion'], tot['forge'], a.weeks, statistics.median(r['snap'][-1]['B'] for r in rs)))


def _wk(v, weeks):
    return '>%d' % weeks if v > weeks else '%g' % v


if __name__ == '__main__':
    main()

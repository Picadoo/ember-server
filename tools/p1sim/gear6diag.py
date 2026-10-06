#!/usr/bin/env python3
"""D246 offline diagnosis of the Stage 0 farm-route W30 shift (6-slot vs 2-slot), farm routes only.

Per player (same seeds as p2econ): phase-1 runs to the Q07 first clear, stats at that point, the first phase-2 week the
player can farm challenge (rates[best] >= 0.5), the week both worn blade and charm are quality 3, and how each q3
piece was obtained (drop tier / mode / source, kept-and-upgraded vs dropped at T3). Reads P1SIM_SIX like p2econ.
Usage: P1SIM_SIX='{...}' python3 gear6diag.py --players 300 --weeks 12 [--mode base|rot] [--json out.json]
"""
import argparse, json, random, statistics as S
import p1sim, p1config, p2econ

TRACE = {}


def install():
    orig_consider = p1sim.Player.consider
    def consider(self, new):
        new.setdefault('_born', (new['tier'], TRACE.get('phase'), TRACE.get('week'), new.get('src'), TRACE.get('ctx')))
        if TRACE.get('phase') == 2 and new['slot'] in ('blade', 'charm') and new['q'] < 3:
            cur = self.blade if new['slot'] == 'blade' else self.charm
            r = orig_consider(self, new)
            now = self.blade if new['slot'] == 'blade' else self.charm
            if now is not cur and cur['q'] >= 3:
                c = TRACE['cnt']; k = 'q3_lost_' + new['slot'] + ('_tgt' if new['fam'] == self.kn.target else '_off')
                c[k] = c.get(k, 0) + 1
            return r
        if TRACE.get('phase') == 2 and new['slot'] in ('blade', 'charm') and new['q'] >= 3:
            c = TRACE['cnt']; k = 'q3_' + new['slot'] + ('_tgt' if new['fam'] == self.kn.target else '_off')
            c[k] = c.get(k, 0) + 1
            cur = self.blade if new['slot'] == 'blade' else self.charm
            r = orig_consider(self, new)
            now = self.blade if new['slot'] == 'blade' else self.charm
            if now is not cur and now['q'] >= 3:
                c['q3_taken'] = c.get('q3_taken', 0) + 1
            elif new['fam'] == self.kn.target:
                why = 'rej_curq3' if cur['q'] >= 3 else ('rej_tier' if new['tier'] < cur['tier'] else ('rej_curoff' if cur['fam'] != self.kn.target else 'rej_other'))
                c[why] = c.get(why, 0) + 1
            return r
        return orig_consider(self, new)
    p1sim.Player.consider = consider
    orig_farm = p2econ.farm_map
    def farm_map(p, kn, ccfg, rates, best):
        TRACE['can_ch'].add(TRACE.get('week'))
        return orig_farm(p, kn, ccfg, rates, best)
    p2econ.farm_map = farm_map
    orig_settle_with = p2econ.settle_with
    def settle_with(p, ccfg, key, extra):
        TRACE['ctx'] = 'ch'
        if TRACE.get('phase') == 2:
            TRACE['cnt']['ch_clears'] = TRACE['cnt'].get('ch_clears', 0) + 1
        try:
            return orig_settle_with(p, ccfg, key, extra)
        finally:
            TRACE['ctx'] = 'n'
    p2econ.settle_with = settle_with
    orig_fs = p2econ.forge_sink
    def forge_sink(p, reserve=None):
        r = orig_fs(p, reserve)
        w = TRACE['week']
        TRACE['wk'].append((w, p.blade['q'], p.charm['q'], p.blade['tier'], p.charm['tier'],
                            p.blade.get('_born'), p.charm.get('_born'), p.st()['H'], p.st()['B'], p.blade['fam'] != p.kn.target, p.charm['fam'] != p.kn.target))
        TRACE['week'] = w + 1
        return r
    p2econ.forge_sink = forge_sink


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--players', type=int, default=300)
    ap.add_argument('--weeks', type=int, default=12)
    ap.add_argument('--dodge', type=float, default=0.5)
    ap.add_argument('--mode', default='base', choices=['base', 'rot'])
    ap.add_argument('--first', type=int, default=0)
    ap.add_argument('--json')
    a = ap.parse_args()
    install()
    cfg = p1config.load()
    ccfg = p2econ.challenge_cfg(cfg)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    rows = []
    for i in range(a.first, a.first + a.players):
        kn = p1sim.Knobs(a.dodge, normal_mods=None, feat_normal='config', feat_farm=False)
        TRACE.update(phase=1, week=None, ctx='n')
        p, runs, rng = p2econ.to_q07(cfg, kn, 5000 + i)
        if p is None:
            rows.append({'i': i, 'q07': None})
            continue
        kn.swap = True
        kn.quality_chase = p2econ.FORGE_SINK
        st = p.st()
        r = {'i': i, 'q07': runs, 'H0': st['H'], 'B0': st['B'], 'bq0': p.blade['q'], 'cq0': p.charm['q'],
             'bt0': p.blade['tier'], 'ct0': p.charm['tier'],
             'arm0': [(x['tier'], x['q'], x['enh']) for x in p.armor] if p.armor else None,
             'res0': (p.coin, p.shard, p.core, p.blank)}
        p0b, p0c = p.blade['fam'] != kn.target, p.charm['fam'] != kn.target
        TRACE.update(phase=2, week=1, ctx='n', can_ch=set(), wk=[], cnt={})
        out = p2econ.phase2(cfg, ccfg, kn, p, random.Random(9000 + i), a.weeks, a.mode == 'rot', per_day, runs)
        r['can_ch'] = min(TRACE['can_ch']) if TRACE['can_ch'] else None
        hit = next((o['week'] for o in out if o['qmin'] >= 3), None)
        r['two_q3'] = hit
        if hit:
            wk = TRACE['wk'][hit - 1]
            r['born'] = [wk[5], wk[6]]
        r['H'] = [round(x[7], 1) for x in TRACE['wk']]
        r['cnt'] = dict(TRACE['cnt'])
        r['off'] = [(int(x[9]), int(x[10])) for x in TRACE['wk']]
        r['off0'] = (int(p0b), int(p0c))
        rows.append(r)
    ok = [r for r in rows if r.get('q07') is not None]
    def med(xs):
        xs = [x for x in xs if x is not None]
        return S.median(xs) if xs else None
    print('players %d reached Q07 %d' % (len(rows), len(ok)))
    print('median runs to Q07 FC %.1f  H0 %.1f  B0 %.1f' % (med([r['q07'] for r in ok]), med([r['H0'] for r in ok]), med([r['B0'] for r in ok])))
    print('at Q07 FC: blade q3 %.3f charm q3 %.3f  blade T3 %.3f charm T3 %.3f' % tuple(
        sum(1 for r in ok if f(r)) / len(ok) for f in (lambda r: r['bq0'] >= 3, lambda r: r['cq0'] >= 3,
                                                        lambda r: r['bt0'] >= 3, lambda r: r['ct0'] >= 3)))
    cc = [r['can_ch'] for r in ok]
    print('can farm challenge by week: ' + ' '.join('w%d %.2f' % (w, sum(1 for c in cc if c is not None and c <= w) / len(ok)) for w in range(1, a.weeks + 1)))
    tq = [r['two_q3'] for r in ok]
    print('two q3 by week: ' + ' '.join('w%d %.3f' % (w, sum(1 for c in tq if c is not None and c <= w) / len(ok)) for w in range(1, a.weeks + 1)))
    src = {}
    for r in ok:
        for b in r.get('born') or []:
            if b is None:
                k = 'none'
            else:
                k = 'T%s/%s/%s' % (b[0], 'p1' if b[1] == 1 else 'p2', b[4] if b[1] == 2 else '-')
            src[k] = src.get(k, 0) + 1
    print('q3 piece origins at the two-q3 week: ' + ', '.join('%s %d' % kv for kv in sorted(src.items(), key=lambda kv: -kv[1])))
    print('median H by week: ' + ' '.join('%.0f' % med([r['H'][w] for r in ok if len(r['H']) > w]) for w in range(a.weeks)))
    print('off-family worn at Q07 FC: blade %.3f charm %.3f' % (sum(r['off0'][0] for r in ok) / len(ok), sum(r['off0'][1] for r in ok) / len(ok)))
    print('off-family worn by week (blade/charm): ' + ' '.join('w%d %.2f/%.2f' % (w + 1, sum(r['off'][w][0] for r in ok) / len(ok), sum(r['off'][w][1] for r in ok) / len(ok)) for w in range(a.weeks)))
    tot = {}
    for r in ok:
        for k, v in (r.get('cnt') or {}).items():
            tot[k] = tot.get(k, 0) + v
    print('phase-2 totals per player: ' + ', '.join('%s %.2f' % (k, v / len(ok)) for k, v in sorted(tot.items())))
    if a.json:
        json.dump(rows, open(a.json, 'w'), default=str)


if __name__ == '__main__':
    main()


# ---------------------------------------------------------------- D246: pooled W30 (farm routes) with large samples
def run_slice(job):
    """(six, first, n, mode, weeks) → per-player rows (same flow as main(); p1sim.SIX set in this worker)."""
    six, first, n, mode, weeks = job
    p1sim.SIX = six
    if not getattr(p1sim.Player.consider, '_d246', False):
        install()
        p1sim.Player.consider._d246 = True
    cfg = p1config.load()
    ccfg = p2econ.challenge_cfg(cfg)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    rows = []
    for i in range(first, first + n):
        kn = p1sim.Knobs(0.5, normal_mods=None, feat_normal='config', feat_farm=False)
        TRACE.update(phase=1, week=None, ctx='n')
        p, runs, rng = p2econ.to_q07(cfg, kn, 5000 + i)
        if p is None:
            continue
        kn.swap = True
        kn.quality_chase = p2econ.FORGE_SINK
        off0 = (p.blade['fam'] != kn.target) or (p.charm['fam'] != kn.target)
        TRACE.update(phase=2, week=1, ctx='n', can_ch=set(), wk=[], cnt={})
        out = p2econ.phase2(cfg, ccfg, kn, p, random.Random(9000 + i), weeks, mode == 'rot', per_day, runs)
        rows.append({'i': i, 'two': next((o['week'] for o in out if o['qmin'] >= 3), None), 'off0': off0,
                     'off12': (p.blade['fam'] != kn.target) or (p.charm['fam'] != kn.target),
                     'tgt12': out[-1]['set'], 'cnt': dict(TRACE['cnt'])})
    return rows


def w30_of(share):
    prev = 0.0
    for w, v in enumerate(share, 1):
        if v >= 0.3:
            return w - 1 + (0.3 - prev) / (v - prev) if v > prev else float(w)
        prev = v
    return None


def pooled(six, players=1200, mode='base', weeks=12, procs=8, chunk=75):
    from multiprocessing import Pool
    jobs = [(six, f, min(chunk, players - f), mode, weeks) for f in range(0, players, chunk)]
    with Pool(procs) as pool:
        rows = [r for part in pool.map(run_slice, jobs) for r in part]
    n = len(rows)
    share = [sum(1 for r in rows if r['two'] and r['two'] <= w) / n for w in range(1, weeks + 1)]
    half = [rows[:n // 2], rows[n // 2:]]
    hw = [w30_of([sum(1 for r in h if r['two'] and r['two'] <= w) / len(h) for w in range(1, weeks + 1)]) for h in half]
    return {'n': n, 'W30': w30_of(share), 'W30_halves': hw, 'w12': share[-1], 'off0': sum(r['off0'] for r in rows) / n,
            'off12': sum(r['off12'] for r in rows) / n, 'tgt12': sum(r['tgt12'] for r in rows) / n,
            'rej_curoff': sum(r['cnt'].get('rej_curoff', 0) for r in rows) / n, 'share': share}


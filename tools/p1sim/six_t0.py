#!/usr/bin/env python3
"""D313 · six-slot Stage1 T0 (docs/design/DESIGN-ember-six-slot-stage1-decision-2026-10-08.md §2.2.4). Offline only;
p1sim.SIX stays None unless a command sets it inside its own process (default runs unchanged).

  python3 six_t0.py g56 '<SIX json>'                              # G5 B zero drift + G6 migration invariant (exhaustive)
  P1SIM_CRN=1 python3 six_t0.py afk '<SIX json>' [PLAYERS] [OUT.json]   # G8 挂机庭: 2-slot+AFK vs 6-slot+AFK, 21 cells
  P1SIM_CRN=1 python3 six_t0.py raid '<SIX json>' [OUT.json] [--trials T] [--procs P]   # G8 团本: R01–R03 natural teams
  python3 six_t0.py raidrep OUT.json                              # + D316 G10 per-player lag table when OUT has 'g10'

G6 compares the 6-slot formula (charm + 4 armor pieces with the charm's tier / enhance / quality / craft) to the 2-slot
formula with exact float equality and with |Δ| ≤ 1e-9; T1 mirrors it as a Java unit test.
G8 afk: same player seeds, AFK share 1.0 on both arms (afk.install wraps Player.bounty), first-clear rate before the first
clear per map (p1sim.summarize front_rate), Δpp = 6-slot − 2-slot. G8 raid: the raidcomp.py player pools (36 players per
pool, 7 pools, to_q07 + 2 phase-2 weeks) generated once with SIX = None and once with SIX, natural random teams of
3 / 4 / 5 (p1party.evaluate, identical lineups / seeds), Δpp per pool → pool-clustered 95% CI.
"""
import itertools
import json
import os
import random
import statistics
import sys
import multiprocessing as mp

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config  # noqa: E402
import p1sim  # noqa: E402


def g56(six):
    cfg = p1config.load()
    n = exact = 0
    worst = {'H': 0.0, 'M': 0.0, 'EHP': 0.0, 'D': 0.0}
    bdrift = 0
    fams = ('burst', 'scorch', 'sustain')
    for fam, bt, ct, be, ce, q, f, lv in itertools.product(fams, range(4), range(4), (0, 3, 6, 10), range(11), range(4), range(4), (1, 15, 30)):
        bl = dict(p1sim.item(fam if bt else 'none', 'blade', bt), enh=be)
        ch = dict(p1sim.item(fam if ct else 'none', 'charm', ct), enh=ce, q=q, f=f)
        arm = [dict(ch, slot=a) for a in p1sim.ARMOR_SLOTS]
        p1sim.SIX = None
        s2 = p1sim.stats(cfg, bl, ch, lv)
        h2, d2 = p1sim.hp_def(cfg, ch)
        p1sim.SIX = six
        s6 = p1sim.stats(cfg, bl, ch, lv, None, arm)
        h6, d6 = p1sim.hp_def(cfg, ch, arm)
        p1sim.SIX = None
        n += 1
        bdrift += s6['B'] != s2['B']
        e2, e6 = s2['H'] / s2['M'], s6['H'] / s6['M']
        same = s6['H'] == s2['H'] and s6['M'] == s2['M'] and d6 == d2 and e6 == e2
        exact += same
        for k, a, b in (('H', s6['H'], s2['H']), ('M', s6['M'], s2['M']), ('EHP', e6, e2), ('D', d6, d2)):
            worst[k] = max(worst[k], abs(a - b))
    # G5 also on the static gear6 reference contexts (book §3.2 + challenge T3+6) with a lagging armor set
    ref_b = 0
    for k, (bt, be, ct, ce, lv) in list(p1sim.REF_GEAR.items()) + [('c', (3, 6, 3, 6, 30))]:
        for fam in fams:
            bl = dict(p1sim.item(fam if bt else 'none', 'blade', bt), enh=be)
            ch = dict(p1sim.item(fam if ct else 'none', 'charm', ct), enh=ce)
            for arm_t in range(4):
                arm = [dict(ch, slot=a, tier=arm_t, enh=0) for a in p1sim.ARMOR_SLOTS]
                p1sim.SIX = None
                b2 = p1sim.stats(cfg, bl, ch, lv)['B']
                p1sim.SIX = six
                b6 = p1sim.stats(cfg, bl, ch, lv, None, arm)['B']
                p1sim.SIX = None
                ref_b += b6 != b2
    print('G6 combos %d · exact float equality %d/%d · max |Δ| H %.3g M %.3g EHP %.3g D %.3g' % (
        n, exact, n, worst['H'], worst['M'], worst['EHP'], worst['D']))
    print('G6 within 1e-9: %s' % all(v <= 1e-9 for v in worst.values()))
    print('G5 B drift: %d/%d formula combos · %d/%d reference contexts × armor tier 0–3' % (bdrift, n, ref_b, 8 * 3 * 4))
    return {'n': n, 'exact': exact, 'worst': worst, 'bdrift': bdrift, 'ref_bdrift': ref_b}


def _afk_job(a):
    six, d, players, seed0 = a
    import afk
    cfg = p1config.load()
    per_day = cfg['stamina_day'] // cfg['run_cost']
    afk.install(1.0)
    out = {}
    for lab, sx in (('two', None), ('six', six)):
        p1sim.SIX = sx
        afk.STATE['share'] = 1.0
        rows, _ = p1sim.summarize(cfg, p1sim.Knobs(d), players, 60 * per_day, seed0=seed0)
        out[lab] = rows
    p1sim.SIX = None
    return d, seed0, out


def afk_dyn(six, players=800, out=None, procs=4, chunks=6):
    jobs = []
    step = players // chunks
    for d in (0.3, 0.5, 0.7):
        for c in range(chunks):
            jobs.append((six, d, step, 1000 + c * step))
    with mp.Pool(procs) as p:
        res = p.map(_afk_job, jobs, chunksize=1)
    # merge chunks: recompute front rate from counts → need raw; summarize gives rates, so weight by chunk (equal size)
    cells = {}
    for d, s0, o in res:
        for lab in ('two', 'six'):
            for r in o[lab]:
                cells.setdefault((d, r['map'], lab), []).append(r)
    print('| 躲避 | 图 | 2 槽+挂机 首通前通关率 | 6 槽+挂机 | Δpp [分块 95%% 区间，%d 块] | 首通中位天 2 槽 / 6 槽 | 首通 H 中位 2 槽 / 6 槽 |' % chunks)
    print('|---|---|---:|---:|---:|---|---|')
    inside = 0
    worst = (0.0, None)
    table = []
    for d in (0.3, 0.5, 0.7):
        for m in ('q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07'):
            two, sx = cells[(d, m, 'two')], cells[(d, m, 'six')]
            f2 = statistics.mean(r['front_rate'] for r in two if r['front_rate'] is not None)
            f6 = statistics.mean(r['front_rate'] for r in sx if r['front_rate'] is not None)
            dpp = 100 * (f6 - f2)
            per = [100 * (b['front_rate'] - a['front_rate']) for a, b in zip(two, sx) if a['front_rate'] is not None and b['front_rate'] is not None]
            import raidcomp
            _, half = raidcomp.ci(per)
            inside += abs(dpp) <= 2
            if abs(dpp) > abs(worst[0]):
                worst = (dpp, '%.1f %s' % (d, m.upper()))
            fd = lambda rs: statistics.median([r['fc_day'] for r in rs if r['fc_day'] is not None] or [0])
            hh = lambda rs: statistics.median([r['H'] for r in rs if r['H'] is not None] or [0])
            print('| %.1f | %s | %.1f | %.1f | %+.1f [%+.1f, %+.1f] | %g / %g | %.0f / %.0f |' % (d, m.upper(), 100 * f2, 100 * f6, dpp, dpp - half, dpp + half, fd(two), fd(sx), hh(two), hh(sx)))
            table.append([d, m, f2, f6, dpp, half])
    print('\n**G8 挂机 %d/21 格 ±2pp 内**（最大 |Δ| %+.1f pp @ %s；%d 人/躲避，AFK share 1.0，CRN 配对）' % (inside, worst[0], worst[1], players))
    if out:
        json.dump({'six': six, 'players': players, 'table': table}, open(out, 'w'))


def _pool(cfg, dodge, seed0):
    """raidcomp.players (same loop, same seeds, same RNG use) that also returns each player's seed index, so the
    2-slot and 6-slot pools pair player by player (D316 G10)."""
    import raidcomp, p2econ, growth
    raidcomp.cfg = cfg
    growth.disable()
    ccfg = p2econ.challenge_cfg(cfg)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    out, i = [], 0
    while len(out) < raidcomp.POOL and i < raidcomp.POOL * 3:
        kn = p1sim.Knobs(dodge)
        p, runs, rng = p2econ.to_q07(cfg, kn, seed0 + i)
        i += 1
        if p is None:
            continue
        kn.swap = True
        p2econ.phase2(cfg, ccfg, kn, p, random.Random(seed0 + 99 + i), 2, False, per_day, runs)
        out.append((p, kn, i - 1))
    return out


def _pstate(p, st, sid):
    """D316 G10: one pool member — charm / armor (tier, enhance, quality, craft; armor = the piece's own values), B, H, M"""
    te = lambda it: [it['tier'], it['enh'], it['q'], it['f']]
    return {'sid': sid, 'charm': te(p.charm), 'blade': te(p.blade), 'armor': [te(a) for a in p.armor] if p.armor is not None else None,
            'B': st['B'], 'H': st['H'], 'M': st['M']}


def _raid_job(j):
    """one player pool (seed0): build the 2-slot and the 6-slot pool once, evaluate R01–R03 on both (D316: the pools do
    not depend on the raid, so this equals the D313 per-(raid, pool) jobs bit for bit, at a third of the pool cost)"""
    raids, seed0, six, trials = j
    import p1party, rules
    cfg = p1config.load()
    rr = rules.runs().get('raid_revive') or {}
    p1party.LAST_REVIVE_HP = float(rr.get('last_phase_hp', 0)); p1party.LAST_REVIVE_DELAY = float(rr.get('delay', 10))
    pools, states = {}, {}
    for lab, sx in (('two', None), ('six', six)):
        p1sim.SIX = sx
        ps = _pool(cfg, 0.5, seed0)
        pools[lab] = [(p.st(), kn) for p, kn, _ in ps]
        states[lab] = [_pstate(p, st, sid) for (p, _, sid), (st, _) in zip(ps, pools[lab])]
        p1sim.SIX = None
    res = []
    for raid in raids:
        m = p1party.raid_map(cfg, raid)
        out = {}
        for lab in ('two', 'six'):
            ev = p1party.evaluate(cfg, m, pools[lab], (3, 4, 5), trials, random.Random(11))
            out[lab] = {str(n): x['rate'] for n, x in ev.items()}
            out[lab + '_H'] = statistics.median(s['H'] for s, _ in pools[lab])
        res.append([[raid, seed0], out])
    return res, seed0, states


def raid(six, out, trials=1500, procs=4):
    import raidcomp
    js = [(('r01', 'r02', 'r03'), s, six, trials) for s in raidcomp.POOLS]
    with mp.Pool(procs) as p:
        rs = p.map(_raid_job, js, chunksize=1)
    res = sorted((x for r, _, _ in rs for x in r), key=lambda x: (x[0][0], raidcomp.POOLS.index(x[0][1])))
    json.dump({'six': six, 'trials': trials, 'results': res, 'g10': {str(s): st for _, s, st in rs}}, open(out, 'w'))
    raidrep(out)


def _q(xs, p):
    xs = sorted(xs)
    if not xs:
        return float('nan')
    k = (len(xs) - 1) * p
    lo = int(k)
    return xs[lo] + (xs[min(lo + 1, len(xs) - 1)] - xs[lo]) * (k - lo)


def g10(d):
    """D316 G10: per-player armor lag in the raid pools (Q07 first clear + 2 weeks) and the paired 6-slot vs 2-slot
    B / H / M gaps. Effective tier / enhance follow the charm under SIX['follow'] ('all': both, 'enh': enhance)."""
    cfg = p1config.load()
    fol = (d.get('six') or {}).get('follow')
    rows = []
    for s0, st in sorted(d['g10'].items(), key=lambda x: int(x[0])):
        two = {x['sid']: x for x in st['two']}
        for x in st['six']:
            ch = x['charm']
            arm = x['armor'] or []
            et = [ch[0] if fol == 'all' else a[0] for a in arm]
            ee = [ch[1] if fol in ('all', 'enh') else a[1] for a in arm]
            r = {'low': sum(t < ch[0] for t in et), 'own_low': sum(a[0] < ch[0] for a in arm),
                 'enh': statistics.mean(ee) - ch[1] if arm else 0.0, 'own_enh': statistics.mean(a[1] for a in arm) - ch[1] if arm else 0.0,
                 'q': ch[2] - statistics.mean(a[2] for a in arm) if arm else 0.0,
                 'f': ch[3] - statistics.mean(a[3] for a in arm) if arm else 0.0,
                 'qv': (cfg['q'][ch[2]] + cfg['f'][ch[3]]) - statistics.mean(cfg['q'][a[2]] + cfg['f'][a[3]] for a in arm) if arm else 0.0}
            y = two.get(x['sid'])
            if y:
                r.update(dB=100 * (x['B'] / y['B'] - 1), dH=100 * (x['H'] / y['H'] - 1), dM=100 * (x['M'] / y['M'] - 1))
            rows.append(r)
    pr = [r for r in rows if 'dH' in r]
    n2 = sum(len(st['two']) for st in d['g10'].values())
    print('\n### G10 · 团本池逐人落后分布（%d 池，6 槽 %d 人；与 2 槽同种子配对 %d 人 / 2 槽 %d 人；follow=%s）\n' % (
        len(d['g10']), len(rows), len(pr), n2, fol or '缺省'))
    print('| 指标 | 均值 | 中位 | p10 | p90 | 尾部 |')
    print('|---|---:|---:|---:|---:|---|')
    def line(lab, xs, tail, fmt='%+.2f'):
        if not xs:
            return
        print(('| %s | ' + fmt + ' | ' + fmt + ' | ' + fmt + ' | ' + fmt + ' | %s |') % (lab, statistics.mean(xs), statistics.median(xs), _q(xs, .1), _q(xs, .9), tail))
    lw = [r['low'] for r in rows]
    line('甲比护符低阶件数（有效）', lw, '≥1 件 %.1f%% · ≥2 件 %.1f%%' % (100 * statistics.mean(v >= 1 for v in lw), 100 * statistics.mean(v >= 2 for v in lw)), '%.2f')
    ol = [r['own_low'] for r in rows]
    if fol:
        line('（参考）甲件自身阶低于护符件数', ol, '≥1 件 %.1f%%' % (100 * statistics.mean(v >= 1 for v in ol)), '%.2f')
    en = [r['enh'] for r in rows]
    line('强化差（有效，甲均 − 护符）', en, '≤ −2 级 %.1f%%' % (100 * statistics.mean(v <= -2 for v in en)))
    if fol:
        line('（参考）甲件自身强化差', [r['own_enh'] for r in rows], '')
    qs = [r['q'] for r in rows]
    line('成色差（护符 − 甲均，档）', qs, '≥1 档 %.1f%%' % (100 * statistics.mean(v >= 1 for v in qs)))
    fs = [r['f'] for r in rows]
    line('精工差（护符 − 甲均，档）', fs, '≥1 档 %.1f%%' % (100 * statistics.mean(v >= 1 for v in fs)))
    line('成色+精工系数差（护符 − 甲均）', [r['qv'] for r in rows], '', '%+.4f')
    if pr:
        dh = [r['dH'] for r in pr]
        line('H 6 槽 vs 2 槽（%）', dh, '≤ −2%% %.1f%% · ≤ −4%% %.1f%%' % (100 * statistics.mean(v <= -2 for v in dh), 100 * statistics.mean(v <= -4 for v in dh)))
        db = [r['dB'] for r in pr]
        line('B 6 槽 vs 2 槽（%）', db, '≤ −2%% %.1f%%' % (100 * statistics.mean(v <= -2 for v in db)))
        dm = [r['dM'] for r in pr]
        line('M（受伤系数）6 槽 vs 2 槽（%，正 = 更脆）', dm, '≥ +2%% %.1f%%' % (100 * statistics.mean(v >= 2 for v in dm)))
    return rows


def raidrep(path):
    import raidcomp
    d = json.load(open(path))
    by = {}
    for (r, s0), o in d['results']:
        by.setdefault(r, []).append(o)
    print('| 团本 | 随机组队 | 2 槽 | 6 槽 | Δpp（池聚类 95%% 区间，%d 池 × %d 局） | 判定 | 队员 H 中位 2 槽 / 6 槽 |' % (len(by.get('r01', [])), d['trials']))
    print('|---|---|---:|---:|---|---|---|')
    inside = noise = fail = tot = 0
    for r, rs in sorted(by.items()):
        for n in ('3', '4', '5'):
            a = [100 * o['two'][n] for o in rs]; b = [100 * o['six'][n] for o in rs]
            dm, dh = raidcomp.ci([y - x for x, y in zip(a, b)])
            tot += 1
            ok = abs(dm) <= 2
            nz = not ok and dm - dh <= 2 and dm + dh >= -2
            inside += ok; noise += nz; fail += not ok and not nz
            print('| %s | %s 人 | %.1f | %.1f | %+.1f [%+.1f, %+.1f] | %s | %.0f / %.0f |' % (
                r.upper(), n, statistics.mean(a), statistics.mean(b), dm, dm - dh, dm + dh, '过' if ok else ('~ 噪声内' if nz else '✗'),
                statistics.mean(o['two_H'] for o in rs), statistics.mean(o['six_H'] for o in rs)))
    print('\n**G8 团本 %d/%d 格点估 ±2pp 内 · 含噪声 %d/%d · 整段 CI 在 ±2 外 %d 格**' % (inside, tot, inside + noise, tot, fail))
    if d.get('g10'):
        g10(d)


if __name__ == '__main__':
    cmd = sys.argv[1]
    if cmd == 'g56':
        g56(json.loads(sys.argv[2]))
    elif cmd == 'afk':
        afk_dyn(json.loads(sys.argv[2]), int(sys.argv[3]) if len(sys.argv) > 3 else 800, sys.argv[4] if len(sys.argv) > 4 else None,
                int(os.environ.get('NPROC', '4')))
    elif cmd == 'raid':
        a = sys.argv
        tr = int(a[a.index('--trials') + 1]) if '--trials' in a else 1500
        pr = int(a[a.index('--procs') + 1]) if '--procs' in a else 4
        raid(json.loads(a[2]), a[3], tr, pr)
    elif cmd == 'raidrep':
        raidrep(sys.argv[2])
    else:
        sys.exit(__doc__)

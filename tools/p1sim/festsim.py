#!/usr/bin/env python3
"""D139 国庆活动 model: the festival charm slot (烟火迸发) against Q01–Q07 / raids, and the event boss for its target player.

Reads ember-v1-festival.yml (charm stats + effect, event dungeon) and the live runs / MM files through p1config.
1. Q01–Q07 at the book §3.2 reference loadouts (p1sim --ref), with and without the charm (same seeds).
2. Q01–Q07 front clear rate of the progression sim (players buy nothing else; charm worn from the start = worst case).
3. Raids R01–R03 (p1party, Q07 + 2 weeks pool, 3 players), with and without (every member wears it).
4. The event dungeon (p1sim.run_map on its own map def) for the Q04 / Q05 reference loadouts and for progression-sim
   players right after their own Q04 first clear (the entry requirement).
Standard library only. Usage: python3 festsim.py [--n 2000] [--players 120] [--trials 200]
"""
import argparse, copy, os, random, statistics, sys
sys.path.insert(0, os.path.dirname(__file__))
import miniyaml, p1config, p1sim, p1party, rules

FEST_FILE = 'CoreRpg/src/main/resources/ember-v1-festival.yml'


def fest_cfg():
    return rules.festival()  # M06: both copies checked equal (hard error) in rules.py


def charm_of(f, **over):
    c = f['charm']
    e = c['effect']
    d = {'hp': float(c.get('hp', 0)), 'def': float(c.get('def', 0)), 'coef': float(e['coef']), 'targets': int(e['targets']),
         'radius': float(e['radius']), 'icd': float(e['icd'])}
    d.update(over)
    return d


def event_map(cfg, f):
    m = copy.deepcopy(f['dungeon'])
    mm = rules.data('mm_fest')
    for role, mob in m['mobs'].items():
        row = mm.get(mob.get('mm'), {})
        if row and row.get('Health') != mob['hp']:
            print('WARNING: %s MM Health %s != %s' % (role, row.get('Health'), mob['hp']), file=sys.stderr)
    row = mm.get(m['boss']['mm'], {})
    if row and row.get('Health') != m['boss']['hp']:
        print('WARNING: boss MM Health %s != %s' % (row.get('Health'), m['boss']['hp']), file=sys.stderr)
    return m


def ref_rates(cfg, dodges, n, fest):
    p1sim.FEST = fest
    out = {}
    for k in cfg['order']:
        bt, be, ct, ce, lv = p1sim.REF_GEAR[k]
        bl = dict(p1sim.item('burst' if bt else 'none', 'blade', bt), enh=be)
        ch = dict(p1sim.item('burst' if ct else 'none', 'charm', ct), enh=ce)
        st = p1sim.stats(cfg, bl, ch, lv)
        out[k] = [p1sim.clear_rate(cfg, k, st, p1sim.Knobs(d), n) for d in dodges]
    p1sim.FEST = None
    return out


# book §3.2 reference RANGE per map (the --ref table takes only the upper end): blade tier, charm tier, enhance values
# (both pieces), levels. Averaging over the band smooths the one-point breakpoints (at the Q03 / Q04 reference point
# +1 % B or +2 H already moves the rate by 4–11 points, so a single point says little about a small source).
BAND = {'q01': (0, 0, (0,), (10, 11, 12)), 'q02': (1, 0, (0, 1, 2), (11, 12, 13)), 'q03': (1, 1, (0, 1, 2, 3), (13, 15, 17)),
        'q04': (1, 1, (3, 4, 5, 6), (17, 19, 21)), 'q05': (2, 2, (3, 4, 5, 6), (22, 24, 26)), 'q06': (2, 2, (5, 6, 7), (26, 28, 30)),
        'q07': (2, 2, (6, 7, 8), (28, 30, 32))}


def band_rates(cfg, dodges, n, fest):
    p1sim.FEST = fest
    out = {}
    for k in cfg['order']:
        bt, ct, enhs, lvs = BAND[k]
        acc = [0.0] * len(dodges)
        cells = 0
        for e in enhs:
            for lv in lvs:
                bl = dict(p1sim.item('burst' if bt else 'none', 'blade', bt), enh=e)
                ch = dict(p1sim.item('burst' if ct else 'none', 'charm', ct), enh=e if ct else 0)
                st = p1sim.stats(cfg, bl, ch, lv)
                for i, d in enumerate(dodges):
                    acc[i] += p1sim.clear_rate(cfg, k, st, p1sim.Knobs(d), n, seed=7 + cells)
                cells += 1
        out[k] = [x / cells for x in acc]
    p1sim.FEST = None
    return out


def front_rates(cfg, dodge, players, days, fest):
    p1sim.FEST = fest
    kn = p1sim.Knobs(dodge)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    rows, _ = p1sim.summarize(cfg, kn, players, days * per_day)
    p1sim.FEST = None
    return {r['map']: r for r in rows}


def raid_rates(cfg, keys, trials, fest, pool):
    out = {}
    for k in keys:
        m = p1party.raid_map(cfg, k)
        p1sim.FEST = fest
        if fest:  # the pool states were computed without the charm: add its HP / D the same way stats() does
            sts = []
            for st, kn in pool:
                s = dict(st)
                s['H'] = st['H'] + fest['hp'] * (cfg['sustain_hp'] if st['set'] == 'sustain' else 1.0)
                s['M'] = max(cfg['def_floor'], 1.0 / (1.0 / st['M'] + fest['def'] / cfg['def_k'])) if fest['def'] else st['M']
                sts.append((s, kn))
        else:
            sts = pool
        r = p1party.evaluate(cfg, m, sts, (3,), trials, random.Random(11))
        p1sim.FEST = None
        out[k] = r[3]['rate']
    return out


def event_rates(cfg, em, dodges, n, fest):
    c = copy.deepcopy(cfg)
    c['maps']['gq26'] = em
    out = {}
    p1sim.FEST = fest
    for label, (bt, be, ct, ce, lv) in (('Q04 参考 T1+6 Lv20', p1sim.REF_GEAR['q04']), ('Q05 参考 T2+6 Lv25', p1sim.REF_GEAR['q05']),
                                         ('T2+3 / T2+3 Lv22', (2, 3, 2, 3, 22))):
        bl = dict(p1sim.item('burst', 'blade', bt), enh=be)
        ch = dict(p1sim.item('burst', 'charm', ct), enh=ce)
        st = p1sim.stats(c, bl, ch, lv)
        out[label] = [p1sim.clear_rate(c, 'gq26', st, p1sim.Knobs(d), n) for d in dodges]
    p1sim.FEST = None
    return out


def event_after_q04(cfg, em, dodge, players, n_each, fest):
    """progression-sim players stopped right after their own Q04 first clear (+ invest), then n_each event runs."""
    c = copy.deepcopy(cfg)
    c['maps']['gq26'] = em
    rates = []
    for i in range(players):
        kn = p1sim.Knobs(dodge)
        rng = random.Random(9000 + i)
        p = _player_after(cfg, kn, 9000 + i, 'q04')
        if p is None:
            continue
        p1sim.FEST = fest
        st = p.st()
        ok = sum(p1sim.run_map(c, 'gq26', st, kn, rng, kn.potion_keep)[0] for _ in range(n_each))
        p1sim.FEST = None
        rates.append(ok / n_each)
    return statistics.mean(rates) if rates else None, len(rates)


def _player_after(cfg, kn, seed, key):
    """replay simulate_player but keep the Player object (stop at the first clear of `key`)."""
    holder = {}
    orig = p1sim.Player.__init__

    def init(self, *a, **k):
        orig(self, *a, **k)
        holder['p'] = self
    p1sim.Player.__init__ = init
    try:
        rec, runs = p1sim.simulate_player(cfg, kn, seed, max_runs=600, stop_at=key)
    finally:
        p1sim.Player.__init__ = orig
    p = holder.get('p')
    if p is None or key not in p.cleared:
        return None
    p.invest()
    return p


def main():
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument('--n', type=int, default=2000, help='runs per reference cell')
    ap.add_argument('--players', type=int, default=120)
    ap.add_argument('--days', type=int, default=60)
    ap.add_argument('--trials', type=int, default=200, help='raid trials (3 players)')
    ap.add_argument('--pool', type=int, default=24)
    ap.add_argument('--dodge', type=float, nargs='*', default=[0.3, 0.5, 0.7])
    ap.add_argument('--coef', type=float, default=None, help='sweep: override the burst coefficient')
    ap.add_argument('--hp', type=float, default=None, help='sweep: override the charm HP')
    ap.add_argument('--def', dest='dfn', type=float, default=None, help='sweep: override the charm D')
    ap.add_argument('--icd', type=float, default=None, help='sweep: override the burst cooldown')
    ap.add_argument('--skip', nargs='*', default=[], choices=['ref', 'band', 'front', 'raid', 'event'])
    a = ap.parse_args()
    print('# ' + __import__('rules').stamp(), flush=True)  # M06: which rule snapshot produced this report
    f = fest_cfg()
    over = {}
    if a.coef is not None:
        over['coef'] = a.coef
    if a.hp is not None:
        over['hp'] = a.hp
    if a.dfn is not None:
        over['def'] = a.dfn
    if a.icd is not None:
        over['icd'] = a.icd
    fest = charm_of(f, **over)
    cfg = p1config.load()
    print('# festsim D139 — charm %s: H +%g · D +%g · 烟火迸发 %.2f×B, %d 目标, 半径 %.1f, 内置冷却 %.0f s\n' % (
        f['charm']['name'], fest['hp'], fest['def'], fest['coef'], fest['targets'], fest['radius'], fest['icd']))
    worst = 0.0
    worst_band = [0.0]
    if 'ref' not in a.skip:
        base, withc = ref_rates(cfg, a.dodge, a.n, None), ref_rates(cfg, a.dodge, a.n, fest)
        print('## 1 书参考投入通关率（每格 %d 局，同种子）\n' % a.n)
        print('| 图 | ' + ' | '.join('躲避 %.1f 无 → 有（差）' % d for d in a.dodge) + ' |')
        print('|---|' + '---|' * len(a.dodge))
        for k in cfg['order']:
            cells = []
            for i in range(len(a.dodge)):
                dlt = 100 * (withc[k][i] - base[k][i])
                worst = max(worst, abs(dlt))
                cells.append('%.0f%% → %.0f%%（%+.1f）' % (100 * base[k][i], 100 * withc[k][i], dlt))
            print('| %s | %s |' % (k.upper(), ' | '.join(cells)))
        print()
    if 'band' not in a.skip:
        nb = max(100, a.n // 5)
        base, withc = band_rates(cfg, a.dodge, nb, None), band_rates(cfg, a.dodge, nb, fest)
        print('## 1b 书参考区间平均通关率（每图 9～12 种强化 × 等级组合，每格 %d 局；判据用这一张）\n' % nb)
        print('| 图 | 区间 | ' + ' | '.join('躲避 %.1f 无 → 有（差）' % d for d in a.dodge) + ' |')
        print('|---|---|' + '---|' * len(a.dodge))
        for k in cfg['order']:
            bt, ct, enhs, lvs = BAND[k]
            cells = []
            for i in range(len(a.dodge)):
                dlt = 100 * (withc[k][i] - base[k][i])
                worst_band[0] = max(worst_band[0], abs(dlt))
                cells.append('%.0f%% → %.0f%%（%+.1f）' % (100 * base[k][i], 100 * withc[k][i], dlt))
            print('| %s | T%d刃 T%d符 +%d～%d Lv%d～%d | %s |' % (k.upper(), bt, ct, enhs[0], enhs[-1], lvs[0], lvs[-1], ' | '.join(cells)))
        print()
    if 'front' not in a.skip:
        print('## 2 推进模拟前沿通关率（%d 人，%d 天，躲避 0.5；从第一天就戴着 = 最坏情况）\n' % (a.players, a.days))
        b0, b1 = front_rates(cfg, 0.5, a.players, a.days, None), front_rates(cfg, 0.5, a.players, a.days, fest)
        print('| 图 | 无 | 有 | 差 | 首通天（中位）无 → 有 |\n|---|---:|---:|---:|---|')
        for k in cfg['order']:
            x0, x1 = b0[k]['front_rate'], b1[k]['front_rate']
            d = 100 * ((x1 or 0) - (x0 or 0))
            print('| %s | %s | %s | %+.1f | %s → %s |' % (k.upper(), p1sim.fmt(x0 and 100 * x0, '%.0f%%'), p1sim.fmt(x1 and 100 * x1, '%.0f%%'), d,
                                                    p1sim.fmt(b0[k]['fc_day']), p1sim.fmt(b1[k]['fc_day'])))
        print()
    if 'raid' not in a.skip:
        pool = p1party.player_pool(cfg, a.pool, 2, 0.5)
        r0 = raid_rates(cfg, ('r01', 'r02', 'r03'), a.trials, None, pool)
        r1 = raid_rates(cfg, ('r01', 'r02', 'r03'), a.trials, fest, pool)
        print('## 3 团本 3 人通关率（Q07 + 2 周，躲避 0.5，%d 局，同种子）\n' % a.trials)
        print('| 团本 | 无 | 有 | 差 |\n|---|---:|---:|---:|')
        for k in r0:
            d = 100 * (r1[k] - r0[k])
            worst = max(worst, abs(d))
            worst_band[0] = max(worst_band[0], abs(d))
            print('| %s | %.0f%% | %.0f%% | %+.1f |' % (k.upper(), 100 * r0[k], 100 * r1[k], d))
        print()
    if 'event' not in a.skip:
        em = event_map(cfg, f)
        print('## 4 活动本「%s」通关率（首领 %s 生命 %d）\n' % (em['name'], em['boss']['name'], em['boss']['hp']))
        e0, e1 = event_rates(cfg, em, a.dodge, a.n // 4, None), event_rates(cfg, em, a.dodge, a.n // 4, fest)
        print('| 装备 | ' + ' | '.join('躲避 %.1f 无符 / 戴符' % d for d in a.dodge) + ' |')
        print('|---|' + '---|' * len(a.dodge))
        for lab in e0:
            print('| %s | %s |' % (lab, ' | '.join('%.0f%% / %.0f%%' % (100 * e0[lab][i], 100 * e1[lab][i]) for i in range(len(a.dodge)))))
        for d in a.dodge:
            r, n = event_after_q04(cfg, em, d, a.players // 2, 20, None)
            print('\n刚首通 Q04 的推进玩家（躲避 %.1f，%d 人 × 20 局，无符）：平均通关率 %s' % (d, n, p1sim.fmt(r and 100 * r, '%.0f%%')))
        print()
    print('最大差：参考点（表 1）+ 团本 %.1f 个百分点 · 参考区间（表 1b）+ 团本 %.1f 个百分点（判据：≤ 3）' % (worst, worst_band[0]))


if __name__ == '__main__':
    main()

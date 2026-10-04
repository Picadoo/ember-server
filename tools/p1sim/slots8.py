#!/usr/bin/env python3
"""D168 · 8-slot gear model at the SAME power ceiling (docs/design/DESIGN-ember-v1.2-gear8.md).

Read-only use of p1sim / p1config / p2econ / rules (never edits them). Parameters: gear8-proposal.json.

  python3 slots8.py --check                       # self-checks (matched loadout == p1sim.stats, engine == p1sim)
  python3 slots8.py --ceiling [--n 40]            # 42 tolerance cells: every set pattern vs today
  python3 slots8.py --costs                       # per-slot cost tables + expected totals
  python3 slots8.py --endgame [--n 30]            # full set vs mixes at T3+9/+10 on challenge + abyss tiers
  python3 slots8.py --prog --players 40 --weeks 16 [--dodges 0.3,0.5,0.7] [--nproc 4]
                                                   # progression: p1 (today) vs g8 vs g8 strict-full-set
Model notes
- slot shares: a / h / d are fractions of today's blade A[t], charm h[t], charm D[t]; sums are 1, so a loadout whose
  attack slots copy the blade and whose life slots copy the charm reproduces p1sim.stats exactly (asserted by --check).
- sets: 6 set slots (blade, offhand, helm, chest, legs, boots). proc family = the one with >= proc_at pieces (T1+);
  共鸣 layers L = min(layer_max, #families with >=2 pieces + [some family has full_at]); proc strength = today's coef at
  awakening k x (1 + step*L) / (1 + step*layer_max). k: I; II when >= proc_at proc pieces are T2+6 (capped by cap4);
  III only with full_at proc pieces all T3+9.
"""
import argparse
import collections
import copy
import json
import math
import multiprocessing as mp
import os
import random
import statistics
import sys

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config  # noqa: E402
import p1sim  # noqa: E402
import p2econ  # noqa: E402

HERE = os.path.dirname(os.path.abspath(__file__))
PROP = json.load(open(os.path.join(HERE, 'gear8-proposal.json'), encoding='utf-8'))
SL = PROP['slots']
SLOTS = list(SL)
SET_SLOTS = [s for s in SLOTS if SL[s]['set']]
ACC = [s for s in SLOTS if not SL[s]['set']]
CLS = PROP['classes']
SETR = PROP['set']
DROPS = PROP['drops']
FAMS = p1sim.FAMS
W = {s: CLS[SL[s]['class']]['w'] for s in SLOTS}
MARK = {s: CLS[SL[s]['class']]['mark'] for s in SLOTS}
VAL = {s: SL[s]['a'] + SL[s]['h'] + 0.5 * SL[s]['d'] for s in SLOTS}  # tie-break / value order only


# ---------------------------------------------------------------- costs

def scaled(vals, w, keep_min=True):
    """per-attempt table x w, rounded; a non-zero entry never rounds to 0"""
    out = []
    for v in vals:
        x = int(round(v * w))
        if keep_min and v > 0 and x == 0:
            x = 1
        out.append(x)
    return out


def scaled_sum(vals, w):
    """largest-remainder rounding so sum(out) ~= w * sum(vals) (used for cores, which are small integers)"""
    raw = [v * w for v in vals]
    out = [int(math.floor(x)) for x in raw]
    left = int(round(sum(raw))) - sum(out)
    for i in sorted(range(len(raw)), key=lambda i: -(raw[i] - out[i]))[:max(0, left)]:
        out[i] += 1
    return out


def class_costs(cfg):
    out = {}
    for c, d in CLS.items():
        w = d['w']
        up = {t: {k: (int(round(v * w)) if v else 0) for k, v in cfg['upgrade'][t].items()} for t in cfg['upgrade']}
        out[c] = {'w': w, 'enh_shard': scaled(cfg['enh_shard'], w), 'enh_core': scaled_sum(cfg['enh_core'], w),
                  'enh_coin': [int(round(v * w / 10.0)) * 10 if v else 0 for v in cfg['enh_coin']],
                  'upgrade': up,
                  'craft_coin': {k: int(round(v * w / 10.0)) * 10 for k, v in p2econ.CRAFT_COIN.items()},
                  'quality_coin': {k: int(round(v * w / 10.0)) * 10 for k, v in p2econ.QUALITY_COIN.items()},
                  'craft_blank': [max(1, int(round(x * w))) for x in (3, 6, 12)],
                  'craft_bone': [max(1, int(round(x * w))) for x in (5, 10, 20)],
                  'quality_mat': [max(1, int(round(x * w))) for x in (8, 16)],
                  'reroll_coin': [int(round(x * w / 10.0)) * 10 for x in (300, 600, 1000)],
                  'reroll_shard': [max(1, int(round(x * w))) for x in (40, 80, 120)],
                  'mark': d['mark'], 'dismantle_blank': d['dismantle_blank']}
    return out


def expected_enh(cfg, tab):
    """expected shard / core / coin for +0 -> +10 with the per-step max-tries pity"""
    tot = [0.0, 0.0, 0.0]
    for t in range(1, 11):
        p, mx = cfg['enh_rate'][t], cfg['enh_max'][t]
        n = sum((1 - p) ** k for k in range(mx))  # expected attempts with a forced success at mx
        tot[0] += n * tab['enh_shard'][t]; tot[1] += n * tab['enh_core'][t]; tot[2] += n * tab['enh_coin'][t]
    return tot


# ---------------------------------------------------------------- stats

def item(fam, slot, tier, q=0, f=0, src='drop'):
    it = p1sim.item(fam if SL[slot]['set'] else 'none', slot, tier, q, f, src)
    return it


def stats8(cfg, gear, level, cap4=None, sustain_hp=None, layer=True):
    steps = max(0, min(level, cfg['lvl_cap']) - cfg['lvl_base'])
    g = lambda it: 1 + cfg['e'][it['enh']] + cfg['q'][it['q']] + cfg['f'][it['f']]
    B = sum(SL[s]['a'] * cfg['A'][it['tier']] * g(it) for s, it in gear.items()) + cfg['lvl_atk'] * steps
    H = 20 + sum(SL[s]['h'] * cfg['h'][it['tier']] * g(it) for s, it in gear.items()) + cfg['lvl_hp'] * steps
    D = sum(SL[s]['d'] * cfg['D'][it['tier']] for s, it in gear.items())
    cnt = collections.Counter(it['fam'] for s, it in gear.items()
                              if SL[s]['set'] and it['tier'] >= 1 and it['fam'] in FAMS)
    pa, fa = SETR['proc_at'], SETR['full_at']
    P = next((f for f, n in cnt.items() if n >= pa), None)
    awk, amp = 0, 1.0
    if P:
        ps = [it for s, it in gear.items() if SL[s]['set'] and it['fam'] == P and it['tier'] >= 1]
        awk = 2 if sum(1 for it in ps if it['tier'] >= 2 and it['enh'] >= 6) >= pa else 1
        awk = min(awk, SETR['cap4'] if cap4 is None else cap4)
        if len(ps) >= fa and all(it['tier'] >= 3 and it['enh'] >= 9 for it in ps):
            awk = 3
        if layer:
            L = min(SETR['layer_max'], sum(1 for n in cnt.values() if n >= 2) + (1 if max(cnt.values()) >= fa else 0))
            amp = (1 + SETR['layer_step'] * L) / (1 + SETR['layer_step'] * SETR['layer_max'])
    shp = cfg['sustain_hp'] if sustain_hp is None else sustain_hp
    if P == 'sustain':
        H *= 1 + (shp - 1) * amp
    M = max(cfg['def_floor'], cfg['def_k'] / (cfg['def_k'] + D))
    st = {'B': B, 'H': H, 'M': M, 'set': P, 'awk': awk}
    if abs(amp - 1) > 1e-12:
        st['mods'] = {'burn_mult': amp, 'burst_mult': amp, 'sustain_mult': amp}
    st['amp'] = amp
    return st


def power8(cfg, st, kn):
    amp = st.get('amp', 1.0)
    dps = st['B'] * (1 + cfg['crit_rate'] * (cfg['crit_mult'] - 1)) / kn.swing
    if st['set'] == 'burst':
        dps += amp * cfg['burst'][st['awk']] * st['B'] * min(3, kn.skill_hits) / max(cfg['burst_every'] * kn.swing, cfg['burst_icd'])
    elif st['set'] == 'scorch':
        dps += amp * cfg['burn'][st['awk']] * st['B']
    ehp = st['H'] / st['M']
    if st['set'] == 'sustain':
        ehp *= 1 + 0.15 * amp
    return dps * ehp


def matched(blade, charm, fam_of=None):
    """8-slot loadout equal to today's (blade, charm): attack slots copy the blade, life slots copy the charm.
    fam_of: optional {slot: family} override for set slots (mix patterns)."""
    gear = {}
    for s in SLOTS:
        src = blade if SL[s]['side'] == 'atk' else charm
        it = dict(src, slot=s)
        if not SL[s]['set']:
            it['fam'] = 'none'
        elif fam_of and s in fam_of and it['tier'] >= 1:
            it['fam'] = fam_of[s]
        gear[s] = it
    return gear


def strip(st):
    return {k: st[k] for k in ('B', 'H', 'M', 'set', 'awk')}


# patterns over the 6 set slots: (name, {slot: family-key}) ; 'A' = the build's family, 'B'/'C' = the other two
PATTERNS = [
    ('A6', {}),
    ('A4+B2 (刃+焰灯异族)', {'blade': 'B', 'offhand': 'B'}),
    ('A4+B2 (头+靴异族)', {'helm': 'B', 'boots': 'B'}),
    ('A4+B2 (刃+胸异族)', {'blade': 'B', 'chest': 'B'}),
    ('A4+B1+C1', {'helm': 'B', 'boots': 'C'}),
    ('A5+B1', {'boots': 'B'}),
    ('A3+B3', {'blade': 'B', 'offhand': 'B', 'helm': 'B'}),
    ('A2+B2+C2', {'blade': 'B', 'offhand': 'B', 'helm': 'C', 'boots': 'C'}),
]


def pattern_fams(base, pat):
    others = [f for f in FAMS if f != base]
    m = {'B': others[0], 'C': others[1]}
    return {s: m[v] for s, v in pat.items()}


# ---------------------------------------------------------------- checks

def selfcheck(cfg):
    ok = True
    for key, (bt, be, ct, ce, lv) in p1sim.REF_GEAR.items():
        for fam in FAMS:
            for q, f in ((0, 0), (1, 2), (3, 3)):
                bl = dict(p1sim.item(fam if bt else 'none', 'blade', bt, q, f), enh=be)
                ch = dict(p1sim.item(fam if ct else 'none', 'charm', ct, q, f), enh=ce)
                a = p1sim.stats(cfg, bl, ch, lv)
                for name, pat in PATTERNS[:4]:
                    b = stats8(cfg, matched(bl, ch, pattern_fams(fam, pat)), lv)
                    if any(abs(a[k] - b[k]) > 1e-9 for k in ('B', 'H', 'M')) or (a['set'], a['awk']) != (b['set'], b['awk']) \
                            or b['amp'] != 1.0:
                        ok = False
                        print('MISMATCH', key, fam, q, f, name, strip(a), strip(b), b['amp'])
    # T3+9 full set -> III, A4+B2 -> II
    for fam in FAMS:
        bl = dict(p1sim.item(fam, 'blade', 3, 3, 3), enh=10)
        ch = dict(p1sim.item(fam, 'charm', 3, 3, 3), enh=10)
        a = p1sim.stats(cfg, bl, ch, 30)
        b = stats8(cfg, matched(bl, ch), 30)
        assert strip(a) == strip(b) or all(abs(a[k] - b[k]) < 1e-9 for k in 'BHM'), (a, b)
        c = stats8(cfg, matched(bl, ch, pattern_fams(fam, PATTERNS[1][1])), 30)
        assert c['awk'] == 2 and c['amp'] == 1.0
    print('selfcheck stats8:', 'OK' if ok else 'FAIL')
    return ok


def cells(cfg, ccfg):
    out = []
    for key in cfg['order']:
        bt, be, ct, ce, lv = p1sim.REF_GEAR[key]
        out.append((key, 'normal', cfg, bt, be, ct, ce, lv))
    for key in cfg['order']:
        out.append((key, 'challenge', ccfg, 3, 6, 3, 6, 30))
    return out


def _rate(args):
    cfgname, key, st, dodge, n, seed = args
    c = G_CFGS[cfgname]
    return p1sim.clear_rate(c, key, st, p1sim.Knobs(dodge, swap=True), n, seed=seed)


G_CFGS = {}


def init_cfgs(sustain_hp=None):
    cfg = p1config.load('current')
    if sustain_hp is not None:
        cfg['sustain_hp'] = sustain_hp
    ccfg = p2econ.challenge_cfg(cfg)
    G_CFGS['n'], G_CFGS['c'] = cfg, ccfg
    for t in range(1, len(p2econ.ABYSS) + 1):
        G_CFGS['a%d' % t] = p2econ.abyss_cfg(ccfg, t)
    return cfg, ccfg


def ceiling(cfg, ccfg, n, nproc, dodges=(0.3, 0.5, 0.7), fams=('burst', 'scorch', 'sustain')):
    """42 cells x set family x pattern: clear-rate delta vs today (same seeds = paired)."""
    jobs, meta = [], []
    for fam in fams:
        for key, kind, c, bt, be, ct, ce, lv in cells(cfg, ccfg):
            bl = dict(p1sim.item(fam if bt else 'none', 'blade', bt), enh=be)
            ch = dict(p1sim.item(fam if ct else 'none', 'charm', ct), enh=ce)
            base = p1sim.stats(cfg, bl, ch, lv)
            variants = [('today', base)]
            for name, pat in PATTERNS:
                variants.append((name, stats8(cfg, matched(bl, ch, pattern_fams(fam, pat)), lv)))
            variants.append(('A4+B2 cap4=I', stats8(cfg, matched(bl, ch, pattern_fams(fam, PATTERNS[1][1])), lv, cap4=1)))
            variants.append(('A6 no-layer', stats8(cfg, matched(bl, ch), lv, layer=False)))
            for d in dodges:
                seed = hash((key, kind, d)) & 0xffffff
                for vn, st in variants:
                    st2 = {k: v for k, v in st.items() if k != 'amp'}
                    jobs.append(('n' if kind == 'normal' else 'c', key, st2, d, n, seed))
                    meta.append((fam, key, kind, d, vn, st))
    with mp.Pool(nproc) as pool:
        rates = pool.map(_rate, jobs, chunksize=8)
    res = collections.defaultdict(dict)
    for (fam, key, kind, d, vn, st), r in zip(meta, rates):
        res[(fam, key, kind, d)][vn] = (r, st)
    return res


def ceiling_report(res, n):
    names = ['today'] + [p[0] for p in PATTERNS] + ['A4+B2 cap4=I', 'A6 no-layer']
    L = ['# slots8 · equal-ceiling check (D168)', '',
         'Each cell: Q-map × (normal first clear at p1sim.REF_GEAR | challenge T3+6 lv30) × dodge 0.3/0.5/0.7 = 42 cells, '
         'per set family. Gear is the 8-slot loadout MATCHED to the reference (attack slots = blade numbers, life slots = '
         'charm numbers) with the set pattern shown. Paired seeds, %d runs per cell. Δ = clear rate − today, in pp.' % n, '',
         '| 族 | 型 | 与今日 st 完全相同的格 | 最大 Δ | 平均 Δ | 最小 Δ | 越 +3pp 的格 |', '|---|---|---:|---:|---:|---:|---:|']
    fams = sorted({k[0] for k in res})
    for fam in fams:
        keys = [k for k in res if k[0] == fam]
        for vn in names[1:]:
            ds, same = [], 0
            for k in keys:
                r0, s0 = res[k]['today']
                r1, s1 = res[k][vn]
                ds.append(100 * (r1 - r0))
                same += all(abs(s0[x] - s1[x]) < 1e-9 for x in 'BHM') and (s0['set'], s0['awk']) == (s1['set'], s1['awk']) \
                    and s1.get('amp', 1.0) == 1.0
            L.append('| %s | %s | %d/%d | %+.1f | %+.1f | %+.1f | %d |' % (
                fam, vn, same, len(keys), max(ds), statistics.mean(ds), min(ds), sum(1 for x in ds if x > 3.0)))
    L += ['', '## today clear rate per cell (burst family; reference)', '',
          '| 图 | 类 | 0.3 | 0.5 | 0.7 |', '|---|---|---:|---:|---:|']
    for key in sorted({k[1] for k in res}):
        for kind in ('normal', 'challenge'):
            row = [res[('burst', key, kind, d)]['today'][0] for d in (0.3, 0.5, 0.7) if ('burst', key, kind, d) in res]
            if row:
                L.append('| %s | %s | %s |' % (key, kind, ' | '.join('%d%%' % round(100 * x) for x in row)))
    return L


# ---------------------------------------------------------------- endgame

def endgame(cfg, n, nproc, dodges=(0.3, 0.5, 0.7)):
    rows = []
    jobs, meta = [], []
    for fam in FAMS:
        for gear_name, tier, enh, q, f in (('T3+9 标准', 3, 9, 0, 0), ('T3+10 卓越精工满', 3, 10, 2, 3)):
            bl = dict(p1sim.item(fam, 'blade', tier, q, f), enh=enh)
            ch = dict(p1sim.item(fam, 'charm', tier, q, f), enh=enh)
            vs = [('today', p1sim.stats(cfg, bl, ch, 30))]
            for name, pat in (PATTERNS[0], PATTERNS[1], PATTERNS[4], PATTERNS[5]):
                vs.append((name, stats8(cfg, matched(bl, ch, pattern_fams(fam, pat)), 30)))
            for d in dodges:
                for vn, st in vs:
                    st2 = {k: v for k, v in st.items() if k != 'amp'}
                    for cname in ['c'] + ['a%d' % t for t in (2, 4, 6, 8, 10)]:
                        for key in cfg['order']:
                            jobs.append((cname, key, st2, d, n, hash((cname, key, d)) & 0xffffff))
                            meta.append((fam, gear_name, d, vn, cname))
    with mp.Pool(nproc) as pool:
        rates = pool.map(_rate, jobs, chunksize=8)
    agg = collections.defaultdict(list)
    for m_, r in zip(meta, rates):
        agg[m_].append(r)
    L = ['# slots8 · endgame: full set vs mixes (D168)', '',
         'Mean clear rate over the 7 maps, %d runs each, paired seeds. "today" = two-piece set at the same numbers '
         '(today III needs blade+charm T3+9; in the 8-slot model III needs all 6 set pieces T3+9, so A4+B2 stops at II).' % n, '']
    cnames = ['c'] + ['a%d' % t for t in (2, 4, 6, 8, 10)]
    hdr = '| 族 | 装备 | 躲 | 型 | 挑战 | ' + ' | '.join('深渊%s' % c[1:] for c in cnames[1:]) + ' |'
    L += [hdr, '|---|---|---:|---|' + '---:|' * len(cnames)]
    for fam in FAMS:
        for gear_name in ('T3+9 标准', 'T3+10 卓越精工满'):
            for d in dodges:
                for vn in ('today', 'A6', 'A4+B2 (刃+焰灯异族)', 'A4+B1+C1', 'A5+B1'):
                    vals = [statistics.mean(agg[(fam, gear_name, d, vn, c)]) for c in cnames]
                    L.append('| %s | %s | %.1f | %s | %s |' % (fam, gear_name, d, vn, ' | '.join('%d%%' % round(100 * v) for v in vals)))
    return L


# ---------------------------------------------------------------- progression

class Player8(p1sim.Player):
    """8-slot player. Same economy hooks as p1sim.Player (bounty, settle wrapper, potions)."""

    def __init__(self, cfg, kn, rng, costs, strict=False, pity=True):
        super().__init__(cfg, kn, rng)
        self.costs, self.strict, self.use_pity = costs, strict, pity
        self.gear = {s: item('none', s, 0, src='starter') for s in SLOTS}
        self.pity = collections.Counter()
        self.pity_hits = 0
        self.spent = collections.Counter()
        self.mark_buys = collections.Counter()
        self.drops_n = 0
        self._sync()

    def _sync(self):
        self.blade, self.charm = self.gear['blade'], self.gear['charm']

    def lvl(self):
        return p1sim.level_of(self.cfg, self.xp)

    def st(self):
        s = stats8(self.cfg, self.gear, self.lvl())
        s.pop('amp', None)
        return s

    def score(self, gear):
        st = stats8(self.cfg, gear, self.lvl())
        n = sum(1 for s in SET_SLOTS if gear[s]['fam'] == self.kn.target and gear[s]['tier'] >= 1)
        return power8(self.cfg, st, self.kn) * (1 + 0.01 * n)

    def fam_roll(self, key, u):
        cfg, tgt = self.cfg, self.kn.target
        tw = cfg['target_weight']
        others = [x for x in FAMS if x != tgt]
        lb = cfg.get('loot_bias') or {}
        mf = ((cfg['maps'].get(key) or {}).get('loot') or {}).get('family') if lb else None
        if mf and mf == tgt:
            tw = float(lb.get('own_family', tw))
            return tgt if u < tw else (others[0] if u < tw + (1 - tw) / 2 else others[1])
        if mf:
            third = next(x for x in others if x != mf)
            sh = (1 - tw) * float(lb.get('map_share', 0.5))
            return tgt if u < tw else (mf if u < tw + sh else third)
        return tgt if u < tw else (others[0] if u < tw + (1 - tw) / 2 else others[1])

    def slot_weights(self, key):
        feat = DROPS['featured'].get(key) or []
        if not feat:
            return [1.0 / len(SLOTS)] * len(SLOTS)
        fs = DROPS['featured_share']
        return [fs / len(feat) if s in feat else (1 - fs) / (len(SLOTS) - len(feat)) for s in SLOTS]

    def roll_item(self, tier, key=None, forced=None):
        r = self.rng
        fam = self.fam_roll(key, r.random())
        u = r.random()
        slot = SLOTS[p1sim.pick(self.slot_weights(key), u)]
        if forced:
            slot, fam = forced, self.kn.target
        return item(fam, slot, tier, p1sim.pick(self.cfg['quality_w'], r.random()), p1sim.pick(self.cfg['craft_w'], r.random()))

    def need(self, s, t):
        it = self.gear[s]
        return it['tier'] < t or (SL[s]['set'] and it['fam'] != self.kn.target)

    def good(self, it, t):
        return it['tier'] >= t and (not SL[it['slot']]['set'] or it['fam'] == self.kn.target)

    def consider(self, new):
        s = new['slot']
        cur = self.gear[s]
        if self.strict and SL[s]['set'] and new['fam'] != self.kn.target and cur['src'] != 'starter':
            self.dismantle(new)
            return
        cand = dict(new)
        if self.kn.swap and cur['enh'] > cand['enh']:
            cand['enh'], cand['pity'] = cur['enh'], cur['pity']
        g2 = dict(self.gear)
        g2[s] = cand
        if self.score(g2) > self.score(self.gear) * 1.0001:
            if self.kn.swap and cand['enh'] == cur['enh'] and cur['enh'] > new['enh']:
                cur = dict(cur, enh=new['enh'], pity=new['pity'])
            self.gear[s] = cand
            self._sync()
            self.dismantle(cur)
        else:
            self.dismantle(new)

    def dismantle(self, it):
        if it['src'] in ('drop', 'mark') and it['tier'] >= 1:
            self.blank += self.costs[SL[it['slot']]['class']]['dismantle_blank'][it['tier']]

    def _settle(self, key, extra, var=None):
        cfg = self.cfg
        m = cfg['maps'][key]
        tier = m['tier']
        # reuse the parent for base / bounty / variety / FC materials, but take over the drops
        saved_roll = self.roll_item
        rolled = []

        def fake_roll(t, k=None):
            rolled.append(t)
            return None
        self.roll_item = fake_roll
        fc = (m.get('first_clear') or {})
        first = key not in self.cleared
        saved_fc = None
        if first and 'choice' in fc:  # FC packs are handled below (8-slot rules)
            saved_fc = fc
            m['first_clear'] = {k: v for k, v in fc.items() if k not in ('choice', 'tier')}
        self.consider_orig = self.consider
        self.consider = lambda d: None if d is None else self.consider_orig(d)
        marks_per = cfg['marks_per']
        cfg['marks_per'] = 10 ** 9  # marks handled below
        try:
            super()._settle(key, extra, var)
        finally:
            self.roll_item = saved_roll
            self.consider = self.consider_orig
            cfg['marks_per'] = marks_per
            if saved_fc is not None:
                m['first_clear'] = saved_fc
        # drops: items_per_run + 1 for the extra chest (rolled has 1 or 2 entries from the parent)
        n = DROPS['items_per_run'] + (len(rolled) - 1)
        forced = None
        if self.use_pity:
            needy = [s for s in SLOTS if self.need(s, tier) and self.pity[(tier, s)] >= DROPS['pity_after']]
            if needy:
                forced = max(needy, key=lambda s: (self.pity[(tier, s)], VAL[s]))
        drops = []
        for i in range(n):
            drops.append(self.roll_item(tier, key, forced if i == 0 else None))
        if forced:
            self.pity_hits += 1
        self.drops_n += n
        got = {d['slot'] for d in drops if self.good(d, tier)}
        for s in SLOTS:
            if s in got or not self.need(s, tier):
                self.pity[(tier, s)] = 0
            else:
                self.pity[(tier, s)] += 1
        if first and saved_fc is not None:
            pk = PROP['first_clear'].get(key) or {}
            if 'pack' in pk:
                for s in pk['pack']:
                    drops.append(item('none', s, pk['tier'], src='task'))
            elif 'pick' in pk:
                for d in drops:
                    self.consider(d)
                drops = []
                for _ in range(pk['pick']):
                    best = self.best_target(pk['tier'], 'task')
                    if best is None:
                        break
                    self.consider(best[1])
        for d in drops:
            self.consider(d)
        self.spend_marks()

    def best_target(self, t, src):
        base = self.score(self.gear)
        best = None
        for s in SLOTS:
            cand = item(self.kn.target, s, t, src=src)
            if self.kn.swap and self.gear[s]['enh'] > 0:
                cand['enh'] = self.gear[s]['enh']
            g2 = dict(self.gear)
            g2[s] = cand
            gain = self.score(g2) / base - 1
            if gain > 1e-4 and (best is None or gain / MARK[s] > best[0]):
                best = (gain / MARK[s], cand)
        return best

    def spend_marks(self):
        for t in (1, 2, 3):
            while True:
                best = self.best_target(t, 'mark')
                if best is None:
                    break
                s = best[1]['slot']
                if self.marks[t] < MARK[s]:
                    break
                self.marks[t] -= MARK[s]
                self.mark_buys[s] += 1
                self.pity[(t, s)] = 0
                self.consider(best[1])

    def invest(self):
        cfg, kn = self.cfg, self.kn
        reserve = kn.coin_reserve
        self.save_for_upgrade = False
        for s in sorted(SLOTS, key=lambda s: (self.gear[s]['tier'], -VAL[s])):
            it = self.gear[s]
            flag = {1: 'q04', 2: 'q07'}.get(it['tier'])
            if not flag or flag not in self.cleared or it['src'] == 'starter':
                continue
            if SL[s]['set'] and it['fam'] != kn.target:
                continue
            c = self.costs[SL[s]['class']]['upgrade'][it['tier']]
            if self.shard >= c['shard'] and self.core >= c['core'] and self.blank >= c['blank']:
                if self.coin - c['coin'] >= reserve:
                    self.shard -= c['shard']; self.core -= c['core']; self.blank -= c['blank']; self.coin -= c['coin']
                    self.spent['tier_coin'] += c['coin']; self.spent['tier_blank'] += c['blank']
                    it['tier'] += 1
                else:
                    self.save_for_upgrade = True
                    reserve += c['coin']
            break
        while True:
            s = min(SLOTS, key=lambda s: (self.gear[s]['enh'], -VAL[s]))
            it = self.gear[s]
            if it['enh'] >= 10:
                break
            t = it['enh'] + 1
            tab = self.costs[SL[s]['class']]
            sh, co, cn = tab['enh_shard'][t], tab['enh_core'][t], tab['enh_coin'][t]
            if self.shard < sh or self.core < co or self.coin - cn < reserve:
                break
            self.shard -= sh; self.core -= co; self.coin -= cn
            self.spent['enh_coin'] += cn; self.spent['enh_shard'] += sh; self.spent['enh_core'] += co
            if it['pity'] + 1 >= cfg['enh_max'][t] or self.rng.random() < cfg['enh_rate'][t]:
                it['enh'] = t; it['pity'] = 0
            else:
                it['pity'] += 1

    def pieces(self):
        return [self.gear[s] for s in SLOTS]


def pieces(p):
    return p.pieces() if hasattr(p, 'gear') else [p.blade, p.charm]


def wof(p, it):
    if hasattr(p, 'gear'):
        return CLS[SL[it['slot']]['class']]['w']
    return 1.0


def forge_week(p, reserve=1000):
    """weekly coin sink (as p2econ.forge_sink): raise worn T3 pieces' craft to 3 and quality to 2, cost x w"""
    spent = 0
    while True:
        cands = []
        for it in pieces(p):
            if it['tier'] < 3 or it['src'] == 'starter':
                continue
            w = wof(p, it)
            if it['f'] < 3:
                cands.append((p.cfg['f'][it['f'] + 1] - p.cfg['f'][it['f']], int(p2econ.CRAFT_COIN[it['f']] * w), 'f', it))
            if it['q'] < 2:
                cands.append((p.cfg['q'][it['q'] + 1] - p.cfg['q'][it['q']], int(p2econ.QUALITY_COIN[it['q']] * w), 'q', it))
        cands.sort(key=lambda x: (-x[0] / max(1, x[1]), x[1]))
        pick = next((c for c in cands if p.coin - c[1] >= reserve), None)
        if pick is None:
            break
        _, cost, k, it = pick
        p.coin -= cost; spent += cost
        it[k] += 1
    if hasattr(p, 'spent'):
        p.spent['forge_coin'] += spent
    return spent


def full_ok(p, tgt):
    for it in pieces(p):
        if it['tier'] < 3:
            return False
        if (it['slot'] in ('blade', 'charm') and not hasattr(p, 'gear')) and it['fam'] != tgt:
            return False
        if hasattr(p, 'gear') and SL[it['slot']]['set'] and it['fam'] != tgt:
            return False
    return True


def snapshot(p, tgt, cfg):
    ps = pieces(p)
    st = p.st()
    lv = p1sim.level_of(cfg, p.xp)
    top = p1sim.stats(cfg, dict(p1sim.item(tgt, 'blade', 3, 3, 3), enh=10), dict(p1sim.item(tgt, 'charm', 3, 3, 3), enh=10), lv)
    return {'set3': full_ok(p, tgt), 'awk3': st['awk'] == 3, 'awk2': st['awk'] >= 2 and st['set'] == tgt,
            'enh10': all(it['enh'] >= 10 for it in ps), 'q2': all(it['q'] >= 2 for it in ps),
            'f3': all(it['f'] >= 3 for it in ps),
            'pow': 0.5 * (st['B'] / top['B'] + st['H'] / top['H']), 'coin': p.coin, 'blank': p.blank,
            'shard': p.shard, 'core': p.core}


def new_player(profile, cfg, kn, rng, costs):
    if profile == 'p1':
        return p1sim.Player(cfg, kn, rng)
    return Player8(cfg, kn, rng, costs, strict=(profile == 'g8strict'), pity=(profile != 'g8nopity'))


def phase1(profile, cfg, kn, seed, costs, max_days=60):
    """same route as p1sim.simulate_player (stepdown, no feat/normal-mod detours); returns player, fc days, runs"""
    rng = random.Random(seed)
    p = new_player(profile, cfg, kn, rng, costs)
    order, per_day = cfg['order'], cfg['stamina_day'] // cfg['run_cost']
    fc = {}
    cur, runs, refund_day = 0, 0, -1
    while runs < max_days * per_day:
        day = runs // per_day + 1
        front = next((i for i, k in enumerate(order) if k not in p.cleared), None)
        if front is None:
            break
        cur = min(cur, front)
        key = order[cur]
        p.buy_potions()
        p.day = day
        rep = key in p.cleared
        ok, used, extra, *_ = p1sim.run_map(cfg, key, p.st(), kn, rng, p.potions, repeat=rep)
        p.potions -= used
        runs += 1
        if ok:
            if key not in p.cleared:
                fc[key] = day
            p.settle(key, extra, dict(p1sim.LAST_VAR) if rep else None)
            if key == order[-1]:
                break
            p.invest()
            cur = front + 1 if cur == front else front
        else:
            if cfg['death_refund'] > 0 and refund_day != day:
                refund_day = day
                p.potions += min(used, cfg['death_refund'])
            cur = max(0, cur - 1)
    return p, fc, runs, rng


def choose_map(p, ccfg, rates, best, tgt):
    ok = [k for k in rates if rates[k] >= max(0.5, rates[best] - 0.10)]
    if not ok:
        return best
    if not hasattr(p, 'gear'):
        return p2econ.farm_map(p, p.kn, ccfg, rates, best)

    def need(s):
        it = p.gear[s]
        if it['tier'] < 3 or (SL[s]['set'] and it['fam'] != tgt):
            return VAL[s]
        return 0.15 * VAL[s] if it['q'] < 2 else 0.02 * VAL[s]

    def score(k):
        w = p.slot_weights(k)
        fam = (ccfg['maps'][k].get('loot') or {}).get('family')
        return sum(wi * need(s) for wi, s in zip(w, SLOTS)) * (1.1 if fam == tgt else 1.0), rates[k]
    return max(ok, key=score)


def phase2(p, cfg, ccfg, kn, rng, weeks, per_day):
    tgt = kn.target
    out = []
    for w in range(weeks):
        rates = {k: p1sim.clear_rate(ccfg, k, p.st(), kn, 10, seed=rng.randrange(1 << 30)) for k in ccfg['order']}
        best = max(rates, key=rates.get)
        can_ch = rates[best] >= 0.5
        for i in range(7 * per_day):
            p.day = 1000 + w * 7 + i // per_day
            p.buy_potions()
            if can_ch:
                key = choose_map(p, ccfg, rates, best, tgt)
                ok, used, extra, *_ = p1sim.run_map(ccfg, key, p.st(), kn, rng, p.potions)
            else:
                key = cfg['order'][-1]
                ok, used, extra, *_ = p1sim.run_map(cfg, key, p.st(), kn, rng, p.potions, repeat=True)
            p.potions -= used
            if ok:
                if can_ch:
                    p2econ.settle_with(p, ccfg, key, extra)
                else:
                    p.settle(key, extra, dict(p1sim.LAST_VAR))
                p.invest()
        forge_week(p)
        snap = snapshot(p, tgt, cfg)
        snap['ch'] = can_ch
        out.append(snap)
    return out


def one_player(args):
    profile, dodge, seed, weeks = args
    cfg, ccfg = G_CFGS['n'], G_CFGS['c']
    costs = class_costs(cfg)
    kn = p1sim.Knobs(dodge, swap=True)
    p, fc, runs, rng = phase1(profile, cfg, kn, seed, costs)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    res = {'profile': profile, 'dodge': dodge, 'seed': seed, 'fc': fc, 'runs1': runs, 'q07': 'q07' in fc}
    res['at_q07'] = snapshot(p, kn.target, cfg)
    res['weeks'] = phase2(p, cfg, ccfg, kn, rng, weeks, per_day) if 'q07' in fc else []
    if hasattr(p, 'gear'):
        res['pity_hits'] = p.pity_hits
        res['drops'] = p.drops_n
        res['mark_buys'] = dict(p.mark_buys)
        res['spent'] = dict(p.spent)
    return res


def first_week(res, key):
    """week (phase 2, 1-based) when milestone `key` first holds; None if never in the window"""
    for i, s in enumerate(res['weeks']):
        if s[key]:
            return i + 1
    return None


def pctl(vals, q):
    v = sorted(x if x is not None else math.inf for x in vals)
    x = v[min(len(v) - 1, max(0, int(math.ceil(q * len(v))) - 1))]
    return None if x == math.inf else x


def fmt(x, f='%.0f'):
    return '—' if x is None else f % x


def prog_report(all_res, weeks, players):
    L = ['# slots8 · progression timeline: today (p1) vs 8-slot (g8) (D168)', '',
         '%d simulated players per profile and dodge, burst target, §6.3 enhance swap ON in both. Phase 1 = p1sim route to the '
         'Q07 first clear (stepdown, no detours). Phase 2 = %d weeks after the Q07 first clear: farm the challenge map that '
         'fits the gear gaps (≥50%% clear, within 10 pp of the best) or Q07 normal; one weekly §5.3 forge pass (craft→3, '
         'quality→卓越, cost × slot weight). No raids/abyss/rotation bonus in either profile (same omission both sides). '
         'Profiles: p1 = today (p1sim.Player); g8 = this proposal; g8strict = g8 but never wears an off-family set piece; '
         'g8nopity = g8 without per-slot pity.' % (players, weeks), '',
         '## Phase 1 · first-clear day (P50 / P90)', '',
         '| 躲 | 型 | ' + ' | '.join(k.upper() for k in ('q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07')) + ' |',
         '|---:|---|' + '---:|' * 7]
    profiles = sorted({r['profile'] for r in all_res}, key=lambda x: ('p1', 'g8', 'g8strict', 'g8nopity').index(x))
    dodges = sorted({r['dodge'] for r in all_res})
    for d in dodges:
        for pr in profiles:
            rs = [r for r in all_res if r['profile'] == pr and r['dodge'] == d]
            cells_ = []
            for k in ('q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07'):
                v = [r['fc'].get(k) for r in rs]
                cells_.append('%s / %s' % (fmt(pctl(v, 0.5)), fmt(pctl(v, 0.9))))
            L.append('| %.1f | %s | %s |' % (d, pr, ' | '.join(cells_)))
    L += ['', '## Phase 2 · weeks after the Q07 first clear until a milestone (P50 / P90; — = not within %d weeks)' % weeks, '',
          'set3 = every worn piece T3 and every set piece in the target family (p1: blade+charm; g8: 6 set pieces + 2 '
          'accessories). awk2 = target set active at II. awk3 = III. enh10 = every piece +10. q2 = every piece 卓越+. '
          'f3 = every piece 精工 3.', '',
          '| 躲 | 型 | 挑战可刷 | awk2 | set3 | enh10 | awk3 | q2 | f3 |', '|---:|---|---:|---:|---:|---:|---:|---:|---:|']
    for d in dodges:
        for pr in profiles:
            rs = [r for r in all_res if r['profile'] == pr and r['dodge'] == d]
            row = []
            for key in ('ch', 'awk2', 'set3', 'enh10', 'awk3', 'q2', 'f3'):
                v = [first_week(r, key) if r['weeks'] else None for r in rs]
                row.append('%s / %s' % (fmt(pctl(v, 0.5)), fmt(pctl(v, 0.9))))
            L.append('| %.1f | %s | %s |' % (d, pr, ' | '.join(row)))
    L += ['', '## Power progress (median share of the T3+10 极品 two-piece ceiling at the same level; ½(B/Bmax + H/Hmax))', '',
          '| 躲 | 型 | 首通Q07 | W2 | W4 | W8 | W12 | W%d |' % weeks, '|---:|---|---:|---:|---:|---:|---:|---:|']
    for d in dodges:
        for pr in profiles:
            rs = [r for r in all_res if r['profile'] == pr and r['dodge'] == d]
            row = ['%.0f%%' % (100 * statistics.median(r['at_q07']['pow'] for r in rs))]
            for wk in (2, 4, 8, 12, weeks):
                v = [r['weeks'][wk - 1]['pow'] for r in rs if len(r['weeks']) >= wk]
                row.append('%.0f%%' % (100 * statistics.median(v)) if v else '—')
            L.append('| %.1f | %s | %s |' % (d, pr, ' | '.join(row)))
    L += ['', '## Stock at the end of the window (median)', '', '| 躲 | 型 | 币 | 胚料 | 碎片 | 核心 |', '|---:|---|---:|---:|---:|---:|']
    for d in dodges:
        for pr in profiles:
            rs = [r for r in all_res if r['profile'] == pr and r['dodge'] == d and r['weeks']]
            if rs:
                L.append('| %.1f | %s | %s |' % (d, pr, ' | '.join('%d' % statistics.median(r['weeks'][-1][k] for r in rs)
                                                                  for k in ('coin', 'blank', 'shard', 'core'))))
    g = [r for r in all_res if r['profile'] == 'g8']
    if g:
        L += ['', '## g8 · pity and marks', '',
              'pity-forced drops: %.1f%% of all g8 drops (median player %.0f forced of %.0f). Mark exchanges by slot (sum over players): %s' % (
                  100 * sum(r['pity_hits'] for r in g) / max(1, sum(r['drops'] for r in g)),
                  statistics.median(r['pity_hits'] for r in g), statistics.median(r['drops'] for r in g),
                  ', '.join('%s %d' % (s, sum(r['mark_buys'].get(s, 0) for r in g)) for s in SLOTS))]
    return L


def prog(players, weeks, dodges, nproc, profiles):
    jobs = [(pr, d, 1000 + i, weeks) for d in dodges for pr in profiles for i in range(players)]
    with mp.Pool(nproc) as pool:
        res = pool.map(one_player, jobs, chunksize=1)
    return res


def costs_report(cfg):
    cc = class_costs(cfg)
    L = ['# slots8 · per-slot cost tables (D168)', '',
         'Per-attempt / per-step costs = today × class weight w (heavy 0.70 = 刃; mid 0.35 = 焰灯/戒指/护符/胸/腿; light 0.25 = '
         '头/靴). Σw over 8 slots = %.2f (today 2 pieces = 2.00).' % sum(W.values()), '']
    today = expected_enh(cfg, {'enh_shard': cfg['enh_shard'], 'enh_core': cfg['enh_core'], 'enh_coin': cfg['enh_coin']})
    L += ['| 类 | w | 强化碎片/次 (+1..+10) | 核心/次 | 币/次 | 期望 +0→+10 碎片 / 核心 / 币 | T1→T2 | T2→T3 | 精工 胚料/骨/币 | 成色 料/币 | 洗练 币 | 印记 | 分解胚料 T1/T2/T3 |',
          '|---|---:|---|---|---|---|---|---|---|---|---|---:|---|']
    L.append('| 今日每件 | 1.00 | %s | %s | %s | %.0f / %.1f / %.0f | %s | %s | 3·6·12 / 5·10·20 / 300·600·1200 | 8·16 / 800·1600 | 300·600·1000 | 8 | 1/2/3 |' % (
        ' '.join(map(str, cfg['enh_shard'][1:])), ' '.join(map(str, cfg['enh_core'][1:])), ' '.join(map(str, cfg['enh_coin'][1:])),
        today[0], today[1], today[2], '/'.join(str(cfg['upgrade'][1][k]) for k in ('shard', 'core', 'blank', 'coin')),
        '/'.join(str(cfg['upgrade'][2][k]) for k in ('shard', 'core', 'blank', 'coin'))))
    tot = [0, 0, 0]
    for c in ('heavy', 'mid', 'light'):
        t = cc[c]
        e = expected_enh(cfg, t)
        n = sum(1 for s in SLOTS if SL[s]['class'] == c)
        for i in range(3):
            tot[i] += n * e[i]
        L.append('| %s ×%d | %.2f | %s | %s | %s | %.0f / %.1f / %.0f | %s | %s | %s / %s / %s | %s / %s | %s | %d | %s |' % (
            c, n, t['w'], ' '.join(map(str, t['enh_shard'][1:])), ' '.join(map(str, t['enh_core'][1:])),
            ' '.join(map(str, t['enh_coin'][1:])), e[0], e[1], e[2],
            '/'.join(str(t['upgrade'][1][k]) for k in ('shard', 'core', 'blank', 'coin')),
            '/'.join(str(t['upgrade'][2][k]) for k in ('shard', 'core', 'blank', 'coin')),
            '·'.join(map(str, t['craft_blank'])), '·'.join(map(str, t['craft_bone'])),
            '·'.join(str(t['craft_coin'][k]) for k in (0, 1, 2)), '·'.join(map(str, t['quality_mat'])),
            '·'.join(str(t['quality_coin'][k]) for k in (0, 1)), '·'.join(map(str, t['reroll_coin'])), t['mark'],
            '/'.join(map(str, t['dismantle_blank'][1:]))))
    L += ['', '全套 8 件 +0→+10 期望：碎片 %.0f / 核心 %.0f / 币 %.0f；今日 2 件：%.0f / %.0f / %.0f（比值 %.2f / %.2f / %.2f）。' % (
        tot[0], tot[1], tot[2], 2 * today[0], 2 * today[1], 2 * today[2], tot[0] / (2 * today[0]), tot[1] / (2 * today[1]),
        tot[2] / (2 * today[2]))]
    return L


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--check', action='store_true')
    ap.add_argument('--ceiling', action='store_true')
    ap.add_argument('--costs', action='store_true')
    ap.add_argument('--endgame', action='store_true')
    ap.add_argument('--prog', action='store_true')
    ap.add_argument('--n', type=int, default=40)
    ap.add_argument('--players', type=int, default=40)
    ap.add_argument('--weeks', type=int, default=16)
    ap.add_argument('--dodges', default='0.3,0.5,0.7')
    ap.add_argument('--profiles', default='p1,g8,g8strict,g8nopity')
    ap.add_argument('--nproc', type=int, default=4)
    ap.add_argument('--sustain-hp', type=float, default=None, help='e.g. 1.00 for growth-sidegrades S2')
    ap.add_argument('--out', default=None)
    a = ap.parse_args()
    cfg, ccfg = init_cfgs(a.sustain_hp)
    dodges = tuple(float(x) for x in a.dodges.split(','))
    L = []
    if a.check:
        selfcheck(cfg)
    if a.costs:
        L += costs_report(cfg)
    if a.ceiling:
        L += ceiling_report(ceiling(cfg, ccfg, a.n, a.nproc, dodges), a.n)
    if a.endgame:
        L += endgame(cfg, a.n, a.nproc, dodges)
    if a.prog:
        L += prog_report(prog(a.players, a.weeks, dodges, a.nproc, a.profiles.split(',')), a.weeks, a.players)
    txt = '\n'.join(L) + '\n'
    if a.out:
        open(os.path.join(HERE, a.out), 'w', encoding='utf-8').write(txt)
    print(txt)


if __name__ == '__main__':
    main()

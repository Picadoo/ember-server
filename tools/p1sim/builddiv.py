#!/usr/bin/env python3
"""Build diversity (user 10-04 10:25): does changing a choice change HOW the run plays, or only a number nobody feels?

Reads the live model like every p1sim tool (p1config: runs yml / MM / ember-v1.yml / Java constants, balance_version 29
incl. the D158 converted twists; growth.py: CoreRpg/src/main/resources/ember-v1-growth.yml) and sweeps
  - every legal 6-point talent allocation (3 rows × 3 nodes, one per row = 27) × each set (焚烬 / 烬爆 / 炽愈) and an
    off-set charm (blade and charm of different families = no set: the "charm choice" that breaks the set);
  - blade × charm affix pairs (none + 3 each) at tier 1 (标准 cap / most common roll) and tier 4 (极品 cap);
  - the festival charm slot (盛世烟火符, D139) on / off; 余烬勋记 all on / off.
For each build × context × dodge it records (p1sim.RECORD instrumentation, counting only — results bit-identical):
  clear rate, time per trash room / affixed-elite room by type (炽热 blazing / 分裂 split / 厚甲 shield) / boss, damage
  share by source (普攻 swing / 烬斩 skill / 烬爆 burst / 焚烬 burn / 燎原 spread / 烟火 fest), heals by source, potions,
  survival margin (lowest HP share in a cleared run), mechanic triggers per run, and BEHAVIOUR GRADIENTS: how much the
  same build gains from playing differently (careful = dodge +0.1 / uptime −0.1, greedy = dodge −0.1 / uptime +0.1,
  stack = 烬斩/烬爆 catch 5 instead of 3, single = 1, face-tank = no extra telegraph dodge).
Usage (any dir): python3 tools/p1sim/builddiv.py sweep|affix|raid|systems|propose [--n 1500] [--jobs 7]
Writes tools/p1sim/out-build-diversity-<mode>.md. Sims measure numbers, not fun: see docs/design/DESIGN-ember-build-diversity-2026-10-04.md.
"""
import copy, itertools, json, math, os, random, statistics, sys
from multiprocessing import Pool
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, p2econ, growth, festsim, p1party, miniyaml

HERE = os.path.dirname(os.path.abspath(__file__))
FAMS = ('scorch', 'burst', 'sustain')
FAM_ZH = {'scorch': '焚烬', 'burst': '烬爆', 'sustain': '炽愈', None: '无套'}
AFFIX_ZH = {'blazing': '炽热', 'split': '分裂', 'shield': '厚甲'}
DODGES = (0.3, 0.5, 0.7)
# perceptibility heuristic (see the design doc): relative change in a felt quantity (kill time, potions, heal share)
JND = 0.05      # under 5 %: treat as unnoticeable
CLEAR = 0.10    # 10 % and up: clearly noticeable
STANCES = {
    'base': {},
    'careful': {'dodge': +0.1, 'uptime': -0.1},
    'greedy': {'dodge': -0.1, 'uptime': +0.1},
    'stack': {'skill_hits': 5},
    'single': {'skill_hits': 1},
    'facetank': {'tele_bonus': 0.0},
}

_CFG = {}


def cfgs():
    if not _CFG:
        c = p1config.load('current')
        _CFG['n'] = c
        _CFG['c'] = p2econ.challenge_cfg(c)
        _CFG['g'] = growth.load()
        _CFG['fest'] = festsim.charm_of(festsim.fest_cfg())
        _CFG['bv'] = miniyaml.load(os.path.join(p1config.ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml')).get('balance_version')
    return _CFG


# contexts: (label, cfg key, map, blade tier, blade enh, charm tier, charm enh, level, repeat run?)
CTX = {
    'q04n': ('Q04 普通重打（T1+6 参考装，有词缀精英）', 'n', 'q04', 1, 6, 1, 6, 20, True),
    'q07n': ('Q07 普通重打（T2+8 参考装，有词缀精英）', 'n', 'q07', 2, 8, 2, 8, 30, True),
    'q07c': ('Q07 挑战（T3+6，无词缀精英，首领预警招更重）', 'c', 'q07', 3, 6, 3, 6, 30, False),
}


def knobs(d, stance):
    kn = p1sim.Knobs(d)
    for k, v in PROP_STANCES[stance].items():
        if k in ('skill_hits', 'tele_bonus', 'hold_skill'):
            setattr(kn, k, v)
        else:
            setattr(kn, k, min(0.95, max(0.05, getattr(kn, k) + v)))
    return kn


def growth_fn(spec):
    """spec: talents, honors, affixes {blade, charm}, tier, nodes (override {id: mods} — proposals), extra (raw mods)"""
    g = cfgs()['g']
    if spec.get('nodes'):
        g = copy.deepcopy(g)
        for n in g['talents']['nodes']:
            if n['id'] in spec['nodes']:
                n['mods'] = spec['nodes'][n['id']]
                if 'set' in n and spec.get('nodes_set', {}).get(n['id'], 'keep') is None:
                    n.pop('set')
    if spec.get('affix_vals'):
        g = copy.deepcopy(g)
        for slot, lst in g['reroll']['affixes'].items():
            for a in lst:
                if a['id'] in spec['affix_vals']:
                    a.update(spec['affix_vals'][a['id']])
    tier = spec.get('tier', 4)
    fn = growth.build(g, spec.get('talents', ()), spec.get('honors', ()), spec.get('affixes'), tier)
    extra = spec.get('extra')

    def f(info, ctx):
        # affix tier is capped by quality; the sweep wants the tier itself without the quality's stat bonus
        info2 = dict(info)
        for s in ('blade', 'charm'):
            if info.get(s):
                info2[s] = dict(info[s], q=max(info[s].get('q', 0), tier - 1))
        m = fn(info2, ctx)
        if extra:
            m = growth.combine([m or {}, extra])
        return m
    return f


def gear(ctx, spec):
    c = cfgs()
    _, ck, k, bt, be, ct, ce, lv, rep = CTX[ctx]
    fam = spec.get('set') or 'burst'
    cfam = spec.get('charm_fam') or fam
    bl = dict(p1sim.item(fam if bt else 'none', 'blade', bt), enh=be)
    ch = dict(p1sim.item(cfam if ct else 'none', 'charm', ct), enh=ce)
    return c[ck], k, bl, ch, lv, rep


# tolerance contexts (same as growthcheck.py): first-clear NORMAL at the book §3.2 reference loadout, CHALLENGE at T3+6
for _k, (_bt, _be, _ct, _ce, _lv) in p1sim.REF_GEAR.items():
    CTX[_k + 'r'] = ('%s 普通首通（书参考装）' % _k.upper(), 'n', _k, _bt, _be, _ct, _ce, _lv, False)
    CTX.setdefault(_k + 'c', ('%s 挑战（T3+6）' % _k.upper(), 'c', _k, 3, 6, 3, 6, 30, False))
MAIN = ('q04n', 'q07n', 'q07c')  # the three contexts the diversity sweep reports


def measure(job):
    """job = (spec, ctx, dodge, stance, n, seed) → aggregated metrics dict"""
    spec, ctx, d, stance, n, seed = job
    c = cfgs()
    p1sim.RECORD = True
    p1sim.GROWTH = growth_fn(spec) if (spec.get('talents') or spec.get('honors') or spec.get('affixes') or spec.get('extra')) else None
    p1sim.FEST = c['fest'] if spec.get('fest') else None
    cfg, key, bl, ch, lv, rep = gear(ctx, spec)
    st = p1sim.stats(cfg, bl, ch, lv)
    kn = knobs(d, stance)
    rng = random.Random(seed)
    agg = __import__('collections').Counter()
    secs, minhp, clears = [], [], 0
    for _ in range(n):
        ok, used, extra, t, taken, where = p1sim.run_map(cfg, key, st, kn, rng, kn.potion_keep, repeat=rep)
        f = p1sim.LAST_FIGHT[0]
        agg.update(f.rec)
        agg['potions'] += used
        if ok:
            clears += 1
            secs.append(t)
            minhp.append(f.min_hp)
    p1sim.GROWTH = None; p1sim.FEST = None; p1sim.RECORD = False
    out = {'clear': clears / n, 'n': n, 'secs': statistics.mean(secs) if secs else None,
           'minhp': statistics.median(minhp) if minhp else None,
           'minhp_p10': sorted(minhp)[len(minhp) // 10] if minhp else None,
           'H': st['H'], 'B': st['B'], 'set': st['set']}
    dmg = {k[2:]: v for k, v in agg.items() if k.startswith('d_') and k != 'd_window_extra'}
    tot = sum(dmg.values()) or 1.0
    out['share'] = {k: v / tot for k, v in dmg.items()}
    out['window_extra'] = agg['d_window_extra'] / tot
    out['boss_win_share'] = agg['d_boss_win'] / agg['d_boss_all'] if agg['d_boss_all'] else 0.0
    out['on'] = {k: agg['on_' + k] / tot for k in ('boss', 'affix', 'mob')}
    for seg in ['trash', 'boss'] + ['affix_' + a for a in AFFIX_ZH]:
        cnt = agg['c_' + seg]
        out['t_' + seg] = agg['t_' + seg] / cnt if cnt else None
        out['k_' + seg] = agg['k_' + seg] / cnt / st['H'] if cnt else None   # damage taken per segment, × H
        out['p_' + seg] = agg['p_' + seg] / cnt if cnt else None
        if seg.startswith('affix_'):
            out['ttk_' + seg] = agg['ttk_' + seg] / cnt if cnt else None
            out['own_' + seg] = agg['own_' + seg] / cnt if cnt else None  # first hit on the elite → elite (and adds) dead
    for k in ('n_window', 'n_sustain_low', 'potions', 'n_burst', 'n_burn', 'n_spread', 'n_sustain', 'n_dodge_heal', 'n_hit_burst', 'n_dodge_tele',
              'n_hit_tele', 'n_fest', 'n_dodge_blaze', 'n_hit_blaze', 'died_boss', 'died_affix', 'died_trash'):
        out[k] = agg[k] / n
    for k in ('heal_potion', 'heal_sustain', 'heal_dodge'):
        out[k] = agg[k] / n / st['H']
    tk = {k[6:]: v for k, v in agg.items() if k.startswith('taken_')}
    tt = sum(tk.values()) or 1.0
    out['taken_share'] = {k: v / tt for k, v in tk.items()}
    out['taken'] = tt / n / st['H']
    return out


def run_jobs(jobs, nproc):
    if nproc <= 1:
        return [measure(j) for j in jobs]
    with Pool(nproc) as pool:
        return pool.map(measure, jobs, chunksize=1)


# ---------------------------------------------------------------- helpers for the report

def rel(a, b):
    if a is None or b is None or b == 0:
        return None
    return a / b - 1.0


def mark(x):
    """perceptibility tag of a relative change"""
    if x is None:
        return ''
    a = abs(x)
    return '' if a < JND else ('·' if a < CLEAR else '●')


def pct(x, f='%+.1f%%'):
    return '—' if x is None else f % (100 * x)


ROW3 = {'scorch': 't3a', 'burst': 't3b', 'sustain': 't3c'}
TAL = [(a, b, c) for a in ('t1a', 't1b', 't1c') for b in ('t2a', 't2b', 't2c') for c in ('t3a', 't3b', 't3c')]
NODE_ZH = {}


def node_names():
    if not NODE_ZH:
        for n in cfgs()['g']['talents']['nodes']:
            NODE_ZH[n['id']] = n['name']
    return NODE_ZH


def tname(t):
    nz = node_names()
    return '+'.join(nz[x] for x in t)


FELT = [  # (metric key, label, kind) — kind 'time' / 'count' / 'share'
    ('t_trash', '杂兵房用时', 'time'), ('t_boss', '首领用时', 'time'),
    ('t_affix_blazing', '炽热房用时', 'time'), ('t_affix_split', '分裂房用时', 'time'), ('t_affix_shield', '厚甲房用时', 'time'),
    ('potions', '每局喝药', 'count'), ('taken', '每局受伤(×H)', 'count'),
]


def felt_vec(r):
    return {k: r.get(k) for k, _, _ in FELT}


def felt_diff(a, b):
    """max relative difference over the felt metrics (time / potions / taken) + share-of-damage shifts (absolute pp)"""
    best, which = 0.0, ''
    for k, lab, kind in FELT:
        x = rel(a.get(k), b.get(k))
        if x is not None and abs(x) > best:
            best, which = abs(x), lab
    for src in set(a['share']) | set(b['share']):
        x = abs(a['share'].get(src, 0) - b['share'].get(src, 0))  # absolute share points; 5 pp ≈ noticeable rhythm shift
        if x > best:
            best, which = x, '伤害占比·' + src
    return best, which


# ---------------------------------------------------------------- sweep: every talent allocation × set

def sweep_jobs(n, seed):
    jobs, keys = [], []
    builds = []
    for fam in FAMS:
        builds.append(('%s|none' % fam, {'set': fam}))
        for t in TAL:
            builds.append(('%s|%s' % (fam, '+'.join(t)), {'set': fam, 'talents': list(t)}))
    for fam, cf in (('burst', 'sustain'), ('scorch', 'burst'), ('sustain', 'scorch')):
        builds.append(('%s/%s|none' % (fam, cf), {'set': fam, 'charm_fam': cf}))
        builds.append(('%s/%s|t1a+t2a+t3b' % (fam, cf), {'set': fam, 'charm_fam': cf, 'talents': ['t1a', 't2a', 't3b']}))
    for bk, spec in builds:
        for ctx in MAIN:
            for d in DODGES:
                jobs.append((spec, ctx, d, 'base', n, seed)); keys.append((bk, ctx, d, 'base'))
            for stn in STANCES:
                if stn != 'base':
                    jobs.append((spec, ctx, 0.5, stn, n, seed)); keys.append((bk, ctx, 0.5, stn))
    return jobs, keys


def stance_value(R, bk, ctx, stn):
    """(Δ clear points, Δ run time %) of playing stance stn instead of base, same build, dodge 0.5"""
    b, s = R[(bk, ctx, 0.5, 'base')], R[(bk, ctx, 0.5, stn)]
    return 100 * (s['clear'] - b['clear']), rel(s['secs'], b['secs'])


def fmt_r(r, keys):
    out = []
    for k in keys:
        v = r.get(k)
        out.append('—' if v is None else ('%.1f' % v if isinstance(v, float) else str(v)))
    return out


TIE = {'clear': 0.015, 'secs': 0.01, 'potions': 0.03}


def cmp_cell(a, b):
    """+1 a better beyond tie, -1 worse, 0 tie — for clear (higher), secs / potions (lower)"""
    out = {}
    dc = a['clear'] - b['clear']
    out['clear'] = 0 if abs(dc) <= TIE['clear'] else (1 if dc > 0 else -1)
    for k in ('secs', 'potions'):
        x = rel(a[k], b[k])
        out[k] = 0 if x is None or abs(x) <= TIE[k] else (1 if x < 0 else -1)
    return out


def row_nodes(r):
    return ['t%d%s' % (r, c) for c in 'abc']


def sweep_report(R, n):
    c = cfgs()
    L = []
    w = L.append
    w('# 构筑多样性 · 全天赋 × 套装扫描（builddiv.py sweep），2026-10-04 CST\n')
    w('模型 = 当前配置：ember-v1-runs.yml balance_version %s（含 D158 改换怪属性）、ember-v1-growth.yml 现行数值（v7 冻结 + D147 文案），'
      'p1config 读 runs / MM / ember-v1.yml / Java 常量。每格 %d 局（同种子 7），躲招 0.3 / 0.5 / 0.7；行为梯度只在 0.5 档。' % (c['bv'], n))
    w('普通重打 = 第二次以后的普通局（每局 1 只词缀精英，炽热 / 分裂 / 厚甲 各约 1/3）；挑战 = 无词缀精英。'
      '「可感」标记（启发式）：· = 相对变化 5–10%（细心的人可能察觉），● = ≥10%（明显）；不标 = <5%（基本察觉不到）。\n')
    # ---- A: sets
    w('## A. 套装本身（无天赋）\n')
    for ctx in MAIN:
        w('### %s\n' % CTX[ctx][0])
        w('| 套装 | 通关 0.3 / 0.5 / 0.7 | 全程 s | 杂兵房 s | 首领 s | 炽热 / 分裂 / 厚甲房 s | 喝药/局 | 受伤 ×H/局 | 伤害占比 普攻/烬斩/套装 | 套装回复 ×H/局 | 最低血(中位/P10) | 套装触发/局 |')
        w('|---|---|---:|---:|---:|---|---:|---:|---|---:|---|---:|')
        for fam in FAMS:
            bk = '%s|none' % fam
            r = R[(bk, ctx, 0.5, 'base')]
            cl = ' / '.join('%.0f%%' % (100 * R[(bk, ctx, d, 'base')]['clear']) for d in DODGES)
            sh = r['share']
            setsh = sh.get('burst', 0) + sh.get('burn', 0) + sh.get('spread', 0)
            trig = r['n_burst'] + r['n_burn'] + r['n_sustain']
            af = ' / '.join('%.1f' % r['t_affix_' + a] if r.get('t_affix_' + a) else '—' for a in AFFIX_ZH)
            w('| %s | %s | %.1f | %.1f | %.1f | %s | %.2f | %.2f | %.0f / %.0f / %.0f%% | %.2f | %.2f / %.2f | %.1f |' % (
                FAM_ZH[fam], cl, r['secs'] or 0, r['t_trash'] or 0, r['t_boss'] or 0, af, r['potions'], r['taken'],
                100 * sh.get('swing', 0), 100 * sh.get('skill', 0), 100 * setsh, r['heal_sustain'], r['minhp'] or 0, r['minhp_p10'] or 0, trig))
        w('')
    w('### 无套（刃和护符不同系 = 护符选错 / 为高阶混穿）\n')
    w('刃 / 护符的系别本身不改 B / H，只决定成不成套，所以三种混穿（烬爆刃+炽愈符、焚烬刃+烬爆符、炽愈刃+焚烬符）结果逐位相同，合成一行。\n')
    w('| 情境 | 无套 通关 0.3 / 0.5 / 0.7 | 全程 s | 首领 s | 喝药 | 对比 焚烬 / 烬爆 / 炽愈 成套（0.5 档通关点） |')
    w('|---|---|---:|---:|---:|---|')
    for ctx in MAIN:
        bk = 'burst/sustain|none'
        r = R[(bk, ctx, 0.5, 'base')]
        cl = ' / '.join('%.0f%%' % (100 * R[(bk, ctx, d, 'base')]['clear']) for d in DODGES)
        w('| %s | %s | %.1f | %.1f | %.2f | %s |' % (ctx, cl, r['secs'] or 0, r['t_boss'] or 0, r['potions'],
          ' / '.join('%+.0f' % (100 * (r['clear'] - R[('%s|none' % f, ctx, 0.5, 'base')]['clear'])) for f in FAMS)))
    w('\n无套时第 3 排天赋不生效（游戏里同样）：例 烬爆刃/炽愈符 + 回身斩+破缀+反震 vs 无天赋，Q07 普通 0.5 档通关 %+.1f 点、全程 %s（只剩第 1、2 排）。\n' % (
        100 * (R[('burst/sustain|t1a+t2a+t3b', 'q07n', 0.5, 'base')]['clear'] - R[('burst/sustain|none', 'q07n', 0.5, 'base')]['clear']),
        pct(rel(R[('burst/sustain|t1a+t2a+t3b', 'q07n', 0.5, 'base')]['secs'], R[('burst/sustain|none', 'q07n', 0.5, 'base')]['secs']))))
    # ---- B: node effects vs row-mates
    w('## B. 每个天赋相对同排另外两个的差别（其余两排 9 种组合平均，同一套装）\n')
    w('读法：同一格里只换这一排。Δ 通关 = 百分点；时间 / 喝药 = 相对。最大可感 = 所有可感指标里最大的相对差。'
      '第 3 排只列本套装那个（另外两个不生效 = 等于没点）。噪声：单格通关率标准差约 1.0–1.2 点（12 个种子实测），'
      '时间类约 0.1–0.4%，所以 <3 点的通关差和 <1% 的时间差都当作「没差」。\n')
    feltk = [('secs', '全程'), ('t_trash', '杂兵房'), ('t_boss', '首领'), ('t_affix_blazing', '炽热房'), ('t_affix_split', '分裂房'),
             ('t_affix_shield', '厚甲房'), ('potions', '喝药')]
    node_max = {}
    for ctx in MAIN:
        w('### %s\n' % CTX[ctx][0])
        w('| 套装 | 天赋 | Δ通关 0.3 / 0.5 / 0.7 | ' + ' | '.join(l for _, l in feltk) + ' | 最大可感 |')
        w('|---|---|---|' + '---:|' * len(feltk) + '---|')
        for fam in FAMS:
            for r_ in (1, 2, 3):
                nodes = row_nodes(r_)
                for x in nodes:
                    if r_ == 3 and x != ROW3[fam]:
                        continue  # off-set row-3 nodes do nothing (game: same); the own node is compared with them
                    dcl = {d: [] for d in DODGES}
                    dm = {k: [] for k, _ in feltk}
                    for t in TAL:
                        if t[r_ - 1] != x:
                            continue
                        mates = [tuple(y if i == r_ - 1 else t[i] for i in range(3)) for y in nodes]
                        for d in DODGES:
                            a = R[('%s|%s' % (fam, '+'.join(t)), ctx, d, 'base')]
                            ms = [R[('%s|%s' % (fam, '+'.join(m)), ctx, d, 'base')] for m in mates]
                            dcl[d].append(100 * (a['clear'] - statistics.mean(m['clear'] for m in ms)))
                            if d == 0.5:
                                for k, _ in feltk:
                                    vals = [m[k] for m in ms if m[k] is not None]
                                    if a[k] is not None and vals:
                                        dm[k].append(rel(a[k], statistics.mean(vals)))
                    eff = {k: (statistics.mean(v) if v else None) for k, v in dm.items()}
                    mx = max((abs(v), lab) for (k, lab) in feltk for v in [eff[k]] if v is not None)
                    node_max[(fam, ctx, x)] = mx
                    w('| %s | %s | %s | %s | %s%s |' % (FAM_ZH[fam], node_names()[x], ' / '.join('%+.1f' % statistics.mean(dcl[d]) for d in DODGES),
                      ' | '.join(pct(eff[k]) + mark(eff[k]) for k, _ in feltk), pct(mx[0], '%.1f%%'), (' ' + mx[1]) if mx[0] >= JND else ''))
        w('')
    # ---- C: dominance inside each row
    w('## C. 同排两两比较：谁在哪些格子里更好（通关 ±1.5 点、全程 ±1%、喝药 ±3% 以内算平）\n')
    w('格子 = 3 套装 × 另两排 9 种 × 3 情境 × 3 躲招档 = 243。「严格更好」= 三项都不差且至少一项更好；「互有胜负」= 一项好一项差；「平」= 三项都在误差内。\n')
    w('最后一列「可感差」= 该格里 A、B 在任一可感指标（全程 / 杂兵 / 首领 / 各词缀房用时、喝药）差 ≥5% 或通关差 ≥3 点的比例 —— 数值上「严格更好」不等于玩家能感觉到。\n')
    w('| 排 | A vs B | A 严格更好 | B 严格更好 | 互有胜负 | 平 | 可感差 |')
    w('|---|---|---:|---:|---:|---:|---:|')
    dom = {}
    for r_ in (1, 2, 3):
        nodes = row_nodes(r_)
        for x, y in itertools.combinations(nodes, 2):
            cnt = {'a': 0, 'b': 0, 'mix': 0, 'tie': 0}
            felt = 0
            for fam in FAMS:
                for t in TAL:
                    if t[r_ - 1] != x:
                        continue
                    ty = tuple(y if i == r_ - 1 else t[i] for i in range(3))
                    for ctx in MAIN:
                        for d in DODGES:
                            ra, rb = R[('%s|%s' % (fam, '+'.join(t)), ctx, d, 'base')], R[('%s|%s' % (fam, '+'.join(ty)), ctx, d, 'base')]
                            cc = cmp_cell(ra, rb)
                            fd = max([abs(rel(ra[k], rb[k]) or 0) for k in ('secs', 't_trash', 't_boss', 't_affix_blazing', 't_affix_split', 't_affix_shield', 'potions')])
                            if fd >= JND or abs(ra['clear'] - rb['clear']) >= 0.03:
                                felt += 1
                            v = list(cc.values())
                            if all(z == 0 for z in v):
                                cnt['tie'] += 1
                            elif min(v) >= 0:
                                cnt['a'] += 1
                            elif max(v) <= 0:
                                cnt['b'] += 1
                            else:
                                cnt['mix'] += 1
            tot = sum(cnt.values())
            dom[(x, y)] = cnt
            w('| %d | %s vs %s | %d%% | %d%% | %d%% | %d%% | %d%% |' % (r_, node_names()[x], node_names()[y], *(round(100 * cnt[k] / tot) for k in ('a', 'b', 'mix', 'tie')), round(100 * felt / tot)))
    w('\n第 3 排只在对应套装成套时生效，所以 243 格里有 2/3 是「两个都不生效」→ 必然平；第 3 排只看自己套装的格子见 B 表。\n')
    # ---- D: identical builds (clusters at the JND)
    w('## D. 27 种完整加点里，玩起来能分出几种？（同套装，单链聚类：可感指标全部 <5% 且行为梯度差 <5 点 = 同一组）\n')
    for fam in FAMS:
        bks = ['%s|%s' % (fam, '+'.join(t)) for t in TAL]
        def dist(a, b):
            m = 0.0
            for ctx in MAIN:
                x, _ = felt_diff(R[(a, ctx, 0.5, 'base')], R[(b, ctx, 0.5, 'base')])
                m = max(m, x)
                for stn in STANCES:
                    if stn == 'base':
                        continue
                    sa, sb = stance_value(R, a, ctx, stn), stance_value(R, b, ctx, stn)
                    m = max(m, abs((sa[1] or 0) - (sb[1] or 0)))
            return m
        parent = {b: b for b in bks}
        def find(b):
            while parent[b] != b:
                parent[b] = parent[parent[b]]
                b = parent[b]
            return b
        for a, b in itertools.combinations(bks, 2):
            if dist(a, b) < JND:
                parent[find(a)] = find(b)
        groups = {}
        for b in bks:
            groups.setdefault(find(b), []).append(b)
        gl = sorted(groups.values(), key=len, reverse=True)
        w('- **%s**：27 → **%d 组**。' % (FAM_ZH[fam], len(gl)) + '；'.join('[%s]' % '、'.join(tname(tuple(b.split('|')[1].split('+'))) for b in g[:4]) + ('…共 %d 个' % len(g) if len(g) > 4 else '') for g in gl[:6]))
    w('')
    # ---- E: behaviour gradients
    w('## E. 行为梯度：同一构筑换一种打法能快 / 稳多少（躲招 0.5 档）\n')
    w('谨慎 = 多躲（躲招 +0.1）少砍（出手率 −0.1）；贪刀 = 反过来；聚怪 = 烬斩 / 烬爆一次打中 5 个（原 3 个）；单点 = 1 个；硬吃 = 预警招不额外躲。'
      '每格「Δ通关点 / Δ全程」。如果换天赋后某一列的数明显变了（≥5 点或 ≥5%），说明这个天赋改变了「该怎么打」。\n')
    for ctx in MAIN:
        w('### %s\n' % CTX[ctx][0])
        stn = [s for s in STANCES if s != 'base']
        w('| 构筑 | ' + ' | '.join({'careful': '谨慎', 'greedy': '贪刀', 'stack': '聚怪', 'single': '单点', 'facetank': '硬吃'}[s] for s in stn) + ' |')
        w('|---|' + '---|' * len(stn))
        for fam in FAMS:
            rows = [('%s|none' % fam, FAM_ZH[fam] + ' 无天赋')]
            # the 3 builds with the largest stance-gradient deviation from the no-talent build of the set
            dev = []
            for t in TAL:
                bk = '%s|%s' % (fam, '+'.join(t))
                m = max(max(abs(stance_value(R, bk, ctx, s)[0] - stance_value(R, '%s|none' % fam, ctx, s)[0]) / 100,
                            abs((stance_value(R, bk, ctx, s)[1] or 0) - (stance_value(R, '%s|none' % fam, ctx, s)[1] or 0))) for s in stn)
                dev.append((m, bk))
            dev.sort(reverse=True)
            for m, bk in dev[:2] + dev[-1:]:
                rows.append((bk, '%s %s（偏离 %.1f）' % (FAM_ZH[fam], tname(tuple(bk.split('|')[1].split('+'))), 100 * m)))
            for bk, lab in rows:
                w('| %s | %s |' % (lab, ' | '.join('%+.0f / %s' % (stance_value(R, bk, ctx, s)[0], pct(stance_value(R, bk, ctx, s)[1])) for s in stn)))
        w('')
    # ---- F: whole-system spread
    w('## F. 系统总量：同套装内 27 种加点的最好 vs 最差，对比换套装\n')
    w('| 情境 | 躲招 | 指标 | 天赋 27 种的极差（焚烬 / 烬爆 / 炽愈） | 三套装之间的极差（无天赋） |')
    w('|---|---|---|---|---|')
    for ctx in MAIN:
        for d in DODGES:
            for k, lab in (('clear', '通关'), ('secs', '全程'), ('t_boss', '首领用时'), ('potions', '喝药')):
                sp = []
                for fam in FAMS:
                    vals = [R[('%s|%s' % (fam, '+'.join(t)), ctx, d, 'base')][k] for t in TAL]
                    vals = [v for v in vals if v is not None]
                    if k == 'clear':
                        sp.append('%.1f 点' % (100 * (max(vals) - min(vals))))
                    else:
                        sp.append(pct(max(vals) / min(vals) - 1, '%.1f%%'))
                sv = [R[('%s|none' % fam, ctx, d, 'base')][k] for fam in FAMS]
                sv = [v for v in sv if v is not None]
                ss = '%.1f 点' % (100 * (max(sv) - min(sv))) if k == 'clear' else pct(max(sv) / min(sv) - 1, '%.1f%%')
                w('| %s | %.1f | %s | %s | %s |' % (ctx, d, lab, ' / '.join(sp), ss))
    w('')
    return '\n'.join(L), node_max, dom


# ---------------------------------------------------------------- affix sweep

AFX_B = (None, 'b_affix', 'b_set', 'b_split')
AFX_C = (None, 'c_tele', 'c_share', 'c_affix')


def affix_jobs(n, seed):
    jobs, keys = [], []
    for fam in FAMS:
        for r2 in (None, 't2a', 't2b', 't2c'):
            for tier in (1, 4):
                for ba in AFX_B:
                    for ca in AFX_C:
                        if tier == 4 and ba is None and ca is None:
                            continue
                        af = {k: v for k, v in (('blade', ba), ('charm', ca)) if v}
                        spec = {'set': fam, 'tier': tier}
                        if r2:
                            spec['talents'] = ['t1b', r2]   # row 1 held at 稳桩 (smallest boss effect) so row 2 is reachable
                        if af:
                            spec['affixes'] = af
                        for ctx in ('q07n', 'q07c'):
                            for d in DODGES:
                                jobs.append((spec, ctx, d, 'base', n, seed))
                                keys.append((fam, r2, tier if af else 0, ba, ca, ctx, d))
    return jobs, keys


def affix_report(R, n):
    L = []
    w = L.append
    w('# 构筑多样性 · 词条（D143）扫描（builddiv.py affix），2026-10-04 CST\n')
    w('刃词条 × 护符词条（空 + 各 3 个）× 档位 1（标准件上限 / 最常见的一次洗出）/ 4（极品满档），× 3 套装 × 第 2 排（未点 / 破缀 / 守缀 / 拆分；第 1 排固定稳桩只为解锁第 2 排）。'
      '每格 %d 局，Q07 普通重打（有词缀精英）与 Q07 挑战，躲招 0.3 / 0.5 / 0.7。数值 = 相对「同套装、同第 2 排、无词条」。\n' % n)
    w('锁定词条（D148）不改数值，只改到手速度：rerollsim 极品件指定词条满档 不锁 15.9 次 → 锁定 6.2 次（1910 → 1136 碎片），所以它影响的是「多快拿到」，下表的 4 档就是拿到之后。\n')
    w('## 单条词条的效果（另一个槽空着；3 套装 × 4 种第 2 排平均）\n')
    w('| 词条 | 档 | 情境 | Δ通关 0.3 / 0.5 / 0.7（点） | 全程 | 首领 | 炽热房 | 分裂房 | 厚甲房 | 喝药 | 最大可感 |')
    w('|---|---:|---|---|---:|---:|---:|---:|---:|---:|---|')
    feltk = [('secs', '全程'), ('t_boss', '首领'), ('t_affix_blazing', '炽热房'), ('t_affix_split', '分裂房'), ('t_affix_shield', '厚甲房'), ('potions', '喝药')]
    zh = {}
    for slot, lst in cfgs()['g']['reroll']['affixes'].items():
        for a in lst:
            zh[a['id']] = a['name']
    summary = {}
    for aid in [a for a in AFX_B if a] + [a for a in AFX_C if a]:
        for tier in (1, 4):
            for ctx in ('q07n', 'q07c'):
                dcl = {d: [] for d in DODGES}
                dm = {k: [] for k, _ in feltk}
                for fam in FAMS:
                    for r2 in (None, 't2a', 't2b', 't2c'):
                        ba, ca = (aid, None) if aid.startswith('b_') else (None, aid)
                        for d in DODGES:
                            a = R[(fam, r2, tier, ba, ca, ctx, d)]
                            b = R[(fam, r2, 0, None, None, ctx, d)]
                            dcl[d].append(100 * (a['clear'] - b['clear']))
                            if d == 0.5:
                                for k, _ in feltk:
                                    x = rel(a[k], b[k])
                                    if x is not None:
                                        dm[k].append(x)
                eff = {k: (statistics.mean(v) if v else None) for k, v in dm.items()}
                cand = [(abs(eff[k]), lab) for k, lab in feltk if eff[k] is not None]
                mx = max(cand) if cand else (0, '')
                summary[(aid, tier, ctx)] = mx
                w('| %s | %d | %s | %s | %s | %s%s |' % (zh[aid], tier, ctx, ' / '.join('%+.1f' % statistics.mean(dcl[d]) for d in DODGES),
                  ' | '.join(pct(eff[k]) + mark(eff[k]) for k, _ in feltk), pct(mx[0], '%.1f%%'), (' ' + mx[1]) if mx[0] >= JND else ''))
    w('')
    w('## 两槽组合：最好 vs 最差（4 档，Q07 普通重打 0.5 档，同套装同第 2 排）\n')
    w('| 套装 | 第 2 排 | 最快的一对 | 全程 | 最慢的一对 | 全程 | 差 | 通关差（点） |')
    w('|---|---|---|---:|---|---:|---:|---:|')
    for fam in FAMS:
        for r2 in (None, 't2a', 't2b', 't2c'):
            cells = [((ba, ca), R[(fam, r2, 4, ba, ca, 'q07n', 0.5)]) for ba in AFX_B for ca in AFX_C if ba or ca]
            cells.sort(key=lambda x: x[1]['secs'])
            f_, s_ = cells[0], cells[-1]
            nm = lambda p: '+'.join(zh[x] for x in p if x) or '无'
            w('| %s | %s | %s | %.1f | %s | %.1f | %s | %.1f |' % (FAM_ZH[fam], node_names().get(r2, '未点'), nm(f_[0]), f_[1]['secs'], nm(s_[0]), s_[1]['secs'],
              pct(s_[1]['secs'] / f_[1]['secs'] - 1, '%.1f%%'), 100 * (max(c[1]['clear'] for c in cells) - min(c[1]['clear'] for c in cells))))
    w('')
    return '\n'.join(L), summary


# ---------------------------------------------------------------- raid (R01 / R03, p1party)

RAID_ENH = 9


def raid_measure(job):
    """job = (spec, raid key, size, dodge, trials, seed): member 0 wears `spec`, the others no growth; same sets mix"""
    spec, key, size, d, trials, seed = job
    c = cfgs()
    cfg = c['n']
    m = p1party.raid_map(cfg, key)
    rr = (miniyaml.load(os.path.join(p1config.ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml')).get('raid_revive') or {})
    p1party.LAST_REVIVE_HP = float(rr.get('last_phase_hp', 0)); p1party.LAST_REVIVE_DELAY = float(rr.get('delay', 10))
    mates = spec.get('mates', ['burst', 'sustain', 'scorch', 'burst'])
    sts, kns = [], []
    for i in range(size):
        fam = spec['set'] if i == 0 else mates[(i - 1) % len(mates)]
        msp = spec if i == 0 else (spec.get('mate_spec') or {})
        p1sim.GROWTH = growth_fn(dict(msp, set=fam)) if (msp.get('talents') or msp.get('affixes') or msp.get('extra')) else None
        E = RAID_ENH; bl = dict(p1sim.item(fam, 'blade', 3), enh=E); ch = dict(p1sim.item(spec.get('charm_fam', fam) if i == 0 else fam, 'charm', 3), enh=E)
        sts.append(p1sim.stats(cfg, bl, ch, 30)); kns.append(p1sim.Knobs(d))
        p1sim.GROWTH = None
    rec = __import__('collections').Counter()
    orig = p1party.Party.hurt

    def hurt(self, mem, raw, tele, dodgeable=True, kind='mob'):
        if mem is not self.ms[0]:
            return orig(self, mem, raw, tele, dodgeable, kind)
        hp0, u0 = mem.hp, mem.used
        orig(self, mem, raw, tele, dodgeable, kind)
        healed = (mem.used - u0) * cfg['potion_pct'] * p1sim.gm(mem.st, 'potion') * mem.st['H']
        dmg = hp0 - (mem.hp - healed)
        if dmg > 1e-9:
            rec['taken_' + kind] += dmg / mem.st['H']
            rec['n_hit_' + kind] += 1
        elif kind == 'tele':
            rec['n_dodge_tele'] += 1
        if hp0 > 0 and mem.hp <= 0:
            rec['deaths'] += 1
    p1party.Party.hurt = hurt
    rng = random.Random(seed)
    ok_n, boss = 0, []
    for _ in range(trials):
        ok, t, deaths, pots, bsecs = p1party.run_party(cfg, m, sts, kns, rng)
        if ok:
            ok_n += 1; boss.append(bsecs)
    p1party.Party.hurt = orig
    out = {'clear': ok_n / trials, 'boss': statistics.mean(boss) if boss else None}
    for k, v in rec.items():
        out[k] = v / trials
    return out


RAID_BUILDS = [
    ('无天赋', {}),
    ('回身斩', {'talents': ['t1a']}), ('稳桩', {'talents': ['t1b']}), ('踏步回气', {'talents': ['t1c']}),
    ('稳桩+守缀+扛核', {'talents': ['t1b', 't2b', 't3c']}),
    ('稳桩+守缀+反震', {'talents': ['t1b', 't2b', 't3b']}),
    ('稳桩+守缀+燎原', {'talents': ['t1b', 't2b', 't3a']}),
    ('分核纹 4 档', {'affixes': {'charm': 'c_share'}, 'tier': 4}),
    ('定身纹 4 档', {'affixes': {'charm': 'c_tele'}, 'tier': 4}),
    ('扛核 + 分核纹 4 档', {'talents': ['t1b', 't2b', 't3c'], 'affixes': {'charm': 'c_share'}, 'tier': 4}),
]


def raid_jobs(trials, seed):
    jobs, keys = [], []
    for key in ('r01', 'r03'):
        for fam in FAMS:
            for lab, sp in RAID_BUILDS:
                for d in DODGES:
                    jobs.append((dict(sp, set=fam), key, 4, d, trials, seed)); keys.append((key, fam, lab, d))
    # everybody 炽愈 + 扛核 (weights equal → no shift) vs only member 0
    for d in DODGES:
        sp = {'set': 'sustain', 'talents': ['t1b', 't2b', 't3c'], 'mates': ['sustain'], 'mate_spec': {'talents': ['t1b', 't2b', 't3c']}}
        jobs.append((sp, 'r03', 4, d, trials, seed)); keys.append(('r03', 'sustain', '全队炽愈都点扛核', d))
        sp = {'set': 'sustain', 'mates': ['sustain']}
        jobs.append((sp, 'r03', 4, d, trials, seed)); keys.append(('r03', 'sustain', '全队炽愈无天赋', d))
    return jobs, keys


def raid_report(R, trials):
    L = []
    w = L.append
    w('# 构筑多样性 · 团本（builddiv.py raid），2026-10-04 CST\n')
    w('p1party，4 人队：1 号位穿被测构筑，队友 3 人无成长（套装 烬爆 / 炽愈 / 焚烬 各一，T3+9 Lv30 = 三觉醒），每格 %d 次，同种子。'
      '只看 1 号位自己：受到的烬核分摊 / 预警招 / 小怪伤害（×H/局）、倒下次数；全队：通关率、首领用时。\n' % trials)
    for key in ('r01', 'r03'):
        w('## %s\n' % key.upper())
        w('| 1 号位套装 | 构筑 | 通关 0.3 / 0.5 / 0.7 | 首领 s (0.5) | 烬核分摊 ×H | 预警招 ×H | 首领普攻+小怪 ×H | 倒下/局 |')
        w('|---|---|---|---:|---:|---:|---:|---:|')
        for fam in FAMS:
            for lab, _ in RAID_BUILDS:
                r = R[(key, fam, lab, 0.5)]
                w('| %s | %s | %s | %s | %.2f | %.2f | %.2f | %.2f |' % (FAM_ZH[fam], lab, ' / '.join('%.0f%%' % (100 * R[(key, fam, lab, d)]['clear']) for d in DODGES),
                  '%.0f' % r['boss'] if r['boss'] else '—', r.get('taken_share', 0), r.get('taken_tele', 0), r.get('taken_boss', 0) + r.get('taken_mob', 0), r.get('deaths', 0)))
        w('')
    w('## 扛核要「有人不点」才有意义\n')
    w('| 构筑 | 通关 0.3 / 0.5 / 0.7 | 1 号位烬核 ×H (0.5) |')
    w('|---|---|---:|')
    for lab in ('全队炽愈无天赋', '全队炽愈都点扛核'):
        r = R[('r03', 'sustain', lab, 0.5)]
        w('| %s | %s | %.2f |' % (lab, ' / '.join('%.0f%%' % (100 * R[('r03', 'sustain', lab, d)]['clear']) for d in DODGES), r.get('taken_share', 0)))
    w('')
    return '\n'.join(L)


# ---------------------------------------------------------------- tolerance check (progress must not move)

TOL_SEEDS = (7, 11, 13)


def tol_jobs(spec, n):
    jobs, keys = [], []
    for k in p1sim.REF_GEAR:
        for kind in ('r', 'c'):
            for d in DODGES:
                for sd in TOL_SEEDS:
                    jobs.append((spec, k + kind, d, 'base', n, sd)); keys.append((k + kind, d, sd))
    return jobs, keys


def tol_rates(spec, n, nproc):
    jobs, keys = tol_jobs(spec, n)
    res = run_jobs(jobs, nproc)
    out = {}
    for (ck, d, sd), r in zip(keys, res):
        out.setdefault((ck, d), []).append(r['clear'])
    return {k: statistics.mean(v) for k, v in out.items()}


def tol_diff(a, b):
    """max up / max down (points) and the worst cell"""
    ds = {k: 100 * (a[k] - b[k]) for k in a}
    up = max(ds.items(), key=lambda x: x[1]); dn = min(ds.items(), key=lambda x: x[1])
    return up, dn


# ---------------------------------------------------------------- proposals (sim-only modifier keys marked PROPOSAL in p1sim)

def node_mods(nid):
    return dict(next(n for n in cfgs()['g']['talents']['nodes'] if n['id'] == nid)['mods'])


PROPOSALS = [
    {'id': 'P1', 'node': 't3a', 'set': 'scorch', 'name': '燎原 → 连锁燃烧',
     'text': '燃烧中的敌人倒下时，把剩余燃烧（最多 4 秒）整段传给最近的未燃烧敌人，无冷却；代价：燃烧每跳 ×{burn_mult}',
     'variants': [{'burn_spread': 4, 'spread_icd': 0, 'burn_mult': x} for x in (0.70, 0.75, 0.80)]},
    {'id': 'P2', 'node': 't3b', 'set': 'burst', 'name': '反震 → 聚爆',
     'text': '烬爆一次命中 ≥3 个目标时系数 ×{burst_pack}；只命中 1 个时 ×{burst_solo}（2 个不变）',
     'variants': [{'burst_pack': p, 'burst_solo': x} for p, x in ((1.3, 0.3), (1.3, 0.4), (1.4, 0.3))]},
    {'id': 'P3', 'node': 't3c', 'set': 'sustain', 'name': '扛核 → 低血回涌（保留团本 1.5 份）',
     'text': '炽愈回复：生命 <{sustain_low:.0%} 时 ×{low}，≥ 时 ×{sustain_mult}（触发间隔 / 冷却不变）；烬核分摊仍算 1.5 份',
     'variants': [{'sustain_low': 0.5, 'sustain_mult': a, 'sustain_low_mult': b / a, 'share_w': 1.5} for a, b in ((0.5, 1.4), (0.4, 1.6), (0.6, 1.2))]},
    {'id': 'P4', 'node': 't1a', 'set': None, 'name': '回身斩 → 破绽窗口',
     'text': '首领每次预警招落下（躲没躲开都算）后 {win_secs:.0f} 秒内对首领伤害 ×{win_dmg}；窗口外对首领 ×{win_out}',
     'variants': [{'win_dmg': 1.25, 'win_secs': 2, 'win_out': x} for x in (0.91, 0.93)]},
    {'id': 'P5', 'node': 't1b', 'set': None, 'name': '稳桩 → 铁壁',
     'text': '受到首领预警招伤害 ×{taken_tele}；代价：受到首领普攻 ×{taken_boss}',
     'variants': [{'taken_tele': 0.7, 'taken_boss': x} for x in (1.10, 1.15, 1.20)]},
    {'id': 'P6', 'node': 't1c', 'set': None, 'name': '踏步回气 → 回气（以躲代药）',
     'text': '躲开首领预警招回 {dodge_heal:.0%} 最大生命（{dodge_icd:.0f} 秒一次）；代价：回复药 ×{potion}',
     'variants': [{'dodge_heal': 0.04, 'dodge_icd': 10, 'potion': x} for x in (0.80, 0.85, 0.90)]},
    {'id': 'P7', 'node': 't2c', 'set': None, 'name': '拆分 → 破甲（厚甲专精）',
     'text': '对厚甲精英伤害 ×{dmg_affix_shield}（正好抵掉 1.6 倍生命）；对炽热 / 分裂精英（含分身）×{dmg_affix_blazing}',
     'variants': [{'dmg_affix_shield': 1.6, 'dmg_affix_blazing': x, 'dmg_affix_split': x} for x in (0.8, 0.9)]},
]


PROP_STANCES = dict(STANCES, tank={'tele_bonus': 0.0, 'uptime': +0.1}, hold={'hold_skill': True})


def prop_spec(fam, mods, base_set=True):
    return {'set': fam, 'extra': mods} if mods else {'set': fam}


def calib(pid, n=2000, nproc=7, fams=None):
    P = next(p for p in PROPOSALS if p['id'] == pid)
    fams = fams or ([P['set']] if P['set'] else ['burst'])
    out = []
    for fam in fams:
        off = tol_rates({'set': fam}, n, nproc)
        cur = tol_rates({'set': fam, 'extra': node_mods(P['node'])}, n, nproc)
        rows = [('现行 ' + node_names()[P['node']], cur)]
        for v in P['variants']:
            rows.append((json.dumps(v), tol_rates({'set': fam, 'extra': v}, n, nproc)))
        for lab, r in rows:
            u, d = tol_diff(r, off)
            u2, d2 = tol_diff(r, cur)
            out.append((fam, lab, u, d, u2, d2, r))
            print('%s %s %s | vs off up %+.1f (%s %.1f) down %+.1f (%s %.1f) | vs cur up %+.1f down %+.1f' % (
                pid, fam, lab, u[1], u[0][0], u[0][1], d[1], d[0][0], d[0][1], u2[1], d2[1]), flush=True)
    return out, off


# ---------------------------------------------------------------- systems: festival charm, honors, everything stacked

SYS_BUILDS = [
    ('无成长', {}),
    ('勋记全开', {'honors': 'all'}),
    ('烟火符（D139）', {'fest': True}),
    ('天赋 回身斩+破缀+本套', {'talents': ['t1a', 't2a', '@row3']}),
    ('词条 猎缀纹4+抗缀纹4', {'affixes': {'blade': 'b_affix', 'charm': 'c_affix'}, 'tier': 4}),
    ('全叠：天赋+勋记+词条4+烟火符', {'talents': ['t1a', 't2a', '@row3'], 'honors': 'all', 'affixes': {'blade': 'b_affix', 'charm': 'c_affix'}, 'tier': 4, 'fest': True}),
]


def sys_jobs(n, seed):
    jobs, keys = [], []
    for fam in FAMS:
        for lab, sp in SYS_BUILDS:
            sp = dict(sp, set=fam)
            if 'talents' in sp:
                sp['talents'] = [ROW3[fam] if x == '@row3' else x for x in sp['talents']]
            for ctx in MAIN:
                for d in DODGES:
                    jobs.append((sp, ctx, d, 'base', n, seed)); keys.append((fam, lab, ctx, d))
    return jobs, keys


def sys_report(R, n):
    L = []
    w = L.append
    w('# 构筑多样性 · 系统叠加（builddiv.py systems），2026-10-04 CST\n')
    w('每格 %d 局、同种子；相对「同套装无成长」。可感标记同 sweep（· 5–10%%，● ≥10%%）。\n' % n)
    feltk = [('secs', '全程'), ('t_trash', '杂兵房'), ('t_boss', '首领'), ('t_affix_blazing', '炽热房'), ('t_affix_split', '分裂房'), ('t_affix_shield', '厚甲房'), ('potions', '喝药')]
    for ctx in MAIN:
        w('## %s\n' % CTX[ctx][0])
        w('| 套装 | 构筑 | Δ通关 0.3 / 0.5 / 0.7 | ' + ' | '.join(l for _, l in feltk) + ' | 烟火迸发/局 | 最大可感 |')
        w('|---|---|---|' + '---:|' * len(feltk) + '---:|---|')
        for fam in FAMS:
            for lab, _ in SYS_BUILDS[1:]:
                a = {d: R[(fam, lab, ctx, d)] for d in DODGES}
                b = {d: R[(fam, '无成长', ctx, d)] for d in DODGES}
                eff = {k: rel(a[0.5][k], b[0.5][k]) for k, _ in feltk}
                cand = [(abs(v), l) for (k, l) in feltk for v in [eff[k]] if v is not None]
                mx = max(cand)
                w('| %s | %s | %s | %s | %.1f | %s%s |' % (FAM_ZH[fam], lab, ' / '.join('%+.1f' % (100 * (a[d]['clear'] - b[d]['clear'])) for d in DODGES),
                  ' | '.join(pct(eff[k]) + mark(eff[k]) for k, _ in feltk), a[0.5]['n_fest'], pct(mx[0], '%.1f%%'), (' ' + mx[1]) if mx[0] >= JND else ''))
        w('')
    return '\n'.join(L)

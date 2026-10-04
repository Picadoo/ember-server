#!/usr/bin/env python3
"""D167 random / targeted forge — offline check for docs/design/DESIGN-ember-forge-random-2026-10-04.md.

Simple on purpose (steering 10-04 15:45: the 6-slot p1sim / p2econ rewrite belongs to another worker): this file only
imports rules.py (M06 snapshot, read-only) and models the gear CHASE, not combat. Rule numbers (quality / craft tables,
target-family weight, extra-chest weight, marks per exchange, loot bias, challenge / abyss quality tables, raid floor,
rotation / rush marks, stamina, reroll table) come from rules.py; the forge numbers are the PROPOSAL below (the forge is
not live; once ember-v1.yml carries `forge:` that section wins and the header says so).

Modes
  odds    exact cost-equivalence audit: chosen-slot rolls per run of marks (forge) vs per run (drops), by quality
  affix   烙纹 pin vs D143 reroll: tries / shards / coins to a chosen affix at the quality cap; forge-charge use vs cap
  target  runs (T1/T2) and weeks (T3, after Q07) to a chosen piece: drops only / + 8-mark redemption / + random forge
  w30     two-极品 ownership after the Q07 first clear (W30 / P50 / P90), weekly Monte Carlo of the core blade + charm
          chase, calibrated to the D144 p2econ raw table (tools/p1sim/out-p2econ-d144.md, on-0.5); variants for the
          forge mark price, the weekly family conversion and the coin budget

usage
  python3 tools/p1sim/forgesim.py odds
  python3 tools/p1sim/forgesim.py affix  [--n 40000]
  python3 tools/p1sim/forgesim.py target [--n 40000]
  python3 tools/p1sim/forgesim.py w30    [--n 20000] [--weeks 12]
  python3 tools/p1sim/forgesim.py all    > tools/p1sim/out-forgesim-d167.md
"""
import argparse
import random
import re
import statistics

import rules

# ------------------------------------------------------------------ rule inputs (rules.py only)

SETTLE = rules.java('settle')


def _arr(name):
    m = re.search(r'\b' + name + r'\s*=\s*\{([^}]*)\}', SETTLE)
    return [int(x) for x in m.group(1).replace(' ', '').split(',') if x]


def _const(name):
    m = re.search(r'\b' + name + r'\s*=\s*([-0-9.]+)', SETTLE)
    s = m.group(1)
    return float(s) if '.' in s else int(s)


QUALITY_W = _arr('QUALITY_WEIGHTS')          # §5.1 70/23/6/1 — the ONLY table a forge roll may use
CRAFT_W = _arr('CRAFT_WEIGHTS')              # §5.2 70/20/9/1
CHALLENGE_W = _arr('CHALLENGE_QUALITY_WEIGHTS')
EXTRA_W = _arr('EXTRA_WEIGHTS')              # none / treasure / elite / chest
MARKS_PER = _const('MARKS_PER_EXCHANGE')     # 8
TARGET_W = _const('TARGET_WEIGHT')           # 0.60
CHEST = EXTRA_W[3] / float(sum(EXTRA_W))     # 5 % one more item
RUNS = rules.runs()
LOOT = RUNS.get('loot_bias') or {}
OWN, SLOT_W = float(LOOT.get('own_family', TARGET_W)), float(LOOT.get('slot', 0.5))
RAID_FLOOR = int((RUNS.get('raid_item') or {}).get('quality_floor', 1))
ROT = RUNS.get('rotation') or {}
ROT_MARKS = int(ROT.get('bonus_marks', 0)) * int(ROT.get('weekly_cap', 0))
RUSH_MARKS = int((((RUNS.get('rush') or {}).get('rush') or {}).get('reward') or {}).get('marks', 0))
RAID = next(iter((RUNS.get('raids') or {}).values()), {})
RAID_COST, RAID_CAP = int(RAID.get('cost', 50)), int(RAID.get('weekly_cap', 3))
RUN_COST = int(RUNS.get('cost', 30))
STAMINA = int(rules.data('cash')['stamina']['base_max'])
ABYSS_Q = [t['quality'] for t in (RUNS.get('abyss') or {}).get('tiers', [])]
REROLL = rules.growth()['reroll']
POOL = {s: [a['id'] for a in l if a.get('rollable', True) is not False] for s, l in REROLL['affixes'].items()}

# ------------------------------------------------------------------ the proposal (D167)

PROPOSAL = {
    # 8-mark redemption (the floor): unchanged for blade / charm; armor (6-slot stage 1) 4 marks — standard, craft 0, +0
    'redeem_marks': {'core': 8, 'armor': 4},
    # EVALUATED, NOT ADOPTED (w30 shows it moves the two-极品 W30 too far): marks → chosen family + slot with a random
    # quality / craft on QUALITY_W / CRAFT_W (the normal drop table), same tier
    'roll_marks': {'core': 8, 'armor': 8},
    'roll_blank': [0, 2, 4, 6],      # by tier T0..T3 (≥ 2 × the dismantle yield = tier)
    'roll_coin': [0, 300, 600, 1000],
    # 烙纹 pin: 1 余烬烙纹 = 40 shards (crafted at the forge); writing a chosen type costs pin_brand[tier] 烙纹 + the
    # reroll coin of that tier, i.e. exactly the price of one LOCKED reroll (shard + lock_shard = 80 / 160 / 240)
    'brand_shard': 40, 'brand_core': 0,
    'pin_brand': [0, 2, 4, 6],
    # per item: every 洗练 (locked or not) and every 烙纹 write uses 1. 13 = the longest single-type path that can be
    # forced (pin on a 标准 piece 1 + refine to 精良 ≤6 + refine to 卓越 ≤6, pity included)
    'charges': 13,
    # weekly same-slot family conversion (catalyst): 1 per character-week; quality kept up to 卓越 (极品 → 卓越)
    'conv_per_week': 1, 'conv_qcap': 2,
}
_live = (rules.data('p1') or {}).get('forge')
if isinstance(_live, dict):
    PROPOSAL.update(_live)
SOURCE = 'ember-v1.yml forge:' if isinstance(_live, dict) else 'PROPOSAL (forge not live)'

QN = ['标准', '精良', '卓越', '极品']


def pick(w, u):
    t = sum(w)
    x, acc = u * t, 0
    for i, v in enumerate(w):
        acc += v
        if x < acc:
            return i
    return len(w) - 1


def p_at_least(w, q):
    return sum(w[q:]) / float(sum(w))


def header(title):
    print('\n## ' + title + '\n')


# ------------------------------------------------------------------ odds

def odds():
    header('odds — cost-equivalence audit (forge roll vs drops, per run-equivalent)')
    core_drop = (1 + CHEST) * TARGET_W * 0.5          # chosen family + chosen slot per normal run, no map bias
    core_bias = (1 + CHEST) * OWN * SLOT_W            # on the map of the target family + slot (D81)
    armor_drop = 1.0 * TARGET_W * 0.25                # 6-slot stage 1: +1 armor per clear, 4 slots uniform (research §4.3)
    rows = [('核心件 普通本（无偏向）', core_drop, QUALITY_W), ('核心件 普通本（D81 本族本部位图）', core_bias, QUALITY_W),
            ('核心件 挑战版（D81 图）', core_bias, CHALLENGE_W), ('核心件 深渊第 10 层', core_drop, ABYSS_Q[-1] if ABYSS_Q else CHALLENGE_W),
            ('护甲 普通本（阶段 1 每局 +1 件）', armor_drop, QUALITY_W)]
    print('| 来源 | 每局：指定族+指定部位的件数 | ≥精良 | ≥卓越 | 极品 |')
    print('|---|---:|---:|---:|---:|')
    for name, n, w in rows:
        print('| %s | %.4f | %.4f | %.4f | %.5f |' % (name, n, n * p_at_least(w, 1), n * p_at_least(w, 2), n * p_at_least(w, 3)))
    for kind, n in (('core', core_drop), ('armor', armor_drop)):
        k = PROPOSAL['roll_marks'][kind]
        r = 1.0 / k
        print('| **随机成色锻造 %s（%d 印记 = %d 局；评估，未采用）** | %.4f | %.4f | %.4f | %.5f |' % (
            '核心件' if kind == 'core' else '护甲', k, k, r, r * p_at_least(QUALITY_W, 1), r * p_at_least(QUALITY_W, 2), r * p_at_least(QUALITY_W, 3)))
        kmin = -(-1.0 // n) if n > 0 else 0
        print('| ↳ 不比掉落好的最低印记数 ⌈1 / %.4f⌉ | %d | 按 %d：%s | | |' % (n, int(-(-1 // n)), k, '满足' if k >= 1.0 / n else '**不满足**'))
    print('\n（随机成色锻造只作对照，w30 节否决。）每局本来就有掉落；随机锻造用的是另外攒下的印记，所以它是**加法**，但每局印记换来的“指定件抽奖”永远少于同一局的掉落'
          '（核心件 1/%d = %.3f < %.3f；护甲 1/%d = %.3f < %.3f），成色表就是 §5.1 掉落表 %s，精工表 %s。' % (
              PROPOSAL['roll_marks']['core'], 1.0 / PROPOSAL['roll_marks']['core'], core_drop,
              PROPOSAL['roll_marks']['armor'], 1.0 / PROPOSAL['roll_marks']['armor'], armor_drop, QUALITY_W, CRAFT_W))


# ------------------------------------------------------------------ affix (D143 rules + pin + charges)

TW, CAP, PITY = REROLL['tier_weights'], REROLL['tier_cap'], int(REROLL['pity'])


def tier_roll(rng, cap, pity):
    if pity >= PITY:
        return cap
    return pick(TW[:cap], rng.random()) + 1


def chase(rng, cap, pool, how):
    """tries until the chosen affix (index 0) sits at `cap`. how: 'free' (unlocked until the type shows, then lock),
    'pin' (first try = 烙纹 write: type fixed, tier rolled, same pity), returns (tries, locked tries, pins)"""
    pity, have, tries, locks, pins = 0, False, 0, 0, 0
    while True:
        if how == 'pin' and not have:
            a, pins = 0, pins + 1
        elif have:
            a, locks = 0, locks + 1
        else:
            a = rng.randrange(pool)
        t = tier_roll(rng, cap, pity)
        pity = 0 if t >= cap else pity + 1
        tries += 1
        if a == 0:
            have = True
            if t >= cap:
                return tries, locks, pins


def affix(n):
    header('affix — 烙纹 pin vs D143 reroll (T3 prices; tier per D143: weights %s, cap by quality %s, %d misses → next = cap)' % (TW, CAP, PITY))
    rng = random.Random(167)
    t = 3
    coin, shard, lock = REROLL['coin'][t], REROLL['shard'][t], REROLL['lock_shard'][t]
    brand = PROPOSAL['pin_brand'][t]
    bsh, bco = PROPOSAL['brand_shard'], PROPOSAL['brand_core']
    print('一次洗练（T3）= %d 碎片（或一件重复件）+ %d 币；锁定另加 %d 碎片。烙纹写入（T3）= %d 烙纹（= %d 碎片 + %d 核心）+ %d 币。' % (
        shard, coin, lock, brand, brand * bsh, brand * bco, coin))
    print()
    print('| 部位（可洗词条数） | 成色（上限） | D143 不锁→锁：次数 / 碎片 / 币 | 烙纹定向→锁：次数 / 碎片 / 核心 / 币 | 定向最坏次数 | 次数 >%d 的概率（定向）|' % PROPOSAL['charges'])
    print('|---|---|---|---|---:|---:|')
    for slot, ids in POOL.items():
        for qi, cap in enumerate(CAP):
            a = [chase(rng, cap, len(ids), 'free') for _ in range(n)]
            b = [chase(rng, cap, len(ids), 'pin') for _ in range(n)]
            ta, la = statistics.mean(x[0] for x in a), statistics.mean(x[1] for x in a)
            tb, lb = statistics.mean(x[0] for x in b), statistics.mean(x[1] for x in b)
            over = sum(x[0] > PROPOSAL['charges'] for x in b) / float(n)
            print('| %s（%d） | %s（%d） | %.2f / %.0f / %.0f | %.2f / %.0f / %.1f / %.0f | %d | %.3f%% |' % (
                '刃' if slot == 'blade' else '护符', len(ids), QN[qi], cap, ta, ta * shard + la * lock, ta * coin,
                tb, (tb - 1) * shard + lb * lock + brand * bsh, brand * bco, tb * coin, max(x[0] for x in b), 100 * over))
    # charge use on the "refine first, then pin" vs "pin first, refine later" orders (standard → 卓越 by §5.3)
    print()
    print('锻造次数上限 %d 的压力测试（护符 3 选 1，T3）：标准件先写烙纹、再两次成色养成到卓越，每次养成后锁定追到新上限；对照：先养成到卓越再写烙纹。' % PROPOSAL['charges'])
    late, early = [], []
    for _ in range(n):
        pity, used = 0, 0
        for cap in (1, 2, 3):
            while True:
                tt = tier_roll(rng, cap, pity); pity = 0 if tt >= cap else pity + 1; used += 1
                if tt >= cap:
                    break
        late.append(used)
        early.append(chase(rng, 3, 3, 'pin')[0])
    print()
    caps = sorted({10, 12, PROPOSAL['charges'], 15})
    print('| 顺序 | 平均次数 | P90 | 最坏 | ' + ' | '.join('超过 %d 次' % c for c in caps) + ' |')
    print('|---|---:|---:|---:|' + '---:|' * len(caps))
    for name, l in (('先定向、后养成色（标准→精良→卓越）', late), ('先养成色到卓越、再定向', early)):
        l.sort()
        print('| %s | %.2f | %d | %d | %s |' % (name, statistics.mean(l), l[int(0.9 * len(l))], l[-1],
                                            ' | '.join('%.2f%%' % (100.0 * sum(x > c for x in l) / len(l)) for c in caps)))
    print('\n%d 次 = 能被逼出来的最长“单一词条”路线（标准件定向 1 + 精良 ≤6 + 卓越 ≤6，保底计入），所以不改主意的玩家永远不会被次数卡住；'
          '想换词条类型（护符 3 选 1）大约只够换一次。' % PROPOSAL['charges'])


# ------------------------------------------------------------------ target (runs / weeks to one chosen piece)

def runs_to(rng, per_run, w, q, marks_mode, k_roll, k_red, cap_runs=400):
    """runs until a chosen-slot piece of quality ≥ q exists. per_run = chosen family+slot items per run (fractional →
    Bernoulli per item slot), w = drop quality table. marks_mode: None | 'redeem' | 'roll' (roll uses QUALITY_W)."""
    marks = 0
    whole, frac = int(per_run), per_run - int(per_run)
    for r in range(1, cap_runs + 1):
        n = whole + (1 if rng.random() < frac else 0)
        for _ in range(n):
            if pick(w, rng.random()) >= q:
                return r
        marks += 1
        if marks_mode == 'redeem' and marks >= k_red:
            marks -= k_red
            if q == 0:
                return r
        if marks_mode == 'roll' and marks >= k_roll:
            marks -= k_roll
            if pick(QUALITY_W, rng.random()) >= q:
                return r
    return cap_runs


def target(n):
    header('target — runs to ONE chosen piece (chosen family + chosen slot), normal maps, 3 runs a day')
    rng = random.Random(4167)
    core = (1 + CHEST) * TARGET_W * 0.5
    armor = TARGET_W * 0.25
    kc, ka = PROPOSAL['roll_marks']['core'], PROPOSAL['roll_marks']['armor']
    rc, ra = PROPOSAL['redeem_marks']['core'], PROPOSAL['redeem_marks']['armor']
    print('| 目标 | 只靠掉落（局，均值 / P90） | + 兑换（%d / %d 印记） | + 兑换 + 随机锻造（%d 印记，仅成色目标） | 随机锻造比只兑换快 |' % (rc, ra, kc))
    print('|---|---|---|---|---:|')
    for slot_name, per, kr, kk in (('刃或护符', core, rc, kc), ('某件护甲（阶段 1）', armor, ra, ka)):
        for q in (0, 1, 2, 3):
            res = []
            for mode in (None, 'redeem', 'roll'):
                l = sorted(runs_to(rng, per, QUALITY_W, q, mode, kk, kr) for _ in range(n))
                res.append((statistics.mean(l), l[int(0.9 * n)]))
            if q == 0:  # any quality: the redemption is the floor; a roll is never worse, so "roll" = "redeem"
                res[2] = res[1]
            gain = 100.0 * (1 - res[2][0] / res[1][0])
            print('| %s ≥%s | %.1f / %d | %.1f / %d | %.1f / %d | %s |' % (slot_name, QN[q], res[0][0], res[0][1], res[1][0], res[1][1],
                                                                          res[2][0], res[2][1], ('−%.0f%%' % gain) if q else '—'))
    print('\n(“+ 兑换”只给标准件，所以对 ≥精良 的目标与只靠掉落相同；随机锻造的件最差也是标准件。'
          '极品行的 P90 截在 400 局。T3 之后的周数见下节 w30。)')


# ------------------------------------------------------------------ w30 (two 极品 after Q07)

# Calibration inputs from the D144 p2econ raw table (on-0.5, 600 players): weekly share of challenge / abyss runs
# (from the cumulative column), the median abyss tier, the raid clear rate (p2econ RAID_RATES['revive']).
CAL = {
    'rot': {'share': [0.02, 0.64, 0.66, 0.76, 0.72, 0.68, 0.79, 0.69, 0.71, 0.73, 0.75, 0.77]},
    'abyss': {'share': [0.03, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0],
              'tier': [0, 5, 5, 6, 7, 7, 8, 8, 8, 8, 8, 8]},
}
RAID_RATE = {1: 0.44, 2: 0.59, 3: 0.74, 4: 0.88}
# Share of non-raid runs that clear (a failed run gives no item and no mark). Fitted (--fit) so that the base variant
# lands on the D144 W30; abyss players step up a tier at >= 50 % clear (p2econ phase2_abyss), so a low value is expected.
EFF = {'rot': 0.79, 'abyss': 0.47}  # forgesim fit --n 2000: 0.787 / 0.470
D144_TWO = {'rot': [0, 2, 5, 12, 17, 22, 28, 34, 38, 41, 43, 46], 'abyss': [0, 4, 10, 17, 27, 35, 42, 48, 52, 56, 61, 63]}


def one_player(rng, route, weeks, v):
    """returns per-week qmin list and counters. v: variant dict {k: forge mark price or None, conv: (per week, qcap) or None,
    coin: forge coin budget per week (None = unlimited)}"""
    q = {'blade': 0, 'charm': 0}
    off = {'blade': 0, 'charm': 0}  # best off-family T3 piece kept in the gear library (conversion input)
    marks, out = 0, []
    st = {'forge': 0, 'forge_q3': 0, 'conv': 0, 'conv_q': 0, 'coin': 0, 'blank': 0, 'marks': 0}
    cal = CAL[route]

    def weaker():
        return 'blade' if q['blade'] <= q['charm'] else 'charm'

    def item(fam_t, slot_w, table, want, floor=0):
        s = want if rng.random() < slot_w else ('charm' if want == 'blade' else 'blade')
        qq = max(floor, pick(table, rng.random()))
        if rng.random() < fam_t:
            q[s] = max(q[s], qq)
        else:
            off[s] = max(off[s], qq)

    for w in range(1, weeks + 1):
        m0 = marks
        share = cal['share'][min(w, len(cal['share'])) - 1]
        stamina = STAMINA * 7
        if route == 'rot' and w >= 2:  # raids: until RAID_CAP clears or 5 tries
            clears = tries = 0
            while clears < RAID_CAP and tries < 5 and stamina >= RAID_COST:
                tries += 1; stamina -= RAID_COST
                if rng.random() < RAID_RATE[min(w, 4)]:
                    clears += 1; marks += 2
                    item(TARGET_W, 0.5, CHALLENGE_W, weaker())               # challenge settlement roll (raid map)
                    item(1.0, 0.5, CHALLENGE_W, weaker(), RAID_FLOOR)        # raid_item: target family, floor 精良
        for _ in range(stamina // RUN_COST):
            if rng.random() >= v.get('eff', EFF)[route]:
                continue
            want = weaker()
            if rng.random() < share:
                if route == 'abyss':
                    t = cal['tier'][min(w, len(cal['tier'])) - 1]
                    table = ABYSS_Q[t - 1] if t >= 1 else CHALLENGE_W
                    fam, sw = TARGET_W, 0.5      # abyss picks a random map: no family / slot bias
                else:
                    table, fam, sw = CHALLENGE_W, OWN, SLOT_W  # D81: farm the target-family map of the wanted slot
            else:
                table, fam, sw = QUALITY_W, TARGET_W, 0.5      # plain Q07 normal run (T3, normal table)
            marks += 1
            for _ in range(1 + (rng.random() < CHEST)):
                item(fam, sw, table, want)
        if route == 'rot':
            marks += ROT_MARKS
        if rng.random() < 0.9:
            marks += RUSH_MARKS
        st['marks'] += marks - m0
        # weekly family conversion (catalyst): one off-family piece of a slot that lacks 极品, quality kept up to qcap
        if v.get('conv'):
            per, qcap = v['conv']
            for _ in range(per):
                cand = [(min(off[s], qcap), s) for s in q if q[s] < 3 and min(off[s], qcap) > q[s]]
                if not cand:
                    break
                qq, s = max(cand)
                q[s] = qq; off[s] = 0
                st['conv'] += 1; st['conv_q'] += qq == 3
        # random forge: marks → the weaker slot, normal table; floor redemption is a standard piece (no 极品 effect)
        k = v.get('k')
        budget = v.get('coin')
        spent = 0
        while k and marks >= k and min(q.values()) < 3:
            cost = PROPOSAL['roll_coin'][3]
            if budget is not None and spent + cost > budget:
                break
            marks -= k; spent += cost
            st['forge'] += 1; st['coin'] += cost; st['blank'] += PROPOSAL['roll_blank'][3]
            s = 'blade' if (q['blade'] < 3 and (q['blade'] <= q['charm'] or q['charm'] == 3)) else 'charm'
            qq = pick(QUALITY_W, rng.random())
            st['forge_q3'] += qq == 3
            q[s] = max(q[s], qq)
        if not k:
            marks %= MARKS_PER  # base: redeemed into standard pieces (dismantled for blanks) — no quality effect
        out.append(min(q.values()))
    return out, st


def run_cells(route, v, n, weeks):
    rng = random.Random(9167 if route == 'rot' else 9168)
    curves, tot = [], {}
    for _ in range(n):
        qm, st = one_player(random.Random(rng.getrandbits(64)), route, weeks, v)
        curves.append(qm)
        for kk, x in st.items():
            tot[kk] = tot.get(kk, 0) + x
    return [100.0 * sum(c[w] >= 3 for c in curves) / n for w in range(weeks)], tot


def fit(n, weeks):
    header('fit — clear rate that puts the base variant on the D144 W30')
    for route in ('rot', 'abyss'):
        goal = cross(D144_TWO[route], 30.0)
        lo, hi = 0.2, 1.0
        for _ in range(12):
            mid = (lo + hi) / 2
            e = dict(EFF); e[route] = mid
            wk = cross(run_cells(route, {'eff': e}, n, weeks)[0], 30.0) or 99
            lo, hi = (mid, hi) if wk > goal else (lo, mid)
        print('- %s: D144 W30 %.2f → clear rate %.3f' % (route, goal, (lo + hi) / 2))


def cross(curve, at):
    prev = (0, 0.0)
    for w, v in enumerate(curve, 1):
        if v >= at:
            return prev[0] + (w - prev[0]) * (at - prev[1]) / max(1e-9, v - prev[1])
        prev = (w, v)
    return None


def w30(n, weeks):
    header('w30 — two 极品 (blade + charm, target family, T3) after the Q07 first clear')
    print('Weekly Monte Carlo of the core chase only (no combat): 3 runs / day, D81 map bias on challenge runs, raids 3 / week '
          'from week 2 (轮换 + 团本 route), abyss tiers by week (深渊 route), +%d rotation marks, rush %d mark at 90 %%. '
          'Inputs calibrated to the D144 p2econ raw table (on-0.5): challenge share by week and median abyss tier. '
          'W30 = first week ≥ 30 %% own two 极品 (linear between weeks). n = %d players per cell, same seeds across variants.\n'
          % (ROT_MARKS, RUSH_MARKS, n))
    variants = [
        ('base（现行：8 印记兑换标准件，无转化）', {}),
        ('转化 1/周 成色截到卓越（提案）', {'conv': (1, PROPOSAL['conv_qcap'])}),
        ('转化 1/周 保留极品（否决）', {'conv': (1, 3)}),
        ('转化 2/周 截卓越', {'conv': (2, PROPOSAL['conv_qcap'])}),
        ('随机成色锻造 核心件 8 印记（否决）', {'k': 8}),
        ('随机成色锻造 8 印记 · 每周币 ≤2000', {'k': 8, 'coin': 2000}),
        ('随机成色锻造 12 印记', {'k': 12}),
        ('随机成色锻造 16 印记', {'k': 16}),
        ('随机成色锻造 4 印记', {'k': 4}),
    ]
    for route, label in (('rot', '轮换 + 团本'), ('abyss', 'P2-2 深渊')):
        print('### %s\n' % label)
        cal_w = cross(D144_TWO[route], 30.0)
        print('| 方案 | 第 4 / 8 / 12 周双极品 | W30 | ΔW30 | P50 | ΔP50 | 印记 / 人·周 | 锻造 / 人·周 | 锻造币 / 人·周 | 锻造胚料 / 人·周 | 转化 / 人·周 |')
        print('|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|')
        base = None
        for name, v in variants:
            share, tot = run_cells(route, v, n, weeks)
            wk, p50 = cross(share, 30.0), cross(share, 50.0)
            if base is None:
                base = (wk, p50)
            pw = float(n * weeks)
            f = lambda x: '%.2f' % x if x else '>%d' % weeks
            d = lambda x, b: ('%+.2f' % (x - b)) if x and b else '—'
            print('| %s | %.0f%% / %.0f%% / %.0f%% | %s | %s | %s | %s | %.1f | %.2f | %.0f | %.1f | %.2f |' % (
                name, share[3], share[7], share[weeks - 1], f(wk), d(wk, base[0]), f(p50), d(p50, base[1]),
                tot['marks'] / pw, tot['forge'] / pw, tot['coin'] / pw, tot['blank'] / pw, tot['conv'] / pw))
        print('\n对照 D144 p2econ（on-0.5）同一路线：第 4 / 8 / 12 周 %d%% / %d%% / %d%%，W30 %.2f。\n' % (
            D144_TWO[route][3], D144_TWO[route][7], D144_TWO[route][11], cal_w))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('mode', choices=['odds', 'affix', 'target', 'w30', 'fit', 'all'])
    ap.add_argument('--n', type=int, default=0)
    ap.add_argument('--weeks', type=int, default=12)
    a = ap.parse_args()
    print('# forgesim (D167) — %s · forge numbers: %s' % (rules.stamp(), SOURCE))
    if a.mode in ('odds', 'all'):
        odds()
    if a.mode in ('affix', 'all'):
        affix(a.n or 40000)
    if a.mode in ('target', 'all'):
        target(a.n or 40000)
    if a.mode == 'fit':
        fit(a.n or 3000, a.weeks)
    if a.mode in ('w30', 'all'):
        w30(a.n or 20000, a.weeks)


if __name__ == '__main__':
    main()

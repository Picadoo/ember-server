"""P2 economy check (docs/design/design-ember-v1.1-P2-draft.md §4): what the weekly challenge rotation (P2-1) adds after Q07.

Phase 1 = p1sim as-is (balance_version 2, stepdown route) until the player's Q07 first clear.
Phase 2 = W weeks of challenge runs only (3/day, the §18.1 T3 overrides from ember-v1-runs.yml `challenge:`), comparing
  base  : no rotation (player farms whichever challenge map it clears best),
  rot   : P2-1 — the week's featured map pays +1 T3 forge mark on each of the first 3 clears per week (no coin, no item).
Reports, per week checkpoint: share of players with a T3 two-piece of the target family, median enhance, coin balance,
T3 marks earned and the mark share of total T3 items. Standard library only; reads the real config through p1config.
"""
import argparse, copy, math, os, random, statistics, sys
sys.path.insert(0, os.path.dirname(__file__))
import p1config, p1sim, miniyaml, rules

ROOT = os.path.join(os.path.dirname(__file__), '..', '..')
# P2-1 parameter source (D66): ember-v1-runs.yml `rotation:`
_ROT = rules.runs().get('rotation', {'bonus_marks': 0, 'weekly_cap': 0})
ROT = {k: int(v) for k, v in _ROT.items() if k in ('bonus_marks', 'weekly_cap')}
# P2-8 (D80) weekly rules on the featured map's challenge runs (potion cap / role remap / room 1↔3 swap)
MODS = list(_ROT.get('modifiers') or [])


def mod_cfg(ccfg, key, mod):
    """P2-8: challenge config with the week's rule applied to map `key`; returns (cfg, potion cap or None)."""
    if not mod:
        return ccfg, None
    c = copy.deepcopy(ccfg)
    m = c['maps'][key]
    remap = p1sim.convert_roles(m, mod)  # server: only roles the map defines; D158 converted twists (bv29)
    if remap:
        for room in m['rooms'].values():
            for var in ('a', 'b'):
                comp = {}
                for role, n in room[var].items():
                    r = remap.get(role, role)
                    comp[r] = comp.get(r, 0) + n
                room[var] = comp
    if mod.get('swap_rooms'):
        r1, r3 = m['rooms']['r1'], m['rooms']['r3']
        r1['a'], r1['b'], r3['a'], r3['b'] = r3['a'], r3['b'], r1['a'], r1['b']
    cap = int(mod['potion_cap']) if mod.get('potion_cap') else None
    return c, cap


def challenge_cfg(cfg):
    ch = rules.runs()['challenge']
    c = copy.deepcopy(cfg)
    c['quality_w'] = ch['quality']
    for key, m in c['maps'].items():
        m['tier'] = ch['tier']
        for role, mob in m['mobs'].items():
            if role in ch['mobs']:
                mob['hp'], mob['atk'] = ch['mobs'][role]['hp'], ch['mobs'][role]['atk']
        b = m['boss']
        b['hp'], b['atk'] = ch['boss']['hp'], ch['boss']['atk']
        for s in b.get('skills', []):
            s['dmg'] = ch['boss']['light'] if s.get('light') else ch['boss']['heavy']
            if s.get('follow'):
                s['follow']['dmg'] = ch['boss']['light'] if s['follow'].get('light') else ch['boss']['heavy']
        if b.get('adds'):
            b['adds']['role'] = 'melee'
    return c


ABYSS = rules.runs().get('abyss', {}).get('tiers', [])
# F-review #5 (D124): surplus T3 marks (above the 8 kept back) may pay the abyss fee at this many coins per mark (0 = off)
FEE_MARK = int(rules.runs().get('abyss', {}).get('fee_mark_coin', 0) or 0)
MARK_RESERVE = 8
# Endgame #6 (D128): the first failed challenge / abyss run of the day gives back this share of its stamina (0 = off)
FAIL_REFUND = float(rules.runs().get('fail_refund', 0) or 0)

# §5.3 craft (精工) / quality (成色) coin sinks — EmberUpgradeRules.refineCost / qualityCost (coin only; mats assumed)
# craft index 0/1/2/3 = 0/2/4/6%; quality 0→1 / 1→2 only (q≥2 = 极品, drop-only). Two worn T3 pieces full ≈ 9k.
CRAFT_COIN = {0: 300, 1: 600, 2: 1200}
QUALITY_COIN = {0: 800, 1: 1600}
FORGE_SINK = True  # default ON; --no-forge-sink restores pre-sink coin piles
FORGE_RESERVE = 1000  # leave room for enhance / abyss fees (same ballpark as phase2_abyss reserve)


def forge_sink(p, reserve=None):
    """Phase-2 weekly coin sink: greedily raise worn T3 craft toward 3 and quality toward 2.
    Prefers higher growth-gain, then the weaker piece. Materials assumed available (coin-only model).
    Returns coin spent this call; accumulates on p.forge_spent."""
    if not FORGE_SINK:
        return 0
    if reserve is None:
        reserve = FORGE_RESERVE
    cfg = p.cfg
    spent = 0
    while True:
        cands = []
        for it in (p.blade, p.charm):
            if it['tier'] < 3 or it['fam'] == 'none':
                continue
            base = cfg['q'][it['q']] + cfg['f'][it['f']]
            if it['f'] < 3:
                cost = CRAFT_COIN[it['f']]
                gain = cfg['f'][it['f'] + 1] - cfg['f'][it['f']]
                cands.append((gain, base, cost, 'f', it))
            if it['q'] < 2:
                cost = QUALITY_COIN[it['q']]
                gain = cfg['q'][it['q'] + 1] - cfg['q'][it['q']]
                cands.append((gain, base, cost, 'q', it))
        if not cands:
            break
        # higher value gain first, then weaker piece (lower current q+f); skip steps that break the reserve
        cands.sort(key=lambda x: (-x[0], x[1], x[2]))
        pick = next((c for c in cands if p.coin - c[2] >= reserve), None)
        if pick is None:
            break
        gain, base, cost, kind, it = pick
        p.coin -= cost
        spent += cost
        if kind == 'f':
            it['f'] += 1
        else:
            it['q'] += 1
    p.forge_spent = getattr(p, 'forge_spent', 0) + spent
    return spent


def forge_next(p):
    """cheapest craft/quality step still open on a worn T3 target-family piece (0 = nothing left / sinks off)"""
    if not FORGE_SINK:
        return 0
    c = []
    for it in (p.blade, p.charm):
        if it['tier'] < 3 or it['fam'] == 'none':
            continue
        if it['f'] < 3:
            c.append(CRAFT_COIN[it['f']])
        if it['q'] < 2:
            c.append(QUALITY_COIN[it['q']])
    return min(c) if c else 0


def abyss_clear_table(cfg, dodges=(0.3, 0.5, 0.7), n_runs=40, seed=42):
    """P2-2 clear-rate by abyss tier for representative late-game gear states (chrate / bossmoves spirit).
    Returns list of markdown lines."""
    ccfg = challenge_cfg(cfg)
    # representative worn sets (burst family); craft field is 'f', quality is 'q'
    def piece(slot, tier, enh, q, f):
        return dict(p1sim.item('burst', slot, tier, q, f), enh=enh)
    states = [
        ('刚首通Q07', piece('blade', 3, 0, 0, 0), piece('charm', 2, 8, 0, 0), 28),
        ('中期T3', piece('blade', 3, 8, 1, 1), piece('charm', 3, 8, 1, 1), 30),
        ('两件极品', piece('blade', 3, 10, 3, 3), piece('charm', 3, 10, 3, 3), 30),
    ]
    lines = []
    lines.append('# P2-2 abyss clear-rate by tier (ember-v1-runs.yml abyss.tiers)')
    lines.append('')
    lines.append('Each cell = mean clear rate over the 7 maps × %d runs (seed %d). Gear is fixed burst two-piece.'
                 % (n_runs, seed))
    lines.append('')
    hdr = '| 装备 | 躲技能 | ' + ' | '.join('第%d层' % t for t in range(1, len(ABYSS) + 1)) + ' |'
    sep = '|---|---:|' + '|'.join(['---:'] * len(ABYSS)) + '|'
    lines.append(hdr)
    lines.append(sep)
    rng = random.Random(seed)
    for name, bl, ch, lv in states:
        st = p1sim.stats(cfg, bl, ch, lv)
        for d in dodges:
            kn = p1sim.Knobs(d)
            cells = []
            for t in range(1, len(ABYSS) + 1):
                ac = abyss_cfg(ccfg, t)
                rate = abyss_rate(ac, st, kn, rng, n=n_runs)
                cells.append('%d%%' % round(100 * rate))
            lines.append('| %s | %.1f | %s |' % (name, d, ' | '.join(cells)))
    lines.append('')
    lines.append('Tier factors (hp × dmg, fee): ' + ', '.join(
        'L%d ×%.2f/%.2f fee %d' % (i + 1, row['hp'], row['dmg'], row['fee']) for i, row in enumerate(ABYSS)))
    return lines



def fail_refund(p, w, d):
    """D128: call after a failed challenge / abyss run on day d (0..6) of week w. Banks FAIL_REFUND of one run's stamina
    once per day; returns 1 when the bank holds a whole run (spent as an extra run right away), else 0."""
    if FAIL_REFUND <= 0:
        return 0
    used = p.__dict__.setdefault('_fr_days', set())
    if (w, d) in used:
        return 0
    used.add((w, d))
    p._fr_bank = getattr(p, '_fr_bank', 0.0) + FAIL_REFUND
    if p._fr_bank >= 1 - 1e-9:
        p._fr_bank -= 1
        return 1
    return 0


def honor_fee(p, fee):
    """D142 深渊行者: the player's abyss fee (growth off = the table fee)"""
    if p1sim.GROWTH is None:
        return fee
    return int(round(fee * p1sim.gm(p.st(), 'abyss_fee')))


def fee_ok(p, fee, reserve):
    """the fee can be paid: coins above the reserve, else surplus T3 marks (D124)"""
    fee = honor_fee(p, fee)
    if fee + reserve <= p.coin:
        return True
    return FEE_MARK > 0 and p.marks[3] - MARK_RESERVE >= -(-fee // FEE_MARK)


def pay_fee(p, fee, reserve):
    fee = honor_fee(p, fee)
    if fee + reserve <= p.coin or FEE_MARK <= 0:
        p.coin -= fee
    else:
        p.marks[3] -= -(-fee // FEE_MARK)


def abyss_cfg(ccfg, t):
    """P2-2: challenge values × tier factors, tier quality table (tiers are 1-based)."""
    row = ABYSS[t - 1]
    c = copy.deepcopy(ccfg)
    c['quality_w'] = row['quality']
    c['abyss'] = t  # D142 深渊老手: abyss_taken only applies in abyss fights
    for m in c['maps'].values():
        for mob in m['mobs'].values():
            mob['hp'] *= row['hp']; mob['atk'] *= row['dmg']
        b = m['boss']
        b['hp'] *= row['hp']; b['atk'] *= row['dmg']
        for sk in b.get('skills', []):
            sk['dmg'] *= row['dmg']
            if sk.get('follow'):
                sk['follow']['dmg'] *= row['dmg']
    return c


def abyss_rate(acfg, st, kn, rng, n=3):
    order = acfg['order']
    return statistics.mean(p1sim.clear_rate(acfg, k, st, kn, n, seed=rng.randrange(1 << 30)) for k in order)


def raid_once(cfg, ccfg, kn, p, rng, w, want=1):
    """D116 weekly goal: raid entries until `want` clears this week (one per day, 50 stamina each); returns stamina used."""
    cost, clears, used = int(next(iter(RAIDS.values()))['cost']), 0, 0
    for ti in range(RAID_TRIES):
        if clears >= want:
            break
        used += cost
        p.day = 1000 + w * 7 + ti
        if rng.random() < RAID_RATE[min(w + 1, 4)]:
            clears += 1
            p.bounty_weight = 2
            settle_with(p, ccfg, ccfg['order'][-1], 'none')
            p.consider(p1sim.item(kn.target, 'blade' if rng.random() < 0.5 else 'charm', 3,
                                  max(RAID_FLOOR, p1sim.pick(ccfg['quality_w'], rng.random())),
                                  p1sim.pick(ccfg['craft_w'], rng.random())))
            p.bounty_weight = 1
            p.marks[3] += 1
            p.invest()
    return used


RUSH = None  # D144 余烬连战 (rushsim.rush_conf()); None = off


RUSH_TRIES = 1  # D160: attempts per week (the reward is still claimed once); 1 = the D144 one-entry rule


def rush_week(cfg, kn, p, rng):
    """D144: the week's free rush (solo, the player's own gear); a clear pays the rush T3 marks.
    D160 (--rush-tries N): up to N free attempts per week until the first clear; the reward is paid ONCE per week
    (practice clears after it pay nothing, so they are not simulated)."""
    if not RUSH:
        return 0
    import rushsim
    for _ in range(max(1, RUSH_TRIES)):
        ok, _, used, _ = rushsim.run_rush(cfg, RUSH, p.st(), kn, rng, p.potions)
        p.potions = max(0, p.potions - used)
        RUSH_STATS['tries'] += 1
        if ok:
            p.marks[3] += RUSH['marks']
            RUSH_STATS['paid'] += 1
            p.invest()
            return 1
    RUSH_STATS['unpaid_weeks'] += 1
    return 0


RUSH_STATS = {'tries': 0, 'paid': 0, 'unpaid_weeks': 0}
OUTPOST = int(os.environ.get('P1_OUTPOST', '0'))  # D174 stage 2b: T2 marks per week from 连战·前哨 before the Q07 first clear (0 = off)


GOALS = {'featured': 1, 'abyss': 3, 'raid': 1}  # D116 weekly goals that change what a player does (rewards: cosmetic only)


ABYSS_FORGE_FIRST = False  # 2026-10-04 abyss-coin check: forge (craft/quality) each morning before any tier fee


def phase2_abyss(cfg, ccfg, kn, p, rng, weeks, per_day, reserve=1000, goals=False):
    """P2-2 policy: like phase2 until challenge is viable; then every run is an abyss segment at the highest open tier
    (best cleared + 1) whose estimated clear rate is >= 50 % and whose fee leaves `reserve` coins; tier 1 otherwise.
    The fee is paid per started segment (a failed segment keeps it, like stamina).
    ABYSS_FORGE_FIRST (--abyss-forge-first): the player buys open craft/quality steps every morning and keeps the
    cheapest open step on top of `reserve` when paying tier fees (saves for the forge instead of the next fee)."""
    base_reserve = reserve
    order = ccfg['order']
    out, marks_earned, ch_runs, best, fees, tiers_played, blocked, top_short = [], 0, 0, 0, 0, [], 0, 0
    acfgs = {t: abyss_cfg(ccfg, t) for t in range(1, len(ABYSS) + 1)}
    for w in range(weeks):
        rush_week(cfg, kn, p, random.Random(w * 7919 + 17))  # D144: own stream, so --rush keeps the run stream
        rates = {k: p1sim.clear_rate(ccfg, k, p.st(), kn, 10, seed=rng.randrange(1 << 30)) for k in order}
        can_ch = max(rates.values()) >= 0.5
        tier = 1
        skip = 0
        if goals and can_ch:  # D116: the week's raid and one featured challenge clear come out of the same stamina
            featured = order[(w + 3) % len(order)]
            skip = (raid_once(cfg, ccfg, kn, p, rng, w, GOALS['raid']) + cfg['run_cost'] - 1) // cfg['run_cost']
            for ti in range(3):  # up to three tries at the featured challenge
                skip += 1
                p.buy_potions()
                ok, used, extra, *_ = p1sim.run_map(ccfg, featured, p.st(), kn, rng, p.potions)
                p.potions -= used
                ch_runs += 1
                if not ok:
                    skip -= fail_refund(p, w, ti)  # D128: a refunded run frees a slot later in the week
                if ok:
                    p.marks[3] += ROT['bonus_marks']
                    settle_with(p, ccfg, featured, extra)
                    marks_earned += 1
                    p.invest()
                    break
        for d in range(7):
            if ABYSS_FORGE_FIRST and can_ch:
                p.day = 1000 + w * 7 + d
                forge_sink(p, base_reserve)
                reserve = base_reserve + forge_next(p)
            if can_ch:  # re-pick the tier every day (players push up as soon as they clear)
                # D104: tier 0 = the plain challenge (still open next to the abyss); a player only steps into tier 1
                # once its estimated clear rate is >= 50 % (before D104 tier 1 equalled the challenge).
                tier = 0
                for t in range(min(best + 1, len(ABYSS)), 0, -1):
                    if not fee_ok(p, ABYSS[t - 1]['fee'], reserve):
                        # coin-blocked: the player would have stepped into this tier but the fee + reserve is short
                        if t == min(best + 1, len(ABYSS)) and abyss_rate(acfgs[t], p.st(), kn, rng) >= 0.5:
                            if best < len(ABYSS):
                                blocked += 1
                            else:  # recheck #5: already at the top tier, only that day's fee is short (not a missed climb)
                                top_short += 1
                        continue
                    if abyss_rate(acfgs[t], p.st(), kn, rng) >= 0.5:
                        tier = t
                        break
            n_today, ri = per_day, 0
            while ri < n_today:
                ri += 1
                if skip > 0:
                    skip -= 1
                    continue
                p.day = 1000 + w * 7 + d
                if can_ch:
                    # recheck #5: later runs of the day step down until the fee fits (before this the step-down was one
                    # tier and the fee was paid anyway, overdrawing coins or T3 marks); nothing fits -> plain challenge
                    while tier > 0 and not fee_ok(p, ABYSS[tier - 1]['fee'], reserve):
                        tier -= 1
                if can_ch and tier == 0:
                    use, key = ccfg, farm_map(p, kn, ccfg, rates, max(order, key=lambda k: rates[k]))
                elif can_ch:
                    use, key = acfgs[tier], order[rng.randrange(len(order))]
                    pay_fee(p, ABYSS[tier - 1]['fee'], reserve); fees += ABYSS[tier - 1]['fee']
                else:
                    use, key = cfg, order[-1]
                p.buy_potions()
                rep = use is cfg and key in p.cleared  # D138 repeat-run variety on normal runs
                ok, used, extra, *_ = p1sim.run_map(use, key, p.st(), kn, rng, p.potions, repeat=rep)
                p.potions -= used
                ch_runs += use is not cfg
                if not ok and use is not cfg:
                    n_today += fail_refund(p, w, d)  # D128
                if ok:
                    p.settle(key, extra, dict(p1sim.LAST_VAR) if rep else None) if use is cfg else settle_with(p, use, key, extra)
                    marks_earned += 1
                    if use is not cfg and use is not ccfg:
                        best = max(best, tier)
                    p.invest()
        tiers_played.append(tier if can_ch else 0)
        reserve = base_reserve
        forge_sink(p, reserve)  # §5.3 craft/quality coin sink (once per week; --no-forge-sink to disable)
        tgt = kn.target
        done = p.blade['tier'] == 3 and p.charm['tier'] == 3 and p.blade['fam'] == tgt and p.charm['fam'] == tgt
        out.append({'week': w + 1, 'set': done, 'enh': (p.blade['enh'] + p.charm['enh']) / 2, 'coin': p.coin,
                    'q': max(p.blade['q'], p.charm['q']), 'qmin': min(p.blade['q'], p.charm['q']), 'marks': marks_earned,
                    'B': p.st()['B'], 'ch': ch_runs, 'best': best, 'fees': fees, 'tier': tiers_played[-1],
                    'blocked': blocked, 'top_short': top_short, 'days': (w + 1) * 7,
                    'forge': getattr(p, 'forge_spent', 0),
                    'craft': (p.blade['f'] + p.charm['f']) / 2})
    return out


def set_abyss(tiers=None, fees=None, quality=None):
    """E-review tuning: override the abyss tier factors ('hp,dmg;hp,dmg;…') and/or fees ('0,60,…') in memory."""
    global ABYSS
    ABYSS = copy.deepcopy(ABYSS)
    if tiers:
        rows = [tuple(float(x) for x in r.split(',')) for r in tiers.split(';')]
        assert len(rows) == len(ABYSS)
        for row, (h, d) in zip(ABYSS, rows):
            row['hp'], row['dmg'] = h, d
    if fees:
        fs = [int(x) for x in fees.split(',')]
        assert len(fs) == len(ABYSS)
        for row, f in zip(ABYSS, fs):
            row['fee'] = f
    if quality:
        qs = [[int(x) for x in r.split(',')] for r in quality.split(';')]
        assert len(qs) == len(ABYSS) and all(sum(q) == 100 for q in qs)
        for row, q in zip(ABYSS, qs):
            row['quality'] = q


def to_q07(cfg, kn, seed):
    """p1sim.simulate_player, but returns the Player and the run count at the Q07 first clear (None if never)."""
    rng = random.Random(seed)
    p = p1sim.Player(cfg, kn, rng)
    order, per_day = cfg['order'], cfg['stamina_day'] // cfg['run_cost']
    cur, runs, refund_day = 0, 0, -1
    offset = random.Random(seed * 7919 + 13).randrange(7 * len(order) * max(1, len(p1sim.MODS)))  # D94 calendar position
    modded = {}
    while runs < 60 * per_day:
        front = next((i for i, k in enumerate(order) if k not in p.cleared), None)
        if front is None:
            # D108 shared cap: featured bonuses already taken in the week of the Q07 first clear
            p.feat_carry = p.feat_n if getattr(p, 'feat_wk', None) == p1sim.feat_week(order, p.day, offset)[0] else 0
            return p, runs, rng
        cur = min(cur, front)
        key = order[cur]
        p.day = runs // per_day + 1
        if kn.feat_farm:  # D108 featured detour (see p1sim.simulate_player)
            fk = p1sim.feat_week(order, p.day, offset)[1]
            if p1sim.feat_left(p, kn, cfg, order, p.day, offset, fk) > 0:
                key = fk
        detour = key != order[cur]
        p.buy_potions()
        mod = p1sim.week_rule(kn, order, p.day, offset, key, p.cleared)  # D94 (None unless --normal-mods)
        rep = key in p.cleared  # D138
        if mod is None:
            ok, used, extra, *_ = p1sim.run_map(cfg, key, p.st(), kn, rng, p.potions, repeat=rep)
        else:
            mc, cap = modded.setdefault((key, mod['id']), p1sim.mod_cfg(cfg, key, mod))
            ok, used, extra, *_ = p1sim.run_map(mc, key, p.st(), kn, rng, p.potions if cap is None else min(cap, p.potions), repeat=rep)
        p.potions -= used
        runs += 1
        if OUTPOST and 'q05' in p.cleared and runs % (7 * per_day) == 0:  # D174 stage 2b 连战·前哨 upper bound: cleared every week
            p.marks[2] += OUTPOST
            p.invest()
        if ok:
            p1sim.feat_pay(p, kn, cfg, order, p.day, offset, key)  # D108
            p.settle(key, extra, dict(p1sim.LAST_VAR) if rep else None)
            p.invest()
            if not detour:
                cur = front + 1 if cur == front else front
        else:
            day = runs // per_day + 1
            if cfg['death_refund'] > 0 and refund_day != day:
                refund_day = day
                p.potions += min(used, cfg['death_refund'])
            if not detour:
                cur = max(0, cur - 1)
    return None, runs, rng


TRADE = {'price': 800, 'fee': 0.10, 'listings': 10}  # P2-3 model knobs (not live parameters; the market is closed)


def buy_listing(p, ccfg, rng):
    """P2-3: once a week buy the best of `listings` unused T3 target-family drops for the weaker slot (sellers list what
    they do not wear; quality per the challenge table, craft per the normal table)."""
    cost = int(TRADE['price'] * (1 + TRADE['fee']))
    if p.coin < cost + 1000:
        return False
    slot = p.weaker_slot(3)
    best = max((p1sim.item(p.kn.target, slot, 3, p1sim.pick(ccfg['quality_w'], rng.random()), p1sim.pick(ccfg['craft_w'], rng.random()))
                for _ in range(TRADE['listings'])), key=lambda it: (it['q'], it['f']))
    p.coin -= cost
    p.consider(best)
    return True


RUNS = rules.runs()
RAIDS = RUNS.get('raids', {})
# P2-5/P2-6: party clear rate by week after Q07 (tools/p1party.py, 4-player median of r01 / r02 at boss HP 13000:
# week 2 ≈ 0.40, week 4 ≈ 0.76); weeks in between interpolated, capped at week 4's value.
RAID_RATE = {1: 0.30, 2: 0.40, 3: 0.58, 4: 0.76}
# D106 (balance_version 14): p1party.py after D104, 4-player median of r01 / r02 (weeks 2 and 4 measured, 1 and 3
# scaled / interpolated). 'b13' = no revive (the old rule), 'revive' = fallen members stand up at the next room / boss
# appearance / boss 50 % (live from CoreRpg 1.49.0). out-p1party-d106-*.md
RAID_RATES = {'b12': {1: 0.30, 2: 0.40, 3: 0.58, 4: 0.76},
              'b13': {1: 0.34, 2: 0.45, 3: 0.63, 4: 0.81},
              'revive': {1: 0.44, 2: 0.59, 3: 0.74, 4: 0.88}}
RAID_RATE = RAID_RATES['revive']
RAID_TRIES = 5  # a party gives up for the week after 5 attempts


def raid_cap():
    """Weekly settled raid clears per character: raids sharing a cap_group share one counter (P2-6)."""
    groups = {}
    for k, r in RAIDS.items():
        g = r.get('cap_group') or k
        groups[g] = max(groups.get(g, 0), int(r.get('weekly_cap', 0)))
    return sum(groups.values())


def phase2(cfg, ccfg, kn, p, rng, weeks, rotation, per_day, start_run, trade=False, raid=False, mods=False, stats=None, goals=False):
    """Each week: if the player clears some challenge map >= 50 % of the time it farms challenge (featured first when
    rotating), otherwise it farms Q07 normal (T3). As on the server, the bonus only pays on challenge clears."""
    order = ccfg['order']
    out, marks_earned, ch_runs = [], 0, 0
    for w in range(weeks):
        rush_week(cfg, kn, p, random.Random(w * 7919 + 17))  # D144: own stream, so --rush keeps the run stream
        featured = order[(w + 3) % len(order)]  # server: ISO week mod 7 (offset arbitrary here)
        rates = {k: p1sim.clear_rate(ccfg, k, p.st(), kn, 10, seed=rng.randrange(1 << 30)) for k in order}
        best = max(rates, key=rates.get)
        can_ch = rates[best] >= 0.5
        bonus_left = ROT['weekly_cap'] - (getattr(p, 'feat_carry', 0) if w == 0 else 0)  # D108 shared weekly cap
        mi = (w if math.gcd(len(MODS) or 1, len(order)) == 1 else w + 3)  # D187: skewed index shares the featured week number
        mi = p1sim.mod_index(mi, len(MODS), len(order)) if MODS else 0
        mod = MODS[mi] if mods and MODS else None
        fcfg, fcap = mod_cfg(ccfg, featured, mod)
        wmod = MODS[mi] if MODS else None  # D94: the same week's rule on repeat NORMAL Q07 runs when featured
        ncfg, ncap = (p1sim.mod_cfg(cfg, order[-1], wmod) if kn.normal_mods and wmod and featured == order[-1]
                      and (kn.normal_mods == 'all' or p1sim._truthy(wmod.get('normal'))) else (None, None))
        if mod:  # featured rate under this week's rule (own seed: the main rng stream stays paired with 'rot')
            kn2 = copy.copy(kn)
            if fcap is not None:
                kn2.potion_keep = min(kn.potion_keep, fcap)
            sd = 7919 * (w + 1) + int(100 * p.st()['B'])
            rates[featured] = p1sim.clear_rate(fcfg, featured, p.st(), kn2, 10, seed=sd)
            if stats is not None and can_ch:  # paired check: same seed, 40 entries, with vs without the rule
                st_ = stats.setdefault(mod['id'], [0, 0.0, 0.0])
                st_[0] += 1
                st_[1] += p1sim.clear_rate(ccfg, featured, p.st(), kn, 40, seed=sd + 1)
                st_[2] += p1sim.clear_rate(fcfg, featured, p.st(), kn2, 40, seed=sd + 1)
        if trade:
            buy_listing(p, ccfg, rng)
        runs_left = 7 * per_day
        p.day = 1000 + w * 7  # raids happen on the week's first day
        if raid and can_ch:  # raids only once the player can farm challenge (same gear bar as the p1party pool)
            stamina, clears, cost = 7 * per_day * cfg['run_cost'], 0, int(next(iter(RAIDS.values()))['cost'])
            for ti in range(RAID_TRIES):
                if clears >= raid_cap() or stamina < cost:
                    break
                stamina -= cost
                p.day = 1000 + w * 7 + ti  # D105: one raid per day (the bounty counts the raid as 2 clears)
                if rng.random() < RAID_RATE[min(w + 1, 4)]:
                    clears += 1
                    p.bounty_weight = 2
                    if OLD_RAID_ITEM:
                        settle_with(p, ccfg, order[-1], 'chest')  # pre-P2-9: challenge settlement + one more plain T3 roll
                    else:  # P2-9 (D82): raid_item = the player's target family, quality floor 精良
                        settle_with(p, ccfg, order[-1], 'none')
                        p.consider(p1sim.item(kn.target, 'blade' if rng.random() < 0.5 else 'charm', 3,
                                              max(RAID_FLOOR, p1sim.pick(ccfg['quality_w'], rng.random())),
                                              p1sim.pick(ccfg['craft_w'], rng.random())))
                    p.bounty_weight = 1
                    p.marks[3] += 1; marks_earned += 2       # raid_mark + the base mark
                    p.invest()
            runs_left = stamina // cfg['run_cost']
        if goals and can_ch:  # D116: three abyss tiers a week (highest open tier the player clears >= 50 %, else tier 1)
            abest = getattr(p, 'abyss_best', 0)
            acfgs = getattr(p, '_acfgs', None) or {t: abyss_cfg(ccfg, t) for t in range(1, len(ABYSS) + 1)}
            p._acfgs = acfgs
            for gi in range(GOALS['abyss']):
                tier = 1
                for t in range(min(abest + 1, len(ABYSS)), 0, -1):
                    if ABYSS[t - 1]['fee'] + 1000 <= p.coin and abyss_rate(acfgs[t], p.st(), kn, rng) >= 0.5:
                        tier = t
                        break
                runs_left -= 1
                p.coin -= ABYSS[tier - 1]['fee']
                p.buy_potions()
                akey = order[rng.randrange(len(order))]
                ok, used, extra, *_ = p1sim.run_map(acfgs[tier], akey, p.st(), kn, rng, p.potions)
                p.potions -= used
                ch_runs += 1
                if not ok:
                    runs_left += fail_refund(p, w, gi)  # D128
                if ok:
                    settle_with(p, acfgs[tier], akey, extra)
                    abest = max(abest, tier)
                    marks_earned += 1
                    p.invest()
            p.abyss_best = abest
        planned, i = max(1, runs_left), -1
        while i + 1 < runs_left:
            i += 1
            p.day = 1000 + w * 7 + min(6, i * 7 // planned)
            if can_ch:
                use, key = ccfg, (featured if rotation and bonus_left > 0 and rates[featured] >= 0.3 else farm_map(p, kn, ccfg, rates, best))
            else:
                use, key = cfg, order[-1]
            p.buy_potions()
            if mod and use is ccfg and key == featured:
                ok, used, extra, *_ = p1sim.run_map(fcfg, key, p.st(), kn, rng, min(p.potions, fcap) if fcap is not None else p.potions)
            elif ncfg is not None and use is cfg:
                ok, used, extra, *_ = p1sim.run_map(ncfg, key, p.st(), kn, rng, min(p.potions, ncap) if ncap is not None else p.potions, repeat=True)
            else:
                ok, used, extra, *_ = p1sim.run_map(use, key, p.st(), kn, rng, p.potions, repeat=use is cfg)
            p.potions -= used
            ch_runs += use is ccfg
            if not ok and use is ccfg:
                runs_left += fail_refund(p, w, p.day - 1000 - w * 7)  # D128
            if ok:
                if rotation and use is ccfg and key == featured and bonus_left > 0:
                    bonus_left -= 1
                    p.marks[3] += ROT['bonus_marks']
                    marks_earned += ROT['bonus_marks']
                p.settle(key, extra, dict(p1sim.LAST_VAR)) if use is cfg else settle_with(p, ccfg, key, extra)
                marks_earned += 1
                p.invest()
        forge_sink(p, FORGE_RESERVE)  # §5.3 craft/quality coin sink (once per week; --no-forge-sink to disable)
        tgt = kn.target
        done = p.blade['tier'] == 3 and p.charm['tier'] == 3 and p.blade['fam'] == tgt and p.charm['fam'] == tgt
        out.append({'week': w + 1, 'set': done, 'enh': (p.blade['enh'] + p.charm['enh']) / 2, 'coin': p.coin,
                    'q': max(p.blade['q'], p.charm['q']), 'qmin': min(p.blade['q'], p.charm['q']), 'marks': marks_earned,
                    'B': p.st()['B'], 'ch': ch_runs, 'best': 0, 'fees': 0, 'tier': 0,
                    'forge': getattr(p, 'forge_spent', 0),
                    'craft': (p.blade['f'] + p.charm['f']) / 2})
    return out


OLD_RAID_ITEM = False
RAID_FLOOR = 1  # P2-9 (D82) raid_item quality floor (1 = 精良), from ember-v1-runs.yml raid_item.quality_floor


def farm_map(p, kn, ccfg, rates, best):
    """P2-9: with per-map loot identity the player farms the map of its target family and weaker slot when that map
    clears within 10 points of its best one (otherwise the best map, as before)."""
    if not ccfg.get('loot_bias'):
        return best
    bl, ch = p.blade, p.charm
    want = 'blade' if (bl['fam'] != kn.target, -bl['q'], bl['enh']) >= (ch['fam'] != kn.target, -ch['q'], ch['enh']) else 'charm'
    def score(k):
        lo = ccfg['maps'][k].get('loot') or {}
        return (lo.get('family') == kn.target) * 2 + (lo.get('slot') == want), rates[k]
    ok = [k for k in rates if rates[k] >= max(0.5, rates[best] - 0.10)]
    return max(ok, key=score) if ok else best


def settle_with(p, ccfg, key, extra):
    """Challenge settlement: same base, T3 items with the challenge quality table, no first clear (already cleared)."""
    saved = p.cfg
    p.cfg = ccfg
    try:
        p.settle(key, extra)
    finally:
        p.cfg = saved


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--players', type=int, default=60)
    ap.add_argument('--weeks', type=int, default=8)
    ap.add_argument('--dodge', type=float, default=0.5)
    ap.add_argument('--no-swap', action='store_true', help='phase 2 without the §6.3 enhance-track swap (old behaviour)')
    ap.add_argument('--no-forge-sink', action='store_true', help='disable §5.3 craft/quality coin sinks in phase 2 (default ON)')
    ap.add_argument('--trade', action='store_true', help='add the P2-3 market model (weekly best-of-10 purchase)')
    ap.add_argument('--abyss', action='store_true', help='add the P2-2 abyss policy as a third column')
    ap.add_argument('--raid', action='store_true', help='add the P2-5/6 raids (rotation + weekly raid clears, shared cap)')
    ap.add_argument('--loot-own', type=float, help='override loot_bias.own_family (tuning)')
    ap.add_argument('--loot-slot', type=float, help='override loot_bias.slot (tuning)')
    ap.add_argument('--no-loot', action='store_true', help='without the P2-9 per-map loot identity (D81)')
    ap.add_argument('--old-raid-item', action='store_true', help='pre-P2-9 raid_item (plain roll, no family target, no floor)')
    ap.add_argument('--mods', action='store_true', help='add the P2-8 weekly rules on the featured map (rotation + rule)')
    ap.add_argument('--no-bounty', action='store_true', help='without the P2-7 daily bounty (D79) for comparison')
    ap.add_argument('--normal-mods', nargs='?', const='normal', choices=['normal', 'all'], default=None,
                    help='D94: weekly rule on repeat normal runs of the featured map, in every mode (all = include casters)')
    ap.add_argument('--no-feat-normal', action='store_true', help='D108: without the featured mark on repeat normal runs')
    ap.add_argument('--feat-farm', action='store_true', help='D108: phase-1 players detour to the featured map for the bonus')
    ap.add_argument('--feat-separate', action='store_true', help='D108 variant: the normal bonus has its own weekly cap')
    ap.add_argument('--raid-rate', default='revive', choices=sorted(RAID_RATES), help='D106: raid clear rate table')
    ap.add_argument('--goals', action='store_true', help='D116: add weekly-goal columns (raid + goals, abyss + goals)')
    ap.add_argument('--every-week', action='store_true', help='print every week (default: 1, 2, 4, 6, 8, 10, 12)')
    ap.add_argument('--ch-hp', type=float, default=1.0, help='D104 tuning: multiply challenge mob/boss HP (abyss follows)')
    ap.add_argument('--ch-atk', type=float, default=1.0, help='D104 tuning: multiply challenge mob/boss damage (abyss follows)')
    ap.add_argument('--abyss-tiers', help="E-review tuning: tier factors 'hp,dmg;…' (10 rows) instead of the config")
    ap.add_argument('--abyss-quality', help="E-review tuning: tier quality tables '60,28,10,2;…' (10 rows)")
    ap.add_argument('--fee-mark', type=int, default=None, help='D124: coins per surplus T3 mark paying abyss fees (0 = off; default = yml abyss.fee_mark_coin)')
    ap.add_argument('--fail-refund', type=float, default=None, help='D128: share of the stamina given back for the first failed challenge / abyss run of the day (0 = off; default = yml fail_refund)')
    ap.add_argument('--abyss-fees', help="E-review tuning: tier fees '0,60,…' (10 values) instead of the config")
    ap.add_argument('--no-variety', action='store_true', help='D138: without the repeat-run variety (affixed elite + room event)')
    ap.add_argument('--no-vbounty', action='store_true', help='D144: without the 花样委托 daily variety bounty')
    ap.add_argument('--rush', action='store_true', help='D144: the weekly 余烬连战 (one free entry, T3 marks on a clear)')
    ap.add_argument('--rush-tries', type=int, default=1, help='D160: free 余烬连战 attempts per week until the first clear (reward still once a week)')
    ap.add_argument('--abyss-forge-first', action='store_true', help='abyss players buy craft/quality steps each morning before tier fees')
    a = ap.parse_args()
    print('# ' + __import__('rules').stamp(), flush=True)  # M06: which rule snapshot produced this report
    global ABYSS_FORGE_FIRST
    ABYSS_FORGE_FIRST = a.abyss_forge_first
    global FORGE_SINK
    if a.no_forge_sink:
        FORGE_SINK = False
    if a.no_variety:
        p1sim.VARIETY = False
    if a.no_vbounty:
        p1sim.VBOUNTY = False
    global RUSH, RUSH_TRIES
    RUSH_TRIES = a.rush_tries
    if a.rush:
        import rushsim
        RUSH = rushsim.rush_conf() or None
    if a.fee_mark is not None:
        global FEE_MARK
        FEE_MARK = a.fee_mark
    if a.fail_refund is not None:
        global FAIL_REFUND
        FAIL_REFUND = a.fail_refund
    if a.abyss_tiers or a.abyss_fees or a.abyss_quality:
        set_abyss(a.abyss_tiers, a.abyss_fees, a.abyss_quality)
    cfg = p1config.load()
    if a.no_bounty:
        cfg['bounty'] = []
    if a.no_loot:
        cfg['loot_bias'] = {}
    if a.loot_own is not None:
        cfg['loot_bias']['own_family'] = a.loot_own
    if a.loot_slot is not None:
        cfg['loot_bias']['slot'] = a.loot_slot
    global OLD_RAID_ITEM, RAID_FLOOR, RAID_RATE
    RAID_RATE = RAID_RATES[a.raid_rate]
    OLD_RAID_ITEM = a.old_raid_item
    RAID_FLOOR = int((RUNS.get('raid_item') or {}).get('quality_floor', 1))
    ccfg = challenge_cfg(cfg)
    if a.ch_hp != 1.0 or a.ch_atk != 1.0:
        import chrate
        ccfg = chrate.scaled(ccfg, a.ch_hp, a.ch_atk)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    modes = ('base', 'rot') + (('abyss',) if a.abyss else ()) + (('trade',) if a.trade else ()) + (('raid',) if a.raid else ()) + (('mods',) if a.mods else ()) + (('goals',) if a.goals and a.raid else ()) + (('abyssg',) if a.goals and a.abyss else ())
    res = {m: [] for m in modes}
    stats = {}
    for i in range(a.players):
        for mode in modes:
            kn = p1sim.Knobs(a.dodge, normal_mods=a.normal_mods, feat_normal='off' if a.no_feat_normal else 'config',
                             feat_farm=a.feat_farm)
            p, runs, rng = to_q07(cfg, kn, 5000 + i)   # same seed → same phase-1 player for both modes
            if p is not None and a.feat_separate:
                p.feat_carry = 0
            if p is None:
                continue
            kn.swap = not a.no_swap  # §6.3 free enhance-track swap: late-game players re-equip better-quality drops
            kn.quality_chase = FORGE_SINK  # with craft/quality sinks on, still take 极品 over forged 卓越
            if mode in ('abyss', 'abyssg'):
                res[mode].append(phase2_abyss(cfg, ccfg, kn, p, random.Random(9000 + i), a.weeks, per_day, goals=mode == 'abyssg'))
            else:
                res[mode].append(phase2(cfg, ccfg, kn, p, random.Random(9000 + i), a.weeks, mode in ('rot', 'raid', 'mods', 'goals'), per_day, runs,
                                        trade=mode == 'trade', raid=mode in ('raid', 'goals'), mods=mode == 'mods', goals=mode == 'goals',
                                        stats=stats if mode == 'mods' else None))
    print('# p2econ: %d players reached Q07 (dodge %.2f), %d challenge weeks after it, 3 runs/day%s' % (
        len(res['base']), a.dodge, a.weeks, '' if FORGE_SINK else ' · forge sinks OFF'))
    print('| 周 | 方案 | T3 目标族两件 | 强化均值（中位） | 最好成色≥卓越 | 两件都≥卓越 | 有极品 | 两件极品 | 余烬币（中位） | 累计精工/成色币（中位） | 精工均值（中位） | 累计 T3 印记（中位） | B（中位） | 挑战/深渊局占比 | 深渊最高层（中位） | 累计深渊费（中位） |')
    print('|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|')
    for w in (range(1, a.weeks + 1) if a.every_week else (1, 2, 4, 6, 8, 10, 12)):
        if w > a.weeks:
            continue
        for mode in modes:
            rows = [r[w - 1] for r in res[mode]]
            if not rows:
                continue
            n = len(rows)
            print('| %d | %s | %d%% | %.1f | %d%% | %d%% | %d%% | %d%% | %d | %d | %.1f | %d | %.1f | %d%% | %s | %s |' % (
                w, {'base': '无轮换', 'rot': 'P2-1 轮换', 'abyss': 'P2-2 深渊', 'trade': 'P2-3 交易', 'raid': '轮换 + 团本', 'mods': '轮换 + 周规则', 'goals': '团本 + 周目标', 'abyssg': '深渊 + 周目标'}[mode], round(100 * sum(r['set'] for r in rows) / n),
                statistics.median(r['enh'] for r in rows), round(100 * sum(r['q'] >= 2 for r in rows) / n),
                round(100 * sum(r['qmin'] >= 2 for r in rows) / n), round(100 * sum(r['q'] >= 3 for r in rows) / n),
                round(100 * sum(r['qmin'] >= 3 for r in rows) / n),
                statistics.median(r['coin'] for r in rows),
                statistics.median(r.get('forge', 0) for r in rows),
                statistics.median(r.get('craft', 0) for r in rows),
                statistics.median(r['marks'] for r in rows),
                statistics.median(r['B'] for r in rows), round(100 * statistics.mean(r['ch'] for r in rows) / (w * 7 * per_day)),
                ('%d' % statistics.median(r['best'] for r in rows)) if mode in ('abyss', 'abyssg') else '—',
                ('%d' % statistics.median(r['fees'] for r in rows)) if mode in ('abyss', 'abyssg') else '—'))

    if a.abyss and res['abyss']:
        rows = [r[-1] for r in res['abyss']]
        n = len(rows)
        print()
        print('深渊第 %d 周最高层分布：≥1 层 %d%% · ≥3 层 %d%% · ≥5 层 %d%% · ≥10 层 %d%% · 因币不够没进下一层的天数（均值）%.1f / %d 天 · 已到第 10 层但当天层费不够（均值）%.1f 天' % (
            a.weeks, round(100 * sum(r['best'] >= 1 for r in rows) / n), round(100 * sum(r['best'] >= 3 for r in rows) / n),
            round(100 * sum(r['best'] >= 5 for r in rows) / n), round(100 * sum(r['best'] >= 10 for r in rows) / n),
            statistics.mean(r['blocked'] for r in rows), rows[0]['days'], statistics.mean(r['top_short'] for r in rows)))
        print()
        for line in abyss_clear_table(cfg):
            print(line)

    if a.mods:
        print()
        print('P2-8 精选图挑战通关率（能刷挑战的玩家-周；同一种子 40 局配对：无规则 vs 本周规则）：')
        print('| 规则 | 玩家-周 | 无规则 | 有规则 | 差 |')
        print('|---|---:|---:|---:|---:|')
        for m in MODS:
            n, b, v = stats.get(m['id'], [0, 0.0, 0.0])
            print('| %s %s | %d | %d%% | %d%% | %+d 点 |' % (m['id'], m.get('name', ''), n, round(100 * b / max(1, n)),
                                                       round(100 * v / max(1, n)), round(100 * (v - b) / max(1, n))))
    if RUSH:
        rw = RUSH_STATS['paid'] + RUSH_STATS['unpaid_weeks']
        print()
        print('D160 余烬连战（每人每周最多 %d 次尝试，奖励每周最多 1 次）：玩家-周 %d · 尝试 %d · 领奖周 %d（%.1f%%）· T3 印记 %d（每周上限 %d）'
              % (RUSH_TRIES, rw, RUSH_STATS['tries'], RUSH_STATS['paid'], 100.0 * RUSH_STATS['paid'] / max(1, rw),
                 RUSH_STATS['paid'] * RUSH['marks'], RUSH['marks']))
        assert RUSH_STATS['paid'] <= rw, 'more rush rewards than player-weeks'

if __name__ == '__main__':
    main()

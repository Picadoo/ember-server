#!/usr/bin/env python3
"""P1 offline pacing simulator: a player going Q01 -> Q07 on the real config numbers (see README.md)."""
import argparse
import math
import random
import statistics
import sys

import p1config

FAMS = ('scorch', 'burst', 'sustain')
MELEE_ROLES = ('melee', 'heavy', 'elite')


class Knobs:
    """Player-skill / behaviour parameters (not balance numbers)."""
    def __init__(self, dodge=0.5, **kw):
        self.dodge = dodge                 # share of plain enemy hits avoided (spacing, backing off)
        self.tele_bonus = kw.get('tele_bonus', 0.25)  # telegraphed moves (boss skills, caster line) are easier
        self.swing = kw.get('swing', 0.64)  # seconds per full-charge swing (bot 640 ms; book cap 1.6/s)
        self.uptime = kw.get('uptime', 0.70)  # share of time actually swinging (rest: moving / dodging / walking)
        self.engage = kw.get('engage', 4)  # max melee bodies hitting at the same time
        self.skill_hits = kw.get('skill_hits', 3)  # 烬斩 / 烬爆 targets actually caught (cap 5)
        self.potion_at = kw.get('potion_at', 0.675)  # drink below this share of H
        self.potion_keep = kw.get('potion_keep', 5)  # potions carried into each run (bought with own coin)
        self.coin_reserve = kw.get('coin_reserve', 100)
        self.target = kw.get('target', 'burst')
        self.swap = kw.get('swap', False)   # §6.3 free enhance-track swap when equipping a better drop
        self.route = kw.get('route', 'stepdown')  # stepdown (v3 bot) | alternate (v2 bot)


# ---------------------------------------------------------------- stats

def level_of(cfg, xp):
    c = cfg['xp_curve']
    lv = 10
    need = c['base']
    while xp >= need and lv < 60:
        xp -= need
        lv += 1
        need = c['base'] + c['step'] * max(0, lv - c['step_from'])
    return lv


def awakening(blade, charm):
    if not blade or not charm or blade['fam'] != charm['fam'] or blade['fam'] == 'none':
        return 0
    if min(blade['tier'], charm['tier']) < 1:
        return 0
    if blade['tier'] >= 3 and charm['tier'] >= 3 and blade['enh'] >= 9 and charm['enh'] >= 9:
        return 3
    if blade['tier'] >= 2 and charm['tier'] >= 2 and blade['enh'] >= 6 and charm['enh'] >= 6:
        return 2
    return 1


def stats(cfg, blade, charm, level):
    steps = max(0, min(level, cfg['lvl_cap']) - cfg['lvl_base'])
    g = lambda it: 1 + cfg['e'][it['enh']] + cfg['q'][it['q']] + cfg['f'][it['f']]
    B = cfg['A'][blade['tier']] * g(blade) + cfg['lvl_atk'] * steps
    H = 20 + cfg['h'][charm['tier']] * g(charm) + cfg['lvl_hp'] * steps
    awk = awakening(blade, charm)
    fam = blade['fam'] if awk else None
    if fam == 'sustain':
        H *= cfg['sustain_hp']
    M = max(cfg['def_floor'], cfg['def_k'] / (cfg['def_k'] + cfg['D'][charm['tier']]))
    return {'B': B, 'H': H, 'M': M, 'set': fam, 'awk': awk}


def power(cfg, st, kn):
    """Single number for 'equip best': expected damage per second x EHP."""
    dps = st['B'] * (1 + cfg['crit_rate'] * (cfg['crit_mult'] - 1)) / kn.swing
    if st['set'] == 'burst':
        dps += cfg['burst'][st['awk']] * st['B'] * min(3, kn.skill_hits) / max(cfg['burst_every'] * kn.swing, cfg['burst_icd'])
    elif st['set'] == 'scorch':
        dps += cfg['burn'][st['awk']] * st['B']
    ehp = st['H'] / st['M']
    if st['set'] == 'sustain':
        ehp *= 1.15
    return dps * ehp


# ---------------------------------------------------------------- combat

def mob(cfg, m, role, rng, kn, t0=0.0):
    d = m['mobs'][role]
    iv = d.get('interval', 3.0)
    approach = 1.5 if role in MELEE_ROLES else 0.5
    return {'hp': float(d['hp']), 'atk': d['atk'], 'iv': iv, 'role': role,
            'next': t0 + approach + rng.uniform(0, iv), 'tele': role == 'caster', 'burn': 0.0}


class Fight:
    def __init__(self, cfg, st, kn, rng, potions):
        self.cfg, self.st, self.kn, self.rng = cfg, st, kn, rng
        self.hp = st['H']
        self.potions = potions
        self.used = 0
        self.t = 0.0
        self.pcd = 0.0
        self.hits = 0
        self.burst_cd = 0.0
        self.sus_cd = 0.0
        self.taken = 0.0

    def hurt(self, raw, tele):
        kn = self.kn
        p = min(0.95, kn.dodge + kn.tele_bonus) if tele else kn.dodge
        if self.rng.random() < p:
            return
        dmg = raw * self.st['M']
        self.hp -= dmg
        self.taken += dmg
        if self.hp > 0 and self.hp < kn.potion_at * self.st['H'] and self.potions > 0 and self.t >= self.pcd:
            self.potions -= 1
            self.used += 1
            self.pcd = self.t + self.cfg['potion_cd']
            self.hp = min(self.st['H'], self.hp + self.cfg['potion_pct'] * self.st['H'])

    def segment(self, mobs, boss=None, mapdef=None):
        """Fight until every mob (and the boss) is dead or the player dies. Returns True on clear."""
        cfg, kn, st, rng = self.cfg, self.kn, self.st, self.rng
        period = kn.swing / kn.uptime
        t = self.t
        next_swing = t + 1.0
        next_skill = t + 1.0
        skills = []
        adds_done = False
        if boss is not None:
            for s in mapdef['boss'].get('skills', []):
                skills.append({'s': s, 'next': t + s['every']})
        pending = []  # (time, raw dmg) of follow-up hits
        while True:
            alive = [m for m in mobs if m['hp'] > 0]
            if not alive:
                self.t = t
                return True
            # next event
            cand = [next_swing]
            melee_alive = [m for m in alive if m['role'] in MELEE_ROLES or m['role'] == 'boss']
            engaged = set(id(m) for m in melee_alive[:kn.engage])
            for m in alive:
                if m['atk'] > 0:
                    cand.append(m['next'])
            for s in skills:
                cand.append(s['next'])
            for p in pending:
                cand.append(p[0])
            tn = min(cand)
            # burn ticks between t and tn on burning targets
            if st['set'] == 'scorch':
                for m in alive:
                    if m['burn'] > t:
                        m['hp'] -= cfg['burn'][st['awk']] * st['B'] * (min(tn, m['burn']) - t)
            t = tn
            self.t = t
            if t == next_swing:
                next_swing = t + period
                tgt = alive[0]
                crit = rng.random() < cfg['crit_rate']
                tgt['hp'] -= st['B'] * (cfg['crit_mult'] if crit else 1.0)
                self.hits += 1
                if t >= next_skill:
                    for m in alive[:kn.skill_hits]:
                        m['hp'] -= cfg['skill_mult'] * st['B']
                    next_skill = t + cfg['skill_cd']
                if st['set'] == 'burst' and self.hits >= cfg['burst_every'] and t >= self.burst_cd:
                    for m in alive[:min(kn.skill_hits, 5)]:
                        m['hp'] -= cfg['burst'][st['awk']] * st['B']
                    self.hits = 0
                    self.burst_cd = t + cfg['burst_icd']
                elif st['set'] == 'scorch' and self.hits >= cfg['scorch_every']:
                    tgt['burn'] = t + 4.0
                    self.hits = 0
                elif st['set'] == 'sustain' and self.hits >= cfg['sustain_every'] and t >= self.sus_cd:
                    self.hp = min(st['H'], self.hp + cfg['sustain_pct'][st['awk']] * st['H'])
                    self.hits = 0
                    self.sus_cd = t + cfg['sustain_icd']
                elif st['set'] in ('burst', 'sustain'):
                    self.hits = min(self.hits, cfg['burst_every'])
                if boss is not None and not adds_done and 'adds' in mapdef['boss'] and 0 < boss['hp'] <= mapdef['boss']['adds']['at_hp'] * boss['max']:
                    adds_done = True
                    for _ in mapdef['boss']['adds']['points']:
                        mobs.append(mob(cfg, mapdef, mapdef['boss']['adds']['role'], rng, kn, t + 1.0))
                continue
            for m in alive:
                if m['atk'] > 0 and m['next'] == t:
                    m['next'] = t + m['iv']
                    if (m['role'] in MELEE_ROLES or m['role'] == 'boss') and id(m) not in engaged:
                        break
                    self.hurt(m['atk'], m['tele'])
                    break
            else:
                for s in skills:
                    if s['next'] == t:
                        sk = s['s']
                        s['next'] = t + sk['every']
                        if boss['hp'] > 0 and (sk.get('below') is None or boss['hp'] <= sk['below'] * boss['max']):  # phase gate (P2-6)
                            self.hurt(sk['dmg'], True)
                            f = sk.get('follow')
                            if f and (f.get('below') is None or boss['hp'] <= f['below'] * boss['max']):
                                pending.append((t + f.get('delay', 1.0), f['dmg']))
                        break
                else:
                    for p in list(pending):
                        if p[0] == t:
                            pending.remove(p)
                            if boss['hp'] > 0:
                                self.hurt(p[1], True)
                            break
            if self.hp <= 0:
                return False


def run_map(cfg, key, st, kn, rng, potions):
    """One entry. Returns (cleared, potions_used, extra, seconds, damage taken, where it died)."""
    m = cfg['maps'][key]
    f = Fight(cfg, st, kn, rng, potions)
    extra = ('none', 'treasure', 'elite', 'chest')[pick(cfg['extra_w'], rng.random())]
    for rk in ('r1', 'r2', 'r3'):
        room = m['rooms'][rk]
        comp = room['a'] if rng.random() < 0.5 else room['b']
        mobs = [mob(cfg, m, role, rng, kn, f.t) for role, n in comp.items() for _ in range(n)]
        if not f.segment(mobs):
            return False, f.used, extra, f.t, f.taken, rk
        if m.get('event', {}).get('after') == rk and extra in ('treasure', 'elite'):
            if not f.segment([mob(cfg, m, extra, rng, kn, f.t)]):
                return False, f.used, extra, f.t, f.taken, 'event'
    b = m['boss']
    boss = {'hp': float(b['hp']), 'max': float(b['hp']), 'atk': b['atk'], 'iv': b.get('interval', 3.0), 'role': 'boss',
            'next': f.t + 2.0, 'tele': False, 'burn': 0.0}
    if not f.segment([boss], boss=boss, mapdef=m):
        return False, f.used, extra, f.t, f.taken, 'boss'
    return True, f.used, extra, f.t, f.taken, None


def pick(weights, u):
    tot = sum(weights)
    x = u * tot
    acc = 0
    for i, w in enumerate(weights):
        acc += w
        if x < acc:
            return i
    return len(weights) - 1


# ---------------------------------------------------------------- progression

def item(fam, slot, tier, q=0, f=0, src='drop'):
    return {'fam': fam, 'slot': slot, 'tier': tier, 'q': q, 'f': f, 'enh': 0, 'pity': 0, 'src': src}


def desc(it):
    qn = ('标准', '精良', '卓越', '极品')[it['q']]
    fam = {'burst': '爆', 'scorch': '焚', 'sustain': '愈', 'none': '无'}[it['fam']]
    return 'T%d%s%s%s+%d' % (it['tier'], fam, '刃' if it['slot'] == 'blade' else '符', qn if it['q'] else '', it['enh'])


class Player:
    def __init__(self, cfg, kn, rng):
        self.cfg, self.kn, self.rng = cfg, kn, rng
        self.blade = item('none', 'blade', 0, src='starter')
        self.charm = item('none', 'charm', 0, src='starter')
        self.coin = self.shard = self.core = self.bone = self.blank = self.xp = 0
        self.potions = cfg['starter_potions']
        self.marks = {1: 0, 2: 0, 3: 0}
        self.cleared = set()
        self.save_for_upgrade = False
        self.day, self._bday, self._bn = None, None, 0  # P2-7 daily bounty: loops set p.day before settle()

    def st(self):
        return stats(self.cfg, self.blade, self.charm, level_of(self.cfg, self.xp))

    def roll_item(self, tier):
        r, cfg = self.rng, self.cfg
        u = r.random()
        tw = cfg['target_weight']
        others = [x for x in FAMS if x != self.kn.target]
        fam = self.kn.target if u < tw else (others[0] if u < tw + (1 - tw) / 2 else others[1])
        slot = 'blade' if r.random() < 0.5 else 'charm'
        return item(fam, slot, tier, pick(cfg['quality_w'], r.random()), pick(cfg['craft_w'], r.random()))

    def consider(self, new):
        """Equip `new` if the whole loadout gets stronger; dismantle what is not worn."""
        cfg, kn = self.cfg, self.kn
        slot = new['slot']
        cur = self.blade if slot == 'blade' else self.charm
        cand = dict(new)
        if kn.swap and cur['enh'] > cand['enh']:
            cand['enh'], cand['pity'] = cur['enh'], cur['pity']
        lv = level_of(cfg, self.xp)
        if slot == 'blade':
            a, b = stats(cfg, cand, self.charm, lv), stats(cfg, self.blade, self.charm, lv)
        else:
            a, b = stats(cfg, self.blade, cand, lv), stats(cfg, self.blade, self.charm, lv)
        if power(cfg, a, kn) > power(cfg, b, kn) * 1.0001:
            if kn.swap and cand['enh'] == cur['enh'] and cur['enh'] > new['enh']:
                cur = dict(cur, enh=new['enh'], pity=new['pity'])
            if slot == 'blade':
                self.blade = cand
            else:
                self.charm = cand
            self.dismantle(cur)
        else:
            self.dismantle(new)

    def dismantle(self, it):
        if it['src'] in ('drop', 'mark') and it['tier'] >= 1:
            self.blank += it['tier']

    def bounty(self):
        """P2-7 (D79): the n-th settled clear of the stamina day pays the bounty tiers with clears == n."""
        if self.day is None:
            return
        if self._bday != self.day:
            self._bday, self._bn = self.day, 0
        self._bn += 1
        for b in self.cfg.get('bounty', []):
            if int(b.get('clears', 0)) == self._bn:
                self.coin += int(b.get('coin', 0)); self.shard += int(b.get('shard', 0))
                self.bone += int(b.get('bone', 0)); self.core += int(b.get('core', 0))

    def settle(self, key, extra):
        self.bounty()
        cfg, m = self.cfg, self.cfg['maps'][key]
        b = cfg['base']
        self.coin += b['coin']; self.shard += b['shard']; self.bone += b['bone']; self.core += b['core']
        self.xp += b['xp']
        tier = m['tier']
        self.marks[tier] += b['mark']
        drops = [self.roll_item(tier)]
        if extra == 'treasure':
            self.coin += cfg['treasure_coin']
        elif extra == 'elite':
            self.shard += cfg['elite_shard']; self.core += cfg['elite_core']
        elif extra == 'chest':
            drops.append(self.roll_item(tier))
        if key not in self.cleared:
            self.cleared.add(key)
            fc = m.get('first_clear') or {}
            if 'choice' in fc:
                drops.append(item(self.kn.target, fc['choice'], fc.get('tier', 1), src='task'))
            self.shard += fc.get('shard', 0); self.core += fc.get('core', 0); self.coin += fc.get('coin', 0)
            self.bone += fc.get('bone', 0); self.blank += fc.get('blank', 0)
        for d in drops:
            self.consider(d)
        for t in (1, 2, 3):
            while self.marks[t] >= cfg['marks_per']:
                self.marks[t] -= cfg['marks_per']
                slot = self.weaker_slot(t)
                self.consider(item(self.kn.target, slot, t, src='mark'))

    def weaker_slot(self, tier):
        bl, ch = self.blade, self.charm
        tgt = self.kn.target
        if bl['fam'] != tgt or bl['tier'] < tier:
            if ch['fam'] != tgt or ch['tier'] < tier:
                return 'blade' if bl['tier'] <= ch['tier'] else 'charm'
            return 'blade'
        if ch['fam'] != tgt or ch['tier'] < tier:
            return 'charm'
        return 'blade' if bl['q'] <= ch['q'] else 'charm'

    def invest(self):
        cfg, kn = self.cfg, self.kn
        reserve = kn.coin_reserve
        # §6.4 upgrade first (T1→T2 after q04, T2→T3 after q07); lower piece first, blade on ties
        self.save_for_upgrade = False
        for it in sorted((self.blade, self.charm), key=lambda x: (x['tier'], x['slot'] != 'blade')):
            flag = {1: 'q04', 2: 'q07'}.get(it['tier'])
            if not flag or flag not in self.cleared or it['fam'] == 'none':
                continue
            c = cfg['upgrade'][it['tier']]
            if self.shard >= c['shard'] and self.core >= c['core'] and self.blank >= c['blank']:
                if self.coin - c['coin'] >= reserve:
                    self.shard -= c['shard']; self.core -= c['core']; self.blank -= c['blank']; self.coin -= c['coin']
                    it['tier'] += 1
                else:
                    self.save_for_upgrade = True
                    reserve += c['coin']
                break
        # §6.1 enhance the lower piece while affordable
        while True:
            it = min((self.blade, self.charm), key=lambda x: (x['enh'], x['slot'] != 'blade'))
            if it['enh'] >= 10 or it['tier'] == 0 and it['enh'] >= 10:
                break
            t = it['enh'] + 1
            sh, co, cn = cfg['enh_shard'][t], cfg['enh_core'][t], cfg['enh_coin'][t]
            if self.shard < sh or self.core < co or self.coin - cn < reserve:
                break
            self.shard -= sh; self.core -= co; self.coin -= cn
            if it['pity'] + 1 >= cfg['enh_max'][t] or self.rng.random() < cfg['enh_rate'][t]:
                it['enh'] = t; it['pity'] = 0
            else:
                it['pity'] += 1

    def buy_potions(self):
        while self.potions < self.kn.potion_keep and self.coin >= self.cfg['potion_price']:
            self.coin -= self.cfg['potion_price']
            self.potions += 1


def simulate_player(cfg, kn, seed, max_runs=600, stop_at=None):
    rng = random.Random(seed)
    p = Player(cfg, kn, rng)
    order = cfg['order']
    stop_at = stop_at or order[-1]
    per_day = cfg['stamina_day'] // cfg['run_cost']
    rec = {k: {'entries': 0, 'clears': 0, 'deaths': 0, 'fc_run': None, 'fc_day': None, 'gear': None,
               'B': None, 'H': None, 'lv': None, 'front_entries': 0} for k in order}
    refund_day = -1
    cur = 0
    runs = 0
    while runs < max_runs:
        day = runs // per_day + 1
        front = next((i for i, k in enumerate(order) if k not in p.cleared), None)
        if front is None or stop_at in p.cleared:
            break
        cur = min(cur, front)
        key = order[cur]
        p.buy_potions()
        p.day = day
        st = p.st()
        ok, used, extra, secs, taken, where = run_map(cfg, key, st, kn, rng, p.potions)
        p.potions -= used
        runs += 1
        r = rec[key]
        r['entries'] += 1
        if cur == front:
            r['front_entries'] += 1
        if ok:
            r['clears'] += 1
            first = key not in p.cleared
            if first:
                r['fc_run'], r['fc_day'] = runs, day
                r['gear'] = desc(p.blade) + ' ' + desc(p.charm)
                r['B'], r['H'], r['lv'] = st['B'], st['H'], level_of(cfg, p.xp)
            p.settle(key, extra)
            if first:
                if key == stop_at:
                    break
            p.invest()
            cur = front + 1 if cur == front else front
        else:
            r['deaths'] += 1
            if cfg['death_refund'] > 0 and refund_day != day:
                refund_day = day
                p.potions += min(used, cfg['death_refund'])
            if kn.route == 'alternate':
                cur = max(0, front - 1) if cur == front else front
            else:
                cur = max(0, cur - 1)
    return rec, runs


def clear_rate(cfg, key, st, kn, n, seed=7):
    rng = random.Random(seed)
    return sum(run_map(cfg, key, st, kn, rng, kn.potion_keep)[0] for _ in range(n)) / n


# ---------------------------------------------------------------- report

def pct(vals, p):
    """Percentile over ALL players; a player who never got there counts as +inf (returns None if past the cap)."""
    v = sorted(x if x is not None else math.inf for x in vals)
    x = v[min(len(v) - 1, int(math.ceil(p * len(v))) - 1)]
    return None if x == math.inf else x


def summarize(cfg, kn, players, max_runs, seed0=1000):
    order = cfg['order']
    recs = [simulate_player(cfg, kn, seed0 + i, max_runs)[0] for i in range(players)]
    per_day = cfg['stamina_day'] // cfg['run_cost']
    out = []
    for k in order:
        got = [r[k] for r in recs if r[k]['fc_run'] is not None]
        reach = len(got) / players
        fe = [r[k]['front_entries'] for r in recs if r[k]['fc_run'] is not None]
        ent = sum(r[k]['entries'] for r in recs)
        cl = sum(r[k]['clears'] for r in recs)
        fl = sum(r[k]['front_entries'] for r in recs)
        row = {'map': k, 'reach': reach, 'clear_rate_all': cl / ent if ent else None,
               'front_rate': (len(got) / fl) if fl else None,
               'attempts': statistics.median(fe) if fe else None,
               'fc_day': pct([r[k]['fc_day'] for r in recs], 0.5),
               'fc_day_p90': pct([r[k]['fc_day'] for r in recs], 0.9),
               'fc_run': pct([r[k]['fc_run'] for r in recs], 0.5),
               'gear': statistics.mode([g['gear'] for g in got]) if got else None,
               'B': statistics.median([g['B'] for g in got]) if got else None,
               'H': statistics.median([g['H'] for g in got]) if got else None,
               'lv': statistics.median([g['lv'] for g in got]) if got else None}
        out.append(row)
    return out, max_runs / per_day


BOOK_REF = {'q01': 'T0', 'q02': 'T1刃+T0符', 'q03': 'T1两件+0~3', 'q04': 'T1两件+3~6', 'q05': 'T2两件+3~6',
            'q06': 'T2两件+6', 'q07': 'T2两件+6~8'}


# book §3.2 reference loadout per map (upper end of the range), burst set; level is our assumption (book gives Lv30
# only for the §7.4 T2 reference character)
REF_GEAR = {'q01': (0, 0, 0, 0, 10), 'q02': (1, 0, 0, 0, 12), 'q03': (1, 3, 1, 3, 15), 'q04': (1, 6, 1, 6, 20),
            'q05': (2, 6, 2, 6, 25), 'q06': (2, 6, 2, 6, 30), 'q07': (2, 8, 2, 8, 30)}


def ref_table(cfg, dodges, n):
    lines = ['| 图 | 书参考投入（取上沿） | Lv | B / H / M | ' + ' | '.join('躲避 %.1f 通关率' % d for d in dodges) + ' |',
             '|---|---|---:|---|' + '---:|' * len(dodges)]
    for k in cfg['order']:
        bt, be, ct, ce, lv = REF_GEAR[k]
        bl = dict(item('burst' if bt else 'none', 'blade', bt), enh=be)
        ch = dict(item('burst' if ct else 'none', 'charm', ct), enh=ce)
        st = stats(cfg, bl, ch, lv)
        rates = [clear_rate(cfg, k, st, Knobs(d), n) for d in dodges]
        lines.append('| %s | %s | %d | %.1f / %.0f / %.2f | %s |' % (k.upper(), desc(bl) + ' ' + desc(ch), lv, st['B'], st['H'],
                     st['M'], ' | '.join('%d%%' % round(100 * r) for r in rates)))
    return '\n'.join(lines)


def fmt(x, f='%.0f', none='—'):
    return none if x is None else f % x


def table(rows, cap_days, dodge):
    lines = ['| 图 | 到达率 | 前沿通关率 | 首通前进本（中位） | 首通在第几天（中位 / P90） | 首通时装备（众数） | B / H / Lv | 书参考投入 |',
             '|---|---:|---:|---:|---|---|---|---|']
    for r in rows:
        lines.append('| %s | %s | %s | %s | %s | %s | %s | %s |' % (
            r['map'].upper(), '%d%%' % round(100 * r['reach']),
            fmt(r['front_rate'] and 100 * r['front_rate'], '%.0f%%'), fmt(r['attempts']),
            ('第 %s 天 / %s' % (fmt(r['fc_day']), fmt(r['fc_day_p90'], none='>%d' % cap_days))) if r['fc_day']
            else '过半玩家 %d 天内未通' % cap_days,
            r['gear'] or '—',
            ('%.1f / %.0f / %d' % (r['B'], r['H'], r['lv'])) if r['B'] else '—', BOOK_REF.get(r['map'], '')))
    return '\n'.join(lines)


# measured natural playtests (docs/PLAYTEST-P1-progression.md): first-clear run index, or ('>', runs) = not cleared
MEASURED = {
    'v2': {'profile': 'v2', 'route': 'alternate', 'runs': 92,
           'fc': {'q01': 2, 'q02': 7, 'q03': 14, 'q04': 56, 'q05': ('>', 92)}},
    'v3': {'profile': 'current', 'route': 'stepdown', 'runs': 44,
           'fc': {'q01': 1, 'q02': 10, 'q03': 13, 'q04': ('>', 44)}},
}


def calibrate(players, grid):
    out = []
    for name, m in MEASURED.items():
        cfg = p1config.load(m['profile'])
        for d in grid:
            kn = Knobs(d, route=m['route'])
            recs = [simulate_player(cfg, kn, 5000 + i, m['runs'])[0] for i in range(players)]
            err, cells = 0.0, []
            for k, real in m['fc'].items():
                med = pct([r[k]['fc_run'] for r in recs], 0.5)
                share = sum(1 for r in recs if r[k]['fc_run'] is not None) / players
                if isinstance(real, tuple):
                    e = 0.0 if med is None else math.log(real[1] / med)
                    cells.append('%s 实测 >%d · 模拟 %s（%d%% 在 %d 局内通）' % (k.upper(), real[1], fmt(med, none='>%d' % m['runs']),
                                                                   round(100 * share), m['runs']))
                else:
                    e = abs(math.log((med if med is not None else 2 * m['runs']) / real))
                    cells.append('%s 实测 %d · 模拟 %s' % (k.upper(), real, fmt(med, none='>%d' % m['runs'])))
                err += e
            out.append((name, d, err / len(m['fc']), cells))
    return out


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument('--profile', default='current', choices=sorted(p1config.PROFILES))
    ap.add_argument('--dodge', type=float, nargs='*', default=[0.3, 0.5, 0.7])
    ap.add_argument('--players', type=int, default=60)
    ap.add_argument('--days', type=int, default=60, help='stamina-day cap per simulated player')
    ap.add_argument('--route', default='stepdown', choices=['stepdown', 'alternate'])
    ap.add_argument('--swap', action='store_true', help='use the §6.3 free enhance swap (the bots did not)')
    ap.add_argument('--no-bounty', action='store_true', help='without the P2-7 daily bounty (D79) for comparison')
    ap.add_argument('--target', default='burst', choices=FAMS)
    ap.add_argument('--uptime', type=float, default=0.70)
    ap.add_argument('--ref', action='store_true', help='clear rate per map at the book §3.2 reference loadout')
    ap.add_argument('--calibrate', action='store_true', help='fit the dodge level against the measured playtests')
    args = ap.parse_args(argv)
    if args.calibrate:
        grid = args.dodge if args.dodge != [0.3, 0.5, 0.7] else [0.3, 0.4, 0.5, 0.6, 0.7]
        print('| 试玩 | 躲避 | 平均对数误差 | 首通在第几局（中位） |\n|---|---:|---:|---|')
        for name, d, e, cells in calibrate(args.players, grid):
            print('| %s | %.2f | %.2f | %s |' % (name, d, e, '；'.join(cells)))
        return
    cfg = p1config.load(args.profile)
    if args.no_bounty:
        cfg["bounty"] = []
    if args.ref:
        print(ref_table(cfg, args.dodge, args.players * 5))
        return
    for w in cfg['warnings']:
        print('WARNING:', w, file=sys.stderr)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    print('# p1sim profile=%s route=%s swap=%s target=%s players=%d cap=%d days (%d runs/day)\n' % (
        args.profile, args.route, args.swap, args.target, args.players, args.days, per_day))
    for d in args.dodge:
        kn = Knobs(d, route=args.route, swap=args.swap, target=args.target, uptime=args.uptime)
        rows, cap = summarize(cfg, kn, args.players, args.days * per_day)
        last = rows[-1]
        print('## dodge %.2f — Q07 first clear: %s\n' % (d, ('median day %s (P90 %s), %d%% of players within %d days'
              % (fmt(last['fc_day']), fmt(last['fc_day_p90'], none='>%d' % args.days), round(100 * last['reach']),
                 args.days)) if last['fc_day'] else '%d%% of players within %d days' % (round(100 * last['reach']), args.days)))
        print(table(rows, args.days, d))
        print()


if __name__ == '__main__':
    main()

"""P2-5 party model (docs/design/design-ember-v1.1-P2-draft.md §5b, book §18.4): 3–5 role-less players in one run.

Rules modelled (all from the live code / book, nothing raid-specific invented):
- enemy HP × party factor 1 + 0.65 (n − 1) (A18, EmberRunRules.hpFactor), locked at entry; damage does not scale;
- no roles / no taunt: each enemy attack picks a random living member; melee bodies engage at most kn.engage per member;
- boss skills are the existing telegraphed moves: the chosen target always rolls a dodge; every other living member is
  inside the shape with probability `splash` (cone/line/charge 0.45, circle 0.30) and then rolls its own dodge;
- sets act on their owner only (炽愈 heals self; 烬爆 / 焚烬 hit enemies; book §18.4: no raid-only trinkets);
- D106 revive: a dead member watches and comes back at 50 % H when the next room starts, when the boss appears and
  when the boss crosses 50 % (phase change); the run still fails when nobody is left; potions per member
  (shared 15 s cooldown per character, same 20 %). --no-revive = the old rule (a dead member stays down).
Player states come from p2econ (Q07 first clear, then W weeks of challenge farming with the §6.3 swap).
Standard library only.
"""
import argparse, copy, os, random, statistics, sys
sys.path.insert(0, os.path.dirname(__file__))
import p1config, p1sim, p2econ, miniyaml, burnbook, rules

ROOT = os.path.join(os.path.dirname(__file__), '..', '..')
SPLASH = {'cone': 0.45, 'line': 0.45, 'charge': 0.45, 'circle': 0.30}


K = 0.65  # A18 per extra member; raids read `hp_per_member` from their runs block


def hp_factor(n, k=None):
    return 1.0 + (K if k is None else k) * (n - 1)


class Member:
    def __init__(self, cfg, st, kn, potions, rng):
        """rng: the entry's setup stream (M01: explicit, never the module-level random)"""
        self.st, self.kn, self.cfg = st, kn, cfg
        self.hp = st['H']
        self.potions, self.used = potions, 0
        self.pcd = 0.0
        self.hits = 0
        self.burst_cd = self.sus_cd = 0.0
        self.next_swing = 1.0 + rng.random() * 0.5
        self.next_skill = 1.0
        # M03: each player has an own EmberSetEngine / EmberBurnBook (EmberSetService.Session), so two 焚烬 players on
        # one target both tick, each with its own snapshot and schedule
        self.burns = burnbook.BurnBook(burnbook.TICKS + max(0, int(p1sim.gm(st, 'burn_ticks', 0))))
        self.btgt, self.spread_keys, self.spread_cd = {}, set(), -1.0

    def alive(self):
        return self.hp > 0


class Party:
    def __init__(self, cfg, members, rng):
        self.cfg, self.ms, self.rng = cfg, members, rng
        self.t = 0.0

    def living(self):
        return [m for m in self.ms if m.alive()]

    revive_on = True

    def revive(self, t):
        """D106: every fallen member stands up at 50 % H (only while someone is still standing)."""
        if not self.revive_on or not self.living():
            return
        for m in self.ms:
            if not m.alive():
                m.hp = 0.5 * m.st['H']
                m.next_swing = max(m.next_swing, t + 1.0)
                self.revives = getattr(self, 'revives', 0) + 1

    def hurt(self, m, raw, tele, dodgeable=True, kind='mob'):
        cfg, kn, st = self.cfg, m.kn, m.st
        p = min(0.95, kn.dodge + kn.tele_bonus) if tele else kn.dodge
        if kind in ('tele', 'share') and st.get('mods') and 'win_dmg' in st['mods']:  # PROPOSAL 破绽窗口 (p1sim.dmult)
            m.win_until = self.t + p1sim.gm(st, 'win_secs', 0.0)
        if dodgeable and self.rng.random() < p:
            if kind == 'tele' and st.get('mods'):  # D141: a dodged boss telegraph
                m.dodge_until = self.t + p1sim.gm(st, 'dodge_secs', 0.0)
                dh = p1sim.gm(st, 'dodge_heal', 0.0)
                if dh > 0 and self.t >= getattr(m, 'dodge_heal_cd', -1.0):
                    m.dodge_heal_cd = self.t + p1sim.gm(st, 'dodge_icd', 6.0)
                    m.hp = min(st['H'], m.hp + dh * st['H'])
                if p1sim.gm(st, 'dodge_burst', 0.0) > 0:  # D141 借势: counter + n
                    m.hits += int(p1sim.gm(st, 'dodge_burst', 0.0))
            return False
        if st.get('mods'):
            raw *= p1sim.gm(st, 'taken_' + kind) * p1sim.gm(st, 'taken_all')
            if kind == 'tele' and p1sim.gm(st, 'hit_burst', 0.0) > 0:  # D141 反震
                m.hits += int(p1sim.gm(st, 'hit_burst', 0.0))
        m.hp -= raw * m.st['M']
        if m.hp <= 0:
            m.burns.clear(); m.btgt.clear()  # a fallen player's burns end (EmberSetService.onDeath → clearCombat)
        if 0 < m.hp < kn.potion_at * m.st['H'] and m.potions > 0 and self.t >= m.pcd:
            m.potions -= 1; m.used += 1
            m.pcd = self.t + cfg['potion_cd']
            m.hp = min(m.st['H'], m.hp + cfg['potion_pct'] * p1sim.gm(st, 'potion') * m.st['H'])
        return True

    def skill_hit(self, sk, raw):
        """Returns how many members the telegraph landed on (None for a share circle / nobody alive)."""
        liv = self.living()
        if not liv:
            return None
        tgt = self.rng.choice(liv)
        if str(sk.get('share', '')).lower() == 'true':
            # R03 (D137) 烬核: the target is in the circle; every other living member gets there in the 3 s warning with
            # the same telegraph-response chance as a dodge (dodge + tele_bonus, ≤ 95 %); the hit is split equally and
            # cannot be dodged by those inside (standing in it is the point)
            inside = [tgt] + [m for m in liv if m is not tgt and self.rng.random() < min(0.95, m.kn.dodge + m.kn.tele_bonus)]
            # D141 扛核: a member with share_w > 1 carries that many portions (the rest split what is left), × share_taken
            w = [p1sim.gm(m.st, 'share_w') for m in inside]
            for m, wi in zip(inside, w):
                self.hurt(m, raw * wi / sum(w) * p1sim.gm(m.st, 'share_taken'), True, dodgeable=False, kind='share')
            self.shares = getattr(self, 'shares', []) + [len(inside)]
            return None
        sp = SPLASH.get(sk.get('type'), 0.35)
        landed = 0
        for m in liv:
            if m is tgt or self.rng.random() < sp:
                landed += 1 if self.hurt(m, raw, True, kind='tele') else 0
        return landed

    def stun_boss(self, boss, skills, s, secs, t):
        """D188 / D192 / D195: boss stunned `secs` (no melee, no skills; every skill timer slides, like p1sim solo)."""
        boss['next'] = max(boss['next'], t) + secs
        for _s in skills:
            _s['next'] += secs
        self.stuns = getattr(self, 'stuns', 0.0) + secs

    def segment(self, mobs, boss=None, mapdef=None):
        cfg, rng = self.cfg, self.rng
        t = self.t
        skills = [{'s': s, 'next': t + s['every']} for s in (mapdef['boss'].get('skills', []) if boss else [])]
        pending, adds_done = [], False
        for m in self.ms:
            m.next_swing = max(m.next_swing, t + 1.0)
        while True:
            for m in self.ms:  # D141 燎原, per player (EmberSetService.onMobDeath loops every session)
                if m.alive() and len(m.burns) and m.st.get('mods') and p1sim.gm(m.st, 'burn_spread', 0) > 0:
                    p1sim.spread_burn(m, mobs, t)
            alive = [x for x in mobs if x['hp'] > 0]
            if not alive:
                self.t = t
                return True
            liv = self.living()
            if not liv:
                self.t = t
                return False
            if boss is not None and not getattr(self, '_phase_rev', False) and boss['hp'] <= 0.5 * boss['max']:
                self._phase_rev = True  # D106: boss phase change revives
                self.revive(t)
                liv = self.living()
            # D118: one extra revive in the last phase — armed once the boss is at <= LAST_REVIVE_HP with someone down,
            # the fallen stand up LAST_REVIVE_DELAY s later (once per raid, only while someone is still standing)
            if (boss is not None and LAST_REVIVE_HP > 0 and getattr(self, '_phase_rev', False) and not getattr(self, '_last_rev', False)
                    and boss['hp'] <= LAST_REVIVE_HP * boss['max']):
                if getattr(self, '_last_at', None) is None and len(liv) < len(self.ms):
                    self._last_at = t + LAST_REVIVE_DELAY
                if getattr(self, '_last_at', None) is not None and t >= self._last_at:
                    self._last_rev = True
                    self.revive(t)
                    self.last_revives = getattr(self, 'last_revives', 0) + 1
                    liv = self.living()
            cand = [m.next_swing for m in liv] + [x['next'] for x in alive if x['atk'] > 0]
            if getattr(self, '_last_at', None) is not None and not getattr(self, '_last_rev', False) and self._last_at > t:
                cand.append(self._last_at)
            cand += [s['next'] for s in skills] + [p[0] for p in pending]
            cand += [nd / 1000.0 for nd in (m.burns.next_due() for m in liv) if nd is not None]
            tn = min(cand)
            t = self.t = tn
            for m in liv:  # M03: every player's own burn ticks due now (discrete, snapshot; EmberSetService.burnTick)
                if len(m.burns):
                    p1sim.burn_ticks(m, t)
            if p1sim.FEST:  # D139 burn kills (credited to the first living member: one burst per kill step)
                p1sim.fest_proc(liv[0], alive, t, liv[0].st['B'], liv[0].kn.skill_hits)
            sw = next((m for m in liv if m.next_swing == t), None)
            if sw is not None:
                m, st = sw, sw.st
                m.next_swing = t + m.kn.swing / m.kn.uptime
                tgt = alive[0]  # focus fire
                dm = lambda x: p1sim.dmult(st, x, t, m)
                tgt['hp'] -= st['B'] * (cfg['crit_mult'] if self.crit_rng.random() < cfg['crit_rate'] else 1.0) * dm(tgt)
                m.hits += 1
                if t >= m.next_skill:
                    for x in alive[:m.kn.skill_hits]:
                        x['hp'] -= cfg['skill_mult'] * st['B'] * dm(x)
                    m.next_skill = t + cfg['skill_cd']
                bev = cfg['burst_every'] + int(p1sim.gm(st, 'burst_every', 0))
                sev = cfg['sustain_every'] + int(p1sim.gm(st, 'sustain_every', 0))
                if st['set'] == 'burst' and m.hits >= bev and t >= m.burst_cd:
                    caught = alive[:min(m.kn.skill_hits, 5)]
                    bm = p1sim.burst_n_mult(st, len(caught))  # PROPOSAL 聚爆 (1.0 unless the keys are set)
                    for x in caught:
                        x['hp'] -= cfg['burst'][st['awk']] * p1sim.gm(st, 'burst_mult') * bm * p1sim.gm(st, 'set_dmg') * st['B'] * dm(x)
                    m.hits = 0; m.burst_cd = t + cfg['burst_icd']
                elif st['set'] == 'scorch' and m.hits >= cfg['scorch_every']:
                    p1sim.ignite(m, tgt, t); m.hits = 0
                elif st['set'] == 'sustain' and m.hits >= p1sim.sustain_every(cfg, st, m.hp) and t >= m.sus_cd:
                    sm = p1sim.gm(st, 'sustain_mult') * (p1sim.gm(st, 'sustain_low_mult') if m.hp < p1sim.gm(st, 'sustain_low', 0.0) * st['H'] else 1.0)
                    m.hp = min(st['H'], m.hp + cfg['sustain_pct'][st['awk']] * sm * st['H'])
                    m.hits = 0; m.sus_cd = t + cfg['sustain_icd']
                elif st['set'] in ('burst', 'sustain'):
                    m.hits = min(m.hits, max(bev, sev))
                if boss is not None and not adds_done and 'adds' in mapdef['boss'] and 0 < boss['hp'] <= mapdef['boss']['adds']['at_hp'] * boss['max']:
                    adds_done = True
                    for _ in mapdef['boss']['adds']['points']:
                        a = p1sim.mob(cfg, mapdef, mapdef['boss']['adds']['role'], self.spawn_rng, liv[0].kn, t + 1.0)
                        a['hp'] *= self.hpf
                        mobs.append(a)
                if p1sim.FEST:  # D139 festival charm: every member wears one (own cooldown each)
                    p1sim.fest_proc(m, alive, t, st['B'], m.kn.skill_hits)
                continue
            hit = False
            melee = [x for x in alive if x['role'] in p1sim.MELEE_ROLES or x['role'] == 'boss']
            engaged = set(id(x) for x in melee[:liv[0].kn.engage * len(liv)])
            for x in alive:
                if x['atk'] > 0 and x['next'] == t:
                    x['next'] = t + x['iv']
                    if (x['role'] in p1sim.MELEE_ROLES or x['role'] == 'boss') and id(x) not in engaged:
                        hit = True
                        break
                    self.hurt(rng.choice(liv), x['atk'], x['tele'], kind='boss' if x['role'] == 'boss' else 'mob')
                    hit = True
                    break
            if hit:
                continue
            for s in skills:
                if s['next'] == t:
                    sk = s['s']
                    s['next'] = t + sk['every']
                    if boss['hp'] > 0 and (sk.get('below') is None or boss['hp'] <= sk['below'] * boss['max']):  # phase gate (P2-6)
                        landed = self.skill_hit(sk, sk['dmg'])
                        # D195 团本破绽: the target is always inside at warn start (Java arms the whiff only then), so a
                        # whiff_stun telegraph that lands on nobody staggers the boss
                        if RAID_STUN and sk.get('whiff_stun') and landed == 0 and boss['hp'] > 0:
                            self.stun_boss(boss, skills, s, float(sk['whiff_stun']), t)
                        # D188 / D195 撞墙破绽: mean stun per charge = wall_stun × mean living dodge × p1sim.WALL_STUN_K
                        if RAID_STUN and sk.get('wall_stun') and boss['hp'] > 0:
                            _liv = self.living()
                            _wp = (statistics.mean(x.kn.dodge for x in _liv) * p1sim.WALL_STUN_K if p1sim.WALL_STUN_P is None
                                   else p1sim.WALL_STUN_P) if _liv else 0.0
                            if _wp > 0:
                                self.stun_boss(boss, skills, s, float(sk['wall_stun']) * _wp, t)
                        f = sk.get('follow')
                        if f and (f.get('below') is None or boss['hp'] <= f['below'] * boss['max']):
                            pending.append((t + f.get('delay', 1.0), f['dmg'], f))
                    break
            else:
                for p in list(pending):
                    if p[0] == t:
                        pending.remove(p)
                        if boss['hp'] > 0:
                            self.skill_hit(p[2], p[1])
                        break


REVIVE = True  # D106 (--no-revive for the old rule)
RAID_STUN = True  # D195: whiff_stun / wall_stun on raid boss skills (False = strip the stagger, baseline)
LAST_REVIVE_HP, LAST_REVIVE_DELAY = 0.25, 10.0  # D118 (ember-v1-runs.yml raid_revive; --last-revive-hp 0 = off)


def run_party(cfg, m, sts, kns, rng, potions=5):
    """One raid entry with len(sts) members. Returns (cleared, seconds, deaths, potions used, boss seconds).
    M02: own streams for layout + first swings / incoming hits / crits / mid-fight spawns, drawn up front (the caller's
    rng advances by exactly 4)."""
    n = len(sts)
    setup, hits, crits, spawns = (random.Random(rng.getrandbits(64)) for _ in range(4))
    ms = [Member(cfg, st, kn, potions, setup) for st, kn in zip(sts, kns)]
    P = Party(cfg, ms, hits)
    P.crit_rng, P.spawn_rng = crits, spawns
    P.revive_on = REVIVE
    P.hpf = hpf = hp_factor(n, m.get('hp_per_member'))
    dmf = 1.0 + m.get('dmg_per_member', 0.0) * (n - 1)  # raid-only: enemy damage per extra member (role-less parties)
    if dmf != 1.0:
        m = copy.deepcopy(m)
        for mob in m['mobs'].values():
            mob['atk'] *= dmf
        m['boss']['atk'] *= dmf
        for sk in m['boss']['skills']:
            sk['dmg'] *= dmf
    for rk in ('r1', 'r2', 'r3'):
        room = m['rooms'][rk]
        comp = room['a'] if setup.random() < 0.5 else room['b']
        mobs = []
        for role, k in comp.items():
            for _ in range(k):
                x = p1sim.mob(cfg, m, role, setup, kns[0], P.t)
                x['hp'] *= hpf
                mobs.append(x)
        P.revive(P.t)  # D106: next room starts
        if not P.segment(mobs):
            return False, P.t, n - len(P.living()), sum(x.used for x in ms), 0.0
    b = m['boss']
    boss = {'hp': b['hp'] * hpf, 'max': b['hp'] * hpf, 'atk': b['atk'], 'iv': b.get('interval', 3.0), 'role': 'boss',
            'next': P.t + 2.0, 'tele': False, 'uid': next(p1sim._UID)}
    t0 = P.t
    P.revive(P.t)  # D106: the boss appears
    ok = P.segment([boss], boss=boss, mapdef=m)
    return ok, P.t, n - len(P.living()), sum(x.used for x in ms), P.t - t0


def player_pool(cfg, n, weeks, dodge, seed0=7000):
    """Post-Q07 player states: p2econ phase 1 + `weeks` weeks of challenge farming (§6.3 swap on)."""
    ccfg = p2econ.challenge_cfg(cfg)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    pool = []
    i = 0
    while len(pool) < n and i < n * 3:
        kn = p1sim.Knobs(dodge)
        p, runs, rng = p2econ.to_q07(cfg, kn, seed0 + i)
        i += 1
        if p is None:
            continue
        kn.swap = True
        if weeks:
            p2econ.phase2(cfg, ccfg, kn, p, random.Random(seed0 + 99 + i), weeks, False, per_day, runs)
        pool.append((p.st(), kn))
    return pool


def raid_map(cfg, key='r01'):
    return copy.deepcopy(rules.runs()['raids'][key])  # M06: the canonical rule snapshot


def rosters(seed, n, trials, pool_size):
    """M02: the party line-ups (pool indices) for `trials` entries of size n — a function of (seed, n) only, so every
    compared config fights with exactly the same members in the same entries"""
    r = random.Random('%s:roster:%d:%d' % (seed, n, pool_size))
    return [r.sample(range(pool_size), n) for _ in range(trials)]


def entry_rng(seed, n, i):
    """M02: entry i's own stream (layout / hits / crits / spawns are split from it in run_party)"""
    return random.Random('%s:entry:%d:%d' % (seed, n, i))


def evaluate(cfg, m, pool, sizes, trials, rng):
    """rng: only one draw is taken (the base seed); line-ups and entry streams derive from it (M02). Each size's
    result carries `wins` (per entry, in line-up order) for paired confidence intervals."""
    seed = rng.getrandbits(64)
    out = {}
    for n in sizes:
        res = []
        lu = rosters(seed, n, trials, len(pool))
        for i, grp in enumerate(lu):
            res.append(run_party(cfg, m, [pool[j][0] for j in grp], [pool[j][1] for j in grp], entry_rng(seed, n, i)))
        ok = [r for r in res if r[0]]
        out[n] = {'rate': len(ok) / len(res), 'wins': [1 if r[0] else 0 for r in res], 'lineup': __import__('hashlib').sha1(str(lu).encode()).hexdigest()[:10],
                  'secs': statistics.median(r[1] for r in ok) if ok else None,
                  'boss_secs': statistics.median(r[4] for r in ok) if ok else None,
                  'deaths': statistics.mean(r[2] for r in res),
                  'potions': statistics.mean(r[3] for r in res) / n}
    return out


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--raid', default='r01')
    ap.add_argument('--pool', type=int, default=24)
    ap.add_argument('--weeks', type=int, default=2)
    ap.add_argument('--dodge', type=float, default=0.5)
    ap.add_argument('--trials', type=int, default=60)
    ap.add_argument('--boss-hp', type=float, nargs='*', help='sweep the raid boss base HP')
    ap.add_argument('--k', type=float, nargs='*', help='sweep hp_per_member (party HP factor slope)')
    ap.add_argument('--j', type=float, nargs='*', help='sweep dmg_per_member (enemy damage slope per extra member)')
    ap.add_argument('--atk', type=float, nargs='*', help='sweep a damage multiplier on every raid mob / boss / skill')
    ap.add_argument('--share-dmg', type=float, nargs='*', help='D137: sweep the R03 烬核 (share) total damage')
    ap.add_argument('--no-revive', action='store_true', help='D106 comparison: the old rule (no revive)')
    ap.add_argument('--last-revive-hp', type=float, default=None, help='D118: extra last-phase revive at this boss HP ratio (0 = off)')
    ap.add_argument('--last-revive-delay', type=float, default=None, help='D118: seconds from arming to the extra revive')
    a = ap.parse_args()
    print('# ' + __import__('rules').stamp(), flush=True)  # M06: which rule snapshot produced this report
    global REVIVE, LAST_REVIVE_HP, LAST_REVIVE_DELAY
    REVIVE = not a.no_revive
    rr = (rules.runs().get('raid_revive') or {})
    LAST_REVIVE_HP = float(rr.get('last_phase_hp', 0)) if a.last_revive_hp is None else a.last_revive_hp
    LAST_REVIVE_DELAY = float(rr.get('delay', 10)) if a.last_revive_delay is None else a.last_revive_delay
    if a.no_revive:
        LAST_REVIVE_HP = 0
    cfg = p1config.load()
    pool = player_pool(cfg, a.pool, a.weeks, a.dodge)
    m = raid_map(cfg, a.raid)
    print('# p1party: %s, pool %d players (Q07 + %d weeks, dodge %.2f), %d trials per size' % (a.raid, len(pool), a.weeks, a.dodge, a.trials))
    print('# pool B median %.1f, H median %.0f' % (statistics.median(p[0]['B'] for p in pool), statistics.median(p[0]['H'] for p in pool)))
    print('| 每人生命斜率 | 每人伤害斜率 | 伤害 × | 首领基础生命 | 人数 | 通关率 | 全程用时（中位 s） | 首领用时（中位 s） | 平均倒下人数 | 人均喝药 |')
    print('|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|')
    combos = [(z, jj, x, y, sd) for z in (a.k or [m.get('hp_per_member', K)]) for jj in (a.j or [m.get('dmg_per_member', 0.0)])
              for x in (a.atk or [1.0]) for y in (a.boss_hp or [m['boss']['hp']]) for sd in (a.share_dmg or [None])]
    for k, j, am, bh, sd in combos:
        mm = copy.deepcopy(m)
        if sd is not None:
            for sk in mm['boss']['skills']:
                if str(sk.get('share', '')).lower() == 'true':
                    sk['dmg'] = sd
            print('# share dmg %.0f' % sd)
        mm['hp_per_member'] = k
        mm['dmg_per_member'] = j
        mm['boss']['hp'] = bh
        for mob in mm['mobs'].values():
            mob['atk'] *= am
        mm['boss']['atk'] *= am
        for sk in mm['boss']['skills']:
            sk['dmg'] *= am
        r = evaluate(cfg, mm, pool, (3, 4, 5), a.trials, random.Random(11))
        for n, v in r.items():
            f = lambda x: '—' if x is None else '%.0f' % x
            print('| %.2f | %.2f | %.2f | %.0f | %d | %d%% | %s | %s | %.2f | %.1f |' % (k, j, am, bh, n, round(100 * v['rate']), f(v['secs']), f(v['boss_secs']), v['deaths'], v['potions']))


if __name__ == '__main__':
    main()

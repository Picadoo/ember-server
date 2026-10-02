"""P2-5 party model (docs/design-ember-v1.1-P2-draft.md §5b, book §18.4): 3–5 role-less players in one run.

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
import p1config, p1sim, p2econ, miniyaml

ROOT = os.path.join(os.path.dirname(__file__), '..', '..')
SPLASH = {'cone': 0.45, 'line': 0.45, 'charge': 0.45, 'circle': 0.30}


K = 0.65  # A18 per extra member; raids read `hp_per_member` from their runs block


def hp_factor(n, k=None):
    return 1.0 + (K if k is None else k) * (n - 1)


class Member:
    def __init__(self, cfg, st, kn, potions):
        self.st, self.kn = st, kn
        self.hp = st['H']
        self.potions, self.used = potions, 0
        self.pcd = 0.0
        self.hits = 0
        self.burst_cd = self.sus_cd = 0.0
        self.next_swing = 1.0 + random.random() * 0.5
        self.next_skill = 1.0

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

    def hurt(self, m, raw, tele):
        cfg, kn = self.cfg, m.kn
        p = min(0.95, kn.dodge + kn.tele_bonus) if tele else kn.dodge
        if self.rng.random() < p:
            return
        m.hp -= raw * m.st['M']
        if 0 < m.hp < kn.potion_at * m.st['H'] and m.potions > 0 and self.t >= m.pcd:
            m.potions -= 1; m.used += 1
            m.pcd = self.t + cfg['potion_cd']
            m.hp = min(m.st['H'], m.hp + cfg['potion_pct'] * m.st['H'])

    def skill_hit(self, sk, raw):
        liv = self.living()
        if not liv:
            return
        tgt = self.rng.choice(liv)
        sp = SPLASH.get(sk.get('type'), 0.35)
        for m in liv:
            if m is tgt or self.rng.random() < sp:
                self.hurt(m, raw, True)

    def segment(self, mobs, boss=None, mapdef=None):
        cfg, rng = self.cfg, self.rng
        t = self.t
        skills = [{'s': s, 'next': t + s['every']} for s in (mapdef['boss'].get('skills', []) if boss else [])]
        pending, adds_done = [], False
        for m in self.ms:
            m.next_swing = max(m.next_swing, t + 1.0)
        while True:
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
            cand = [m.next_swing for m in liv] + [x['next'] for x in alive if x['atk'] > 0]
            cand += [s['next'] for s in skills] + [p[0] for p in pending]
            tn = min(cand)
            for m in liv:  # 焚烬 burn ticks (owner's B)
                if m.st['set'] == 'scorch':
                    for x in alive:
                        if x.get('burn_by') is m and x['burn'] > t:
                            x['hp'] -= cfg['burn'][m.st['awk']] * m.st['B'] * (min(tn, x['burn']) - t)
            t = self.t = tn
            sw = next((m for m in liv if m.next_swing == t), None)
            if sw is not None:
                m, st = sw, sw.st
                m.next_swing = t + m.kn.swing / m.kn.uptime
                tgt = alive[0]  # focus fire
                tgt['hp'] -= st['B'] * (cfg['crit_mult'] if rng.random() < cfg['crit_rate'] else 1.0)
                m.hits += 1
                if t >= m.next_skill:
                    for x in alive[:m.kn.skill_hits]:
                        x['hp'] -= cfg['skill_mult'] * st['B']
                    m.next_skill = t + cfg['skill_cd']
                if st['set'] == 'burst' and m.hits >= cfg['burst_every'] and t >= m.burst_cd:
                    for x in alive[:min(m.kn.skill_hits, 5)]:
                        x['hp'] -= cfg['burst'][st['awk']] * st['B']
                    m.hits = 0; m.burst_cd = t + cfg['burst_icd']
                elif st['set'] == 'scorch' and m.hits >= cfg['scorch_every']:
                    tgt['burn'] = t + 4.0; tgt['burn_by'] = m; m.hits = 0
                elif st['set'] == 'sustain' and m.hits >= cfg['sustain_every'] and t >= m.sus_cd:
                    m.hp = min(st['H'], m.hp + cfg['sustain_pct'][st['awk']] * st['H'])
                    m.hits = 0; m.sus_cd = t + cfg['sustain_icd']
                elif st['set'] in ('burst', 'sustain'):
                    m.hits = min(m.hits, cfg['burst_every'])
                if boss is not None and not adds_done and 'adds' in mapdef['boss'] and 0 < boss['hp'] <= mapdef['boss']['adds']['at_hp'] * boss['max']:
                    adds_done = True
                    for _ in mapdef['boss']['adds']['points']:
                        a = p1sim.mob(cfg, mapdef, mapdef['boss']['adds']['role'], rng, liv[0].kn, t + 1.0)
                        a['hp'] *= self.hpf
                        mobs.append(a)
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
                    self.hurt(rng.choice(liv), x['atk'], x['tele'])
                    hit = True
                    break
            if hit:
                continue
            for s in skills:
                if s['next'] == t:
                    sk = s['s']
                    s['next'] = t + sk['every']
                    if boss['hp'] > 0 and (sk.get('below') is None or boss['hp'] <= sk['below'] * boss['max']):  # phase gate (P2-6)
                        self.skill_hit(sk, sk['dmg'])
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


def run_party(cfg, m, sts, kns, rng, potions=5):
    """One raid entry with len(sts) members. Returns (cleared, seconds, deaths, potions used)."""
    n = len(sts)
    ms = [Member(cfg, st, kn, potions) for st, kn in zip(sts, kns)]
    P = Party(cfg, ms, rng)
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
        comp = room['a'] if rng.random() < 0.5 else room['b']
        mobs = []
        for role, k in comp.items():
            for _ in range(k):
                x = p1sim.mob(cfg, m, role, rng, kns[0], P.t)
                x['hp'] *= hpf
                mobs.append(x)
        P.revive(P.t)  # D106: next room starts
        if not P.segment(mobs):
            return False, P.t, n - len(P.living()), sum(x.used for x in ms), 0.0
    b = m['boss']
    boss = {'hp': b['hp'] * hpf, 'max': b['hp'] * hpf, 'atk': b['atk'], 'iv': b.get('interval', 3.0), 'role': 'boss',
            'next': P.t + 2.0, 'tele': False, 'burn': 0.0}
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
    runs = miniyaml.load(os.path.join(ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml'))
    return copy.deepcopy(runs['raids'][key])


def evaluate(cfg, m, pool, sizes, trials, rng):
    out = {}
    for n in sizes:
        res = []
        for _ in range(trials):
            grp = rng.sample(pool, n)
            res.append(run_party(cfg, m, [g[0] for g in grp], [g[1] for g in grp], rng))
        ok = [r for r in res if r[0]]
        out[n] = {'rate': len(ok) / len(res),
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
    ap.add_argument('--no-revive', action='store_true', help='D106 comparison: the old rule (no revive)')
    a = ap.parse_args()
    global REVIVE
    REVIVE = not a.no_revive
    cfg = p1config.load()
    pool = player_pool(cfg, a.pool, a.weeks, a.dodge)
    m = raid_map(cfg, a.raid)
    print('# p1party: %s, pool %d players (Q07 + %d weeks, dodge %.2f), %d trials per size' % (a.raid, len(pool), a.weeks, a.dodge, a.trials))
    print('# pool B median %.1f, H median %.0f' % (statistics.median(p[0]['B'] for p in pool), statistics.median(p[0]['H'] for p in pool)))
    print('| 每人生命斜率 | 每人伤害斜率 | 伤害 × | 首领基础生命 | 人数 | 通关率 | 全程用时（中位 s） | 首领用时（中位 s） | 平均倒下人数 | 人均喝药 |')
    print('|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|')
    combos = [(z, jj, x, y) for z in (a.k or [m.get('hp_per_member', K)]) for jj in (a.j or [m.get('dmg_per_member', 0.0)])
              for x in (a.atk or [1.0]) for y in (a.boss_hp or [m['boss']['hp']])]
    for k, j, am, bh in combos:
        mm = copy.deepcopy(m)
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

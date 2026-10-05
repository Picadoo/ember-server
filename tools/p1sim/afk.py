"""D177 rev 2 P1 挂机庭 auto-combat (自动战斗挂机) economy check — docs/design/DESIGN-ember-afk-p1-2026-10-04.md §6.

Rev 2 replaces the stand-still timer: the character fights the AFK tier's mobs by itself with its real P1 stats (auto
swing + 烬斩 on cooldown + set procs), so kills / hour and which tier is survivable come from the player's gear. Each
rewarded kill adds 1/daily_kills of the tier's daily amounts (余烬币 / 经验 / 烬屑 / 骨尘 / 余烬核 / 胚料, deterministic
accumulators — no RNG); a day stops paying after daily_kills rewarded kills (online + offline together).

Kill rate: session() runs p1sim.Fight on waves of PACK melee mobs of the tier's main-story map (dodge 0, no potions —
nobody steers an idle character), with the server's out-of-combat regen between waves and the server's 3-deaths-in-10-
minutes stop. Out-of-combat regen runs while no own mob is alive (the respawn gap). best() = the highest unlocked tier whose 60-minute session is not stopped (the player steps down a tier
otherwise). The economy pays `share` × daily_kills kills at that tier per stamina day: share 1.0 = the cap every day
(AFK machine upper bound), 0.5 = roughly offline accrual alone. A day before the Q01 first clear pays nothing.

Implementation: wraps p1sim.Player.bounty (called once per settled clear, after loops set p.day) so p1sim, p2econ and
every route get it with no edits to those files. A day is paid once, when p.day first exceeds the highest day already
paid; gaps of up to 7 days are paid too, a larger jump (p2econ phase 1 → phase 2 day numbering) pays one day. The AFK
fight uses its own rng seeded from the stats, so player streams are untouched (paired baseline / AFK cells stay paired).

Usage (repo root):
  python3 tools/p1sim/afk.py rate                                                  # kills / h + loot / h by tier × gear
  python3 tools/p1sim/afk.py dyn  [--players 800] [--share 1.0] [--days 60]     # 21 cells Q01–Q07 × dodge 0.3/0.5/0.7
  python3 tools/p1sim/afk.py busy [--players 400] [--runs 1]                    # 1 run/day + AFK vs 3 runs/day, no AFK
  python3 tools/p1sim/afk.py p2econ --share 1.0 -- --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals --every-week
  python3 tools/p1sim/growthrun.py weeks base.md afk.md                         # W30 compare
"""
import argparse, os, random, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

# mirrors ember-v1.yml afk: (D177 rev 2 §3) — CoreRpg EmberAfkConfigTest checks the shipped file against these
DAILY_KILLS = 2400          # rewarded kills / day (online + offline); E3 overrides from economy S22 on install()
PACK = 3                    # personal mobs per wave
RESPAWN = 4.0               # s between a cleared wave and the next
REGEN_PCT = 0.08            # out-of-combat regen (no own mob alive): 8 % H / s
DEATH_GAP = 8.0             # s lost per death (auto respawn at the pad + walk back)
DEATH_STOP = 3              # deaths within 10 min -> auto-combat stops
# (required first clear, AFK mob {mm, hp, atk, interval}, daily amounts at DAILY_KILLS kills). AFK mobs are the
# tier map's melee model with their own (tougher) hp / atk so kill speed and survival depend on gear
TIERS = [
    ('q01', {'mm': 'EmberQ01Melee', 'hp': 160, 'atk': 1.5, 'interval': 2.5}, {'coin': 60, 'xp': 10, 'shard': 2, 'bone': 2, 'core': 0, 'blank': 0}),
    ('q03', {'mm': 'EmberQ03Melee', 'hp': 280, 'atk': 3.5, 'interval': 2.5}, {'coin': 80, 'xp': 15, 'shard': 3, 'bone': 3, 'core': 1, 'blank': 0}),
    ('q05', {'mm': 'EmberQ05Melee', 'hp': 480, 'atk': 5.5, 'interval': 2.5}, {'coin': 100, 'xp': 20, 'shard': 3, 'bone': 4, 'core': 1, 'blank': 1}),
    ('q07', {'mm': 'EmberQ07Melee', 'hp': 720, 'atk': 9, 'interval': 2.5}, {'coin': 120, 'xp': 25, 'shard': 4, 'bone': 5, 'core': 2, 'blank': 1}),
]
RES = ('coin', 'xp', 'shard', 'bone', 'core', 'blank')
STATE = dict({'share': 0.0, 'paid_days': 0, 'tier': [0, 0, 0, 0, 0]}, **{r: 0 for r in RES})
_CACHE = {}


def session(cfg, st, md, minutes=60, seed=7):
    """(kills / h, deaths / h, stopped) of an auto-fighting character with stats st on waves of AFK mob md."""
    import p1sim
    ck = (tuple(sorted(md.items())), PACK, RESPAWN, round(st['B'], 1), round(st['H'], 1), round(st['M'], 3), st['set'], st['awk'], minutes, seed,
          tuple(sorted((st.get('mods') or {}).items())))
    if ck in _CACHE:
        return _CACHE[ck]
    rng = random.Random(seed)
    kn = p1sim.Knobs(0.0, uptime=0.9, potion_keep=0)
    f = p1sim.Fight(cfg, st, kn, rng, 0)
    m = {'mobs': {'melee': md}}
    T = minutes * 60.0
    kills = deaths = 0
    died = []
    stopped = False
    while f.t < T:
        mobs = [p1sim.mob(cfg, m, 'melee', rng, kn, f.t) for _ in range(PACK)]
        ok = f.segment(mobs)
        kills += sum(1 for x in mobs if x['hp'] <= 0)
        if not ok:
            deaths += 1
            died.append(f.t)
            if sum(1 for d in died if f.t - d <= 600) >= DEATH_STOP:
                stopped = True
                break
            f.t += DEATH_GAP
            f.hp = st['H']
        else:
            f.hp = min(st['H'], f.hp + RESPAWN * REGEN_PCT * st['H'])
            f.t += RESPAWN
    h = max(f.t, 1.0) / 3600.0
    out = (kills / h, deaths / h, stopped)
    _CACHE[ck] = out
    return out


def best(cfg, st, cleared):
    """(tier index, kills / h) of the highest unlocked, survivable tier; None before the Q01 first clear."""
    for i in range(len(TIERS) - 1, -1, -1):
        req, md, _ = TIERS[i]
        if req not in cleared:
            continue
        kph, dph, stop = session(cfg, st, md)
        if not stop or i == 0:
            return i, kph
    return None


def pay_days(p, n):
    if n <= 0 or STATE['share'] <= 0:
        return
    b = best(p.cfg, p.st(), p.cleared)
    if b is None:
        return
    i, kph = b
    if kph <= 0:
        return
    kills = STATE['share'] * DAILY_KILLS * n
    d = TIERS[i][2]
    for r in RES:
        v = int(d[r] * kills / DAILY_KILLS)
        setattr(p, r, getattr(p, r) + v)
        STATE[r] += v
    STATE['paid_days'] += n
    STATE['tier'][i + 1] += n


def rate(a):
    """Kills / h, deaths / h and loot / h per tier for reference gear (the gear a player typically has around each
    unlock and one step later)."""
    import p1sim, p1config
    cfg = p1config.load()
    print('# ' + __import__('rules').stamp())
    # median B / H at the moment of each first clear (afk.py dyn, 200 players, dodge 0.5, bv33) and later gear
    S = lambda B, H, M, awk=1: {'B': B, 'H': H, 'M': M, 'set': 'burst', 'awk': awk}
    refs = [
        ('Q01 首通时（初始）', S(12, 40, 0.95, 0)),
        ('Q01 首通奖励 T1 符 + 初始刃', S(12, 71, 0.87, 0)),
        ('Q01 首通后 T1', S(28, 80, 0.87)),
        ('Q03 首通时', S(30, 84, 0.87)),
        ('Q05 首通时', S(52, 144, 0.80)),
        ('Q07 首通时', S(57, 155, 0.80)),
        ('T3+3', S(84, 232, 0.74)),
        ('毕业 T3+9 卓越', S(119, 319, 0.74, 3)),
    ]
    print('\n| 装备 | B / H / M | ' + ' | '.join('T%d %s 杀/h · 死/h' % (i + 1, t[0].upper()) for i, t in enumerate(TIERS)) + ' |')
    print('|---|---|' + '---:|' * len(TIERS))
    for name, st in refs:
        cells = []
        for req, md, d in TIERS:
            kph, dph, stop = session(cfg, st, md)
            cells.append('%s%d · %.1f' % ('✗停 ' if stop else '', kph, dph))
        print('| %s | %.0f / %.0f / %.2f | %s |' % (name, st['B'], st['H'], st['M'], ' | '.join(cells)))
    print('\n| 档 | 每日上限（%d 杀）| 每杀 余烬币 | 每杀 经验 | 每 100 杀 烬屑 / 骨尘 / 核 / 胚料 |' % DAILY_KILLS)
    print('|---|---|---:|---:|---|')
    for i, (req, md, d) in enumerate(TIERS):
        print('| T%d | %s | %.3f | %.3f | %.1f / %.1f / %.2f / %.2f |' % (
            i + 1, ' · '.join('%s %d' % (r, d[r]) for r in RES), d['coin'] / DAILY_KILLS, d['xp'] / DAILY_KILLS,
            *(100.0 * d[r] / DAILY_KILLS for r in ('shard', 'bone', 'core', 'blank'))))


def install(share):
    import p1sim
    import rules
    global DAILY_KILLS
    DAILY_KILLS = rules.amount('S22', 'daily_kills')  # E3 / D225: same SoT as live EmberEconomy
    STATE['share'] = float(share)
    if getattr(p1sim.Player, '_afk_installed', False):
        return
    orig = p1sim.Player.bounty

    def bounty(self):
        if self.day is not None and STATE['share'] > 0:
            last = getattr(self, '_afk_max', None)
            if last is None:
                self._afk_max = self.day
                pay_days(self, 1)
            elif self.day > last:
                gap = self.day - last
                pay_days(self, gap if gap <= 7 else 1)
                self._afk_max = self.day
        return orig(self)
    p1sim.Player.bounty = bounty
    p1sim.Player._afk_installed = True


def dyn(a):
    import p1sim, p1config
    cfg = p1config.load()
    print('# ' + __import__('rules').stamp())
    per_day = cfg['stamina_day'] // cfg['run_cost']
    print('# afk dyn (rev 2 auto-combat): players=%d seed0=%d days=%d share=%.2f daily_kills=%d tiers=%s\n' % (
        a.players, a.seed0, a.days, a.share, DAILY_KILLS, TIERS))
    print('| 躲避 | 图 | 首通前通关率 基线 | +挂机 | 差 pp | 首通中位天 基线 | +挂机 | P90 基线 | +挂机 | 首通 B 中位 基线/挂机 | H 中位 基线/挂机 | Lv 基线/挂机 |')
    print('|---|---|---:|---:|---:|---:|---:|---:|---:|---|---|---|')
    inside, cells = 0, 0
    worst = 0.0
    for d in a.dodge:
        kn = p1sim.Knobs(d)
        STATE['share'] = 0.0
        base, _ = p1sim.summarize(cfg, kn, a.players, a.days * per_day, seed0=a.seed0)
        install(a.share)
        new, _ = p1sim.summarize(cfg, kn, a.players, a.days * per_day, seed0=a.seed0)
        STATE['share'] = 0.0
        for b, n in zip(base, new):
            fr0, fr1 = b['front_rate'], n['front_rate']
            dpp = None if fr0 is None or fr1 is None else 100 * (fr1 - fr0)
            cells += 1
            if dpp is not None and abs(dpp) <= 2.0:
                inside += 1
            if dpp is not None:
                worst = max(worst, abs(dpp))
            f = lambda x, s='%.1f': '—' if x is None else s % x
            print('| %.1f | %s | %s | %s | %s | %s | %s | %s | %s | %s / %s | %s / %s | %s / %s |' % (
                d, b['map'].upper(), f(fr0 and 100 * fr0), f(fr1 and 100 * fr1), f(dpp, '%+.1f'),
                f(b['fc_day'], '%d'), f(n['fc_day'], '%d'), f(b['fc_day_p90'], '%d'), f(n['fc_day_p90'], '%d'),
                f(b['B']), f(n['B']), f(b['H']), f(n['H']), f(b['lv'], '%d'), f(n['lv'], '%d')))
    print('\n**%d/%d 格在 ±2pp 内**（最大 |差| %.1f pp）· 挂机付费天数 %d（按档 T1–T4 %s），合计 %s' % (
        inside, cells, worst, STATE['paid_days'], STATE['tier'][1:], ' '.join('%s %d' % (r, STATE[r]) for r in RES)))


def busy(a):
    """Idle must not beat active: a busy player (a.runs runs/day) WITH the full AFK cap vs an active player (3/day) without."""
    import p1sim, p1config
    cfg = p1config.load()
    print('# ' + __import__('rules').stamp())
    print('# afk busy: players=%d busy runs/day=%d (AFK share %.2f) vs active %d runs/day (no AFK), 120 days\n' % (
        a.players, a.runs, a.share, cfg['stamina_day'] // cfg['run_cost']))
    print('| 躲避 | 图 | 忙碌 无挂机 首通中位天 | 忙碌 + 挂机 | 活跃 无挂机 | 活跃 + 挂机 |')
    print('|---|---|---:|---:|---:|---:|')
    import copy
    slow = copy.deepcopy(cfg); slow['stamina_day'] = a.runs * cfg['run_cost']
    for d in a.dodge:
        kn = p1sim.Knobs(d)
        out = []
        for c, sh in ((slow, 0.0), (slow, a.share), (cfg, 0.0), (cfg, a.share)):
            STATE['share'] = 0.0
            if sh > 0:
                install(sh)
            per = c['stamina_day'] // c['run_cost']
            rows, _ = p1sim.summarize(c, kn, a.players, 120 * per)
            out.append(rows)
            STATE['share'] = 0.0
        for i, k in enumerate(cfg['order']):
            f = lambda x: '—' if x is None else '%d' % x
            print('| %.1f | %s | %s | %s | %s | %s |' % (d, k.upper(), *(f(r[i]['fc_day']) for r in out)))


def p2(a, rest):
    install(a.share)
    import p2econ
    sys.argv = ['p2econ'] + rest
    p2econ.main()
    print('\n# afk share=%.2f paid_days=%d tiers=%s %s' % (a.share, STATE['paid_days'], STATE['tier'][1:],
          ' '.join('%s=%d' % (r, STATE[r]) for r in RES)))


if __name__ == '__main__':
    argv = sys.argv[1:]
    rest = []
    if '--' in argv:
        i = argv.index('--'); rest = argv[i + 1:]; argv = argv[:i]
    ap = argparse.ArgumentParser()
    ap.add_argument('tool', choices=['rate', 'dyn', 'busy', 'p2econ'])
    ap.add_argument('--players', type=int, default=800)
    ap.add_argument('--days', type=int, default=60)
    ap.add_argument('--share', type=float, default=1.0)
    ap.add_argument('--runs', type=int, default=1)
    ap.add_argument('--dodge', type=float, nargs='*', default=[0.3, 0.5, 0.7])
    ap.add_argument('--seed0', type=int, default=1000, help='first player seed (p1sim default 1000; hold-out replicate e.g. 50000)')
    ap.add_argument('--tiers', help='JSON [[flag, map, {coin, xp, shard, bone, core, blank}], …] instead of TIERS (tuning)')
    ap.add_argument('--daily-kills', type=int, default=None)
    ap.add_argument('--mobs', help='JSON [{hp, atk}, …] per tier (tuning)')
    ap.add_argument('--respawn', type=float, default=None)
    a = ap.parse_args(argv)
    if a.tiers:
        import json
        TIERS[:] = [tuple(t) for t in json.loads(a.tiers)]
    if a.daily_kills:
        globals()['DAILY_KILLS'] = a.daily_kills
    if a.mobs:
        import json
        for t, o in zip(TIERS, json.loads(a.mobs)):
            t[1].update(o)
    if a.respawn is not None:
        globals()['RESPAWN'] = a.respawn
    {'rate': lambda: rate(a), 'dyn': lambda: dyn(a), 'busy': lambda: busy(a), 'p2econ': lambda: p2(a, rest)}[a.tool]()

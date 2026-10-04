"""D177 P1 挂机庭 (AFK grounds) economy check — docs/design/DESIGN-ember-afk-p1-2026-10-04.md §6.

The AFK income is account-bound 余烬币 + 余烬经验 only (no items, no marks, no cores / shards). Each stamina day pays
`share` × the daily cap of the player's highest unlocked AFK tier (tier = main-story first clears, see TIERS); a day
before the Q01 first clear pays nothing. `share` 1.0 = the full cap every single day (online 120 counted minutes —
the AFK-machine / bot upper bound), 0.5 = offline accrual only (60 counted minutes a day).

Implementation: wraps p1sim.Player.bounty (called once per settled clear, after loops set p.day) so p1sim, p2econ and
every route get it with no edits to those files. A day is paid once, when p.day first exceeds the highest day already
paid; gaps of up to 7 days (days without a clear) are paid too, a larger jump (p2econ phase 1 → phase 2 day numbering)
pays one day. Days with no settled clear at the very end are not paid (≤ 1 day undercount per player).

Usage (repo root):
  python3 tools/p1sim/afk.py dyn  [--players 800] [--share 1.0] [--days 60]     # 21 cells Q01–Q07 × dodge 0.3/0.5/0.7
  python3 tools/p1sim/afk.py busy [--players 400] [--runs 1]                    # 1 run/day + AFK vs 3 runs/day, no AFK
  python3 tools/p1sim/afk.py p2econ --share 1.0 -- --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals --every-week
  python3 tools/p1sim/growthrun.py weeks base.md afk.md                         # W30 compare
"""
import argparse, os, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

# (required first clear, coin cap / day, xp cap / day) — mirrors config afk_p1.tiers (D177 §3)
# = 12 rounds × (8/3, 12/5, 16/6, 25/10) per round (ember-v1.yml afk.tiers)
TIERS = [('q01', 96, 36), ('q03', 144, 60), ('q05', 192, 72), ('q07', 300, 120)]
STATE = {'share': 0.0, 'paid_days': 0, 'coin': 0, 'xp': 0}


def tier_of(cleared):
    best = None
    for t in TIERS:
        if t[0] in cleared:
            best = t
    return best


def pay_days(p, n):
    t = tier_of(p.cleared)
    if t is None or n <= 0 or STATE['share'] <= 0:
        return
    c, x = int(round(t[1] * STATE['share'])) * n, int(round(t[2] * STATE['share'])) * n
    p.coin += c
    p.xp += x
    STATE['paid_days'] += n; STATE['coin'] += c; STATE['xp'] += x


def install(share):
    import p1sim
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
    print('# afk dyn: players=%d seed0=%d days=%d share=%.2f tiers=%s\n' % (a.players, a.seed0, a.days, a.share, TIERS))
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
    print('\n**%d/%d 格在 ±2pp 内**（最大 |差| %.1f pp）· 挂机付费天数 %d，合计余烬币 %d、经验 %d' % (
        inside, cells, worst, STATE['paid_days'], STATE['coin'], STATE['xp']))


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
    print('\n# afk share=%.2f paid_days=%d coin=%d xp=%d' % (a.share, STATE['paid_days'], STATE['coin'], STATE['xp']))


if __name__ == '__main__':
    argv = sys.argv[1:]
    rest = []
    if '--' in argv:
        i = argv.index('--'); rest = argv[i + 1:]; argv = argv[:i]
    ap = argparse.ArgumentParser()
    ap.add_argument('tool', choices=['dyn', 'busy', 'p2econ'])
    ap.add_argument('--players', type=int, default=800)
    ap.add_argument('--days', type=int, default=60)
    ap.add_argument('--share', type=float, default=1.0)
    ap.add_argument('--runs', type=int, default=1)
    ap.add_argument('--dodge', type=float, nargs='*', default=[0.3, 0.5, 0.7])
    ap.add_argument('--seed0', type=int, default=1000, help='first player seed (p1sim default 1000; hold-out replicate e.g. 50000)')
    ap.add_argument('--tiers', help='JSON [[flag, coin, xp], …] instead of TIERS (tuning)')
    a = ap.parse_args(argv)
    if a.tiers:
        import json
        TIERS[:] = [tuple(t) for t in json.loads(a.tiers)]
    {'dyn': lambda: dyn(a), 'busy': lambda: busy(a), 'p2econ': lambda: p2(a, rest)}[a.tool]()

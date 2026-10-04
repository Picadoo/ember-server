"""D180 P1 每日签到 + 在线时长奖励 economy check — docs/design/DESIGN-ember-signin-online-2026-10-04.md §7.

Reads the SHIPPED tables from plugins/CoreRpg/ember-v1.yml (signin: / online:) so the sim never drifts from the config.
Paid per stamina day (wraps p1sim.Player.bounty exactly like afk.py, so p1sim, p2econ and every route get it):
  * sign-in: the n-th sign-in of a 30-day month pays signin.special[n] or signin.daily (n > 28 → signin.extra);
    with --sign-share s a day is signed with probability s (deterministic per player/day, own rng → paired streams);
    make-ups are modelled as part of the share (upper bound s = 1.0: every day signed).
  * online: the milestones with min <= --online (counted active minutes / day outside the AFK world), every paid day.
  * mark → p.marks[tier of the highest first-cleared map] (exchanged by the normal p1sim policy); sigmark (首领徽记) is
    counted only (p1sim has no imprint model — covered by the D174 'signatures worn from day 1' W30 upper bound);
    sigmark before any signature-map clear → signin.sigmark_fallback_coin.
Optional --afk DIR also installs the AFK lane's afk.py from DIR (read-only import) at --afk-share, so the W30 check
covers sign-in + online + AFK together.

Usage (repo root):
  python3 tools/p1sim/signin.py table                                            # what a month / a day pays
  python3 tools/p1sim/signin.py dyn [--players 800] [--online 120] [--sign-share 1.0]   # 21 cells ±2pp
  python3 tools/p1sim/signin.py p2econ [--online 120] [--afk /workspace/afk-wt2/tools/p1sim --afk-share 0.5] \
      -- --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals --every-week
  python3 tools/p1sim/growthrun.py weeks base.md signin.md                       # W30 compare
"""
import argparse, importlib.util, os, random, sys
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import miniyaml  # noqa: E402

CFG_PATH = os.path.join(HERE, '..', '..', 'plugins', 'CoreRpg', 'ember-v1.yml')
STATE = {'on': False, 'online': 120, 'sign_share': 1.0, 'paid_days': 0, 'signed': 0, 'coin': 0, 'xp': 0, 'mark': 0, 'sigmark': 0}
SIG_MAPS = ('q01', 'q02', 'q03')  # D174 stage 1 signature maps (live); Q04–Q06 come in stage 2a
T = {}


def load(path=CFG_PATH):
    c = miniyaml.load(path)
    s, o = c.get('signin') or {}, c.get('online') or {}
    T['daily'] = s.get('daily') or {}
    T['special'] = {int(k): v for k, v in (s.get('special') or {}).items()}
    T['extra'] = s.get('extra') or {}
    T['fallback'] = int(s.get('sigmark_fallback_coin', 0))
    T['milestones'] = sorted(o.get('milestones') or [], key=lambda m: m['min'])
    return T


def sign_row(n):
    if n in T['special']:
        return T['special'][n]
    return T['extra'] if n > 28 else T['daily']


def online_rows(minutes):
    return [m for m in T['milestones'] if m['min'] <= minutes]


def month_total():
    tot = {}
    for n in range(1, 31):
        for k, v in sign_row(n).items():
            tot[k] = tot.get(k, 0) + v
    return tot


def _pay(p, row):
    c = int(row.get('coin', 0)); x = int(row.get('xp', 0))
    if row.get('sigmark'):
        if any(k in p.cleared for k in SIG_MAPS):
            STATE['sigmark'] += int(row['sigmark'])
        else:
            c += T['fallback']
    if row.get('mark'):
        tiers = [p.cfg['maps'][k]['tier'] for k in p.cleared if k in p.cfg['maps']]
        t = max(tiers) if tiers else 1
        p.marks[t] += int(row['mark'])
        STATE['mark'] += int(row['mark'])
    p.coin += c; p.xp += x
    STATE['coin'] += c; STATE['xp'] += x


def pay_day(p, day):
    if not hasattr(p, '_sg_id'):  # creation order is deterministic → stable per-player id without touching p.rng
        STATE['ids'] = STATE.get('ids', 0) + 1
        p._sg_id = STATE['ids']
    rng = random.Random('%d-%d-signin' % (p._sg_id, day))
    if STATE['sign_share'] >= 1.0 or rng.random() < STATE['sign_share']:
        if getattr(p, '_sg_month', None) != (day - 1) // 30:
            p._sg_month, p._sg_n = (day - 1) // 30, 0
        p._sg_n += 1
        _pay(p, sign_row(p._sg_n))
        STATE['signed'] += 1
    for m in online_rows(STATE['online']):
        _pay(p, m)
    STATE['paid_days'] += 1


def install(online=120, sign_share=1.0):
    import p1sim
    load()
    STATE.update(on=True, online=online, sign_share=sign_share)
    if getattr(p1sim.Player, '_signin_installed', False):
        return
    orig = p1sim.Player.bounty

    def bounty(self):
        if self.day is not None and STATE['on']:
            last = getattr(self, '_sg_max', None)
            if last is None:
                self._sg_max = self.day
                pay_day(self, self.day)
            elif self.day > last:
                gap = self.day - last
                for d in (range(last + 1, self.day + 1) if gap <= 7 else [self.day]):
                    pay_day(self, d)
                self._sg_max = self.day
        return orig(self)
    p1sim.Player.bounty = bounty
    p1sim.Player._signin_installed = True


def install_afk(path, share):
    if not path or share <= 0:
        return None
    spec = importlib.util.spec_from_file_location('afk_lane', os.path.join(path, 'afk.py'))
    m = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(m)
    m.install(share)
    return m


def table(a):
    load()
    print('# D180 tables from %s' % os.path.relpath(CFG_PATH))
    print('\n| 第 n 次签到 | 奖励 |\n|---|---|')
    for n in range(1, 32):
        print('| %d | %s |' % (n, ' · '.join('%s %s' % (k, v) for k, v in sign_row(n).items())))
    print('\n30 次合计：%s' % month_total())
    print('\n| 在线分钟 | 奖励 |\n|---|---|')
    for m in T['milestones']:
        print('| %d | %s |' % (m['min'], ' · '.join('%s %s' % (k, v) for k, v in m.items() if k != 'min')))
    day = {}
    for m in T['milestones']:
        for k, v in m.items():
            if k != 'min':
                day[k] = day.get(k, 0) + v
    print('\n在线满 120 分钟合计 / 天：%s' % day)


def dyn(a):
    import p1sim, p1config
    cfg = p1config.load()
    print('# ' + __import__('rules').stamp())
    per_day = cfg['stamina_day'] // cfg['run_cost']
    print('# signin dyn: players=%d seed0=%d days=%d online=%d sign_share=%.2f\n' % (a.players, a.seed0, a.days, a.online, a.sign_share))
    print('| 躲避 | 图 | 首通前通关率 基线 | +签到在线 | 差 pp | 首通中位天 基线 | +签到在线 | P90 基线 | +签到在线 | Lv 基线/新 |')
    print('|---|---|---:|---:|---:|---:|---:|---:|---:|---|')
    inside = cells = 0
    worst = 0.0
    for d in a.dodge:
        kn = p1sim.Knobs(d)
        STATE['on'] = False
        base, _ = p1sim.summarize(cfg, kn, a.players, a.days * per_day, seed0=a.seed0)
        install(a.online, a.sign_share)
        new, _ = p1sim.summarize(cfg, kn, a.players, a.days * per_day, seed0=a.seed0)
        STATE['on'] = False
        for b, n in zip(base, new):
            fr0, fr1 = b['front_rate'], n['front_rate']
            dpp = None if fr0 is None or fr1 is None else 100 * (fr1 - fr0)
            cells += 1
            if dpp is not None and abs(dpp) <= 2.0:
                inside += 1
            if dpp is not None:
                worst = max(worst, abs(dpp))
            f = lambda x, s='%.1f': '—' if x is None else s % x
            print('| %.1f | %s | %s | %s | %s | %s | %s | %s | %s | %s / %s |' % (
                d, b['map'].upper(), f(fr0 and 100 * fr0), f(fr1 and 100 * fr1), f(dpp, '%+.1f'),
                f(b['fc_day'], '%d'), f(n['fc_day'], '%d'), f(b['fc_day_p90'], '%d'), f(n['fc_day_p90'], '%d'),
                f(b['lv'], '%d'), f(n['lv'], '%d')))
    print('\n**%d/%d 格在 ±2pp 内**（最大 |差| %.1f pp）· 付费天 %d、签到 %d 次，合计余烬币 %d、经验 %d、印记 %d、徽记 %d' % (
        inside, cells, worst, STATE['paid_days'], STATE['signed'], STATE['coin'], STATE['xp'], STATE['mark'], STATE['sigmark']))


def p2(a, rest):
    if a.online >= 0:
        install(a.online, a.sign_share)
    afk = install_afk(a.afk, a.afk_share)
    import p2econ
    sys.argv = ['p2econ'] + rest
    p2econ.main()
    print('\n# signin online=%d sign_share=%.2f paid_days=%d signed=%d coin=%d xp=%d mark=%d sigmark=%d' % (
        a.online, a.sign_share, STATE['paid_days'], STATE['signed'], STATE['coin'], STATE['xp'], STATE['mark'], STATE['sigmark']))
    if afk is not None:
        print('# afk lane %s share=%.2f state=%s' % (a.afk, a.afk_share, {k: v for k, v in afk.STATE.items()}))


if __name__ == '__main__':
    argv = sys.argv[1:]
    rest = []
    if '--' in argv:
        i = argv.index('--'); rest = argv[i + 1:]; argv = argv[:i]
    ap = argparse.ArgumentParser()
    ap.add_argument('tool', choices=['table', 'dyn', 'p2econ'])
    ap.add_argument('--players', type=int, default=800)
    ap.add_argument('--days', type=int, default=60)
    ap.add_argument('--online', type=int, default=120, help='counted active minutes / day (-1 = sign-in + online off, e.g. AFK-only run)')
    ap.add_argument('--sign-share', type=float, default=1.0)
    ap.add_argument('--dodge', type=float, nargs='*', default=[0.3, 0.5, 0.7])
    ap.add_argument('--seed0', type=int, default=1000)
    ap.add_argument('--afk', help='directory holding the AFK lane afk.py to install too (read-only import)')
    ap.add_argument('--afk-share', type=float, default=0.0)
    a = ap.parse_args(argv)
    {'table': lambda: table(a), 'dyn': lambda: dyn(a), 'p2econ': lambda: p2(a, rest)}[a.tool]()

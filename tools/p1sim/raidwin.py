"""D195 团本破绽 gate: p1party paired runs, RAID_STUN False (live 1.65.32: raid bosses have no stagger window) vs True
(as shipped: R01 冲撞 wall_stun 1.5, R02 砸地 / R03 斧刃横扫 whiff_stun 0.5), same pools / line-ups / entry streams.
Pass = every |Δ clear rate| <= 3 pp (cap 3.0, prefer <= 2.0). Usage (repo root):
  python3 tools/p1sim/raidwin.py [--trials 300] [--pool 24] [--weeks 2] [--dodge 0.3 0.5 0.7] [--upper]
--upper: every charge ends in a wall (p1sim.WALL_STUN_P = 1.0, informational).
The baseline also restores the live 1.65.32 damage of the three moves (REVERT); the shipped yml carries the offsets.
Standard library only."""
import argparse, os, random, statistics, sys
sys.path.insert(0, os.path.dirname(__file__))
import p1config, p1party, p1sim, rules

ap = argparse.ArgumentParser()
ap.add_argument('--trials', type=int, default=300)
ap.add_argument('--pool', type=int, default=24)
ap.add_argument('--weeks', type=int, default=2)
ap.add_argument('--dodge', type=float, nargs='*', default=[0.3, 0.5, 0.7])
ap.add_argument('--upper', action='store_true')
ap.add_argument('--raids', nargs='*', default=['r01', 'r02', 'r03'])
ap.add_argument('--dmg', nargs='*', default=[], help='offset sweep on the NEW side only: raid:skill:dmg (e.g. r01:冲撞:88)')
a = ap.parse_args()
# live 1.65.32 values D195 changed besides the stagger keys, restored on the baseline side
REVERT = {('r01', '冲撞'): 79, ('r02', '砸地'): 79, ('r03', '斧刃横扫'): 79}
if a.upper:
    p1sim.WALL_STUN_P = 1.0
print('# ' + rules.stamp(), flush=True)
cfg = p1config.load()
rr = rules.runs().get('raid_revive') or {}
p1party.LAST_REVIVE_HP = float(rr.get('last_phase_hp', 0)); p1party.LAST_REVIVE_DELAY = float(rr.get('delay', 10))
print('# new-side dmg overrides: %s' % (' '.join(a.dmg) or 'none'))
print('# raidwin D195: trials %d per size, pool %d (Q07 + %d weeks), WALL_STUN_K %.2f%s' % (
    a.trials, a.pool, a.weeks, p1sim.WALL_STUN_K, ' (--upper: WALL_STUN_P 1.0)' if a.upper else ''))
print('| 团本 | 躲避 | 人数 | 通关率 旧 | 通关率 新 | Δ pp | 首领用时 旧 s | 首领用时 新 s | 倒下 旧 | 倒下 新 |')
print('|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|')
mx = 0.0
for d in a.dodge:
    pool = p1party.player_pool(cfg, a.pool, a.weeks, d)
    for k in a.raids:
        m = p1party.raid_map(cfg, k)
        mn = __import__('copy').deepcopy(m)
        for o in a.dmg:
            rk, sn, dv = o.split(':')
            if rk == k:
                hit = [s for s in mn['boss']['skills'] if s['name'] == sn]
                assert hit, o
                hit[0]['dmg'] = float(dv)
        assert any(s.get('wall_stun') or s.get('whiff_stun') for s in m['boss']['skills']), k + ' has no D195 window'
        mb = __import__('copy').deepcopy(m)
        for sk in mb['boss']['skills']:
            if (k, sk['name']) in REVERT:
                sk['dmg'] = REVERT[(k, sk['name'])]
        p1party.RAID_STUN = False
        base = p1party.evaluate(cfg, mb, pool, (3, 4, 5), a.trials, random.Random(11))
        p1party.RAID_STUN = True
        new = p1party.evaluate(cfg, mn, pool, (3, 4, 5), a.trials, random.Random(11))
        for n in (3, 4, 5):
            b, v = base[n], new[n]
            assert b['lineup'] == v['lineup']
            dp = 100 * (v['rate'] - b['rate'])
            mx = max(mx, abs(dp))
            f = lambda x: '—' if x is None else '%.0f' % x
            print('| %s | %.1f | %d | %d%% | %d%% | %+.1f | %s | %s | %.2f | %.2f |' % (
                k, d, n, round(100 * b['rate']), round(100 * v['rate']), dp, f(b['boss_secs']), f(v['boss_secs']), b['deaths'], v['deaths']), flush=True)
print('\nMAX_ABS_DPP = %.1f (cap 3.0, prefer <= 2.0)' % mx)

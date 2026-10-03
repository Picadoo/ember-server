"""D144 余烬连战 (boss rush): Q05 → Q06 → Q07 bosses back-to-back (their D140 moves), one fight, short breather +
heal between bosses. Reads the real runs yml `rush:` block (chain / hp / dmg / break_secs / heal) through p1config.
Usage (repo root):
  python3 tools/p1sim/rushsim.py                       # table: gear × dodge, clear rate + median time
  python3 tools/p1sim/rushsim.py --hp 1.3 --dmg 1.25   # tuning override
Also imported by p2econ (rush_rate) for the weekly rush slot.
"""
import argparse, copy, os, random, statistics, sys
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config, p1sim, miniyaml

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..')


def rush_conf():
    r = miniyaml.load(os.path.join(ROOT, 'CoreRpg/src/main/resources/ember-v1-runs.yml')).get('rush') or {}
    out = {}
    for k, v in r.items():
        if isinstance(v, dict) and v.get('chain'):
            out = {'key': k, 'chain': list(v['chain']), 'hp': float(v.get('boss_hp', 1.0)), 'dmg': float(v.get('boss_dmg', 1.0)),
                   'break': float(v.get('break_secs', 10)), 'heal': float(v.get('heal', 0.3)), 'marks': int((v.get('reward') or {}).get('marks', 0))}
            break
    return out


def run_rush(cfg, rc, st, kn, rng, potions):
    """One rush entry. Returns (cleared, seconds, potions used, bosses killed)."""
    f = p1sim.Fight(cfg, st, kn, rng, potions)
    for i, key in enumerate(rc['chain']):
        md = copy.deepcopy(cfg['maps'][key])
        b = md['boss']
        b['hp'] = float(b['hp']) * rc['hp']
        b['atk'] = b['atk'] * rc['dmg']
        for s in b.get('skills', []):
            s['dmg'] = s['dmg'] * rc['dmg']
            if s.get('follow'):
                s['follow']['dmg'] = s['follow']['dmg'] * rc['dmg']
        if b.get('adds'):
            md['mobs'] = dict(md['mobs'])
            for r in md['mobs']:
                md['mobs'][r] = dict(md['mobs'][r], atk=md['mobs'][r]['atk'] * rc['dmg'])
        boss = {'hp': b['hp'], 'max': b['hp'], 'atk': b['atk'], 'iv': b.get('interval', 3.0), 'role': 'boss',
                'next': f.t + 2.0, 'tele': False, 'burn': 0.0}
        if not f.segment([boss], boss=boss, mapdef=md):
            return False, f.t, f.used, i
        if i < len(rc['chain']) - 1:
            f.t += rc['break']
            f.hp = min(st['H'], f.hp + rc['heal'] * st['H'])
    return True, f.t, f.used, len(rc['chain'])


def rush_rate(cfg, rc, st, kn, n, seed=7):
    rng = random.Random(seed)
    return sum(run_rush(cfg, rc, st, kn, rng, kn.potion_keep)[0] for _ in range(n)) / n


GEAR = [('Q07 首通参考 T2+8', (2, 8, 2, 8, 30)), ('T3+3 两件', (3, 3, 3, 3, 30)), ('T3+6 两件', (3, 6, 3, 6, 30)), ('T3+9 两件', (3, 9, 3, 9, 30))]


def main(argv=None):
    ap = argparse.ArgumentParser()
    ap.add_argument('--hp', type=float)
    ap.add_argument('--dmg', type=float)
    ap.add_argument('--n', type=int, default=2000)
    a = ap.parse_args(argv)
    cfg = p1config.load('current')
    rc = rush_conf()
    if not rc:
        print('no rush block in the runs yml'); return
    if a.hp: rc['hp'] = a.hp
    if a.dmg: rc['dmg'] = a.dmg
    print('# rushsim: %s chain %s · boss hp ×%.2f dmg ×%.2f · break %.0f s heal %d%% · %d runs per cell (solo)' % (
        rc['key'], '→'.join(rc['chain']), rc['hp'], rc['dmg'], rc['break'], round(rc['heal'] * 100), a.n))
    print('| 装备 | 躲避 0.3 | 躲避 0.5 | 躲避 0.7 | 通关用时中位（0.5） | 倒在第几个首领（0.5，均值） |')
    print('|---|---:|---:|---:|---:|---:|')
    for label, (bt, be, ct, ce, lv) in GEAR:
        st = p1sim.stats(cfg, dict(p1sim.item('burst', 'blade', bt), enh=be), dict(p1sim.item('burst', 'charm', ct), enh=ce), lv)
        cells, times, dead = [], [], []
        for d in (0.3, 0.5, 0.7):
            rng = random.Random(17)
            ok = 0
            for _ in range(a.n):
                c, t, u, k = run_rush(cfg, rc, st, p1sim.Knobs(d), rng, 5)
                ok += c
                if d == 0.5:
                    (times if c else dead).append(t if c else k + 1)
            cells.append('%d%%' % round(100 * ok / a.n))
        print('| %s | %s | %s | %s |' % (label, ' | '.join(cells), ('%d 秒' % statistics.median(times)) if times else '—',
                                         ('%.1f' % statistics.mean(dead)) if dead else '—'))


if __name__ == '__main__':
    main()

#!/usr/bin/env python3
"""Tuning search helper (proposal only, never writes configs): apply an override set in memory, run the dodge-0.5
pacing sim, compare with the book target day. Usage: python3 tune.py [--players N] [--set NAME]"""
import argparse
import copy
import math
import sys

import p1config
import p1sim


def book_target(cfg, enh=7):
    """Book-implied Q07 day: stamina days of normal clears (§9.6 income, 100% clear) needed to own the §3.2 Q07
    reference loadout (T2 two-piece, both +enh), counting the §9.4 first-clear packages. Binding resource wins."""
    need = {'coin': 0.0, 'shard': 0.0, 'core': 0.0}
    for t in range(1, enh + 1):
        r, n = cfg['enh_rate'][t], cfg['enh_max'][t]
        e = sum((1 - r) ** (k - 1) * r * k for k in range(1, n)) + (1 - r) ** (n - 1) * n
        need['coin'] += 2 * e * cfg['enh_coin'][t]
        need['shard'] += 2 * e * cfg['enh_shard'][t]
        need['core'] += 2 * e * cfg['enh_core'][t]
    up = cfg['upgrade'][1]
    for k in need:
        need[k] += 2 * up[k]
    for k in cfg['order']:
        fc = cfg['maps'][k].get('first_clear') or {}
        for res in need:
            need[res] -= fc.get(res, 0)
    w = cfg['extra_w']
    per = {'coin': cfg['base']['coin'] + cfg['treasure_coin'] * w[1] / sum(w) - 3 * cfg['potion_price'],  # ~3 potions / run
           'shard': cfg['base']['shard'] + cfg['elite_shard'] * w[2] / sum(w),
           'core': cfg['base']['core'] + cfg['elite_core'] * w[2] / sum(w)}
    runs = {k: max(0.0, need[k]) / per[k] for k in need}
    per_day = cfg['stamina_day'] // cfg['run_cost']
    worst = max(runs, key=runs.get)
    return runs[worst] / per_day, worst, runs, need


def apply(cfg, ov):
    cfg = copy.deepcopy(cfg)
    for path, v in ov.items():
        parts = path.split('.')
        node = cfg if parts[0] in ('base',) else cfg['maps']
        for p in parts[:-1]:
            node = node[int(p)] if isinstance(node, list) else node[p]
        last = parts[-1]
        if isinstance(node, list):
            node[int(last)] = v
        else:
            node[last] = v
    return cfg


def scale(cfg, k, hp=1.0, atk=1.0, boss_hp=None, boss_atk=None):
    """Override dict scaling one map's mob HP / attack (integers, like the configs)."""
    ov = {}
    m = cfg['maps'][k]
    for role, mob in m['mobs'].items():
        if role == 'treasure':
            continue
        ov['%s.mobs.%s.hp' % (k, role)] = int(round(mob['hp'] * hp))
        ov['%s.mobs.%s.atk' % (k, role)] = int(round(mob['atk'] * atk))
    ov['%s.boss.hp' % k] = int(round(m['boss']['hp'] * (boss_hp if boss_hp is not None else hp) / 10.0) * 10)
    ov['%s.boss.atk' % k] = int(round(m['boss']['atk'] * (boss_atk if boss_atk is not None else atk)))
    if boss_atk is not None or atk != 1.0:
        for i, sk in enumerate(m['boss'].get('skills', [])):
            ov['%s.boss.skills.%d.dmg' % (k, i)] = int(round(sk['dmg'] * (boss_atk if boss_atk is not None else atk)))
            if sk.get('follow'):
                ov['%s.boss.skills.%d.follow.dmg' % (k, i)] = int(round(sk['follow']['dmg'] * (boss_atk if boss_atk is not None else atk)))
    return ov


def run(cfg, players, dodge=0.5, days=60):
    kn = p1sim.Knobs(dodge)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    rows, _ = p1sim.summarize(cfg, kn, players, days * per_day)
    return rows


def show(rows):
    out = []
    for r in rows:
        out.append('%s reach %3d%% front %s fc_day %s/%s att %s' % (
            r['map'], round(100 * r['reach']), '%3d%%' % round(100 * r['front_rate']) if r['front_rate'] else '  — ',
            p1sim.fmt(r['fc_day']), p1sim.fmt(r['fc_day_p90']), p1sim.fmt(r['attempts'])))
    return '\n'.join(out)


if __name__ == '__main__':
    ap = argparse.ArgumentParser()
    ap.add_argument('--players', type=int, default=60)
    a = ap.parse_args()
    cfg = p1config.load()
    d, worst, runs, need = book_target(cfg)
    print('book target Q07 day ~%.1f (binding %s; runs %s; need %s)' % (d, worst, {k: round(v, 1) for k, v in runs.items()},
                                                                     {k: round(v) for k, v in need.items()}))

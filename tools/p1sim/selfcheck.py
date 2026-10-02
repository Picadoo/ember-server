#!/usr/bin/env python3
"""Unit-ish self-check: config loading, formulas against the book's worked numbers, and simulator sanity."""
import random
import sys

import miniyaml
import p1config
import p1sim

fails = []


def check(name, ok, detail=''):
    print(('PASS ' if ok else 'FAIL ') + name + ((' — ' + detail) if detail else ''))
    if not ok:
        fails.append(name)


def near(a, b, tol=1e-6):
    return abs(a - b) <= tol


# 1. mini YAML: multi-line flow maps, sequences of maps, comments
y = miniyaml.loads("""a: 1  # c
b:
  - {x: 1, y: [1, 2],
     z: {w: "q#r"}}
  - k: v
    n: 2
c: [a, 'b c']
""")
check('miniyaml subset', y == {'a': 1, 'b': [{'x': 1, 'y': [1, 2], 'z': {'w': 'q#r'}}, {'k': 'v', 'n': 2}], 'c': ['a', 'b c']}, str(y))

cfg = p1config.load()
check('config loads, 7 maps in unlock order', cfg['order'] == ['q01', 'q02', 'q03', 'q04', 'q05', 'q06', 'q07'], str(cfg['order']))
check('runs yml copies + MM Health agree', not cfg['warnings'], '; '.join(cfg['warnings']))

# 2. §7.4 reference character: T2, Lv30, both +6, standard, no craft → B 58.6, H 163.5, EHP 204.375
it = lambda slot, fam='burst': dict(p1sim.item(fam, slot, 2), enh=6)
st = p1sim.stats(cfg, it('blade'), it('charm'), 30)
check('§7.4 B = 58.6', near(st['B'], 58.6), '%.4f' % st['B'])
check('§7.4 H = 163.5', near(st['H'], 163.5), '%.4f' % st['H'])
check('§7.4 EHP = 204.375', near(st['H'] / st['M'], 204.375), '%.4f' % (st['H'] / st['M']))
st2 = p1sim.stats(cfg, it('blade', 'sustain'), it('charm', 'sustain'), 30)
check('§7.4 炽愈 H = 183.12', near(st2['H'], 183.12), '%.4f' % st2['H'])
check('awakening II at T2 +6/+6', st['awk'] == 2)
check('awakening I at T2 +6/+5', p1sim.awakening(it('blade'), dict(it('charm'), enh=5)) == 1)
check('mixed family = no set', p1sim.stats(cfg, it('blade'), it('charm', 'scorch'), 30)['set'] is None)

# 3. §6.2 enhance +0→+10 expectation from the parsed table: ≈22.784 tries, ≈12,984.12 coins
tries = coins = 0.0
for t in range(1, 11):
    r, n = cfg['enh_rate'][t], cfg['enh_max'][t]
    e = sum((1 - r) ** (k - 1) * r * k for k in range(1, n)) + (1 - r) ** (n - 1) * n
    tries += e
    coins += e * cfg['enh_coin'][t]
check('§6.2 expected tries 22.784', near(tries, 22.784, 0.001), '%.4f' % tries)
check('§6.2 expected coins 12,984.12', near(coins, 12984.12, 0.05), '%.2f' % coins)

# 4. §9.3 expected coin per normal clear = 315
w = cfg['extra_w']
ev = cfg['base']['coin'] + cfg['treasure_coin'] * w[1] / sum(w)
check('§9.3 expected coin per clear 315', near(ev, 315), '%.1f' % ev)

# 5. level curve: 10 at 0 xp, Lv20 after 1140 xp (progress.yml curve)
check('level curve', p1sim.level_of(cfg, 0) == 10 and p1sim.level_of(cfg, 1140) == 20 and p1sim.level_of(cfg, 1139) == 19)

# 6. simulator sanity: deterministic, monotone in dodge, gear helps
a = p1sim.simulate_player(cfg, p1sim.Knobs(0.5), 42, 60)
b = p1sim.simulate_player(cfg, p1sim.Knobs(0.5), 42, 60)
check('deterministic with a seed', a == b)
lo = p1sim.clear_rate(cfg, 'q03', st, p1sim.Knobs(0.2), 300)
hi = p1sim.clear_rate(cfg, 'q03', st, p1sim.Knobs(0.7), 300)

t0 = p1sim.stats(cfg, p1sim.item('none', 'blade', 0), p1sim.item('none', 'charm', 0), 10)
weak = p1sim.clear_rate(cfg, 'q02', t0, p1sim.Knobs(0.5), 300)
strong = p1sim.clear_rate(cfg, 'q02', st, p1sim.Knobs(0.5), 300)
check('T2+6 clears Q02 more often than T0', strong > weak, '%.2f vs %.2f' % (weak, strong))
check('T2+6 at dodge 0.5 nearly always clears Q01', p1sim.clear_rate(cfg, 'q01', st, p1sim.Knobs(0.5), 200) > 0.95)
v2 = p1config.load('v2')
check('v2 profile restores pre-D31 numbers', v2['maps']['q03']['boss']['hp'] == 1300 and v2['death_refund'] == 0
      and v2['maps']['q04']['first_clear']['coin'] == 900)


# B2.178 图录 · 主线首领 (TrMenu ember_p1_codex): the numbers written in the menu must match the configs
import os
import re
codex = open(os.path.join(p1config.ROOT, 'plugins/TrMenu/menus/ember_p1_codex.yml'), encoding='utf-8').read()
bad = []
for i, key in enumerate(cfg['order'], 1):
    mblk = re.search(r"\n  '%d':\n(.*?)(?=\n  \S)" % i, codex, re.S)
    if not mblk:
        bad.append('%s: no icon' % key)
        continue
    txt = mblk.group(1)
    b = cfg['maps'][key]['boss']
    want = ['生命 §f%s' % b['hp'], '普攻 §f%s' % b['atk'], '≥%g 秒' % b['interval']]
    for sk in b.get('skills', []):
        for s2 in (sk, sk.get('follow')):
            if not s2:
                continue
            if '·' not in s2['name']:
                want.append('§c%s§7' % s2['name'])
            if s2.get('every'):
                want.append('每 %g 秒' % s2['every'])
            want += ['预警 %.1f 秒' % s2['warn'], '伤害 %s' % s2['dmg']]
    bad += ['%s: missing %r' % (key, w) for w in want if w not in txt]
check('codex menu numbers match MM + runs yml', not bad, '; '.join(bad))

print('\n%d failed' % len(fails))
sys.exit(1 if fails else 0)

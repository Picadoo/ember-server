#!/usr/bin/env python3
"""Unit-ish self-check: config loading, formulas against the book's worked numbers, and simulator sanity."""
import copy
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


# 7. review M01 / M02 / M03 / M06 (docs/reviews/review-gpt-comprehensive-2026-10-04.md §04)
import ast
import shutil
import subprocess
import tempfile
import p1party
import burnbook
import rules

HERE = os.path.dirname(os.path.abspath(__file__))
SIM = [x for x in sorted(os.listdir(HERE)) if x.endswith('.py') and x != 'selfcheck.py']
banned = []
for fn in SIM:
    tree = ast.parse(open(os.path.join(HERE, fn), encoding='utf-8').read())
    for node in ast.walk(tree):
        if isinstance(node, ast.ImportFrom) and node.module == 'random' and any(a.name != 'Random' for a in node.names):
            banned.append('%s:%d from random import %s' % (fn, node.lineno, ','.join(a.name for a in node.names)))
        if isinstance(node, ast.Call) and isinstance(node.func, ast.Attribute) and node.func.attr != 'Random':
            v = node.func.value
            if (isinstance(v, ast.Name) and v.id == 'random') or (isinstance(v, ast.Attribute) and v.attr == 'random'):
                banned.append('%s:%d random.%s()' % (fn, node.lineno, node.func.attr))
check('M01 no module-level random.* in sim code (only random.Random(seed) instances)', not banned, '; '.join(banned))
raw = []
for fn in SIM:
    if fn in ('rules.py', 'miniyaml.py', 'growth.py', 'apply_2c.py', 'simfix_check.py'):  # simfix_check: pre/post harness, must also run on the pre-fix tree (no rules.py); it reads rules.py when present
        continue  # rules.py = the one reader; growth.load(path) = explicit what-if file; apply_2c = a config WRITER
    for i, line in enumerate(open(os.path.join(HERE, fn), encoding='utf-8'), 1):
        if 'miniyaml.load(' in line and not line.lstrip().startswith('#'):
            raw.append('%s:%d' % (fn, i))
check('M06 no sim reads a rule file directly (all via rules.py)', not raw, ', '.join(raw))


def perturb(k):
    random.seed(k)
    for _ in range(k):
        random.random()


perturb(3); a = p1sim.simulate_player(cfg, p1sim.Knobs(0.5), 42, 80)
perturb(17); b = p1sim.simulate_player(cfg, p1sim.Knobs(0.5), 42, 80)
check('M01 same seed ⇒ same player trajectory, whatever the global random does', a == b)
pool = p1party.player_pool(cfg, 8, 1, 0.5)
rm = p1party.raid_map(cfg, 'r01')
perturb(5); e1 = p1party.evaluate(cfg, rm, pool, (3,), 60, random.Random(11))
perturb(23); e2 = p1party.evaluate(cfg, rm, pool, (3,), 60, random.Random(11))
check('M01 same seed ⇒ same raid entries (p1party.Member takes the entry rng)', e1[3]['wins'] == e2[3]['wins'] and e1[3]['secs'] == e2[3]['secs'])
r1 = random.Random(5); p1sim.run_map(cfg, 'q03', st, p1sim.Knobs(0.2), r1, 3)
r2 = random.Random(5); [r2.getrandbits(64) for _ in range(4)]
r3 = random.Random(5); p1sim.run_map(cfg, 'q03', st, p1sim.Knobs(0.9), r3, 3)
check('M02 one entry advances the caller rng by exactly 4 draws (whatever the fight did)', r1.getstate() == r2.getstate() == r3.getstate())
check('M02 raid line-ups depend on (seed, size) only', p1party.rosters(99, 3, 40, 8) == p1party.rosters(99, 3, 40, 8) and p1party.rosters(99, 3, 40, 8) != p1party.rosters(98, 3, 40, 8))
weak_rm = copy.deepcopy(rm); weak_rm['boss']['hp'] *= 0.3
ew = p1party.evaluate(cfg, weak_rm, pool, (3,), 60, random.Random(11))  # a much easier boss: entries end early
check('M02 compared configs fight with identical line-ups (easier boss: same members in every entry)',
      ew[3]['lineup'] == e1[3]['lineup'] and ew[3]['rate'] >= e1[3]['rate'], '%.2f vs %.2f' % (e1[3]['rate'], ew[3]['rate']))

bk1, bk2 = burnbook.BurnBook(), burnbook.BurnBook()
bk1.ignite('T', 10, 0); bk2.ignite('T', 20, 0)
t500 = sum(x[1] for x in bk1.due(500) + bk2.due(500)); t1000 = sum(x[1] for x in bk1.due(1000) + bk2.due(1000))
check('M03 two 焚烬 players on one target: 0 at 500 ms, 10 + 20 at 1000 ms (review R03 / R04)', t500 == 0 and t1000 == 30, '%s / %s' % (t500, t1000))
jc = subprocess.run([sys.executable, os.path.join(HERE, 'javacheck', 'burncheck.py'), '--scripts', '200'], capture_output=True, text=True)
if jc.returncode == 2:
    print('SKIP M03 Java EmberBurnBook == burnbook.py (no javac): ' + jc.stdout.strip())
else:
    check('M03 Java EmberBurnBook == burnbook.py on identical event scripts', jc.returncode == 0, jc.stdout.strip().splitlines()[0] if jc.stdout else jc.stderr[-300:])
sc = dict(st, set='scorch'); two = [sc, dict(sc)]
kn2 = [p1sim.Knobs(0.5), p1sim.Knobs(0.5)]
P = p1party.Party(cfg, [p1party.Member(cfg, x, k, 0, random.Random(1)) for x, k in zip(two, kn2)], random.Random(2))
tgt = p1sim.mob(cfg, cfg['maps']['q03'], 'heavy', random.Random(3), kn2[0])
for mem in P.ms:
    p1sim.ignite(mem, tgt, 0.0)
check('M03 p1party keeps one burn per (player, target)', all(mem.burns.get(tgt['uid']) is not None for mem in P.ms))

s0 = rules.snapshot()
check('M06 canonical snapshot has a content hash and balance_version', len(s0['hash']) >= 64 and s0['balance_version'] is not None, rules.stamp())
tmp = tempfile.mkdtemp(prefix='rules-')
try:
    rels = [r for pair in rules.PAIRS.values() for r in pair] + list(rules.SINGLE.values()) + list(rules.JAVA.values())
    for r in rels:
        if os.path.exists(os.path.join(rules.ROOT, r)):
            os.makedirs(os.path.dirname(os.path.join(tmp, r)), exist_ok=True)
            shutil.copy(os.path.join(rules.ROOT, r), os.path.join(tmp, r))
    src = os.path.join(tmp, rules.PAIRS['runs'][1])
    txt = open(src, encoding='utf-8').read()
    open(src, 'w', encoding='utf-8').write(txt.replace('balance_version:', 'balance_version: 9999 #', 1))
    old_root = rules.ROOT
    rules.ROOT = tmp
    try:
        rules.build(); mism = False
    except rules.RuleError:
        mism = True
    finally:
        rules.ROOT = old_root
    check('M06 a plugins/ vs src rule difference is a hard error', mism)
    bad = copy.deepcopy(s0['data']); bad['growth']['talents']['nodes'][0]['mods'] = {'dmg_everything': 2.0}
    try:
        rules.validate(bad); unsup = False
    except rules.RuleError:
        unsup = True
    check('M06 an unsupported growth mod key is a hard error', unsup)
    ex = os.path.join(tmp, 'snap.json')
    subprocess.check_call([sys.executable, os.path.join(HERE, 'rules.py'), '--export', ex], stdout=subprocess.DEVNULL)
    pinned = rules.load_file(ex)
    check('M06 an exported snapshot re-loads type-exact (same hash, same parsed data)', pinned['hash'] == s0['hash']
          and rules._canon(pinned['data']) == rules._canon(s0['data']) and pinned['data']['runs'] == s0['data']['runs'])
finally:
    shutil.rmtree(tmp, ignore_errors=True)


# D244 / ARCH S4-3: affix numbers come from the exported table (EmberAffixExportTest), never re-derived by hand
import affixtable, copy as _copy
_at = affixtable.table()
check('D244 affix table covers the live pool', all(k in _at['affixes'] for k in cfg['variety']['affixes']), str(cfg['variety']['affixes']))
check('D244 rules: table inputs == live variety', not rules.affix_table_drift(_at, rules.runs()), '; '.join(rules.affix_table_drift(_at, rules.runs())))
_c2 = _copy.deepcopy(cfg); _c2['variety']['blazing']['every'] = float(_c2['variety']['blazing']['every']) + 1.0
try:
    affixtable.row(_c2, 'blazing'); _drift_caught = False
except rules.RuleError:
    _drift_caught = True
check('D244 a variety what-if without re-export is refused', _drift_caught)
_mobs = [{'role': 'heavy', 'hp': 100.0, 'atk': 10.0}]
_am = p1sim.affix_mob(cfg, None, _mobs, 'venom', 100.0)
_v = _at['affixes']['venom']
check('D244 affix_mob uses the table cadence', _am['blaze'] == (100.0 + _v['first_hit_s'], _v['period_s'], 10.0 * _v['dmg_atk']), str(_am['blaze']))

print('\n%d failed' % len(fails))
sys.exit(1 if fails else 0)

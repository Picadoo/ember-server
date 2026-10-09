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
# book §7.4: 183.12 = 163.5 × 1.12 (sustain_hp_mult at the time); D293 / bv59 set the multiplier to 1.00 → follow the config
check('§7.4 炽愈 H = 163.5 × sustain_hp (book 183.12 @1.12; now %.2f)' % cfg['sustain_hp'], near(st2['H'], 163.5 * cfg['sustain_hp']), '%.4f' % st2['H'])
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

# D247: five previously-unmodelled affixes now modelled; gate baseline sentinel `_plain`
check('D247 MODELLED covers regen/charge/frost/mortar/molten', all(k in affixtable.MODELLED for k in ('regen','charge','frost','mortar','molten')))
check('D247 charge/mortar are PERIODIC', all(k in affixtable.PERIODIC for k in ('charge','mortar')))
_mobs2 = [{'role': 'heavy', 'hp': 100.0, 'atk': 10.0}]
_am_c = p1sim.affix_mob(cfg, None, list(_mobs2), 'charge', 100.0)
_c = _at['affixes']['charge']
check('D247 charge uses table cadence', _am_c['blaze'] == (100.0 + _c['first_hit_s'], _c['period_s'], 10.0 * _c['dmg_atk']), str(_am_c.get('blaze')))
_mobs3 = [{'role': 'heavy', 'hp': 100.0, 'atk': 10.0}]
_am_f = p1sim.affix_mob(cfg, None, list(_mobs3), 'frost', 100.0)
check('D247 frost sets amplifier', _am_f.get('frost_amp') == float(_at['affixes']['frost']['amplifier']), str(_am_f.get('frost_amp')))
_mobs4 = [{'role': 'heavy', 'hp': 200.0, 'atk': 10.0}]
_am_r = p1sim.affix_mob(cfg, None, list(_mobs4), 'regen', 100.0)
_r = _at['affixes']['regen']
check('D247 regen arms channel', _am_r.get('max') == 200.0 and abs(_am_r['regen'][0] - (100.0 + _r['first_arm_s'] + _r['window_s'])) < 1e-9, str(_am_r.get('regen')))
_mobs5 = [{'role': 'heavy', 'hp': 100.0, 'atk': 10.0}]
_am_m = p1sim.affix_mob(cfg, None, list(_mobs5), 'molten', 100.0)
check('D247 molten schedules death blast', _am_m.get('molten') == (_at['affixes']['molten']['hit_after_death_s'], 10.0 * _at['affixes']['molten']['dmg_atk']), str(_am_m.get('molten')))
_mobs6 = [{'role': 'heavy', 'hp': 100.0, 'atk': 10.0}]
_am_p = p1sim.affix_mob(cfg, None, list(_mobs6), affixtable.PLAIN, 100.0)
check('D247 _plain is pressure-free', 'blaze' not in _am_p and 'frost_amp' not in _am_p and 'regen' not in _am_p and 'molten' not in _am_p and _am_p.get('affix'), str(_am_p))

# D316 (six-slot armor catch-up T0″): SIX['follow'] is opt-in — absent = the D313 weighted formula, bit for bit;
# 'all' with armor of the charm's quality / craft (any armor tier / enhance) = the 2-slot H / M / D, bit for bit
_six0 = p1sim.SIX
_W = [0.8, 0.05, 0.05, 0.05, 0.05]
_rf = random.Random(316)
_g = lambda it: 1 + cfg['e'][it['enh']] + cfg['q'][it['q']] + cfg['f'][it['f']]
_bad_def = _bad_all = 0
for _i in range(3000):
    _fam = _rf.choice(('burst', 'scorch', 'sustain'))
    _ch = dict(p1sim.item(_fam, 'charm', _rf.randrange(1, 4)), enh=_rf.randrange(11), q=_rf.randrange(4), f=_rf.randrange(4))
    _bl = dict(p1sim.item(_fam, 'blade', _rf.randrange(1, 4)), enh=_rf.randrange(11))
    _arm = [dict(p1sim.item(_rf.choice(('burst', 'scorch', 'sustain')), a, _rf.randrange(1, 4)), enh=_rf.randrange(11),
                 q=_rf.randrange(4), f=_rf.randrange(4)) for a in p1sim.ARMOR_SLOTS]
    _lv = _rf.choice((1, 15, 30))
    p1sim.SIX = {'w': _W}
    _ps = (_ch,) + tuple(_arm)
    _ref = (sum(w * cfg['h'][it['tier']] * _g(it) for w, it in zip(_W, _ps)), sum(w * cfg['D'][it['tier']] for w, it in zip(_W, _ps)))
    _bad_def += p1sim.hp_def(cfg, _ch, _arm) != _ref
    _same = [dict(a, q=_ch['q'], f=_ch['f']) for a in _arm]
    p1sim.SIX = {'w': _W, 'follow': 'all'}
    _s6, _hd6 = p1sim.stats(cfg, _bl, _ch, _lv, None, _same), p1sim.hp_def(cfg, _ch, _same)
    p1sim.SIX = None
    _s2, _hd2 = p1sim.stats(cfg, _bl, _ch, _lv), p1sim.hp_def(cfg, _ch)
    _bad_all += not (_s6['H'] == _s2['H'] and _s6['M'] == _s2['M'] and _hd6 == _hd2 and _s6['B'] == _s2['B'])
p1sim.SIX = _six0
check('D316 SIX without follow = D313 weighted hp_def, bit for bit (3000 random sets)', _bad_def == 0, '%d differ' % _bad_def)
check("D316 follow 'all' + armor of the charm's quality / craft = 2-slot H / M / D / B, bit for bit (3000)", _bad_all == 0, '%d differ' % _bad_all)

# D328 T0‴ K3 same-slot refine (SIX['k3_merge']): craft = max; material destroyed with no blank; q/fam/tier/enh
# untouched; no-op (max does not raise) keeps the normal dismantle / blank path. Absent flag = bit-identical.
_six0 = p1sim.SIX
p1sim.SIX = {'w': _W, 'follow': 'all', 'k3_merge': True}
_p = p1sim.Player(cfg, p1sim.Knobs(0.5), __import__("random").Random(328))
_p.armor = [dict(p1sim.item('burst', a, 2), q=2, f=1, src='drop') for a in p1sim.ARMOR_SLOTS]
_p.blank = 0
_p.k3_merges = 0
# keep worn (higher q), merge higher craft from drop → craft 1→3, no blank
_p.consider_armor(dict(p1sim.item('burst', 'head', 2), q=1, f=3, src='drop'))
check('D328 k3_merge raises worn craft and skips blank', _p.armor[0]['f'] == 3 and _p.armor[0]['q'] == 2 and _p.blank == 0 and _p.k3_merges == 1,
      'f=%s q=%s blank=%s merges=%s' % (_p.armor[0]['f'], _p.armor[0]['q'], _p.blank, _p.k3_merges))
# equip better-q drop (q3 f0 beats worn q1 f3 on qf), merge craft from old into new; no blank
_p.armor[0] = dict(p1sim.item('burst', 'head', 2), q=1, f=3, src='drop')
_p.blank = 0
_p.consider_armor(dict(p1sim.item('burst', 'head', 2), q=3, f=0, src='drop'))
check('D328 k3_merge into newly equipped piece keeps max craft, q from new', _p.armor[0]['f'] == 3 and _p.armor[0]['q'] == 3 and _p.blank == 0 and _p.k3_merges == 2,
      'f=%s q=%s blank=%s merges=%s' % (_p.armor[0]['f'], _p.armor[0]['q'], _p.blank, _p.k3_merges))
# max does not raise → normal dismantle (blank += tier)
_before = (_p.armor[0]['f'], _p.armor[0]['q'], _p.blank, _p.k3_merges)
_p.consider_armor(dict(p1sim.item('scorch', 'head', 2), q=0, f=1, src='drop'))  # worse power, lower craft
check('D328 k3_merge skip when craft would not rise → dismantle for blank',
      _p.armor[0]['f'] == _before[0] and _p.armor[0]['q'] == _before[1] and _p.blank == _before[2] + 2 and _p.k3_merges == _before[3],
      'f=%s q=%s blank=%s merges=%s' % (_p.armor[0]['f'], _p.armor[0]['q'], _p.blank, _p.k3_merges))
# flag off = previous dismantle-on-keep path (blank from discarded)
p1sim.SIX = {'w': _W, 'follow': 'all'}
_p2 = p1sim.Player(cfg, p1sim.Knobs(0.5), __import__("random").Random(329))
_p2.armor = [dict(p1sim.item('burst', a, 2), q=2, f=1, src='drop') for a in p1sim.ARMOR_SLOTS]
_p2.blank = 0
_p2.consider_armor(dict(p1sim.item('burst', 'head', 2), q=1, f=3, src='drop'))
check('D328 without k3_merge still dismantles non-equip for blank (craft stays)',
      _p2.armor[0]['f'] == 1 and _p2.armor[0]['q'] == 2 and _p2.blank == 2,
      'f=%s q=%s blank=%s' % (_p2.armor[0]['f'], _p2.armor[0]['q'], _p2.blank))
p1sim.SIX = _six0

print('\n%d failed' % len(fails))
sys.exit(1 if fails else 0)

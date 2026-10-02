"""Load every number the simulator uses from the real repo files (no hard-coded balance)."""
import copy
import os
import re

import miniyaml

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), '..', '..'))
P1 = 'CoreRpg/src/main/java/town/sunshine/corerpg/p1/'

FILES = {
    'runs': 'plugins/CoreRpg/ember-v1-runs.yml',
    'runs_src': 'CoreRpg/src/main/resources/ember-v1-runs.yml',
    'p1': 'plugins/CoreRpg/ember-v1.yml',
    'mm': 'plugins/MythicMobs/Mobs/EmberP1Main.yml',
    'cash': 'plugins/CoreRpg/cash.yml',
    'progress': 'plugins/CoreRpg/progress.yml',
    'upgrade': P1 + 'EmberUpgradeRules.java',
    'sets': P1 + 'EmberSetRules.java',
    'settle': P1 + 'EmberRunRules.java',
    'tables': P1 + 'EmberTables.java',
}

# Pre-D31 numbers (book §1.5 table, "原值"), used by --profile v2 to reproduce the 1.20.5 natural playtest.
PROFILES = {
    'current': {},
    'v2': {
        'q03.boss.hp': 1300,
        'q04.mobs.melee.hp': 105,
        'q04.rooms.r3.a': {'melee': 6, 'heavy': 1},
        'q04.boss.hp': 2200,
        'q04.first_clear.coin': 900,
        'q05.boss.hp': 4000,
        'q06.boss.hp': 4600,
        'q07.boss.hp': 5800,
        'death_refund': 0,
    },
}


def _java_array(src, name):
    m = re.search(r'\b' + name + r'\s*=\s*\{([^}]*)\}', src)
    if not m:
        raise ValueError('array %s not found' % name)
    return [float(x) if '.' in x else int(x) for x in (s.strip() for s in m.group(1).split(',')) if x]


def _java_const(src, name):
    m = re.search(r'\b' + name + r'\s*=\s*([-0-9.]+)L?;', src)
    if not m:
        raise ValueError('const %s not found' % name)
    s = m.group(1)
    return float(s) if '.' in s else int(s)


def _java_cost(src, fn, arg):
    """new Cost(shards, cores, blanks, bone, coins) inside `fn` for `if (tier == arg)`."""
    body = src[src.index(fn):]
    body = body[:body.index('\n    }')]
    m = re.search(r'==\s*' + str(arg) + r'\)\s*return new Cost\(([^)]*)\)', body)
    v = [int(x) for x in m.group(1).split(',')]
    return dict(shard=v[0], core=v[1], blank=v[2], bone=v[3], coin=v[4])


def _table_tail(src):
    i = src.index('DEFAULTS = new EmberTables(')
    body = src[i:src.index(');', i)]
    v = [float(x) for x in body[body.rindex('},') + 2:].split(',') if x.strip()]
    # crit rate, crit mult, crit min charge, def k, def floor, sustain, lvl atk, lvl hp, lvl base, lvl cap, skill mult
    return {'lvl_atk': v[6], 'lvl_hp': v[7], 'lvl_base': int(v[8]), 'lvl_cap': int(v[9]), 'skill_mult': v[10]}


def _read(rel):
    with open(os.path.join(ROOT, rel), encoding='utf-8') as f:
        return f.read()


def load(profile='current'):
    runs = miniyaml.load(os.path.join(ROOT, FILES['runs']))
    runs_src = miniyaml.load(os.path.join(ROOT, FILES['runs_src']))
    p1 = miniyaml.load(os.path.join(ROOT, FILES['p1']))
    mm = miniyaml.load(os.path.join(ROOT, FILES['mm']))
    cash = miniyaml.load(os.path.join(ROOT, FILES['cash']))
    prog = miniyaml.load(os.path.join(ROOT, FILES['progress']))
    up, st, se, tb = (_read(FILES[k]) for k in ('upgrade', 'sets', 'settle', 'tables'))
    warnings = []
    if runs != runs_src:
        warnings.append('ember-v1-runs.yml: plugins/ copy differs from CoreRpg/src/main/resources copy (using plugins/)')

    t = p1['tables']
    cfg = {
        'A': t['weapon_a'], 'h': t['charm_h'], 'D': t['charm_d'],
        'q': t['quality'], 'f': t['craft'], 'e': t['enhance'],
        'crit_rate': p1['crit']['rate'], 'crit_mult': p1['crit']['mult'],
        'def_k': p1['defense']['k'], 'def_floor': p1['defense']['floor'],
        'sustain_hp': p1['sustain_hp_mult'],
        'skill_cd': p1['skill']['cooldown_seconds'], 'skill_targets_max': p1['skill']['max_targets'],
        'potion_pct': p1['heal_potion']['percent'], 'potion_cd': p1['heal_potion']['cooldown_seconds'],
        'potion_price': p1['shop']['heal_potion']['price'],
        'starter_potions': p1['starter']['heal_potions'],
        'death_refund': p1['death_refund']['max_potions'],
        'stamina_day': cash['stamina']['base_max'],
        'run_cost': runs['cost'],
        # level / skill: tail of EmberTables.DEFAULTS (crit, def, sustain, lvl atk, lvl hp, lvl base, lvl cap, skill mult)
        **_table_tail(tb),
        'xp_curve': prog['ember_xp']['curve'],
        # §6.1 / §6.4 / §5.3 from EmberUpgradeRules.java
        'enh_rate': _java_array(up, 'RATE'), 'enh_max': _java_array(up, 'MAX_TRIES'),
        'enh_shard': _java_array(up, 'SHARDS'), 'enh_core': _java_array(up, 'CORES'),
        'enh_coin': _java_array(up, 'COINS'),
        'upgrade': {1: _java_cost(up, 'upgradeCost', 1), 2: _java_cost(up, 'upgradeCost', 2)},
        # §4.3 set coefficients
        'burn': _java_array(st, 'BURN_COEF'), 'burst': _java_array(st, 'BURST_COEF'),
        'sustain_pct': _java_array(st, 'SUSTAIN_PCT'),
        'scorch_every': _java_const(st, 'SCORCH_EVERY'), 'burst_every': _java_const(st, 'BURST_EVERY'),
        'sustain_every': _java_const(st, 'SUSTAIN_EVERY'),
        'burst_icd': _java_const(st, 'BURST_ICD_MS') / 1000.0, 'sustain_icd': _java_const(st, 'SUSTAIN_ICD_MS') / 1000.0,
        # §9.1 / §9.3 settlement
        'base': {k: _java_const(se, 'BASE_' + k.upper()) for k in ('coin', 'shard', 'bone', 'core', 'xp', 'mark')},
        'treasure_coin': _java_const(se, 'TREASURE_COIN'), 'elite_shard': _java_const(se, 'ELITE_SHARD'),
        'elite_core': _java_const(se, 'ELITE_CORE'), 'marks_per': _java_const(se, 'MARKS_PER_EXCHANGE'),
        'target_weight': _java_const(se, 'TARGET_WEIGHT'),
        'quality_w': _java_array(se, 'QUALITY_WEIGHTS'), 'craft_w': _java_array(se, 'CRAFT_WEIGHTS'),
        'extra_w': _java_array(se, 'EXTRA_WEIGHTS'),
        'maps': {},
    }
    order = []
    k = next(k for k, m in runs['maps'].items() if not m.get('requires'))
    while k:
        order.append(k)
        k = runs['maps'][k].get('unlocks') or None
    for key in order:
        m = copy.deepcopy(runs['maps'][key])
        # MM Health / Damage are what actually spawns (CoreRpg pins hits to the runs value; both must agree)
        for role, mob in m['mobs'].items():
            mmrow = mm.get(mob.get('mm'), {})
            if mmrow and mmrow.get('Health') != mob['hp']:
                warnings.append('%s %s: MM Health %s != runs hp %s (using MM)' % (key, role, mmrow.get('Health'), mob['hp']))
                mob['hp'] = mmrow['Health']
        b = m['boss']
        mmb = mm.get(b.get('mm'), {})
        if mmb and mmb.get('Health') != b['hp']:
            warnings.append('%s boss: MM Health %s != runs hp %s (using MM)' % (key, mmb.get('Health'), b['hp']))
            b['hp'] = mmb['Health']
        cfg['maps'][key] = m
    cfg['order'] = order
    for path, v in PROFILES[profile].items():
        if path == 'death_refund':
            cfg['death_refund'] = v
            continue
        parts = path.split('.')
        node = cfg['maps']
        for p in parts[:-1]:
            node = node.setdefault(p, {})
        node[parts[-1]] = v
    cfg['warnings'] = warnings
    cfg['profile'] = profile
    return cfg

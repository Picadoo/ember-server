"""M06: ONE canonical rule snapshot for every simulator entry point (p1config, p1sim, p1party, p2econ, growth, rushsim,
festsim, growthcheck / growthraid / growthrun, builddiv ...). Nothing else in tools/p1sim may open a rule file.

- Sources: the deployed copies under plugins/ (what the server runs) + the CoreRpg/src/main/resources copies of the
  files that exist in both and that sims used to read from either place (runs / growth / festival / economy). A pair must parse
  to identical data, else RuleError — every entry point fails instead of warning and printing a plausible table.
- Validation: every growth mod key (talents / honors / affixes) must be one the model implements (or a known economic key
  listed as unmodelled); week-rule `converted` multipliers only hp / atk / interval / speed. Unsupported → RuleError.
- Content hash: sha256 over the canonical JSON of the parsed data + the Java rule sources (comment-only YAML edits do
  not change it). `stamp()` is the one-line header every report prints.
- Pinning: P1SIM_RULES=<file.json> (from `python3 tools/p1sim/rules.py --export f.json`) makes every entry point use that
  frozen snapshot (its stored hash is re-verified); P1SIM_RULES_EXPECT=<hash prefix> fails when the snapshot differs.
  P1SIM_ALLOW_MISMATCH=1 is the only escape hatch for a pair mismatch: it is stamped into the hash ('-MISMATCH').
"""
import copy
import hashlib
import json
import os
import sys

import miniyaml

ROOT = os.path.abspath(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..'))
P1 = 'CoreRpg/src/main/java/town/sunshine/corerpg/p1/'
PAIRS = {
    'runs': ('plugins/CoreRpg/ember-v1-runs.yml', 'CoreRpg/src/main/resources/ember-v1-runs.yml'),
    'growth': ('plugins/CoreRpg/ember-v1-growth.yml', 'CoreRpg/src/main/resources/ember-v1-growth.yml'),
    'festival': ('plugins/CoreRpg/ember-v1-festival.yml', 'CoreRpg/src/main/resources/ember-v1-festival.yml'),
    # E3 / D225: amount() SoT (live D224); plugins/ vs src must match like the other paired yml
    'economy': ('plugins/CoreRpg/ember-v1-economy.yml', 'CoreRpg/src/main/resources/ember-v1-economy.yml'),
}
# deployed copy only (the src copies of these differ by design: the server writes runtime values into plugins/)
SINGLE = {
    'p1': 'plugins/CoreRpg/ember-v1.yml',
    'mm': 'plugins/MythicMobs/Mobs/EmberP1Main.yml',
    'mm_fest': 'plugins/MythicMobs/Mobs/EmberFestival.yml',
    'cash': 'plugins/CoreRpg/cash.yml',
    'progress': 'plugins/CoreRpg/progress.yml',
    # D243 / ARCH S4-2: the machine-readable source map (one-time sources the sim pays: `sim:` entries → sourcemap.py)
    'sourcemap': 'docs/design/ember-source-map.yml',
}
JAVA = {
    'upgrade': P1 + 'EmberUpgradeRules.java',
    'sets': P1 + 'EmberSetRules.java',
    'settle': P1 + 'EmberRunRules.java',
    'tables': P1 + 'EmberTables.java',
    'codex': P1 + 'EmberCodex.java',  # D243: STAGE_AT / STAGE_COIN dual-assert for the S33 codex stage coin
}
# growth mod keys the model implements (p1sim.gm / p2econ / growth.combine) ...
SIM_KEYS = {'abyss_taken', 'burn_mult', 'burn_spread', 'burn_ticks', 'burst_every', 'burst_mult', 'coin', 'dmg_affix',
            'dmg_boss', 'dmg_mob', 'dmg_split', 'dodge_burst', 'dodge_dmg', 'dodge_heal', 'dodge_icd', 'dodge_secs',
            'hit_burst', 'potion', 'set_dmg', 'shard_bonus', 'share_taken', 'share_w', 'spread_icd', 'sustain_every',
            'sustain_mult', 'taken_affix', 'taken_all', 'taken_boss', 'taken_mob', 'taken_tele', 'abyss_fee',
            'dmg_affix_body',  # dmg_affix_body: B01 patch key (body only, not the clones) — p1sim.dmult implements it
            'dmg_affix_shield', 'dmg_affix_blazing', 'dmg_affix_split'}  # D164 破甲: per elite type (split incl. clones) — p1sim.dmult
# ... and economic keys it knowingly does not model (reported, not fatal)
UNMODELLED = {'reroll_coin'}
CONVERTED = {'hp', 'atk', 'interval', 'speed'}


class RuleError(RuntimeError):
    pass


def _norm(x):
    """type-preserving canonical form: non-str dict keys are tagged (JSON would silently turn 1 into '1')"""
    if isinstance(x, dict):
        return {(k if isinstance(k, str) else '\u0000%s:%r' % (type(k).__name__, k)): _norm(v) for k, v in x.items()}
    if isinstance(x, (list, tuple)):
        return [_norm(v) for v in x]
    if isinstance(x, float) and x == int(x) and abs(x) < 1e15:
        return {'\u0000float': repr(x)}
    return x


def _canon(x):
    return json.dumps(_norm(x), sort_keys=True, ensure_ascii=False, separators=(',', ':'), default=repr)


def _hash(data):
    return hashlib.sha256(_canon(data).encode('utf-8')).hexdigest()


def _read(rel):
    with open(os.path.join(ROOT, rel), encoding='utf-8') as f:
        return f.read()


def validate(data):
    """unsupported values → RuleError; returns the list of known-but-unmodelled keys found"""
    errs, unmod = [], set()
    g = data['growth']
    parts = [('talent ' + n['id'], n.get('mods') or {}) for n in g['talents']['nodes']]
    parts += [('honor ' + h['id'], h.get('mods') or {}) for h in (g.get('honors') or {}).get('list', [])]
    for slot, lst in ((g.get('reroll') or {}).get('affixes') or {}).items():
        parts += [('affix %s %s' % (slot, a['id']), {a['key']: a['values'][0]}) for a in lst]
    for who, mods in parts:
        for k in mods:
            if k in UNMODELLED:
                unmod.add(k)
            elif k not in SIM_KEYS:
                errs.append('%s: mod key %r is not implemented by the simulator' % (who, k))
    for m in (data['runs'].get('rotation') or {}).get('modifiers') or []:
        for k in (m.get('converted') or {}):
            if k not in CONVERTED:
                errs.append('week rule %s: converted.%s is not implemented by the simulator' % (m.get('id'), k))
    if str(data['runs'].get('balance_version')) in ('', 'None'):
        errs.append('ember-v1-runs.yml: balance_version missing')
    econ = data.get('economy') or {}
    if not econ:
        errs.append('ember-v1-economy.yml: missing or empty (E3 SoT required)')
    else:
        if str(econ.get('balance_version')) in ('', 'None'):
            errs.append('ember-v1-economy.yml: balance_version missing')
        elif str(econ.get('balance_version')) != str(data['runs'].get('balance_version')):
            errs.append('ember-v1-economy.yml balance_version %s != runs %s' % (
                econ.get('balance_version'), data['runs'].get('balance_version')))
        if not isinstance(econ.get('sources'), dict) or not isinstance(econ.get('sinks'), dict):
            errs.append('ember-v1-economy.yml: sources/sinks maps required')
    # D243: every one-time source the sim pays (`sim:` in ember-source-map.yml) must name a registered economy row
    sm = data.get('sourcemap') or {}
    if sm:
        rows = set((econ.get('sources') or {}).keys())
        for sid, e in (sm.get('sources') or {}).items():
            if isinstance(e, dict) and e.get('sim') and str(e.get('econ_row')) not in rows:
                errs.append('ember-source-map.yml %s (sim %s): econ_row %s has no ember-v1-economy.yml sources row'
                            % (sid, e.get('sim'), e.get('econ_row')))
    if errs:
        raise RuleError('rule snapshot rejected:\n  ' + '\n  '.join(errs))
    return sorted(unmod)


def build():
    data, files, mismatch = {}, {}, []
    for name, (dep, src) in PAIRS.items():
        a, b = miniyaml.load(os.path.join(ROOT, dep)), miniyaml.load(os.path.join(ROOT, src))
        if _canon(a) != _canon(b):
            mismatch.append('%s: %s and %s parse to different data' % (name, dep, src))
        data[name] = a
        files[dep] = files[src] = hashlib.sha256(_read(dep).encode('utf-8')).hexdigest()[:12]
    for name, rel in SINGLE.items():
        p = os.path.join(ROOT, rel)
        data[name] = miniyaml.load(p) if os.path.exists(p) else {}
        if os.path.exists(p):
            files[rel] = hashlib.sha256(_read(rel).encode('utf-8')).hexdigest()[:12]
    data['java'] = {k: _read(v) for k, v in JAVA.items()}
    texts = {rel: _read(rel) for rel in [p[0] for p in PAIRS.values()] + [r for r in SINGLE.values() if os.path.exists(os.path.join(ROOT, r))]}
    for v in JAVA.values():
        files[v] = hashlib.sha256(_read(v).encode('utf-8')).hexdigest()[:12]
    if mismatch and os.environ.get('P1SIM_ALLOW_MISMATCH') != '1':
        raise RuleError('rule copies differ (deploy / source out of sync) — refusing to simulate:\n  ' + '\n  '.join(mismatch)
                        + '\n  (P1SIM_ALLOW_MISMATCH=1 overrides; the hash is then stamped -MISMATCH)')
    unmod = validate(data)
    h = _hash(data) + ('-MISMATCH' if mismatch else '')
    return {'hash': h, 'balance_version': data['runs'].get('balance_version'), 'files': files,
            'unmodelled': unmod, 'mismatch': mismatch, 'source': 'repo', 'data': data, 'texts': texts}


def _parse_texts(texts, java):
    """the exported snapshot carries the raw rule texts; re-parsing them gives exactly the repo build's objects
    (a JSON dump of the parsed data would not: int keys become strings)"""
    data = {}
    for name, (dep, _src) in PAIRS.items():
        data[name] = miniyaml.loads(texts[dep])
    for name, rel in SINGLE.items():
        data[name] = miniyaml.loads(texts[rel]) if rel in texts else {}
    data['java'] = dict(java)
    return data


def load_file(path):
    with open(path, encoding='utf-8') as f:
        snap = json.load(f)
    snap['data'] = _parse_texts(snap['texts'], snap['java'])
    h = _hash(snap['data'])
    if not snap['hash'].startswith(h):
        raise RuleError('%s: stored hash %s does not match its content %s' % (path, snap['hash'][:12], h[:12]))
    validate(snap['data'])
    snap['source'] = path
    return snap


_SNAP = None


def snapshot():
    global _SNAP
    if _SNAP is None:
        pin = os.environ.get('P1SIM_RULES')
        s = load_file(pin) if pin else build()
        want = os.environ.get('P1SIM_RULES_EXPECT')
        if want and not s['hash'].startswith(want):
            raise RuleError('rule snapshot %s != expected %s (P1SIM_RULES_EXPECT)' % (s['hash'][:12], want))
        wi = os.environ.get('P1SIM_WHATIF')
        if wi:  # offline what-if (sidegrade.py): a JSON patch on top of the snapshot; the stamp says so
            s = whatif(s, json.load(open(wi, encoding='utf-8')), wi)
        _SNAP = s
    return _SNAP


def _merge(dst, src):
    for k, v in src.items():
        if isinstance(v, dict) and isinstance(dst.get(k), dict):
            _merge(dst[k], v)
        else:
            dst[k] = copy.deepcopy(v)


def whatif(s, patch, path):
    """patch = {"growth": {...deep merge, lists replaced...}, "growth_nodes": {id: {field: value}},
    "growth_affixes": {id: {field: value}}, "p1": {...}} — what-if only, never a real rule source"""
    s = copy.deepcopy(s)
    d = s['data']
    for name in ('growth', 'runs', 'p1'):
        if name in patch:
            _merge(d[name], patch[name])
    for n in d['growth']['talents']['nodes']:
        if n['id'] in patch.get('growth_nodes', {}):
            n.update(copy.deepcopy(patch['growth_nodes'][n['id']]))
    for lst in d['growth']['reroll']['affixes'].values():
        for a in lst:
            if a['id'] in patch.get('growth_affixes', {}):
                a.update(copy.deepcopy(patch['growth_affixes'][a['id']]))
    h = hashlib.sha256(json.dumps(patch, sort_keys=True).encode()).hexdigest()[:8]
    s['source'] = '%s + WHAT-IF %s (%s)' % (s['source'], os.path.basename(path), h)
    return s


def runs():
    return copy.deepcopy(snapshot()['data']['runs'])  # copies: callers may edit their dicts (set_abyss, PROFILES ...)


def growth():
    return copy.deepcopy(snapshot()['data']['growth'])


def festival():
    return copy.deepcopy(snapshot()['data']['festival'])


def economy():
    """E3 / D225: ember-v1-economy.yml (amount SoT; same tree live EmberEconomy.amount reads)."""
    return copy.deepcopy(snapshot()['data']['economy'])


def amount(row_id, key, default=None):
    """Integer amount for sources.<id>.<key> or sinks.<id>.<key> (mirrors EmberEconomy.amount).
    Missing key → default if given, else RuleError (fail-closed like live)."""
    e = snapshot()['data']['economy']
    block = None
    if isinstance(e.get('sources'), dict) and row_id in e['sources']:
        block = e['sources'][row_id]
    elif isinstance(e.get('sinks'), dict) and row_id in e['sinks']:
        block = e['sinks'][row_id]
    if not isinstance(block, dict) or key not in block:
        if default is not None:
            return default
        raise RuleError('economy amount missing: %s.%s' % (row_id, key))
    v = block[key]
    if isinstance(v, bool) or not isinstance(v, (int, float)):
        raise RuleError('economy amount not numeric: %s.%s = %r' % (row_id, key, v))
    return int(v)


def data(name):
    return copy.deepcopy(snapshot()['data'][name])


def java(name):
    return snapshot()['data']['java'][name]


def stamp():
    s = snapshot()
    return 'rules sha256 %s (balance_version %s, %s%s)' % (s['hash'][:16], s['balance_version'], s['source'],
                                                          ', unmodelled keys: ' + '/'.join(s['unmodelled']) if s['unmodelled'] else '')


if __name__ == '__main__':
    if '--export' in sys.argv:
        out = sys.argv[sys.argv.index('--export') + 1]
        s = dict(snapshot())
        s['java'] = s['data']['java']
        del s['data']  # rebuilt from the raw texts on load (type-exact)
        with open(out, 'w', encoding='utf-8') as f:
            json.dump(s, f, ensure_ascii=False, sort_keys=True)
        print('wrote', out, s['hash'])
    else:
        print(stamp())

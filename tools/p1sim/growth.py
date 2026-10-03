"""D141–D143 horizontal growth model: talents (天赋专精) / honors (余烬勋记) / affixes (词条洗练), read from
CoreRpg/src/main/resources/ember-v1-growth.yml and turned into the `mods` dict p1sim.Fight / p1party read (p1sim.GROWTH).

  import growth; growth.enable(talents=['t1a','t2b','t3b'], honors='all', affixes={'blade': 'a_set', 'charm': 'a_tele'})

Mods combine like the Java EmberGrowth.Mods: multiplicative keys multiply, additive keys add (dodge_secs: max,
dodge_icd: min). Set-gated talents (`set:`) only count while that set is active. Unlocks follow the player's cleared
maps (ctx): talent rows need the points (q03 / q05 / q07 first clears = 1 each; after Q07 the model assumes all 6, an
upper bound); honors only after Q07 (all of them, upper bound). ctx None = everything unlocked (reference checks).
Affix tier = min(wanted tier, quality cap of the item: 标准 1 / 精良 2 / 卓越 3 / 极品 4).
"""
import os
import miniyaml
import p1config
import p1sim

PATH = os.path.join(p1config.ROOT, 'CoreRpg/src/main/resources/ember-v1-growth.yml')
ADD = {'dodge_secs', 'dodge_heal', 'dodge_icd', 'burn_ticks', 'burst_every', 'sustain_every', 'shard_bonus', 'burn_spread', 'dodge_burst', 'spread_icd'}
ADD_MAX = {'dodge_secs'}
ADD_MIN = {'dodge_icd', 'spread_icd'}
SHARE_W = 'share_w'  # additive over the base 1 (share_w: 2 = one extra portion)


def load(path=PATH):
    return miniyaml.load(path)


def combine(parts):
    out = {}
    for m in parts:
        for k, v in m.items():
            v = float(v)
            if k in ADD_MAX:
                out[k] = max(out.get(k, 0.0), v)
            elif k in ADD_MIN:
                out[k] = min(out.get(k, 1e9), v)
            elif k in ADD:
                out[k] = out.get(k, 0.0) + v
            elif k == SHARE_W:
                out[k] = out.get(k, 1.0) + (v - 1.0)
            else:
                out[k] = out.get(k, 1.0) * v
    return out


def talent_points(ctx):
    if ctx is None:
        return 6
    if 'q07' in ctx:
        return 6  # post-Q07 upper bound: challenge / raid / abyss 5 assumed done
    # before Q07: q03 / q05 first clears + one challenge first clear (possible early) = at most 3 (rows 1 + 2)
    return sum(1 for k in ('q03', 'q05', 'q07') if k in ctx) + (1 if 'q03' in ctx else 0)


def pick_talents(g, wanted, points):
    """wanted: list of node ids (at most one per row); rows are taken low row first while the points last"""
    rows = {r['row']: r for r in g['talents']['rows']}
    nodes = {n['id']: n for n in g['talents']['nodes']}
    out, left, last = [], points, 0
    for nid in sorted(wanted, key=lambda x: nodes[x]['row']):
        row = nodes[nid]['row']
        need = int(rows[row]['points'])
        if row != last + 1 or need > left:  # tree: row r needs row r-1 (same as Java canPick)
            break
        out.append(nodes[nid])
        left -= need
        last = row
    return out


def build(g, talents=(), honors=(), affixes=None, affix_tier=4):
    """returns the GROWTH callable for p1sim"""
    def fn(info, ctx):
        parts = []
        for n in pick_talents(g, list(talents), talent_points(ctx)):
            if n.get('set') and n['set'] != info.get('set'):
                continue
            parts.append(n['mods'])
        if honors and (ctx is None or 'q07' in ctx):
            for h in (g.get('honors') or {}).get('list', []):
                if honors == 'all' or h['id'] in honors:
                    parts.append(h['mods'])
        if affixes:
            rr = g.get('reroll') or {}
            for slot, aid in affixes.items():
                it = info.get(slot)
                if not it or it.get('tier', 0) < 1 or not aid:
                    continue
                a = next(x for x in rr['affixes'][slot] if x['id'] == aid)
                cap = rr['tier_cap'][it.get('q', 0)]
                tier = min(affix_tier, cap)
                parts.append({a['key']: a['values'][tier - 1]})
        return combine(parts) if parts else None
    return fn


def enable(talents=(), honors=(), affixes=None, affix_tier=4, g=None):
    p1sim.GROWTH = build(g or load(), talents, honors, affixes, affix_tier)


def disable():
    p1sim.GROWTH = None

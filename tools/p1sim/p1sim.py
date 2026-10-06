#!/usr/bin/env python3
"""P1 offline pacing simulator: a player going Q01 -> Q07 on the real config numbers (see README.md)."""
import argparse
import math
import random
import statistics
import sys

import copy
import os

import miniyaml
import p1config
import burnbook
import itertools

# P2-8 / D94 weekly rules (rotation.modifiers); `normal: true` rules also hit repeat normal runs of the featured map
import rules
_RUNS = rules.runs()  # M06 canonical snapshot
MODS = list((_RUNS.get('rotation') or {}).get('modifiers') or [])
ROT_CFG = _RUNS.get('rotation') or {}

FAMS = ('scorch', 'burst', 'sustain')

# D139 festival charm (None = not worn): {'hp', 'def', 'coef', 'targets', 'radius', 'icd'}; set by festsim.py
FEST = None
# D141–D143 horizontal growth (talents / honors / affixes): None = off, else a callable (st, ctx) -> mods dict
# (growth.py builds it from ember-v1-growth.yml; ctx = the player's cleared map keys, None = everything unlocked)
GROWTH = None
# build-diversity instrumentation (builddiv.py): True = every Fight keeps a Counter `rec` of damage by source, heals,
# mechanic triggers, damage taken by kind and segment times. Counting only — never touches the rng, so results are
# bit-identical with RECORD on or off.
RECORD = False
# D243 / ARCH S4-2: one-time sources from docs/design/ember-source-map.yml (sourcemap.py). CODEX = pay the S33 codex
# stage coin when the player's registered item kinds reach 5 / 10 / 15 / 20 (claimed at once). Pure bookkeeping on the
# coin balance — never touches the rng. P1SIM_NO_CODEX=1 (or CODEX = False) = the pre-D243 sim (A/B).
import sourcemap
import affixtable  # D244 / ARCH S4-3: affix numbers exported from the Java primitives
CODEX = os.environ.get('P1SIM_NO_CODEX') != '1'
CODEX_STAGES = sourcemap.codex_stages()
CODEX_STATS = {}  # {'coin': total paid, 'players': players created} while CODEX is on (sourcemap.py dyn report)


def gm(st, k, d=1.0):
    """growth modifier k of these stats (default d when growth is off / the key is absent)"""
    m = st.get('mods')
    return d if not m else m.get(k, d)


def dmult(st, tgt, t, owner):
    """D141: outgoing multiplier on target tgt at time t (boss / affixed elite or its split adds / other mob, plus
    the after-dodge window kept on the owner)"""
    if not st.get('mods'):
        return 1.0
    r = gm(st, 'dmg_boss') if tgt['role'] == 'boss' else (gm(st, 'dmg_affix') if tgt.get('affix') else gm(st, 'dmg_mob'))
    if tgt.get('split_add'):
        r *= gm(st, 'dmg_split')
    elif tgt.get('affix') and 'dmg_affix_body' in st['mods']:  # PROPOSAL (builddiv 10-04, not in Java): body-only
        r *= gm(st, 'dmg_affix_body')
    if tgt.get('affix_kind') and ('dmg_affix_' + tgt['affix_kind']) in st['mods']:  # D164 破甲 (Java EmberGrowthService.classMult): per elite type
        r *= st['mods']['dmg_affix_' + tgt['affix_kind']]
    if t < getattr(owner, 'dodge_until', -1.0):
        r *= gm(st, 'dodge_dmg')
    if tgt['role'] == 'boss' and 'win_dmg' in st['mods']:  # PROPOSAL (builddiv 10-04, not in Java): 破绽窗口
        r *= gm(st, 'win_dmg') if t < getattr(owner, 'win_until', -1.0) else gm(st, 'win_out')
    return r


def burst_n_mult(st, k):
    """PROPOSAL (builddiv 10-04, not in Java) 聚爆: 烬爆 × burst_solo when it catches 1 target, × burst_pack at >= 3"""
    m = st.get('mods')
    if not m or ('burst_solo' not in m and 'burst_pack' not in m):
        return 1.0
    return m.get('burst_solo', 1.0) if k <= 1 else (m.get('burst_pack', 1.0) if k >= 3 else 1.0)


def sustain_every(cfg, st, hp):
    """炽愈 interval in swings; PROPOSAL 低血回涌: below sustain_low × H the interval moves by sustain_low_every instead"""
    m = st.get('mods')
    if m and 'sustain_low' in m and hp < m['sustain_low'] * st['H']:
        return cfg['sustain_every'] + int(m.get('sustain_low_every', 0))
    return cfg['sustain_every'] + int(gm(st, 'sustain_every', 0))


def fest_proc(owner, alive, t, B, caught):
    """D139 烟火迸发: an enemy that died this step bursts on up to min(targets, caught) other living enemies for
    coef × B, at most once per icd (a burst kill never starts another burst: the cooldown is set first)."""
    dead = [m for m in alive if m['hp'] <= 0 and not m.get('_dead')]
    if not dead:
        return
    for m in dead:
        m['_dead'] = True
    if FEST is None or t < getattr(owner, 'fest_cd', -1.0):
        return
    owner.fest_cd = t + FEST['icd']
    rest = [m for m in alive if m['hp'] > 0][:min(int(FEST['targets']), caught)]
    rec = getattr(owner, 'rec', None)
    for m in rest:
        if rec is not None:
            rec['d_fest'] += min(FEST['coef'] * B, m['hp']); rec['n_fest'] += 1
        m['hp'] -= FEST['coef'] * B
    owner.fest_bursts = getattr(owner, 'fest_bursts', 0) + 1
    fest_proc(owner, alive, t, B, caught)  # mark burst kills dead (cooldown already running → no new burst)
MELEE_ROLES = ('melee', 'heavy', 'elite')


class Knobs:
    """Player-skill / behaviour parameters (not balance numbers)."""
    def __init__(self, dodge=0.5, **kw):
        self.dodge = dodge                 # share of plain enemy hits avoided (spacing, backing off)
        self.tele_bonus = kw.get('tele_bonus', 0.25)  # telegraphed moves (boss skills, caster line) are easier
        self.swing = kw.get('swing', 0.64)  # seconds per full-charge swing (bot 640 ms; book cap 1.6/s)
        self.uptime = kw.get('uptime', 0.70)  # share of time actually swinging (rest: moving / dodging / walking)
        self.engage = kw.get('engage', 4)  # max melee bodies hitting at the same time
        self.skill_hits = kw.get('skill_hits', 3)  # 烬斩 / 烬爆 targets actually caught (cap 5)
        self.potion_at = kw.get('potion_at', 0.675)  # drink below this share of H
        self.potion_keep = kw.get('potion_keep', 5)  # potions carried into each run (bought with own coin)
        self.coin_reserve = kw.get('coin_reserve', 100)
        self.target = kw.get('target', 'burst')
        self.swap = kw.get('swap', False)   # §6.3 free enhance-track swap when equipping a better drop
        self.quality_chase = kw.get('quality_chase', False)  # late-game: prefer higher q even if craft makes power dip
        self.route = kw.get('route', 'stepdown')  # stepdown (v3 bot) | alternate (v2 bot)
        self.normal_mods = kw.get('normal_mods', None)  # D94: None | 'normal' (rules marked normal) | 'all' (incl. casters)
        # D108: +normal_bonus_marks on repeat NORMAL clears of the featured map (T1/T2 maps). 'config' = as the runs yml,
        # 'off' = never; feat_farm = the player detours to the featured map while bonus clears are left this week
        self.feat_normal = kw.get('feat_normal', 'config')
        self.feat_farm = kw.get('feat_farm', False)


# ---------------------------------------------------------------- stats

def level_of(cfg, xp):
    c = cfg['xp_curve']
    lv = 10
    need = c['base']
    while xp >= need and lv < 60:
        xp -= need
        lv += 1
        need = c['base'] + c['step'] * max(0, lv - c['step_from'])
    return lv


def awakening(blade, charm):
    if not blade or not charm or blade['fam'] != charm['fam'] or blade['fam'] == 'none':
        return 0
    if min(blade['tier'], charm['tier']) < 1:
        return 0
    if blade['tier'] >= 3 and charm['tier'] >= 3 and blade['enh'] >= 9 and charm['enh'] >= 9:
        return 3
    if blade['tier'] >= 2 and charm['tier'] >= 2 and blade['enh'] >= 6 and charm['enh'] >= 6:
        return 2
    return 1


# STAGE 0 6-slot gear (gear6.py, 10-04 research doc §4.1; not in Java): None = the live 2-slot model. Otherwise
# {'w': (charm, head, chest, legs, boots)} = each piece's share of the charm h / D budget (sums to 1). With every
# armor piece equal to the charm (tier / enhance / quality / craft) H and M are exactly the 2-slot values.
SIX = __import__('json').loads(os.environ['P1SIM_SIX']) if os.environ.get('P1SIM_SIX') else None  # e.g. {"w": [0.4, 0.15, 0.15, 0.15, 0.15], "drop": 1, "start": true, "cost": {"charm": 0.4, "armor": 0.15}}
ARMOR_SLOTS = ('head', 'chest', 'legs', 'boots')


def hp_def(cfg, charm, armor=None):
    """(h·g, D) of the HP / DEF side: the charm alone (2-slot) or the share-weighted charm + 4 armor pieces"""
    g = lambda it: 1 + cfg['e'][it['enh']] + cfg['q'][it['q']] + cfg['f'][it['f']]
    if SIX is None or armor is None:
        return cfg['h'][charm['tier']] * g(charm), cfg['D'][charm['tier']]
    ps = (charm,) + tuple(armor)
    return (sum(w * cfg['h'][it['tier']] * g(it) for w, it in zip(SIX['w'], ps)),
            sum(w * cfg['D'][it['tier']] for w, it in zip(SIX['w'], ps)))


def stats(cfg, blade, charm, level, ctx=None, armor=None):
    steps = max(0, min(level, cfg['lvl_cap']) - cfg['lvl_base'])
    g = lambda it: 1 + cfg['e'][it['enh']] + cfg['q'][it['q']] + cfg['f'][it['f']]
    B = cfg['A'][blade['tier']] * g(blade) + cfg['lvl_atk'] * steps
    hg, dd = hp_def(cfg, charm, armor)
    H = 20 + hg + cfg['lvl_hp'] * steps
    if FEST:  # D139 festival charm slot (festsim.py): H0 += hp, D += def, same formula
        H += FEST['hp']
    awk = awakening(blade, charm)
    fam = blade['fam'] if awk else None
    mods = GROWTH({'set': fam, 'blade': blade, 'charm': charm}, ctx) if GROWTH else None
    if fam == 'sustain':
        H *= 1 + (cfg['sustain_hp'] - 1) * (mods.get('sustain_hp', 1.0) if mods else 1.0)
    if mods:
        H *= mods.get('max_hp', 1.0)
    M = max(cfg['def_floor'], cfg['def_k'] / (cfg['def_k'] + dd + (FEST['def'] if FEST else 0)))
    out = {'B': B, 'H': H, 'M': M, 'set': fam, 'awk': awk}
    if SIX is not None and armor is not None:  # 4-piece: core 2pc + >= 2 same-family armor, all four >= T2 (no effect modelled)
        same = [a for a in armor if fam and a['fam'] == fam and a['tier'] >= 2]
        out['pc4'] = bool(fam and len(same) >= 2 and blade['tier'] >= 2 and charm['tier'] >= 2)
    if mods:
        out['mods'] = mods
    return out


def power(cfg, st, kn):
    """Single number for 'equip best': expected damage per second x EHP."""
    dps = st['B'] * (1 + cfg['crit_rate'] * (cfg['crit_mult'] - 1)) / kn.swing
    if st['set'] == 'burst':
        dps += cfg['burst'][st['awk']] * st['B'] * min(3, kn.skill_hits) / max(cfg['burst_every'] * kn.swing, cfg['burst_icd'])
    elif st['set'] == 'scorch':
        dps += cfg['burn'][st['awk']] * st['B']
    ehp = st['H'] / st['M']
    if st['set'] == 'sustain':
        ehp *= 1.15
    return dps * ehp


# ---------------------------------------------------------------- combat

# D188 撞墙破绽: share of boss charges that end in a wall, as a mean (stun × share; no rng draw so paired runs stay paired).
# None = tied to player skill: dodge × WALL_STUN_K (a player who dodges well also baits the boss into walls some of the time);
# 1.0 = upper bound (every charge crashes). Java stuns only when a real wall cuts the strip short.
WALL_STUN_P = None
WALL_STUN_K = 0.5
# D192 落空破绽: a dodged boss telegraph that carries whiff_stun staggers the boss (solo model: dodged = nobody hit).
# WHIFF_ARM = share of casts where the player was inside the shape at warn start (Java arms only then); 1.0 = upper bound
# (the player always stands in melee). WHIFF_STUN = False strips the mechanic (baseline).
WHIFF_STUN = True
WHIFF_ARM = 1.0
# D193 破招: a boss channel that carries break_hp is broken (no hit, boss staggers break_stun s) when the player deals
# >= break_hp x boss max HP during its warn; otherwise it lands like any telegraph (dodge + tele_bonus: run out of the ring).
# Solo model: swings in the window = warn x BREAK_UPTIME / swing (fractional part drawn from a per-cast rng so paired runs
# stay paired), each B x dmult x crit. A gated break skill waits for its HP gate (Java dueSkill), it is not skipped.
# BREAK = False strips break skills entirely (baseline = live without the new move).
BREAK = True
BREAK_UPTIME = 0.85
BREAK_STATS = [0, 0]  # D193 [broken, landed] channels since the last reset (gate report only; no rng)
_UID = itertools.count(1)  # M03: burn-book key of a mob (unique per process; never touches an rng)


def uid(m):
    return m['uid'] if 'uid' in m else m.setdefault('uid', next(_UID))


CV = '@cv'  # D158: role key suffix of a mob converted by the week's rule (mod_cfg adds e.g. 'heavy@cv' with its multipliers)


def mob(cfg, m, role, rng, kn, t0=0.0):
    d = m['mobs'][role]
    iv = d.get('interval', 3.0)
    base = role[:-len(CV)] if role.endswith(CV) else role
    approach = (1.5 / float(d.get('speed', 1.0))) if base in MELEE_ROLES else 0.5
    out = {'hp': float(d['hp']), 'atk': d['atk'], 'iv': iv, 'role': base,
           'next': t0 + approach + rng.uniform(0, iv), 'tele': base == 'caster', 'uid': next(_UID)}
    if base != role:
        out['converted'] = True
    return out


def ignite(owner, tgt, t, scale=1.0):
    """焚烬 trigger on the main target (EmberSetService.ignite): skipped when the swing already killed it; per-tick
    amount = coef × burn_mult × B, fixed at ignition; a burning target only gets its end moved."""
    if tgt['hp'] <= 0:
        return
    st = owner.st
    k = uid(tgt)
    ev = owner.burns.ignite(k, owner.cfg['burn'][st['awk']] * gm(st, 'burn_mult') * scale * st['B'], burnbook.ms(t))
    owner.btgt[k] = tgt
    owner.spread_keys.discard(k)
    if ev is not None:
        owner.btgt.pop(ev, None)


def skill_variant(owner, alive, t, next_swing):
    """PROPOSAL (growth sidegrades 10-04, not in Java): a set-specific 烬斩 that REPLACES the shared one (book §4.2:
    replace, never a second active). Same 8 s CD, no crit, does not move the set counter (owner.hits untouched).
      skill_mult      × the 1.5B coefficient
      skill_cap       max targets the shape can catch (line = 3)        skill_plus  extra targets vs the 100° arc (circle)
      skill_ignite    1 = the first skill_ignite_n targets hit are ignited with the 焚烬 burn × skill_burn (EmberBurnBook
                      refresh rules: a weaker skill_burn poisons the snapshot of an already-burning boss → keep it 1.0)
      skill_charge    seconds of wind-up: the hit lands after it, no swing meanwhile
      skill_shield    shield = this × H per target hit (cap skill_shield_max × H), lasts skill_shield_secs, not stacking
    Row-1 / row-3 growth multipliers apply to the HIT only (dmult), never to the shield size."""
    st, cfg, kn = owner.st, owner.cfg, owner.kn
    n = max(1, min(int(gm(st, 'skill_cap', 5)), getattr(owner, '_sh', kn.skill_hits) + int(gm(st, 'skill_plus', 0)), 5))
    caught = alive[:n]
    ch = gm(st, 'skill_charge', 0.0)
    for m in caught:
        owner.hit(m, cfg['skill_mult'] * gm(st, 'skill_mult') * st['B'] * dmult(st, m, t + ch, owner), 'skill')
    rec = owner.rec
    if gm(st, 'skill_ignite', 0.0) > 0 and st['set'] == 'scorch':
        for m in caught[:int(gm(st, 'skill_ignite_n', 5))]:
            ignite(owner, m, t, gm(st, 'skill_burn'))
            if rec is not None and m['hp'] > 0:
                rec['n_skill_ignite'] += 1
    sh = gm(st, 'skill_shield', 0.0)
    if sh > 0:
        got = sum(1 for m in caught) * sh * st['H']
        owner.shield = max(getattr(owner, 'shield', 0.0) if t < getattr(owner, 'shield_until', -1.0) else 0.0,
                           min(got, gm(st, 'skill_shield_max', 1.0) * st['H']))
        owner.shield_until = t + gm(st, 'skill_shield_secs', 5.0)
        if rec is not None:
            rec['shield_given'] += owner.shield
    return next_swing + ch


def burn_ticks(owner, t, sink=None):
    """deal the burn ticks due at t: × set_dmg × the target-class / dodge-window multipliers at tick time
    (EmberGrowthService.outMult); a dead target's burn is dropped without damage"""
    st = owner.st
    for k, amt in owner.burns.due(burnbook.ms(t)):
        x = owner.btgt.get(k)
        if x is None or x['hp'] <= 0:
            owner.burns.remove(k)
            owner.btgt.pop(k, None)
            continue
        dmg = amt * gm(st, 'set_dmg') * dmult(st, x, t, owner)
        if hasattr(owner, 'hit'):
            owner.hit(x, dmg, 'spread' if k in owner.spread_keys else 'burn')
        else:
            x['hp'] -= dmg


def spread_burn(owner, mobs, t):
    """D141 燎原 (EmberSetService.onMobDeath): a burning enemy that died passes what is left of its burn (at most
    burn_spread s, same snapshot) to an enemy that is not burning yet (the server: nearest within 4 blocks; the model has
    no positions: the first one in the room list), at most once per spread_icd s."""
    st = owner.st
    for m in mobs:
        if m['hp'] <= 0 and not m.get('_spread_seen_%d' % id(owner)):
            m['_spread_seen_%d' % id(owner)] = True
            k = m.get('uid')
            if k is None or owner.burns.get(k) is None or t < owner.spread_cd:
                continue
            nxt = next((x for x in mobs if x['hp'] > 0 and owner.burns.get(uid(x)) is None), None)
            if nxt is not None and owner.burns.transfer(k, uid(nxt), burnbook.ms(t), burnbook.ms(gm(st, 'burn_spread', 0))):
                owner.btgt.pop(k, None)
                owner.btgt[uid(nxt)] = nxt
                owner.spread_keys.add(uid(nxt))
                owner.spread_cd = t + gm(st, 'spread_icd', 0.0)
                rec = getattr(owner, 'rec', None)
                if rec is not None:
                    rec['n_spread'] += 1


# ---------------------------------------------------------------- PROPOSAL D210 skill kit (S0 offline, not in Java)
# DESIGN-ember-skill-kit-2026-10-06.md. Opt-in growth keys; absent keys = today's model, bit-identical. Every kit roll
# uses its own time-seeded Random, so the dodge / crit / spawn streams are never shifted.
#   守招 (Q, one of):  kit_guard_cd / kit_guard_secs / kit_guard_red   守墓壁垒: pressed on a boss telegraph when ready;
#                      −red on every hit for secs, no swing meanwhile (the cost)
#                      kit_int_cd / kit_int_p                          破势: pressed on a boss telegraph when ready; with
#                      prob p the move is cancelled (no hit, no whiff/dodge procs); 0.5 s no swing; never on 破招 moves
#   副招 (sneak+F, one of; cast on a swing slot, instant like 烬斩 (kit_sec_cost: optional swing delay); no crit,
#                      no set counter):
#                      kit_gather_cd / kit_gather_plus / kit_gather_secs   聚火: trash only (no boss in the fight),
#                      >= 3 alive; for secs 烬斩 / 烬爆 catch +plus (cap 5) and one more melee body engages (the cost)
#                      kit_dash_cd / kit_dash_mult / kit_dash_n / kit_dash_boss   烬突: >= 2 non-boss alive; mult × B to
#                      the first n alive (boss × kit_dash_boss)
#                      kit_mark_cd / kit_mark_delay / kit_mark_boss    灰印: front target's next attack pushed back by
#                      delay s (boss: × kit_mark_boss) — the Slowness I model; no damage
#   身法 (sneak+Q):    kit_step_cd / kit_step_n / kit_step_cost        火痕步 (焚烬 only): on cd ignite the first n alive
#                      with the 焚烬 burn ×1.0 (C4); the step costs cost × one swing period
#   shared cooldowns (S0 recommendation, WoW 1.12 shocks-style): kit_sec_shared = the 副招 spends the 烬斩 charge (8 s,
#                      no own cd; 灰印 then only on boss fights); kit_q_shared = 守招 uses 身法's cooldown → while it is
#                      down boss telegraphs keep only kit_q_keep × tele_bonus (default 0: no step to dodge with),
#                      the triggering one included · kit_guard_charge: 守墓壁垒 spends the 烬斩 charge instead
#                      (usable only while 烬斩 is ready; 烬斩 then waits a full cd) · kit_mark_mult / kit_gather_mult: the 副招's own hit (× B) when shared
#   S0b 守招 (D212):   kit_guard_defer / kit_guard_pay   醉拳: during the guard window damage is pooled (HP untouched);
#                      after the window it is paid evenly over kit_guard_pay seconds (total taken unchanged; only a
#                      potion / heal window). kit_guard_red is ignored when defer is on.
#                      kit_guard_heavy   only press on a non-light boss telegraph (no light:true; break_hp / heavy main
#                      moves). Light / follow-up / unknown skills never trigger the press — so 守招 and 踏步 do not
#                      overlap on the same dodgeable cone/line.
#                      kit_guard_nobusy / kit_guard_busy_secs  absorb without locking swings (or shorter lock).
KIT_KEYS = frozenset(('kit_guard_cd', 'kit_int_cd', 'kit_gather_cd', 'kit_dash_cd', 'kit_mark_cd', 'kit_step_cd'))


def _krng(owner, salt):
    return random.Random(int(owner.t * 1000) * 7919 + salt)


def kit_tele(owner, skill=None, raw=0.0):
    """a boss telegraph is about to land: maybe press 守招. Returns None when 破势 cancelled it, else True.
    skill = the boss skill dict (S0b: kit_guard_heavy skips light:true / unknown).
    raw = pre-mitigation skill damage (S0b kit_guard_lethal: only press if the hit threatens)."""
    st, t, rec = owner.st, owner.t, owner.rec
    gc = gm(st, 'kit_guard_cd', 0.0)
    chg = gm(st, 'kit_guard_charge', 0.0) > 0  # 守招 spends the 烬斩 charge (no own cd): ready only when 烬斩 is
    heavy_ok = True
    if gm(st, 'kit_guard_heavy', 0.0) > 0:  # S0b: only non-light telegraphs (break channels / main heavies)
        heavy_ok = bool(skill) and not skill.get('light')
    hp_ok = True
    hp_gate = gm(st, 'kit_guard_hp', 0.0)  # S0b: only press when current HP fraction < gate (0 = always)
    if hp_gate > 0:
        hp_ok = (owner.hp / owner.st['H']) < hp_gate
    lethal_ok = True
    if gm(st, 'kit_guard_lethal', 0.0) > 0 and raw > 0:  # S0b: only if hit would drop HP below fraction (default 0.35)
        est = raw * owner.st['M']
        thr = gm(st, 'kit_guard_lethal', 0.35) * owner.st['H']
        lethal_ok = (owner.hp - est) < thr
    if gc > 0 and heavy_ok and hp_ok and lethal_ok and t >= getattr(owner, 'guard_ready', -1.0) and (not chg or t >= getattr(owner, '_nsk', 1e18)):
        if chg:
            owner._charge_block = t + owner.cfg['skill_cd']
        owner.guard_ready = t + gc
        secs = gm(st, 'kit_guard_secs', 2.0)
        owner.guard_until = t + secs
        # S0b: kit_guard_nobusy = can still swing during absorb (cost is CD / shared only); else busy for the window
        # kit_guard_busy_secs overrides the busy length (default = absorb window)
        if gm(st, 'kit_guard_nobusy', 0.0) <= 0:
            bsec = gm(st, 'kit_guard_busy_secs', secs)
            owner.busy_until = max(getattr(owner, 'busy_until', -1.0), t + bsec)
        if gm(st, 'kit_guard_defer', 0.0) > 0:  # S0b 醉拳: start (or refresh) the absorb window; payback after
            owner.guard_pool = getattr(owner, 'guard_pool', 0.0)
            owner.guard_pay_t0 = owner.guard_until
            owner.guard_pay_t1 = owner.guard_until + gm(st, 'kit_guard_pay', 4.0)
            owner.guard_pay_next = owner.guard_pay_t0
        if rec is not None:
            rec['n_guard'] += 1
    ic = gm(st, 'kit_int_cd', 0.0)
    ichg = gm(st, 'kit_int_charge', 0.0) > 0
    if ic > 0 and not getattr(owner, '_noint', False) and t >= getattr(owner, 'int_ready', -1.0) and (not ichg or t >= getattr(owner, '_nsk', 1e18)):
        if ichg:
            owner._charge_block = t + owner.cfg['skill_cd']
        owner.int_ready = t + ic
        owner.busy_until = max(getattr(owner, 'busy_until', -1.0), t + 0.5)
        if _krng(owner, 31).random() < gm(st, 'kit_int_p', 0.6):
            if rec is not None:
                rec['n_int'] += 1
            return None
        if rec is not None:
            rec['n_int_fail'] += 1
    return True


def kit_swing(owner, alive, t, boss, period):
    """a swing slot: guard stance blocks it; a ready 副招 is cast. Returns the next swing time, or None = swing."""
    st, cfg, rec = owner.st, owner.cfg, owner.rec
    bu = getattr(owner, 'busy_until', -1.0)
    if t < bu:
        return bu
    if gm(st, 'kit_sec_shared', 0.0) > 0 or t < getattr(owner, 'sec_ready', -1.0):
        return None
    if kit_sec(owner, alive, t, boss):
        owner.sec_ready = t + max(gm(st, 'kit_gather_cd', 0.0), gm(st, 'kit_dash_cd', 0.0), gm(st, 'kit_mark_cd', 0.0))
        return _sec_cost(owner, t, period)
    return None


def kit_sec(owner, alive, t, boss):
    """cast the 副招 if its use condition holds (聚火: trash, >= 3 alive · 烬突: >= 2 non-boss · 灰印: always). True = cast.
    kit_sec_shared: it spends the 烬斩 charge instead of its own cooldown (the caller then skips 烬斩)."""
    st, rec = owner.st, owner.rec
    nb = [m for m in alive if m['role'] != 'boss']
    if gm(st, 'kit_gather_cd', 0.0) > 0:
        if boss is None and len(alive) >= 3 and t >= getattr(owner, 'gather_until', -1.0):
            owner.gather_until = t + gm(st, 'kit_gather_secs', 4.0)
            for m in alive[:min(5, owner.kn.skill_hits)]:
                if gm(st, 'kit_gather_mult', 0.0) > 0:  # the pull pulse itself (× B, no crit)
                    owner.hit(m, gm(st, 'kit_gather_mult', 0.0) * st['B'] * dmult(st, m, t, owner), 'dash')
            if rec is not None:
                rec['n_gather'] += 1
            return True
    elif gm(st, 'kit_dash_cd', 0.0) > 0:
        if len(nb) >= 2:
            for m in alive[:int(gm(st, 'kit_dash_n', 3))]:
                k = gm(st, 'kit_dash_boss', 0.5) if m['role'] == 'boss' else 1.0
                owner.hit(m, gm(st, 'kit_dash_mult', 0.5) * k * st['B'] * dmult(st, m, t, owner), 'dash')
            if rec is not None:
                rec['n_dash'] += 1
            return True
    elif gm(st, 'kit_mark_cd', 0.0) > 0:
        if boss is not None or gm(st, 'kit_sec_shared', 0.0) <= 0:
            tgt = alive[0]
            if gm(st, 'kit_mark_mult', 0.0) > 0:  # the brand's hit (single target, × B, no crit)
                owner.hit(tgt, gm(st, 'kit_mark_mult', 0.0) * st['B'] * dmult(st, tgt, t, owner), 'dash')
            if tgt['atk'] > 0:
                tgt['next'] += gm(st, 'kit_mark_delay', 0.5) * (gm(st, 'kit_mark_boss', 0.5) if tgt['role'] == 'boss' else 1.0)
            if rec is not None:
                rec['n_mark'] += 1
            return True
    return False


def _sec_cost(owner, t, period):
    """a 副招 cast is instant like 烬斩 (Java: no swing reset) → None = the swing still happens; kit_sec_cost > 0 delays
    it by that share of a swing period (sweep knob)"""
    c = gm(owner.st, 'kit_sec_cost', 0.0)
    return None if c <= 0 else t + c * period


def kit_step(owner, alive, t, period):
    """火痕步 on cooldown (焚烬 only): ignite the first n alive; returns the swing delay it costs."""
    st = owner.st
    sc = gm(st, 'kit_step_cd', 0.0)
    if sc <= 0 or st['set'] != 'scorch' or t < getattr(owner, 'step_ready', -1.0):
        return 0.0
    owner.step_ready = t + sc
    for m in alive[:int(gm(st, 'kit_step_n', 2))]:
        ignite(owner, m, t, 1.0)
    if owner.rec is not None:
        owner.rec['n_step'] += 1
    return gm(st, 'kit_step_cost', 0.5) * period


class Fight:
    def __init__(self, cfg, st, kn, rng, potions, crit_rng=None, spawn_rng=None):
        # M02: rng = incoming hits (dodge rolls); crits and mid-fight spawns (split / boss adds) have own streams, so a
        # config that changes the number of swings does not shift the dodge rolls (callers without them: derived here)
        self.cfg, self.st, self.kn, self.rng = cfg, st, kn, rng
        self.crit_rng = crit_rng if crit_rng is not None else random.Random(rng.getrandbits(64))
        self.spawn_rng = spawn_rng if spawn_rng is not None else random.Random(rng.getrandbits(64))
        # M03: the player's 焚烬 burns = the Java EmberBurnBook (discrete 1 s ticks, snapshot, refresh = end only, ≤ 5)
        self.burns = burnbook.BurnBook(burnbook.TICKS + max(0, int(gm(st, 'burn_ticks', 0))))
        self.btgt = {}          # burn key → mob dict (EmberSetService.burnTargets)
        self.spread_keys = set()  # RECORD label: burns that arrived by 燎原
        self.spread_cd = -1.0
        self.hp = st['H']
        self.potions = potions
        self.used = 0
        self.t = 0.0
        self.pcd = 0.0
        self.hits = 0
        self.burst_cd = 0.0
        self.sus_cd = 0.0
        self.taken = 0.0
        self.nhit = 0           # D191: hits that landed (after dodge), for the 无伤 unscathed event
        self.rec = __import__('collections').Counter() if RECORD else None
        self.min_hp = 1.0

    def hit(self, tgt, amount, key):
        """apply `amount` damage to tgt; with RECORD, count the effective part (no overkill) under d_<key>, by target
        class (on_boss / on_affix / on_mob) and the after-dodge window's extra share"""
        rec = self.rec
        if rec is not None and tgt['hp'] > 0:
            if tgt.get('affix') and getattr(self, 'affix_t0', None) is None:
                self.affix_t0 = self.t  # first damage on the affixed elite (or its adds) this run
            eff = min(amount, tgt['hp'])
            rec['d_' + key] += eff
            rec['on_boss' if tgt['role'] == 'boss' else ('on_affix' if tgt.get('affix') else 'on_mob')] += eff
            dd = gm(self.st, 'dodge_dmg')
            if dd != 1.0 and self.t < getattr(self, 'dodge_until', -1.0):
                rec['d_window_extra'] += eff * (1 - 1 / dd)
            if tgt['role'] == 'boss':
                rec['d_boss_all'] += eff
                if self.t < getattr(self, 'win_until', -1.0):
                    rec['d_boss_win'] += eff
        tgt['hp'] -= amount

    def hurt(self, raw, tele, kind='mob', affix=False, skill=None):
        kn, st = self.kn, self.st
        p = min(0.95, kn.dodge + kn.tele_bonus) if tele else kn.dodge
        rec = self.rec
        if kind == 'tele' and st.get('mods') and KIT_KEYS & st['mods'].keys():  # PROPOSAL D210 skill kit (not in Java)
            if kit_tele(self, skill, raw) is None:
                return None  # 破势: the move was interrupted (no hit, no dodge / whiff effects)
            if gm(st, 'kit_q_shared', 0.0) > 0 and self.t < max(getattr(self, 'guard_ready', -1.0), getattr(self, 'int_ready', -1.0)):
                p = min(0.95, kn.dodge + kn.tele_bonus * gm(st, 'kit_q_keep', 0.0))  # 守招 used 身法's cooldown: the
                # telegraph bonus drops to kit_q_keep of itself until the step is back (this hit included)
        if kind == 'tele' and st.get('mods') and 'win_dmg' in st['mods']:  # PROPOSAL 破绽窗口: opens on every boss telegraph
            self.win_until = self.t + gm(st, 'win_secs', 0.0)
            if rec is not None:
                rec['n_window'] += 1
        if self.rng.random() < p:
            if rec is not None:
                rec['n_dodge_' + ('tele' if kind == 'tele' else ('blaze' if tele and affix else 'mob'))] += 1
            if kind == 'tele' and st.get('mods'):  # D141: a dodged boss telegraph
                self.dodge_until = self.t + gm(st, 'dodge_secs', 0.0)
                dh = gm(st, 'dodge_heal', 0.0)
                if dh > 0 and self.t >= getattr(self, 'dodge_heal_cd', -1.0):
                    self.dodge_heal_cd = self.t + gm(st, 'dodge_icd', 6.0)
                    if rec is not None:
                        rec['heal_dodge'] += min(st['H'], self.hp + dh * st['H']) - self.hp; rec['n_dodge_heal'] += 1
                    self.hp = min(st['H'], self.hp + dh * st['H'])
                if gm(st, 'dodge_burst', 0.0) > 0:  # D141 借势: the 烬爆 counter jumps by this many swings (icd unchanged)
                    self.hits += int(gm(st, 'dodge_burst', 0.0))
                    if rec is not None:
                        rec['n_dodge_burst'] += 1
            return True  # D192: dodged (callers that ignore the value are unchanged)
        if st.get('mods'):
            raw *= gm(st, 'taken_' + kind) * (gm(st, 'taken_affix') if affix else 1.0) * gm(st, 'taken_all')
            if self.cfg.get('abyss'):
                raw *= gm(st, 'abyss_taken')
            if kind == 'tele' and gm(st, 'hit_burst', 0.0) > 0:  # D141 反震: hit by a boss telegraph → 烬爆 counter + n
                self.hits += int(gm(st, 'hit_burst', 0.0))
                if rec is not None:
                    rec['n_hit_burst'] += 1
        dmg = raw * self.st['M']
        if self.t < getattr(self, 'guard_until', -1.0):  # PROPOSAL D210 守墓壁垒 / S0b 醉拳
            if gm(st, 'kit_guard_defer', 0.0) > 0:  # S0b: pool the hit; pay it back after the window
                # kit_guard_pay_mult > 1 = tax (pay back more than absorbed) — opportunity cost without sharing 身法
                pooled = dmg * gm(st, 'kit_guard_pay_mult', 1.0)
                self.guard_pool = getattr(self, 'guard_pool', 0.0) + pooled
                if rec is not None:
                    rec['guard_defer'] = rec.get('guard_defer', 0.0) + pooled
                    rec['n_guard_hit'] = rec.get('n_guard_hit', 0) + 1
                dmg = 0.0
            else:
                if rec is not None:
                    rec['guard_abs'] += dmg * gm(st, 'kit_guard_red', 0.4)
                dmg *= 1.0 - gm(st, 'kit_guard_red', 0.4)
        if getattr(self, 'shield', 0.0) > 0:  # PROPOSAL 护心斩 (set-specific 烬斩, not in Java): absorbs before HP
            if self.t < self.shield_until:
                a = min(self.shield, dmg)
                self.shield -= a
                dmg -= a
                if rec is not None:
                    rec['shield_abs'] += a
            else:
                self.shield = 0.0
        self.hp -= dmg
        self.taken += dmg
        self.nhit += 1
        if rec is not None:
            rec['taken_' + ('affix' if affix else kind)] += dmg
            rec['n_hit_' + ('tele' if kind == 'tele' else ('blaze' if tele and affix else 'mob'))] += 1
            self.min_hp = min(self.min_hp, max(0.0, self.hp) / self.st['H'])
        if self.hp > 0 and self.hp < kn.potion_at * self.st['H'] and self.potions > 0 and self.t >= self.pcd:
            self.potions -= 1
            self.used += 1
            self.pcd = self.t + self.cfg['potion_cd']
            if rec is not None:
                rec['heal_potion'] += min(self.st['H'], self.hp + self.cfg['potion_pct'] * gm(self.st, 'potion') * self.st['H']) - self.hp
            self.hp = min(self.st['H'], self.hp + self.cfg['potion_pct'] * gm(self.st, 'potion') * self.st['H'])

    def _guard_pay(self, t):
        """S0b 醉拳 payback: drain guard_pool evenly over [guard_pay_t0, guard_pay_t1]. Sets guard_pay_next."""
        pool = getattr(self, 'guard_pool', 0.0)
        t0 = getattr(self, 'guard_pay_t0', -1.0)
        t1 = getattr(self, 'guard_pay_t1', -1.0)
        if pool <= 0 or t1 <= t0:
            self.guard_pool = 0.0
            self.guard_pay_next = None
            return None
        if t < t0:
            self.guard_pay_next = t0
            return t0
        step = 0.25
        nxt = min(t1, t + step)
        take = pool if nxt >= t1 else pool * ((nxt - t) / (t1 - t))
        take = min(pool, max(0.0, take))
        self.guard_pool = pool - take
        if take > 0:
            self.hp -= take
            self.taken += take
            self.nhit += 1
            rec = self.rec
            if rec is not None:
                rec['taken_guard_pay'] = rec.get('taken_guard_pay', 0.0) + take
                rec['n_guard_pay'] = rec.get('n_guard_pay', 0) + 1
                self.min_hp = min(self.min_hp, max(0.0, self.hp) / self.st['H'])
            if self.hp > 0 and self.hp < self.kn.potion_at * self.st['H'] and self.potions > 0 and t >= self.pcd:
                self.potions -= 1
                self.used += 1
                self.pcd = t + self.cfg['potion_cd']
                heal = self.cfg['potion_pct'] * gm(self.st, 'potion') * self.st['H']
                if rec is not None:
                    rec['heal_potion'] += min(self.st['H'], self.hp + heal) - self.hp
                self.hp = min(self.st['H'], self.hp + heal)
        if self.guard_pool <= 1e-9 or nxt >= t1:
            self.guard_pool = 0.0
            self.guard_pay_next = None
        else:
            self.guard_pay_next = nxt
        return self.guard_pay_next

    def segment(self, mobs, boss=None, mapdef=None):
        """Fight until every mob (and the boss) is dead or the player dies. Returns True on clear."""
        cfg, kn, st, rng = self.cfg, self.kn, self.st, self.rng
        period = kn.swing / kn.uptime
        t = self.t
        next_swing = t + 1.0
        next_skill = t + 1.0
        self._nsk = next_skill
        skills = []
        adds_done = False
        if boss is not None:
            for s in mapdef['boss'].get('skills', []):
                if s.get('break_hp') and not BREAK:
                    continue  # D193 baseline: the break channel does not exist
                skills.append({'s': s, 'next': t + s['every']})
        pending = []  # (time, raw dmg) of follow-up hits
        while True:
            if self.rec is not None:
                for m in mobs:
                    if m.get('affix') and m['hp'] <= 0 and not m.get('_kt'):
                        m['_kt'] = True
                        self.affix_dead_at = t
            for m in mobs:  # D138 分裂: the affixed elite splits on death (the door waits for the adds)
                if m['hp'] <= 0 and m.get('split'):
                    n, share, md = m.pop('split')
                    for _ in range(n):
                        a = mob(cfg, md, 'melee', self.spawn_rng, kn, t + 1.0)
                        a['hp'] *= share
                        a['affix'] = True  # D141: the split adds count as the affixed elite (破缀 / 抗缀)
                        a['split_add'] = True
                        a['affix_kind'] = 'split'
                        mobs.append(a)
            if len(self.burns) and st.get('mods') and gm(st, 'burn_spread', 0) > 0:
                spread_burn(self, mobs, t)  # D141 燎原 (EmberSetService.onMobDeath)
            for m in mobs:  # D191: death time of every room mob (连斩 chain / 裂隙 breach models)
                if m['hp'] <= 0 and '_dt' not in m:
                    m['_dt'] = t
            alive = [m for m in mobs if m['hp'] > 0]
            if not alive:
                self.t = t
                return True
            # next event
            cand = [next_swing]
            melee_alive = [m for m in alive if m['role'] in MELEE_ROLES or m['role'] == 'boss']
            engaged = set(id(m) for m in melee_alive[:kn.engage + (1 if t < getattr(self, 'gather_until', -1.0) else 0)])
            for m in alive:
                if m['atk'] > 0:
                    cand.append(m['next'])
                if 'blaze' in m:
                    cand.append(m['blaze'][0])
            for s in skills:
                cand.append(s['next'])
            for p in pending:
                cand.append(p[0])
            gpn = getattr(self, 'guard_pay_next', None)
            if gpn is not None:
                cand.append(gpn)
            nd = self.burns.next_due()
            if nd is not None:
                cand.append(nd / 1000.0)
            tn = min(cand)
            t = tn
            self.t = t
            if len(self.burns):  # M03: discrete burn ticks due now (EmberSetService.burnTick)
                burn_ticks(self, t)
            if FEST:
                fest_proc(self, alive, t, st['B'], kn.skill_hits)
            if gpn is not None and t == gpn:  # S0b 醉拳 payback tick
                self._guard_pay(t)
                if self.hp <= 0:
                    return False
                continue
            if t == next_swing:
                next_swing = t + period
                if st.get('mods') and KIT_KEYS & st['mods'].keys():  # PROPOSAL D210 skill kit (not in Java)
                    _ks = kit_swing(self, alive, t, boss, period)
                    if _ks is not None:
                        next_swing = max(next_swing, _ks)
                        continue  # the swing slot was spent (guard stance / secondary cast)
                    self._sh = min(5, kn.skill_hits + int(gm(st, 'kit_gather_plus', 0))) if (boss is None and t < getattr(self, 'gather_until', -1.0)) else kn.skill_hits
                    next_swing += kit_step(self, alive, t, period)
                tgt = alive[0]
                crit = self.crit_rng.random() < cfg['crit_rate']
                self.hit(tgt, st['B'] * (cfg['crit_mult'] if crit else 1.0) * dmult(st, tgt, t, self), 'swing')
                self.hits += 1
                hold = getattr(kn, 'hold_skill', False) and boss is not None and t >= getattr(self, 'win_until', -1.0)
                if boss is not None and st.get('mods') and (gm(st, 'kit_guard_charge', 0.0) > 0 or gm(st, 'kit_int_charge', 0.0) > 0):
                    hold = True  # PROPOSAL D210: a 守招-on-charge player keeps the charge for the next boss telegraph
                if t < getattr(self, '_charge_block', -1.0):  # PROPOSAL D210: the charge went to 守招
                    next_skill = max(next_skill, self._charge_block)
                if t >= next_skill and not hold:  # hold_skill (behaviour knob): keep 烬斩 for the 破绽窗口
                    if st.get('mods') and gm(st, 'kit_sec_shared', 0.0) > 0 and kit_sec(self, alive, t, boss):
                        pass  # PROPOSAL D210: the 烬斩 charge was spent on the 副招
                    elif st.get('mods') and 'skill_var' in st['mods']:
                        next_swing = skill_variant(self, alive, t, next_swing)
                    else:
                        for m in alive[:getattr(self, '_sh', kn.skill_hits)]:
                            self.hit(m, cfg['skill_mult'] * st['B'] * dmult(st, m, t, self), 'skill')
                    next_skill = t + cfg['skill_cd']
                self._nsk = next_skill
                bev = cfg['burst_every'] + int(gm(st, 'burst_every', 0))
                if st['set'] == 'burst' and self.hits >= bev and t >= self.burst_cd:
                    caught = alive[:min(getattr(self, '_sh', kn.skill_hits), 5)]
                    bm = burst_n_mult(st, len(caught))
                    for m in caught:
                        self.hit(m, cfg['burst'][st['awk']] * gm(st, 'burst_mult') * bm * gm(st, 'set_dmg') * st['B'] * dmult(st, m, t, self), 'burst')
                    if self.rec is not None:
                        self.rec['n_burst'] += 1
                    self.hits = 0
                    self.burst_cd = t + cfg['burst_icd']
                elif st['set'] == 'scorch' and self.hits >= cfg['scorch_every']:
                    ignite(self, tgt, t)
                    if self.rec is not None:
                        self.rec['n_burn'] += 1
                    self.hits = 0
                elif st['set'] == 'sustain' and self.hits >= sustain_every(cfg, st, self.hp) and t >= self.sus_cd:
                    sm = gm(st, 'sustain_mult') * (gm(st, 'sustain_low_mult') if self.hp < gm(st, 'sustain_low', 0.0) * st['H'] else 1.0)
                    if self.rec is not None:
                        self.rec['heal_sustain'] += min(st['H'], self.hp + cfg['sustain_pct'][st['awk']] * sm * st['H']) - self.hp
                        self.rec['n_sustain'] += 1
                        if self.hp < 0.5 * st['H']:
                            self.rec['n_sustain_low'] += 1
                    self.hp = min(st['H'], self.hp + cfg['sustain_pct'][st['awk']] * sm * st['H'])
                    self.hits = 0
                    self.sus_cd = t + cfg['sustain_icd']
                elif st['set'] in ('burst', 'sustain'):
                    self.hits = min(self.hits, max(bev, cfg['sustain_every'] + int(gm(st, 'sustain_every', 0))))
                if boss is not None and not adds_done and 'adds' in mapdef['boss'] and 0 < boss['hp'] <= mapdef['boss']['adds']['at_hp'] * boss['max']:
                    adds_done = True
                    for _ in mapdef['boss']['adds']['points']:
                        mobs.append(mob(cfg, mapdef, mapdef['boss']['adds']['role'], self.spawn_rng, kn, t + 1.0))
                if FEST:
                    fest_proc(self, alive, t, st['B'], kn.skill_hits)
                continue
            blazed = False
            for m in alive:  # D138 炽热: a warned fire circle at the elite's feet
                if 'blaze' in m and m['blaze'][0] == t:
                    nt, ev, dmg = m['blaze']
                    m['blaze'] = (t + ev, ev, dmg)
                    self.hurt(dmg, True, 'mob', True)
                    blazed = True
                    break
            if blazed:
                if self.hp <= 0:
                    return False
                continue
            for m in alive:
                if m['atk'] > 0 and m['next'] == t:
                    m['next'] = t + m['iv']
                    if (m['role'] in MELEE_ROLES or m['role'] == 'boss') and id(m) not in engaged:
                        break
                    self.hurt(m['atk'], m['tele'], 'boss' if m['role'] == 'boss' else 'mob', bool(m.get('affix')))
                    break
            else:
                for s in skills:
                    if s['next'] == t:
                        sk = s['s']
                        if sk.get('break_hp') and sk.get('below') is not None and boss['hp'] > sk['below'] * boss['max']:
                            s['next'] = t + 0.5  # D193: Java keeps a gated skill due until its HP gate opens
                            break
                        s['next'] = t + sk['every']
                        if sk.get('break_hp') and boss['hp'] > 0:  # D193 破招
                            _br = random.Random(int(t * 1000) * 7919 + int(boss['max']))
                            _n = float(sk['warn']) * BREAK_UPTIME / kn.swing
                            _w = int(_n) + (1 if _br.random() < _n - int(_n) else 0)
                            _d = sum(st['B'] * (cfg['crit_mult'] if _br.random() < cfg['crit_rate'] else 1.0) * dmult(st, boss, t, self) for _ in range(_w))
                            if _d >= float(sk['break_hp']) * boss['max']:
                                _st = float(sk.get('break_stun', 1.0))
                                boss['next'] = max(boss['next'], t) + _st
                                for _s in skills:
                                    if _s is not s:
                                        _s['next'] += _st
                                s['next'] += _st
                                BREAK_STATS[0] += 1
                                if self.rec is not None:
                                    self.rec['n_break'] = self.rec.get('n_break', 0) + 1
                            else:
                                self._noint = True
                                self.hurt(sk['dmg'], True, 'tele', skill=sk)
                                self._noint = False
                                BREAK_STATS[1] += 1
                                if self.rec is not None:
                                    self.rec['n_break_fail'] = self.rec.get('n_break_fail', 0) + 1
                            break
                        if boss['hp'] > 0 and (sk.get('below') is None or boss['hp'] <= sk['below'] * boss['max']):  # phase gate (P2-6)
                            _dodged = self.hurt(sk['dmg'], True, 'tele', skill=sk)
                            if WHIFF_STUN and sk.get('whiff_stun') and _dodged and boss['hp'] > 0:  # D192 落空破绽 (solo: dodged = whiff)
                                _st = float(sk['whiff_stun']) * WHIFF_ARM
                                boss['next'] = max(boss['next'], t) + _st
                                for _s in skills:
                                    if _s is not s:
                                        _s['next'] += _st
                                s['next'] += _st
                                if self.rec is not None:
                                    self.rec['n_whiff_stun'] = self.rec.get('n_whiff_stun', 0) + 1
                            _wp = (kn.dodge * WALL_STUN_K) if WALL_STUN_P is None else WALL_STUN_P
                            if sk.get('wall_stun') and _wp > 0 and boss['hp'] > 0:  # D188 撞墙破绽 (mean stun per charge)
                                _st = float(sk['wall_stun']) * _wp
                                boss['next'] = max(boss['next'], t) + _st
                                for _s in skills:
                                    if _s is not s:
                                        _s['next'] += _st
                                s['next'] += _st
                            f = sk.get('follow')
                            if f and (f.get('below') is None or boss['hp'] <= f['below'] * boss['max']):
                                pending.append((t + f.get('delay', 1.0), f['dmg']))
                        break
                else:
                    for p in list(pending):
                        if p[0] == t:
                            pending.remove(p)
                            if boss['hp'] > 0:
                                self.hurt(p[1], True, 'tele')
                            break
            if self.hp <= 0:
                return False


VARIETY = True   # D138 repeat-run variety (--no-variety to compare)
VBOUNTY = True   # D144 花样委托 (--no-vbounty to compare)
LAST_VAR = {'affix': False, 'event': False}  # outcome of the last run_map(repeat=True)
LAST_FIGHT = [None]  # RECORD: the Fight of the last run_map (cleared or not)


def variety_roll(cfg, seed):
    """D138: (affix room, affix type, event room) for a repeat normal run; None where not rolled."""
    v = cfg.get('variety') or {}
    vr = random.Random(seed)
    rooms = ('r1', 'r2', 'r3')
    ar = at = er = None
    if vr.random() < float(v.get('affix_rate', 0)) and v.get('affixes'):
        ar = rooms[vr.randrange(3)]
        at = v['affixes'][vr.randrange(len(v['affixes']))]
    if vr.random() < float(v.get('event_rate', 0)):
        er = rooms[vr.randrange(3)]
    return ar, at, er


EVENT_FORCE = None  # D191 gate: force every rolled event to this kind (None = equal-weight pick from variety.events)
EVENT_P_IN = {'melee': 0.9, 'heavy': 0.9, 'elite': 0.9}  # D191 裂隙: share of kills inside the rift by role (others 0.5)


def event_kind(cfg, vseed):
    """D191: event kind of a rolled event room — own stream (vseed-derived), so affix / room picks stay as before."""
    if EVENT_FORCE is not None:
        return EVENT_FORCE
    ev = (cfg.get('variety') or {}).get('events') or ['timed']
    return ev[random.Random(vseed ^ 0x5EED191).randrange(len(ev))]


def chain_best(times, gap):
    """D191 连斩: longest run of kills where each follows the previous within `gap` seconds (EmberRunDirector.chainNext)."""
    best = cur = 0
    prev = None
    for d in sorted(times):
        cur = cur + 1 if prev is not None and d - prev <= gap else 1
        best = max(best, cur)
        prev = d
    return best


def breach_open(cfg, deaths, t0, t_end, rng):
    """D191 裂隙: radius r0 shrinks `shrink`/s; a kill inside adds `grow` (≤ rmax); collapsed at ≤ rmin → fail.
    deaths = [(time, role)]; inside share by role (EVENT_P_IN, else 0.5)."""
    b = (cfg.get('variety') or {}).get('breach') or {}
    r0, rmin, rmax = float(b.get('radius', 5.0)), float(b.get('min', 1.5)), float(b.get('max', 7.0))
    shrink, grow = float(b.get('shrink', 0.15)), float(b.get('grow', 0.8))
    r, prev = r0, t0
    for d, role in sorted(deaths):
        r -= shrink * (d - prev)
        if r <= rmin:
            return False
        if rng.random() < EVENT_P_IN.get(role, 0.5):
            r = min(rmax, r + grow)
        prev = d
    return r - shrink * max(0.0, t_end - prev) > rmin


def event_ok(cfg, kind, f, t0, h0, mobs, vseed):
    """D138 generic (cleared within event_secs) for the D138–D179 kinds; D191 kinds modelled on what they ask."""
    v = cfg['variety']
    if kind == 'unscathed':
        u = v.get('unscathed') or {}
        return f.nhit - h0 <= int(u.get('hits', 4))
    if kind == 'chain':
        c = v.get('chain') or {}
        times = [m['_dt'] for m in mobs if '_dt' in m]
        need = min(int(c.get('need', 4)), max(1, len([m for m in mobs if not m.get('split_add')])))
        return chain_best(times, float(c.get('gap', 4.0))) >= need
    if kind == 'breach':
        return breach_open(cfg, [(m['_dt'], m['role']) for m in mobs if '_dt' in m], t0, f.t,
                           random.Random(vseed ^ 0xB4EAC4))
    return f.t - t0 <= float(v['event_secs'])


AFFIX_STRESS = False  # D196 gate: periodic affix hits start right at room start (t0 + grace_s, D244 table) instead of after one cycle
FIRECHAIN_EXPOSURE = 1.0 / 3.0  # D196: share of the fight a player spends touching the 火链 tether (sim has no positions)


def affix_mob(cfg, m, mobs, kind, t0):
    """Promote the toughest mob of the room (heavy, else melee, else the first) — mirrors EmberRunDirector.
    D244: every number comes from tools/p1sim/affix-table.json (exported from the Java affix primitives; affixtable.py)."""
    pick_ = next((x for x in mobs if x['role'] == 'heavy'), None) or next((x for x in mobs if x['role'] == 'melee'), None) or mobs[0]
    A = affixtable.row(cfg, kind) if kind in affixtable.MODELLED else None
    if kind == 'shield':
        pick_['hp'] *= float(A['hp_mult'])
    elif kind == 'split':
        pick_['split'] = (int(A['count']), float(A['add_hp']), m)
    elif kind in affixtable.PERIODIC:
        # D138 炽热 / D189 毒十字 · 禁锢 / D196 旋光: one telegraphed hit per cast (same dodge path), at the Java cadence:
        # first hit = 1.5 s grace + every + warn (旋光: + spin/2, target mid-arc), period = every + warn (旋光: + spin).
        # The 1 s jailer root is not modelled (no extra hit is forced during it).
        pick_['blaze'] = (t0 + float(A['first_hit_s']), float(A['period_s']), pick_['atk'] * float(A['dmg_atk']))
    elif kind == 'firechain':
        # D196 火链: burns at most once per burn_every_s while touching; the sim has no positions, so a player is assumed
        # to touch the chain FIRECHAIN_EXPOSURE of the time while the elite has a partner (= the room is not yet down to it)
        per = float(A['burn_every_s']) / FIRECHAIN_EXPOSURE
        pick_['blaze'] = (t0 + float(A['first_burn_s']) + per, per, pick_['atk'] * float(A['dmg_atk']))
    if AFFIX_STRESS and 'blaze' in pick_ and kind in ('venom', 'jailer', 'arcane', 'firechain'):
        _, ev, dmg = pick_['blaze']
        pick_['blaze'] = (t0 + affixtable.grace(), ev, dmg)  # worst case: the first cast lands as soon as the room opens
    pick_['affix'] = True
    pick_['affix_kind'] = kind
    return pick_


def run_map(cfg, key, st, kn, rng, potions, repeat=False):
    """One entry. Returns (cleared, potions_used, extra, seconds, damage taken, where it died)."""
    m = cfg['maps'][key]
    # M02: own streams per entry (room layout / incoming hits / crits / mid-fight spawns), all drawn up front, so the
    # caller's rng advances by exactly 4 per entry whatever happens in the fight (paired configs stay paired)
    setup, hits, crits, spawns = (random.Random(rng.getrandbits(64)) for _ in range(4))
    f = Fight(cfg, st, kn, hits, potions, crit_rng=crits, spawn_rng=spawns)
    LAST_FIGHT[0] = f
    extra = ('none', 'treasure', 'elite', 'chest')[pick(cfg['extra_w'], setup.random())]
    ar = at = er = ek = None
    vseed = 0
    if repeat:
        vseed = setup.getrandbits(32)  # drawn either way so --no-variety keeps the same stream shape
        if VARIETY and cfg.get('variety'):
            ar, at, er = variety_roll(cfg, vseed)
            if er is not None:
                ek = event_kind(cfg, vseed)
    LAST_VAR['affix'] = LAST_VAR['event'] = False
    for rk in ('r1', 'r2', 'r3'):
        room = m['rooms'][rk]
        comp = room['a'] if setup.random() < 0.5 else room['b']
        mobs = [mob(cfg, m, role, setup, kn, f.t) for role, n in comp.items() for _ in range(n)]
        am = affix_mob(cfg, m, mobs, at, f.t) if rk == ar else None
        t0 = f.t
        if f.rec is not None:
            u0, k0 = f.used, f.taken
        h0 = f.nhit
        if not f.segment(mobs):
            if f.rec is not None:
                f.rec['died_' + ('affix' if am is not None else 'trash')] += 1
            return False, f.used, extra, f.t, f.taken, rk
        if f.rec is not None:
            seg = ('affix_' + at) if am is not None else 'trash'
            f.rec['t_' + seg] += f.t - t0; f.rec['k_' + seg] += f.taken - k0; f.rec['p_' + seg] += f.used - u0
            f.rec['c_' + seg] += 1
            if am is not None:
                f.rec['ttk_affix_' + at] += getattr(f, 'affix_dead_at', f.t) - t0
                f.rec['own_affix_' + at] += getattr(f, 'affix_dead_at', f.t) - (getattr(f, 'affix_t0', None) or t0)
        if am is not None:
            LAST_VAR['affix'] = True
        if rk == er and event_ok(cfg, ek, f, t0, h0, mobs, vseed):
            LAST_VAR['event'] = True
        if m.get('event', {}).get('after') == rk and extra in ('treasure', 'elite'):
            if not f.segment([mob(cfg, m, extra, setup, kn, f.t)]):
                return False, f.used, extra, f.t, f.taken, 'event'
    b = m['boss']
    boss = {'hp': float(b['hp']), 'max': float(b['hp']), 'atk': b['atk'], 'iv': b.get('interval', 3.0), 'role': 'boss',
            'next': f.t + 2.0, 'tele': False, 'uid': next(_UID)}
    if f.rec is not None:
        t0, u0, k0 = f.t, f.used, f.taken
    if not f.segment([boss], boss=boss, mapdef=m):
        if f.rec is not None:
            f.rec['died_boss'] += 1
        return False, f.used, extra, f.t, f.taken, 'boss'
    if f.rec is not None:
        f.rec['t_boss'] += f.t - t0; f.rec['k_boss'] += f.taken - k0; f.rec['p_boss'] += f.used - u0; f.rec['c_boss'] += 1
    LAST_FIGHT[0] = f
    return True, f.used, extra, f.t, f.taken, None


def pick(weights, u):
    tot = sum(weights)
    x = u * tot
    acc = 0
    for i, w in enumerate(weights):
        acc += w
        if x < acc:
            return i
    return len(weights) - 1


# ---------------------------------------------------------------- progression

def item(fam, slot, tier, q=0, f=0, src='drop'):
    return {'fam': fam, 'slot': slot, 'tier': tier, 'q': q, 'f': f, 'enh': 0, 'pity': 0, 'src': src}


def desc(it):
    qn = ('标准', '精良', '卓越', '极品')[it['q']]
    fam = {'burst': '爆', 'scorch': '焚', 'sustain': '愈', 'none': '无'}[it['fam']]
    return 'T%d%s%s%s+%d' % (it['tier'], fam, '刃' if it['slot'] == 'blade' else '符', qn if it['q'] else '', it['enh'])


class Player:
    def __init__(self, cfg, kn, rng):
        # M02: drops / enhancement rolls get an own stream (one draw from the caller's), so a clear in one config and a
        # failure in the other does not shift every later entry's layout and combat rolls
        self.cfg, self.kn, self.rng = cfg, kn, random.Random(rng.getrandbits(64))
        self.blade = item('none', 'blade', 0, src='starter')
        self.charm = item('none', 'charm', 0, src='starter')
        self.armor = [item('none', a, 0, src='starter') for a in ARMOR_SLOTS] if SIX is not None else None
        self.coin = self.shard = self.core = self.bone = self.blank = self.xp = 0
        self.potions = sourcemap.starter_kit(cfg['starter_potions'])['potions']  # D243: S35 starter kit (= starter.heal_potions)
        self.codex, self.codex_paid, self.codex_coin = set(), 0, 0
        self.codex_see(self.blade); self.codex_see(self.charm)  # the T0 kit pieces are codex entries 1 and 2
        if CODEX:
            CODEX_STATS['players'] = CODEX_STATS.get('players', 0) + 1
        self.marks = {1: 0, 2: 0, 3: 0}
        self.cleared = set()
        self.save_for_upgrade = False
        self.day, self._bday, self._bn = None, None, 0  # P2-7 daily bounty: loops set p.day before settle()

    def st(self):
        return stats(self.cfg, self.blade, self.charm, level_of(self.cfg, self.xp), self.cleared, self.armor)

    def roll_armor(self, tier, key=None):
        """STAGE 0: the extra armor drop of a completed run — family by the same target / map rule as roll_item, slot
        uniform (SIX['gap']: a slot whose worn piece is below this tier / not the target family is twice as likely),
        quality / craft from the same tables"""
        if SIX.get('arm_cap') == 'all':  # rule option: an armor drop never exceeds the worn charm's tier
            tier = min(tier, max(1, self.charm['tier']))
        it = self.roll_item(tier, key)
        r = self.rng
        ws = [2.0 if SIX.get('gap') and (a['tier'] < tier or a['fam'] != self.kn.target) else 1.0 for a in self.armor]
        it['slot'] = ARMOR_SLOTS[pick(ws, r.random())]
        return it

    def roll_base(self, tier, key=None):
        """D246 (today's structure): the settlement base roll covers all six slots. SIX['base_armor'] = a: the roll is an
        armor piece with probability a (else blade / charm by roll_item). SIX['base_mapslot']: a map with a loot slot keeps
        P(map slot) = loot_bias.slot and a applies only to the rest. No SIX / no base_armor: plain roll_item (same RNG stream)."""
        a = float(SIX.get('base_armor', 0)) if SIX is not None else 0.0
        if a <= 0:
            return self.roll_item(tier, key)
        r = self.rng.random()
        if SIX.get('base_mapslot'):
            lb = self.cfg.get('loot_bias') or {}
            ms = ((self.cfg['maps'].get(key) or {}).get('loot') or {}).get('slot') if key and lb else None
            if ms:
                sw = float(lb.get('slot', 0.5))
                if r < sw:
                    it = self.roll_item(tier, key)
                    it['slot'] = ms
                    return it
                r = (r - sw) / (1 - sw)
        return self.roll_armor(tier, key) if r < a else self.roll_item(tier, key)

    def consider_armor(self, new):
        cfg, kn = self.cfg, self.kn
        i = ARMOR_SLOTS.index(new['slot'])
        cur = self.armor[i]
        lv = level_of(cfg, self.xp)
        alt = list(self.armor); alt[i] = new
        a, b = stats(cfg, self.blade, self.charm, lv, None, alt), stats(cfg, self.blade, self.charm, lv, None, self.armor)
        # 4-piece bookkeeping only (no effect in stage 0): prefer the target family on ties
        if power(cfg, a, kn) > power(cfg, b, kn) * 1.0001 or (power(cfg, a, kn) >= power(cfg, b, kn) * 0.9999
                                                             and new['fam'] == kn.target and cur['fam'] != kn.target):
            self.armor[i] = new
            self.dismantle(cur)
        else:
            self.dismantle(new)

    def roll_item(self, tier, key=None):
        r, cfg = self.rng, self.cfg
        u = r.random()
        tw = cfg['target_weight']
        tgt = self.kn.target
        others = [x for x in FAMS if x != tgt]
        lb = cfg.get('loot_bias') or {}
        loot = (cfg['maps'].get(key) or {}).get('loot') if key and lb else None
        mf, ms = (loot or {}).get('family'), (loot or {}).get('slot')
        if mf and mf == tgt:      # P2-9: map family is the player's target
            tw = float(lb.get('own_family', tw))
            fam = tgt if u < tw else (others[0] if u < tw + (1 - tw) / 2 else others[1])
        elif mf:                  # map family takes map_share of the non-target rest
            third = next(x for x in others if x != mf)
            sh = (1 - tw) * float(lb.get('map_share', 0.5))
            fam = tgt if u < tw else (mf if u < tw + sh else third)
        else:
            fam = tgt if u < tw else (others[0] if u < tw + (1 - tw) / 2 else others[1])
        if ms:
            sw = float(lb.get('slot', 0.5))
            slot = ms if r.random() < sw else ('charm' if ms == 'blade' else 'blade')
        else:
            slot = 'blade' if r.random() < 0.5 else 'charm'
        return item(fam, slot, tier, pick(cfg['quality_w'], r.random()), pick(cfg['craft_w'], r.random()))

    def codex_see(self, it):
        """D243: register the item kind (live: EmberCodex.register on the inventory scan) and claim reached S33 stages."""
        k = sourcemap.codex_key(it)
        if k is None or k in self.codex:
            return
        self.codex.add(k)
        while CODEX and self.codex_paid < len(CODEX_STAGES) and len(self.codex) >= CODEX_STAGES[self.codex_paid][0]:
            c = CODEX_STAGES[self.codex_paid][1]
            self.coin += c; self.codex_coin += c; self.codex_paid += 1
            CODEX_STATS['coin'] = CODEX_STATS.get('coin', 0) + c

    def consider(self, new):
        """Equip `new` if the whole loadout gets stronger; dismantle what is not worn."""
        cfg, kn = self.cfg, self.kn
        self.codex_see(new)  # D243: every piece handed to the player registers (drops, first-clear picks, marks, raid items)
        slot = new['slot']
        if slot in ARMOR_SLOTS:
            return self.consider_armor(new)
        cur = self.blade if slot == 'blade' else self.charm
        cand = dict(new)
        if kn.swap and cur['enh'] > cand['enh']:
            cand['enh'], cand['pity'] = cur['enh'], cur['pity']
        # quality_chase (forge-sink phase 2): take a strictly better-quality target-family piece even when
        # the worn piece's forged craft makes raw power a hair higher — player re-forges craft next week.
        chase = (getattr(kn, 'quality_chase', False) and new['q'] > cur['q']
                 and new['tier'] >= cur['tier'] and new['fam'] == kn.target and cur['fam'] == kn.target)
        lv = level_of(cfg, self.xp)
        ar = self.armor
        if slot == 'blade':
            a, b = stats(cfg, cand, self.charm, lv, None, ar), stats(cfg, self.blade, self.charm, lv, None, ar)
        else:
            a, b = stats(cfg, self.blade, cand, lv, None, ar), stats(cfg, self.blade, self.charm, lv, None, ar)
        if chase or power(cfg, a, kn) > power(cfg, b, kn) * 1.0001:
            if kn.swap and cand['enh'] == cur['enh'] and cur['enh'] > new['enh']:
                cur = dict(cur, enh=new['enh'], pity=new['pity'])
            if slot == 'blade':
                self.blade = cand
            else:
                self.charm = cand
            self.dismantle(cur)
        else:
            self.dismantle(new)

    def dismantle(self, it):
        if it['src'] in ('drop', 'mark') and it['tier'] >= 1:
            self.blank += it['tier'] * (SIX.get('blank', 1.0) if SIX is not None and it['slot'] in ARMOR_SLOTS else 1.0)

    def bounty(self):
        """P2-7 (D79): the n-th settled clear of the stamina day pays the bounty tiers with clears == n."""
        if self.day is None:
            return
        if self._bday != self.day:
            self._bday, self._bn = self.day, 0
        prev = self._bn
        self._bn += getattr(self, 'bounty_weight', 1)  # D105: a raid clear counts as 2 (50 stamina ≈ two runs)
        for b in self.cfg.get('bounty', []):
            if prev < int(b.get('clears', 0)) <= self._bn:
                self.coin += int(b.get('coin', 0)); self.shard += int(b.get('shard', 0))
                self.bone += int(b.get('bone', 0)); self.core += int(b.get('core', 0))

    def settle(self, key, extra, var=None):
        c0 = self.coin
        self._settle(key, extra, var)
        if GROWTH is not None:  # D142 余烬勋记: settlement coin % and +shards
            st = self.st()
            if self.coin > c0 and gm(st, 'coin') > 1.0:
                self.coin += int(round((self.coin - c0) * (gm(st, 'coin') - 1.0)))
            self.shard += int(gm(st, 'shard_bonus', 0.0))

    def _settle(self, key, extra, var=None):
        self.bounty()
        cfg, m = self.cfg, self.cfg['maps'][key]
        b = cfg['base']
        if var is not None and key in self.cleared:  # D138 repeat-run variety (existing reward types only)
            v = cfg.get('variety') or {}
            if var.get('affix'):
                self.shard += int(v.get('affix_shard', 0))
            if var.get('event'):
                self.core += int(v.get('event_core', 0))
            if VBOUNTY and self.day is not None:  # D144 花样委托: daily count per kind, paid once when reached
                if getattr(self, '_vbday', None) != self.day:
                    self._vbday, self._vbn = self.day, {}
                for vb in cfg.get('vbounty', []):
                    k = vb.get('kind')
                    if not var.get('affix' if k == 'affix' else 'event'):
                        continue
                    prev = self._vbn.get(k, 0)
                    self._vbn[k] = prev + 1
                    if prev + 1 == int(vb.get('count', 1)):
                        self.coin += int(vb.get('coin', 0)); self.shard += int(vb.get('shard', 0))
        self.coin += b['coin']; self.shard += b['shard']; self.bone += b['bone']; self.core += b['core']
        self.xp += b['xp']
        tier = m['tier']
        self.marks[tier] += b['mark']
        drops = [self.roll_base(tier, key)]
        if SIX is not None:
            dr = float(SIX.get('drop', 1))  # fractional = probability of one more armor drop
            na = int(dr) + (self.rng.random() < dr - int(dr) if dr != int(dr) else 0)
            front = key not in self.cleared  # D246 options: the player's own first clear of this map
            if SIX.get('arm_repeat') and front:
                na = 0  # armor only from repeat runs (the first clear pays its first-clear package instead)
            drops += [self.roll_armor(max(1, tier - 1) if SIX.get('arm_front') and front else tier, key) for _ in range(na)]
            if SIX.get('start') and key not in self.cleared and key in ('q01', 'q02'):  # research §4.3 starter armor
                four = SIX.get('start') == 'q01'  # all four at the Q01 first clear (no Q02 gap)
                for a in (ARMOR_SLOTS if four and key == 'q01' else () if four else ('head', 'chest') if key == 'q01' else ('legs', 'boots')):
                    drops.append(item(self.kn.target, a, 1, src='task'))
        if extra == 'treasure':
            self.coin += cfg['treasure_coin']
        elif extra == 'elite':
            self.shard += cfg['elite_shard']; self.core += cfg['elite_core']
        elif extra == 'chest':
            drops.append(self.roll_base(tier, key) if SIX is not None and SIX.get('chest_armor') else self.roll_item(tier, key))
        if key not in self.cleared:
            self.cleared.add(key)
            fc = m.get('first_clear') or {}
            if 'choice' in fc:
                slot = fc['choice']
                if slot == 'piece':  # E-review #6: a free targeted exchange — the player picks family and slot
                    slot = self.weaker_slot(fc.get('tier', 1))
                drops.append(item(self.kn.target, slot, fc.get('tier', 1), src='task'))
            self.shard += fc.get('shard', 0); self.core += fc.get('core', 0); self.coin += fc.get('coin', 0)
            self.bone += fc.get('bone', 0); self.blank += fc.get('blank', 0)
        for d in drops:
            self.consider(d)
        for t in (1, 2, 3):
            while self.marks[t] >= cfg['marks_per']:
                self.marks[t] -= cfg['marks_per']
                slot = self.weaker_slot(t)
                self.consider(item(self.kn.target, slot, t, src='mark'))

    def weaker_slot(self, tier):
        bl, ch = self.blade, self.charm
        tgt = self.kn.target
        if bl['fam'] != tgt or bl['tier'] < tier:
            if ch['fam'] != tgt or ch['tier'] < tier:
                return 'blade' if bl['tier'] <= ch['tier'] else 'charm'
            return 'blade'
        if ch['fam'] != tgt or ch['tier'] < tier:
            return 'charm'
        return 'blade' if bl['q'] <= ch['q'] else 'charm'

    def invest(self):
        cfg, kn = self.cfg, self.kn
        reserve = kn.coin_reserve
        # §6.4 upgrade first (T1→T2 after q04, T2→T3 after q07); lower piece first, blade on ties
        self.save_for_upgrade = False
        pieces = (self.blade, self.charm) + (tuple(self.armor) if self.armor is not None else ())
        for it in sorted(pieces, key=lambda x: (x['tier'], ('blade', 'charm').index(x['slot']) if x['slot'] in ('blade', 'charm') else 2)):
            flag = {1: 'q04', 2: 'q07'}.get(it['tier'])
            if not flag or flag not in self.cleared or it['fam'] == 'none':
                continue
            if SIX is not None and SIX.get('arm_cap') and it['slot'] in ARMOR_SLOTS and it['tier'] >= self.charm['tier']:
                continue  # STAGE 0 rule option: an armor piece is upgraded only up to the charm's tier (drops are not capped)
            k = self.cost_mult(it, up=True)
            c = {x: v * k for x, v in cfg['upgrade'][it['tier']].items()}
            if self.shard >= c['shard'] and self.core >= c['core'] and self.blank >= c['blank']:
                if self.coin - c['coin'] >= reserve:
                    self.shard -= c['shard']; self.core -= c['core']; self.blank -= c['blank']; self.coin -= c['coin']
                    it['tier'] += 1
                    if SIX is not None and SIX.get('up_all') and it['slot'] in ARMOR_SLOTS:
                        continue  # STAGE 0 policy: upgrade every affordable armor piece in one go (2-slot unchanged)
                else:
                    self.save_for_upgrade = True
                    reserve += c['coin']
                break
        # §6.1 enhance the lower piece while affordable
        while True:
            it = min(pieces, key=lambda x: (x['enh'], ('blade', 'charm').index(x['slot']) if x['slot'] in ('blade', 'charm') else 2))
            if it['enh'] >= 10 or it['tier'] == 0 and it['enh'] >= 10:
                break
            t = it['enh'] + 1
            k = self.cost_mult(it)
            sh, co, cn = cfg['enh_shard'][t] * k, cfg['enh_core'][t] * k, cfg['enh_coin'][t] * k
            if self.shard < sh or self.core < co or self.coin - cn < reserve:
                break
            self.shard -= sh; self.core -= co; self.coin -= cn
            if it['pity'] + 1 >= cfg['enh_max'][t] or self.rng.random() < cfg['enh_rate'][t]:
                it['enh'] = t; it['pity'] = 0
            else:
                it['pity'] += 1

    def cost_mult(self, it, up=False):
        """STAGE 0: enhance / upgrade cost multiplier of a piece (SIX['cost'] = {'charm': x, 'armor': y}; blade 1;
        SIX['cost_up'] = same shape, overrides SIX['cost'] for tier upgrades only)"""
        if SIX is None or it['slot'] == 'blade':
            return 1.0
        kind = 'armor' if it['slot'] in ARMOR_SLOTS else 'charm'
        if up and kind in (SIX.get('cost_up') or {}):
            return float(SIX['cost_up'][kind])
        return float((SIX.get('cost') or {}).get(kind, 1.0))

    def buy_potions(self):
        while self.potions < self.kn.potion_keep and self.coin >= self.cfg['potion_price']:
            self.coin -= self.cfg['potion_price']
            self.potions += 1


def _truthy(v):
    return v is True or str(v).lower() == 'true'


def convert_roles(m, mod):
    """D94 / D158: role remap of week rule `mod` on map def m (server: only roles the map defines). With a `converted:`
    block (D158, balance_version 29) the swapped mobs spawn as '<to>@cv' — the target role's numbers × hp / atk /
    interval, speed (approach time ÷ speed) — exactly like EmberRunDirector.spawn(conv). Returns the remap to use."""
    remap = {k: v for k, v in (mod.get('remap') or {}).items() if v in m['mobs']}
    cv = mod.get('converted') or {}
    if remap and cv:
        clamp = lambda x: max(0.25, min(4.0, float(x)))  # Modifier.clampMult
        for frm, to in list(remap.items()):
            d = dict(m['mobs'][to])
            d['hp'] = float(d['hp']) * clamp(cv.get('hp', 1.0))
            d['atk'] = float(d['atk']) * clamp(cv.get('atk', 1.0))
            d['interval'] = float(d.get('interval', 3.0)) * clamp(cv.get('interval', 1.0))
            d['speed'] = clamp(cv.get('speed', 1.0))
            m['mobs'][to + CV] = d
            remap[frm] = to + CV
    return remap


def mod_cfg(cfg, key, mod):
    """D94: config with weekly rule `mod` applied to map `key` (role remap / room 1↔3 swap); returns (cfg, potion cap)."""
    c = copy.deepcopy(cfg)
    m = c['maps'][key]
    remap = convert_roles(m, mod)
    if remap:
        for room in m['rooms'].values():
            for var in ('a', 'b'):
                comp = {}
                for role, n in room[var].items():
                    comp[remap.get(role, role)] = comp.get(remap.get(role, role), 0) + n
                room[var] = comp
    if _truthy(mod.get('swap_rooms')):
        r1, r3 = m['rooms']['r1'], m['rooms']['r3']
        r1['a'], r1['b'], r3['a'], r3['b'] = r3['a'], r3['b'], r1['a'], r1['b']
    return c, (int(mod['potion_cap']) if mod.get('potion_cap') else None)


def week_rule(kn, order, day, offset, key, cleared):
    """D94: the weekly rule hitting this NORMAL run (featured map, already first-cleared), or None.
    D174 stage 2b upper bound: P1_PLEDGE=lean+reverse puts the 自选誓约 rules on EVERY repeat normal run of Q01–Q06."""
    pl = os.environ.get('P1_PLEDGE', '')
    if pl and key in cleared and key != order[-1]:
        ids = pl.split('+')
        mod = {'id': 'pledge:' + pl}
        if 'lean' in ids:
            mod['potion_cap'] = 3
        if 'reverse' in ids:
            mod['swap_rooms'] = True
        return mod
    if not kn.normal_mods or not MODS:
        return None
    week = (day - 1 + offset) // 7
    if order[week % len(order)] != key or key not in cleared:
        return None
    mod = MODS[mod_index(week, len(MODS), len(order))]  # D187 skewed when pool and map count share a factor
    return mod if (kn.normal_mods == 'all' or _truthy(mod.get('normal'))) else None


def mod_index(week, n, k):
    """D187 mirror of EmberRunMaps.modifierIndex: rule index of rotation week `week` (featured map = week mod k).
    Coprime pool sizes keep week mod n; otherwise (w mod k + w div k) mod n so every map meets every rule."""
    import math
    if n <= 0:
        return 0
    if k <= 1 or math.gcd(n, k) == 1:
        return week % n
    return ((week % k) + week // k) % n


def feat_week(order, day, offset):
    week = (day - 1 + offset) // 7
    return week, order[week % len(order)]


def feat_left(p, kn, cfg, order, day, offset, key):
    """D108: bonus clears left this week if a repeat NORMAL clear of `key` would pay the featured mark (else 0)."""
    n = int(ROT_CFG.get('normal_bonus_marks', 0) or 0)
    if kn.feat_normal == 'off' or n <= 0 or key not in p.cleared or cfg['maps'][key]['tier'] >= 3:
        return 0
    week, feat = feat_week(order, day, offset)
    if feat != key:
        return 0
    if getattr(p, 'feat_wk', None) != week:
        p.feat_wk, p.feat_n = week, 0
    return max(0, int(ROT_CFG.get('weekly_cap', 3)) - p.feat_n)


def feat_pay(p, kn, cfg, order, day, offset, key):
    """Call BEFORE settle (the mark converts in settle). Shares the weekly cap counter with the challenge bonus."""
    if feat_left(p, kn, cfg, order, day, offset, key) <= 0:
        return 0
    n = int(ROT_CFG.get('normal_bonus_marks', 0))
    p.feat_n += 1
    p.marks[cfg['maps'][key]['tier']] += n
    p.feat_marks = getattr(p, 'feat_marks', 0) + n
    return n


def simulate_player(cfg, kn, seed, max_runs=600, stop_at=None):
    rng = random.Random(seed)
    p = Player(cfg, kn, rng)
    order = cfg['order']
    stop_at = stop_at or order[-1]
    per_day = cfg['stamina_day'] // cfg['run_cost']
    rec = {k: {'entries': 0, 'clears': 0, 'deaths': 0, 'fc_run': None, 'fc_day': None, 'gear': None,
               'B': None, 'H': None, 'lv': None, 'front_entries': 0} for k in order}
    refund_day = -1
    cur = 0
    runs = 0
    offset = random.Random(seed * 7919 + 13).randrange(7 * len(order) * max(1, len(MODS)))  # calendar position
    modded = {}
    while runs < max_runs:
        day = runs // per_day + 1
        front = next((i for i, k in enumerate(order) if k not in p.cleared), None)
        if front is None or stop_at in p.cleared:
            break
        cur = min(cur, front)
        key = order[cur]
        if kn.feat_farm:  # D108 detour: spend this week's bonus clears on the featured map when it pays
            fk = feat_week(order, day, offset)[1]
            if feat_left(p, kn, cfg, order, day, offset, fk) > 0:
                key = fk
        p.buy_potions()
        p.day = day
        st = p.st()
        mod = week_rule(kn, order, day, offset, key, p.cleared)
        rep = key in p.cleared
        if mod is None:
            ok, used, extra, secs, taken, where = run_map(cfg, key, st, kn, rng, p.potions, repeat=rep)
        else:
            if (key, mod['id']) not in modded:
                modded[(key, mod['id'])] = mod_cfg(cfg, key, mod)
            mc, cap = modded[(key, mod['id'])]
            ok, used, extra, secs, taken, where = run_map(mc, key, st, kn, rng, p.potions if cap is None else min(cap, p.potions), repeat=rep)
        p.potions -= used
        runs += 1
        r = rec[key]
        r['entries'] += 1
        detour = key != order[cur]  # D108 featured detour (feat_farm)
        if cur == front and not detour:
            r['front_entries'] += 1
        if ok:
            r['clears'] += 1
            first = key not in p.cleared
            if first:
                r['fc_run'], r['fc_day'] = runs, day
                r['gear'] = desc(p.blade) + ' ' + desc(p.charm)
                r['B'], r['H'], r['lv'] = st['B'], st['H'], level_of(cfg, p.xp)
                r['M'] = st['M']
                r['charm_te'] = (p.charm['tier'], p.charm['enh'], p.charm['q'])
                if p.armor is not None:  # STAGE 0: armor worn at the first clear (tier, enhance, quality)
                    r['armor'] = [(a['tier'], a['enh'], a['q']) for a in p.armor]
            feat_pay(p, kn, cfg, order, day, offset, key)
            p.settle(key, extra, dict(LAST_VAR) if rep else None)
            if first:
                if key == stop_at:
                    break
            p.invest()
            if not detour:
                cur = front + 1 if cur == front else front
        else:
            r['deaths'] += 1
            if cfg['death_refund'] > 0 and refund_day != day:
                refund_day = day
                p.potions += min(used, cfg['death_refund'])
            if detour:
                pass
            elif kn.route == 'alternate':
                cur = max(0, front - 1) if cur == front else front
            else:
                cur = max(0, cur - 1)
    return rec, runs


def clear_rate(cfg, key, st, kn, n, seed=7):
    rng = random.Random(seed)
    return sum(run_map(cfg, key, st, kn, rng, kn.potion_keep)[0] for _ in range(n)) / n


# ---------------------------------------------------------------- report

def pct(vals, p):
    """Percentile over ALL players; a player who never got there counts as +inf (returns None if past the cap)."""
    v = sorted(x if x is not None else math.inf for x in vals)
    x = v[min(len(v) - 1, int(math.ceil(p * len(v))) - 1)]
    return None if x == math.inf else x


def summarize(cfg, kn, players, max_runs, seed0=1000):
    order = cfg['order']
    recs = [simulate_player(cfg, kn, seed0 + i, max_runs)[0] for i in range(players)]
    per_day = cfg['stamina_day'] // cfg['run_cost']
    out = []
    for k in order:
        got = [r[k] for r in recs if r[k]['fc_run'] is not None]
        reach = len(got) / players
        fe = [r[k]['front_entries'] for r in recs if r[k]['fc_run'] is not None]
        ent = sum(r[k]['entries'] for r in recs)
        cl = sum(r[k]['clears'] for r in recs)
        fl = sum(r[k]['front_entries'] for r in recs)
        row = {'map': k, 'reach': reach, 'clear_rate_all': cl / ent if ent else None,
               'front_rate': (len(got) / fl) if fl else None,
               'attempts': statistics.median(fe) if fe else None,
               'fc_day': pct([r[k]['fc_day'] for r in recs], 0.5),
               'fc_day_p90': pct([r[k]['fc_day'] for r in recs], 0.9),
               'fc_run': pct([r[k]['fc_run'] for r in recs], 0.5),
               'gear': statistics.mode([g['gear'] for g in got]) if got else None,
               'B': statistics.median([g['B'] for g in got]) if got else None,
               'H': statistics.median([g['H'] for g in got]) if got else None,
               'lv': statistics.median([g['lv'] for g in got]) if got else None}
        out.append(row)
    return out, max_runs / per_day


BOOK_REF = {'q01': 'T0', 'q02': 'T1刃+T0符', 'q03': 'T1两件+0~3', 'q04': 'T1两件+3~6', 'q05': 'T2两件+3~6',
            'q06': 'T2两件+6', 'q07': 'T2两件+6~8'}


# book §3.2 reference loadout per map (upper end of the range), burst set; level is our assumption (book gives Lv30
# only for the §7.4 T2 reference character)
REF_GEAR = {'q01': (0, 0, 0, 0, 10), 'q02': (1, 0, 0, 0, 12), 'q03': (1, 3, 1, 3, 15), 'q04': (1, 6, 1, 6, 20),
            'q05': (2, 6, 2, 6, 25), 'q06': (2, 6, 2, 6, 30), 'q07': (2, 8, 2, 8, 30)}


def ref_table(cfg, dodges, n):
    lines = ['| 图 | 书参考投入（取上沿） | Lv | B / H / M | ' + ' | '.join('躲避 %.1f 通关率' % d for d in dodges) + ' |',
             '|---|---|---:|---|' + '---:|' * len(dodges)]
    for k in cfg['order']:
        bt, be, ct, ce, lv = REF_GEAR[k]
        bl = dict(item('burst' if bt else 'none', 'blade', bt), enh=be)
        ch = dict(item('burst' if ct else 'none', 'charm', ct), enh=ce)
        st = stats(cfg, bl, ch, lv)
        rates = [clear_rate(cfg, k, st, Knobs(d), n) for d in dodges]
        lines.append('| %s | %s | %d | %.1f / %.0f / %.2f | %s |' % (k.upper(), desc(bl) + ' ' + desc(ch), lv, st['B'], st['H'],
                     st['M'], ' | '.join('%d%%' % round(100 * r) for r in rates)))
    return '\n'.join(lines)


def fmt(x, f='%.0f', none='—'):
    return none if x is None else f % x


def table(rows, cap_days, dodge):
    lines = ['| 图 | 到达率 | 前沿通关率 | 首通前进本（中位） | 首通在第几天（中位 / P90） | 首通时装备（众数） | B / H / Lv | 书参考投入 |',
             '|---|---:|---:|---:|---|---|---|---|']
    for r in rows:
        lines.append('| %s | %s | %s | %s | %s | %s | %s | %s |' % (
            r['map'].upper(), '%d%%' % round(100 * r['reach']),
            fmt(r['front_rate'] and 100 * r['front_rate'], '%.0f%%'), fmt(r['attempts']),
            ('第 %s 天 / %s' % (fmt(r['fc_day']), fmt(r['fc_day_p90'], none='>%d' % cap_days))) if r['fc_day']
            else '过半玩家 %d 天内未通' % cap_days,
            r['gear'] or '—',
            ('%.1f / %.0f / %d' % (r['B'], r['H'], r['lv'])) if r['B'] else '—', BOOK_REF.get(r['map'], '')))
    return '\n'.join(lines)


# measured natural playtests (docs/reviews/PLAYTEST-P1-progression.md): first-clear run index, or ('>', runs) = not cleared
MEASURED = {
    'v2': {'profile': 'v2', 'route': 'alternate', 'runs': 92,
           'fc': {'q01': 2, 'q02': 7, 'q03': 14, 'q04': 56, 'q05': ('>', 92)}},
    'v3': {'profile': 'current', 'route': 'stepdown', 'runs': 44,
           'fc': {'q01': 1, 'q02': 10, 'q03': 13, 'q04': ('>', 44)}},
}


def calibrate(players, grid):
    out = []
    for name, m in MEASURED.items():
        cfg = p1config.load(m['profile'])
        for d in grid:
            kn = Knobs(d, route=m['route'])
            recs = [simulate_player(cfg, kn, 5000 + i, m['runs'])[0] for i in range(players)]
            err, cells = 0.0, []
            for k, real in m['fc'].items():
                med = pct([r[k]['fc_run'] for r in recs], 0.5)
                share = sum(1 for r in recs if r[k]['fc_run'] is not None) / players
                if isinstance(real, tuple):
                    e = 0.0 if med is None else math.log(real[1] / med)
                    cells.append('%s 实测 >%d · 模拟 %s（%d%% 在 %d 局内通）' % (k.upper(), real[1], fmt(med, none='>%d' % m['runs']),
                                                                   round(100 * share), m['runs']))
                else:
                    e = abs(math.log((med if med is not None else 2 * m['runs']) / real))
                    cells.append('%s 实测 %d · 模拟 %s' % (k.upper(), real, fmt(med, none='>%d' % m['runs'])))
                err += e
            out.append((name, d, err / len(m['fc']), cells))
    return out


def main(argv=None):
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument('--profile', default='current', choices=sorted(p1config.PROFILES))
    ap.add_argument('--dodge', type=float, nargs='*', default=[0.3, 0.5, 0.7])
    ap.add_argument('--players', type=int, default=60)
    ap.add_argument('--days', type=int, default=60, help='stamina-day cap per simulated player')
    ap.add_argument('--route', default='stepdown', choices=['stepdown', 'alternate'])
    ap.add_argument('--swap', action='store_true', help='use the §6.3 free enhance swap (the bots did not)')
    ap.add_argument('--no-bounty', action='store_true', help='without the P2-7 daily bounty (D79) for comparison')
    ap.add_argument('--no-loot', action='store_true', help='without the P2-9 per-map loot identity (D81) for comparison')
    ap.add_argument('--normal-mods', nargs='?', const='normal', choices=['normal', 'all'], default=None,
                    help='D94: weekly rule on repeat normal runs of the featured map (all = include casters)')
    ap.add_argument('--no-feat-normal', action='store_true', help='D108: without the featured mark on repeat normal runs')
    ap.add_argument('--no-variety', action='store_true', help='D138: without the repeat-run variety (affixed elite + room event)')
    ap.add_argument('--no-vbounty', action='store_true', help='D144: without the 花样委托 daily variety bounty')
    ap.add_argument('--feat-farm', action='store_true', help='D108: players detour to the featured map for the bonus')
    ap.add_argument('--target', default='burst', choices=FAMS)
    ap.add_argument('--uptime', type=float, default=0.70)
    ap.add_argument('--ref', action='store_true', help='clear rate per map at the book §3.2 reference loadout')
    ap.add_argument('--calibrate', action='store_true', help='fit the dodge level against the measured playtests')
    args = ap.parse_args(argv)
    print('# ' + __import__('rules').stamp(), flush=True)  # M06: which rule snapshot produced this report
    if args.calibrate:
        grid = args.dodge if args.dodge != [0.3, 0.5, 0.7] else [0.3, 0.4, 0.5, 0.6, 0.7]
        print('| 试玩 | 躲避 | 平均对数误差 | 首通在第几局（中位） |\n|---|---:|---:|---|')
        for name, d, e, cells in calibrate(args.players, grid):
            print('| %s | %.2f | %.2f | %s |' % (name, d, e, '；'.join(cells)))
        return
    cfg = p1config.load(args.profile)
    if args.no_bounty:
        cfg["bounty"] = []
    if args.no_loot:
        cfg["loot_bias"] = {}
    if args.no_variety:
        global VARIETY
        VARIETY = False
    if args.no_vbounty:
        global VBOUNTY
        VBOUNTY = False
    if args.ref:
        print(ref_table(cfg, args.dodge, args.players * 5))
        return
    for w in cfg['warnings']:
        print('WARNING:', w, file=sys.stderr)
    per_day = cfg['stamina_day'] // cfg['run_cost']
    print('# p1sim profile=%s route=%s swap=%s target=%s normal_mods=%s players=%d cap=%d days (%d runs/day)\n' % (
        args.profile, args.route, args.swap, args.target, args.normal_mods, args.players, args.days, per_day))
    for d in args.dodge:
        kn = Knobs(d, route=args.route, swap=args.swap, target=args.target, uptime=args.uptime, normal_mods=args.normal_mods,
                   feat_normal='off' if args.no_feat_normal else 'config', feat_farm=args.feat_farm)
        rows, cap = summarize(cfg, kn, args.players, args.days * per_day)
        last = rows[-1]
        print('## dodge %.2f — Q07 first clear: %s\n' % (d, ('median day %s (P90 %s), %d%% of players within %d days'
              % (fmt(last['fc_day']), fmt(last['fc_day_p90'], none='>%d' % args.days), round(100 * last['reach']),
                 args.days)) if last['fc_day'] else '%d%% of players within %d days' % (round(100 * last['reach']), args.days)))
        print(table(rows, args.days, d))
        print()


if __name__ == '__main__':
    main()

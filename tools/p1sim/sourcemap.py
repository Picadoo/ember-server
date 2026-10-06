"""D243 / ARCH S4-2: one-time sources read from docs/design/ember-source-map.yml (through the rules.py snapshot).

The source map marks the sources the offline sim must pay with `sim:`:
  * codex_stage (S33 图录阶段奖励): coin at 5 / 10 / 15 / 20 registered codex entries, amounts from ember-v1-economy.yml
    `S33.at<n>.coin`, dual-asserted against EmberCodex.STAGE_AT / STAGE_COIN (Java) — p1sim.Player registers every item
    it is handed (Player.consider: drops, first-clear picks, mark exchanges, raid items) like the live inventory scan,
    and claims a reached stage at once (the bots' / players' `/corerpg p1 codex claim`).
  * starter_kit (S35 起步包): T0 blade + T0 charm + `S35.potions` bound potions, dual-asserted against ember-v1.yml
    starter.heal_potions; the two T0 pieces are the first two codex entries.
P1SIM_NO_CODEX=1 switches the codex stage coin off (pre-D243 sim, for A/B); the starter kit was always modelled.

  python3 tools/p1sim/sourcemap.py show                  # what the sim pays from the map
  python3 tools/p1sim/sourcemap.py dyn [--players 800]   # 21 cells: codex off vs on (±2pp gate), first-clear days
"""
import argparse, os, re, sys
HERE = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, HERE)
import rules  # noqa: E402

FAMILIES = ('scorch', 'burst', 'sustain')  # EmberRunRules.FAMILIES (asserted below)


def _java_ints(text, name):
    m = re.search(r'\b%s\s*=\s*\{([^}]*)\}' % name, text)
    if not m:
        raise rules.RuleError('EmberCodex.%s not found' % name)
    return [int(x) for x in m.group(1).split(',')]


def sim_entries():
    sm = rules.data('sourcemap')
    if not sm:
        raise rules.RuleError('docs/design/ember-source-map.yml missing from the rule snapshot')
    out = {}
    for sid, e in (sm.get('sources') or {}).items():
        if isinstance(e, dict) and e.get('sim'):
            out[e['sim']] = dict(e, id=sid)
    for need in ('codex_stage', 'starter_kit'):
        if need not in out:
            raise rules.RuleError('ember-source-map.yml has no sources entry with sim: %s' % need)
    return out


def codex_stages():
    """[(entries needed, coin)] from the economy yml keys the map lists for the codex_stage entry."""
    e = sim_entries()['codex_stage']
    row = e['econ_row']
    st = []
    for k in e.get('economy_keys') or []:
        m = re.match(r'^%s\.at(\d+)\.coin$' % re.escape(row), k)
        if m:
            st.append((int(m.group(1)), rules.amount(row, 'at%s.coin' % m.group(1))))
    st.sort()
    jt = rules.java('codex')
    at, coin = _java_ints(jt, 'STAGE_AT'), _java_ints(jt, 'STAGE_COIN')
    if st != list(zip(at, coin)):
        raise rules.RuleError('codex stages: source map / economy yml %s != EmberCodex %s' % (st, list(zip(at, coin))))
    fam = re.search(r'FAMILIES\s*=\s*\{([^}]*)\}', rules.java('settle'))
    if not fam or tuple(x.strip().strip('"') for x in fam.group(1).split(',')) != FAMILIES:
        raise rules.RuleError('EmberRunRules.FAMILIES drifted from sourcemap.FAMILIES')
    return st


def codex_size():
    return 2 + len(FAMILIES) * 2 * 3  # EmberCodex.ENTRIES: T0 blade / charm + family × slot × T1..T3


def codex_key(it):
    if it['slot'] not in ('blade', 'charm') or not 0 <= it['tier'] <= 3:
        return None
    return '%s_%s_t%d' % ('none' if it['tier'] == 0 else it['fam'], it['slot'], it['tier'])


def starter_kit(p1yml_potions):
    e = sim_entries()['starter_kit']
    row = e['econ_row']
    kit = {'pieces': rules.amount(row, 'pieces'), 'potions': rules.amount(row, 'potions')}
    if kit['potions'] != int(p1yml_potions):
        raise rules.RuleError('starter potions: economy yml %s.potions=%s != ember-v1.yml starter.heal_potions=%s'
                              % (row, kit['potions'], p1yml_potions))
    if kit['pieces'] != 2:
        raise rules.RuleError('starter kit pieces %s != 2 (T0 blade + charm)' % kit['pieces'])
    return kit


def show(_a):
    import p1config
    cfg = p1config.load()
    print('# ' + rules.stamp())
    print('codex_stage (%s): %s of %d entries' % (sim_entries()['codex_stage']['id'], codex_stages(), codex_size()))
    print('starter_kit (%s): %s' % (sim_entries()['starter_kit']['id'], starter_kit(cfg['starter_potions'])))


def dyn(a):
    import p1sim, p1config
    cfg = p1config.load()
    print('# ' + rules.stamp())
    per_day = cfg['stamina_day'] // cfg['run_cost']
    print('# D243 codex stage coin A/B: players=%d seed0=%d days=%d (A = P1SIM_NO_CODEX, B = live S33)\n' % (a.players, a.seed0, a.days))
    print('| 躲避 | 图 | 前沿通关率 A | B | 差 pp | 首通中位天 A | B | P90 A | B | Lv A/B |')
    print('|---|---|---:|---:|---:|---:|---:|---:|---:|---|')
    inside = cells = 0
    worst = 0.0
    coin = {}
    for d in a.dodge:
        kn = p1sim.Knobs(d)
        p1sim.CODEX = False
        base, _ = p1sim.summarize(cfg, kn, a.players, a.days * per_day, seed0=a.seed0)
        p1sim.CODEX = True
        p1sim.CODEX_STATS.clear()
        new, _ = p1sim.summarize(cfg, kn, a.players, a.days * per_day, seed0=a.seed0)
        coin[d] = dict(p1sim.CODEX_STATS)
        for b, n in zip(base, new):
            fr0, fr1 = b['front_rate'], n['front_rate']
            dpp = None if fr0 is None or fr1 is None else 100 * (fr1 - fr0)
            cells += 1
            if dpp is not None and abs(dpp) <= 2.0:
                inside += 1
            if dpp is not None:
                worst = max(worst, abs(dpp))
            f = lambda x, s='%.1f': '—' if x is None else s % x
            print('| %.1f | %s | %s | %s | %s | %s | %s | %s | %s | %s / %s |' % (
                d, b['map'].upper(), f(fr0 and 100 * fr0), f(fr1 and 100 * fr1), f(dpp, '%+.1f'),
                f(b['fc_day'], '%d'), f(n['fc_day'], '%d'), f(b['fc_day_p90'], '%d'), f(n['fc_day_p90'], '%d'),
                f(b['lv'], '%d'), f(n['lv'], '%d')))
    print('\n**%d/%d 格在 ±2pp 内**（最大 |差| %.1f pp）' % (inside, cells, worst))
    for d, c in coin.items():
        print('- dodge %.1f: codex coin paid %s (players %s, per player %.0f)' % (
            d, c.get('coin', 0), c.get('players', 0), c.get('coin', 0) / max(1, c.get('players', 1))))


if __name__ == '__main__':
    ap = argparse.ArgumentParser()
    ap.add_argument('tool', choices=['show', 'dyn'])
    ap.add_argument('--players', type=int, default=800)
    ap.add_argument('--days', type=int, default=60)
    ap.add_argument('--dodge', type=float, nargs='*', default=[0.3, 0.5, 0.7])
    ap.add_argument('--seed0', type=int, default=1000)
    a = ap.parse_args()
    {'show': show, 'dyn': dyn}[a.tool](a)

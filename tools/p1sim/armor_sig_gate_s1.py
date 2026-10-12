#!/usr/bin/env python3
"""TIP-AC / stage1 offline gate helper for armor-side signatures.

Docs: docs/design/DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md

Does NOT touch live jars, economy YAML, or invent new balance numbers.
Reuses mainline.py tol/pairs/talent/econ for combat/econ packs.

  python3 armor_sig_gate_s1.py rules      # §2.6 / 5-take-1 / excl / same-tag unit checks
  python3 armor_sig_gate_s1.py cmds       # print exact S3/S4 command pack
  python3 armor_sig_gate_s1.py checklist  # PASS/FAIL rubric one-pager
"""
from __future__ import annotations

import sys

# Defense-side pick order from stage0 DESIGN §2.6 (stable, testable).
PRIO = ('charm', 'head', 'chest', 'legs', 'boots')


def pick_defense(candidates, *, dual_sig_unlocked, blade_tag, talent_tags):
    """Return at most one defense-side signature id from worn candidates.

    candidates: list of dicts {id, slot, tag} for charm+armor pieces that carry a defense-side sig.
    dual_sig_unlocked: False → none of defense-side active (blade-priority spirit before Q03 dual).
    blade_tag: tag of the active blade signature, or None.
    talent_tags: set of talent tags that excl matching signatures.
    """
    if not dual_sig_unlocked:
        return None
    pool = []
    for c in candidates:
        if c['tag'] in talent_tags:
            continue
        if blade_tag is not None and c['tag'] == blade_tag:
            continue
        if c['slot'] not in PRIO:
            continue
        pool.append(c)
    if not pool:
        return None
    pool.sort(key=lambda c: PRIO.index(c['slot']))
    return pool[0]['id']


def rules():
    fails = []

    def check(name, cond):
        if not cond:
            fails.append(name)
            print('FAIL', name)
        else:
            print('PASS', name)

    # 0 worn → 0 active
    check('zero_worn', pick_defense([], dual_sig_unlocked=True, blade_tag=None, talent_tags=set()) is None)

    # 1 worn → that one
    one = [{'id': 'L02', 'slot': 'charm', 'tag': 'dodgeheal'}]
    check('one_charm', pick_defense(one, dual_sig_unlocked=True, blade_tag=None, talent_tags=set()) == 'L02')

    # multi armor → charm wins over head
    multi = [
        {'id': 'L03', 'slot': 'head', 'tag': 'hitburst'},
        {'id': 'L02', 'slot': 'charm', 'tag': 'dodgeheal'},
        {'id': 'L06', 'slot': 'chest', 'tag': 'supply'},
    ]
    check('prio_charm', pick_defense(multi, dual_sig_unlocked=True, blade_tag=None, talent_tags=set()) == 'L02')

    # no charm → head > chest > legs > boots
    armor_only = [
        {'id': 'A_boots', 'slot': 'boots', 'tag': 'supply'},
        {'id': 'A_head', 'slot': 'head', 'tag': 'hitburst'},
        {'id': 'A_legs', 'slot': 'legs', 'tag': 'heal'},
    ]
    check('prio_head', pick_defense(armor_only, dual_sig_unlocked=True, blade_tag=None, talent_tags=set()) == 'A_head')

    # dual sig locked → none
    check('dual_locked', pick_defense(multi, dual_sig_unlocked=False, blade_tag=None, talent_tags=set()) is None)

    # same tag as blade → whole side dropped for that tag; other tags may remain
    same = [
        {'id': 'L08', 'slot': 'charm', 'tag': 'burn'},
        {'id': 'L06', 'slot': 'chest', 'tag': 'supply'},
    ]
    check('blade_same_tag', pick_defense(same, dual_sig_unlocked=True, blade_tag='burn', talent_tags=set()) == 'L06')
    check('blade_same_tag_all', pick_defense(
        [{'id': 'L08', 'slot': 'charm', 'tag': 'burn'}],
        dual_sig_unlocked=True, blade_tag='burn', talent_tags=set()) is None)

    # talent excl
    check('talent_excl', pick_defense(
        [{'id': 'L03', 'slot': 'charm', 'tag': 'hitburst'}],
        dual_sig_unlocked=True, blade_tag=None, talent_tags={'hitburst'}) is None)

    # never more than one (priority collapses to single id)
    many = [
        {'id': 'c', 'slot': 'charm', 'tag': 'a'},
        {'id': 'h', 'slot': 'head', 'tag': 'b'},
        {'id': 't', 'slot': 'chest', 'tag': 'c'},
        {'id': 'l', 'slot': 'legs', 'tag': 'd'},
        {'id': 'b', 'slot': 'boots', 'tag': 'e'},
    ]
    got = pick_defense(many, dual_sig_unlocked=True, blade_tag=None, talent_tags=set())
    check('cap_one', got == 'c')

    # ×0.97 orthogonal is documentary — remind only
    print('NOTE S3-X: checklist in DESIGN §3.3 / RESULT §2.3 (no new dmg mult / skill_mult / no retune ×0.97)')

    if fails:
        print('S3-R FAIL count=%d' % len(fails))
        return 1
    print('S3-R PASS')
    return 0


def cmds():
    print('''# TIP-AC stage1 offline command pack (from repo root)
# Zero business jar. Do not invent new SIGS mods.

python3 tools/p1sim/armor_sig_gate_s1.py rules

mkdir -p /tmp/ml-armor-sig
cd tools/p1sim

# S3-T
python3 mainline.py tol    2000 /tmp/ml-armor-sig/tol.pkl
python3 mainline.py report /tmp/ml-armor-sig/tol.pkl > ../../docs/design/out-armor-sig-stage1-tol.md

# S3-P
python3 mainline.py pairs  2000 /tmp/ml-armor-sig/pairs.pkl
python3 mainline.py report /tmp/ml-armor-sig/pairs.pkl > ../../docs/design/out-armor-sig-stage1-pairs.md

# S3-E (set ML_BEST to strongest legal pairs from pairs report)
# ML_BEST='{"burst":["L04+L03"],"scorch":["L01+L02"],"sustain":["L05+L06"]}' \\
#   python3 mainline.py talent 2000 /tmp/ml-armor-sig/talent.pkl
# python3 mainline.py report /tmp/ml-armor-sig/talent.pkl > ../../docs/design/out-armor-sig-stage1-talent.md

# S4 W30 — BASE
python3 mainline.py econ '[]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \\
  > ../../docs/design/out-armor-sig-stage1-w30-base.md

# S4 W30 — CAND (example existing blade+charm; same mods as armor-side after imprint expand)
python3 mainline.py econ '["L01","L02"]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \\
  > ../../docs/design/out-armor-sig-stage1-w30-cand-L01L02.md

# Fill docs/design/RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md
# PASS still HOLD construction until 总控 explicit 开工.
''')


def checklist():
    print('''STAGE1 offline rubric
S1/S2: inherit PASS from TIP-AB stage0 (spec freeze + imprint-expand / drop-not)
S3-R: armor_sig_gate_s1.py rules → all PASS
S3-T/P/E: mainline tol/pairs/talent within D174 §8 bands
S3-X: no new dmg mult / skill_mult; ×0.97 & resonate F untouched
S4: |ΔW30| ≤ 0.5 wk vs BASE (current six-slot live + current sig econ) or 总控 re-nail
FAIL → stop; no STAMP/Affix/dmg-mult to pass
PASS → still HOLD jar; need 总控 显式开工 before construction tip
RESULT: docs/design/RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md
''')


def main():
    cmd = sys.argv[1] if len(sys.argv) > 1 else 'checklist'
    if cmd == 'rules':
        sys.exit(rules())
    if cmd == 'cmds':
        cmds()
        return
    if cmd == 'checklist':
        checklist()
        return
    print(__doc__)
    sys.exit(2)


if __name__ == '__main__':
    main()

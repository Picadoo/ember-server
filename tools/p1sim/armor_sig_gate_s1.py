#!/usr/bin/env python3
"""TIP-AC/AD · stage1 offline gate helper for armor-side signatures.

Docs:
  docs/design/DESIGN-ember-armor-signature-gate-stage1-offline-2026-10-12.md
  docs/design/DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md (TIP-AD)

Does NOT touch live jars, economy YAML, or invent / retune balance numbers.
Reuses mainline.py tol/pairs/talent/econ for combat/econ packs.
TIP-AD: S3-T/P armor-gate membership excludes blade L11/L13 (blade-track debt HOLD).

  python3 armor_sig_gate_s1.py rules       # §2.6 / 5-take-1 / excl / same-tag unit checks
  python3 armor_sig_gate_s1.py cmds        # print exact S3/S4 command pack (+ exclude note)
  python3 armor_sig_gate_s1.py checklist   # PASS/FAIL rubric one-pager
  python3 armor_sig_gate_s1.py judge-s3    # filter existing tol/pairs outs; exclude L11/L13
"""
from __future__ import annotations

import re
import sys
from pathlib import Path

# Defense-side pick order from stage0 DESIGN §2.6 (stable, testable).
PRIO = ('charm', 'head', 'chest', 'legs', 'boots')

# TIP-AD stage0 S3 eval patch: blade L11/L13 are blade-track pollution — not armor-gate membership.
# Membership only; does NOT delete SIGS rows or change mods.
ARMOR_GATE_S3_EXCLUDE = frozenset({'L11', 'L13'})

REPO = Path(__file__).resolve().parents[2]
OUT_TOL = REPO / 'docs/design/out-armor-sig-stage1-tol.md'
OUT_PAIRS = REPO / 'docs/design/out-armor-sig-stage1-pairs.md'


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

    check('zero_worn', pick_defense([], dual_sig_unlocked=True, blade_tag=None, talent_tags=set()) is None)

    one = [{'id': 'L02', 'slot': 'charm', 'tag': 'dodgeheal'}]
    check('one_charm', pick_defense(one, dual_sig_unlocked=True, blade_tag=None, talent_tags=set()) == 'L02')

    multi = [
        {'id': 'L03', 'slot': 'head', 'tag': 'hitburst'},
        {'id': 'L02', 'slot': 'charm', 'tag': 'dodgeheal'},
        {'id': 'L06', 'slot': 'chest', 'tag': 'supply'},
    ]
    check('prio_charm', pick_defense(multi, dual_sig_unlocked=True, blade_tag=None, talent_tags=set()) == 'L02')

    armor_only = [
        {'id': 'A_boots', 'slot': 'boots', 'tag': 'supply'},
        {'id': 'A_head', 'slot': 'head', 'tag': 'hitburst'},
        {'id': 'A_legs', 'slot': 'legs', 'tag': 'heal'},
    ]
    check('prio_head', pick_defense(armor_only, dual_sig_unlocked=True, blade_tag=None, talent_tags=set()) == 'A_head')

    check('dual_locked', pick_defense(multi, dual_sig_unlocked=False, blade_tag=None, talent_tags=set()) is None)

    same = [
        {'id': 'L08', 'slot': 'charm', 'tag': 'burn'},
        {'id': 'L06', 'slot': 'chest', 'tag': 'supply'},
    ]
    check('blade_same_tag', pick_defense(same, dual_sig_unlocked=True, blade_tag='burn', talent_tags=set()) == 'L06')
    check('blade_same_tag_all', pick_defense(
        [{'id': 'L08', 'slot': 'charm', 'tag': 'burn'}],
        dual_sig_unlocked=True, blade_tag='burn', talent_tags=set()) is None)

    check('talent_excl', pick_defense(
        [{'id': 'L03', 'slot': 'charm', 'tag': 'hitburst'}],
        dual_sig_unlocked=True, blade_tag=None, talent_tags={'hitburst'}) is None)

    many = [
        {'id': 'c', 'slot': 'charm', 'tag': 'a'},
        {'id': 'h', 'slot': 'head', 'tag': 'b'},
        {'id': 't', 'slot': 'chest', 'tag': 'c'},
        {'id': 'l', 'slot': 'legs', 'tag': 'd'},
        {'id': 'b', 'slot': 'boots', 'tag': 'e'},
    ]
    got = pick_defense(many, dual_sig_unlocked=True, blade_tag=None, talent_tags=set())
    check('cap_one', got == 'c')

    print('NOTE S3-X: checklist in DESIGN §3.3 / RESULT §2.3 (no new dmg mult / skill_mult / no retune ×0.97)')
    print('NOTE TIP-AD: S3-T/P armor-gate set excludes blade %s (membership only; no SIGS retune)' % sorted(ARMOR_GATE_S3_EXCLUDE))

    if fails:
        print('S3-R FAIL count=%d' % len(fails))
        return 1
    print('S3-R PASS')
    return 0


def cmds():
    excl = ','.join(sorted(ARMOR_GATE_S3_EXCLUDE))
    print('# TIP-AC/AD stage1 offline command pack (from repo root)')
    print('# Zero business jar. Do not invent new SIGS mods / do not retune L11·L13 here.')
    print('# TIP-AD: armor-gate S3-T/P membership EXCLUDES blade %s (HOLD blade-track tip).' % excl)
    print()
    print('python3 tools/p1sim/armor_sig_gate_s1.py rules')
    print()
    print('mkdir -p /tmp/ml-armor-sig')
    print('cd tools/p1sim')
    print()
    print('# S3-T (full table OK to run; gate judgment = judge-s3 filter)')
    print('python3 mainline.py tol    2000 /tmp/ml-armor-sig/tol.pkl')
    print('python3 mainline.py report /tmp/ml-armor-sig/tol.pkl > ../../docs/design/out-armor-sig-stage1-tol.md')
    print()
    print('# S3-P')
    print('python3 mainline.py pairs  2000 /tmp/ml-armor-sig/pairs.pkl')
    print('python3 mainline.py report /tmp/ml-armor-sig/pairs.pkl > ../../docs/design/out-armor-sig-stage1-pairs.md')
    print()
    print('# Gate re-judge after TIP-AD exclude (prefer before claiming S3-T/P PASS)')
    print('python3 tools/p1sim/armor_sig_gate_s1.py judge-s3')
    print()
    print('# S3-E (set ML_BEST to strongest legal pairs WITHOUT L11/L13)')
    print("# ML_BEST='{\"burst\":[\"L04+L14\"],\"scorch\":[\"L01+L14\"],\"sustain\":[\"L05+L14\"]}' \\\\")
    print('#   python3 mainline.py talent 2000 /tmp/ml-armor-sig/talent.pkl')
    print('# python3 mainline.py report /tmp/ml-armor-sig/talent.pkl > ../../docs/design/out-armor-sig-stage1-talent.md')
    print()
    print('# S4 W30 — BASE (TIP-AC already PASS; inherit unless stage0 drop policy changes)')
    print("python3 mainline.py econ '[]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \\\\")
    print('  > ../../docs/design/out-armor-sig-stage1-w30-base.md')
    print()
    print('# S4 W30 — CAND (example existing blade+charm; same mods as armor-side after imprint expand)')
    print("python3 mainline.py econ '[\"L01\",\"L02\"]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \\\\")
    print('  > ../../docs/design/out-armor-sig-stage1-w30-cand-L01L02.md')
    print()
    print('# Fill / append docs/design/RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md')
    print('# PASS still HOLD construction until 总控 explicit 开工.')


def checklist():
    excl = ','.join(sorted(ARMOR_GATE_S3_EXCLUDE))
    print('STAGE1 offline rubric (TIP-AD patched)')
    print('S1/S2: inherit PASS from TIP-AB stage0 (spec freeze + imprint-expand / drop-not)')
    print('S3-R: armor_sig_gate_s1.py rules → all PASS')
    print('S3-T/P: mainline tol/pairs within D174 §8 bands on ARMOR-GATE set only')
    print('        EXCLUDE blade %s (alone + pairs containing them) — blade debt HOLD other tip' % excl)
    print('        Prefer: armor_sig_gate_s1.py judge-s3 on existing outs')
    print('S3-E: talent; ML_BEST without L11/L13')
    print('S3-X: no new dmg mult / skill_mult; ×0.97 & resonate F untouched')
    print('S4: |ΔW30| ≤ 0.5 wk vs BASE (TIP-AC PASS — inherit)')
    print('FAIL → stop; no STAMP/Affix/dmg-mult to pass')
    print('PASS → still HOLD jar; need 总控 显式开工 before construction tip')
    print('RESULT: docs/design/RESULT-ember-armor-signature-gate-stage1-offline-2026-10-12.md')
    print('PATCH: docs/design/DESIGN-ember-armor-signature-gate-stage0-s3-eval-patch-2026-10-12.md')


def _parse_reachable_rows(path: Path):
    rows = []
    if not path.is_file():
        return rows
    for line in path.read_text().splitlines():
        if not line.startswith('| ') or '方案' in line or line.startswith('|---'):
            continue
        parts = [p.strip() for p in line.strip().strip('|').split('|')]
        if len(parts) < 7:
            continue
        label, ok = parts[0], parts[6]
        if '可达' not in label:
            continue
        head = label.split('·')[0].strip()
        token = head.split()[0] if head.split() else head
        ids = re.findall(r'L\d+', token)
        rows.append((label, ids, ok))
    return rows


def judge_s3():
    """Filter existing tol/pairs markdown; score only armor-gate membership (excl L11/L13)."""
    print('ARMOR_GATE_S3_EXCLUDE=%s' % sorted(ARMOR_GATE_S3_EXCLUDE))
    print('criteria: 可达 rows only; D174 hard-line mark from report (❌ = fail)')
    all_fail = 0
    for name, path in (('S3-T', OUT_TOL), ('S3-P', OUT_PAIRS)):
        rows = _parse_reachable_rows(path)
        if not rows:
            print('%s MISSING %s' % (name, path))
            all_fail += 1
            continue
        kept = [r for r in rows if not any(i in ARMOR_GATE_S3_EXCLUDE for i in r[1])]
        dropped = [r for r in rows if any(i in ARMOR_GATE_S3_EXCLUDE for i in r[1])]
        fails = [r for r in kept if '❌' in r[2]]
        soft = [r for r in kept if '🟡' in r[2]]
        ok = [r for r in kept if '✅' in r[2]]
        print('%s path=%s' % (name, path.name))
        print('  kept=%d dropped_excl=%d PASS=%d soft=%d FAIL=%d' % (
            len(kept), len(dropped), len(ok), len(soft), len(fails)))
        for r in fails[:12]:
            print('  FAIL', r[0], r[2])
        if fails:
            all_fail += 1
            print('%s armor-gate FAIL' % name)
        else:
            print('%s armor-gate PASS (excl %s)' % (name, sorted(ARMOR_GATE_S3_EXCLUDE)))
    if all_fail:
        print('judge-s3 FAIL')
        return 1
    print('judge-s3 PASS — S3-T/P armor-gate set clean; still ≠ 开工; S3-R/E/X/S4 inherit TIP-AC')
    return 0


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
    if cmd in ('judge-s3', 'judge_s3', 'judge'):
        sys.exit(judge_s3())
    print(__doc__)
    sys.exit(2)


if __name__ == '__main__':
    main()

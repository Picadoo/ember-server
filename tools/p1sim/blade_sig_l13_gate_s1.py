#!/usr/bin/env python3
"""TIP-AI · stage1 offline gate helper for blade L13 charge-identity (R1+R2).

Docs:
  docs/design/DESIGN-ember-blade-sig-l13-stage1-offline-2026-10-12.md
  docs/design/RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md

Does NOT touch live jars, economy YAML, or edit mainline.SIGS['L13'] numbers.
Reuses mainline.py tol/econ for combat/econ packs. TIP-AD armor-gate exclude of L13 remains.

  python3 blade_sig_l13_gate_s1.py checklist
  python3 blade_sig_l13_gate_s1.py cmds
  python3 blade_sig_l13_gate_s1.py judge-alone [--tol PATH]
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]
DEFAULT_TOL = REPO / 'docs/design/out-armor-sig-stage1-tol.md'
OUT_TOL_STAGE1 = REPO / 'docs/design/out-blade-l13-stage1-tol.md'

# Hypothesis nail (docs only — not a SIGS patch)
HYPOTHESIS = 'H-R12 = R1 charge-window structure + R2 mid-charge input'
ESCALATE = 'R5 full resignature (HOLD until R1+R2 space exhausted)'


def checklist():
    print('STAGE1 L13 offline rubric (TIP-AI)')
    print('hypothesis: %s' % HYPOTHESIS)
    print('escalate_on_fail: %s' % ESCALATE)
    print('G-alone: L13 alone · 3 fams reachable · D174 hard-line (mean|Δ|≤1, no cell >±3pp)')
    print('G-pair: default KEEP TIP-AD exclude (armor gate); do not re-admit L13 this window')
    print('G-W30: if structure cand modeled, |ΔW30|≤0.5 vs ref route; else SKIP')
    print('G-X: no skill_mult / Affix / STAMP / armor stampable / sx20 / deepen densify')
    print('G-split: blade ≠ armor; L11 HOLD; no TIP-AE rollback')
    print('G-id: keep ring / no-aim core; L13=charged heavy ring vs L09=instant light ring')
    print('G-charge: target dead-window variance; ban permanent dmg-mult as fix')
    print('FAIL → stop; no table-raise. PASS → still HOLD jar until 总控 显式开工')
    print('RESULT: docs/design/RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md')
    print('NOTE: do NOT edit mainline.py SIGS[L13]; historical charge 0.25–0.45 overshot positive cells')


def cmds():
    print('# TIP-AI L13 stage1 offline command pack (from repo root)')
    print('# Zero business jar. Do NOT edit SIGS[L13] / skill_mult / STAMP.')
    print('# Hypothesis: %s' % HYPOTHESIS)
    print()
    print('python3 tools/p1sim/blade_sig_l13_gate_s1.py checklist')
    print()
    print('mkdir -p /tmp/ml-blade-l13')
    print('cd tools/p1sim')
    print()
    print('# G-alone — full tol then judge L13 alone rows')
    print('python3 mainline.py tol    2000 /tmp/ml-blade-l13/tol.pkl')
    print('python3 mainline.py report /tmp/ml-blade-l13/tol.pkl \\')
    print('  > ../../docs/design/out-blade-l13-stage1-tol.md')
    print('python3 blade_sig_l13_gate_s1.py judge-alone \\')
    print('  --tol ../../docs/design/out-blade-l13-stage1-tol.md')
    print()
    print('# Optional: re-judge baseline from TIP-AC armor stage1 tol (expect L13 FAIL)')
    print('python3 blade_sig_l13_gate_s1.py judge-alone \\')
    print('  --tol ../../docs/design/out-armor-sig-stage1-tol.md')
    print()
    print('# G-W30 — only if cand modeled; else RESULT SKIP')
    print("python3 mainline.py econ '[\"L01\",\"L02\"]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \\")
    print('  > ../../docs/design/out-blade-l13-stage1-w30-base-ref.md')
    print("python3 mainline.py econ '[\"L13\",\"L02\"]' --players 300 --weeks 12 --dodge 0.5 --abyss --raid --goals \\")
    print('  > ../../docs/design/out-blade-l13-stage1-w30-cand-L13L02.md')
    print()
    print('# Fill docs/design/RESULT-ember-blade-sig-l13-stage1-offline-2026-10-12.md')
    print('# PASS still HOLD construction until 总控 explicit 开工.')


def _parse_l13_alone_reachable(path: Path):
    """Return list of (label, ok_mark) for L13 alone reachable rows."""
    rows = []
    if not path.is_file():
        return rows
    for line in path.read_text().splitlines():
        if not line.startswith('| ') or line.startswith('|---'):
            continue
        parts = [p.strip() for p in line.strip().strip('|').split('|')]
        if len(parts) < 7:
            continue
        label, ok = parts[0], parts[6]
        if '可达' not in label:
            continue
        # alone L13: label starts with L13 and is not a pair (no second Lxx in head token)
        head = label.split('·')[0].strip()
        ids = re.findall(r'L\d+', head.split()[0] if head.split() else head)
        if ids == ['L13']:
            rows.append((label, ok))
    return rows


def judge_alone(tol_path: Path):
    print('hypothesis: %s' % HYPOTHESIS)
    print('criteria: L13 alone · 可达 rows · D174 hard-line mark from report (❌ = fail)')
    print('tol=%s' % tol_path)
    rows = _parse_l13_alone_reachable(tol_path)
    if not rows:
        print('L13 alone rows MISSING — run tol/report or pass --tol')
        print('judge-alone FAIL (no data)')
        return 1
    fails = [r for r in rows if '❌' in r[1]]
    soft = [r for r in rows if '🟡' in r[1]]
    ok = [r for r in rows if '✅' in r[1]]
    print('L13_alone kept=%d PASS=%d soft=%d FAIL=%d' % (len(rows), len(ok), len(soft), len(fails)))
    for r in fails[:12]:
        print('  FAIL', r[0], r[1])
    for r in soft[:6]:
        print('  SOFT', r[0], r[1])
    if fails:
        print('G-alone FAIL — baseline expected until R1+R2 cand ships; ban table-raise')
        print('judge-alone FAIL')
        return 1
    print('G-alone PASS on this out — still ≠ 开工; still ≠ edit live SIGS without 显式开工')
    print('judge-alone PASS')
    return 0


def main():
    ap = argparse.ArgumentParser(add_help=False)
    ap.add_argument('cmd', nargs='?', default='checklist')
    ap.add_argument('--tol', type=Path, default=None)
    args, _ = ap.parse_known_args()
    cmd = args.cmd
    if cmd == 'checklist':
        checklist()
        return
    if cmd == 'cmds':
        cmds()
        return
    if cmd in ('judge-alone', 'judge_alone', 'judge'):
        tol = args.tol
        if tol is None:
            if OUT_TOL_STAGE1.is_file():
                tol = OUT_TOL_STAGE1
            else:
                tol = DEFAULT_TOL
        sys.exit(judge_alone(tol))
    print(__doc__)
    sys.exit(2)


if __name__ == '__main__':
    main()

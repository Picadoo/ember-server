#!/usr/bin/env python3
"""TIP-BF · T0 offline G-within gate for SF4 U1 微缓 (docs+tools).

Docs:
  docs/design/DESIGN-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md
  docs/design/RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md
  docs/tips/TIP-ember-skill-sf4-u1-t0-p1sim-thin-d13000-d13015-2026-10-12.md

Zero Java / zero skill_mult / zero business jar.
Reuses skillkit.py measure grid. Offline model keys: kit_sf4_mark_secs / kit_sf4_slow
(in p1sim.kit_slash_flavor). PASS ≠ 开工.

  python3 sf4_u1_gate_t0.py checklist
  python3 sf4_u1_gate_t0.py cmds
  python3 sf4_u1_gate_t0.py run [N] [OUT.pkl]
  python3 sf4_u1_gate_t0.py report [OUT.pkl]
  python3 sf4_u1_gate_t0.py judge [OUT.pkl|REPORT.md]
"""
from __future__ import annotations

import json
import os
import pickle
import re
import statistics
import sys
from pathlib import Path

REPO = Path(__file__).resolve().parents[2]
HERE = Path(__file__).resolve().parent
DEFAULT_PKL = Path('/tmp/sk-sf4-u1/t0.pkl')
DEFAULT_REPORT = REPO / 'docs/design/out-sf4-u1-t0-p1sim.md'
RESULT = REPO / 'docs/design/RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md'

# Primary + narrow backup (DESIGN TIP-BE U1 · ≤1.0s Slow-I magnitude)
CANDS = {
    'U1_sl10': (
        'q01', 'any',
        'SF4·U1 微缓 1.0s slow0.12（主荐）',
        {'kit_sf4_mark_secs': 1.0, 'kit_sf4_slow': 0.12},
    ),
    'U1_sl08': (
        'q01', 'any',
        'SF4·U1 微缓 0.8s slow0.12（收窄备）',
        {'kit_sf4_mark_secs': 0.8, 'kit_sf4_slow': 0.12},
    ),
}

HYPOTHESIS = 'H-U1 = SF4 shape-micro Slow short window on 烬斩 hit (set-agnostic offline proxy)'
HARD = 'G-within hard: mean|Δ|≤1 pp AND no reachable cell |Δ|>3 pp (skill-kit D174)'
TARGET = 'G-within target: all reachable cells |Δ|≤2 pp'


def checklist():
    print('TIP-BF SF4 U1 T0 offline rubric')
    print('hypothesis: %s' % HYPOTHESIS)
    print('cand_primary: U1_sl10 kit_sf4_mark_secs=1.0 kit_sf4_slow=0.12')
    print('cand_backup:  U1_sl08 kit_sf4_mark_secs=0.8 kit_sf4_slow=0.12')
    print(HARD)
    print(TARGET)
    print('G-prefer: 乱按不净赚通关（择优行仅辅助；主判 always-on cand）')
    print('G-x: no skill_mult / 伤乘 / Affix / 抬表 / 改招架 / 改 CD·几何盒')
    print('G-ortho: 不改 SF1–3 文案墙；不改 D301 招架；不改身法/烬突')
    print('G-id: 可读作形状短窗微缓；禁读作永久更痛')
    print('G-hold: T0 PASS ≠ 开工；须总控显式开工 tip 才改 CoreRpg')
    print('model: kit_sf4_* in p1sim.kit_slash_flavor (tools-only); natural ICD = 烬斩 charge')
    print('shape_note: LINE/套装推荐 = 施工接线层；本窗离线代理 = keys 开即生效')
    print('FAIL → 停；禁放宽硬线；禁抬 skill_mult 凑绿')
    print('RESULT: %s' % RESULT.relative_to(REPO))
    print('NOTE: zero Java / zero business jar / L13 施工 HOLD / Anvil densify 仍冻')


def cmds():
    print('# TIP-BF SF4 U1 T0 command pack (from repo root)')
    print('# Zero business jar. Do NOT edit skill_mult / CoreRpg / TrMenu.')
    print('# Hypothesis: %s' % HYPOTHESIS)
    print()
    print('python3 tools/p1sim/sf4_u1_gate_t0.py checklist')
    print()
    print('mkdir -p /tmp/sk-sf4-u1')
    print('cd tools/p1sim')
    print()
    print('# Formal grid (n=1500 mirrors D438 / guard-parry); n=800 ok for probe')
    print('python3 sf4_u1_gate_t0.py run 1500 /tmp/sk-sf4-u1/t0.pkl')
    print('python3 sf4_u1_gate_t0.py report /tmp/sk-sf4-u1/t0.pkl \\')
    print('  > ../../docs/design/out-sf4-u1-t0-p1sim.md')
    print('python3 sf4_u1_gate_t0.py judge ../../docs/design/out-sf4-u1-t0-p1sim.md')
    print()
    print('# Fill RESULT-ember-skill-sf4-u1-t0-p1sim-2026-10-12.md')
    print('# PASS still HOLD construction until 总控 explicit 开工.')


def _ensure_skillkit():
    sys.path.insert(0, str(HERE))
    import skillkit  # noqa: F401
    return skillkit


def run(n: int, out: Path):
    sk = _ensure_skillkit()
    out.parent.mkdir(parents=True, exist_ok=True)
    sk.run(CANDS, n, str(out))
    print('ran cands=%s n=%d out=%s' % (','.join(CANDS), n, out), file=sys.stderr)


def report(out: Path):
    sk = _ensure_skillkit()
    R = pickle.load(open(out, 'rb'))
    # ensure meta has our cands (skillkit stores them under (_meta,))
    meta = R.get(('_meta,',)) or R.get(('_meta',), {})
    # skillkit uses ('_meta',) as key — tuple of one str
    if ('_meta',) in R:
        # merge our labels if missing
        R[('_meta',)].update({k: v for k, v in CANDS.items() if k not in R[('_meta',)]})
    sk.report(R)


def _parse_report_rows(text: str):
    """Parse skillkit markdown table rows for U1_* cands. Returns list of dicts."""
    rows = []
    for line in text.splitlines():
        if not line.startswith('| ') or line.startswith('|---') or '候选' in line:
            continue
        if '↳' in line:
            continue
        parts = [p.strip() for p in line.strip().strip('|').split('|')]
        if len(parts) < 8:
            continue
        name = parts[0]
        if not name.startswith('U1_'):
            continue
        # name field is "U1_sl10 SF4·U1 ..."
        cand = name.split()[0]
        fam = parts[1]
        mark = parts[7]  # 判定
        try:
            mabs = float(parts[6])
        except ValueError:
            mabs = None
        # extract max |Δ| from 最高/最低
        hi = parts[4]
        lo = parts[5]
        def _pp(s):
            m = re.search(r'([+-]?\d+(?:\.\d+)?)', s)
            return float(m.group(1)) if m else None
        rows.append({
            'cand': cand, 'fam': fam, 'mark': mark,
            'mabs': mabs, 'hi': _pp(hi), 'lo': _pp(lo),
            'raw': name,
        })
    return rows


def judge(src: Path):
    print('hypothesis: %s' % HYPOTHESIS)
    print('criteria: %s' % HARD)
    if not src.is_file():
        print('MISSING %s' % src)
        print('judge FAIL (no data)')
        return 1
    text = src.read_text() if src.suffix == '.md' else ''
    if src.suffix == '.pkl' or src.name.endswith('.pkl'):
        # regenerate report text
        import io
        from contextlib import redirect_stdout
        buf = io.StringIO()
        with redirect_stdout(buf):
            report(src)
        text = buf.getvalue()
    rows = _parse_report_rows(text)
    if not rows:
        print('U1 rows MISSING — run report first')
        print('judge FAIL (no data)')
        return 1
    # Primary cand first
    primary = [r for r in rows if r['cand'] == 'U1_sl10']
    backup = [r for r in rows if r['cand'] == 'U1_sl08']
    focus = primary if primary else backup
    label = focus[0]['cand'] if focus else '?'
    fails = [r for r in focus if '❌' in r['mark']]
    soft = [r for r in focus if '🟡' in r['mark']]
    ok = [r for r in focus if '✅' in r['mark']]
    print('focus=%s kept=%d PASS=%d soft=%d FAIL=%d' % (label, len(focus), len(ok), len(soft), len(fails)))
    for r in focus:
        print('  %s %s mark=%s mabs=%s hi=%s lo=%s' % (
            r['cand'], r['fam'], r['mark'], r['mabs'], r['hi'], r['lo']))
    # G-within: all three fams must be ✅ (hard). 🟡 = soft fail for G-within target but
    # skillkit ✅ requires all cells ≤2pp; 🟡 is mean|Δ|≤1 and max≤3 — that IS the hard line.
    # DESIGN: 硬线 mean|Δ|≤1 and no cell >±3; target all ≤2.
    # Map: ✅ = target met; 🟡 = hard line met; ❌ = hard fail.
    hard_fail = [r for r in focus if '❌' in r['mark']]
    if hard_fail:
        print('G-within FAIL — stop; ban skill_mult / table-raise')
        print('judge FAIL')
        print('STAGE0_SF4_U1_T0=FAIL')
        return 1
    if any('🟡' in r['mark'] for r in focus):
        print('G-within HARD-PASS (🟡 soft cells) — target ±2pp not full; still ≠ 开工')
        print('judge HARD-PASS')
        print('STAGE0_SF4_U1_T0=HARD_PASS')
        return 0
    print('G-within PASS (✅ all fams) — still ≠ 开工; still HOLD jar until 显式开工')
    print('judge PASS')
    print('STAGE0_SF4_U1_T0=PASS')
    return 0


def main():
    args = sys.argv[1:]
    cmd = args[0] if args else 'checklist'
    if cmd == 'checklist':
        checklist()
        return
    if cmd == 'cmds':
        cmds()
        return
    if cmd == 'run':
        n = int(args[1]) if len(args) > 1 else 1500
        out = Path(args[2]) if len(args) > 2 else DEFAULT_PKL
        run(n, out)
        return
    if cmd == 'report':
        out = Path(args[1]) if len(args) > 1 else DEFAULT_PKL
        report(out)
        return
    if cmd in ('judge', 'judge-within', 'judge_within'):
        src = Path(args[1]) if len(args) > 1 else (
            DEFAULT_REPORT if DEFAULT_REPORT.is_file() else DEFAULT_PKL)
        sys.exit(judge(src))
    if cmd == 'cands':
        print(json.dumps({k: [v[0], v[1], v[3]] for k, v in CANDS.items()}, ensure_ascii=False, indent=2))
        return
    print(__doc__)
    sys.exit(2)


if __name__ == '__main__':
    main()

"""D296 gate: event_rate 0.50 vs 0.85 — regenerate with:
  python3 tools/p1sim/d296-event-rate-econ.py
Outputs tools/p1sim/out-d296-event-rate-econ.md
Shipped run 2026-10-07: PASS (max Δ两件极品 +4pp, E[core] ×1.72, event_core=1).
"""
import copy, os, random, sys, time
from io import StringIO
from pathlib import Path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
import p1config

OUT = os.path.join(os.path.dirname(__file__), 'out-d296-event-rate-econ.md')
RATES = (0.50, 0.85)
PLAYERS, WEEKS, DODGE = 120, 12, 0.5
MEAN_SUCC = 0.952  # out-eventpack4-d191 new-pool E[core|event rolled]
N_ROLL = 20000


def with_rate(rate):
    c = copy.deepcopy(p1config.load('current'))
    c.setdefault('variety', {})['event_rate'] = float(rate)
    assert int(c['variety'].get('event_core', 0)) == 1
    return c


def part_a():
    lines = [
        '## Part A — S05 期望核心（出事件率 × Pack4 成功系数 0.952 × event_core）',
        '',
        '成功系数取自 `out-eventpack4-d191.md` 新池每次出事件期望核心。',
        '',
        '| rate | MC 出事件率 | E[core/重打局] | vs 0.50 |',
        '|---:|---:|---:|---:|',
    ]
    rows = {}
    for rate in RATES:
        rng = random.Random(42)
        roll = sum(1 for _ in range(N_ROLL) if rng.random() < rate) / N_ROLL
        e = roll * MEAN_SUCC
        rows[rate] = (roll, e)
    base = rows[0.50][1]
    for rate in RATES:
        roll, e = rows[rate]
        lines.append('| %.2f | %.3f | %.3f | ×%.2f |' % (rate, roll, e, e / base))
    lines += ['', '`event_core` = **1**。设计期望约 ×1.70（0.85/0.50）。', '']
    return lines, rows


def run_p2econ(rate):
    import p2econ
    sys.argv = ['p2econ', '--players', str(PLAYERS), '--weeks', str(WEEKS), '--dodge', str(DODGE)]
    _orig = p1config.load
    def _load(*a, **k):
        c = copy.deepcopy(_orig(*a, **k))
        c.setdefault('variety', {})['event_rate'] = float(rate)
        return c
    p1config.load = _load
    buf, old = StringIO(), sys.stdout
    sys.stdout = buf
    t0 = time.time()
    try:
        p2econ.main()
    finally:
        sys.stdout = old
        p1config.load = _orig
    return buf.getvalue(), time.time() - t0


def parse_table(text):
    out = {}
    for line in text.splitlines():
        if not line.startswith('|') or '周' in line or '---' in line:
            continue
        parts = [p.strip() for p in line.strip('|').split('|')]
        if len(parts) < 13:
            continue
        try:
            w = int(parts[0])
        except ValueError:
            continue
        def pct(s):
            return int(s.replace('%', ''))
        out[(w, parts[1])] = {
            'prem': pct(parts[6]), 'two_prem': pct(parts[7]),
            'coin': int(parts[8]), 'enh': float(parts[3]), 'B': float(parts[12]),
        }
    return out


def main():
    lines = [
        '# D296 event_rate 经济门禁（0.50 → 0.85）', '',
        '**日期：** 2026-10-07 Asia/Shanghai · bv **60** · `event_core` **1**', '',
        'Commands: `python3 tools/p1sim/d296-event-rate-econ.py`', '',
        '对照：两件极品周口径（p2econ 120 人 × 12 周 × dodge 0.5 · 默认精工/成色 sink）。', '',
    ]
    a_lines, a_rows = part_a()
    lines.extend(a_lines)
    lines.append('## Part B — p2econ（两件极品等列）')
    lines.append('')
    tables = {}
    for rate in RATES:
        print('Running p2econ rate=%.2f …' % rate, flush=True)
        text, dt = run_p2econ(rate)
        tables[rate] = parse_table(text)
        lines.append('### rate = %.2f（%.0fs）' % (rate, dt))
        lines.append('')
        for line in text.splitlines():
            if line.startswith('# p2econ') or line.startswith('|'):
                lines.append(line)
        lines.append('')
    lines += ['## 差值表（0.85 − 0.50）', '',
              '| 周 | 方案 | Δ两件极品 pp | Δ有极品 pp | Δ强化中位 | Δ币中位 | ΔB中位 |',
              '|---:|---|---:|---:|---:|---:|---:|']
    max_two = 0
    base_coin_w12 = new_coin_w12 = None
    for w in (1, 2, 4, 6, 8, 10, 12):
        for mode in ('无轮换', 'P2-1 轮换'):
            a, b = tables[0.50].get((w, mode)), tables[0.85].get((w, mode))
            if not a or not b:
                continue
            d_two = b['two_prem'] - a['two_prem']
            max_two = max(max_two, d_two)
            if w == 12 and mode == 'P2-1 轮换':
                base_coin_w12, new_coin_w12 = a['coin'], b['coin']
            lines.append('| %d | %s | %+d | %+d | %+.1f | %+d | %+.1f |' % (
                w, mode, d_two, b['prem'] - a['prem'], b['enh'] - a['enh'],
                b['coin'] - a['coin'], b['B'] - a['B']))
    lines.append('')
    coin_ok = base_coin_w12 and (abs(new_coin_w12 - base_coin_w12) / base_coin_w12 <= 0.05
                                 or abs(new_coin_w12 - base_coin_w12) <= 3000)
    two_ok = max_two <= 8
    core_ok = int(with_rate(0.85)['variety']['event_core']) == 1
    e_ratio = a_rows[0.85][1] / a_rows[0.50][1]
    ratio_ok = e_ratio <= 1.85
    passed = core_ok and two_ok and coin_ok and ratio_ok
    lines += ['## Verdict', '', '| 检查 | 阈值 | 实测 | 结果 |', '|---|---|---|---|',
              '| event_core | =1 | %s | %s |' % (with_rate(0.85)['variety']['event_core'], 'OK' if core_ok else 'FAIL'),
              '| E[core/重打] 增幅 | ≤ ×1.85（设计约 ×1.7） | ×%.2f | %s |' % (e_ratio, 'OK' if ratio_ok else 'FAIL'),
              '| max Δ两件极品 pp | ≤ +8 | %+d | %s |' % (max_two, 'OK' if two_ok else 'FAIL'),
              '| W12 轮换 |Δ币| | ≤5%% 或 ≤3k | %d（基线 %d） | %s |' % (
                  abs(new_coin_w12 - base_coin_w12), base_coin_w12, 'OK' if coin_ok else 'FAIL'),
              '']
    lines.append('**PASS** — 未破两件极品周 / 币带；S05 增量与设计 ×1.7 同量级；**未**抬 `event_core`。'
                 if passed else
                 '**破带（FAIL）** — 建议：降 `event_rate`（例 0.85→0.70）或砍方案 R（W2）；**禁止**暗抬 `event_core`；**本 tip 不改 rate**。')
    lines += ['', '禁：Pack6 / 新 kind / 六槽 / 天赋盲调 / 擅自改 rate / 暗抬 event_core。', '']
    Path(OUT).write_text('\n'.join(lines) + '\n', encoding='utf-8')
    print('Wrote', OUT)
    print('VERDICT', 'PASS' if passed else 'FAIL', 'max_two=%+d e_ratio=×%.2f' % (max_two, e_ratio))
    return 0 if passed else 1


if __name__ == '__main__':
    sys.exit(main())

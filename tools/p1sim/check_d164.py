#!/usr/bin/env python3
"""D164 pre-ship check (CoreRpg 1.65.3): talent t2c 拆分 (B01: clones ×1.5 / body ×0.8) → 破甲 as now in
ember-v1-growth.yml (dmg_affix_shield 1.6 / dmg_affix_blazing 0.8 / dmg_affix_split 0.8, read through the rule snapshot).
Felt: Q04 / Q07 repeat normal runs + Q07 challenge, dodge 0.5, paired seeds → per-elite kill time, clear rate, run time.
Tolerance: the 42 first-clear / challenge cells (Q01–Q07 r/c × dodge 0.3/0.5/0.7, seeds 7/11/13) must be identical
(row 2 only touches affixed elites, which exist only in repeat normal runs).
usage: python3 tools/p1sim/check_d164.py [--n 3000] [--ntol 600] [--nproc 5] > tools/p1sim/out-d164-armorbreak-check.md"""
import sys, statistics
import builddiv as bd, rules

N = int(sys.argv[sys.argv.index('--n') + 1]) if '--n' in sys.argv else 3000
NT = int(sys.argv[sys.argv.index('--ntol') + 1]) if '--ntol' in sys.argv else 600
NP = int(sys.argv[sys.argv.index('--nproc') + 1]) if '--nproc' in sys.argv else 5
SEED = 4243
OLD = {'dmg_split': 1.5, 'dmg_affix_body': 0.8}            # 1.65.2 / D163 拆分
NEW = bd.node_mods('t2c')                                  # 1.65.3 yml
assert NEW == {'dmg_affix_shield': 1.6, 'dmg_affix_blazing': 0.8, 'dmg_affix_split': 0.8}, NEW
FAMS = ('scorch', 'burst', 'sustain')
LABS = (('无成长', None), ('拆分（1.65.2）', OLD), ('破甲（1.65.3）', NEW))
KEYS = [('clear', '通关率'), ('secs', '全程'), ('ttk_affix_shield', '厚甲精英'), ('ttk_affix_split', '分裂精英'), ('ttk_affix_blazing', '炽热精英'), ('potions', '喝药')]


def spec(fam, mods):
    return {'set': fam} if mods is None else {'set': fam, 'extra': dict(mods)}


def main():
    jobs, keys = [], []
    for fam in FAMS:
        for lab, mods in LABS:
            for ctx in bd.MAIN:
                jobs.append((spec(fam, mods), ctx, 0.5, 'base', N, SEED)); keys.append(('felt', fam, lab, ctx))
            if mods is not None:
                tj, tk = bd.tol_jobs(spec(fam, mods), NT)
                jobs += tj; keys += [('tol', fam, lab) + k for k in tk]
    res = bd.run_jobs(jobs, NP)
    R = dict(zip(keys, res))
    w = print
    w('# D164 破甲 上线前模拟检查（CoreRpg 1.65.3，tools/p1sim/check_d164.py）\n')
    w('%s。现行 yml 的 t2c = `%s`；对照 1.65.2 拆分 `%s`。手感：躲避 0.5，%d 局，种子 %d（配对）。容差：14 情境 × 3 躲避 × 种子 7/11/13 × %d 局。\n'
      % (rules.stamp(), NEW, OLD, N, SEED, NT))
    w('## 手感（破甲相对拆分；括号 = 相对无成长）\n')
    w('| 套装 | 情境 | ' + ' | '.join(l for _, l in KEYS) + ' |')
    w('|---|---|' + '---|' * len(KEYS))
    worst_shield = []
    for fam in FAMS:
        for ctx in bd.MAIN:
            a, b, z = R[('felt', fam, '破甲（1.65.3）', ctx)], R[('felt', fam, '拆分（1.65.2）', ctx)], R[('felt', fam, '无成长', ctx)]
            cells = []
            for k, _ in KEYS:
                if k == 'clear':
                    cells.append('%.1f%% → %.1f%%（%+.1f pp；无成长 %.1f%%）' % (100 * b[k], 100 * a[k], 100 * (a[k] - b[k]), 100 * z[k]))
                    continue
                x, x0 = bd.rel(a.get(k), b.get(k)), bd.rel(a.get(k), z.get(k))
                cells.append('—' if x is None else '%s%s（%s）' % (bd.pct(x), bd.mark(x), bd.pct(x0)))
                if k == 'ttk_affix_shield' and x is not None:
                    worst_shield.append(x)
            w('| %s | %s | %s |' % (bd.FAM_ZH[fam], bd.CTX[ctx][0][:16], ' | '.join(cells)))
    w('')
    w('## 容差：首通 / 挑战 42 格（破甲 − 拆分，通关率 pp）\n')
    w('| 套装 | 最大升 | 最大降 | 全部相同？ |')
    w('|---|---:|---:|---|')
    allsame = True
    for fam in FAMS:
        ta, tb = {}, {}
        for k, r in R.items():
            if k[0] == 'tol' and k[1] == fam:
                (ta if k[2].startswith('破甲') else tb).setdefault((k[3], k[4]), []).append(r['clear'])
        ma = {c: statistics.mean(v) for c, v in ta.items()}; mb = {c: statistics.mean(v) for c, v in tb.items()}
        (u, d) = bd.tol_diff(ma, mb)
        same = all(abs(ma[c] - mb[c]) < 1e-12 for c in ma)
        allsame &= same
        w('| %s | %+.2f（%s d%.1f） | %+.2f（%s d%.1f） | %s |' % (bd.FAM_ZH[fam], u[1], u[0][0], u[0][1], d[1], d[0][0], d[0][1], '是' if same else '**否**'))
    w('')
    w('**判定：** 厚甲精英击杀用时（破甲 vs 拆分）%s ～ %s；首通 / 挑战 %s。' % (bd.pct(min(worst_shield)), bd.pct(max(worst_shield)),
      '42 格逐局相同（不生效）' if allsame else '有差异（**不合格**）'))


if __name__ == '__main__':
    main()
